/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.internal.CoreConstants;
import org.mwg.memory.offheap.primary.OffHeapDoubleArray;
import org.mwg.memory.offheap.primary.OffHeapIntArray;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.memory.offheap.primary.OffHeapString;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.plugin.Resolver;
import org.mwg.utility.Base64;

import java.util.HashSet;
import java.util.Set;

public class OffHeapENode implements ENode, OffHeapContainer {
    private final OffHeapEGraph egraph;
    private final Graph _graph;

    private static final byte LOAD_WAITING_ALLOC = 0;
    private static final byte LOAD_WAITING_TYPE = 1;
    private static final byte LOAD_WAITING_KEY = 2;
    private static final byte LOAD_WAITING_VALUE = 3;

    private long addr = OffHeapConstants.NULL_PTR;

    private static final int ID = 0;
    private static final int DIRTY = 1;
    private static final int SIZE = 2;
    private static final int CAPACITY = 3;
    private static final int SUBHASH = 4;

    private static final int OFFSET = 5;
    private static final int ELEM_SIZE = 3;

    OffHeapENode(final OffHeapEGraph p_egraph, final Graph p_graph, final long p_id, final long originAddr) {
        egraph = p_egraph;
        _graph = p_graph;

        long capacity = Constants.MAP_INITIAL_CAPACITY;
        if (originAddr != OffHeapConstants.NULL_PTR) {
            capacity = OffHeapLongArray.get(originAddr, CAPACITY);
        }
        addr = allocate(originAddr, capacity);
        OffHeapLongArray.set(addr, ID, p_id);

        OffHeapEGraph.setNodeAddrAt(egraph.getAddr(), p_id, addr);
    }

    private long allocate(final long addr, final long newCapacity) {
        if (addr == OffHeapConstants.NULL_PTR) {
            //nothing before, initial allocation...
            final long new_addr = OffHeapLongArray.allocate(OFFSET + (newCapacity * ELEM_SIZE));
            OffHeapLongArray.set(new_addr, CAPACITY, newCapacity);
            OffHeapLongArray.set(new_addr, DIRTY, 0);
            OffHeapLongArray.set(new_addr, SIZE, 0);
            if (newCapacity > Constants.MAP_INITIAL_CAPACITY) {
                OffHeapLongArray.set(new_addr, SUBHASH, OffHeapLongArray.allocate(newCapacity * 3));
            } else {
                OffHeapLongArray.set(new_addr, SUBHASH, OffHeapConstants.NULL_PTR);
            }

            return new_addr;

        } else {
            //reallocation or overallocation
            long graphAddr = egraph.getAddr();
            long nodeId = OffHeapLongArray.get(addr, ID);

            final long new_addr = OffHeapLongArray.reallocate(addr, OFFSET + (newCapacity * ELEM_SIZE));
            OffHeapLongArray.set(new_addr, CAPACITY, newCapacity);
            long subHash_ptr = OffHeapLongArray.get(new_addr, SUBHASH);
            if (subHash_ptr == OffHeapConstants.NULL_PTR) {
                subHash_ptr = OffHeapLongArray.allocate(newCapacity * 3);
            } else {
                subHash_ptr = OffHeapLongArray.reallocate(subHash_ptr, newCapacity * 3);
                OffHeapLongArray.reset(subHash_ptr, newCapacity * 3);
            }
            OffHeapLongArray.set(new_addr, SUBHASH, subHash_ptr);
            //reHash
            final long size = OffHeapLongArray.get(new_addr, SIZE);
            final long hash_capacity = newCapacity * 2;
            for (long i = 0; i < size; i++) {
                long keyHash = key(new_addr, i) % hash_capacity;
                if (keyHash < 0) {
                    keyHash = keyHash * -1;
                }
                setNext(subHash_ptr, i, hash(subHash_ptr, newCapacity, keyHash));
                setHash(subHash_ptr, newCapacity, keyHash, i);
            }
            OffHeapEGraph.setNodeAddrAt(graphAddr, nodeId, new_addr);
            return new_addr;
        }
    }

    @Override
    public void declareDirty() {
        long dirty = OffHeapLongArray.get(addr, DIRTY);
        if (dirty == 0) {
            OffHeapLongArray.set(addr, DIRTY, 1);
            egraph.declareDirty();
        }
    }

    private static long value(final long addr, final long index) {
        return OffHeapLongArray.get(addr, OFFSET + (index * ELEM_SIZE) + 2);
    }

    private static void setValue(final long addr, final long index, final long insertValue) {
        OffHeapLongArray.set(addr, OFFSET + (index * ELEM_SIZE) + 2, insertValue);
    }

    private static long key(final long addr, final long index) {
        return OffHeapLongArray.get(addr, OFFSET + (index * ELEM_SIZE));
    }

    private static void setKey(final long addr, final long index, final long insertKey) {
        OffHeapLongArray.set(addr, OFFSET + (index * ELEM_SIZE), insertKey);
    }


    private static long next(final long hashAddr, final long index) {
        return OffHeapLongArray.get(hashAddr, index);
    }

    private static void setNext(final long hashAddr, final long index, final long insertNext) {
        OffHeapLongArray.set(hashAddr, index, insertNext);
    }

    private static long hash(final long hashAddr, final long capacity, final long index) {
        return OffHeapLongArray.get(hashAddr, capacity + index);
    }

    private static void setHash(final long hashAddr, final long capacity, final long index, final long insertHash) {
        OffHeapLongArray.set(hashAddr, capacity + index, insertHash);
    }

    private static byte type(final long addr, final long index) {
        return (byte) OffHeapLongArray.get(addr, OFFSET + (index * ELEM_SIZE) + 1);
    }

    private static void setType(final long addr, final long index, final long insertType) {
        OffHeapLongArray.set(addr, OFFSET + (index * ELEM_SIZE) + 1, insertType);
    }

    private static double doubleValue(final long addr, final long index) {
        return OffHeapDoubleArray.get(addr, OFFSET + (index * ELEM_SIZE) + 2);
    }

    private static void setDoubleValue(final long addr, final long index, final double insertValue) {
        OffHeapDoubleArray.set(addr, OFFSET + (index * ELEM_SIZE) + 2, insertValue);
    }

    @Override
    public ENode set(String name, byte type, Object value) {
        internal_set(_graph.resolver().stringToHash(name, true), type, value, true, false);
        return this;
    }

    @SuppressWarnings("Duplicates")
    private long internal_set(final long p_key, final byte p_type, final Object p_unsafe_elem, boolean replaceIfPresent, boolean initial) {
        if (addr == OffHeapConstants.NULL_PTR) {
            addr = allocate(addr, Constants.MAP_INITIAL_CAPACITY);
        }
        long entry = -1;
        long prev_entry = -1;
        long hashIndex = -1;
        long size = OffHeapLongArray.get(addr, SIZE);
        long capacity = OffHeapLongArray.get(addr, CAPACITY);
        long subhash_ptr = OffHeapLongArray.get(addr, SUBHASH);
        if (subhash_ptr == OffHeapConstants.NULL_PTR) {
            for (int i = 0; i < size; i++) {
                if (key(addr, i) == p_key) {
                    entry = i;
                    break;
                }
            }
        } else {
            hashIndex = p_key % (capacity * 2);
            if (hashIndex < 0) {
                hashIndex = hashIndex * -1;
            }
            long m = hash(subhash_ptr, capacity, hashIndex);
            while (m != -1) {
                if (key(addr, m) == p_key) {
                    entry = m;
                    break;
                }
                prev_entry = m;
                m = next(subhash_ptr, m);
            }
        }
        //case already present
        if (entry != -1) {
            final byte found_type = type(addr, entry);
            if (replaceIfPresent || (p_type != found_type)) {
                if (p_unsafe_elem == null) {
                    /* Case: suppression of a value */
                    //freeThePreviousValue
                    freeElement(value(addr, entry), found_type);
                    //then clean the acces chain
                    if (subhash_ptr != OffHeapConstants.NULL_PTR) {
                        //unHash previous
                        if (prev_entry != -1) {
                            setNext(subhash_ptr, prev_entry, next(subhash_ptr, entry));
                        } else {
                            setHash(subhash_ptr, capacity, hashIndex, -1);
                        }
                    }
                    long indexVictim = size - 1;
                    //just pop the last value
                    if (entry == indexVictim) {
                        setKey(addr, entry, OffHeapConstants.NULL_PTR);
                        setValue(addr, entry, OffHeapConstants.NULL_PTR);
                        setType(addr, entry, (byte) OffHeapConstants.NULL_PTR);
                    } else {
                        //we need to reHash the new last element at our place
                        setKey(addr, entry, key(addr, indexVictim));
                        final byte typeOfVictim = type(addr, indexVictim);
                        if (typeOfVictim == Type.DOUBLE) {
                            final double victimDoubleValue = doubleValue(addr, indexVictim);
                            setDoubleValue(addr, entry, victimDoubleValue);
                        } else {
                            setValue(addr, entry, value(addr, indexVictim));
                        }
                        setType(addr, entry, typeOfVictim);
                        if (subhash_ptr != OffHeapConstants.NULL_PTR) {
                            setNext(addr, entry, next(subhash_ptr, indexVictim));
                            long victimHash = key(addr, entry) % (capacity * 2);
                            if (victimHash < 0) {
                                victimHash = victimHash * -1;
                            }
                            long m = hash(subhash_ptr, capacity, victimHash);
                            if (m == indexVictim) {
                                //the victim was the head of hashing list
                                setHash(subhash_ptr, capacity, victimHash, entry);
                            } else {
                                //the victim is in the next, rechain it
                                while (m != -1) {
                                    if (next(addr, m) == indexVictim) {
                                        setNext(subhash_ptr, m, entry);
                                        break;
                                    }
                                    m = next(subhash_ptr, m);
                                }
                            }
                        }
                        setKey(addr, indexVictim, OffHeapConstants.NULL_PTR);
                        freeElement(value(addr, indexVictim), type(addr, indexVictim));
                        setValue(addr, indexVictim, OffHeapConstants.NULL_PTR);
                        setType(addr, indexVictim, OffHeapConstants.NULL_PTR);
                    }
                    OffHeapLongArray.set(addr, SIZE, size - 1);
                } else {
                    final long previous_value = value(addr, entry);
                    //freeThePreviousValue
                    if (p_type == Type.DOUBLE) {
                        setDoubleValue(addr, entry, toDoubleValue(p_unsafe_elem));
                    } else {
                        setValue(addr, entry, toAddr(p_type, p_unsafe_elem));
                    }
                    freeElement(previous_value, found_type);
                    if (found_type != p_type) {
                        setType(addr, entry, p_type);
                    }
                }
            }
            if (!initial) {
                declareDirty();
            }
        }
        if (size >= capacity) {
            long newCapacity = capacity * 2;
            addr = allocate(addr, newCapacity);
            subhash_ptr = OffHeapLongArray.get(addr, SUBHASH);
            capacity = newCapacity;
            hashIndex = p_key % (capacity * 2);
            if (hashIndex < 0) {
                hashIndex = hashIndex * -1;
            }
        }
        final long insert_index = size;
        setKey(addr, insert_index, p_key);
        if (p_type == Type.DOUBLE) {
            setDoubleValue(addr, insert_index, toDoubleValue(p_unsafe_elem));
        } else {
            setValue(addr, insert_index, toAddr(p_type, p_unsafe_elem));
        }
        setType(addr, insert_index, p_type);
        if (subhash_ptr != OffHeapConstants.NULL_PTR) {
            setNext(subhash_ptr, insert_index, hash(subhash_ptr, capacity, hashIndex));
            setHash(subhash_ptr, capacity, hashIndex, insert_index);
        }
        size++;
        OffHeapLongArray.set(addr, SIZE, size);
        if (!initial) {
            declareDirty();
        }
        return insert_index;
    }

    @SuppressWarnings("Duplicates")
    private long toAddr(final byte p_type, final Object p_unsafe_elem) {
        long param_elem = -1;
        if (p_unsafe_elem != null) {
            try {
                switch (p_type) {
                    case Type.BOOL:
                        param_elem = ((boolean) p_unsafe_elem) ? 1 : 0;
                        break;
                    case Type.LONG:
                        if (p_unsafe_elem instanceof Long) {
                            param_elem = (long) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Double) {
                            param_elem = (long) (double) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Float) {
                            param_elem = (long) (float) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Integer) {
                            param_elem = (long) (int) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Byte) {
                            param_elem = (long) (byte) p_unsafe_elem;
                        } else {
                            param_elem = (long) p_unsafe_elem;
                        }
                        break;
                    case Type.INT:
                        if (p_unsafe_elem instanceof Integer) {
                            param_elem = (int) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Double) {
                            param_elem = (int) (double) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Float) {
                            param_elem = (int) (float) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Long) {
                            param_elem = (int) (long) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Byte) {
                            param_elem = (int) (byte) p_unsafe_elem;
                        } else {
                            param_elem = (int) p_unsafe_elem;
                        }
                        break;
                    case Type.STRING:
                        param_elem = OffHeapString.fromObject((String) p_unsafe_elem);
                        break;
                    case Type.DOUBLE_ARRAY:
                        param_elem = OffHeapDoubleArray.fromObject((double[]) p_unsafe_elem);
                        break;
                    case Type.LONG_ARRAY:
                        param_elem = OffHeapLongArray.fromObject((long[]) p_unsafe_elem);
                        break;
                    case Type.INT_ARRAY:
                        param_elem = OffHeapIntArray.fromObject((int[]) p_unsafe_elem);
                        break;
                    case Type.ENODE:
                        param_elem = ((OffHeapENode) p_unsafe_elem).getAddr();
                        break;
                    case Type.ERELATION:
                        OffHeapERelation eRelation = ((OffHeapERelation) p_unsafe_elem);
                        param_elem = addrByIndex(eRelation.index());
                        break;
                    case Type.RELATION:
                    case Type.DMATRIX:
                    case Type.LMATRIX:
                    case Type.STRING_TO_INT_MAP:
                    case Type.LONG_TO_LONG_MAP:
                    case Type.RELATION_INDEXED:
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        //throw new RuntimeException("mwDB usage error, set method called with type " + Type.typeName(p_type) + ", is getOrCreate method instead");
                        param_elem = OffHeapConstants.NULL_PTR; //empty initial ptr
                        break;
                    default:
                        throw new RuntimeException("Internal Exception, unknown type");
                }
            } catch (Exception e) {
                throw new RuntimeException("mwDB usage error, set method called with type " + Type.typeName(p_type) + " while param object is " + p_unsafe_elem);
            }
        }
        return param_elem;
    }

    @Override
    public ENode setAt(int key, byte type, Object value) {
        internal_set(key, type, value, true, false);
        return this;
    }

    @Override
    public Object get(String name) {
        return internal_get(_graph.resolver().stringToHash(name, false));
    }

    private Object internal_get(long p_key) {
        long size = OffHeapLongArray.get(addr, SIZE);
        //empty chunk, we return immediately
        if (size == 0) {
            return null;
        }
        long index = internal_find(p_key);
        if (index != -1) {
            int type = type(addr, index);
            long rawValue = value(addr, index);

            switch (type) {
                case Type.BOOL:
                    return rawValue == 1;
                case Type.DOUBLE:
                    return doubleValue(addr, index);
                case Type.LONG:
                    return rawValue;
                case Type.INT:
                    return (int) rawValue;
                case Type.STRING:
                    return OffHeapString.asObject(rawValue);
                case Type.DOUBLE_ARRAY:
                    return OffHeapDoubleArray.asObject(rawValue);
                case Type.LONG_ARRAY:
                    return OffHeapLongArray.asObject(rawValue);
                case Type.INT_ARRAY:
                    return OffHeapIntArray.asObject(rawValue);
                case Type.RELATION:
                    return new OffHeapRelation(this, index);
                case Type.ERELATION:
                    return new OffHeapERelation(this, egraph, _graph, index);
                case Type.ENODE:
                    return new OffHeapENode(egraph, _graph, OffHeapENode.getId(rawValue), rawValue);
                case Type.DMATRIX:
                    return new OffHeapDMatrix(this, index);
                case Type.LMATRIX:
                    return new OffHeapLMatrix(this, index);
                case Type.STRING_TO_INT_MAP:
                    return new OffHeapStringIntMap(this, index);
                case Type.LONG_TO_LONG_MAP:
                    return new OffHeapLongLongMap(this, index);
                case Type.LONG_TO_LONG_ARRAY_MAP:
                    return new OffHeapLongLongArrayMap(this, index);
                case Type.RELATION_INDEXED:
                    return new OffHeapRelationIndexed(this, index, _graph);
                case OffHeapConstants.NULL_PTR:
                    return null;
                default:
                    throw new RuntimeException("Should never happen " + type);
            }
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    private long internal_find(long p_key) {
        final long size = OffHeapLongArray.get(addr, SIZE);
        final long subhash_ptr = OffHeapLongArray.get(addr, SUBHASH);
        if (size == 0) {
            return -1;
        } else if (subhash_ptr == OffHeapConstants.NULL_PTR) {
            for (int i = 0; i < size; i++) {
                if (key(addr, i) == p_key) {
                    return i;
                }
            }
            return -1;
        } else {
            final long capacity = OffHeapLongArray.get(addr, CAPACITY);
            long hashIndex = p_key % (capacity * 2);
            if (hashIndex < 0) {
                hashIndex = hashIndex * -1;
            }
            long m = hash(subhash_ptr, capacity, hashIndex);
            while (m >= 0) {
                if (p_key == key(addr, m)) {
                    return m;
                } else {
                    m = next(subhash_ptr, m);
                }
            }
            return -1;
        }
    }

    @Override
    public Object getAt(int key) {
        return internal_get(key);
    }

    @Override
    public Object getOrCreate(String key, byte type) {
        Object previous = get(key);
        if (previous != null) {
            return previous;
        } else {
            return getOrCreateAt(egraph.graph().resolver().stringToHash(key, true), type);
        }
    }

    @Override
    public Object getOrCreateAt(int key, byte type) {
        long index = OffHeapLongArray.get(addr, SIZE);

        long found = internal_find(key);
        if (found != -1) {
            Object elem = internal_get(key);
            if (elem != null) {
                return elem;
            }
        }
        Object toSet = null;
        switch (type) {
            case Type.ERELATION:
                toSet = new OffHeapERelation(this, egraph, _graph, index);
                break;
            case Type.RELATION:
                toSet = new OffHeapRelation(this, index);
                break;
            case Type.RELATION_INDEXED:
                toSet = new OffHeapRelationIndexed(this, index, egraph.graph());
                break;
            case Type.DMATRIX:
                toSet = new OffHeapDMatrix(this, index);
                break;
            case Type.LMATRIX:
                toSet = new OffHeapLMatrix(this, index);
                break;
            case Type.STRING_TO_INT_MAP:
                toSet = new OffHeapStringIntMap(this, index);
                break;
            case Type.LONG_TO_LONG_MAP:
                toSet = new OffHeapLongLongMap(this, index);
                break;
            case Type.LONG_TO_LONG_ARRAY_MAP:
                toSet = new OffHeapLongLongArrayMap(this, index);
                break;
        }
        internal_set(key, type, toSet, true, false);
        return toSet;
    }

    @Override
    public void drop() {
        egraph.drop(this);
    }

    @Override
    public EGraph graph() {
        return egraph;
    }

    @Override
    public void each(NodeStateCallback callBack) {
        long size = OffHeapLongArray.get(addr, SIZE);
        for (long i = 0; i < size; i++) {
            if (value(addr, i) != OffHeapConstants.NULL_PTR) {
                long key = key(addr, i);
                byte type = type(addr, i);
                Object elem = internal_get(key);

                callBack.on((int) key, type, elem);
            }
        }
    }

    @Override
    public ENode clear() {
        long subhash = OffHeapLongArray.get(addr, SUBHASH);
        long size = OffHeapLongArray.get(addr, SIZE);

        OffHeapLongArray.set(addr, CAPACITY, 0);
        OffHeapLongArray.set(addr, SIZE, 0);
        OffHeapLongArray.set(addr, SUBHASH, OffHeapConstants.NULL_PTR);

        for (long i = 0; i < size; i++) {
            freeElement(value(addr, i), type(addr, i));
            setKey(addr, i, OffHeapConstants.NULL_PTR);
            setValue(addr, i, OffHeapConstants.NULL_PTR);
            setType(addr, i, OffHeapConstants.NULL_PTR);
        }
        if (subhash != OffHeapConstants.NULL_PTR) {
            OffHeapLongArray.free(subhash);
            OffHeapLongArray.set(addr, SUBHASH, OffHeapConstants.NULL_PTR);
        }
        OffHeapLongArray.free(addr);

        return this;
    }


    public long getAddr() {
        return this.addr;
    }

    @SuppressWarnings("Duplicates")
    private double toDoubleValue(final Object p_unsafe_elem) {
        if (p_unsafe_elem instanceof Double) {
            return (double) p_unsafe_elem;
        } else if (p_unsafe_elem instanceof Integer) {
            return (double) (int) p_unsafe_elem;
        } else if (p_unsafe_elem instanceof Float) {
            return (double) (float) p_unsafe_elem;
        } else if (p_unsafe_elem instanceof Long) {
            return (double) (long) p_unsafe_elem;
        } else if (p_unsafe_elem instanceof Byte) {
            return (double) (byte) p_unsafe_elem;
        }
        return (double) p_unsafe_elem;
    }

    static void rebase(long addr, long eGraphAddr) {
        long size = OffHeapLongArray.get(addr, SIZE);
        for (int i = 0; i < size; i++) {
            int type = type(addr, i);
            switch (type) {
                case Type.ERELATION:
                    final long previousERel_ptr = value(addr, i);
                    OffHeapERelation.rebase(previousERel_ptr, eGraphAddr);
                    break;
                case Type.ENODE:
                    final long previousENode_ptr = value(addr, i);
                    final long previousENodeId = OffHeapENode.getId(previousENode_ptr);
                    setValue(addr, i, OffHeapEGraph.nodeAddrAt(eGraphAddr, previousENodeId));
                    break;
            }
        }
    }

    static void setId(long addr, long id) {
        OffHeapLongArray.set(addr, ID, id);
    }

    static long getId(long addr) {
        return OffHeapLongArray.get(addr, ID);
    }

    @SuppressWarnings("Duplicates")
    static void free(long addr) {
        if (addr != OffHeapConstants.NULL_PTR) {
            final long subhash_ptr = OffHeapLongArray.get(addr, SUBHASH);
            final long size = OffHeapLongArray.get(addr, SIZE);
            for (long i = 0; i < size; i++) {
                long value = value(addr, i);
                byte type = type(addr, i);
                if (value != OffHeapConstants.NULL_PTR) {
                    freeElement(value, type);
                }
            }
            if (subhash_ptr != OffHeapConstants.NULL_PTR) {
                OffHeapLongArray.free(subhash_ptr);
            }
            OffHeapLongArray.free(addr);
        }
    }


    @SuppressWarnings("Duplicates")
    private static void freeElement(final long addr, final byte elemType) {
        switch (elemType) {
            case Type.BOOL:
            case Type.LONG:
            case Type.INT:
            case Type.DOUBLE:
                break;
            case Type.STRING:
                OffHeapString.free(addr);
                break;
            case Type.DOUBLE_ARRAY:
                OffHeapDoubleArray.freeObject(addr);
                break;
            case Type.RELATION:
                OffHeapRelation.free(addr);
            case Type.ERELATION:
                OffHeapERelation.free(addr);
                break;
            case Type.DMATRIX:
                OffHeapDMatrix.free(addr);
                break;
            case Type.LMATRIX:
                OffHeapLMatrix.free(addr);
                break;
            case Type.LONG_ARRAY:
                OffHeapLongArray.freeObject(addr);
                break;
            case Type.INT_ARRAY:
                OffHeapIntArray.freeObject(addr);
                break;
            case Type.STRING_TO_INT_MAP:
                OffHeapStringIntMap.free(addr);
                break;
            case Type.LONG_TO_LONG_MAP:
                OffHeapLongLongMap.free(addr);
                break;
            case Type.RELATION_INDEXED:
            case Type.LONG_TO_LONG_ARRAY_MAP:
                OffHeapLongLongArrayMap.free(addr);
                break;
//            case Type.ENODE:
//                OffHeapENode.freeElement(addr, elemType);
//                break;
//            default:
//                throw new RuntimeException("freeElement unknown type " + elemType);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        final boolean[] isFirst = {true};
        boolean isFirstField = true;
        long size = OffHeapLongArray.get(addr, SIZE);
        builder.append("{");
        for (int i = 0; i < size; i++) {
            final long elem = value(addr, i);
            final Resolver resolver = _graph.resolver();
            final long attributeKey = key(addr, i);
            final byte elemType = type(addr, i);
            if (elem != OffHeapConstants.NULL_PTR) {
                if (isFirstField) {
                    isFirstField = false;
                } else {
                    builder.append(",");
                }
                String resolveName = resolver.hashToString((int) attributeKey);
                if (resolveName == null) {
                    resolveName = attributeKey + "";
                }
                switch (elemType) {
                    case Type.BOOL: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        if (elem == 1) {
                            builder.append("1");
                        } else {
                            builder.append("0");
                        }
                        break;
                    }
                    case Type.STRING: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("\"");
                        builder.append(OffHeapString.asObject(elem));
                        builder.append("\"");
                        break;
                    }
                    case Type.LONG: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append(elem);
                        break;
                    }
                    case Type.INT: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append(elem);
                        break;
                    }
                    case Type.DOUBLE: {
                        if (!BaseNode.isNaN((double) elem)) {
                            builder.append("\"");
                            builder.append(resolveName);
                            builder.append("\":");
                            builder.append(elem);
                        }
                        break;
                    }
                    case Type.DOUBLE_ARRAY: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("[");
                        double[] castedArr = OffHeapDoubleArray.asObject(elem);
                        for (int j = 0; j < castedArr.length; j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            builder.append(castedArr[j]);
                        }
                        builder.append("]");
                        break;
                    }
                    case Type.RELATION:
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("[");
                        OffHeapRelation castedRelArr = new OffHeapRelation(this, i);
                        for (int j = 0; j < castedRelArr.size(); j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            builder.append(castedRelArr.get(j));
                        }
                        builder.append("]");
                        break;
                    case Type.ERELATION:
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("[");
                        OffHeapERelation castedERel = new OffHeapERelation(this, egraph, _graph, i);
                        for (int j = 0; j < castedERel.size(); j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            long eRelAddr = addrByIndex(castedERel.index());
                            long nodeAddr = OffHeapERelation.nodeAddrAt(eRelAddr, j);
                            builder.append(OffHeapENode.getId(nodeAddr));
                        }
                        builder.append("]");
                        break;
                    case Type.LONG_ARRAY: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("[");
                        long[] castedArr2 = OffHeapLongArray.asObject(elem);
                        for (int j = 0; j < castedArr2.length; j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            builder.append(castedArr2[j]);
                        }
                        builder.append("]");
                        break;
                    }
                    case Type.INT_ARRAY: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("[");
                        int[] castedArr3 = OffHeapIntArray.asObject(elem);
                        for (int j = 0; j < castedArr3.length; j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            builder.append(castedArr3[j]);
                        }
                        builder.append("]");
                        break;
                    }
                    case Type.LONG_TO_LONG_MAP: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("{");
                        OffHeapLongLongMap castedMapL2L = new OffHeapLongLongMap(this, i);
                        isFirst[0] = true;
                        castedMapL2L.each(new LongLongMapCallBack() {
                            @Override
                            public void on(long key, long value) {
                                if (!isFirst[0]) {
                                    builder.append(",");
                                } else {
                                    isFirst[0] = false;
                                }
                                builder.append("\"");
                                builder.append(key);
                                builder.append("\":");
                                builder.append(value);
                            }
                        });
                        builder.append("}");
                        break;
                    }
                    case Type.RELATION_INDEXED:
                    case Type.LONG_TO_LONG_ARRAY_MAP: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("{");
                        OffHeapLongLongArrayMap castedMapL2LA = new OffHeapLongLongArrayMap(this, i);
                        isFirst[0] = true;
                        Set<Long> keys = new HashSet<Long>();
                        castedMapL2LA.each(new LongLongArrayMapCallBack() {
                            @Override
                            public void on(long key, long value) {
                                keys.add(key);
                            }
                        });
                        final Long[] flatKeys = keys.toArray(new Long[keys.size()]);
                        for (int k = 0; k < flatKeys.length; k++) {
                            long[] values = castedMapL2LA.get(flatKeys[k]);
                            if (!isFirst[0]) {
                                builder.append(",");
                            } else {
                                isFirst[0] = false;
                            }
                            builder.append("\"");
                            builder.append(flatKeys[k]);
                            builder.append("\":[");
                            for (int j = 0; j < values.length; j++) {
                                if (j != 0) {
                                    builder.append(",");
                                }
                                builder.append(values[j]);
                            }
                            builder.append("]");
                        }
                        builder.append("}");
                        break;
                    }
                    case Type.STRING_TO_INT_MAP: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("{");
                        OffHeapStringIntMap castedMapS2L = new OffHeapStringIntMap(this, i);
                        isFirst[0] = true;
                        castedMapS2L.each(new StringLongMapCallBack() {
                            @Override
                            public void on(String key, long value) {
                                if (!isFirst[0]) {
                                    builder.append(",");
                                } else {
                                    isFirst[0] = false;
                                }
                                builder.append("\"");
                                builder.append(key);
                                builder.append("\":");
                                builder.append(value);
                            }
                        });
                        builder.append("}");
                        break;
                    }

                }
            }
        }
        builder.append("}");
        return builder.toString();
    }

    @SuppressWarnings("Duplicates")
    final void save(final Buffer buffer) {
        int size = (int) OffHeapLongArray.get(addr, SIZE);
        Base64.encodeIntToBuffer(size, buffer);
        for (int i = 0; i < size; i++) {
            if (value(addr, i) != OffHeapConstants.NULL_PTR) { //there is a real value
                final long loopValue = value(addr, i);
                if (loopValue != OffHeapConstants.NULL_PTR) {
                    buffer.write(CoreConstants.CHUNK_ESEP);
                    Base64.encodeIntToBuffer(type(addr, i), buffer);
                    buffer.write(CoreConstants.CHUNK_ESEP);
                    Base64.encodeLongToBuffer(key(addr, i), buffer);
                    buffer.write(CoreConstants.CHUNK_ESEP);
                    switch (type(addr, i)) {
                        //additional types for embedded
                        case Type.ENODE:
                            Base64.encodeIntToBuffer((int) OffHeapLongArray.get(loopValue, ID), buffer);
                            break;
                        case Type.ERELATION:
                            OffHeapERelation castedLongArrERel = new OffHeapERelation(this, egraph, _graph, i);
                            Base64.encodeIntToBuffer(castedLongArrERel.size(), buffer);
                            for (int j = 0; j < castedLongArrERel.size(); j++) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                long nodeAddr = OffHeapERelation.nodeAddrAt(loopValue, j);
                                int nodeId = (int) OffHeapENode.getId(nodeAddr);
                                Base64.encodeIntToBuffer(nodeId, buffer);
                            }
                            break;
                        //common types
                        case Type.STRING:
                            OffHeapString.save(loopValue, buffer);
                            break;
                        case Type.BOOL:
                            if (loopValue == 1) {
                                Base64.encodeIntToBuffer(CoreConstants.BOOL_TRUE, buffer);
                            } else {
                                Base64.encodeIntToBuffer(CoreConstants.BOOL_FALSE, buffer);
                            }
                            break;
                        case Type.LONG:
                            Base64.encodeLongToBuffer(loopValue, buffer);
                            break;
                        case Type.DOUBLE:
                            Base64.encodeDoubleToBuffer(doubleValue(addr, i), buffer);
                            break;
                        case Type.INT:
                            Base64.encodeIntToBuffer((int) loopValue, buffer);
                            break;
                        case Type.DOUBLE_ARRAY:
                            OffHeapDoubleArray.save(loopValue, buffer);
                            break;
                        case Type.RELATION:
                            OffHeapRelation.save(loopValue, buffer);
                            break;
                        case Type.LONG_ARRAY:
                            OffHeapLongArray.save(loopValue, buffer);
                            break;
                        case Type.INT_ARRAY:
                            OffHeapIntArray.save(loopValue, buffer);
                            break;
                        case Type.DMATRIX:
                            OffHeapDMatrix.save(loopValue, buffer);
                            break;
                        case Type.LMATRIX:
                            OffHeapLMatrix.save(loopValue, buffer);
                            break;
                        case Type.STRING_TO_INT_MAP:
                            OffHeapStringIntMap.save(loopValue, buffer);
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            OffHeapLongLongMap.save(loopValue, buffer);
                            break;
                        case Type.RELATION_INDEXED:
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            OffHeapLongLongArrayMap.save(loopValue, buffer);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        OffHeapLongArray.set(addr, DIRTY, 0);
    }


    @SuppressWarnings("Duplicates")
    public final long load(final Buffer buffer, final long currentCursor) {
        final boolean initial = addr == OffHeapConstants.NULL_PTR;

        final long payloadSize = buffer.length();
        long cursor = currentCursor;
        long previous = cursor;
        byte state = LOAD_WAITING_ALLOC;
        byte read_type = -1;
        long read_key = -1;
        while (cursor < payloadSize) {
            byte current = buffer.read(cursor);
            if (current == Constants.CHUNK_ENODE_SEP || current == Constants.CHUNK_SEP) {
                break;
            } else if (current == Constants.CHUNK_ESEP) {
                switch (state) {
                    case LOAD_WAITING_ALLOC:
                        allocate(OffHeapConstants.NULL_PTR, Base64.decodeToLongWithBounds(buffer, previous, cursor));
                        state = LOAD_WAITING_TYPE;
                        cursor++;
                        previous = cursor;
                        break;
                    case LOAD_WAITING_TYPE:
                        read_type = (byte) Base64.decodeToIntWithBounds(buffer, previous, cursor);
                        state = LOAD_WAITING_KEY;
                        cursor++;
                        previous = cursor;
                        break;
                    case LOAD_WAITING_KEY:
                        read_key = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                        //primitive default loader
                        switch (read_type) {
                            //primitive types
                            case Type.BOOL:
                            case Type.INT:
                            case Type.DOUBLE:
                            case Type.LONG:
                            case Type.STRING:
                            case Type.ENODE:
                                state = LOAD_WAITING_VALUE;
                                cursor++;
                                previous = cursor;
                                break;
                            //arrays
                            case Type.DOUBLE_ARRAY:
                                double[] doubleArrayLoaded = null;
                                int doubleArrayIndex = 0;
                                cursor++;
                                previous = cursor;
                                current = buffer.read(cursor);
                                while (cursor < payloadSize && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP && current != Constants.CHUNK_ESEP) {
                                    if (current == Constants.CHUNK_VAL_SEP) {
                                        if (doubleArrayLoaded == null) {
                                            doubleArrayLoaded = new double[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                        } else {
                                            doubleArrayLoaded[doubleArrayIndex] = Base64.decodeToDoubleWithBounds(buffer, previous, cursor);
                                            doubleArrayIndex++;
                                        }
                                        previous = cursor + 1;
                                    }
                                    cursor++;
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                    }
                                }
                                if (doubleArrayLoaded == null) {
                                    doubleArrayLoaded = new double[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                } else {
                                    doubleArrayLoaded[doubleArrayIndex] = Base64.decodeToDoubleWithBounds(buffer, previous, cursor);
                                }
                                internal_set(read_key, read_type, doubleArrayLoaded, true, initial);
                                if (current == Constants.CHUNK_ESEP && cursor < payloadSize) {
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                }
                                break;
                            case Type.LONG_ARRAY:
                                long[] longArrayLoaded = null;
                                int longArrayIndex = 0;
                                cursor++;
                                previous = cursor;
                                current = buffer.read(cursor);
                                while (cursor < payloadSize && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP && current != Constants.CHUNK_ESEP) {
                                    if (current == Constants.CHUNK_VAL_SEP) {
                                        if (longArrayLoaded == null) {
                                            longArrayLoaded = new long[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                        } else {
                                            longArrayLoaded[longArrayIndex] = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                            longArrayIndex++;
                                        }
                                        previous = cursor + 1;
                                    }
                                    cursor++;
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                    }
                                }
                                if (longArrayLoaded == null) {
                                    longArrayLoaded = new long[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                } else {
                                    longArrayLoaded[longArrayIndex] = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                }
                                internal_set(read_key, read_type, longArrayLoaded, true, initial);
                                if (current == Constants.CHUNK_ESEP && cursor < payloadSize) {
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                }
                                break;
                            case Type.INT_ARRAY:
                                int[] intArrayLoaded = null;
                                int intArrayIndex = 0;
                                cursor++;
                                previous = cursor;
                                current = buffer.read(cursor);
                                while (cursor < payloadSize && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP && current != Constants.CHUNK_ESEP) {
                                    if (current == Constants.CHUNK_VAL_SEP) {
                                        if (intArrayLoaded == null) {
                                            intArrayLoaded = new int[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                        } else {
                                            intArrayLoaded[intArrayIndex] = Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                            intArrayIndex++;
                                        }
                                        previous = cursor + 1;
                                    }
                                    cursor++;
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                    }
                                }
                                if (intArrayLoaded == null) {
                                    intArrayLoaded = new int[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                } else {
                                    intArrayLoaded[intArrayIndex] = Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                }
                                internal_set(read_key, read_type, intArrayLoaded, true, initial);
                                if (current == Constants.CHUNK_ESEP && cursor < payloadSize) {
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                }
                                break;
                            case Type.RELATION:
                                OffHeapRelation relation = new OffHeapRelation(this, internal_set(read_key, read_type, null, true, initial));
                                cursor++;
                                cursor = relation.load(buffer, cursor, payloadSize);
                                cursor++;
                                previous = cursor;
                                state = LOAD_WAITING_TYPE;
                                break;
                            case Type.DMATRIX:
                                OffHeapDMatrix matrix = new OffHeapDMatrix(this, internal_set(read_key, read_type, null, true, initial));
                                cursor++;
                                cursor = matrix.load(buffer, cursor, payloadSize);
                                if (cursor < payloadSize) {
                                    current = buffer.read(cursor);
                                    if (current == Constants.CHUNK_ESEP && cursor < payloadSize) {
                                        state = LOAD_WAITING_TYPE;
                                        cursor++;
                                        previous = cursor;
                                    }
                                }
                                break;
                            case Type.LMATRIX:
                                OffHeapLMatrix lmatrix = new OffHeapLMatrix(this, internal_set(read_key, read_type, null, true, initial));
                                cursor++;
                                cursor = lmatrix.load(buffer, cursor, payloadSize);
                                if (cursor < payloadSize) {
                                    current = buffer.read(cursor);
                                    if (current == Constants.CHUNK_ESEP && cursor < payloadSize) {
                                        state = LOAD_WAITING_TYPE;
                                        cursor++;
                                        previous = cursor;
                                    }
                                }
                                break;
                            case Type.LONG_TO_LONG_MAP:
                                OffHeapLongLongMap l2lmap = new OffHeapLongLongMap(this, internal_set(read_key, read_type, null, true, initial));
                                cursor++;
                                cursor = l2lmap.load(buffer, cursor, payloadSize);
                                if (cursor < payloadSize) {
                                    current = buffer.read(cursor);
                                    if (current == Constants.CHUNK_ESEP && cursor < payloadSize) {
                                        state = LOAD_WAITING_TYPE;
                                        cursor++;
                                        previous = cursor;
                                    }
                                }
                                break;
                            case Type.LONG_TO_LONG_ARRAY_MAP:
                                OffHeapLongLongArrayMap l2lrmap = new OffHeapLongLongArrayMap(this, internal_set(read_key, read_type, null, true, initial));
                                cursor++;
                                cursor = l2lrmap.load(buffer, cursor, payloadSize);
                                if (cursor < payloadSize) {
                                    current = buffer.read(cursor);
                                    if (current == Constants.CHUNK_ESEP && cursor < payloadSize) {
                                        state = LOAD_WAITING_TYPE;
                                        cursor++;
                                        previous = cursor;
                                    }
                                }
                                break;
                            case Type.RELATION_INDEXED:
                                OffHeapRelationIndexed relationIndexed = new OffHeapRelationIndexed(this, internal_set(read_key, read_type, null, true, initial), _graph);
                                cursor++;
                                cursor = relationIndexed.load(buffer, cursor, payloadSize);
                                if (cursor < payloadSize) {
                                    current = buffer.read(cursor);
                                    if (current == Constants.CHUNK_ESEP && cursor < payloadSize) {
                                        state = LOAD_WAITING_TYPE;
                                        cursor++;
                                        previous = cursor;
                                    }
                                }
                                break;
                            case Type.STRING_TO_INT_MAP:
                                OffHeapStringIntMap s2lmap = new OffHeapStringIntMap(this, internal_set(read_key, read_type, null, true, initial));
                                cursor++;
                                cursor = s2lmap.load(buffer, cursor, payloadSize);
                                if (cursor < payloadSize) {
                                    current = buffer.read(cursor);
                                    if (current == Constants.CHUNK_ESEP && cursor < payloadSize) {
                                        state = LOAD_WAITING_TYPE;
                                        cursor++;
                                        previous = cursor;
                                    }
                                }
                                break;
                            case Type.ERELATION:
                                OffHeapERelation eRelation = null;
                                cursor++;
                                previous = cursor;
                                current = buffer.read(cursor);
                                while (cursor < payloadSize && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP && current != Constants.CHUNK_ESEP) {
                                    if (current == Constants.CHUNK_VAL_SEP) {
                                        if (eRelation == null) {
                                            eRelation = new OffHeapERelation(this, egraph, _graph, internal_set(read_key, read_type, null, true, initial));
                                            eRelation.allocate(Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                        } else {
                                            eRelation.add(egraph.nodeByIndex((int) Base64.decodeToLongWithBounds(buffer, previous, cursor), true));
                                        }
                                        previous = cursor + 1;
                                    }
                                    cursor++;
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                    }
                                }
                                if (eRelation == null) {
                                    eRelation = new OffHeapERelation(this, egraph, _graph, internal_set(read_key, read_type, null, true, initial));
                                    eRelation.allocate(Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                } else {
                                    eRelation.add(egraph.nodeByIndex(Base64.decodeToIntWithBounds(buffer, previous, cursor), true));
                                }
                                if (current == Constants.CHUNK_ESEP && cursor < payloadSize) {
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                }
                                break;
                            default:
                                throw new RuntimeException("Not implemented yet!!!");
                        }
                        break;
                    case LOAD_WAITING_VALUE:
                        load_primitive(read_key, read_type, buffer, previous, cursor, initial);
                        state = LOAD_WAITING_TYPE;
                        cursor++;
                        previous = cursor;
                        break;
                }
            } else {
                cursor++;
            }
        }
        if (state == LOAD_WAITING_VALUE) {
            load_primitive(read_key, read_type, buffer, previous, cursor, initial);
        }
        return cursor;
    }

    @SuppressWarnings("Duplicates")
    private void load_primitive(final long read_key, final byte read_type, final Buffer buffer, final long previous, final long cursor, final boolean initial) {
        switch (read_type) {
            case Type.BOOL:
                internal_set(read_key, read_type, (((byte) Base64.decodeToIntWithBounds(buffer, previous, cursor)) == CoreConstants.BOOL_TRUE), true, initial);
                break;
            case Type.INT:
                internal_set(read_key, read_type, Base64.decodeToIntWithBounds(buffer, previous, cursor), true, initial);
                break;
            case Type.DOUBLE:
                internal_set(read_key, read_type, Base64.decodeToDoubleWithBounds(buffer, previous, cursor), true, initial);
                break;
            case Type.LONG:
                internal_set(read_key, read_type, Base64.decodeToLongWithBounds(buffer, previous, cursor), true, initial);
                break;
            case Type.STRING:
                internal_set(read_key, read_type, Base64.decodeToStringWithBounds(buffer, previous, cursor), true, initial);
                break;
            case Type.ENODE:
                internal_set(read_key, read_type, Base64.decodeToIntWithBounds(buffer, previous, cursor), true, initial);
                break;
        }
    }

    @Override
    public long addrByIndex(long elemIndex) {
        return value(addr, elemIndex);
    }

    @Override
    public void setAddrByIndex(long elemIndex, long newAddr) {
        setValue(addr, elemIndex, newAddr);
    }

    @Override
    public void lock() {
        // no locking for OffHeapENode
    }

    @Override
    public void unlock() {
        // no locking for OffHeapENode
    }
}
