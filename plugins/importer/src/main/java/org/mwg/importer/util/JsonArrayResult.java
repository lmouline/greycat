package org.mwg.importer.util;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

import java.util.Iterator;

public class JsonArrayResult implements TaskResult {

    private final JsonArray _content;

    public JsonArrayResult(JsonArray init) {
        _content = init;
    }

    @Override
    public TaskResultIterator iterator() {
        return new TaskResultIterator() {
            private Iterator<JsonValue> it = _content.iterator();

            @Override
            public Object next() {
                if(it.hasNext()){
                    return JsonValueResultBuilder.build(it.next());
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public Object get(int index) {
        return JsonValueResultBuilder.build(_content.get(index));
    }

    @Override
    public void set(int index, Object input) {
    }

    @Override
    public void allocate(int index) {
    }

    @Override
    public void add(Object input) {
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
        return _content.size();
    }

    @Override
    public Object[] asArray() {
        Object[] flat = new Object[_content.size()];
        for (int i = 0; i < _content.size(); i++) {
            flat[i] = _content.get(i);
        }
        return flat;
    }

    @Override
    public String toString(){
        return _content.toString();
    }

}
