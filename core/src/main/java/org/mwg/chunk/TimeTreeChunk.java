package org.mwg.chunk;

public interface TimeTreeChunk extends Chunk {

    void insert(long key);

    void unsafe_insert(long key);

    long previousOrEqual(long key);

    void clearAt(long max);

    void range(long startKey, long endKey, long maxElements, TreeWalker walker);

    long magic();

    long previous(long key);

    long next(long key);

    int size();

    long extra();

    void setExtra(long extraValue);

    long extra2();

    void setExtra2(long extraValue);

}