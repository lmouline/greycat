package org.mwg.ml.neuralnet;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.ml.MLPlugin;

import java.util.Random;

public class FlatNeuralNodeTest {

    public static void main(String[] arg) {
        Graph g = new GraphBuilder().withPlugin(new MLPlugin()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                //Node root = g.newNode(0, 0);
                FlatNeuralNode nn = (FlatNeuralNode) g.newTypedNode(0, 0, FlatNeuralNode.NAME);
                nn.configure(4, 2, 2, 3, 0.01);
                double[] inputs = {0.01, 0.003, -0.5, 0.3};
                double[] outputs = nn.predict(inputs);
                nn.learn(inputs, outputs);
                for (int i = 0; i < outputs.length; i++) {
                    System.out.println(outputs[i]);
                }
            }
        });
    }
}
