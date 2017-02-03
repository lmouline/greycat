package greycat.ml.neuralnet.functions;

import greycat.ml.neuralnet.NeuralFunction;

public class LinearFunction implements NeuralFunction{
    @Override
    public double activate(double x) {
        return x;
    }

    @Override
    public double derivate(double x, double fct) {
        return 1;
    }
}
