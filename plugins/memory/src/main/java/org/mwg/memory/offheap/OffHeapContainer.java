package org.mwg.memory.offheap;

public interface OffHeapContainer {

    long addrByIndex(long elemIndex);

    void setAddrByIndex(long elemIndex, long newAddr);

    void lock();

    void unlock();

    void declareDirty();
}
