/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
