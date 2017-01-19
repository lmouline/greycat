package org.mwg.plugin;

import org.mwg.Graph;

public interface Plugin {

    void start(Graph graph);

    void stop();

}
