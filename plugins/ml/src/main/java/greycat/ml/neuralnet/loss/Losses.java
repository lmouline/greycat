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
package greycat.ml.neuralnet.loss;

public class Losses {
    public static final int SUM_OF_SQUARES = 0;
    public static final int SOFTMAX = 1;
    public static final int ARGMAX = 2;
    public static final int MULTI_DIM_BINARY = 3;


    public static final int DEFAULT = SUM_OF_SQUARES;

    public static Loss getUnit(int lossUnit) {
        switch (lossUnit) {
            case SUM_OF_SQUARES:
                return SumOfSquares.instance();
            case SOFTMAX:
                return Softmax.instance();
            case ARGMAX:
                return ArgMax.instance();
            case MULTI_DIM_BINARY:
                return MultiDimensionalBinary.instance();
        }
        return getUnit(DEFAULT);
    }
}
