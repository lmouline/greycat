package org.mwg.core.task;

import org.mwg.*;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
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
    public Task doWhileScript(Task task, String condScript) {
        return then(new CF_ActionDoWhile(task, condFromScript(condScript)));
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
    public Task ifThenScript(String condScript, Task then) {
        return then(new CF_ActionIfThen(condFromScript(condScript), then));
    }

    @Override
    public Task ifThenElse(ConditionalFunction cond, Task thenSub, Task elseSub) {
        return then(new CF_ActionIfThenElse(cond, thenSub, elseSub));
    }

    @Override
    public Task ifThenElseScript(String condScript, Task thenSub, Task elseSub) {
        return then(new CF_ActionIfThenElse(condFromScript(condScript), thenSub, elseSub));
    }

    @Override
    public Task whileDo(ConditionalFunction cond, Task task) {
        return then(new CF_ActionWhileDo(cond, task));
    }

    @Override
    public Task whileDoScript(String condScript, Task task) {
        return then(new CF_ActionWhileDo(condFromScript(condScript), task));
    }

    @Override
    public Task mapReduce(Task... subTasks) {
        then(new CF_ActionMap(subTasks));
        return this;
    }

    @Override
    public Task mapReducePar(Task... subTasks) {
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
            final char current = flat.charAt(cursor);
            switch (current) {
                case '\"':
                case '\'':
                    isEscaped = true;
                    cursor++;
                    boolean previousBackS = false;
                    while (cursor < flatSize) {
                        char loopChar = flat.charAt(cursor);
                        if (loopChar == '\\') {
                            previousBackS = true;
                        } else if (current == loopChar && !previousBackS) {
                            break;
                        }
                        cursor++;
                    }
                    break;
                case Constants.TASK_SEP:
                    if (!isClosed) {
                        final String getName = flat.substring(previous, cursor);
                        then(new ActionTraverseOrAttribute(getName));//default action
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
                    then(new ActionNamed(actionName, extracted));
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
                then(new ActionNamed("traverse", getName));//default action
            }
        }
        return this;
    }

    private static ConditionalFunction condFromScript(final String script) {
        return new ConditionalFunction() {
            @Override
            public boolean eval(TaskContext context) {
                return executeScript(script, context);
            }
        };
    }

    /**
     * @native ts
     * var print = console.log;
     * var println = console.log;
     * var ctx = context;
     * return eval(script);
     */
    private static boolean executeScript(String script, TaskContext context) {
        ScriptContext scriptCtx = new SimpleScriptContext();
        scriptCtx.setAttribute("ctx", context, ScriptContext.ENGINE_SCOPE);
        try {
            return (boolean) TaskHelper.SCRIPT_ENGINE.eval(script, scriptCtx);
        } catch (ScriptException | ClassCastException e) {
            e.printStackTrace();
            return false;
        }
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
        registry.put("script", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("script action needs one parameter. Received:" + params.length);
                }
                return new ActionScript(params[0]);
            }
        });

        registry.put("createNode", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("script action needs zero parameter. Received:" + params.length);
                }
                return new ActionCreateNode(null);
            }
        });

        registry.put("print", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("print action needs one parameter. Received:" + params.length);
                }
                return new ActionPrint(params[0],false);
            }
        });

        registry.put("println", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("println action needs one parameter. Received:" + params.length);
                }
                return new ActionPrint(params[0],true);
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

    @Override
    public Task travelInWorld(String world) {
        return then(Actions.travelInWorld(world));
    }

    @Override
    public Task travelInTime(String time) {
        return then(Actions.travelInTime(time));
    }

    @Override
    public Task inject(Object input) {
        return then(Actions.inject(input));
    }

    @Override
    public Task defineAsGlobalVar(String name) {
        return then(Actions.defineAsGlobalVar(name));
    }

    @Override
    public Task defineAsVar(String name) {
        return then(Actions.defineAsVar(name));
    }

    @Override
    public Task declareGlobalVar(String name) {
        return then(Actions.declareGlobalVar(name));
    }

    @Override
    public Task declareVar(String name) {
        return then(Actions.declareVar(name));
    }

    @Override
    public Task readVar(String name) {
        return then(Actions.readVar(name));
    }

    @Override
    public Task setAsVar(String name) {
        return then(Actions.setAsVar(name));
    }

    @Override
    public Task addToVar(String name) {
        return then(Actions.addToVar(name));
    }

    @Override
    public Task setAttribute(String name, byte type, String value) {
        return then(Actions.setAttribute(name, type, value));
    }

    @Override
    public Task forceAttribute(String name, byte type, String value) {
        return then(Actions.forceAttribute(name, type, value));
    }

    @Override
    public Task remove(String name) {
        return then(Actions.remove(name));
    }

    @Override
    public Task attributes() {
        return then(Actions.attributes());
    }

    @Override
    public Task timepoints(String from, String to) {
        return then(Actions.timepoints(from, to));
    }

    @Override
    public Task attributesWithTypes(byte filterType) {
        return then(Actions.attributesWithTypes(filterType));
    }

    @Override
    public Task addVarToRelation(String relName, String varName, String... attributes) {
        return then(Actions.addVarToRelation(relName, varName, attributes));
    }

    @Override
    public Task removeVarFromRelation(String relName, String varFrom, String... attributes) {
        return then(Actions.removeVarFromRelation(relName, varFrom, attributes));
    }

    @Override
    public Task traverse(String name, String... params) {
        return then(Actions.traverse(name, params));
    }

    @Override
    public Task attribute(String name, String... params) {
        return then(Actions.attribute(name, params));
    }

    @Override
    public Task readGlobalIndex(String name, String... query) {
        return then(Actions.readGlobalIndex(name, query));
    }

    @Override
    public Task addToGlobalIndex(String name, String... attributes) {
        return then(Actions.addToGlobalIndex(name, attributes));
    }

    @Override
    public Task removeFromGlobalIndex(String name, String... attributes) {
        return then(Actions.removeFromGlobalIndex(name, attributes));
    }

    @Override
    public Task indexNames() {
        return then(Actions.indexNames());
    }

    @Override
    public Task selectWith(String name, String pattern) {
        return then(Actions.selectWith(name, pattern));
    }

    @Override
    public Task selectWithout(String name, String pattern) {
        return then(Actions.selectWithout(name, pattern));
    }

    @Override
    public Task select(TaskFunctionSelect filterFunction) {
        return then(Actions.select(filterFunction));
    }

    @Override
    public Task selectObject(TaskFunctionSelectObject filterFunction) {
        return then(Actions.selectObject(filterFunction));
    }

    @Override
    public Task selectScript(String script) {
        return then(Actions.selectScript(script));
    }

    @Override
    public Task print(String name) {
        return then(Actions.print(name));
    }

    @Override
    public Task println(String name) {
        return then(Actions.println(name));
    }

    @Override
    public Task executeExpression(String expression) {
        return then(Actions.executeExpression(expression));
    }

    @Override
    public Task createNode() {
        return then(Actions.createNode());
    }

    @Override
    public Task createTypedNode(String type) {
        return then(Actions.createTypedNode(type));
    }

    @Override
    public Task save() {
        return then(Actions.save());
    }

    @Override
    public Task script(String script) {
        return then(Actions.script(script));
    }

    @Override
    public Task lookup(String nodeId) {
        return then(Actions.lookup(nodeId));
    }

    @Override
    public Task lookupAll(String nodeIds) {
        return then(Actions.lookupAll(nodeIds));
    }

    @Override
    public Task clearResult() {
        return then(Actions.clearResult());
    }

    @Override
    public Task action(String name, String... params) {
        return then(Actions.action(name, params));
    }
}
