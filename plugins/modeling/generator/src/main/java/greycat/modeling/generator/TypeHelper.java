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
package greycat.modeling.generator;

import greycat.Type;
import greycat.modeling.language.ast.Attribute;
import greycat.modeling.language.ast.Relation;
import greycat.struct.DoubleArray;
import greycat.struct.IntArray;
import greycat.struct.LongArray;
import greycat.struct.StringArray;

class TypeHelper {

    static String stringType(final Attribute attribute) {
        StringBuilder typeBuilder = new StringBuilder();
        switch (attribute.type()) {
            case "String":
                typeBuilder.append("Type.STRING");
                break;
            case "Double":
                typeBuilder.append("Type.DOUBLE");
                break;
            case "Long":
                typeBuilder.append("Type.LONG");
                break;
            case "Integer":
                typeBuilder.append("Type.INT");
                break;
            case "Boolean":
                typeBuilder.append("Type.BOOL");
                break;
            default:
                //todo
        }

        if (attribute.isArray()) {
            typeBuilder.append("_ARRAY");
        }

        return typeBuilder.toString();
    }

    static byte type(final Attribute attribute) {
        if (attribute.isArray()) {
            switch (attribute.type()) {
                case "String":
                    return Type.STRING_ARRAY;
                case "Double":
                    return Type.DOUBLE_ARRAY;
                case "Long":
                    return Type.LONG_ARRAY;
                case "Integer":
                    return Type.INT_ARRAY;
                case "Boolean":
                    //todo
                    return -1;
                default:
                    //todo
                    return -1;
            }
        }

        switch (attribute.type()) {
            case "String":
                return Type.STRING;
            case "Double":
                return Type.DOUBLE;
            case "Long":
                return Type.LONG;
            case "Integer":
                return Type.INT;
            case "Boolean":
                return Type.BOOL;
            default:
                //todo
                return -1;
        }
    }

    static String typeToClassName(final Attribute attribute) {
        if (attribute.isArray()) {
            switch (attribute.type()) {
                case "String":
                    return StringArray.class.getName();
                case "Double":
                    return DoubleArray.class.getName();
                case "Long":
                    return LongArray.class.getName();
                case "Integer":
                    return IntArray.class.getName();
                case "Boolean":
                    //return BoolArray.class.getName();
                default:
                    //todo
                    return "";
            }
        } else {
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
                    //todo
                    return "";
            }
        }
    }


    static String formatClassType(Relation relation) {
        return relation.type();

        // Keep for later
//        int lastPoint = relation.type().lastIndexOf(".");
//        return relation.type().substring(0,lastPoint).toLowerCase() + relation.type().substring(lastPoint);
    }


}
