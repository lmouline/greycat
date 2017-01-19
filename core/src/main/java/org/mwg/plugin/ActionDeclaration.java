package org.mwg.plugin;

public interface ActionDeclaration {

    ActionFactory factory();

    ActionDeclaration setFactory(ActionFactory factory);

    byte[] params();

    ActionDeclaration setParams(byte... params);

    String description();

    ActionDeclaration setDescription(String description);

    String name();

}
