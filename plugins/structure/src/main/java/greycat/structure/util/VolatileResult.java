package greycat.structure.util;

import greycat.Type;
import greycat.struct.EGraph;
import greycat.struct.ENode;
import greycat.struct.LMatrix;
import greycat.struct.DMatrix;
import greycat.structure.TreeResult;

public class VolatileResult implements TreeResult {

    private static double maxPriority = Double.MAX_VALUE;
    private static int _KEYS = 1;
    private static int _VALUES = 2;
    private static int _DISTANCES = 3;

    private ENode node;
    private int capacity;
    private int count;
    private double worst;

    private DMatrix _keys;
    private LMatrix _values;
    private DMatrix _distances;


    public VolatileResult(ENode node, int capacity) {
        this.node = node;
        this.count = 0;
        _keys = (DMatrix) node.getOrCreateAt(_KEYS, Type.DMATRIX);
        _values = (LMatrix) node.getOrCreateAt(_VALUES, Type.LMATRIX);
        _distances = (DMatrix) node.getOrCreateAt(_DISTANCES, Type.DMATRIX);
        this.capacity = capacity;
    }


    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean insert(double[] key, long value, double distance) {

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
            if (distance > getWorstDistance()) {
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
        worst = _distances.get(0, 1);
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
    public double getWorstDistance() {
        if (count == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return worst;
    }

    @Override
    public void free() {
        EGraph g = node.graph();
        node.drop();
        if (g.size() == 0) {
            g.free();
        }
    }

    @Override
    public boolean isCapacityReached() {
        return capacity > 0 && (count == capacity);
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


    private int partition(int l, int h, boolean ascending) {
        double x = _distances.get(0, h);
        int i = (l - 1);


        for (int j = l; j <= h - 1; j++) {
            if (ascending) {
                if (_distances.get(0, j) <= x) {
                    i++;
                    swap(i, j);
                }
            } else {
                if (_distances.get(0, j) > x) {
                    i++;
                    swap(i, j);
                }
            }
        }
        // swap arr[i+1] and arr[h]
        swap(i + 1, h);
        return (i + 1);
    }

    private void quickSort(int l, int h, boolean ascending) {
        // create auxiliary stack
        int stack[] = new int[h - l + 1];

        // initialize top of stack
        int top = -1;

        // push initial values in the stack
        stack[++top] = l;
        stack[++top] = h;

        // keep popping elements until stack is not empty
        while (top >= 0) {
            // pop h and l
            h = stack[top--];
            l = stack[top--];

            // set pivot element at it's proper position
            int p = partition(l, h, ascending);

            // If there are elements on left side of pivot,
            // then push left side to stack
            if (p - 1 > l) {
                stack[++top] = l;
                stack[++top] = p - 1;
            }

            // If there are elements on right side of pivot,
            // then push right side to stack
            if (p + 1 < h) {
                stack[++top] = p + 1;
                stack[++top] = h;
            }
        }
    }

    @Override
    public void sort(boolean ascending) {
        if (count > 1) {
            quickSort(1, count, ascending);
        }
    }

}
