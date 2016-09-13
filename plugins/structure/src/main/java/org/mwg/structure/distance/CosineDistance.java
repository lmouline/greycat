package org.mwg.structure.distance;

public class CosineDistance implements Distance {

    private static CosineDistance static_instance = null;

    public static CosineDistance instance(){
        if(static_instance == null){
            static_instance = new CosineDistance();
        }
        return static_instance;
    }

    private CosineDistance() {
    }

    @Override
    public final double measure(double[] x, double[] y) {
        double sumTop = 0;
        double sumOne = 0;
        double sumTwo = 0;
        for (int i = 0; i < x.length; i++) {
            sumTop += x[i] * y[i];
            sumOne += x[i] * x[i];
            sumTwo += y[i] * y[i];
        }
        double cosSim = sumTop / (Math.sqrt(sumOne) * Math.sqrt(sumTwo));
        if (cosSim < 0) {
            cosSim = 0;//This should not happen, but does because of rounding errorsl
        }
        return 1 - cosSim;
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
