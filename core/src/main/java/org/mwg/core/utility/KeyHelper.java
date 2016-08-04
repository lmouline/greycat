package org.mwg.core.utility;

import org.mwg.core.CoreConstants;
import org.mwg.utility.Base64;

public class KeyHelper {

    public static void keyToBuffer(org.mwg.struct.Buffer buffer, byte chunkType, long world, long time, long id) {
        buffer.write(chunkType);
        buffer.write(CoreConstants.KEY_SEP);
        Base64.encodeLongToBuffer(world, buffer);
        buffer.write(CoreConstants.KEY_SEP);
        Base64.encodeLongToBuffer(time, buffer);
        buffer.write(CoreConstants.KEY_SEP);
        Base64.encodeLongToBuffer(id, buffer);
    }

}
