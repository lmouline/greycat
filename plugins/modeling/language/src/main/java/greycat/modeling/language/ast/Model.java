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
package greycat.modeling.language.ast;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Model {

    private final Map<String, Classifier> classifiers;

    public Model() {
        classifiers = new HashMap<String, Classifier>();
    }

    public Classifier[] classifiers() {
        return classifiers.values().toArray(new Classifier[classifiers.size()]);
    }

    public void addClassifier(Classifier classifier) {
        classifiers.put(classifier.name(), classifier);
    }

    public Classifier get(String fqn) {
        return classifiers.get(fqn);
    }


    public static Model parse(File content, Model model) throws Exception {
        return build(new ANTLRFileStream(content.getAbsolutePath()), model);
    }

    private static Model build(ANTLRInputStream in, Model model) {

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
            final Enum enumClass = (Enum) getOrAddEnum(model, fqn);
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
            final Class newClass = (Class) getOrAddClass(model, classFqn);
            //process parents
            if (classDeclrContext.parentsDeclr() != null) {
                if (classDeclrContext.parentsDeclr().TYPE_NAME() != null) {
                    final Class newClassTT = (Class) getOrAddClass(model, classDeclrContext.parentsDeclr().TYPE_NAME().toString());
                    newClass.setParent(newClassTT);
                }
                if (classDeclrContext.parentsDeclr().IDENT() != null) {
                    final Class newClassTT = (Class) getOrAddClass(model, classDeclrContext.parentsDeclr().IDENT().toString());
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

                final Attribute attribute = new Attribute(name, value,isArray);
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
                final Relation relation = new Relation(name, type);
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
            final Index indexClass = (Index) getOrAddIndex(model, name, (Class) model.get(type));
            for (TerminalNode literal : indexDeclrContext.indexLiterals().IDENT()) {
                indexClass.addProperty(literal.getText());
            }
        }

        return model;
    }

    private static Classifier getOrAddClass(Model model, String fqn) {
        Classifier previous = model.get(fqn);
        if (previous != null) {
            return previous;
        }
        previous = new Class(fqn);
        model.addClassifier(previous);
        return previous;
    }

    private static Classifier getOrAddIndex(Model model, String fqn, Class clazz) {
        Classifier previous = model.get(fqn);
        if (previous != null) {
            return previous;
        }
        previous = new Index(fqn, clazz);
        model.addClassifier(previous);
        return previous;
    }

    private static Classifier getOrAddEnum(Model model, String fqn) {
        Classifier previous = model.get(fqn);
        if (previous != null) {
            return previous;
        }
        previous = new Enum(fqn);
        model.addClassifier(previous);
        return previous;
    }
}
