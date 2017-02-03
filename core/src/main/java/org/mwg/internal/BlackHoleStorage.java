package org.mwg.internal;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.plugin.Storage;
import org.mwg.struct.Buffer;
import org.mwg.struct.BufferIterator;
import org.mwg.utility.Base64;

public class BlackHoleStorage implements Storage {

    private Graph _graph;

    private short prefix = 0;

    @Override
    public final void get(Buffer keys, Callback<Buffer> callback) {
        Buffer result = _graph.newBuffer();
        BufferIterator it = keys.iterator();
        boolean isFirst = true;

        while (it.hasNext()) {
            Buffer tempView = it.next();
            //do nothing with the view, redirect to BlackHole...
            if (isFirst) {
                isFirst = false;
            } else {
                result.write(CoreConstants.BUFFER_SEP);
            }
        }
        callback.on(result);
    }

    @Override
    public final void put(Buffer stream, Callback<Boolean> callback) {
        //System.err.println("WARNING: POTENTIAL DATA LOSSES, NOOP STORAGE don't save");
        if (callback != null) {
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
