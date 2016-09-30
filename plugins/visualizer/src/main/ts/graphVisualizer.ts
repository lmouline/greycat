/// <reference path="mwg/mwg.d.ts" />
/// <reference path="mwg/mwg.ws.d.ts" />


import WSClient = org.mwg.plugin.WSClient;
import Task = org.mwg.task.Task;
import Actions = org.mwg.task.Actions;
import TaskContext = org.mwg.task.TaskContext;
import Type = org.mwg.Type;
import TaskResult = org.mwg.task.TaskResult;
import Graph = org.mwg.Graph;
import HeapRelationship = org.mwg.core.chunk.heap.HeapRelationship;
import CoreDeferCounterSync = org.mwg.core.utility.CoreDeferCounterSync;

interface Window {
    Viva? : any
}

//todo delete
var defaultGraphVisu : org.mwg.plugins.GraphVisu;

module org.mwg.plugins {
    const timeVar : string = "time";
    const worldVar : string = "world";
    const nodeIdVar : string = "nodeId";
    const printNodeTask : Task = Actions
        .setTime(`{{${timeVar}}}`)
        .setWorld(`{{${worldVar}}}`)
        .inject("{\n")
        .asGlobalVar("string")
        .lookup(`{{${nodeIdVar}}}`)
        .asGlobalVar("node")
        .ifThen(function(context : TaskContext){return context.result().size() > 0},Actions.then(function(context : TaskContext) {
                let res : String = context.variable("string").get(0);
                let n : org.mwg.Node = context.variable("node").get(0);
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
                let res : String = context.variable("string").get(0);
                let n : org.mwg.Node = context.variable("node").get(0);
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
                let n : org.mwg.Node = context.variable("node").get(0);
                let map : org.mwg.struct.LongLongArrayMap = n.get(context.result().get(0));

                let index : number = 0;
                let res : String = context.variable("string").get(0);
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
            .fromVar("string"));

    const depthVar : string = "depth";
    const graphVisuVar : string = "graphVisu";
    const drawGraphTask : Task = Actions
        .setTime(`{{${timeVar}}}`)
        .setWorld(`{{${worldVar}}}`)
        .indexesNames()
        .foreach(
            Actions
                .fromIndexAll("{{result}}")
                .asGlobalVar("toVisit")
                .foreach(Actions.then(function(context : TaskContext) {
                    let node : org.mwg.Node = context.resultAsNodes().get(0);
                    let graphVisu : GraphVisu = <GraphVisu> context.variable(graphVisuVar).get(0);
                    let id : number = node.id();

                    let nodeType : string = node.nodeTypeName() || 'default';
                    graphVisu._graphVisu.addNode(id,{_type: nodeType});
                    if(graphVisu._mapTypeColor[nodeType] == null) {
                        graphVisu._mapTypeColor[nodeType] = getRandomColor();
                    }
                    context.continueTask();
                }))
                .fromVar("toVisit")
                .loop("1",`{{${depthVar}}}`,
                    Actions
                        .defineVar("nextToVisit")
                        .fromVar("toVisit")
                        .foreach(
                            Actions
                                .asGlobalVar("currentNode")
                                .then(function(context : TaskContext) {
                                    let node : org.mwg.Node = context.result().get(0);
                                    context.addToGlobalVariable("alreadyVisit",node.id())
                                    context.continueTask();
                                })
                                .propertiesWithTypes(Type.RELATION)
                                .foreach(
                                    Actions.asVar("relationName")
                                        .fromVar("currentNode")
                                        .traverse("{{relationName}}")
                                        .ifThenElse(function (context:TaskContext) : boolean {
                                            return context.result().size() > 0;
                                        },Actions.foreach(
                                            Actions.then(function(context : TaskContext) {
                                                var alreadyVisit : TaskResult<number> = context.variable("alreadyVisit");
                                                var srcNode : number = context.variable("currentNode").get(0).id();
                                                var result : org.mwg.Node = context.resultAsNodes().get(0);

                                                var alreadyVisited : boolean = false;
                                                for(var i=0;i<alreadyVisit.size();i++) {
                                                    alreadyVisited = alreadyVisited || (result.id() == alreadyVisit.get(i))
                                                    if(alreadyVisited) {
                                                        break;
                                                    }
                                                }

                                                let graphVisu : GraphVisu = <GraphVisu> context.variable(graphVisuVar).get(0);
                                                if(!alreadyVisited) {
                                                    let nodeType : string = result.nodeTypeName() || 'default';
                                                    if(graphVisu._mapTypeColor[nodeType] == null) {
                                                        graphVisu._mapTypeColor[nodeType] = getRandomColor();
                                                    }

                                                    graphVisu._graphVisu.addNode(result.id(),{_type: nodeType});
                                                    let nextToVisit : TaskResult<org.mwg.Node> = context.variable("nextToVisit");
                                                    let alreadyAdded :boolean = false;
                                                    for(let ntv=0;ntv<nextToVisit.size();ntv++) {
                                                        alreadyAdded = alreadyAdded || (result.id() == nextToVisit.get(ntv).id());
                                                        if(alreadyAdded) {
                                                            break;
                                                        }
                                                    }
                                                    if(!alreadyAdded) {
                                                        context.addToGlobalVariable("nextToVisit", result);
                                                    }
                                                }

                                                graphVisu._graphVisu.addLink(srcNode,result.id());
                                                context.continueTask();

                                            })
                                        ),Actions.then(function(context: TaskContext) {
                                            let node : org.mwg.Node = context.variable("currentNode").get(0);
                                            let hashReation : number = context.variable("relationName").get(0);


                                            node.relByIndex(hashReation, function(nodes: Array<org.mwg.Node>) {
                                                let alreadyVisit : TaskResult<number> = context.variable("alreadyVisit");
                                                let srcNode : number = context.variable("currentNode").get(0).id();

                                                for(let i=0;i<nodes.length;i++) {
                                                    let result : org.mwg.Node = nodes[i];
                                                    let alreadyVisited : boolean = false;
                                                    for(let i=0;i<alreadyVisit.size();i++) {
                                                        alreadyVisited = alreadyVisited || (result.id() == alreadyVisit.get(i))
                                                        if(alreadyVisited) {
                                                            break;
                                                        }
                                                    }

                                                    let graphVisu : GraphVisu = <GraphVisu> context.variable(graphVisuVar).get(0);
                                                    if(!alreadyVisited) {
                                                        let nodeType : string = result.nodeTypeName() || 'default';
                                                        if(graphVisu._mapTypeColor[nodeType] == null) {
                                                            graphVisu._mapTypeColor[nodeType] = getRandomColor();
                                                        }

                                                        graphVisu._graphVisu.addNode(result.id(),{_type: nodeType});
                                                        let nextToVisit : TaskResult<org.mwg.Node> = context.variable("nextToVisit");
                                                        var alreadyAdded :boolean = false;
                                                        for(let ntv=0;ntv<nextToVisit.size();ntv++) {
                                                            alreadyAdded = alreadyAdded || (result.id() == nextToVisit.get(ntv).id());
                                                            if(alreadyAdded) {
                                                                break;
                                                            }
                                                        }
                                                        if(!alreadyAdded) {
                                                            context.addToGlobalVariable("nextToVisit", result);
                                                        }
                                                    }

                                                    graphVisu._graphVisu.addLink(srcNode,result.id());
                                                }
                                                context.continueTask();
                                            });

                                        }))
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
                                                var srcNode : number = context.variable("currentNode").get(0).id();
                                                var result : org.mwg.Node = context.resultAsNodes().get(0);

                                                var alreadyVisited : boolean = false;
                                                for(var i=0;i<alreadyVisit.size();i++) {
                                                    alreadyVisited = alreadyVisited || (result.id() == alreadyVisit.get(i));
                                                    if(alreadyVisited) {
                                                        break;
                                                    }
                                                }

                                                let graphVisu : GraphVisu = <GraphVisu> context.variable(graphVisuVar).get(0);
                                                if(!alreadyVisited) {
                                                    var nodeType : string = result.nodeTypeName() || 'default';
                                                    if(graphVisu._mapTypeColor[nodeType] == null) {
                                                        graphVisu._mapTypeColor[nodeType] = getRandomColor();
                                                    }

                                                    graphVisu._graphVisu.addNode(result.id(),{_type: nodeType});
                                                    var nextToVisit : TaskResult<org.mwg.Node> = context.variable("nextToVisit");
                                                    var alreadyAdded :boolean = false;
                                                    for(var ntv=0;ntv<nextToVisit.size();ntv++) {
                                                        alreadyAdded = alreadyAdded || (result.id() == nextToVisit.get(ntv).id());
                                                        if(alreadyAdded) {
                                                            break;
                                                        }
                                                    }
                                                    if(!alreadyAdded) {
                                                        context.addToGlobalVariable("nextToVisit", result);
                                                    }
                                                }

                                                graphVisu._graphVisu.addLink(srcNode,result.id());
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
        );


    export class GraphVisu {
        _graph : org.mwg.Graph;
        _graphVisu : any;
        _time : number = 0;
        _world : number = 0;
        _depth : number = 10; //todo delete

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
            .inject(nodeId)
            .asGlobalVar(nodeIdVar)
            .inject(graphVisu._time)
            .asGlobalVar(timeVar)
            .inject(graphVisu._world)
            .asGlobalVar(worldVar)
            .subTask(printNodeTask)
            .execute(graphVisu._graph, function(result : TaskResult<string>) {
                document.getElementById("nodeDetail").innerHTML = result.get(0) + "}";
            });
    }

    function selectNode(nodeID : number) {
        if(nodeID != defaultGraphVisu._previousSelect) {
            printNodeDetails(nodeID, defaultGraphVisu);
            var selectedNodeUI = defaultGraphVisu._renderer.getGraphics().getNodeUI(nodeID);
            if(selectedNodeUI != null) {
                var currentColor = selectedNodeUI.color;
                selectedNodeUI.color = 0xFFA500ff;

                if (defaultGraphVisu._previousSelect != -1) {
                    var previousSelected = defaultGraphVisu._renderer.getGraphics().getNodeUI(defaultGraphVisu._previousSelect);
                    previousSelected.color = defaultGraphVisu._previousColor;
                }
                defaultGraphVisu._previousSelect = nodeID;
                defaultGraphVisu._previousColor = currentColor;
            }
        }

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
                        selectNode(selectedNode.id);
                    });

                graphVisu._renderer = window.Viva.Graph.View.renderer(graphVisu._graphVisu, {
                    container: document.getElementById(idDiv),
                    graphics: graphics
                });
                graphVisu._renderer.run();
                //
                // setTimeout(function(){
                //     graphVisu._renderer.pause();
                // },10000);


                drawGraph(graphVisu);
            } else {
                console.error("Problem during connection.")
            }
        });
    }



    function drawGraph(graphVisu : GraphVisu) {
        graphVisu._graphVisu.clear();
        Actions
            .inject(graphVisu._time)
            .asGlobalVar(timeVar)
            .inject(graphVisu._world)
            .asGlobalVar(worldVar)
            .inject(graphVisu._depth)
            .asGlobalVar(depthVar)
            .inject(graphVisu)
            .asGlobalVar(graphVisuVar)
            .subTask(drawGraphTask)
            .execute(graphVisu._graph,function() {
                if(graphVisu._previousSelect != -1) {
                    let nodeId = graphVisu._previousSelect;
                    graphVisu._previousSelect = -1;
                    selectNode(nodeId);
                }
            });
    }

    function internal_initVivaGraph(url: string, idDiv : string) {
        defaultGraphVisu = new GraphVisu(url);
        connect(defaultGraphVisu,idDiv);
    }

    function getRandomColor() {
        var letters = '789ABCD'.split('');
        var color = "#";
        for (var i = 0; i < 6; i++ ) {
            color += letters[Math.round(Math.random() * 6)];
        }
        return color;
    }

    export function initVivaGraph(url: string, idDiv : string) {
        if(document.getElementById(idDiv) == null) {
            setTimeout(internal_initVivaGraph,5,url,idDiv)
        } else {
            internal_initVivaGraph(url,idDiv);
        }

    }

    export function updateTime(time : number, graphVisu : GraphVisu) {
        graphVisu._time = time;
        drawGraph(graphVisu);

    }

    export function updateWorld(world : number, graphVisu : GraphVisu) {
        graphVisu._world = world;
        drawGraph(graphVisu);

    }

}

