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
package greycat.model;

import greycat.Graph;
import greycat.Node;
import greycat.plugin.NodeFactory;
import greycat.plugin.Plugin;

public class ModelPlugin implements Plugin {

    @Override
    public final void start(final Graph graph) {
        graph.nodeRegistry().declaration(MetaClass.NAME).setFactory(new NodeFactory() {
            @Override
            public Node create(final long world, final long time, final long id, final Graph graph) {
                return new MetaClass(world, time, id, graph);
            }
        });
    }

    @Override
    public final void stop() {

    }

}
