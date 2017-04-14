package greycatTest.internal.task;

import com.sun.javaws.progress.Progress;
import greycat.ProgressReport;
import greycat.internal.heap.HeapBuffer;
import greycat.internal.task.CoreProgressReport;
import greycat.struct.Buffer;
import greycat.utility.ProgressType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by Gregory NAIN on 14/04/17.
 */
public class CoreProgressReportTest {

    @Test
    public void saveLoadTest() {

        CoreProgressReport report = new CoreProgressReport(ProgressType.START_TASK,2,3,"Yaha");
        Buffer b = new HeapBuffer();
        report.saveToBuffer(b);
        ProgressReport reportBack = new CoreProgressReport();
        reportBack.loadFromBuffer(b);

        Assert.assertEquals(report.type(), reportBack.type());
        Assert.assertEquals(report.index(), reportBack.index());
        Assert.assertEquals(report.total(), reportBack.total());
        Assert.assertEquals(report.comment(), reportBack.comment());

        report = new CoreProgressReport(ProgressType.START_TASK,2,3,null);
        b = new HeapBuffer();
        report.saveToBuffer(b);
        reportBack = new CoreProgressReport();
        reportBack.loadFromBuffer(b);

        Assert.assertEquals(report.type(), reportBack.type());
        Assert.assertEquals(report.index(), reportBack.index());
        Assert.assertEquals(report.total(), reportBack.total());
        Assert.assertEquals(report.comment(), reportBack.comment());

    }



}
