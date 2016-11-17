package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.struct.Relationship;

import java.util.Arrays;

class HeapRelationship implements Relationship {

    private long[] _back;
    private volatile int _size;
    private final HeapStateChunk parent;
    private boolean aligned = true;

    HeapRelationship(final HeapStateChunk p_listener, final HeapRelationship origin) {
        parent = p_listener;
        if (origin != null) {
            aligned = false;
            _back = origin._back;
            _size = origin._size;
        } else {
            _back = null;
            _size = 0;
        }
    }

    public final void allocate(int _capacity) {
        long[] new_back = new long[_capacity];
        if (_back != null) {
            System.arraycopy(_back, 0, new_back, 0, _back.length);
        }
        _back = new_back;
    }

    @Override
    public final int size() {
        return _size;
    }

    @Override
    public final long get(int index) {
        long result;
        synchronized (parent) {
            result = _back[index];
        }
        return result;
    }

    @Override
    public void set(int index, long value) {
        synchronized (parent) {
            _back[index] = value;
        }
    }

    final long unsafe_get(int index) {
        return _back[index];
    }

    @Override
    public final Relationship add(long newValue) {
        synchronized (parent) {
            if (!aligned) {
                long[] temp_back = new long[_back.length];
                System.arraycopy(_back, 0, temp_back, 0, _back.length);
                _back = temp_back;
                aligned = true;
            }
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
            parent.declareDirty();
        }
        return this;
    }

    @Override
    public Relationship insert(final long newValue, final int targetIndex) {
        synchronized (parent) {
            if (!aligned) {
                long[] temp_back = new long[_back.length];
                System.arraycopy(_back, 0, temp_back, 0, _back.length);
                _back = temp_back;
                aligned = true;
            }
            if (_back == null) {
                if (targetIndex != 0) {
                    throw new RuntimeException("Bad API usage ! index out of bounds: " + targetIndex);
                }
                _back = new long[Constants.MAP_INITIAL_CAPACITY];
                _back[0] = newValue;
                _size = 1;
            } else if (_size == _back.length) {
                long[] ex_back = new long[_back.length * 2];
                System.arraycopy(_back, 0, ex_back, 0, _size);
                _back[targetIndex] = newValue;
                System.arraycopy(_back, 0, ex_back, 0, _size);


                System.arraycopy(_back, 0, ex_back, 0, _size);
                _back = ex_back;
                _back[_size] = newValue;
                _size++;
            } else {
                /*
                int afterIndexSize = _size - taretIndex;
                long[] temp = new long[afterIndexSize];
                System.arraycopy(_back, taretIndex, temp, 0, afterIndexSize);
                _back[taretIndex] = newValue;
                System.arraycopy(temp, 0, _back, taretIndex + 1, afterIndexSize);
                _size++;
                */
                //TODO
            }
            parent.declareDirty();
        }
        return this;
    }

    @Override
    public Relationship remove(long oldValue) {
        synchronized (parent) {
            if (!aligned) {
                long[] temp_back = new long[_back.length];
                System.arraycopy(_back, 0, temp_back, 0, _back.length);
                _back = temp_back;
                aligned = true;
            }
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
        return this;
    }

    @Override
    public Relationship delete(int toRemoveIndex) {
        synchronized (parent) {
            if (!aligned) {
                long[] temp_back = new long[_back.length];
                System.arraycopy(_back, 0, temp_back, 0, _back.length);
                _back = temp_back;
                aligned = true;
            }
            if (toRemoveIndex != -1) {
                if ((_size - 1) == 0) {
                    _back = null;
                    _size = 0;
                } else {
                    //TODO this is by far not optimal
                    long[] red_back = new long[_size - 1];
                    System.arraycopy(_back, 0, red_back, 0, toRemoveIndex);
                    System.arraycopy(_back, toRemoveIndex + 1, red_back, toRemoveIndex, _size - toRemoveIndex - 1);
                    _back = red_back;
                    _size--;
                }
            }
        }
        return this;
    }

    @Override
    public Relationship clear() {
        synchronized (parent) {
            _size = 0;
            if (!aligned) {
                _back = null;
                aligned = true;
            } else {
                if (_back != null) {
                    Arrays.fill(_back, 0, _back.length, -1);
                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        for (int i = 0; i < _size; i++) {
            if (i != 0) {
                buffer.append(",");
            }
            buffer.append(_back[i]);
        }
        buffer.append("]");
        return buffer.toString();
    }
}
