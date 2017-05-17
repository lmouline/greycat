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
package greycat.modeling.language.impl;

import greycat.modeling.language.Attribute;
import greycat.modeling.language.Class;
import greycat.modeling.language.Index;
import greycat.modeling.language.Property;

import java.util.Set;
import java.util.TreeSet;

public class IndexImpl extends AbstractClassifier implements Index {

    private final Set<Property> literals;

    private final Class clazz;

    public IndexImpl(String p_name, Class clazz) {
        super(p_name);
        this.clazz = clazz;
        literals = new TreeSet<Property>();
    }

    @Override
    public Property[] properties() {
        return literals.toArray(new Property[literals.size()]);
    }

    @Override
    public void addProperty(String value) {
        Property prop = clazz.property(value);
        literals.add(prop);
        if(prop instanceof Attribute) {
            ((Attribute)prop).addIndex(this);
        }
    }

    @Override
    public Class type() {
        return this.clazz;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if(!(obj instanceof Index)) {
            return false;
        }

        return this.name().equals(((Index)obj).name());
    }

    @Override
    public int hashCode() {
        return this.name().hashCode();
    }
}
