package org.mwg;

import org.mwg.mlext.NeuralNetAttribute;
import org.mwg.plugin.AbstractPlugin;

public class MLExtPlugin extends AbstractPlugin {

    public MLExtPlugin() {
        declareExternalAttribute("NeuralNetAttribute", () -> new NeuralNetAttribute());
    }
}
