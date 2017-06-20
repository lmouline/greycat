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
package greycat.ml.math.timeseries;

/**
 * Created by assaad on 20/06/2017.
 */
public class StationaryCalc {
    public static double[] diff(double[] series) {
        if (series.length > 1) {
            double[] temp = new double[series.length - 1];

            for (int i = 1; i < series.length; i++) {
                temp[i - 1] = series[i] - series[i - 1];
            }

            return temp;
        }
        throw new RuntimeException("Series should be at least 2 elements!");
    }

    public static double[] diffNOrder(double[] series, int n) {
        if (series.length > n) {
            double[] temp = series;
            for (int i = 0; i < n; i++) {
                temp = diff(temp);
            }
            return temp;
        }
        throw new RuntimeException("Series should be at least N elements!");
    }




}
