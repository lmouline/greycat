package greycat.internal.task;

import greycat.Constants;
import greycat.ProgressReport;
import greycat.internal.CoreConstants;
import greycat.struct.Buffer;
import greycat.utility.Base64;
import greycat.utility.ProgressType;

/**
 * Created by Gregory NAIN on 14/04/17.
 */
public class CoreProgressReport implements ProgressReport {

    private ProgressType _type;
    private int _index;
    private int _total;
    private String _comment;

    public CoreProgressReport(){
    }

    public CoreProgressReport(final ProgressType type, final int _index, final int _total, final String _comment) {
        this._type = type;
        this._index = _index;
        this._total = _total;
        this._comment = _comment;
    }

    @Override
    public ProgressType type() {
        return this._type;
    }

    @Override
    public int index() {
        return this._index;
    }

    @Override
    public int total() {
        return this._total;
    }

    @Override
    public String comment() {
        return this._comment;
    }


    public void loadFromBuffer(Buffer buffer) {

        int cursor = 0;
        int previous = 0;
        int index = 0;
        while (cursor < buffer.length()) {
            byte current = buffer.read(cursor);
            if (current == Constants.CHUNK_ESEP || cursor + 1 == buffer.length()) {
                switch (index) {
                    case 0:
                        _type = ProgressType.valueOf(Base64.decodeToStringWithBounds(buffer, previous, cursor));
                        index++;
                        break;
                    case 1:
                        _index = Base64.decodeToIntWithBounds(buffer, previous, cursor);
                        index++;
                        break;
                    case 2:
                        _total = Base64.decodeToIntWithBounds(buffer, previous, cursor);
                        index++;
                        break;
                    case 3:
                        if (cursor != previous) {
                            _comment = Base64.decodeToStringWithBounds(buffer, previous, cursor + 1);
                        }
                        break;
                }
                previous = cursor + 1;
            }
            cursor++;
        }
    }

    public void saveToBuffer(Buffer buffer) {
        Base64.encodeStringToBuffer(this._type.toString(), buffer);
        buffer.write(CoreConstants.CHUNK_ESEP);
        Base64.encodeIntToBuffer(this._index, buffer);
        buffer.write(CoreConstants.CHUNK_ESEP);
        Base64.encodeIntToBuffer(this._total, buffer);
        buffer.write(CoreConstants.CHUNK_ESEP);
        if (this._comment != null) {
            Base64.encodeStringToBuffer(this._comment, buffer);
        }

    }

    public String toString() {
        return _type.toString() + "\t\t" + _index + "/" + _total + "\t" + _comment;
    }

}
