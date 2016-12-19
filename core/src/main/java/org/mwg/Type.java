package org.mwg;

/**
 * Defines the constants used in mwDB.
 */
public class Type {

    /**
     * Primitive Types
     */
    public static final byte BOOL = 1;
    public static final byte STRING = 2;
    public static final byte LONG = 3;
    public static final byte INT = 4;
    public static final byte DOUBLE = 5;

    /*
     * Primitive Arrays
     */
    public static final byte DOUBLE_ARRAY = 6;
    public static final byte LONG_ARRAY = 7;
    public static final byte INT_ARRAY = 8;
    public static final byte LONG_TO_LONG_MAP = 9;
    public static final byte LONG_TO_LONG_ARRAY_MAP = 10;
    public static final byte STRING_TO_LONG_MAP = 11;
    public static final byte RELATION = 12;
    public static final byte RELATION_INDEXED = 13;
    public static final byte MATRIX = 15;
    public static final byte EGRAPH = 16;
    public static final byte EXTERNAL = 17;

    /**
     * Convert a type that represent a byte to a readable String representation
     *
     * @param p_type byte encoding a particular type
     * @return readable string representation of the type
     */
    public static String typeName(byte p_type) {
        switch (p_type) {
            /* Primitives */
            case Type.BOOL:
                return "BOOL";
            case Type.STRING:
                return "STRING";
            case Type.LONG:
                return "LONG";
            case Type.INT:
                return "INT";
            case Type.DOUBLE:
                return "DOUBLE";
            /* Arrays */
            case Type.DOUBLE_ARRAY:
                return "DOUBLE_ARRAY";
            case Type.LONG_ARRAY:
                return "LONG_ARRAY";
            case Type.INT_ARRAY:
                return "INT_ARRAY";
            /* Maps */
            case Type.LONG_TO_LONG_MAP:
                return "LONG_TO_LONG_MAP";
            case Type.LONG_TO_LONG_ARRAY_MAP:
                return "LONG_TO_LONG_ARRAY_MAP";
            case Type.STRING_TO_LONG_MAP:
                return "STRING_TO_LONG_MAP";
            case Type.RELATION:
                return "RELATION";
            case Type.RELATION_INDEXED:
                return "RELATION_INDEXED";
            case Type.MATRIX:
                return "MATRIX";
            case Type.EGRAPH:
                return "EGRAPH";
            case Type.EXTERNAL:
                return "EXTERNAL";
            default:
                return "unknown";
        }
    }

    public static byte typeFromName(String name) {
        switch (name) {
            case "BOOL":
                return Type.BOOL;
            case "STRING":
                return Type.STRING;
            case "LONG":
                return Type.LONG;
            case "INT":
                return Type.INT;
            case "DOUBLE":
                return Type.DOUBLE;
            case "DOUBLE_ARRAY":
                return Type.DOUBLE_ARRAY;
            case "LONG_ARRAY":
                return Type.LONG_ARRAY;
            case "INT_ARRAY":
                return Type.INT_ARRAY;
            case "LONG_TO_LONG_MAP":
                return Type.LONG_TO_LONG_MAP;
            case "LONG_TO_LONG_ARRAY_MAP":
                return Type.LONG_TO_LONG_ARRAY_MAP;
            case "STRING_TO_LONG_MAP":
                return Type.STRING_TO_LONG_MAP;
            case "RELATION":
                return Type.RELATION;
            case "RELATION_INDEXED":
                return Type.RELATION_INDEXED;
            case "MATRIX":
                return Type.MATRIX;
            case "EXTERNAL":
                return Type.EXTERNAL;
            case "EGRAPH":
                return Type.EGRAPH;
            default:
                return -1;
        }
    }

}
