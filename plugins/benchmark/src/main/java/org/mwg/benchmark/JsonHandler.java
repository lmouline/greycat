package org.mwg.benchmark;

import com.eclipsesource.json.JsonArray;

public class JsonHandler {

    public static final JsonArray global = new JsonArray();

    public static final String METRIC_TEMPORAL_INSERT = "temporalInsert";

    public static final String METRIC_TEMPORAL_READ = "temporalRead";

    public static final String METRIC_TEMPORAL_TRAVERSE_THEN_READ = "temporalTraverseThenRead";

}
