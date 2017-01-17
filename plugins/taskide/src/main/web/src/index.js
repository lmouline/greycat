import React from 'react';
import ReactDOM from 'react-dom';
import SplitPane from 'react-split-pane';
import MonacoEditor from 'react-monaco-editor';
import renderjson from 'renderjson';

import 'bulma/css/bulma.css';
import './index.css';

global.context = {};

let defaultURL = "ws://" + window.location.hostname + ":"+window.location.port+"/ws";
//let defaultURL = "ws://" + window.location.hostname + ":" + 4000 + "/ws";

global.context.ws = new global.org.mwg.plugin.WSClient(defaultURL);
global.context.graph = global.org.mwg.GraphBuilder.newBuilder().withStorage(global.context.ws).build();
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

renderjson.set_show_to_level(3);

class Editor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            code: 'readGlobalIndex(nodes)'
        };
        global.context.code = this.state.code;
    }

    editorWillMount(monaco) {
        monaco.languages.register({id: 'mwgTask'});
        monaco.languages.registerCompletionItemProvider('mwgTask', {
            provideCompletionItems: function (model, position) {
                let textUntilPosition = model.getValueInRange({
                    startLineNumber: 1,
                    startColumn: 1,
                    endLineNumber: position.lineNumber,
                    endColumn: position.column
                }).trim();
                if (textUntilPosition.endsWith(".")) {
                    let names = global.org.mwg.internal.task.CoreActionNames;
                    let result = [];
                    for (let name in names) {
                        if (names.hasOwnProperty(name)) {
                            result.push({
                                label: names[name],
                                kind: monaco.languages.CompletionItemKind.Function,
                                insertText: names[name] + '()'
                            });
                        }
                    }
                    return result;
                } else {
                    return [];
                }
            }
        });
        monaco.languages.setMonarchTokensProvider('mwgTask', {
            // Set defaultToken to invalid to see what you do not tokenize yet
            // defaultToken: 'invalid',
            keywords: [
                'abstract', 'continue', 'for', 'new', 'switch', 'assert', 'goto', 'do',
                'if', 'private', 'this', 'break', 'protected', 'throw', 'else', 'public',
                'enum', 'return', 'catch', 'try', 'interface', 'static', 'class',
                'finally', 'const', 'super', 'while', 'true', 'false'
            ],

            typeKeywords: [
                'boolean', 'double', 'byte', 'int', 'short', 'char', 'void', 'long', 'float'
            ],

            operators: [
                '=', '>', '<', '!', '~', '?', ':', '==', '<=', '>=', '!=',
                '&&', '||', '++', '--', '+', '-', '*', '/', '&', '|', '^', '%',
                '<<', '>>', '>>>', '+=', '-=', '*=', '/=', '&=', '|=', '^=',
                '%=', '<<=', '>>=', '>>>='
            ],

            // we include these common regular expressions
            symbols:  /[=><!~?:&|+\-*\/\^%]+/,

            // C# style strings
            escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,

            // The main tokenizer for our languages
            tokenizer: {
                root: [
                    // identifiers and keywords
                    [/[a-z_$][\w$]*/, { cases: { '@typeKeywords': 'keyword',
                        '@keywords': 'keyword',
                        '@default': 'identifier' } }],
                    [/[A-Z][\w\$]*/, 'type.identifier' ],  // to show class names nicely

                    // whitespace
                    { include: '@whitespace' },

                    // delimiters and operators
                    [/[{}()\[\]]/, '@brackets'],
                    [/[<>](?!@symbols)/, '@brackets'],
                    [/@symbols/, { cases: { '@operators': 'operator',
                        '@default'  : '' } } ],

                    // @ annotations.
                    // As an example, we emit a debugging log message on these tokens.
                    // Note: message are supressed during the first load -- change some lines to see them.
                    [/@\s*[a-zA-Z_\$][\w\$]*/, { token: 'annotation', log: 'annotation token: $0' }],

                    // numbers
                    [/\d*\.\d+([eE][\-+]?\d+)?/, 'number.float'],
                    [/0[xX][0-9a-fA-F]+/, 'number.hex'],
                    [/\d+/, 'number'],

                    // delimiter: after number because of .\d floats
                    [/[;,.]/, 'delimiter'],

                    // strings
                    [/"([^"\\]|\\.)*$/, 'string.invalid' ],  // non-teminated string
                    [/"/,  { token: 'string.quote', bracket: '@open', next: '@string' } ],

                    // characters
                    [/'[^\\']'/, 'string'],
                    [/(')(@escapes)(')/, ['string','string.escape','string']],
                    [/'/, 'string.invalid']
                ],

                comment: [
                    [/[^\/*]+/, 'comment' ],
                    [/\/\*/,    'comment', '@push' ],    // nested comment
                    ["\\*/",    'comment', '@pop'  ],
                    [/[\/*]/,   'comment' ]
                ],

                string: [
                    [/[^\\"]+/,  'string'],
                    [/@escapes/, 'string.escape'],
                    [/\\./,      'string.escape.invalid'],
                    [/"/,        { token: 'string.quote', bracket: '@close', next: '@pop' } ]
                ],

                whitespace: [
                    [/[ \t\r\n]+/, 'white'],
                    [/\/\*/,       'comment', '@comment' ],
                    [/\/\/.*$/,    'comment'],
                ],
            },
        });

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
            selectOnLineNumbers: true,
            wrappingIndent: "indent",
            wrappingColumn: 0
        };
        return (
            <MonacoEditor
                width="100%"
                height="100%"
                language="mwgTask"
                value={code}
                options={options}
                onChange={this.onChange}
                editorDidMount={this.editorDidMount}
                editorWillMount={this.editorWillMount}
                theme="vs"
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
        let task = global.org.mwg.internal.task.CoreActions.newTask();
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
            <Editor />
            <SplitPane split="horizontal" defaultSize="80%" allowResize={true}>
                <div id="json_result"></div>
                <div id="output_error_result"></div>
            </SplitPane>
        </SplitPane>
    </SplitPane>
    ,
    document.getElementById("root")
);

