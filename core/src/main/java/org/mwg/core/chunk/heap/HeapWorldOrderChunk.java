
package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.WorldOrderChunk;
import org.mwg.core.CoreConstants;
import org.mwg.struct.Buffer;
import org.mwg.struct.LongLongMapCallBack;
import org.mwg.utility.Base64;
import org.mwg.utility.HashHelper;
import org.mwg.utility.Unsafe;

import java.util.Arrays;

final class HeapWorldOrderChunk implements WorldOrderChunk {

    /**
     * @ignore ts
     */
    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    private final HeapChunkSpace _space;
    private final long _index;

    private volatile int _lock;
    private volatile int _externalLock;

    private volatile long _magic;
    private volatile long _extra;

    private volatile int _size;
    private int _capacity;
    private long[] _kv;
    private int[] _next;
    private boolean[] _diff;
    private int[] _hash;

    private boolean _dirty;

    /**
     * @ignore ts
     */
    private static final long _lockOffset;
    /**
     * @ignore ts
     */
    private static final long _externalLockOffset;

    /** @ignore ts */
    static {
        try {
            _lockOffset = unsafe.objectFieldOffset(HeapWorldOrderChunk.class.getDeclaredField("_lock"));
            _externalLockOffset = unsafe.objectFieldOffset(HeapWorldOrderChunk.class.getDeclaredField("_externalLock"));
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
        _diff = null;
        _hash = null;
        _dirty = false;
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

    /**
     * @native ts
     */
    @Override
    public final void externalLock() {
        while (!unsafe.compareAndSwapInt(this, _externalLockOffset, 0, 1)) ;
    }

    /**
     * @native ts
     */
    @Override
    public final void externalUnlock() {
        if (!unsafe.compareAndSwapInt(this, _externalLockOffset, 1, 0)) {
            throw new RuntimeException("CAS Error !!!");
        }
    }


    @Override
    public final long magic() {
        return this._magic;
    }

    @Override
    public synchronized final void each(final LongLongMapCallBack callback) {
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
        if (_capacity > 0) {
            //we need to init
            int hashIndex = (int) HashHelper.longHash(key, _capacity * 2);
            int m = _hash[hashIndex];
            int found = -1;
            while (m >= 0) {
                if (key == _kv[m * 2]) {
                    found = m;
                    break;
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
                if (notifyUpdate) {
                    _diff[_size] = true;
                }
                _next[_size] = _hash[hashIndex];
                _hash[hashIndex] = _size;
                _size++;
                _magic = _magic + 1;
                if (notifyUpdate && !_dirty) {
                    _dirty = true;
                    if (_space != null) {
                        _space.notifyUpdate(_index);
                    }
                }
            } else {
                if (_kv[found * 2 + 1] != value) {
                    _kv[found * 2 + 1] = value;
                    if (notifyUpdate) {
                        _diff[found] = true;
                    }
                    _magic = _magic + 1;
                    if (notifyUpdate && !_dirty) {
                        _dirty = true;
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
            _diff = new boolean[_capacity];
            CoreConstants.fillBooleanArray(_diff, false);
            _hash = new int[_capacity * 2];
            Arrays.fill(_hash, 0, _capacity * 2, -1);
            _kv = new long[_capacity * 2];
            _size = 1;
            _kv[0] = key;
            _kv[1] = value;
            if (notifyUpdate) {
                _diff[0] = true;
            }
            _hash[(int) HashHelper.longHash(key, _capacity * 2)] = 0;
            if (notifyUpdate && !_dirty) {
                _dirty = true;
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
                _diff = new boolean[newCapacity];
                _capacity = newCapacity;
                Arrays.fill(_next, 0, newCapacity, -1);
                CoreConstants.fillBooleanArray(_diff, false);
                Arrays.fill(_hash, 0, newCapacity * 2, -1);
                return true;
            } else {
                final long[] temp_kv = new long[newCapacity * 2];
                System.arraycopy(_kv, 0, temp_kv, 0, _size * 2);
                final boolean[] temp_diff = new boolean[newCapacity];
                CoreConstants.fillBooleanArray(temp_diff, false);
                System.arraycopy(_diff, 0, temp_diff, 0, _size);
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
                _diff = temp_diff;
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public synchronized final void load(final Buffer buffer) {
        internal_load(true, buffer);
    }

    @Override
    public synchronized void loadDiff(Buffer buffer) {
        internal_load(false, buffer);
    }

    private void internal_load(final boolean initial, final Buffer buffer) {
        if (buffer != null && buffer.length() > 0) {
            long cursor = 0;
            long bufferSize = buffer.length();
            boolean initDone = false;
            long previousStart = 0;
            long loopKey = CoreConstants.NULL_LONG;
            while (cursor < bufferSize) {
                final byte current = buffer.read(cursor);
                switch (current) {
                    case Constants.CHUNK_SEP:
                        _extra = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        previousStart = cursor + 1;
                        break;
                    case Constants.CHUNK_VAL_SEP:
                        if (!initDone) {
                            resize(Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                            initDone = true;
                        } else if (loopKey == CoreConstants.NULL_LONG) {
                            loopKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        } else {
                            long loopValue = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                            internal_put(loopKey, loopValue, !initial);
                            //reset key for next round
                            loopKey = CoreConstants.NULL_LONG;
                        }
                        previousStart = cursor + 1;
                        break;
                }
                cursor++;
            }
            if (!initDone) {
                resize((int) Base64.decodeToLongWithBounds(buffer, 0, cursor));
            } else {
                if (loopKey != CoreConstants.NULL_LONG) {
                    long loopValue = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    internal_put(loopKey, loopValue, !initial);
                }
            }
        }
    }

    @Override
    public final long index() {
        return _index;
    }

    @Override
    public final synchronized void remove(long key) {
        throw new RuntimeException("Not implemented yet!!!");
    }

    @Override
    public final int size() {
        return _size;
    }

    @Override
    public final byte chunkType() {
        return ChunkType.WORLD_ORDER_CHUNK;
    }

    @Override
    public final synchronized void save(final Buffer buffer) {
        if (_extra != CoreConstants.NULL_LONG) {
            Base64.encodeLongToBuffer(_extra, buffer);
            buffer.write(CoreConstants.CHUNK_SEP);
        }
        Base64.encodeIntToBuffer(_size, buffer);
        for (int i = 0; i < _size; i++) {
            buffer.write(CoreConstants.CHUNK_VAL_SEP);
            Base64.encodeLongToBuffer(_kv[i * 2], buffer);
            buffer.write(CoreConstants.CHUNK_VAL_SEP);
            Base64.encodeLongToBuffer(_kv[i * 2 + 1], buffer);
        }
        _dirty = false;
    }

    @Override
    public final synchronized void saveDiff(Buffer buffer) {
        if (_dirty) {
            if (_extra != CoreConstants.NULL_LONG) {
                Base64.encodeLongToBuffer(_extra, buffer);
                buffer.write(CoreConstants.CHUNK_SEP);
            }
            Base64.encodeIntToBuffer(_size, buffer);
            for (int i = 0; i < _size; i++) {
                if (_diff[i]) {
                    buffer.write(CoreConstants.CHUNK_VAL_SEP);
                    Base64.encodeLongToBuffer(_kv[i * 2], buffer);
                    buffer.write(CoreConstants.CHUNK_VAL_SEP);
                    Base64.encodeLongToBuffer(_kv[i * 2 + 1], buffer);
                }
            }
            _dirty = false;
        }
    }

}



