package greycatMLTest.profiling;

import greycat.*;
import greycat.ml.MLPlugin;
import greycat.ml.profiling.GaussianENode;
import greycat.struct.EGraph;
import greycat.struct.ENode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by assaad on 21/06/2017.
 */
public class TestGmmEnode {
    @Test
    public void Test(){
        Graph graph= GraphBuilder
                .newBuilder()
                .build();

        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                Node node = graph.newNode(0,0);

                EGraph eg= (EGraph) node.getOrCreate("graph", Type.EGRAPH);
                ENode en=eg.newNode();
                ENode en2=eg.newNode();
                ENode en3=eg.newNode();

                GaussianENode gaussian= new GaussianENode(en);
                GaussianENode gaussian2= new GaussianENode(en2);
                GaussianENode gaussian3= new GaussianENode(en3);

                double[] key={1.1,2.2,3.3};
                int n=10;

                for(int i=0;i<n;i++){
                    gaussian.learn(key);
                }

                gaussian2.learnNTimes(key,n);

                gaussian3.learn(key);
                gaussian3.learnNTimes(key,n-1);

                double[] sumsq=gaussian.getSumSq();
                double[] sumsq2=gaussian2.getSumSq();
                double[] sumsq3=gaussian3.getSumSq();

                Assert.assertArrayEquals(sumsq,sumsq2,1e-7);
                Assert.assertArrayEquals(sumsq,sumsq3,1e-7);


                double[] sum=gaussian.getSum();
                double[] sum2=gaussian2.getSum();
                double[] sum3=gaussian3.getSum();

                Assert.assertArrayEquals(sum,sum2,1e-7);
                Assert.assertArrayEquals(sum,sum3,1e-7);

            }
        });

    }

}
