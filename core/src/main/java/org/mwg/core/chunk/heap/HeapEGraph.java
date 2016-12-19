package org.mwg.core.chunk.heap;

import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;

import java.util.HashMap;
import java.util.Map;

public class HeapEGraph implements EGraph {

    private final HeapStateChunk parent;
    private boolean _dirty;
    private Map<Long, ENode> _nodesMapping;
    private int counter = 0;
    private long _root = -1;

    HeapEGraph(HeapStateChunk p_parent) {
        parent = p_parent;
        _nodesMapping = new HashMap<Long, ENode>();
    }

    void declareDirty() {
        if (!_dirty) {
            _dirty = true;
            parent.declareDirty();
        }
    }

    @Override
    public ENode root() {
        return lookup(_root);
    }

    @Override
    public ENode newNode() {
        HeapENode newNode = new HeapENode(this, parent.graph(), counter);
        counter++;
        _nodesMapping.put(newNode.id(), newNode);
        return newNode;
    }

    @Override
    public EGraph setRoot(ENode eNode) {
        _root = eNode.id();
        return this;
    }

    @Override
    public EGraph drop(ENode eNode) {
        _nodesMapping.remove(eNode.id());
        return this;
    }

    public ENode lookup(long id) {
        return _nodesMapping.get(id);
    }


}
