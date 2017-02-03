package greycat.ml;

import greycat.Graph;
import greycat.Type;
import greycat.Callback;

public class NoopRegressionNode extends BaseMLNode implements RegressionNode {

    public static final String NAME = "NoopRegressionNode";

    public NoopRegressionNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    @Override
    public void learn(double output, final Callback<Boolean> callback) {
        extractFeatures(new Callback<double[]>() {
            @Override
            public void on(double[] result) {
                set("extracted", Type.DOUBLE_ARRAY, result);
                if (callback != null) {
                    callback.on(true);
                }
            }
        });


    }

    @Override
    public void extrapolate(Callback<Double> callback) {

    }

}
