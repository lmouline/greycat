import cloud.Cloud;
import cloud.Server;
import cloud.Software;
import org.mwg.Callback;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.sample.HelloModel;
import org.mwg.task.Actions;
import org.mwg.task.Task;
import org.mwg.utility.VerboseHookFactory;

import java.util.Arrays;

import static org.mwg.task.Actions.get;

public class HelloWorld {

    public static void main(String[] args) {

        HelloModel model = new HelloModel(new GraphBuilder()/*.withOffHeapMemory()*/);
        model.graph().connect((Boolean result) -> {

            //Test typed node creation
            Cloud cloud = model.newCloud(0, 0);
            Server server = model.newServer(0, 0);
            server.setName("Hello");
            System.out.println("server.getName()= " + server.getName());
            System.out.println("server= " + server);

            cloud.addToServers(server);
            System.out.println("cloud= " + cloud);

            cloud.getServers(new Callback<Server[]>() {
                @Override
                public void on(Server[] servers) {
                    System.out.println("cloud.getServers()= " + Arrays.toString(servers));
                    System.out.println("cloud.getServers()[0]= " + servers[0]);
                }
            });


            Software soft0 = model.newSoftware(0, 0);
            soft0.setName("Hello");
            soft0.setLoad(42.0);

            soft0.jump(10, new Callback<Node>() {
                @Override
                public void on(Node soft0_t10) {
                    ((Software) soft0_t10).setLoad(50.0);
                    System.out.println("soft0.jump(10)= "  + soft0_t10);
                }
            });

            System.out.println("soft0.getLoad()= " + soft0.getLoad());

            //Test find usage
            model.graph().findAll(0, 0, "clouds", cloudsResult -> {
                System.out.println("model.graph().findAll(0,0,clouds)[0]=" + cloudsResult[0]);
            });
            model.graph().find(0, 0, "clouds", "name=Hello", cloudsResult -> {
                System.out.println("model.graph().find(0, 0,clouds,name=Hello)= " + cloudsResult[0]);
            });

            model.findAllClouds(0, 0, new Callback<Software[]>() {
                @Override
                public void on(Software[] softwares) {
                    System.out.println(" model.findAllClouds(0, 0)=" + softwares[0]);
                }
            });


            model.findClouds(0, 0, "name=Hello", new Callback<Software[]>() {
                @Override
                public void on(Software[] clouds) {
                    System.out.println("model.findClouds(0, 0,name=Hello)=" + Arrays.toString(clouds));
                }
            });

            model.findClouds(0, 0, "name=NOOP", new Callback<Software[]>() {
                @Override
                public void on(Software[] result) {
                    System.out.println("model.findClouds(0, 0, name=NOOP)=" + Arrays.toString(result));
                }
            });

            System.out.println();

            //Test task usage
            Actions
                .fromIndexAll("clouds")
                .foreach(
                    get("name")
                    .then(ctx -> {
                        System.out.println(ctx.result().get(0));
                    })
                )
                .hook(new VerboseHookFactory())
                .execute(model.graph(), null);

            System.out.println();

            Task t = Actions.newTask();
            t.fromIndexAll("clouds")
                    .get("name")
                    .foreach(Actions.newTask()
                            .then(context -> System.out.println(context.result().get(0)))
                    )
                    .hook(new VerboseHookFactory())
                    .execute(model.graph(),null);

        });


    }

}
