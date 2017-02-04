/**
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
package greycat;

import greycat.plugin.Job;
import greycat.struct.Buffer;
import greycat.struct.BufferIterator;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import greycat.chunk.Chunk;
import greycat.utility.KeyHelper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class WSServer implements WebSocketConnectionCallback {

    private final Graph graph;
    private final int port;
    private Undertow server;

    private Set<WebSocketChannel> peers;
    private Map<String, HttpHandler> handlers = new HashMap<String, HttpHandler>();

    public WSServer(Graph p_graph, int p_port) {
        this.graph = p_graph;
        this.port = p_port;
        peers = new HashSet<WebSocketChannel>();
    }

    public WSServer addHandler(String prefix, HttpHandler httpHandler) {
        handlers.put(prefix, httpHandler);
        return this;
    }

    public static final String PREFIX = "ws";

    public void start() {
        PathHandler pathHandler = Handlers.path();
        for (String name : handlers.keySet()) {
            pathHandler.addPrefixPath(name, handlers.get(name));
        }
        pathHandler.addPrefixPath(PREFIX, Handlers.websocket(this));
        this.server = Undertow.builder().addHttpListener(port, "0.0.0.0", pathHandler).build();
        server.start();
    }

    public void stop() {
        server.stop();
        server = null;
    }

    @Override
    public void onConnect(WebSocketHttpExchange webSocketHttpExchange, WebSocketChannel webSocketChannel) {
        webSocketChannel.getReceiveSetter().set(new PeerInternalListener());
        webSocketChannel.resumeReceives();
        peers.add(webSocketChannel);
    }

    private class PeerInternalListener extends AbstractReceiveListener {

        @Override
        protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
            ByteBuffer byteBuffer = WebSockets.mergeBuffers(message.getData().getResource());
            process_rpc(byteBuffer.array(), channel);
            super.onFullBinaryMessage(channel, message);
        }

        @Override
        protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
            process_rpc(message.getData().getBytes(), channel);
            super.onFullTextMessage(channel, message);
        }

        @Override
        protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) throws IOException {
            peers.remove(webSocketChannel);
            super.onClose(webSocketChannel, channel);
        }

    }

    private void process_rpc(final byte[] input, final WebSocketChannel channel) {
        if (input.length == 0) {
            return;
        }
        final Buffer payload = graph.newBuffer();
        payload.writeAll(input);
        final BufferIterator it = payload.iterator();
        final Buffer codeView = it.next();
        final Buffer callbackCodeView = it.next();
        if (codeView != null && callbackCodeView != null && codeView.length() != 0) {
            byte firstCodeView = codeView.read(0);
            //compute resp prefix
            switch (firstCodeView) {
                case WSConstants.REQ_GET:
                    //build keys list
                    final List<ChunkKey> keys = new ArrayList<ChunkKey>();
                    while (it.hasNext()) {
                        keys.add(ChunkKey.build(it.next()));
                    }
                    process_get(keys.toArray(new ChunkKey[keys.size()]), new Callback<Buffer>() {
                        @Override
                        public void on(Buffer streamResult) {
                            Buffer concat = graph.newBuffer();
                            concat.write(WSConstants.RESP_GET);
                            concat.write(Constants.BUFFER_SEP);
                            concat.writeAll(callbackCodeView.data());
                            concat.write(Constants.BUFFER_SEP);
                            concat.writeAll(streamResult.data());
                            streamResult.free();
                            payload.free();
                            WSServer.this.send_resp(concat, channel);
                        }
                    });
                    break;
                case WSConstants.REQ_TASK:
                    final List<Object> tasks = new ArrayList<Object>();
                    while (it.hasNext()) {
                        Task t = Tasks.newTask();
                        try {
                            t.loadFromBuffer(it.next(), graph);
                            tasks.add(t);
                        } catch (Exception e) {
                            tasks.add(Tasks.emptyResult().setException(e));
                        }
                    }
                    final TaskResult[] results = new TaskResult[tasks.size()];
                    final DeferCounter counter = graph.newCounter(tasks.size());
                    counter.then(new Job() {
                        @Override
                        public void run() {
                            Buffer concat = graph.newBuffer();
                            concat.write(WSConstants.RESP_TASK);
                            concat.write(Constants.BUFFER_SEP);
                            concat.writeAll(callbackCodeView.data());
                            for (int i = 0; i < tasks.size(); i++) {
                                concat.write(Constants.BUFFER_SEP);
                                //improve the interface
                                greycat.utility.Base64.encodeStringToBuffer(results[i].toString(), concat);
                                results[i].free();
                            }
                            payload.free();
                            WSServer.this.send_resp(concat, channel);
                        }
                    });
                    //call all subTask
                    for (int i = 0; i < tasks.size(); i++) {
                        int finalI = i;
                        Object elem = tasks.get(i);
                        if (elem instanceof TaskResult) {
                            results[finalI] = (TaskResult) elem;
                            counter.count();
                        } else {
                            ((Task) elem).execute(graph, new Callback<TaskResult>() {
                                @Override
                                public void on(TaskResult result) {
                                    results[finalI] = result;
                                    counter.count();
                                }
                            });
                        }
                    }
                    break;
                case WSConstants.REQ_LOCK:
                    process_lock(new Callback<Buffer>() {
                        @Override
                        public void on(Buffer result) {
                            Buffer concat = graph.newBuffer();
                            concat.write(WSConstants.RESP_LOCK);
                            concat.write(Constants.BUFFER_SEP);
                            concat.writeAll(callbackCodeView.data());
                            concat.write(Constants.BUFFER_SEP);
                            concat.writeAll(result.data());
                            result.free();
                            payload.free();
                            WSServer.this.send_resp(concat, channel);
                        }
                    });
                    break;
                case WSConstants.REQ_UNLOCK:
                    process_unlock(it.next(), new Callback<Boolean>() {
                        @Override
                        public void on(Boolean result) {
                            Buffer concat = graph.newBuffer();
                            concat.write(WSConstants.RESP_UNLOCK);
                            concat.write(Constants.BUFFER_SEP);
                            concat.writeAll(callbackCodeView.data());
                            payload.free();
                            WSServer.this.send_resp(concat, channel);
                        }
                    });
                    break;
                case WSConstants.REQ_PUT:
                    final List<ChunkKey> flatKeys = new ArrayList<ChunkKey>();
                    final List<Buffer> flatValues = new ArrayList<Buffer>();
                    while (it.hasNext()) {
                        final Buffer keyView = it.next();
                        final Buffer valueView = it.next();
                        if (valueView != null) {
                            flatKeys.add(ChunkKey.build(keyView));
                            flatValues.add(valueView);
                        }
                    }
                    final ChunkKey[] collectedKeys = flatKeys.toArray(new ChunkKey[flatKeys.size()]);
                    process_put(collectedKeys, flatValues.toArray(new Buffer[flatValues.size()]), new Job() {
                        @Override
                        public void run() {
                            graph.save(null);//TODO transaction management

                            Buffer concat = graph.newBuffer();
                            concat.write(WSConstants.RESP_UNLOCK);
                            concat.write(Constants.BUFFER_SEP);
                            concat.writeAll(callbackCodeView.data());
                            payload.free();
                            WSServer.this.send_resp(concat, channel);

                            //for all other channel
                            WebSocketChannel[] others = WSServer.this.peers.toArray(new WebSocketChannel[WSServer.this.peers.size()]);
                            Buffer notificationBuffer = graph.newBuffer();
                            notificationBuffer.write(WSConstants.REQ_UPDATE);
                            for (int i = 0; i < collectedKeys.length; i++) {
                                notificationBuffer.write(Constants.BUFFER_SEP);
                                KeyHelper.keyToBuffer(notificationBuffer, collectedKeys[i].type, collectedKeys[i].world, collectedKeys[i].time, collectedKeys[i].id);
                            }
                            byte[] notificationMsg = notificationBuffer.data();
                            notificationBuffer.free();
                            for (int i = 0; i < others.length; i++) {
                                if (!others[i].equals(channel)) {
                                    //send notification
                                    WSServer.this.send_flat_resp(notificationMsg, others[i]);
                                }
                            }
                        }
                    });
                    break;
            }
        }
    }

    private void process_lock(Callback<Buffer> callback) {
        graph.storage().lock(callback);
    }

    private void process_unlock(Buffer toUnlock, Callback<Boolean> callback) {
        graph.storage().unlock(toUnlock, callback);
    }

    private void process_put(final ChunkKey[] keys, final Buffer[] values, Job job) {
        final DeferCounter defer = graph.newCounter(keys.length);
        defer.then(job);
        for (int i = 0; i < keys.length; i++) {
            final int finalI = i;
            ChunkKey tuple = keys[i];
            graph.space().getOrLoadAndMark(tuple.type, tuple.world, tuple.time, tuple.id, new Callback<Chunk>() {
                @Override
                public void on(Chunk memoryChunk) {
                    if (memoryChunk != null) {
                        memoryChunk.load(values[finalI]);
                        graph.space().unmark(memoryChunk.index());
                    } else {
                        Chunk newChunk = graph.space().createAndMark(tuple.type, tuple.world, tuple.time, tuple.id);
                        if (newChunk != null) {
                            newChunk.load(values[finalI]);
                            graph.space().unmark(newChunk.index());
                        }

                    }
                    defer.count();
                }
            });
        }
    }

    private void process_get(ChunkKey[] keys, final Callback<Buffer> callback) {
        final DeferCounter defer = graph.newCounter(keys.length);
        final Buffer[] buffers = new Buffer[keys.length];
        defer.then(new Job() {
            @Override
            public void run() {
                Buffer stream = graph.newBuffer();
                for (int i = 0; i < buffers.length; i++) {
                    if (i != 0) {
                        stream.write(Constants.BUFFER_SEP);
                    }
                    if (buffers[i] != null) {
                        stream.writeAll(buffers[i].data());
                        buffers[i].free();
                    }
                }
                callback.on(stream);
            }
        });
        for (int i = 0; i < keys.length; i++) {
            final int fixedI = i;
            ChunkKey tuple = keys[i];
            graph.space().getOrLoadAndMark(tuple.type, tuple.world, tuple.time, tuple.id, new Callback<Chunk>() {
                @Override
                public void on(Chunk memoryChunk) {
                    if (memoryChunk != null) {
                        final Buffer toSaveBuffer = graph.newBuffer();
                        memoryChunk.save(toSaveBuffer);
                        graph.space().unmark(memoryChunk.index());
                        buffers[fixedI] = toSaveBuffer;
                    } else {
                        buffers[fixedI] = null;
                    }
                    defer.count();
                }
            });
        }
    }

    private void send_resp(Buffer stream, final WebSocketChannel channel) {
        ByteBuffer finalBuf = ByteBuffer.wrap(stream.data());
        stream.free();
        WebSockets.sendBinary(finalBuf, channel, null);
    }

    private void send_flat_resp(byte[] flat, final WebSocketChannel channel) {
        WebSockets.sendBinary(ByteBuffer.wrap(flat), channel, null);
    }

}
