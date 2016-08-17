package org.mwg.memory.offheap;

import org.mwg.chunk.Stack;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @ignore ts
 */
public class OffHeapFixedStack implements Stack {

    private long _first;
    private long _last;
    private long _next;
    private long _prev;
    private long _count;
    final ReentrantLock lock = new ReentrantLock();
    final long _capacity;

    public OffHeapFixedStack(long capacity) {
        this._capacity = capacity;
        this._next = OffHeapLongArray.allocate(capacity);
        this._prev = OffHeapLongArray.allocate(capacity);
        this._first = OffHeapConstants.OFFHEAP_NULL_PTR;
        this._last = OffHeapConstants.OFFHEAP_NULL_PTR;
        for (long i = 0; i < capacity; i++) {
            long l = _last;
            OffHeapLongArray.set(_prev, i, l);
            _last = i;
            if (_first == OffHeapConstants.OFFHEAP_NULL_PTR) {
                _first = i;
            } else {
                OffHeapLongArray.set(_next, l, i);
            }
        }
        _count = capacity;
    }

    @Override
    public final boolean enqueue(long index) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (_count >= _capacity) {
                return false;
            }
            if (_first == index || _last == index) {
                return false;
            }
            if (OffHeapLongArray.get(_prev, index) != OffHeapConstants.OFFHEAP_NULL_PTR || OffHeapLongArray.get(_next, index) != OffHeapConstants.OFFHEAP_NULL_PTR) { //test if was already in FIFO
                return false;
            }
            long l = _last;
            OffHeapLongArray.set(_prev, index, l);
            _last = index;
            if (_first == OffHeapConstants.OFFHEAP_NULL_PTR) {
                _first = index;
            } else {
                OffHeapLongArray.set(_next, l, index);
            }
            ++_count;
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final long dequeueTail() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            long f = _first;
            if (f == OffHeapConstants.OFFHEAP_NULL_PTR) {
                return OffHeapConstants.OFFHEAP_NULL_PTR;
            }
            long n = OffHeapLongArray.get(_next, f);
            //tag as unused
            OffHeapLongArray.set(_next, f, OffHeapConstants.OFFHEAP_NULL_PTR);
            OffHeapLongArray.set(_prev, f, OffHeapConstants.OFFHEAP_NULL_PTR);
            _first = n;
            if (n == OffHeapConstants.OFFHEAP_NULL_PTR) {
                _last = OffHeapConstants.OFFHEAP_NULL_PTR;
            } else {
                OffHeapLongArray.set(_prev, n, OffHeapConstants.OFFHEAP_NULL_PTR);
            }
            --_count;
            return f;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean dequeue(long index) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            long p = OffHeapLongArray.get(_prev, index);
            long n = OffHeapLongArray.get(_next, index);
            if (p == OffHeapConstants.OFFHEAP_NULL_PTR && n == OffHeapConstants.OFFHEAP_NULL_PTR) {
                return false;
            }
            if (p == OffHeapConstants.OFFHEAP_NULL_PTR) {
                long f = _first;
                if (f == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    return false;
                }
                long n2 = OffHeapLongArray.get(_next, f);
                OffHeapLongArray.set(_next, f, OffHeapConstants.OFFHEAP_NULL_PTR);
                OffHeapLongArray.set(_prev, f, OffHeapConstants.OFFHEAP_NULL_PTR);
                _first = n2;
                if (n2 == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    _last = OffHeapConstants.OFFHEAP_NULL_PTR;
                } else {
                    OffHeapLongArray.set(_prev, n2, OffHeapConstants.OFFHEAP_NULL_PTR);
                }
                --_count;
            } else if (n == OffHeapConstants.OFFHEAP_NULL_PTR) {
                long l = _last;
                if (l == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    return false;
                }
                long p2 = OffHeapLongArray.get(_prev, l);
                OffHeapLongArray.set(_prev, l, OffHeapConstants.OFFHEAP_NULL_PTR);
                OffHeapLongArray.set(_next, l, OffHeapConstants.OFFHEAP_NULL_PTR);
                _last = p2;
                if (p2 == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    _first = OffHeapConstants.OFFHEAP_NULL_PTR;
                } else {
                    OffHeapLongArray.set(_next, p2, OffHeapConstants.OFFHEAP_NULL_PTR);
                }
                --_count;
            } else {
                OffHeapLongArray.set(_next, p, n);
                OffHeapLongArray.set(_prev, n, p);
                OffHeapLongArray.set(_prev, index, OffHeapConstants.OFFHEAP_NULL_PTR);
                OffHeapLongArray.set(_next, index, OffHeapConstants.OFFHEAP_NULL_PTR);
                --_count;
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final void free() {
        OffHeapLongArray.free(_next);
        OffHeapLongArray.free(_prev);
    }

    @Override
    public long size() {
        return _count;
    }
}
