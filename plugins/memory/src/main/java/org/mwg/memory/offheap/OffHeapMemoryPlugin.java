package org.mwg.memory.offheap;

import org.mwg.plugin.AbstractPlugin;

public class OffHeapMemoryPlugin extends AbstractPlugin {

    public OffHeapMemoryPlugin() {
        declareMemoryFactory(new OffHeapMemoryFactory());
    }
}
