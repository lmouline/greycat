package greycat.model.actions;

import greycat.Action;

public class MetaActions {

    public static Action declareMetaClass(String name) {
        return new DeclareMetaClass(name);
    }

    public static Action declareMetaAttribute(String name, String type) {
        return new DeclareMetaAttribute(name, type);
    }

    public static Action getMetaClasses() {
        return new GetMetaClasses();
    }

}
