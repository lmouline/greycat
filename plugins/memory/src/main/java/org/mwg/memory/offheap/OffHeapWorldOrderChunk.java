
package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.WorldOrderChunk;
import org.mwg.struct.Buffer;
import org.mwg.struct.LongLongMapCallBack;
import org.mwg.utility.Base64;
import org.mwg.utility.HashHelper;

final class OffHeapWorldOrderChunk implements WorldOrderChunk {

    private static final int DIRTY = 0;
    private static final int SIZE = 1;
    private static final int CAPACITY = 2;
    private static final int LOCK_EXT = 3;
    private static final int MAGIC = 4;
    private static final int EXTRA = 5;
    private static final int HASH_SUB = 6;

    private static final int KV_OFFSET = 7;

    private final OffHeapChunkSpace space;
    private final long index;//direct pointer to space

    OffHeapWorldOrderChunk(final OffHeapChunkSpace p_space, final long p_index) {
        index = p_index;
        space = p_space;
        long temp_addr = space.addrByIndex(index);
        if (temp_addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            long initialCapacity = Constants.MAP_INITIAL_CAPACITY;
            temp_addr = OffHeapLongArray.allocate(KV_OFFSET + (initialCapacity * 2));
            space.setAddrByIndex(index, temp_addr);
            //init the initial values
            OffHeapLongArray.set(temp_addr, DIRTY, 0);
            OffHeapLongArray.set(temp_addr, SIZE, 0);
            OffHeapLongArray.set(temp_addr, CAPACITY, initialCapacity);
            OffHeapLongArray.set(temp_addr, LOCK_EXT, 0);
            OffHeapLongArray.set(temp_addr, MAGIC, 0);
            OffHeapLongArray.set(temp_addr, EXTRA, Constants.NULL_LONG);
            OffHeapLongArray.set(temp_addr, HASH_SUB, OffHeapConstants.OFFHEAP_NULL_PTR);
        }
    }

    private void setKV(final long addr, final long key, final long value, final long index) {
        OffHeapLongArray.set(addr, KV_OFFSET + (index * 2), key);
        OffHeapLongArray.set(addr, KV_OFFSET + (index * 2) + 1, value);
    }

    private long key(final long addr, final long index) {
        return OffHeapLongArray.get(addr, KV_OFFSET + (index * 2));
    }

    private long value(final long addr, final long index) {
        return OffHeapLongArray.get(addr, KV_OFFSET + (index * 2) + 1);
    }

    private long hash(final long sub_hash_ptr, final long capacity, final long index) {
        return OffHeapLongArray.get(sub_hash_ptr, capacity + index);
    }

    private long next(final long sub_hash_ptr, final long index) {
        return OffHeapLongArray.get(sub_hash_ptr, index);
    }

    static void free(final long addr) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long hash_sub = OffHeapLongArray.get(addr, HASH_SUB);
            if (hash_sub != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(hash_sub);
            }
            OffHeapLongArray.free(addr);
        }
    }

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

    @Override
    public final long extra() {
        return OffHeapLongArray.get(space.addrByIndex(index), EXTRA);
    }

    @Override
    public final byte chunkType() {
        return ChunkType.WORLD_ORDER_CHUNK;
    }

    @Override
    public final long magic() {
        return OffHeapLongArray.get(space.addrByIndex(index), MAGIC);
    }

    @Override
    public final void setExtra(final long extraValue) {
        OffHeapLongArray.set(space.addrByIndex(index), EXTRA, extraValue);
    }

    @Override
    public final long index() {
        return index;
    }

    @Override
    public final long size() {
        return OffHeapLongArray.get(space.addrByIndex(index), SIZE);
    }

    @Override
    public final void lock() {
        space.lockByIndex(index);
        try {
            final long addr = space.addrByIndex(index);
            while (!OffHeapLongArray.compareAndSwap(addr, LOCK_EXT, 0, 1)) ;
        } finally {
            space.unlockByIndex(index);
        }
    }

    @Override
    public final void unlock() {
        space.lockByIndex(index);
        try {
            final long addr = space.addrByIndex(index);
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK_EXT, 1, 0)) {
                throw new RuntimeException("CAS Error !!!");
            }
        } finally {
            space.unlockByIndex(index);
        }
    }

    @Override
    public final void each(final LongLongMapCallBack callback) {
        space.lockByIndex(index);
        try {
            final long addr = space.addrByIndex(index);
            final long size = OffHeapLongArray.get(addr, SIZE);
            for (long i = 0; i < size; i++) {
                callback.on(key(addr, i), value(addr, i));
            }
        } finally {
            space.unlockByIndex(index);
        }
    }

    @Override
    public final long get(final long requestKey) {
        long result = Constants.NULL_LONG;
        space.lockByIndex(index);
        try {
            final long addr = space.addrByIndex(index);
            final long size = OffHeapLongArray.get(addr, SIZE);
            if (size > 0) {
                final long hash_sub_ptr = OffHeapLongArray.get(addr, HASH_SUB);
                if (hash_sub_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    /* NO HASH STRUCTURE, LET'S ITERATES */
                    for (long i = 0; i < size; i++) {
                        if (requestKey == key(addr, i)) {
                            result = value(addr, i);
                            break;
                        }
                    }
                } else {
                    /* HASH STRUCTURE IS PRESENT LET'S USE IT */
                    final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                    final long hashed_requestKey = HashHelper.longHash(requestKey, capacity * 2);
                    long m = hash(hash_sub_ptr, capacity, hashed_requestKey);
                    while (m >= 0) {
                        if (requestKey == key(addr, m)) {
                            result = value(addr, m);
                            break;
                        } else {
                            m = next(hash_sub_ptr, m);
                        }
                    }
                }
            }
        } finally {
            space.unlockByIndex(index);
        }
        return result;
    }

    @Override
    public final void put(final long insertKey, final long insertValue) {
        space.lockByIndex(index);
        try {
            internal_put(space.addrByIndex(index), insertKey, insertValue, true);
        } finally {
            space.unlockByIndex(index);
        }
    }

    private long resize(final long currentAddr, final long previousSize, final long previousCapacity, final long newCapacity) {
        if (newCapacity > previousCapacity) {
            final long newAddr = OffHeapLongArray.reallocate(currentAddr, KV_OFFSET + newCapacity * 2);
            space.setAddrByIndex(index, newAddr);
            long sub_hash = OffHeapLongArray.get(newAddr, HASH_SUB);
            if (sub_hash == OffHeapConstants.OFFHEAP_NULL_PTR) {
                if (newCapacity > Constants.MAP_INITIAL_CAPACITY) {
                    sub_hash = OffHeapLongArray.allocate(newCapacity * 3);
                }
            } else {
                sub_hash = OffHeapLongArray.reallocate(sub_hash, newCapacity * 3);
                OffHeapLongArray.reset(sub_hash, newCapacity * 3);
            }
            if (sub_hash != OffHeapConstants.OFFHEAP_NULL_PTR) {
                /* reHash everything */
                final long double_newCapacity = newCapacity * 2;
                for (long i = 0; i < previousSize; i++) {
                    long hashed_loop_key = HashHelper.longHash(key(newAddr, i), double_newCapacity);
                    OffHeapLongArray.set(sub_hash, i, hash(sub_hash, newCapacity, hashed_loop_key));
                    OffHeapLongArray.set(sub_hash, newCapacity + hashed_loop_key, i);
                }
                OffHeapLongArray.set(newAddr, HASH_SUB, sub_hash);
            }
            OffHeapLongArray.set(newAddr, CAPACITY, newCapacity);
            return newAddr;
        } else {
            return currentAddr;
        }
    }

    private long internal_put(final long initialAddr, final long insertKey, final long insertValue, final boolean notifyUpdate) {

        long addr = initialAddr;
        long size = OffHeapLongArray.get(addr, SIZE);
        long capacity = OffHeapLongArray.get(addr, CAPACITY);
        //   long double_capacity = capacity * 2;

        long hashed_requestKey = HashHelper.longHash(insertKey, capacity * 2);

        long foundIndex = -1;
        long hash_sub_ptr = OffHeapLongArray.get(addr, HASH_SUB);
        if (hash_sub_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            //NO HASH STRUCTURE, LET'S ITERATES
            for (long i = 0; i < size; i++) {
                if (insertKey == key(addr, i)) {
                    foundIndex = i;
                    break;
                }
            }
        } else {
            //HASH STRUCTURE IS PRESENT LET'S USE IT
            long m = hash(hash_sub_ptr, capacity, hashed_requestKey);
            while (m >= 0) {
                if (insertKey == key(addr, m)) {
                    foundIndex = m;
                    break;
                } else {
                    m = next(hash_sub_ptr, m);
                }
            }
        }
        //ok not found
        if (foundIndex == -1) {
            if (capacity == size) {
                long newCapacity = capacity * 2;
                addr = resize(addr, size, capacity, newCapacity);
                capacity = newCapacity;
                hash_sub_ptr = OffHeapLongArray.get(addr, HASH_SUB);
                hashed_requestKey = HashHelper.longHash(insertKey, capacity * 2);
            }
            setKV(addr, insertKey, insertValue, size);
            //if present, update hashing structure
            if (hash_sub_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.set(hash_sub_ptr, size, hash(hash_sub_ptr, capacity, hashed_requestKey));
                OffHeapLongArray.set(hash_sub_ptr, capacity + hashed_requestKey, size);
            }
            OffHeapLongArray.set(addr, SIZE, size + 1);
            OffHeapLongArray.set(addr, MAGIC, OffHeapLongArray.get(addr, MAGIC) + 1);
            if (notifyUpdate && OffHeapLongArray.get(addr, DIRTY) != 1) {
                OffHeapLongArray.set(addr, DIRTY, 1);
                space.notifyUpdate(index);
            }
        } else {
            if (value(addr, foundIndex) != insertValue) {
                setKV(addr, insertKey, insertValue, foundIndex);
                OffHeapLongArray.set(addr, MAGIC, OffHeapLongArray.get(addr, MAGIC) + 1);
                if (notifyUpdate && OffHeapLongArray.get(addr, DIRTY) != 1) {
                    OffHeapLongArray.set(addr, DIRTY, 1);
                    space.notifyUpdate(index);
                }
            }
        }
        return addr;
    }

    @Override
    public final void load(final Buffer buffer) {
        if (buffer == null || buffer.length() == 0) {
            return;
        }
        space.lockByIndex(index);
        try {
            long addr = space.addrByIndex(index);
            final long initialSize = OffHeapLongArray.get(addr, SIZE);
            final long initialCapacity = OffHeapLongArray.get(addr, CAPACITY);
            final boolean isInitial = initialSize == 0;
            long cursor = 0;
            long bufferSize = buffer.length();
            boolean initDone = false;
            long previousStart = 0;
            long loopKey = Constants.NULL_LONG;
            while (cursor < bufferSize) {
                if (buffer.read(cursor) == Constants.CHUNK_SEP) {
                    if (!initDone) {
                        final long mapSize = Base64.decodeToLongWithBounds(buffer, 0, cursor);
                        final long closePowerOfTwo = (long) Math.pow(2, Math.ceil(Math.log(mapSize) / Math.log(2)));
                        addr = resize(addr, initialSize, initialCapacity, closePowerOfTwo);
                        initDone = true;
                    } else {
                        //extra char read
                        OffHeapLongArray.set(addr, EXTRA, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    previousStart = cursor + 1;
                } else if (buffer.read(cursor) == Constants.CHUNK_SUB_SEP) {
                    if (loopKey != Constants.NULL_LONG) {
                        long loopValue = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        addr = internal_put(addr, loopKey, loopValue, !isInitial);
                        //reset key for next round
                        loopKey = Constants.NULL_LONG;
                    }
                    previousStart = cursor + 1;
                } else if (buffer.read(cursor) == Constants.CHUNK_SUB_SUB_SEP) {
                    loopKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                }
                //loop in all case
                cursor++;
            }
            if (loopKey != Constants.NULL_LONG) {
                long loopValue = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                internal_put(addr, loopKey, loopValue, !isInitial);
            }
        } finally {
            space.unlockByIndex(index);
        }
    }

    @Override
    public final void remove(long key) {
        throw new RuntimeException("Not implemented yet!!!");
    }

    @Override
    public final void save(final Buffer buffer) {
        space.lockByIndex(index);
        try {
            final long addr = space.addrByIndex(index);
            final long size = OffHeapLongArray.get(addr, SIZE);
            Base64.encodeLongToBuffer(size, buffer);
            buffer.write(Constants.CHUNK_SEP);
            final long extra = OffHeapLongArray.get(addr, EXTRA);
            if (extra != Constants.NULL_LONG) {
                Base64.encodeLongToBuffer(extra, buffer);
                buffer.write(Constants.CHUNK_SEP);
            }
            boolean isFirst = true;
            for (long i = 0; i < size; i++) {
                if (!isFirst) {
                    buffer.write(Constants.CHUNK_SUB_SEP);
                }
                isFirst = false;
                //save KV
                Base64.encodeLongToBuffer(key(addr, i), buffer);
                buffer.write(Constants.CHUNK_SUB_SUB_SEP);
                Base64.encodeLongToBuffer(value(addr, i), buffer);
            }
            OffHeapLongArray.set(addr, DIRTY, 0);//set dirty to false
        } finally {
            space.unlockByIndex(index);
        }
    }

}



