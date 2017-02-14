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

import greycat.ml.neuralnet.layer.Layer;


// The abstract class here manages all three cases between full online learning,
// mini-batch, and full batch in an elegant way. All that is needed to be done is
// to implement the update method and take into account the numberOfSamples that
// have passed since the last update

public abstract class AbstractLearner implements Learner {
    protected int numberOfSamples;
    private int maxCounter;

    @Override
    public void setUpdateFrequency(int n) {
        this.maxCounter = n;
    }


    @Override
    public void stepUpdate(Layer[] layers) {
        numberOfSamples++;
        if (maxCounter > 0 && numberOfSamples == maxCounter) {
            update(layers);
            numberOfSamples = 0;
        }
    }

    @Override
    public void finalUpdate(Layer[] layers) {
        if (maxCounter <= 0 || numberOfSamples > 0) {
            update(layers);
            numberOfSamples = 0;
        }

    }

    protected abstract void update(Layer[] layers);

}
