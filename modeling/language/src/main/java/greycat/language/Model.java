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
package greycat.language;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Model {

    private final Map<String, Class> classesMap;
    private final Map<String, Enum> enumsMap;
    private final Map<String, GlobalIndex> globalIndexesMap;
    private final Map<String, Type> typesMap;
    private final Map<String, Task> tasksMap;

    public Model() {
        classesMap = new HashMap<>();
        enumsMap = new HashMap<>();
        globalIndexesMap = new HashMap<>();
        typesMap = new HashMap<>();
        tasksMap = new HashMap<>();
    }


    public void parse(File content) throws Exception {
        build(new ANTLRFileStream(content.getAbsolutePath()));
    }

    private void build(ANTLRInputStream in) {
        BufferedTokenStream tokens = new CommonTokenStream(new GreyCatModelLexer(in));
        GreyCatModelParser parser = new GreyCatModelParser(tokens);
        GreyCatModelParser.ModelDclContext modelDclCtx = parser.modelDcl();

        // enums
        for (GreyCatModelParser.EnumDclContext enumDclCtx : modelDclCtx.enumDcl()) {
            String fqn = enumDclCtx.name.getText();
            final Enum enumClass = getOrCreateEnum(fqn);
            for (TerminalNode literal : enumDclCtx.enumLiteralsDcl().IDENT()) {
                enumClass.addLiteral(literal.getText());
            }
        }

        // classes
        for (GreyCatModelParser.ClassDclContext classDclCtx : modelDclCtx.classDcl()) {
            String classFqn = classDclCtx.name.getText();
            final Class newClass = getOrCreateClass(classFqn);

            // parents
            if (classDclCtx.parentDcl() != null) {
                final Class parentClass = getOrCreateClass(classDclCtx.parentDcl().name.getText());
                newClass.setParent(parentClass);
            }
            // attributes
            for (GreyCatModelParser.AttributeDclContext attDcl : classDclCtx.attributeDcl()) {
                String name = attDcl.name.getText();
                GreyCatModelParser.AttributeTypeDclContext attTypeDclCxt = attDcl.attributeTypeDcl();
                String value = attTypeDclCxt.getText();
                boolean isArray = false;
                if (value.endsWith("[]")) {
                    value = value.substring(0, value.length() - 2);
                    isArray = true;
                }

                final Attribute attribute = new Attribute(name, value, isArray);
                newClass.addAttribute(attribute);
            }

            // relations
            for (GreyCatModelParser.RelationDclContext relDclCtx : classDclCtx.relationDcl()) {
                String name = relDclCtx.name.getText();
                String type = relDclCtx.type.getText();

                Relation relation = new Relation(name, type);
                newClass.addRelation(relation);
            }

            // references
            for (GreyCatModelParser.ReferenceDclContext refDclCtx : classDclCtx.referenceDcl()) {
                String name = refDclCtx.name.getText();
                String type = refDclCtx.type.getText();

                Reference reference = new Reference(name, type);
                newClass.addReference(reference);
            }

            // local indexes
            for (GreyCatModelParser.LocalIndexDclContext localIndexDclCtx : classDclCtx.localIndexDcl()) {
                String name = localIndexDclCtx.name.getText();
                String type = localIndexDclCtx.type.getText();
                Class indexedClass = getOrCreateClass(type);

                LocalIndex localIndex = new LocalIndex(name, type);
                for (TerminalNode idxDclIdent : localIndexDclCtx.indexAttributesDcl().IDENT()) {
                    Attribute att = indexedClass.getAttribute(idxDclIdent.getText());
                    localIndex.addAttribute(att);
                }
            }
        }

        // global indexes
        for (GreyCatModelParser.GlobalIndexDclContext globalIdxDclContext : modelDclCtx.globalIndexDcl()) {
            String name = globalIdxDclContext.name.getText();
            String type = globalIdxDclContext.type.getText();
            Class indexedClass = getOrCreateClass(type);

            final GlobalIndex globalIndex = getOrCreateGlobalIndex(name, type);
            for (TerminalNode idxDclIdent : globalIdxDclContext.indexAttributesDcl().IDENT()) {
                Attribute att = indexedClass.getAttribute(idxDclIdent.getText());
                globalIndex.addAttribute(att);
            }
        }


        // types
        // TODO
//        for (GreyCatModelParser.TypeDclContext typeDclContext : modelDclCtx.typeDcl()) {
//            String name = typeDclContext.name.getText();
//
//            final Type type = getOrCreateType(name);
//            for (TerminalNode literal : enumDclCtx.enumLiteralsDcl().IDENT()) {
//                enumClass.addLiteral(literal.getText());
//            }
//        }

        // tasks
        // TODO
    }


    private GlobalIndex getOrCreateGlobalIndex(String fqn, String type) {
        GlobalIndex previous = this.globalIndexesMap.get(fqn);
        if(previous != null) {
            return previous;
        }
        previous = new GlobalIndex(fqn, type);
        this.globalIndexesMap.put(fqn, previous);
        return previous;
    }

    private Class getOrCreateClass(String fqn) {
        Class previous = this.classesMap.get(fqn);
        if (previous != null) {
            return previous;
        }
        previous = new Class(fqn);
        this.classesMap.put(fqn, previous);
        return previous;
    }

    private Enum getOrCreateEnum(String fqn) {
        Enum previous = this.enumsMap.get(fqn);
        if (previous != null) {
            return previous;
        }
        previous = new Enum(fqn);
        this.enumsMap.put(fqn, previous);
        return previous;
    }

    private Type getOrCreateType(String fqn) {
        Type previous = this.typesMap.get(fqn);
        if (previous != null) {
            return previous;
        }
        previous = new Type(fqn);
        this.typesMap.put(fqn, previous);
        return previous;
    }

}
