package greycat.struct.proxy;

import greycat.Container;
import greycat.plugin.NodeStateCallback;
import greycat.struct.EGraph;
import greycat.struct.ENode;

public class ENodeProxy implements ENode {

    EGraphProxy _parent;
    ENode _node;
    final int _index;

    public ENodeProxy(EGraphProxy _parent, ENode _node, int _index) {
        this._parent = _parent;
        this._node = _node;
        this._index = _index;
    }

    private void check() {
        if (_parent != null) {
            if (_index == -1) {
                _node = (ENode) _parent.rephase().root();
            } else {
                _node = (ENode) _parent.rephase().node(_index);
            }
            _parent = null;
        }
    }

    @Override
    public Object get(String name) {
        return _;
    }

    @Override
    public Object getAt(int index) {
        return null;
    }

    @Override
    public byte type(String name) {
        return 0;
    }

    @Override
    public byte typeAt(int index) {
        return 0;
    }

    @Override
    public Container set(String name, byte type, Object value) {
        return null;
    }

    @Override
    public Container setAt(int index, byte type, Object value) {
        return null;
    }

    @Override
    public Container remove(String name) {
        return null;
    }

    @Override
    public Container removeAt(int index) {
        return null;
    }

    @Override
    public Object getOrCreate(String name, byte type) {
        return null;
    }

    @Override
    public Object getOrCreateAt(int index, byte type) {
        return null;
    }

    @Override
    public <A> A getWithDefault(String key, A defaultValue) {
        return null;
    }

    @Override
    public <A> A getAtWithDefault(int key, A defaultValue) {
        return null;
    }

    @Override
    public Container rephase() {
        return null;
    }

    @Override
    public void drop() {

    }

    @Override
    public EGraph egraph() {
        return null;
    }

    @Override
    public void each(NodeStateCallback callBack) {

    }

    @Override
    public ENode clear() {
        return null;
    }
}
