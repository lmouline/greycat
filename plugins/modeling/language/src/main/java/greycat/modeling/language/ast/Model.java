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

import greycat.modeling.language.GreyCatModelLexer;
import greycat.modeling.language.GreyCatModelParser;
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
        classifiers = new HashMap<>();
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
        BufferedTokenStream tokens = new CommonTokenStream(new GreyCatModelLexer(in));
        GreyCatModelParser parser = new GreyCatModelParser(tokens);
        GreyCatModelParser.ModelContext mctx = parser.model();

        // enums
        for (GreyCatModelParser.EnumDecContext enumDeclrContext : mctx.enumDec()) {
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

        // classes
        for (GreyCatModelParser.ClassDecContext classDeclContext : mctx.classDec()) {
            String classFqn = null;
            if (classDeclContext.TYPE_NAME() != null) {
                classFqn = classDeclContext.TYPE_NAME().toString();
            }
            if (classDeclContext.IDENT() != null) {
                classFqn = classDeclContext.IDENT().toString();
            }
            final Class newClass = (Class) getOrAddClass(model, classFqn);

            // parents
            if (classDeclContext.parentDec() != null) {
                if (classDeclContext.parentDec().TYPE_NAME() != null) {
                    final Class newClassTT = (Class) getOrAddClass(model, classDeclContext.parentDec().TYPE_NAME().toString());
                    newClass.setParent(newClassTT);
                }
                if (classDeclContext.parentDec().IDENT() != null) {
                    final Class newClassTT = (Class) getOrAddClass(model, classDeclContext.parentDec().IDENT().toString());
                    newClass.setParent(newClassTT);
                }
            }

            // attributes
            for (GreyCatModelParser.AttributeDecContext attDec : classDeclContext.attributeDec()) {
                String name = attDec.IDENT().getText();
                GreyCatModelParser.AttributeTypeContext attType = attDec.attributeType();
                String value = attType.getText();
                boolean isArray = false;
                if (value.endsWith("[]")) {
                    value = value.substring(0, value.length() - 2);
                    isArray = true;
                }

                final Attribute attribute = new Attribute(name, value, isArray);
                newClass.addProperty(attribute);
            }

            // relations
            for (GreyCatModelParser.RelationDecContext relDec : classDeclContext.relationDec()) {
                String name = relDec.IDENT().get(0).getText();
                String type;
                if (relDec.TYPE_NAME() == null) {
                    type = relDec.IDENT(1).toString();
                } else {
                    type = relDec.TYPE_NAME().toString();
                }
                final Relation relation = new Relation(name, type);
                newClass.addProperty(relation);
            }

            // indexes
            for (int i = 0; i < classDeclContext.indexDec().size(); i++) {
                GreyCatModelParser.IndexDecContext indexDecContext = classDeclContext.indexDec().get(i);
                // global indexes
                if (indexDecContext.globalIndex() != null) {
                    String idxName = newClass.name() + "Idx" + i;
                    if (indexDecContext.globalIndex().indexName() != null) {
                        idxName = indexDecContext.globalIndex().indexName().getText();
                    }
                    Index idx = new Index(idxName);
                    for (TerminalNode idxIdent : indexDecContext.globalIndex().IDENT()) {
                        Property indexedProperty = newClass.property(idxIdent.getText());
                        idx.addProperty(indexedProperty);
                    }
                    if (indexDecContext.globalIndex().getText().contains("timed")) {
                        idx.setTimed(true);
                    }

                    newClass.addIndex(idx);
                }
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
