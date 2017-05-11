package greycat.internal.tree.ndmanager;

import greycat.struct.NDManager;

/**
 * Created by assaad on 11/05/2017.
 */
public class ProfileManager implements NDManager {
    @Override
    public Object get(long id) {
        return id;
    }

    @Override
    public long updateExistingLeafNode(long oldKey, Object valueToInsert) {
        return oldKey + (long) valueToInsert;
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
    public long getNewLeafNode(Object valueToInsert) {
        return 0;
    }

    @Override
    public long getNewParentNode() {
        return 0;
    }

    @Override
    public long updateParent(long parentkey, double[] key, Object valueToInsert) {
        return parentkey + (long) valueToInsert;
    }
}
