package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.struct.ENode;
import org.mwg.struct.ERelation;

class HeapERelation implements ERelation {

    private ENode[] _back;
    private int _size;
    private int _capacity;
    private final HeapStateChunk parent;

    HeapERelation(final HeapStateChunk p_listener, final HeapERelation origin) {
        parent = p_listener;
        if (origin != null) {
            _back = new ENode[origin._capacity];
            System.arraycopy(origin._back, 0, _back, 0, origin._capacity);
            _size = origin._size;
            _capacity = origin._capacity;
        } else {
            _back = null;
            _size = 0;
            _capacity = 0;
        }
    }

    @Override
    public final int size() {
        return _size;
    }

    @Override
    public final ENode[] nodes() {
        ENode[] copy = new ENode[_size];
        System.arraycopy(_back, 0, copy, 0, _size);
        return copy;
    }

    @Override
    public final ENode node(int index) {
        return _back[index];
    }

    @Override
    public final ERelation add(ENode eNode) {
        if (_capacity == _size) {
            if (_capacity == 0) {
                allocate(Constants.MAP_INITIAL_CAPACITY);
            } else {
                allocate(_capacity * 2);
            }
        }
        _back[_size] = eNode;
        _size++;
        parent.declareDirty();
        return this;
    }

    @Override
    public final ERelation addAll(final ENode[] eNodes) {
        allocate(eNodes.length + _size);
        System.arraycopy(eNodes, 0, _back, _size, eNodes.length);
        parent.declareDirty();
        return this;
    }

    @Override
    public final ERelation clear() {
        _size = 0;
        _back = null;
        parent.declareDirty();
        return this;
    }

    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        for (int i = 0; i < _size; i++) {
            if (i != 0) {
                buffer.append(",");
            }
            buffer.append(((HeapENode) _back[i])._id);
        }
        buffer.append("]");
        return buffer.toString();
    }

    final void allocate(int newCapacity) {
        final int closePowerOfTwo = (int) Math.pow(2, Math.ceil(Math.log(newCapacity) / Math.log(2)));
        if (closePowerOfTwo > _capacity) {
            ENode[] new_back = new ENode[closePowerOfTwo];
            if (_back != null) {
                System.arraycopy(_back, 0, new_back, 0, _size);
            }
            _back = new_back;
            _capacity = closePowerOfTwo;
        }
    }

}
