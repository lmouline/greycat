package greycatBench;

import greycat.Graph;
import greycat.GraphBuilder;
import greycat.Node;
import greycat.scheduler.NoopScheduler;

import java.util.ArrayList;
import java.util.List;

public class CloneSpeed {

    public static void main(String[] args) {
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).build();
        g.connect(null);

        Node n = g.newNode(0,0);
        int round = 1000000;
        long before = System.currentTimeMillis();
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(n);
        for(int i=0;i<round;i++){
            nodes.add(g.cloneNode(n));
        }
        for(int i=0;i<round;i++){
            nodes.get(i).free();
        }

        long after = System.currentTimeMillis();
        double time = (after - before) / 1000.0;

        double throughput = round / time;
        System.out.println(time+"/"+throughput);
    }

}
