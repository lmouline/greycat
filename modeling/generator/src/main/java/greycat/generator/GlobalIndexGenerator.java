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
package greycat.generator;

import greycat.language.Attribute;
import greycat.language.GlobalIndex;
import greycat.language.Model;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;

public class GlobalIndexGenerator {

    static JavaSource[] generate(String packageName, Model model) {
        JavaSource[] sources = new JavaSource[model.globalIndexes().length];

        for (int i = 0; i < model.globalIndexes().length; i++) {
            sources[i] = generateGlobalIndexes(packageName, model.globalIndexes()[i]);
        }

        return sources;
    }

    private static JavaSource generateGlobalIndexes(String packageName, GlobalIndex globalIndex) {
        final JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
        javaClass.setPackage(packageName);
        javaClass.setName(globalIndex.name());

        String indexName = globalIndex.name();

        // index name constant
        javaClass.addField()
                .setVisibility(Visibility.PUBLIC)
                .setFinal(true)
                .setName(indexName.toUpperCase())
                .setType(String.class)
                .setStringInitializer(indexName)
                .setStatic(true);

        // attribute constants
        for (Attribute att : globalIndex.attributes()) {
            javaClass.addField()
                    .setVisibility(Visibility.PUBLIC)
                    .setFinal(true)
                    .setName(att.name().toUpperCase())
                    .setType(String.class)
                    .setStringInitializer(att.name())
                    .setStatic(true);
        }

        StringBuilder indexedAttributes = new StringBuilder();
        for (Attribute att : globalIndex.attributes()) {
            indexedAttributes.append(att.name().toUpperCase());
            indexedAttributes.append(",");
        }
        indexedAttributes.deleteCharAt(indexedAttributes.length() - 1);

        // declare index
        if (globalIndex.isWithTime()) {
            MethodSource<JavaClassSource> declareIndex = javaClass.addMethod()
                    .setName("declareTimedIndex")
                    .setReturnTypeVoid()
                    .setFinal(true)
                    .setVisibility(Visibility.PUBLIC)
                    .setStatic(true);
            declareIndex.addParameter("greycat.Graph", "graph");
            declareIndex.addParameter("long", "world");
            declareIndex.addParameter("long", "time");
            declareIndex.addParameter("greycat.Callback<Boolean>", "callback");

            StringBuilder declareIndexBody = new StringBuilder();
            declareIndexBody.append("graph.declareTimedIndex(world, time, " + indexName.toUpperCase() + ", new greycat.Callback<greycat.NodeIndex>() {");
            declareIndexBody.append("@Override\n");
            declareIndexBody.append("public void on(greycat.NodeIndex result) {");
            declareIndexBody.append("if (callback != null) {");
            declareIndexBody.append("callback.on(true);");
            declareIndexBody.append("}");
            declareIndexBody.append("}");
            declareIndexBody.append("}, " + indexedAttributes.toString() + ");");

            declareIndex.setBody(declareIndexBody.toString());
        } else {
            MethodSource<JavaClassSource> declareIndex = javaClass.addMethod()
                    .setName("declareIndex")
                    .setReturnTypeVoid()
                    .setFinal(true)
                    .setVisibility(Visibility.PUBLIC)
                    .setStatic(true);
            declareIndex.addParameter("greycat.Graph", "graph");
            declareIndex.addParameter("long", "world");
            declareIndex.addParameter("greycat.Callback<Boolean>", "callback");

            StringBuilder declareIndexBody = new StringBuilder();
            declareIndexBody.append("graph.declareIndex(world, " + indexName.toUpperCase() + ", new greycat.Callback<greycat.NodeIndex>() {");
            declareIndexBody.append("@Override\n");
            declareIndexBody.append("public void on(greycat.NodeIndex result) {");
            declareIndexBody.append("if (callback != null) {");
            declareIndexBody.append("callback.on(true);");
            declareIndexBody.append("}");
            declareIndexBody.append("}");
            declareIndexBody.append("}, " + indexedAttributes.toString() + ");");

            declareIndex.setBody(declareIndexBody.toString());
        }


        return javaClass;
    }

}
