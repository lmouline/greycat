/// <reference path="layout.ts" />
var GraphVisu = (function () {
    function GraphVisu() {
    }
    return GraphVisu;
}());
function initVivaGraph() {
    var graph = window.Viva.Graph.graph();
    graph.addNode(1);
    graph.addNode(2);
    graph.addLink(1, 2);
    var graphics = window.Viva.Graph.View.webglGraphics();
    var renderer = window.Viva.Graph.View.renderer(graph, {
        container: document.getElementById(idDivGraphVisu),
        graphics: graphics
    });
    renderer.run();
}
