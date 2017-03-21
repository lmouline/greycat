package greycat.model;

import greycat.Type;

public final class MetaAttribute {

    private final String _name;
    private final byte _type;

    public MetaAttribute(String name, byte type) {
        _name = name;
        _type = type;
    }

    public final String name() {
        return _name;
    }

    public final byte type() {
        return _type;
    }

    @Override
    public String toString() {
        return "@"+_name+":"+ Type.typeName(_type);
    }
}
