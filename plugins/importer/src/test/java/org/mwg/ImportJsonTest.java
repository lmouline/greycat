package org.mwg;

import org.junit.Test;
import org.mwg.importer.ImporterActions;
import org.mwg.importer.ImporterPlugin;

public class ImportJsonTest {

    @Test
    public void testReadJson() {
        final Graph g = new GraphBuilder().withPlugin(new ImporterPlugin()).build();
        g.connect(result -> {

            ImporterActions.readJson("sample.geojson").execute(g,null);

            g.disconnect(null);
        });
    }


}
