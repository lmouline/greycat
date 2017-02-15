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
package greycat.ml.neuralnet.learner;

import greycat.struct.ENode;

/**
 * Created by assaad on 13/02/2017.
 */
public class Learners {
    public static final int GRADIENT_DESCENT = 0;
    public static final int RMSPROP = 1;

    public static final int DEFAULT = GRADIENT_DESCENT;


    public static Learner getUnit(int learnerUnit, ENode root) {
        switch (learnerUnit) {
            case GRADIENT_DESCENT:
                return new GradientDescent(root);
            case RMSPROP:
                return new RMSProp(root);
        }
        return getUnit(DEFAULT, root);
    }
}
