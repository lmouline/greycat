package greycat.struct.proxy;

import greycat.Container;
import greycat.struct.IntStringMap;
import greycat.struct.IntStringMapCallBack;

public class IntStringMapProxy implements IntStringMap {
    private final int _index;
    private Container _target;
    private IntStringMap _elem;

    public IntStringMapProxy(final int _relationIndex, final Container _target, final IntStringMap _relation) {
        this._index = _relationIndex;
        this._target = _target;
        this._elem = _relation;
    }

    private void check() {
        if (_target != null) {
            _elem = (IntStringMap) _target.getRawAt(_index);
            _target = null;
        }
    }

    @Override
    public final int size() {
        return _elem.size();
    }

    @Override
    public final String get(final int key) {
        return _elem.get(key);
    }

    @Override
    public final void each(final IntStringMapCallBack callback) {
        _elem.each(callback);
    }

    @Override
    public final void put(final int key, final String value) {
        check();
        _elem.put(key, value);
    }

    @Override
    public final void remove(final int key) {
        check();
        _elem.remove(key);
    }
}
