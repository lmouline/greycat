package org.mwg.debug;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lucasm on 22/08/16.
 */
public class JsonServer {

    private HttpServer server;
    private int port;

    private static String GULP_SERVER_ADDRESS = "http://localhost:8999";
    private static String TASK_PREFIX = "task_";
    private static String CONTEXT_PREFIX = "context_";
    private static String JSON_EXTENSION = ".json";

    public JsonServer(int port) {
        this.port = port;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            createContexts();
            ListHandler taskListHandler = new ListHandler();
            server.createContext("/task", taskListHandler);
            server.createContext("/context", taskListHandler);
            server.setExecutor(null); // creates a default executor
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void start() {
        server.start();
        System.out.println("JSON Server listener through :" + port);
    }

    /**
     * Returns a list of the id of the JSON files in the JSON directory
     * @return
     */
    private static Set<String> getTaskIDs() {
        Set<String> taskIDs = new HashSet<>();

        File[] files = new File("JSON").listFiles();
        for (File file : files) {
            String fileName = file.getName();
            //checking only if task_XXX.json exists (and not context_XXX.json)
            if (fileName.startsWith(TASK_PREFIX)) {
                String taskID = fileName.substring(TASK_PREFIX.length());
                taskID = taskID.substring(0, taskID.length() - JSON_EXTENSION.length());
                taskIDs.add(taskID);
            }
        }
        return taskIDs;
    }

    /**
     * Create a context for each JSON file
     */
    private void createContexts() {
        Set<String> taskIDs = getTaskIDs();
        for (String taskID : taskIDs){
            server.createContext("/task/" + taskID, new JsonHandler("JSON/task_" + taskID + ".json"));
            server.createContext("/context/" + taskID, new JsonHandler("JSON/context_" + taskID + ".json"));
            System.out.println(taskID);
        }
    }

    public static void main(String[] args) throws Exception {
        new JsonServer(9999).start();
    }


    static class JsonHandler implements HttpHandler {
        private String filename;

        public JsonHandler(String filename) {
            this.filename = filename;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            final Headers headers = t.getResponseHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Access-Control-Allow-Origin", "*");


            try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }

                String response = sb.toString();
                t.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class ListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            final Headers headers = t.getResponseHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Access-Control-Allow-Origin", "*");


            StringBuilder sb = new StringBuilder();
            sb.append("[");
            Set<String> taskIDs = getTaskIDs();
            for (String taskID : taskIDs){
                sb.append(taskID);
                sb.append(",");
            }

            //remove last comma if there is one
            if (sb.charAt(sb.length() - 1) == ','){
                sb.setLength(sb.length() - 1);
            }
            sb.append("]");

            String response = sb.toString();
            t.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
