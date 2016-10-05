import initVivaGraph = org.mwg.plugin.initVivaGraph;

interface Window {
    GoldenLayout?:any;
    monaco?:any;


}

declare function require(name,callback);

module org.mwg.plugins {
    
    var indexVisu = 0;

//To use local storage
// if(savedState != null) {
//     layout = new window.GoldenLayout(JSON.parse(savedState));
// } else {
// layout = new window.GoldenLayout(configLayout, document.getElementById("goldenLayout"));
// }
    let defaultConfig = {
        type: 'row',
        componantState : {graphVisu: Object()},
        content: [{
            type: "column",
            isClosable: false,
            content: [
                {
                    type:'stack',
                    isClosable: false,
                    content: [
                        {
                            type: 'component',
                            isClosable: false,
                            componentName: "Query Editor"
                        },
                        {
                            type: 'component',
                            isClosable: false,
                            componentName: 'Graph command'
                        }
                    ]
                }
                ,
                {
                    type: 'component',
                    isClosable: false,
                    componentName: 'nodeDetails',
                    title: "Node details"
                }
            ]
        },
            {
                title: "Graph",
                isClosable: false,
                type: 'component',
                componentName: 'Graph visualizer'
            }]
    };

    let globalConfig = {
        content: [{
            type: 'stack',
            isClosable: false,
            content: []
        }]
    };
    let layout;


    export function addVisu() {
        let elem:any = document.getElementsByName("graphUrl")[0];
        let url:string = elem.value;
        elem.value = "";

        let newItemConfig:any = defaultConfig;
        newItemConfig.title = url;

        layout.root.contentItems[0].addChild(newItemConfig);
    }

    export function addVisuWithString(url : string) {
        let newItemConfig:any = defaultConfig;
        newItemConfig.title = url;

        layout.root.contentItems[0].addChild(newItemConfig);
    }

    export function initLayout() {
        layout = new window.GoldenLayout(globalConfig, document.getElementById("goldenLayout"));
        layout.registerComponent('Graph command', function (container, componantState) {
            const graphVisu = container.parent.parent.parent.parent.config.componantState.graphVisu;
            container.getElement().html('' +
                `Time <input type="number" min="0" max="20" value="${org.mwg.plugin.INIT_TIME}"  step="1" class="timeWorldSelector" onchange="org.mwg.plugin.updateTime(this.value,graphVisu);"/> <br />` +
                `World <input type="number" min="0"  max="20" value="${org.mwg.plugin.INIT_WORLD}" step="1" class="timeWorldSelector" onchange="org.mwg.plugin.updateWorld(this.value,graphVisu);"/> <br />` +
                `Depth <input type="number" min="0"  max="20" value="${org.mwg.plugin.INIT_DEPTH}" step="1" class="timeWorldSelector" onchange="org.mwg.plugin.updateDepth(this.value,graphVisu);"/>`
            );
            
        });

        layout.registerComponent('Graph visualizer', function (container, componentState) {
            container.getElement().html('<div class="graphVisu" id="id' + indexVisu + '"></div>');
            container.on('open', initVivaGraph.bind(this, container.parent.parent.parent.config.title, "id" + indexVisu)); //fixme multiple stack

            container.on('open', function() {
                const url = container.parent.parent.parent.config.title;
                container.parent.parent.parent.config.componantState.graphVisu = initVivaGraph(url,"id" + indexVisu)
            });

            container.on('resize', function () {
                const graphVisu = container.parent.parent.parent.config.componantState.graphVisu;
                if(container.getElement().children("div div").children().length > 0) {
                    graphVisu._graphics.resetScale();
                    graphVisu._renderer.resume();
                }

            });

            indexVisu++;
        });

        layout.registerComponent('nodeDetails', function (container, componentState) {
            container.getElement().html('<div><pre id="nodeDetail">No node selected</pre></div>'); //todo fix multiple tab
        });

        layout.registerComponent('Query Editor',function(container, componentState){
            container.getElement().html('<div id="container"></div>');
            container.on('open', function(){
                require(['vs/editor/editor.main'], function() {
                    var editor = window.monaco.editor.create(document.getElementById('container'), {
                        value: [
                            'function x() {',
                            '\tconsole.log("Hello world!");',
                            '}'
                        ].join('\n'),
                        language: 'javascript'
                    });
                });
            });

        });

        layout.on('initialised',function () {
            var param = window.location.search.slice(1);
            if(param != null) {
                var paramsArray = param.split('&');
                if(paramsArray.length > 1) {
                    throw "The url should contain one parameter : q.";
                }
                var query = paramsArray[0].split('=');
                if (query[0] != "q") {
                    throw "The url should contain one parameter: q";
                }

                var inputs = document.getElementById("header").getElementsByTagName("input");
                for(var i=0; i<inputs.length;i++) {
                    (<any>inputs[i]).setAttribute("disabled","");
                }
                org.mwg.plugins.addVisuWithString(query[1]);
            }
        });

        layout.init();



    }

}
