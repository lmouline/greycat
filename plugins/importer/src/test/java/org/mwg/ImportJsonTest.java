package org.mwg;

import org.junit.Test;
import org.mwg.importer.ImporterActions;
import org.mwg.importer.ImporterPlugin;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.utility.VerbosePlugin;

import static org.mwg.task.Actions.*;

public class ImportJsonTest {

    @Test
    public void testReadJson() {
        final Graph g = new GraphBuilder()
                // .withPlugin(new VerbosePlugin())
                .withPlugin(new ImporterPlugin())
                .build();
        g.connect(result -> {

            ImporterActions.readJson("sample.geojson")
                    .foreach(
                            ImporterActions.jsonMatch("features",
                                    foreach(
                                            defineVar("jsonObj")

                                            .print("{{result}}")
                                    )
                            )).execute(g, null);

            g.disconnect(null);
        });
    }


}
