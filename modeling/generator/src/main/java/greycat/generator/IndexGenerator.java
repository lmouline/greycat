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
import greycat.language.Index;
import greycat.language.Model;
import greycat.utility.HashHelper;
import greycat.utility.Meta;

import javax.lang.model.element.Modifier;
import java.util.List;

import static greycat.generator.Helper.*;

class IndexGenerator {

    static void generate(String packageName, Model model, List<JavaFile> collector) {
        for (int i = 0; i < model.globalIndexes().length; i++) {
            generate(packageName, model.globalIndexes()[i], collector);
        }
    }

    static void generate(final String packageName, final Index index, final List<JavaFile> collector) {
        TypeSpec.Builder javaClass = TypeSpec.classBuilder(index.name());
        javaClass.addModifiers(Modifier.PUBLIC);

        //Meta field
        javaClass.addField(FieldSpec.builder(gMeta, "META")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T($S,$L,$L)", gMeta, index.name(), Helper.typeName("Index"), HashHelper.hash(index.name()))
                .build());

        //FindAll method
        javaClass.addMethod(MethodSpec.methodBuilder("findAll")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .returns(TypeName.VOID)
                .addParameter(gGraph, "graph")
                .addParameter(TypeName.LONG, "world")
                .addParameter(TypeName.LONG, "time")
                .addParameter(ParameterizedTypeName.get(gCallback, ArrayTypeName.of(ClassName.get(packageName, index.type()))), "callback")
                .addStatement("graph.index(world, time, META.name, $L)", TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ParameterizedTypeName.get(gCallback, ClassName.get(NodeIndex.class)))
                        .addMethod(MethodSpec.methodBuilder("on")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(ClassName.get(NodeIndex.class), "idx")
                                .returns(TypeName.VOID)
                                .beginControlFlow("if(idx != null)")
                                .addStatement("idx.findFrom($L)", TypeSpec.anonymousClassBuilder("")
                                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Callback.class), ArrayTypeName.of(ClassName.get(Node.class))))
                                        .addMethod(MethodSpec.methodBuilder("on")
                                                .addAnnotation(Override.class)
                                                .addModifiers(Modifier.PUBLIC)
                                                .addParameter(ArrayTypeName.of(gNode), "result")
                                                .returns(TypeName.VOID)
                                                .addStatement("$1T[] typedResult = new $1T[result.length]", ClassName.get(packageName, index.type()))
                                                .addStatement("$T.arraycopy(result, 0, typedResult, 0, result.length)", ClassName.get(java.lang.System.class))
                                                .addStatement("idx.free()")
                                                .addStatement("callback.on(typedResult)")
                                                .build())
                                        .build())
                                .nextControlFlow("else")
                                .addStatement("callback.on(new $T[0])", ClassName.get(packageName, index.type()))
                                .endControlFlow()
                                .build())
                        .build())
                .build());

        //Find method
        MethodSpec.Builder find = MethodSpec.methodBuilder("find")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .returns(TypeName.VOID)
                .addParameter(gGraph, "graph")
                .addParameter(TypeName.LONG, "world")
                .addParameter(TypeName.LONG, "time");

        StringBuilder params = new StringBuilder();
        index.attributes().forEach(attributeRef -> {
            find.addParameter(Helper.clazz(attributeRef.ref().type()), attributeRef.ref().name());
            params.append(",").append(attributeRef.ref().name());
        });
        find.addParameter(ParameterizedTypeName.get(ClassName.get(Callback.class), ArrayTypeName.of(ClassName.get(packageName, index.type()))), "callback")
                .addStatement("graph.index(world, time, META.name, $L)", TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Callback.class), ClassName.get(NodeIndex.class)))
                        .addMethod(MethodSpec.methodBuilder("on")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(ClassName.get(NodeIndex.class), "idx")
                                .returns(TypeName.VOID)
                                .beginControlFlow("if(idx != null)")
                                .addStatement("idx.findFrom($L$L)", TypeSpec.anonymousClassBuilder("")
                                        .addSuperinterface(ParameterizedTypeName.get(gCallback, ArrayTypeName.of(ClassName.get(Node.class))))
                                        .addMethod(MethodSpec.methodBuilder("on")
                                                .addAnnotation(Override.class)
                                                .addModifiers(Modifier.PUBLIC)
                                                .addParameter(ArrayTypeName.of(ClassName.get(Node.class)), "result")
                                                .returns(TypeName.VOID)
                                                .addStatement("$1T[] typedResult = new $1T[result.length]", ClassName.get(packageName, index.type()))
                                                .addStatement("$T.arraycopy(result, 0, typedResult, 0, result.length)", ClassName.get(java.lang.System.class))
                                                .addStatement("idx.free()")
                                                .addStatement("callback.on(typedResult)")
                                                .build())
                                        .build(), params)
                                .nextControlFlow("else")
                                .addStatement("callback.on(new $T[0])", ClassName.get(packageName, index.type()))
                                .endControlFlow()
                                .build())
                        .build());
        javaClass.addMethod(find.build());

        //Update method
        javaClass.addMethod(MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(packageName, index.type()), "toIndex")
                .addParameter(ParameterizedTypeName.get(ClassName.get(Callback.class), TypeName.BOOLEAN.box()), "callback")
                .addStatement("toIndex.graph().index(toIndex.world(), toIndex.time(), META.name, $L)", TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Callback.class), ClassName.get(NodeIndex.class)))
                        .addMethod(MethodSpec.methodBuilder("on")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(ClassName.get(NodeIndex.class), "idx")
                                .returns(TypeName.VOID)
                                .beginControlFlow("if(idx != null)")
                                .addStatement("idx.update(toIndex)")
                                .addStatement("callback.on(true)")
                                .nextControlFlow("else")
                                .addStatement("System.err.println($S)", "undeclared index " + index.name())
                                .addStatement("callback.on(false)")
                                .endControlFlow()
                                .build())
                        .build())
                .build());

        //Remove method
        javaClass.addMethod(MethodSpec.methodBuilder("remove")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(packageName, index.type()), "toUnIndex")
                .addParameter(ParameterizedTypeName.get(ClassName.get(Callback.class), TypeName.BOOLEAN.box()), "callback")
                .addStatement("toUnIndex.graph().index(toUnIndex.world(), toUnIndex.time(), META.name, $L)", TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Callback.class), ClassName.get(NodeIndex.class)))
                        .addMethod(MethodSpec.methodBuilder("on")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(ClassName.get(NodeIndex.class), "idx")
                                .returns(TypeName.VOID)
                                .beginControlFlow("if(idx != null)")
                                .addStatement("idx.unindex(toUnIndex)")
                                .addStatement("callback.on(true)")
                                .nextControlFlow("else")
                                .addStatement("System.err.println($S)", "undeclared index " + index.name())
                                .addStatement("callback.on(false)")
                                .endControlFlow()
                                .build())
                        .build())
                .build());

        collector.add(JavaFile.builder(packageName, javaClass.build()).build());
    }


}
