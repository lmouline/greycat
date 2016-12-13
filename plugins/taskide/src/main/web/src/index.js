import React from 'react';
import ReactDOM from 'react-dom';
import SplitPane from 'react-split-pane';
import MonacoEditor from 'react-monaco-editor';
import {Button} from 'react-bootstrap';
import renderjson from 'renderjson';

import 'bootstrap/dist/css/bootstrap.css';
import './index.css';

window.context = {};

renderjson.set_show_to_level(3);

class Editor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            code: 'travelInWorld(0)'
        };
        window.context.code = this.state.code;
    }

    editorDidMount(editor, monaco) {
        editor.focus();
    }

    onChange(newValue, e) {
        window.context.code = newValue;
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
            <Button
                bsStyle="primary"
                disabled={isLoading}
                onClick={!isLoading ? this.handleClick : null}>
                {isLoading ? 'Loading...' : 'Loading state'}
            </Button>
        );
    },
    handleClick() {
        this.setState({isLoading: true});
        let targetDomElem = document.getElementById("json_result");
        while (targetDomElem.firstChild) {
            targetDomElem.removeChild(targetDomElem.firstChild);
        }
        targetDomElem.appendChild(
            renderjson({ hello: [1,2,3,4], there: { a:1, b:2, c:["hello", null] } })
        );

        //console.log(window.context.code);

        setTimeout(() => {

            // Completed of async action, set loading state back
            this.setState({isLoading: false});
        }, 2000);
    }
});

ReactDOM.render(
    <SplitPane split="vertical" defaultSize="40%" minSize={50}>
        <Editor />
        <SplitPane split="horizontal" defaultSize="10%">
            <div>
                <LoadingButton />
            </div>
            <div id="json_result"></div>
        </SplitPane>
    </SplitPane>,
    document.getElementById('root')
);

