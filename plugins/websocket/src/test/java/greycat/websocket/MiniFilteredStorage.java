package greycat.websocket;

import greycat.Callback;
import greycat.Graph;
import greycat.plugin.Storage;
import greycat.struct.Buffer;

public class MiniFilteredStorage implements Storage {

    private final Storage backend;

    MiniFilteredStorage(final Storage proxy) {
        this.backend = proxy;
    }

    @Override
    public final void get(Buffer keys, Callback<Buffer> callback) {
        backend.get(keys, callback);
    }

    @Override
    public final void put(Buffer stream, Callback<Boolean> callback) {
        backend.put(stream, callback);
    }

    @Override
    public final void putSilent(Buffer stream, Callback<Buffer> callback) {
        backend.putSilent(stream, callback);
    }

    @Override
    public final void remove(Buffer keys, Callback<Boolean> callback) {
        backend.remove(keys, callback);
    }

    @Override
    public final void connect(Graph graph, Callback<Boolean> callback) {
        backend.connect(graph, callback);
    }

    @Override
    public final void lock(Callback<Buffer> callback) {
        backend.lock(callback);
    }

    @Override
    public final void unlock(Buffer previousLock, Callback<Boolean> callback) {
        backend.unlock(previousLock, callback);
    }

    @Override
    public final void disconnect(Callback<Boolean> callback) {
        backend.disconnect(callback);
    }

    @Override
    public final void listen(Callback<Buffer> synCallback) {
        backend.listen(synCallback);
    }
}
