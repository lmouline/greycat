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
import greycat.language.*;
import greycat.language.Class;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.*;

class ClassTypeGenerator {

    static JavaSource[] generate(String packageName, String pluginName, Model model) {
        JavaSource[] sources = new JavaSource[model.classes().length * 2]; // interfaces + classes

        int index = 0;
        for (Class classType : model.classes()) {
            JavaInterfaceSource interfaceSource = generateInterface(packageName, classType);
            sources[index] = interfaceSource;
            index++;

            JavaClassSource classSource = generateClass(packageName, classType);
            sources[index] = classSource;
            index++;
        }


        return sources;
    }


    private static JavaInterfaceSource generateInterface(String packageName, Class classType) {
        final JavaInterfaceSource javaInterface = Roaster.create(JavaInterfaceSource.class);
        javaInterface.setPackage(packageName);
        javaInterface.setName(classType.name());

        for (Class parent : classType.parents()) {
            javaInterface.addInterface(packageName + "." + parent.name());
        }

        return javaInterface;
    }

    private static JavaClassSource generateClass(String packageName, Class classType) {
        // interfaces
        final JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
        javaClass.setPackage(packageName);
        javaClass.setName(classType.name() + "Impl");

        javaClass.addInterface(packageName + "." + classType.name());
        for (Class parent : classType.parents()) {
            javaClass.addInterface(packageName + "." + parent.name());
        }

        javaClass.setSuperType("greycat.base.BaseNode");

        // constructor
        MethodSource<JavaClassSource> constructor = javaClass.addMethod().setConstructor(true);
        constructor.addParameter("long", "p_world");
        constructor.addParameter("long", "p_time");
        constructor.addParameter("long", "p_id");
        constructor.addParameter(Graph.class, "p_graph");
        constructor.setBody("super(p_world, p_time, p_id, p_graph);");
        constructor.setVisibility(Visibility.PUBLIC);


        return javaClass;
    }

    private static String upperCaseFirstChar(String init) {
        return init.substring(0, 1).toUpperCase() + init.substring(1);
    }


    private static String typeToString(final Attribute attribute) {
        StringBuilder typeBuilder = new StringBuilder();
        switch (attribute.type()) {
            case "String":
                typeBuilder.append("CustomType.STRING");
                break;
            case "Double":
                typeBuilder.append("CustomType.DOUBLE");
                break;
            case "Long":
                typeBuilder.append("CustomType.LONG");
                break;
            case "Integer":
                typeBuilder.append("CustomType.INT");
                break;
            case "Boolean":
                typeBuilder.append("CustomType.BOOL");
                break;
            default:
                throw new RuntimeException("type " + attribute.type() + " is unknown");
        }

//        if (attribute.isArray()) {
//            typeBuilder.append("_ARRAY");
//        }

        return typeBuilder.toString();
    }


    private static String typeToClassName(final Attribute attribute) {
        switch (attribute.type()) {
            case "String":
                return String.class.getCanonicalName();
            case "Double":
                return double.class.getCanonicalName();
            case "Long":
                return long.class.getCanonicalName();
            case "Integer":
                return int.class.getCanonicalName();
            case "Boolean":
                return boolean.class.getCanonicalName();
            default:
                throw new RuntimeException("type " + attribute.type() + " is unknown");
        }
    }


}
