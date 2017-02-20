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
package greycat.ml.profiling;

import greycat.Graph;
import greycat.base.BaseNode;

public class GaussianSlotsNode extends BaseNode {
    public static final String PERIOD_SIZE = "PERIOD_SIZE"; //The period over which the profile returns to the initial slot
    public static final long PERIOD_SIZE_DEF = 24 * 3600 * 1000; //By default it is 24 hours

    public static final String NUMBER_OF_SLOTS = "numberOfSlots"; //Number of slots to create in the profile, default is 1
    public static final int NUMBER_OF_SLOTS_DEF = 1;



    public GaussianSlotsNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }



    public static int getIntTime(long time, int numOfSlot, long periodSize) {
        if (numOfSlot <= 1) {
            return 0;
        }
        long res = time % periodSize;
        res = res / (periodSize / numOfSlot);
        return (int) res;
    }


}
