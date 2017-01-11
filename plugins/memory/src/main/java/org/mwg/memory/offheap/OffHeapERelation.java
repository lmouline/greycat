package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.ENode;
import org.mwg.struct.ERelation;

public class OffHeapERelation implements ERelation {
    private final OffHeapStateChunk parent;
    private final OffHeapEGraph eGraph;
    private final Graph graph;

    private static final int SIZE = 0;
    private static final int CAPACITY = 1;
    private static final int HEADER_SIZE = 2;

    private long addr = OffHeapConstants.OFFHEAP_NULL_PTR;

    public OffHeapERelation(OffHeapStateChunk parent, OffHeapEGraph eGraph, Graph graph, long originAddr) {
        this.parent = parent;
        this.eGraph = eGraph;
        this.graph = graph;

        allocate(originAddr, Constants.MAP_INITIAL_CAPACITY);
    }

    final void allocate(long originAddr, long newCapacity) {
        final long closePowerOfTwo = (long) Math.pow(2, Math.ceil(Math.log(newCapacity) / Math.log(2)));

        if (originAddr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            if (originAddr == addr) {
                addr = OffHeapLongArray.reallocate(addr, closePowerOfTwo);
            } else {
                long originCapacity = OffHeapLongArray.get(originAddr, CAPACITY);
                addr = OffHeapLongArray.cloneArray(originAddr, HEADER_SIZE + originCapacity);
                addr = OffHeapLongArray.reallocate(addr, closePowerOfTwo);
            }

        } else {
            // allocate memory
            addr = OffHeapLongArray.allocate(HEADER_SIZE + closePowerOfTwo);
            OffHeapLongArray.set(addr, SIZE, 0);
            OffHeapLongArray.set(addr, CAPACITY, closePowerOfTwo);
        }
    }

    @Override
    public ENode[] nodes() {
        int size = (int) OffHeapLongArray.get(addr, SIZE);
        ENode[] nodes = new ENode[size];
        for (int i = 0; i < size; i++) {
            long nodeAddr = nodeAddrAt(addr, i);
            long nodeId = OffHeapENode.getId(nodeAddr);
            OffHeapENode eNode = new OffHeapENode(parent, eGraph, graph, nodeId, nodeAddr);
            nodes[i] = eNode;
        }
        return nodes;
    }

    @Override
    public ENode node(int index) {
        long nodeAddr = nodeAddrAt(addr, index);
        long nodeId = OffHeapENode.getId(nodeAddr);
        return new OffHeapENode(parent, eGraph, graph, nodeId, nodeAddr);
    }

    @Override
    public int size() {
        return (int) OffHeapLongArray.get(addr, SIZE);
    }

    @Override
    public ERelation add(ENode eNode) {
        long capacity = OffHeapLongArray.get(addr, CAPACITY);
        long size = OffHeapLongArray.get(addr, SIZE);

        if (capacity == size) {
            if (capacity == 0) {
                allocate(addr, Constants.MAP_INITIAL_CAPACITY);
            } else {
                allocate(addr, capacity * 2);
            }
        }
        setNodeAddrAt(addr, size, ((OffHeapENode) eNode).getAddr());
        OffHeapLongArray.set(addr, SIZE, size + 1);
        if (parent != null) {
            parent.declareDirty();
        }
        return this;
    }

    @Override
    public ERelation addAll(ENode[] eNodes) {
        long size = OffHeapLongArray.get(addr, SIZE);
        allocate(addr, HEADER_SIZE + eNodes.length + size);
        for (int i = 0; i < eNodes.length; i++) {
            OffHeapENode eNode = (OffHeapENode) eNodes[i];
            setNodeAddrAt(addr, size + i, eNode.getAddr());
        }
        if (parent != null) {
            parent.declareDirty();
        }
        return this;
    }

    @Override
    public ERelation clear() {
        long size = OffHeapLongArray.get(addr, SIZE);
        OffHeapLongArray.set(addr, SIZE, 0);
        for (long i = 0; i < size; i++) {
            setNodeAddrAt(addr, i, OffHeapConstants.OFFHEAP_NULL_PTR);
        }
        if (parent != null) {
            parent.declareDirty();
        }
        return this;
    }

    static void rebase(long addr, long newEGraphAddr) {
        long size = OffHeapLongArray.get(addr, SIZE);

        for (int i = 0; i < size; i++) {
            long previousNodeAddr = nodeAddrAt(addr, i);
            long previousNodeId = OffHeapENode.getId(previousNodeAddr);

            long newNodeAddr = OffHeapEGraph.nodeAddrAt(newEGraphAddr, previousNodeId);
            setNodeAddrAt(addr, i, newNodeAddr);
        }
    }

    static void free(long addr) {
        OffHeapLongArray.free(addr);
    }

    static void save(long addr, Buffer buffer) {
        // TODO
    }

    static long nodeAddrAt(long addr, long index) {
        return OffHeapLongArray.get(addr, HEADER_SIZE + index);
    }

    static void setNodeAddrAt(long addr, long index, long value) {
        OffHeapLongArray.set(addr, HEADER_SIZE + index, value);
    }

    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        long size = OffHeapLongArray.get(addr, SIZE);
        for (long i = 0; i < size; i++) {
            if (i != 0) {
                buffer.append(",");
            }
            long nodeAddr = nodeAddrAt(addr, i);
            long nodeId = OffHeapENode.getId(nodeAddr);
            buffer.append(nodeId);
        }
        buffer.append("]");
        return buffer.toString();
    }

    public long getAddr() {
        return addr;
    }


}
