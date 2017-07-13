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

import com.squareup.javapoet.*;
import greycat.*;
import greycat.language.*;
import greycat.language.Class;
import greycat.language.Index;
import greycat.plugin.Job;
import greycat.plugin.NodeFactory;
import greycat.plugin.Plugin;
import greycat.plugin.TypeFactory;
import greycat.struct.EStructArray;

import javax.lang.model.element.Modifier;
import java.util.List;

class PluginGenerator {

    static void generate(final String packageName, final String pluginName, final Model model, final List<JavaFile> collector) {
        TypeSpec.Builder javaClass = TypeSpec.classBuilder(pluginName);
        javaClass.addModifiers(Modifier.PUBLIC);
        javaClass.addSuperinterface(ClassName.get(Plugin.class));

        javaClass.addMethod(MethodSpec.methodBuilder("stop")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Override.class)
                .build());

        MethodSpec.Builder startMethod = MethodSpec.methodBuilder("start")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addParameter(ClassName.get(Graph.class), "graph")
                .addAnnotation(Override.class);
        //Register NodeTypes
        for (Class aClass : model.classes()) {
            startMethod.addStatement("graph.nodeRegistry().getOrCreateDeclaration($L.META.name).setFactory($L)", aClass.name(), TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ClassName.get(NodeFactory.class))
                    .addMethod(MethodSpec.methodBuilder("create")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(TypeName.LONG, "world")
                            .addParameter(TypeName.LONG, "time")
                            .addParameter(TypeName.LONG, "id")
                            .addParameter(ClassName.get(Graph.class), "graph")
                            .returns(ClassName.get(Node.class))
                            .addStatement("return new $T(world,time,id,graph)", ClassName.get(packageName, aClass.name()))
                            .build())
                    .build());
        }
        //Register CustomTypes
        for (CustomType aType : model.customTypes()) {
            startMethod.addStatement("graph.typeRegistry().getOrCreateDeclaration($L.META.name).setFactory($L)", aType.name(), TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ClassName.get(TypeFactory.class))
                    .addMethod(MethodSpec.methodBuilder("wrap")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ClassName.get(EStructArray.class), "backend")
                            .returns(TypeName.OBJECT)
                            .addStatement("return new $T(backend)", ClassName.get(packageName, aType.name()))
                            .build())
                    .build());
        }
        //Declare indexes
        if (model.globalIndexes().length > 0) {
            MethodSpec.Builder onMethod = MethodSpec.methodBuilder("on")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterizedTypeName.get(ClassName.get(Callback.class), TypeName.BOOLEAN.box()), "endIndexes")
                    .returns(TypeName.VOID)
                    .addStatement("$T waiter = graph.newCounter($L)", ClassName.get(DeferCounter.class), model.globalIndexes().length);
            for (Index index : model.globalIndexes()) {
                onMethod.addStatement("graph.declareIndex(0,$L.META.name,$L)", index.name(), TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Callback.class), ClassName.get(greycat.NodeIndex.class)))
                        .addMethod(MethodSpec.methodBuilder("on")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(ClassName.get(greycat.NodeIndex.class), "idx")
                                .addStatement("idx.free()")
                                .addStatement("waiter.count()")
                                .returns(TypeName.VOID).build())
                        .build());
            }
            onMethod.addStatement("waiter.then($L)", TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ClassName.get(Job.class))
                    .addMethod(MethodSpec.methodBuilder("run")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("endIndexes.on(true)")
                            .returns(TypeName.VOID).build())
                    .build());
            startMethod.addStatement("graph.addConnectHook($L)", TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Callback.class), ParameterizedTypeName.get(ClassName.get(Callback.class), TypeName.BOOLEAN.box())))
                    .addMethod(onMethod.build())
                    .build());
        }
        javaClass.addMethod(startMethod.build());

        collector.add(JavaFile.builder(packageName, javaClass.build()).build());
    }

}
