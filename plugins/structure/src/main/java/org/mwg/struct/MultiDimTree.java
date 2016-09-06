package org.mwg.struct;

import org.mwg.Callback;
import org.mwg.Node;

public interface MultiDimTree {

    void nearestN(final double[] keys, final int nbElem, final Callback<Node[]> callback);

    void nearestWithinRadius(final double[] keys, final double radius, final Callback<Node[]> callback);

    void nearestNWithinRadius(final double[] keys, int nbElem, double radius, final Callback<Node[]> callback);

    void insert(final double[] keys, final Node value, final Callback<Boolean> callback);

    int size();

}
