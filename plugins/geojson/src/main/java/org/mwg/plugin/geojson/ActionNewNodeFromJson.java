package org.mwg.plugin.geojson;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Iterator;

/**
 * Created by gnain on 01/09/16.
 */
public class ActionNewNodeFromJson extends AbstractTaskAction {

    @Override
    public void eval(TaskContext context) {
        TaskResult<Node> res = context.newResult();
        JsonValue val = (JsonValue) context.result().get(0);
        if (val.isObject()) {
            JsonObject obj = val.asObject();
            context.setTime(obj.getLong("last_update", context.time()));
            res.add(buildNodeFromJsonObject(context, val.asObject()));
        }
        context.continueWith(res);
    }

    private Node buildNodeFromJsonObject(TaskContext context, JsonObject currentObject) {
        Graph g = context.graph();
        Node n = g.newNode(context.world(), context.time());
        //Node n = g.newNode(0,0);
        Iterator<JsonObject.Member> membersIt = currentObject.iterator();
        while (membersIt.hasNext()) {
            try {
                JsonObject.Member m = membersIt.next();
                JsonValue value = m.getValue();
                if (value.isObject()) {
                    n.add(m.getName(), buildNodeFromJsonObject(context, value.asObject()));
                } else if (value.isString()) {
                    n.set(m.getName(), value.asString());
                } else if (value.isBoolean()) {
                    n.set(m.getName(), value.asBoolean());
                } else if (value.isNumber()) {
                    try {
                        n.set(m.getName(), value.asInt());
                    } catch (Exception e) {
                        try {
                            n.set(m.getName(), value.asLong());
                        } catch (Exception e2) {
                            try {
                                n.set(m.getName(), value.asFloat());
                            } catch (Exception e3) {
                                n.set(m.getName(), value.asDouble());
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return n;
    }
}
