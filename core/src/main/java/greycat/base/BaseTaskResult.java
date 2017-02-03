/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greycat.base;

import greycat.Node;
import greycat.internal.task.TaskHelper;
import greycat.task.TaskResult;
import greycat.task.TaskResultIterator;

import java.util.ArrayList;

public class BaseTaskResult<A> implements TaskResult<A> {

    private Object[] _backend;
    private int _capacity = 0;
    private int _size = 0;

    Exception _exception = null;
    String _output = null;

    @Override
    public Object[] asArray() {
        Object[] flat = new Object[_size];
        if (_backend != null) {
            System.arraycopy(_backend, 0, flat, 0, _size);
        }
        return flat;
    }

    @Override
    public Exception exception() {
        return _exception;
    }

    @Override
    public String output() {
        return _output;
    }

    @Override
    public final TaskResult<A> setException(Exception e) {
        _exception = e;
        return this;
    }

    @Override
    public final TaskResult<A> setOutput(String output) {
        _output = output;
        return this;
    }

    @Override
    public TaskResult<A> fillWith(TaskResult<A> source) {
        if (source != null) {
            _backend = source.asArray();
            _capacity = _backend.length;
            _size = _backend.length;
        }
        return this;
    }

    public BaseTaskResult(Object toWrap, boolean protect) {
        if (toWrap instanceof Object[]) {
            Object[] castedToWrap = (Object[]) toWrap;
            _size = ((Object[]) toWrap).length;
            _capacity = _size;
            _backend = new Object[this._size];
            if (protect) {
                for (int i = 0; i < _size; i++) {
                    Object loopObj = castedToWrap[i];
                    if (loopObj instanceof BaseNode) {
                        Node loopNode = (Node) loopObj;
                        _backend[i] = loopNode.graph().cloneNode(loopNode);
                    } else {
                        _backend[i] = loopObj;
                    }
                }
            } else {
                System.arraycopy(castedToWrap, 0, _backend, 0, this._size);
            }
        } else if (toWrap instanceof long[]) {
            long[] castedOther = (long[]) toWrap;
            _backend = new Object[castedOther.length];
            for (int i = 0; i < castedOther.length; i++) {
                _backend[i] = castedOther[i];
            }
            _capacity = _backend.length;
            _size = _capacity;
        } else if (toWrap instanceof int[]) {
            int[] castedOther = (int[]) toWrap;
            _backend = new Object[castedOther.length];
            for (int i = 0; i < castedOther.length; i++) {
                _backend[i] = castedOther[i];
            }
            _capacity = _backend.length;
            _size = _capacity;
        } else if (toWrap instanceof double[]) {
            double[] castedOther = (double[]) toWrap;
            _backend = new Object[castedOther.length];
            for (int i = 0; i < castedOther.length; i++) {
                _backend[i] = castedOther[i];
            }
            _capacity = _backend.length;
            _size = _capacity;
        } else if (toWrap instanceof ArrayList) {
            ArrayList<Object> castedOtherList = (ArrayList<Object>) toWrap;
            _backend = new Object[castedOtherList.size()];
            for (int i = 0; i < castedOtherList.size(); i++) {
                _backend[i] = castedOtherList.get(i);
            }
            _capacity = _backend.length;
            _size = _capacity;
        } else if (toWrap instanceof BaseTaskResult) {
            BaseTaskResult other = (BaseTaskResult) toWrap;
            _size = other._size;
            _capacity = other._capacity;
            if (other._backend != null) {
                _backend = new Object[other._backend.length];
                if (protect) {
                    for (int i = 0; i < _size; i++) {
                        Object loopObj = other._backend[i];
                        if (loopObj instanceof BaseNode) {
                            Node loopNode = (Node) loopObj;
                            _backend[i] = loopNode.graph().cloneNode(loopNode);
                        } else {
                            _backend[i] = loopObj;
                        }
                    }
                } else {
                    System.arraycopy(other._backend, 0, _backend, 0, _size);
                }
            }
        } else {
            if (toWrap != null) {
                _backend = new Object[1];
                _capacity = 1;
                _size = 1;
                if (toWrap instanceof BaseNode) {
                    Node toWrapNode = (Node) toWrap;
                    if (protect) {
                        _backend[0] = toWrapNode.graph().cloneNode(toWrapNode);
                    } else {
                        _backend[0] = toWrapNode;
                    }
                } else {
                    _backend[0] = toWrap;
                }
            }
        }
    }

    @Override
    public TaskResultIterator iterator() {
        return new BaseTaskResultIterator(_backend);
    }

    @Override
    public A get(int index) {
        if (index < _size) {
            return (A) _backend[index];
        } else {
            return null;
        }
    }

    @Override
    public TaskResult<A> set(int index, A input) {
        if (index >= _capacity) {
            extendTil(index);
        }
        _backend[index] = input;
        if (index >= _size) {
            _size++;
        }
        return this;
    }

    @Override
    public TaskResult<A> allocate(int index) {
        if (index >= _capacity) {
            if (_backend == null) {
                _backend = new Object[index];
                _capacity = index;
            } else {
                throw new RuntimeException("Not implemented yet!!!");
            }
        }
        return this;
    }

    @Override
    public TaskResult<A> add(A input) {
        if (_size >= _capacity) {
            extendTil(_size);
        }
        set(_size, input);
        return this;
    }

    @Override
    public TaskResult<A> clear() {
        this._backend = null;
        this._capacity = 0;
        this._size = 0;
        return this;
    }

    @Override
    public TaskResult<A> clone() {
        return new BaseTaskResult<A>(this, true);
    }

    @Override
    public void free() {
        for (int i = 0; i < _capacity; i++) {
            if (_backend[i] instanceof BaseNode) {
                ((Node) _backend[i]).free();
            }
        }
    }

    @Override
    public int size() {
        return this._size;
    }

    private synchronized void extendTil(int index) {
        if (_capacity <= index) {
            int newCapacity = _capacity * 2;
            if (newCapacity <= index) {
                newCapacity = index + 1;
            }
            Object[] extendedBackend = new Object[newCapacity];
            if (_backend != null) {
                System.arraycopy(_backend, 0, extendedBackend, 0, _size);
            }
            _backend = extendedBackend;
            _capacity = newCapacity;
        }
    }

    @Override
    public String toString() {
        return toJson(true);
    }

    private String toJson(boolean withContent) {
        final StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        builder.append("{");
        if (_exception != null) {
            isFirst = false;
            builder.append("\"error\":");
            TaskHelper.serializeString(_exception.getMessage(), builder, false);
        }
        if (_output != null) {
            if (!isFirst) {
                builder.append(",");
            } else {
                isFirst = false;
            }
            builder.append("\"output\":");
            TaskHelper.serializeString(_output, builder, false);
        }
        if (_size > 0) {
            if (!isFirst) {
                builder.append(",");
            }
            builder.append("\"result\":[");
            for (int i = 0; i < _size; i++) {
                if (i != 0) {
                    builder.append(",");
                }
                Object loop = _backend[i];
                if (loop != null) {
                    String saved = loop.toString();
                    if (saved.length() > 0) {
                        if (saved.charAt(0) == '{' || saved.charAt(0) == '[') { //Array or Nodes
                            builder.append(saved);
                        } else {
                            //escape string
                            TaskHelper.serializeString(saved, builder, false);
                        }
                    }
                }
            }
            builder.append("]");
        }
        builder.append("}");
        return builder.toString();
    }


}
