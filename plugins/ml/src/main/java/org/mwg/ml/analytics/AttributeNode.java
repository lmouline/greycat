package org.mwg.ml.analytics;

import org.mwg.Graph;
import org.mwg.ml.AbstractMLNode;

/**
 * Created by assaad on 19/10/2016.
 */
public class AttributeNode extends AbstractMLNode {
    public final static String NAME = "AttributeNode";

    public static final String MIN_TOLERATED = "min_tol";
    public static final String MAX_TOLERATED  = "max_tol";
    public static final String VALUE  = "value";
    public static final String VALID  = "valid";
    public static final String EXTRAPOLATE  = "extrapolate";

    public static final String MIN_OBSERVED = "min_obs";
    public static final String MAX_OBSERVED  = "max_obs";
    public static final String AVG = "avg";
    public static final String SIGMA  = "sigma";

    private static final String INTERNAL_SUM_KEY = "_sum";
    private static final String INTERNAL_SUMSQUARE_KEY = "_sumSquare";

    public AttributeNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }
}
