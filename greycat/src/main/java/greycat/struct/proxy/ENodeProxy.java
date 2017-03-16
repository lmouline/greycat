/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greycat.struct.proxy;

import greycat.Container;
import greycat.Type;
import greycat.plugin.NodeStateCallback;
import greycat.struct.*;

public final class ENodeProxy implements ENode {

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
                _node = _parent.rephase().root();
            } else {
                _node = _parent.rephase().node(_index);
            }
            _parent = null;
        }
    }

    @Override
    public final Object get(final String name) {
        return this.getAt(_node.egraph().graph().resolver().stringToHash(name, false));
    }

    @Override
    public final <A> A getWithDefault(final String key, final A defaultValue) {
        return getAtWithDefault(_node.egraph().graph().resolver().stringToHash(key, false), defaultValue);
    }

    @Override
    public final <A> A getAtWithDefault(final int key, final A defaultValue) {
        Object elem = getAt(key);
        if (elem != null) {
            return (A) elem;
        } else {
            return defaultValue;
        }
    }

    @Override
    public final Object getOrCreate(final String name, final byte type) {
        return this.getOrCreateAt(_node.egraph().graph().resolver().stringToHash(name, false), type);
    }

    @Override
    public final Object getOrCreateAt(final int index, final byte type) {
        Object elem = getAt(index);
        if (elem != null) {
            return proxifyIfNecesserary(elem, index);
        } else {
            check();
            return _node.getOrCreateAt(index, type);
        }
    }

    private Object proxifyIfNecesserary(Object elem, int index) {
        if (elem == null || _parent == null) { //implement time sensitivity
            return elem;
        } else {
            byte type = typeAt(index);
            //temporary proxy
            switch (type) {
                case Type.LMATRIX:
                    return new LMatrixProxy(index, this, (LMatrixProxy) elem);
                case Type.DMATRIX:
                    return new DMatrixProxy(index, this, (DMatrixProxy) elem);
                case Type.ERELATION:
                    return new ERelationProxy(index, this, (ERelation) elem);
                case Type.RELATION:
                    return new RelationProxy(index, this, (Relation) elem);
                case Type.RELATION_INDEXED:
                    return new RelationIndexedProxy(index, this, (RelationIndexed) elem);
                case Type.KDTREE:
                    return new TreeProxy(index, this, (Tree) elem);
                case Type.NDTREE:
                    return new ProfileProxy(index, this, (Profile) elem);
                case Type.LONG_TO_LONG_MAP:
                    return new LongLongMapProxy(index, this, (LongLongMap) elem);
                case Type.LONG_TO_LONG_ARRAY_MAP:
                    return new LongLongArrayMapProxy(index, this, (LongLongArrayMap) elem);
                case Type.STRING_TO_INT_MAP:
                    return new StringIntMapProxy(index, this, (StringIntMap) elem);
                default:
                    return elem;
            }
        }
    }

    @Override
    public final Object getAt(int index) {
        return proxifyIfNecesserary(_node.getAt(index), index);
    }

    @Override
    public final byte type(String name) {
        return _node.type(name);
    }

    @Override
    public final byte typeAt(int index) {
        return _node.typeAt(index);
    }

    @Override
    public final EGraph egraph() {
        if (_parent != null) {
            return _parent;
        } else {
            return _node.egraph();
        }
    }

    @Override
    public final void each(final NodeStateCallback callBack) {
        _node.each(callBack);
    }

    @Override
    public final Container set(final String name, final byte type, final Object value) {
        check();
        return _node.set(name, type, value);
    }

    @Override
    public final Container setAt(final int index, final byte type, final Object value) {
        check();
        return _node.setAt(index, type, value);
    }

    @Override
    public final Container remove(final String name) {
        check();
        return _node.remove(name);
    }

    @Override
    public final Container removeAt(final int index) {
        check();
        return _node.removeAt(index);
    }

    @Override
    public final Container rephase() {
        if (_index == -1) {
            return _parent.rephase().root();
        } else {
            return _parent.rephase().node(_index);
        }
    }

    @Override
    public final void drop() {
        check();
        _node.drop();
    }

    @Override
    public final ENode clear() {
        check();
        return _node.clear();
    }

    @Override
    public final String toString() {
        return _node.toString();
    }

}
