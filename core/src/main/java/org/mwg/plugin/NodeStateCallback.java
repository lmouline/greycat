package org.mwg.plugin;

@FunctionalInterface
public interface NodeStateCallback {

    void on(int attributeKey, byte elemType, Object elem);

}
