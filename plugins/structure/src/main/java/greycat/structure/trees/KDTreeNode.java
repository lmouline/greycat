package greycat.structure.trees;

import greycat.Graph;
import greycat.Node;
import greycat.Type;
import greycat.base.BaseNode;
import greycat.struct.EGraph;
import greycat.structure.Tree;
import greycat.structure.TreeResult;

/**
 * Created by assaad on 07/02/2017.
 */
public class KDTreeNode extends BaseNode implements Tree {

    public static String NAME = "KDTreeNode";
    public static String BOUND_MIN="bound_min";
    public static String BOUND_MAX="bound_max";
    public static String RESOLUTION="resolution";

    private static String E_GRAPH = "kdtree";


    public KDTreeNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    private KDTree _kdTree = null;

    private KDTree getTree() {
        if (_kdTree == null) {
            EGraph egraph = (EGraph) getOrCreate(E_GRAPH, Type.EGRAPH);
            _kdTree = new KDTree(egraph, graph());
        }
        return _kdTree;
    }


    @Override
    public Node set(String name, byte type, Object value) {
        if(name.equals(BOUND_MIN)){
            setMinBound((double[]) value);
        }
        else if(name.equals(BOUND_MAX)){
            setMaxBound((double[]) value);
        }
        else if(name.equals(RESOLUTION)){
            setResolution((double[]) value);
        }
        else {
            super.set(name,type,value);
        }
        return this;
    }

    @Override
    public void setDistance(int distanceType) {
        getTree().setDistance(distanceType);
    }

    @Override
    public void setResolution(double[] resolution) {
        getTree().setResolution(resolution);
    }

    @Override
    public void setMinBound(double[] min) {
        getTree().setMinBound(min);
    }

    @Override
    public void setMaxBound(double[] max) {
        getTree().setMaxBound(max);
    }

    @Override
    public void insert(double[] keys, long value) {
        getTree().insert(keys,value);
    }

    @Override
    public void profile(double[] keys, long occurrence) {
        getTree().profile(keys,occurrence);
    }

    @Override
    public TreeResult nearestN(double[] keys, int nbElem) {
        return getTree().nearestN(keys,nbElem);
    }

    @Override
    public TreeResult nearestWithinRadius(double[] keys, double radius) {
        return getTree().nearestWithinRadius(keys,radius);
    }

    @Override
    public TreeResult nearestNWithinRadius(double[] keys, int nbElem, double radius) {
        return getTree().nearestNWithinRadius(keys,nbElem,radius);
    }

    @Override
    public TreeResult query(double[] min, double[] max) {
        return getTree().query(min,max);
    }

    @Override
    public long size() {
        return getTree().size();
    }

    @Override
    public long numberOfNodes() {
        return getTree().numberOfNodes();
    }
}
