package org.mwg.memory.offheap;

import org.mwg.struct.ENode;
import org.mwg.struct.ERelation;

public class OffHeapERelation implements ERelation {

    public OffHeapERelation(OffHeapStateChunk chunk, long found) {
    }

    @Override
    public ENode[] nodes() {
        return new ENode[0];
    }

    @Override
    public ENode node(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public ERelation add(ENode eNode) {
        return null;
    }

    @Override
    public ERelation addAll(ENode[] eNodes) {
        return null;
    }

    @Override
    public ERelation clear() {
        return null;
    }

    static void rebase(long addr) {
        // TODO
    }
}
