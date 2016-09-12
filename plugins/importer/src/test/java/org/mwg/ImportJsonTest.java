package org.mwg;

import org.junit.Test;
import org.mwg.importer.ImporterActions;
import org.mwg.importer.ImporterPlugin;

import static org.mwg.task.Actions.foreach;
import static org.mwg.task.Actions.print;

public class ImportJsonTest {

    @Test
    public void testReadJson() {
        final Graph g = new GraphBuilder().withPlugin(new ImporterPlugin()).build();
        g.connect(result -> {

            ImporterActions.readJson("sample.geojson")
                    .foreach(foreach(print("{{result}}")))
                    .execute(g, null);

            g.disconnect(null);
        });
    }


}
