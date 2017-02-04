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
package greycat.internal.task;

import greycat.Constants;
import greycat.Node;
import greycat.Type;
import greycat.base.BaseNode;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class TaskHelper {

    /**
     * @ignore ts
     */
    static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");

    /**
     * Return an array with all nodes contains in the input.
     * If the strict mode is enable, the input should contain only Node element. Otherwise, the not Node element will
     * be ignore.
     *
     * @param toFLat input to flat
     * @param strict is the function throw an exception when a no node element is found
     * @return a node array (one dimension)
     */
    public static Node[] flatNodes(Object toFLat, boolean strict) {
        if (toFLat instanceof BaseNode) {
            return new Node[]{(Node) toFLat};
        }

        if (toFLat instanceof Object[]) {
            Object[] resAsArray = (Object[]) toFLat;
            Node[] nodes = new Node[0];
            for (int i = 0; i < resAsArray.length; i++) {
                if (resAsArray[i] instanceof BaseNode) {
                    Node tmp[] = new Node[nodes.length + 1];
                    System.arraycopy(nodes, 0, tmp, 0, nodes.length);
                    tmp[nodes.length] = (BaseNode) resAsArray[i];
                    nodes = tmp;
                } else if (resAsArray[i] instanceof Object[]) {
                    Node[] innerNodes = flatNodes(resAsArray[i], strict);
                    Node[] tmp = new Node[nodes.length + innerNodes.length];
                    System.arraycopy(nodes, 0, tmp, 0, nodes.length);
                    System.arraycopy(innerNodes, 0, tmp, nodes.length, innerNodes.length);
                    nodes = tmp;
                } else if (strict) {
                    throw new RuntimeException("[ActionAddRemoveToGlobalIndex] The array in result contains an element with wrong type. " +
                            "Expected type: BaseNode. Actual type: " + resAsArray[i]);
                }
            }
            return nodes;
        } else if (strict) {
            throw new RuntimeException("[ActionAddRemoveToGlobalIndex] Wrong type of result. Expected type is BaseNode or an array of BaseNode." +
                    "Actual type is " + toFLat);
        }
        return new Node[0];
    }


    /**
     * Parses a String to an integer value.
     * @param s the String to parse
     * @return the integer value
     */
    /**
     * {@native ts
     * return parseInt(s);
     * }
     */
    public static int parseInt(String s) {
        return Integer.parseInt(s);
    }

    public static void serializeString(String param, StringBuilder builder, boolean singleQuote) {
        if(singleQuote){
            builder.append("\'");
        } else {
            builder.append("\"");
        }
        boolean escapteActivated = false;
        boolean previousIsEscape = false;
        if(param != null){
            for (int i = 0; i < param.length(); i++) {
                final char current = param.charAt(i);
                if(current == '\r' || current == '\n'){
                    if (!escapteActivated) {
                        escapteActivated = true;
                        builder.append(param.substring(0, i));
                    }
                    //simply ignore the '\r'
                } else if ( (singleQuote && current == '\'') || (!singleQuote && current == '\"')) {
                    if (!escapteActivated) {
                        escapteActivated = true;
                        builder.append(param.substring(0, i));
                    }
                    if (!previousIsEscape) {
                        builder.append('\\');
                    }
                    builder.append(param.charAt(i));
                } else {
                    if (escapteActivated) {
                        builder.append(param.charAt(i));
                    }
                }
                previousIsEscape = (current == '\\');
            }
        }
        if (!escapteActivated) {
            builder.append(param);
        }
        if(singleQuote){
            builder.append("\'");
        } else {
            builder.append("\"");
        }
    }

    public static void serializeType(byte type, StringBuilder builder) {
        builder.append(Type.typeName(type));
    }

    //TODO inject escape char
    public static void serializeStringParams(String[] params, StringBuilder builder) {
        for (int i = 0; i < params.length; i++) {
            if (i != 0) {
                builder.append(Constants.TASK_PARAM_SEP);
            }
            serializeString(params[i], builder,true);
        }
    }

    public static void serializeNameAndStringParams(String name, String[] params, StringBuilder builder) {
        builder.append(name);
        builder.append(Constants.TASK_PARAM_OPEN);
        serializeStringParams(params, builder);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
