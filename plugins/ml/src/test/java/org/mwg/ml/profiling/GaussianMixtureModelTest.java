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
package org.mwg.ml.profiling;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.internal.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;

import java.util.Random;

public class GaussianMixtureModelTest {
    @Test
    public void mixtureTest() {
        final Graph graph = new GraphBuilder().withPlugin(new MLPlugin()).withScheduler(new NoopScheduler()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                double[] data = new double[3];
                Random rand = new Random();

                GaussianMixtureNode node1 = (GaussianMixtureNode) graph.newTypedNode(0, 0, GaussianMixtureNode.NAME);

                node1.set(GaussianMixtureNode.LEVEL, Type.INT, 1);
                node1.set(GaussianMixtureNode.WIDTH, Type.INT, 100);

                double[] sum = new double[3];
                int total = 220;

                for (int i = 0; i < total; i++) {
                    data[0] = 8 + rand.nextDouble() * 4; //avg =10, [8,12]
                    data[1] = 90 + rand.nextDouble() * 20; //avg=100 [90,110]
                    data[2] = -60 + rand.nextDouble() * 20; //avg=-50 [-60,-40]
                    //node1.setTrainingVector(data);

                    node1.learnWith(data);

                    sum[0] += data[0];
                    sum[1] += data[1];
                    sum[2] += data[2];
                }

                sum[0] = sum[0] / total;
                sum[1] = sum[1] / total;
                sum[2] = sum[2] / total;

                double eps = 1e-7;

                double[] res = node1.getAvg();
                for (int i = 0; i < 3; i++) {
                    Assert.assertTrue(Math.abs(res[i] - sum[i]) < eps);
                }
            }
        });
    }
}
