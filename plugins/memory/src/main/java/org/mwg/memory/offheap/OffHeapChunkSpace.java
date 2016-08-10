
package org.mwg.memory.offheap;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.chunk.*;
import org.mwg.struct.Buffer;
import org.mwg.utility.HashHelper;
import org.mwg.utility.KeyHelper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ignore ts
 */
public class OffHeapChunkSpace implements ChunkSpace, ChunkListener {

    private static final int HASH_LOAD_FACTOR = 4;

    /**
     * Global variables
     */
    private final long _capacity;
    private final long _hashCapacity;
    private final long _saveBatchSize;
    private final Stack _lru;

    private final Graph _graph;

    /**
     * HashMap variables
     */
    private final AtomicInteger _elementCount;

    private final long _elementNext;
    private final long _elementHash;
    private final long _elementValues;
    private final long _elementHashLock;

    private final AtomicReference<InternalDirtyStateList> _dirtyState;

    @Override
    public Graph graph() {
        return _graph;
    }

    final private class InternalDirtyStateList  {

        private final long _dirtyElements;
        private final long _max;
        private final AtomicLong _iterationCounter;
        private final AtomicLong _nextCounter;
        private final OffHeapChunkSpace _parent;

        InternalDirtyStateList(long dirtiesCapacity, OffHeapChunkSpace p_parent) {
            this._dirtyElements = OffHeapLongArray.allocate(dirtiesCapacity);
            this._nextCounter = new AtomicLong(0);
            this._iterationCounter = new AtomicLong(0);
            this._max = dirtiesCapacity;
            this._parent = p_parent;
        }

        @Override
        public void free() {
            OffHeapLongArray.free(_dirtyElements);
        }

        @Override
        public boolean hasNext() {
            return this._iterationCounter.get() < this._nextCounter.get();
        }

        @Override
        public Chunk next() {
            long previous;
            long next;
            boolean shouldReturnNull = false;
            do {
                previous = this._iterationCounter.get();
                if (this._nextCounter.get() == previous) {
                    OffHeapLongArray.free(this._dirtyElements);
                    return null;
                }
                next = previous + 1;
            } while (!this._iterationCounter.compareAndSet(previous, next));
            long chunkIndex = OffHeapLongArray.get(_dirtyElements, previous);
            long chunkRootAddr = OffHeapLongArray.get(_elementValues, chunkIndex);
            OffHeapChunk chunk = this._parent.internal_create(chunkRootAddr);
            if (chunk != null) {
                chunk.setIndex(chunkIndex);
            }
            return chunk;
        }

        public boolean declareDirty(long dirtyIndex) {
            long previousDirty;
            long nextDirty;
            do {
                previousDirty = this._nextCounter.get();
                if (previousDirty == this._max) {
                    return false;
                }
                nextDirty = previousDirty + 1;
            } while (!this._nextCounter.compareAndSet(previousDirty, nextDirty));
            //ok we have the token previous
            OffHeapLongArray.set(_dirtyElements, previousDirty, dirtyIndex);
            return true;
        }

        @Override
        public long size() {
            return this._nextCounter.get();
        }

    }

    public OffHeapChunkSpace(long initialCapacity, long saveBatchSize, Graph p_graph) {
        this._graph = p_graph;

        if (saveBatchSize > initialCapacity) {
            throw new RuntimeException("Save Batch Size can't be bigger than cache size");
        }

        this._capacity = initialCapacity;
        this._hashCapacity = initialCapacity * HASH_LOAD_FACTOR;

        this._saveBatchSize = saveBatchSize;
        this._lru = new OffHeapFixedStack(initialCapacity); //only one object
        this._dirtyState = new AtomicReference<InternalDirtyStateList>();
        this._dirtyState.set(new InternalDirtyStateList(this._saveBatchSize, this));

        //init std variables
        this._elementNext = OffHeapLongArray.allocate(initialCapacity);
        this._elementValues = OffHeapLongArray.allocate(initialCapacity);

        this._elementHash = OffHeapLongArray.allocate(_hashCapacity);
        this._elementHashLock = OffHeapLongArray.allocate(_hashCapacity);

        this._elementCount = new AtomicInteger(0);
    }

    @Override
    public final Chunk getAndMark(byte type, long world, long time, long id) {
        long hashIndex = HashHelper.tripleHash(type, world, time, id, this._hashCapacity);
        long m = OffHeapLongArray.get(_elementHash, hashIndex);
        while (m != OffHeapConstants.OFFHEAP_NULL_PTR) {
            long foundChunkPtr = OffHeapLongArray.get(_elementValues, m);
            if (foundChunkPtr != OffHeapConstants.OFFHEAP_NULL_PTR
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE) == type
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_WORLD) == world
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TIME) == time
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_ID) == id
                    ) {

                //CAS on the mark of the chunk
                long previousFlag;
                long newFlag;
                do {
                    previousFlag = OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS);
                    newFlag = previousFlag + 1;
                }
                while (!OffHeapLongArray.compareAndSwap(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS, previousFlag, newFlag));

                if (newFlag == 1) {
                    //was at zero before, risky operation, check selectWith LRU
                    if (this._lru.dequeue(m)) {
                        OffHeapChunk chunk = internal_create(foundChunkPtr);
                        if (chunk != null) {
                            chunk.setIndex(m);
                        }
                        return chunk;
                    } else {
                        if (OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS) > 1) {
                            //ok fine we are several on the same object...
                            OffHeapChunk chunk = internal_create(foundChunkPtr);
                            if (chunk != null) {
                                chunk.setIndex(m);
                            }
                            return chunk;
                        } else {
                            //better return null the object will be recycled by somebody else...
                            return null;
                        }
                    }
                } else {
                    OffHeapChunk chunk = internal_create(foundChunkPtr);
                    if (chunk != null) {
                        chunk.setIndex(m);
                    }
                    return chunk;
                }
            } else {
                m = OffHeapLongArray.get(_elementNext, m);
            }
        }
        return null;
    }

    @Override
    public Chunk get(final long index) {
        final long addr = OffHeapLongArray.get(_elementValues, index);
        final OffHeapChunk chunk = internal_create(addr);
        if (chunk != null) {
            chunk.setIndex(index);
        }
        return chunk;
    }

    @Override
    public void getOrLoadAndMark(final byte type, final long world, final long time, final long id, final Callback<Chunk> callback) {
        Chunk fromMemory = getAndMark(type, world, time, id);
        if (fromMemory != null) {
            callback.on(fromMemory);
        } else {
            final Buffer keys = graph().newBuffer();
            KeyHelper.keyToBuffer(keys, type, world, time, id);
            graph().storage().get(keys, new Callback<Buffer>() {
                @Override
                public void on(Buffer result) {
                    if (result != null) {
                        Chunk loadedChunk_0 = create(type, world, time, id, result, null);
                        result.free();
                        Chunk loadedChunk = putAndMark(type, world, time, id, loadedChunk_0);
                        if (loadedChunk != loadedChunk_0) {
                            free(loadedChunk_0);
                        }
                        callback.on(loadedChunk);
                    } else {
                        keys.free();
                        callback.on(null);
                    }
                }
            });
        }
    }

    @Override
    public void unmark(long index) {
        final long addr = OffHeapLongArray.get(_elementValues, index);

//CAS on the mark of the chunk
        long previousFlag;
        long newFlag;
        do {
            previousFlag = OffHeapLongArray.get(addr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS);
            newFlag = previousFlag - 1;
        }
        while (!OffHeapLongArray.compareAndSwap(addr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS, previousFlag, newFlag));
        //check if this object hasField to be re-enqueue to the list of available
        if (newFlag == 0) {
            //declare available for recycling
            this._lru.enqueue(index);
        }
    }

    @Override
    public void mark(long index) {
        final long addr = OffHeapLongArray.get(_elementValues, index);

        //TODO
        long previousFlag;
        long newFlag;
        do {
            previousFlag = OffHeapLongArray.get(addr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS);
            newFlag = previousFlag + 1;
        }
        while (!OffHeapLongArray.compareAndSwap(addr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS, previousFlag, newFlag));
        if (OffHeapLongArray.get(index, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS) == 1) {
            throw new RuntimeException("Access by index is supposed to be safe...internal problem!");
        }
    }

    private OffHeapChunk internal_create(long addr) {
        byte chunkType = (byte) OffHeapLongArray.get(addr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE);
        switch (chunkType) {
            case ChunkType.STATE_CHUNK:
                return new OffHeapStateChunk(this, addr, null, null);
            case ChunkType.TIME_TREE_CHUNK:
                return new OffHeapTimeTreeChunk(this, addr, null);
            case ChunkType.WORLD_ORDER_CHUNK:
                return new OffHeapWorldOrderChunk(this, addr, null);
            case ChunkType.GEN_CHUNK:
                return new OffHeapGenChunk(this, addr, null);
            default:
                return null;
        }
    }

    /*
    @Override
    public void unmark(byte type, long world, long time, long id) {
        long index = HashHelper.tripleHash(type, world, time, id, this._hashCapacity);
        long m = OffHeapLongArray.get(_elementHash, index);
        while (m != CoreConstants.OFFHEAP_NULL_PTR) {
            long foundChunkPtr = OffHeapLongArray.get(_elementValues, m);
            if (foundChunkPtr != CoreConstants.OFFHEAP_NULL_PTR
                    && OffHeapLongArray.get(foundChunkPtr, CoreConstants.OFFHEAP_CHUNK_INDEX_TYPE) == type
                    && OffHeapLongArray.get(foundChunkPtr, CoreConstants.OFFHEAP_CHUNK_INDEX_WORLD) == world
                    && OffHeapLongArray.get(foundChunkPtr, CoreConstants.OFFHEAP_CHUNK_INDEX_TIME) == time
                    && OffHeapLongArray.get(foundChunkPtr, CoreConstants.OFFHEAP_CHUNK_INDEX_ID) == id
                    ) {

                //CAS on the mark of the chunk
                long previousFlag;
                long newFlag;
                do {
                    previousFlag = OffHeapLongArray.get(foundChunkPtr, CoreConstants.OFFHEAP_CHUNK_INDEX_MARKS);
                    newFlag = previousFlag - 1;
                }
                while (!OffHeapLongArray.compareAndSwap(foundChunkPtr, CoreConstants.OFFHEAP_CHUNK_INDEX_MARKS, previousFlag, newFlag));
                //check if this object hasField to be re-enqueue to the list of available
                if (newFlag == 0) {
                    //declare available for recycling
                    this._lru.enqueue(m);
                }
                //in any case we go out, we have found the good chunk
                return;
            } else {
                m = OffHeapLongArray.get(_elementNext, m);
            }
        }
    }*/

    @Override
    public void unmarkChunk(Chunk chunk) {
        long chunkAddr = ((OffHeapChunk) chunk).addr();
        long previousMarks;
        long newMarks;
        do {
            previousMarks = OffHeapLongArray.get(chunkAddr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS);
            newMarks = previousMarks - 1;
        }
        while (!OffHeapLongArray.compareAndSwap(chunkAddr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS, previousMarks, newMarks));
        if (newMarks == 0) {
            long world = chunk.world();
            long time = chunk.time();
            long id = chunk.id();
            byte type = chunk.chunkType();
            long hashIndex = HashHelper.tripleHash(type, world, time, id, this._hashCapacity);
            long m = OffHeapLongArray.get(_elementHash, hashIndex);
            while (m != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long foundChunkPtr = OffHeapLongArray.get(_elementValues, m);
                if (foundChunkPtr != OffHeapConstants.OFFHEAP_NULL_PTR
                        && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE) == type
                        && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_WORLD) == world
                        && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TIME) == time
                        && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_ID) == id
                        ) {
                    //declare available for recycling
                    this._lru.enqueue(m);
                    break;
                } else {
                    m = OffHeapLongArray.get(_elementNext, m);
                }
            }
        }
    }

    @Override
    public Chunk create(byte p_type, long p_world, long p_time, long p_id, Buffer initialPayload, Chunk previousChunk) {
        OffHeapChunk newChunk = null;
        switch (p_type) {
            case ChunkType.STATE_CHUNK:
                newChunk = new OffHeapStateChunk(this, OffHeapConstants.OFFHEAP_NULL_PTR, initialPayload, previousChunk);
                break;
            case ChunkType.WORLD_ORDER_CHUNK:
                newChunk = new OffHeapWorldOrderChunk(this, OffHeapConstants.OFFHEAP_NULL_PTR, initialPayload);
                break;
            case ChunkType.TIME_TREE_CHUNK:
                newChunk = new OffHeapTimeTreeChunk(this, OffHeapConstants.OFFHEAP_NULL_PTR, initialPayload);
                break;
            case ChunkType.GEN_CHUNK:
                newChunk = new OffHeapGenChunk(this, OffHeapConstants.OFFHEAP_NULL_PTR, initialPayload);
                break;
        }
        if (newChunk != null) {
            long newChunkPtr = newChunk.addr();
            OffHeapLongArray.set(newChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_WORLD, p_world);
            OffHeapLongArray.set(newChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TIME, p_time);
            OffHeapLongArray.set(newChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_ID, p_id);

            OffHeapLongArray.set(newChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_FLAGS, 0);
            OffHeapLongArray.set(newChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE, p_type);
            OffHeapLongArray.set(newChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS, 0);
        }
        return newChunk;
    }

    @Override
    public Chunk putAndMark(byte type, long world, long time, long id, Chunk p_elem) {

        final long elemPtr = ((OffHeapChunk) p_elem).addr();

        //First try to mark the chunk, the mark should be previously to zero
        long previousFlag;
        long newFlag;
        do {
            previousFlag = OffHeapLongArray.get(elemPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS);
            newFlag = previousFlag + 1;
        }
        while (!OffHeapLongArray.compareAndSwap(elemPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS, previousFlag, newFlag));

        if (newFlag != 1) {
            throw new RuntimeException("Warning, trying to put an unsafe object " + p_elem);
        }

        /*
        final long world = OffHeapLongArray.get(elemPtr, CoreConstants.OFFHEAP_CHUNK_INDEX_WORLD);
        final long time = OffHeapLongArray.get(elemPtr, CoreConstants.OFFHEAP_CHUNK_INDEX_TIME);
        final long id = OffHeapLongArray.get(elemPtr, CoreConstants.OFFHEAP_CHUNK_INDEX_ID);
        final byte type = (byte) OffHeapLongArray.get(elemPtr, CoreConstants.OFFHEAP_CHUNK_INDEX_TYPE);
*/
        long entry = -1;
        long hashIndex = HashHelper.tripleHash(type, world, time, id, this._hashCapacity);
        long m = OffHeapLongArray.get(_elementHash, hashIndex);
        while (m != -1) {
            long foundChunkPtr = OffHeapLongArray.get(_elementValues, m);
            if (foundChunkPtr != OffHeapConstants.OFFHEAP_NULL_PTR
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE) == type
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_WORLD) == world
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TIME) == time
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_ID) == id
                    ) {
                entry = m;
                break;
            }
            m = OffHeapLongArray.get(_elementNext, m);
        }
        if (entry == -1) {
            //we look for nextIndex
            long currentVictimIndex = this._lru.dequeueTail();
            if (currentVictimIndex == -1) {
                //TODO cache is full :(
                System.gc();
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentVictimIndex = this._lru.dequeueTail();
                if (currentVictimIndex == -1) {
                    throw new RuntimeException("mwDB crashed, cache is full, please avoid to much retention of nodes or augment cache capacity!");
                }
            }
            long currentVictimPtr = OffHeapLongArray.get(_elementValues, currentVictimIndex);
            if (currentVictimPtr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long victimWorld = OffHeapLongArray.get(currentVictimPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_WORLD);
                long victimTime = OffHeapLongArray.get(currentVictimPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TIME);
                long victimObj = OffHeapLongArray.get(currentVictimPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_ID);
                byte victimType = (byte) OffHeapLongArray.get(currentVictimPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE);

                long indexVictim = HashHelper.tripleHash(victimType, victimWorld, victimTime, victimObj, this._hashCapacity);

                //negociate a lock on the indexVictim hash
                while (!OffHeapLongArray.compareAndSwap(_elementHashLock, indexVictim, -1, 0)) ;
                //we obtains the token, now remove the element
                m = OffHeapLongArray.get(_elementHash, indexVictim);
                long last = OffHeapConstants.OFFHEAP_NULL_PTR;
                while (m != OffHeapConstants.OFFHEAP_NULL_PTR) {
                    long foundChunkPtr = OffHeapLongArray.get(_elementValues, m);
                    if (foundChunkPtr != OffHeapConstants.OFFHEAP_NULL_PTR
                            && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE) == victimType
                            && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_WORLD) == victimWorld
                            && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TIME) == victimTime
                            && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_ID) == victimObj
                            ) {
                        break;
                    }
                    last = m;
                    m = OffHeapLongArray.get(_elementNext, m);
                }
                //POP THE VALUE FROM THE NEXT LIST
                if (last == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    OffHeapLongArray.set(_elementHash, indexVictim, OffHeapLongArray.get(_elementNext, m));
                } else {
                    OffHeapLongArray.set(_elementNext, last, OffHeapLongArray.get(_elementNext, m));
                }
                OffHeapLongArray.set(_elementNext, m, OffHeapConstants.OFFHEAP_NULL_PTR);
                //free the lock

                if (!OffHeapLongArray.compareAndSwap(_elementHashLock, indexVictim, 0, -1)) {
                    throw new RuntimeException("CAS Error !!!");
                }
                this._elementCount.decrementAndGet();
                //FREE VICTIM FROM MEMORY
                byte chunkType = (byte) OffHeapLongArray.get(currentVictimPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE);
                switch (chunkType) {
                    case ChunkType.STATE_CHUNK:
                        OffHeapStateChunk.free(currentVictimPtr);
                        break;
                    case ChunkType.TIME_TREE_CHUNK:
                        OffHeapTimeTreeChunk.free(currentVictimPtr);
                        break;
                    case ChunkType.WORLD_ORDER_CHUNK:
                        OffHeapWorldOrderChunk.free(currentVictimPtr);
                        break;
                    case ChunkType.GEN_CHUNK:
                        OffHeapGenChunk.free(currentVictimPtr);
                }
            }
            OffHeapLongArray.set(_elementValues, currentVictimIndex, elemPtr);
            //negociate the lock to write on hashIndex
            while (!OffHeapLongArray.compareAndSwap(_elementHashLock, hashIndex, -1, 0)) ;
            OffHeapLongArray.set(_elementNext, currentVictimIndex, OffHeapLongArray.get(_elementHash, hashIndex));
            OffHeapLongArray.set(_elementHash, hashIndex, currentVictimIndex);
            //free the lock
            if (!OffHeapLongArray.compareAndSwap(_elementHashLock, hashIndex, 0, -1)) {
                throw new RuntimeException("CAS Error !!!");
            }

            this._elementCount.incrementAndGet();
            ((OffHeapChunk) p_elem).setIndex(currentVictimIndex);
            return p_elem;
        } else {
            //return the previous chunk
            OffHeapChunk chunk = internal_create(OffHeapLongArray.get(_elementValues, entry));
            if (chunk != null) {
                chunk.setIndex(entry);
            }
            return chunk;
        }
    }

    @Override
    public void declareDirty(Chunk dirtyChunk) {
        long world = dirtyChunk.world();
        long time = dirtyChunk.time();
        long id = dirtyChunk.id();
        byte type = dirtyChunk.chunkType();
        long hashIndex = HashHelper.tripleHash(type, world, time, id, this._hashCapacity);
        long m = OffHeapLongArray.get(_elementHash, hashIndex);
        while (m != OffHeapConstants.OFFHEAP_NULL_PTR) {
            long foundChunkPtr = OffHeapLongArray.get(_elementValues, m);
            if (foundChunkPtr != OffHeapConstants.OFFHEAP_NULL_PTR
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE) == type
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_WORLD) == world
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TIME) == time
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_ID) == id
                    ) {

                long previousFlag;
                long nextFlag;
                do {
                    previousFlag = OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_FLAGS);
                    nextFlag = previousFlag | Constants.DIRTY_BIT;
                }
                while (!OffHeapLongArray.compareAndSwap(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_FLAGS, previousFlag, nextFlag));
                if (previousFlag != nextFlag) {
                    //add an additional mark
                    long previousMarks;
                    long nextMarks;
                    do {
                        previousMarks = OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS);
                        nextMarks = previousMarks + 1;
                    }
                    while (!OffHeapLongArray.compareAndSwap(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS, previousMarks, nextMarks));
                    //add to dirty list
                    boolean success = false;
                    while (!success) {
                        InternalDirtyStateList previousState = this._dirtyState.get();
                        success = previousState.declareDirty(m);
                        if (!success) {
                            this._graph.save(null);
                        }
                    }
                }
                return;
            }
            m = OffHeapLongArray.get(_elementNext, m);
        }
        throw new RuntimeException("Try to declare a non existing object!");
    }

    @Override
    public void declareClean(Chunk cleanChunk) {
        long world = cleanChunk.world();
        long time = cleanChunk.time();
        long id = cleanChunk.id();
        byte type = cleanChunk.chunkType();
        long hashIndex = HashHelper.tripleHash(type, world, time, id, this._hashCapacity);
        long m = OffHeapLongArray.get(_elementHash, hashIndex);
        while (m != OffHeapConstants.OFFHEAP_NULL_PTR) {
            long foundChunkPtr = OffHeapLongArray.get(_elementValues, m);
            if (foundChunkPtr != OffHeapConstants.OFFHEAP_NULL_PTR
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE) == type
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_WORLD) == world
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TIME) == time
                    && OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_ID) == id
                    ) {

                //remove the dirty bit
                long previousFlag;
                long nextFlag;
                do {
                    previousFlag = OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_FLAGS);
                    nextFlag = previousFlag & ~Constants.DIRTY_BIT;
                }
                while (!OffHeapLongArray.compareAndSwap(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_FLAGS, previousFlag, nextFlag));
                //unmark
                long previousMarks;
                long nextMarks;
                do {
                    previousMarks = OffHeapLongArray.get(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS);
                    nextMarks = previousMarks - 1;
                }
                while (!OffHeapLongArray.compareAndSwap(foundChunkPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS, previousMarks, nextMarks));
                if (nextMarks == 0) {
                    this._lru.enqueue(m);
                }
                return;
            }
            m = OffHeapLongArray.get(_elementNext, m);
        }
        throw new RuntimeException("Try to declare a non existing object!");
    }

    @Override
    public final void clear() {
        //TODO
    }

    @Override
    public void freeAll() {
        for (long i = 0; i < this._capacity; i++) {
            long previousPtr = OffHeapLongArray.get(_elementValues, i);
            if (previousPtr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                byte chunkType = (byte) OffHeapLongArray.get(previousPtr, OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE);
                switch (chunkType) {
                    case ChunkType.STATE_CHUNK:
                        OffHeapStateChunk.free(previousPtr);
                        break;
                    case ChunkType.TIME_TREE_CHUNK:
                        OffHeapTimeTreeChunk.free(previousPtr);
                        break;
                    case ChunkType.WORLD_ORDER_CHUNK:
                        OffHeapWorldOrderChunk.free(previousPtr);
                        break;
                    case ChunkType.GEN_CHUNK:
                        OffHeapGenChunk.free(previousPtr);
                        break;
                }
            }
        }
        _dirtyState.get().free();
        OffHeapLongArray.free(_elementNext);
        OffHeapLongArray.free(_elementHash);
        OffHeapLongArray.free(_elementValues);
        OffHeapLongArray.free(_elementHashLock);
        _lru.free();
    }

    @Override
    public void free(Chunk chunk) {
        OffHeapChunk casted = (OffHeapChunk) chunk;
        switch (casted.chunkType()) {
            case ChunkType.STATE_CHUNK:
                OffHeapStateChunk.free(casted.addr());
                break;
            case ChunkType.TIME_TREE_CHUNK:
                OffHeapTimeTreeChunk.free(casted.addr());
                break;
            case ChunkType.WORLD_ORDER_CHUNK:
                OffHeapWorldOrderChunk.free(casted.addr());
                break;
            case ChunkType.GEN_CHUNK:
                OffHeapGenChunk.free(casted.addr());
                break;
        }
    }


    @Override
    public final long size() {
        return this._elementCount.get();
    }

    @Override
    public long available() {
        return _lru.size();
    }
}



