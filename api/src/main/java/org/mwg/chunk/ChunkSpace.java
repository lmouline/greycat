package org.mwg.chunk;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.struct.Buffer;

public interface ChunkSpace {

    /**
     * Create Chunk, out of the space, not marked, used asVar a factory
     */
    Chunk create(byte type, long world, long time, long id, Buffer initialPayload, Chunk origin);

    /**
     * Get and mark chunk for the association of keys
     */
    Chunk getAndMark(byte type, long world, long time, long id);

    /**
     * Get and mark chunk for the associated index
     */
    Chunk getByIndex(long index);

    /**
     * Insert the chunk into the space and mark it before asVar used
     */
    Chunk putAndMark(byte type, long world, long time, long id, Chunk elem);

    /**
     * Get and mark chunk for the association of keys, if not present in cache, load it from configured storage
     */
    void getOrLoadAndMark(byte type, long world, long time, long id, Callback<Chunk> callback);

    /**
     * UnMark chunk for the association of keys
     */
    void unmarkByIndex(final long index);

    void markByIndex(final long index);

    /**
     * UnMark chunk
     */
    void unmarkChunk(final Chunk chunk);

    void freeChunk(final Chunk chunk);

    /**
     * Declare the chunk asVar dirty
     */
    void declareDirty(Chunk elem);

    /**
     * Declare the chunk asVar clean
     */
    void declareClean(Chunk elem);

    /**
     * Get current working graph
     *
     * @return current graph
     */
    Graph graph();

    void clear();

    void free();

    long size();

    long available();

    ChunkIterator detachDirties();

}
