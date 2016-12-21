package org.mwg.structure;

import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.Type;

public interface Tree extends Node {

    //Settings param
    void setDistance(int distanceType);

    void setDistanceThreshold(double distanceThreshold);

    void setStrategy(byte strategy);


    //Insert functions
    void insertWith(final double[] keys, final Type valuetype, final Object value, final Callback<Boolean> callback);



    //Retrieve functions
    void nearestN(final double[] keys, final int nbElem, final Callback<Object[]> callback);

    void nearestWithinRadius(final double[] keys, final double radius, final Callback<Object[]> callback);

    void nearestNWithinRadius(final double[] keys, int nbElem, double radius, final Callback<Object[]> callback);


    //Tree properties
    int size();


}
