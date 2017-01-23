package org.mwg.structure.tree;

import org.mwg.structure.distance.Distance;
import org.mwg.structure.util.VolatileResult;

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
