package org.mwg.plugin.geojson;

import org.mwg.plugin.AbstractPlugin;
import org.mwg.task.TaskAction;
import org.mwg.task.TaskActionFactory;

/**
 * Created by gnain on 01/09/16.
 */
public class GeoJsonPlugin extends AbstractPlugin {

    public GeoJsonPlugin() {

        declareTaskAction(GeoJsonActions.LOADJSON, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(GeoJsonActions.LOADJSON + " action need one parameter");
                }
                return new ActionLoadJson(params[0]);
            }
        });

        declareTaskAction(GeoJsonActions.NEW_NODE_FROM_JSON, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                return new ActionNewNodeFromJson();
            }
        });

    }
}
