package org.mwg.plugin.geojson;

import com.eclipsesource.json.JsonValue;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

public class JsonResult implements TaskResult<JsonValue> {

    private JsonValue[] _content;

    JsonResult(JsonValue[] init) {
        _content = init;
    }

    @Override
    public TaskResultIterator iterator() {
        return new TaskResultIterator() {
            private int currentIndex = 0;

            @Override
            public Object next() {
                return (currentIndex < _content.length ? _content[currentIndex++] : null);
            }
        };
    }

    @Override
    public JsonValue get(int index) {
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
        return _content.length;
    }

    @Override
    public Object[] asArray() {
        return _content;
    }
}
