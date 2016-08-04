package org.mwg.core;

public class CoreConstants extends org.mwg.Constants {


    /**
     * ChunkFlags
     */
    //public static final short DIRTY_BIT_INDEX = 0;


    //public static final short REMOVED_BIT_INDEX = 1;
    //public static final int REMOVED_BIT = 1 << REMOVED_BIT_INDEX;

    /**
     * Keys constants
     */
    //public static final int KEYS_SIZE = 3;

    public static final int PREFIX_TO_SAVE_SIZE = 2;

    public static final long[] NULL_KEY = new long[]{END_OF_TIME, END_OF_TIME, END_OF_TIME};

    public static final long[] GLOBAL_UNIVERSE_KEY = new long[]{NULL_LONG, NULL_LONG, NULL_LONG};

    public static final long[] GLOBAL_DICTIONARY_KEY = new long[]{NULL_LONG, 0, 0};

    public static final long[] GLOBAL_INDEX_KEY = new long[]{NULL_LONG, 1, 0};

    public static final String INDEX_ATTRIBUTE = "index";

    /**
     * Map constants
     */

    /**
     * Error messages
     */
    public static final String DISCONNECTED_ERROR = "Please connect your graph, prior to any usage of it";

    /**
     * SuperTimeTree
     */
    public static final long SCALE_1 = 1000;
    public static final long SCALE_2 = 10000;
    public static final long SCALE_3 = 100000;
    public static final long SCALE_4 = 1000000;


    /**
     * Error messages
     */
    public static String DEAD_NODE_ERROR = "This Node has been tagged destroyed, please don't use it anymore!";


}
