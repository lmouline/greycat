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

import greycat.Graph;
import greycat.Type;
import greycat.language.*;
import greycat.language.Class;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.*;

class ClassTypeGenerator {

    static JavaSource[] generate(String packageName, String pluginName, Model model) {
        JavaSource[] sources = new JavaSource[model.classes().length];

        for (int i = 0; i < model.classes().length; i++) {
            sources[i] = generateClass(packageName, model.classes()[i]);
        }

        return sources;
    }


    private static JavaClassSource generateClass(String packageName, Class classType) {
        final JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
        javaClass.setPackage(packageName);
        javaClass.setName(classType.name());

        if (classType.parent() != null) {
            javaClass.setSuperType(packageName + "." + classType.parent().name());
        } else {
            javaClass.setSuperType("greycat.base.BaseNode");
        }

        // constructor
        MethodSource<JavaClassSource> constructor = javaClass.addMethod().setConstructor(true);
        constructor.addParameter("long", "p_world");
        constructor.addParameter("long", "p_time");
        constructor.addParameter("long", "p_id");
        constructor.addParameter(Graph.class, "p_graph");
        constructor.setBody("super(p_world, p_time, p_id, p_graph);");
        constructor.setVisibility(Visibility.PUBLIC);

        // field for node name
        javaClass.addField()
                .setVisibility(Visibility.PUBLIC)
                .setFinal(true)
                .setName("NODE_NAME")
                .setType(String.class)
                .setStringInitializer(javaClass.getCanonicalName())
                .setStatic(true);

        javaClass.addImport(Type.class);

        // attributes
        for (Attribute att : classType.attributes()) {

            // fields
            FieldSource<JavaClassSource> typeField = javaClass.addField()
                    .setVisibility(Visibility.PUBLIC)
                    .setFinal(true)
                    .setName(att.name().toUpperCase() + "_TYPE")
                    .setType(byte.class)
                    .setStatic(true);
            // TODO custom type!
            typeField.setLiteralInitializer(TypeManager.builtInTypeName(att.type()));

            // getter
            MethodSource<JavaClassSource> getter = javaClass.addMethod();
            getter.setVisibility(Visibility.PUBLIC).setFinal(true);
            getter.setReturnType(TypeManager.builtInClassName(att.name()));
            getter.setName("get" + upperCaseFirstChar(att.name()));

            if (TypeManager.isPrimitive(att.type())) {
                getter.setBody("return (" + TypeManager.builtInClassName(att.type()) + ") super.get(" + att.name().toUpperCase() + ");");

            } else {
                getter.setBody("return (" + TypeManager.builtInClassName(att.type()) + ") super.getOrCreate(" + att.name().toUpperCase() + ", " + att.name().toUpperCase() + "_TYPE);");

            }

            // setter
            javaClass.addMethod()
                    .setVisibility(Visibility.PUBLIC).setFinal(true)
                    .setName("set" + upperCaseFirstChar(att.name()))
                    .setReturnType(classType.name())
                    .setBody("super.set(" + att.name().toUpperCase() + ", " + att.name().toUpperCase()
                            + "_TYPE,value);\nreturn this;"
                    )
                    .addParameter(TypeManager.builtInClassName(att.type()), "value");


        }
        return javaClass;
    }

    private static String upperCaseFirstChar(String init) {
        return init.substring(0, 1).toUpperCase() + init.substring(1);
    }


}
