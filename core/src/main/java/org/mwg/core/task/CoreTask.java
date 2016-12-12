package org.mwg.core.task;

import org.mwg.*;
import org.mwg.core.CoreConstants;
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
    public Task parse(final String flat, final Graph graph) {
        if (flat == null) {
            throw new RuntimeException("flat should not be null");
        }
        int cursor = 0;
        int flatSize = flat.length();
        int previous = 0;
        String actionName = null;
        boolean isClosed = false;
        boolean isEscaped = false;

        //Param storage
        int paramsCapacity = 0;
        String[] params = null;
        int paramsIndex = 0;

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
                        } else {
                            previousBackS = false;
                        }
                        cursor++;
                    }
                    break;
                case Constants.TASK_SEP:
                    if (!isClosed) {
                        final String getName = flat.substring(previous, cursor);
                        then(new ActionTraverseOrAttribute(false, true, getName));//default action
                    }
                    actionName = null;
                    isEscaped = false;
                    previous = cursor + 1;
                    paramsCapacity = 0;
                    params = null;
                    paramsIndex = 0;
                    isClosed = false;
                    break;
                case Constants.TASK_PARAM_OPEN:
                    actionName = flat.substring(previous, cursor);
                    previous = cursor + 1;
                    break;
                case Constants.TASK_PARAM_CLOSE:
                    //ADD LAST PARAM
                    String lastParamExtracted;
                    if (isEscaped) {
                        lastParamExtracted = flat.substring(previous + 1, cursor - 1);
                    } else {
                        lastParamExtracted = flat.substring(previous, cursor);
                    }
                    if (lastParamExtracted.length() > 0) {
                        if ((paramsIndex + 1) != paramsCapacity) {
                            String[] newParams = new String[paramsIndex + 1];
                            if (params != null) {
                                System.arraycopy(params, 0, newParams, 0, paramsIndex);
                            }
                            params = newParams;
                            paramsCapacity = paramsIndex + 1;
                        }
                        params[paramsIndex] = lastParamExtracted;
                        paramsIndex++;
                    } else {
                        //shrink params
                        if (paramsIndex < paramsCapacity) {
                            String[] shrinked = new String[paramsIndex];
                            System.arraycopy(params, 0, shrinked, 0, paramsIndex);
                            params = shrinked;
                        }
                    }
                    if (graph == null) {
                        then(new ActionNamed(actionName, params));
                    } else {
                        final TaskActionFactory factory = graph.taskAction(actionName);
                        if (factory == null) {
                            throw new RuntimeException("Parse error, unknown action : " + actionName);
                        }
                        then(factory.create(params));
                    }
                    actionName = null;
                    previous = cursor + 1;
                    isClosed = true;
                    //ADD TASK
                    break;
                case Constants.QUERY_SEP:
                    String paramExtracted;
                    if (isEscaped) {
                        paramExtracted = flat.substring(previous + 1, cursor - 1);
                    } else {
                        paramExtracted = flat.substring(previous, cursor);
                    }
                    if (paramExtracted.length() > 0) {
                        if (paramsIndex >= paramsCapacity) {
                            int newParamsCapacity = paramsCapacity * 2;
                            if (newParamsCapacity == 0) {
                                newParamsCapacity = CoreConstants.MAP_INITIAL_CAPACITY;
                            }
                            String[] newParams = new String[newParamsCapacity];
                            if (params != null) {
                                System.arraycopy(params, 0, newParams, 0, paramsCapacity);
                            }
                            params = newParams;
                            paramsCapacity = newParamsCapacity;
                        }
                        params[paramsIndex] = paramExtracted;
                        paramsIndex++;
                    }
                    previous = cursor + 1;
                    break;
            }
            cursor++;
        }
        if (!isClosed) {
            final String getName = flat.substring(previous, cursor);
            if (getName.length() > 0) {
                if (actionName != null) {
                    if (graph == null) {
                        then(new ActionNamed(actionName, params));
                    } else {
                        final TaskActionFactory factory = graph.taskAction(actionName);
                        if (factory == null) {
                            throw new RuntimeException("Parse error, unknown action : " + actionName);
                        }
                        final String[] singleParam = new String[1];
                        singleParam[0] = getName;
                        then(factory.create(singleParam));
                    }
                } else {
                    then(new ActionTraverseOrAttribute(false, true, getName));//default action
                }

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
        registry.put(ActionNames.TRAVEL_IN_WORLD, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.TRAVEL_IN_WORLD + " action need one parameter");
                }
                return new ActionTravelInWorld(params[0]);
            }
        });
        registry.put(ActionNames.TRAVEL_IN_TIME, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.TRAVEL_IN_TIME + " action need one parameter");
                }
                return new ActionTravelInTime(params[0]);
            }
        });
        registry.put(ActionNames.DEFINE_AS_GLOBAL_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.DEFINE_AS_GLOBAL_VAR + " action need one parameter");
                }
                return new ActionDefineAsVar(params[0], true);
            }
        });
        registry.put(ActionNames.DEFINE_AS_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.DEFINE_AS_VAR + " action need one parameter");
                }
                return new ActionDefineAsVar(params[0], false);
            }
        });
        registry.put(ActionNames.DECLARE_GLOBAL_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.DECLARE_GLOBAL_VAR + " action need one parameter");
                }
                return new ActionDeclareVar(true, params[0]);
            }
        });
        registry.put(ActionNames.DECLARE_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.DECLARE_VAR + " action need one parameter");
                }
                return new ActionDeclareVar(false, params[0]);
            }
        });
        registry.put(ActionNames.READ_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.READ_VAR + " action need one parameter");
                }
                return new ActionReadVar(params[0]);
            }
        });
        registry.put(ActionNames.SET_AS_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.SET_AS_VAR + " action need one parameter");
                }
                return new ActionSetAsVar(params[0]);
            }
        });
        registry.put(ActionNames.ADD_TO_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("addToVar action need one parameter");
                }
                return new ActionAddToVar(params[0]);
            }
        });
        registry.put(ActionNames.TRAVERSE, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length < 1) {
                    throw new RuntimeException(ActionNames.TRAVERSE + " action needs at least one parameter. Received:" + params.length);
                }
                final String getName = params[0];
                final String[] getParams = new String[params.length - 1];
                if (params.length > 1) {
                    System.arraycopy(params, 1, getParams, 0, params.length - 1);
                }
                return new ActionTraverseOrAttribute(false, false, getName, getParams);
            }
        });
        registry.put(ActionNames.ATTRIBUTE, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length == 0) {
                    throw new RuntimeException(ActionNames.ATTRIBUTE + " action need one parameter");
                }
                final String getName = params[0];
                final String[] getParams = new String[params.length - 1];
                if (params.length > 1) {
                    System.arraycopy(params, 1, getParams, 0, params.length - 1);
                }
                return new ActionTraverseOrAttribute(true, false, getName, getParams);
            }
        });

        registry.put(ActionNames.EXECUTE_EXPRESSION, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.EXECUTE_EXPRESSION + " action need one parameter");
                }
                return new ActionExecuteExpression(params[0]);
            }
        });
        registry.put(ActionNames.READ_GLOBAL_INDEX, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length < 1) {
                    throw new RuntimeException(ActionNames.READ_GLOBAL_INDEX + " action needs at least one parameter. Received:" + params.length);
                }
                final String indexName = params[0];
                final String[] queryParams = new String[params.length - 1];
                if (params.length > 1) {
                    System.arraycopy(params, 1, queryParams, 0, params.length - 1);
                }
                return new ActionReadGlobalIndex(indexName, queryParams);
            }
        });
        registry.put(ActionNames.WITH, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException(ActionNames.WITH + " action needs two parameters. Received:" + params.length);
                }
                return new ActionWith(params[0], params[1]);
            }
        });
        registry.put(ActionNames.WITHOUT, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException(ActionNames.WITHOUT + " action needs two parameters. Received:" + params.length);
                }
                return new ActionWithout(params[0], params[1]);
            }
        });
        registry.put(ActionNames.SCRIPT, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.SCRIPT + " action needs one parameter. Received:" + params.length);
                }
                return new ActionScript(params[0]);
            }
        });
        registry.put(ActionNames.CREATE_NODE, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params != null && params.length != 0) {
                    throw new RuntimeException(ActionNames.CREATE_NODE + " action needs zero parameter. Received:" + params.length);
                }
                return new ActionCreateNode(null);
            }
        });
        registry.put(ActionNames.CREATE_TYPED_NODE, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.CREATE_TYPED_NODE + " action needs one parameter. Received:" + params.length);
                }
                return new ActionCreateNode(params[0]);
            }
        });
        registry.put(ActionNames.PRINT, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.PRINT + " action needs one parameter. Received:" + params.length);
                }
                return new ActionPrint(params[0], false);
            }
        });
        registry.put(ActionNames.PRINTLN, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params == null || params.length != 1) {
                    if (params != null) {
                        throw new RuntimeException(ActionNames.PRINTLN + " action needs one parameter. Received:" + params.length);
                    } else {
                        throw new RuntimeException(ActionNames.PRINTLN + " action needs one parameter. Received: 0");
                    }
                }
                return new ActionPrint(params[0], true);
            }
        });
        registry.put(ActionNames.SET_ATTRIBUTE, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 3) {
                    throw new RuntimeException(ActionNames.SET_ATTRIBUTE + " action needs three parameters. Received:" + params.length);
                }
                return new ActionSetAttribute(params[0], Type.typeFromName(params[1]), params[2], false);
            }
        });
        registry.put(ActionNames.FORCE_ATTRIBUTE, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 3) {
                    throw new RuntimeException(ActionNames.FORCE_ATTRIBUTE + " action needs three parameters. Received:" + params.length);
                }
                return new ActionSetAttribute(params[0], Type.typeFromName(params[1]), params[2], true);
            }
        });
        registry.put(ActionNames.ATTRIBUTES, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 0) {
                    throw new RuntimeException(ActionNames.ATTRIBUTES + " action needs no parameter. Received:" + params.length);
                }
                return new ActionAttributes((byte) -1);
            }
        });
        registry.put(ActionNames.ATTRIBUTES_WITH_TYPE, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.ATTRIBUTES_WITH_TYPE + " action needs one parameter. Received:" + params.length);
                }
                return new ActionAttributes(Type.typeFromName(params[0]));
            }
        });

    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        //todo DAG in tasks are not managed
        for (int i = 0; i < actions.length; i++) {
            res.append(Constants.TASK_SEP);
            actions[i].serialize(res);
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
    public Task attributesWithType(byte filterType) {
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
