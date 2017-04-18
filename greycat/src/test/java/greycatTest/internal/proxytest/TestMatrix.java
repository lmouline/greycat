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
package greycatTest.internal.proxytest;

import greycat.*;
import greycat.struct.DMatrix;
import org.junit.Test;

/**
 * Created by assaad on 18/04/2017.
 */
public class TestMatrix {

    @Test
    public void testMatrix() {
        Graph graph= GraphBuilder
                .newBuilder()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                Node node = graph.newNode(0,0);
                DMatrix matrix= (DMatrix)node.getOrCreate("matrix", Type.DMATRIX);

                matrix.init(3,3);
                matrix.set(0,0,0);
                matrix.set(1,1,1);
                matrix.set(2,2,2);

                System.out.println("at time: "+node.time()+" before set: "+matrix.get(0,0)+" , "+matrix.get(1,1)+" , "+matrix.get(2,2));

                node.travelInTime(1, new Callback<Node>() {
                    @Override
                    public void on(Node node1) {
                        DMatrix matrix= (DMatrix)node1.getOrCreate("matrix", Type.DMATRIX);
                        System.out.println("at time: "+node1.time()+" before set: "+matrix.get(0,0)+" , "+matrix.get(1,1)+" , "+matrix.get(2,2));
                        matrix.set(0,0,10);
                        matrix.set(1,1,11);
                        matrix.set(2,2,12);
                        graph.save(new Callback<Boolean>() {
                            @Override
                            public void on(Boolean saved) {
                                node1.travelInTime(0, new Callback<Node>() {
                                    @Override
                                    public void on(Node node2) {
                                        DMatrix matrix= (DMatrix)node2.getOrCreate("matrix", Type.DMATRIX);
                                        System.out.println("at time: "+node2.time()+" after set: "+matrix.get(0,0)+" , "+matrix.get(1,1)+" , "+matrix.get(2,2));

                                        node2.travelInTime(1, new Callback<Node>() {
                                            @Override
                                            public void on(Node node3) {
                                                DMatrix matrix= (DMatrix)node3.getOrCreate("matrix", Type.DMATRIX);
                                                System.out.println("at time: "+node3.time()+" after set: "+matrix.get(0,0)+" , "+matrix.get(1,1)+" , "+matrix.get(2,2));

                                            }
                                        });


                                    }
                                });



                            }
                        });


                    }
                });

            }
        });

    }
}
