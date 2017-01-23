package org.mwg.structure.tree;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.plugin.NodeState;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;
import org.mwg.structure.Tree;
import org.mwg.structure.TreeResult;
import org.mwg.structure.distance.Distance;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.util.HRect;
import org.mwg.structure.util.VolatileResult;

/**
 * Created by assaad on 18/01/2017.
 */
public class KDTree extends BaseNode implements Tree {
    public static String NAME = "KDTree";

    public static String RESOLUTION = "resolution";
    public static String DISTANCE = "distance";
    private static String EGRAPH = "egraph";
    private static String STRATEGY = "strategy";
    private static String DIM = "dim";

    public static int DISTANCE_DEF = Distances.DEFAULT;


    private static int E_SUBTREE_NODES = 0;
    private static int E_KEY = 1;
    private static int E_SUM_KEY = 2;
    private static int E_VALUE = 3;
    private static int E_SUBTREE_VALUES = 4;
    private static int E_RIGHT = 5;
    private static int E_LEFT = 6;

    public KDTree(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }


    private static boolean checkCreateLevels(double[] key1, double[] key2, double[] resolutions) {
        if (resolutions != null) {
            for (int i = 0; i < key2.length; i++) {
                if (Math.abs(key1[i] - key2[i]) > resolutions[i]) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < key2.length; i++) {
                if ((key1[i] != key2[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    // Method rsearch translated from 352.range.c of Gonnet & Baeza-Yates
    protected static void rangeSearch(final double[] lowk, final double[] uppk, final double[] center, final Distance distance, final ENode node, final int lev, final int dim, final VolatileResult nnl) {

        if (node == null)
            return;

        double[] key = (double[]) node.getAt(E_KEY);

        if (lowk[lev] <= key[lev]) {
            rangeSearch(lowk, uppk, center, distance, (ENode) node.getAt(E_LEFT), (lev + 1) % dim, dim, nnl);
        }
        int j;
        j = 0;
        while (j < dim && lowk[j] <= key[j] && uppk[j] >= key[j]) {
            j++;
        }

        if (j == dim) {
            nnl.insert(key, (long) node.getAt(E_VALUE), distance.measure(key, center));
        }
        if (uppk[lev] > key[lev]) {
            rangeSearch(lowk, uppk, center, distance, (ENode) node.getAt(E_RIGHT), (lev + 1) % dim, dim, nnl);
        }
    }


    private static void reccursiveTraverse(final ENode node, final VolatileResult nnl, final Distance distance, final double[] target, HRect hr, final int lev, final int dim, double max_dist_sqd, final double radius) {

        // 1. if kd is empty exit.
        if (node == null) {
            return;
        }

        double[] pivot = (double[]) node.getAt(E_KEY);
        if (pivot == null) {
            throw new RuntimeException("Key can't be null");
        }


        // 2. s := split field of kd
        int s = lev % dim;

        // 3. pivot := dom-elt field of kd

        double pivot_to_target = distance.measure(pivot, target);

        // 4. Cut hr into to sub-hyperrectangles left-hr and right-hr.
        // The cut plane is through pivot and perpendicular to the s
        // dimension.
        HRect left_hr = hr; // optimize by not cloning
        HRect right_hr = (HRect) hr.clone();
        left_hr.max[s] = pivot[s];
        right_hr.min[s] = pivot[s];

        // 5. target-in-left := target_s <= pivot_s
        boolean target_in_left = target[s] < pivot[s];

        ENode nearer_kd;
        HRect nearer_hr;
        ENode further_kd;
        HRect further_hr;

        // 6. if target-in-left then
        // 6.1. nearer-kd := left field of kd and nearer-hr := left-hr
        // 6.2. further-kd := right field of kd and further-hr := right-hr
        if (target_in_left) {
            nearer_kd = (ENode) node.getAt(E_LEFT);
            nearer_hr = left_hr;
            further_kd = (ENode) node.getAt(E_RIGHT);
            further_hr = right_hr;
        }
        //
        // 7. if not target-in-left then
        // 7.1. nearer-kd := right field of kd and nearer-hr := right-hr
        // 7.2. further-kd := left field of kd and further-hr := left-hr
        else {
            nearer_kd = (ENode) node.getAt(E_RIGHT);
            nearer_hr = right_hr;
            further_kd = (ENode) node.getAt(E_LEFT);
            further_hr = left_hr;
        }

        // 8. Recursively call Nearest Neighbor with paramters
        // (nearer-kd, target, nearer-hr, max-dist-sqd), storing the
        // results in nearest and dist-sqd
        //nnbr(nearer_kd, target, nearer_hr, max_dist_sqd, lev + 1, K, nnl);
        reccursiveTraverse(nearer_kd, nnl, distance, target, nearer_hr, lev + 1, dim, max_dist_sqd, radius);


        double dist_sqd;

        if (!nnl.isCapacityReached()) {
            dist_sqd = Double.MAX_VALUE;
        } else {
            dist_sqd = nnl.getWorstDistance();
        }

        // 9. max-dist-sqd := minimum of max-dist-sqd and dist-sqd
        max_dist_sqd = Math.min(max_dist_sqd, dist_sqd);

        // 10. A nearer point could only lie in further-kd if there were some
        // part of further-hr within distance sqrt(max-dist-sqd) of
        // target. If this is the case then
        double[] closest = further_hr.closest(target);
        if (distance.measure(closest, target) < max_dist_sqd) {

            // 10.1 if (pivot-target)^2 < dist-sqd then
            if (pivot_to_target < dist_sqd) {

                // 10.1.2 dist-sqd = (pivot-target)^2
                dist_sqd = pivot_to_target;

                if (radius > 0) {
                    if (dist_sqd < radius) {
                        nnl.insert(pivot, (Long) node.getAt(E_VALUE), dist_sqd);
                    }
                } else {
                    nnl.insert(pivot, (Long) node.getAt(E_VALUE), dist_sqd);
                }


                // 10.1.3 max-dist-sqd = dist-sqd
                // max_dist_sqd = dist_sqd;
                if (nnl.isCapacityReached()) {
                    max_dist_sqd = nnl.getWorstDistance();
                } else {
                    max_dist_sqd = Double.MAX_VALUE;
                }
            }

            // 10.2 Recursively call Nearest Neighbor with parameters
            // (further-kd, target, further-hr, max-dist_sqd),
            // storing results in temp-nearest and temp-dist-sqd
            //nnbr(further_kd, target, further_hr, max_dist_sqd, lev + 1, K, nnl);
            reccursiveTraverse(further_kd, nnl, distance, target, further_hr, lev + 1, dim, max_dist_sqd, radius);
        }
    }

    private static boolean internalInsert(final ENode node, final double[] key, final long value, final int strategyType, final int lev, final double[] resolution) {
        double[] pKey = (double[]) node.getAt(E_KEY);
        if (pKey == null) {
            pKey = new double[key.length];
            System.arraycopy(key, 0, pKey, 0, key.length);
            node.setAt(E_KEY, Type.DOUBLE_ARRAY, pKey);
            node.setAt(E_VALUE, Type.LONG, value);
            if (strategyType == IndexStrategy.PROFILE) {
                node.setAt(E_SUBTREE_VALUES, Type.LONG, value);
                double[] sk = new double[pKey.length];
                for (int i = 0; i < key.length; i++) {
                    sk[i] = pKey[i] * value;
                }
                node.setAt(E_SUM_KEY, Type.DOUBLE_ARRAY, sk);
            }
            node.setAt(E_SUBTREE_NODES, Type.LONG, 1);
            return true;
        } else if (!checkCreateLevels(key, pKey, resolution)) {
            //Need to replace the key here if indexing
            if (strategyType == IndexStrategy.INDEX) {
                node.setAt(E_VALUE, Type.LONG, value);
            } else if (strategyType == IndexStrategy.PROFILE) {
                //need to update the keys and values of the profiles
                double[] sk = (double[]) node.getAt(E_SUM_KEY);
                for (int i = 0; i < pKey.length; i++) {
                    sk[i] = (sk[i] + key[i] * value);
                }
                node.setAt(E_SUM_KEY, Type.DOUBLE_ARRAY, sk);
                node.setAt(E_VALUE, Type.LONG, (long) node.getOrCreateAt(E_VALUE, Type.LONG) + value);
                node.setAt(E_SUBTREE_VALUES, Type.LONG, (long) node.getOrCreateAt(E_SUBTREE_VALUES, Type.LONG) + value);
            }
            return false;
        } else {
            ENode child;
            if (key[lev] > pKey[lev]) {
                child = (ENode) node.getAt(E_RIGHT);
                if (child == null) {
                    child = createNode(node, true);
                }
            } else {
                child = (ENode) node.getAt(E_LEFT);
                if (child == null) {
                    child = createNode(node, false);
                }
            }

            if (internalInsert(child, key, value, strategyType, (lev + 1) % key.length, resolution)) {
                //update parents reccursively
                if (strategyType == IndexStrategy.PROFILE) {
                    node.setAt(E_SUBTREE_VALUES, Type.LONG, (long) node.getAt(E_SUBTREE_VALUES) + value);
                }

                node.setAt(E_SUBTREE_NODES, Type.LONG, (long) node.getAt(E_SUBTREE_NODES) + 1);
                return true;
            } else {
                if (strategyType == IndexStrategy.PROFILE) {
                    node.setAt(E_SUBTREE_VALUES, Type.LONG, (long) node.getAt(E_SUBTREE_VALUES) + value);
                }
                return false;
            }
        }

    }

    private static ENode createNode(ENode parent, boolean right) {
        ENode child = parent.graph().newNode();
        if (right) {
            parent.setAt(E_RIGHT, Type.ENODE, child);
        } else {
            parent.setAt(E_LEFT, Type.ENODE, child);
        }

        return child;
    }


    @Override
    public void setDistance(int distanceType) {
        super.set(DISTANCE, Type.INT, distanceType);
    }


    @Override
    public void insert(double[] keys, long value) {
        final NodeState state = unphasedState();
        int strategy = IndexStrategy.INDEX;

        double[] resolution = (double[]) state.getFromKey(RESOLUTION);
        EGraph graph = (EGraph) state.getOrCreateFromKey(EGRAPH, Type.EGRAPH);

        synchronized (state) { // assumption that NodeState == StateChunk
            ENode root = graph.root();
            if (root == null) {
                root = graph.newNode();
                state.setFromKey(STRATEGY, Type.INT, strategy);
                graph.setRoot(root);
                state.setFromKey(DIM, Type.INT, keys.length);
            } else {
                if (keys.length != (int) state.getFromKey(DIM)) {
                    throw new RuntimeException("Keys should always be the same length");
                }
            }
            internalInsert(root, keys, value, strategy, 0, resolution);
        }
    }


    @Override
    public void profile(double[] keys, long occurrence) {
        final NodeState state = unphasedState();
        int strategy = IndexStrategy.PROFILE;

        double[] resolution = (double[]) state.getFromKey(RESOLUTION);
        EGraph graph = (EGraph) state.getOrCreateFromKey(EGRAPH, Type.EGRAPH);

        synchronized (state) { // assumption that NodeState == StateChunk
            ENode root = graph.root();
            if (root == null) {
                root = graph.newNode();
                state.setFromKey(STRATEGY, Type.INT, strategy);
                graph.setRoot(root);
                state.setFromKey(DIM, Type.INT, keys.length);
            } else {
                if (keys.length != (int) state.getFromKey(DIM)) {
                    throw new RuntimeException("Keys should always be the same length");
                }
            }
            internalInsert(root, keys, occurrence, strategy, 0, resolution);
        }
    }

    @Override
    public TreeResult nearestN(double[] keys, int nbElem) {
        final NodeState state = unphasedState();
        final EGraph graph = (EGraph) state.getOrCreateFromKey(EGRAPH, Type.EGRAPH);
        final Distance distance = Distances.getDistance(state.getFromKeyWithDefault(DISTANCE, DISTANCE_DEF));
        synchronized (state) {
            ENode root = graph.root();
            if (root == null) {
                return null;
            }
            if (keys.length != ((double[]) root.getAt(E_KEY)).length) {
                throw new RuntimeException("Keys are not of the same size");
            }

            EGraph calcZone = graph().space().newVolatileGraph();
            if (nbElem <= 0) {
                throw new RuntimeException("nb elements can't be <=0");
            }
            VolatileResult nnl = new VolatileResult(calcZone.newNode(), nbElem);
            reccursiveTraverse(root, nnl, distance, keys, HRect.infiniteHRect(keys.length), 0, keys.length, Double.MAX_VALUE, -1);
            nnl.sort(true);
            return nnl;
        }
    }

    @Override
    public TreeResult nearestWithinRadius(double[] keys, double radius) {
        final NodeState state = unphasedState();
        final EGraph graph = (EGraph) state.getOrCreateFromKey(EGRAPH, Type.EGRAPH);
        final Distance distance = Distances.getDistance(state.getFromKeyWithDefault(DISTANCE, DISTANCE_DEF));
        synchronized (state) {
            ENode root = graph.root();
            if (root == null) {
                return null;
            }
            if (keys.length != ((double[]) root.getAt(E_KEY)).length) {
                throw new RuntimeException("Keys are not of the same size");
            }
            EGraph calcZone = graph().space().newVolatileGraph();
            VolatileResult nnl = new VolatileResult(calcZone.newNode(), -1);
            reccursiveTraverse(root, nnl, distance, keys, HRect.infiniteHRect(keys.length), 0, keys.length, Double.MAX_VALUE, radius);
            nnl.sort(true);
            return nnl;
        }
    }

    @Override
    public TreeResult nearestNWithinRadius(double[] keys, int nbElem, double radius) {
        final NodeState state = unphasedState();
        final EGraph graph = (EGraph) state.getOrCreateFromKey(EGRAPH, Type.EGRAPH);
        final Distance distance = Distances.getDistance(state.getFromKeyWithDefault(DISTANCE, DISTANCE_DEF));
        synchronized (state) {
            ENode root = graph.root();
            if (root == null) {
                return null;
            }
            if (keys.length != ((double[]) root.getAt(E_KEY)).length) {
                throw new RuntimeException("Keys are not of the same size");
            }

            EGraph calcZone = graph().space().newVolatileGraph();
            if (nbElem <= 0) {
                throw new RuntimeException("nb elements can't be <=0");
            }
            VolatileResult nnl = new VolatileResult(calcZone.newNode(), nbElem);
            reccursiveTraverse(root, nnl, distance, keys, HRect.infiniteHRect(keys.length), 0, keys.length, Double.MAX_VALUE, radius);
            nnl.sort(true);
            return nnl;
        }
    }

    @Override
    public TreeResult query(double[] min, double[] max) {
        NodeState state = unphasedState();

        EGraph graph = (EGraph) state.getOrCreateFromKey(EGRAPH, Type.EGRAPH);
        Distance distance = Distances.getDistance(state.getFromKeyWithDefault(DISTANCE, DISTANCE_DEF));

        final double[] center = new double[max.length];

        for (int i = 0; i < center.length; i++) {
            center[i] = (min[i] + max[i]) / 2;
        }

        ENode root = graph.root();
        if (root == null) {
            return null;
        }

        EGraph calcZone = graph().space().newVolatileGraph();
        VolatileResult nnl = new VolatileResult(calcZone.newNode(), -1);
        rangeSearch(min, max, center, distance, root, 0, min.length, nnl);

        nnl.sort(true);
        return nnl;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public long numberOfNodes() {
        return 0;
    }
}
