package org.mwg.core.chunk.heap;

import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class HeapEGraph implements EGraph {

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
        HeapENode newNode = new HeapENode(parent, this, parent.graph(), counter);
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
    @Override
    public ENode lookup(long id) {
        return _nodesMapping.get(id);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (_root != -1) {
            builder.append("\"root\":");
            builder.append(_root);
            builder.append(",");
        }
        builder.append("\"nodes\":[");
        Set<Long> keys = _nodesMapping.keySet();
        Long[] flat = keys.toArray(new Long[keys.size()]);
        for(int i=0;i<flat.length;i++){
            if(i!=0){
                builder.append(",");
            }
            ENode eNode = _nodesMapping.get(flat[i]);
            builder.append(eNode.toString());
        }
        builder.append("]}");
        return builder.toString();
    }

}
