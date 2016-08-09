
package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.core.CoreConstants;
import org.mwg.chunk.WorldOrderChunk;
import org.mwg.utility.HashHelper;
import org.mwg.utility.Unsafe;
import org.mwg.utility.Base64;
import org.mwg.chunk.ChunkType;
import org.mwg.struct.Buffer;
import org.mwg.struct.LongLongMapCallBack;

import java.util.Arrays;

final class HeapWorldOrderChunk implements WorldOrderChunk {

    /**
     * @ignore ts
     */
    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    private final HeapChunkSpace _space;
    private final long _index;

    private volatile int _lock;
    private volatile long _magic;
    private volatile long _extra;

    private volatile int _size;
    private int _capacity;
    private long[] _kv;
    private int[] _next;
    private int[] _hash;

    /**
     * @ignore ts
     */
    private static final long _lockOffset;

    /** @ignore ts */
    static {
        try {
            _lockOffset = unsafe.objectFieldOffset(HeapWorldOrderChunk.class.getDeclaredField("_lock"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    HeapWorldOrderChunk(final HeapChunkSpace p_space, final long p_index) {
        _index = p_index;
        _space = p_space;

        _lock = 0;
        _magic = 0;
        _extra = CoreConstants.NULL_LONG;
        _size = 0;
        _capacity = 0;
        _kv = null;
        _next = null;
        _hash = null;

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
    public final long extra() {
        return this._extra;
    }

    @Override
    public final void setExtra(final long extraValue) {
        this._extra = extraValue;
    }

    /**
     * @native ts
     */
    @Override
    public final void lock() {
        while (!unsafe.compareAndSwapInt(this, _lockOffset, 0, 1)) ;
    }

    /**
     * @native ts
     */
    @Override
    public final void unlock() {
        if (!unsafe.compareAndSwapInt(this, _lockOffset, 1, 0)) {
            throw new RuntimeException("CAS Error !!!");
        }
    }

    @Override
    public final long magic() {
        return this._magic;
    }

    @Override
    public final void each(final LongLongMapCallBack callback) {
        for (int i = 0; i < _size; i++) {
            callback.on(_kv[i * 2], _kv[i * 2 + 1]);
        }
    }

    @Override
    public synchronized final long get(final long key) {
        if (_size > 0) {
            final int index = (int) HashHelper.longHash(key, _capacity * 2);
            int m = _hash[index];
            while (m >= 0) {
                if (key == _kv[m * 2]) {
                    return _kv[(m * 2) + 1];
                } else {
                    m = _next[m];
                }
            }
        }
        return CoreConstants.NULL_LONG;
    }

    @Override
    public synchronized final void put(final long key, final long value) {
        internal_put(key, value, true);
    }

    private void internal_put(final long key, final long value, final boolean notifyUpdate) {
        if (_size > 0) {
            //we need to init
            int hashIndex = (int) HashHelper.longHash(key, _capacity * 2);
            int m = _hash[hashIndex];
            int found = -1;
            while (m >= 0) {
                if (key == _kv[m * 2]) {
                    found = m;
                }
                m = _next[m];
            }
            if (found == -1) {
                if (_capacity == _size) {
                    resize(_capacity * 2);
                    hashIndex = (int) HashHelper.longHash(key, _capacity * 2);
                }
                _kv[_size * 2] = key;
                _kv[_size * 2 + 1] = value;
                _next[_size] = _hash[hashIndex];
                _hash[hashIndex] = _size;
                _size++;
                _magic = _magic + 1;
                if (notifyUpdate) {
                    if (_space != null) {
                        _space.notifyUpdate(_index);
                    }
                }
            } else {
                if (_kv[m * 2 + 1] != value) {
                    _kv[m * 2 + 1] = value;
                    _magic = _magic + 1;
                    if (notifyUpdate) {
                        if (_space != null) {
                            _space.notifyUpdate(_index);
                        }
                    }
                }
            }
        } else {
            _capacity = Constants.MAP_INITIAL_CAPACITY;
            _next = new int[_capacity];
            Arrays.fill(_next, 0, _capacity, -1);
            _hash = new int[_capacity * 2];
            Arrays.fill(_hash, 0, _capacity * 2, -1);
            _kv = new long[_capacity * 2];
            _size = 1;
            _kv[0] = key;
            _kv[1] = value;
            _hash[(int) HashHelper.longHash(key, _capacity * 2)] = 0;
            if (notifyUpdate) {
                if (_space != null) {
                    _space.notifyUpdate(_index);
                }
            }
        }
    }

    private boolean resize(final int newCapacity) {
        if (newCapacity > _capacity) {
            if (_kv == null) {
                _kv = new long[newCapacity * 2];
                _hash = new int[newCapacity * 2];
                _next = new int[newCapacity];
                _capacity = newCapacity;
                return true;
            } else {
                final long[] temp_kv = new long[newCapacity * 2];
                System.arraycopy(_kv, 0, temp_kv, 0, _size * 2);
                final int[] temp_next = new int[newCapacity];
                final int[] temp_hash = new int[newCapacity * 2];
                Arrays.fill(temp_next, 0, newCapacity, -1);
                Arrays.fill(temp_hash, 0, newCapacity * 2, -1);
                for (int i = 0; i < _size; i++) {
                    int loopIndex = (int) HashHelper.longHash(temp_kv[i * 2], newCapacity * 2);
                    temp_next[i] = temp_hash[loopIndex];
                    temp_hash[loopIndex] = i;
                }
                _capacity = newCapacity;
                _hash = temp_hash;
                _next = temp_next;
                _kv = temp_kv;
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public synchronized final void load(final Buffer buffer) {
        if(buffer == null || buffer.length() == 0){
            return;
        }
        final boolean isInitial = _kv == null;
        long cursor = 0;
        long bufferSize = buffer.length();
        boolean initDone = false;
        long previousStart = 0;
        long loopKey = CoreConstants.NULL_LONG;
        while (cursor < bufferSize) {
            if (buffer.read(cursor) == CoreConstants.CHUNK_SEP) {
                if (!initDone) {
                    resize((int) Base64.decodeToLongWithBounds(buffer, 0, cursor));
                    initDone = true;
                } else {
                    //extra char read
                    _extra = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                }
                previousStart = cursor + 1;
            } else if (buffer.read(cursor) == CoreConstants.CHUNK_SUB_SEP) {
                if (loopKey != CoreConstants.NULL_LONG) {
                    long loopValue = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    internal_put(loopKey, loopValue, !isInitial);
                    //reset key for next round
                    loopKey = CoreConstants.NULL_LONG;
                }
                previousStart = cursor + 1;
            } else if (buffer.read(cursor) == CoreConstants.CHUNK_SUB_SUB_SEP) {
                loopKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                previousStart = cursor + 1;
            }
            //loop in all case
            cursor++;
        }
        if (loopKey != CoreConstants.NULL_LONG) {
            long loopValue = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
            internal_put(loopKey, loopValue, !isInitial);
        }
    }

    @Override
    public final long index() {
        return _index;
    }

    @Override
    public synchronized final void remove(long key) {
        throw new RuntimeException("Not implemented yet!!!");
    }

    @Override
    public final long size() {
        return _size;
    }

    @Override
    public synchronized final void save(final Buffer buffer) {
        Base64.encodeLongToBuffer(_size, buffer);
        buffer.write(CoreConstants.CHUNK_SEP);
        if (_extra != CoreConstants.NULL_LONG) {
            Base64.encodeLongToBuffer(_extra, buffer);
            buffer.write(CoreConstants.CHUNK_SEP);
        }
        boolean isFirst = true;
        for (int i = 0; i < _size; i++) {
            if (!isFirst) {
                buffer.write(CoreConstants.CHUNK_SUB_SEP);
            }
            isFirst = false;
            Base64.encodeLongToBuffer(_kv[i * 2], buffer);
            buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
            Base64.encodeLongToBuffer(_kv[i * 2 + 1], buffer);
        }
    }

    @Override
    public final byte chunkType() {
        return ChunkType.WORLD_ORDER_CHUNK;
    }

}



