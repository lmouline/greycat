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
package greycat.ml;

import greycat.Action;
import greycat.Graph;
import greycat.Node;
import greycat.Type;
import greycat.ml.regression.LiveLinearRegressionNode;
import greycat.ml.regression.PolynomialNode;
import greycat.ml.regression.actions.ReadContinuous;
import greycat.ml.regression.actions.SetContinuous;
import greycat.plugin.ActionFactory;
import greycat.plugin.NodeFactory;
import greycat.plugin.Plugin;

public class MLPlugin  implements Plugin  {

    @Override
    public void start(Graph graph) {
        graph.actionRegistry()
                .declaration(greycat.ml.regression.actions.ReadContinuous.NAME)
                .setParams(Type.STRING)
                .setFactory(new ActionFactory() {
                    @Override
                    public Action create(Object[] params) {
                        return new ReadContinuous((String) params[0]);
                    }
                });
        graph.actionRegistry()
                .declaration(SetContinuous.NAME)
                .setParams(Type.STRING, Type.STRING)
                .setFactory(new ActionFactory() {
                    @Override
                    public Action create(Object[] params) {
                        return new SetContinuous((String) params[0], (String) params[1]);
                    }
                });
        graph.nodeRegistry()
                .declaration(PolynomialNode.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new PolynomialNode(world, time, id, graph);
                    }
                });

        graph.nodeRegistry()
                .declaration(LiveLinearRegressionNode.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new LiveLinearRegressionNode(world, time, id, graph);
                    }
                });
    }

    @Override
    public void stop() {

    }
}
