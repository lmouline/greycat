package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.chunk.GenChunk;
import org.mwg.utility.Base64;
import org.mwg.chunk.ChunkType;
import org.mwg.struct.Buffer;

final class HeapGenChunk implements GenChunk {

    private final HeapChunkSpace _space;
    private final long _index;

    /**
     * @native ts
     * private _prefix: Long;
     */
    private final long _prefix;
    private long _seed;

    /**
     * @native ts
     * this._index = p_index;
     * this._space = p_space;
     * this._prefix = Long.fromNumber(p_id).shiftLeft((org.mwg.Constants.LONG_SIZE - org.mwg.Constants.PREFIX_SIZE));
     * this._seed = 0;
     */
    HeapGenChunk(final HeapChunkSpace p_space, final long p_id, final long p_index) {
        _index = p_index;
        _space = p_space;
        //moves the prefix 53-size(short) times to the left;
        _prefix = p_id << (Constants.LONG_SIZE - Constants.PREFIX_SIZE);
        _seed = 0;
    }

    @Override
    public synchronized final void save(final Buffer buffer) {
        Base64.encodeLongToBuffer(_seed, buffer);
    }

    @Override
    public synchronized final void load(final Buffer buffer) {
        _seed = Base64.decodeToLongWithBounds(buffer, 0, buffer.length());
    }

    /**
     * @native ts
     * if (this._seed == org.mwg.Constants.KEY_PREFIX_MASK) {
     *  throw new Error("Object Index could not be created because it exceeded the capacity of the current prefix. Ask for a new prefix.");
     * }
     * this._seed++;
     * var nextIndex = this._seed;
     * if(this._space){
     *     this._space.notifyUpdate(this._index);
     * }
     * var objectKey = this._prefix.add(this._seed).toNumber();
     * if (objectKey >= org.mwg.Constants.NULL_LONG) {
     *  throw new Error("Object Index exceeds the maximum JavaScript number capacity. (2^"+org.mwg.Constants.LONG_SIZE+")");
     * }
     * return objectKey;
     */
    @Override
    public synchronized final long newKey() {
        if (_seed == Constants.KEY_PREFIX_MASK) {
            throw new IndexOutOfBoundsException("Object Index could not be created because it exceeded the capacity of the current prefix. Ask for a new prefix.");
        }
        _seed++;
        final long nextIndex = _seed;
        long objectKey = _prefix + nextIndex;
        if (_space != null) {
            _space.notifyUpdate(_index);
        }
        if (objectKey >= Constants.END_OF_TIME) {
            throw new IndexOutOfBoundsException("Object Index exceeds the maximum JavaScript number capacity. (2^" + Constants.LONG_SIZE + ")");
        }
        return objectKey;
    }

    @Override
    public final long index() {
        return _index;
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
    public final byte chunkType() {
        return ChunkType.GEN_CHUNK;
    }

}
