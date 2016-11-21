package org.mwg.ml.neuralnet.functions;

import org.mwg.ml.neuralnet.NeuralFunction;

/**
 * Created by assaad on 10/11/2016.
 */
public class LinearFunction implements NeuralFunction{

    public LinearFunction(){

    }
    
    @Override
    public double activate(double x) {
        return x;
    }

    @Override
    public double derivate(double x, double fct) {
        return 1;
    }
}
