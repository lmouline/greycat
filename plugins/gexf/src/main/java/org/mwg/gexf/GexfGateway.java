package org.mwg.gexf;

import io.undertow.Undertow;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.plugin.NodeState;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.task.TaskResult;

import java.util.Deque;
import java.util.Map;

import static org.mwg.task.Actions.fromIndexAll;
import static org.mwg.task.Actions.setWorld;

public class GexfGateway implements HttpHandler {

    private int port;
    private Undertow server;
    private Graph graph;

    public GexfGateway(Graph p_graph, int p_port) {
        this.port = p_port;
        this.graph = p_graph;
    }

    @Override
    public void handleRequest(final HttpServerExchange httpServerExchange) throws Exception {
        String rawPath = httpServerExchange.getRelativePath();
        long world = 0;
        long time = System.currentTimeMillis();
        Map<String, Deque<String>> params = httpServerExchange.getQueryParameters();
        if (params.containsKey("world")) {
            try {
                world = Long.parseLong(params.get("world").getFirst());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (params.containsKey("time")) {
            try {
                time = Long.parseLong(params.get("time").getFirst());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (httpServerExchange.getRequestMethod().equalToString("GET")) {
            httpServerExchange.dispatch();

            Gexf gexf = new GexfImpl();
            it.uniroma1.dis.wsngroup.gexf4j.core.Graph egraph = gexf.getGraph();

            long finalWorld = world;
            long finalTime = time;
            graph.indexes(world, time, new Callback<String[]>() {
                @Override
                public void on(String[] result) {
                    setWorld("" + finalWorld).setTime("" + finalTime).inject(result).foreach(fromIndexAll("{{result}}")).execute(graph, new Callback<TaskResult>() {
                        @Override
                        public void on(TaskResult result) {
                            TaskResult<Node> nodes = result;
                            for (int i = 0; i < nodes.size(); i++) {
                                Node loopNode = nodes.get(i);
                                it.uniroma1.dis.wsngroup.gexf4j.core.Node enode = egraph.createNode();
                                NodeState state = graph.resolver().resolveState(loopNode);
                                state.each(new NodeStateCallback() {
                                    @Override
                                    public void on(long attributeKey, int elemType, Object elem) {
                                        System.out.println(attributeKey);
                                    }
                                });
                            }
                            result.free();

                            httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/xml");
                            Sender sender = httpServerExchange.getResponseSender();
                            sender.send("<graphml />");
                            httpServerExchange.endExchange();
                        }
                    });
                }
            });


        }
    }

    public static GexfGateway expose(Graph p_graph, int port) {
        GexfGateway newgateway = new GexfGateway(p_graph, port);
        return newgateway;
    }

    public void start() {
        server = Undertow.builder().addHttpListener(port, "0.0.0.0").setHandler(this).build();
        server.start();
    }

    public void stop() {
        server.stop();
        server = null;
    }

}
