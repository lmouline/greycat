package greycat.ml.profiling;

import greycat.struct.EGraph;
import greycat.struct.ENode;
import greycat.struct.NDManager;

/**
 * Created by assaad on 21/06/2017.
 */
public class GmmManager implements NDManager {

    EGraph _backend;
    public GmmManager(EGraph eGraph){
        _backend=eGraph;
    }

    @Override
    public Object get(long id) {
        return _backend.node((int) id);
    }

    @Override
    public long updateExistingLeafNode(long oldKey, double[] key, Object valueToInsert) {
        ENode node= _backend.node((int) oldKey);
        GaussianENode gn= new GaussianENode(node);


        return 0;
    }

    @Override
    public boolean updateParentsOnExisting() {
        return true;
    }

    @Override
    public boolean updateParentsOnNewValue() {
        return true;
    }

    @Override
    public boolean parentsHaveNodes() {
        return true;
    }

    @Override
    public long getNewLeafNode(double[] key, Object valueToInsert) {
        return 0;
    }

    @Override
    public long getNewParentNode() {
        return 0;
    }

    @Override
    public long updateParent(long parentkey, double[] key, Object valueToInsert) {
        return 0;
    }
}
