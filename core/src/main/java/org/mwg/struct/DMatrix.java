package org.mwg.struct;

import java.util.Random;

public interface DMatrix {

    DMatrix init(int rows, int columns);

    DMatrix fill(double value);

    DMatrix fillWith(double[] values);

    DMatrix fillWithRandom(Random random, double min, double max);

    DMatrix fillWithRandomStd(Random random, double std);

    int rows();

    int columns();

    double[] column(int i);

    double get(int rowIndex, int columnIndex);

    DMatrix set(int rowIndex, int columnIndex, double value);

    DMatrix add(int rowIndex, int columnIndex, double value);

    DMatrix appendColumn(double[] newColumn);

    double[] data();

    int leadingDimension();

    double unsafeGet(int index);

    DMatrix unsafeSet(int index, double value);

}
