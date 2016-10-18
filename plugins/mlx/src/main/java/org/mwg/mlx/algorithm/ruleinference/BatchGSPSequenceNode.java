package org.mwg.mlx.algorithm.ruleinference;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.ml.AbstractMLNode;
import org.mwg.plugin.NodeState;

import java.util.*;

/**
 * Created by andrey.boytsov on 14/10/2016.
 *
 * TODO All code here for now. To extract abstract subclasses later
 */
public class BatchGSPSequenceNode extends AbstractMLNode{

    public static final String NAME = "BatchGSPSequenceNode";

    //TODO Various detection thresholds
    /**
     * Attribute key - Required number of occurrences for the sequence to be considered.
     */
    public static final String SUPPORT_LIMIT_KEY = "requiredSupport";
    /**
     * Default value - Required number of occurrences for the sequence to be considered.
     */
    protected static final int SUPPORT_LIMIT_DEF = 3;

    //Sequence encoding in double array:
    //[0] - number of sequences
    //[1] - First sequence - its support (de-facto int).
    //[2] - First sequence - number of elements.
    //[3-n] - First sequence - elements themselves.
    //[n+1] - Second sequence - its support
    //etc.

    //TODO Enforcer

    /**
     * Attribute key - Feature that has relevant values
     */
    public static final String RELEVANT_FEATURE_KEY = "relevantFeature";

    /**
     *  Relevant feature number - default value
     */
    protected static final int RELEVANT_FEATURE_DEF = 0;

    /**
     * Attribute key - length of longest detected sequence
     */
    protected static final String INTERNAL_LONGEST_SEQUENCE_LENGTH = "_longestSequenceLength";

    /**
     * Attribute key - sliding window of values
     */
    protected static final String INTERNAL_VALUE_BUFFER_KEY = "_valueBuffer";

    /**
     * Attribute key - array of detected sequences
     */
    protected static final String INTERNAL_SEQUENCES = "_sequences";

    /**
     * Attribute key - array of detected sequences
     */
    protected static final int[] INTERNAL_SEQUENCES_DEF = new int[0];

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

    public BatchGSPSequenceNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    /**
     * Adjust buffer: adds value to the end of it, removes first value(s) if necessary.
     *
     * @param state
     * @param value
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
        for (int i=0;i<value.length;i++){

        }
        System.arraycopy(value, 0, newBuffer, newBuffer.length - dimensions, dimensions);

        state.setFromKey(INTERNAL_VALUE_BUFFER_KEY, Type.INT_ARRAY, newBuffer);
        return newBuffer;
    }

    protected void updateModelParameters(NodeState state, int newBuffer[]){
        final int minRequiredSupport = state.getFromKeyWithDefault(SUPPORT_LIMIT_KEY, SUPPORT_LIMIT_DEF);

        //Step 1. Extract relevant column out of buffer.
        final int dimensions = state.getFromKeyWithDefault(INPUT_DIM_KEY, INPUT_DIM_DEF);
        //Should be more than the number of relevant feature
        //TODO Enforce it?
        final int elementsInColumn = newBuffer.length/dimensions;
        final int featureNum = state.getFromKeyWithDefault(RELEVANT_FEATURE_KEY, RELEVANT_FEATURE_DEF);
        final int relevantColumn[] = new int[elementsInColumn];
        System.arraycopy(newBuffer, elementsInColumn*featureNum, relevantColumn, 0, elementsInColumn);

        //Step 2. Transfer to int.

        Set<Integer> alphabetSet = new HashSet<>();
        for (int i = 0; i<relevantColumn.length ; i++){
            alphabetSet.add(new Integer(relevantColumn[i]));
        }

        //Step 3. Apply GSP algorithm.
        /* Python code:
        f = list([[]]) #0-sequences do not exist. Also it makes making list of lists easier. For prototype - OK.
        f.append([[i] for i in set(sequence)]) # Distinct 1-sequences
        alphabet = [i for i in set(sequence)] # Kind of "alphabet" to detect possible sequences
        k = 2
        while (len(f[k-1]) > 0):
            sequence_list = []
            # Step 1. Get possible subsequences, combined from existing n-1 sequence and a symbol in the alphabet
            for subseq in f[k-1]:
                for letter in alphabet:
                    #Generate the sequence
                    new_subsequence = list(subseq)
                    new_subsequence.append(letter)
                    #Check support. If support is less than the threshold - do not count.
                    support = 0
                    for i in range(len(sequence)-len(new_subsequence)+1):
                        test_subseq = sequence[i:i+len(new_subsequence)]
                        if (test_subseq == new_subsequence):
                            support += 1
                    if (support >= support_threshold):
                        sequence_list.append(new_subsequence)
            k+=1
            f.append(sequence_list)
        return f
         */

        //Sequence encoding in int array:
        //[0] - number of 1-sequences (e.g. n)
        //[1..n] - 1-sequences
        //[n+1] - Number of 2-sequences (e.g. k)
        //[n+2..n+2+2*k] - 2-sequences
        //etc.

        List<Integer> finalArray = new ArrayList<>();

        final List<List<int[]>> sequences = new ArrayList<>();
        sequences.add(new ArrayList<>()); //0-sequences do not exist

        int k=1;
        do{
            final List<int[]> nextSequences = new ArrayList<>();

            //Number of k-sequences
            int finalArraySeqNumberIndex = finalArray.size();
            finalArray.add(0); //Start with 0, later increase

            List<int[]> lastSubsequences = sequences.get(k-1);
            if (lastSubsequences.isEmpty()){
                lastSubsequences.add(new int[0]);
            }

            for (final int[] subseq : lastSubsequences){
                for (final int letter : alphabetSet){
                    final int newSubsequence[] = new int[subseq.length+1];
                    System.arraycopy(subseq, 0, newSubsequence, 0, subseq.length);
                    newSubsequence[subseq.length] = letter;

                    //Checking support
                    int support = 0;

                    for (int i=0;i<=relevantColumn.length-newSubsequence.length;i++) {
                        int testSubseq[] = new int[newSubsequence.length];
                        System.arraycopy(relevantColumn, i, testSubseq, 0, newSubsequence.length);
                        if (Arrays.equals(testSubseq, newSubsequence)){
                            support++;
                        }
                    }

                    //If support exceeds threshold, save it in the final array.
                    if (support >= minRequiredSupport){
                        finalArray.set(finalArraySeqNumberIndex, finalArray.get(finalArraySeqNumberIndex)+1);
                        for (int i=0;i<newSubsequence.length;i++){
                            finalArray.add(newSubsequence[i]);
                        }
                        nextSequences.add(newSubsequence);
                    }
                }
            }
            k++;
            sequences.add(nextSequences);
        }while (sequences.get(k-1).size() > 0);

        state.setFromKey(INTERNAL_LONGEST_SEQUENCE_LENGTH, Type.INT, k-2);

        //Rework list to int[] array
        final int finalArrayInt[] = new int[finalArray.size()];
        for (int i=0;i<finalArray.size();i++){
            finalArrayInt[i] = finalArray.get(i);
        }
        //Save the encoded values
        state.setFromKey(INTERNAL_SEQUENCES, Type.INT_ARRAY, finalArrayInt);
    }

    /**
     *
     * @param value
     * @return New bootstrap mode value (TODO stub, always false now)
     */
    protected boolean addValue(NodeState state, int[] value) {
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

    public int[][][] inferRules(NodeState state) {
        //Step 2. Check whether sequences are there. If not - re-caluclate.
        int seq[] = state.getFromKeyWithDefault(INTERNAL_SEQUENCES, INTERNAL_SEQUENCES_DEF);
        if ((seq==null)||(seq.length==0)){
            int buffer[] = state.getFromKeyWithDefault(INTERNAL_VALUE_BUFFER_KEY, new int[0]);
            updateModelParameters(state, buffer);
            seq = state.getFromKeyWithDefault(INTERNAL_SEQUENCES, INTERNAL_SEQUENCES_DEF);
        }

        //Step 3. With state and 1-D sequences, transform 3D array (see encoding above)
        final int longestSequenceLength = state.getFromKeyWithDefault(INTERNAL_LONGEST_SEQUENCE_LENGTH, 0);
        final int result[][][] = new int[longestSequenceLength+1][][];
        int index = 0;
        result[0] = new int[0][];
        for (int i=1;i<=longestSequenceLength;i++){
            final int numSequences = seq[index];
            index++;
            result[i] = new int[numSequences][i]; //Reserved memory for all the sequences
            //For each sequence
            for (int j=0;j<numSequences;j++){
                for (int k=0;k<i;k++){
                    result[i][j][k] = seq[index];
                    index++;
                }
            }
        }

        return result;
    }

    public void inferRules(Callback<int[][][]> callback) {
        //Step 1. Get the state.
        NodeState state = unphasedState();
        int result[][][] = inferRules(state);
        //Last step: Call the actual callback with parameters.
        callback.on(result);
    }

}
