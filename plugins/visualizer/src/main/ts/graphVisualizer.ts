/// <reference path="layout.ts" />
/// <reference path="mwg/mwg.d.ts" />
/// <reference path="mwg/mwg.ws.d.ts" />


import WSClient = org.mwg.plugin.WSClient;
import Task = org.mwg.task.Task;
import Actions = org.mwg.task.Actions;
import TaskContext = org.mwg.task.TaskContext;
import Type = org.mwg.Type;
import TaskResult = org.mwg.task.TaskResult;
import Graph = org.mwg.Graph;

interface Window {
    Viva? : any
}

class GraphVisu {
    _graph : org.mwg.Graph;
    _graphVisu : any;
    _time : number = 0;
    _world : number = 0;
    _depth : number = 10;

    _mapTypeColor : Object = new Object();

    _previousSelect : number = -1;
    _previousColor : any;

    _renderer : any;

    constructor(url : string) {
        this._graph = new org.mwg.GraphBuilder()
            .withStorage(new org.mwg.plugin.WSClient(url))
            .build();

        this._graphVisu = window.Viva.Graph.graph();

        this._mapTypeColor['default'] = 0x009ee8ff;
    }
}

function printNodeDetails(nodeId: number, graphVisu : GraphVisu) {
    Actions
        .setTime(graphVisu._time + "")
        .setWorld(graphVisu._world + "")
        .inject("{\n")
        .asGlobalVar("string")
        .lookup(nodeId + "")
        .asGlobalVar("node")
        .then(function(context : TaskContext) {
            var res : String = context.variable("string").get(0);
            var n : org.mwg.Node = context.variable("node").get(0);
            res += "  _id=" + n.id() + "\n";
            res += "  _type=" + (n.nodeTypeName() || 'default') + "\n";
            context.setGlobalVariable("string",res);
            context.continueTask();
        })
        .subTasks([Actions.propertiesWithTypes(Type.BOOL),
                    Actions.propertiesWithTypes(Type.INT),
                    Actions.propertiesWithTypes(Type.DOUBLE),
                    Actions.propertiesWithTypes(Type.LONG),
                    Actions.propertiesWithTypes(Type.STRING),
                    Actions.propertiesWithTypes(Type.RELATION)]
        )
        .foreach(Actions.then(function(context : TaskContext) {
            var res : String = context.variable("string").get(0);
            var n : org.mwg.Node = context.variable("node").get(0);
            if(typeof context.result().get(0) != "number") {
                res += "  " + context.result().get(0) + "=" + n.get(context.result().get(0)) + "\n";
            } else {
                res += "  " + context.result().get(0) + "=" + n.getByIndex(context.result().get(0)) + "\n";
            }
            context.setGlobalVariable("string",res);
            context.continueTask();
        }))
        .fromVar("node")
        .propertiesWithTypes(Type.LONG_TO_LONG_ARRAY_MAP)
        .ifThen(function(context : TaskContext) {
                return context.result().size() > 0;
            },Actions.then(function(context :TaskContext) {
            var n : org.mwg.Node = context.variable("node").get(0);
            var map : org.mwg.struct.LongLongArrayMap = n.get(context.result().get(0));

            var index : number = 0;
            var res : String = context.variable("string").get(0);
            res += "  " + context.result().get(0) + "=[";
            map.each(function (key:number, value:number) {
                res += (value + "");
                if((index + 1) < map.size() ) {
                    res += ",";
                }
                index++;
            });
            res += "]\n";

            context.setGlobalVariable("string",res);
            context.continueTask();
        }))
        .fromVar("string")
        .execute(graphVisu._graph, function(result : TaskResult<string>) {
            document.getElementById("nodeDetail").innerHTML = result.get(0) + "}";
        });
}

function connect(graphVisu : GraphVisu, idDiv : string) {
    graphVisu._graph.connect(function (succeed : boolean){
        if(succeed) {
            var graphics = window.Viva.Graph.View.webglGraphics();
            graphics.node(function(node){
                    return window.Viva.Graph.View.webglSquare(12,graphVisu._mapTypeColor[node.data._type]);
                });

            window.Viva.Graph.webglInputEvents(graphics,graphVisu._graphVisu)
                .click(function(selectedNode : any) {
                    if(selectedNode.id != graphVisu._previousSelect) {
                        printNodeDetails(selectedNode.id, graphVisu);
                        var selectedNodeUI = graphVisu._renderer.getGraphics().getNodeUI(selectedNode.id);
                        var currentColor = selectedNodeUI.color;
                        selectedNodeUI.color = 0xFFA500ff;

                        if (graphVisu._previousSelect != -1) {
                            var previousSelected = graphVisu._renderer.getGraphics().getNodeUI(graphVisu._previousSelect)
                            previousSelected.color = graphVisu._previousColor;
                        }
                        graphVisu._previousSelect = selectedNode.id;
                        graphVisu._previousColor = currentColor;
                    }

                });

            graphVisu._renderer = window.Viva.Graph.View.renderer(graphVisu._graphVisu, {
                container: document.getElementById(idDiv),
                graphics: graphics
            });
            graphVisu._renderer.run();

            drawGraph(graphVisu);
        } else {
            console.error("Problem during connection.")
        }
    });
}

function drawGraph(graphVisu : GraphVisu) {

    var task : Task = Actions.newTask();
    graphVisu._graphVisu.clear();
    task
        .indexesNames()
        .foreach(
            Actions
                .fromIndexAll("{{result}}")
                .asGlobalVar("toVisit")
                .foreach(Actions.then(function(context : TaskContext) {
                    var node : org.mwg.Node = context.resultAsNodes().get(0);
                    var id : number = node.id();

                    var nodeType : string = node.nodeTypeName() || 'default';
                    graphVisu._graphVisu.addNode(id,{_type: nodeType});
                    console.log("Node added: " + id);
                    if(graphVisu._mapTypeColor[nodeType] == null) {
                        graphVisu._mapTypeColor[nodeType] = getRandomColor();
                    }
                    context.addToGlobalVariable("alreadyVisit",id);
                    context.continueTask();
                }))
                .fromVar("toVisit")
                .loop("1",graphVisu._depth + "",
                    Actions.defineVar("nextToVisit")
                        .fromVar("toVisit")
                        .foreach(
                            Actions.asGlobalVar("currentNode")
                                .addToGlobalVar("alreadyVisit")
                                .propertiesWithTypes(Type.RELATION)
                                .foreach(
                                    Actions.asVar("relationName")
                                        .fromVar("currentNode")
                                        .traverse("{{relationName}}")
                                        .foreach(
                                            Actions.then(function(context : TaskContext) {
                                                var alreadyVisit : TaskResult<number> = context.variable("alreadyVisit");
                                                var result : org.mwg.Node = context.resultAsNodes().get(0);
                                                for(var i=0;i<alreadyVisit.size();i++) {
                                                    if( result.id() == alreadyVisit.get(0)) {
                                                        context.continueTask();
                                                    }
                                                }
                                                var srcNode : number = context.variable("currentNode").get(0).id();

                                                var nodeType : string = result.nodeTypeName() || 'default';
                                                if(graphVisu._mapTypeColor[nodeType] == null) {
                                                    graphVisu._mapTypeColor[nodeType] = getRandomColor();
                                                }

                                                graphVisu._graphVisu.addNode(result.id(),{_type: nodeType});
                                                graphVisu._graphVisu.addLink(srcNode,result.id());


                                                context.addToGlobalVariable("nextToVisit",result);
                                                context.continueTask();
                                            })
                                        )
                                )
                                .fromVar("currentNode")
                                .propertiesWithTypes(Type.LONG_TO_LONG_ARRAY_MAP)
                                .foreach(
                                    Actions.asVar("relationName")
                                        .fromVar("currentNode")
                                        .traverseIndexAll("{{relationName}}")
                                        .foreach(
                                            Actions.then(function(context : TaskContext){
                                                var alreadyVisit : TaskResult<number> = context.variable("alreadyVisit");
                                                var result : org.mwg.Node = context.resultAsNodes().get(0);
                                                for(var i=0;i<alreadyVisit.size();i++) {
                                                    if( result.id() == alreadyVisit.get(0)) {
                                                        context.continueTask();
                                                    }
                                                }
                                                var srcNode : number = context.variable("currentNode").get(0).id();
                                                var nodeType : string = result.nodeTypeName() || 'default';

                                                if(graphVisu._mapTypeColor[nodeType] == null) {
                                                    graphVisu._mapTypeColor[nodeType] = getRandomColor();
                                                }

                                                graphVisu._graphVisu.addNode(result.id(),{_type: nodeType});
                                                graphVisu._graphVisu.addLink(srcNode,result.id());

                                                context.addToGlobalVariable("nextToVisit",result);
                                                context.continueTask();
                                            })
                                        )
                                )

                        )
                        .fromVar("nextToVisit")
                        .asGlobalVar("toVisit")
                        .fromVar("nextToVisit")
                        .clear()
                        .asGlobalVar("nextToVisit")
                )
        )
        .execute(graphVisu._graph,null);

}

function internal_initVivaGraph(url: string, idDiv : string) {
    connect(new GraphVisu(url),idDiv);
}

function initVivaGraph(url: string, idDiv : string) {
    if(document.getElementById(idDiv) == null) {
        setTimeout(internal_initVivaGraph,5,url,idDiv)
    } else {
        internal_initVivaGraph(url,idDiv);
    }

}


function getRandomColor() {
    return '#'+Math.floor(Math.random()*16777215).toString(16);
}
