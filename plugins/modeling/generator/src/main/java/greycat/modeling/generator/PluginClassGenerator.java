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

import greycat.Graph;
import greycat.modeling.language.ast.Classifier;
import greycat.modeling.language.ast.Class;
import greycat.modeling.language.ast.Index;
import greycat.modeling.language.ast.Model;
import greycat.plugin.NodeFactory;
import greycat.plugin.Plugin;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;

class PluginClassGenerator {

    static JavaSource generate(final String packageName, final String pluginName, final Model model) {
        final JavaClassSource pluginClass = Roaster.create(JavaClassSource.class);
        pluginClass.addImport(NodeFactory.class);
        pluginClass.addImport(Graph.class);
        pluginClass.setPackage(packageName);
        pluginClass.setName(pluginName);

        pluginClass.addInterface(Plugin.class);


        for (int i = 0; i < model.classifiers().length; i++) {
            if (model.classifiers()[i] instanceof Index) {
                Index casted = (Index) model.classifiers()[i];
                pluginClass.addField()
                        .setVisibility(Visibility.PUBLIC)
                        .setStatic(true)
                        .setFinal(true)
                        .setName("IDX_" + casted.name().toUpperCase())
                        .setType(String.class)
                        .setStringInitializer(casted.name());


                StringBuilder initArrayAtt = new StringBuilder();
                initArrayAtt.append("new String[]{");
                for (int j = 0; j < casted.properties().length; j++) {
                    initArrayAtt.append(casted.type().name() + "." + casted.properties()[j].name().toUpperCase());

                    if (j < casted.properties().length - 1) {
                        initArrayAtt.append(",");
                    }
                }
                initArrayAtt.append("};");
                pluginClass.addField()
                        .setVisibility(Visibility.PUBLIC)
                        .setStatic(true)
                        .setFinal(true)
                        .setName("IDX_" + casted.name().toUpperCase() + "_ATT")
                        .setType(String[].class)
                        .setLiteralInitializer(initArrayAtt.toString());
            }
        }

        pluginClass.addMethod().setReturnTypeVoid()
                .setVisibility(Visibility.PUBLIC)
                .setName("stop")
                .setBody("")
                .addAnnotation(Override.class);

        StringBuilder startBodyBuilder = new StringBuilder();
        for (Classifier classifier : model.classifiers()) {
            if (classifier instanceof Class) {
                startBodyBuilder.append("\t\tgraph.nodeRegistry()\n")
                        .append("\t\t\t.getOrCreateDeclaration(").append(classifier.name()).append(".NODE_NAME").append(")").append("\n")
                        .append("\t\t\t.setFactory(new NodeFactory() {\n" +
                                "\t\t\t\t\t@Override\n" +
                                "\t\t\t\t\tpublic greycat.Node create(long world, long time, long id, Graph graph) {\n" +
                                "\t\t\t\t\t\treturn new ").append(classifier.name()).append("(world,time,id,graph);\n" +
                        "\t\t\t\t\t}\n" +
                        "\t\t\t\t});\n");

            }
        }

        MethodSource<JavaClassSource> startMethod = pluginClass.addMethod();
        startMethod.setReturnTypeVoid()
                .setVisibility(Visibility.PUBLIC)
                .addAnnotation(Override.class);
        startMethod.setBody(startBodyBuilder.toString());
        startMethod.setName("start");
        startMethod.addParameter("Graph", "graph");

        return pluginClass;
    }

}
