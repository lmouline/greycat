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
package greycat.structure.util;


import greycat.ml.common.distance.Distance;

/**
 * Created by assaad on 20/01/2017.
 */
public class TreeHelper {
    public static boolean checkBoundsIntersection(final double[] targetmin, final double[] targetmax, final double[] boundMin, final double[] boundMax) {
        for (int i = 0; i < boundMax.length; i++) {
            if (targetmin[i] > boundMax[i] || targetmax[i] < boundMin[i]) {
                return false;
            }
        }
        return true;
    }


    public static boolean checkKeyInsideBounds(final double[] key, final double[] boundMin, final double[] boundMax) {
        for (int i = 0; i < boundMax.length; i++) {
            if (key[i] > boundMax[i] || key[i] < boundMin[i]) {
                return false;
            }
        }
        return true;
    }

    public static double getclosestDistance(double[] target, double[] boundMin, double[] boundMax, Distance distance) {
        double[] closest = new double[target.length];

        for (int i = 0; i < target.length; i++) {
            if (target[i] >= boundMax[i]) {
                closest[i] = boundMax[i];
            } else if (target[i] <= boundMin[i]) {
                closest[i] = boundMin[i];
            } else {
                closest[i] = target[i];
            }
        }
        return distance.measure(closest, target);
    }



    public static void filterAndInsert(double[] key, long value, double[] target, double[] targetmin, double[] targetmax, double[] targetcenter, Distance distance, double radius, VolatileResult nnl) {
        if (targetmin != null) {
            if (checkKeyInsideBounds(key, targetmin, targetmax)) {
                nnl.insert(key, value, distance.measure(key, targetcenter));
            }
        } else {
            double dist = distance.measure(key, target);
            if (radius > 0) {
                if (dist < radius) {
                    nnl.insert(key, value, dist);
                }
            } else {
                nnl.insert(key, value, dist);
            }
        }
    }
}
