package org.mwg.memory.offheap;

import java.util.HashMap;
import java.util.Map;

public class OffHeapConstants {

    public static final int OFFHEAP_NULL_PTR = -1;

    //TODO remove this before release
    public static boolean DEBUG_MODE = false;
    public static final Map<Long, Long> SEGMENTS = new HashMap<Long, Long>();

}
