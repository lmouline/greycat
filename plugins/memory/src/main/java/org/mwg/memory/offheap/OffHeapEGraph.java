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
    private final OffHeapStateChunk parent;

    private static final int DIRTY = 0;
    private static final int NODES_CAPACITY = 1;
    private static final int NODES_INDEX = 2;
    private static final int NODES = 3;

    private long addr = OffHeapConstants.OFFHEAP_NULL_PTR;

    OffHeapEGraph(final OffHeapStateChunk p_parent, final long originAddr, final Graph p_graph) {
        parent = p_parent;
        _graph = p_graph;
        if (originAddr != OffHeapConstants.OFFHEAP_NULL_PTR) {

            long nodesCapacity = OffHeapLongArray.get(originAddr, NODES_CAPACITY);
            long nodesIndex = OffHeapLongArray.get(originAddr, NODES_INDEX);

            addr = OffHeapLongArray.allocate(NODES + nodesCapacity);
            OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex);
            OffHeapLongArray.set(addr, NODES_CAPACITY, nodesCapacity);

            //pass #1: copy nodes
            for (int i = 0; i < nodesIndex; i++) {
                OffHeapENode eNode = new OffHeapENode(parent, this, _graph, i, nodeAddrAt(originAddr, i));
                setNodeAddrAt(addr, i, eNode.getAddr());
            }
            //pass #2: rebase all links
            for (int i = 0; i < nodesIndex; i++) {
                OffHeapENode.rebase(nodeAddrAt(addr, i), addr);
            }
        } else {
            addr = OffHeapLongArray.allocate(NODES);
            OffHeapLongArray.set(addr, DIRTY, 0);
            OffHeapLongArray.set(addr, NODES_INDEX, 0);
            OffHeapLongArray.set(addr, NODES_CAPACITY, 0);
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
                cursor = eNode.load(buffer, cursor, parent);
                insertIndex++;
            } else {
                cursor++;
            }
            current = buffer.read(cursor);
        }
        return cursor;
    }

    final void allocate(int newCapacity) {
        final int closePowerOfTwo = (int) Math.pow(2, Math.ceil(Math.log(newCapacity) / Math.log(2)));
        long nodesCapacity = OffHeapLongArray.get(addr, NODES_CAPACITY);

        if (closePowerOfTwo > nodesCapacity) {
            addr = OffHeapLongArray.reallocate(addr, closePowerOfTwo);
            OffHeapLongArray.set(addr, NODES_CAPACITY, closePowerOfTwo);
        }
    }

    long getAddr() {
        return this.addr;
    }

    static long nodeAddrAt(long eGraphAddr, long nodeIndex) {
        return OffHeapLongArray.get(eGraphAddr, NODES + nodeIndex);
    }

    static void setNodeAddrAt(long eGraphAddr, long nodeIndex, long nodeAddr) {
        OffHeapLongArray.set(eGraphAddr, NODES + nodeIndex, nodeAddr);
    }

    @Override
    public ENode root() {
        if (OffHeapLongArray.get(addr, NODES_INDEX) > 0) {
            return new OffHeapENode(parent, this, _graph, 0, nodeAddrAt(addr, 0));
        }
        return null;
    }

    @Override
    public ENode newNode() {
        long nodesIndex = OffHeapLongArray.get(addr, NODES_INDEX);
        long nodesCapacity = OffHeapLongArray.get(addr, NODES_CAPACITY);

        if (nodesIndex == nodesCapacity) {
            long newCapacity = nodesCapacity * 2;
            if (newCapacity == 0) {
                newCapacity = Constants.MAP_INITIAL_CAPACITY;
            }

            // reallocate
            addr = OffHeapLongArray.reallocate(addr, NODES + newCapacity);
            OffHeapLongArray.set(addr, NODES_CAPACITY, newCapacity);
        }

        OffHeapENode newNode = new OffHeapENode(parent, this, _graph, nodesIndex, OffHeapConstants.OFFHEAP_NULL_PTR);
        setNodeAddrAt(addr, nodesIndex, newNode.getAddr());
        OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex + 1);

        return newNode;
    }

    @Override
    public EGraph setRoot(ENode eNode) {
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
    public EGraph drop(ENode eNode) {
        OffHeapENode casted = (OffHeapENode) eNode;
        long previousId = OffHeapENode.getId(casted.getAddr());
        long nodesIndex = OffHeapLongArray.get(addr, NODES_INDEX);
        if (previousId == nodesIndex - 1) {
            //free
            OffHeapENode.free(casted.getAddr());
            setNodeAddrAt(addr, previousId, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex - 1);
        } else {
            setNodeAddrAt(addr, previousId, nodeAddrAt(addr, nodesIndex - 1));
            OffHeapENode.setId(nodeAddrAt(addr, previousId), previousId);
            OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex - 1);
        }
        return this;
    }

    @Override
    public int size() {
        return (int) OffHeapLongArray.get(addr, NODES_INDEX);
    }

    @Override
    public void free() {
        // free all nodes
        long nodesCapacity = OffHeapLongArray.get(addr, NODES_CAPACITY);
        for (long i = 0; i < nodesCapacity; i++) {

        }
    }

    final void declareDirty() {
        long dirty = OffHeapLongArray.get(addr, DIRTY);
        if (dirty == 0) {
            OffHeapLongArray.set(addr, DIRTY, 1);
            if (parent != null) {
                parent.declareDirty();
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("{\"nodes\":[");
        long nodesIndex = OffHeapLongArray.get(addr, NODES_INDEX);
        for (int i = 0; i < nodesIndex; i++) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append(OffHeapENode.toString(nodeAddrAt(addr ,i)));
        }
        builder.append("]}");
        return builder.toString();
    }

    final OffHeapENode nodeByIndex(final long index, final boolean createIfAbsent) {
        long nodesCapacity = OffHeapLongArray.get(addr, NODES_CAPACITY);
        long nodesIndex = OffHeapLongArray.get(addr, NODES_INDEX);

        if (index < nodesCapacity) {
            if (index > nodesIndex) {
                OffHeapLongArray.set(addr, NODES_INDEX, index + 1);
            }
            OffHeapENode elem = null;
            long elemAddr = nodeAddrAt(addr, index);
            if (elemAddr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                elem = new OffHeapENode(parent, this, _graph, index, elemAddr);
            }
            if (elemAddr == OffHeapConstants.OFFHEAP_NULL_PTR && createIfAbsent) {
                elem = new OffHeapENode(parent, this, _graph, index, OffHeapConstants.OFFHEAP_NULL_PTR);
                setNodeAddrAt(addr, index, elem.getAddr());
            }
            return elem;
        } else {
            throw new RuntimeException("bad API usage");
        }
    }

}
