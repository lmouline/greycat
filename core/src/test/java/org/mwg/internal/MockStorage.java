package org.mwg.internal;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.plugin.Storage;
import org.mwg.struct.Buffer;
import org.mwg.struct.BufferIterator;
import org.mwg.utility.Base64;

import java.util.HashMap;
import java.util.Map;

public class MockStorage implements Storage {

    private Graph _graph;
    private short prefix = 0;
    private final Map<String, byte[]> backend = new HashMap<String, byte[]>();

    /**
     * @native ts
     * return p.toString();
     */
    private String keyToString(byte[] p) {
        return new String(p);
    }

    @Override
    public final void get(Buffer keys, Callback<Buffer> callback) {
        final Buffer result = _graph.newBuffer();
        final BufferIterator it = keys.iterator();
        boolean isFirst = true;
        while (it.hasNext()) {
            //do nothing with the view, redirect to BlackHole...
            byte[] key = it.next().data();
            final byte[] resolved = backend.get(keyToString(key));
            if (isFirst) {
                isFirst = false;
                if (resolved != null) {
                    result.writeAll(resolved);
                }
            } else {
                result.write(CoreConstants.BUFFER_SEP);
                if (resolved != null) {
                    result.writeAll(resolved);
                }
            }
        }
        callback.on(result);
    }

    @Override
    public final void put(Buffer stream, Callback<Boolean> callback) {
        if (callback != null) {
            Buffer result = _graph.newBuffer();
            BufferIterator it = stream.iterator();
            while (it.hasNext()) {
                Buffer keyView = it.next();
                Buffer valueView = it.next();
                backend.put(keyToString(keyView.data()), valueView.data());
            }
            callback.on(true);
        }
    }

    @Override
    public final void remove(Buffer keys, Callback<Boolean> callback) {
        callback.on(true);
    }

    @Override
    public final void connect(Graph graph, Callback<Boolean> callback) {
        _graph = graph;
        callback.on(true);
    }

    @Override
    public final void lock(Callback<Buffer> callback) {
        Buffer buffer = _graph.newBuffer();
        Base64.encodeIntToBuffer(prefix, buffer);
        prefix++;
        callback.on(buffer);
    }

    @Override
    public final void unlock(Buffer previousLock, Callback<Boolean> callback) {
        callback.on(true);
    }

    @Override
    public final void disconnect(Callback<Boolean> callback) {
        _graph = null;
        callback.on(true);
    }

}