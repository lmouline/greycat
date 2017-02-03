package org.mwg.internal.task;

import org.mwg.plugin.ActionDeclaration;
import org.mwg.plugin.ActionFactory;

class CoreActionDeclaration implements ActionDeclaration {

    private ActionFactory _factory = null;
    private byte[] _params = null;
    private String _description = null;
    private final String _name;

    CoreActionDeclaration(String name) {
        this._name = name;
    }

    @Override
    public final ActionFactory factory() {
        return _factory;
    }

    @Override
    public final ActionDeclaration setFactory(ActionFactory factory) {
        this._factory = factory;
        return this;
    }

    @Override
    public final byte[] params() {
        return _params;
    }

    /**
     * @native ts
     * this._params = Int8Array.from(params);
     * return this;
     */
    @Override
    public final ActionDeclaration setParams(byte... params) {
        this._params = params;
        return this;
    }

    @Override
    public final String description() {
        return _description;
    }

    @Override
    public final ActionDeclaration setDescription(String description) {
        this._description = description;
        return this;
    }

    @Override
    public final String name() {
        return _name;
    }

}
