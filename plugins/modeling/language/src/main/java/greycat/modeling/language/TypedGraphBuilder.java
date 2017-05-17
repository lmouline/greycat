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
package greycat.modeling.language;


import greycat.modeling.language.impl.*;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;

public class TypedGraphBuilder {

    public static TypedGraph parse(String content) {
        TypedGraph typedGraph = new TypedGraphImpl();
        return build(new ANTLRInputStream(content), typedGraph);
    }

    public static TypedGraph parse(File content) throws Exception {
        TypedGraph typedGraph = new TypedGraphImpl();
        return build(new ANTLRFileStream(content.getAbsolutePath()), typedGraph);
    }

    public static TypedGraph parse(File content, TypedGraph typedGraph) throws Exception {
        return build(new ANTLRFileStream(content.getAbsolutePath()), typedGraph);
    }

    private static TypedGraph build(ANTLRInputStream in, TypedGraph typedGraph) {

        BufferedTokenStream tokens = new CommonTokenStream(new greycat.modeling.language.GreyCatModelLexer(in));
        greycat.modeling.language.GreyCatModelParser parser = new greycat.modeling.language.GreyCatModelParser(tokens);
        greycat.modeling.language.GreyCatModelParser.ModelContext mmctx = parser.model();

        // Generate the enumeration
        for (greycat.modeling.language.GreyCatModelParser.EnumDeclrContext enumDeclrContext : mmctx.enumDeclr()) {
            String fqn = null;
            if (enumDeclrContext.TYPE_NAME() != null) {
                fqn = enumDeclrContext.TYPE_NAME().toString();
            }
            if (enumDeclrContext.IDENT() != null) {
                fqn = enumDeclrContext.IDENT().toString();
            }
            final Enum enumClass = (Enum) getOrAddEnum(typedGraph, fqn);
            for (TerminalNode literal : enumDeclrContext.enumLiterals().IDENT()) {
                enumClass.addLiteral(literal.getText());
            }
        }

        for (greycat.modeling.language.GreyCatModelParser.ClassDeclrContext classDeclrContext : mmctx.classDeclr()) {
            String classFqn = null;
            if (classDeclrContext.TYPE_NAME() != null) {
                classFqn = classDeclrContext.TYPE_NAME().toString();
            }
            if (classDeclrContext.IDENT() != null) {
                classFqn = classDeclrContext.IDENT().toString();
            }
            final greycat.modeling.language.Class newClass = (greycat.modeling.language.Class) getOrAddClass(typedGraph, classFqn);
            //process parents
            if (classDeclrContext.parentsDeclr() != null) {
                if (classDeclrContext.parentsDeclr().TYPE_NAME() != null) {
                    final greycat.modeling.language.Class newClassTT = (greycat.modeling.language.Class) getOrAddClass(typedGraph, classDeclrContext.parentsDeclr().TYPE_NAME().toString());
                    newClass.setParent(newClassTT);
                }
                if (classDeclrContext.parentsDeclr().IDENT() != null) {
                    final greycat.modeling.language.Class newClassTT = (greycat.modeling.language.Class) getOrAddClass(typedGraph, classDeclrContext.parentsDeclr().IDENT().toString());
                    newClass.setParent(newClassTT);
                }
            }
            for (greycat.modeling.language.GreyCatModelParser.AttributeDeclarationContext attDecl : classDeclrContext.attributeDeclaration()) {
                String name = attDecl.IDENT().getText();
                greycat.modeling.language.GreyCatModelParser.AttributeTypeContext attType = attDecl.attributeType();
                String value = attType.getText();
                boolean isArray = false;
                if(value.endsWith("[]")) {
                    value = value.substring(0,value.length()-2);
                    isArray = true;
                }

                final Attribute attribute = new AttributeImpl(name, value,isArray);
                newClass.addProperty(attribute);
            }
            for (greycat.modeling.language.GreyCatModelParser.RelationDeclarationContext relDecl : classDeclrContext.relationDeclaration()) {
                String name = relDecl.IDENT().get(0).getText();
                String type;
                if (relDecl.TYPE_NAME() == null) {
                    type = relDecl.IDENT(1).toString();
                } else {
                    type = relDecl.TYPE_NAME().toString();
                }
                final Relation relation = new RelationImpl(name, type);
                newClass.addProperty(relation);
            }
        }

        for (greycat.modeling.language.GreyCatModelParser.IndexDeclrContext indexDeclrContext : mmctx.indexDeclr()) {
            String name = indexDeclrContext.IDENT().get(0).getText();
            String type;
            if (indexDeclrContext.TYPE_NAME() == null) {
                type = indexDeclrContext.IDENT(1).toString();
            } else {
                type = indexDeclrContext.TYPE_NAME().toString();
            }
            final Index indexClass = (Index) getOrAddIndex(typedGraph, name, (greycat.modeling.language.Class) typedGraph.get(type));
            for (TerminalNode literal : indexDeclrContext.indexLiterals().IDENT()) {
                indexClass.addProperty(literal.getText());
            }
        }

        return typedGraph;
    }

    private static Classifier getOrAddClass(TypedGraph typedGraph, String fqn) {
        Classifier previous = typedGraph.get(fqn);
        if (previous != null) {
            return previous;
        }
        previous = new ClassImpl(fqn);
        typedGraph.addClassifier(previous);
        return previous;
    }

    private static Classifier getOrAddIndex(TypedGraph typedGraph, String fqn, greycat.modeling.language.Class clazz) {
        Classifier previous = typedGraph.get(fqn);
        if (previous != null) {
            return previous;
        }
        previous = new IndexImpl(fqn, clazz);
        typedGraph.addClassifier(previous);
        return previous;
    }

    private static Classifier getOrAddEnum(TypedGraph typedGraph, String fqn) {
        Classifier previous = typedGraph.get(fqn);
        if (previous != null) {
            return previous;
        }
        previous = new EnumImpl(fqn);
        typedGraph.addClassifier(previous);
        return previous;
    }
}
