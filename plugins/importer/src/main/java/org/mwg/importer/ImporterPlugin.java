package org.mwg.importer;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.plugin.ActionFactory;
import org.mwg.plugin.Plugin;
import org.mwg.task.Action;

public class ImporterPlugin implements Plugin {

    @Override
    public void start(Graph graph) {
        graph.actionRegistry().declaration(ImporterActions.READFILES).setParams(Type.STRING).setFactory(new ActionFactory() {
            @Override
            public Action create(Object[] params) {
                return new ActionReadFiles((String) params[0]);
            }
        });
        graph.actionRegistry().declaration(ImporterActions.READLINES).setParams(Type.STRING).setFactory(new ActionFactory() {
            @Override
            public Action create(Object[] params) {
                return new ActionReadLines((String) params[0]);
            }
        });
    }

    @Override
    public void stop() {

    }


}
