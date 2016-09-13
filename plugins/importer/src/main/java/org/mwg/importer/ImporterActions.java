package org.mwg.importer;

import org.mwg.importer.action.JsonMatch;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;

import static org.mwg.task.Actions.newTask;

public class ImporterActions {

    public static final String READFILES = "readFiles";

    public static final String READLINES = "readLines";

    public static final String READJSON = "readJson";

    public static final String JSONMATCH = "jsonMatch";

    public static Task readLines(String path) {
        return newTask().action(READLINES, path);
    }

    public static Task readFiles(String pathOrVar) {
        return newTask().action(READFILES, pathOrVar);
    }

    public static Task readJson(String pathOrVar) {
        return newTask().action(READJSON, pathOrVar);
    }

    public static Task jsonMatch(String filter, Task then) {
        return newTask().then(new Action() {
            @Override
            public void eval(TaskContext context) {
                new JsonMatch(filter, then).eval(context);
            }
        });
    }

}
