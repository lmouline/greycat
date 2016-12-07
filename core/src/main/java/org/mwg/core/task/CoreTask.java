package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.DeferCounterSync;
import org.mwg.Graph;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;

import java.util.Map;

public class CoreTask implements org.mwg.task.Task {

    private int insertCapacity = Constants.MAP_INITIAL_CAPACITY;
    public Action[] actions = new Action[insertCapacity];
    public int insertCursor = 0;
    TaskHook[] _hooks = null;

    @Override
    public Task addHook(final TaskHook p_hook) {
        if (_hooks == null) {
            _hooks = new TaskHook[1];
            _hooks[0] = p_hook;
        } else {
            TaskHook[] new_hooks = new TaskHook[_hooks.length + 1];
            System.arraycopy(_hooks, 0, new_hooks, 0, _hooks.length);
            new_hooks[_hooks.length] = p_hook;
            _hooks = new_hooks;
        }
        return this;
    }

    @Override
    public final Task then(Action nextAction) {
        if (insertCapacity == insertCursor) {
            Action[] new_actions = new Action[insertCapacity * 2];
            System.arraycopy(actions, 0, new_actions, 0, insertCapacity);
            actions = new_actions;
            insertCapacity = insertCapacity * 2;
        }
        actions[insertCursor] = nextAction;
        insertCursor++;
        return this;
    }

    @Override
    public final Task thenDo(ActionFunction nextActionFunction) {
        return then(new CF_ActionThenDo(nextActionFunction));
    }

    @Override
    public Task doWhile(Task task, ConditionalFunction cond) {
        return then(new CF_ActionDoWhile(task, cond));
    }

    @Override
    public Task loop(String from, String to, Task subTask) {
        return then(new CF_ActionLoop(from, to, subTask));
    }

    @Override
    public Task loopPar(String from, String to, Task subTask) {
        return then(new CF_ActionLoopPar(from, to, subTask));
    }

    @Override
    public Task forEach(Task subTask) {
        return then(new CF_ActionForEach(subTask));
    }

    @Override
    public Task forEachPar(Task subTask) {
        return then(new CF_ActionForEachPar(subTask));
    }

    @Override
    public Task flatMap(Task subTask) {
        return then(new CF_ActionFlatMap(subTask));
    }

    @Override
    public Task flatMapPar(Task subTask) {
        return then(new CF_ActionFlatMapPar(subTask));
    }

    @Override
    public Task ifThen(ConditionalFunction cond, Task then) {
        return then(new CF_ActionIfThen(cond, then));
    }

    @Override
    public Task ifThenElse(ConditionalFunction cond, Task thenSub, Task elseSub) {
        return then(new CF_ActionIfThenElse(cond, thenSub, elseSub));
    }

    @Override
    public Task whileDo(ConditionalFunction cond, Task then) {
        return then(new CF_ActionWhileDo(cond, then));
    }

    @Override
    public Task map(Task... subTasks) {
        then(new CF_ActionMap(subTasks));
        return this;
    }

    @Override
    public Task mapPar(Task... subTasks) {
        then(new CF_ActionMapPar(subTasks));
        return this;
    }

    @Override
    public Task isolate(Task subTask) {
        then(new CF_ActionIsolate(subTask));
        return this;
    }

    @Override
    public void execute(final Graph graph, final Callback<TaskResult> callback) {
        executeWith(graph, null, callback);
    }

    @Override
    public TaskResult executeSync(final Graph graph) {
        DeferCounterSync waiter = graph.newSyncCounter(1);
        executeWith(graph, null, waiter.wrap());
        return (TaskResult) waiter.waitResult();
    }

    @Override
    public void executeWith(final Graph graph, final Object initial, final Callback<TaskResult> callback) {
        if (insertCursor > 0) {
            final TaskResult initalRes;
            if (initial instanceof CoreTaskResult) {
                initalRes = ((TaskResult) initial).clone();
            } else {
                initalRes = new CoreTaskResult(initial, true);
            }
            final CoreTaskContext context = new CoreTaskContext(this, _hooks, null, initalRes, graph, callback);
            graph.scheduler().dispatch(SchedulerAffinity.SAME_THREAD, new Job() {
                @Override
                public void run() {
                    context.execute();
                }
            });
        } else {
            if (callback != null) {
                callback.on(Actions.emptyResult());
            }
        }
    }

    @Override
    public TaskContext prepare(Graph graph, Object initial, Callback<TaskResult> callback) {
        final TaskResult initalRes;
        if (initial instanceof CoreTaskResult) {
            initalRes = ((TaskResult) initial).clone();
        } else {
            initalRes = new CoreTaskResult(initial, true);
        }
        return new CoreTaskContext(this, _hooks, null, initalRes, graph, callback);
    }

    @Override
    public void executeUsing(final TaskContext preparedContext) {
        if (insertCursor > 0) {
            preparedContext.graph().scheduler().dispatch(SchedulerAffinity.SAME_THREAD, new Job() {
                @Override
                public void run() {
                    ((CoreTaskContext) preparedContext).execute();
                }
            });
        } else {
            CoreTaskContext casted = (CoreTaskContext) preparedContext;
            if (casted._callback != null) {
                casted._callback.on(Actions.emptyResult());
            }
        }
    }

    @Override
    public void executeFrom(final TaskContext parentContext, final TaskResult initial, byte affinity, final Callback<TaskResult> callback) {
        if (insertCursor > 0) {
            TaskHook[] aggregatedHooks = null;
            if (parentContext != null) {
                aggregatedHooks = ((CoreTaskContext) parentContext)._hooks;
            }
            if (_hooks != null) {
                if (aggregatedHooks == null) {
                    aggregatedHooks = _hooks;
                } else {
                    TaskHook[] temp_hooks = new TaskHook[aggregatedHooks.length + _hooks.length];
                    System.arraycopy(aggregatedHooks, 0, temp_hooks, 0, aggregatedHooks.length);
                    System.arraycopy(_hooks, 0, temp_hooks, aggregatedHooks.length, _hooks.length);
                    aggregatedHooks = temp_hooks;
                }
            }
            final CoreTaskContext context = new CoreTaskContext(this, aggregatedHooks, parentContext, initial.clone(), parentContext.graph(), callback);
            parentContext.graph().scheduler().dispatch(affinity, new Job() {
                @Override
                public void run() {
                    context.execute();
                }
            });
        } else {
            if (callback != null) {
                callback.on(Actions.emptyResult());
            }
        }
    }

    @Override
    public void executeFromUsing(TaskContext parentContext, TaskResult initial, byte affinity, Callback<TaskContext> contextInitializer, Callback<TaskResult> callback) {
        if (insertCursor > 0) {
            TaskHook[] aggregatedHooks = null;
            if (parentContext != null) {
                aggregatedHooks = ((CoreTaskContext) parentContext)._hooks;
            }
            if (_hooks != null) {
                if (aggregatedHooks == null) {
                    aggregatedHooks = _hooks;
                } else {
                    TaskHook[] temp_hooks = new TaskHook[aggregatedHooks.length + _hooks.length];
                    System.arraycopy(aggregatedHooks, 0, temp_hooks, 0, aggregatedHooks.length);
                    System.arraycopy(_hooks, 0, temp_hooks, aggregatedHooks.length, _hooks.length);
                    aggregatedHooks = temp_hooks;
                }
            }
            final CoreTaskContext context = new CoreTaskContext(this, aggregatedHooks, parentContext, initial.clone(), parentContext.graph(), callback);
            if (contextInitializer != null) {
                contextInitializer.on(context);
            }
            parentContext.graph().scheduler().dispatch(affinity, new Job() {
                @Override
                public void run() {
                    context.execute();
                }
            });
        } else {
            if (callback != null) {
                callback.on(Actions.emptyResult());
            }
        }
    }

    @Override
    public Task parse(final String flat) {
        if (flat == null) {
            throw new RuntimeException("flat should not be null");
        }
        int cursor = 0;
        int flatSize = flat.length();
        int previous = 0;
        String actionName = null;
        boolean isClosed = false;
        boolean isEscaped = false;
        while (cursor < flatSize) {
            char current = flat.charAt(cursor);
            switch (current) {
                case '\'':
                    isEscaped = true;
                    while (cursor < flatSize) {
                        if (flat.charAt(cursor) == '\'') {
                            break;
                        }
                        cursor++;
                    }
                    break;
                case Constants.TASK_SEP:
                    if (!isClosed) {
                        String getName = flat.substring(previous, cursor);
                        then(new ActionPlugin("traverse", getName));//default action
                    }
                    actionName = null;
                    isEscaped = false;
                    previous = cursor + 1;
                    break;
                case Constants.TASK_PARAM_OPEN:
                    actionName = flat.substring(previous, cursor);
                    previous = cursor + 1;
                    break;
                case Constants.TASK_PARAM_CLOSE:
                    //ADD LAST PARAM
                    String extracted;
                    if (isEscaped) {
                        extracted = flat.substring(previous + 1, cursor - 1);
                    } else {
                        extracted = flat.substring(previous, cursor);
                    }
                    then(new ActionPlugin(actionName, extracted));
                    actionName = null;
                    previous = cursor + 1;
                    isClosed = true;
                    //ADD TASK
                    break;
            }
            cursor++;
        }
        if (!isClosed) {
            String getName = flat.substring(previous, cursor);
            if (getName.length() > 0) {
                then(new ActionPlugin("traverse", getName));//default action
            }
        }
        return this;
    }

    public static void fillDefault(Map<String, TaskActionFactory> registry) {
        registry.put("travelInWorld", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("travelInWorld action need one parameter");
                }
                return new ActionTravelInWorld(params[0]);
            }
        });
        registry.put("travelInTime", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("travelInTime action need one parameter");
                }
                return new ActionTravelInTime(params[0]);
            }
        });
        registry.put("defineAsGlobalVar", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("defineAsGlobalVar action need one parameter");
                }
                return new ActionDefineAsVar(params[0], true);
            }
        });
        registry.put("defineAsVar", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("defineAsVar action need one parameter");
                }
                return new ActionDefineAsVar(params[0], false);
            }
        });
        registry.put("declareGlobalVar", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("declareGlobalVar action need one parameter");
                }
                return new ActionDeclareGlobalVar(params[0]);
            }
        });
        registry.put("declareVar", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("declareVar action need one parameter");
                }
                return new ActionDeclareVar(params[0]);
            }
        });
        registry.put("readVar", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("readVar action need one parameter");
                }
                return new ActionReadVar(params[0]);
            }
        });
        registry.put("setAsVar", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("setAsVar action need one parameter");
                }
                return new ActionSetAsVar(params[0]);
            }
        });
        registry.put("addToVar", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("addToVar action need one parameter");
                }
                return new ActionAddToVar(params[0]);
            }
        });

        //TODO


        registry.put("traverse", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length < 1) {
                    throw new RuntimeException("traverse action needs at least one parameter. Received:" + params.length);
                }
                final String getName = params[0];
                final String[] getParams = new String[params.length - 1];
                if (params.length > 1) {
                    System.arraycopy(params, 1, getParams, 0, params.length - 1);
                }
                return new ActionTraverseOrAttribute(getName, getParams);
            }
        });
        registry.put("attribute", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length == 0) {
                    throw new RuntimeException("attribute action need one parameter");
                }
                final String getName = params[0];
                final String[] getParams = new String[params.length - 1];
                if (params.length > 1) {
                    System.arraycopy(params, 1, getParams, 0, params.length - 1);
                }
                return new ActionTraverseOrAttribute(getName, getParams);
            }
        });

        registry.put("executeExpression", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("executeExpression action need one parameter");
                }
                return new ActionExecuteExpression(params[0]);
            }
        });
        registry.put("readGlobalIndex", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length < 1) {
                    throw new RuntimeException("readGlobalIndex action needs at least one parameter. Received:" + params.length);
                }
                final String indexName = params[0];
                final String[] queryParams = new String[params.length - 1];
                if (params.length > 1) {
                    System.arraycopy(params, 1, queryParams, 0, params.length - 1);
                }
                return new ActionReadGlobalIndex(indexName, queryParams);
            }
        });
        registry.put("with", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException("with action needs two parameters. Received:" + params.length);
                }
                return new ActionWith(params[0], params[1]);
            }
        });
        registry.put("without", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException("without action needs two parameters. Received:" + params.length);
                }
                return new ActionWithout(params[0], params[1]);
            }
        });
    }


    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        //todo DAG in tasks are not managed
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] instanceof CF_ActionMap) {
                res.append(((CF_ActionMap) actions[i]).serialize());
            } else {
                res.append(actions[i]);
            }
        }

        return res.toString();
    }
}
