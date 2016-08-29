
package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.struct.Buffer;
import org.mwg.struct.StringLongMap;
import org.mwg.struct.StringLongMapCallBack;
import org.mwg.utility.Base64;
import org.mwg.utility.HashHelper;

class OffHeapStringLongMap implements StringLongMap {

    private static int SIZE = 0;
    private static int CAPACITY = 1;
    private static int KEYS = 2;
    private static int KEYS_H = 3;
    private static int VALUES = 4;
    private static int NEXTS = 5;
    private static int HASHS = 6;

    private static int CHUNK_ELEM_SIZE = 7;

    private static long addr = OffHeapConstants.OFFHEAP_NULL_PTR;
    private static long keys_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
    private static long keys_h_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
    private static long values_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
    private static long nexts_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;
    private static long hashs_ptr = OffHeapConstants.OFFHEAP_NULL_PTR;

    private final long index;
    private final OffHeapStateChunk chunk;

    OffHeapStringLongMap(final OffHeapStateChunk p_chunk, final long p_index) {
        chunk = p_chunk;
        index = p_index;
    }

    private void update_ptr() {
        addr = chunk.addrByIndex(index);
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            keys_ptr = OffHeapLongArray.get(addr, KEYS);
            keys_h_ptr = OffHeapLongArray.get(addr, KEYS_H);
            values_ptr = OffHeapLongArray.get(addr, VALUES);
            nexts_ptr = OffHeapLongArray.get(addr, NEXTS);
            hashs_ptr = OffHeapLongArray.get(addr, HASHS);
        }
    }

    private String key(long i) {
        return OffHeapStringArray.get(keys_ptr, i);
    }

    private void setKey(long i, String newValue) {
        OffHeapStringArray.set(keys_ptr, i, newValue);
    }

    private long key_h(long i) {
        return OffHeapLongArray.get(keys_h_ptr, i);
    }

    private void setKey_h(long i, long newValue) {
        OffHeapLongArray.set(keys_h_ptr, i, newValue);
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
                keys_ptr = OffHeapStringArray.allocate(newCapacity);
                OffHeapLongArray.set(addr, KEYS, keys_ptr);
                keys_h_ptr = OffHeapLongArray.allocate(newCapacity);
                OffHeapLongArray.set(addr, KEYS_H, keys_h_ptr);
                values_ptr = OffHeapLongArray.allocate(newCapacity);
                OffHeapLongArray.set(addr, VALUES, values_ptr);
                nexts_ptr = OffHeapLongArray.allocate(newCapacity);
                OffHeapLongArray.set(addr, NEXTS, nexts_ptr);
                hashs_ptr = OffHeapLongArray.allocate(newCapacity * 2);
                OffHeapLongArray.set(addr, HASHS, hashs_ptr);
            } else {
                keys_ptr = OffHeapStringArray.reallocate(keys_ptr, newCapacity, currentCapacity);
                keys_h_ptr = OffHeapLongArray.reallocate(keys_h_ptr, newCapacity);
                values_ptr = OffHeapLongArray.reallocate(values_ptr, newCapacity);
                nexts_ptr = OffHeapLongArray.reallocate(nexts_ptr, newCapacity);
                OffHeapLongArray.reset(nexts_ptr, newCapacity);
                final long newHashCapacity = newCapacity * 2;
                hashs_ptr = OffHeapLongArray.reallocate(hashs_ptr, newHashCapacity);
                OffHeapLongArray.reset(hashs_ptr, newHashCapacity);
                for (long i = 0; i < currentSize; i++) {
                    long new_key_hash = HashHelper.longHash(key_h(i), newHashCapacity);
                    setNext(i, hash(new_key_hash));
                    setHash(new_key_hash, i);
                }
                OffHeapLongArray.set(addr, CAPACITY, newCapacity);
            }
        }
    }

    @Override
    public final long getValue(final String requestStringKey) {
        long result = Constants.NULL_LONG;
        chunk.lock();
        try {
            update_ptr();
            final long keyHash = HashHelper.hash(requestStringKey);
            if (keys_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long hashIndex = HashHelper.longHash(keyHash, OffHeapLongArray.get(addr, CAPACITY) * 2);
                long m = hash(hashIndex);
                while (m >= 0) {
                    if (keyHash == key_h(m)) {
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
    public final String getByHash(final long keyHash) {
        String result = null;
        chunk.lock();
        try {
            update_ptr();
            if (keys_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long hashIndex = HashHelper.longHash(keyHash, OffHeapLongArray.get(addr, CAPACITY) * 2);
                long m = hash(hashIndex);
                while (m >= 0) {
                    if (keyHash == key_h(m)) {
                        result = key(m);
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
    public final boolean containsHash(long keyHash) {
        boolean result = false;
        chunk.lock();
        try {
            update_ptr();
            if (keys_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long hashIndex = HashHelper.longHash(keyHash, OffHeapLongArray.get(addr, CAPACITY) * 2);
                long m = hash(hashIndex);
                while (m >= 0) {
                    if (keyHash == key_h(m)) {
                        result = true;
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
    public final void each(StringLongMapCallBack callback) {
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
    public final void remove(final String requestStringKey) {
        chunk.lock();
        try {
            update_ptr();
            if (keys_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long mapSize = OffHeapLongArray.get(addr, SIZE);
                final long keyHash = HashHelper.hash(requestStringKey);
                if (mapSize != 0) {
                    long capacity = OffHeapLongArray.get(addr, CAPACITY);
                    long hashCapacity = capacity * 2;
                    long hashIndex = HashHelper.longHash(keyHash, hashCapacity);
                    long m = hash(hashIndex);
                    long found = -1;
                    while (m >= 0) {
                        if (keyHash == key_h(m)) {
                            found = m;
                            break;
                        }
                        m = next(m);
                    }
                    if (found != -1) {
                        //first remove currentKey from hashChain
                        long toRemoveHash = HashHelper.longHash(keyHash, hashCapacity);
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
                            final long lastStringIndex = OffHeapLongArray.get(keys_ptr, lastIndex);
                            OffHeapLongArray.set(keys_ptr, found, lastStringIndex);
                            final long lastKey = key_h(lastIndex);
                            setKey_h(found, lastKey);
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
    public final void put(final String insertStringKey, final long insertValue) {
        chunk.lock();
        try {
            update_ptr();
            final long keyHash = HashHelper.hash(insertStringKey);
            if (keys_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                reallocate(0, 0, Constants.MAP_INITIAL_CAPACITY);
                setKey(0, insertStringKey);
                setKey_h(0, keyHash);
                setValue(0, insertValue);
                long mapSize = OffHeapLongArray.get(addr, SIZE);
                long capacity = OffHeapLongArray.get(addr, CAPACITY);
                setHash((int) HashHelper.longHash(keyHash, capacity * 2), 0);
                setNext(0, -1);
                mapSize++;
                OffHeapLongArray.set(addr, SIZE, mapSize);
            } else {
                long mapSize = OffHeapLongArray.get(addr, SIZE);
                long capacity = OffHeapLongArray.get(addr, CAPACITY);
                long hashCapacity = capacity * 2;
                long insertKeyHash = HashHelper.longHash(keyHash, hashCapacity);
                long currentHash = hash(insertKeyHash);
                long m = currentHash;
                long found = -1;
                while (m >= 0) {
                    if (keyHash == key_h(m)) {
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
                    setKey(lastIndex, insertStringKey);
                    setKey_h(lastIndex, keyHash);
                    setValue(lastIndex, insertValue);
                    setHash((int) HashHelper.longHash(keyHash, capacity * 2), lastIndex);
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
        } finally {
            chunk.unlock();
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
            final long capacity = OffHeapLongArray.get(addr, CAPACITY);
            final long keys_ptr = OffHeapLongArray.get(addr, KEYS);
            if (keys_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapStringArray.free(keys_ptr, capacity);
            }
            final long keys_h_ptr = OffHeapLongArray.get(addr, KEYS_H);
            if (keys_h_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(keys_h_ptr);
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
        OffHeapLongArray.set(new_addr, KEYS, OffHeapStringArray.cloneArray(keys_ptr, capacity));
        long keys_h_ptr = OffHeapLongArray.get(addr, KEYS_H);
        OffHeapLongArray.set(keys_h_ptr, KEYS_H, OffHeapLongArray.cloneArray(keys_h_ptr, capacity));
        long values_ptr = OffHeapLongArray.get(addr, VALUES);
        OffHeapLongArray.set(new_addr, VALUES, OffHeapLongArray.cloneArray(values_ptr, capacity));
        long nexts_ptr = OffHeapLongArray.get(addr, NEXTS);
        OffHeapLongArray.set(new_addr, NEXTS, OffHeapLongArray.cloneArray(nexts_ptr, capacity));
        long hashs_ptr = OffHeapLongArray.get(addr, HASHS);
        OffHeapLongArray.set(new_addr, HASHS, OffHeapLongArray.cloneArray(hashs_ptr, capacity));
        return new_addr;
    }

}



