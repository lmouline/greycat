package greycat.ml.neuralnet.functions;

import greycat.ml.neuralnet.NeuralUnit;

public class TanhUnit implements NeuralUnit {


	private static TanhUnit static_unit= null;

	public static TanhUnit instance() {
		if (static_unit == null) {
			static_unit = new TanhUnit();
		}
		return static_unit;
	}

	@Override
	public double forward(double x) {
		return Math.tanh(x);
	}

	@Override
	public double backward(double x) {
		double coshx = Math.cosh(x);
		double denom = (Math.cosh(2*x) + 1);
		return 4 * coshx * coshx / (denom * denom);
	}

}
