/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
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
package org.mwg.ml.algorithm.regression.actions;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.Type;
import org.mwg.internal.task.TaskHelper;
import org.mwg.ml.algorithm.regression.PolynomialNode;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.internal.task.CoreActions.readVar;
import static org.mwg.task.Tasks.newTask;

public class SetPrecision implements Action {

    public final static String NAME = "setPrecision";
    private final Task polyTask;

    private final String _relName;
    private final String _value;

    public SetPrecision(final String relName, final String c_value) {
        if (relName == null || c_value == null) {
            throw new RuntimeException("name or value should not be null");
        }
        this._relName = relName;
        this._value = c_value;
        polyTask = newTask()
                .then(defineAsVar("origin"))
                .then(traverse(_relName))
                .then(setAttribute(PolynomialNode.PRECISION, Type.DOUBLE, _value))
                .then(readVar("origin"));
    }

    @Override
    public void eval(final TaskContext context) {
        polyTask.executeWith(context.graph(), context.result(), new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                context.continueWith(result);
            }
        });
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(NAME);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_relName, builder,true);
        builder.append(Constants.TASK_PARAM_SEP);
        TaskHelper.serializeString(_value, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
