package org.mwg.plugin;

public interface NodeRegistry {

    NodeDeclaration declaration(String name);

    NodeDeclaration declarationByHash(int hash);

}
