package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class HeapEGraph implements EGraph {

    private final Graph _graph;
    private final HeapStateChunk parent;
    private boolean _dirty;

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

    void declareDirty() {
        if (!_dirty) {
            _dirty = true;
            if (parent != null) {
                parent.declareDirty();
            }
        }
    }

    @Override
    public ENode newNode() {
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

}
