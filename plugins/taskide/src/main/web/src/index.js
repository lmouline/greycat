/*
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

global.context.ws = new global.greycat.plugin.WSClient(defaultURL);
global.context.graph = global.greycat.GraphBuilder.newBuilder().withStorage(global.context.ws).build();
global.context.actions = [];
global.context.graph.connect(null);

global.context.url = function (val) {
    if (global.context.graph !== undefined) {
        global.context.graph.disconnect(null);
    }
    global.context.ws = new global.geycat.plugin.WSClient(val);
    global.context.graph = global.greycat.GraphBuilder.newBuilder().withStorage(global.context.ws).build();
    global.context.graph.connect(null);
    console.log('change url to ' + val);
};

fetch("http://" + window.location.hostname + ":" + defaultPORT + "/actionregistry/").then(function (response) {
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
        let task = global.greycat.task.Tasks.newTask();
        try {
            task.parse(global.context.code, window.context.graph);
            global.context.ws.executeTasks(function (results) {
                let targetDomElem = document.getElementById("json_result");
                while (targetDomElem.firstChild) {
                    targetDomElem.removeChild(targetDomElem.firstChild);
                }
                let outputDomElem = document.getElementById("output_error_result");
                while (outputDomElem.firstChild) {
                    outputDomElem.removeChild(outputDomElem.firstChild);
                }
                let JSON_RESULT = JSON.parse(results[0]);
                if (JSON_RESULT['result'] !== undefined) {
                    targetDomElem.appendChild(
                        renderjson(JSON_RESULT['result'])
                    );
                }
                if (JSON_RESULT['error'] !== undefined) {
                    let contentNode = document.createElement("div");
                    contentNode.style.color = 'red';
                    contentNode.innerText = JSON_RESULT['error'];
                    outputDomElem.appendChild(contentNode);
                }
                if (JSON_RESULT['output'] !== undefined) {
                    let contentNode = document.createElement("span");
                    contentNode.innerText = JSON_RESULT['output'];
                    outputDomElem.appendChild(contentNode);
                }
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

