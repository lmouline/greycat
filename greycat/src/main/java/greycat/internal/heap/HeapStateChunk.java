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
package greycat.internal.heap;

import greycat.Constants;
import greycat.Container;
import greycat.Graph;
import greycat.Type;
import greycat.chunk.ChunkType;
import greycat.chunk.StateChunk;
import greycat.internal.CoreConstants;
import greycat.internal.tree.KDTree;
import greycat.internal.tree.NDTree;
import greycat.plugin.NodeStateCallback;
import greycat.struct.*;
import greycat.utility.Base64;

import java.util.Arrays;

class HeapStateChunk implements StateChunk, HeapContainer {

    private final long _index;
    private final HeapChunkSpace _space;

    private int _capacity;
    private volatile int _size;
    private int[] _k;
    private Object[] _v;
    private byte[] _type;

    private int[] next_and_hash;
    private boolean _dirty;

    final Graph graph() {
        return _space.graph();
    }

    HeapStateChunk(final HeapChunkSpace p_space, final long p_index) {
        _space = p_space;
        _index = p_index;
        //null hash function
        next_and_hash = null;
        _type = null;
        //init to empty size
        _size = 0;
        _capacity = 0;
        _dirty = false;
    }

    @Override
    public final long world() {
        return _space.worldByIndex(_index);
    }

    @Override
    public final long time() {
        return _space.timeByIndex(_index);
    }

    @Override
    public final long id() {
        return _space.idByIndex(_index);
    }

    @Override
    public final byte chunkType() {
        return ChunkType.STATE_CHUNK;
    }

    @Override
    public final long index() {
        return _index;
    }

    @Override
    public synchronized final Object getAt(final int p_key) {
        return internal_get(p_key);
    }

    private int internal_find(final int p_key) {
        if (_size == 0) {
            return -1;
        } else if (next_and_hash == null) {
            for (int i = 0; i < _size; i++) {
                if (_k[i] == p_key) {
                    return i;
                }
            }
            return -1;
        } else {
            int hashIndex = p_key % (_capacity * 2);
            if (hashIndex < 0) {
                hashIndex = hashIndex * -1;
            }
            int m = next_and_hash[_capacity + hashIndex];
            while (m >= 0) {
                if (p_key == _k[m]) {
                    return m;
                } else {
                    m = next_and_hash[m];
                }
            }
            return -1;
        }
    }

    private Object internal_get(final int p_key) {
        //empty chunk, we return immediately
        if (_size == 0) {
            return null;
        }
        int found = internal_find(p_key);
        Object result;
        if (found != -1) {
            result = _v[found];
            if (result != null) {
                switch (_type[found]) {
                    /*
                    case Type.DOUBLE_ARRAY:
                        double[] castedResultD = (double[]) result;
                        double[] copyD = new double[castedResultD.length];
                        System.arraycopy(castedResultD, 0, copyD, 0, castedResultD.length);
                        return copyD;
                    case Type.LONG_ARRAY:
                        long[] castedResultL = (long[]) result;
                        long[] copyL = new long[castedResultL.length];
                        System.arraycopy(castedResultL, 0, copyL, 0, castedResultL.length);
                        return copyL;
                    case Type.INT_ARRAY:
                        int[] castedResultI = (int[]) result;
                        int[] copyI = new int[castedResultI.length];
                        System.arraycopy(castedResultI, 0, copyI, 0, castedResultI.length);
                        return copyI;
                        */
                    case Type.NDTREE:
                        return new NDTree((EGraph) result);
                    case Type.KDTREE:
                        return new KDTree((EGraph) result);
                    default:
                        return result;
                }
            }
        }
        return null;
    }

    /**
     * {@native ts
     * if(p_unsafe_elem != null){
     * if(p_elemType == Type.STRING){ if(!(typeof p_unsafe_elem === 'string')){ throw new Error("GreyCat usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == Type.BOOL){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("GreyCat usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == Type.DOUBLE || p_elemType == Type.LONG || p_elemType == Type.INT){ if(!(typeof p_unsafe_elem === 'number')){ throw new Error("GreyCat usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == Type.DOUBLE_ARRAY){ if(!(p_unsafe_elem instanceof Float64Array)){ throw new Error("GreyCat usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == Type.LONG_ARRAY){ if(!(p_unsafe_elem instanceof Float64Array)){ throw new Error("GreyCat usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == Type.INT_ARRAY){ if(!(p_unsafe_elem instanceof Int32Array)){ throw new Error("GreyCat usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == Type.STRING_TO_INT_MAP){ if(!(typeof p_unsafe_elem === 'object')){ throw new Error("GreyCat usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == Type.LONG_TO_LONG_MAP){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("GreyCat usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == Type.LONG_TO_LONG_ARRAY_MAP){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("GreyCat usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * }
     * this.internal_set(p_elementIndex, p_elemType, p_unsafe_elem, true, false);
     * return this;
     * }
     */
    @Override
    public synchronized final Container setAt(final int p_elementIndex, final byte p_elemType, final Object p_unsafe_elem) {
        internal_set(p_elementIndex, p_elemType, p_unsafe_elem, true, false);
        return this;
    }

    @Override
    public Container remove(String name) {
        return set(name, Type.INT, null);
    }

    @Override
    public Container removeAt(int key) {
        return setAt(key, Type.INT, null);
    }

    @Override
    public synchronized final Container set(final String key, final byte p_elemType, final Object p_unsafe_elem) {
        internal_set(_space.graph().resolver().stringToHash(key, true), p_elemType, p_unsafe_elem, true, false);
        return this;
    }

    @Override
    public synchronized final Object get(final String key) {
        return internal_get(_space.graph().resolver().stringToHash(key, false));
    }

    @Override
    public final <A> A getWithDefault(final String key, final A defaultValue) {
        final Object result = get(key);
        if (result == null) {
            return defaultValue;
        } else {
            return (A) result;
        }
    }

    @Override
    public final <A> A getAtWithDefault(final int key, final A defaultValue) {
        final Object result = getAt(key);
        if (result == null) {
            return defaultValue;
        } else {
            return (A) result;
        }
    }

    @Override
    public synchronized final byte typeAt(final int p_key) {
        final int found_index = internal_find(p_key);
        if (found_index != -1) {
            return _type[found_index];
        } else {
            return -1;
        }
    }

    @Override
    public byte type(final String key) {
        return typeAt(_space.graph().resolver().stringToHash(key, false));
    }

    @Override
    public synchronized final Object getOrCreateAt(final int p_key, final byte p_type) {
        final int found = internal_find(p_key);
        if (found != -1) {
            if (_type[found] == p_type) {
                return _v[found];
            }
        }
        Object toSet = null;
        Object toGet = null;
        switch (p_type) {
            case Type.RELATION:
                toSet = new HeapRelation(this, null);
                toGet = toSet;
                break;
            case Type.RELATION_INDEXED:
                toSet = new HeapRelationIndexed(this, _space.graph());
                toGet = toSet;
                break;
            case Type.DMATRIX:
                toSet = new HeapDMatrix(this, null);
                toGet = toSet;
                break;
            case Type.LMATRIX:
                toSet = new HeapLMatrix(this, null);
                toGet = toSet;
                break;
            case Type.STRING_TO_INT_MAP:
                toSet = new HeapStringIntMap(this);
                toGet = toSet;
                break;
            case Type.LONG_TO_LONG_MAP:
                toSet = new HeapLongLongMap(this);
                toGet = toSet;
                break;
            case Type.LONG_TO_LONG_ARRAY_MAP:
                toSet = new HeapLongLongArrayMap(this);
                toGet = toSet;
                break;
            case Type.EGRAPH:
                toSet = new HeapEGraph(this, null, _space.graph());
                toGet = toSet;
                break;
            case Type.KDTREE:
                EGraph tempKD = new HeapEGraph(this, null, _space.graph());
                toSet = tempKD;
                toGet = new KDTree(tempKD);
                break;
            case Type.NDTREE:
                EGraph tempND = new HeapEGraph(this, null, _space.graph());
                toSet = tempND;
                toGet = new NDTree(tempND);
                break;
        }
        internal_set(p_key, p_type, toSet, true, false);
        return toGet;
    }

    @Override
    public final Object getOrCreate(final String key, final byte elemType) {
        return getOrCreateAt(_space.graph().resolver().stringToHash(key, true), elemType);
    }

    @Override
    final public void declareDirty() {
        if (_space != null && !_dirty) {
            _dirty = true;
            _space.notifyUpdate(_index);
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public synchronized final void save(final Buffer buffer) {
        Base64.encodeIntToBuffer(_size, buffer);
        for (int i = 0; i < _size; i++) {
            final Object loopValue = _v[i]; //there is a real value
            if (loopValue != null) {
                buffer.write(CoreConstants.CHUNK_SEP);
                Base64.encodeIntToBuffer((int) _type[i], buffer);
                buffer.write(CoreConstants.CHUNK_SEP);
                Base64.encodeIntToBuffer(_k[i], buffer);
                buffer.write(CoreConstants.CHUNK_SEP);
                switch (_type[i]) {
                    case Type.STRING:
                        Base64.encodeStringToBuffer((String) loopValue, buffer);
                        break;
                    case Type.BOOL:
                        if ((Boolean) _v[i]) {
                            Base64.encodeIntToBuffer(CoreConstants.BOOL_TRUE, buffer);
                        } else {
                            Base64.encodeIntToBuffer(CoreConstants.BOOL_FALSE, buffer);
                        }
                        break;
                    case Type.LONG:
                        Base64.encodeLongToBuffer((Long) loopValue, buffer);
                        break;
                    case Type.DOUBLE:
                        Base64.encodeDoubleToBuffer((Double) loopValue, buffer);
                        break;
                    case Type.INT:
                        Base64.encodeIntToBuffer((Integer) loopValue, buffer);
                        break;
                    case Type.DOUBLE_ARRAY:
                        double[] castedDoubleArr = (double[]) loopValue;
                        Base64.encodeIntToBuffer(castedDoubleArr.length, buffer);
                        for (int j = 0; j < castedDoubleArr.length; j++) {
                            buffer.write(CoreConstants.CHUNK_VAL_SEP);
                            Base64.encodeDoubleToBuffer(castedDoubleArr[j], buffer);
                        }
                        break;
                    case Type.LONG_ARRAY:
                        long[] castedLongArr = (long[]) loopValue;
                        Base64.encodeIntToBuffer(castedLongArr.length, buffer);
                        for (int j = 0; j < castedLongArr.length; j++) {
                            buffer.write(CoreConstants.CHUNK_VAL_SEP);
                            Base64.encodeLongToBuffer(castedLongArr[j], buffer);
                        }
                        break;
                    case Type.INT_ARRAY:
                        int[] castedIntArr = (int[]) loopValue;
                        Base64.encodeIntToBuffer(castedIntArr.length, buffer);
                        for (int j = 0; j < castedIntArr.length; j++) {
                            buffer.write(CoreConstants.CHUNK_VAL_SEP);
                            Base64.encodeIntToBuffer(castedIntArr[j], buffer);
                        }
                        break;
                    case Type.RELATION:
                        HeapRelation castedLongArrRel = (HeapRelation) loopValue;
                        Base64.encodeIntToBuffer(castedLongArrRel.size(), buffer);
                        for (int j = 0; j < castedLongArrRel.size(); j++) {
                            buffer.write(CoreConstants.CHUNK_VAL_SEP);
                            Base64.encodeLongToBuffer(castedLongArrRel.unsafe_get(j), buffer);
                        }
                        break;
                    case Type.DMATRIX:
                        HeapDMatrix castedMatrix = (HeapDMatrix) loopValue;
                        final double[] unsafeContent = castedMatrix.unsafe_data();
                        if (unsafeContent != null) {
                            Base64.encodeIntToBuffer(unsafeContent.length, buffer);
                            for (int j = 0; j < unsafeContent.length; j++) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeDoubleToBuffer(unsafeContent[j], buffer);
                            }
                        }
                        break;
                    case Type.LMATRIX:
                        HeapLMatrix castedLMatrix = (HeapLMatrix) loopValue;
                        final long[] unsafeLContent = castedLMatrix.unsafe_data();
                        if (unsafeLContent != null) {
                            Base64.encodeIntToBuffer(unsafeLContent.length, buffer);
                            for (int j = 0; j < unsafeLContent.length; j++) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeLongToBuffer(unsafeLContent[j], buffer);
                            }
                        }
                        break;
                    case Type.STRING_TO_INT_MAP:
                        HeapStringIntMap castedStringLongMap = (HeapStringIntMap) loopValue;
                        Base64.encodeIntToBuffer(castedStringLongMap.size(), buffer);
                        castedStringLongMap.unsafe_each(new StringLongMapCallBack() {
                            @Override
                            public void on(final String key, final long value) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeStringToBuffer(key, buffer);
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeLongToBuffer(value, buffer);
                            }
                        });
                        break;
                    case Type.LONG_TO_LONG_MAP:
                        HeapLongLongMap castedLongLongMap = (HeapLongLongMap) loopValue;
                        Base64.encodeIntToBuffer(castedLongLongMap.size(), buffer);
                        castedLongLongMap.unsafe_each(new LongLongMapCallBack() {
                            @Override
                            public void on(final long key, final long value) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeLongToBuffer(key, buffer);
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeLongToBuffer(value, buffer);
                            }
                        });
                        break;
                    case Type.RELATION_INDEXED:
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        HeapLongLongArrayMap castedLongLongArrayMap = (HeapLongLongArrayMap) loopValue;
                        Base64.encodeIntToBuffer(castedLongLongArrayMap.size(), buffer);
                        castedLongLongArrayMap.unsafe_each(new LongLongArrayMapCallBack() {
                            @Override
                            public void on(final long key, final long value) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeLongToBuffer(key, buffer);
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeLongToBuffer(value, buffer);
                            }
                        });
                        break;
                    case Type.NDTREE:
                    case Type.KDTREE:
                    case Type.EGRAPH:
                        HeapEGraph castedEGraph = (HeapEGraph) loopValue;
                        HeapENode[] eNodes = castedEGraph._nodes;
                        int eGSize = castedEGraph.size();
                        Base64.encodeIntToBuffer(eGSize, buffer);
                        for (int j = 0; j < eGSize; j++) {
                            buffer.write(CoreConstants.CHUNK_ENODE_SEP);
                            eNodes[j].save(buffer);
                        }
                        castedEGraph._dirty = false;
                        break;
                    default:
                        break;
                }
            }
        }
        _dirty = false;
    }

    @Override
    public void saveDiff(Buffer buffer) {

    }

    @Override
    public synchronized final void each(final NodeStateCallback callBack) {
        for (int i = 0; i < _size; i++) {
            if (_v[i] != null) {
                callBack.on(_k[i], _type[i], _v[i]);
            }
        }
    }

    @Override
    public synchronized void loadFrom(final StateChunk origin) {
        if (origin == null) {
            return;
        }
        HeapStateChunk casted = (HeapStateChunk) origin;
        _capacity = casted._capacity;
        _size = casted._size;
        //copy keys
        if (casted._k != null) {
            int[] cloned_k = new int[_capacity];
            System.arraycopy(casted._k, 0, cloned_k, 0, _capacity);
            _k = cloned_k;
        }
        //copy values
        /*
        Object[] cloned_v = new Object[_capacity];
        System.arraycopy(casted._v, 0, cloned_v, 0, _capacity);
        _v = cloned_v;
        */
        //copy types
        if (casted._type != null) {
            byte[] cloned_type = new byte[_capacity];
            System.arraycopy(casted._type, 0, cloned_type, 0, _capacity);
            _type = cloned_type;
        }
        //copy next if not empty
        if (casted.next_and_hash != null) {
            int[] cloned_hash = new int[_capacity * 3];
            System.arraycopy(casted.next_and_hash, 0, cloned_hash, 0, _capacity * 3);
            next_and_hash = cloned_hash;
        }
        if (casted._v != null) {
            _v = new Object[_capacity];
            for (int i = 0; i < _size; i++) {
                switch (casted._type[i]) {
                    case Type.LONG_TO_LONG_MAP:
                        if (casted._v[i] != null) {
                            _v[i] = ((HeapLongLongMap) casted._v[i]).cloneFor(this);
                        }
                        break;
                    case Type.RELATION_INDEXED:
                        if (casted._v[i] != null) {
                            _v[i] = ((HeapRelationIndexed) casted._v[i]).cloneIRelFor(this, casted.graph());
                        }
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        if (casted._v[i] != null) {
                            _v[i] = ((HeapLongLongArrayMap) casted._v[i]).cloneFor(this);
                        }
                        break;
                    case Type.STRING_TO_INT_MAP:
                        if (casted._v[i] != null) {
                            _v[i] = ((HeapStringIntMap) casted._v[i]).cloneFor(this);
                        }
                        break;
                    case Type.RELATION:
                        if (casted._v[i] != null) {
                            _v[i] = new HeapRelation(this, (HeapRelation) casted._v[i]);
                        }
                        break;
                    case Type.DMATRIX:
                        if (casted._v[i] != null) {
                            _v[i] = new HeapDMatrix(this, (HeapDMatrix) casted._v[i]);
                        }
                        break;
                    case Type.LMATRIX:
                        if (casted._v[i] != null) {
                            _v[i] = new HeapLMatrix(this, (HeapLMatrix) casted._v[i]);
                        }
                        break;
                    case Type.EGRAPH:
                        if (casted._v[i] != null) {
                            _v[i] = new HeapEGraph(this, (HeapEGraph) casted._v[i], _space.graph());
                        }
                        break;
                    default:
                        _v[i] = casted._v[i];
                        break;
                }
            }
        }
    }

    private void internal_set(final int p_key, final byte p_type, final Object p_unsafe_elem, boolean replaceIfPresent, boolean initial) {
        Object param_elem = null;
        //check the param type
        if (p_unsafe_elem != null) {
            try {
                switch (p_type) {
                    case Type.BOOL:
                        param_elem = (boolean) p_unsafe_elem;
                        break;
                    case Type.INT:
                        if (p_unsafe_elem instanceof Integer) {
                            param_elem = (int) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Double) {
                            double preCasting = (Double) p_unsafe_elem;
                            param_elem = (int) preCasting;
                        } else if (p_unsafe_elem instanceof Long) {
                            long preCastingLong = (Long) p_unsafe_elem;
                            param_elem = (int) preCastingLong;
                        } else if (p_unsafe_elem instanceof Float) {
                            float preCastingLong = (Float) p_unsafe_elem;
                            param_elem = (int) preCastingLong;
                        } else if (p_unsafe_elem instanceof Byte) {
                            byte preCastingLong = (Byte) p_unsafe_elem;
                            param_elem = (int) preCastingLong;
                        } else {
                            param_elem = (int) p_unsafe_elem;
                        }
                        break;
                    case Type.DOUBLE:
                        if (p_unsafe_elem instanceof Double) {
                            param_elem = (double) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Integer) {
                            int preCasting = (Integer) p_unsafe_elem;
                            param_elem = (double) preCasting;
                        } else if (p_unsafe_elem instanceof Long) {
                            long preCastingLong = (Long) p_unsafe_elem;
                            param_elem = (double) preCastingLong;
                        } else if (p_unsafe_elem instanceof Float) {
                            float preCastingLong = (Float) p_unsafe_elem;
                            param_elem = (double) preCastingLong;
                        } else if (p_unsafe_elem instanceof Byte) {
                            byte preCastingLong = (Byte) p_unsafe_elem;
                            param_elem = (double) preCastingLong;
                        } else {
                            param_elem = (double) p_unsafe_elem;
                        }
                        break;
                    case Type.LONG:
                        if (p_unsafe_elem instanceof Long) {
                            param_elem = (long) p_unsafe_elem;
                        } else if (p_unsafe_elem instanceof Integer) {
                            int preCasting = (Integer) p_unsafe_elem;
                            param_elem = (long) preCasting;
                        } else if (p_unsafe_elem instanceof Double) {
                            double preCastingLong = (Double) p_unsafe_elem;
                            param_elem = (long) preCastingLong;
                        } else if (p_unsafe_elem instanceof Float) {
                            float preCastingLong = (Float) p_unsafe_elem;
                            param_elem = (long) preCastingLong;
                        } else if (p_unsafe_elem instanceof Byte) {
                            byte preCastingLong = (Byte) p_unsafe_elem;
                            param_elem = (long) preCastingLong;
                        } else {
                            param_elem = (long) p_unsafe_elem;
                        }
                        break;
                    case Type.STRING:
                        param_elem = (String) p_unsafe_elem;
                        break;
                    case Type.DMATRIX:
                        param_elem = (DMatrix) p_unsafe_elem;
                        break;
                    case Type.LMATRIX:
                        param_elem = (LMatrix) p_unsafe_elem;
                        break;
                    case Type.RELATION:
                        param_elem = (Relation) p_unsafe_elem;
                        break;
                    case Type.DOUBLE_ARRAY:
                        double[] castedParamDouble = (double[]) p_unsafe_elem;
                        double[] clonedDoubleArray = new double[castedParamDouble.length];
                        System.arraycopy(castedParamDouble, 0, clonedDoubleArray, 0, castedParamDouble.length);
                        param_elem = clonedDoubleArray;
                        break;
                    case Type.LONG_ARRAY:
                        long[] castedParamLong = (long[]) p_unsafe_elem;
                        long[] clonedLongArray = new long[castedParamLong.length];
                        System.arraycopy(castedParamLong, 0, clonedLongArray, 0, castedParamLong.length);
                        param_elem = clonedLongArray;
                        break;
                    case Type.INT_ARRAY:
                        int[] castedParamInt = (int[]) p_unsafe_elem;
                        int[] clonedIntArray = new int[castedParamInt.length];
                        System.arraycopy(castedParamInt, 0, clonedIntArray, 0, castedParamInt.length);
                        param_elem = clonedIntArray;
                        break;
                    case Type.STRING_TO_INT_MAP:
                        param_elem = (StringIntMap) p_unsafe_elem;
                        break;
                    case Type.LONG_TO_LONG_MAP:
                        param_elem = (LongLongMap) p_unsafe_elem;
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        param_elem = (LongLongArrayMap) p_unsafe_elem;
                        break;
                    case Type.RELATION_INDEXED:
                        param_elem = (RelationIndexed) p_unsafe_elem;
                        break;
                    case Type.NDTREE:
                    case Type.KDTREE:
                    case Type.EGRAPH:
                        param_elem = (EGraph) p_unsafe_elem;
                        break;
                    default:
                        throw new RuntimeException("Internal Exception, unknown type");
                }
            } catch (Exception e) {
                throw new RuntimeException("GreyCat usage error, set method called with type " + Type.typeName(p_type) + " while param object is " + p_unsafe_elem);
            }
        }
        //first value
        if (_k == null) {
            //we do not allocate for empty element
            if (param_elem == null) {
                return;
            }
            _capacity = Constants.MAP_INITIAL_CAPACITY;
            _k = new int[_capacity];
            _v = new Object[_capacity];
            _type = new byte[_capacity];
            _k[0] = p_key;
            _v[0] = param_elem;
            _type[0] = p_type;
            _size = 1;
            if (!initial) {
                declareDirty();
            }
            return;
        }
        int entry = -1;
        int p_entry = -1;
        int hashIndex = -1;
        if (next_and_hash == null) {
            for (int i = 0; i < _size; i++) {
                if (_k[i] == p_key) {
                    entry = i;
                    break;
                }
            }
        } else {
            hashIndex = p_key % (_capacity * 2);
            if (hashIndex < 0) {
                hashIndex = hashIndex * -1;
            }
            int m = next_and_hash[_capacity + hashIndex];
            while (m != -1) {
                if (_k[m] == p_key) {
                    entry = m;
                    break;
                }
                p_entry = m;
                m = next_and_hash[m];
            }
        }
        //case already present
        if (entry != -1) {
            if (replaceIfPresent || (p_type != _type[entry])) {
                if (param_elem == null) {
                    if (next_and_hash != null) {
                        //unHash previous
                        if (p_entry != -1) {
                            next_and_hash[p_entry] = next_and_hash[entry];
                        } else {
                            next_and_hash[_capacity + hashIndex] = -1;
                        }
                    }
                    int indexVictim = _size - 1;
                    //just pop the last value
                    if (entry == indexVictim) {
                        _k[entry] = -1;
                        _v[entry] = null;
                        _type[entry] = -1;
                    } else {
                        //we need to reHash the new last element at our place
                        _k[entry] = _k[indexVictim];
                        _v[entry] = _v[indexVictim];
                        _type[entry] = _type[indexVictim];
                        if (next_and_hash != null) {
                            next_and_hash[entry] = next_and_hash[indexVictim];
                            int victimHash = _k[entry] % (_capacity * 2);
                            if (victimHash < 0) {
                                victimHash = victimHash * -1;
                            }
                            int m = next_and_hash[_capacity + victimHash];
                            if (m == indexVictim) {
                                //the victim was the head of hashing list
                                next_and_hash[_capacity + victimHash] = entry;
                            } else {
                                //the victim is in the next, reChain it
                                while (m != -1) {
                                    if (next_and_hash[m] == indexVictim) {
                                        next_and_hash[m] = entry;
                                        break;
                                    }
                                    m = next_and_hash[m];
                                }
                            }
                        }
                        _k[indexVictim] = -1;
                        _v[indexVictim] = null;
                        _type[indexVictim] = -1;
                    }
                    _size--;
                } else {
                    _v[entry] = param_elem;
                    if (_type[entry] != p_type) {
                        _type[entry] = p_type;
                    }
                }
            }
            if (!initial) {
                declareDirty();
            }
            return;
        }
        if (_size < _capacity) {
            _k[_size] = p_key;
            _v[_size] = param_elem;
            _type[_size] = p_type;
            if (next_and_hash != null) {
                next_and_hash[_size] = next_and_hash[_capacity + hashIndex];
                next_and_hash[_capacity + hashIndex] = _size;
            }
            _size++;
            if (!initial) {
                declareDirty();
            }
            return;
        }
        //extend capacity
        int newCapacity = _capacity * 2;
        int[] ex_k = new int[newCapacity];
        System.arraycopy(_k, 0, ex_k, 0, _capacity);
        _k = ex_k;
        Object[] ex_v = new Object[newCapacity];
        System.arraycopy(_v, 0, ex_v, 0, _capacity);
        _v = ex_v;
        byte[] ex_type = new byte[newCapacity];
        System.arraycopy(_type, 0, ex_type, 0, _capacity);
        _type = ex_type;
        _capacity = newCapacity;
        //insert the next
        _k[_size] = p_key;
        _v[_size] = param_elem;
        _type[_size] = p_type;
        _size++;
        //reHash
        next_and_hash = new int[_capacity * 3];
        Arrays.fill(next_and_hash, 0, _capacity * 3, -1);
        int double_capacity = _capacity * 2;
        for (int i = 0; i < _size; i++) {
            int keyHash = _k[i] % double_capacity;
            if (keyHash < 0) {
                keyHash = keyHash * -1;
            }
            next_and_hash[i] = next_and_hash[_capacity + keyHash];
            next_and_hash[_capacity + keyHash] = i;
        }
        if (!initial) {
            declareDirty();
        }
    }

    private void allocate(int newCapacity) {
        if (newCapacity <= _capacity) {
            return;
        }
        int[] ex_k = new int[newCapacity];
        if (_k != null) {
            System.arraycopy(_k, 0, ex_k, 0, _capacity);
        }
        _k = ex_k;
        Object[] ex_v = new Object[newCapacity];
        if (_v != null) {
            System.arraycopy(_v, 0, ex_v, 0, _capacity);
        }
        _v = ex_v;
        byte[] ex_type = new byte[newCapacity];
        if (_type != null) {
            System.arraycopy(_type, 0, ex_type, 0, _capacity);
        }
        _type = ex_type;
        _capacity = newCapacity;
        next_and_hash = new int[_capacity * 3];
        Arrays.fill(next_and_hash, 0, _capacity * 3, -1);
        for (int i = 0; i < _size; i++) {
            int keyHash = _k[i] % (_capacity * 2);
            if (keyHash < 0) {
                keyHash = keyHash * -1;
            }
            next_and_hash[i] = next_and_hash[_capacity + keyHash];
            next_and_hash[_capacity + keyHash] = i;
        }
    }

    private static final byte LOAD_WAITING_ALLOC = 0;
    private static final byte LOAD_WAITING_TYPE = 1;
    private static final byte LOAD_WAITING_KEY = 2;
    private static final byte LOAD_WAITING_VALUE = 3;

    private final synchronized void internal_load(final Buffer buffer, final boolean initial) {
        if (buffer != null && buffer.length() > 0) {
            final long payloadSize = buffer.length();
            long previous = 0;
            long cursor = 0;
            byte state = LOAD_WAITING_ALLOC;
            byte read_type = -1;
            int read_key = -1;
            while (cursor < payloadSize) {
                byte current = buffer.read(cursor);
                if (current == Constants.CHUNK_SEP) {
                    switch (state) {
                        case LOAD_WAITING_ALLOC:
                            allocate(Base64.decodeToIntWithBounds(buffer, previous, cursor));
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
                            read_key = Base64.decodeToIntWithBounds(buffer, previous, cursor);
                            //primitive default loader
                            switch (read_type) {
                                //primitive types
                                case Type.BOOL:
                                case Type.INT:
                                case Type.DOUBLE:
                                case Type.LONG:
                                case Type.STRING:
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
                                    while (cursor < payloadSize && current != Constants.CHUNK_SEP) {
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
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                    break;
                                case Type.LONG_ARRAY:
                                    long[] longArrayLoaded = null;
                                    int longArrayIndex = 0;
                                    cursor++;
                                    previous = cursor;
                                    current = buffer.read(cursor);
                                    while (cursor < payloadSize && current != Constants.CHUNK_SEP) {
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
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                    break;
                                case Type.INT_ARRAY:
                                    int[] intArrayLoaded = null;
                                    int intArrayIndex = 0;
                                    cursor++;
                                    previous = cursor;
                                    current = buffer.read(cursor);
                                    while (cursor < payloadSize && current != Constants.CHUNK_SEP) {
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
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                    break;
                                case Type.RELATION:
                                    HeapRelation relation = new HeapRelation(this, null);
                                    cursor++;
                                    cursor = relation.load(buffer, cursor, payloadSize);
                                    internal_set(read_key, read_type, relation, true, initial);
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                        if (current == Constants.CHUNK_SEP && cursor < payloadSize) {
                                            state = LOAD_WAITING_TYPE;
                                            cursor++;
                                            previous = cursor;
                                        }
                                    }
                                    break;
                                case Type.DMATRIX:
                                    HeapDMatrix matrix = new HeapDMatrix(this, null);
                                    cursor++;
                                    cursor = matrix.load(buffer, cursor, payloadSize);
                                    internal_set(read_key, read_type, matrix, true, initial);
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                        if (current == Constants.CHUNK_SEP && cursor < payloadSize) {
                                            state = LOAD_WAITING_TYPE;
                                            cursor++;
                                            previous = cursor;
                                        }
                                    }
                                    break;
                                case Type.LMATRIX:
                                    HeapLMatrix lmatrix = new HeapLMatrix(this, null);
                                    cursor++;
                                    cursor = lmatrix.load(buffer, cursor, payloadSize);
                                    internal_set(read_key, read_type, lmatrix, true, initial);
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                        if (current == Constants.CHUNK_SEP && cursor < payloadSize) {
                                            state = LOAD_WAITING_TYPE;
                                            cursor++;
                                            previous = cursor;
                                        }
                                    }
                                    break;
                                case Type.LONG_TO_LONG_MAP:
                                    HeapLongLongMap l2lmap = new HeapLongLongMap(this);
                                    cursor++;
                                    cursor = l2lmap.load(buffer, cursor, payloadSize);
                                    internal_set(read_key, read_type, l2lmap, true, initial);
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                        if (current == Constants.CHUNK_SEP && cursor < payloadSize) {
                                            state = LOAD_WAITING_TYPE;
                                            cursor++;
                                            previous = cursor;
                                        }
                                    }
                                    break;
                                case Type.LONG_TO_LONG_ARRAY_MAP:
                                    HeapLongLongArrayMap l2lrmap = new HeapLongLongArrayMap(this);
                                    cursor++;
                                    cursor = l2lrmap.load(buffer, cursor, payloadSize);
                                    internal_set(read_key, read_type, l2lrmap, true, initial);
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                        if (current == Constants.CHUNK_SEP && cursor < payloadSize) {
                                            state = LOAD_WAITING_TYPE;
                                            cursor++;
                                            previous = cursor;
                                        }
                                    }
                                    break;
                                case Type.RELATION_INDEXED:
                                    HeapRelationIndexed relationIndexed = new HeapRelationIndexed(this, _space.graph());
                                    cursor++;
                                    cursor = relationIndexed.load(buffer, cursor, payloadSize);
                                    internal_set(read_key, read_type, relationIndexed, true, initial);
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                        if (current == Constants.CHUNK_SEP && cursor < payloadSize) {
                                            state = LOAD_WAITING_TYPE;
                                            cursor++;
                                            previous = cursor;
                                        }
                                    }
                                    break;
                                case Type.STRING_TO_INT_MAP:
                                    HeapStringIntMap s2lmap = new HeapStringIntMap(this);
                                    cursor++;
                                    cursor = s2lmap.load(buffer, cursor, payloadSize);
                                    internal_set(read_key, read_type, s2lmap, true, initial);
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                        if (current == Constants.CHUNK_SEP && cursor < payloadSize) {
                                            state = LOAD_WAITING_TYPE;
                                            cursor++;
                                            previous = cursor;
                                        }
                                    }
                                    break;
                                case Type.NDTREE:
                                case Type.KDTREE:
                                case Type.EGRAPH:
                                    HeapEGraph eGraph = new HeapEGraph(this, null, this.graph());
                                    cursor++;
                                    cursor = eGraph.load(buffer, cursor, payloadSize);
                                    internal_set(read_key, read_type, eGraph, true, initial);
                                    if (cursor < payloadSize) {
                                        current = buffer.read(cursor);
                                        if (current == Constants.CHUNK_SEP && cursor < payloadSize) {
                                            state = LOAD_WAITING_TYPE;
                                            cursor++;
                                            previous = cursor;
                                        }
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
        }
    }

    private void load_primitive(final int read_key, final byte read_type, final Buffer buffer, final long previous, final long cursor, final boolean initial) {
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
        }
    }

    public final synchronized void load(final Buffer buffer) {
        internal_load(buffer, true);
    }

    @Override
    public final void loadDiff(Buffer buffer) {
        internal_load(buffer, false);
    }



}
