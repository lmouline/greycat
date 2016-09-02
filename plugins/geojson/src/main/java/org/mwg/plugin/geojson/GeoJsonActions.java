package org.mwg.plugin.geojson;

import org.mwg.task.Task;

import static org.mwg.task.Actions.newTask;

/**
 * Created by gnain on 01/09/16.
 */
public class GeoJsonActions {


    public static final String LOADJSON = "loadJson";

    public static Task loadJson(String path) {
        return newTask().action(LOADJSON, path);
    }

    public static final String NEW_NODE_FROM_JSON = "newNodeFromJson";

    public static Task newNodeFromJson() {
        return newTask().action(NEW_NODE_FROM_JSON,"");
    }

}
