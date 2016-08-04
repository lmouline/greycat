package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.chunk.ChunkListener;
import org.mwg.chunk.GenChunk;
import org.mwg.utility.Base64;
import org.mwg.chunk.ChunkType;
import org.mwg.struct.Buffer;

/**
 * @ignore ts
 */
public class OffHeapGenChunk implements GenChunk, OffHeapChunk {

    private final ChunkListener listener;
    private final long rootPtr;

    /**
     * Global Chunk indexes
     */
    private static final int INDEX_WORLD = OffHeapConstants.OFFHEAP_CHUNK_INDEX_WORLD;
    private static final int INDEX_TIME = OffHeapConstants.OFFHEAP_CHUNK_INDEX_TIME;
    private static final int INDEX_ID = OffHeapConstants.OFFHEAP_CHUNK_INDEX_ID;
    private static final int INDEX_TYPE = OffHeapConstants.OFFHEAP_CHUNK_INDEX_TYPE;
    private static final int INDEX_FLAGS = OffHeapConstants.OFFHEAP_CHUNK_INDEX_FLAGS;
    private static final int INDEX_MARKS = OffHeapConstants.OFFHEAP_CHUNK_INDEX_MARKS;

    private static final int INDEX_CURRENT = 6;

    private static final int CHUNK_SIZE = 7;

    private long index;

    public OffHeapGenChunk(ChunkListener p_listener, long previousAddr, Buffer initialPayload) {
        //listener
        this.listener = p_listener;
        //init
        if (previousAddr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            rootPtr = previousAddr;
        } else if (initialPayload != null && initialPayload.length() > 0) {
            rootPtr = OffHeapLongArray.allocate(CHUNK_SIZE);
            load(initialPayload);
        } else {
            rootPtr = OffHeapLongArray.allocate(CHUNK_SIZE);
            OffHeapLongArray.set(rootPtr, INDEX_CURRENT, 0);
            OffHeapLongArray.set(rootPtr, INDEX_FLAGS, 0);
            OffHeapLongArray.set(rootPtr, INDEX_MARKS, 0);
        }
    }

    private void load(Buffer payload) {
        if (payload != null) {
            OffHeapLongArray.set(rootPtr, INDEX_CURRENT, Base64.decodeToLongWithBounds(payload, 0, payload.length()));
        }
    }

    @Override
    public void merge(Buffer buffer) {
        long previous;
        long toInsert = Base64.decodeToLongWithBounds(buffer, 0, buffer.length());
        do {
            previous = OffHeapLongArray.get(rootPtr, INDEX_CURRENT);
        } while (!OffHeapLongArray.compareAndSwap(rootPtr, INDEX_CURRENT, previous, toInsert));
        if (toInsert != previous) {
            internal_set_dirty();
        }
    }

    @Override
    public long index() {
        return index;
    }

    public static void free(long addr) {
        OffHeapLongArray.free(addr);
    }

    @Override
    public final long world() {
        return OffHeapLongArray.get(rootPtr, INDEX_WORLD);
    }

    @Override
    public final long time() {
        return OffHeapLongArray.get(rootPtr, INDEX_TIME);
    }

    @Override
    public final long id() {
        return OffHeapLongArray.get(rootPtr, INDEX_ID);
    }

    @Override
    public final long marks() {
        return OffHeapLongArray.get(rootPtr, INDEX_MARKS);
    }

    @Override
    public final byte chunkType() {
        return ChunkType.GEN_CHUNK;
    }

    @Override
    public long addr() {
        return rootPtr;
    }

    @Override
    public void setIndex(long index) {
        this.index = index;
    }

    @Override
    public final long flags() {
        return OffHeapLongArray.get(rootPtr, INDEX_FLAGS);
    }

    @Override
    public long newKey() {

        long previous;
        long after;
        do {
            previous = OffHeapLongArray.get(rootPtr, INDEX_CURRENT);
            after = previous + 1;
        } while (!OffHeapLongArray.compareAndSwap(rootPtr, INDEX_CURRENT, previous, after));

        internal_set_dirty();
        if (after == Constants.KEY_PREFIX_MASK) {
            throw new IndexOutOfBoundsException("Object Index could not be created because it exceeded the capacity of the current prefix. Ask for a new prefix.");
        }
        //moves the prefix 53-size(short) times to the left;
        long prefix = OffHeapLongArray.get(rootPtr, INDEX_ID) << Constants.LONG_SIZE - Constants.PREFIX_SIZE;
        long objectKey = prefix + after;
        if (objectKey >= Constants.END_OF_TIME) {
            throw new IndexOutOfBoundsException("Object Index exceeds the maximum JavaScript number capacity. (2^" + Constants.LONG_SIZE + ")");
        }
        return objectKey;
    }

    @Override
    public void save(Buffer buffer) {
        Base64.encodeLongToBuffer(OffHeapLongArray.get(rootPtr, INDEX_CURRENT), buffer);
    }

    private void internal_set_dirty() {
        if (listener != null) {
            if ((OffHeapLongArray.get(rootPtr, INDEX_FLAGS) & Constants.DIRTY_BIT) != Constants.DIRTY_BIT) {
                listener.declareDirty(this);
            }
        }
    }

}
