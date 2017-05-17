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

import greycat.Callback;
import greycat.Graph;
import greycat.Type;
import greycat.modeling.language.*;
import greycat.modeling.language.Class;
import greycat.modeling.language.Enum;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.*;

class NodeTypeGenerator {

    static JavaSource[] generate(String packageName, String name, TypedGraph graph) {
        JavaSource[] sources = new JavaSource[graph.classifiers().length];

        for(int i=0;i<graph.classifiers().length;i++) {
            final Classifier classifier = graph.classifiers()[i];

            if(classifier instanceof Enum) {
                sources[i] = generateEnum(packageName, (Enum) classifier);
            } else if(classifier instanceof Class) {
                sources[i] = generateClass(packageName,name, (Class)classifier);
            } else if(classifier instanceof Index) {
                // Ignore it
            } else {
                //todo
            }
        }

        return sources;
    }



    private static JavaSource generateEnum(String packageName, Enum enumClassifier) {
        final JavaEnumSource javaEnum = Roaster.create(JavaEnumSource.class);
        javaEnum.setPackage(packageName);


        javaEnum.setName(enumClassifier.name());
        for (String literal : enumClassifier.literals()) {
            javaEnum.addEnumConstant(literal);
        }
        return javaEnum;
    }

//    private static JavaSource generateWorld(String packageName, String name, World worldClassifier) {
//        final JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
//
//        javaClass.setPackage(packageName);
//        javaClass.setName("Worlds");
//        javaClass.setSuperType("greycat.base.BaseNode");
//
//        MethodSource<JavaClassSource> constructor = javaClass.addMethod().setConstructor(true);
//        constructor.addParameter("long", "p_world");
//        constructor.addParameter("long", "p_time");
//        constructor.addParameter("long", "p_id");
//        constructor.addParameter(Graph.class, "p_graph");
//        constructor.setBody("super(p_world, p_time, p_id, p_graph);");
//        constructor.setVisibility(Visibility.PUBLIC);
//
//        javaClass.addField()
//                .setVisibility(Visibility.PUBLIC)
//                .setFinal(true)
//                .setName("NODE_NAME")
//                .setType(String.class)
//                .setStringInitializer(javaClass.getCanonicalName())
//                .setStatic(true);
//
//        for (int i = 0; i < worldClassifier.worldNames().length; i++) {
//            String worldName = worldClassifier.worldNames()[i];
//            javaClass.addField()
//                    .setVisibility(Visibility.PUBLIC)
//                    .setFinal(true)
//                    .setName(worldName.toUpperCase())
//                    .setType(String.class)
//                    .setStringInitializer(worldClassifier.worldNames()[i])
//                    .setStatic(true);
//
//
//            javaClass.addField()
//                    .setVisibility(Visibility.PUBLIC)
//                    .setFinal(true)
//                    .setName(worldName.toUpperCase() + "_TYPE")
//                    .setType(byte.class)
//                    .setLiteralInitializer("greycat.Type.STRING")
//                    .setStatic(true);
//
//            MethodSource<JavaClassSource> getter = javaClass.addMethod();
//            getter.setVisibility(Visibility.PUBLIC).setFinal(true);
//            getter.setReturnType(String.class);
//            getter.setName("get" + upperCaseFirstChar(worldName));
//            getter.setBody("return (String) super.get(" + worldName.toUpperCase() + ");");
//        }
//
//
//
//        return javaClass;
//
//
//    }

    private static JavaSource generateClass(String packageName, String name, Class classClassifier) {
        final JavaClassSource javaClass = Roaster.create(JavaClassSource.class);

        javaClass.setPackage(packageName);
        javaClass.setName(classClassifier.name());

        if (classClassifier.parent() != null) {
            javaClass.setSuperType(classClassifier.parent().name());
        } else {
            javaClass.setSuperType("greycat.base.BaseNode");
        }


        MethodSource<JavaClassSource> constructor = javaClass.addMethod().setConstructor(true);
        constructor.addParameter("long", "p_world");
        constructor.addParameter("long", "p_time");
        constructor.addParameter("long", "p_id");
        constructor.addParameter(Graph.class, "p_graph");
        constructor.setBody("super(p_world, p_time, p_id, p_graph);");
        constructor.setVisibility(Visibility.PUBLIC);

        //add helper name
        javaClass.addField()
                .setVisibility(Visibility.PUBLIC)
                .setFinal(true)
                .setName("NODE_NAME")
                .setType(String.class)
                .setStringInitializer(javaClass.getCanonicalName())
                .setStatic(true);

        StringBuilder indexedProperties=null;
        String indexName = null;
        for (Property prop : classClassifier.properties()) {

            //add helper name
            javaClass.addField()
                    .setVisibility(Visibility.PUBLIC)
                    .setFinal(true)
                    .setName(prop.name().toUpperCase())
                    .setType(String.class)
                    .setStringInitializer(prop.name())
                    .setStatic(true);

            if (prop instanceof Attribute) {
                Attribute casted = (Attribute) prop;
                javaClass.addImport(Type.class);
                FieldSource<JavaClassSource> typeHelper = javaClass.addField()
                        .setVisibility(Visibility.PUBLIC)
                        .setFinal(true)
                        .setName(casted.name().toUpperCase() + "_TYPE")
                        .setType(byte.class)
                        .setStatic(true);
                typeHelper.setLiteralInitializer(TypeHelper.stringType((Attribute)prop));

                //generate getter
                MethodSource<JavaClassSource> getter = javaClass.addMethod();
                getter.setVisibility(Visibility.PUBLIC).setFinal(true);
                getter.setReturnType(TypeHelper.typeToClassName((Attribute) prop));
                getter.setName("get" + upperCaseFirstChar(prop.name()));
                if(casted.isArray()) {
                    getter.setBody("return (" + TypeHelper.typeToClassName(casted) + ") super.getOrCreate(" + casted.name().toUpperCase() + ", " + casted.name().toUpperCase() + "_TYPE);");
                } else {
                    getter.setBody("return (" + TypeHelper.typeToClassName(casted) + ") super.get(" + casted.name().toUpperCase() + ");");
                }


                //generate setter
                javaClass.addMethod()
                        .setVisibility(Visibility.PUBLIC).setFinal(true)
                        .setName("set" + upperCaseFirstChar(prop.name()))
                        .setReturnType(classClassifier.name())
                        .setBody("super.set(" + prop.name().toUpperCase() + ", " + prop.name().toUpperCase()
                                + "_TYPE,value);\nreturn this;"
                        )
                        .addParameter(TypeHelper.typeToClassName(casted), "value");

                if(casted.indexes().length > 0) {
                    if(indexedProperties == null) {
                        indexedProperties = new StringBuilder();
                        indexName = casted.indexes()[0].name().toUpperCase();
                    } else {
                        indexedProperties.append(",");
                    }

                    indexedProperties.append(prop.name().toUpperCase());

                }

            } else if (prop instanceof Relation) {
                Relation casted = (Relation) prop;
                //generate getter
                String resultType = TypeHelper.formatClassType(casted);
                MethodSource<JavaClassSource> getter = javaClass.addMethod();
                getter.setVisibility(Visibility.PUBLIC);
                getter.setFinal(true);
                getter.setReturnTypeVoid();
                getter.setName("get" + upperCaseFirstChar(casted.name()));
                getter.addParameter("greycat.Callback<" + resultType + "[]>","callback");
                getter.setBody(
                        "this.relation(" + prop.name().toUpperCase() + ",new greycat.Callback<greycat.Node[]>() {\n" +
                                "@Override\n" +
                                "public void on(greycat.Node[] nodes) {\n" +
                                resultType + "[] result = new " + resultType + "[nodes.length];\n" +
                                "for(int i=0;i<result.length;i++) {\n" +
                                "result[i] = (" + resultType + ") nodes[i];\n" +
                                "}\n" +
                                "callback.on(result);" +
                                "}\n" +
                                "});"
                );



                //generate setter
                StringBuilder bodyBuilder = new StringBuilder();
                MethodSource<JavaClassSource> add = javaClass.addMethod();
                add.setVisibility(Visibility.PUBLIC).setFinal(true);
                add.setName("addTo" + upperCaseFirstChar(prop.name()));
                add.setReturnType(classClassifier.name());
                add.addParameter(TypeHelper.formatClassType(casted), "value");
                bodyBuilder.append("super.addToRelation(").append(prop.name().toUpperCase()).append(",(greycat.Node)value);");
                bodyBuilder.append("return this;");
                add.setBody(bodyBuilder.toString());

                bodyBuilder = new StringBuilder();
                //generate setter
                MethodSource<JavaClassSource> remove = javaClass.addMethod();
                remove.setVisibility(Visibility.PUBLIC).setFinal(true);
                remove.setName("removeFrom" + upperCaseFirstChar(prop.name()));
                remove.setReturnType(classClassifier.name());
                remove.addParameter(TypeHelper.formatClassType(casted), "value");
                bodyBuilder.append("super.removeFromRelation(").append(prop.name().toUpperCase()).append(",(greycat.Node)value);");
                bodyBuilder.append("return this;");
                remove.setBody(bodyBuilder.toString());



            } else {
                //todo
            }
        }

        if(indexedProperties != null) {
            javaClass.addMethod()
                    .setName("index" + classClassifier.name())
                    .setVisibility(Visibility.PUBLIC)
                    .setFinal(true)
                    .setReturnTypeVoid()
                    .setBody("\t\tfinal " + classClassifier.name() +" self = this;\n" +
                            "\t\tthis.graph().index(world(), time(), " + name + ".IDX_" + indexName + ", new greycat.Callback<greycat.NodeIndex>() {\n" +
                            "\t\t\t@Override\n" +
                            "\t\t\tpublic void on(greycat.NodeIndex indexNode) {\n" +
                            "\t\t\t\tindexNode.removeFromIndex(self, " + indexedProperties +" );\n" +
                            "\t\t\t\tindexNode.addToIndex(self," + indexedProperties +");\n" +
                            "\t\t\t\tcallback.on(true);\n" +
                            "\t\t\t}\n" +
                            "\t\t});")
                    .addParameter("Callback<Boolean>","callback");
            javaClass.addImport(Callback.class);

        }

        return javaClass;
    }

    static String upperCaseFirstChar(String init) {
        return init.substring(0,1).toUpperCase() + init.substring(1);
    }



}
