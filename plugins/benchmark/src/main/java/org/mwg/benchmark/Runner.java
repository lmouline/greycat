package org.mwg.benchmark;

import com.eclipsesource.json.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;

public class Runner {

    public static void main(String[] args) throws Exception {

        System.out.println("=== Heap ===");
        run(NoopCallback.class, false);
        run(TrampolineCallback.class, false);
        run(TrampolineTask.class, false);
        System.out.println("=== OffHeap ===");
        run(NoopCallback.class, true);
        run(TrampolineCallback.class, true);
        run(TrampolineTask.class, true);

        save();
    }

    private static void save() throws Exception {
        final JsonObject runBench = new JsonObject();
        runBench.add("benchmarks", JsonHandler.global);
        runBench.add("time", System.currentTimeMillis());

        if (System.getProperty("commit") != null) {
            runBench.add("commit", System.getProperty("commit"));
        }
        if (System.getProperty("data") != null) {
            File targetDir = new File(System.getProperty("data"));
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            File output = new File(targetDir, System.currentTimeMillis() + ".json");
            FileWriter writer = new FileWriter(output);
            runBench.writeTo(writer);
            writer.flush();
            writer.close();
        } else {
            System.out.println(runBench.toString());
        }
    }

    private static void clean() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 3; i++) {
            System.gc();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void run(Class main, boolean offHeap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        clean();
        System.out.println("Bench:" + main.getSimpleName());
        String[] params = new String[1];
        params[0] = offHeap ? "offheap" : "heap";
        main.getMethod("main", String[].class).invoke(main, (Object) params);
    }

}
