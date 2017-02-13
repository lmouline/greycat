package greycat.ml.neuralnet.layers;

import greycat.struct.ENode;

public class Registry {

    public static Layer toLayer(ENode node) {
        switch ((int) node.get("type")) {
            case FeedForwardLayer.TYPE:
                return new FeedForwardLayer(node);
        }
        return null;
    }

}
