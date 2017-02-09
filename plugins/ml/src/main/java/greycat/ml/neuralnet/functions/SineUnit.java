package greycat.ml.neuralnet.functions;


import greycat.ml.neuralnet.NeuralUnit;

public class SineUnit implements NeuralUnit {

	private static SigmoidUnit static_unit= null;

	public static SigmoidUnit instance() {
		if (static_unit == null) {
			static_unit = new SigmoidUnit();
		}
		return static_unit;
	}

	@Override
	public double forward(double x) {
		return Math.sin(x);
	}

	@Override
	public double backward(double x) {
		return Math.cos(x);
	}
}
