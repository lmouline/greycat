package greycat.struct.proxy;

import greycat.struct.DMatrix;

public class DMatrixProxy implements DMatrix {

    @Override
    public DMatrix init(int rows, int columns) {
        return null;
    }

    @Override
    public DMatrix fill(double value) {
        return null;
    }

    @Override
    public DMatrix fillWith(double[] values) {
        return null;
    }

    @Override
    public int rows() {
        return 0;
    }

    @Override
    public int columns() {
        return 0;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public double[] column(int i) {
        return new double[0];
    }

    @Override
    public double get(int rowIndex, int columnIndex) {
        return 0;
    }

    @Override
    public DMatrix set(int rowIndex, int columnIndex, double value) {
        return null;
    }

    @Override
    public DMatrix add(int rowIndex, int columnIndex, double value) {
        return null;
    }

    @Override
    public DMatrix appendColumn(double[] newColumn) {
        return null;
    }

    @Override
    public double[] data() {
        return new double[0];
    }

    @Override
    public int leadingDimension() {
        return 0;
    }

    @Override
    public double unsafeGet(int index) {
        return 0;
    }

    @Override
    public DMatrix unsafeSet(int index, double value) {
        return null;
    }
}
