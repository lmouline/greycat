package org.mwg.plugin;

public interface NodeDeclaration {

    String name();

    NodeFactory factory();

    void setFactory(NodeFactory newFactory);

}
