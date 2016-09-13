/**
 * Created by lucasm on 06/07/16.
 */

console.time("all");
var graph = new org.mwg.GraphBuilder()
    .withStorage(new org.mwg.plugin.WSClient("ws://localhost:8050"))
    .withPlugin(new org.mwg.ml.MLPlugin())
    .build();


graph.connect(function () {
    initVivaGraph();
});