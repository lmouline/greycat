package greycat.model.actions;

import greycat.Action;
import greycat.Constants;
import greycat.TaskContext;
import greycat.TaskResult;
import greycat.internal.task.TaskHelper;
import greycat.model.MetaClass;
import greycat.struct.Buffer;

public class DeclareMetaAttribute implements Action {

    static final String NAME = "declareMetaAttribute";

    private final String _name;
    private final String _type;

    DeclareMetaAttribute(final String name, final String type) {
        _name = name;
        _type = type;
    }

    @Override
    public void eval(TaskContext ctx) {
        TaskResult result = ctx.result();
        for (int i = 0; i < result.size(); i++) {
            Object loop = result.get(i);
            if (loop instanceof MetaClass) {
                ((MetaClass) loop).declareAttribute(_name, greycat.Type.typeFromName(_type));
            }
        }
        ctx.continueTask();
    }

    @Override
    public void serialize(final Buffer builder) {
        builder.writeString(NAME);
        builder.writeChar(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder, true);
        builder.writeChar(Constants.TASK_PARAM_SEP);
        TaskHelper.serializeString(_type, builder, true);
        builder.writeChar(Constants.TASK_PARAM_CLOSE);
    }

}
