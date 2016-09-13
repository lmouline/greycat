package org.mwg.ml.common.structure;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.structure.distance.Distance;
import org.mwg.structure.distance.EuclideanDistance;
import org.mwg.structure.tree.NDTree;


public class TestNDSubs {

    private static long[] powers = {8, 4, 2, 1};

    private static long convert(double[] center, double[] key) {
        long res = 0;
        for (int i = 0; i < center.length; i++) {
            if (key[i] > center[i]) {
                res += powers[i];
            }
        }
        //System.out.println(res+NDTree._RELCONST);
        return res + NDTree._RELCONST;
    }

    @Test
    public void testConv1() {
        double[] center = {2, 2, 2, 2};
        double[] min={0,0,0,0};
        double[] max={4,4,4,4};
        double[] precision={0.5,0.5,0.5,0.5};
        Distance distance= EuclideanDistance.instance();

        double[] test1 = {2, 2, 2, 2};
        Assert.assertTrue(NDTree.getRelationId(center, test1) == NDTree._RELCONST);

        double[] test2 = {1, 1, 1, 1};

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    for (int l = 0; l < 2; l++) {
                        test2[0] = i + 2;
                        test2[1] = j + 2;
                        test2[2] = k + 2;
                        test2[3] = l + 2;
                        Assert.assertTrue(NDTree.getRelationId(center, test2) == convert(center, test2));
                    }
                }
            }
        }


        for (int ii = 0; ii < 16; ii++) {
            boolean[] res = NDTree.binaryFromLong(ii + NDTree._RELCONST, 4);
            //System.out.println(res[0]+" "+res[1]+" "+res[2]+" "+res[3]);

            double[] tempr = NDTree.convertToDistance(ii + NDTree._RELCONST, null, center, min, max, precision, distance);
            System.out.println(tempr[0]+" "+tempr[1]+" "+tempr[2]+" "+tempr[3]);

            int count = 0;

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 2; k++) {
                        for (int l = 0; l < 2; l++) {
                            if (count == ii) {
                                Assert.assertTrue(res[0] == (i != 0));
                                Assert.assertTrue(res[1] == (j != 0));
                                Assert.assertTrue(res[2] == (k != 0));
                                Assert.assertTrue(res[3] == (l != 0));
                            }
                            count++;
                        }
                    }
                }
            }
        }


    }
}
