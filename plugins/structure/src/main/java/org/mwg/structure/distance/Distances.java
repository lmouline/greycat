package org.mwg.structure.distance;

public class Distances {

    public static final int EUCLIDEAN = 0;
    public static final int GEODISTANCE = 1;
    public static final int COSINE = 2;
    public static final int PEARSON = 3;
    public static final int DEFAULT = EUCLIDEAN;


    public static Distance getDistance(int distance) {
        switch (distance){
            case EUCLIDEAN: return EuclideanDistance.instance();
            case GEODISTANCE: return GeoDistance.instance();
            case COSINE: return CosineDistance.instance();
            case PEARSON: return PearsonDistance.instance();
        }
        return getDistance(DEFAULT);
    }

}
