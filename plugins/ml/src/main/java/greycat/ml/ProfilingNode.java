package greycat.ml;

import greycat.Node;
import greycat.Callback;

public interface ProfilingNode extends Node {
    /**
     * Main training function to learn from the the expected output,
     * The input features are defined through features extractions.
     *
     * @param callback Called when the learning is completed with the status of learning true/false
     */
    void learn(Callback<Boolean> callback);

    void learnWith(double[] values);

    /**
     * Main infer function to give a cluster ID,
     * The input features are defined through features extractions.
     *
     * @param callback Called when the infer is completed with the result of the predictions
     */
    void predict(Callback<double[]> callback);
}
