package greycat.modeling.language.ast;

import java.util.LinkedList;
import java.util.List;

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
public class GlobalIndex {
    private final String name;
    private final List<Property> properties;

    private boolean timed;


    public GlobalIndex(String p_name) {
        this.name = p_name;
        this.properties = new LinkedList<>();
        this.timed = false;
    }

    public Property[] properties() {
        return properties.toArray(new Property[properties.size()]);
    }


    public void addProperty(Property p_property) {
        properties.add(p_property);
    }

    public boolean equals(Object p_other) {
        if (this == p_other) return true;
        if (!(p_other instanceof GlobalIndex)) return false;
        final GlobalIndex that = (GlobalIndex) p_other;
        return this.name().equals(that.name());
    }

    @Override
    public int hashCode() {
        return this.name().hashCode();
    }

    public String name() {
        return this.name;
    }

    public boolean timed() {
        return this.timed;
    }

    public void setTimed(boolean p_timed) {
        this.timed = p_timed;
    }
}
