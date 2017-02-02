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
package org.mwg.ml.neuralnet.functions;

import org.mwg.ml.neuralnet.NeuralFunction;

public class SigmoidFunction implements NeuralFunction {
    @Override
    public double activate(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    @Override
    public double derivate(double x, double fct) {
        return fct * (1 - fct);
    }
}
