///
/// Copyright 2017 The MWG Authors.  All rights reserved.
/// <p>
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
/// <p>
/// http://www.apache.org/licenses/LICENSE-2.0
/// <p>
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

///<reference path='mwg.d.ts'/>
///<reference path='reconnecting-websocket.ts'/>

module org {
    export module mwg {
        export module plugin {
            export class WSClient implements org.mwg.plugin.Storage {

                private url:string;
                private callbacks:java.util.Map<number,org.mwg.Callback<any>>;
                private ws:WebSocketHelper.ReconnectingWebSocket = null;
                private graph:org.mwg.Graph = null;
                private generator:number = 0;

                private REQ_GET = 0;
                private REQ_PUT = 1;
                private REQ_LOCK = 2;
                private REQ_UNLOCK = 3;
                private REQ_REMOVE = 4;
                private REQ_UPDATE = 5;
                private REQ_TASK = 6;

                private RESP_GET = 7;
                private RESP_PUT = 8;
                private RESP_REMOVE = 9;
                private RESP_LOCK = 10;
                private RESP_UNLOCK = 11;
                private RESP_TASK = 12;

                constructor(p_url:string) {
                    this.url = p_url;
                    this.callbacks = new java.util.HashMap<number,org.mwg.Callback<any>>();
                }

                connect(p_graph:org.mwg.Graph, callback:org.mwg.Callback<boolean>):void {
                    this.graph = p_graph;
                    if (this.ws == null) {
                        let selfPointer = this;
                        this.ws = new WebSocketHelper.ReconnectingWebSocket(this.url);
                        this.ws.onopen = function (event:MessageEvent) {
                            callback(true);
                        };
                        this.ws.onmessage = function (msg:MessageEvent) {
                            selfPointer.process_rpc_resp(new Int8Array(msg.data));
                        };
                        this.ws.connect(false);
                    } else {
                        //do nothing
                        callback(true);
                    }
                }

                disconnect(callback:org.mwg.Callback<boolean>):void {
                    if (this.ws != null) {
                        this.ws.close();
                        this.ws = null;
                        callback(true);
                    }
                }

                get(keys:org.mwg.struct.Buffer, callback:org.mwg.Callback<org.mwg.struct.Buffer>):void {
                    this.send_rpc_req(this.REQ_GET, keys, callback);
                }

                put(stream:org.mwg.struct.Buffer, callback:org.mwg.Callback<boolean>):void {
                    this.send_rpc_req(this.REQ_PUT, stream, callback);
                }

                remove(keys:org.mwg.struct.Buffer, callback:org.mwg.Callback<boolean>):void {
                    this.send_rpc_req(this.REQ_REMOVE, keys, callback);
                }

                lock(callback:org.mwg.Callback<org.mwg.struct.Buffer>):void {
                    this.send_rpc_req(this.REQ_LOCK, null, callback);
                }

                unlock(previousLock:org.mwg.struct.Buffer, callback:org.mwg.Callback<boolean>):void {
                    this.send_rpc_req(this.REQ_UNLOCK, previousLock, callback);
                }

                executeTasks(callback:org.mwg.Callback<String[]>,...tasks: org.mwg.task.Task[]): void {
                    let tasksBuffer = this.graph.newBuffer();
                    for(let i=0;i<tasks.length;i++){
                        if(i!=0){
                            tasksBuffer.write(org.mwg.Constants.BUFFER_SEP);
                        }
                        tasks[i].saveToBuffer(tasksBuffer);
                    }
                    let finalCB = callback;
                    this.send_rpc_req(this.REQ_TASK, tasksBuffer, function(resultBuffer){
                        var result = [];
                        var it = resultBuffer.iterator();
                        while(it.hasNext()){
                            let view = it.next();
                            result.push(org.mwg.utility.Base64.decodeToStringWithBounds(view,0,view.length()));
                        }
                        resultBuffer.free();
                        finalCB(result);
                    });
                }

                process_rpc_resp(payload:Int8Array) {
                    let payloadBuf = this.graph.newBuffer();
                    payloadBuf.writeAll(payload);
                    let it = payloadBuf.iterator();
                    let codeView = it.next();
                    if (codeView != null && codeView.length() != 0) {
                        let firstCode = codeView.read(0);
                        if(firstCode == this.REQ_UPDATE){
                            //console.log("NOTIFY UPDATE"); //TODO
                        } else {
                            var callbackCodeView = it.next();
                            if(callbackCodeView != null){
                                var callbackCode = org.mwg.utility.Base64.decodeToIntWithBounds(callbackCodeView, 0, callbackCodeView.length());
                                var resolvedCallback = this.callbacks.get(callbackCode);
                                if (resolvedCallback != null) {
                                    if (firstCode == this.RESP_GET || firstCode == this.RESP_LOCK || firstCode == this.RESP_TASK) {
                                        var newBuf = this.graph.newBuffer();
                                        var isFirst = true;
                                        while (it.hasNext()) {
                                            if (isFirst) {
                                                isFirst = false;
                                            } else {
                                                newBuf.write(org.mwg.Constants.BUFFER_SEP);
                                            }
                                            newBuf.writeAll(it.next().data());
                                        }
                                        resolvedCallback(newBuf);
                                    } else {
                                        resolvedCallback(true);
                                    }
                                }
                            }
                        }
                    }
                }

                send_rpc_req(code:number, payload:org.mwg.struct.Buffer, callback:org.mwg.Callback<any>):void {
                    if (this.ws == null) {
                        throw new Error("Not connected!");
                    }
                    var buffer:org.mwg.struct.Buffer = this.graph.newBuffer();
                    buffer.write(code);
                    buffer.write(org.mwg.Constants.BUFFER_SEP);
                    var hash = this.generator;
                    this.generator = this.generator + 1 % 1000000;
                    this.callbacks.put(hash, callback);
                    org.mwg.utility.Base64.encodeIntToBuffer(hash, buffer);
                    if (payload != null) {
                        buffer.write(org.mwg.Constants.BUFFER_SEP);
                        buffer.writeAll(payload.data());
                    }
                    var flatData = buffer.data();
                    buffer.free();
                    this.ws.send(flatData);
                }

            }
        }
    }
}

