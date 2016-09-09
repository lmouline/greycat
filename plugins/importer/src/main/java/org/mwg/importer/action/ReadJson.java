package org.mwg.importer.action;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import org.mwg.importer.util.JsonResult;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;

import java.io.*;
import java.net.URI;

public class ReadJson extends AbstractTaskAction {

    private final String _pathOrTemplate;

    public ReadJson(String _pathOrTemplate) {
        this._pathOrTemplate = _pathOrTemplate;
    }

    @Override
    public void eval(TaskContext context) {
        JsonValue[] result = null;
        final String path = context.template(_pathOrTemplate);
        InputStream foundStream = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                foundStream = new FileInputStream(file);
            } else {
                foundStream = this.getClass().getClassLoader().getResourceAsStream(path);
            }
            if (foundStream.available() <= 0) {
                foundStream = null;
            }
        } catch (Exception e) {
            foundStream = null;
        }
        if (foundStream == null) {
            try {
                URI uri = URI.create(path);
                foundStream = uri.toURL().openStream();
            } catch (Exception e) {
                foundStream = null;
            }
        }
        if (foundStream != null) {
            try {
                InputStreamReader reader = new InputStreamReader(foundStream);
                JsonValue firstElem = Json.parse(reader);
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
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    foundStream.close();
                } catch (Exception e) {

                }
            }
        }
        context.continueWith(new JsonResult(result));
    }

}
