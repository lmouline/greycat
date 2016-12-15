package leveldb;

import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.LevelDBStorage;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.core.task.Actions;
import org.mwg.task.Task;
import org.mwg.task.TaskResult;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class IndexTest {

    @Test
    public void testIndex() {
        Graph graph = new GraphBuilder().withScheduler(new NoopScheduler()).withStorage(new LevelDBStorage("db")).build();
        Graph graph2 = new GraphBuilder().withStorage(new LevelDBStorage("db")).build();

        final Task task = Actions.newTask()
                .travelInWorld("0")
                .travelInTime(System.currentTimeMillis() + "")
                .readGlobalIndex("indexName")
                .save();

        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                task.execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        graph.disconnect(new Callback<Boolean>() {
                            @Override
                            public void on(Boolean result) {
                                graph2.connect(new Callback<Boolean>() {
                                    @Override
                                    public void on(Boolean result) {
                                        task.execute(graph2, null);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        try {
            Path path = Paths.get("db");
            if (Files.exists(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (Exception e) {

        }

    }
}
