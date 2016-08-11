package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.core.CoreConstants;
import org.mwg.chunk.TimeTreeChunk;
import org.mwg.chunk.TreeWalker;
import org.mwg.utility.Base64;
import org.mwg.chunk.ChunkType;
import org.mwg.struct.Buffer;

class HeapTimeTreeChunk implements TimeTreeChunk {

    //constants definition
    private static final int META_SIZE = 3;

    private final long _index;
    private final HeapChunkSpace _space;

    private int _root = -1;
    private int[] _back_meta;
    private long[] _k;
    private boolean[] _colors;

    private volatile long _magic;
    private volatile int _size = 0;

    private boolean _dirty;

    HeapTimeTreeChunk(final HeapChunkSpace p_space, final long p_index) {
        _space = p_space;
        _index = p_index;
        _magic = 0;
        _dirty = false;
    }

    @Override
    public final long world() {
        return _space.worldByIndex(_index);
    }

    @Override
    public final long time() {
        return _space.timeByIndex(_index);
    }

    @Override
    public final long id() {
        return _space.idByIndex(_index);
    }

    @Override
    public final long size() {
        return _size;
    }

    @Override
    public synchronized final void range(final long startKey, final long endKey, final long maxElements, final TreeWalker walker) {
        //lock and load fromVar main memory
        int nbElements = 0;
        int indexEnd = internal_previousOrEqual_index(endKey);
        while (indexEnd != -1 && key(indexEnd) >= startKey && nbElements < maxElements) {
            walker.elem(key(indexEnd));
            nbElements++;
            indexEnd = previous(indexEnd);
        }
    }

    @Override
    public synchronized final void save(Buffer buffer) {
        Base64.encodeLongToBuffer(_size, buffer);
        buffer.write(CoreConstants.CHUNK_SEP);
        boolean isFirst = true;
        for (int i = 0; i < _size; i++) {
            if (!isFirst) {
                buffer.write(CoreConstants.CHUNK_SUB_SEP);
            } else {
                isFirst = false;
            }
            Base64.encodeLongToBuffer(this._k[i], buffer);
        }
        _dirty = false;
    }

    @Override
    public synchronized final void load(Buffer buffer) {
        if (buffer == null || buffer.length() == 0) {
            return;
        }
        final boolean initial = _k == null;
        boolean isDirty = false;
        long cursor = 0;
        long previous = 0;
        long payloadSize = buffer.length();
        while (cursor < payloadSize) {
            byte current = buffer.read(cursor);
            if (current == CoreConstants.CHUNK_SUB_SEP) {
                isDirty = isDirty || internal_insert(Base64.decodeToLongWithBounds(buffer, previous, cursor));
                previous = cursor + 1;
            } else if (current == CoreConstants.CHUNK_SEP) {
                reallocate((int) Base64.decodeToLongWithBounds(buffer, previous, cursor));
                previous = cursor + 1;
            }
            cursor++;
        }
        isDirty = isDirty || internal_insert(Base64.decodeToLongWithBounds(buffer, previous, cursor));
        if (isDirty && !initial && !_dirty) {
            _dirty = true;
            if (_space != null) {
                _space.notifyUpdate(_index);
            }
        }

    }

    @Override
    public final long index() {
        return _index;
    }

    @Override
    public synchronized final long previousOrEqual(long key) {
        //lock and load fromVar main memory
        long resultKey;
        int result = internal_previousOrEqual_index(key);

        if (result != -1) {
            resultKey = key(result);
        } else {
            resultKey = CoreConstants.NULL_LONG;
        }
        return resultKey;
    }

    @Override
    public final long magic() {
        return this._magic;
    }

    @Override
    public synchronized final void insert(final long p_key) {
        if (internal_insert(p_key)) {
            internal_set_dirty();
        }
    }

    @Override
    public synchronized final void unsafe_insert(final long p_key) {
        internal_insert(p_key);
    }

    @Override
    public final byte chunkType() {
        return ChunkType.TIME_TREE_CHUNK;
    }

    @Override
    public synchronized final void clearAt(long max) {
        //lock and load fromVar main memory
        long[] previousValue = _k;
        //reset the state
        _k = new long[_k.length];
        _back_meta = new int[_k.length * META_SIZE];
        _colors = new boolean[_k.length];
        _root = -1;
        int _previousSize = _size;
        _size = 0;
        for (int i = 0; i < _previousSize; i++) {
            if (previousValue[i] != CoreConstants.NULL_LONG && previousValue[i] < max) {
                internal_insert(previousValue[i]);
            }
        }
        //dirty
        internal_set_dirty();
    }

    private void reallocate(int newCapacity) {
        long[] new_back_kv = new long[newCapacity];
        if (_k != null) {
            System.arraycopy(_k, 0, new_back_kv, 0, _size);
        }
        boolean[] new_back_colors = new boolean[newCapacity];
        if (_colors != null) {
            System.arraycopy(_colors, 0, new_back_colors, 0, _size);
            for (int i = _size; i < newCapacity; i++) {
                new_back_colors[i] = false;
            }
        }
        int[] new_back_meta = new int[newCapacity * META_SIZE];
        if (_back_meta != null) {
            System.arraycopy(_back_meta, 0, new_back_meta, 0, _size * META_SIZE);
            for (int i = _size * META_SIZE; i < newCapacity * META_SIZE; i++) {
                new_back_meta[i] = -1;
            }
        }
        _back_meta = new_back_meta;
        _k = new_back_kv;
        _colors = new_back_colors;
    }

    private long key(int p_currentIndex) {
        if (p_currentIndex == -1) {
            return -1;
        }
        return _k[p_currentIndex];
    }

    private void setKey(int p_currentIndex, long p_paramIndex) {
        _k[p_currentIndex] = p_paramIndex;
    }

    /*
    protected final long value(int p_currentIndex) {
        if (p_currentIndex == -1) {
            return -1;
        }
        return _k[(p_currentIndex) + 1];
    }

    private void setValue(int p_currentIndex, long p_paramIndex) {
        _k[(p_currentIndex) + 1] = p_paramIndex;
    }*/

    private int left(int p_currentIndex) {
        if (p_currentIndex == -1) {
            return -1;
        }
        return _back_meta[p_currentIndex * META_SIZE];
    }

    private void setLeft(int p_currentIndex, int p_paramIndex) {
        _back_meta[p_currentIndex * META_SIZE] = p_paramIndex;
    }

    private int right(int p_currentIndex) {
        if (p_currentIndex == -1) {
            return -1;
        }
        return _back_meta[(p_currentIndex * META_SIZE) + 1];
    }

    private void setRight(int p_currentIndex, int p_paramIndex) {
        _back_meta[(p_currentIndex * META_SIZE) + 1] = p_paramIndex;
    }

    private int parent(int p_currentIndex) {
        if (p_currentIndex == -1) {
            return -1;
        }
        return _back_meta[(p_currentIndex * META_SIZE) + 2];
    }

    private void setParent(int p_currentIndex, int p_paramIndex) {
        _back_meta[(p_currentIndex * META_SIZE) + 2] = p_paramIndex;
    }

    private boolean color(int p_currentIndex) {
        if (p_currentIndex == -1) {
            return true;
        }
        return _colors[p_currentIndex];
    }

    private void setColor(int p_currentIndex, boolean p_paramIndex) {
        _colors[p_currentIndex] = p_paramIndex;
    }

    private int grandParent(int p_currentIndex) {
        if (p_currentIndex == -1) {
            return -1;
        }
        if (parent(p_currentIndex) != -1) {
            return parent(parent(p_currentIndex));
        } else {
            return -1;
        }
    }

    private int sibling(int p_currentIndex) {
        if (parent(p_currentIndex) == -1) {
            return -1;
        } else {
            if (p_currentIndex == left(parent(p_currentIndex))) {
                return right(parent(p_currentIndex));
            } else {
                return left(parent(p_currentIndex));
            }
        }
    }

    private int uncle(int p_currentIndex) {
        if (parent(p_currentIndex) != -1) {
            return sibling(parent(p_currentIndex));
        } else {
            return -1;
        }
    }

    private int previous(int p_index) {
        int p = p_index;
        if (left(p) != -1) {
            p = left(p);
            while (right(p) != -1) {
                p = right(p);
            }
            return p;
        } else {
            if (parent(p) != -1) {
                if (p == right(parent(p))) {
                    return parent(p);
                } else {
                    while (parent(p) != -1 && p == left(parent(p))) {
                        p = parent(p);
                    }
                    return parent(p);
                }
            } else {
                return -1;
            }
        }
    }

    /*
    private int next(int p_index) {
        int p = p_index;
        if (right(p) != -1) {
            p = right(p);
            while (left(p) != -1) {
                p = left(p);
            }
            return p;
        } else {
            if (parent(p) != -1) {
                if (p == left(parent(p))) {
                    return parent(p);
                } else {
                    while (parent(p) != -1 && p == right(parent(p))) {
                        p = parent(p);
                    }
                    return parent(p);
                }

            } else {
                return -1;
            }
        }
    }

    private long lookup(long p_key) {
        int n = _root;
        if (n == -1) {
            return CoreConstants.NULL_LONG;
        }
        while (n != -1) {
            if (p_key == key(n)) {
                return key(n);
            } else {
                if (p_key < key(n)) {
                    n = left(n);
                } else {
                    n = right(n);
                }
            }
        }
        return n;
    }
    */


    private int internal_previousOrEqual_index(long p_key) {
        int p = _root;
        if (p == -1) {
            return p;
        }
        while (p != -1) {
            if (p_key == key(p)) {
                return p;
            }
            if (p_key > key(p)) {
                if (right(p) != -1) {
                    p = right(p);
                } else {
                    return p;
                }
            } else {
                if (left(p) != -1) {
                    p = left(p);
                } else {
                    int parent = parent(p);
                    long ch = p;
                    while (parent != -1 && ch == left(parent)) {
                        ch = parent;
                        parent = parent(parent);
                    }
                    return parent;
                }
            }
        }
        return -1;
    }

    private void rotateLeft(int n) {
        int r = right(n);
        replaceNode(n, r);
        setRight(n, left(r));
        if (left(r) != -1) {
            setParent(left(r), n);
        }
        setLeft(r, n);
        setParent(n, r);
    }

    private void rotateRight(int n) {
        int l = left(n);
        replaceNode(n, l);
        setLeft(n, right(l));
        if (right(l) != -1) {
            setParent(right(l), n);
        }
        setRight(l, n);
        setParent(n, l);
    }

    private void replaceNode(int oldn, int newn) {
        if (parent(oldn) == -1) {
            _root = newn;
        } else {
            if (oldn == left(parent(oldn))) {
                setLeft(parent(oldn), newn);
            } else {
                setRight(parent(oldn), newn);
            }
        }
        if (newn != -1) {
            setParent(newn, parent(oldn));
        }
    }

    private void insertCase1(int n) {
        if (parent(n) == -1) {
            setColor(n, true);
        } else {
            insertCase2(n);
        }
    }

    private void insertCase2(int n) {
        if (!color(parent(n))) {
            insertCase3(n);
        }
    }

    private void insertCase3(int n) {
        if (!color(uncle(n))) {
            setColor(parent(n), true);
            setColor(uncle(n), true);
            setColor(grandParent(n), false);
            insertCase1(grandParent(n));
        } else {
            insertCase4(n);
        }
    }

    private void insertCase4(int n_n) {
        int n = n_n;
        if (n == right(parent(n)) && parent(n) == left(grandParent(n))) {
            rotateLeft(parent(n));
            n = left(n);
        } else {
            if (n == left(parent(n)) && parent(n) == right(grandParent(n))) {
                rotateRight(parent(n));
                n = right(n);
            }
        }
        insertCase5(n);
    }

    private void insertCase5(int n) {
        setColor(parent(n), true);
        setColor(grandParent(n), false);
        if (n == left(parent(n)) && parent(n) == left(grandParent(n))) {
            rotateRight(grandParent(n));
        } else {
            rotateLeft(grandParent(n));
        }
    }

    private boolean internal_insert(long p_key) {
        if (_k == null || _k.length == _size) {
            int length = _size;
            if (length == 0) {
                length = Constants.MAP_INITIAL_CAPACITY;
            } else {
                length = length * 2;
            }
            reallocate(length);
        }
        int newIndex = _size;
        if (newIndex == 0) {
            setKey(newIndex, p_key);
            setColor(newIndex, false);
            setLeft(newIndex, -1);
            setRight(newIndex, -1);
            setParent(newIndex, -1);
            _root = newIndex;
            _size = 1;
        } else {
            int n = _root;
            while (true) {
                if (p_key == key(n)) {
                    return false;
                } else if (p_key < key(n)) {
                    if (left(n) == -1) {
                        setKey(newIndex, p_key);
                        setColor(newIndex, false);
                        setLeft(newIndex, -1);
                        setRight(newIndex, -1);
                        setParent(newIndex, -1);
                        setLeft(n, newIndex);
                        _size++;
                        break;
                    } else {
                        n = left(n);
                    }
                } else {
                    if (right(n) == -1) {
                        setKey(newIndex, p_key);
                        setColor(newIndex, false);
                        setLeft(newIndex, -1);
                        setRight(newIndex, -1);
                        setParent(newIndex, -1);
                        setRight(n, newIndex);
                        _size++;
                        break;
                    } else {
                        n = right(n);
                    }
                }
            }
            setParent(newIndex, n);
        }
        insertCase1(newIndex);
        return true;
    }

    private void internal_set_dirty() {
        _magic = _magic + 1;
        if (_space != null && !_dirty) {
            _dirty = true;
            _space.notifyUpdate(_index);
        }
    }

     /*
    public void delete(long key) {
        TreeNode n = lookup(key);
        if (n == null) {
            return;
        } else {
            _size--;
            if (n.getLeft() != null && n.getRight() != null) {
                // Copy domainKey/value fromVar predecessor and done delete it instead
                TreeNode pred = n.getLeft();
                while (pred.getRight() != null) {
                    pred = pred.getRight();
                }
                n.key = pred.key;
                n = pred;
            }
            TreeNode child;
            if (n.getRight() == null) {
                child = n.getLeft();
            } else {
                child = n.getRight();
            }
            if (nodeColor(n) == true) {
                n.color = nodeColor(child);
                deleteCase1(n);
            }
            replaceNode(n, child);
        }
    }

    private void deleteCase1(TreeNode n) {
        if (n.getParent() == null) {
            return;
        } else {
            deleteCase2(n);
        }
    }

    private void deleteCase2(TreeNode n) {
        if (nodeColor(n.sibling()) == false) {
            n.getParent().color = false;
            n.sibling().color = true;
            if (n == n.getParent().getLeft()) {
                rotateLeft(n.getParent());
            } else {
                rotateRight(n.getParent());
            }
        }
        deleteCase3(n);
    }

    private void deleteCase3(TreeNode n) {
        if (nodeColor(n.getParent()) == true && nodeColor(n.sibling()) == true && nodeColor(n.sibling().getLeft()) == true && nodeColor(n.sibling().getRight()) == true) {
            n.sibling().color = false;
            deleteCase1(n.getParent());
        } else {
            deleteCase4(n);
        }
    }

    private void deleteCase4(TreeNode n) {
        if (nodeColor(n.getParent()) == false && nodeColor(n.sibling()) == true && nodeColor(n.sibling().getLeft()) == true && nodeColor(n.sibling().getRight()) == true) {
            n.sibling().color = false;
            n.getParent().color = true;
        } else {
            deleteCase5(n);
        }
    }

    private void deleteCase5(TreeNode n) {
        if (n == n.getParent().getLeft() && nodeColor(n.sibling()) == true && nodeColor(n.sibling().getLeft()) == false && nodeColor(n.sibling().getRight()) == true) {
            n.sibling().color = false;
            n.sibling().getLeft().color = true;
            rotateRight(n.sibling());
        } else if (n == n.getParent().getRight() && nodeColor(n.sibling()) == true && nodeColor(n.sibling().getRight()) == false && nodeColor(n.sibling().getLeft()) == true) {
            n.sibling().color = false;
            n.sibling().getRight().color = true;
            rotateLeft(n.sibling());
        }
        deleteCase6(n);
    }

    private void deleteCase6(TreeNode n) {
        n.sibling().color = nodeColor(n.getParent());
        n.getParent().color = true;
        if (n == n.getParent().getLeft()) {
            n.sibling().getRight().color = true;
            rotateLeft(n.getParent());
        } else {
            n.sibling().getLeft().color = true;
            rotateRight(n.getParent());
        }
    }*/

}
