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
package greycat.internal.task;

import greycat.Constants;
import greycat.DeferCounter;
import greycat.Node;
import greycat.internal.CoreDeferCounter;
import greycat.plugin.Job;
import greycat.Action;
import greycat.Callback;
import greycat.base.BaseNode;
import greycat.TaskContext;
import greycat.TaskResult;


class ActionTravelInTime implements Action {
    private final String _time;

    ActionTravelInTime(final String time) {
        _time = time;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final String flatTime = ctx.template(_time);
        long parsedTime;
        try {
            parsedTime = Long.parseLong(flatTime);
        } catch (Throwable t) {
            Double d = Double.parseDouble(flatTime);
            parsedTime = d.longValue();
        }
        ctx.setTime(parsedTime);
        final TaskResult previous = ctx.result();
        final DeferCounter defer = new CoreDeferCounter(previous.size());
        final int previousSize = previous.size();
        for (int i = 0; i < previousSize; i++) {
            Object loopObj = previous.get(i);
            if (loopObj instanceof BaseNode) {
                Node castedPreviousNode = (Node) loopObj;
                final int finalIndex = i;
                castedPreviousNode.travelInTime(parsedTime, new Callback<Node>() {
                    @Override
                    public void on(Node result) {
                        castedPreviousNode.free();
                        previous.set(finalIndex, result);
                        defer.count();
                    }
                });
            } else {
                defer.count();
            }
        }
        defer.then(new Job() {
            @Override
            public void run() {
                ctx.continueTask();
            }
        });
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.TRAVEL_IN_TIME);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append(_time);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
