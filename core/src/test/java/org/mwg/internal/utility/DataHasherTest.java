package org.mwg.internal.utility;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.internal.CoreConstants;
import org.mwg.utility.HashHelper;

public class DataHasherTest {

    @Test
    public void dataHash_1Test() {
        long hash = HashHelper.hash(CoreConstants.END_OF_TIME + "");
        //System.out.println("dataHash_1Test: " + hash);
        //64 bits
        //Assert.assertTrue(hash == 1749261374604296L);
        Assert.assertEquals(hash,1160719163);
    }

    @Test
    public void dataHash_2Test() {
        long hash = HashHelper.hash(CoreConstants.BEGINNING_OF_TIME + "");
        //System.out.println("dataHash_2Test: " + hash);
        //64 bits
        //Assert.assertTrue(hash == -7914587012082605L);
        Assert.assertEquals(hash,1930113384);
    }

    @Test
    public void dataHash_3Test() {
        long hash = HashHelper.hash(CoreConstants.BEGINNING_OF_TIME + "");
        long hash2 = HashHelper.hash(CoreConstants.END_OF_TIME + "");
        //System.out.println("dataHash_3Test: " + hash + " -> " + hash2);
        Assert.assertTrue(hash != hash2);
    }

}
