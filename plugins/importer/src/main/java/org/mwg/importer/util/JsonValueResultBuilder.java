package org.mwg.importer.util;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.task.TaskResult;

public class JsonValueResultBuilder {

    public static TaskResult build(JsonValue value) {
        if (value.isObject()) {
            return new JsonObjectResult((JsonObject) value);
        } else if (value.isArray()) {
            return new JsonArrayResult((JsonArray) value);
        } else if (value.isBoolean()) {
            return new JsonSingleResult(value.asBoolean());
        } else if (value.isNull()) {
            return null;
        } else if (value.isString()) {
            return new JsonSingleResult(value.asString());

        } else if (value.isNumber()) {
            return new JsonSingleResult(value.asDouble());
        } else {
            return null;
        }
    }

}
