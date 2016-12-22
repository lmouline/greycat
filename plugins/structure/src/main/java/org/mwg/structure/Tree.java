package org.mwg.structure;

import org.mwg.Node;

public interface Tree extends Node {

    //Settings param
    void setDistance(int distanceType);

    void setDistanceThreshold(double distanceThreshold);

    void setStrategy(byte strategy);


    //Insert functions
    void insertWith(final double[] keys, final byte valuetype, final Object value);

    void profile(final double[] keys, int occurance);

    //Retrieve functions
    Object[] nearestN(final double[] keys, final int nbElem);

    Object[] nearestWithinRadius(final double[] keys, final double radius);

    Object[] nearestNWithinRadius(final double[] keys, final int nbElem, final double radius);


    //Tree properties
    int size();


}
