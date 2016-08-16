
package org.mwg.core.chunk.heap;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.core.BlackHoleStorage;
import org.mwg.core.CoreConstants;
import org.mwg.core.chunk.Stack;
import org.mwg.utility.HashHelper;
import org.mwg.utility.KeyHelper;
import org.mwg.chunk.Chunk;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.struct.Buffer;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class HeapChunkSpace implements ChunkSpace {

    private static final int HASH_LOAD_FACTOR = 4;

    private final int _maxEntries;
    private final int _hashEntries;

    private final Stack _lru;
    private final Stack _dirtiesStack;

    private final int[] _hashNext;
    private final int[] _hash;

    private final AtomicLongArray _chunkWorlds;
    private final AtomicLongArray _chunkTimes;
    private final AtomicLongArray _chunkIds;

    //TODO transform to atomic
    private final byte[] _chunkTypes;

    private final AtomicReferenceArray<Chunk> _chunkValues;

    private final AtomicLongArray _chunkMarks;

    private final Graph _graph;

    @Override
    public final Graph graph() {
        return this._graph;
    }

    final long worldByIndex(long index) {
        return this._chunkWorlds.get((int) index);
    }

    final long timeByIndex(long index) {
        return this._chunkTimes.get((int) index);
    }

    final long idByIndex(long index) {
        return this._chunkIds.get((int) index);
    }

    public HeapChunkSpace(final int initialCapacity, final Graph p_graph) {
        _graph = p_graph;
        _maxEntries = initialCapacity;
        _hashEntries = initialCapacity * HASH_LOAD_FACTOR;
        _lru = new FixedStack(initialCapacity, true);
        _dirtiesStack = new FixedStack(initialCapacity, false);
        _hashNext = new int[initialCapacity];
        Arrays.fill(_hashNext, 0, _maxEntries, -1);

        _chunkValues = new AtomicReferenceArray<Chunk>(initialCapacity);

        _hash = new int[_hashEntries];
        Arrays.fill(_hash, 0, _hashEntries, -1);

        _chunkWorlds = new AtomicLongArray(_maxEntries);


        //Arrays.fill(_chunkWorlds, 0, _maxEntries, -1);
        _chunkTimes = new AtomicLongArray(_maxEntries);
        //Arrays.fill(_chunkTimes, 0, _maxEntries, -1);
        _chunkIds = new AtomicLongArray(_maxEntries);
        //Arrays.fill(_chunkIds, 0, _maxEntries, -1);
        _chunkTypes = new byte[_maxEntries];
        Arrays.fill(_chunkTypes, 0, _maxEntries, (byte) -1);

        _chunkMarks = new AtomicLongArray(_maxEntries);
        for (int i = 0; i < _maxEntries; i++) {
            _chunkMarks.set(i, 0);
        }
    }

    @Override
    public final Chunk getAndMark(final byte type, final long world, final long time, final long id) {
        final int index = (int) HashHelper.tripleHash(type, world, time, id, this._hashEntries);
        int m = this._hash[index];
        int found = -1;
        while (m != -1) {
            if (_chunkTypes[m] == type
                    && _chunkWorlds.get(m) == world
                    && _chunkTimes.get(m) == time
                    && _chunkIds.get(m) == id) {
                if (mark(m) > 0) {
                    found = m;
                }
                break;
            } else {
                m = this._hashNext[m];
            }
        }
        if (found != -1) {
            return this._chunkValues.get(found);
        } else {
            return null;
        }
    }

    @Override
    public final Chunk get(final long index) {
        return this._chunkValues.get((int) index);
    }

    @Override
    public final void getOrLoadAndMark(final byte type, final long world, final long time, final long id, final Callback<Chunk> callback) {
        final Chunk fromMemory = getAndMark(type, world, time, id);
        if (fromMemory != null) {
            callback.on(fromMemory);
        } else {
            final Buffer keys = graph().newBuffer();
            KeyHelper.keyToBuffer(keys, type, world, time, id);
            graph().storage().get(keys, new Callback<Buffer>() {
                @Override
                public void on(final Buffer result) {
                    if (result != null && result.length() > 0) {
                        Chunk loadedChunk = createAndMark(type, world, time, id);
                        loadedChunk.load(result);
                        result.free();
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
    public long mark(long index) {
        int castedIndex = (int) index;
        long before;
        long after;
        do {
            before = _chunkMarks.get(castedIndex);
            if (before != -1) {
                after = before + 1;
            } else {
                after = before;
            }
        } while (!_chunkMarks.compareAndSet(castedIndex, before, after));
        if (before == 0 && after == 1) {
            //was at zero before, risky operation, check selectWith LRU
            this._lru.dequeue(index);
        }
        return after;
    }

    @Override
    public void unmark(long index) {
        int castedIndex = (int) index;
        long before;
        long after;
        do {
            before = _chunkMarks.get(castedIndex);
            if (before > 0) {
                after = before - 1;
            } else {
                System.err.println("WARNING: DOUBLE UNMARK");
                after = before;
            }
        } while (!_chunkMarks.compareAndSet(castedIndex, before, after));
        if (before == 1 && after == 0) {
            //was at zero before, risky operation, check selectWith LRU
            this._lru.enqueue(index);
        }
    }

    @Override
    public void free(Chunk chunk) {
        //NOOP
    }

    @Override
    public synchronized Chunk createAndMark(final byte type, final long world, final long time, final long id) {
        //first mark the object
        int entry = -1;
        int hashIndex = (int) HashHelper.tripleHash(type, world, time, id, this._hashEntries);
        int m = this._hash[hashIndex];
        while (m >= 0) {
            if (type == _chunkTypes[m] && world == _chunkWorlds.get(m) && time == _chunkTimes.get(m) && id == _chunkIds.get(m)) {
                entry = m;
                break;
            }
            m = this._hashNext[m];
        }
        if (entry != -1) {
            long previous;
            long after;
            do {
                previous = _chunkMarks.get(entry);
                if (previous != -1) {
                    after = previous + 1;
                } else {
                    after = previous;
                }
            } while (!_chunkMarks.compareAndSet(entry, previous, after));
            if (after == (previous + 1)) {
                return _chunkValues.get(entry);
            }
        }
        int currentVictimIndex = -1;
        while (currentVictimIndex == -1) {
            int temp_victim = (int) this._lru.dequeueTail();
            if (temp_victim == -1) {
                break;
            } else {
                if (_chunkMarks.compareAndSet(temp_victim, 0, -1)) {
                    currentVictimIndex = temp_victim;
                }
            }
        }
        if (currentVictimIndex == -1) {
            // printMarked();
            throw new RuntimeException("mwDB crashed, cache is full, please avoid to much retention of nodes or augment cache capacity! available:" + available());
        }
        Chunk toInsert = null;
        switch (type) {
            case ChunkType.STATE_CHUNK:
                toInsert = new HeapStateChunk(this, currentVictimIndex);
                break;
            case ChunkType.WORLD_ORDER_CHUNK:
                toInsert = new HeapWorldOrderChunk(this, currentVictimIndex);
                break;
            case ChunkType.TIME_TREE_CHUNK:
                toInsert = new HeapTimeTreeChunk(this, currentVictimIndex);
                break;
            case ChunkType.GEN_CHUNK:
                toInsert = new HeapGenChunk(this, id, currentVictimIndex);
                break;
        }
        if (this._chunkValues.get(currentVictimIndex) != null) {
            // Chunk victim = this._chunkValues[currentVictimIndex];
            final long victimWorld = _chunkWorlds.get(currentVictimIndex);
            final long victimTime = _chunkTimes.get(currentVictimIndex);
            final long victimObj = _chunkIds.get(currentVictimIndex);
            final byte victimType = _chunkTypes[currentVictimIndex];
            final int indexVictim = (int) HashHelper.tripleHash(victimType, victimWorld, victimTime, victimObj, this._hashEntries);
            m = _hash[indexVictim];
            int last = -1;
            while (m >= 0) {
                if (victimType == _chunkTypes[m] && victimWorld == _chunkWorlds.get(m) && victimTime == _chunkTimes.get(m) && victimObj == _chunkIds.get(m)) {
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
            _hashNext[m] = -1;
        }
        _chunkValues.set(currentVictimIndex, toInsert);
        _chunkMarks.set(currentVictimIndex, 1);
        _chunkTypes[currentVictimIndex] = type;
        _chunkWorlds.set(currentVictimIndex, world);
        _chunkTimes.set(currentVictimIndex, time);
        _chunkIds.set(currentVictimIndex, id);
        //negociate the lock to write on hashIndex
        _hashNext[currentVictimIndex] = _hash[hashIndex];
        _hash[hashIndex] = currentVictimIndex;
        //free the lock
        return toInsert;
    }

    @Override
    public void notifyUpdate(long index) {
        if (_dirtiesStack.enqueue(index)) {
            mark(index);
        }
    }

    @Override
    public synchronized void save(final Callback<Boolean> callback) {
        final Buffer stream = this._graph.newBuffer();
        boolean isFirst = true;
        while (_dirtiesStack.size() != 0) {
            int tail = (int) _dirtiesStack.dequeueTail();
            Chunk loopChunk = _chunkValues.get(tail);
            //Save chunk Key
            if (isFirst) {
                isFirst = false;
            } else {
                stream.write(CoreConstants.BUFFER_SEP);
            }
            KeyHelper.keyToBuffer(stream, _chunkTypes[tail], _chunkWorlds.get(tail), _chunkTimes.get(tail), _chunkIds.get(tail));
            //Save chunk payload
            stream.write(CoreConstants.BUFFER_SEP);
            try {
                loopChunk.save(stream);
                unmark(tail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //shrink in case of i != full size
        this.graph().storage().put(stream, new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                //free all value
                stream.free();
                if (callback != null) {
                    callback.on(result);
                }
            }
        });
    }

    @Override
    public final void clear() {
        //TODO reset everything
    }

    @Override
    public final void freeAll() {
        //TODO reset everything
    }

    @Override
    public final long available() {
        return _lru.size();
    }

    public final void printMarked() {
        for (int i = 0; i < _chunkValues.length(); i++) {
            if (_chunkValues.get(i) != null) {
                if (_chunkMarks.get(i) != 0) {
                    switch (_chunkTypes[i]) {
                        case ChunkType.STATE_CHUNK:
                            System.out.println("STATE(" + _chunkWorlds.get(i) + "," + _chunkTimes.get(i) + "," + _chunkIds.get(i) + ")->marks->" + _chunkMarks.get(i));
                            break;
                        case ChunkType.TIME_TREE_CHUNK:
                            System.out.println("TIME_TREE(" + _chunkWorlds.get(i) + "," + _chunkTimes.get(i) + "," + _chunkIds.get(i) + ")->marks->" + _chunkMarks.get(i));
                            break;
                        case ChunkType.WORLD_ORDER_CHUNK:
                            System.out.println("WORLD_ORDER(" + _chunkWorlds.get(i) + "," + _chunkTimes.get(i) + "," + _chunkIds.get(i) + ")->marks->" + _chunkMarks.get(i));
                            break;
                        case ChunkType.GEN_CHUNK:
                            System.out.println("GENERATOR(" + _chunkWorlds.get(i) + "," + _chunkTimes.get(i) + "," + _chunkIds.get(i) + ")->marks->" + _chunkMarks.get(i));
                            break;
                    }
                }
            }
        }
    }

}



