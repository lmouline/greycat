package org.mwg.importer.util;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

public class JsonMemberResult implements TaskResult<JsonValue> {

    private final JsonObject.Member _member;

    public JsonMemberResult(JsonObject.Member member) {
        _member = member;
    }

    @Override
    public TaskResultIterator iterator() {
        return new TaskResultIterator() {
            private int currentIndex = 0;

            @Override
            public Object next() {
                Object result = null;
                if (currentIndex == 0) {
                    result = _member.getName();
                }
                if (currentIndex == 1) {
                    if (_member.getValue().isObject()) {
                        result = new JsonObjectResult((JsonObject) _member.getValue());
                    } else if (_member.getValue().isArray()) {
                        result = new JsonArrayResult((JsonArray) _member.getValue());
                    } else {
                        //TODO
                    }
                    result = new JsonV_member.getValue();
                }
                currentIndex++;
                return result;
            }
        };
    }

    @Override
    public JsonValue get(int index) {
        if (_content == null) {
            return null;
        }
        return (index < _content.length ? _content[index] : null);
    }

    @Override
    public void set(int index, JsonValue input) {
    }

    @Override
    public void allocate(int index) {
    }

    @Override
    public void add(JsonValue input) {
    }

    @Override
    public void clear() {
    }

    @Override
    public TaskResult<JsonValue> clone() {
        return this;
    }

    @Override
    public void free() {

    }

    @Override
    public int size() {
        if (_content == null) {
            return 0;
        }
        return _content.length;
    }

    @Override
    public Object[] asArray() {
        return _content;
    }
}
