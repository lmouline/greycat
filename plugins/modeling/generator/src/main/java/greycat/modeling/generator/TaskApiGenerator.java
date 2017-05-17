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
package greycat.modeling.generator;

import greycat.Node;
import greycat.TaskContext;
import greycat.TaskFunctionSelect;
import greycat.modeling.language.Class;
import greycat.modeling.language.Classifier;
import greycat.modeling.language.TypedGraph;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;

class TaskApiGenerator {

    static JavaSource[] generate(final String packageName, TypedGraph graph) {
        final JavaSource[] result = new JavaSource[1 + graph.classifiers().length];
        int index = 0;


        for(Classifier classifier: graph.classifiers()) {
            if(classifier instanceof Class) {
                result[index] = generateSelectFunctions(packageName,classifier);
                index++;
            }
        }

        return result;
    }

    static JavaSource generateSelectFunctions(String packageName, Classifier classifier) {
        // Create TaskFunctionSelect
        final JavaInterfaceSource functionSelect = Roaster.create(JavaInterfaceSource.class);
        functionSelect.setPackage(packageName + ".task");
        functionSelect.setName("TaskFunctionSelect" + classifier.name());
        functionSelect.addAnnotation(FunctionalInterface.class);
        functionSelect.addInterface(TaskFunctionSelect.class);
        functionSelect.addImport(Node.class);
        functionSelect.addImport(TaskContext.class);

        MethodSource first = functionSelect.addMethod()
                .setName("select")
                .setReturnType(boolean.class)
                .setBody("return select((" + packageName + "." + classifier.name() + ")node,ctx);")
                .setDefault(true);
        first.setParameters("Node node, TaskContext ctx");
        first.addAnnotation(Override.class);

        MethodSource second = functionSelect.addMethod()
                .setName("select")
                .setReturnType(boolean.class);
        second.setParameters(packageName + "." + classifier.name() + " " + classifier.name().toLowerCase() + ", TaskContext ctx");


        return functionSelect;
    }
}
