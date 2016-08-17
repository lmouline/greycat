package org.mwg.memory.offheap;

import org.mwg.plugin.AbstractPlugin;

public class OffHeapPlugin extends AbstractPlugin {

    public OffHeapPlugin() {
        declareMemoryFactory(new OffHeapMemoryFactory());
    }
}
