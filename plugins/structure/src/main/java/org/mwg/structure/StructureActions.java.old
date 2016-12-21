package org.mwg.structure;

import org.mwg.structure.action.NTreeInsertTo;
import org.mwg.structure.action.NTreeNearestN;
import org.mwg.structure.action.NTreeNearestNWithinRadius;
import org.mwg.structure.action.NTreeNearestWithinRadius;
import org.mwg.task.Action;

import static org.mwg.core.task.Actions.action;


public class StructureActions {

    public static Action nTreeInsertTo(String path) {
        return action(NTreeInsertTo.NAME, path);
    }

    public static Action nTreeNearestN(String pathOrVar) {
        return action(NTreeNearestN.NAME, pathOrVar);
    }

    public static Action nTreeNearestWithinRadius(String pathOrVar) {
        return action(NTreeNearestWithinRadius.NAME, pathOrVar);
    }

    public static Action nTreeNearestNWithinRadius(String pathOrVar) {
        return action(NTreeNearestNWithinRadius.NAME, pathOrVar);
    }

}
