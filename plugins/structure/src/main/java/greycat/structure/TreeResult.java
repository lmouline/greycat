package greycat.structure;

/**
 * Created by assaad on 05/01/2017.
 */
public interface TreeResult {

    int size();

    boolean insert(double[] key, long value, double distance);

    double[] keys(int index);
    long value(int index);
    double distance(int index);
    double getWorstDistance();

    void free();
    boolean isCapacityReached();
    void sort(boolean ascending);
}
