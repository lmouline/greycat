/**
 * Created by lucasm on 11/07/16.
 */

var WORLD = 0;
var TIME = 0;
var NB_NODES_TO_DISPLAY = 4000;
var NB_HOPS = 2;
var NODE_SIZE = 12;

// these variables shouldn't change over time once loaded
var colorsByNodeType = {};
var colorsByLinkType = {};
var gAllNodes = [];
var gNodesPerType = {};
var gNodesWithRel = [];
var gChildrenNodes = [];
var gRootNodes = [];
var gNodesWithChildren = [];
var gTree = {_id: 'root',
    childNodes: []};

// these variables need to be cleared at every change in the graph
var gNodesDisplayed = [];
var nodesToBeLinked = {};
var childrenNodes = [];


var globalFunc = { setNodeDetailOnClick: function(){},
    addNodesInList: function(){},
    loadNextNodesInList: function(){},
    setTypesInList: function(){},
    initTable: function(){},
    loadRootNodesInTree: function(){},
    selectNodeFromTree: function(){},
    loadTree: function(){}
};

var renderer = {};
var graphics = {};
var layout = {};

var prevNodeUI;

//GoldenLayout
var myLayout;

/**
 * Initializes the vivagraph graph, iterate through the mwg (indexed) nodes and calls
 * addToGlobalNodes to get a list of all the nodes, then add each node and relationship to build the graph.
 * This function is called from demo_mwg.js
 */
function initVivaGraph(){
    g = Viva.Graph.graph();
    isPaused = false;

    divGraph = document.getElementById('divGraph');

    $('#divGraph').append("<div id='loadingText' class='hero-body has-text-centered'><h1 class='title'>Loading...</h1></div>");

    console.time("nodesFromMWG");
    graph.indexes(WORLD, TIME, function (indexNames) {
        var defer = graph.newCounter(indexNames.length);
        for (var i = 0; i < indexNames.length; ++i) {
            const indexName = indexNames[i];
            /*graph.getIndexNode(WORLD, TIME, indexName, function (node) {
             console.log(node.toString());
             });*/
            graph.findAll(WORLD, TIME, indexName, function (nodes) {
                // transform every mwg nodes and add them to a global array
                for (var j = 0; j < nodes.length; ++j){
                    addToGlobalNodes(nodes[j]);
                }
                defer.count();
            });
        }

        defer.then(function() {
            console.timeEnd("nodesFromMWG");
            console.time("nodesToVivagraph");
            // add only 'nbNodesToDisplay' nodes to the graph
            var nbNodesToDisplay = (gAllNodes.length > NB_NODES_TO_DISPLAY ? NB_NODES_TO_DISPLAY : gAllNodes.length);
            for (var i = 0; i < nbNodesToDisplay; ++i){
                addNodeToGraph(gAllNodes[i]);
            }
            for (var i = 0; i < gNodesWithRel.length; ++i){
                addRelToGraph(gNodesWithRel[i]);
            }
            gRootNodes = _.difference(gAllNodes, gChildrenNodes);
            createTree();
            var maxChildNodes = getMaxChildNodes();
            console.timeEnd("nodesToVivagraph");

            console.time("rendering+loading");
            graphics = Viva.Graph.View.webglGraphics();
            graphics
                .node(function(node){
                    return Viva.Graph.View.webglSquare(NODE_SIZE, colorsByNodeType[node.data._type]);
                })
                .link(function(link) {
                    return Viva.Graph.View.webglLine(colorsByLinkType[link.data]);
                });

            var events = Viva.Graph.webglInputEvents(graphics, g);

            events.mouseEnter(function (vivagraphNode) {
                /*var nodeUI = graphics.getNodeUI(vivagraphNode.id);
                 nodeUI.color = '0x00BFFFFF';
                 renderer.rerender();*/
            }).mouseLeave(function (vivagraphNode) {
                /* var nodeUI = graphics.getNodeUI(vivagraphNode.id);
                 nodeUI.color = hexColorToWebGLColor(colorsByNodeType[vivagraphNode.data._type]);
                 renderer.rerender();*/
            }).dblClick(function (vivagraphNode) {
                drawNodeContext(vivagraphNode.data._id, NB_HOPS);
            }).click(function (vivagraphNode) {
                highlightNodeWebGL(vivagraphNode.data);
                displayNameDOM(vivagraphNode.data);
                globalFunc.setNodeDetailOnClick(vivagraphNode.data);
            });

            //TODO : slow things really badly
            /*
             graphics.placeNode(function(ui, pos){
             var label = $('.nodeLabel');
             if (label.length){
             var nodePos = layout.getNodePosition(label.attr('id'));
             var domPos = {
             x: nodePos.x,
             y: nodePos.y
             };

             graphics.transformGraphToClientCoordinates(domPos);
             label.css({'left' : domPos.x, 'top' : domPos.y});

             //hide label if it goes out of the divGraph layout
             if (domPos.x < 0 || domPos.y < 0) {
             label.hide();
             } else {
             label.show();
             }
             }
             });
             */

            console.time("loading data in components");
            // Load the necessary stuff (nodes) in the react components
            globalFunc.addNodesInList();
            globalFunc.setTypesInList();
            globalFunc.loadRootNodesInTree();
            globalFunc.loadTree();
            console.timeEnd("loading data in components");

            var springLength = 200;
            var displayedChildNodes = Math.ceil(maxChildNodes / (gAllNodes.length / nbNodesToDisplay));
            var springLengthCoeff = (displayedChildNodes <= 250 ? 1 : displayedChildNodes / 250);
            layout = Viva.Graph.Layout.forceDirected(g, {
                springLength : springLength * springLengthCoeff,
                springCoeff : 0.0008,
                dragCoeff : 0.02
            });

            renderer = Viva.Graph.View.renderer(g, {
                graphics : graphics,
                layout: layout,
                container: divGraph,
                prerender: 50
            });

            $('#loadingText').hide();

            renderer.run();
            console.timeEnd("rendering+loading");

            console.time("table+events");
            createTableLayout();
            initJQueryEvents();
            console.timeEnd("table+events");

            console.timeEnd("all");
        });
    });


}

/**
 * Returns the max of child nodes (to draw a coherent graph)
 * @returns {*}
 */
function getMaxChildNodes(){
    var currentMaxNbChildren = 0;
    for (idNode in gNodesWithChildren){
        if(gNodesWithChildren.hasOwnProperty(idNode)){
            if (currentMaxNbChildren < gNodesWithChildren[idNode].length){
                currentMaxNbChildren = gNodesWithChildren[idNode].length;
            }
        }
    }
    return currentMaxNbChildren;
}

/**
 * Depth First Search algorithm that returns the node and its children with the id "nodeId"
 * @param treeRoot
 * @param nodeId
 * @returns {*}
 */
function searchNodeInTree(treeRoot, nodeId){
    if (treeRoot.childNodes){
        for (var i = 0; i < treeRoot.childNodes.length; ++i){
            var currentNode = treeRoot.childNodes[i];
            if (currentNode._id == nodeId){
                return currentNode;
            } else {
                var found = searchNodeInTree(currentNode, nodeId);
                if (found) {
                    return found;
                }
            }
        }
    }
    return false;
}

/**
 * Deletes the current graph and draws the node and its children (depth of nbHops)
 * @param nodeId
 * @param nbHops
 */
function drawNodeContext(nodeId, nbHops){
    clearGraph();

    var node = searchNodeInTree(gTree, nodeId);
    if (!contains(gNodesDisplayed, node.node)){
        addNodeToGraph(node.node);
        addRelToGraph(node.node);
    }

    if (node.childNodes){
        var lastNode;
        var nodesWithChildren = [node];
        for (var i = 0; i < nbHops; ++i){
            var currentNode = nodesWithChildren[i];
            if (currentNode){
                var childNodes = currentNode.childNodes;
                for (var j = 0; j < childNodes.length; ++j){
                    var currChild = childNodes[j];
                    if (!contains(gNodesDisplayed, currChild.node)){
                        addNodeToGraph(currChild.node);
                    }
                    if (contains (gNodesWithRel, currChild.node)){
                        addRelToGraph(currChild.node);
                        nodesWithChildren.push(currChild);
                    }
                }
                lastNode = currentNode;
            } else {
                nbHops = (nodesWithChildren.length > nbHops ? nbHops : nodesWithChildren.length);
            }
        }
    }

    if (isPaused){
        renderer.rerender();
    }
}


/**
 * Creates an object containing the nodes of the graph in the form of a tree
 */
function createTree(){
    for (var i = 0; i < gRootNodes.length; ++i){
        var node = {
            _id : gRootNodes[i]._id, //todo : id instead of name?
            node : gRootNodes[i],
            parent : "root",
            childNodes : loadChildNodes(gRootNodes[i])
        };
        gTree.childNodes.push(node);
    }
}

/**
 * Returns the nodes children of the node in parameter
 * @param parentNode
 * @returns {Array}
 */
function loadChildNodes(parentNode){
    if (gNodesWithChildren[parentNode._id]){
        var children = [];
        for (var i = 0; i < gNodesWithChildren[parentNode._id].length; ++i){
            var currentNode = gNodesWithChildren[parentNode._id][i];
            children.push({
                _id : currentNode._id, //todo : id instead of name?
                node : currentNode,
                parent : parentNode._id, //todo id instead of name?
                childNodes : loadChildNodes(currentNode)
            });
        }
        return children;
    }
}

/**
 * Clears the graph and reset the global variables
 */
function clearGraph(){
    $('.nodeLabel').remove();
    graphics.release();
    g.clear();

    gNodesDisplayed = [];
    nodesToBeLinked = {};
    childrenNodes = [];
}

/**
 * Removes the previous label and add a new one for the current node
 * @param node
 */
function displayNameDOM(node){
    $('.nodeLabel').remove();
    var nodePos = layout.getNodePosition(node._id);
    var domPos = {
        x: nodePos.x,
        y: nodePos.y
    };

    graphics.transformGraphToClientCoordinates(domPos);

    var labelStyle = generateDOMLabel(node).style;
    labelStyle.left = domPos.x;
    labelStyle.top = domPos.y;
}

/**
 * Create a span element containing the name of the parameter node
 * @param node
 * @returns {Element}
 */
function generateDOMLabel(node) {
    var label = document.createElement('span');
    label.className = 'nodeLabel';
    label.id = node._id;
    if (node.name){
        label.innerText = node.name;
    } else {
        label.innerText = node._id;
    }
    divGraph.appendChild(label);
    return label;

}

/**
 * Creates a golden-layout component for SlickGrid and initializes it
 */
function createTableLayout(){
    var tableItem = {
        type: 'component',
        componentName: 'Table'
    };

    var stackItem = myLayout.root.getItemsById('stack-topleft')[0];
    stackItem.addChild(tableItem);
    // get focus on the search tab
    stackItem.setActiveContentItem(myLayout.root.getItemsById('node-list')[0]);
    globalFunc.initTable();
}

/**
 * Initializes the necessary events
 */
function initJQueryEvents(){
    $('#searchDiv').parent().scroll(function() {
        if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight - 100) {
            globalFunc.addNodesInList();
        }
    });

    $('#toggleLayout').click(function() {
        toggleLayout();
    });

    $("#sliderText").html("Context with <strong>2</strong> hops");

    $("#slider").slider({
        range: "max",
        min: 0,
        max: 10,
        value: 2,
        slide: function (event, ui){
            var strHops = " hop";
            if (ui.value > 1){
                strHops = " hops";
            }
            $("#sliderText").html("Context with <strong>" + ui.value + "</strong>" + strHops);
            NB_HOPS = ui.value;
        }
    });
}

/**
 * Center the focus on the node with nodeId, then highlight it
 * @param node
 */
function panToNode(node) {
    var pos = layout.getNodePosition(node._id);
    renderer.moveTo(pos.x, pos.y);

    highlightNodeWebGL(node);
}

/**
 * Highlight function especially for WebGL
 * @param node
 */
function highlightNodeWebGL(node) {
    var ui = graphics.getNodeUI(node._id);
    if (prevNodeUI){
        prevNodeUI.size = NODE_SIZE;
        //prevNodeUI.color = hexColorToWebGLColor(colorsByNodeType[node._type]);
    }
    prevNodeUI = ui;

    ui.size = NODE_SIZE * 4;
    //ui.color = 0xFFA500FF; //orange
    if (isPaused){
        renderer.rerender();
    }
}

/**
 * Transform every node from mwg and add them to a global var
 * @param node
 */
function addToGlobalNodes(node){
    var nodeToAdd = {
        "_id": node.id(),
        "_world": node.world(),
        "_time": node.time(),
        "_type": (node.nodeTypeName() ? node.nodeTypeName() : "default")
    };

    var containsRel = false;
    // add all the attributes of the node to the JSON
    graph.resolver().resolveState(node, false).each(function (attributeKey, elemType, elem) {
        var key = graph.resolver().hashToString(attributeKey);

        if(elemType == org.mwg.Type.BOOL || elemType == org.mwg.Type.STRING || elemType == org.mwg.Type.INT
            || elemType == org.mwg.Type.LONG || elemType == org.mwg.Type.DOUBLE) { //primitive types
            nodeToAdd[key] = elem;
        } else if(elemType == org.mwg.Type.RELATION) { //classic relation
            nodeToAdd[key] = [];
            for (var i = 0; i < elem.size(); ++i){
                nodeToAdd[key].push(elem.get(i));
            }
            containsRel = true;
        } else if(elemType == org.mwg.Type.LONG_TO_LONG_ARRAY_MAP) { //indexed relation
            nodeToAdd[key] = [];
            elem.each(function(relKey,relIdNode) {
                nodeToAdd[key].push(relIdNode);
            });
            containsRel = true;
        } else {
            throw "Type(" + elemType + ") is not yet managed. Please update the debugger."
        }
    });
    // if the node contains a relationship, we add them into a global variable to deal with the relationships later
    if (containsRel){
        gNodesWithRel.push(nodeToAdd);
    }

    // we sort the nodes per type
    if (gNodesPerType[nodeToAdd._type] == null){
        gNodesPerType[nodeToAdd._type] = [];
        colorsByNodeType[nodeToAdd._type] = getRandomColor(); // add the type of the node to the type list and assign a color to the type
    }
    gNodesPerType[nodeToAdd._type].push(nodeToAdd);

    // finally we add them to the global array containing all the nodes
    gAllNodes.push(nodeToAdd);

}

/**
 * Add the node in param to the graph
 * @param node
 */
function addNodeToGraph(node){

    gNodesDisplayed.push(node);
    g.addNode(node._id, node);


    if (contains(childrenNodes, node._id)){
        var parentNode = getNodeFromId(nodesToBeLinked[node._id].from);
        if (contains(gNodesDisplayed, parentNode)){
            addLinkToParent(node._id, parentNode._id);
        }
    }
}

/**
 * Add the necessary nodes and the relationships to the graph
 * @param node
 */
function addRelToGraph(node){
    var children = [];
    for (var prop in node){
        if(node.hasOwnProperty(prop)){
            if (typeof node[prop] === 'object') {

                var linkedNodes = node[prop];
                // same for links
                if (colorsByLinkType[prop] == null) {
                    colorsByLinkType[prop] = getRandomColor();
                }
                
                for (var i = 0; i < linkedNodes.length; i++) {
                    //find node by id in the array (using jQuery)
                    const linkedNode = linkedNodes[i];
                    var nodeResult = getNodeFromId(linkedNode);
                    gChildrenNodes.push(nodeResult);
                    children.push(nodeResult);
                    //if the node is displayed, we add a link otherwise we will need to add it later
                    if (contains(gNodesDisplayed, nodeResult)){
                        g.addLink(node._id, nodeResult._id, prop);
                    } else {
                        nodesToBeLinked[nodeResult._id] = delayLinkCreation(node._id, nodeResult._id, prop);
                    }


                }
            }
        }
    }
    gNodesWithChildren[node._id] = children;
}

/**
 * Function that will return the node from the id in parameter
 * @param nodeId
 * @returns {*}
 */
function getNodeFromId(nodeId){
    var res = $.grep(gAllNodes, function(e) {
        return e._id === nodeId;
    });
    return res[0];
}
/**
 * Handle the links that need to be created by keeping the nodes id and the relationship name
 * @param idParent
 * @param idChild
 * @param relationName
 * @returns {{from: *, to: *, name: *}}
 */
function delayLinkCreation(idParent, idChild, relationName){
    childrenNodes.push(idChild);
    return {
        from: idParent,
        name: relationName
    };
}

/**
 * Add a link to the graph between the node and its parent
 * @param idNode
 * @param idParentNode
 */
function addLinkToParent(idNode, idParentNode){
    g.addLink(idParentNode, idNode, nodesToBeLinked[idNode].name);
    delete nodesToBeLinked[idNode];

    //we remove the node from childrenNodes
    var index = childrenNodes.indexOf(idNode);
    if (index > -1) {
        childrenNodes.splice(index, 1);
    }
}

/**
 * Allows the rendering to be paused or resumed
 * @returns {boolean}
 */
function toggleLayout() {
    $('.nodeLabel').remove();
    isPaused = !isPaused;
    if (isPaused) {
        $('#toggleLayout').html('<i class="fa fa-play"></i>Resume layout');
        renderer.pause();
    } else {
        $('#toggleLayout').html('<i class="fa fa-pause"></i>Pause layout');
        renderer.resume();
    }
    return false;
}

/**
 * Returns a color in a form of a string
 * Credits to Anatoliy, Bergi and KevinIsNowOnline from StackOverflow
 * @returns {string}
 */
function getRandomColor() {
    var letters = '789ABCD'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.round(Math.random() * 6)];
    }
    return color;
}

/**
 * Returns a color #ff5a5a to 0xff5a5aFF in order to be displayed correctly with webgl
 * @param color
 * @returns {string}
 */
function hexColorToWebGLColor(color) {
    return '0x' + color.substring(1) + 'FF';
}

/**
 * Checks if object 'obj' is in array 'a'
 * @param a
 * @param obj
 * @returns {boolean}
 */
function contains(a, obj) {
    for (var i = 0; i < a.length; i++) {
        if (a[i] === obj) {
            return true;
        }
    }
    return false;
}