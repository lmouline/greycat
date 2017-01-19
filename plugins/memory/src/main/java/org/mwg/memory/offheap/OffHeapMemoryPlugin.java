package org.mwg.memory.offheap;

import org.mwg.Graph;
import org.mwg.plugin.Plugin;

public class OffHeapMemoryPlugin implements Plugin {

    @Override
    public void start(Graph graph) {
        graph.setMemoryFactory(new OffHeapMemoryFactory());
    }

    @Override
    public void stop() {

    }

}
