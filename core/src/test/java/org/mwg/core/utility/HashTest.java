package org.mwg.core.utility;

import org.junit.Test;
import org.mwg.utility.HashHelper;

import java.util.Random;

/**
 * Created by assaad on 15/09/16.
 */
public class HashTest {

    @Test
    public void hashing(){
        int testTimes=100000;

        int dim=100;
        int[] values=new int[dim];
        int[] rndval=new int[dim];

        Random random=new Random();

        int temp=0;
        int tempr=0;
        for(int i=0;i<testTimes;i++){
            temp= (int) HashHelper.tripleHash((byte) 1, random.nextInt(), random.nextInt(), random.nextInt(), dim);
            tempr=random.nextInt(dim);
            rndval[tempr]++;
            values[temp]++;
        }

        int avg=testTimes/dim;
        int max=0;
        int t=0;

        int maxr=0;
        int tr=0;

        for(int i=0;i<dim;i++){
            t=Math.abs(values[i]-avg);
            if(t>max){
                max=t;
            }

            tr=Math.abs(rndval[i]-avg);
            if(tr>maxr){
                maxr=tr;
            }
        }
        System.out.println("max hash deviation: "+max+", random deviation: "+maxr);
    }
}

