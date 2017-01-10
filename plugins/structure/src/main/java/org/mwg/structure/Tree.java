package org.mwg.structure;

import org.mwg.Node;

public interface Tree extends Node {

    //Settings param
    void setDistance(int distanceType);

    void setDistanceThreshold(double distanceThreshold);

    void setStrategy(byte strategy);


    //Insert functions
    void insert(final double[] keys, final long value);

    void profile(final double[] keys, final long occurrence);

    //Retrieve functions
    TreeResult nearestN(final double[] keys, final int nbElem);

    TreeResult nearestWithinRadius(final double[] keys, final double radius);

    TreeResult nearestNWithinRadius(final double[] keys, final int nbElem, final double radius);

    TreeResult query(final double[] min, final double[] max);

    //Tree properties
    long size();
    long numberOfNodes();


}
