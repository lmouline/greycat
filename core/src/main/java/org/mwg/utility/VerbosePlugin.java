package org.mwg.utility;

import org.mwg.Graph;
import org.mwg.plugin.Plugin;

public class VerbosePlugin implements Plugin {

    @Override
    public void start(Graph graph) {
        graph.addGlobalTaskHook(new VerboseHook());
    }

    @Override
    public void stop() {

    }

}
