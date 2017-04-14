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
package greycat.ml.actions;

import greycat.*;
import greycat.internal.task.TaskHelper;
import greycat.ml.MLPlugin;
import greycat.ml.profiling.Gaussian;
import greycat.plugin.Job;
import greycat.struct.Buffer;
import greycat.struct.Relation;


/**
 * Takes a list of feature nodes, and update the profile
 */


public class ActionBuildProfile implements Action {
    int _histogramBins;

    public ActionBuildProfile(int histogramBins){
        this._histogramBins=histogramBins;
    }

    @Override
    public void eval(TaskContext ctx) {
        TaskResult res = ctx.resultAsNodes();
        final DeferCounter allnodes = ctx.graph().newCounter(res.size());
        allnodes.then(new Job() {
            @Override
            public void run() {
                ctx.continueWith(res);
            }
        });


        for (int i = 0; i < res.size(); i++) {
            final Node feature = (Node) res.get(i);
            final long valueId = ((Relation) feature.get("value")).get(0);
            final double min = feature.getWithDefault("value_min", Double.NEGATIVE_INFINITY);
            final double max = feature.getWithDefault("value_max", Double.POSITIVE_INFINITY);
            //clear the previously created profile
            Gaussian.clearProfile(feature);

            ctx.graph().lookupAllTimes(ctx.world(), Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, new long[]{valueId}, new Callback<Node[]>() {
                @Override
                public void on(Node[] result) {
                    Double value;
                    for (int j = 0; j < result.length; j++) {
                        value = (Double) result[j].get("value");
                        Gaussian.profile(feature, value, min, max);
                    }

                    double pmin= feature.getWithDefault(Gaussian.MIN,0);
                    double pmax= feature.getWithDefault(Gaussian.MAX,0);

                    if(pmin!=pmax) {
                        for (int j = 0; j < result.length; j++) {
                            value = (Double) result[j].get("value");
                            Gaussian.histogram(feature, pmin, pmax, value,_histogramBins);
                        }
                    }

                    for (int j = 0; j < result.length; j++) {
                        result[j].free();
                    }

                    allnodes.count();
                }
            });

        }
    }

    @Override
    public void serialize(Buffer builder) {
        builder.writeString(MLPlugin.BUILD_PROFILE);
        builder.writeChar(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_histogramBins+"", builder,true);
        builder.writeChar(Constants.TASK_PARAM_CLOSE);
    }
}
