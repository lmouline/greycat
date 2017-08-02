package test;

import greycat.Callback;
import greycat.Graph;
import greycat.GraphBuilder;
import greycat.scheduler.NoopScheduler;
import model.GPSPosition;
import model.ModelPlugin;
import model.SmartCity;
import org.junit.Assert;

/**
 * Created by Gregory NAIN on 02/08/2017.
 */
public class CustomTypesTest {
    private static long cityNodeId;
    private static MockStorage storage = new MockStorage();

    public static void main(String[] args) {


        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).withStorage(storage).withPlugin(new ModelPlugin()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                SmartCity city = SmartCity.create(0,0, g);
                cityNodeId = city.id();
                
                GPSPosition location = city.getOrCreateLocation();
                location.setLat(1.2);
                location.setLat(3.4);
                
                g.save((saved) -> {

                    Graph graph2 = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).withStorage(storage).withPlugin(new ModelPlugin()).build();
                    graph2.connect(new Callback<Boolean>() {
                        @Override
                        public void on(Boolean result) {
                            graph2.lookup(0,0, cityNodeId, city ->{
                                SmartCity retreivedCity = (SmartCity) city;
                                GPSPosition location = retreivedCity.getLocation();
                                Assert.assertTrue(location.getLat() == 1.2);
                            });
                        }
                    });

                });
            }
        });
        
        
    }
    
}
