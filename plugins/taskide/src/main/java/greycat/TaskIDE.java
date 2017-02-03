package greycat;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import greycat.internal.task.TaskHelper;
import greycat.plugin.ActionDeclaration;
import greycat.plugin.ActionRegistry;

public class TaskIDE {

    public static void attach(final WSServer server, final Graph graph) {
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
