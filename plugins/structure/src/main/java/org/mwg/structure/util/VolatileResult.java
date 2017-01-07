package org.mwg.structure.util;

import org.mwg.Type;
import org.mwg.struct.DMatrix;
import org.mwg.struct.ENode;
import org.mwg.struct.LMatrix;
import org.mwg.structure.TreeResult;


public class VolatileResult implements TreeResult {

    private static double maxPriority = Double.MAX_VALUE;
    private static long _KEYS = 1;
    private static long _VALUES = 2;
    private static long _DISTANCES = 3;

    private ENode node;
    private int capacity;
    private int count;
    private double radius;

    private DMatrix _keys;
    private LMatrix _values;
    private DMatrix _distances;


    public VolatileResult(ENode node, int capacity, double radius) {
        this.node = node;
        _keys = (DMatrix) node.getOrCreateAt(_KEYS, Type.DMATRIX);
        _values = (LMatrix) node.getOrCreateAt(_VALUES, Type.LMATRIX);
        _distances = (DMatrix) node.getOrCreateAt(_DISTANCES, Type.DMATRIX);
        this.capacity = capacity;
        this.radius = radius;
    }


    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean insert(double[] key, long value, double distance) {
        if (radius > 0 && distance > radius) {
            return false;
        }

        if (capacity > 0 && count == capacity) {
            add(key, value, distance, true);
            return true;
        }
        //Only add
        add(key, value, distance, false);
        return true;


    }

    private void add(double[] key, long value, double distance, boolean remove) {

        if (count == 0) {
            _keys.appendColumn(new double[key.length]);
            _values.appendColumn(new long[1]);
            _distances.appendColumn(new double[]{maxPriority});
        }


        if (remove) {
            if (distance > getMaxPriority()) {
                return;
            }
            remove();
            count++;
            //set at last
            for (int i = 0; i < _keys.rows(); i++) {
                _keys.set(i, count, key[i]);
            }
            _values.set(0, count, value);
            _distances.set(0, count, distance);
        } else {
            count++;
            _keys.appendColumn(key);
            _values.appendColumn(new long[]{value});
            _distances.appendColumn(new double[]{distance});
        }

        bubbleUp(count);
    }


    //value -> _distances
    //data -> _values

    private void remove() {
        if (count == 0)
            return;


        /* swap the last element into the first */
        for (int i = 0; i < _keys.rows(); i++) {
            _keys.set(i, 1, _keys.get(i, count));
            _keys.set(i, count, 0);
        }

        _values.set(0, 1, _values.get(0, count));
        _distances.set(0, 1, _distances.get(0, count));

        _values.set(0, count, 0L);
        _distances.set(0, count, 0);

        count--;
        bubbleDown(1);

    }


    public double getMaxPriority() {
        if (count == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return _distances.get(0, 1);
    }


    //value -> _distances
    //data -> _values

    private void bubbleUp(int pos) {

        double[] okey = _keys.column(pos);
        long element = _values.column(pos)[0];
        double priority = _distances.column(pos)[0];

        /* when the parent is not less than the child, end */
        int halfpos = (int) Math.floor(pos / 2);
        while (_distances.column(halfpos)[0] < priority) {
            _distances.set(0, pos, _distances.get(0, halfpos));
            _values.set(0, pos, _values.get(0, halfpos));
            for (int i = 0; i < _keys.rows(); i++) {
                _keys.set(i, pos, _keys.get(i, halfpos));
            }

            /* overwrite the child with the parent */
            pos = (int) Math.floor(pos / 2);
            halfpos = (int) Math.floor(pos / 2);
        }

        _distances.set(0, pos, priority);
        _values.set(0, pos, element);

        for (int i = 0; i < _keys.rows(); i++) {
            _keys.set(i, pos, okey[i]);
        }

    }


    //value -> _distances
    //data -> _values

    private void bubbleDown(int pos) {

        double[] okey = _keys.column(pos);
        long element = _values.column(pos)[0];
        double priority = _distances.column(pos)[0];

        int child;
        /* hole is position '1' */
        for (; pos * 2 <= count; pos = child) {
            child = pos * 2;
            /*
             * if 'child' equals 'count' then there is only one leaf for this
             * parent
             */
            if (child != count)

                /* left_child > right_child */
                if (_distances.get(0, child) < _distances.get(0, child + 1))
                    child++; /* choose the biggest child */
            /*
             * percolate down the data at 'pos', one level i.e biggest child
             * becomes the parent
             */
            if (priority < _distances.get(0, child)) {
                _distances.set(0, pos, _distances.get(0, child));
                _values.set(0, pos, _values.get(0, child));
                for (int i = 0; i < _keys.rows(); i++) {
                    _keys.set(i, pos, _keys.get(i, child));
                }
            } else {
                break;
            }
        }
        _distances.set(0, pos, priority);
        _values.set(0, pos, element);

        for (int i = 0; i < _keys.rows(); i++) {
            _keys.set(i, pos, okey[i]);
        }
    }


    @Override
    public double[] keys(int index) {
        return _keys.column(index + 1);
    }

    @Override
    public long value(int index) {
        return _values.get(index + 1, 0);
    }

    @Override
    public double distance(int index) {
        return _distances.get(index + 1, 0);
    }

    @Override
    public void free() {
        node.drop();
    }


    private void swap(int i, int j) {
        double[] tempkey = _keys.column(i);
        long tempvalue = _values.get(0, i);
        double tempdist = _distances.get(0, i);


        _distances.set(0, i, _distances.get(0, j));
        _values.set(0, i, _values.get(0, j));
        for (int k = 0; k < _keys.rows(); k++) {
            _keys.set(k, i, _keys.get(k, j));
        }

        _distances.set(0, j, tempdist);
        _values.set(0, j, tempvalue);
        for (int k = 0; k < _keys.rows(); k++) {
            _keys.set(k, j, tempkey[k]);
        }

    }


    private void quickSort(int lowerIndex, int higherIndex, boolean ascending) {

        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        double pivot = _distances.get(0, lowerIndex + (higherIndex - lowerIndex) / 2);
        // Divide into two arrays
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which
             * is greater then the pivot value, and also we will identify a number
             * from right side which is less then the pivot value. Once the search
             * is done, then we exchange both numbers.
             */
            if(ascending) {
                while (_distances.get(0, i) < pivot) {
                    i++;
                }
                while (_distances.get(0, j) > pivot) {
                    j--;
                }
            }
            else {
                while (_distances.get(0, i) > pivot) {
                    i++;
                }
                while (_distances.get(0, j) < pivot) {
                    j--;
                }
            }
            if (i <= j) {
                swap(i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quickSort(lowerIndex, j, ascending);
        if (i < higherIndex)
            quickSort(i, higherIndex, ascending);
    }


    @Override
    public void sort(boolean ascending) {
        quickSort(1, count, ascending);
    }

}
