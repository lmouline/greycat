package org.mwg;

import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

public class TaskIDE {

    public static void attach(org.mwg.WSServer server) {
        server.addHandler("taskide", new ResourceHandler(new ClassPathResourceManager(TaskIDE.class.getClassLoader(), "taskide")).addWelcomeFiles("index.html").setDirectoryListingEnabled(false));
    }

}
