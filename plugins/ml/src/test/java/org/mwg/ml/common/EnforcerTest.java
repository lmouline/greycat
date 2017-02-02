/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.ml.common;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Type;
import org.mwg.utility.Enforcer;

/**
 * Created by assaad on 17/06/16.
 */
public class EnforcerTest {

    @Test
    public void enforce(){
        Enforcer enforcer=new Enforcer().asPositiveLong("plong").asPositiveInt("pint").asPositiveDouble("pdouble").asBool("bool").asIntWithin("int1-10",1,10);
        enforcer.check("plong", Type.LONG, 1);
        enforcer.check("pint", Type.INT, 1);
        enforcer.check("pdouble", Type.DOUBLE, 1);
        enforcer.check("int1-10", Type.INT, 1);

        boolean catched=false;
        try{
            enforcer.check("plong", Type.LONG, 0);
        }
        catch (Exception ex){
            catched=true;
        }
        Assert.assertTrue(catched);
        catched=false;

        try{
            enforcer.check("pint", Type.INT, 0);
        }
        catch (Exception ex){
            catched=true;
        }
        Assert.assertTrue(catched);
        catched=false;

        try{
            enforcer.check("pdouble", Type.DOUBLE, 0);
        }
        catch (Exception ex){
            catched=true;
        }
        Assert.assertTrue(catched);
        catched=false;

        try{
            enforcer.check("int1-10", Type.INT, 0);
        }
        catch (Exception ex){
            catched=true;
        }
        Assert.assertTrue(catched);

        catched=false;
        try{
            enforcer.check("int1-10", Type.INT, 100);
        }
        catch (Exception ex){
            catched=true;
        }
        Assert.assertTrue(catched);
    }
}
