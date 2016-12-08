package org.mwg.structure;

import org.mwg.Callback;
import org.mwg.Node;

public interface NTree {

    void nearestN(final double[] keys, final int nbElem, final Callback<Node[]> callback);

    void nearestWithinRadius(final double[] keys, final double radius, final Callback<Node[]> callback);

    void nearestNWithinRadius(final double[] keys, int nbElem, double radius, final Callback<Node[]> callback);

    void insertWith(final double[] keys, final Node value, final Callback<Boolean> callback);

    void insert(final Node value, final Callback<Boolean> callback);

    int size();

    void setDistance(int distanceType);

    void setDistanceThreshold(double distanceThreshold);

    void setFrom(String extractor);

}
