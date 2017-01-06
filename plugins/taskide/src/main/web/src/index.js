import React from 'react';
import ReactDOM from 'react-dom';
import SplitPane from 'react-split-pane';
import MonacoEditor from 'react-monaco-editor';
import renderjson from 'renderjson';

import 'bulma/css/bulma.css';
import './index.css';

global.context = {};

let defaultURL = "ws://" + window.location.hostname + ":"+window.location.port+"/ws";
//let defaultURL = "ws://" + window.location.hostname + ":"+4000+"/ws";

global.context.ws = new global.org.mwg.plugin.WSClient(defaultURL);
global.context.graph = global.org.mwg.GraphBuilder.newBuilder().withStorage(global.context.ws).build();
global.context.graph.connect(null);

global.context.url = function (val) {
    if(global.context.graph !== undefined){
        global.context.graph.disconnect(null);
    }
    global.context.ws = new global.org.mwg.plugin.WSClient(val);
    global.context.graph = global.org.mwg.GraphBuilder.newBuilder().withStorage(global.context.ws).build();
    global.context.graph.connect(null);
    console.log('change url to ' + val);
};

renderjson.set_show_to_level(3);

class Editor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            code: 'readGlobalIndex(nodes)'
        };
        global.context.code = this.state.code;
    }

    editorDidMount(editor, monaco) {
        editor.focus();
        editor.layout();
        global.context.layout = function () {
            editor.layout();
        };
        window.onresize = function () {
            global.context.layout();
        }
    }

    onChange(newValue, e) {
        global.context.code = newValue;
    }

    render() {
        const code = this.state.code;
        const options = {
            selectOnLineNumbers: true
        };
        return (
            <MonacoEditor
                width="100%"
                height="100%"
                language="javascript"
                value={code}
                options={options}
                onChange={this.onChange}
                editorDidMount={this.editorDidMount}
            />
        );
    }
}

const LoadingButton = React.createClass({
    getInitialState() {
        return {
            isLoading: false
        };
    },
    render() {
        let isLoading = this.state.isLoading;
        return (
            <button className={isLoading ? 'button is-primary is-loading' : 'button is-primary'}
                    onClick={!isLoading ? this.handleClick : null}>
                {isLoading ? '' : 'Execute'}
            </button>
        );
    },
    handleClick() {
        let self = this;
        self.setState({isLoading: true});
        let task = global.org.mwg.core.task.Actions.newTask();
        try {
            task.parse(global.context.code, window.context.graph);
            global.context.ws.executeTasks(function (results) {
                let targetDomElem = document.getElementById("json_result");
                while (targetDomElem.firstChild) {
                    targetDomElem.removeChild(targetDomElem.firstChild);
                }
                //console.log(results[0]);
                targetDomElem.appendChild(
                    renderjson(JSON.parse(results[0]))
                );
                self.setState({isLoading: false});
            }, task);
        } catch (e) {
            let targetDomElem = document.getElementById("json_result");
            while (targetDomElem.firstChild) {
                targetDomElem.removeChild(targetDomElem.firstChild);
            }
            targetDomElem.appendChild(
                renderjson({message: e.message})
            );
            self.setState({isLoading: false});
        }
    }
});

ReactDOM.render(
    <SplitPane split="horizontal" defaultSize={47} allowResize={false}>
        <div className="message-header is-primary flex">
            <p className="control is-horizontal has-addons">
                <input className="input" defaultValue={defaultURL} type="text" placeholder="IP:PORT"
                       onChange={event => global.context.url(event.target.value)}/>
                <LoadingButton />
            </p>

        </div>
        <SplitPane split="vertical" defaultSize="40%" minSize={50} onChange={
            size => global.context.layout()
        }>
            <Editor />
            <div id="json_result">

            </div>
        </SplitPane>
    </SplitPane>,
    document.getElementById('root')
);

