package org.mwg.struct;

public interface EGraph {

    ENode root();

    ENode newNode();

    EGraph setRoot(ENode eNode);

    EGraph drop(ENode eNode);

    int size();

    void free();

}
