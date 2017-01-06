package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;

public class OffHeapEGraph implements EGraph {
    private final Graph _graph;
    private final OffHeapStateChunk parent;

    private static final int DIRTY = 0;
    private static final int NODES_CAPACITY = 1;
    private static final int NODES_INDEX = 2;
    private static final int NODES = 3;

    private long addr = OffHeapConstants.OFFHEAP_NULL_PTR;

    OffHeapEGraph(final OffHeapStateChunk p_parent, final OffHeapEGraph origin, final Graph p_graph) {
        parent = p_parent;
        _graph = p_graph;
        if (origin != null) {

            long nodesCapacity = OffHeapLongArray.get(origin.getAddr(), NODES_CAPACITY);
            long nodesIndex = OffHeapLongArray.get(origin.getAddr(), NODES_INDEX);

            addr = OffHeapLongArray.allocate(NODES + nodesCapacity);
            OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex);
            OffHeapLongArray.set(addr, NODES_CAPACITY, nodesCapacity);

            //pass #1: copy nodes
            for (int i = 0; i < nodesIndex; i++) {
                OffHeapENode eNode = new OffHeapENode(parent, this, _graph, i, origin.getNodeAddrAt(i));
                setNodeAddrAt(i, eNode.getAddr());
            }
            //pass #2: rebase all links
            for (int i = 0; i < nodesIndex; i++) {
                OffHeapENode.rebase(getNodeAddrAt(i));
            }
        } else {
            addr = OffHeapLongArray.allocate(NODES);
            OffHeapLongArray.set(addr, DIRTY, 0);
            OffHeapLongArray.set(addr, NODES_INDEX, 0);
            OffHeapLongArray.set(addr, NODES_CAPACITY, 0);
        }
    }

    public final long load(final Buffer buffer, final long offset, final long max) {
        // TODO
//        long cursor = offset;
//        byte current = buffer.read(cursor);
//        boolean isFirst = true;
//        int insertIndex = 0;
//        while (cursor < max && current != Constants.CHUNK_SEP) {
//            if (current == Constants.CHUNK_ENODE_SEP) {
//                if (isFirst) {
//                    allocate(Base64.decodeToIntWithBounds(buffer, offset, cursor));
//                    isFirst = false;
//                }
//                cursor++;
//                OffHeapENode eNode = nodeByIndex(insertIndex, true);
//                cursor = eNode.load(buffer, cursor, parent);
//                insertIndex++;
//            } else {
//                cursor++;
//            }
//            current = buffer.read(cursor);
//        }
//        return cursor;
        return 0;
    }

    long getAddr() {
        return this.addr;
    }

    long getNodeAddrAt(long nodeIndex) {
        return OffHeapLongArray.get(addr, NODES + nodeIndex);
    }

    void setNodeAddrAt(long nodeIndex, long nodeAddr) {
        OffHeapLongArray.set(addr, NODES + nodeIndex, nodeAddr);
    }

    @Override
    public ENode root() {
        if (OffHeapLongArray.get(addr, NODES_INDEX) > 0) {
            return new OffHeapENode(parent, this, _graph, 0, getNodeAddrAt(0));
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
        setNodeAddrAt(nodesIndex, newNode.getAddr());
        OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex + 1);

        return newNode;
    }

    @Override
    public EGraph setRoot(ENode eNode) {
        final OffHeapENode casted = (OffHeapENode) eNode;
        final long previousId = OffHeapENode.getId(casted.getAddr());
        if (previousId != 0) {
            long previousRootAddr = getNodeAddrAt(0);
            setNodeAddrAt(previousId, previousRootAddr);
            OffHeapENode.setId(previousRootAddr, previousId);
            setNodeAddrAt(0, casted.getAddr());
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
            setNodeAddrAt(previousId, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex - 1);
        } else {
            setNodeAddrAt(previousId, getNodeAddrAt(nodesIndex - 1));
            OffHeapENode.setId(getNodeAddrAt(previousId), previousId);
            OffHeapLongArray.set(addr, NODES_INDEX, nodesIndex - 1);
        }
        return this;
    }

    @Override
    public int size() {
        return (int) OffHeapLongArray.get(addr, NODES_INDEX);
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
            builder.append(OffHeapENode.toString(getNodeAddrAt(i)));
        }
        builder.append("]}");
        return builder.toString();
    }
}
