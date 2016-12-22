package org.mwg.struct;

public interface LMatrix {

    LMatrix init(int rows, int columns);

    LMatrix fill(long value);

    LMatrix fillWith(long[] values);

    LMatrix fillWithRandom(long min, long max, long seed);

    int rows();

    int columns();

    long[] column(int i);

    long get(int rowIndex, int columnIndex);

    LMatrix set(int rowIndex, int columnIndex, long value);

    LMatrix add(int rowIndex, int columnIndex, long value);

    LMatrix appendColumn(long[] newColumn);

    long[] data();

    int leadingDimension();

    long unsafeGet(int index);

    LMatrix unsafeSet(int index, long value);

}
