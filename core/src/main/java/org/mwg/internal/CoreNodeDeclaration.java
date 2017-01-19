package org.mwg.internal;

import org.mwg.plugin.NodeDeclaration;
import org.mwg.plugin.NodeFactory;

class CoreNodeDeclaration implements NodeDeclaration {

    private final String _name;
    private NodeFactory _factory;

    CoreNodeDeclaration(String name) {
        _name = name;
    }

    @Override
    public final String name() {
        return _name;
    }

    @Override
    public final NodeFactory factory() {
        return _factory;
    }

    @Override
    public final void setFactory(NodeFactory newFactory) {
        _factory = newFactory;
    }
}
