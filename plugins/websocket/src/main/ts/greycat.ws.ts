///
/// Copyright 2017 The GreyCat Authors.  All rights reserved.
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

import * as greycat from "greycat";

export class WSClient implements greycat.plugin.Storage {

    private url: string;
    private callbacks;
    private ws: WebSocket = null;
    private graph: greycat.Graph = null;
    private generator: number = 0;

    private REQ_GET = 0;
    private REQ_PUT = 1;
    private REQ_LOCK = 2;
    private REQ_UNLOCK = 3;
    private REQ_REMOVE = 4;
    private REQ_TASK = 5;
    private RESP_GET = 6;
    private RESP_PUT = 7;
    private RESP_REMOVE = 8;
    private RESP_LOCK = 9;
    private RESP_UNLOCK = 10;
    private RESP_TASK = 11;

    private NOTIFY_UPDATE = 12;
    private NOTIFY_PRINT = 12;

    constructor(p_url: string) {
        this.url = p_url;
        this.callbacks = {};
    }

    private _listeners: greycat.Callback<greycat.struct.Buffer>[] = [];

    listen(cb: greycat.Callback<greycat.struct.Buffer>) {
        this._listeners.push(cb);
    }

    connect(p_graph: greycat.Graph, callback: greycat.Callback<boolean>): void {
        this.graph = p_graph;
        if (this.ws == null) {
            let selfPointer = this;
            this.ws = new WebSocket(this.url);

            this.ws.onmessage = function (msg: MessageEvent) {
                let fr = new FileReader();
                fr.onload = function () {
                    selfPointer.process_rpc_resp(new Int8Array(fr.result));
                };
                fr.readAsArrayBuffer(msg.data);
            };

            this.ws.onclose = function (event: CloseEvent) {
                console.log("Connection closed.", event);
                if (this.readyState === WebSocket.CONNECTING) {
                    callback(false);
                }
            };

            this.ws.onerror = function (event: ErrorEvent) {
                console.error("An error occurred while connecting to server:", event);
                if (this.readyState === WebSocket.CONNECTING) {
                    callback(false);
                }
            };

            this.ws.onopen = function (event: Event) {
                callback(true);
            };
        } else {
            //do nothing
            callback(true);
        }
    }

    disconnect(callback: greycat.Callback<boolean>): void {
        if (this.ws != null) {
            this.ws.close();
            this.ws = null;
            callback(true);
        }
    }

    get(keys: greycat.struct.Buffer, callback: greycat.Callback<greycat.struct.Buffer>): void {
        this.send_rpc_req(this.REQ_GET, keys, callback);
    }

    put(stream: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void {
        this.send_rpc_req(this.REQ_PUT, stream, callback);
    }

    putSilent(stream: greycat.struct.Buffer, callback: greycat.Callback<greycat.struct.Buffer>): void {
        this.send_rpc_req(this.REQ_PUT, stream, function (b: boolean) {
            callback(null);
        });
    }

    remove(keys: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void {
        this.send_rpc_req(this.REQ_REMOVE, keys, callback);
    }

    lock(callback: greycat.Callback<greycat.struct.Buffer>): void {
        this.send_rpc_req(this.REQ_LOCK, null, callback);
    }

    unlock(previousLock: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void {
        this.send_rpc_req(this.REQ_UNLOCK, previousLock, callback);
    }

    execute(callback: greycat.Callback<greycat.TaskResult<any>>, task: greycat.Task, context: greycat.TaskContext): void {
        let reqBuffer = this.graph.newBuffer();
        let finalGraph = this.graph;
        task.saveToBuffer(reqBuffer);
        if (context != null) {
            //TODO
        }
        let finalCB = callback;
        let notifyMethod = this.process_notify;
        this.send_rpc_req(this.REQ_TASK, reqBuffer, function (resultBuffer) {
            reqBuffer.free();
            let baseTaskResult: greycat.base.BaseTaskResult<any> = new greycat.base.BaseTaskResult<any>(null, false);
            baseTaskResult.load(resultBuffer, finalGraph);
            notifyMethod(baseTaskResult.notifications(), finalGraph);
            baseTaskResult.loadRefs(finalGraph,function (b: boolean) {
                resultBuffer.free();
                finalCB(baseTaskResult);
            });
        });
    }

    process_notify(buffer : greycat.struct.Buffer, graph : greycat.Graph){
        if (buffer != null) {
            var type = 0;
            var world = 0;
            var time = 0;
            var id = 0;
            var hash = 0;
            var step = 0;
            var cursor = 0;
            var previous = 0;
            var end = buffer.length();
            while (cursor < end) {
                let current = buffer.read(cursor);
                if (current == greycat.Constants.KEY_SEP) {
                    switch (step) {
                        case 0:
                            type = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                            break;
                        case 1:
                            world = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                            break;
                        case 2:
                            time = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                            break;
                        case 3:
                            id = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                            break;
                        case 4:
                            hash = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                            break;
                    }
                    previous = cursor + 1;
                    if (step == 4) {
                        step = 0;
                        let ch : greycat.chunk.Chunk = this.graph.space().getAndMark(type, world, time, id);
                        if (ch != null) {
                            ch.sync(hash);
                            graph.unmark(ch.index());
                        }
                    } else {
                        step++;
                    }
                }
                cursor++;
            }
            switch (step) {
                case 0:
                    type = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                    break;
                case 1:
                    world = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                    break;
                case 2:
                    time = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                    break;
                case 3:
                    id = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                    break;
                case 4:
                    hash = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                    break;
            }
            if (step == 4) {
                //invalidate
                let ch : greycat.chunk.Chunk = this.graph.space().getAndMark(type, world, time, id);
                if (ch != null) {
                    ch.sync(hash);
                    this.graph.space().unmark(ch.index());
                }
            }
        }
    }

    process_rpc_resp(payload: Int8Array) {
        let payloadBuf = this.graph.newBuffer();
        payloadBuf.writeAll(payload);
        let it = payloadBuf.iterator();
        let codeView = it.next();
        if (codeView != null && codeView.length() != 0) {
            let firstCode = codeView.read(0);
            if (firstCode == this.NOTIFY_UPDATE) {
                while (it.hasNext()) {
                    this.process_notify(it.next(), this.graph);
                }
                //optimize this
                if (this._listeners.length > 0) {
                    const notifyBuffer = this.graph.newBuffer();
                    notifyBuffer.writeAll(payloadBuf.slice(1, payloadBuf.length() - 1));
                    for (var i = 0; i < this._listeners.length; i++) {
                        this._listeners[i](notifyBuffer);
                    }
                    notifyBuffer.free();
                }
            } else {
                let callbackCodeView = it.next();
                if (callbackCodeView != null) {
                    let callbackCode = greycat.utility.Base64.decodeToIntWithBounds(callbackCodeView, 0, callbackCodeView.length());
                    let resolvedCallback = this.callbacks[callbackCode];
                    this.callbacks[callbackCode] = undefined;
                    if (resolvedCallback != null) {
                        if (firstCode == this.RESP_GET || firstCode == this.RESP_LOCK || firstCode == this.RESP_TASK) {
                            let newBuf = this.graph.newBuffer();
                            let isFirst = true;
                            while (it.hasNext()) {
                                if (isFirst) {
                                    isFirst = false;
                                } else {
                                    newBuf.write(greycat.Constants.BUFFER_SEP);
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

    send_rpc_req(code: number, payload: greycat.struct.Buffer, callback: greycat.Callback<any>): void {
        if (this.ws == null) {
            throw new Error("Not connected!");
        }
        let buffer: greycat.struct.Buffer = this.graph.newBuffer();
        buffer.write(code);
        buffer.write(greycat.Constants.BUFFER_SEP);
        let hash = this.generator;
        this.generator = this.generator + 1 % 1000000;
        this.callbacks[hash] = callback;
        greycat.utility.Base64.encodeIntToBuffer(hash, buffer);
        if (payload != null) {
            buffer.write(greycat.Constants.BUFFER_SEP);
            buffer.writeAll(payload.data());
        }
        let flatData = buffer.data();
        buffer.free();
        this.ws.send(flatData);
    }

}

class ChunkKey {
    type: number;

    world: number;

    time: number;

    id: number;

    static build(buffer: greycat.struct.Buffer): ChunkKey {
        let result = new ChunkKey();
        var cursor = 0;
        let length = buffer.length();
        var previous = 0;
        var index = 0;
        while (cursor < length) {
            let current = buffer.read(cursor);
            if (current == greycat.Constants.KEY_SEP) {
                switch (index) {
                    case 0:
                        result.type = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                        break;
                    case 1:
                        result.world = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                        break;
                    case 2:
                        result.time = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                        break;
                    case 3:
                        result.id = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                        break;
                }
                index++;
                previous = cursor + 1;
            }
            cursor++;
        }
        switch (index) {
            case 0:
                result.type = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                break;
            case 1:
                result.world = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                break;
            case 2:
                result.time = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                break;
            case 3:
                result.id = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                break;
        }
        return result;
    };
}




