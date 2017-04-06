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
package greycat.websocket;

import greycat.*;
import greycat.base.BaseTaskResult;
import greycat.plugin.TaskExecutor;
import greycat.struct.BufferIterator;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.core.*;
import greycat.chunk.Chunk;
import greycat.plugin.Storage;
import greycat.struct.Buffer;
import greycat.utility.Base64;
import org.xnio.*;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WSClient implements Storage, TaskExecutor {

    private final String url;

    private WebSocketChannel channel;

    private XnioWorker _worker;

    private Graph graph;

    private Map<Integer, Callback> callbacks;

    private final List<Callback<Buffer>> listeners = new ArrayList<Callback<Buffer>>();

    public WSClient(String p_url) {
        this.url = p_url;
        this.callbacks = new HashMap<Integer, Callback>();
    }

    @Override
    public void get(Buffer keys, Callback<Buffer> callback) {
        send_rpc_req(WSConstants.REQ_GET, keys, callback);
    }

    @Override
    public void put(Buffer stream, Callback<Boolean> callback) {
        send_rpc_req(WSConstants.REQ_PUT, stream, callback);
    }

    @Override
    public void putSilent(Buffer stream, Callback<Buffer> callback) {
        send_rpc_req(WSConstants.REQ_PUT, stream, new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                callback.on(null);
            }
        });
    }

    @Override
    public void remove(Buffer keys, Callback<Boolean> callback) {
        send_rpc_req(WSConstants.REQ_REMOVE, keys, callback);
    }

    @Override
    public void lock(Callback<Buffer> callback) {
        send_rpc_req(WSConstants.REQ_LOCK, null, callback);
    }

    @Override
    public void unlock(Buffer previousLock, Callback<Boolean> callback) {
        send_rpc_req(WSConstants.REQ_UNLOCK, previousLock, callback);
    }

    @Override
    public void connect(final Graph p_graph, final Callback<Boolean> callback) {
        if (channel != null) {
            if (callback != null) {
                callback.on(true);//already connected
            }
        }
        this.graph = p_graph;
        try {
            final Xnio xnio = Xnio.getInstance(io.undertow.websockets.client.WebSocketClient.class.getClassLoader());
            _worker = xnio.createWorker(OptionMap.builder()
                    .set(Options.WORKER_IO_THREADS, 2)
                    .set(Options.CONNECTION_HIGH_WATER, 1000000)
                    .set(Options.CONNECTION_LOW_WATER, 1000000)
                    .set(Options.WORKER_TASK_CORE_THREADS, 30)
                    .set(Options.WORKER_TASK_MAX_THREADS, 30)
                    .set(Options.TCP_NODELAY, true)
                    .set(Options.CORK, true)
                    .getMap());
            ByteBufferPool _buffer = new DefaultByteBufferPool(true, 1024 * 1024);
            WebSocketClient.ConnectionBuilder builder = io.undertow.websockets.client.WebSocketClient
                    .connectionBuilder(_worker, _buffer, new URI(url));

            /*
            if(_sslContext != null) {
                UndertowXnioSsl ssl = new UndertowXnioSsl(Xnio.getInstance(), OptionMap.EMPTY, _sslContext);
                builder.setSsl(ssl);
            }*/

            IoFuture<WebSocketChannel> futureChannel = builder.connect();
            futureChannel.await(5, TimeUnit.SECONDS); //Todo change this magic number!!!
            if (futureChannel.getStatus() != IoFuture.Status.DONE) {
                System.err.println("Error during connexion with webSocket");
                if (callback != null) {
                    callback.on(null);
                }
            }
            channel = futureChannel.get();
            channel.getReceiveSetter().set(new MessageReceiver());
            channel.resumeReceives();
            if (callback != null) {
                callback.on(true);
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.on(false);
            }
            e.printStackTrace();
        }
    }

    @Override
    public final void disconnect(Callback<Boolean> callback) {
        try {
            channel.sendClose();
            channel.close();
            _worker.shutdown();
            channel = null;
            _worker = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            callback.on(true);
        }
    }

    @Override
    public final void listen(Callback<Buffer> synCallback) {
        listeners.add(synCallback);
    }

    @Override
    public final void execute(final Callback<TaskResult> callback, final Task task, TaskContext prepared) {
        final Buffer buffer = graph.newBuffer();
        task.saveToBuffer(buffer);
        if (prepared != null) {
            buffer.write(Constants.BUFFER_SEP);
            //TODO saveTo
            throw new RuntimeException("Remote Task Context not implemented yet!");
        }
        send_rpc_req(WSConstants.REQ_TASK, buffer, new Callback<Buffer>() {
            @Override
            public void on(final Buffer bufferResult) {
                buffer.free();
                //process_notify(it.next());
                final BaseTaskResult baseTaskResult = new BaseTaskResult(null, false);
                baseTaskResult.load(bufferResult, graph);
                process_notify(baseTaskResult.notifications());
                baseTaskResult.loadRefs(graph, new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {
                        bufferResult.free();
                        callback.on(baseTaskResult);
                    }
                });
            }
        });
    }

    private class MessageReceiver extends AbstractReceiveListener {
        @Override
        protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
            ByteBuffer byteBuffer = WebSockets.mergeBuffers(message.getData().getResource());
            process_rpc_resp(byteBuffer.array());
            super.onFullBinaryMessage(channel, message);
        }

        @Override
        protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
            process_rpc_resp(message.getData().getBytes());
            super.onFullTextMessage(channel, message);
        }
    }

    private void send_rpc_req(byte operationId, Buffer payload, Callback callback) {
        if (channel == null) {
            throw new RuntimeException(WSConstants.DISCONNECTED_ERROR);
        }
        Buffer buffer = graph.newBuffer();
        buffer.write(operationId);
        buffer.write(Constants.BUFFER_SEP);
        int hash = callback.hashCode();
        callbacks.put(hash, callback);
        Base64.encodeIntToBuffer(hash, buffer);
        if (payload != null) {
            buffer.write(Constants.BUFFER_SEP);
            buffer.writeAll(payload.data());
        }
        ByteBuffer wrapped = ByteBuffer.wrap(buffer.data());
        buffer.free();
        WebSockets.sendBinary(wrapped, channel, new WebSocketCallback<Void>() {
            @Override
            public void complete(WebSocketChannel webSocketChannel, Void aVoid) {

            }

            @Override
            public void onError(WebSocketChannel webSocketChannel, Void aVoid, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void process_notify(Buffer buffer) {
        if (buffer != null) {
            byte type = 0;
            long world = 0;
            long time = 0;
            long id = 0;
            long hash = 0;
            int step = 0;
            long cursor = 0;
            long previous = 0;
            int end = (int) buffer.length();
            while (cursor < end) {
                byte current = buffer.read(cursor);
                if (current == Constants.KEY_SEP) {
                    switch (step) {
                        case 0:
                            type = (byte) Base64.decodeToIntWithBounds(buffer, previous, cursor);
                            break;
                        case 1:
                            world = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                            break;
                        case 2:
                            time = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                            break;
                        case 3:
                            id = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                            break;
                        case 4:
                            hash = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                            break;
                    }
                    previous = cursor + 1;
                    if (step == 4) {
                        step = 0;
                        final Chunk ch = graph.space().getAndMark(type, world, time, id);
                        if (ch != null) {
                            ch.sync(hash);
                            graph.space().unmark(ch.index());
                        }
                    } else {
                        step++;
                    }
                }
                cursor++;
            }
            switch (step) {
                case 0:
                    type = (byte) Base64.decodeToIntWithBounds(buffer, previous, cursor);
                    break;
                case 1:
                    world = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                    break;
                case 2:
                    time = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                    break;
                case 3:
                    id = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                    break;
                case 4:
                    hash = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                    break;
            }
            if (step == 4) {
                //invalidate
                final Chunk ch = graph.space().getAndMark(type, world, time, id);
                if (ch != null) {
                    ch.sync(hash);
                    graph.space().unmark(ch.index());
                }
            }
        }
    }

    private void process_rpc_resp(byte[] payload) {
        Buffer payloadBuf = graph.newBuffer();
        payloadBuf.writeAll(payload);
        BufferIterator it = payloadBuf.iterator();
        Buffer codeView = it.next();
        if (codeView != null && codeView.length() != 0) {
            final byte firstCode = codeView.read(0);
            if (firstCode == WSConstants.NOTIFY_UPDATE) {
                while (it.hasNext()) {
                    process_notify(it.next());
                }
                //optimize this
                if (listeners.size() > 0) {
                    final Buffer notifyBuffer = graph.newBuffer();
                    notifyBuffer.writeAll(payloadBuf.slice(1, payloadBuf.length() - 1));
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).on(notifyBuffer);
                    }
                    notifyBuffer.free();
                }
            } else {
                Buffer callbackCodeView = it.next();
                if (callbackCodeView != null) {
                    int callbackCode = Base64.decodeToIntWithBounds(callbackCodeView, 0, callbackCodeView.length());
                    Callback resolvedCallback = callbacks.get(callbackCode);
                    if (resolvedCallback != null) {
                        if (firstCode == WSConstants.RESP_LOCK || firstCode == WSConstants.RESP_GET || firstCode == WSConstants.RESP_TASK) {
                            Buffer newBuf = graph.newBuffer();//will be free by the core
                            boolean isFirst = true;
                            while (it.hasNext()) {
                                if (isFirst) {
                                    isFirst = false;
                                } else {
                                    newBuf.write(Constants.BUFFER_SEP);
                                }
                                newBuf.writeAll(it.next().data());
                            }
                            resolvedCallback.on(newBuf);
                        } else {
                            resolvedCallback.on(true);
                        }
                    }
                }
            }
        }
        payloadBuf.free();
    }

}
