package org.mwg.ml.common.structure;

import org.mwg.*;
import org.mwg.ml.common.distance.Distance;
import org.mwg.ml.common.distance.DistanceEnum;
import org.mwg.ml.common.distance.EuclideanDistance;
import org.mwg.ml.common.distance.GaussianDistance;
import org.mwg.plugin.*;
import org.mwg.struct.Relationship;
import org.mwg.task.*;
import org.mwg.utility.Enforcer;

import static org.mwg.task.Actions.*;
import static org.mwg.task.Actions.traverse;

public class KDTree extends AbstractNode {

    public static final String NAME = "KDTree";

    private static final String INTERNAL_LEFT = "_left";                //to navigate left
    private static final String INTERNAL_RIGHT = "_right";              //to navigate right

    private static final String INTERNAL_KEY = "_key";                  //Keys of the node
    private static final String INTERNAL_VALUE = "_value";              //Values of the node
    public static final String NUM_NODES = "_num";                      //Number of nodes inserted in the tree

    private static final String INTERNAL_DIM = "_dim";                  //Dimension of the key

    public static final String DISTANCE_THRESHOLD = "_threshold";       //Distance threshold to define when 2 keys are not considered the same anymopre
    public static final double DISTANCE_THRESHOLD_DEF = 1e-10;

    public static final String DISTANCE_TYPE = "disttype";
    public static final int DISTANCE_TYPE_DEF = 0;
    public static final String GAUSSIAN_PRECISION = "_precision";


    //Insert key/value task
    private static Task insert = whileDo(new TaskFunctionConditional() {
        @Override
        public boolean eval(TaskContext context) {

            Node current = context.resultAsNodes().get(0);
            double[] nodeKey = (double[]) current.get(INTERNAL_KEY);

            //Get variables from context

            //toDo optimize the variables here
            int dim = (int) context.variable("dim").get(0);
            double[] keyToInsert = (double[]) context.variable("key").get(0);

            Node valueToInsert = (Node) context.variable("value").get(0);
            Node root = (Node) context.variable("root").get(0);
            Distance distance = (Distance) context.variable("distance").get(0);
            double err = (double) context.variable("err").get(0);
            int lev = (int) context.variable("lev").get(0);


            //Bootstrap, first insert ever
            if (nodeKey == null) {
                current.setProperty(INTERNAL_KEY, Type.DOUBLE_ARRAY, keyToInsert);
                current.setProperty(INTERNAL_VALUE, Type.RELATION, new long[]{valueToInsert.id()});
                current.setProperty(NUM_NODES, Type.INT, 1);
                return false; //stop the while loop and insert here
            } else if (distance.measure(keyToInsert, nodeKey) < err) {
                current.setProperty(INTERNAL_VALUE, Type.RELATION, new long[]{valueToInsert.id()});
                return false; //insert in the current node, and done with it, no need to continue looping
            } else {
                //Decision point for next step
                Relationship child;
                String nextRel;
                if (keyToInsert[lev] > nodeKey[lev]) {
                    child = (Relationship) current.get(INTERNAL_RIGHT);
                    nextRel = INTERNAL_RIGHT;
                } else {
                    child = (Relationship) current.get(INTERNAL_LEFT);
                    nextRel = INTERNAL_LEFT;
                }

                //If there is no node to the right, we create one and the game is over
                if (child == null || child.size() == 0) {
                    KDTree childNode = (KDTree) context.graph().newTypedNode(current.world(), current.time(), NAME);
                    childNode.setProperty(INTERNAL_KEY, Type.DOUBLE_ARRAY, keyToInsert);
                    childNode.setProperty(INTERNAL_VALUE, Type.RELATION, new long[]{valueToInsert.id()});
                    current.setProperty(nextRel, Type.RELATION, new long[]{childNode.id()});
                    root.setProperty(NUM_NODES, Type.INT, (Integer) root.get(NUM_NODES) + 1);
                    childNode.free();
                    return false;
                } else {
                    //Otherwise we need to prepare for the next while iteration
                    context.setGlobalVariable("next", nextRel);
                    context.setGlobalVariable("lev", (lev + 1) % dim);
                    return true;
                }
            }
        }

    }, traverse("{{next}}"));

    private static Task initFindNear() {
        Task reccursiveDown = newTask();

        reccursiveDown.then(new Action() {
            @Override
            public void eval(TaskContext context) {

                // 1. Load the variables and if kd is empty exit.

                Node node = context.resultAsNodes().get(0);
                if (node == null) {
                    context.continueTask();
                    return;
                }

//                node.graph().save(null);
                // System.out.println("A- "+node.id()+": "+node.graph().space().available());

                double[] pivot = (double[]) node.get(INTERNAL_KEY);

                //Global variable
                int dim = (int) context.variable("dim").get(0);
                double[] target = (double[]) context.variable("key").get(0);
                Distance distance = (Distance) context.variable("distance").get(0);

                //Local variables
                int lev = (int) context.variable("lev").get(0);
                HRect hr = (HRect) context.variable("hr").get(0);
                double max_dist_sqd = (double) context.variable("max_dist_sqd").get(0);

//                System.out.println("T1 " + node.id() + " lev " + lev);


                // 2. s := split field of kd
                int s = lev % dim;
                //System.out.println("T1 "+node.id()+ " lev "+lev+ " s "+s);

                // 3. pivot := dom-elt field of kd

                double pivot_to_target = distance.measure(pivot, target);

                // 4. Cut hr into to sub-hyperrectangles left-hr and right-hr.
                // The cut plane is through pivot and perpendicular to the s
                // dimension.
                HRect left_hr = hr; // optimize by not cloning
                HRect right_hr = (HRect) hr.clone();
                left_hr.max[s] = pivot[s];
                right_hr.min[s] = pivot[s];

                // 5. target-in-left := target_s <= pivot_s
                boolean target_in_left = target[s] < pivot[s];

                long[] nearer_kd;
                HRect nearer_hr;
                long[] further_kd;
                HRect further_hr;
                String nearer_st;
                String farther_st;

                // 6. if target-in-left then
                // 6.1. nearer-kd := left field of kd and nearer-hr := left-hr
                // 6.2. further-kd := right field of kd and further-hr := right-hr
                if (target_in_left) {
                    nearer_kd = (long[]) node.get(INTERNAL_LEFT);
                    nearer_st = INTERNAL_LEFT;
                    nearer_hr = left_hr;

                    further_kd = (long[]) node.get(INTERNAL_RIGHT);
                    further_hr = right_hr;
                    farther_st = INTERNAL_RIGHT;
                }
                //
                // 7. if not target-in-left then
                // 7.1. nearer-kd := right field of kd and nearer-hr := right-hr
                // 7.2. further-kd := left field of kd and further-hr := left-hr
                else {
                    nearer_kd = (long[]) node.get(INTERNAL_RIGHT);
                    nearer_hr = right_hr;
                    nearer_st = INTERNAL_RIGHT;

                    further_kd = (long[]) node.get(INTERNAL_LEFT);
                    further_hr = left_hr;
                    farther_st = INTERNAL_LEFT;
                }

                //define contextual variables for reccursivity:
                context.defineVariable("further_hr", further_hr);
                context.defineVariable("pivot_to_target", pivot_to_target);

                if (nearer_kd != null && nearer_kd.length != 0) {
                    context.defineVariable("near", nearer_st);
                    //The 3 variables to set for next round of reccursivity:
                    context.defineVariableForSubTask("hr", nearer_hr);
                    context.defineVariableForSubTask("max_dist_sqd", max_dist_sqd);
                    context.defineVariableForSubTask("lev", lev + 1);

                } else {
                    context.defineVariableForSubTask("near", context.newResult());  //stop the loop
                }

                if (further_kd != null && further_kd.length != 0) {
                    context.defineVariableForSubTask("far", farther_st);
                } else {
                    context.defineVariableForSubTask("far", context.newResult()); //stop the loop
                }

                context.continueTask();
            }
        })
                .isolatedSubTask(ifThen(new TaskFunctionConditional() {
                    @Override
                    public boolean eval(TaskContext context) {
                        return context.variable("near").size() > 0;
                    }
                }, traverse("{{near}}").isolatedSubTask(reccursiveDown)))

                .then(new Action() {
                    @Override
                    public void eval(TaskContext context) {

                        //Global variables
                        NearestNeighborList nnl = (NearestNeighborList) context.variable("nnl").get(0);
                        double[] target = (double[]) context.variable("key").get(0);
                        Distance distance = (Distance) context.variable("distance").get(0);

                        //Local variables
                        double max_dist_sqd = (double) context.variable("max_dist_sqd").get(0);
                        HRect further_hr = (HRect) context.variable("further_hr").get(0);
                        double pivot_to_target = (double) context.variable("pivot_to_target").get(0);
                        int lev = (int) context.variable("lev").get(0);
                        Node node = context.resultAsNodes().get(0);
//                System.out.println("T2 " + node.id() + " lev " + lev);


//                        node.graph().save(null);
                        //  System.out.println("B- "+node.id()+": "+node.graph().space().available());

                        double dist_sqd;
                        if (!nnl.isCapacityReached()) {
                            dist_sqd = Double.MAX_VALUE;
                        } else {
                            dist_sqd = nnl.getMaxPriority();
                        }

                        // 9. max-dist-sqd := minimum of max-dist-sqd and dist-sqd
                        double max_dist_sqd2 = Math.min(max_dist_sqd, dist_sqd);

                        // 10. A nearer point could only lie in further-kd if there were some
                        // part of further-hr within distance sqrt(max-dist-sqd) of
                        // target. If this is the case then
                        double[] closest = further_hr.closest(target);
                        if (distance.measure(closest, target) < max_dist_sqd) {

                            // 10.1 if (pivot-target)^2 < dist-sqd then
                            if (pivot_to_target < dist_sqd) {

                                // 10.1.2 dist-sqd = (pivot-target)^2
                                dist_sqd = pivot_to_target;
                                //System.out.println("T3 "+node.id()+" insert-> "+((long[]) (node.get(INTERNAL_VALUE)))[0]);
                                //System.out.println("INSTASK " + ((long[]) (node.get(INTERNAL_VALUE)))[0] + " id: "+node.id());
                                nnl.insert(((long[]) (node.get(INTERNAL_VALUE)))[0], dist_sqd);

                                // 10.1.3 max-dist-sqd = dist-sqd
                                // max_dist_sqd = dist_sqd;
                                if (nnl.isCapacityReached()) {
                                    max_dist_sqd2 = nnl.getMaxPriority();
                                } else {
                                    max_dist_sqd2 = Double.MAX_VALUE;
                                }
                            }

                            // 10.2 Recursively call Nearest Neighbor with parameters
                            // (further-kd, target, further-hr, max-dist_sqd),
                            // storing results in temp-nearest and temp-dist-sqd
                            //nnbr(further_kd, target, further_hr, max_dist_sqd, lev + 1, K, nnl);


                            //The 3 variables to set for next round of reccursivity:
                            context.defineVariableForSubTask("hr", further_hr);
                            context.defineVariableForSubTask("max_dist_sqd", max_dist_sqd2);
                            context.defineVariableForSubTask("lev", lev + 1);


                            context.defineVariable("continueFar", true);

                        } else {
                            context.defineVariable("continueFar", false);
                        }
                        context.continueTask();
                    }
                })
                .isolatedSubTask(ifThen(new TaskFunctionConditional() {
                    @Override
                    public boolean eval(TaskContext context) {
                        return ((boolean) context.variable("continueFar").get(0) && context.variable("far").size() > 0); //Exploring the far depends also on the distance
                    }
                }, traverse("{{far}}").isolatedSubTask(reccursiveDown)));


        return reccursiveDown;
    }

    //Static tasks to manage insert and find nearest neighbours
    //find nearest N neighbours task
    private static Task nearestTask = initFindNear();


    public KDTree(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    private static final Enforcer enforcer = new Enforcer()
            .asPositiveDouble(DISTANCE_THRESHOLD);

    @Override
    public void setProperty(String propertyName, byte propertyType, Object propertyValue) {
        enforcer.check(propertyName, propertyType, propertyValue);
        super.setProperty(propertyName, propertyType, propertyValue);
    }


    private Distance getDistance(NodeState state) {
        int d = state.getFromKeyWithDefault(DISTANCE_TYPE, DISTANCE_TYPE_DEF);
        Distance distance;
        if (d == DistanceEnum.EUCLIDEAN) {
            distance = new EuclideanDistance();
        } else if (d == DistanceEnum.GAUSSIAN) {
            double[] precision = (double[]) state.getFromKey(GAUSSIAN_PRECISION);
            if (precision == null) {
                throw new RuntimeException("covariance of gaussian distances cannot be null");
            }
            distance = new GaussianDistance(precision);
        } else {
            throw new RuntimeException("Unknown distance code metric");
        }
        return distance;
    }


    public void insert(final double[] key, final Node value, final Callback<Boolean> callback) {
        NodeState state = unphasedState();
        final int dim = state.getFromKeyWithDefault(INTERNAL_DIM, key.length);
        final double err = state.getFromKeyWithDefault(DISTANCE_THRESHOLD, DISTANCE_THRESHOLD_DEF);

        if (key.length != dim) {
            throw new RuntimeException("Key size should always be the same");
        }
        Distance distance = getDistance(state);

        TaskContext tc = insert.prepareWith(graph(), this, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                result.free();
                if (callback != null) {
                    callback.on(true);
                }
            }
        });

        TaskResult res = tc.newResult();
        res.add(key);

        //Set global variables
        tc.setGlobalVariable("key", res);
        tc.setGlobalVariable("value", value);
        tc.setGlobalVariable("root", this);
        tc.setGlobalVariable("distance", distance);
        tc.setGlobalVariable("dim", dim);
        tc.setGlobalVariable("err", err);

        //Set local variables
        tc.defineVariable("lev", 0);
        insert.executeUsing(tc);
    }

    public void nearestWithinDistance(final double[] key, final Callback<Node> callback) {
        NodeState state = unphasedState();
        final double err = state.getFromKeyWithDefault(DISTANCE_THRESHOLD, DISTANCE_THRESHOLD_DEF);
        NearestNeighborList nnl = new NearestNeighborList(1);
        nearestN(key, 1, new Callback<Node[]>() {
            @Override
            public void on(Node[] result) {
                if (nnl.getBestDistance() <= err) {
                    long res = nnl.getHighest();

                    graph().lookup(world(), time(), res, new Callback<Node>() {
                        @Override
                        public void on(Node result) {
                            callback.on(result);
                        }
                    });
                } else {
                    callback.on(null);
                }
            }
        });

    }


    public void nearestN(final double[] key, final int n, final Callback<Node[]> callback) {
        NodeState state = unphasedState();
        final int dim = state.getFromKeyWithDefault(INTERNAL_DIM, key.length);

        if (key.length != dim) {
            throw new RuntimeException("Key size should always be the same");
        }

        // initial call is with infinite hyper-rectangle and max distance
        HRect hr = HRect.infiniteHRect(key.length);
        double max_dist_sqd = Double.MAX_VALUE;

        final NearestNeighborList nnl = new NearestNeighborList(n);
        Distance distance = getDistance(state);


        TaskContext tc = nearestTask.prepareWith(graph(), this, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {

                //ToDo replace by lookupAll later
                long[] res = nnl.getAllNodes();

                Task lookupall = fromVar("res").foreach(lookup(String.valueOf(world()), String.valueOf(time()), "{{result}}"));
                TaskContext tc = lookupall.prepareWith(graph(), null, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        final Node[] finalres = new Node[result.size()];
                        for (int i = 0; i < result.size(); i++) {
                            finalres[i] = (Node) result.get(i);
                        }
                        callback.on(finalres);
                    }
                });

                TaskResult tr = tc.wrap(res);
                tc.addToGlobalVariable("res", tr);
                lookupall.executeUsing(tc);
            }
        });


        TaskResult res = tc.newResult();
        res.add(key);

        // (this, distance, key, hr, max_dist_sqd, 0, dim, err, nnl);

        tc.setGlobalVariable("key", res);
        tc.setGlobalVariable("distance", distance);
        tc.setGlobalVariable("dim", dim);
        tc.setGlobalVariable("nnl", nnl);

        tc.defineVariable("lev", 0);
        tc.defineVariable("hr", hr);
        tc.defineVariable("max_dist_sqd", max_dist_sqd);

        nearestTask.executeUsing(tc);
    }

}
