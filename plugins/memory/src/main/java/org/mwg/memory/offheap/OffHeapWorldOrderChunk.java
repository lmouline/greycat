
package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.WorldOrderChunk;
import org.mwg.struct.Buffer;
import org.mwg.struct.LongLongMapCallBack;
import org.mwg.utility.Base64;
import org.mwg.utility.HashHelper;

final class OffHeapWorldOrderChunk implements WorldOrderChunk {

    private static final int LOCK = 0;
    private static final int LOCK_EXT = 1;
    private static final int MAGIC = 2;
    private static final int EXTRA = 3;
    private static final int SIZE = 4;
    private static final int CAPACITY = 5;
    private static final int KV = 6;
    private static final int NEXT = 7;
    private static final int HASH = 8;
    private static final int DIRTY = 9;
    private static final int CHUNK_SIZE = 10;

    private final OffHeapChunkSpace space;
    private final long index;
    private final long addr;

    OffHeapWorldOrderChunk(final OffHeapChunkSpace p_space, final long p_index) {
        index = p_index;
        space = p_space;
        long temp_addr = space.addrByIndex(index);
        if (temp_addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            temp_addr = OffHeapLongArray.allocate(CHUNK_SIZE);
            space.setAddrByIndex(index, temp_addr);
            //init the initial values
            OffHeapLongArray.set(temp_addr, LOCK, 0);
            OffHeapLongArray.set(temp_addr, LOCK_EXT, 0);
            OffHeapLongArray.set(temp_addr, MAGIC, 0);
            OffHeapLongArray.set(temp_addr, CAPACITY, 0);
            OffHeapLongArray.set(temp_addr, DIRTY, 0);
            OffHeapLongArray.set(temp_addr, SIZE, 0);
            OffHeapLongArray.set(temp_addr, EXTRA, Constants.NULL_LONG);
            OffHeapLongArray.set(temp_addr, KV, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(temp_addr, NEXT, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(temp_addr, HASH, OffHeapConstants.OFFHEAP_NULL_PTR);
        }
        addr = temp_addr;
    }

    public static void free(final long addr) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long hash_addr = OffHeapLongArray.get(addr, HASH);
            if (hash_addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(hash_addr);
            }
            final long next_addr = OffHeapLongArray.get(addr, NEXT);
            if (next_addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(next_addr);
            }
            final long kv_addr = OffHeapLongArray.get(addr, KV);
            if (kv_addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(kv_addr);
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
        return OffHeapLongArray.get(addr, EXTRA);
    }

    @Override
    public final long magic() {
        return OffHeapLongArray.get(addr, MAGIC);
    }

    @Override
    public final void setExtra(final long extraValue) {
        OffHeapLongArray.set(addr, EXTRA, extraValue);
    }

    @Override
    public final long index() {
        return index;
    }

    @Override
    public final long size() {
        return OffHeapLongArray.get(addr, SIZE);
    }

    @Override
    public final void lock() {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK_EXT, 0, 1)) ;
    }

    @Override
    public final void unlock() {
        if (!OffHeapLongArray.compareAndSwap(addr, LOCK_EXT, 1, 0)) {
            throw new RuntimeException("CAS Error !!!");
        }
    }

    @Override
    public final void each(final LongLongMapCallBack callback) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        try {
            long size = OffHeapLongArray.get(addr, SIZE);
            long kv_addr = OffHeapLongArray.get(addr, KV);
            for (long i = 0; i < size; i++) {
                callback.on(OffHeapLongArray.get(kv_addr, i * 2), OffHeapLongArray.get(kv_addr, i * 2 + 1));
            }
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                System.out.println("CAS error !!!");
            }
        }
    }

    @Override
    public final long get(final long key) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        try {
            if (OffHeapLongArray.get(addr, SIZE) > 0) {
                final long index = HashHelper.longHash(key, OffHeapLongArray.get(addr, CAPACITY) * 2);
                final long hash_addr = OffHeapLongArray.get(addr, HASH);
                final long kv_addr = OffHeapLongArray.get(addr, KV);
                final long kv_next = OffHeapLongArray.get(addr, NEXT);
                long m = OffHeapLongArray.get(hash_addr, index);
                while (m >= 0) {
                    if (key == OffHeapLongArray.get(kv_addr, m * 2)) {
                        return OffHeapLongArray.get(kv_addr, (m * 2) + 1);
                    } else {
                        m = OffHeapLongArray.get(kv_next, m);
                    }
                }
            }
            return Constants.NULL_LONG;
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                System.out.println("CAS error !!!");
            }
        }
    }

    @Override
    public final void put(final long key, final long value) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        try {
            internal_put(key, value, true);
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                System.out.println("CAS error !!!");
            }
        }
    }

    private void internal_put(final long key, final long value, final boolean notifyUpdate) {
        long capacity = OffHeapLongArray.get(addr, CAPACITY);
        long size = OffHeapLongArray.get(addr, SIZE);
        if (capacity > 0) {
            long kv_addr = OffHeapLongArray.get(addr, KV);
            long hash_addr = OffHeapLongArray.get(addr, HASH);
            long next_addr = OffHeapLongArray.get(addr, NEXT);
            long hashIndex = HashHelper.longHash(key, capacity * 2);
            long m = OffHeapLongArray.get(hash_addr, hashIndex);
            long found = -1;
            while (m >= 0) {
                if (key == OffHeapLongArray.get(kv_addr, m * 2)) {
                    found = m;
                    break;
                }
                m = OffHeapLongArray.get(next_addr, m);
            }
            if (found == -1) {
                if (capacity == size) {
                    resize(capacity * 2);
                    capacity = OffHeapLongArray.get(addr, CAPACITY);
                    kv_addr = OffHeapLongArray.get(addr, KV);
                    hash_addr = OffHeapLongArray.get(addr, HASH);
                    next_addr = OffHeapLongArray.get(addr, NEXT);
                    hashIndex = HashHelper.longHash(key, capacity * 2);
                }
                OffHeapLongArray.set(kv_addr, size * 2, key);
                OffHeapLongArray.set(kv_addr, size * 2 + 1, value);
                long previousHash = OffHeapLongArray.get(hash_addr, hashIndex);
                OffHeapLongArray.set(next_addr, size, previousHash);
                OffHeapLongArray.set(hash_addr, hashIndex, size);
                size++;
                OffHeapLongArray.set(addr, SIZE, size);
                OffHeapLongArray.set(addr, MAGIC, OffHeapLongArray.get(addr, MAGIC) + 1);
                if (notifyUpdate && OffHeapLongArray.get(addr, DIRTY) != 1) {
                    OffHeapLongArray.set(addr, DIRTY, 1);
                    if (space != null) {
                        space.notifyUpdate(index);
                    }
                }
            } else {
                if (OffHeapLongArray.get(kv_addr, found * 2 + 1) != value) {
                    OffHeapLongArray.set(kv_addr, found * 2 + 1, value);
                    OffHeapLongArray.set(addr, MAGIC, OffHeapLongArray.get(addr, MAGIC) + 1);
                    if (notifyUpdate && OffHeapLongArray.get(addr, DIRTY) != 1) {
                        OffHeapLongArray.set(addr, DIRTY, 1);
                        if (space != null) {
                            space.notifyUpdate(index);
                        }
                    }
                }
            }
        } else {
            capacity = Constants.MAP_INITIAL_CAPACITY;
            OffHeapLongArray.set(addr, CAPACITY, capacity);
            OffHeapLongArray.set(addr, NEXT, OffHeapLongArray.allocate(capacity));
            long hash_addr = OffHeapLongArray.allocate(capacity * 2);
            OffHeapLongArray.set(addr, HASH, hash_addr);
            long kv_addr = OffHeapLongArray.allocate(capacity * 2);
            OffHeapLongArray.set(addr, KV, kv_addr);
            OffHeapLongArray.set(addr, SIZE, 1);
            OffHeapLongArray.set(kv_addr, 0, key);
            OffHeapLongArray.set(kv_addr, 1, value);
            OffHeapLongArray.set(hash_addr, HashHelper.longHash(key, capacity * 2), 0);
            if (notifyUpdate && OffHeapLongArray.get(addr, DIRTY) != 1) {
                OffHeapLongArray.set(addr, DIRTY, 1);
                if (space != null) {
                    space.notifyUpdate(index);
                }
            }
        }
    }

    private boolean resize(final long newCapacity) {
        final long capacity = OffHeapLongArray.get(addr, CAPACITY);
        long kv_addr = OffHeapLongArray.get(addr, KV);
        if (newCapacity > capacity) {
            if (kv_addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.set(addr, KV, OffHeapLongArray.allocate(newCapacity * 2));
                OffHeapLongArray.set(addr, HASH, OffHeapLongArray.allocate(newCapacity * 2));
                OffHeapLongArray.set(addr, NEXT, OffHeapLongArray.allocate(newCapacity));
                OffHeapLongArray.set(addr, CAPACITY, newCapacity);
                return true;
            } else {
                //extend kv
                long new_kv_addr = OffHeapLongArray.reallocate(kv_addr, newCapacity * 2);
                if (new_kv_addr != kv_addr) {
                    OffHeapLongArray.set(addr, KV, new_kv_addr);
                }
                //extend hash
                long hash_addr = OffHeapLongArray.get(addr, HASH);
                long new_hash_addr = OffHeapLongArray.reallocate(hash_addr, newCapacity * 2);
                if (new_hash_addr != hash_addr) {
                    OffHeapLongArray.set(addr, HASH, new_hash_addr);
                }
                OffHeapLongArray.reset(new_hash_addr, newCapacity * 2);
                //extend next
                long next_addr = OffHeapLongArray.get(addr, NEXT);
                long new_next_addr = OffHeapLongArray.reallocate(next_addr, newCapacity);
                if (new_next_addr != next_addr) {
                    OffHeapLongArray.set(addr, NEXT, new_next_addr);
                }
                OffHeapLongArray.reset(new_next_addr, newCapacity);
                long size = OffHeapLongArray.get(addr, SIZE);
                long newHashCapacity = newCapacity * 2;
                for (long i = 0; i < size; i++) {
                    long loopIndex = HashHelper.longHash(OffHeapLongArray.get(new_kv_addr, i * 2), newHashCapacity);
                    long previousHashed = OffHeapLongArray.get(new_hash_addr, loopIndex);
                    OffHeapLongArray.set(new_next_addr, i, previousHashed);
                    OffHeapLongArray.set(new_hash_addr, loopIndex, i);
                }
                OffHeapLongArray.set(addr, CAPACITY, newCapacity);
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public final void load(final Buffer buffer) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        try {
            if (buffer == null || buffer.length() == 0) {
                return;
            }
            final boolean isInitial = (OffHeapLongArray.get(addr, KV) == OffHeapConstants.OFFHEAP_NULL_PTR);
            long cursor = 0;
            long bufferSize = buffer.length();
            boolean initDone = false;
            long previousStart = 0;
            long loopKey = Constants.NULL_LONG;
            while (cursor < bufferSize) {
                if (buffer.read(cursor) == Constants.CHUNK_SEP) {
                    if (!initDone) {
                        final long mapSize = Base64.decodeToLongWithBounds(buffer, 0, cursor);
                        final int closePowerOfTwo = (int) Math.pow(2, Math.ceil(Math.log(mapSize) / Math.log(2)));
                        resize(closePowerOfTwo);
                        initDone = true;
                    } else {
                        //extra char read
                        OffHeapLongArray.set(addr, EXTRA, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    previousStart = cursor + 1;
                } else if (buffer.read(cursor) == Constants.CHUNK_SUB_SEP) {
                    if (loopKey != Constants.NULL_LONG) {
                        long loopValue = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        internal_put(loopKey, loopValue, !isInitial);
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
                internal_put(loopKey, loopValue, !isInitial);
            }
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                System.out.println("CAS error !!!");
            }
        }
    }

    @Override
    public final void remove(long key) {
        throw new RuntimeException("Not implemented yet!!!");
    }

    @Override
    public final byte chunkType() {
        return ChunkType.WORLD_ORDER_CHUNK;
    }

    @Override
    public final void save(final Buffer buffer) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        try {
            long size = OffHeapLongArray.get(addr, SIZE);
            Base64.encodeLongToBuffer(size, buffer);
            buffer.write(Constants.CHUNK_SEP);
            long extra = OffHeapLongArray.get(addr, EXTRA);
            if (extra != Constants.NULL_LONG) {
                Base64.encodeLongToBuffer(extra, buffer);
                buffer.write(Constants.CHUNK_SEP);
            }
            boolean isFirst = true;
            final long kv_addr = OffHeapLongArray.get(addr, KV);
            for (long i = 0; i < size; i++) {
                if (!isFirst) {
                    buffer.write(Constants.CHUNK_SUB_SEP);
                }
                isFirst = false;
                Base64.encodeLongToBuffer(OffHeapLongArray.get(kv_addr, i * 2), buffer);
                buffer.write(Constants.CHUNK_SUB_SUB_SEP);
                Base64.encodeLongToBuffer(OffHeapLongArray.get(kv_addr, i * 2 + 1), buffer);
            }
            OffHeapLongArray.set(addr, DIRTY, 0);
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                System.out.println("CAS error !!!");
            }
        }
    }

}



