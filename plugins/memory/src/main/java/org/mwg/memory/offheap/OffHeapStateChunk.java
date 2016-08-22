package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Type;
import org.mwg.chunk.ChunkListener;
import org.mwg.chunk.StateChunk;
import org.mwg.utility.Base64;
import org.mwg.chunk.ChunkType;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.struct.*;
import org.mwg.utility.HashHelper;
import org.mwg.utility.Unsafe;

public class OffHeapStateChunk implements StateChunk, ChunkListener {

    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int NEXT = 2;
    private static final int HASH = 3;
    private static final int TYPES = 4;

    private static final int LOCK = 5;
    private static final int COUNTER = 6;
    private static final int DATA_SIZE = 7;
    private static final int THRESHOLD = 8;
    private static final int COUNT = 9;
    private static final int HASH_READ_ONLY = 10;
    private static final int DIRTY = 11;

    private static final int CHUNK_SIZE = 12;

    //pointer values
    private final OffHeapChunkSpace space;
    private final long index;
    private final long addr;

    private long elementK_ptr;
    private long elementV_ptr;
    private long elementNext_ptr;
    private long elementHash_ptr;
    private long elementType_ptr;

    // simple values
    private boolean inLoadMode = false;

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
        if (OffHeapLongArray.get(this.addr, NEXT) != elementNext_ptr) {
            elementK_ptr = OffHeapLongArray.get(addr, KEYS);
            elementV_ptr = OffHeapLongArray.get(addr, VALUES);
            elementNext_ptr = OffHeapLongArray.get(addr, NEXT);
            elementHash_ptr = OffHeapLongArray.get(addr, HASH);
            elementType_ptr = OffHeapLongArray.get(addr, TYPES);
        }
    }

    public OffHeapStateChunk(final OffHeapChunkSpace p_space, final long p_index) {

        index = p_index;
        space = p_space;
        long temp_addr = space.addrByIndex(index);
        if (temp_addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            temp_addr = OffHeapLongArray.allocate(CHUNK_SIZE);
            space.setAddrByIndex(index, temp_addr);
            //init the initial values
            OffHeapLongArray.set(temp_addr, LOCK, 0);
            //OffHeapLongArray.set(temp_addr, CAPACITY, 0);
            OffHeapLongArray.set(temp_addr, DIRTY, 0);
            //OffHeapLongArray.set(temp_addr, SIZE, 0);
            //OffHeapLongArray.set(temp_addr, EXTRA, Constants.NULL_LONG);
            //OffHeapLongArray.set(temp_addr, KV, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(temp_addr, NEXT, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(temp_addr, HASH, OffHeapConstants.OFFHEAP_NULL_PTR);
        }
        addr = temp_addr;


        /*
        _space = space;
        if (previousAddr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            root_array_ptr = OffHeapLongArray.allocate(17);
            OffHeapLongArray.set(root_array_ptr, COUNTER, 0);
            long initialCapacity = Constants.MAP_INITIAL_CAPACITY;
            OffHeapLongArray.set(root_array_ptr, DATA_SIZE, initialCapacity);
            OffHeapLongArray.set(root_array_ptr, COUNT, 0);
            long threshold = (long) (initialCapacity * Constants.MAP_LOAD_FACTOR);
            OffHeapLongArray.set(root_array_ptr, THRESHOLD, threshold);
            OffHeapLongArray.set(root_array_ptr, LOCK, 0); // not locked

            inLoadMode = false;

            if (initialPayload != null && initialPayload.length() > 0) {
                load(initialPayload, false);
                if (elementK_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    init(initialCapacity);
                }

            } else if (origin != null) {
                OffHeapStateChunk castedOrigin = (OffHeapStateChunk) origin;
                softClone(castedOrigin);
                incrementCopyOnWriteCounter(castedOrigin.root_array_ptr);
            } else {
                init(initialCapacity);

            }

        } else {
            addr = previousAddr;
            elementK_ptr = OffHeapLongArray.get(addr, KEYS);
            elementV_ptr = OffHeapLongArray.get(addr, VALUES);
            elementNext_ptr = OffHeapLongArray.get(addr, NEXT);
            elementHash_ptr = OffHeapLongArray.get(addr, HASH);
            elementType_ptr = OffHeapLongArray.get(addr, TYPES);
        }*/

    }

    private void init(long initialCapacity) {
        /** init long[] variables */
        elementK_ptr = OffHeapLongArray.allocate(initialCapacity);
        OffHeapLongArray.set(addr, KEYS, elementK_ptr);
        elementV_ptr = OffHeapLongArray.allocate(initialCapacity);
        OffHeapLongArray.set(addr, VALUES, elementV_ptr); //used for soft clone, therefore cow counter cannot be here
        elementNext_ptr = OffHeapLongArray.allocate(initialCapacity + 1); //cow counter + capacity
        OffHeapLongArray.set(elementNext_ptr, 0, 1); //init cow counter
        OffHeapLongArray.set(addr, NEXT, elementNext_ptr);
        elementHash_ptr = OffHeapLongArray.allocate(initialCapacity);
        OffHeapLongArray.set(addr, HASH, elementHash_ptr);
        elementType_ptr = OffHeapLongArray.allocate(initialCapacity);
        OffHeapLongArray.set(addr, TYPES, elementType_ptr);

        OffHeapLongArray.set(addr, HASH_READ_ONLY, 0);
    }

    private void softClone(OffHeapStateChunk origin) {
        long elementDataSize = OffHeapLongArray.get(origin.addr, DATA_SIZE);
        long elementCount = OffHeapLongArray.get(origin.addr, COUNT);

        // root array is already initialized
        // copy elementV array
        long elementV_ptr = OffHeapLongArray.get(origin.addr, VALUES);
        long clonedElementV_ptr = OffHeapLongArray.cloneArray(elementV_ptr, elementDataSize);
        OffHeapLongArray.set(addr, VALUES, clonedElementV_ptr);

        // link
        OffHeapLongArray.set(addr, KEYS, origin.elementK_ptr);
        OffHeapLongArray.set(addr, NEXT, origin.elementNext_ptr);
        OffHeapLongArray.set(addr, HASH, origin.elementHash_ptr);
        OffHeapLongArray.set(addr, TYPES, origin.elementType_ptr);

        // set elementDataSize
        OffHeapLongArray.set(addr, DATA_SIZE, elementDataSize);
        // set elementCount
        OffHeapLongArray.set(addr, COUNT, elementCount);
        // set hashReadOnly
        OffHeapLongArray.set(addr, HASH_READ_ONLY, 1);

        // increase the copy on write counters, manage the map indirections
        for (long i = 0; i < elementCount; i++) {
            byte elementType = (byte) OffHeapLongArray.get(origin.elementType_ptr, i);
            if (elementType != OffHeapConstants.OFFHEAP_NULL_PTR) { // is there a real value?
                long elemPtr = OffHeapLongArray.get(clonedElementV_ptr, i);
                switch (elementType) {
                    /** String */
                    case Type.STRING:
                        unsafe.getAndAddLong(null, elemPtr, 1);
                        break;
                    /** Arrays */
                    case Type.DOUBLE_ARRAY:
                        unsafe.getAndAddLong(null, elemPtr, 1);
                        break;
                    case Type.RELATION:
                    case Type.LONG_ARRAY:
                        unsafe.getAndAddLong(null, elemPtr, 1);
                        break;
                    case Type.INT_ARRAY:
                        unsafe.getAndAddLong(null, elemPtr, 1);
                        break;
                    // Maps
                    case Type.LONG_TO_LONG_MAP:
                        if (OffHeapLongArray.get(clonedElementV_ptr, i) != OffHeapConstants.OFFHEAP_NULL_PTR) {
                            long tmpLongLongMap_ptr = OffHeapLongArray.get(clonedElementV_ptr, i);
                            long longLongMap_ptr = OffHeapLongLongMap.softClone(tmpLongLongMap_ptr);
                            OffHeapLongLongMap.incrementCopyOnWriteCounter(tmpLongLongMap_ptr);
                            OffHeapLongArray.set(clonedElementV_ptr, i, longLongMap_ptr);
                        }
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        if (OffHeapLongArray.get(clonedElementV_ptr, i) != OffHeapConstants.OFFHEAP_NULL_PTR) {
                            long tmpLongLongArrayMap_ptr = OffHeapLongArray.get(clonedElementV_ptr, i);
                            long longLongArrayMap_ptr = OffHeapLongLongArrayMap.softClone(tmpLongLongArrayMap_ptr);
                            OffHeapLongLongArrayMap.incrementCopyOnWriteCounter(tmpLongLongArrayMap_ptr);
                            OffHeapLongArray.set(clonedElementV_ptr, i, longLongArrayMap_ptr);
                        }
                        break;
                    case Type.STRING_TO_LONG_MAP:
                        if (OffHeapLongArray.get(clonedElementV_ptr, i) != OffHeapConstants.OFFHEAP_NULL_PTR) {
                            long tmpStringLongMap_ptr = OffHeapLongArray.get(clonedElementV_ptr, i);
                            long stringLongMap_ptr = OffHeapStringLongMap.softClone(tmpStringLongMap_ptr);
                            OffHeapStringLongMap.incrementCopyOnWriteCounter(tmpStringLongMap_ptr);
                            OffHeapLongArray.set(clonedElementV_ptr, i, stringLongMap_ptr);
                        }
                        break;
                }
            }
        }

        this.elementV_ptr = clonedElementV_ptr;
        this.elementK_ptr = origin.elementK_ptr;
        this.elementHash_ptr = origin.elementHash_ptr;
        this.elementNext_ptr = origin.elementNext_ptr;
        this.elementType_ptr = origin.elementType_ptr;
    }

    private void shallowClone(OffHeapStateChunk origin) {
        long elementDataSize = OffHeapLongArray.get(origin.addr, DATA_SIZE);
        long elementCount = OffHeapLongArray.get(origin.addr, COUNT);

        // root array is already initialized
        // copy elementK array
        long elementK_ptr = OffHeapLongArray.get(origin.addr, KEYS);
        long clonedElementK_ptr = OffHeapLongArray.cloneArray(elementK_ptr, elementDataSize);
        OffHeapLongArray.set(addr, KEYS, clonedElementK_ptr);
//        // copy elementV array
//        long elementV_ptr = OffHeapLongArray.get(origin.root_array_ptr, VALUES);
//        long clonedElementV_ptr = OffHeapLongArray.cloneArray(elementV_ptr, elementDataSize);
//        OffHeapLongArray.set(root_array_ptr, VALUES, clonedElementV_ptr);
        // copy elementNext array
        long elementNext_ptr = OffHeapLongArray.get(origin.addr, NEXT);
        long clonedElementNext_ptr = OffHeapLongArray.cloneArray(elementNext_ptr, elementDataSize + 1); //cow counter + size
        OffHeapLongArray.set(addr, NEXT, clonedElementNext_ptr);
        // copy elementHash array
        long elementHash_ptr = OffHeapLongArray.get(origin.addr, HASH);
        long clonedElementHash_ptr = OffHeapLongArray.cloneArray(elementHash_ptr, elementDataSize);
        OffHeapLongArray.set(addr, HASH, clonedElementHash_ptr);
        // copy elementType array
        long elementType_ptr = OffHeapLongArray.get(origin.addr, TYPES);
        long clonedElementType_ptr = OffHeapLongArray.cloneArray(elementType_ptr, elementDataSize);
        OffHeapLongArray.set(addr, TYPES, clonedElementType_ptr);
        // set elementDataSize
        OffHeapLongArray.set(addr, DATA_SIZE, elementDataSize);
        // set elementCount
        OffHeapLongArray.set(addr, COUNT, elementCount);
        // set hashReadOnly
        OffHeapLongArray.set(addr, HASH_READ_ONLY, 0);

        this.elementK_ptr = clonedElementK_ptr;
        this.elementNext_ptr = clonedElementNext_ptr;
        this.elementHash_ptr = clonedElementHash_ptr;
        this.elementType_ptr = clonedElementType_ptr;
    }

    @Override
    public final void each(NodeStateCallback callBack) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        try {
            consistencyCheck();
            for (int i = 0; i < OffHeapLongArray.get(addr, COUNT); i++) {
                if (OffHeapLongArray.get(elementType_ptr, i) != OffHeapConstants.OFFHEAP_NULL_PTR) {
                    callBack.on(OffHeapLongArray.get(elementK_ptr, i),
                            (int) OffHeapLongArray.get(elementType_ptr, i),
                            internal_getElementV(i) /*OffHeapLongArray.get(elementV_ptr, i)*/);
                }
            }
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                throw new RuntimeException("CAS error !!!");
            }
        }
    }

    @Override
    public final void save(final Buffer buffer) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ; // lock
        try {
            consistencyCheck();
            long elementCount = OffHeapLongArray.get(addr, COUNT);
            Base64.encodeLongToBuffer(elementCount, buffer);
            for (int i = 0; i < elementCount; i++) {
                byte elementType = (byte) OffHeapLongArray.get(elementType_ptr, i); // can be safely casted
                if (elementType != OffHeapConstants.OFFHEAP_NULL_PTR) { //there is a real value
                    long loopKey = OffHeapLongArray.get(elementK_ptr, i);
                    Object loopValue = internal_getElementV(i);
                    if (loopValue != null) {
                        buffer.write(Constants.CHUNK_SEP);
                        Base64.encodeLongToBuffer(loopKey, buffer);
                        buffer.write(Constants.CHUNK_SUB_SEP);
                        /** Encode to type of elem, for unSerialization */
                        Base64.encodeIntToBuffer(elementType, buffer);
                        buffer.write(Constants.CHUNK_SUB_SEP);
                        switch (elementType) {
                            /** Primitive Types */
                            case Type.STRING:
                                Base64.encodeStringToBuffer((String) loopValue, buffer);
                                break;
                            case Type.BOOL:
                                if ((Boolean) loopValue) {
                                    buffer.write(Constants.BOOL_TRUE);
                                } else {
                                    buffer.write(Constants.BOOL_FALSE);
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
                                    buffer.write(Constants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeDoubleToBuffer(castedDoubleArr[j], buffer);
                                }
                                break;
                            case Type.RELATION:
                            case Type.LONG_ARRAY:
                                long[] castedLongArr = (long[]) loopValue;
                                Base64.encodeIntToBuffer(castedLongArr.length, buffer);
                                for (int j = 0; j < castedLongArr.length; j++) {
                                    buffer.write(Constants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(castedLongArr[j], buffer);
                                }
                                break;
                            case Type.INT_ARRAY:
                                int[] castedIntArr = (int[]) loopValue;
                                Base64.encodeIntToBuffer(castedIntArr.length, buffer);
                                for (int j = 0; j < castedIntArr.length; j++) {
                                    buffer.write(Constants.CHUNK_SUB_SUB_SEP);
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
                                        buffer.write(Constants.CHUNK_SUB_SUB_SEP);
                                        Base64.encodeStringToBuffer(key, buffer);
                                        buffer.write(Constants.CHUNK_SUB_SUB_SUB_SEP);
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
                                        buffer.write(Constants.CHUNK_SUB_SUB_SEP);
                                        Base64.encodeLongToBuffer(key, buffer);
                                        buffer.write(Constants.CHUNK_SUB_SUB_SUB_SEP);
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
                                        buffer.write(Constants.CHUNK_SUB_SUB_SEP);
                                        Base64.encodeLongToBuffer(key, buffer);
                                        buffer.write(Constants.CHUNK_SUB_SUB_SUB_SEP);
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
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                throw new RuntimeException("CAS Error !!!");
            }
        }
    }

    @Override
    public void load(Buffer buffer) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ; // lock
        try {
            load(buffer, true);
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                throw new RuntimeException("CAS Error !!!");
            }
        }
    }

    @Override
    public long index() {
        return index;
    }

    private void load(Buffer buffer, boolean isMerge) {
        if (buffer == null || buffer.length() == 0) {
            return;
        }
        inLoadMode = true;

        //future map elements
        long newElementK_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
        long newElementV_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
        long newElementType_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
        long newElementNext_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
        long newElementHash_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
        long newNumberElement = 0;
        long newStateCapacity = 0;
        //reset size
        long currentElemIndex = 0;

        int cursor = 0;
        long payloadSize = buffer.length();

        int previousStart = -1;
        long currentChunkElemKey = Constants.NULL_LONG;
        int currentChunkElemType = -1;

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
        int currentSubSize = -1;
        int currentSubIndex = 0;

        //map key variables
        long currentMapLongKey = Constants.NULL_LONG;
        String currentMapStringKey = null;

        while (cursor < payloadSize) {
            if (buffer.read(cursor) == Constants.CHUNK_SEP) {
                if (isFirstElem) {
                    //initial the map
                    isFirstElem = false;

                    if (!isMerge) {
                        long stateChunkSize = Base64.decodeToLongWithBounds(buffer, 0, cursor);
                        newNumberElement = stateChunkSize;
                        long newStateChunkSize = (stateChunkSize == 0 ? 1 : stateChunkSize << 1);
                        //init map element
                        newElementK_ptr = OffHeapLongArray.allocate(newStateChunkSize);
                        newElementV_ptr = OffHeapLongArray.allocate(newStateChunkSize);
                        newElementType_ptr = OffHeapLongArray.allocate(newStateChunkSize);
                        newStateCapacity = newStateChunkSize;
                        //init hash and chaining
                        newElementNext_ptr = OffHeapLongArray.allocate(newStateChunkSize + 1); //cow counter + size
                        OffHeapLongArray.set(newElementNext_ptr, 0, 1); //init cow counter
                        newElementHash_ptr = OffHeapLongArray.allocate(newStateChunkSize);
                    }

                    previousStart = cursor + 1;
                } else {
                    //beginning of the Chunk elem
                    //check if something is still in buffer
                    if (currentChunkElemType != -1) {
                        Object toInsert = null;
                        switch (currentChunkElemType) {
                            /** Primitive Object */
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
                            //insert KEYS/VALUES
                            if (isMerge) {
                                internal_set(currentChunkElemKey, (byte) currentChunkElemType, toInsert, true);
                            } else {
                                long newIndex = currentElemIndex;
                                OffHeapLongArray.set(newElementK_ptr, newIndex, currentChunkElemKey);
                                internal_setElementV(newElementV_ptr, newIndex, newElementType_ptr, (byte) currentChunkElemType, toInsert);
                                OffHeapLongArray.set(newElementType_ptr, newIndex, currentChunkElemType);

                                long hashIndex = HashHelper.longHash(currentChunkElemKey, newStateCapacity);
                                long currentHashedIndex = OffHeapLongArray.get(newElementHash_ptr, hashIndex);
                                if (currentHashedIndex != -1) {
                                    OffHeapLongArray.set(newElementNext_ptr + 8, newIndex, currentHashedIndex);
                                }
                                OffHeapLongArray.set(newElementHash_ptr, hashIndex, newIndex);
                                currentElemIndex++;
                            }

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
            } else if (buffer.read(cursor) == Constants.CHUNK_SUB_SEP) { //SEPARATION BETWEEN KEY,TYPE,VALUE
                if (currentChunkElemKey == Constants.NULL_LONG) {
                    currentChunkElemKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                } else if (currentChunkElemType == -1) {
                    currentChunkElemType = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                }
            } else if (buffer.read(cursor) == Constants.CHUNK_SUB_SUB_SEP) { //SEPARATION BETWEEN ARRAY VALUES AND MAP KEY/VALUE TUPLES
                if (currentSubSize == -1) {
                    currentSubSize = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                    //init array or maps
                    switch (currentChunkElemType) {
                        /** Arrays */
                        case Type.DOUBLE_ARRAY:
                            currentDoubleArr = new double[currentSubSize];
                            break;
                        case Type.RELATION:
                        case Type.LONG_ARRAY:
                            currentLongArr = new long[currentSubSize];
                            break;
                        case Type.INT_ARRAY:
                            currentIntArr = new int[currentSubSize];
                            break;
                        /** Maps */
                        case Type.STRING_TO_LONG_MAP:
                            currentStringLongMap = new OffHeapStringLongMap(this, currentSubSize, OffHeapConstants.OFFHEAP_NULL_PTR);
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            currentLongLongMap = new OffHeapLongLongMap(this, currentSubSize, OffHeapConstants.OFFHEAP_NULL_PTR);
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            currentLongLongArrayMap = new OffHeapLongLongArrayMap(this, currentSubSize, OffHeapConstants.OFFHEAP_NULL_PTR);
                            break;
                    }
                } else {
                    switch (currentChunkElemType) {
                        /** Arrays */
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
                        /** Maps */
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
            } else if (buffer.read(cursor) == Constants.CHUNK_SUB_SUB_SUB_SEP) {
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
                /** Primitive Object */
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
                //insert KEYS/VALUES
                if (isMerge) {
                    internal_set(currentChunkElemKey, (byte) currentChunkElemType, toInsert, true);
                } else {
                    OffHeapLongArray.set(newElementK_ptr, currentElemIndex, currentChunkElemKey);
                    internal_setElementV(newElementV_ptr, currentElemIndex, newElementType_ptr, (byte) currentChunkElemType, toInsert);
                    OffHeapLongArray.set(newElementType_ptr, currentElemIndex, currentChunkElemType);

                    long hashIndex = HashHelper.longHash(currentChunkElemKey, newStateCapacity);
                    long currentHashedIndex = OffHeapLongArray.get(newElementHash_ptr, hashIndex);
                    if (currentHashedIndex != -1) {
                        OffHeapLongArray.set(newElementNext_ptr + 8, currentElemIndex, currentHashedIndex);
                    }
                    OffHeapLongArray.set(newElementHash_ptr, hashIndex, currentElemIndex);
                }

            }
        }

        if (!isMerge) {
            OffHeapLongArray.set(addr, KEYS, newElementK_ptr);
            OffHeapLongArray.set(addr, VALUES, newElementV_ptr);
            OffHeapLongArray.set(addr, NEXT, newElementNext_ptr);
            OffHeapLongArray.set(addr, HASH, newElementHash_ptr);
            OffHeapLongArray.set(addr, TYPES, newElementType_ptr);

            elementK_ptr = newElementK_ptr;
            elementV_ptr = newElementV_ptr;
            elementNext_ptr = newElementNext_ptr;
            elementHash_ptr = newElementHash_ptr;
            elementType_ptr = newElementType_ptr;

            OffHeapLongArray.set(addr, DATA_SIZE, newStateCapacity);
            OffHeapLongArray.set(addr, COUNT, newNumberElement);
            long threshold = (long) (newStateCapacity * Constants.MAP_LOAD_FACTOR);
            OffHeapLongArray.set(addr, THRESHOLD, threshold);

            OffHeapLongArray.set(addr, HASH_READ_ONLY, 0);
        }

        inLoadMode = false;
    }

    public static void free(long addr) {
        long elementType_ptr = OffHeapLongArray.get(addr, TYPES);
        long elementDataSize = OffHeapLongArray.get(addr, DATA_SIZE);
        long elementV_array_ptr = OffHeapLongArray.get(addr, VALUES);
        for (long i = 0; i < elementDataSize; i++) {
            long elemV_ptr = OffHeapLongArray.get(elementV_array_ptr, i);
            if (elemV_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                freeElement(elemV_ptr, (byte) OffHeapLongArray.get(elementType_ptr, i));
            }
        }
        OffHeapLongArray.free(OffHeapLongArray.get(addr, VALUES));
        long thisCowCounter = decrementCopyOnWriteCounter(addr);
        if (thisCowCounter == 0) {
            OffHeapLongArray.free(OffHeapLongArray.get(addr, KEYS));
            OffHeapLongArray.free(OffHeapLongArray.get(addr, NEXT));
            OffHeapLongArray.free(OffHeapLongArray.get(addr, HASH));
            OffHeapLongArray.free(elementType_ptr);
        }
        OffHeapLongArray.free(addr);
    }

    private static void freeElement(long addr, byte elemType) {
        long cowCounter;
        switch (elemType) {
            /** Primitive Object */
            case Type.STRING:
                cowCounter = unsafe.getAndAddLong(null, addr, -1) - 1;
                if (cowCounter == 0) {
                    unsafe.freeMemory(addr);
                }
                break;
            /** Arrays */
            case Type.DOUBLE_ARRAY:
                cowCounter = unsafe.getAndAddLong(null, addr, -1) - 1;
                if (cowCounter == 0) {
                    OffHeapDoubleArray.free(addr);
                }
                break;
            case Type.RELATION:
            case Type.LONG_ARRAY:
                cowCounter = unsafe.getAndAddLong(null, addr, -1) - 1;
                if (cowCounter == 0) {
                    OffHeapLongArray.free(addr);
                }
                break;
            case Type.INT_ARRAY:
                cowCounter = unsafe.getAndAddLong(null, addr, -1) - 1;
                if (cowCounter == 0) {
                    OffHeapLongArray.free(addr);
                }
                break;
            /** Maps */
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

    @Override
    public final byte chunkType() {
        return ChunkType.STATE_CHUNK;
    }


    @Override
    public final void set(long index, byte elemType, Object elem) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ; // lock
        try {
            internal_set(index, elemType, elem, true);
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                throw new RuntimeException("CAS Error !!!");
            }
        }
    }

    /*
    @Override
    public void append(long index, byte elemType, Object elem) {
        switch (elemType) {
            case Type.RELATION:
                long[] previous = (long[]) get(index);
                if (previous == null) {
                    previous = new long[1];
                    previous[0] = (Long) elem;
                } else {
                    long[] incArray = new long[previous.length + 1];
                    System.arraycopy(previous, 0, incArray, 0, previous.length);
                    incArray[previous.length] = (Long) elem;
                    previous = incArray;
                }
                internal_set(index, elemType, previous, true);
                break;
            default:
                throw new RuntimeException("Append is only implemented for relationships!");
        }
    }*/

    @Override
    public void setFromKey(String key, byte elemType, Object elem) {
        set(space.graph().resolver().stringToHash(key, true), elemType, elem);
    }

    private void internal_set(final long p_elementIndex, final byte p_elemType, final Object p_unsafe_elem, boolean replaceIfPresent) {
        Object param_elem = null;
        //check the param type
        if (p_unsafe_elem != null) {
            try {
                switch (p_elemType) {
                    /** Primitives */
                    case Type.BOOL:
                        param_elem = (Boolean) p_unsafe_elem;
                        break;
                    case Type.DOUBLE:
                        param_elem = (Double) p_unsafe_elem;
                        break;
                    case Type.LONG:
                        if (p_unsafe_elem instanceof Integer) {
                            int preCasting = (Integer) p_unsafe_elem;
                            param_elem = (long) preCasting;
                        } else {
                            param_elem = (Long) p_unsafe_elem;
                        }
                        break;
                    case Type.INT:
                        param_elem = (Integer) p_unsafe_elem;
                        break;
                    case Type.STRING:
                        param_elem = (String) p_unsafe_elem;
                        break;
                    /** Arrays */
                    case Type.DOUBLE_ARRAY:
                        param_elem = (double[]) p_unsafe_elem;
                        break;
                    case Type.RELATION:
                    case Type.LONG_ARRAY:
                        param_elem = (long[]) p_unsafe_elem;
                        break;
                    case Type.INT_ARRAY:
                        param_elem = (int[]) p_unsafe_elem;
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
                        throw new RuntimeException("mwDB usage error, set method called selectWith an unknown type " + p_elemType);
                }
            } catch (Exception e) {
                //e.printStackTrace();
                throw new RuntimeException("mwDB usage error, set method called with type " + p_elemType + " while param object is " + param_elem);
            }
        }

        long entry = -1;
        long hashIndex = -1;
        long elementDataSize = OffHeapLongArray.get(addr, DATA_SIZE);
        if (elementDataSize > 0) {
            hashIndex = HashHelper.longHash(p_elementIndex, elementDataSize);
            long m = OffHeapLongArray.get(elementHash_ptr, hashIndex);
            while (m != -1) {
                if (p_elementIndex == OffHeapLongArray.get(elementK_ptr, m)) {
                    entry = m;
                    break;
                }
                m = OffHeapLongArray.get(elementNext_ptr + 8, m);
            }
        }
        if (entry == -1) {
            long elementCount = OffHeapLongArray.get(addr, COUNT);
            long threshold = OffHeapLongArray.get(addr, THRESHOLD);
            if (elementCount + 1 > threshold) {
                long newLength = (elementDataSize == 0 ? 1 : elementDataSize << 1);
                long newElementK_ptr = OffHeapLongArray.allocate(newLength);
                long newElementV_ptr = OffHeapLongArray.allocate(newLength);
                long newElementType_ptr = OffHeapLongArray.allocate(newLength);

                unsafe.copyMemory(elementK_ptr, newElementK_ptr, elementDataSize * 8);
                unsafe.copyMemory(elementV_ptr, newElementV_ptr, elementDataSize * 8);
                unsafe.copyMemory(elementType_ptr, newElementType_ptr, elementDataSize * 8);

                long newElementNext_ptr = OffHeapLongArray.allocate(newLength + 1); //cow counter + length
                OffHeapLongArray.set(newElementNext_ptr, 0, 1); //init cow counter
                long newElementHash_ptr = OffHeapLongArray.allocate(newLength);

                //rehashEveryThing
                for (long i = 0; i < elementDataSize; i++) {
                    if (OffHeapLongArray.get(newElementType_ptr, i) != OffHeapConstants.OFFHEAP_NULL_PTR) { //there is a real value
                        long keyHash = HashHelper.longHash(OffHeapLongArray.get(newElementK_ptr, i), newLength);
                        long currentHashedIndex = OffHeapLongArray.get(newElementHash_ptr, keyHash);
                        if (currentHashedIndex != -1) {
                            OffHeapLongArray.set(newElementNext_ptr + 8, i, currentHashedIndex);
                        }
                        OffHeapLongArray.set(newElementHash_ptr, keyHash, i);
                    }
                }

                OffHeapLongArray.free(elementK_ptr);
                OffHeapLongArray.free(elementV_ptr);
                OffHeapLongArray.free(elementType_ptr);

                OffHeapLongArray.free(elementNext_ptr);
                OffHeapLongArray.free(elementHash_ptr);

                //setPrimitiveType value for all
                OffHeapLongArray.set(addr, DATA_SIZE, newLength);
                // elementCount stays the same
                OffHeapLongArray.set(addr, THRESHOLD, (long) (newLength * Constants.MAP_LOAD_FACTOR));
                OffHeapLongArray.set(addr, KEYS, newElementK_ptr);
                OffHeapLongArray.set(addr, VALUES, newElementV_ptr);
                OffHeapLongArray.set(addr, NEXT, newElementNext_ptr);
                OffHeapLongArray.set(addr, HASH, newElementHash_ptr);
                OffHeapLongArray.set(addr, TYPES, newElementType_ptr);

                OffHeapLongArray.set(addr, HASH_READ_ONLY, 0);

                elementK_ptr = OffHeapLongArray.get(addr, KEYS);
                elementV_ptr = OffHeapLongArray.get(addr, VALUES);
                elementNext_ptr = OffHeapLongArray.get(addr, NEXT);
                elementHash_ptr = OffHeapLongArray.get(addr, HASH);
                elementType_ptr = OffHeapLongArray.get(addr, TYPES);

                hashIndex = HashHelper.longHash(p_elementIndex, newLength);
            } else if (OffHeapLongArray.get(addr, HASH_READ_ONLY) == 1) {
                //deepClone state
                decrementCopyOnWriteCounter(this.addr);
                shallowClone(this);
                OffHeapLongArray.set(elementNext_ptr, 0, 1); //set cow counter
            }
            long newIndex = OffHeapLongArray.get(addr, COUNT);
            OffHeapLongArray.set(addr, COUNT, OffHeapLongArray.get(addr, COUNT) + 1);
            OffHeapLongArray.set(elementK_ptr, newIndex, p_elementIndex);
            internal_setElementV(elementV_ptr, newIndex, elementType_ptr, p_elemType, param_elem);

            long currentHashedIndex = OffHeapLongArray.get(elementHash_ptr, hashIndex);
            if (currentHashedIndex != -1) {
                OffHeapLongArray.set(elementNext_ptr + 8, newIndex, currentHashedIndex);
            }
            //now the object is reachable to other thread everything should be ready
            OffHeapLongArray.set(elementHash_ptr, hashIndex, newIndex);
        } else {
            if (replaceIfPresent || (p_elemType != OffHeapLongArray.get(elementType_ptr, entry))) {
                internal_setElementV(elementV_ptr, entry, elementType_ptr, p_elemType, param_elem); /*setValue*/
            }
        }
        declareDirty();
    }

    private void internal_setElementV(long addr, long index, long elementTypeAddr, byte elemType, Object elem) {
        long tempPtr = OffHeapLongArray.get(addr, index);
        byte tempType = (byte) OffHeapLongArray.get(elementTypeAddr, index);

        if (elem != null) {
            switch (elemType) {
                /** Primitives */
                case Type.BOOL:
                    OffHeapLongArray.set(addr, index, ((Boolean) elem) ? 1 : 0);
                    break;
                case Type.DOUBLE:
                    OffHeapDoubleArray.set(addr, index, ((Double) elem));
                    break;
                case Type.LONG:
                    OffHeapLongArray.set(addr, index, ((Long) elem));
                    break;
                case Type.INT:
                    OffHeapLongArray.set(addr, index, ((Integer) elem));
                    break;
                /** String */
                case Type.STRING:
                    String stringToInsert = (String) elem;
                    if (stringToInsert == null) {
                        OffHeapLongArray.set(addr, index, OffHeapConstants.OFFHEAP_NULL_PTR);
                    } else {
                        byte[] valueAsByte = stringToInsert.getBytes();
                        long newStringPtr = unsafe.allocateMemory(8 + 4 + valueAsByte.length); //counter for copy on write, length, and string content
                        //init counter for copy on write
                        unsafe.putLong(newStringPtr, 1);
                        //set size of the string
                        unsafe.putInt(newStringPtr + 8, valueAsByte.length);
                        //copy string content
                        for (int i = 0; i < valueAsByte.length; i++) {
                            unsafe.putByte(8 + 4 + newStringPtr + i, valueAsByte[i]);
                        }
                        OffHeapLongArray.set(addr, index, newStringPtr);
                    }
                    break;
                /** Arrays */
                case Type.DOUBLE_ARRAY:
                    double[] doubleArrayToInsert = (double[]) elem;
                    if (doubleArrayToInsert != null) {
                        long doubleArrayToInsert_ptr = OffHeapDoubleArray.allocate(2 + doubleArrayToInsert.length); // cow counter + length + content of the array
                        OffHeapLongArray.set(doubleArrayToInsert_ptr, 0, 1);// set cow counter
                        OffHeapLongArray.set(doubleArrayToInsert_ptr, 1, doubleArrayToInsert.length);// set length
                        for (int i = 0; i < doubleArrayToInsert.length; i++) {
                            OffHeapDoubleArray.set(doubleArrayToInsert_ptr, 2 + i, doubleArrayToInsert[i]);
                        }
                        OffHeapLongArray.set(addr, index, doubleArrayToInsert_ptr);
                    } else {
                        OffHeapLongArray.set(addr, index, OffHeapConstants.OFFHEAP_NULL_PTR);
                    }
                    break;
                case Type.RELATION:
                case Type.LONG_ARRAY:
                    long[] longArrayToInsert = (long[]) elem;
                    if (longArrayToInsert != null) {
                        long longArrayToInsert_ptr = OffHeapLongArray.allocate(2 + longArrayToInsert.length); // cow counter + length + content of the array
                        OffHeapLongArray.set(longArrayToInsert_ptr, 0, 1);// init cow counter
                        OffHeapLongArray.set(longArrayToInsert_ptr, 1, longArrayToInsert.length);// set length
                        for (int i = 0; i < longArrayToInsert.length; i++) {
                            OffHeapLongArray.set(longArrayToInsert_ptr, 2 + i, longArrayToInsert[i]);
                        }
                        OffHeapLongArray.set(addr, index, longArrayToInsert_ptr);
                    } else {
                        OffHeapLongArray.set(addr, index, OffHeapConstants.OFFHEAP_NULL_PTR);
                    }
                    break;
                case Type.INT_ARRAY:
                    int[] intArrayToInsert = (int[]) elem;
                    if (intArrayToInsert != null) {
                        long intArrayToInsert_ptr = OffHeapLongArray.allocate(2 + intArrayToInsert.length); // cow counter + length + content of the array
                        OffHeapLongArray.set(intArrayToInsert_ptr, 0, 1);// init cow counter
                        OffHeapLongArray.set(intArrayToInsert_ptr, 1, intArrayToInsert.length);// set length
                        for (int i = 0; i < intArrayToInsert.length; i++) {
                            OffHeapLongArray.set(intArrayToInsert_ptr, 2 + i, intArrayToInsert[i]);
                        }
                        OffHeapLongArray.set(addr, index, intArrayToInsert_ptr);
                    } else {
                        OffHeapLongArray.set(addr, index, OffHeapConstants.OFFHEAP_NULL_PTR);
                    }
                    break;
                case Type.STRING_TO_LONG_MAP:
                    long stringLongMap_ptr = ((OffHeapStringLongMap) elem).rootAddress();
                    OffHeapStringLongMap.incrementCopyOnWriteCounter(stringLongMap_ptr);
                    OffHeapLongArray.set(addr, index, stringLongMap_ptr);
                    break;
                case Type.LONG_TO_LONG_MAP:
                    long longLongMap_ptr = ((OffHeapLongLongMap) elem).rootAddress();
                    OffHeapLongLongMap.incrementCopyOnWriteCounter(longLongMap_ptr);
                    OffHeapLongArray.set(addr, index, longLongMap_ptr);
                    break;
                case Type.LONG_TO_LONG_ARRAY_MAP:
                    long longLongArrayMap_ptr = ((OffHeapLongLongArrayMap) elem).rootAddress();
                    OffHeapLongLongArrayMap.incrementCopyOnWriteCounter(longLongArrayMap_ptr);
                    OffHeapLongArray.set(addr, index, longLongArrayMap_ptr);
                    break;
                default:
                    throw new RuntimeException("Should never happen...");
            }
        } else {
            OffHeapLongArray.set(addr, index, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(elementTypeAddr, index, OffHeapConstants.OFFHEAP_NULL_PTR);
        }

        // free the previous elements
        if (tempPtr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            freeElement(tempPtr, tempType);
        }
        if (elem != null) {
            OffHeapLongArray.set(elementTypeAddr, index, elemType);
        }
    }

    @Override
    public final Object get(long index) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        try {
            consistencyCheck();
            long elementDataSize = OffHeapLongArray.get(addr, DATA_SIZE);
            if (elementDataSize == 0) {
                return null;
            }
            long hashIndex = HashHelper.longHash(index, elementDataSize);
            long m = OffHeapLongArray.get(elementHash_ptr, hashIndex);
            while (m >= 0) {
                if (index == OffHeapLongArray.get(elementK_ptr, m) /* getKey */) {
                    return internal_getElementV(m); /* getValue */
                } else {
                    m = OffHeapLongArray.get(elementNext_ptr + 8, m);
                }
            }
            return null;
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                throw new RuntimeException("CAS error !!!");
            }
        }
    }

    @Override
    public Object getFromKey(String key) {
        return get(space.graph().resolver().stringToHash(key, false));
    }

    @Override
    public <A> A getFromKeyWithDefault(String key, A defaultValue) {
        Object result = getFromKey(key);
        if (result == null) {
            return defaultValue;
        } else {
            return (A) result;
        }
    }

    private Object internal_getElementV(long index) {
        byte elemType = (byte) OffHeapLongArray.get(elementType_ptr, index); // can be safely casted
        switch (elemType) {
            /** Primitives */
            case Type.BOOL:
                return OffHeapLongArray.get(elementV_ptr, index) == 1 ? true : false;
            case Type.DOUBLE:
                return OffHeapDoubleArray.get(elementV_ptr, index); // no indirection, value is directly inside
            case Type.LONG:
                return OffHeapLongArray.get(elementV_ptr, index);  // no indirection, value is directly inside
            case Type.INT:
                return (int) OffHeapLongArray.get(elementV_ptr, index); // no indirection, value is directly inside
            /** String */
            case Type.STRING:
                long elemStringPtr = OffHeapLongArray.get(elementV_ptr, index);
                if (elemStringPtr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    return null;
                }
                int length = unsafe.getInt(elemStringPtr + 8); //cow counter
                byte[] bytes = new byte[length];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = unsafe.getByte(elemStringPtr + 4 + 8 + i);
                }
                return new String(bytes);
            /** Arrays */
            case Type.DOUBLE_ARRAY:
                long elemDoublePtr = OffHeapLongArray.get(elementV_ptr, index);
                if (elemDoublePtr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    return null;
                }
                int doubleArrayLength = (int) OffHeapLongArray.get(elemDoublePtr, 1); // can be safely casted
                double[] doubleArray = new double[doubleArrayLength];
                for (int i = 0; i < doubleArrayLength; i++) {
                    doubleArray[i] = OffHeapDoubleArray.get(elemDoublePtr, 2 + i);
                }
                return doubleArray;
            case Type.RELATION:
            case Type.LONG_ARRAY:
                long elemLongPtr = OffHeapLongArray.get(elementV_ptr, index);
                if (elemLongPtr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    return null;
                }
                int longArrayLength = (int) OffHeapLongArray.get(elemLongPtr, 1); // can be safely casted
                long[] longArray = new long[longArrayLength];
                for (int i = 0; i < longArrayLength; i++) {
                    longArray[i] = OffHeapLongArray.get(elemLongPtr, 2 + i);
                }
                return longArray;
            case Type.INT_ARRAY:
                long elemIntPtr = OffHeapLongArray.get(elementV_ptr, index);
                if (elemIntPtr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    return null;
                }
                int intArrayLength = (int) OffHeapLongArray.get(elemIntPtr, 1); // can be safely casted
                int[] intArray = new int[intArrayLength];
                for (int i = 0; i < intArrayLength; i++) {
                    intArray[i] = (int) OffHeapLongArray.get(elemIntPtr, 2 + i);
                }
                return intArray;
            case Type.STRING_TO_LONG_MAP:
                long elemStringLongMapPtr = OffHeapLongArray.get(elementV_ptr, index);
                return new OffHeapStringLongMap(this, Constants.MAP_INITIAL_CAPACITY, elemStringLongMapPtr);
            case Type.LONG_TO_LONG_MAP:
                long elemLongLongMapPtr = OffHeapLongArray.get(elementV_ptr, index);
                return new OffHeapLongLongMap(this, Constants.MAP_INITIAL_CAPACITY, elemLongLongMapPtr);
            case Type.LONG_TO_LONG_ARRAY_MAP:
                long elemLongLongArrayMapPtr = OffHeapLongArray.get(elementV_ptr, index);
                return new OffHeapLongLongArrayMap(this, Constants.MAP_INITIAL_CAPACITY, elemLongLongArrayMapPtr);
            case OffHeapConstants.OFFHEAP_NULL_PTR:
                return null;
            default:
                throw new RuntimeException("Should never happen");
        }
    }

    @Override
    public final Object getOrCreate(long index, byte elemType) {
        Object previousObject = get(index);
        byte previousType = getType(index);
        if (previousObject != null && previousType == elemType) {
            return previousObject;
        }
        switch (elemType) {
            case Type.STRING_TO_LONG_MAP:
                internal_set(index, elemType, new OffHeapStringLongMap(this, Constants.MAP_INITIAL_CAPACITY, OffHeapConstants.OFFHEAP_NULL_PTR), false);
                break;
            case Type.LONG_TO_LONG_MAP:
                internal_set(index, elemType, new OffHeapLongLongMap(this, Constants.MAP_INITIAL_CAPACITY, OffHeapConstants.OFFHEAP_NULL_PTR), false);
                break;
            case Type.LONG_TO_LONG_ARRAY_MAP:
                internal_set(index, elemType, new OffHeapLongLongArrayMap(this, Constants.MAP_INITIAL_CAPACITY, OffHeapConstants.OFFHEAP_NULL_PTR), false);
                break;
        }
        return get(index);

    }

    @Override
    public Object getOrCreateFromKey(String key, byte elemType) {
        return getOrCreate(space.graph().resolver().stringToHash(key, true), elemType);
    }

    @Override
    public final byte getType(long index) {
        long elementDataSize = OffHeapLongArray.get(addr, DATA_SIZE);
        if (elementDataSize == 0) {
            return -1;
        }
        long hashIndex = HashHelper.longHash(index, elementDataSize);
        long m = OffHeapLongArray.get(elementHash_ptr, hashIndex);
        while (m >= 0) {
            if (index == OffHeapLongArray.get(elementK_ptr, m) /* getKey */) {
                return (byte) OffHeapLongArray.get(elementType_ptr, m); /* getValue */
            } else {
                m = OffHeapLongArray.get(elementNext_ptr + 8, m);
            }
        }
        return -1;
    }

    @Override
    public byte getTypeFromKey(String key) {
        return getType(space.graph().resolver().stringToHash(key, false));
    }

    private static long incrementCopyOnWriteCounter(long root_addr) {
        long elemNext_ptr = OffHeapLongArray.get(root_addr, NEXT);
        return unsafe.getAndAddLong(null, elemNext_ptr, 1) + 1;
    }

    private static long decrementCopyOnWriteCounter(long root_addr) {
        long elemNext_ptr = OffHeapLongArray.get(root_addr, NEXT);
        return unsafe.getAndAddLong(null, elemNext_ptr, -1) - 1;
    }

    @Override
    public void declareDirty() {
        if (this.space != null) {
            if (OffHeapLongArray.get(addr, DIRTY) != 1) {
                OffHeapLongArray.set(addr, DIRTY, 1);
                space.notifyUpdate(index);
            }
        }
    }

    @Override
    public void loadFrom(StateChunk origin) {
        //TODO
    }
}
