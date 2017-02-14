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

/**
 * Created by assaad on 13/02/2017.
 */
public class Learners {
    public static final int STOCHASTIC_GD = 0;
    public static final int MINI_BATCH_GD = 1;
    public static final int BATCH_GD = 2;
    public static final int RMSPROP = 3;

    public static final int DEFAULT = STOCHASTIC_GD;


    public static Learner getUnit(int learnerUnit, double[] learnParams) {
        switch (learnerUnit) {
            case STOCHASTIC_GD:
                return null;
            case MINI_BATCH_GD:
                return null;
            case BATCH_GD:
                return null;
            case RMSPROP:
                return null ;
        }
        return getUnit(DEFAULT,learnParams);
    }
}
