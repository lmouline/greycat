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
package org.mwg.internal.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.task.*;

class ActionSelectObject implements Action {

    private final TaskFunctionSelectObject _filter;

    ActionSelectObject(final TaskFunctionSelectObject filterFunction) {
        if (filterFunction == null) {
            throw new RuntimeException("filterFunction should not be null");
        }
        this._filter = filterFunction;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previous = ctx.result();
        final TaskResult next = ctx.wrap(null);
        final TaskResultIterator iterator = previous.iterator();
        Object nextElem = iterator.next();
        while (nextElem != null) {
            if (_filter.select(nextElem, ctx)) {
                if (nextElem instanceof BaseNode) {
                    Node casted = (Node) nextElem;
                    next.add(casted.graph().cloneNode(casted));
                } else {
                    next.add(nextElem);
                }
            }
            nextElem = iterator.next();
        }
        ctx.continueWith(next);
    }

    @Override
    public void serialize(StringBuilder builder) {
        throw new RuntimeException("SelectObject remote usage not managed yet, please use SelectScript instead !");
    }

    @Override
    public String toString() {
        return "selectObject()";
    }
}
