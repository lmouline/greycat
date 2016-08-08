package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.Type;
import org.mwg.core.CoreConstants;
import org.mwg.core.chunk.ChunkListener;
import org.mwg.chunk.StateChunk;
import org.mwg.utility.HashHelper;
import org.mwg.utility.Base64;
import org.mwg.chunk.ChunkType;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.struct.*;

import java.util.Arrays;

class HeapStateChunk implements StateChunk, ChunkListener {

    private final long _index;
    private final HeapChunkSpace _space;

    private int _capacity;
    private volatile int _size;
    private long[] _k;
    private Object[] _v;

    private int[] _next;
    private int[] _hash;
    private byte[] _type;

    HeapStateChunk(final HeapChunkSpace p_space, final long p_index) {
        _space = p_space;
        _index = p_index;
        //null hash function
        _next = null;
        _hash = null;
        _type = null;
        //init to empty size
        _size = 0;
        _capacity = 0;
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
    public synchronized final Object get(final long p_key) {
        //empty chunk, we return immediately
        if (_size == 0) {
            return null;
        } else if (_hash == null) {
            for (int i = 0; i < _size; i++) {
                if (_k[i] == p_key) {
                    return _v[i];
                }
            }
            return null;
        } else {
            final int hashIndex = (int) HashHelper.longHash(p_key, _capacity * 2);
            int m = _hash[hashIndex];
            Object result = null;
            while (m >= 0) {
                if (p_key == _k[m]) {
                    result = _v[m];
                    break;
                } else {
                    m = _next[m];
                }
            }
            //TODO optimize this
            switch (_type[m]) {
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
                default:
                    return result;
            }
        }
    }

    /**
     * @native ts
     * if(p_unsafe_elem != null){
     * if(p_elemType == org.mwg.Type.STRING){ if(!(typeof p_unsafe_elem === 'string')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.BOOL){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.DOUBLE || p_elemType == org.mwg.Type.LONG || p_elemType == org.mwg.Type.INT){ if(!(typeof p_unsafe_elem === 'number')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.DOUBLE_ARRAY){ if(!(p_unsafe_elem instanceof Float64Array)){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.LONG_ARRAY){ if(!(p_unsafe_elem instanceof Float64Array)){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.INT_ARRAY){ if(!(p_unsafe_elem instanceof Int32Array)){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.STRING_TO_LONG_MAP){ if(!(typeof p_unsafe_elem === 'object')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.LONG_TO_LONG_MAP){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.LONG_TO_LONG_ARRAY_MAP){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * }
     * this.internal_set(p_elementIndex, p_elemType, p_unsafe_elem, true,false);
     */
    @Override
    public synchronized final void set(final long p_elementIndex, final byte p_elemType, final Object p_unsafe_elem) {
        internal_set(p_elementIndex, p_elemType, p_unsafe_elem, true);
    }

    @Override
    public synchronized final void setFromKey(final String key, final byte p_elemType, final Object p_unsafe_elem) {
        internal_set(_space.graph().resolver().stringToHash(key, true), p_elemType, p_unsafe_elem, true);
    }


    @Override
    public synchronized final Object getFromKey(String key) {
        return get(_space.graph().resolver().stringToHash(key, false));
    }

    @Override
    public synchronized final <A> A getFromKeyWithDefault(String key, A defaultValue) {
        final Object result = getFromKey(key);
        if (result == null) {
            return defaultValue;
        } else {
            return (A) result;
        }
    }

    @Override
    public synchronized final byte getType(final long p_elementIndex) {
        if (_size == 0) {
            return -1;
        }
        int hashIndex = (int) HashHelper.longHash(p_elementIndex, _capacity);
        int m = _hash[hashIndex];
        while (m >= 0) {
            if (p_elementIndex == _k[m]) {
                return _type[m];
            } else {
                m = _next[m];
            }
        }
        return -1;
    }

    @Override
    public synchronized byte getTypeFromKey(String key) {
        return getType(_space.graph().resolver().stringToHash(key, false));
    }

    @Override
    public synchronized final Object getOrCreate(final long p_elementIndex, final byte elemType) {
        final Object previousObject = get(p_elementIndex);
        byte previousType = getType(p_elementIndex);
        if (previousObject != null && previousType == elemType) {
            return previousObject;
        }
        switch (elemType) {
            case Type.RELATION:
                internal_set(p_elementIndex, elemType, new HeapLongArray(this), false);
                break;
            case Type.STRING_TO_LONG_MAP:
                internal_set(p_elementIndex, elemType, new HeapStringLongMap(this, CoreConstants.MAP_INITIAL_CAPACITY, null), false);
                break;
            case Type.LONG_TO_LONG_MAP:
                internal_set(p_elementIndex, elemType, new HeapLongLongMap(this, CoreConstants.MAP_INITIAL_CAPACITY, null), false);
                break;
            case Type.LONG_TO_LONG_ARRAY_MAP:
                internal_set(p_elementIndex, elemType, new HeapLongLongArrayMap(this, CoreConstants.MAP_INITIAL_CAPACITY, null), false);
                break;
        }
        return get(p_elementIndex);
    }

    @Override
    public synchronized final Object getOrCreateFromKey(final String key, final byte elemType) {
        return getOrCreate(_space.graph().resolver().stringToHash(key, true), elemType);
    }


    @Override
    public final void declareDirty() {
        if (_space != null) {
            _space.notifyUpdate(_index);
        }
    }

    @Override
    public synchronized final void save(final Buffer buffer) {
        Base64.encodeIntToBuffer(_size, buffer);
        for (int i = 0; i < _size; i++) {
            if (_v[i] != null) { //there is a real value
                final Object loopValue = _v[i];
                if (loopValue != null) {
                    buffer.write(CoreConstants.CHUNK_SEP);
                    Base64.encodeLongToBuffer(_k[i], buffer);
                    buffer.write(CoreConstants.CHUNK_SUB_SEP);
                    /** Encode to type of elem, for unSerialization */
                    Base64.encodeIntToBuffer(_type[i], buffer);
                    buffer.write(CoreConstants.CHUNK_SUB_SEP);
                    switch (_type[i]) {
                        /** Primitive Types */
                        case Type.STRING:
                            Base64.encodeStringToBuffer((String) loopValue, buffer);
                            break;
                        case Type.BOOL:
                            if ((Boolean) _v[i]) {
                                buffer.write(CoreConstants.BOOL_TRUE);
                            } else {
                                buffer.write(CoreConstants.BOOL_FALSE);
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
                        /** Arrays */
                        case Type.DOUBLE_ARRAY:
                            double[] castedDoubleArr = (double[]) loopValue;
                            Base64.encodeIntToBuffer(castedDoubleArr.length, buffer);
                            for (int j = 0; j < castedDoubleArr.length; j++) {
                                buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeDoubleToBuffer(castedDoubleArr[j], buffer);
                            }
                            break;
                        case Type.RELATION:
                            LongArray castedLongArrRel = (LongArray) loopValue;
                            Base64.encodeIntToBuffer(castedLongArrRel.size(), buffer);
                            for (int j = 1; j <= castedLongArrRel.size(); j++) {
                                buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeLongToBuffer(castedLongArrRel.get(j), buffer);
                            }
                            break;
                        case Type.LONG_ARRAY:
                            long[] castedLongArr = (long[]) loopValue;
                            Base64.encodeIntToBuffer(castedLongArr.length, buffer);
                            for (int j = 0; j < castedLongArr.length; j++) {
                                buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeLongToBuffer(castedLongArr[j], buffer);
                            }
                            break;
                        case Type.INT_ARRAY:
                            int[] castedIntArr = (int[]) loopValue;
                            Base64.encodeIntToBuffer(castedIntArr.length, buffer);
                            for (int j = 0; j < castedIntArr.length; j++) {
                                buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeIntToBuffer(castedIntArr[j], buffer);
                            }
                            break;
                        /** Maps */
                        case Type.STRING_TO_LONG_MAP:
                            StringLongMap castedStringLongMap = (StringLongMap) loopValue;
                            Base64.encodeLongToBuffer(castedStringLongMap.size(), buffer);
                            castedStringLongMap.each(new StringLongMapCallBack() {
                                @Override
                                public void on(final String key, final long value) {
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeStringToBuffer(key, buffer);
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            LongLongMap castedLongLongMap = (LongLongMap) loopValue;
                            Base64.encodeLongToBuffer(castedLongLongMap.size(), buffer);
                            castedLongLongMap.each(new LongLongMapCallBack() {
                                @Override
                                public void on(final long key, final long value) {
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(key, buffer);
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            LongLongArrayMap castedLongLongArrayMap = (LongLongArrayMap) loopValue;
                            Base64.encodeLongToBuffer(castedLongLongArrayMap.size(), buffer);
                            castedLongLongArrayMap.each(new LongLongArrayMapCallBack() {
                                @Override
                                public void on(final long key, final long value) {
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(key, buffer);
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    @Override
    public synchronized final void each(final NodeStateCallback callBack) {
        for (int i = 0; i < _size; i++) {
            if (_v[i] != null) {
                if (_type[i] == Type.RELATION) {
                    long[] castedRel = (long[]) _v[i];
                    int relSize = (int) castedRel[0];
                    long[] shrinkedRel = new long[relSize];
                    System.arraycopy(castedRel, 1, shrinkedRel, 0, relSize);
                    callBack.on(_k[i], _type[i], shrinkedRel);
                } else {
                    callBack.on(_k[i], _type[i], _v[i]);
                }
            }
        }
    }


    @Override
    public synchronized void loadFrom(StateChunk origin) {
        //TODO
    }

    /*
    private final class InternalState {
        InternalState(int elementDataSize, long[] p_elementK, Object[] p_elementV, int[] p_elementNext, int[] p_elementHash, byte[] p_elementType, int p_elementCount, boolean p_hashReadOnly) {
            this.hashReadOnly = p_hashReadOnly;
            this._elementDataSize = elementDataSize;
            this._elementK = p_elementK;
            this._elementV = p_elementV;
            this._elementNext = p_elementNext;
            this._elementHash = p_elementHash;
            this._elementType = p_elementType;
            this._elementCount = p_elementCount;
            this.threshold = (int) (_elementDataSize * CoreConstants.MAP_LOAD_FACTOR);
        }
        InternalState deepClone() {
            long[] clonedElementK = new long[this._elementDataSize];
            System.arraycopy(_elementK, 0, clonedElementK, 0, this._elementDataSize);
            int[] clonedElementNext = new int[this._elementDataSize];
            System.arraycopy(_elementNext, 0, clonedElementNext, 0, this._elementDataSize);
            int[] clonedElementHash = new int[this._elementDataSize];
            System.arraycopy(_elementHash, 0, clonedElementHash, 0, this._elementDataSize);
            byte[] clonedElementType = new byte[this._elementDataSize];
            System.arraycopy(_elementType, 0, clonedElementType, 0, this._elementDataSize);
            return new InternalState(this._elementDataSize, clonedElementK, _elementV, clonedElementNext, clonedElementHash, clonedElementType, _elementCount, false);
        }
        InternalState softClone() {
            Object[] clonedElementV = new Object[this._elementDataSize];
            System.arraycopy(_elementV, 0, clonedElementV, 0, this._elementDataSize);
            return new InternalState(this._elementDataSize, _elementK, clonedElementV, _elementNext, _elementHash, _elementType, _elementCount, true);
        }
    }*/

    //HeapStateChunk(final HeapChunkSpace p_space, final long p_index) {
    //this._space = p_space;
    //this._index = p_index;
        /*
        if (initialPayload != null && initialPayload.length() > 0) {
            load(initialPayload, false);
        } else if (origin != null) {
            HeapStateChunk castedOrigin = (HeapStateChunk) origin;
            InternalState clonedState = castedOrigin.state.softClone();
            state = clonedState;
            //deep clone for map
            for (int i = 0; i < clonedState._elementCount; i++) {
                switch (clonedState._elementType[i]) {
                    case Type.LONG_TO_LONG_MAP:
                        if (clonedState._elementV[i] != null) {
                            clonedState._elementV[i] = new HeapLongLongMap(this, -1, (HeapLongLongMap) clonedState._elementV[i]);
                        }
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        if (clonedState._elementV[i] != null) {
                            clonedState._elementV[i] = new HeapLongLongArrayMap(this, -1, (HeapLongLongArrayMap) clonedState._elementV[i]);
                        }
                        break;
                    case Type.STRING_TO_LONG_MAP:
                        if (clonedState._elementV[i] != null) {
                            clonedState._elementV[i] = new HeapStringLongMap(this, -1, (HeapStringLongMap) clonedState._elementV[i]);
                        }
                        break;
                }
            }
        } else {
            //init a new state
            int initialCapacity = CoreConstants.MAP_INITIAL_CAPACITY;
            InternalState newstate = new InternalState(initialCapacity, new long[initialCapacity], new Object[initialCapacity], new int[initialCapacity], new int[initialCapacity], new byte[initialCapacity], 0, false);
            for (int i = 0; i < initialCapacity; i++) {
                newstate._elementNext[i] = -1;
                newstate._elementHash[i] = -1;
            }
            state = newstate;
        }*/
    //}


    private synchronized void internal_set(final long p_key, final byte p_type, final Object p_unsafe_elem, boolean replaceIfPresent) {
        Object param_elem = null;
        //check the param type
        if (p_unsafe_elem != null) {
            try {
                switch (p_type) {
                    /** Primitives */
                    case Type.BOOL:
                        param_elem = (boolean) p_unsafe_elem;
                        break;
                    case Type.DOUBLE:
                        param_elem = (double) p_unsafe_elem;
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
                        param_elem = (String) p_unsafe_elem;
                        break;
                    /** Arrays */
                    case Type.RELATION:
                        param_elem = (LongArray) p_unsafe_elem;
                        break;
                    case Type.DOUBLE_ARRAY:
                        if (p_unsafe_elem != null) {
                            double[] castedParamDouble = (double[]) p_unsafe_elem;
                            double[] clonedDoubleArray = new double[castedParamDouble.length];
                            System.arraycopy(castedParamDouble, 0, clonedDoubleArray, 0, castedParamDouble.length);
                            param_elem = clonedDoubleArray;
                        }
                        break;
                    case Type.LONG_ARRAY:
                        if (p_unsafe_elem != null) {
                            long[] castedParamLong = (long[]) p_unsafe_elem;
                            long[] clonedLongArray = new long[castedParamLong.length];
                            System.arraycopy(castedParamLong, 0, clonedLongArray, 0, castedParamLong.length);
                            param_elem = clonedLongArray;
                        }
                        break;
                    case Type.INT_ARRAY:
                        if (p_unsafe_elem != null) {
                            int[] castedParamInt = (int[]) p_unsafe_elem;
                            int[] clonedIntArray = new int[castedParamInt.length];
                            System.arraycopy(castedParamInt, 0, clonedIntArray, 0, castedParamInt.length);
                            param_elem = clonedIntArray;
                        }
                        break;
                    /** Maps */
                    case Type.STRING_TO_LONG_MAP:
                        param_elem = (StringLongMap) p_unsafe_elem;
                        break;
                    case Type.LONG_TO_LONG_MAP:
                        param_elem = (LongLongMap) p_unsafe_elem;
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        param_elem = (LongLongArrayMap) p_unsafe_elem;
                        break;
                    default:
                        throw new RuntimeException("Internal Exception, unknown type");
                }
            } catch (Exception e) {
                throw new RuntimeException("mwDB usage error, set method called with type " + Type.typeName(p_type) + " while param object is " + p_unsafe_elem);
            }
        }
        //first value
        if (_k == null) {
            _capacity = Constants.MAP_INITIAL_CAPACITY;
            _k = new long[_capacity];
            _v = new Object[_capacity];
            _type = new byte[_capacity];
            _k[0] = p_key;
            _v[0] = param_elem;
            _type[0] = p_type;
            _size = 1;
            return;
        }
        int entry = -1;
        int hashIndex;
        if (_hash == null) {
            for (int i = 0; i < _size; i++) {
                if (_k[i] == p_key) {
                    entry = i;
                    break;
                }
            }
        } else {
            hashIndex = (int) HashHelper.longHash(p_key, _capacity);
            int m = _hash[hashIndex];
            while (m != -1) {
                if (_k[m] == p_key) {
                    entry = m;
                    break;
                }
                m = _next[m];
            }
        }
        //case already present
        if (entry != -1) {
            if (replaceIfPresent || (p_type != _type[entry])) {
                _v[entry] = param_elem;
                if (_type[entry] != p_type) {
                    //TODO deep clone
                    _type[entry] = p_type;
                }
            }
            declareDirty();
            return;
        }
        if (_size < _capacity) {
            _k[_size] = p_key;
            _v[_size] = param_elem;
            _type[_size] = p_type;
            if (_hash != null) {
                int keyHash = (int) HashHelper.longHash(p_key, _capacity);
                _next[_size] = _hash[keyHash];
                _hash[keyHash] = _size;
            }
            _size++;
            declareDirty();
            return;
        }
        //extend capacity
        int newCapacity = _capacity * 2;
        long[] ex_k = new long[newCapacity];
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
        _hash = new int[_capacity * 2];
        Arrays.fill(_hash, 0, _capacity * 2, -1);
        _next = new int[_capacity];
        Arrays.fill(_next, 0, _capacity, -1);
        for (int i = 0; i < _size; i++) {
            int keyHash = (int) HashHelper.longHash(_k[i], _capacity);
            _next[i] = _hash[keyHash];
            _hash[keyHash] = i;
        }
        declareDirty();
    }

    private void allocate(int newCapacity) {
        long[] ex_k = new long[newCapacity];
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

        _hash = new int[_capacity * 2];
        Arrays.fill(_hash, 0, _capacity * 2, -1);
        _next = new int[_capacity];
        Arrays.fill(_next, 0, _capacity, -1);
        for (int i = 0; i < _size; i++) {
            int keyHash = (int) HashHelper.longHash(_k[i], _capacity);
            _next[i] = _hash[keyHash];
            _hash[keyHash] = i;
        }

    }

    @Override
    public synchronized void load(final Buffer buffer) {
        if (buffer == null || buffer.length() == 0) {
            return;
        }
        //reset size
        int currentElemIndex = 0;

        int cursor = 0;
        long payloadSize = buffer.length();

        int previousStart = -1;
        long currentChunkElemKey = CoreConstants.NULL_LONG;
        byte currentChunkElemType = -1;

        //init detections
        boolean isFirstElem = true;

        //array sub creation variable
        double[] currentDoubleArr = null;
        long[] currentLongArr = null;
        int[] currentIntArr = null;

        //map sub creation variables
        StringLongMap currentStringLongMap = null;
        LongLongMap currentLongLongMap = null;
        LongLongArrayMap currentLongLongArrayMap = null;

        //array variables
        long currentSubSize = -1;
        int currentSubIndex = 0;

        //map key variables
        long currentMapLongKey = CoreConstants.NULL_LONG;
        String currentMapStringKey = null;

        while (cursor < payloadSize) {
            byte current = buffer.read(cursor);
            if (current == CoreConstants.CHUNK_SEP) {
                if (isFirstElem) {
                    //initial the map
                    isFirstElem = false;
                    int stateChunkSize = Base64.decodeToIntWithBounds(buffer, 0, cursor);
                    allocate(stateChunkSize);
                    previousStart = cursor + 1;
                } else {
                    //beginning of the Chunk elem
                    //check if something is still in buffer
                    if (currentChunkElemType != -1) {
                        Object toInsert = null;
                        switch (currentChunkElemType) {
                            /** Primitive Object */
                            case Type.BOOL:
                                if (buffer.read(previousStart) == CoreConstants.BOOL_FALSE) {
                                    toInsert = false;
                                } else if (buffer.read(previousStart) == CoreConstants.BOOL_TRUE) {
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
                            /** Arrays */
                            case Type.DOUBLE_ARRAY:
                                if (currentDoubleArr == null) {
                                    currentDoubleArr = new double[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                                } else {
                                    currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                                }
                                toInsert = currentDoubleArr;
                                break;
                            case Type.RELATION:
                                if (currentLongArr == null) {
                                    long relSize = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                                    currentLongArr = new long[(int) relSize + 1];
                                    currentLongArr[0] = relSize;
                                    currentSubIndex = 1;
                                } else {
                                    currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                                }
                                toInsert = currentLongArr;
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
                            /** Maps */
                            case Type.STRING_TO_LONG_MAP:
                                if (currentMapStringKey != null) {
                                    currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                toInsert = currentStringLongMap;
                                break;
                            case Type.LONG_TO_LONG_MAP:
                                if (currentMapLongKey != CoreConstants.NULL_LONG) {
                                    currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                toInsert = currentLongLongMap;
                                break;
                            case Type.LONG_TO_LONG_ARRAY_MAP:
                                if (currentMapLongKey != CoreConstants.NULL_LONG) {
                                    currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                toInsert = currentLongLongArrayMap;
                                break;
                        }
                        if (toInsert != null) {
                            //insert K/V
                            internal_set(currentChunkElemKey, currentChunkElemType, toInsert, true); //enhance this with boolean array
                        }
                    }
                    //next round, reset all variables...
                    previousStart = cursor + 1;
                    currentChunkElemKey = CoreConstants.NULL_LONG;
                    currentChunkElemType = -1;
                    currentSubSize = -1;
                    currentSubIndex = 0;
                    currentMapLongKey = CoreConstants.NULL_LONG;
                    currentMapStringKey = null;
                }
            } else if (current == CoreConstants.CHUNK_SUB_SEP) { //SEPARATION BETWEEN KEY,TYPE,VALUE
                if (currentChunkElemKey == CoreConstants.NULL_LONG) {
                    currentChunkElemKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                } else if (currentChunkElemType == -1) {
                    currentChunkElemType = (byte) Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                }
            } else if (current == CoreConstants.CHUNK_SUB_SUB_SEP) { //SEPARATION BETWEEN ARRAY VALUES AND MAP KEY/VALUE TUPLES
                if (currentSubSize == -1) {
                    currentSubSize = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    //init array or maps
                    switch (currentChunkElemType) {
                        case Type.DOUBLE_ARRAY:
                            currentDoubleArr = new double[(int) currentSubSize];
                            break;
                        case Type.RELATION:
                            currentLongArr = new long[(int) currentSubSize + 1];
                            currentLongArr[0] = currentSubSize;
                            currentSubIndex = 1;
                            break;
                        case Type.LONG_ARRAY:
                            currentLongArr = new long[(int) currentSubSize];
                            break;
                        case Type.INT_ARRAY:
                            currentIntArr = new int[(int) currentSubSize];
                            break;
                        case Type.STRING_TO_LONG_MAP:
                            currentStringLongMap = new HeapStringLongMap(this, (int) currentSubSize, null);
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            currentLongLongMap = new HeapLongLongMap(this, (int) currentSubSize, null);
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            currentLongLongArrayMap = new HeapLongLongArrayMap(this, (int) currentSubSize, null);
                            break;
                    }
                } else {
                    switch (currentChunkElemType) {
                        case Type.DOUBLE_ARRAY:
                            currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                            currentSubIndex++;
                            break;
                        case Type.RELATION:
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
                            if (currentMapLongKey != CoreConstants.NULL_LONG) {
                                currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                currentMapLongKey = CoreConstants.NULL_LONG;
                            }
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            if (currentMapLongKey != CoreConstants.NULL_LONG) {
                                currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                currentMapLongKey = CoreConstants.NULL_LONG;
                            }
                            break;
                    }
                }
                previousStart = cursor + 1;
            } else if (current == CoreConstants.CHUNK_SUB_SUB_SUB_SEP) {
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
                        if (currentMapLongKey == CoreConstants.NULL_LONG) {
                            currentMapLongKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        } else {
                            currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                            //reset key for next loop
                            currentMapLongKey = CoreConstants.NULL_LONG;
                        }
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        if (currentMapLongKey == CoreConstants.NULL_LONG) {
                            currentMapLongKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        } else {
                            currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                            //reset key for next loop
                            currentMapLongKey = CoreConstants.NULL_LONG;
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
                /** Primitive Object */
                case Type.BOOL:
                    if (buffer.read(previousStart) == CoreConstants.BOOL_FALSE) {
                        toInsert = false;
                    } else if (buffer.read(previousStart) == CoreConstants.BOOL_TRUE) {
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
                case Type.RELATION:
                    if (currentLongArr == null) {
                        int relSize = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                        currentLongArr = new long[relSize + 1];
                        currentLongArr[0] = relSize;
                        currentSubIndex = 1;
                    } else {
                        currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    }
                    toInsert = currentLongArr;
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
                case Type.STRING_TO_LONG_MAP:
                    if (currentMapStringKey != null) {
                        currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentStringLongMap;
                    break;
                case Type.LONG_TO_LONG_MAP:
                    if (currentMapLongKey != CoreConstants.NULL_LONG) {
                        currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentLongLongMap;
                    break;
                case Type.LONG_TO_LONG_ARRAY_MAP:
                    if (currentMapLongKey != CoreConstants.NULL_LONG) {
                        currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentLongLongArrayMap;
                    break;

            }
            if (toInsert != null) {
                internal_set(currentChunkElemKey, currentChunkElemType, toInsert, true); //enhance this with boolean array
            }
        }
    }

}
