package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;
import org.mwg.utility.Base64;

public class OffHeapEGraph implements EGraph {
    private final Graph _graph;
    private final long index;
    private final OffHeapContainer parent;

    private static final int DIRTY = 0;
    private static final int NODES_CAPACITY = 1;
    private static final int NODES_INDEX = 2;
    private static final int OFFSET = 3;

    OffHeapEGraph(final OffHeapContainer p_container, final long p_index, final Graph p_graph) {
        parent = p_container;
        index = p_index;
        _graph = p_graph;

        preAllocate();
    }

    private void preAllocate() {
        long originAddr = parent.addrByIndex(index);
        if (originAddr != OffHeapConstants.NULL_PTR) {

            long nodesCapacity = OffHeapLongArray.get(originAddr, NODES_CAPACITY);
            long nodesIndex = OffHeapLongArray.get(originAddr, NODES_INDEX);

            long newAddr = OffHeapLongArray.allocate(OFFSET + nodesCapacity);
            parent.setAddrByIndex(index, newAddr);
            OffHeapLongArray.set(newAddr, NODES_INDEX, nodesIndex);
            OffHeapLongArray.set(newAddr, NODES_CAPACITY, nodesCapacity);

            //pass #1: copy nodes
            for (int i = 0; i < nodesIndex; i++) {
                OffHeapENode eNode = new OffHeapENode(this, _graph, i, nodeAddrAt(originAddr, i));
                setNodeAddrAt(newAddr, i, eNode.getAddr());
            }
            //pass #2: rebase all links
            for (int i = 0; i < nodesIndex; i++) {
                OffHeapENode.rebase(nodeAddrAt(newAddr, i), newAddr);
            }
        } else {
            long newAddr = OffHeapLongArray.allocate(OFFSET);
            parent.setAddrByIndex(index, newAddr);
            OffHeapLongArray.set(newAddr, DIRTY, 0);
            OffHeapLongArray.set(newAddr, NODES_INDEX, 0);
            OffHeapLongArray.set(newAddr, NODES_CAPACITY, 0);
        }
    }

    public final long load(final Buffer buffer, final long offset, final long max) {
        long cursor = offset;
        byte current = buffer.read(cursor);
        boolean isFirst = true;
        int insertIndex = 0;
        while (cursor < max && current != Constants.CHUNK_SEP) {
            if (current == Constants.CHUNK_ENODE_SEP) {
                if (isFirst) {
                    allocate(Base64.decodeToIntWithBounds(buffer, offset, cursor));
                    isFirst = false;
                }
                cursor++;
                OffHeapENode eNode = nodeByIndex(insertIndex, true);
                cursor = eNode.load(buffer, cursor);
                insertIndex++;
            } else {
                cursor++;
            }
            current = buffer.read(cursor);
        }
        return cursor;
    }

    final void allocate(int newCapacity) {
        long addr = parent.addrByIndex(index);
        final int closePowerOfTwo = (int) Math.pow(2, Math.ceil(Math.log(newCapacity) / Math.log(2)));
        long nodesCapacity = OffHeapLongArray.get(addr, NODES_CAPACITY);
        if (closePowerOfTwo > nodesCapacity) {
            long newAddr = OffHeapLongArray.reallocate(addr, OFFSET + closePowerOfTwo);
            OffHeapLongArray.fillByte(newAddr, OFFSET + nodesCapacity, OFFSET + closePowerOfTwo, (byte) OffHeapConstants.NULL_PTR);
            parent.setAddrByIndex(index, newAddr);
            OffHeapLongArray.set(newAddr, NODES_CAPACITY, closePowerOfTwo);
        }
    }

    static long nodeAddrAt(long eGraphAddr, long nodeIndex) {
        return OffHeapLongArray.get(eGraphAddr, OFFSET + nodeIndex);
    }

    static void setNodeAddrAt(long eGraphAddr, long nodeIndex, long nodeAddr) {
        OffHeapLongArray.set(eGraphAddr, OFFSET + nodeIndex, nodeAddr);
    }

    @Override
    public final ENode root() {
        long addr = parent.addrByIndex(index);
        if (OffHeapLongArray.get(addr, NODES_INDEX) > 0) {
            return new OffHeapENode(this, _graph, 0, nodeAddrAt(addr, 0));
        }
        return null;
    }

    @Override
    public final ENode newNode() {
        long addr = parent.addrByIndex(index);
        long nodesIndex = OffHeapLongArray.get(addr, NODES_INDEX);
        long nodesCapacity = OffHeapLongArray.get(addr, NODES_CAPACITY);

        if (nodesIndex == nodesCapacity) {
            long newCapacity = nodesCapacity * 2;
            if (newCapacity == 0) {
                newCapacity = Constants.MAP_INITIAL_CAPACITY;
            }
            // reallocate
            addr = OffHeapLongArray.reallocate(addr, OFFSET + newCapacity);
            parent.setAddrByIndex(index, addr);
            OffHeapLongArray.set(addr, NODES_CAPACITY, newCapacity);
        }

        OffHeapENode newNode = new OffHeapENode(this, _graph, nodesIndex, OffHeapConstants.NULL_PTR);
        setNodeAddrAt(addr, nodesIndex, newNode.getAddr());
        OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex + 1);

        return newNode;
    }

    @Override
    public final EGraph setRoot(ENode eNode) {
        long addr = parent.addrByIndex(index);
        final OffHeapENode casted = (OffHeapENode) eNode;
        final long previousId = OffHeapENode.getId(casted.getAddr());
        if (previousId != 0) {
            long previousRootAddr = nodeAddrAt(addr, 0);
            setNodeAddrAt(addr, previousId, previousRootAddr);
            OffHeapENode.setId(previousRootAddr, previousId);
            setNodeAddrAt(addr, 0, casted.getAddr());
            OffHeapENode.setId(casted.getAddr(), 0);
        }
        return this;
    }

    @Override
    public final EGraph drop(ENode eNode) {
        long addr = parent.addrByIndex(index);
        OffHeapENode casted = (OffHeapENode) eNode;
        long previousId = OffHeapENode.getId(casted.getAddr());
        long nodesIndex = OffHeapLongArray.get(addr, NODES_INDEX);
        if (previousId == nodesIndex - 1) {
            //free
            OffHeapENode.free(casted.getAddr());
            setNodeAddrAt(addr, previousId, OffHeapConstants.NULL_PTR);
            OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex - 1);
        } else {
            setNodeAddrAt(addr, previousId, nodeAddrAt(addr, nodesIndex - 1));
            OffHeapENode.setId(nodeAddrAt(addr, previousId), previousId);
            OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex - 1);
        }
        return this;
    }

    @Override
    public final int size() {
        long addr = parent.addrByIndex(index);
        return (int) OffHeapLongArray.get(addr, NODES_INDEX);
    }

    @Override
    public final void free() {
        long addr = parent.addrByIndex(index);
        freeByAddr(addr);
        parent.setAddrByIndex(index, OffHeapConstants.NULL_PTR);
    }


    static void freeByAddr(long addr) {
        if (addr != OffHeapConstants.NULL_PTR) {
            long nodesIndex = OffHeapLongArray.get(addr, NODES_INDEX);
            for (long i = 0; i < nodesIndex; i++) {
                OffHeapENode.free(nodeAddrAt(addr, i));
            }
            OffHeapLongArray.free(addr);
        }
    }

    @Override
    public Graph graph() {
        return _graph;
    }

    final void declareDirty() {
        long addr = parent.addrByIndex(index);
        long dirty = OffHeapLongArray.get(addr, DIRTY);
        if (dirty == 0) {
            OffHeapLongArray.set(addr, DIRTY, 1);
            if (parent != null) {
                parent.declareDirty();
            }
        }
    }

    @Override
    public final String toString() {
        long addr = parent.addrByIndex(index);
        final StringBuilder builder = new StringBuilder();
        builder.append("{\"nodes\":[");
        long nodesIndex = OffHeapLongArray.get(addr, NODES_INDEX);
        for (int i = 0; i < nodesIndex; i++) {
            if (i != 0) {
                builder.append(",");
            }
            long nodeAddr = nodeAddrAt(addr, i);
            long nodeId = OffHeapENode.getId(nodeAddr);
            OffHeapENode enode = new OffHeapENode(this, _graph, nodeId, nodeAddr);
            builder.append(enode.toString());
        }
        builder.append("]}");
        return builder.toString();
    }

    final OffHeapENode nodeByIndex(final long nodeIndex, final boolean createIfAbsent) {
        final long addr = parent.addrByIndex(index);
        final long nodesCapacity = OffHeapLongArray.get(addr, NODES_CAPACITY);
        final long nodesIndex = OffHeapLongArray.get(addr, NODES_INDEX);
        if (nodeIndex < nodesCapacity) {
            if (nodeIndex >= nodesIndex) {
                OffHeapLongArray.set(addr, NODES_INDEX, nodeIndex + 1);
            }
            OffHeapENode elem = null;
            long elemAddr = nodeAddrAt(addr, nodeIndex);
            if (elemAddr != OffHeapConstants.NULL_PTR) {
                elem = new OffHeapENode(this, _graph, nodeIndex, elemAddr);
            }
            if (elemAddr == OffHeapConstants.NULL_PTR && createIfAbsent) {
                elem = new OffHeapENode(this, _graph, nodeIndex, OffHeapConstants.NULL_PTR);
                setNodeAddrAt(addr, nodeIndex, elem.getAddr());
            }
            return elem;
        } else {
            throw new RuntimeException("bad API usage");
        }
    }

    public static long clone(long addr) {
        if (addr == OffHeapConstants.NULL_PTR) {
            return OffHeapConstants.NULL_PTR;
        }
        long capacity = OffHeapLongArray.get(addr, NODES_CAPACITY);

        return OffHeapLongArray.cloneArray(addr, OFFSET + capacity);
    }

    final long getAddr() {
        return parent.addrByIndex(index);
    }
}
