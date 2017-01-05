package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.struct.Buffer;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;
import org.mwg.utility.Base64;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class HeapEGraph implements EGraph {

    private final Graph _graph;
    private final HeapStateChunk parent;
    boolean _dirty;

    HeapENode[] _nodes = null;
    private int _nodes_capacity = 0;
    private int _nodes_index = 0;
    private HeapENode _root;

    HeapEGraph(final HeapStateChunk p_parent, final HeapEGraph origin, final Graph p_graph) {
        parent = p_parent;
        _graph = p_graph;
        if (origin != null) {
            _nodes_index = origin._nodes_index;
            _nodes_capacity = origin._nodes_capacity;
            _nodes = new HeapENode[_nodes_capacity];
            //pass #1: copy nodes
            for (int i = 0; i < _nodes_index; i++) {
                _nodes[i] = new HeapENode(parent, this, _graph, i, origin._nodes[i]);
            }
            //pass #2: rebase all links
            for (int i = 0; i < _nodes_index; i++) {
                _nodes[i].rebase();
            }
        }
    }

    @Override
    public final int size() {
        return _nodes_index;
    }

    final void allocate(int newCapacity) {
        final int closePowerOfTwo = (int) Math.pow(2, Math.ceil(Math.log(newCapacity) / Math.log(2)));
        if (closePowerOfTwo > _nodes_capacity) {
            HeapENode[] new_back = new HeapENode[closePowerOfTwo];
            if (_nodes != null) {
                System.arraycopy(_nodes, 0, new_back, 0, _nodes_index);
            }
            _nodes = new_back;
            _nodes_capacity = closePowerOfTwo;
        }
    }

    final ENode nodeByIndex(int index, boolean createIfAbsent) {
        if (index < _nodes_capacity) {
            ENode elem = _nodes[index];
            if (elem == null && createIfAbsent) {
                HeapENode newNode = new HeapENode(parent, this, _graph, index, null);
                _nodes[_nodes_index] = newNode;
            }
            return elem;
        } else {
            throw new RuntimeException("bad API usage");
        }
    }

    final void declareDirty() {
        if (!_dirty) {
            _dirty = true;
            if (parent != null) {
                parent.declareDirty();
            }
        }
    }

    @Override
    public final ENode newNode() {
        if (_nodes_index == _nodes_capacity) {
            int newCapacity = _nodes_capacity * 2;
            if (newCapacity == 0) {
                newCapacity = Constants.MAP_INITIAL_CAPACITY;
            }
            HeapENode[] newNodes = new HeapENode[newCapacity];
            if (_nodes != null) {
                System.arraycopy(_nodes, 0, newNodes, 0, _nodes_capacity);
            }
            _nodes_capacity = newCapacity;
            _nodes = newNodes;
        }
        HeapENode newNode = new HeapENode(parent, this, _graph, _nodes_index, null);
        _nodes[_nodes_index] = newNode;
        _nodes_index++;
        return newNode;
    }

    @Override
    public ENode root() {
        return _root;
    }


    @Override
    public EGraph setRoot(ENode eNode) {
        _root = (HeapENode) eNode;
        return this;
    }

    @Override
    public EGraph drop(ENode eNode) {
        HeapENode casted = (HeapENode) eNode;
        int previousId = casted._id;
        if (previousId == _nodes_index - 1) {
            //free
            _nodes[previousId] = null;
            _nodes_index--;
        } else {
            _nodes[previousId] = _nodes[_nodes_index - 1];
            _nodes[previousId]._id = previousId;
            _nodes_index--;
        }
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (_root != null) {
            builder.append("\"root\":");
            builder.append(_root._id);
            builder.append(",");
        }
        builder.append("\"nodes\":[");
        for (int i = 0; i < _nodes_index; i++) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append(_nodes[i].toString());
        }
        builder.append("]}");
        return builder.toString();
    }

    public final long load(final Buffer buffer, final long offset, final long max) {
        long cursor = offset;
        byte current = buffer.read(cursor);
        boolean isFirst = true;
        long previous = offset;
        while (cursor < max && current != Constants.CHUNK_SEP) {
            if (current == Constants.CHUNK_ENODE_SEP) {
                if (isFirst) {
                    allocate((int) Base64.decodeToLongWithBounds(buffer, previous, cursor));
                    isFirst = false;
                } else {
                    // add(Base64.decodeToLongWithBounds(buffer, previous, cursor));
                }
                previous = cursor + 1;
            }
            cursor++;
            current = buffer.read(cursor);
        }
        if (isFirst) {
            allocate((int) Base64.decodeToLongWithBounds(buffer, previous, cursor));
        } else {
            // add(Base64.decodeToLongWithBounds(buffer, previous, cursor));
        }
        return cursor;
    }

}
