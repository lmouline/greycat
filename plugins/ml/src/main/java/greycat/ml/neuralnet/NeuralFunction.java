package greycat.ml.neuralnet;

public interface NeuralFunction {
    double activate(double x);
    double derivate(double x, double fct);
}
