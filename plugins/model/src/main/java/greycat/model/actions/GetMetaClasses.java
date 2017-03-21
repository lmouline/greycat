package greycat.model.actions;

import greycat.*;
import greycat.model.MetaModelPlugin;
import greycat.struct.Buffer;

public class GetMetaClasses implements Action {

    static final String NAME = "getMetaClasses";

    @Override
    public void eval(TaskContext ctx) {
        ctx.graph().indexIfExists(ctx.world(), ctx.time(), MetaModelPlugin.INDEXES, new Callback<NodeIndex>() {
            @Override
            public void on(NodeIndex result) {
                if (result == null) {
                    ctx.continueWith(ctx.newResult());
                } else {
                    result.find(new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            ctx.continueWith(ctx.wrap(result));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void serialize(final Buffer builder) {
        builder.writeString(NAME);
        builder.writeChar(Constants.TASK_PARAM_OPEN);
        builder.writeChar(Constants.TASK_PARAM_CLOSE);
    }
}
