package org.mwg.core.task;

import org.mwg.*;
import org.mwg.core.CoreConstants;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.struct.Buffer;
import org.mwg.task.*;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CoreTask implements org.mwg.task.Task {

    private int insertCapacity = Constants.MAP_INITIAL_CAPACITY;
    public Action[] actions = new Action[insertCapacity];
    public int insertCursor = 0;
    TaskHook[] _hooks = null;


    @Override
    public final Task addHook(final TaskHook p_hook) {
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
        return then(new CF_ThenDo(nextActionFunction));
    }

    @Override
    public final Task doWhile(Task task, ConditionalFunction cond) {
        return then(new CF_DoWhile(task, cond, null));
    }

    @Override
    public final Task doWhileScript(Task task, String condScript) {
        return then(new CF_DoWhile(task, condFromScript(condScript), condScript));
    }

    @Override
    public final Task loop(String from, String to, Task subTask) {
        return then(new CF_Loop(from, to, subTask));
    }

    @Override
    public final Task loopPar(String from, String to, Task subTask) {
        return then(new CF_LoopPar(from, to, subTask));
    }

    @Override
    public final Task forEach(Task subTask) {
        return then(new CF_ForEach(subTask));
    }

    @Override
    public final Task forEachPar(Task subTask) {
        return then(new CF_ForEachPar(subTask));
    }

    @Override
    public final Task map(Task subTask) {
        return then(new CF_Map(subTask));
    }

    @Override
    public final Task mapPar(Task subTask) {
        return then(new CF_MapPar(subTask));
    }

    @Override
    public final Task ifThen(ConditionalFunction cond, Task then) {
        return then(new CF_IfThen(cond, then, null));
    }

    @Override
    public final Task ifThenScript(String condScript, Task then) {
        return then(new CF_IfThen(condFromScript(condScript), then, condScript));
    }

    @Override
    public final Task ifThenElse(ConditionalFunction cond, Task thenSub, Task elseSub) {
        return then(new CF_IfThenElse(cond, thenSub, elseSub, null));
    }

    @Override
    public final Task ifThenElseScript(String condScript, Task thenSub, Task elseSub) {
        return then(new CF_IfThenElse(condFromScript(condScript), thenSub, elseSub, condScript));
    }

    @Override
    public final Task whileDo(ConditionalFunction cond, Task task) {
        return then(new CF_WhileDo(cond, task, null));
    }

    @Override
    public final Task whileDoScript(String condScript, Task task) {
        return then(new CF_WhileDo(condFromScript(condScript), task, condScript));
    }

    @Override
    public final Task pipe(Task... subTasks) {
        return then(new CF_Pipe(subTasks));
    }

    @Override
    public final Task pipePar(Task... subTasks) {
        return then(new CF_PipePar(subTasks));
    }

    @Override
    public final Task isolate(Task subTask) {
        return then(new CF_Isolate(subTask));
    }

    @Override
    public final Task atomic(Task protectedTask, String... variablesToLock) {
        return then(new CF_Atomic(protectedTask, variablesToLock));
    }

    @Override
    public final void execute(final Graph graph, final Callback<TaskResult> callback) {
        executeWith(graph, null, callback);
    }

    @Override
    public final TaskResult executeSync(final Graph graph) {
        DeferCounterSync waiter = graph.newSyncCounter(1);
        executeWith(graph, null, waiter.wrap());
        return (TaskResult) waiter.waitResult();
    }

    @Override
    public final void executeWith(final Graph graph, final Object initial, final Callback<TaskResult> callback) {
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
    public final TaskContext prepare(Graph graph, Object initial, Callback<TaskResult> callback) {
        final TaskResult initalRes;
        if (initial instanceof CoreTaskResult) {
            initalRes = ((TaskResult) initial).clone();
        } else {
            initalRes = new CoreTaskResult(initial, true);
        }
        return new CoreTaskContext(this, _hooks, null, initalRes, graph, callback);
    }

    @Override
    public final void executeUsing(final TaskContext preparedContext) {
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
    public final void executeFrom(final TaskContext parentContext, final TaskResult initial, byte affinity, final Callback<TaskResult> callback) {
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
    public final void executeFromUsing(TaskContext parentContext, TaskResult initial, byte affinity, Callback<TaskContext> contextInitializer, Callback<TaskResult> callback) {
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
    public final Task loadFromBuffer(final Buffer buffer, final Graph graph) {
        return parse(org.mwg.utility.Base64.decodeToStringWithBounds(buffer, 0, buffer.length()), graph);
    }

    @Override
    public final Task saveToBuffer(Buffer buffer) {
        final String saved = toString();
        org.mwg.utility.Base64.encodeStringToBuffer(saved, buffer);
        return this;
    }

    @Override
    public final Task parse(final String flat, final Graph graph) {
        if (flat == null) {
            throw new RuntimeException("flat should not be null");
        }
        final Map<Integer, Task> contextTasks = new HashMap<Integer, Task>();
        sub_parse(new CoreTaskReader(flat, 0), graph, contextTasks);
        return this;
    }

    private void sub_parse(final CoreTaskReader reader, final Graph graph, final Map<Integer, Task> contextTasks) {
        int cursor = 0;
        int flatSize = reader.available();
        int previous = 0;
        String actionName = null;
        boolean isClosed = false;
        boolean isEscaped = false;
        //Param storage
        int paramsCapacity = 0;
        String[] params = null;
        int paramsIndex = 0;
        String previousTaskId = null;
        boolean subTaskMode = false;
        while (cursor < flatSize) {
            final char current = reader.charAt(cursor);
            switch (current) {
                case '\"':
                case '\'':
                    isEscaped = true;
                    cursor++;
                    boolean previousBackS = false;
                    while (cursor < flatSize) {
                        char loopChar = reader.charAt(cursor);
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
                        final String getName = reader.extract(previous, cursor).trim();
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
                case Constants.SUB_TASK_DECLR:
                    if (!isClosed) {
                        final String getName = reader.extract(previous, cursor).trim();
                        then(new ActionTraverseOrAttribute(false, true, getName));//default action
                    }
                    subTaskMode = true;
                    actionName = null;
                    isEscaped = false;
                    previous = cursor + 1;
                    paramsCapacity = 0;
                    params = null;
                    paramsIndex = 0;
                    break;
                case Constants.TASK_PARAM_OPEN:
                    actionName = reader.extract(previous, cursor).trim();
                    previous = cursor + 1;
                    break;
                case Constants.TASK_PARAM_CLOSE:
                    //ADD LAST PARAM
                    String lastParamExtracted;
                    if (previousTaskId != null) {
                        lastParamExtracted = previousTaskId;
                        previousTaskId = null;
                    } else {
                        if (isEscaped) {
                            lastParamExtracted = reader.extract(previous + 1, cursor - 1);
                        } else {
                            lastParamExtracted = reader.extract(previous, cursor);
                        }
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
                            throw new RuntimeException("Parse error, unknown action |" + actionName + "|");
                        }
                        then(factory.create(params, contextTasks));
                    }
                    actionName = null;
                    previous = cursor + 1;
                    isClosed = true;
                    //ADD TASK
                    break;
                case Constants.TASK_PARAM_SEP:
                    String paramExtracted;
                    if (previousTaskId != null) {
                        paramExtracted = previousTaskId;
                        previousTaskId = null;
                    } else {
                        if (isEscaped) {
                            paramExtracted = reader.extract(previous + 1, cursor - 1);
                        } else {
                            paramExtracted = reader.extract(previous, cursor);
                        }
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
                    isEscaped = false;
                    break;
                case Constants.SUB_TASK_OPEN:
                    if (cursor > 0 && cursor + 1 < flatSize && reader.charAt(cursor + 1) != Constants.SUB_TASK_OPEN && reader.charAt(cursor - 1) != Constants.SUB_TASK_OPEN) {
                        CoreTaskReader subReader = reader.slice(cursor + 1);
                        CoreTask subTask;
                        if (subTaskMode) {
                            final String subTaskName = reader.extract(previous, cursor).trim();
                            final Integer subTaskID = TaskHelper.parseInt(subTaskName);
                            subTask = (CoreTask) contextTasks.get(subTaskID);
                        } else {
                            subTask = new CoreTask();
                        }
                        subTask.sub_parse(subReader, graph, contextTasks);
                        cursor = cursor + subReader.end() + 1;
                        previous = cursor + 1; //to skip the string param
                        Integer hash = subTask.hashCode();
                        contextTasks.put(hash, subTask);
                        previousTaskId = hash + "";
                    }
                    break;

                case Constants.SUB_TASK_CLOSE:
                    if (cursor > 0 && cursor + 1 < flatSize && reader.charAt(cursor + 1) != Constants.SUB_TASK_CLOSE && reader.charAt(cursor - 1) != Constants.SUB_TASK_CLOSE) {
                        reader.markend(cursor);
                        return;
                    }
                    break;
            }
            cursor++;
        }
        if (!isClosed) {
            final String getName = reader.extract(previous, flatSize);
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
                        then(factory.create(singleParam, contextTasks));
                    }
                } else {
                    then(new ActionTraverseOrAttribute(false, true, getName.trim()));//default action
                }

            }
        }
    }

    private static ConditionalFunction condFromScript(final String script) {
        return new ConditionalFunction() {
            @Override
            public boolean eval(TaskContext ctx) {
                return executeScript(script, ctx);
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
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.TRAVEL_IN_WORLD + " action need one parameter");
                }
                return new ActionTravelInWorld(params[0]);
            }
        });
        registry.put(ActionNames.TRAVEL_IN_TIME, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.TRAVEL_IN_TIME + " action need one parameter");
                }
                return new ActionTravelInTime(params[0]);
            }
        });
        registry.put(ActionNames.DEFINE_AS_GLOBAL_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.DEFINE_AS_GLOBAL_VAR + " action need one parameter");
                }
                return new ActionDefineAsVar(params[0], true);
            }
        });
        registry.put(ActionNames.DEFINE_AS_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.DEFINE_AS_VAR + " action need one parameter");
                }
                return new ActionDefineAsVar(params[0], false);
            }
        });
        registry.put(ActionNames.DECLARE_GLOBAL_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.DECLARE_GLOBAL_VAR + " action need one parameter");
                }
                return new ActionDeclareVar(true, params[0]);
            }
        });
        registry.put(ActionNames.DECLARE_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.DECLARE_VAR + " action need one parameter");
                }
                return new ActionDeclareVar(false, params[0]);
            }
        });
        registry.put(ActionNames.READ_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.READ_VAR + " action need one parameter");
                }
                return new ActionReadVar(params[0]);
            }
        });
        registry.put(ActionNames.SET_AS_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.SET_AS_VAR + " action need one parameter");
                }
                return new ActionSetAsVar(params[0]);
            }
        });
        registry.put(ActionNames.ADD_TO_VAR, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException("addToVar action need one parameter");
                }
                return new ActionAddToVar(params[0]);
            }
        });
        registry.put(ActionNames.TRAVERSE, new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
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
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
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
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.EXECUTE_EXPRESSION + " action need one parameter");
                }
                return new ActionExecuteExpression(params[0]);
            }
        });
        registry.put(ActionNames.READ_GLOBAL_INDEX, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
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
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 2) {
                    throw new RuntimeException(ActionNames.WITH + " action needs two parameters. Received:" + params.length);
                }
                return new ActionWith(params[0], params[1]);
            }
        });
        registry.put(ActionNames.WITHOUT, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 2) {
                    throw new RuntimeException(ActionNames.WITHOUT + " action needs two parameters. Received:" + params.length);
                }
                return new ActionWithout(params[0], params[1]);
            }
        });
        registry.put(ActionNames.SCRIPT, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.SCRIPT + " action needs one parameter. Received:" + params.length);
                }
                return new ActionScript(params[0], false);
            }
        });
        registry.put(ActionNames.ASYNC_SCRIPT, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.SCRIPT + " action needs one parameter. Received:" + params.length);
                }
                return new ActionScript(params[0], true);
            }
        });
        registry.put(ActionNames.SELECT, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.SELECT + " action needs one parameter. Received:" + params.length);
                }
                return new ActionSelect(params[0], null);
            }
        });
        registry.put(ActionNames.CREATE_NODE, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params != null && params.length != 0) {
                    throw new RuntimeException(ActionNames.CREATE_NODE + " action needs zero parameter. Received:" + params.length);
                }
                return new ActionCreateNode(null);
            }
        });
        registry.put(ActionNames.CREATE_TYPED_NODE, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.CREATE_TYPED_NODE + " action needs one parameter. Received:" + params.length);
                }
                return new ActionCreateNode(params[0]);
            }
        });
        registry.put(ActionNames.PRINT, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.PRINT + " action needs one parameter. Received:" + params.length);
                }
                return new ActionPrint(params[0], false);
            }
        });
        registry.put(ActionNames.PRINTLN, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
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
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 3) {
                    throw new RuntimeException(ActionNames.SET_ATTRIBUTE + " action needs three parameters. Received:" + params.length);
                }
                return new ActionSetAttribute(params[0], Type.typeFromName(params[1]), params[2], false);
            }
        });
        registry.put(ActionNames.TIME_SENSITIVITY, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 2) {
                    throw new RuntimeException(ActionNames.TIME_SENSITIVITY + " action needs two parameters. Received:" + params.length);
                }
                return new ActionTimeSensitivity(Long.parseLong(params[0]), Long.parseLong(params[1]));
            }
        });
        registry.put(ActionNames.FORCE_ATTRIBUTE, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 3) {
                    throw new RuntimeException(ActionNames.FORCE_ATTRIBUTE + " action needs three parameters. Received:" + params.length);
                }
                return new ActionSetAttribute(params[0], Type.typeFromName(params[1]), params[2], true);
            }
        });
        registry.put(ActionNames.ATTRIBUTES, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 0) {
                    throw new RuntimeException(ActionNames.ATTRIBUTES + " action needs no parameter. Received:" + params.length);
                }
                return new ActionAttributes((byte) -1);
            }
        });
        registry.put(ActionNames.ATTRIBUTES_WITH_TYPE, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.ATTRIBUTES_WITH_TYPE + " action needs one parameter. Received:" + params.length);
                }
                return new ActionAttributes(Type.typeFromName(params[0]));
            }
        });
        registry.put(ActionNames.LOOP, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 3) {
                    throw new RuntimeException(ActionNames.LOOP + " action needs three parameters. Received:" + params.length);
                }
                final Task subTask = getOrCreate(contextTasks, params[2]);
                return new CF_Loop(params[0], params[1], subTask);
            }
        });
        registry.put(ActionNames.LOOP_PAR, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 3) {
                    throw new RuntimeException(ActionNames.LOOP_PAR + " action needs three parameters. Received:" + params.length);
                }
                final Task subTask = getOrCreate(contextTasks, params[2]);
                return new CF_LoopPar(params[0], params[1], subTask);
            }
        });
        registry.put(ActionNames.FOR_EACH, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.FOR_EACH + " action needs one parameters. Received:" + params.length);
                }
                final Task subTask = getOrCreate(contextTasks, params[0]);
                return new CF_ForEach(subTask);
            }
        });
        registry.put(ActionNames.FOR_EACH_PAR, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.FOR_EACH_PAR + " action needs one parameters. Received:" + params.length);
                }
                final Task subTask = getOrCreate(contextTasks, params[0]);
                return new CF_ForEachPar(subTask);
            }
        });
        registry.put(ActionNames.FLAT, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 0) {
                    throw new RuntimeException(ActionNames.FLAT + " action needs one parameters. Received:" + params.length);
                }
                return new ActionFlat();
            }
        });
        registry.put(ActionNames.MAP, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.MAP + " action needs one parameters. Received:" + params.length);
                }
                final Task subTask = getOrCreate(contextTasks, params[0]);
                return new CF_Map(subTask);
            }
        });
        registry.put(ActionNames.MAP_PAR, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.MAP_PAR + " action needs one parameters. Received:" + params.length);
                }
                final Task subTask = getOrCreate(contextTasks, params[0]);
                return new CF_MapPar(subTask);
            }
        });
        registry.put(ActionNames.PIPE, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                final Task[] subTasks = new Task[params.length];
                for (int i = 0; i < params.length; i++) {
                    subTasks[i] = getOrCreate(contextTasks, params[i]);
                }
                return new CF_Pipe(subTasks);
            }
        });
        registry.put(ActionNames.PIPE_PAR, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                final Task[] subTasks = new Task[params.length];
                for (int i = 0; i < params.length; i++) {
                    subTasks[i] = getOrCreate(contextTasks, params[i]);
                }
                return new CF_PipePar(subTasks);
            }
        });
        registry.put(ActionNames.DO_WHILE, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 2) {
                    throw new RuntimeException(ActionNames.DO_WHILE + " action needs two parameters. Received:" + params.length);
                }
                final Task subTask = getOrCreate(contextTasks, params[0]);
                final String script = params[1];
                return new CF_DoWhile(subTask, condFromScript(script), script);
            }
        });
        registry.put(ActionNames.WHILE_DO, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 2) {
                    throw new RuntimeException(ActionNames.DO_WHILE + " action needs two parameters. Received:" + params.length);
                }
                final String script = params[0];
                final Task subTask = getOrCreate(contextTasks, params[1]);
                return new CF_WhileDo(condFromScript(script), subTask, script);
            }
        });
        registry.put(ActionNames.ISOLATE, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 1) {
                    throw new RuntimeException(ActionNames.ISOLATE + " action needs three parameters. Received:" + params.length);
                }
                final Task subTask = getOrCreate(contextTasks, params[0]);
                return new CF_Isolate(subTask);
            }
        });
        registry.put(ActionNames.ATOMIC, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length < 1) {
                    throw new RuntimeException(ActionNames.ATOMIC + " action needs at least one parameters. Received:" + params.length);
                }
                final Task subTask = getOrCreate(contextTasks, params[0]);
                String[] variables = new String[params.length - 1];
                System.arraycopy(params, 1, variables, 0, params.length - 1);
                return new CF_Atomic(subTask, variables);
            }
        });
        registry.put(ActionNames.IF_THEN, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 2) {
                    throw new RuntimeException(ActionNames.IF_THEN + " action needs two parameters. Received:" + params.length);
                }
                final String script = params[0];
                final Task taskThen = getOrCreate(contextTasks, params[1]);
                return new CF_IfThen(condFromScript(script), taskThen, script);
            }
        });
        registry.put(ActionNames.IF_THEN_ELSE, new TaskActionFactory() {
            @Override
            public Action create(String[] params, Map<Integer, Task> contextTasks) {
                if (params.length != 3) {
                    throw new RuntimeException(ActionNames.IF_THEN_ELSE + " action three two parameters. Received:" + params.length);
                }
                final String script = params[0];
                final Task taskThen = getOrCreate(contextTasks, params[1]);
                final Task taskElse = getOrCreate(contextTasks, params[2]);
                return new CF_IfThenElse(condFromScript(script), taskThen, taskElse, script);
            }
        });
    }

    private static Task getOrCreate(Map<Integer, Task> contextTasks, String param) {
        Integer taskId = TaskHelper.parseInt(param);
        Task previous = contextTasks.get(taskId);
        if (previous == null) {
            previous = new CoreTask();
            contextTasks.put(taskId, previous);
        }
        return previous;
    }

    /**
     * @native ts
     * if(this['hashCodeCache'] === undefined){
     * this['hashCodeCache'] = Math.floor((Math.random() * 1000000000) + 1);
     * }
     * return this['hashCodeCache'];
     */
    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final String toString() {
        final StringBuilder res = new StringBuilder();
        final Map<Integer, Integer> dagCounters = new HashMap<Integer, Integer>();
        final Map<Integer, Task> dagCollector = new HashMap<Integer, Task>();
        deep_analyze(this, dagCounters, dagCollector);

        Set<Integer> keys = dagCounters.keySet();
        Integer[] flatKeys = keys.toArray(new Integer[keys.size()]);
        final Map<Integer, Integer> dagIDS = new HashMap<Integer, Integer>();
        for (int i = 0; i < flatKeys.length; i++) {
            Integer key = flatKeys[i];
            Integer counter = dagCounters.get(key);
            if (counter != null && counter > 1) {
                dagIDS.put(key, dagIDS.size());
            }
        }
        serialize(res, dagIDS);

        Set<Integer> set_dagIDS = dagIDS.keySet();
        Integer[] flatDagIDS = set_dagIDS.toArray(new Integer[set_dagIDS.size()]);
        for (int i = 0; i < flatDagIDS.length; i++) {
            Integer key = flatDagIDS[i];
            Integer index = dagIDS.get(key);
            final CoreTask dagTask = (CoreTask) dagCollector.get(key);
            res.append(Constants.SUB_TASK_DECLR);
            res.append("" + index);
            res.append(Constants.SUB_TASK_OPEN);
            dagTask.serialize(res, dagIDS);
            res.append(Constants.SUB_TASK_CLOSE);

        }
        return res.toString();
    }

    public final void serialize(StringBuilder builder, Map<Integer, Integer> dagCounters) {
        for (int i = 0; i < insertCursor; i++) {
            if (i != 0) {
                builder.append(Constants.TASK_SEP);
            }
            if (actions[i] instanceof CF_Action) {
                ((CF_Action) actions[i]).cf_serialize(builder, dagCounters);
            } else {
                actions[i].serialize(builder);
            }
        }
    }

    private static void deep_analyze(CoreTask t, Map<Integer, Integer> counters, Map<Integer, Task> dagCollector) {
        Integer tHash = t.hashCode();
        Integer previous = counters.get(tHash);
        if (previous == null) {
            counters.put(tHash, 1);
            dagCollector.put(tHash, t);
            for (int i = 0; i < t.insertCursor; i++) {
                if (t.actions[i] instanceof CF_Action) {
                    Task[] children = ((CF_Action) t.actions[i]).children();
                    for (int j = 0; j < children.length; j++) {
                        deep_analyze((CoreTask) children[j], counters, dagCollector);
                    }
                }
            }
        } else {
            counters.put(tHash, previous + 1);
        }
    }

    @Override
    public final Task travelInWorld(final String world) {
        return then(Actions.travelInWorld(world));
    }

    @Override
    public final Task travelInTime(final String time) {
        return then(Actions.travelInTime(time));
    }

    @Override
    public final Task inject(final Object input) {
        return then(Actions.inject(input));
    }

    @Override
    public final Task defineAsGlobalVar(final String name) {
        return then(Actions.defineAsGlobalVar(name));
    }

    @Override
    public final Task defineAsVar(final String name) {
        return then(Actions.defineAsVar(name));
    }

    @Override
    public final Task declareGlobalVar(final String name) {
        return then(Actions.declareGlobalVar(name));
    }

    @Override
    public final Task declareVar(final String name) {
        return then(Actions.declareVar(name));
    }

    @Override
    public final Task readVar(final String name) {
        return then(Actions.readVar(name));
    }

    @Override
    public final Task setAsVar(final String name) {
        return then(Actions.setAsVar(name));
    }

    @Override
    public final Task addToVar(final String name) {
        return then(Actions.addToVar(name));
    }

    @Override
    public final Task setAttribute(final String name, final byte type, final String value) {
        return then(Actions.setAttribute(name, type, value));
    }

    @Override
    public final Task timeSensitivity(final long delta, final long offset) {
        return then(Actions.timeSensitivity(delta, offset));
    }

    @Override
    public final Task forceAttribute(final String name, final byte type, final String value) {
        return then(Actions.forceAttribute(name, type, value));
    }

    @Override
    public final Task remove(final String name) {
        return then(Actions.remove(name));
    }

    @Override
    public final Task attributes() {
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
    public final Task addVarToRelation(final String relName, final String varName, final String... attributes) {
        return then(Actions.addVarToRelation(relName, varName, attributes));
    }

    @Override
    public final Task removeVarFromRelation(final String relName, final String varFrom, final String... attributes) {
        return then(Actions.removeVarFromRelation(relName, varFrom, attributes));
    }

    @Override
    public final Task traverse(final String name, final String... params) {
        return then(Actions.traverse(name, params));
    }

    @Override
    public final Task attribute(final String name, final String... params) {
        return then(Actions.attribute(name, params));
    }

    @Override
    public final Task readGlobalIndex(final String name, final String... query) {
        return then(Actions.readGlobalIndex(name, query));
    }

    @Override
    public final Task addToGlobalIndex(final String name, final String... attributes) {
        return then(Actions.addToGlobalIndex(name, attributes));
    }

    @Override
    public final Task addToGlobalTimedIndex(final String name, final String... attributes) {
        return then(Actions.addToGlobalTimedIndex(name, attributes));
    }

    @Override
    public final Task removeFromGlobalIndex(final String name, final String... attributes) {
        return then(Actions.removeFromGlobalIndex(name, attributes));
    }

    @Override
    public final Task removeFromGlobalTimedIndex(final String name, final String... attributes) {
        return then(Actions.removeFromGlobalTimedIndex(name, attributes));
    }

    @Override
    public final Task indexNames() {
        return then(Actions.indexNames());
    }

    @Override
    public final Task selectWith(final String name, final String pattern) {
        return then(Actions.selectWith(name, pattern));
    }

    @Override
    public final Task selectWithout(final String name, final String pattern) {
        return then(Actions.selectWithout(name, pattern));
    }

    @Override
    public final Task select(final TaskFunctionSelect filterFunction) {
        return then(Actions.select(filterFunction));
    }

    @Override
    public final Task selectObject(final TaskFunctionSelectObject filterFunction) {
        return then(Actions.selectObject(filterFunction));
    }

    @Override
    public final Task selectScript(final String script) {
        return then(Actions.selectScript(script));
    }

    @Override
    public final Task print(final String name) {
        return then(Actions.print(name));
    }

    @Override
    public final Task println(final String name) {
        return then(Actions.println(name));
    }

    @Override
    public final Task executeExpression(final String expression) {
        return then(Actions.executeExpression(expression));
    }

    @Override
    public final Task createNode() {
        return then(Actions.createNode());
    }

    @Override
    public final Task createTypedNode(final String type) {
        return then(Actions.createTypedNode(type));
    }

    @Override
    public final Task save() {
        return then(Actions.save());
    }

    @Override
    public final Task script(final String script) {
        return then(Actions.script(script));
    }

    @Override
    public final Task asyncScript(final String ascript) {
        return then(Actions.asyncScript(ascript));
    }

    @Override
    public final Task lookup(final String nodeId) {
        return then(Actions.lookup(nodeId));
    }

    @Override
    public final Task lookupAll(final String nodeIds) {
        return then(Actions.lookupAll(nodeIds));
    }

    @Override
    public final Task clearResult() {
        return then(Actions.clearResult());
    }

    @Override
    public final Task action(final String name, final String... params) {
        return then(Actions.action(name, params));
    }

    @Override
    public final Task flipVar(final String name) {
        return then(Actions.flipVar(name));
    }

    @Override
    public final Task flat() {
        return then(Actions.flat());
    }

}
