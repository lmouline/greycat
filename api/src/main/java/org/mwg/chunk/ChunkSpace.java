package org.mwg.chunk;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.struct.Buffer;

public interface ChunkSpace {

    /**
     * Create Chunk, out of the space, not marked, used asVar a factory
     */
    Chunk createAndMark(byte type, long world, long time, long id);

    /**
     * Get and mark chunk for the association of keys
     */
    Chunk getAndMark(byte type, long world, long time, long id);

    /**
     * Get and mark chunk for the association of keys, if not present in cache, load it from configured storage
     */
    void getOrLoadAndMark(byte type, long world, long time, long id, Callback<Chunk> callback);

    /**
     * Get chunk for the associated index
     */
    Chunk get(long index);
    
    void unmark(final long index);

    long mark(final long index);

    void free(final Chunk chunk);

    void notifyUpdate(long index);

    /**
     * Get current working graph
     *
     * @return current graph
     */
    Graph graph();

    void save();

    void clear();

    void freeAll();

    long size();

    long available();

}
