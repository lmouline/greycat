package org.mwg.plugin;


public interface ActionRegistry {

    ActionDeclaration declaration(String name);

    ActionDeclaration[] declarations();

}
