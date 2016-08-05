package org.mwg.core;

import org.mwg.GraphBuilder;
import org.mwg.core.scheduler.TrampolineScheduler;
import org.mwg.core.task.CoreTask;
import org.mwg.core.utility.ReadOnlyStorage;
import org.mwg.plugin.Plugin;
import org.mwg.plugin.Scheduler;
import org.mwg.plugin.Storage;
import org.mwg.task.Task;

public class Builder implements GraphBuilder.InternalBuilder {

    @Override
    public org.mwg.Graph newGraph(Storage p_storage, boolean p_readOnly, Scheduler p_scheduler, Plugin[] p_plugins, long p_memorySize, long p_autoSaveSize) {
        Storage storage = p_storage;
        if (storage == null) {
            storage = new BlackHoleStorage();
        }
        if (p_readOnly) {
            storage = new ReadOnlyStorage(storage);
        }
        Scheduler scheduler = p_scheduler;
        if (scheduler == null) {
            scheduler = new TrampolineScheduler();
        }
        long memorySize = p_memorySize;
        if (memorySize == -1) {
            memorySize = 100000;
        }
        long autoSaveSize = p_autoSaveSize;
        if (p_autoSaveSize == -1) {
            autoSaveSize = memorySize;
        }
        return new org.mwg.core.CoreGraph(storage, memorySize, autoSaveSize, scheduler, p_plugins);
    }

    @Override
    public Task newTask() {
        return new CoreTask();
    }

}

