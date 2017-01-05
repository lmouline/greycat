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


    public VolatileResult(ENode node, int capacity, double radius) {
        this.node = node;
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

        DMatrix keys = (DMatrix) node.getOrCreateAt(_KEYS, Type.DMATRIX);
        LMatrix values = (LMatrix) node.getOrCreateAt(_VALUES, Type.LMATRIX);
        DMatrix distances = (DMatrix) node.getOrCreateAt(_DISTANCES, Type.DMATRIX);

        if (count == 0) {
            keys.appendColumn(new double[key.length]);
            values.appendColumn(new long[1]);
            distances.appendColumn(new double[]{maxPriority});
        }


        if (remove) {
            if(distance>getMaxPriority(distances)){
                return;
            }

            remove(keys, values, distances);

            count++;
            //set at last
            for (int i = 0; i < keys.rows(); i++) {
                keys.set(i, count, key[i]);
            }
            values.set(0, count, value);
            distances.set(0, count, distance);
        } else {
            count++;
            keys.appendColumn(key);
            values.appendColumn(new long[]{value});
            distances.appendColumn(new double[]{distance});
        }

        bubbleUp(count, keys, values, distances);
    }


    //value -> distances
    //data -> values

    private void remove(DMatrix keys, LMatrix values, DMatrix distances) {
        if (count == 0)
            return;


        /* swap the last element into the first */
        for (int i = 0; i < keys.rows(); i++) {
            keys.set(i, 1, keys.get(i, count));
            keys.set(i, count, 0);
        }

        values.set(0, 1, values.get(0, count));
        distances.set(0, 1, distances.get(0, count));

        values.set(0, count, 0L);
        distances.set(0, count, 0);

        count--;
        bubbleDown(1, keys, values, distances);


    }


    public double getMaxPriority(DMatrix distances) {
        if (count == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return distances.get(0,1);
    }


    //value -> distances
    //data -> values

    private void bubbleUp(int pos, DMatrix keys, LMatrix values, DMatrix distances) {

        double[] okey = keys.column(pos);
        long element = values.column(pos)[0];
        double priority = distances.column(pos)[0];

        /* when the parent is not less than the child, end */
        int halfpos = (int) Math.floor(pos / 2);
        while (distances.column(halfpos)[0] < priority) {
            distances.set(0, pos, distances.get(0, halfpos));
            values.set(0, pos, values.get(0, halfpos));
            for (int i = 0; i < keys.rows(); i++) {
                keys.set(i, pos, keys.get(i, halfpos));
            }

            /* overwrite the child with the parent */
            pos = (int) Math.floor(pos / 2);
            halfpos = (int) Math.floor(pos / 2);
        }

        distances.set(0, pos, priority);
        values.set(0, pos, element);

        for (int i = 0; i < keys.rows(); i++) {
            keys.set(i, pos, okey[i]);
        }

    }


    //value -> distances
    //data -> values

    private void bubbleDown(int pos, DMatrix keys, LMatrix values, DMatrix distances) {

        double[] okey = keys.column(pos);
        long element = values.column(pos)[0];
        double priority = distances.column(pos)[0];

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
                if (distances.get(0,child)  < distances.get(0,child+1))
                    child++; /* choose the biggest child */
            /*
             * percolate down the data at 'pos', one level i.e biggest child
             * becomes the parent
             */
            if (priority < distances.get(0,child)) {
                distances.set(0, pos, distances.get(0, child));
                values.set(0, pos, values.get(0, child));
                for (int i = 0; i < keys.rows(); i++) {
                    keys.set(i, pos, keys.get(i, child));
                }
            } else {
                break;
            }
        }
        distances.set(0, pos, priority);
        values.set(0, pos, element);

        for (int i = 0; i < keys.rows(); i++) {
            keys.set(i, pos, okey[i]);
        }
    }


    @Override
    public double[] keys(int index) {
        return new double[0];
    }

    @Override
    public long value(int index) {
        return 0;
    }

    @Override
    public double distance(int index) {
        return 0;
    }

    @Override
    public DMatrix getAllKeys() {
        return null;
    }


    @Override
    public long[] getAllValues() {
        return new long[0];
    }

    @Override
    public double[] getAllDistances() {
        return new double[0];
    }

    @Override
    public void sortByDistance(boolean ascending) {

    }

    @Override
    public void sortByValue(boolean ascending) {

    }
}
