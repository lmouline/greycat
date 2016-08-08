package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.core.chunk.ChunkListener;
import org.mwg.struct.LongArray;

class HeapLongArray implements LongArray {

    private long[] _back;
    private int _size;
    private final ChunkListener _listener;

    HeapLongArray(ChunkListener p_listener) {
        _back = null;
        _size = 0;
        _listener = p_listener;
    }

    @Override
    public int size() {
        return _size;
    }

    @Override
    public long get(int index) {
        return _back[index];
    }

    @Override
    public void add(long newValue) {
        if (_back == null) {
            _back = new long[Constants.MAP_INITIAL_CAPACITY];
            _back[0] = newValue;
            _size = 1;
        } else if (_size == _back.length) {
            long[] ex_back = new long[_back.length * 2];
            System.arraycopy(_back, 0, ex_back, 0, _size);
            _back = ex_back;
            _back[_size] = newValue;
            _size++;
        } else {
            _back[_size] = newValue;
            _size++;
        }
        _listener.declareDirty();
    }

    @Override
    public void remove(long oldValue) {
        int indexToRemove = -1;
        for (int i = 0; i < _size; i++) {
            if (_back[i] == oldValue) {
                indexToRemove = i;
                break;
            }
        }
        if (indexToRemove != -1) {
            if ((_size - 1) == 0) {
                _back = null;
                _size = 0;
            } else {
                long[] red_back = new long[_size - 1];
                System.arraycopy(_back, 0, red_back, 0, indexToRemove);
                System.arraycopy(_back, indexToRemove + 1, red_back, indexToRemove, _size - indexToRemove - 1);
                _back = red_back;
                _size--;
            }
        }
    }

}
