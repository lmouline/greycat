package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;
import org.mwg.utility.HashHelper;

public class OffHeapENode implements ENode {
    private final OffHeapEGraph egraph;
    private final OffHeapStateChunk chunk;
    private final Graph _graph;

    private long addr = OffHeapConstants.OFFHEAP_NULL_PTR;

    private static final int ID = 0;
    private static final int DIRTY = 1;
    private static final int SIZE = 2;
    private static final int CAPACITY = 3;
    private static final int SUBHASH = 4;

    private static final int OFFSET = 5;
    private static final int ELEM_SIZE = 3;

    OffHeapENode(final OffHeapStateChunk p_chunk, final OffHeapEGraph p_egraph, final Graph p_graph, final long p_id, final long originAddr) {
        chunk = p_chunk;
        egraph = p_egraph;
        _graph = p_graph;

        long capacity = 0;
        if (originAddr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            capacity = OffHeapLongArray.get(originAddr, CAPACITY);
        }
        addr = allocate(originAddr, capacity);
        OffHeapLongArray.set(addr, ID, p_id);
    }

    private long allocate(final long addr, final long newCapacity) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            //nothing before, initial allocation...
            final long new_addr = OffHeapLongArray.allocate(OFFSET + (newCapacity * ELEM_SIZE));
            OffHeapLongArray.set(new_addr, CAPACITY, newCapacity);
            OffHeapLongArray.set(new_addr, DIRTY, 0);
            OffHeapLongArray.set(new_addr, SIZE, 0);
            if (newCapacity > Constants.MAP_INITIAL_CAPACITY) {
                OffHeapLongArray.set(new_addr, SUBHASH, OffHeapLongArray.allocate(newCapacity * 3));
            } else {
                OffHeapLongArray.set(new_addr, SUBHASH, OffHeapConstants.OFFHEAP_NULL_PTR);
            }
            return new_addr;
        } else {
            //reallocation or overallocation
            final long new_addr = OffHeapLongArray.reallocate(addr, OFFSET + (newCapacity * ELEM_SIZE));
            OffHeapLongArray.set(new_addr, CAPACITY, newCapacity);
            long subHash_ptr = OffHeapLongArray.get(new_addr, SUBHASH);
            if (subHash_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                subHash_ptr = OffHeapLongArray.allocate(newCapacity * 3);
            } else {
                subHash_ptr = OffHeapLongArray.reallocate(subHash_ptr, newCapacity * 3);
                OffHeapLongArray.reset(subHash_ptr, newCapacity * 3);
            }
            OffHeapLongArray.set(new_addr, SUBHASH, subHash_ptr);
            //reHash
            final long size = OffHeapLongArray.get(new_addr, SIZE);
            final long hash_capacity = newCapacity * 2;
            for (long i = 0; i < size; i++) {
                long keyHash = HashHelper.longHash(key(new_addr, i), hash_capacity);
                setNext(subHash_ptr, i, hash(subHash_ptr, newCapacity, keyHash));
                setHash(subHash_ptr, newCapacity, keyHash, i);
            }
            return new_addr;
        }
    }

    private void declareDirty() {
        long dirty = OffHeapLongArray.get(addr, DIRTY);
        if (dirty == 0) {
            OffHeapLongArray.set(addr, DIRTY, 1);
            egraph.declareDirty();
        }
    }

    private static long value(final long addr, final long index) {
        return OffHeapLongArray.get(addr, OFFSET + (index * ELEM_SIZE) + 2);
    }

    private static void setValue(final long addr, final long index, final long insertValue) {
        OffHeapLongArray.set(addr, OFFSET + (index * ELEM_SIZE) + 2, insertValue);
    }

    private static long key(final long addr, final long index) {
        return OffHeapLongArray.get(addr, OFFSET + (index * ELEM_SIZE));
    }

    private static void setKey(final long addr, final long index, final long insertKey) {
        OffHeapLongArray.set(addr, OFFSET + (index * ELEM_SIZE), insertKey);
    }


    private static long next(final long hashAddr, final long index) {
        return OffHeapLongArray.get(hashAddr, index);
    }

    private static void setNext(final long hashAddr, final long index, final long insertNext) {
        OffHeapLongArray.set(hashAddr, index, insertNext);
    }

    private static long hash(final long hashAddr, final long capacity, final long index) {
        return OffHeapLongArray.get(hashAddr, capacity + index);
    }

    private static void setHash(final long hashAddr, final long capacity, final long index, final long insertHash) {
        OffHeapLongArray.set(hashAddr, capacity + index, insertHash);
    }
    private static byte type(final long addr, final long index) {
        return (byte) OffHeapLongArray.get(addr, OFFSET + (index * ELEM_SIZE) + 1);
    }

    private static void setType(final long addr, final long index, final byte insertType) {
        OffHeapLongArray.set(addr, OFFSET + (index * ELEM_SIZE) + 1, insertType);
    }

    @Override
    public ENode set(String name, byte type, Object value) {
//        internal_set(_graph.resolver().stringToHash(name, true), type, value, true, false);
        return this;
    }

    @Override
    public ENode setAt(long key, byte type, Object value) {
//        internal_set(key, type, value, true, false);
        return this;
    }

    @Override
    public Object get(String name) {
//        return internal_get(_graph.resolver().stringToHash(name, false));
        return null;
    }

    @Override
    public Object getAt(long key) {
//        return internal_get(key);
        return null;
    }

    @Override
    public Object getOrCreate(String key, byte type) {
        return null;
    }

    @Override
    public Object getOrCreateAt(long key, byte type) {
        return null;
    }

    @Override
    public void drop() {
        egraph.drop(this);
    }

    @Override
    public EGraph graph() {
        return egraph;
    }


    public long getAddr() {
        return this.addr;
    }

    static void rebase(long addr, OffHeapEGraph egraph) {
        long size = OffHeapLongArray.get(addr, SIZE);
        for (int i = 0; i < size; i++) {
            int type = type(addr, i);
            switch (type) {
                case Type.ERELATION:
                    final long previousERel_ptr = value(addr, i);
                    OffHeapERelation.rebase(previousERel_ptr);
                    break;
                case Type.ENODE:
                    final long previousENode_ptr = value(addr, i);
                    final long previousENodeId = OffHeapENode.getId(previousENode_ptr);
                    setValue(addr, i, egraph.getNodeAddrAt(previousENodeId));
                    break;
            }
        }
    }

    static void setId(long addr, long id) {
        OffHeapLongArray.set(addr, ID, id);
    }

    static long getId(long addr) {
        return OffHeapLongArray.get(addr, ID);
    }

    static void free(long addr) {
        // TODO
//        long subHash = OffHeapLongArray.get(addr, SUBHASH);
//        OffHeapLongArray.free(subHash);
//        OffHeapLongArray.free(addr);
    }

    static String toString(long addr) {
        // TODO
        return "";
    }
}
