package greycat.struct;

@FunctionalInterface
public interface IntStringMapCallBack {
    void on(int key, String value);
}
