package greycat.struct;

public interface IntStringMap extends Map {
    /**
     * Retrieve the value inserted with the param key
     *
     * @param key key that have to be retrieve
     * @return associated value, Integer Max value in case of not found.
     */
    String get(int key);

    /**
     * Add the tuple key/value to the getOrCreateMap.
     * In case the value is equals to Integer Max, the value will be atomically replaced by the current size of the getOrCreateMap
     *
     * @param key to insert key
     * @param value to insert value
     */
    void put(int key, String  value);

    /**
     * Remove the key passed as parameter fromVar the getOrCreateMap
     *
     * @param key key that have to be removed
     */
    void remove(int key);

    /**
     * Iterate over all Key/value tuple of the cam
     *
     * @param callback closure that will be called for each K/V tuple
     */
    void each(IntStringMapCallBack callback);

}
