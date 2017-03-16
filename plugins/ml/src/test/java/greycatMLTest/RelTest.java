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
package greycatMLTest;

import greycat.*;
import greycat.struct.Relation;
import org.junit.Test;

/**
 * Created by assaad on 15/03/2017.
 */
public class RelTest {

    @Test
    public void testRel(){
        Graph g= GraphBuilder
                .newBuilder()
                .withMemorySize(10000000)
                .build();

        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                final int size=10000;
                Node[] nodes=new Node[size];

                for(int i=0;i<size;i++){
                    nodes[i]= g.newNode(0,0);
                    nodes[i].setTimeSensitivity(10,0);
                    nodes[i].set("name", Type.STRING,"Node "+i);
                }

                int reltoadd=100;
                int counter=0;

                long start=System.currentTimeMillis();

                for(int time=0;time<reltoadd;time++) {
                    for(int i=0;i<size;i++){
                        int finalI = i;
                        int finalJ = time;
                        nodes[i].travelInTime(time, new Callback<Node>() {
                            @Override
                            public void on(Node result) {
                                Relation rel= (Relation) result.getOrCreate("conx",Type.RELATION);
                                rel.addNode(nodes[(finalI + finalJ +1) % size]);
                            }
                        });
                        if(i==0){
                            for(int k=0;k<=time;k++) {
                                int finalTime = time;
                                int finalK = k;
                                nodes[i].travelInTime(k, new Callback<Node>() {
                                    @Override
                                    public void on(Node result) {
                                        Relation rel= (Relation) result.getOrCreate("conx",Type.RELATION);
                                        System.out.println("time "+ finalTime+ " but get: "+ finalK +" size: "+rel.size());
                                    }
                                });
                            }
                            System.out.println("");
                        }
                        counter++;
                    }
                }
                long end=System.currentTimeMillis();
                double speed=end-start;
                speed=counter*1000/speed;
                System.out.println("time to add "+counter+" relations: "+(end-start)+"ms speed: "+speed+" rel/s");


            }
        });

    }
}
