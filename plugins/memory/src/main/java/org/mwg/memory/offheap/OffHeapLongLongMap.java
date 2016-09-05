
package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.struct.Buffer;
import org.mwg.struct.LongLongMap;
import org.mwg.struct.LongLongMapCallBack;
import org.mwg.utility.Base64;
import org.mwg.utility.HashHelper;

class OffHeapLongLongMap implements LongLongMap {

    private static int SIZE = 0;
    private static int CAPACITY = 1;
    private static int KEYS = 2;
    private static int VALUES = 3;
    private static int NEXTS = 4;
    private static int HASHS = 5;

    private static int CHUNK_ELEM_SIZE = 6;

    private long addr = OffHeapConstants.OFFHEAP_NULL_PTR;
    private long keys_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
    private long values_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
    private long nexts_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
    private long hashs_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;

    private final long index;
    private final OffHeapStateChunk chunk;

    OffHeapLongLongMap(final OffHeapStateChunk p_chunk, final long p_index) {
        chunk = p_chunk;
        index = p_index;
    }

    private void update_ptr() {
        addr = chunk.addrByIndex(index);
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            keys_ptr = OffHeapLongArray.get(addr, KEYS);
            values_ptr = OffHeapLongArray.get(addr, VALUES);
            nexts_ptr = OffHeapLongArray.get(addr, NEXTS);
            hashs_ptr = OffHeapLongArray.get(addr, HASHS);
        }
    }

    private long key(long i) {
        return OffHeapLongArray.get(keys_ptr, i);
    }

    private void setKey(long i, long newValue) {
        OffHeapLongArray.set(keys_ptr, i, newValue);
    }

    private long value(long i) {
        return OffHeapLongArray.get(values_ptr, i);
    }

    private void setValue(long i, long newValue) {
        OffHeapLongArray.set(values_ptr, i, newValue);
    }

    private long next(long i) {
        return OffHeapLongArray.get(nexts_ptr, i);
    }

    private void setNext(long i, long newValue) {
        OffHeapLongArray.set(nexts_ptr, i, newValue);
    }

    private long hash(long i) {
        return OffHeapLongArray.get(hashs_ptr, i);
    }

    private void setHash(long i, long newValue) {
        OffHeapLongArray.set(hashs_ptr, i, newValue);
    }

    void reallocate(long currentCapacity, long currentSize, long newCapacity) {
        if (newCapacity > currentCapacity) {
            if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                addr = OffHeapLongArray.allocate(CHUNK_ELEM_SIZE);
                chunk.setAddrByIndex(index, addr);
                OffHeapLongArray.set(addr, SIZE, 0);
                OffHeapLongArray.set(addr, CAPACITY, newCapacity);
                keys_ptr = OffHeapLongArray.allocate(newCapacity);
                OffHeapLongArray.set(addr, KEYS, keys_ptr);
                values_ptr = OffHeapLongArray.allocate(newCapacity);
                OffHeapLongArray.set(addr, VALUES, values_ptr);
                nexts_ptr = OffHeapLongArray.allocate(newCapacity);
                OffHeapLongArray.set(addr, NEXTS, nexts_ptr);
                hashs_ptr = OffHeapLongArray.allocate(newCapacity * 2);
                OffHeapLongArray.set(addr, HASHS, hashs_ptr);
            } else {
                keys_ptr = OffHeapLongArray.reallocate(keys_ptr, newCapacity);
                OffHeapLongArray.set(addr, KEYS, keys_ptr);
                values_ptr = OffHeapLongArray.reallocate(values_ptr, newCapacity);
                OffHeapLongArray.set(addr, VALUES, values_ptr);
                nexts_ptr = OffHeapLongArray.reallocate(nexts_ptr, newCapacity);
                nexts_ptr = OffHeapLongArray.allocate(newCapacity);
                OffHeapLongArray.reset(nexts_ptr, newCapacity);
                final long newHashCapacity = newCapacity * 2;
                hashs_ptr = OffHeapLongArray.reallocate(hashs_ptr, newHashCapacity);
                OffHeapLongArray.set(addr, HASHS, hashs_ptr);
                OffHeapLongArray.reset(hashs_ptr, newHashCapacity);
                for (long i = 0; i < currentSize; i++) {
                    long new_key_hash = HashHelper.longHash(key(i), newHashCapacity);
                    setNext(i, hash(new_key_hash));
                    setHash(new_key_hash, i);
                }
                OffHeapLongArray.set(addr, CAPACITY, newCapacity);
            }
        }
    }

    @Override
    public final long get(final long requestKey) {
        long result = Constants.NULL_LONG;
        chunk.lock();
        try {
            update_ptr();
            if (keys_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                final long hashIndex = HashHelper.longHash(requestKey, capacity * 2);
                long m = hash(hashIndex);
                while (m >= 0) {
                    if (requestKey == key(m)) {
                        result = value(m);
                        break;
                    }
                    m = next(m);
                }
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final void each(LongLongMapCallBack callback) {
        chunk.lock();
        try {
            update_ptr();
            final long mapSize = OffHeapLongArray.get(addr, SIZE);
            for (long i = 0; i < mapSize; i++) {
                callback.on(key(i), value(i));
            }
        } finally {
            chunk.unlock();
        }
    }

    @Override
    public long size() {
        long result = 0;
        chunk.lock();
        try {
            addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                result = OffHeapLongArray.get(addr, SIZE);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final void remove(final long requestKey) {
        chunk.lock();
        try {
            update_ptr();
            if (keys_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long mapSize = OffHeapLongArray.get(addr, SIZE);
                if (mapSize != 0) {
                    long capacity = OffHeapLongArray.get(addr, CAPACITY);
                    long hashCapacity = capacity * 2;
                    long hashIndex = HashHelper.longHash(requestKey, hashCapacity);
                    long m = hash(hashIndex);
                    long found = -1;
                    while (m >= 0) {
                        if (requestKey == key(m)) {
                            found = m;
                            break;
                        }
                        m = next(m);
                    }
                    if (found != -1) {
                        //first remove currentKey from hashChain
                        long toRemoveHash = HashHelper.longHash(requestKey, hashCapacity);
                        m = hash(toRemoveHash);
                        if (m == found) {
                            setHash(toRemoveHash, next(m));
                        } else {
                            while (m != -1) {
                                long next_of_m = next(m);
                                if (next_of_m == found) {
                                    setNext(m, next(next_of_m));
                                    break;
                                }
                                m = next_of_m;
                            }
                        }
                        final long lastIndex = mapSize - 1;
                        if (lastIndex == found) {
                            //easy, was the last element
                            mapSize--;
                            OffHeapLongArray.set(addr, SIZE, mapSize);
                        } else {
                            //less cool, we have to unchain the last value of the map
                            final long lastKey = key(lastIndex);
                            setKey(found, lastKey);
                            setValue(found, value(lastIndex));
                            setNext(found, next(lastIndex));
                            long victimHash = HashHelper.longHash(lastKey, hashCapacity);
                            m = hash(victimHash);
                            if (m == lastIndex) {
                                //the victim was the head of hashing list
                                setHash(victimHash, found);
                            } else {
                                //the victim is in the next, reChain it
                                while (m != -1) {
                                    long next_of_m = next(m);
                                    if (next_of_m == lastIndex) {
                                        setNext(m, found);
                                        break;
                                    }
                                    m = next_of_m;
                                }
                            }
                            mapSize--;
                            OffHeapLongArray.set(addr, SIZE, mapSize);
                        }
                        chunk.declareDirty();
                    }
                }
            }
        } finally {
            chunk.unlock();
        }
    }

    @Override
    public final void put(final long insertKey, final long insertValue) {
        chunk.lock();
        try {
            update_ptr();
            internal_put(insertKey, insertValue);
        } finally {
            chunk.unlock();
        }
    }

    void internal_put(final long insertKey, final long insertValue) {
        if (keys_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            reallocate(0, 0, Constants.MAP_INITIAL_CAPACITY);
            setKey(0, insertKey);
            setValue(0, insertValue);
            long mapSize = OffHeapLongArray.get(addr, SIZE);
            long capacity = OffHeapLongArray.get(addr, CAPACITY);
            setHash((int) HashHelper.longHash(insertKey, capacity * 2), 0);
            setNext(0, -1);
            mapSize++;
            OffHeapLongArray.set(addr, SIZE, mapSize);
        } else {
            long mapSize = OffHeapLongArray.get(addr, SIZE);
            long capacity = OffHeapLongArray.get(addr, CAPACITY);
            long hashCapacity = capacity * 2;
            long insertKeyHash = HashHelper.longHash(insertKey, hashCapacity);
            long currentHash = hash(insertKeyHash);
            long m = currentHash;
            long found = -1;
            while (m >= 0) {
                if (insertKey == key(m)) {
                    found = m;
                    break;
                }
                m = next(m);
            }
            if (found == -1) {
                final long lastIndex = mapSize;
                if (lastIndex == capacity) {
                    reallocate(capacity, mapSize, capacity * 2);
                    capacity = OffHeapLongArray.get(addr, CAPACITY);
                    mapSize = OffHeapLongArray.get(addr, SIZE);
                }
                setKey(lastIndex, insertKey);
                setValue(lastIndex, insertValue);
                setHash((int) HashHelper.longHash(insertKey, capacity * 2), lastIndex);
                setNext(lastIndex, currentHash);
                mapSize++;
                OffHeapLongArray.set(addr, SIZE, mapSize);
                chunk.declareDirty();
            } else {
                if (value(found) != insertValue) {
                    setValue(found, insertValue);
                    chunk.declareDirty();
                }
            }
        }
    }

    static void save(final long addr, final Buffer buffer) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long size = OffHeapLongArray.get(addr, SIZE);
            final long keys_ptr = OffHeapLongArray.get(addr, KEYS);
            final long values_ptr = OffHeapLongArray.get(addr, VALUES);
            Base64.encodeLongToBuffer(size, buffer);
            for (long i = 0; i < size; i++) {
                buffer.write(Constants.CHUNK_SUB_SUB_SEP);
                Base64.encodeLongToBuffer(OffHeapLongArray.get(keys_ptr, i), buffer);
                buffer.write(Constants.CHUNK_SUB_SUB_SUB_SEP);
                Base64.encodeLongToBuffer(OffHeapLongArray.get(values_ptr, i), buffer);
            }
        }
    }

    static void free(final long addr) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long keys_ptr = OffHeapLongArray.get(addr, KEYS);
            if (keys_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(keys_ptr);
            }
            final long values_ptr = OffHeapLongArray.get(addr, VALUES);
            if (values_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(values_ptr);
            }
            final long nexts_ptr = OffHeapLongArray.get(addr, NEXTS);
            if (nexts_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(nexts_ptr);
            }
            final long hashs_ptr = OffHeapLongArray.get(addr, HASHS);
            if (hashs_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(hashs_ptr);
            }
            OffHeapLongArray.free(addr);
        }
    }

    static long clone(final long addr) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return OffHeapConstants.OFFHEAP_NULL_PTR;
        }
        long new_addr = OffHeapLongArray.cloneArray(addr, CHUNK_ELEM_SIZE);
        long capacity = OffHeapLongArray.get(addr, CAPACITY);
        long keys_ptr = OffHeapLongArray.get(addr, KEYS);
        OffHeapLongArray.set(new_addr, KEYS, OffHeapLongArray.cloneArray(keys_ptr, capacity));
        long values_ptr = OffHeapLongArray.get(addr, VALUES);
        OffHeapLongArray.set(new_addr, VALUES, OffHeapLongArray.cloneArray(values_ptr, capacity));
        long nexts_ptr = OffHeapLongArray.get(addr, NEXTS);
        OffHeapLongArray.set(new_addr, NEXTS, OffHeapLongArray.cloneArray(nexts_ptr, capacity));
        long hashs_ptr = OffHeapLongArray.get(addr, HASHS);
        OffHeapLongArray.set(new_addr, HASHS, OffHeapLongArray.cloneArray(hashs_ptr, capacity * 2));
        return new_addr;
    }

}



