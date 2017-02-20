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
import greycat.Node;
import greycat.Type;
import greycat.base.BaseNode;
import greycat.struct.EGraph;

/**
 * Created by assaad on 20/02/2017.
 */
public class GaussianNode extends BaseNode {
    public final static String NAME = "GaussianNode";

    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String AVG = "avg";
    public static final String COV = "cov";
    public static final String STD = "std";
    public static final String SUM = "sum";
    public static final String SUMSQ = "sumSquare";
    public static final String TOTAL = "total";
    public static final String VALUES = "values";
    public static final String PRECISIONS = "precisions";
    private static final String BACKEND="backend";

    private EGraph egraph;
    private GaussianENode backend;


    public GaussianNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
        egraph = (EGraph) super.getOrCreate(BACKEND, Type.EGRAPH);
        backend=new GaussianENode(egraph.newNode());
    }


    @Override
    public Node set(String name, byte type, Object value) {
        if(!load()){
            egraph = (EGraph) super.getOrCreate(BACKEND, Type.EGRAPH);
            backend=new GaussianENode(egraph.newNode());
        }
        switch (name){
            case VALUES:
                backend.learn((double[]) value);
                return this;
            case PRECISIONS:
                backend.setPrecisions((double[]) value);
                return this;
        }
        throw new RuntimeException("can't set anything other than precisions or values on this node!");
    }

    @Override
    public Object get(String attributeName) {
        if(!load()){
            return null;
        }
        switch (attributeName){
            case MIN:
                return backend.getMin();
            case MAX:
                return backend.getMax();
            case AVG:
                return backend.getAvg();
            case COV:
                return backend.getCovariance();
            case STD:
                return backend.getSTD();
            case SUM:
                return backend.getSum();
            case SUMSQ:
                return backend.getSumSq();
            case TOTAL:
                return backend.getTotal();
        }
        throw new RuntimeException("Attribute "+attributeName+" not found!");
    }

    private boolean load(){
        if(backend!=null){
            return true;
        }
        else {
            if(super.get(BACKEND)==null){
                return false;
            }
            else {
                egraph = (EGraph) super.get(BACKEND);
                if(egraph.root()==null){
                    return false;
                }
                backend=new GaussianENode(egraph.root());
                return true;
            }
        }
    }

}
