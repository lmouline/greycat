package org.mwg.importer.action;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import org.mwg.importer.util.JsonResult;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class ReadJson extends AbstractTaskAction {

    private final String _pathOrTemplate;

    ReadJson(String _pathOrTemplate) {
        this._pathOrTemplate = _pathOrTemplate;
    }

    @Override
    public void eval(TaskContext context) {
        JsonValue[] result = null;
        final String path = context.template(_pathOrTemplate);
        URI uri = URI.create(path);
        try {
            try (InputStreamReader isr = new InputStreamReader(uri.toURL().openStream())) {
                JsonValue firstElem = Json.parse(isr);
                if (firstElem.isArray()) {
                    JsonArray array = firstElem.asArray();
                    JsonValue[] values = new JsonValue[array.size()];
                    for (int i = 0; i < array.size(); i++) {
                        values[i] = array.get(i);
                    }
                    result = values;
                } else {
                    result = new JsonValue[]{firstElem};
                }
                isr.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            context.continueWith(new JsonResult(result));
        }
    }
}
