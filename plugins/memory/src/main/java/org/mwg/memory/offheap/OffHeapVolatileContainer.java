package org.mwg.memory.offheap;

public class OffHeapVolatileContainer implements OffHeapContainer {
    private long indexedAddr;

    public OffHeapVolatileContainer() {
        indexedAddr = OffHeapConstants.NULL_PTR;
    }

    @Override
    public long addrByIndex(long elemIndex) {
        return indexedAddr;
    }

    @Override
    public void setAddrByIndex(long elemIndex, long newAddr) {
        indexedAddr = newAddr;
    }

    @Override
    public void lock() {
        // do nothing
    }

    @Override
    public void unlock() {
        // do nothing
    }

    @Override
    public void declareDirty() {
        // do nothing
    }
}
