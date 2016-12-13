package org.mwg;

class WSConstants {

    static final String DISCONNECTED_ERROR = "Please connect your WebSocket client first.";

    static final byte REQ_GET = 0;
    static final byte REQ_PUT = 1;
    static final byte REQ_LOCK = 2;
    static final byte REQ_UNLOCK = 3;
    static final byte REQ_REMOVE = 4;
    static final byte REQ_UPDATE = 5;
    static final byte REQ_TASK = 6;

    static final byte RESP_GET = 7;
    static final byte RESP_PUT = 8;
    static final byte RESP_REMOVE = 9;
    static final byte RESP_LOCK = 10;
    static final byte RESP_UNLOCK = 11;
    static final byte RESP_TASK = 12;

}
