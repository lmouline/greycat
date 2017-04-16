package greycatTest.base;

import greycat.Graph;
import greycat.GraphBuilder;
import greycat.base.BaseTaskResult;
import greycat.internal.heap.HeapBuffer;
import greycat.struct.Buffer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Gregory NAIN on 16/04/17.
 */
public class BaseTaskResultTest {

    @Test
    public void saveLoadTest() {

        Graph graph = GraphBuilder.newBuilder().build();
        graph.connect(connected -> {
            BaseTaskResult<String> taskResultOrigin = new BaseTaskResult("ResultContent", false);
            taskResultOrigin.setException(new RuntimeException("Exception text"));

            Buffer buffer = new HeapBuffer();
            taskResultOrigin.saveToBuffer(buffer);

            BaseTaskResult taskResultLoaded = new BaseTaskResult(null, false);
            taskResultLoaded.load(buffer, graph);
            Assert.assertEquals(expectedException, taskResultLoaded.exception().toString());
            graph.disconnect(null);
        });
    }

    private static final String expectedException="java.lang.Exception: java.lang.RuntimeException: Exception text\n" +
            "\tat greycatTest.base.BaseTaskResultTest.lambda$saveLoadTest$0(BaseTaskResultTest.java:22)\n" +
            "\tat greycatTest.base.BaseTaskResultTest$$Lambda$1/824009085.on(Unknown Source)\n" +
            "\tat greycat.internal.CoreGraph$4$1$1.on(CoreGraph.java:348)\n" +
            "\tat greycat.internal.CoreGraph$4$1$1.on(CoreGraph.java:309)\n" +
            "\tat greycat.internal.BlackHoleStorage.get(BlackHoleStorage.java:46)\n" +
            "\tat greycat.internal.CoreGraph$4$1.on(CoreGraph.java:309)\n" +
            "\tat greycat.internal.CoreGraph$4$1.on(CoreGraph.java:291)\n" +
            "\tat greycat.internal.BlackHoleStorage.lock(BlackHoleStorage.java:80)\n" +
            "\tat greycat.internal.CoreGraph$4.on(CoreGraph.java:291)\n" +
            "\tat greycat.internal.CoreGraph$4.on(CoreGraph.java:288)\n" +
            "\tat greycat.internal.BlackHoleStorage.connect(BlackHoleStorage.java:72)\n" +
            "\tat greycat.internal.CoreGraph.connect(CoreGraph.java:288)\n" +
            "\tat greycatTest.base.BaseTaskResultTest.saveLoadTest(BaseTaskResultTest.java:20)\n" +
            "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
            "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
            "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
            "\tat java.lang.reflect.Method.invoke(Method.java:497)\n" +
            "\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)\n" +
            "\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)\n" +
            "\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)\n" +
            "\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)\n" +
            "\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)\n" +
            "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)\n" +
            "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)\n" +
            "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)\n" +
            "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)\n" +
            "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)\n" +
            "\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)\n" +
            "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)\n" +
            "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:363)\n" +
            "\tat org.junit.runner.JUnitCore.run(JUnitCore.java:137)\n" +
            "\tat com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)\n" +
            "\tat com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51)\n" +
            "\tat com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:237)\n" +
            "\tat com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)\n" +
            "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
            "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
            "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
            "\tat java.lang.reflect.Method.invoke(Method.java:497)\n" +
            "\tat com.intellij.rt.execution.application.AppMain.main(AppMain.java:147)\n";


}
