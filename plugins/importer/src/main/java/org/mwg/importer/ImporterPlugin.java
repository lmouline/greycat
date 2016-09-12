package org.mwg.importer;

import org.mwg.importer.action.ReadFiles;
import org.mwg.importer.action.ReadJson;
import org.mwg.importer.action.ReadLines;
import org.mwg.plugin.AbstractPlugin;
import org.mwg.task.TaskAction;
import org.mwg.task.TaskActionFactory;

public class ImporterPlugin extends AbstractPlugin {

    public ImporterPlugin() {
        declareTaskAction(ImporterActions.READLINES, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ImporterActions.READLINES + " action need one parameter");
                }
                return new ReadLines(params[0]);
            }
        });
        declareTaskAction(ImporterActions.READFILES, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ImporterActions.READFILES + " action need one parameter");
                }
                return new ReadFiles(params[0]);
            }
        });

        declareTaskAction(ImporterActions.READJSON, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ImporterActions.READFILES + " action need one parameter");
                }
                return new ReadJson(params[0]);
            }
        });

    }
}
