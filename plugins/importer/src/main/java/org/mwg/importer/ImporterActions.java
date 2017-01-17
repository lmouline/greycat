package org.mwg.importer;

import org.mwg.task.Action;

public class ImporterActions {

    public static final String READFILES = "readFiles";

    public static final String READLINES = "readLines";

    /*
    public static final String READJSON = "readJson";

    public static final String JSONMATCH = "jsonMatch";
*/
    public static Action split(String path) {
        return new ActionSplit(path);
    }

    public static Action readLines(String path) {
        return new ActionReadLines(path);
    }

    public static Action readFiles(String pathOrVar) {
        return new ActionReadFiles(pathOrVar);
    }

    /*
    public static Action readJson(String pathOrVar) {
        return action(READJSON, pathOrVar);
    }
*/
}
