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

import greycat.modeling.language.Enum;

import java.util.Set;
import java.util.TreeSet;

public class EnumImpl extends AbstractClassifier implements Enum {

    private final Set<String> literals;

    public EnumImpl(String p_name) {
        super(p_name);
        literals = new TreeSet<String>();
    }

    @Override
    public String[] literals() {
        return literals.toArray(new String[literals.size()]);
    }

    @Override
    public void addLiteral(String value) {
        literals.add(value);
    }

}
