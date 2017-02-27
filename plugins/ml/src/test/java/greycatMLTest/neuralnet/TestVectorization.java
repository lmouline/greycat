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
package greycatMLTest.neuralnet;

import greycat.Callback;
import greycat.Graph;
import greycat.GraphBuilder;
import greycat.ml.MLPlugin;
import greycat.ml.common.matrix.MatrixOps;
import greycat.ml.common.matrix.VolatileDMatrix;
import greycat.struct.DMatrix;
import org.junit.Test;

import java.util.Random;

/**
 * Created by assaad on 27/02/2017.
 */
public class TestVectorization {

    //@Test
    public void vectorize(){
        Graph g= GraphBuilder.newBuilder().withPlugin(new MLPlugin()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                //set number of input to outputs
                int inputdim=5;
                int outputdim=2;

                //number of training set to generate
                int trainset=100;

                DMatrix inputs= VolatileDMatrix.random(inputdim,trainset,-1,1);
                DMatrix linearsys= VolatileDMatrix.random(outputdim,inputdim,-2,2);
                DMatrix outputs= MatrixOps.multiply(linearsys,inputs);
                System.out.println(outputs.rows()+" , "+outputs.columns());





            }
        });

















    }

}
