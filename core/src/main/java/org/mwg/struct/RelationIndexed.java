package org.mwg.struct;

import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.Query;

public interface RelationIndexed {

    int size();

    long[] all();

    RelationIndexed add(Node node, String... attributeNames);

    RelationIndexed remove(Node node, String... attributeNames);

    RelationIndexed clear();

    void find(Callback<Node[]> callback, long world, long time, String... params);

    void findByQuery(Query query, Callback<Node[]> callback);

}
