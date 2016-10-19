package ml.ruleinference;

import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.AbstractMLNode;
import org.mwg.mlx.MLXPlugin;
import org.mwg.mlx.algorithm.ruleinference.BatchAprioriRuleNode;

import static org.junit.Assert.assertEquals;

/**
 * Created by andrey.boytsov on 19/10/2016.
 */
public class BatchAprioriRuleNodeTest {
    public static final String NAME = "BatchAprioriRuleNode";

    public static final String FEATURE = "f1";

    public static final String FEATURE2 = "f2";

    public static final String FEATURE3 = "f3";

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

        public void on(BatchAprioriRuleNode result) {
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

    private int verySimpleSequence[][] = new int[][]{{1,0,1},{1,0,1},{1,1,0},{1,0,1},{1,0,0},{1,1,1},{1,0,1},{1,0,1},
            {1,0,1},{0,1,1}};

    protected BatchAprioriRuleNodeTest.RuleJumpCallback runThroughVerysimpleSequence(BatchAprioriRuleNode gspNode){
        BatchAprioriRuleNodeTest.RuleJumpCallback rjc = new BatchAprioriRuleNodeTest.RuleJumpCallback(new String[]{FEATURE, FEATURE2, FEATURE3});

        for (int i = 0; i < verySimpleSequence.length; i++) {
            rjc.value = verySimpleSequence[i];
            gspNode.jump(i, new Callback<Node>() {
                @Override
                public void on(Node result) {
                    rjc.on((BatchAprioriRuleNode) result);
                }
            });
        }

        return rjc;
    }

    @Test
    public void testSimpleRule() {
        final Graph graph = new GraphBuilder().withPlugin(new MLXPlugin()).withScheduler(new NoopScheduler()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                BatchAprioriRuleNode gspNode = (BatchAprioriRuleNode) graph.newTypedNode(0, 0, BatchAprioriRuleNode.NAME);

                //gspNode.setProperty(AbstractAnySlidingWindowManagingNode.BUFFER_SIZE_KEY, Type.INT, 60);
                gspNode.set(AbstractMLNode.FROM,
                        FEATURE+AbstractMLNode.FROM_SEPARATOR+FEATURE2+AbstractMLNode.FROM_SEPARATOR+FEATURE3);
                gspNode.set(BatchAprioriRuleNode.SUPPORT_LIMIT_KEY, 3);

                BatchAprioriRuleNodeTest.RuleJumpCallback rjc = runThroughVerysimpleSequence(gspNode);
                rjc.test(new int[][][]{
                        {}, //0-sequences
                        {{0},{1},{2}}, //1-sequences
                        {{0,2}}, //2-sequences
                });
                gspNode.free();
                graph.disconnect(null);
            }
        });
    }
}
