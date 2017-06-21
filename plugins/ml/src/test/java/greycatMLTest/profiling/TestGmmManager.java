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
package greycatMLTest.profiling;

import greycat.*;
import greycat.internal.custom.NDTree;
import greycat.ml.profiling.GmmManager;
import greycat.struct.EGraph;
import org.junit.Test;

/**
 * Created by assaad on 21/06/2017.
 */
public class TestGmmManager {
    @Test
    public void Test() {
        Graph graph = GraphBuilder
                .newBuilder()
                .build();

        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                Node host = graph.newNode(0, 0);

                EGraph ndTree = (EGraph) host.getOrCreate("graphNDTree", Type.EGRAPH);
                EGraph gmmTree = (EGraph) host.getOrCreate("graphgmm", Type.EGRAPH);
                GmmManager manager = new GmmManager(gmmTree);

                NDTree tree = new NDTree(ndTree, manager);


            }
        });

    }
}
