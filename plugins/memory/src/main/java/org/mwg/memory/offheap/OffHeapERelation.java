package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.ENode;
import org.mwg.struct.ERelation;

public class OffHeapERelation implements ERelation {
    private final OffHeapEGraph eGraph;
    private final Graph graph;
    private final long index;
    private final OffHeapContainer container;

    private static final int SIZE = 0;
    private static final int CAPACITY = 1;
    private static final int HEADER_SIZE = 2;

    public OffHeapERelation(OffHeapContainer container, OffHeapEGraph eGraph, Graph graph, long p_index) {
        this.eGraph = eGraph;
        this.graph = graph;
        this.index = p_index;
        this.container = container;

        allocate(Constants.MAP_INITIAL_CAPACITY);
    }

    final void allocate(long newCapacity) {
        long addr = OffHeapConstants.NULL_PTR;
        if (index != -1) {
            addr = container.addrByIndex(index);
        }

        final long closePowerOfTwo = (long) Math.pow(2, Math.ceil(Math.log(newCapacity) / Math.log(2)));
        if (addr != OffHeapConstants.NULL_PTR) {
            addr = OffHeapLongArray.reallocate(addr, closePowerOfTwo);
        } else {
            // allocate memory
            addr = OffHeapLongArray.allocate(HEADER_SIZE + closePowerOfTwo);
            OffHeapLongArray.set(addr, SIZE, 0);
            OffHeapLongArray.set(addr, CAPACITY, closePowerOfTwo);
        }

        if (index != -1) {
            container.setAddrByIndex(index, addr);
        }
    }

    @Override
    public ENode[] nodes() {
        long addr = container.addrByIndex(index);
        int size = (int) OffHeapLongArray.get(addr, SIZE);
        ENode[] nodes = new ENode[size];
        for (int i = 0; i < size; i++) {
            long nodeAddr = nodeAddrAt(addr, i);
            long nodeId = OffHeapENode.getId(nodeAddr);
            OffHeapENode eNode = new OffHeapENode(eGraph, graph, nodeId, nodeAddr);
            nodes[i] = eNode;
        }
        return nodes;
    }

    @Override
    public ENode node(int index) {
        long addr = container.addrByIndex(index);
        long nodeAddr = nodeAddrAt(addr, index);
        long nodeId = OffHeapENode.getId(nodeAddr);
        return new OffHeapENode(eGraph, graph, nodeId, nodeAddr);
    }

    @Override
    public int size() {
        long addr = container.addrByIndex(index);
        return (int) OffHeapLongArray.get(addr, SIZE);
    }

    @Override
    public ERelation add(ENode eNode) {
        long addr = container.addrByIndex(index);
        long capacity = OffHeapLongArray.get(addr, CAPACITY);
        long size = OffHeapLongArray.get(addr, SIZE);

        if (capacity == size) {
            if (capacity == 0) {
                allocate(Constants.MAP_INITIAL_CAPACITY);
            } else {
                allocate(capacity * 2);
            }
        }
        setNodeAddrAt(addr, size, ((OffHeapENode) eNode).getAddr());
        OffHeapLongArray.set(addr, SIZE, size + 1);
        eGraph.declareDirty();
        return this;
    }

    @Override
    public ERelation addAll(ENode[] eNodes) {
        long addr = container.addrByIndex(index);
        long size = OffHeapLongArray.get(addr, SIZE);
        allocate(HEADER_SIZE + eNodes.length + size);
        for (int i = 0; i < eNodes.length; i++) {
            long currentAddr = container.addrByIndex(index);
            OffHeapENode eNode = (OffHeapENode) eNodes[i];
            setNodeAddrAt(currentAddr, size + i, eNode.getAddr());
        }
        eGraph.declareDirty();
        return this;
    }

    @Override
    public ERelation clear() {
        long addr = container.addrByIndex(index);
        long size = OffHeapLongArray.get(addr, SIZE);
        OffHeapLongArray.set(addr, SIZE, 0);
        for (long i = 0; i < size; i++) {
            long currentAddr = container.addrByIndex(index);
            setNodeAddrAt(currentAddr, i, OffHeapConstants.NULL_PTR);
        }
        eGraph.declareDirty();
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

    static long nodeAddrAt(long addr, long index) {
        return OffHeapLongArray.get(addr, HEADER_SIZE + index);
    }

    static void setNodeAddrAt(long addr, long index, long value) {
        OffHeapLongArray.set(addr, HEADER_SIZE + index, value);
    }

    @Override
    public final String toString() {
        long addr = container.addrByIndex(index);
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

    public long index() {
        return this.index;
    }


}