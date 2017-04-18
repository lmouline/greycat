package greycatMLTest.neuralnet;

import greycat.*;
import greycat.struct.DMatrix;
import greycat.struct.EGraph;
import greycat.struct.ENode;
import org.junit.Test;

/**
 * Created by assaad on 18/04/2017.
 */
public class TestTemporalStruct {
    @Test
    public void testTime() {
        Graph graph= GraphBuilder
                .newBuilder()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                Node node = graph.newNode(0,0);

                EGraph eg= (EGraph) node.getOrCreate("egraph", Type.EGRAPH);
                ENode en =eg.newNode();
                eg.setRoot(en);

                DMatrix matrix= (DMatrix)en.getOrCreate("matrix", Type.DMATRIX);

                matrix.init(3,3);
                matrix.set(0,0,0);
                matrix.set(1,1,1);
                matrix.set(2,2,2);
                System.out.println("at t0 before set: "+matrix.get(0,0)+" , "+matrix.get(1,1)+" , "+matrix.get(2,2)+" node time: "+node.time());

                node.travelInTime(1, new Callback<Node>() {
                    @Override
                    public void on(Node result) {
                        EGraph eg= (EGraph) result.getOrCreate("egraph", Type.EGRAPH);
                        ENode en =eg.root();
                        DMatrix matrix= (DMatrix)en.getOrCreate("matrix", Type.DMATRIX);
                        System.out.println("at t1 before set: "+matrix.get(0,0)+" , "+matrix.get(1,1)+" , "+matrix.get(2,2)+" node time: "+result.time());
                        matrix.set(0,0,10);
                        matrix.set(1,1,11);
                        matrix.set(2,2,12);
                        graph.save(new Callback<Boolean>() {
                            @Override
                            public void on(Boolean saved) {
                                result.travelInTime(0, new Callback<Node>() {
                                    @Override
                                    public void on(Node result) {
                                        EGraph eg= (EGraph) result.getOrCreate("egraph", Type.EGRAPH);
                                        ENode en =eg.root();
                                        DMatrix matrix= (DMatrix)en.getOrCreate("matrix", Type.DMATRIX);
                                        System.out.println("at t0 after set: "+matrix.get(0,0)+" , "+matrix.get(1,1)+" , "+matrix.get(2,2)+" node time: "+result.time());

                                        result.travelInTime(1, new Callback<Node>() {
                                            @Override
                                            public void on(Node result) {
                                                EGraph eg= (EGraph) result.getOrCreate("egraph", Type.EGRAPH);
                                                ENode en =eg.root();
                                                DMatrix matrix= (DMatrix)en.getOrCreate("matrix", Type.DMATRIX);
                                                System.out.println("at t1 after set: "+matrix.get(0,0)+" , "+matrix.get(1,1)+" , "+matrix.get(2,2)+" node time: "+result.time());

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
