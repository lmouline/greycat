package org.mwg.struct.distance;

public class PearsonDistance implements Distance {

    private static PearsonDistance static_instance = null;

    public static PearsonDistance instance() {
        if (static_instance == null) {
            static_instance = new PearsonDistance();
        }
        return static_instance;
    }

    private PearsonDistance() {
    }

    @Override
    public final double measure(double[] a, double[] b) {
        double xy = 0, x = 0, x2 = 0, y = 0, y2 = 0;
        for (int i = 0; i < a.length; i++) {
            xy += a[i] * b[i];
            x += a[i];
            y += b[i];
            x2 += a[i] * a[i];
            y2 += b[i] * b[i];
        }
        int n = a.length;
        return (xy - (x * y) / n) / Math.sqrt((x2 - (x * x) / n) * (y2 - (y * y) / n));
    }

    @Override
    public final boolean compare(double x, double y) {
        return Math.abs(x) > Math.abs(y);
    }

    @Override
    public final double getMinValue() {
        return 1;
    }

    @Override
    public final double getMaxValue() {
        return 0;
    }

}
