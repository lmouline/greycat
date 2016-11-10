package org.mwg.ml.neuralnet;

/**
 * Created by assaad on 10/11/2016.
 */
public interface NeuralFunction {
    double activate(double x);
    double derivate(double x, double fct);
}
