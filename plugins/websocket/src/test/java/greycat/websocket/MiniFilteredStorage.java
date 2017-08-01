package greycat.websocket;

import greycat.Callback;
import greycat.Constants;
import greycat.Graph;
import greycat.internal.heap.HeapBuffer;
import greycat.plugin.Storage;
import greycat.struct.Buffer;
import greycat.utility.Base64;

public class MiniFilteredStorage implements Storage {

    private final Storage backend;
    private final int[] negFilters;

    MiniFilteredStorage(final Storage proxy, int[] negFilters) {
        this.backend = proxy;
        this.negFilters = negFilters;
    }

    @Override
    public final void get(Buffer keys, Callback<Buffer> callback) {
        backend.get(keys, new Callback<Buffer>() {
            @Override
            public void on(final Buffer proxyResult) {
                final Buffer filtered = filter(proxyResult);
                proxyResult.free();
                callback.on(filtered);
            }
        });
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

    private Buffer filter(Buffer in) {
        final Buffer result = new HeapBuffer();
        long max = in.length();
        long cursor = 0;
        int group = 0;
        long previous = 0;
        while (cursor < max) {
            byte elem = in.read(cursor);
            switch (elem) {
                case Constants.CHUNK_META_SEP:
                    group = Base64.decodeToIntWithBounds(in, previous, cursor);
                    break;
                case Constants.BUFFER_SEP:
                    if (filterChunk(group)) {
                        result.writeAll(in.slice(previous, cursor));
                    } else {
                        result.write(Constants.BUFFER_SEP);
                    }
                    group = 0;
                    previous = cursor + 1;
                    break;
            }
            cursor++;
        }
        if (filterChunk(group)) {
            result.writeAll(in.slice(previous, cursor - 1));
        } else {
            result.write(Constants.BUFFER_SEP);
        }
        return result;
    }

    private boolean filterChunk(int group) {
        for (int i = 0; i < this.negFilters.length; i++) {
            if (group == this.negFilters[i]) {
                return false;
            }
        }
        return true;
    }


}
