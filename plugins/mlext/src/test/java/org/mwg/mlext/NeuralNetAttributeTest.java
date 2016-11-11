package org.mwg.mlext;

import org.junit.Test;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.MLExtPlugin;
import org.mwg.Node;

public class NeuralNetAttributeTest {

    @Test
    public void test() {
        Graph g = new GraphBuilder().withPlugin(new MLExtPlugin()).build();
        g.connect(result -> {

            Node node = g.newNode(0, 0);
            NeuralNetAttribute nn = (NeuralNetAttribute) node.getOrCreateExternal("nn",NeuralNetAttribute.NAME);
            nn.reconf();

            g.save(result1 -> {
                g.disconnect(null);
            });

        });

    }

}
