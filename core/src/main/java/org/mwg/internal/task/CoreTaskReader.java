package org.mwg.internal.task;

class CoreTaskReader {

    private final String flat;

    private final int offset;

    private int _end = -1;

    CoreTaskReader(String p_flat, int p_offset) {
        this.flat = p_flat;
        this.offset = p_offset;
    }

    public int available() {
        return flat.length() - offset;
    }

    public char charAt(int cursor) {
        return flat.charAt(offset + cursor);
    }

    public String extract(int begin, int end) {
        return flat.substring(offset + begin, offset + end);
    }

    public void markend(int p_end) {
        this._end = p_end;
    }

    public int end() {
        if (_end == -1) {
            return available();
        } else {
            return _end;
        }
    }

    public CoreTaskReader slice(int cursor) {
        return new CoreTaskReader(flat, cursor);
    }

}
