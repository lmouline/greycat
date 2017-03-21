/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greycat.model;

import greycat.*;
import greycat.base.BaseNode;
import greycat.plugin.NodeState;
import greycat.plugin.NodeStateCallback;
import greycat.struct.EGraph;
import greycat.struct.ENode;

import java.util.ArrayList;
import java.util.List;

public class MetaClass extends BaseNode {

    public static final String NAME = "MetaClass";

    private final String attributes = "attributes";

    MetaClass(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    //TOOD concurrency check
    public final MetaClass declareAttribute(String name, byte type) {
        EGraph subAttributes = (EGraph) getOrCreate(attributes, Type.EGRAPH);
        ENode root = subAttributes.root();
        if (root == null) {
            root = subAttributes.newNode();
        }
        root.set(name, Type.INT, (int) type);
        return this;
    }


    public final MetaAttribute[] attributes() {
        List<MetaAttribute> result = new ArrayList<MetaAttribute>();
        EGraph subAttributes = (EGraph) get(attributes);
        if (subAttributes != null) {
            ENode root = subAttributes.root();
            if (root != null) {
                root.each(new NodeStateCallback() {
                    @Override
                    public void on(int attributeKey, byte elemType, Object elem) {
                        if (elemType == Type.INT) {
                            result.add(new MetaAttribute(_resolver.hashToString(attributeKey), (byte) (int) elem));
                        }
                    }
                });
            }
        }
        return result.toArray(new MetaAttribute[result.size()]);
    }

}
