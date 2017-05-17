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

import greycat.modeling.language.Classifier;

abstract class AbstractClassifier implements Classifier {

    protected final String name;

    protected AbstractClassifier(String p_name) {
        this.name = p_name;


        // Keep for later usage: namespace concept
//        if (p_name.contains(".")) {
//            name = p_name.substring(p_name.lastIndexOf('.') + 1);
//        } else {
//            name = p_name;
//        }
    }

    @Override
    public String name() {
        return name;
    }

//    @Override
//    public String fqn() {
//        if (pack != null) {
//            return pack + "." + name;
//        } else {
//            return name;
//        }
//    }

//    @Override
//    public String pack() {
//        return pack;
//    }

//    @Override
//    public String formatPack() {
//        if(pack != null) {
//            return pack.toLowerCase();
//        }
//        return null;
//    }
}
