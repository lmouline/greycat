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

import greycat.struct.LMatrix;

public class LMatrixProxy implements LMatrix {
    
    @Override
    public LMatrix init(int rows, int columns) {
        return null;
    }

    @Override
    public LMatrix fill(long value) {
        return null;
    }

    @Override
    public LMatrix fillWith(long[] values) {
        return null;
    }

    @Override
    public LMatrix fillWithRandom(long min, long max, long seed) {
        return null;
    }

    @Override
    public int rows() {
        return 0;
    }

    @Override
    public int columns() {
        return 0;
    }

    @Override
    public long[] column(int i) {
        return new long[0];
    }

    @Override
    public long get(int rowIndex, int columnIndex) {
        return 0;
    }

    @Override
    public LMatrix set(int rowIndex, int columnIndex, long value) {
        return null;
    }

    @Override
    public LMatrix add(int rowIndex, int columnIndex, long value) {
        return null;
    }

    @Override
    public LMatrix appendColumn(long[] newColumn) {
        return null;
    }

    @Override
    public long[] data() {
        return new long[0];
    }

    @Override
    public int leadingDimension() {
        return 0;
    }

    @Override
    public long unsafeGet(int index) {
        return 0;
    }

    @Override
    public LMatrix unsafeSet(int index, long value) {
        return null;
    }
}
