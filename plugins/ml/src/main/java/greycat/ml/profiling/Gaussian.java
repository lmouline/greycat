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
package greycat.ml.profiling;

import greycat.Constants;
import greycat.Node;
import greycat.Type;
import greycat.ml.math.Gaussian1D;

/**
 * Created by assaad on 20/02/2017.
 */
public class Gaussian {
    public static final String MIN = "profile_min";
    public static final String MAX = "profile_max";
    public static final String AVG = "profile_avg";
    public static final String COV = "profile_cov";
    public static final String STD = "profile_std";
    public static final String SUM = "profile_sum";
    public static final String SUMSQ = "profile_sumsq";
    public static final String TOTAL = "profile_total";
    public static final String PRECISIONS = "profile_precisions"; //Default covariance matrix for a dirac function
    public static final String VALUES = "profile_values";

    public static void profile(Node host, double value) {
        Double min = host.getWithDefault(MIN, null);
        Double max = host.getWithDefault(MAX, null);
        if (min == null || value < min) {
            host.set(MIN, Type.DOUBLE, value);
        }
        if (max == null || value > max) {
            host.set(MAX, Type.DOUBLE, value);
        }


        long total = host.getWithDefault(TOTAL, 0L) + 1;
        double sum = host.getWithDefault(SUM, 0) + value;
        double sumsq = host.getWithDefault(SUMSQ, 0) + value * value;


        host.set(TOTAL, Type.LONG, total);
        host.set(SUM, Type.DOUBLE, sum);
        host.set(SUMSQ, Type.DOUBLE, sumsq);
        host.set(AVG, Type.DOUBLE, sum / total);

        if (total > 1) {
            double cov = Gaussian1D.getCovariance(sum, sumsq, total);
            host.set(COV, Type.DOUBLE, cov);
            host.set(STD, Type.DOUBLE, Math.sqrt(cov));
        }
    }


}
