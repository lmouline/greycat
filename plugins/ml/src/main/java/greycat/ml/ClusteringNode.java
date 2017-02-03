package greycat.ml;

import greycat.Node;
import greycat.Callback;

public interface ClusteringNode extends Node {

    /**
     * Main training function to learn from the the expected output,
     * The input features are defined through features extractions.
     *
     * @param callback Called when the learning is completed with the status of learning true/false
     */
    void learn(Callback<Boolean> callback);

    /**
     * Main infer function to give a cluster ID,
     * The input features are defined through features extractions.
     *
     * @param callback Called when the infer is completed with the result of the cluster ID
     */
    void inferCluster(Callback<Integer> callback);
}
