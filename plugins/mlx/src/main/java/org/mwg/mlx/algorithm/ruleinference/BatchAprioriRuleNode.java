package org.mwg.mlx.algorithm.ruleinference;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.ml.AbstractMLNode;
import org.mwg.plugin.NodeState;
import org.mwg.utility.Enforcer;

import java.util.*;

/**
 * Created by andrey.boytsov on 12/10/2016.
 *
 * TODO Stub. Not ready yet.
 */
public class BatchAprioriRuleNode extends AbstractMLNode {

    public static final String NAME = "BatchAprioriRuleNode";

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
     * Attribute key - size of largest detected association
     */
    protected static final String INTERNAL_MAX_RULE_SIZE = "_maxRuleSize";

    /**
     * Attribute key - association rules
     */
    protected static final String INTERNAL_RULES = "_rules";

    /**
     * Attribute key - Required number of occurrences for the sequence to be considered.
     */
    public static final String SUPPORT_LIMIT_KEY = "requiredSupport";
    /**
     * Default value - Required number of occurrences for the sequence to be considered.
     */
    protected static final int SUPPORT_LIMIT_DEF = 3;

    /**
     * Adjust buffer: adds value to the end of it, removes first value(s) if necessary.
     * thebn
     *
     * @param state Node state to get/set properties
     * @param value New value to be added to the buffer
     * @return New value buffer
     */
    protected static int[] adjustValueBuffer(NodeState state, int value[]) {
        int dimensions = state.getFromKeyWithDefault(INPUT_DIM_KEY, INPUT_DIM_DEF);
        if (dimensions < 0) {
            dimensions = value.length;
            state.setFromKey(INPUT_DIM_KEY, Type.INT, value.length);
        }

        int buffer[] = state.getFromKeyWithDefault(INTERNAL_VALUE_BUFFER_KEY, new int[0]);

        final int bufferLength = buffer.length / dimensions; //Buffer is "unrolled" into 1D array.
        //So adding 1 value to the end and removing (currentBufferLength + 1) - maxBufferLength from the beginning.
        final int maxBufferLength = state.getFromKeyWithDefault(BUFFER_SIZE_KEY, BUFFER_SIZE_DEF);
        final int numValuesToRemoveFromBeginning = Math.max(0, bufferLength + 1 - maxBufferLength);
        final int newBufferLength = bufferLength + 1 - numValuesToRemoveFromBeginning;

        int newBuffer[] = new int[newBufferLength * dimensions];

        System.arraycopy(buffer, numValuesToRemoveFromBeginning*dimensions, newBuffer, 0, newBuffer.length - dimensions);
        System.arraycopy(value, 0, newBuffer, newBuffer.length - dimensions, dimensions);

        state.setFromKey(INTERNAL_VALUE_BUFFER_KEY, Type.INT_ARRAY, newBuffer);

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
            .asPositiveInt(BUFFER_SIZE_KEY)
            .asPositiveInt(SUPPORT_LIMIT_KEY);

    @Override
    public void setProperty(String propertyName, byte propertyType, Object propertyValue) {
        if (INTERNAL_VALUE_BUFFER_KEY.equals(propertyName) || INPUT_DIM_KEY.equals(propertyName)) {
            //Nothing. They are unsettable directly
        } else {
            enforcer.check(propertyName, propertyType, propertyValue);
            super.setProperty(propertyName, propertyType, propertyValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    public BatchAprioriRuleNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    /**
     * Updates model parameter (i.e. looks for new rule set)
     *
     * @param state Node state to get/set properties
     * @param newBuffer New value buffer to detect rules
     */
    protected void updateModelParameters(NodeState state, int newBuffer[]){
        final int minRequiredSupport = state.getFromKeyWithDefault(SUPPORT_LIMIT_KEY, SUPPORT_LIMIT_DEF);
        final int dimensions = state.getFromKeyWithDefault(INPUT_DIM_KEY, INPUT_DIM_DEF);
        //TODO Dimensions should not be zero at that point. Assert?

        final int numOfRows = newBuffer.length / dimensions;
        //TODO Should be dividable exactly. Assert?

        //Step 1. Get the "alphabet" of items.
        Set<Integer> alphabetSet = new HashSet<>();
        for (int i = 0; i<numOfRows ; i++){
            for (int j=0;j<dimensions;j++){
                if (newBuffer[i*dimensions+j] > 0){
                    alphabetSet.add(j);
                }
            }
        }

        //Step 3. Apply Apriori algorithm.
        /*
        f = list([[]]) #0-sequences do not exist. Also it makes making list of lists easier. For prototype - OK.

        #Let's flatten this 2D list of lists
        alphabet = [] # Kind of "alphabet" to detect possible sequences
        for i in dataset:
            alphabet.extend(i)
        alphabet = set(alphabet)
        print(alphabet)
        f.append([set([i]) for i in alphabet]) # Distinct 1-sequences
        k = 2
        while (len(f[k-1]) > 0):
            sequence_list = []
            # Step 1. Get possible subsequences, combined from existing n-1 sequence and a symbol in the alphabet
            for subseq in f[k-1]:
                for letter in alphabet:
                    #Generate the sequence
                    new_subsequence = set(subseq)
                    if letter not in new_subsequence:
                        new_subsequence = new_subsequence.union({letter})
                        #Check support. If support is less than the threshold - do not count.
                        support = 0
                        for i in dataset:
                            if (new_subsequence.issubset(i)):
                                support += 1
                        if (support >= support_threshold):
                            sequence_list.append(new_subsequence)
            k+=1
            sequence_list = {frozenset(i) for i in sequence_list}
            f.append(list(sequence_list))
        return f
        */

        //Sequence encoding in int array:
        //[0] - number of 1-sequences (e.g. n)
        //[1..n] - 1-sequences
        //[n+1] - Number of 2-sequences (e.g. k)
        //[n+2..n+2+2*k] - 2-sequences
        //etc.

        List<Integer> finalArray = new ArrayList<>();

        final List<List<Set<Integer>>> sequences = new ArrayList<>();
        sequences.add(new ArrayList<>()); //0-sequences do not exist

        int k=1;
        Set<Set<Integer>> testedRules = new HashSet<>(); //To rule out order-based duplications
        do{
            final List<Set<Integer>> nextRules = new ArrayList<>();

            //Number of k-sequences
            int finalArrayRuleNumberIndex = finalArray.size();
            finalArray.add(0); //Start with 0, later increase

            List<Set<Integer>> lastRules = sequences.get(k-1);
            if (lastRules.isEmpty()){
                lastRules.add(new HashSet<>());
            }

            for (final Set<Integer> ruleStub : lastRules){
                for (final Integer letter : alphabetSet){
                    final Set<Integer> newRuleToTest = new HashSet<>();
                    newRuleToTest.addAll(ruleStub);
                    newRuleToTest.add(letter);
                    if ((newRuleToTest.size() == k) && (!testedRules.contains(newRuleToTest))){
                        //Checking support
                        int support = 0;

                        for (int i=0;i<numOfRows;i++) {
                            Set<Integer> rowSet = new HashSet<>();
                            for (int j=0;j<dimensions;j++){
                                if (newBuffer[i*dimensions+j] > 0){
                                    rowSet.add(j);
                                }
                            }
                            if (rowSet.containsAll(newRuleToTest)){
                                support++;
                            }
                        }

                        //TODO
                        //If support exceeds threshold, save it in the final array.
                        if (support >= minRequiredSupport){
                            finalArray.set(finalArrayRuleNumberIndex, finalArray.get(finalArrayRuleNumberIndex)+1);
                            for (Integer l : newRuleToTest){
                                finalArray.add(l);
                            }
                            nextRules.add(newRuleToTest);
                        }

                        testedRules.add(newRuleToTest);
                    }
                }
            }
            k++;
            sequences.add(nextRules);
        }while (sequences.get(k-1).size() > 0);

        state.setFromKey(INTERNAL_MAX_RULE_SIZE, Type.INT, k-2);

        //Rework list to int[] array
        final int finalArrayInt[] = new int[finalArray.size()];
        for (int i=0;i<finalArray.size();i++){
            finalArrayInt[i] = finalArray.get(i);
        }
        //Save the encoded values
        state.setFromKey(INTERNAL_RULES, Type.INT_ARRAY, finalArrayInt);
    }


    /**
     * Adds new value to the buffer. Connotations change depending on whether the node is in bootstrap mode or not.
     *
     * @param state Node state to get/set properties
     * @param value New value to add; {@code null} disallowed
     * @return new value of bootstrap mode
     */
    protected boolean addValue(NodeState state, int value[]) {
        illegalArgumentIfFalse(value != null, "Value must be not null");
        int newBuffer[] = adjustValueBuffer(state, value);

        //Recalculate sequences
        updateModelParameters(state, newBuffer);

        return false; //TODO This is temporary solution. There is no bootstrap concept here for now.
    }

    /**
     * {@inheritDoc}
     */
    public void learn(final Callback<int[][][]> callback) {
        extractFeatures(new Callback<double[]>() {
            @Override
            public void on(double[] result) {
                NodeState state = unphasedState();
                int intResult[] = new int[result.length];
                for (int i=0;i<result.length;i++){
                    intResult[i] = (int)result[i];
                }
                boolean outcome = addValue(state, intResult);
                int res[][][] = inferRules(state);
                callback.on(res);
            }
        });
    }

    /**
     * Retrieves the detected set of rules. If necessary, recalculates
     *
     * @param state Node state to get properties
     * @return Set of rules: [number of elements][index of rule][elements in rule]<br>
     * [0][][] is empty
     */
    public int[][][] inferRules(NodeState state) {
        //Step 2. Check whether sequences are there. If not - re-caluclate.
        int rules[] = state.getFromKeyWithDefault(INTERNAL_RULES, new int[0]);
        if ((rules==null)||(rules.length==0)){
            int buffer[] = state.getFromKeyWithDefault(INTERNAL_VALUE_BUFFER_KEY, new int[0]);
            updateModelParameters(state, buffer);
            rules = state.getFromKeyWithDefault(INTERNAL_RULES, new int[0]);
        }

        //Step 3. With state and 1-D sequences, transform 3D array (see encoding above)
        final int maxRuleSize = state.getFromKeyWithDefault(INTERNAL_MAX_RULE_SIZE, 0);
        final int result[][][] = new int[maxRuleSize+1][][];
        int index = 0;
        result[0] = new int[0][];
        for (int i=1;i<=maxRuleSize;i++){
            final int numRules = rules[index];
            index++;
            result[i] = new int[numRules][i]; //Reserved memory for all the sequences
            //For each sequence
            for (int j=0;j<numRules;j++){
                for (int k=0;k<i;k++){
                    result[i][j][k] = rules[index];
                    index++;
                }
            }
        }

        return result;
    }

}