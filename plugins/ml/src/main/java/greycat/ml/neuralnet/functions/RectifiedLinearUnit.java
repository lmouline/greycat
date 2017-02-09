package greycat.ml.neuralnet.functions;


import greycat.ml.neuralnet.NeuralUnit;

public class RectifiedLinearUnit implements NeuralUnit {
	private double slope;

	public RectifiedLinearUnit(double slope) {
		this.slope = slope;
	}
	
	@Override
	public double forward(double x) {
		if (x >= 0) {
			return x;
		}
		else {
			return x * slope;
		}
	}

	@Override
	public double backward(double x) {
		if (x >= 0) {
			return 1.0;
		}
		else {
			return slope;
		}
	}
}
