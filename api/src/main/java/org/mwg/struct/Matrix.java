package org.mwg.struct;

public interface Matrix {

    Matrix init(int rows, int columns);

    Matrix fill(double value);

    int rows();

    int columns();

    double get(int rowIndex, int columnIndex);

    Matrix set(int rowIndex, int columnIndex, double value);

    double[] data();

}
