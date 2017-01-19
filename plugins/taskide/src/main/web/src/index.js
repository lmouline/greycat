import React from 'react';
import ReactDOM from 'react-dom';
import SplitPane from 'react-split-pane';
import renderjson from 'renderjson';

import TaskEditor from './TaskEditor.js'

import 'bulma/css/bulma.css';
import './index.css';

global.context = {};

//let defaultPORT = 4000;
let defaultPORT = window.location.port;
let defaultURL = "ws://" + window.location.hostname + ":" + defaultPORT + "/ws";

global.context.ws = new global.org.mwg.plugin.WSClient(defaultURL);
global.context.graph = global.org.mwg.GraphBuilder.newBuilder().withStorage(global.context.ws).build();
global.context.actions = [];
global.context.graph.connect(null);

global.context.url = function (val) {
    if (global.context.graph !== undefined) {
        global.context.graph.disconnect(null);
    }
    global.context.ws = new global.org.mwg.plugin.WSClient(val);
    global.context.graph = global.org.mwg.GraphBuilder.newBuilder().withStorage(global.context.ws).build();
    global.context.graph.connect(null);
    console.log('change url to ' + val);
};

fetch("http://"+window.location.hostname+":"+defaultPORT+"/actionregistry/").then(function (response) {
    return response.json().then(function (actions) {
        global.context.actions = actions;
    });
});

renderjson.set_show_to_level(3);

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
        let task = global.org.mwg.task.Tasks.newTask();
        try {
            task.parse(global.context.code, window.context.graph);
            global.context.ws.executeTasks(function (results) {
                let targetDomElem = document.getElementById("json_result");
                while (targetDomElem.firstChild) {
                    targetDomElem.removeChild(targetDomElem.firstChild);
                }
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
        <SplitPane split="vertical" defaultSize="40%" minSize={50} onChange={size => global.context.layout()}>
            <TaskEditor />
            <SplitPane split="horizontal" defaultSize="80%" allowResize={true}>
                <div id="json_result"></div>
                <div id="output_error_result"></div>
            </SplitPane>
        </SplitPane>
    </SplitPane>
    ,
    document.getElementById("root")
);

