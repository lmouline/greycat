/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.Relation;
import org.mwg.utility.Base64;

@SuppressWarnings("Duplicates")
class OffHeapRelation implements Relation {

    private static int CAPACITY = 0;
    private static int SIZE = 1;
    private static int SHIFT = 2;

    private final long index;
    private final OffHeapContainer container;

    OffHeapRelation(final OffHeapContainer p_container, final long p_index) {
        container = p_container;
        index = p_index;
    }

    @Override
    public long[] all() {
        long[] ids;
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr == OffHeapConstants.NULL_PTR) {
                ids = new long[0];
            } else {
                final long relSize = OffHeapLongArray.get(addr, SIZE);
                ids = new long[(int) relSize];
                for (int i = 0; i < relSize; i++) {
                    ids[i] = OffHeapLongArray.get(addr, i + SHIFT);
                }
            }
        } finally {
            container.unlock();
        }
        return ids;
    }

    public final void allocate(int newCapacity) {
        container.lock();
        try {
            unsafe_allocate(newCapacity);
        } finally {
            container.unlock();
        }
    }

    private void unsafe_allocate(int newCapacity) {
        final long addr = container.addrByIndex(index);
        if (addr == OffHeapConstants.NULL_PTR) {
            //initial allocation
            final long newly = OffHeapLongArray.allocate(newCapacity + SHIFT);
            OffHeapLongArray.set(newly, SIZE, 0);
            OffHeapLongArray.set(newly, CAPACITY, newCapacity);
            container.setAddrByIndex(index, newly);
        } else {
            final long capacity = OffHeapLongArray.get(addr, CAPACITY);
            if (capacity < newCapacity) {
                //extends
                long exAddr = OffHeapLongArray.reallocate(addr, newCapacity + SHIFT);
                container.setAddrByIndex(index, exAddr);
                OffHeapLongArray.set(exAddr, CAPACITY, newCapacity);
            }
        }
    }

    @Override
    public final int size() {
        container.lock();
        long size = 0;
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                size = OffHeapLongArray.get(addr, SIZE);
            }
        } finally {
            container.unlock();
        }
        return (int) size;
    }

    @Override
    public final long get(final int elemIndex) {
        long result = -1;
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                final long size = OffHeapLongArray.get(addr, SIZE);
                if (elemIndex < size) {
                    result = OffHeapLongArray.get(addr, elemIndex + SHIFT);
                } else {
                    return -1;
                }
            }
        } finally {
            container.unlock();
        }
        return result;
    }

    @Override
    public void set(int index, long value) {
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                final long size = OffHeapLongArray.get(addr, SIZE);
                if (index < size) {
                    OffHeapLongArray.set(addr, index + SHIFT, value);
                }
            }
        } finally {
            container.unlock();
        }
    }

    @Override
    public final Relation add(final long newValue) {
        container.lock();
        try {
            internal_add(newValue);
            container.declareDirty();
        } finally {
            container.unlock();
        }
        return this;
    }

    @Override
    public Relation addAll(long[] newValues) {
        container.lock();
        try {
            for (int i = 0; i < newValues.length; i++) {
                internal_add(newValues[i]);
            }
            container.declareDirty();
        } finally {
            container.unlock();
        }
        return this;
    }

    @Override
    public Relation addNode(Node node) {
        return add(node.id());
    }

    @Override
    public Relation insert(int insertIndex, long newValue) {
        container.lock();
        try {
            long size = 0;
            long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                size = OffHeapLongArray.get(addr, SIZE);
            }
            if (size == 0) {
                if (insertIndex != 0) {
                    throw new RuntimeException("Bad API usage ! index out of bounds: " + index);
                }
                long capacity = Constants.MAP_INITIAL_CAPACITY;
                addr = OffHeapLongArray.allocate(SHIFT + capacity);
                OffHeapLongArray.set(addr, CAPACITY, capacity);
                container.setAddrByIndex(index, addr);
                OffHeapLongArray.set(addr, SHIFT, newValue);
                OffHeapLongArray.set(addr, SIZE, 1);
            } else {
                final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                if (capacity == size) {
                    //need to reallocate first
                    final long newCapacity = capacity * 2;
                    addr = OffHeapLongArray.reallocate(addr, newCapacity + SHIFT);
                    container.setAddrByIndex(index, addr);
                    OffHeapLongArray.set(addr, CAPACITY, newCapacity);
                }
                OffHeapLongArray.insert(addr, SHIFT + insertIndex, newValue, size + SHIFT);
                OffHeapLongArray.set(addr, SIZE, size + 1);
            }
            container.declareDirty();
        } finally {
            container.unlock();
        }
        return this;
    }

    final void internal_add(final long newValue) {
        long addr = container.addrByIndex(index);
        long size;
        if (addr == OffHeapConstants.NULL_PTR) {
            long capacity = Constants.MAP_INITIAL_CAPACITY;
            addr = OffHeapLongArray.allocate(SHIFT + capacity);
            OffHeapLongArray.set(addr, CAPACITY, capacity);
            container.setAddrByIndex(index, addr);
            size = 0;
        } else {
            size = OffHeapLongArray.get(addr, SIZE);
            final long capacity = OffHeapLongArray.get(addr, CAPACITY);
            if (size == capacity) {
                final long newCapacity = capacity * 2;
                addr = OffHeapLongArray.reallocate(addr, newCapacity + SHIFT);
                container.setAddrByIndex(index, addr);
                OffHeapLongArray.set(addr, CAPACITY, newCapacity);
            }
        }
        OffHeapLongArray.set(addr, size + SHIFT, newValue);
        OffHeapLongArray.set(addr, SIZE, size + 1);
    }

    @Override
    public final Relation remove(final long oldValue) {
        boolean leftShift = false;
        container.lock();
        try {
            long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                final long size = OffHeapLongArray.get(addr, SIZE);
                for (int i = 0; i < size; i++) {
                    long current = OffHeapLongArray.get(addr, SHIFT + i);
                    if (leftShift) {
                        OffHeapLongArray.set(addr, SHIFT + i - 1, current);
                    } else {
                        if (current == oldValue) {
                            leftShift = true;
                        }
                    }
                }
                if (leftShift) {
                    OffHeapLongArray.set(addr, SIZE, size - 1);
                    container.declareDirty();
                }
            }

        } finally {
            container.unlock();
        }
        return this;
    }

    @Override
    public Relation delete(int indexToDelete) {
        container.lock();
        try {
            long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                final long size = OffHeapLongArray.get(addr, SIZE);
                OffHeapLongArray.delete(addr, indexToDelete + SHIFT, size);
                OffHeapLongArray.set(addr, SIZE, size - 1);
                container.declareDirty();
            }

        } finally {
            container.unlock();
        }
        return this;
    }

    @Override
    public final Relation clear() {
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                OffHeapLongArray.set(addr, SIZE, 0);
            }
        } finally {
            container.unlock();
        }
        return this;
    }


    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        //chunk.lock();
        // try {
        final long addr = container.addrByIndex(index);
        if (addr != OffHeapConstants.NULL_PTR) {
            buffer.append("[");
            final long size = OffHeapLongArray.get(addr, SIZE);
            for (int i = 0; i < size; i++) {
                if (i != 0) {
                    buffer.append(",");
                }
                buffer.append(OffHeapLongArray.get(addr, i + SHIFT));
            }
            buffer.append("]");
        }
        //} finally {
        // chunk.unlock();
        //}
        return buffer.toString();
    }

    static void save(final long addr, final Buffer buffer) {
        if (addr == OffHeapConstants.NULL_PTR) {
            return;
        }
        final long size = OffHeapLongArray.get(addr, SIZE);
        Base64.encodeLongToBuffer(size, buffer);
        for (long i = 0; i < size; i++) {
            buffer.write(Constants.CHUNK_VAL_SEP);
            Base64.encodeLongToBuffer(OffHeapLongArray.get(addr, i + SHIFT), buffer);
        }
    }

    static long clone(final long addr) {
        if (addr == OffHeapConstants.NULL_PTR) {
            return OffHeapConstants.NULL_PTR;
        }
        final long capacity = OffHeapLongArray.get(addr, CAPACITY);
        return OffHeapLongArray.cloneArray(addr, capacity + SHIFT);
    }

    static void free(final long addr) {
        OffHeapLongArray.free(addr);
    }

    final long load(final Buffer buffer, final long offset, final long max) {
        long cursor = offset;
        byte current = buffer.read(cursor);
        boolean isFirst = true;
        long previous = offset;
        while (cursor < max && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP) {
            if (current == Constants.CHUNK_VAL_SEP) {
                if (isFirst) {
                    unsafe_allocate((int) Base64.decodeToLongWithBounds(buffer, previous, cursor));
                    isFirst = false;
                } else {
                    internal_add(Base64.decodeToLongWithBounds(buffer, previous, cursor));
                }
                previous = cursor + 1;
            }
            cursor++;
            if (cursor < max) {
                current = buffer.read(cursor);
            }
        }
        if (!isFirst) {
            internal_add(Base64.decodeToLongWithBounds(buffer, previous, cursor));
        }
        return cursor;
    }


}
