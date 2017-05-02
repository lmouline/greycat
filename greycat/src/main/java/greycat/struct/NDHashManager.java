package greycat.struct;

/**
 * Created by assaad on 02/05/2017.
 */
public interface NDHashManager {

    /**
     * Create a new element in the hashtable
     **/
    long createNewElement();


    /**
     * Update the element in the hashtable
     *
     * @param id                the id of the resolved element from the hashtable
     * @param key               the multi dimensional key being inserted
     * @param value             the object being indexed/profiled for this key
     */
    void update(long id, double[] key, long value);


    /**
     * Resolve the current object of the hashed id 
     * @param id
     * @return
     */
    Object get(long id);


}
