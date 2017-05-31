package greycatTest.utility;

import greycat.utility.L3GMap;
import org.junit.Test;

/**
 * Created by Gregory NAIN on 31/05/2017.
 */
public class L3GMapTest {

    @Test
    public void insetTest() {
        L3GMap map = new L3GMap(true);
        map.put(0, -9007199254740990L, 6047313952769L, "");
    }


}
