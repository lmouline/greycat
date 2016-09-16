package org.mwg;

import org.mwg.structure.StructurePlugin;

public class JCDecauxLevelDB {

    //@Test
    public void baseTest() {


        final Graph g = new GraphBuilder().withStorage(new LevelDBStorage("tempStorage")).withMemorySize(1000000).build();
        g.connect(connectionResult -> {

            WSServer graphServer = new WSServer(g, 8050);
            graphServer.start();

        });
    }

    public static void main(String[] args) {
        JCDecauxLevelDB test = new JCDecauxLevelDB();
        test.baseTest();
    }

}
