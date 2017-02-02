/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import org.mwg.internal.task.TaskHelper;
import org.mwg.plugin.ActionDeclaration;
import org.mwg.plugin.ActionRegistry;

public class TaskIDE {

    public static void attach(final org.mwg.WSServer server, final org.mwg.Graph graph) {
        server.addHandler("taskide", new ResourceHandler(new ClassPathResourceManager(TaskIDE.class.getClassLoader(), "taskide")).addWelcomeFiles("index.html").setDirectoryListingEnabled(false));
        server.addHandler("actionregistry", new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
                StringBuilder builder = new StringBuilder();
                builder.append("[");
                ActionRegistry registry = graph.actionRegistry();
                ActionDeclaration[] declarations = registry.declarations();
                for (int i = 0; i < declarations.length; i++) {
                    ActionDeclaration declaration = declarations[i];
                    if (i != 0) {
                        builder.append(",");
                    }
                    builder.append("{\"name\":");
                    TaskHelper.serializeString(declaration.name(), builder, false);
                    builder.append(",\"description\":");
                    TaskHelper.serializeString(declaration.description(), builder, false);
                    byte[] params = declaration.params();
                    if (params != null) {
                        builder.append(",\"params\":[");
                        for (int j = 0; j < params.length; j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            TaskHelper.serializeString(Type.typeName(params[j]), builder, false);
                        }
                        builder.append("]");
                    }
                    builder.append("}");
                }
                builder.append("]");
                httpServerExchange.getResponseHeaders().add(new HttpString("Access-Control-Allow-Origin"), "*");
                httpServerExchange.setStatusCode(StatusCodes.OK);
                httpServerExchange.getResponseSender().send(builder.toString());
            }
        });

    }

}
