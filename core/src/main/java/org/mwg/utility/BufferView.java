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
package org.mwg.utility;

import org.mwg.struct.Buffer;
import org.mwg.struct.BufferIterator;

class BufferView implements Buffer {

    private Buffer _origin;

    private long _initPos;

    private long _endPos;

    BufferView(Buffer p_origin, long p_initPos, long p_endPos) {
        this._origin = p_origin;
        this._initPos = p_initPos;
        this._endPos = p_endPos;
    }

    @Override
    public final void write(byte b) {
        throw new RuntimeException("Write operation forbidden during iteration");
    }

    @Override
    public final void writeAll(byte[] bytes) {
        throw new RuntimeException("Write operation forbidden during iteration");
    }

    @Override
    public final byte read(long position) {
        if (_initPos + position > _endPos) {
            throw new ArrayIndexOutOfBoundsException("" + position);
        }
        return _origin.read(_initPos + position);
    }

    @Override
    public final byte[] data() {
        return _origin.slice(_initPos, _endPos);
    }

    @Override
    public final long length() {
        return _endPos - _initPos + 1;
    }

    @Override
    public final void free() {
        throw new RuntimeException("Free operation forbidden during iteration");
    }

    @Override
    public final BufferIterator iterator() {
        throw new RuntimeException("iterator creation forbidden forbidden during iteration");
    }

    @Override
    public void removeLast() {
        throw new RuntimeException("Write operation forbidden during iteration");
    }

    @Override
    public byte[] slice(long initPos, long endPos) {
        throw new RuntimeException("Write operation forbidden during iteration");
    }
}
