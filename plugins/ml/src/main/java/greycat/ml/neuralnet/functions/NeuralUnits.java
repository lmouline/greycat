package greycat.ml.neuralnet.functions;

import greycat.ml.neuralnet.NeuralUnit;
import greycat.utility.distance.*;

/**
 * Created by assaad on 09/02/2017.
 */
public class NeuralUnits {
    public static final int LINEAR = 0;
    public static final int SIGMOID = 1;
    public static final int SINE = 2;
    public static final int TANH = 3;

    public static final int DEFAULT = LINEAR;

    public static NeuralUnit getUnit(int neuralUnit) {
        switch (neuralUnit) {
            case LINEAR:
                return LinearUnit.instance();
            case SIGMOID:
                return SigmoidUnit.instance();
            case SINE:
                return SineUnit.instance();
            case TANH:
                return TanhUnit.instance();
        }
        return getUnit(DEFAULT);
    }

}
