package org.mwg.plugin;

import org.mwg.struct.Buffer;

public interface ExternalAttribute {

    void save(Buffer buffer);

    void load(Buffer buffer);

    ExternalAttribute clone();

    void notifyDirty(Job dirtyNotifier);

}
