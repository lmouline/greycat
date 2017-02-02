/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
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
package org.mwg.ml.nn;

import org.mwg.Type;
import org.mwg.ml.common.matrix.VolatileDMatrix;
import org.mwg.struct.DMatrix;
import org.mwg.struct.ENode;

/**
 * Created by assaad on 27/01/2017.
 */
public class ExMatrix implements DMatrix {

    private static String DW_KEY="-dw";
    private static String STEPCACHE_KEY="-sc";


    private DMatrix w;
    private DMatrix dw;
    private DMatrix stepCache;

    public ExMatrix (ENode node, String attribute){
        if(node!=null){
            w= (DMatrix) node.getOrCreate(attribute, Type.DMATRIX);
            dw=(DMatrix) node.getOrCreate(attribute+DW_KEY, Type.DMATRIX);
            stepCache=(DMatrix) node.getOrCreate(attribute+STEPCACHE_KEY, Type.DMATRIX);
        }
    }


    @Override
    public DMatrix init(int rows, int columns) {
        if(w==null){
            w= VolatileDMatrix.empty(rows,columns);
            dw=VolatileDMatrix.empty(rows,columns);
            stepCache=VolatileDMatrix.empty(rows,columns);
        }
        else {
            w.init(rows,columns);
            dw.init(rows,columns);
            stepCache.init(rows,columns);
        }
        return this;
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
    public DMatrix fillWithRandom(double min, double max, long seed) {
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
