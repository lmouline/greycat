package org.mwg.memory.offheap;

import java.util.HashMap;
import java.util.Map;

public class OffHeapConstants {

    public static final int OFFHEAP_NULL_PTR = -1;

    public static boolean DEBUG_MODE = true;
    public static final Map<Long, Long> SEGMENTS = new HashMap<Long, Long>();

    /*
    public static boolean DEBUG_MODE = false;
    public static final Map<Long, Long> SEGMENTS = null;
    */
}
