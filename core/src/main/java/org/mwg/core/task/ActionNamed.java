package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskActionFactory;
import org.mwg.task.TaskContext;

class ActionNamed implements Action {

    private final String _name;
    private final String[] _params;

    ActionNamed(final String name, final String... params) {
        this._name = name;
        this._params = params;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final String templatedName = ctx.template(_name);
        final String[] templatedParams = ctx.templates(_params);
        final TaskActionFactory actionFactory = ctx.graph().taskAction(templatedName);
        if (actionFactory == null) {
            throw new RuntimeException("Unknown task action: " + templatedName);
        }
        final Action subAction = actionFactory.create(templatedParams);
        if (subAction != null) {
            subAction.eval(ctx);
        } else {
            ctx.continueTask();
        }

        /*
        int paramsCapacity = CoreConstants.MAP_INITIAL_CAPACITY;
        String[] params = new String[paramsCapacity];
        int paramsIndex = 0;
        int cursor = 0;
        int flatSize = templatedParams.length();
        int previous = 0;
        while (cursor < flatSize) {
            char current = templatedParams.charAt(cursor);
            if (current == Constants.QUERY_SEP) {
                String param = templatedParams.substring(previous, cursor);
                if (param.length() > 0) {
                    if (paramsIndex >= paramsCapacity) {
                        int newParamsCapacity = paramsCapacity * 2;
                        String[] newParams = new String[newParamsCapacity];
                        System.arraycopy(params, 0, newParams, 0, paramsCapacity);
                        params = newParams;
                        paramsCapacity = newParamsCapacity;
                    }
                    params[paramsIndex] = param;
                    paramsIndex++;
                }
                previous = cursor + 1;
            }
            cursor++;
        }
        //add last param
        String param = templatedParams.substring(previous, cursor);
        if (param.length() > 0) {
            if (paramsIndex >= paramsCapacity) {
                int newParamsCapacity = paramsCapacity * 2;
                String[] newParams = new String[newParamsCapacity];
                System.arraycopy(params, 0, newParams, 0, paramsCapacity);
                params = newParams;
                paramsCapacity = newParamsCapacity;
            }
            params[paramsIndex] = param;
            paramsIndex++;
        }
        //schrink
        if (paramsIndex < params.length) {
            String[] shrinked = new String[paramsIndex];
            System.arraycopy(params, 0, shrinked, 0, paramsIndex);
            params = shrinked;
        }
        */
        //add the action to the action
    }

    @Override
    public String toString() {
        return _name + "(" + _params + ")";
    }

}
