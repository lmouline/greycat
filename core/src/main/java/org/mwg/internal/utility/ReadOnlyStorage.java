/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.internal.utility;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.plugin.Storage;
import org.mwg.struct.Buffer;

public class ReadOnlyStorage implements Storage {

    private final Storage wrapped;

    public ReadOnlyStorage(final Storage toWrap) {
        wrapped = toWrap;
    }

    @Override
    public void get(Buffer keys, Callback<Buffer> callback) {
        wrapped.get(keys, callback);
    }

    @Override
    public void put(Buffer stream, Callback<Boolean> callback) {
        System.err.println("WARNING: PUT TO A READ ONLY STORAGE");
    }

    @Override
    public void remove(Buffer keys, Callback<Boolean> callback) {
        System.err.println("WARNING: REMOVE TO A READ ONLY STORAGE");
    }

    @Override
    public void connect(Graph graph, Callback<Boolean> callback) {
        wrapped.connect(graph, callback);
    }

    @Override
    public void disconnect(Callback<Boolean> callback) {
        wrapped.disconnect(callback);
    }

    @Override
    public void lock(Callback<Buffer> callback) {
        wrapped.lock(callback);
    }

    @Override
    public void unlock(Buffer previousLock, Callback<Boolean> callback) {
        wrapped.unlock(previousLock, callback);
    }
}
