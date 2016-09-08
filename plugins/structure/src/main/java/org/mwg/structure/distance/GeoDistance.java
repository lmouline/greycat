package org.mwg.structure.distance;

public class GeoDistance implements Distance {

    //x=[lat,long]
    //y=[lat,long]

    private static GeoDistance static_instance = null;

    public static GeoDistance instance(){
        if(static_instance == null){
            static_instance = new GeoDistance();
        }
        return static_instance;
    }

    private GeoDistance() {
    }


    @Override
    public final double measure(double[] x, double[] y) {
        double earthRadius = 6371000; //meters
        double dLat = toRadians(y[0] - x[0]);
        double dLng = toRadians(y[1] - x[1]);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(toRadians(x[0])) * Math.cos(toRadians(y[0])) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    /**
     * {@native ts
     *  return angledeg * Math.PI / 180;
     * }
     * @param angledeg
     * @return
     */
    private static double toRadians(double angledeg){
        return Math.toRadians(angledeg);
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
