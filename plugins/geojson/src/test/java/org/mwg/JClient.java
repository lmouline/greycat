package org.mwg;

import org.mwg.chunk.StateChunk;
import org.mwg.chunk.TimeTreeChunk;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.geojson.GeoJsonPlugin;
import org.mwg.struct.Buffer;
import org.mwg.structure.StructurePlugin;

import java.util.Arrays;

/**
 * Created by gnain on 14/09/16.
 */
public class JClient {


    public static void main(String[] args) {

        final Graph g = new GraphBuilder().withMemorySize(10000000).withPlugin(new StructurePlugin()).withStorage(new WSClient("ws://localhost:8050")).build();
        g.connect(connectionResult -> {
            g.lookup(0, Constants.END_OF_TIME, 25, new Callback<Node>() {
                @Override
                public void on(Node result) {

                    result.timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, result1 -> {
                        System.out.println(Arrays.toString(result1));
                    });

                    //ugly internal api :-)
                    AbstractNode casted = (AbstractNode) result;
                    StateChunk state = (StateChunk) g.space().get(casted._index_stateChunk);
                    TimeTreeChunk timeTreeChunk = (TimeTreeChunk) g.space().get(casted._index_timeTree);
                    TimeTreeChunk superTimeTreeChunk = (TimeTreeChunk) g.space().get(casted._index_superTimeTree);

                    Buffer b = g.newBuffer();
                    state.save(b);

                    System.out.println(result);
                    System.out.println(b);

                }
            });
        });



    }

}
