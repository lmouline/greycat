package org.mwg.utility;

import org.mwg.plugin.AbstractPlugin;

public class VerbosePlugin extends AbstractPlugin {

    public VerbosePlugin() {
        super();
        declareTaskHookFactory(new VerboseHookFactory());
    }

}
