package rocksdb;

import org.junit.Test;
import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by ludovicmouline on 15/12/2016.
 */
public class IndexTest {

    @Test
    public void indexWithDB() {
        Graph graph = new GraphBuilder().withScheduler(new NoopScheduler()).withStorage(new RocksDBStorage("db")).build();
        Graph graph2 = new GraphBuilder().withStorage(new RocksDBStorage("db")).build();

        final String index = "indexName";
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                graph.index(0, 0, index, new Callback<NodeIndex>() {
                    @Override
                    public void on(NodeIndex result) {
                        graph.save(new Callback<Boolean>() {
                            @Override
                            public void on(Boolean result) {
                                graph.disconnect(new Callback<Boolean>() {
                                    @Override
                                    public void on(Boolean result) {
                                        graph2.connect(new Callback<Boolean>() {
                                            @Override
                                            public void on(Boolean result) {
                                                graph2.index(0, 0, index, new Callback<NodeIndex>() {
                                                    @Override
                                                    public void on(NodeIndex result) {
                                                        result.find(new Callback<Node[]>() {
                                                            @Override
                                                            public void on(Node[] result) {

                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
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
