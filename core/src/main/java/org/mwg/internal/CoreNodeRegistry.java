package org.mwg.internal;

import org.mwg.plugin.NodeDeclaration;
import org.mwg.plugin.NodeRegistry;
import org.mwg.utility.HashHelper;

import java.util.HashMap;
import java.util.Map;

class CoreNodeRegistry implements NodeRegistry {

    private final Map<String, NodeDeclaration> backend = new HashMap<String, NodeDeclaration>();
    private final Map<Integer, NodeDeclaration> backend_hash = new HashMap<Integer, NodeDeclaration>();

    CoreNodeRegistry() {
    }

    @Override
    public final synchronized NodeDeclaration declaration(String name) {
        NodeDeclaration previous = backend.get(name);
        if (previous == null) {
            previous = new CoreNodeDeclaration(name);
            backend.put(name, previous);
            backend_hash.put(HashHelper.hash(name), previous);
        }
        return previous;
    }

    @Override
    public final NodeDeclaration declarationByHash(int hash) {
        return backend_hash.get(hash);
    }


}
