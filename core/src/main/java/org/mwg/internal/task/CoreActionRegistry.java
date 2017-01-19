package org.mwg.internal.task;

import org.mwg.plugin.ActionDeclaration;
import org.mwg.plugin.ActionRegistry;

import java.util.HashMap;
import java.util.Map;

public class CoreActionRegistry implements ActionRegistry {

    private final Map<String, ActionDeclaration> backend = new HashMap<String, ActionDeclaration>();

    public CoreActionRegistry() {

    }

    @Override
    public final synchronized ActionDeclaration declaration(String name) {
        ActionDeclaration previous = backend.get(name);
        if (previous == null) {
            previous = new CoreActionDeclaration(name);
            backend.put(name, previous);
        }
        return previous;
    }

    @Override
    public final ActionDeclaration[] declarations() {
        ActionDeclaration[] result = backend.values().toArray(new ActionDeclaration[backend.size()]);
        return result;
    }

}
