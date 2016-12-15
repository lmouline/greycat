package rocksdb;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.RocksDBStorage;
import org.mwg.core.task.Actions;
import org.mwg.task.Task;

import java.io.File;

/**
 * Created by ludovicmouline on 15/12/2016.
 */
public class IndexTest {

    @Test
    public void indexWithDB() {
        //Bug: Only with storage, if an empty index is only read (without modification), saved,
        // and read again after a connection, a NullPointerException is thrown

        final Task readIdx = Actions.newTask()
                .travelInTime(System.currentTimeMillis() + "")
                .travelInWorld("0")
                .readGlobalIndex("index","idx_id","complexOne_complexTwo")
                .save();

        String url = "dbStorage";
        File location = new File(url);
        location.deleteOnExit();
        if (!location.exists()) {
            location.mkdirs();
        }


        boolean exception = false;
        Graph graph = new GraphBuilder().withStorage(new RocksDBStorage("dbStorage")).build();
        try {

            graph.connect(new Callback<Boolean>() {
                @Override
                public void on(Boolean result) {

                    readIdx.executeSync(graph);

                    graph.disconnect(succeed -> {
                        graph.connect(ok -> {
                            readIdx.executeSync(graph);
                        });
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            exception = true;
        } finally {
            graph.disconnect(null);
        }

        Assert.assertEquals(false,exception);
    }
}
