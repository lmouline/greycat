package org.mwg.struct.util;


import java.util.ArrayList;

public class NearestNeighborArrayList {

    /**
     * The maximum priority possible in this priority queue.
     */
    private double maxPriority = Double.MAX_VALUE;

    /**
     * This contains the list of objects in the queue.
     */
    private ArrayList<Long> data;

    /**
     * This contains the list of prioritys in the queue.
     */
    private ArrayList<Double> value;

    /**
     * Holds the number of elements currently in the queue.
     */
    private int count;


    // constructor
    public NearestNeighborArrayList() {
        count = 0;
        this.data = new ArrayList<Long>();
        this.value = new ArrayList<Double>();
        this.value.add(maxPriority);
        this.data.add(-1L);
    }

    public double getMaxPriority() {
        if (count == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return value.get(1);
    }

    public boolean insert(long node, double priority) {

        // add new object
        count++;

        /* put this as the last element */
        value.add(priority);
        data.add(node);
        bubbleUp(count);
        return true;
    }

    public long[] getAllNodes() {
        int size = count;
        long[] nbrs = new long[count];

        for (int i = 0; i < size; ++i) {
            nbrs[size - i - 1] = remove();
        }
        return nbrs;
    }


    public long getHighest() {
        return data.get(1);
    }

    public double getBestDistance() {
        return value.get(1);
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int getSize() {
        return count;
    }


    /**
     * Remove is a function to remove the element in the queue with the maximum
     * priority. Once the element is removed then it can never be recovered from
     * the queue with further calls. The lowest priority object will leave last.
     *
     * @return the object with the highest priority or if it's empty null
     */
    private long remove() {
        if (count == 0)
            return 0;
        long element = data.get(1);
        /* swap the last element into the first */
        data.set(1, data.get(count));
        value.set(1, value.get(count));
        /* let the GC clean up */
        data.set(count, 0L);
        value.set(count, 0d);
        count--;
        bubbleDown(1);
        return element;
    }


    /**
     * Bubble down is used to put the element at subscript 'pos' into it's
     * rightful place in the heap (i.e heap is another name for
     * <code>PriorityQueue</code>). If the priority of an element at
     * subscript 'pos' is less than it's children then it must be put under one
     * of these children, i.e the ones with the maximum priority must come
     * first.
     *
     * @param pos is the position within the arrays of the element and priority
     */
    private void bubbleDown(int pos) {
        long element = data.get(pos);
        double priority = value.get(pos);
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
                if (value.get(child) < value.get(child + 1))
                    child++; /* choose the biggest child */
            /*
             * percolate down the data at 'pos', one level i.e biggest child
             * becomes the parent
             */
            if (priority < value.get(child)) {
                value.set(pos, value.get(child));
                data.set(pos, data.get(child));
            } else {
                break;
            }
        }
        value.set(pos, priority);
        data.set(pos, element);
    }

    /**
     * Bubble up is used to place an element relatively low in the queue to it's
     * rightful place higher in the queue, but only if it's priority allows it
     * to do so, similar to bubbleDown only in the other direction this swaps
     * out its parents.
     *
     * @param pos the position in the arrays of the object to be bubbled up
     */
    private void bubbleUp(int pos) {
        long element = data.get(pos);
        double priority = value.get(pos);
        /* when the parent is not less than the child, end */
        while (value.get(pos / 2) < priority) {
            /* overwrite the child with the parent */
            value.set(pos, value.get(pos / 2));
            data.set(pos, data.get(pos / 2));
            pos /= 2;
        }
        value.set(pos, priority);
        data.set(pos, element);
    }


}
