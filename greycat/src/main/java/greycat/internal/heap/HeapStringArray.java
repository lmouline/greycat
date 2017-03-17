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
package greycat.internal.heap;

import greycat.Constants;
import greycat.struct.Buffer;
import greycat.struct.StringArray;
import greycat.utility.Base64;

class HeapStringArray implements StringArray {

    private String[] _backend = null;
    private final HeapContainer _parent;

    public HeapStringArray(final HeapContainer parent) {
        this._parent = parent;
    }

    @Override
    public synchronized String get(int index) {
        if (_backend != null) {
            if (index >= _backend.length) {
                throw new RuntimeException("Array Out of Bounds");
            }
            return _backend[index];
        }
        return null;
    }

    @Override
    public synchronized void set(int index, String value) {
        if (_backend == null || index >= _backend.length) {
            throw new RuntimeException("allocate first!");
        } else {
            _backend[index] = value;
            _parent.declareDirty();
        }
    }

    @Override
    public synchronized int size() {
        if (_backend != null) {
            return _backend.length;
        }
        return 0;
    }

    @Override
    public synchronized void init(int size) {
        _backend = new String[size];
        _parent.declareDirty();
    }

    @Override
    public synchronized final void initWith(final String[] values) {
        _backend = new String[values.length];
        System.arraycopy(values, 0, _backend, 0, values.length);
    }

    /* TODO merge */
    public final long load(final Buffer buffer, final long offset, final long max) {
        long cursor = offset;
        byte current = buffer.read(cursor);
        boolean isFirst = true;
        long previous = offset;
        int elemIndex = 0;
        while (cursor < max && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP && current != Constants.CHUNK_ESEP) {
            if (current == Constants.CHUNK_VAL_SEP) {
                if (isFirst) {
                    _backend = new String[Base64.decodeToIntWithBounds(buffer, previous, cursor)];
                    isFirst = false;
                } else {
                    _backend[elemIndex] = Base64.decodeToStringWithBounds(buffer, previous, cursor);
                    elemIndex++;
                }
                previous = cursor + 1;
            }
            cursor++;
            if (cursor < max) {
                current = buffer.read(cursor);
            }
        }
        if (isFirst) {
            _backend = new String[Base64.decodeToIntWithBounds(buffer, previous, cursor)];
        } else {
            _backend[elemIndex] = Base64.decodeToStringWithBounds(buffer, previous, cursor);
        }
        return cursor;
    }

    public final HeapStringArray cloneFor(HeapContainer target) {
        HeapStringArray cloned = new HeapStringArray(target);
        cloned.initWith(_backend);
        return cloned;
    }

}
