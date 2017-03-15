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

import greycat.struct.DMatrix;

public class DMatrixProxy implements DMatrix {

    @Override
    public DMatrix init(int rows, int columns) {
        return null;
    }

    @Override
    public DMatrix fill(double value) {
        return null;
    }

    @Override
    public DMatrix fillWith(double[] values) {
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
    public int length() {
        return 0;
    }

    @Override
    public double[] column(int i) {
        return new double[0];
    }

    @Override
    public double get(int rowIndex, int columnIndex) {
        return 0;
    }

    @Override
    public DMatrix set(int rowIndex, int columnIndex, double value) {
        return null;
    }

    @Override
    public DMatrix add(int rowIndex, int columnIndex, double value) {
        return null;
    }

    @Override
    public DMatrix appendColumn(double[] newColumn) {
        return null;
    }

    @Override
    public double[] data() {
        return new double[0];
    }

    @Override
    public int leadingDimension() {
        return 0;
    }

    @Override
    public double unsafeGet(int index) {
        return 0;
    }

    @Override
    public DMatrix unsafeSet(int index, double value) {
        return null;
    }
}
