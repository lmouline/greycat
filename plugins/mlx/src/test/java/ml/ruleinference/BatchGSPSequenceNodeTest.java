package ml.ruleinference;

import ml.classifier.AbstractClassifierTest;
import org.junit.Test;
import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.AbstractMLNode;
import org.mwg.mlx.MLXPlugin;
import org.mwg.mlx.algorithm.AbstractAnySlidingWindowManagingNode;
import org.mwg.mlx.algorithm.AbstractClassifierSlidingWindowManagingNode;
import org.mwg.mlx.algorithm.classifier.GaussianClassifierNode;
import org.mwg.mlx.algorithm.ruleinference.BatchGSPSequenceNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by andrey.boytsov on 17/10/2016.
 */
public class BatchGSPSequenceNodeTest {

    public static final String FEATURE = "f1";

    public static final String FEATURE2 = "f2";

    /**
     * Created by andre on 5/9/2016.
     */
    protected static class RuleJumpCallback {

        final String features[];

        public RuleJumpCallback(String featureNames[]){
            this.features = featureNames;
        }

        public boolean correct = false;
        public int lastValue[][][] = new int[0][][];
        public int value[] = null;

        Callback<int[][][]> cb = new Callback<int[][][]>() {
            @Override
            public void on(int[][][] result) {
                //Need deep copy here
                lastValue = new int[result.length][][];
                for (int i=0;i<result.length;i++){
                    lastValue[i] = new int[result[i].length][];
                    for (int j=0;j<result[i].length;j++){
                        lastValue[i][j] = new int[result[i][j].length];
                        System.arraycopy(result[i][j],0,lastValue[i][j],0,result[i][j].length);
                    }
                }
            }
        };

        public void on(BatchGSPSequenceNode result) {
            for (int i=0;i<features.length;i++){
                result.set(features[i], value[i]);
            }
            result.learn(cb);
            result.free();
        }

        protected static String generateStringDescription(int val[][][]){
            StringBuilder res = new StringBuilder();
            for (int i=0;i<val.length;i++){
                res.append("\t"+i+"-sequences:\n\t");
                for (int j=0;j<val[i].length;j++){
                    res.append("[");
                    for (int k=0;k<val[i][j].length;k++){
                        res.append(val[i][j][k]);
                        res.append(", ");
                    }
                    res.append("],  ");
                }
                res.append("\n");
            }
            return res.toString();
        }

        //TODO Make test function less brittle (see below)
        public void test(int [][][] expectedResult){
            StringBuilder errorStringBuilder = new StringBuilder();
            errorStringBuilder.append("Wrong sequences. Expected: \n");
            errorStringBuilder.append(generateStringDescription(expectedResult));
            errorStringBuilder.append("Found:\n");
            errorStringBuilder.append(generateStringDescription(lastValue));
            String errorString = errorStringBuilder.toString();

            assertEquals(errorString, expectedResult.length, lastValue.length);
            for (int i=0;i<expectedResult.length;i++){
                assertEquals(errorString, expectedResult[i].length, lastValue[i].length);
                //TODO Order does not really matter here
                for (int j=0;j<expectedResult[i].length;j++){
                    //TODO But still it matters here
                    assertEquals(errorString, expectedResult[i][j].length, lastValue[i][j].length);
                    for (int k=0;k<expectedResult[i][j].length;k++){
                        assertEquals(errorString, expectedResult[i][j][k], lastValue[i][j][k]);
                    }
                }
            }
        }
    }

    private int verySimpleSequence[] = new int[]{1,2,3,1,2,3,1,2,3,4};

    private int verySimple2DSequence[][] = new int[][]{{1,2},{2,4},{3,6},{1,2},{2,4},{3,6},{1,2},{2,4},{3,6},{4,8}};

    protected RuleJumpCallback runThroughVerysimpleSequence(BatchGSPSequenceNode gspNode){
        RuleJumpCallback rjc = new RuleJumpCallback(new String[]{FEATURE});

        for (int i = 0; i < verySimpleSequence.length; i++) {
            rjc.value = new int[]{verySimpleSequence[i]};
            gspNode.jump(i, new Callback<Node>() {
                @Override
                public void on(Node result) {
                    rjc.on((BatchGSPSequenceNode) result);
                }
            });
        }

        return rjc;
    }

    protected RuleJumpCallback runThroughVerysimple2DSequence(BatchGSPSequenceNode gspNode){
        RuleJumpCallback rjc = new RuleJumpCallback(new String[]{FEATURE, FEATURE2});

        for (int i = 0; i < verySimple2DSequence.length; i++) {
            rjc.value = verySimple2DSequence[i];
            gspNode.jump(i, new Callback<Node>() {
                @Override
                public void on(Node result) {
                    rjc.on((BatchGSPSequenceNode) result);
                }
            });
        }

        return rjc;
    }


    @Test
    public void test() {
        final Graph graph = new GraphBuilder().withPlugin(new MLXPlugin()).withScheduler(new NoopScheduler()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                BatchGSPSequenceNode gspNode = (BatchGSPSequenceNode) graph.newTypedNode(0, 0, BatchGSPSequenceNode.NAME);

                //node.setProperty(AbstractAnySlidingWindowManagingNode.BUFFER_SIZE_KEY, Type.INT, 60);
                gspNode.set(AbstractMLNode.FROM, FEATURE);
                gspNode.set(BatchGSPSequenceNode.SUPPORT_LIMIT_KEY, 3);

                RuleJumpCallback rjc = runThroughVerysimpleSequence(gspNode);
                rjc.test(new int[][][]{
                        {}, //0-sequences
                        {{1},{2},{3}}, //1-sequences
                        {{1,2},{2,3}}, //2-sequences
                        {{1,2,3}}, //3-sequences
                });
                gspNode.free();
                graph.disconnect(null);
            }
        });
    }

    @Test
    public void test2D() {
        final Graph graph = new GraphBuilder().withPlugin(new MLXPlugin()).withScheduler(new NoopScheduler()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                BatchGSPSequenceNode gspNode = (BatchGSPSequenceNode) graph.newTypedNode(0, 0, BatchGSPSequenceNode.NAME);

                //gspNode.setProperty(AbstractAnySlidingWindowManagingNode.BUFFER_SIZE_KEY, Type.INT, 60);
                gspNode.set(AbstractMLNode.FROM, FEATURE+AbstractMLNode.FROM_SEPARATOR+FEATURE2);
                gspNode.set(BatchGSPSequenceNode.SUPPORT_LIMIT_KEY, 3);

                RuleJumpCallback rjc = runThroughVerysimple2DSequence(gspNode);
                rjc.test(new int[][][]{
                        {}, //0-sequences
                        {{1},{2},{3}}, //1-sequences
                        {{1,2},{2,3}}, //2-sequences
                        {{1,2,3}}, //3-sequences
                });
                gspNode.free();
                graph.disconnect(null);
            }
        });
    }

    @Test
    public void test2DOtherChannel() {
        final Graph graph = new GraphBuilder().withPlugin(new MLXPlugin()).withScheduler(new NoopScheduler()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                BatchGSPSequenceNode gspNode = (BatchGSPSequenceNode) graph.newTypedNode(0, 0, BatchGSPSequenceNode.NAME);

                //gspNode.setProperty(AbstractAnySlidingWindowManagingNode.BUFFER_SIZE_KEY, Type.INT, 60);
                gspNode.set(AbstractMLNode.FROM, FEATURE+AbstractMLNode.FROM_SEPARATOR+FEATURE2);
                gspNode.set(BatchGSPSequenceNode.SUPPORT_LIMIT_KEY, 3);
                gspNode.set(BatchGSPSequenceNode.RELEVANT_FEATURE_KEY, 1);

                RuleJumpCallback rjc = runThroughVerysimple2DSequence(gspNode);
                rjc.test(new int[][][]{
                        {}, //0-sequences
                        {{2},{4},{6}}, //1-sequences
                        {{2,4},{4,6}}, //2-sequences
                        {{2,4,6}}, //3-sequences
                });
                gspNode.free();
                graph.disconnect(null);
            }
        });
    }
}
