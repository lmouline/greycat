package org.mwg.ml.neuralnet;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.ml.common.matrix.MatrixOps;
import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.ml.neuralnet.functions.SigmoidFunction;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.NodeState;
import org.mwg.struct.Matrix;

/**
 * Created by assaad on 21/11/2016.
 */
public class LSTMNeuralNode extends AbstractNode {




    public static String NAME = "LSTMNeuralNode";

    private final String FULL_INPUT_DIM = "full_input_dim";
    private final String OUTPUT_DIM = "output_dim";
    private final String CELL_BLOCK = "cell_blocks";


    //Matrices

    private final String CONTEXT = "context";
    private final String WEIGHTS = "weights";
    private final String WEIGHTS_OUT = "weights_out";
    private final String DS = "ds_weights";

    public final String INIT_WEIGHT_RANGE = "init_weight";
    public final double INIT_WEIGHT_RANGE_DEF = 0.1;

    public final String LEARNING_RATE = "learning_rate";
    public final double LEARNING_RATE_DEF = 0.07;


    public LSTMNeuralNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    public LSTMNeuralNode configure(int input_dimension, int output_dimension, int cell_blocks) {
        NodeState state = phasedState();

        //Set dimensions
        int full_input_dimension = input_dimension + cell_blocks + 1;
        state.setFromKey(FULL_INPUT_DIM, Type.INT, full_input_dimension);
        state.setFromKey(OUTPUT_DIM, Type.INT, output_dimension);
        state.setFromKey(CELL_BLOCK, Type.INT, cell_blocks);

        double range = state.getFromKeyWithDefault(INIT_WEIGHT_RANGE, INIT_WEIGHT_RANGE_DEF);

        //Initialize matrices
        Matrix context = (Matrix) state.getOrCreateFromKey(CONTEXT, Type.MATRIX);
        context.init(cell_blocks, 1);

        Matrix weights = (Matrix) state.getOrCreateFromKey(WEIGHTS, Type.MATRIX);
        weights.init(cell_blocks * 2, full_input_dimension);
        weights.fillWithRandom(-range, range, System.currentTimeMillis());

        Matrix ds = (Matrix) state.getOrCreateFromKey(DS, Type.MATRIX);
        ds.init(cell_blocks * 2, full_input_dimension);

        Matrix weightsOut = (Matrix) state.getOrCreateFromKey(WEIGHTS_OUT, Type.MATRIX);
        weightsOut.init(output_dimension, cell_blocks + 1);
        weightsOut.fillWithRandom(-range, range, System.currentTimeMillis());

        return this;
    }

    public void reset() {
        NodeState state = phasedState();
        Matrix context = (Matrix) state.getFromKey(CONTEXT);
        Matrix dS = (Matrix) state.getFromKey(DS);

        for (int c = 0; c < context.rows(); c++)
            context.set(c, 0, 0.0);
        //reset accumulated partials
        for (int c = 0; c < dS.rows(); c++) {
            for (int i = 0; i < dS.columns(); i++) {
                dS.set(c, i, 0.0);
            }
        }
    }


    public double[] predict(double[] input) {
        NodeState state = phasedState();
        return predictFromState(input, state);
    }


    private double[] predictFromState(double[] input, NodeState state) {

        int full_input_dimension = (int) state.getFromKey(FULL_INPUT_DIM);
        int cell_blocks = (int) state.getFromKey(CELL_BLOCK);


        Matrix weightsOut = (Matrix) state.getFromKey(WEIGHTS_OUT);
        Matrix weights = (Matrix) state.getFromKey(WEIGHTS);
        Matrix context = (Matrix) state.getFromKey(CONTEXT);
        Matrix dS = (Matrix) state.getFromKey(DS);

        //todo replace by static
        NeuralFunction F= new SigmoidFunction();
        NeuralFunction G= new SigmoidFunction();


        Matrix full_input = VolatileMatrix.empty(full_input_dimension, 1);

        //setup input vector


        int loc = 0;
        for (int i = 0; i < input.length; i++)
            full_input.set(loc++, 0, input[i]);
        for (int c = 0; c < context.rows(); c++)
            full_input.set(loc++, 0, context.get(c, 0));
        full_input.set(loc++, 0, 1.0); //bias

        //cell block arrays
        double[] actF = new double[cell_blocks];
        double[] actG = new double[cell_blocks];
        double[] actH = new double[cell_blocks];


        Matrix sum = MatrixOps.multiply(weights, full_input);


        for (int j = 0; j < cell_blocks; j++) {

            actF[j] = F.activate(sum.get(j, 0));
            actG[j] = G.activate(sum.get(j + cell_blocks, 0));
            actH[j] = actF[j] * context.get(j, 0) + (1 - actF[j]) * actG[j];
        }


        Matrix full_hidden = VolatileMatrix.empty(cell_blocks + 1, 1);

        //prepare hidden layer plus bias
        loc = 0;
        for (int j = 0; j < cell_blocks; j++)
            full_hidden.set(loc++, 0, actH[j]);
        full_hidden.set(loc++, 0, 1.0); //bias

        Matrix output = MatrixOps.multiply(weightsOut, full_hidden);


        //////////////////////////////////////////////////////////////
        //BACKPROP - preparation
        //////////////////////////////////////////////////////////////

        //scale partials
        for (int j = 0; j < cell_blocks; j++) {

            double f = actF[j];
            double df = F.derivate(sum.get(j, 0), f);
            double g = actG[j];
            double dg = G.derivate(sum.get(j + cell_blocks, 0), g);
            double h_ = context.get(j, 0); //prev value of h

            for (int i = 0; i < full_input_dimension; i++) {

                double prevdSdF = dS.get(j, i);
                double prevdSdG = dS.get(j + cell_blocks, i);
                double inp = full_input.get(i, 0);

                dS.set(j, i, ((h_ - g) * df * inp) + (f * prevdSdF));
                dS.set(j + cell_blocks, i, ((1 - f) * dg * inp) + (f * prevdSdG));
            }
        }

        //roll-over context to next time step
        for (int j = 0; j < cell_blocks; j++) {
            context.set(j, 0, actH[j]);
        }

        return output.data();
    }

    public double[] learn(double[] input, double[] output) {
        NodeState state = phasedState();
        double[] predicted = predictFromState(input, state);

        int full_input_dimension = (int) state.getFromKey(FULL_INPUT_DIM);
        int cell_blocks = (int) state.getFromKey(CELL_BLOCK);
        int output_dimension = (int) state.getFromKey(OUTPUT_DIM);
        double learningRate = state.getFromKeyWithDefault(LEARNING_RATE, LEARNING_RATE_DEF);


        Matrix weightsOut = (Matrix) state.getFromKey(WEIGHTS_OUT);
        Matrix weights = (Matrix) state.getFromKey(WEIGHTS);
        Matrix context = (Matrix) state.getFromKey(CONTEXT);
        Matrix dS = (Matrix) state.getFromKey(DS);


        //output to hidden
        double[] deltaOutput = new double[output_dimension];
        double[] deltaH = new double[cell_blocks];
        for (int k = 0; k < output_dimension; k++) {
            deltaOutput[k] = (output[k] - predicted[k]);
            for (int j = 0; j < cell_blocks; j++) {
                deltaH[j] += deltaOutput[k] * weightsOut.get(k, j);
                weightsOut.add(k, j, deltaOutput[k] * context.get(j, 0) * learningRate);
            }
            //bias
            weightsOut.add(k, cell_blocks, deltaOutput[k] * 1.0 * learningRate);
        }

        //input to hidden
        for (int j = 0; j < cell_blocks; j++) {
            for (int i = 0; i < full_input_dimension; i++) {
                weights.add(j, i, deltaH[j] * dS.get(j, i) * learningRate);
                weights.add(j + cell_blocks, i, deltaH[j] * dS.get(j + cell_blocks, i) * learningRate);
            }
        }

        return predicted;
    }


}
