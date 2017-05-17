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


import greycat.modeling.language.Property;
import greycat.modeling.language.Class;

import java.util.HashMap;
import java.util.Map;

public class ClassImpl extends AbstractClassifier implements Class {

    private final Map<String, Property> properties;

    private Class parent;

    public ClassImpl(String p_name) {
        super(p_name);
        properties = new HashMap<String, Property>();
    }

    @Override
    public Property[] properties() {
        return properties.values().toArray(new Property[properties.size()]);
    }

    @Override
    public Property property(String name) {
        for (Property property : properties()) {
            if (property.name().equals(name)) {
                return property;
            }
        }
        return null;
    }

    @Override
    public void addProperty(Property property) {
        properties.put(property.name(), property);
    }

    @Override
    public Class parent() {
        return parent;
    }

    @Override
    public void setParent(Class parent) {
        this.parent = parent;
    }

}
