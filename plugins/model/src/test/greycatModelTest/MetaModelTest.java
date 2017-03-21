/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import greycat.*;
import greycat.model.MetaAttribute;
import greycat.model.MetaClass;
import greycat.model.MetaModelPlugin;
import greycat.model.actions.MetaActions;
import greycat.scheduler.NoopScheduler;
import org.junit.Test;

public class MetaModelTest {

    @Test
    public void test() {
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).withPlugin(new MetaModelPlugin()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                Tasks
                        .newTask()
                        .then(MetaActions.declareMetaClass("Sensor"))
                        .then(MetaActions.declareMetaAttribute("name", "STRING"))
                        .then(MetaActions.declareMetaAttribute("value", "DOUBLE"))
                        .then(MetaActions.declareMetaClass("Room"))
                        .then(MetaActions.declareMetaAttribute("name", "STRING"))
                        .then(MetaActions.getMetaClasses())
                        .execute(g, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {

                        for(int i=0;i<result.size();i++){
                            MetaClass meta = (MetaClass) result.get(i);
                            System.out.println(meta.get("name"));
                            MetaAttribute[] attributes = meta.attributes();
                            for(int j=0;j<attributes.length;j++){
                                System.out.println("\t"+attributes[j]);
                            }
                        }
                        result.free();
                    }
                });


                /*
                MetaClass.declare(g, 0, 0, "Sensor", new Callback<MetaClass>() {
                    @Override
                    public void on(MetaClass sensorMetaClass) {
                        sensorMetaClass.declareAttribute("name", Type.STRING);
                        MetaAttribute[] atts = sensorMetaClass.attributes();
                        System.out.println(atts[0]);
                        System.out.println(sensorMetaClass);
                    }
                });
                */
            }
        });
    }

}
