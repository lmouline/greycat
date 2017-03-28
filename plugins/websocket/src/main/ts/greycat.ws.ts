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

    constructor(p_url: string) {
        this.url = p_url;
        this.callbacks = {};
    }

    listen(cb: greycat.Callback<greycat.struct.Buffer>) {
        //TODO to propagate listener
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

    remove(keys: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void {
        this.send_rpc_req(this.REQ_REMOVE, keys, callback);
    }

    lock(callback: greycat.Callback<greycat.struct.Buffer>): void {
        this.send_rpc_req(this.REQ_LOCK, null, callback);
    }

    unlock(previousLock: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void {
        this.send_rpc_req(this.REQ_UNLOCK, previousLock, callback);
    }

    executeTasks(callback: greycat.Callback<String[]>, ...tasks: greycat.Task[]): void {
        let tasksBuffer = this.graph.newBuffer();
        for (let i = 0; i < tasks.length; i++) {
            if (i != 0) {
                tasksBuffer.write(greycat.Constants.BUFFER_SEP);
            }
            tasks[i].saveToBuffer(tasksBuffer);
        }
        let finalCB = callback;
        this.send_rpc_req(this.REQ_TASK, tasksBuffer, function (resultBuffer) {
            let result = [];
            let it = resultBuffer.iterator();
            while (it.hasNext()) {
                let view = it.next();
                result.push(greycat.utility.Base64.decodeToStringWithBounds(view, 0, view.length()));
            }
            resultBuffer.free();
            finalCB(result);
        });
    }

    //** TEMPORARY FIX :: task execution callback received before cache Notify terminates **//
    //TODO: remove
    private notificationCallbacks : any[] = [];
    registerNotificationCallback(cb) {
        this.notificationCallbacks.push(cb);
    }
    //** TEMPORARY FIX END :: task execution callback received before cache Notify terminates **//

    process_rpc_resp(payload: Int8Array) {
        let payloadBuf = this.graph.newBuffer();
        payloadBuf.writeAll(payload);
        let it = payloadBuf.iterator();
        let codeView = it.next();
        if (codeView != null && codeView.length() != 0) {
            let firstCode = codeView.read(0);
            if (firstCode == this.NOTIFY_UPDATE) {

                //TODO: remove
                //console.log("Processing Notify");
                while (it.hasNext()) {
                    let view = it.next();
                    let key = ChunkKey.build(view);
                    let hashView = it.next();
                    if (key != null && hashView != null) {
                        let hash = greycat.utility.Base64.decodeToLongWithBounds(hashView, 0, hashView.length());
                        let ch = this.graph.space().getAndMark(key.type, key.world, key.time, key.id);
                        if (ch != null) {
                            ch.sync(hash);
                            this.graph.space().unmark(ch.index());
                        }
                    }
                }
                //** TEMPORARY FIX :: task execution callback received before cache Notify terminates **//
                //TODO: remove
                //console.log("Processing Notify done. calling callbacks");
                for(let i = 0; i < this.notificationCallbacks.length; i++) {
                    this.notificationCallbacks[i]();
                    this.notificationCallbacks.slice(i,i);
                }
                //** TEMPORARY FIX END:: task execution callback received before cache Notify terminates **//
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




