package org.mwg;

import org.mwg.chunk.StateChunk;
import org.mwg.chunk.TimeTreeChunk;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;
import org.mwg.ml.algorithm.regression.PolynomialNode;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.Job;
import org.mwg.struct.Buffer;
import org.mwg.structure.StructurePlugin;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mwg.task.Actions.*;

/**
 * Created by gnain on 14/09/16.
 */
public class JClient {


    public static void main(String[] args) {


        final Graph g = new GraphBuilder().withMemorySize(200000).withPlugin(new StructurePlugin()).withPlugin(new MLPlugin()).withStorage(new WSClient("ws://localhost:8050")).build();
        g.connect(connectionResult -> {

            //updateContract(g);

            Task readProfile = newTask()
                    .setTime("" + System.currentTimeMillis())
                    .fromIndex("cities", "name=Luxembourg")
                    .traverseIndex("stations", "name", "ROBERT SCHUMAN")
                    .print("{{result}}")
                    .asVar("station")
                    .inject(System.currentTimeMillis() + (2 * 3600 * 1000))
                    .asVar("currentTime")
                    .fromVar("station")
                    .doWhile(
                            jump("{{currentTime}}")
                                    .traverse(JCDecauxHistoryLoad.AVAILABLE_BIKES_PROFILE)
                                    .asVar(JCDecauxHistoryLoad.AVAILABLE_BIKES_PROFILE_NODE)
                                    .fromVar("station")
                                    .traverse(JCDecauxHistoryLoad.AVAILABLE_STANDS_PROFILE)
                                    .asVar(JCDecauxHistoryLoad.AVAILABLE_STANDS_PROFILE_NODE)
                                    .fromVar("station")
                                    .jump("{{=currentTime-(7*24*3600*1000)}}")
                                    .traverse(JCDecauxHistoryLoad.AVAILABLE_BIKES)
                                    .asVar(JCDecauxHistoryLoad.AVAILABLE_BIKES_VALUE)
                                    .fromVar("station")
                                    .traverse(JCDecauxHistoryLoad.AVAILABLE_STANDS)
                                    .asVar(JCDecauxHistoryLoad.AVAILABLE_STANDS_VALUE)
                                    .fromVar("station")
                                    .jump("{{currentTime}}")
                                    .then(context -> {
                                        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(context.time()), ZoneId.systemDefault());
                                        GaussianSlotNode availableBikesNode = (GaussianSlotNode) context.variable(JCDecauxHistoryLoad.AVAILABLE_BIKES_PROFILE_NODE).get(0);
                                        GaussianSlotNode availableStandsNode = (GaussianSlotNode) context.variable(JCDecauxHistoryLoad.AVAILABLE_STANDS_PROFILE_NODE).get(0);

                                        PolynomialNode availableBikesValue = (PolynomialNode) context.variable(JCDecauxHistoryLoad.AVAILABLE_BIKES_VALUE).get(0);
                                        PolynomialNode availableStandsValue = (PolynomialNode) context.variable(JCDecauxHistoryLoad.AVAILABLE_STANDS_VALUE).get(0);

                                        DeferCounter s = context.graph().newCounter(2);
                                        final double[][] availableBikes = new double[1][1];
                                        availableBikesNode.predict(result -> {
                                            availableBikes[0] = result;
                                            s.count();
                                        });
                                        final double[][] availableStands = new double[1][1];
                                        availableStandsNode.predict(result -> {
                                            availableStands[0] = result;
                                            s.count();
                                        });

                                        s.then(() -> {

                                            System.out.println("" + ldt.getHour() + "h: \t" + availableBikes[0][0] + " bikes("+availableBikesValue.get("value")+")  and " + availableStands[0][0] + " stands("+availableStandsValue.get("value")+") available");
                                            context.continueTask();

                                        });


                                    }), context -> {
                                long time = context.time();
                                time += 3600 * 1000;
                                if (time < (System.currentTimeMillis() + (25 * 3600 * 1000))) {
                                    context.setTime(time);
                                    context.setVariable("currentTime", time);
                                    return true;
                                } else {
                                    return false;
                                }
                            });
            readProfile.execute(g, null);


        });
    }

}
