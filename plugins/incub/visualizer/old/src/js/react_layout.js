/**
 * Created by lucasm on 12/07/16.
 */

console.time("layout");

var nodeStringProperties = [];
var chosenProperties = [];
var nodeProperties = [];
var NB_NODES_PER_STEP = 100;

/**
 * Config for golden-layout
 * @type {{content: *[]}}
 */
var layoutConfig = {
    content: [{
        type: 'row',
        content: [{
            type: 'column',
            width: 33,
            id: 'first-column',
            content: [{
                type: 'stack',
                id: 'stack-topleft',
                content: [{
                    type: 'react-component',
                    title: 'Search',
                    component: 'node-list',
                    id: 'node-list'
                }, {
                    type: 'react-component',
                    title: 'Types',
                    component: 'type-list'
                }, {
                    type: 'react-component',
                    title: 'Tree',
                    component: 'tree-list'
                }]
            }, {
                type: 'stack',
                id: 'stack-bottomleft',
                content: [{
                    type: 'react-component',
                    title: 'Node Details',
                    component: 'node-detail'
                }]
            }]
        }, {
            type: 'component',
            componentName: 'Graph'
        }]
    }]
};

myLayout = new GoldenLayout(layoutConfig);

/**
 * Search component
 */
var Search = React.createClass({
    getInitialState: function(){
        return { searchString: '' ,
                 advanced: false };
    },
    handleChange: function(e){
        this.setState({searchString: e.target.value});
    },
    setAdvancedSearch: function(){
        this.setState({advanced: !this.state.advanced});
    },
    resetGraph: function() {
        this.props.resetGraph();
        this.update();
    },
    update: function(){
        this.forceUpdate();
    },
    isPropChecked: function(prop){
        return (contains(chosenProperties, prop));
    },
    render: function() {
        var eventHub = this.props.eventHub;
        var nodes = this.props.items;
        var searchString = this.state.searchString.trim().toLowerCase();

        if(searchString.length > 0){

            // FUZZY SEARCH (Fuse.js)
            // Stringify every property of the nodes to allow the fuzzy searching on every property
            /*for (var i = 0; i < nodes.length; ++i){
                var currentNode = nodes[i];
                for (var prop in currentNode){
                    if(currentNode.hasOwnProperty(prop)){
                        currentNode[prop] = currentNode[prop].toString();
                    }
                }
            }*/

            var options = {
                keys: chosenProperties,
                threshold: 0.3
            };

            var f = new Fuse(nodes, options);
            nodes = f.search(searchString);
        }

        var strNbNodes = "node";
        if (gAllNodes.length > 1) {
            strNbNodes = "nodes"
        }

        //displays the properties or not depending on the advanced search
        var currProperties = (this.state.advanced ? nodeStringProperties : []);
        var currComponent = this;

        return (
                <div id="searchDiv">
                    <button id="resetGraphButtonSearch" className="button is-danger is-small" onClick={this.resetGraph}>Reset Graph</button>
                    <button id="loadNodesButton" className="button is-info is-small" onClick={this.props.loadAllNodes}>Load all nodes</button>
                    <div id="sliderDiv">
                        <span id="sliderText"></span>
                        <div id="slider"></div>
                    </div>
                    <nav id="searchNav" className="level">
                        <div className="level-left">
                            <p className="control has-addons">
                                <input type="text" className="input" value={this.state.searchString} onChange={this.handleChange} placeholder="Find a node" />
                                <button className="button" onClick={this.setAdvancedSearch}>Advanced</button>
                            </p>
                        </div>
                        <div id="infosSearch">
                            <div><p className="subtitle"><strong>{gAllNodes.length}</strong> {strNbNodes} total</p></div>
                            <div><p className="infosNodesSearch"><strong>{gNodesDisplayed.length}</strong> displayed</p></div>
                            <div><p className="infosNodesSearch"><strong>{nodes.length}</strong> currently loaded in the list</p></div>
                        </div>
                    </nav>
                    <p className="control">
                        {currProperties.map(function (prop) {
                            return <SearchCheckbox key={prop} update={currComponent.update} propName={prop} isChecked={currComponent.isPropChecked(prop)}/>
                        })}

                    </p>
                    <ul className="nodeList">
                        {nodes.map(function (node) {
                            return <Node
                                key={node._id}
                                nodeData={node}
                                glEventHub={eventHub}
                                update={currComponent.update}/>
                        })}
                    </ul>
                </div>
            );
    }
});

/**
 * SearchCheckbox component
 */
var SearchCheckbox = React.createClass({
    getInitialState: function () {
        return { propName: this.props.propName,
                 defaultChecked: this.props.isChecked}
    },
    handleChoice: function(){
        this.setState({defaultChecked: !this.state.defaultChecked});
        var index = chosenProperties.indexOf(this.state.propName);
        if (index > -1){
            chosenProperties.splice(index, 1);
        } else {
            chosenProperties.push(this.state.propName);
        }
        this.props.update();
    },
    render: function() {
        return (
            <label className="checkbox">
                <input onChange={this.handleChoice} checked={this.state.defaultChecked} type="checkbox" /> {this.state.propName}
            </label>
        )
    }
});

/**
 * NodeList component
 */
var NodeList = React.createClass({
    getInitialState: function () {
        return {
            nodes: [],
            index: 0
        };
    },
    resetGraph: function(){
        if (gNodesDisplayed.length != gAllNodes.length) {
            clearGraph();

            for (var i = 0; i < gAllNodes.length; ++i){
                addNodeToGraph(gAllNodes[i]);
            }
            for (var i = 0; i < gNodesWithRel.length; ++i){
                if (contains(gNodesDisplayed, gNodesWithRel[i])){
                    addRelToGraph(gNodesWithRel[i]);
                }
            }
            renderer.rerender();
        }
    },
    loadNextNodes: function(nbNodes) {
        var arrayNodes = [];
        var nbRemainingNodes = gAllNodes.length - this.state.nodes.length;
        if (nbRemainingNodes == 0){
            return arrayNodes;
        }

        if (nbRemainingNodes <= NB_NODES_PER_STEP){
            for (var i = 0; i < nbRemainingNodes; ++i){
                arrayNodes.push(gAllNodes[i + this.state.index]);
            }
        } else {
            var nbNodesToLoad = (nbNodes > gAllNodes.length ? gAllNodes.length : nbNodes);
            for (var i = 0; i < nbNodesToLoad; ++i){
                arrayNodes.push(gAllNodes[i + this.state.index]);
            }
        }

        this.setState({index: this.state.index + nbNodesToLoad});
        return arrayNodes;
    },
    loadNodes: function() {
        this.setState({nodes: this.state.nodes.concat(this.loadNextNodes(NB_NODES_PER_STEP))});
    },
    loadAllNodes: function() {
        this.setState({nodes: gAllNodes});
    },
    loadNodeProperties: function(){
        var indexProp = this.state.index - NB_NODES_PER_STEP;
        for (var i = indexProp; i < this.state.nodes.length; ++i){
            var currentNode = this.state.nodes[i];
            for (var prop in currentNode){
                if(currentNode.hasOwnProperty(prop)){
                    if (!contains(nodeProperties, prop)){
                        nodeProperties.push(prop);

                        // checks if the property is already in the global nodeStringProperties array and if its value is a String
                        if (!contains(nodeStringProperties, prop) && Object.prototype.toString.call(currentNode[prop]) === Object.prototype.toString.call("")){
                            nodeStringProperties.push(prop);
                        }
                    }
                }
            }
        }
        // copy by value
        chosenProperties = $.extend([], nodeStringProperties);
    },
    // allow to load the nodes only after their init in demo_vivagraph.js
    componentWillMount: function (){
        globalFunc.addNodesInList = () => {
            this.loadNodes();
            this.loadNodeProperties();
        };
    },
    render: function () {
        return (
            <Search eventHub={this.props.glEventHub} className="input" items={this.state.nodes} loadAllNodes={this.loadAllNodes} resetGraph={this.resetGraph}/>
        )
    }
});


/**
 * Node component
 */
var Node = React.createClass({
    getInitialState: function () {
        return this.props.nodeData;
    },
    selectNode: function () {
        this.props.update();
        this.props.glEventHub.emit('node-select', this.state);

        //todo : tmp --> context is only drawn when node is selected from the search component
        drawNodeContext(this.state._id, NB_HOPS);

        /*if (! contains(gNodesDisplayed, this.state)){
         addNodeToGraph(this.state);
         addRelToGraph(this.state);
        }
        panToNode(this.state);
        displayNameDOM(this.state); */
    },
    render: function () {
        return (
            <li onClick={this.selectNode}>{this.state.name || this.state._id}</li> //if a node doesn't have a name, it will display its id
        )
    }
});

/**
 * NodeDetail component
 */
var NodeDetail = React.createClass({
    componentWillMount: function () {
        this.props.glEventHub.on('node-select', this.setNode);
        globalFunc.setNodeDetailOnClick = (data) => {
            this.setNode(data);
        }
    },
    componentWillUnmount: function () {
        this.props.glEventHub.off('node-select', this.setNode);
    },
    setNode: function (nodeData) {
        this.replaceState(nodeData); //replaceState is mandatory because setState merges the two states
    },
    render: function () {
        if (this.state) {
            return (
                <div id="nodedetails">
                    <pre>
                        {JSON.stringify(this.state, null, 2)}
                    </pre>
                </div>
            )
        } else {
            return (<div id="nodedetails">No node selected</div>)
        }
    }
});

/**
 * NodesType react component
 */
var NodesType = React.createClass({
    render: function() {
        var types = this.props.types;
        var eventHub = this.props.eventHub;

        var strNbTypes = "type";
        if (types.length > 1) {
            strNbTypes = "types"
        }
        return (
            <div id="typesDiv">
                <button id="resetGraphButtonTypes" className="button is-danger" onClick={this.props.resetGraph}>Reset Graph</button>
                <div className="level-right">
                        <p className="subtitle is-5">
                            <strong>{types.length}</strong> {strNbTypes}
                        </p>
                    </div>
                <ul id="typeList">
                    {types.map(function (type) {
                        return <Type
                            key={type}
                            type={type}
                            eventHub={eventHub}/>
                    })}
                </ul>
            </div>
        )
    }
});

/**
 * TypeList component
 */
var TypeList = React.createClass({
    getInitialState: function () {
        return {
            types: []
        };
    },
    // allow to load the nodes only after their init in demo_vivagraph.js
    componentWillMount: function (){
        globalFunc.setTypesInList = () => {
            this.setState({types: Object.keys(gNodesPerType)});
        };
    },
    resetGraph: function(){
        if (gNodesDisplayed.length != gAllNodes.length) {
            clearGraph();

            for (var i = 0; i < gAllNodes.length; ++i){
                addNodeToGraph(gAllNodes[i]);
            }
            for (var i = 0; i < gNodesWithRel.length; ++i){
                if (contains(gNodesDisplayed, gNodesWithRel[i])){
                    addRelToGraph(gNodesWithRel[i]);
                }
            }
            renderer.rerender();
        }
    },
    render: function () {
        return (
            <NodesType eventHub={this.props.glEventHub} resetGraph={this.resetGraph} types={this.state.types} />
        )
    }
});

/**
 * Type component
 */
var Type = React.createClass({
    selectType: function () {
        var isTypeDetailActive = myLayout.root.getItemsById('type-detail').length > 0;
        if (!isTypeDetailActive){
            var typeDetail = {
                type: 'react-component',
                title: 'Nodes By Type',
                component: 'type-detail',
                id: 'type-detail',
                height: 15
            };
            var columnItem = myLayout.root.getItemsById('first-column')[0];

            columnItem.addChild(typeDetail, 1);
        }


        var typeName = this.props.type;
        this.props.eventHub.emit('type-select', {type: typeName, nodeList: gNodesPerType[typeName]});
        clearGraph();

        for (var i = 0; i < gNodesPerType[typeName].length; ++i){
            addNodeToGraph(gNodesPerType[typeName][i]);
        }
        for (var i = 0; i < gNodesWithRel.length; ++i){
            if (contains(gNodesDisplayed, gNodesWithRel[i])){
                addRelToGraph(gNodesWithRel[i]);
            }
        }
        renderer.rerender();
    },
    render: function () {
        return (
            <li onClick={this.selectType}>{this.props.type}</li>
        )
    }
});

/**
 * TypeDetail component
 */
var TypeDetail = React.createClass({
    componentWillMount: function () {
        this.props.glEventHub.on('type-select', this.setNodeList);
    },
    componentWillUnmount: function () {
        this.props.glEventHub.off('type-select', this.setNodeList);
    },
    setNodeList: function (data) {
        this.replaceState(data.nodeList); //replaceState is mandatory because setState merges the two states
        this.props.glContainer.parent.setTitle(data.type);
    },
    selectNode: function (node) {
        this.props.glEventHub.emit('node-select', node);
        if (! contains(gNodesDisplayed, node)){
            addNodeToGraph(node);
            addRelToGraph(node);
        }
        panToNode(node);
        displayNameDOM(node);
    },
    render: function () {
        var that = this;
        if (this.state) {
            var nodeList = this.state;
            return (
                <div className="nodeList">
                    <ul>
                        {nodeList.map(function (node) {
                            return <li key={node._id} onClick={that.selectNode.bind(that, node)}>{node.name || node._id}</li>
                        })}
                    </ul>
                </div>
            )
        } else {
            return (<div id="typeDetail">No type selected</div>)
        }
    }
});


/**
 * Tree react component
 */
var Tree = React.createClass({
    handleNode: function(){

    },
    getTreeRoots: function(){
        var tree = this.props.tree;
        if (tree.constructor !== Array){ //if tree is not an empty array, that means it has been initialized
            return tree.childNodes;
        }
    },
    render: function() {
        var eventHub = this.props.eventHub;
        var treeRoots = this.getTreeRoots();

        if (treeRoots){
            return (
                <div id="tree">
                    <ul id="roots">
                        {treeRoots.map(function (treeNode) {
                            return <TreeNode
                                key={treeNode.node._id}
                                treeNode={treeNode}
                                eventHub={eventHub}/>
                        })}
                    </ul>
                </div>
            )
        } else {
            return (
                <div id="tree">
                    Tree is loading...
                </div>
            )
        }
    }
});

/**
 * TreeList react component
 */
var TreeList = React.createClass({
    getInitialState: function () {
        return {
            tree: [],
        };
    },
    // allow to load the nodes only after their init in demo_vivagraph.js
    componentWillMount: function (){
        globalFunc.loadTree = () => {
            this.setState({tree: gTree});
        };
    },
    render: function () {
        return (
            <Tree eventHub={this.props.glEventHub} tree={this.state.tree}/>
        )
    }
});

/**
 * TreeNode react component
 */
var TreeNode = React.createClass({
    getInitialState: function() {
        return {
            treeNode: this.props.treeNode,
            visible: true
        }
    },
    handleChildNodes: function(){
        var childNodes = [];
        if (this.state.treeNode.childNodes){
            var eventHub = this.props.eventHub;
            childNodes = this.state.treeNode.childNodes.map(function (treeNode) {
                return <TreeNode
                    key={treeNode.node._id}
                    treeNode={treeNode}
                    eventHub={eventHub}/>
            })
        }
        return childNodes;
    },
    selectNode: function (e) {
        e.stopPropagation(); //otherwise it will call it recursively
        var node = this.state.treeNode.node;
        this.props.eventHub.emit('node-select', node);

        if (contains(gNodesDisplayed, node)){
            panToNode(node);
            displayNameDOM(node);
        }
    },
    selectNodeAndToggle: function (e) {
        e.stopPropagation(); //otherwise it will call it recursively
        var node = this.state.treeNode.node;
        this.props.eventHub.emit('node-select', node);

        this.setState({visible: !this.state.visible});

        if (contains(gNodesDisplayed, node)){
            panToNode(node);
            displayNameDOM(node);
        }
    },
    render: function() {
        var node = this.state.treeNode.node;
        var childNodes = this.handleChildNodes();
        var classObj;

        var style;
        if (!this.state.visible) {
            style = {display: "none"};
        }

        if (childNodes.length > 0){
            classObj = {
                treeRoots: true,
                togglable: true,
                "togglable-down": this.state.visible,
                "togglable-up": !this.state.visible
            };

            var classNames = Object.keys(classObj).filter(function(key) {
                return classObj[key];
            });

            var strClass = "";
            for (var i = 0; i < classNames.length; ++i){
                strClass += classNames[i] + " ";
            }

             return (
                 <li onClick={this.selectNodeAndToggle} className={strClass}><p className="treeRootTitle">{node.name || node._id}</p>
                     <ul style={style}>
                         {childNodes}
                     </ul>
                 </li>
             )
        } else {
            return (
                <li onClick={this.selectNode} className="treeNodes">{node.name || node._id}</li>
            )
        }

    }
});
/**
 * TableNode component
 * @param container
 * @param state
 * @constructor
 */
var TableNodeComponent = function( container, state ) {
    this._container = container;
    this._state = state;

    this._options = {
        editable: false,
        enableAddRow: false,
        enableCellNavigation: true
    };
    this._columns = createColumnsFromProperties();

    globalFunc.initTable = () => {
        this._grid = new Slick.Grid(
            this._container.getElement(),
            gAllNodes,
            this._columns,
            this._options
        );
    };

    this._container.on( 'resize', this._resize, this );
    this._container.on( 'destroy', this._destroy, this );
    this._resize();
};

TableNodeComponent.prototype._resize = function() {
    if (this._grid){
        this._grid.resizeCanvas();
        this._grid.autosizeColumns();
    }

};

TableNodeComponent.prototype._destroy = function() {
    this._grid.destroy();
};


/**
 * Create columns for the slickgrid from the properties of the nodes
 * @returns {Array}
 */
function createColumnsFromProperties(){
    var res = [];
    for (var i = 0; i < nodeProperties.length; ++i){
        var prop = nodeProperties[i];
        var obj = {
            id: prop,
            name: prop,
            field: prop
        };
        res.push(obj);
    }
    return res;
}

// Registration of the components for golden-layout
myLayout.registerComponent('node-list', NodeList);
myLayout.registerComponent('node-detail', NodeDetail);

myLayout.registerComponent('type-list', TypeList);
myLayout.registerComponent('type-detail', TypeDetail);

myLayout.registerComponent('tree-list', TreeList);

myLayout.registerComponent( 'Table', TableNodeComponent );

myLayout.registerComponent('Graph', function (container, componentState) {
    container.getElement().html('<div id="divGraph">' +
        '<div id="toggleLayout"><i class="fa fa-pause"></i>Pause layout' +
        '</div></div>');
});


myLayout.init();

console.timeEnd("layout");
/*
window.initGoldenLayout = function() {
    console.log(gAllNodes);
};*/

