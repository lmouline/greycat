package greycat;

import greycat.struct.Buffer;
import greycat.utility.ProgressType;

/**
 * Created by Gregory NAIN on 14/04/17.
 */
public interface ProgressReport {

    ProgressType type();
    int index();
    int total();
    String comment();

    void saveToBuffer(Buffer buffer);
    void loadFromBuffer(Buffer buffer);

}
