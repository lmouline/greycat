package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.TimeTreeChunk;
import org.mwg.chunk.TreeWalker;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;

class OffHeapTimeTreeChunk implements TimeTreeChunk {

    private static final int DIRTY = 0;
    private static final int SIZE = 1;
    private static final int CAPACITY = 2;
    private static final int HEAD = 3;
    private static final int MAGIC = 4;

    private static final int OFFSET = 5;
    private static final int ELEM_SIZE = 5;

    private final OffHeapChunkSpace space;
    private final long index;

    OffHeapTimeTreeChunk(final OffHeapChunkSpace p_space, final long p_index) {
        space = p_space;
        index = p_index;
        space.lockByIndex(index);
        try {
            long temp_addr = space.addrByIndex(index);
            if (temp_addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                long initialCapacity = Constants.MAP_INITIAL_CAPACITY;
                temp_addr = OffHeapLongArray.allocate(OFFSET + (initialCapacity * ELEM_SIZE));
                space.setAddrByIndex(index, temp_addr);
                //init the initial values
                OffHeapLongArray.set(temp_addr, MAGIC, 0);
                OffHeapLongArray.set(temp_addr, CAPACITY, initialCapacity);
                OffHeapLongArray.set(temp_addr, SIZE, 0);
                OffHeapLongArray.set(temp_addr, DIRTY, 0);
                OffHeapLongArray.set(temp_addr, HEAD, -1);
            }
        } finally {
            space.unlockByIndex(index);
        }
    }

    public static void free(final long addr) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            OffHeapLongArray.free(addr);
        }
    }

    @Override
    public final long world() {
        return space.worldByIndex(index);
    }

    @Override
    public final long time() {
        return space.timeByIndex(index);
    }

    @Override
    public final long id() {
        return space.idByIndex(index);
    }

    @Override
    public final long size() {
        long result;
        space.lockByIndex(index);
        try {
            result = OffHeapLongArray.get(space.addrByIndex(index), SIZE);
        } finally {
            space.unlockByIndex(index);
        }
        return result;
    }

    @Override
    public final long index() {
        return index;
    }

    @Override
    public final long magic() {
        long result;
        space.lockByIndex(index);
        try {
            result = OffHeapLongArray.get(space.addrByIndex(index), MAGIC);
        } finally {
            space.unlockByIndex(index);
        }
        return result;
    }

    @Override
    public final void range(final long startKey, final long endKey, final long maxElements, final TreeWalker walker) {
        space.lockByIndex(index);
        try {
            final long addr = space.addrByIndex(index);
            long nbElements = 0;
            long indexEnd = internal_previousOrEqual_index(addr, endKey);
            while (indexEnd != -1 && key(addr, indexEnd) >= startKey && nbElements < maxElements) {
                walker.elem(key(addr, indexEnd));
                nbElements++;
                indexEnd = previous(addr, indexEnd);
            }
        } finally {
            space.unlockByIndex(index);
        }
    }

    @Override
    public final void save(final Buffer buffer) {
        space.lockByIndex(index);
        try {
            final long addr = space.addrByIndex(index);
            final long size = OffHeapLongArray.get(addr, SIZE);
            Base64.encodeLongToBuffer(size, buffer);
            buffer.write(Constants.CHUNK_SEP);
            boolean isFirst = true;
            for (long i = 0; i < size; i++) {
                if (!isFirst) {
                    buffer.write(Constants.CHUNK_SUB_SEP);
                } else {
                    isFirst = false;
                }
                Base64.encodeLongToBuffer(key(addr, i), buffer);
            }
            OffHeapLongArray.set(addr, DIRTY, 0);
        } finally {
            space.unlockByIndex(index);
        }
    }

    @Override
    public final void load(Buffer buffer) {
        if (buffer == null || buffer.length() == 0) {
            return;
        }
        space.lockByIndex(index);
        try {
            long addr = space.addrByIndex(index);
            final long size = OffHeapLongArray.get(addr, SIZE);
            final boolean initial = size == 0;
            boolean isDirty = false;
            long cursor = 0;
            long previous = 0;
            long payloadSize = buffer.length();
            while (cursor < payloadSize) {
                byte current = buffer.read(cursor);
                if (current == Constants.CHUNK_SUB_SEP) {
                    boolean insertResult = internal_insert(addr, Base64.decodeToLongWithBounds(buffer, previous, cursor));
                    isDirty = isDirty || insertResult;
                    previous = cursor + 1;
                } else if (current == Constants.CHUNK_SEP) {
                    final long treeSize = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                    final long closePowerOfTwo = (long) Math.pow(2, Math.ceil(Math.log(treeSize) / Math.log(2)));
                    addr = reallocate(addr, OffHeapLongArray.get(addr, CAPACITY), closePowerOfTwo);
                    previous = cursor + 1;
                }
                cursor++;
            }
            boolean insertResult = internal_insert(addr, Base64.decodeToLongWithBounds(buffer, previous, cursor));
            isDirty = isDirty || insertResult;
            if (isDirty && !initial && OffHeapLongArray.get(addr, DIRTY) != 1) {
                OffHeapLongArray.set(addr, DIRTY, 1);
                space.notifyUpdate(index);
            }
        } finally {
            space.unlockByIndex(index);
        }
    }

    @Override
    public final long previousOrEqual(final long key) {
        space.lockByIndex(index);
        try {
            final long addr = space.addrByIndex(index);
            long resultKey;
            final long result = internal_previousOrEqual_index(addr, key);
            if (result != -1) {
                resultKey = key(addr, result);
            } else {
                resultKey = Constants.NULL_LONG;
            }
            return resultKey;
        } finally {
            space.unlockByIndex(index);
        }
    }

    @Override
    public final long previous(long key) {
        space.lockByIndex(index);
        try {
            final long addr = space.addrByIndex(index);
            long resultKey;
            final long result = internal_previous_index(addr, key);
            if (result != -1) {
                resultKey = key(addr, result);
            } else {
                resultKey = Constants.NULL_LONG;
            }
            return resultKey;
        } finally {
            space.unlockByIndex(index);
        }
    }

    @Override
    public long next(long key) {
        long resultKey;
        space.lockByIndex(index);
        try {
            final long addr = space.addrByIndex(index);
            long result = internal_previousOrEqual_index(addr, key);
            if (result != -1) {
                result = internal_next_index(addr, result);
            }
            if (result != -1) {
                resultKey = key(addr, result);
            } else {
                resultKey = Constants.NULL_LONG;
            }
        } finally {
            space.unlockByIndex(index);
        }
        return resultKey;
    }

    @Override
    public final void insert(final long insertKey) {
        space.lockByIndex(index);
        try {
            if (internal_insert(space.addrByIndex(index), insertKey)) {
                internal_set_dirty(space.addrByIndex(index));
            }
        } finally {
            space.unlockByIndex(index);
        }
    }

    @Override
    public final void unsafe_insert(final long p_key) {
        internal_insert(space.addrByIndex(index), p_key);
    }

    @Override
    public final byte chunkType() {
        return ChunkType.TIME_TREE_CHUNK;
    }

    @Override
    public final void clearAt(long max) {
        space.lockByIndex(index);
        try {
            final long previous_addr = space.addrByIndex(index);
            final long previousCapacity = OffHeapLongArray.get(previous_addr, CAPACITY);
            final long previousSize = OffHeapLongArray.get(previous_addr, SIZE);

            //alocate the new segment
            long new_addr = OffHeapLongArray.allocate(OFFSET + (previousCapacity * ELEM_SIZE));
            OffHeapLongArray.set(new_addr, MAGIC, OffHeapLongArray.get(previous_addr, MAGIC) + 1);
            OffHeapLongArray.set(new_addr, CAPACITY, previousCapacity);
            OffHeapLongArray.set(new_addr, SIZE, 0);
            OffHeapLongArray.set(new_addr, DIRTY, 0);
            OffHeapLongArray.set(new_addr, HEAD, -1);

            for (long i = 0; i < previousSize; i++) {
                long currentVal = key(previous_addr, i);
                if (currentVal < max) {
                    internal_insert(new_addr, currentVal);
                }
            }
            space.setAddrByIndex(index, new_addr);
            OffHeapLongArray.free(previous_addr);
            internal_set_dirty(new_addr);
        } finally {
            space.unlockByIndex(index);
        }
    }

    private long reallocate(final long addr, final long previousCapacity, final long newCapacity) {
        if (previousCapacity < newCapacity) {
            final long new_addr = OffHeapLongArray.reallocate(addr, OFFSET + (newCapacity * ELEM_SIZE));
            OffHeapLongArray.set(new_addr, CAPACITY, newCapacity);
            space.setAddrByIndex(index, new_addr);
            return new_addr;
        } else {
            return addr;
        }
    }

    private static long key(final long addr, final long index) {
        if (index == -1) {
            return -1;
        }
        return OffHeapLongArray.get(addr, OFFSET + (index * 5));
    }

    private static void setKey(final long addr, final long index, final long insertKey) {
        OffHeapLongArray.set(addr, OFFSET + (index * 5), insertKey);
    }


    private static boolean color(final long addr, final long index) {
        if (index == -1) {
            return true;
        }
        return OffHeapLongArray.get(addr, OFFSET + (index * 5) + 1) == 1;
    }

    private static void setColor(final long addr, final long index, boolean insertColor) {
        OffHeapLongArray.set(addr, OFFSET + (index * 5) + 1, insertColor ? 1 : 0);
    }

    private static long left(final long addr, final long index) {
        if (index == -1) {
            return -1;
        }
        return OffHeapLongArray.get(addr, OFFSET + (index * 5) + 2);
    }

    private static void setLeft(final long addr, final long index, final long insertLeft) {
        OffHeapLongArray.set(addr, OFFSET + (index * 5) + 2, insertLeft);
    }

    private static long right(final long addr, final long index) {
        if (index == -1) {
            return -1;
        }
        return OffHeapLongArray.get(addr, OFFSET + (index * 5) + 3);
    }

    private static void setRight(final long addr, final long index, final long insertRight) {
        OffHeapLongArray.set(addr, OFFSET + +(index * 5) + 3, insertRight);
    }

    private static long parent(final long addr, final long index) {
        if (index == -1) {
            return -1;
        }
        return OffHeapLongArray.get(addr, OFFSET + (index * 5) + 4);
    }

    private static void setParent(final long addr, final long index, final long insertParent) {
        OffHeapLongArray.set(addr, OFFSET + (index * 5) + 4, insertParent);
    }

    private static long grandParent(final long addr, final long index) {
        if (index == -1) {
            return -1;
        }
        final long selfParent = parent(addr, index);
        if (selfParent != -1) {
            return parent(addr, selfParent);
        } else {
            return -1;
        }
    }

    private static long sibling(final long addr, final long index) {
        final long selfParent = parent(addr, index);
        if (selfParent == -1) {
            return -1;
        } else {
            if (index == left(addr, selfParent)) {
                return right(addr, selfParent);
            } else {
                return left(addr, selfParent);
            }
        }
    }

    private static long uncle(final long addr, final long index) {
        final long selfParent = parent(addr, index);
        if (selfParent != -1) {
            return sibling(addr, selfParent);
        } else {
            return -1;
        }
    }

    private static long previous(final long addr, final long index) {
        long p = index;
        if (left(addr, p) != -1) {
            p = left(addr, p);
            while (right(addr, p) != -1) {
                p = right(addr, p);
            }
            return p;
        } else {
            if (parent(addr, p) != -1) {
                if (p == right(addr, parent(addr, p))) {
                    return parent(addr, p);
                } else {
                    while (parent(addr, p) != -1 && p == left(addr, parent(addr, p))) {
                        p = parent(addr, p);
                    }
                    return parent(addr, p);
                }
            } else {
                return -1;
            }
        }
    }

    private static void rotateLeft(final long addr, final long n) {
        long r = right(addr, n);
        replaceNode(addr, n, r);
        setRight(addr, n, left(addr, r));
        if (left(addr, r) != -1) {
            setParent(addr, left(addr, r), n);
        }
        setLeft(addr, r, n);
        setParent(addr, n, r);
    }

    private static void rotateRight(final long addr, final long n) {
        long l = left(addr, n);
        replaceNode(addr, n, l);
        setLeft(addr, n, right(addr, l));
        if (right(addr, l) != -1) {
            setParent(addr, right(addr, l), n);
        }
        setRight(addr, l, n);
        setParent(addr, n, l);
    }

    private static void replaceNode(final long addr, final long oldn, final long newn) {
        final long parentOldN = parent(addr, oldn);
        if (parentOldN == -1) {
            OffHeapLongArray.set(addr, HEAD, newn);
        } else {
            if (oldn == left(addr, parentOldN)) {
                setLeft(addr, parentOldN, newn);
            } else {
                setRight(addr, parentOldN, newn);
            }
        }
        if (newn != -1) {
            setParent(addr, newn, parentOldN);
        }
    }

    private static void insertCase1(final long addr, final long n) {
        if (parent(addr, n) == -1) {
            setColor(addr, n, true);
        } else {
            insertCase2(addr, n);
        }
    }

    private static void insertCase2(final long addr, final long n) {
        if (!color(addr, parent(addr, n))) {
            insertCase3(addr, n);
        }
    }

    private static void insertCase3(final long addr, final long n) {
        final long uncleN = uncle(addr, n);
        if (!color(addr, uncleN)) {
            setColor(addr, parent(addr, n), true);
            setColor(addr, uncleN, true);
            final long grandParentN = grandParent(addr, n);
            setColor(addr, grandParentN, false);
            insertCase1(addr, grandParentN);
        } else {
            insertCase4(addr, n);
        }
    }

    private static void insertCase4(final long addr, final long n_n) {
        long n = n_n;
        if (n == right(addr, parent(addr, n)) && parent(addr, n) == left(addr, grandParent(addr, n))) {
            rotateLeft(addr, parent(addr, n));
            n = left(addr, n);
        } else {
            if (n == left(addr, parent(addr, n)) && parent(addr, n) == right(addr, grandParent(addr, n))) {
                rotateRight(addr, parent(addr, n));
                n = right(addr, n);
            }
        }
        insertCase5(addr, n);
    }

    private static void insertCase5(final long addr, final long n) {
        setColor(addr, parent(addr, n), true);
        setColor(addr, grandParent(addr, n), false);
        if (n == left(addr, parent(addr, n)) && parent(addr, n) == left(addr, grandParent(addr, n))) {
            rotateRight(addr, grandParent(addr, n));
        } else {
            rotateLeft(addr, grandParent(addr, n));
        }
    }

    private static long internal_previous_index(final long addr, final long p_key) {
        long p = OffHeapLongArray.get(addr, HEAD);
        if (p == -1) {
            return p;
        }
        while (p != -1) {
            if (p_key > key(addr, p)) {
                if (right(addr, p) != -1) {
                    p = right(addr, p);
                } else {
                    return p;
                }
            } else {
                if (left(addr, p) != -1) {
                    p = left(addr, p);
                } else {
                    long parent = parent(addr, p);
                    long ch = p;
                    while (parent != -1 && ch == left(addr, parent)) {
                        ch = parent;
                        parent = parent(addr, parent);
                    }
                    return parent;
                }
            }
        }
        return -1;
    }

    private static long internal_previousOrEqual_index(final long addr, final long p_key) {
        long p = OffHeapLongArray.get(addr, HEAD);
        if (p == -1) {
            return p;
        }
        while (p != -1) {
            if (p_key == key(addr, p)) {
                return p;
            }
            if (p_key > key(addr, p)) {
                if (right(addr, p) != -1) {
                    p = right(addr, p);
                } else {
                    return p;
                }
            } else {
                if (left(addr, p) != -1) {
                    p = left(addr, p);
                } else {
                    long parent = parent(addr, p);
                    long ch = p;
                    while (parent != -1 && ch == left(addr, parent)) {
                        ch = parent;
                        parent = parent(addr, parent);
                    }
                    return parent;
                }
            }
        }
        return -1;
    }

    private static long internal_next_index(final long addr, final long p_key) {
        long p = p_key;
        if (right(addr, p) != -1) {
            p = right(addr, p);
            while (left(addr, p) != -1) {
                p = left(addr, p);
            }
            return p;
        } else {
            if (parent(addr, p) != -1) {
                if (p == left(addr, parent(addr, p))) {
                    return parent(addr, p);
                } else {
                    while (parent(addr, p) != -1 && p == right(addr, parent(addr, p))) {
                        p = parent(addr, p);
                    }
                    return parent(addr, p);
                }
            } else {
                return -1;
            }
        }
    }

    private boolean internal_insert(long addr, final long insertLey) {
        long size = OffHeapLongArray.get(addr, SIZE);
        long capacity = OffHeapLongArray.get(addr, CAPACITY);
        if (capacity == size) {
            long nextCapacity = size;
            if (nextCapacity == 0) {
                nextCapacity = Constants.MAP_INITIAL_CAPACITY;
            } else {
                nextCapacity = nextCapacity * 2;
            }
            addr = reallocate(addr, capacity, nextCapacity);
        }
        if (size == 0) {
            setKey(addr, size, insertLey);
            setColor(addr, size, false);
            setLeft(addr, size, -1);
            setRight(addr, size, -1);
            setParent(addr, size, -1);
            OffHeapLongArray.set(addr, HEAD, size);
            OffHeapLongArray.set(addr, SIZE, 1);
        } else {
            long n = OffHeapLongArray.get(addr, HEAD);
            while (true) {
                if (insertLey == key(addr, n)) {
                    return false;
                } else if (insertLey < key(addr, n)) {
                    if (left(addr, n) == -1) {
                        setKey(addr, size, insertLey);
                        setColor(addr, size, false);
                        setLeft(addr, size, -1);
                        setRight(addr, size, -1);
                        setParent(addr, size, -1);
                        setLeft(addr, n, size);
                        OffHeapLongArray.set(addr, SIZE, size + 1);
                        break;
                    } else {
                        n = left(addr, n);
                    }
                } else {
                    if (right(addr, n) == -1) {
                        setKey(addr, size, insertLey);
                        setColor(addr, size, false);
                        setLeft(addr, size, -1);
                        setRight(addr, size, -1);
                        setParent(addr, size, -1);
                        setRight(addr, n, size);
                        OffHeapLongArray.set(addr, SIZE, size + 1);
                        break;
                    } else {
                        n = right(addr, n);
                    }
                }
            }
            setParent(addr, size, n);
        }
        insertCase1(addr, size);
        return true;
    }

    private void internal_set_dirty(final long addr) {
        OffHeapLongArray.set(addr, MAGIC, OffHeapLongArray.get(addr, MAGIC) + 1);
        if (space != null && OffHeapLongArray.get(addr, DIRTY) != 1) {
            OffHeapLongArray.set(addr, DIRTY, 1);
            space.notifyUpdate(index);
        }
    }

}
