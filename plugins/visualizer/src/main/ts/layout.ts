import initVivaGraph = org.mwg.plugins.initVivaGraph;

interface Window {
    GoldenLayout?:any;
}

module org.mwg.plugins {
    
    var indexVisu = 0;

//To use local storage
// if(savedState != null) {
//     layout = new window.GoldenLayout(JSON.parse(savedState));
// } else {
// layout = new window.GoldenLayout(configLayout, document.getElementById("goldenLayout"));
// }
    var defaultConfig = {
        type: 'row',
        content: [{
            type: "column",
            isClosable: false,
            content: [{
                type: 'component',
                isClosable: false,
                componentName: 'Graph command'
            }, {
                type: 'component',
                isClosable: false,
                componentName: 'nodeDetails',
                title: "Node details"
            }]
        },
            {
                title: "Graph",
                isClosable: false,
                type: 'component',
                componentName: 'Graph visualizer'
            }]
    };

    var globalConfig = {
        content: [{
            type: 'stack',
            isClosable: false,
            content: []
        }]
    };
    var layout;


    export function addVisu() {
        var elem:any = document.getElementsByName("graphUrl")[0];
        var url:string = elem.value;
        elem.value = "";

        var newItemConfig:any = defaultConfig;
        newItemConfig.title = url;

        layout.root.contentItems[0].addChild(newItemConfig);
    }

    export function initLayout() {
        layout = new window.GoldenLayout(globalConfig, document.getElementById("goldenLayout"));
        layout.registerComponent('Graph command', function (container, componantState) {
            container.getElement().html('' +
                'Time <input type="number" min="0" max="20" value="0" step="1" class="timeWorldSelector" onchange="org.mwg.plugins.updateTime(this.value,defaultGraphVisu);"/> <br />' +
                'World <input type="number" min="0"  max="20" value="0" step="1" class="timeWorldSelector" onchange="org.mwg.plugins.updateWorld(this.value,defaultGraphVisu);"/>'
            );
            
        });

        layout.registerComponent('Graph visualizer', function (container, componentState) {
            container.getElement().html('<div class="graphVisu" id="id' + indexVisu + '"></div>');
            container.on('open', initVivaGraph.bind(this, container.parent.parent.parent.config.title, "id" + indexVisu)); //fixmultiple stack
            container.on('resize', function () {

                if(container.getElement().children("div div").children().length > 0) {
                    container.getElement().children("div div").children()[0].height = container.height;
                    container.getElement().children("div div").children()[0].width = container.width;
                    defaultGraphVisu._renderer.resume()
                }
            });

            indexVisu++;
        });

        layout.registerComponent('nodeDetails', function (container, componentState) {
            container.getElement().html('<div><pre id="nodeDetail">No node selected</pre></div>'); //todo fix multiple tab
        });

        layout.init();

    }

}
