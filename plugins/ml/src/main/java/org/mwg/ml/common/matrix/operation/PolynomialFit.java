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
package org.mwg.ml.common.matrix.operation;


import org.mwg.ml.common.matrix.MatrixOps;
import org.mwg.ml.common.matrix.TransposeType;
import org.mwg.ml.common.matrix.VolatileDMatrix;
import org.mwg.struct.DMatrix;

public class PolynomialFit {

    private DMatrix coef;
    private int degree = 0;

    public PolynomialFit(int degree) {
        this.degree = degree;
    }

    public double[] getCoef() {
        return coef.data();
    }

    public void fit(double samplePoints[], double[] observations) {
        DMatrix y = VolatileDMatrix.wrap(observations, observations.length, 1);
        DMatrix a = VolatileDMatrix.empty(y.rows(), degree + 1);
        // cset up the A matrix
        for (int i = 0; i < observations.length; i++) {
            double obs = 1;
            for (int j = 0; j < degree + 1; j++) {
                a.set(i, j, obs);
                obs *= samplePoints[i];
            }
        }
        // processValues the A matrix and see if it failed
        coef = MatrixOps.defaultEngine().solveQR(a, y, true, TransposeType.NOTRANSPOSE);
    }

    public static double extrapolate(double time, double[] weights) {
        double result = 0;
        double power = 1;
        for (int j = 0; j < weights.length; j++) {
            result += weights[j] * power;
            power = power * time;
        }
        return result;
    }

}