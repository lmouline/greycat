package org.mwg.struct;

public interface ERelation {

    ENode[] nodes();

    ENode node(int index);

    int size();

    ERelation add(ENode eNode);

    ERelation addAll(ENode[] eNodes);

    ERelation clear();

}
