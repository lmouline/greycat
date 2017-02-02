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

public class ActionMapTest extends AbstractActionTest {

    /*
    @Test
    public void test() {
        initGraph();
        task()
                .then(readIndexAll("nodes"))
                .map(node -> ((Node)node).get("name"))
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<String> names = context.resultAsStrings();
                        Assert.assertEquals(names.get(0), "n0");
                        Assert.assertEquals(names.get(1), "n1");
                        Assert.assertEquals(names.get(2), "root");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }*/

}
