package org.mwg;

public interface NodeIndex extends Node {

    long size();

    long[] all();

    NodeIndex addToIndex(Node node, String... attributeNames);

    NodeIndex removeFromIndex(Node node, String... attributeNames);

    NodeIndex clear();

    void find(Callback<Node[]> callback, String... params);

    void findByQuery(Query query, Callback<Node[]> callback);

}
