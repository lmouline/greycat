package greycat.importer;

import greycat.plugin.Plugin;
import greycat.task.Action;
import greycat.Graph;
import greycat.Type;
import greycat.plugin.ActionFactory;

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
