/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.base;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;

public class BaseHook implements TaskHook {

    @Override
    public void start(TaskContext initialContext) {
        //NOOP
    }

    @Override
    public void beforeAction(Action action, TaskContext context) {
        //NOOP
    }

    @Override
    public void afterAction(Action action, TaskContext context) {
        //NOOP
    }

    @Override
    public void beforeTask(TaskContext parentContext, TaskContext context) {
        //NOOP
    }

    @Override
    public void afterTask(TaskContext context) {
        //NOOP
    }

    @Override
    public void end(TaskContext finalContext) {
        //NOOP
    }

}
