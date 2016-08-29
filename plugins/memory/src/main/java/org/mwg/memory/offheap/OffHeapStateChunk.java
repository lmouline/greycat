package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Type;
import org.mwg.chunk.StateChunk;
import org.mwg.utility.Base64;
import org.mwg.chunk.ChunkType;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.struct.*;
import org.mwg.utility.HashHelper;

class OffHeapStateChunk implements StateChunk {

    private final OffHeapChunkSpace space;
    private final long index;
    private final long addr;

    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int NEXT = 2;
    private static final int HASH = 3;
    private static final int TYPES = 4;

    private static final int LOCK = 5;
    private static final int SIZE = 6;
    private static final int CAPACITY = 7;
    private static final int DIRTY = 8;

    private static final int CHUNK_SIZE = 9;

    private long keys_ptr = -1;
    private long values_ptr = -1;
    private long next_ptr = -1;
    private long hash_ptr = -1;
    private long types_ptr = -1;

    @Override
    public final long world() {
        return space.worldByIndex(index);
    }

    @Override
    public final long time() {
        return space.timeByIndex(index);
    }

    @Override
    public final long id() {
        return space.idByIndex(index);
    }

    private void consistencyCheck() {
        if (OffHeapLongArray.get(this.addr, NEXT) != next_ptr) {
            keys_ptr = OffHeapLongArray.get(addr, KEYS);
            values_ptr = OffHeapLongArray.get(addr, VALUES);
            next_ptr = OffHeapLongArray.get(addr, NEXT);
            hash_ptr = OffHeapLongArray.get(addr, HASH);
            types_ptr = OffHeapLongArray.get(addr, TYPES);
        }
    }

    OffHeapStateChunk(final OffHeapChunkSpace p_space, final long p_index) {
        index = p_index;
        space = p_space;
        long temp_addr = space.addrByIndex(index);
        if (temp_addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            temp_addr = OffHeapLongArray.allocate(CHUNK_SIZE);
            space.setAddrByIndex(index, temp_addr);
            OffHeapLongArray.set(temp_addr, LOCK, 0);
            OffHeapLongArray.set(temp_addr, CAPACITY, 0);
            OffHeapLongArray.set(temp_addr, DIRTY, 0);
            OffHeapLongArray.set(temp_addr, SIZE, 0);
            OffHeapLongArray.set(temp_addr, KEYS, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(temp_addr, VALUES, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(temp_addr, TYPES, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(temp_addr, NEXT, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(temp_addr, HASH, OffHeapConstants.OFFHEAP_NULL_PTR);
        }
        addr = temp_addr;
    }

    void lock() {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        consistencyCheck();
    }

    void unlock() {
        if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) {
            System.err.println("CAS Error!");
        }
    }

    long addrByIndex(long index) {
        return OffHeapLongArray.get(values_ptr, index);
    }

    void setAddrByIndex(long index, long newAddr) {
        OffHeapLongArray.set(values_ptr, index, newAddr);
    }

    @Override
    public final byte chunkType() {
        return ChunkType.STATE_CHUNK;
    }

    @Override
    public final long index() {
        return index;
    }

    @Override
    public final Object get(final long p_key) {
        Object result = null;
        lock();
        try {
            result = internal_get(internal_find(p_key));
        } finally {
            unlock();
        }
        return result;
    }

    @Override
    public final Object getFromKey(final String key) {
        return get(space.graph().resolver().stringToHash(key, false));
    }

    @Override
    public final void each(final NodeStateCallback callBack) {
        lock();
        try {
            final long size = OffHeapLongArray.get(addr, SIZE);
            for (int i = 0; i < size; i++) {
                callBack.on(OffHeapLongArray.get(keys_ptr, i), OffHeapByteArray.get(types_ptr, i), OffHeapLongArray.get(values_ptr, i));
            }
        } finally {
            unlock();
        }
    }

    private Object internal_get(final long p_index) {
        if (p_index < 0) {
            return null;
        }
        if (p_index != -1) {
            final byte elemType = OffHeapByteArray.get(types_ptr, p_index);
            final long rawValue = OffHeapLongArray.get(values_ptr, p_index);
            switch (elemType) {
                case Type.BOOL:
                    return rawValue == 1;
                case Type.DOUBLE:
                    return OffHeapDoubleArray.get(values_ptr, p_index);
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
                    return new OffHeapRelationship(this, p_index);
                case Type.STRING_TO_LONG_MAP:
                    return new OffHeapStringLongMap(this, p_index);
                case Type.LONG_TO_LONG_MAP:
                    return new OffHeapLongLongMap(this, p_index);
                case Type.LONG_TO_LONG_ARRAY_MAP:
                    return new OffHeapLongLongArrayMap(this, p_index);
                case OffHeapConstants.OFFHEAP_NULL_PTR:
                    return null;
                default:
                    throw new RuntimeException("Should never happen");
            }
        }
        return null;
    }

    private long internal_find(final long p_key) {
        final long size = OffHeapLongArray.get(addr, SIZE);
        if (size == 0) {
            return -1;
        } else if (hash_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            for (int i = 0; i < size; i++) {
                if (OffHeapLongArray.get(keys_ptr, i) == p_key) {
                    return i;
                }
            }
            return -1;
        } else {
            final long capacity = OffHeapLongArray.get(addr, CAPACITY);
            final int hashIndex = (int) HashHelper.longHash(p_key, capacity * 2);
            long m = OffHeapLongArray.get(hash_ptr, hashIndex);
            while (m >= 0) {
                if (p_key == OffHeapLongArray.get(keys_ptr, m)) {
                    return m;
                } else {
                    m = OffHeapLongArray.get(next_ptr, m);
                }
            }
            return -1;
        }
    }

    @Override
    public final Object getOrCreate(final long p_key, final byte p_type) {
        Object result = null;
        lock();
        try {
            long found = internal_find(p_key);
            if (found == OffHeapConstants.OFFHEAP_NULL_PTR || OffHeapByteArray.get(types_ptr, found) != p_type) {
                found = internal_set(p_key, p_type, null, true, false);
            }
            result = internal_get(found);
        } finally {
            unlock();
        }
        return result;
    }

    @Override
    public final Object getOrCreateFromKey(final String key, final byte elemType) {
        return getOrCreate(space.graph().resolver().stringToHash(key, true), elemType);
    }

    @Override
    public final void set(final long p_elementIndex, final byte p_elemType, final Object p_unsafe_elem) {
        lock();
        try {
            internal_set(p_elementIndex, p_elemType, p_unsafe_elem, true, false);
        } finally {
            unlock();
        }
    }

    @Override
    public final void setFromKey(final String key, final byte p_elemType, final Object p_unsafe_elem) {
        set(space.graph().resolver().stringToHash(key, true), p_elemType, p_unsafe_elem);
    }

    @Override
    public final <A> A getFromKeyWithDefault(final String key, final A defaultValue) {
        final Object result = getFromKey(key);
        if (result == null) {
            return defaultValue;
        } else {
            return (A) result;
        }
    }

    @Override
    public final byte getType(final long p_key) {
        byte result = -1;
        lock();
        try {
            final long index = internal_find(p_key);
            if (index != OffHeapConstants.OFFHEAP_NULL_PTR) {
                result = OffHeapByteArray.get(types_ptr, index);
            }
        } finally {
            unlock();
        }
        return result;
    }

    @Override
    public byte getTypeFromKey(final String key) {
        return getType(space.graph().resolver().stringToHash(key, false));
    }

    final void declareDirty() {
        if (space != null && OffHeapLongArray.get(addr, DIRTY) != 1) {
            OffHeapLongArray.set(addr, DIRTY, 1);
            space.notifyUpdate(index);
        }
    }

    @Override
    public final void save(final Buffer buffer) {
        lock();
        try {
            long size = OffHeapLongArray.get(addr, SIZE);
            Base64.encodeLongToBuffer(size, buffer);
            for (int i = 0; i < size; i++) {
                buffer.write(Constants.CHUNK_SEP);
                Base64.encodeLongToBuffer(OffHeapLongArray.get(keys_ptr, i), buffer);
                buffer.write(Constants.CHUNK_SUB_SEP);
                Base64.encodeIntToBuffer(OffHeapByteArray.get(types_ptr, i), buffer);
                buffer.write(Constants.CHUNK_SUB_SEP);
                final byte type = OffHeapByteArray.get(types_ptr, i);
                final long rawValue = OffHeapLongArray.get(values_ptr, i);
                switch (type) {
                    case Type.STRING:
                        OffHeapString.save(rawValue, buffer);
                        break;
                    case Type.BOOL:
                        if (rawValue == 1) {
                            buffer.write(Constants.BOOL_TRUE);
                        } else {
                            buffer.write(Constants.BOOL_FALSE);
                        }
                        break;
                    case Type.LONG:
                        Base64.encodeLongToBuffer(rawValue, buffer);
                        break;
                    case Type.DOUBLE:
                        Base64.encodeDoubleToBuffer(OffHeapDoubleArray.get(values_ptr, i), buffer);
                        break;
                    case Type.INT:
                        Base64.encodeIntToBuffer((int) rawValue, buffer);
                        break;
                    case Type.DOUBLE_ARRAY:
                        OffHeapDoubleArray.save(rawValue, buffer);
                        break;
                    case Type.LONG_ARRAY:
                        OffHeapLongArray.save(rawValue, buffer);
                        break;
                    case Type.INT_ARRAY:
                        OffHeapLongArray.save(rawValue, buffer);
                        break;
                    case Type.RELATION:
                        OffHeapRelationship.save(rawValue, buffer);
                        break;
                    case Type.STRING_TO_LONG_MAP:
                        OffHeapStringLongMap.save(rawValue, buffer);
                        break;
                    case Type.LONG_TO_LONG_MAP:
                        OffHeapLongLongMap.save(rawValue, buffer);
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        OffHeapLongLongArrayMap.save(rawValue, buffer);
                        break;
                    default:
                        break;
                }
            }
        } finally {
            unlock();
        }
    }

    @Override
    public void loadFrom(final StateChunk origin) {
        if (origin == null) {
            return;
        }
        lock();
        try {
            if (keys_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(keys_ptr);
            }
            if (values_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(values_ptr);
            }
            if (types_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapByteArray.free(types_ptr);
            }
            OffHeapStateChunk casted = (OffHeapStateChunk) origin;
            final long origin_addr = casted.addr;
            final long previousCapacity = OffHeapLongArray.get(origin_addr, CAPACITY);
            OffHeapLongArray.set(addr, CAPACITY, previousCapacity);
            final long previousSize = OffHeapLongArray.get(origin_addr, SIZE);
            OffHeapLongArray.set(addr, SIZE, previousSize);
            //copy main structure
            long new_keys = OffHeapLongArray.cloneArray(OffHeapLongArray.get(origin_addr, KEYS), previousCapacity);
            OffHeapLongArray.set(addr, KEYS, new_keys);
            long new_values = OffHeapLongArray.cloneArray(OffHeapLongArray.get(origin_addr, VALUES), previousCapacity);
            OffHeapLongArray.set(addr, VALUES, new_values);
            long new_types = OffHeapByteArray.cloneArray(OffHeapLongArray.get(origin_addr, TYPES), previousCapacity);
            OffHeapLongArray.set(addr, TYPES, new_types);
            //copy next if not empty
            long previous_next_addr = OffHeapLongArray.get(origin_addr, NEXT);
            if (previous_next_addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long new_next = OffHeapLongArray.cloneArray(previous_next_addr, previousCapacity);
                OffHeapLongArray.set(addr, NEXT, new_next);
            }
            //copy hash if not empty
            long previous_hash_addr = OffHeapLongArray.get(origin_addr, HASH);
            if (previous_hash_addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long new_hash = OffHeapLongArray.cloneArray(previous_hash_addr, previousCapacity * 2);
                OffHeapLongArray.set(addr, HASH, new_hash);
            }
            for (int i = 0; i < previousSize; i++) {
                final byte loopType = OffHeapByteArray.get(types_ptr, i);
                final long loopValue = OffHeapLongArray.get(values_ptr, i);
                switch (loopType) {
                    case Type.STRING:
                        OffHeapString.clone(loopValue);
                        break;
                    case Type.RELATION:
                        OffHeapLongArray.set(values_ptr, i, OffHeapRelationship.clone(loopValue));
                        break;
                    case Type.LONG_TO_LONG_MAP:
                        OffHeapLongArray.set(values_ptr, i, OffHeapLongLongMap.clone(loopValue));
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        OffHeapLongArray.set(values_ptr, i, OffHeapLongLongArrayMap.clone(loopValue));
                        break;
                    case Type.STRING_TO_LONG_MAP:
                        OffHeapLongArray.set(values_ptr, i, OffHeapStringLongMap.clone(loopValue));
                        break;
                }
            }
        } finally {
            unlock();
        }
    }

    private long internal_set(final long p_key, final byte p_type, final Object p_unsafe_elem, boolean replaceIfPresent, boolean initial) {
        long param_elem = -1;
        double param_double_elem = -1;
        //check the param type
        if (p_unsafe_elem != null) {
            try {
                switch (p_type) {
                    case Type.BOOL:
                        param_elem = ((boolean) p_unsafe_elem) ? 1 : 0;
                        break;
                    case Type.DOUBLE:
                        param_double_elem = (double) p_unsafe_elem;
                        break;
                    case Type.LONG:
                        if (p_unsafe_elem instanceof Integer) {
                            int preCasting = (Integer) p_unsafe_elem;
                            param_elem = (long) preCasting;
                        } else {
                            param_elem = (long) p_unsafe_elem;
                        }
                        break;
                    case Type.INT:
                        param_elem = (int) p_unsafe_elem;
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
                    case Type.RELATION:
                    case Type.STRING_TO_LONG_MAP:
                    case Type.LONG_TO_LONG_MAP:
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        param_elem = OffHeapConstants.OFFHEAP_NULL_PTR; //empty initial ptr
                        break;
                    default:
                        throw new RuntimeException("Internal Exception, unknown type");
                }
            } catch (Exception e) {
                throw new RuntimeException("mwDB usage error, set method called with type " + Type.typeName(p_type) + " while param object is " + p_unsafe_elem);
            }
        }
        //first value
        if (keys_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            long new_capacity = Constants.MAP_INITIAL_CAPACITY;
            OffHeapLongArray.set(addr, CAPACITY, new_capacity);
            long new_keys = OffHeapLongArray.allocate(new_capacity);
            OffHeapLongArray.set(addr, KEYS, new_keys);
            long new_values = OffHeapLongArray.allocate(new_capacity);
            OffHeapLongArray.set(addr, VALUES, new_values);
            long new_types = OffHeapByteArray.allocate(new_capacity);
            OffHeapLongArray.set(addr, TYPES, new_types);
            OffHeapLongArray.set(keys_ptr, 0, p_key);
            OffHeapLongArray.set(types_ptr, 0, p_type);
            if (p_type == Type.DOUBLE) {
                OffHeapDoubleArray.set(values_ptr, 0, param_double_elem);
            } else {
                OffHeapLongArray.set(values_ptr, 0, param_elem);
            }
            OffHeapLongArray.set(addr, SIZE, 1);
            return 0;
        }
        long entry = -1;
        long p_entry = -1;
        long hashIndex = -1;
        long size = OffHeapLongArray.get(addr, SIZE);
        long capacity = OffHeapLongArray.get(addr, CAPACITY);
        if (hash_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            for (int i = 0; i < size; i++) {
                if (OffHeapLongArray.get(keys_ptr, i) == p_key) {
                    entry = i;
                    break;
                }
            }
        } else {
            hashIndex = HashHelper.longHash(p_key, capacity * 2);
            long m = OffHeapLongArray.get(hash_ptr, hashIndex);
            while (m != -1) {
                if (OffHeapLongArray.get(keys_ptr, m) == p_key) {
                    entry = m;
                    break;
                }
                p_entry = m;
                m = OffHeapLongArray.get(next_ptr, m);
            }
        }
        //case already present
        if (entry != -1) {
            if (replaceIfPresent || (p_type != OffHeapLongArray.get(types_ptr, entry))) {
                if (p_unsafe_elem == null) {
                    if (hash_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                        //unHash previous
                        if (p_entry != -1) {
                            OffHeapLongArray.set(next_ptr, p_entry, OffHeapLongArray.get(next_ptr, entry));
                        } else {
                            OffHeapLongArray.set(hash_ptr, hashIndex, -1);
                        }
                    }
                    long indexVictim = size - 1;
                    //just pop the last value
                    if (entry == indexVictim) {
                        OffHeapLongArray.set(keys_ptr, entry, OffHeapConstants.OFFHEAP_NULL_PTR);
                        OffHeapLongArray.set(values_ptr, entry, OffHeapConstants.OFFHEAP_NULL_PTR);
                        OffHeapLongArray.set(types_ptr, entry, OffHeapConstants.OFFHEAP_NULL_PTR);
                    } else {
                        //we need to reHash the new last element at our place
                        OffHeapLongArray.set(keys_ptr, entry, OffHeapLongArray.get(keys_ptr, indexVictim));
                        OffHeapLongArray.set(values_ptr, entry, OffHeapLongArray.get(values_ptr, indexVictim));
                        OffHeapLongArray.set(types_ptr, entry, OffHeapLongArray.get(types_ptr, indexVictim));
                        if (hash_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                            OffHeapLongArray.set(next_ptr, entry, OffHeapLongArray.get(next_ptr, indexVictim));
                            long victimHash = HashHelper.longHash(OffHeapLongArray.get(keys_ptr, entry), capacity * 2);
                            long m = OffHeapLongArray.get(hash_ptr, victimHash);
                            if (m == indexVictim) {
                                //the victim was the head of hashing list
                                OffHeapLongArray.set(hash_ptr, victimHash, entry);
                            } else {
                                //the victim is in the next, rechain it
                                while (m != -1) {
                                    if (OffHeapLongArray.get(next_ptr, m) == indexVictim) {
                                        OffHeapLongArray.set(next_ptr, m, entry);
                                        break;
                                    }
                                    m = OffHeapLongArray.get(next_ptr, m);
                                }
                            }
                        }
                    }
                    size--;
                    OffHeapLongArray.set(addr, SIZE, size);
                } else {
                    final byte previous_type = OffHeapByteArray.get(types_ptr, entry);
                    final long previous_value = OffHeapLongArray.get(values_ptr, entry);
                    if (previous_value != OffHeapConstants.OFFHEAP_NULL_PTR) {
                        switch (previous_type) {
                            case Type.RELATION:
                                OffHeapRelationship.free(previous_value);
                                break;
                            case Type.STRING:
                                OffHeapString.free(previous_value);
                                break;
                            case Type.LONG_TO_LONG_MAP:
                                OffHeapLongLongMap.free(previous_value);
                                break;
                            case Type.LONG_TO_LONG_ARRAY_MAP:
                                OffHeapLongLongArrayMap.free(previous_value);
                                break;
                            case Type.STRING_TO_LONG_MAP:
                                OffHeapLongLongArrayMap.free(previous_value);
                                break;
                            case Type.DOUBLE_ARRAY:
                                OffHeapDoubleArray.free(previous_value);
                                break;
                            case Type.INT_ARRAY:
                                OffHeapIntArray.free(previous_value);
                                break;
                            case Type.LONG_ARRAY:
                                OffHeapLongArray.free(previous_value);
                                break;
                        }
                    }
                    OffHeapLongArray.set(values_ptr, entry, param_elem);
                    if (previous_type != p_type) {
                        OffHeapLongArray.set(types_ptr, entry, p_type);
                    }
                }
            }
            if (!initial) {
                declareDirty();
            }
            return entry;
        }
        if (size < capacity) {
            final long insert_index = size;
            OffHeapLongArray.set(keys_ptr, insert_index, p_key);
            if (p_type == Type.DOUBLE) {
                OffHeapDoubleArray.set(values_ptr, insert_index, param_double_elem);
            } else {
                OffHeapLongArray.set(values_ptr, insert_index, param_elem);
            }
            OffHeapByteArray.set(types_ptr, insert_index, p_type);
            if (hash_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.set(next_ptr, insert_index, OffHeapLongArray.get(hash_ptr, hashIndex));
                OffHeapLongArray.set(hash_ptr, hashIndex, insert_index);
            }
            size++;
            OffHeapLongArray.set(addr, SIZE, size);
            declareDirty();
            return insert_index;
        }
        //extend capacity
        long newCapacity = capacity * 2;
        keys_ptr = OffHeapLongArray.reallocate(keys_ptr, newCapacity);
        OffHeapLongArray.set(addr, KEYS, keys_ptr);
        values_ptr = OffHeapLongArray.reallocate(values_ptr, newCapacity);
        OffHeapLongArray.set(addr, VALUES, values_ptr);
        types_ptr = OffHeapLongArray.reallocate(types_ptr, newCapacity);
        OffHeapLongArray.set(addr, TYPES, types_ptr);
        OffHeapLongArray.set(addr, CAPACITY, newCapacity);
        //insert the new value
        final long insert_index = size;
        OffHeapLongArray.set(keys_ptr, insert_index, p_key);
        if (p_type == Type.DOUBLE) {
            OffHeapDoubleArray.set(values_ptr, insert_index, param_double_elem);
        } else {
            OffHeapLongArray.set(values_ptr, insert_index, param_elem);
        }
        OffHeapLongArray.set(types_ptr, insert_index, p_type);
        size++;
        OffHeapLongArray.set(addr, SIZE, size);
        //reHash
        final long double_newCapacity = newCapacity * 2;
        //extend hash
        if (hash_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            hash_ptr = OffHeapLongArray.allocate(double_newCapacity);
        } else {
            hash_ptr = OffHeapLongArray.reallocate(hash_ptr, double_newCapacity);
        }
        OffHeapLongArray.set(addr, HASH, hash_ptr);
        OffHeapLongArray.reset(hash_ptr, double_newCapacity);
        //extend next
        if (next_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            next_ptr = OffHeapLongArray.allocate(newCapacity);
        } else {
            next_ptr = OffHeapLongArray.reallocate(next_ptr, newCapacity);
        }
        OffHeapLongArray.set(addr, NEXT, next_ptr);
        OffHeapLongArray.reset(next_ptr, newCapacity);
        for (int i = 0; i < size; i++) {
            final long keyHash = HashHelper.longHash(OffHeapLongArray.get(keys_ptr, i), double_newCapacity);
            OffHeapLongArray.set(next_ptr, i, OffHeapLongArray.get(hash_ptr, keyHash));
            OffHeapLongArray.set(hash_ptr, keyHash, i);
        }
        if (!initial) {
            declareDirty();
        }
        return insert_index;
    }

    private void allocate(int newCapacity) {
        final long capacity = OffHeapLongArray.get(addr, CAPACITY);
        if (newCapacity <= capacity) {
            return;
        }
        //allocate or reallocate values
        long new_keys;
        if (keys_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            new_keys = OffHeapLongArray.allocate(newCapacity);
        } else {
            new_keys = OffHeapLongArray.reallocate(keys_ptr, newCapacity);
        }
        OffHeapLongArray.set(addr, KEYS, new_keys);
        //allocate or reallocate values
        long new_values;
        if (values_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            new_values = OffHeapLongArray.allocate(newCapacity);
        } else {
            new_values = OffHeapLongArray.reallocate(values_ptr, newCapacity);
        }
        OffHeapLongArray.set(addr, VALUES, new_values);
        //allocate or reallocate types
        long new_types;
        if (types_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            new_types = OffHeapByteArray.allocate(newCapacity);
        } else {
            new_types = OffHeapByteArray.reallocate(types_ptr, newCapacity);
        }
        OffHeapLongArray.set(addr, TYPES, new_types);
        OffHeapLongArray.set(addr, CAPACITY, newCapacity);
        long hash_capacity = newCapacity * 2;
        long new_hash;
        if (hash_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            new_hash = OffHeapLongArray.allocate(hash_capacity);
        } else {
            new_hash = OffHeapLongArray.reallocate(types_ptr, hash_capacity);
            OffHeapLongArray.reset(new_hash, hash_capacity);
        }
        OffHeapLongArray.set(addr, HASH, new_hash);
        long new_next;
        if (next_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            new_next = OffHeapLongArray.allocate(newCapacity);
        } else {
            new_next = OffHeapLongArray.reallocate(next_ptr, newCapacity);
            OffHeapLongArray.reset(new_next, newCapacity);
        }
        OffHeapLongArray.set(addr, NEXT, new_next);
        final long size = OffHeapLongArray.get(addr, SIZE);
        for (long i = 0; i < size; i++) {
            long keyHash = HashHelper.longHash(OffHeapLongArray.get(new_keys, i), hash_capacity);
            OffHeapLongArray.set(new_next, i, OffHeapLongArray.get(new_hash, keyHash));
            OffHeapLongArray.set(new_hash, keyHash, i);
        }
    }

    @Override
    public final synchronized void load(final Buffer buffer) {
        if (buffer == null || buffer.length() == 0) {
            return;
        }
        final boolean initial = (keys_ptr == OffHeapConstants.OFFHEAP_NULL_PTR);
        //reset size
        int cursor = 0;
        long payloadSize = buffer.length();
        int previousStart = -1;
        long currentChunkElemKey = Constants.NULL_LONG;
        byte currentChunkElemType = -1;
        //init detections
        boolean isFirstElem = true;
        //array sub creation variable
        double[] currentDoubleArr = null;
        long[] currentLongArr = null;
        int[] currentIntArr = null;
        //map sub creation variables
        OffHeapRelationship currentRelation = null;
        OffHeapStringLongMap currentStringLongMap = null;
        OffHeapLongLongMap currentLongLongMap = null;
        OffHeapLongLongArrayMap currentLongLongArrayMap = null;
        //array variables
        long currentSubSize = -1;
        int currentSubIndex = 0;
        //map key variables
        long currentMapLongKey = Constants.NULL_LONG;
        String currentMapStringKey = null;
        while (cursor < payloadSize) {
            byte current = buffer.read(cursor);
            if (current == Constants.CHUNK_SEP) {
                if (isFirstElem) {
                    //initial the map
                    isFirstElem = false;
                    final int stateChunkSize = Base64.decodeToIntWithBounds(buffer, 0, cursor);
                    final int closePowerOfTwo = (int) Math.pow(2, Math.ceil(Math.log(stateChunkSize) / Math.log(2)));
                    allocate(closePowerOfTwo);
                    previousStart = cursor + 1;
                } else {
                    if (currentChunkElemType != -1) {
                        Object toInsert = null;
                        switch (currentChunkElemType) {
                            case Type.BOOL:
                                if (buffer.read(previousStart) == Constants.BOOL_FALSE) {
                                    toInsert = false;
                                } else if (buffer.read(previousStart) == Constants.BOOL_TRUE) {
                                    toInsert = true;
                                }
                                break;
                            case Type.STRING:
                                toInsert = Base64.decodeToStringWithBounds(buffer, previousStart, cursor);
                                break;
                            case Type.DOUBLE:
                                toInsert = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                                break;
                            case Type.LONG:
                                toInsert = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                                break;
                            case Type.INT:
                                toInsert = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                                break;
                            case Type.DOUBLE_ARRAY:
                                if (currentDoubleArr == null) {
                                    currentDoubleArr = new double[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                                } else {
                                    currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                                }
                                toInsert = currentDoubleArr;
                                break;
                            case Type.LONG_ARRAY:
                                if (currentLongArr == null) {
                                    currentLongArr = new long[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                                } else {
                                    currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                                }
                                toInsert = currentLongArr;
                                break;
                            case Type.INT_ARRAY:
                                if (currentIntArr == null) {
                                    currentIntArr = new int[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                                } else {
                                    currentIntArr[currentSubIndex] = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                                }
                                toInsert = currentIntArr;
                                break;
                            case Type.RELATION:
                                if (currentRelation == null) {
                                    currentRelation = new OffHeapRelationship(this, internal_set(currentChunkElemKey, currentChunkElemType, null, true, initial));
                                    currentRelation.allocate(Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                                } else {
                                    currentRelation.add(Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                //toInsert = currentRelation;
                                break;
                            case Type.STRING_TO_LONG_MAP:
                                if (currentMapStringKey != null) {
                                    currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                //toInsert = currentStringLongMap;
                                break;
                            case Type.LONG_TO_LONG_MAP:
                                if (currentMapLongKey != Constants.NULL_LONG) {
                                    currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                //toInsert = currentLongLongMap;
                                break;
                            case Type.LONG_TO_LONG_ARRAY_MAP:
                                if (currentMapLongKey != Constants.NULL_LONG) {
                                    currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                //toInsert = currentLongLongArrayMap;
                                break;
                        }
                        if (toInsert != null) {
                            //insert K/V
                            internal_set(currentChunkElemKey, currentChunkElemType, toInsert, true, initial); //enhance this with boolean array
                        }
                    }
                    //next round, reset all variables...
                    previousStart = cursor + 1;
                    currentChunkElemKey = Constants.NULL_LONG;
                    currentChunkElemType = -1;
                    currentSubSize = -1;
                    currentSubIndex = 0;
                    currentMapLongKey = Constants.NULL_LONG;
                    currentMapStringKey = null;
                }
            } else if (current == Constants.CHUNK_SUB_SEP) { //SEPARATION BETWEEN KEY,TYPE,VALUE
                if (currentChunkElemKey == Constants.NULL_LONG) {
                    currentChunkElemKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                } else if (currentChunkElemType == -1) {
                    currentChunkElemType = (byte) Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                }
            } else if (current == Constants.CHUNK_SUB_SUB_SEP) { //SEPARATION BETWEEN ARRAY VALUES AND MAP KEY/VALUE TUPLES
                if (currentSubSize == -1) {
                    currentSubSize = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    //init array or maps
                    switch (currentChunkElemType) {
                        case Type.DOUBLE_ARRAY:
                            currentDoubleArr = new double[(int) currentSubSize];
                            break;
                        case Type.LONG_ARRAY:
                            currentLongArr = new long[(int) currentSubSize];
                            break;
                        case Type.INT_ARRAY:
                            currentIntArr = new int[(int) currentSubSize];
                            break;
                        case Type.RELATION:
                            currentRelation = new OffHeapRelationship(this, internal_set(currentChunkElemKey, currentChunkElemType, null, true, initial));
                            currentRelation.allocate((int) currentSubSize);
                            break;
                        case Type.STRING_TO_LONG_MAP:
                            currentStringLongMap = new OffHeapStringLongMap(this, internal_set(currentChunkElemKey, currentChunkElemType, null, true, initial));
                            currentStringLongMap.reallocate(0, 0, currentSubSize);
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            currentLongLongMap = new OffHeapLongLongMap(this, internal_set(currentChunkElemKey, currentChunkElemType, null, true, initial));
                            currentLongLongMap.reallocate(0, 0, currentSubSize);
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            currentLongLongArrayMap = new OffHeapLongLongArrayMap(this, internal_set(currentChunkElemKey, currentChunkElemType, null, true, initial));
                            currentLongLongArrayMap.reallocate(0, 0, currentSubSize);
                            break;
                    }
                } else {
                    switch (currentChunkElemType) {
                        case Type.DOUBLE_ARRAY:
                            currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                            currentSubIndex++;
                            break;
                        case Type.RELATION:
                            currentRelation.add(Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                            break;
                        case Type.LONG_ARRAY:
                            currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                            currentSubIndex++;
                            break;
                        case Type.INT_ARRAY:
                            currentIntArr[currentSubIndex] = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                            currentSubIndex++;
                            break;
                        case Type.STRING_TO_LONG_MAP:
                            if (currentMapStringKey != null) {
                                currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                currentMapStringKey = null;
                            }
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            if (currentMapLongKey != Constants.NULL_LONG) {
                                currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                currentMapLongKey = Constants.NULL_LONG;
                            }
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            if (currentMapLongKey != Constants.NULL_LONG) {
                                currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                currentMapLongKey = Constants.NULL_LONG;
                            }
                            break;
                    }
                }
                previousStart = cursor + 1;
            } else if (current == Constants.CHUNK_SUB_SUB_SUB_SEP) {
                switch (currentChunkElemType) {
                    case Type.STRING_TO_LONG_MAP:
                        if (currentMapStringKey == null) {
                            currentMapStringKey = Base64.decodeToStringWithBounds(buffer, previousStart, cursor);
                        } else {
                            currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                            //reset key for next loop
                            currentMapStringKey = null;
                        }
                        break;
                    case Type.LONG_TO_LONG_MAP:
                        if (currentMapLongKey == Constants.NULL_LONG) {
                            currentMapLongKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        } else {
                            currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                            //reset key for next loop
                            currentMapLongKey = Constants.NULL_LONG;
                        }
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        if (currentMapLongKey == Constants.NULL_LONG) {
                            currentMapLongKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        } else {
                            currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                            //reset key for next loop
                            currentMapLongKey = Constants.NULL_LONG;
                        }
                        break;
                }
                previousStart = cursor + 1;
            }
            cursor++;
        }
        //take the last element
        if (currentChunkElemType != -1) {
            Object toInsert = null;
            switch (currentChunkElemType) {
                case Type.BOOL:
                    if (buffer.read(previousStart) == Constants.BOOL_FALSE) {
                        toInsert = false;
                    } else if (buffer.read(previousStart) == Constants.BOOL_TRUE) {
                        toInsert = true;
                    }
                    break;
                case Type.STRING:
                    toInsert = Base64.decodeToStringWithBounds(buffer, previousStart, cursor);
                    break;
                case Type.DOUBLE:
                    toInsert = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                    break;
                case Type.LONG:
                    toInsert = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    break;
                case Type.INT:
                    toInsert = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                    break;
                case Type.DOUBLE_ARRAY:
                    if (currentDoubleArr == null) {
                        currentDoubleArr = new double[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                    } else {
                        currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                    }
                    toInsert = currentDoubleArr;
                    break;
                case Type.LONG_ARRAY:
                    if (currentLongArr == null) {
                        currentLongArr = new long[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                    } else {
                        currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    }
                    toInsert = currentLongArr;
                    break;
                case Type.INT_ARRAY:
                    if (currentIntArr == null) {
                        currentIntArr = new int[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                    } else {
                        currentIntArr[currentSubIndex] = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                    }
                    toInsert = currentIntArr;
                    break;
                case Type.RELATION:
                    if (currentRelation != null) {
                        currentRelation.add(Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentRelation;
                    break;
                case Type.STRING_TO_LONG_MAP:
                    if (currentMapStringKey != null) {
                        currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentStringLongMap;
                    break;
                case Type.LONG_TO_LONG_MAP:
                    if (currentMapLongKey != Constants.NULL_LONG) {
                        currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentLongLongMap;
                    break;
                case Type.LONG_TO_LONG_ARRAY_MAP:
                    if (currentMapLongKey != Constants.NULL_LONG) {
                        currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentLongLongArrayMap;
                    break;
            }
            if (toInsert != null) {
                internal_set(currentChunkElemKey, currentChunkElemType, toInsert, true, initial); //enhance this with boolean array
            }
        }
    }

    public static void free(long addr) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long keys_ptr = OffHeapLongArray.get(addr, KEYS);
            final long values_ptr = OffHeapLongArray.get(addr, VALUES);
            final long types_ptr = OffHeapByteArray.get(addr, TYPES);
            final long size = OffHeapLongArray.get(addr, SIZE);
            for (long i = 0; i < size; i++) {
                freeElement(OffHeapLongArray.get(values_ptr, i), OffHeapByteArray.get(types_ptr, i));
            }
            OffHeapLongArray.free(keys_ptr);
            OffHeapLongArray.free(values_ptr);
            OffHeapByteArray.free(types_ptr);
            final long next_ptr = OffHeapLongArray.get(addr, NEXT);
            if (next_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(next_ptr);
            }
            final long hash_ptr = OffHeapLongArray.get(addr, HASH);
            if (hash_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(hash_ptr);
            }
            OffHeapLongArray.free(addr);
        }
    }

    private static void freeElement(long addr, byte elemType) {
        switch (elemType) {
            case Type.STRING:
                OffHeapString.free(addr);
                break;
            case Type.DOUBLE_ARRAY:
                OffHeapDoubleArray.free(addr);
                break;
            case Type.RELATION:
                OffHeapRelationship.free(addr);
                break;
            case Type.LONG_ARRAY:
                OffHeapLongArray.free(addr);
                break;
            case Type.INT_ARRAY:
                OffHeapIntArray.free(addr);
                break;
            case Type.STRING_TO_LONG_MAP:
                OffHeapStringLongMap.free(addr);
                break;
            case Type.LONG_TO_LONG_MAP:
                OffHeapLongLongMap.free(addr);
                break;
            case Type.LONG_TO_LONG_ARRAY_MAP:
                OffHeapLongLongArrayMap.free(addr);
                break;
        }
    }

}
