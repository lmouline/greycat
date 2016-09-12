package org.mwg.importer.util;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

import java.util.Iterator;
import java.util.List;

public class JsonObjectResult implements TaskResult<JsonValue> {

    private final JsonObject _content;

    public JsonObjectResult(JsonObject init) {
        _content = init;
    }

    @Override
    public TaskResultIterator iterator() {
        return new TaskResultIterator() {
            private int currentIndex = 0;
            private Iterator<JsonObject.Member> subIt = _content.iterator();

            @Override
            public Object next() {
                JsonObject.Member member = subIt.next();
            }
        };
    }

    private class JsonValueIterator implements TaskResultIterator {

        private int currentIndex = 0;

        public JsonValueIterator() {
            if (_content.isObject()) {
                JsonObject
            }
        }

        @Override
        public Object next() {
            return null;
        }
    }

    @Override
    public JsonValue get(int index) {
        if (_content == null) {
            return null;
        }
        return (index < 1 ? _content : null);
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
        return 1;
    }

    @Override
    public Object[] asArray() {
        return new Object[]{_content};
    }
}
