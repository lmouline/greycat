package org.mwg.mlx.algorithm.ruleinference;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.ml.AbstractMLNode;
import org.mwg.mlx.algorithm.AbstractAnySlidingWindowManagingNode;
import org.mwg.mlx.algorithm.AbstractClassifierSlidingWindowManagingNode;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.NodeState;
import org.mwg.utility.Enforcer;

import java.util.List;

/**
 * Created by andrey.boytsov on 12/10/2016.
 *
 * TODO Stub. Not ready yet.
 */
public class BatchAprioriRuleNode extends AbstractMLNode {
    /**
     * Buffer size
     */
    public static final String BUFFER_SIZE_KEY = "BufferSize";
    /**
     * Buffer size - default
     */
    public static final int BUFFER_SIZE_DEF = 50;

    /**
     * Number of input dimensions
     */
    public static final String INPUT_DIM_KEY = "InputDimensions";
    /**
     * Number of input dimensions - default
     */
    public static final int INPUT_DIM_UNKNOWN = -1;
    /**
     * Number of input dimensions - default (unknown so far)
     */
    public static final int INPUT_DIM_DEF = INPUT_DIM_UNKNOWN;

    /**
     * Attribute key - sliding window of values
     */
    protected static final String INTERNAL_VALUE_BUFFER_KEY = "_valueBuffer";


    /**
     * Adjust buffer: adds value to the end of it, removes first value(s) if necessary.
     *
     * @param state
     * @param value
     */
    protected static double[] adjustValueBuffer(NodeState state, double value[], boolean bootstrapMode) {
        int dimensions = state.getFromKeyWithDefault(INPUT_DIM_KEY, INPUT_DIM_DEF);
        if (dimensions < 0) {
            dimensions = value.length;
            state.setFromKey(INPUT_DIM_KEY, Type.INT, value.length);
        }

        double buffer[] = state.getFromKeyWithDefault(INTERNAL_VALUE_BUFFER_KEY, new double[0]);

        final int bufferLength = buffer.length / dimensions; //Buffer is "unrolled" into 1D array.
        //So adding 1 value to the end and removing (currentBufferLength + 1) - maxBufferLength from the beginning.
        final int maxBufferLength = state.getFromKeyWithDefault(BUFFER_SIZE_KEY, BUFFER_SIZE_DEF);
        final int numValuesToRemoveFromBeginning = bootstrapMode ? 0 : Math.max(0, bufferLength + 1 - maxBufferLength);
        final int newBufferLength = bufferLength + 1 - numValuesToRemoveFromBeginning;

        double newBuffer[] = new double[newBufferLength * dimensions];
        System.arraycopy(buffer, numValuesToRemoveFromBeginning*dimensions, newBuffer, 0, newBuffer.length - dimensions);
        System.arraycopy(value, 0, newBuffer, newBuffer.length - dimensions, dimensions);

        state.setFromKey(INTERNAL_VALUE_BUFFER_KEY, Type.DOUBLE_ARRAY, newBuffer);
        return newBuffer;
    }

    /**
     * @return Class index - index in a value array, where class label is supposed to be
     */
    protected int getMaxBufferLength() {
        return unphasedState().getFromKeyWithDefault(BUFFER_SIZE_KEY, BUFFER_SIZE_DEF);
    }

    //Results buffer is set by further class. .
    private static final Enforcer enforcer = new Enforcer()
            .asPositiveInt(BUFFER_SIZE_KEY);

    @Override
    public void setProperty(String propertyName, byte propertyType, Object propertyValue) {
        if (INTERNAL_VALUE_BUFFER_KEY.equals(propertyName) || INPUT_DIM_KEY.equals(propertyName)) {
            //Nothing. They are unsettable directly
        } else {
            enforcer.check(propertyName, propertyType, propertyValue);
            super.setProperty(propertyName, propertyType, propertyValue);
        }
    }


    public BatchAprioriRuleNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }


    /**
     * Adds new value to the buffer. Connotations change depending on whether the node is in bootstrap mode or not.
     *
     * @param value New value to add; {@code null} disallowed
     */
    protected boolean addValue(boolean value[]) {
        illegalArgumentIfFalse(value != null, "Value must be not null");
        NodeState state = unphasedState();

        //TODO add value as if it was no-bootstrap mode
        return true;
    }

    //TODO retrieve last inferred rules? This can save a good deal of time.

}