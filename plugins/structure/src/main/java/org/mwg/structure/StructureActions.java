package org.mwg.structure;

import org.mwg.structure.action.NTreeInsertTo;
import org.mwg.structure.action.NTreeNearestN;
import org.mwg.structure.action.NTreeNearestNWithinRadius;
import org.mwg.structure.action.NTreeNearestWithinRadius;
import org.mwg.task.Task;

import static org.mwg.task.Actions.newTask;

public class StructureActions {

    public static Task nTreeInsertTo(String path) {
        return newTask().action(NTreeInsertTo.NAME, path);
    }

    public static Task nTreeNearestN(String pathOrVar) {
        return newTask().action(NTreeNearestN.NAME, pathOrVar);
    }

    public static Task nTreeNearestWithinRadius(String pathOrVar) {
        return newTask().action(NTreeNearestWithinRadius.NAME, pathOrVar);
    }

    public static Task nTreeNearestNWithinRadius(String pathOrVar) {
        return newTask().action(NTreeNearestNWithinRadius.NAME, pathOrVar);
    }

}
