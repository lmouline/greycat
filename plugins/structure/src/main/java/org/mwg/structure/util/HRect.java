package org.mwg.structure.util;


// Hyper-Rectangle class supporting KDTreeOld class


public class HRect {

    public double[] min;
    public double[] max;

    protected HRect(double[] vmin, double[] vmax) {

        min = new double[vmin.length];
        max = new double[vmax.length];

        System.arraycopy(vmin, 0, min, 0, vmin.length);
        System.arraycopy(vmax, 0, max, 0, vmax.length);
    }

    public Object clone() {
        return new HRect(min, max);
    }

    // from Moore's eqn. 6.6
    public double[] closest(double[] t) {

        double[] p = new double[t.length];

        for (int i = 0; i < t.length; ++i) {
            if (t[i] <= min[i]) {
                p[i] = min[i];
            } else if (t[i] >= max[i]) {
                p[i] = max[i];
            } else {
                p[i] = t[i];
            }
        }

        return p;
    }

    // used in initial conditions of KDTreeOld.nearest()
    public static HRect infiniteHRect(int d) {

        double[] vmin = new double[d];
        double[] vmax = new double[d];

        for (int i = 0; i < d; ++i) {
            vmin[i] = Double.NEGATIVE_INFINITY;
            vmax[i] = Double.POSITIVE_INFINITY;
        }

        return new HRect(vmin, vmax);
    }


    public String toString() {
        return min + "\n" + max + "\n";
    }
}

