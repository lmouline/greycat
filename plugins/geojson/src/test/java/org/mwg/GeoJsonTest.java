package org.mwg;

import org.junit.Test;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.common.structure.KDTree;
import org.mwg.plugin.geojson.GeoJsonPlugin;
import org.mwg.task.*;


import static org.mwg.plugin.geojson.GeoJsonActions.loadJson;
import static org.mwg.plugin.geojson.GeoJsonActions.newNodeFromJson;
import static org.mwg.task.Actions.newTypedNode;

public class GeoJsonTest {

    private final static String _baseParam = "?apiKey=9c84c41dfc20a1539e3cb1239efa65889b6341e4";
    private final static String _stationsListAddress = "https://api.jcdecaux.com/vls/v1/stations";


    @Test
    public void baseTest() {


        //API KEY:          9c84c41dfc20a1539e3cb1239efa65889b6341e4
        // GetStations:     https://api.jcdecaux.com/vls/v1/stations
        // Contract information GET https://api.jcdecaux.com/vls/v1/stations/{station_number}?contract={contract_name} HTTP/1.1

        final Graph g = new GraphBuilder().withPlugin(new GeoJsonPlugin()).withPlugin(new MLPlugin()).build();
        g.connect(connectionResult -> {
            final Task t = loadJson(_stationsListAddress + _baseParam)
                    .isolate(newTypedNode(KDTree.NAME).asVar("tree").indexNode("positionsTree", ""))
                    .foreachPar(
                            newNodeFromJson()
                                    .indexNode("stations", "contract_name, name")
                                    .asVar("node")
                                    .traverse("position")
                                    .then(new Action() {
                                        @Override
                                        public void eval(TaskContext context) {
                                            KDTree tree = (KDTree) context.variable("tree").get(0);
                                            Node n = context.resultAsNodes().get(0);
                                                tree.insert(
                                                        new double[]{(double) n.get("lat"), (double) n.get("lng")},
                                                        (Node) context.variable("node").get(0),
                                                        null);
                                            context.continueTask();
                                        }
                                    }).fromVar("node"))
                    .print("{{result}}");
            t.execute(g, null);
        });
    }


    private double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km
        return d;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

}
