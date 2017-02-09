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
package greycat.ml.neuralnet.functions;

import greycat.ml.neuralnet.NeuralUnit;
import greycat.utility.distance.*;

/**
 * Created by assaad on 09/02/2017.
 */
public class NeuralUnits {
    public static final int LINEAR = 0;
    public static final int SIGMOID = 1;
    public static final int SINE = 2;
    public static final int TANH = 3;
    public static final int RECTIFIED_LINEAR=4;

    public static final int DEFAULT = LINEAR;

    public static NeuralUnit getUnit(int neuralUnit, double[] unitarg) {
        switch (neuralUnit) {
            case LINEAR:
                return LinearUnit.instance();
            case SIGMOID:
                return SigmoidUnit.instance();
            case SINE:
                return SineUnit.instance();
            case TANH:
                return TanhUnit.instance();
            case RECTIFIED_LINEAR:
                return new RectifiedLinearUnit(unitarg[0]);
        }
        return getUnit(DEFAULT, null);
    }

}
