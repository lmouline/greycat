
package org.mwg.core.chunk.heap;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.core.CoreConstants;
import org.mwg.core.chunk.ChunkListener;
import org.mwg.core.chunk.Stack;
import org.mwg.utility.HashHelper;
import org.mwg.utility.KeyHelper;
import org.mwg.chunk.Chunk;
import org.mwg.chunk.ChunkIterator;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.struct.Buffer;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class HeapChunkSpace implements ChunkSpace, ChunkListener {

    private static final int HASH_LOAD_FACTOR = 4;

    private final int _maxEntries;
    private final int _hashEntries;
    private final int _saveBatchSize;

    private final AtomicInteger _elementCount;

    private final Stack _lru;

    private final int[] _hashNext;
    private final int[] _hash;
    private final long[] _chunkWorlds;
    private final long[] _chunkTimes;
    private final long[] _chunkIds;
    private final byte[] _chunkTypes;
    private final Chunk[] _chunkValues;
    private final long[] _chunkMarks;
    private final boolean[] _dirties;

    private final AtomicReference<InternalDirtyStateList> _dirtyState;
    private final Graph _graph;

    @Override
    public final Graph graph() {
        return this._graph;
    }

    final long worldByIndex(long index) {
        return this._chunkWorlds[(int) index];
    }

    final long timeByIndex(long index) {
        return this._chunkTimes[(int) index];
    }

    final long idByIndex(long index) {
        return this._chunkIds[(int) index];
    }

    final private class InternalDirtyStateList implements ChunkIterator {

        private final AtomicInteger _nextCounter;
        private final int[] _dirtyElements;
        private final int _max;
        private final AtomicInteger _iterationCounter;
        private final HeapChunkSpace _parent;

        InternalDirtyStateList(int maxSize, HeapChunkSpace p_parent) {
            this._dirtyElements = new int[maxSize];
            this._nextCounter = new AtomicInteger(0);
            this._iterationCounter = new AtomicInteger(0);
            this._max = maxSize;
            this._parent = p_parent;
        }

        @Override
        public boolean hasNext() {
            return this._iterationCounter.get() < this._nextCounter.get();
        }

        @Override
        public Chunk next() {
            int previous;
            int next;
            do {
                previous = this._iterationCounter.get();
                if (this._nextCounter.get() == previous) {
                    return null;
                }
                next = previous + 1;
            } while (!this._iterationCounter.compareAndSet(previous, next));
            return this._parent.getValues()[this._dirtyElements[previous]];
        }

        public boolean declareDirty(int dirtyIndex) {
            int previousDirty;
            int nextDirty;
            do {
                previousDirty = this._nextCounter.get();
                if (previousDirty == this._max) {
                    return false;
                }
                nextDirty = previousDirty + 1;
            } while (!this._nextCounter.compareAndSet(previousDirty, nextDirty));
            //ok we have the token previous
            this._dirtyElements[previousDirty] = dirtyIndex;
            return true;
        }

        @Override
        public long size() {
            return this._nextCounter.get();
        }

        @Override
        public void free() {
            //noop
        }
    }

    public Chunk[] getValues() {
        return _chunkValues;
    }

    public HeapChunkSpace(final int initialCapacity, final int saveBatchSize, final Graph p_graph) {
        _graph = p_graph;
        if (saveBatchSize > initialCapacity) {
            throw new RuntimeException("Save Batch Size can't be bigger than cache size");
        }
        _maxEntries = initialCapacity;
        _hashEntries = initialCapacity * HASH_LOAD_FACTOR;
        _saveBatchSize = saveBatchSize;
        _lru = new FixedStack(initialCapacity);
        _dirtyState = new AtomicReference<InternalDirtyStateList>();
        _dirtyState.set(new InternalDirtyStateList(saveBatchSize, this));
        _hashNext = new int[initialCapacity];
        Arrays.fill(_hashNext, 0, _maxEntries, -1);
        _chunkValues = new Chunk[initialCapacity];
        Arrays.fill(_chunkValues, 0, _maxEntries, null);
        _elementCount = new AtomicInteger(0);
        _hash = new int[_hashEntries];
        Arrays.fill(_hash, 0, _hashEntries, -1);
        _chunkWorlds = new long[_maxEntries];
        Arrays.fill(_chunkWorlds, 0, _maxEntries, -1);
        _chunkTimes = new long[_maxEntries];
        Arrays.fill(_chunkTimes, 0, _maxEntries, -1);
        _chunkIds = new long[_maxEntries];
        Arrays.fill(_chunkIds, 0, _maxEntries, -1);
        _chunkTypes = new byte[_maxEntries];
        Arrays.fill(_chunkTypes, 0, _maxEntries, (byte) -1);
        _chunkMarks = new long[_maxEntries];
        Arrays.fill(_chunkMarks, 0, _maxEntries, 0);
        _dirties = new boolean[_maxEntries];

    }

    @Override
    public final Chunk getAndMark(final byte type, final long world, final long time, final long id) {
        final int index = (int) HashHelper.tripleHash(type, world, time, id, this._hashEntries);
        int m = this._hash[index];
        int found = -1;
        while (m != -1) {
            if (_chunkTypes[m] == type
                    && _chunkWorlds[m] == world
                    && _chunkTimes[m] == time
                    && _chunkIds[m] == id) {

                //GET VALUE
                //if (mark(m) != -1) {
                found = m;
                break;
                //}
            } else {
                m = this._hashNext[m];
            }
        }
        if (found != -1) {
            HeapChunk foundChunk = (HeapChunk) this._chunkValues[found];
            if (foundChunk != null && foundChunk.mark() == 1) {
                //was at zero before, risky operation, check selectWith LRU
                this._lru.dequeue(m);
            }
            return foundChunk;
        } else {
            return null;
        }
    }

    /*
    public long mark(long index) {

        do {

        } while (unsafe.compareAndSwapLong());
    }*/

    @Override
    public final Chunk getByIndex(long index) {
        return this._chunkValues[(int) index];
    }

    @Override
    public final void getOrLoadAndMark(final byte type, final long world, final long time, final long id, final Callback<Chunk> callback) {
        Chunk fromMemory = getAndMark(type, world, time, id);
        if (fromMemory != null) {
            callback.on(fromMemory);
        } else {
            final Buffer keys = graph().newBuffer();
            KeyHelper.keyToBuffer(keys, type, world, time, id);
            graph().storage().get(keys, new Callback<Buffer>() {
                @Override
                public void on(final Buffer result) {
                    if (result != null) {
                        Chunk loadedChunk_0 = create(type, world, time, id, result, null);
                        result.free();
                        if (loadedChunk_0 == null) {
                            callback.on(null);
                        } else {
                            Chunk loadedChunk = putAndMark(type, world, time, id, loadedChunk_0);
                            if (loadedChunk != loadedChunk_0) {
                                freeChunk(loadedChunk_0);
                            }
                            callback.on(loadedChunk);
                        }
                    } else {
                        keys.free();
                        callback.on(null);
                    }
                }
            });
        }
    }

    @Override
    public void unmarkByIndex(long index) {
        HeapChunk foundChunk = (HeapChunk) this._chunkValues[(int) index];
        if (foundChunk != null) {
            if (foundChunk.unmark() == 0) {
                //declare available for recycling
                this._lru.enqueue(index);
            }
        }
    }

    @Override
    public void markByIndex(long index) {
        HeapChunk foundChunk = (HeapChunk) this._chunkValues[(int) index];
        if (foundChunk != null) {
            //GET VALUE
            if (foundChunk.mark() == 1) {
                //was at zero before, risky operation, check selectWith LRU
                this._lru.dequeue(index);
            }
        }
    }

    @Override
    public void unmarkChunk(Chunk chunk) {
        if (chunk != null) {
            unmarkByIndex(chunk.index());
        }
    }

    @Override
    public void freeChunk(Chunk chunk) {
        //NOOP
    }


    @Override
    public Chunk create(byte p_type, long p_world, long p_time, long p_id, Buffer p_initialPayload, Chunk origin) {
        switch (p_type) {
            case ChunkType.STATE_CHUNK:
                return new HeapStateChunk(this, p_initialPayload, origin);
            case ChunkType.WORLD_ORDER_CHUNK:
                return new HeapWorldOrderChunk(this, p_initialPayload);
            case ChunkType.TIME_TREE_CHUNK:
                return new HeapTimeTreeChunk(this, p_initialPayload);
            case ChunkType.GEN_CHUNK:
                return new HeapGenChunk(this, p_id, p_initialPayload);
        }
        return null;
    }

    //TODO, this method has performance issue
    @Override
    public Chunk putAndMark(final byte type, final long world, final long time, final long id, final Chunk p_elem) {
        //first mark the object
        HeapChunk heapChunk = (HeapChunk) p_elem;
        if (heapChunk.mark() != 1) {
            throw new RuntimeException("Warning, trying to put an unsafe object " + p_elem);
        }
        int entry = -1;
        int hashIndex = (int) HashHelper.tripleHash(type, world, time, id, this._hashEntries);
        int m = this._hash[hashIndex];
        while (m >= 0) {
            if (type == _chunkTypes[m] && world == _chunkWorlds[m] && time == _chunkTimes[m] && id == _chunkIds[m]) {
                entry = m;
                break;
            }
            m = this._hashNext[m];
        }
        if (entry == -1) {
            //we look for nextIndex
            int currentVictimIndex = (int) this._lru.dequeueTail();
            if (currentVictimIndex == -1) {
                //TODO cache is full :(
                System.gc();
                try {
                    System.err.println("GC failback...");
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                currentVictimIndex = (int) this._lru.dequeueTail();
                if (currentVictimIndex == -1) {
                    throw new RuntimeException("mwDB crashed, cache is full, please avoid to much retention of nodes or augment cache capacity!");
                }
            }
            if (this._chunkValues[currentVictimIndex] != null) {
                // Chunk victim = this._chunkValues[currentVictimIndex];
                final long victimWorld = _chunkWorlds[currentVictimIndex];
                final long victimTime = _chunkTimes[currentVictimIndex];
                final long victimObj = _chunkIds[currentVictimIndex];
                final byte victimType = _chunkTypes[currentVictimIndex];
                final int indexVictim = (int) HashHelper.tripleHash(victimType, victimWorld, victimTime, victimObj, this._hashEntries);

                m = _hash[indexVictim];
                int last = -1;
                while (m >= 0) {
                    if (victimType == _chunkTypes[m] && victimWorld == _chunkWorlds[m] && victimTime == _chunkTimes[m] && victimObj == _chunkIds[m]) {
                        break;
                    }
                    last = m;
                    m = _hashNext[m];
                }
                //POP THE VALUE FROM THE NEXT LIST
                if (last == -1) {
                    int previousNext = _hashNext[m];
                    _hash[indexVictim] = previousNext;
                } else {
                    if (m == -1) {
                        _hashNext[last] = -1;
                    } else {
                        _hashNext[last] = _hashNext[m];
                    }
                }
                _hashNext[m] = -1;//flag to dropped value
                //UNREF victim value object
                _chunkValues[currentVictimIndex] = null;

                //free the lock
                this._elementCount.decrementAndGet();
            }

            _chunkValues[currentVictimIndex] = p_elem;
            _chunkTypes[currentVictimIndex] = type;
            _chunkWorlds[currentVictimIndex] = world;
            _chunkTimes[currentVictimIndex] = time;
            _chunkIds[currentVictimIndex] = id;

            ((HeapChunk) p_elem).setIndex(currentVictimIndex);
            //negociate the lock to write on hashIndex
            _hashNext[currentVictimIndex] = _hash[hashIndex];
            _hash[hashIndex] = currentVictimIndex;
            //free the lock
            this._elementCount.incrementAndGet();
            return p_elem;
        } else {
            return _chunkValues[entry];
        }
    }

    @Override
    public ChunkIterator detachDirties() {
        return _dirtyState.getAndSet(new InternalDirtyStateList(this._saveBatchSize, this));
    }

    @Override
    public void declareDirty(Chunk dirtyChunk) {
        HeapChunk currentM = (HeapChunk) dirtyChunk;
        if (currentM.setFlags(CoreConstants.DIRTY_BIT, 0)) {
            //add an additional mark
            currentM.mark();
            //now enqueue in the dirtyList to be saved later
            boolean success = false;
            while (!success) {
                InternalDirtyStateList previousState = this._dirtyState.get();
                success = previousState.declareDirty((int) dirtyChunk.index());
                if (!success) {
                    this._graph.save(null);
                }
            }
        }
    }

    @Override
    public void declareClean(Chunk cleanChunk) {
        HeapChunk currentM = (HeapChunk) cleanChunk;
        currentM.setFlags(0, CoreConstants.DIRTY_BIT);
        //free the save mark
        if (currentM.unmark() == 0) {
            this._lru.enqueue(currentM.index());
        }
    }

    @Override
    public final void clear() {
        //TODO
    }

    @Override
    public final void free() {
        //TODO
    }

    @Override
    public final long size() {
        return this._elementCount.get();
    }

    @Override
    public final long available() {
        return _lru.size();
    }

    public final void printMarked() {
        for (int i = 0; i < _chunkValues.length; i++) {
            if (_chunkValues[i] != null) {
                if (_chunkValues[i].marks() != 0) {
                    switch (_chunkTypes[i]) {
                        case ChunkType.STATE_CHUNK:
                            System.out.println("STATE(" + _chunkWorlds[i] + "," + _chunkValues[i].time() + "," + _chunkValues[i].id() + ")->marks->" + _chunkValues[i].marks());
                            break;
                        case ChunkType.TIME_TREE_CHUNK:
                            System.out.println("TIME_TREE(" + _chunkWorlds[i] + "," + _chunkValues[i].time() + "," + _chunkValues[i].id() + ")->marks->" + _chunkValues[i].marks());
                            break;
                        case ChunkType.WORLD_ORDER_CHUNK:
                            System.out.println("WORLD_ORDER(" + _chunkWorlds[i] + "," + _chunkValues[i].time() + "," + _chunkValues[i].id() + ")->marks->" + _chunkValues[i].marks());
                            break;
                        case ChunkType.GEN_CHUNK:
                            System.out.println("GENERATOR(" + _chunkWorlds[i] + "," + _chunkValues[i].time() + "," + _chunkValues[i].id() + ")->marks->" + _chunkValues[i].marks());
                            break;
                    }
                }
            }
        }
    }

}



