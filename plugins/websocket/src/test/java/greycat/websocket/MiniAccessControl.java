package greycat.websocket;

import greycat.*;
import greycat.scheduler.NoopScheduler;
import greycatTest.internal.MockStorage;

import static greycat.Tasks.newTask;

public class MiniAccessControl {

    public static void main(String[] args) {
        MockStorage raw_storage = new MockStorage();

        Graph master = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).withStorage(raw_storage).build();
        master.connect(null);

        //init from master
        master.declareIndex(0, "nodes", new Callback<NodeIndex>() {
            @Override
            public void on(NodeIndex nodesIndex) {
                Node priv = master.newNode(0, 0);
                priv.setGroup(2);//protected
                priv.set("name", Type.STRING, "private");
                nodesIndex.update(priv);

                Node pub = master.newNode(0, 0);
                pub.setGroup(0);//protected
                pub.set("name", Type.STRING, "public");
                nodesIndex.update(pub);

            }
        }, "name");

        master.save(null);

        MiniFilteredStorage mac = new MiniFilteredStorage(raw_storage);

        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).withStorage(mac).build();
        g.connect(null);
        newTask().travelInTime("0").readIndex("nodes").log("{{result}}").execute(g, null);

    }

}
