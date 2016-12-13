package org.mwg.task;

import org.mwg.struct.Buffer;

public interface TaskResult<A> {

    TaskResultIterator iterator();

    A get(int index);

    TaskResult<A> set(int index, A input);

    TaskResult<A> allocate(int index);

    TaskResult<A> add(A input);

    TaskResult<A> clear();

    TaskResult<A> clone();

    void free();

    int size();

    Object[] asArray();

    //void saveToBuffer(Buffer buffer);

}
