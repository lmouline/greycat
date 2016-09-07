package org.mwg.struct.distance;

public class EuclideanDistance implements Distance {

    public static EuclideanDistance INSTANCE = new EuclideanDistance();

    private EuclideanDistance() {
    }

    @Override
    public final double measure(double[] x, double[] y) {
        double value = 0;
        for (int i = 0; i < x.length; i++) {
            value = value + (x[i] - y[i]) * (x[i] - y[i]);
        }
        return Math.sqrt(value);
    }

    @Override
    public final boolean compare(double x, double y) {
        return x < y;
    }


    @Override
    public final double getMinValue() {
        return 0;
    }

    @Override
    public final double getMaxValue() {
        return Double.MAX_VALUE;
    }
}
