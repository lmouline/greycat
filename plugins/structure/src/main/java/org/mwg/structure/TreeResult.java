package org.mwg.structure;

import org.mwg.struct.DMatrix;

/**
 * Created by assaad on 05/01/2017.
 */
public interface TreeResult {

    int size();

    boolean insert(double[] key, long value, double distance);

    double[] keys(int index);
    long value(int index);
    double distance(int index);

    DMatrix getAllKeys();
    long[] getAllValues();
    double[] getAllDistances();

    void sortByDistance(boolean ascending);
    void sortByValue(boolean ascending);
}
