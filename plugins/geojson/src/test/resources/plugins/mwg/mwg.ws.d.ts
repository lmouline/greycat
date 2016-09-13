/// <reference path="mwg.d.ts" />
/**
 * This behaves like a WebSocket in every way, except if it fails to connect,
 * or it gets disconnected, it will repeatedly poll until it succesfully connects
 * again.
 *
 * It is API compatible, so when you have:
 *   ws = new WebSocket('ws://....');
 * you can replace with:
 *   ws = new ReconnectingWebSocket('ws://....');
 *
 * The event stream will typically look like:
 *  onconnecting
 *  onopen
 *  onmessage
 *  onmessage
 *  onclose // lost connection
 *  onconnecting
 *  onopen  // sometime later...
 *  onmessage
 *  onmessage
 *  etc...
 *
 * It is API compatible with the standard WebSocket API.
 *
 * Latest version: https://github.com/joewalnes/reconnecting-websocket/
 * - Joe Walnes
 *
 * Latest TypeScript version: https://github.com/daviddoran/typescript-reconnecting-websocket/
 * - David Doran
 */
declare module WebSocketHelper {
    class ReconnectingWebSocket {
        debug: boolean;
        reconnectInterval: number;
        timeoutInterval: number;
        readyState: number;
        private forcedClose;
        private timedOut;
        private protocols;
        private ws;
        private url;
        /**
         * Setting this to true is the equivalent of setting all instances of ReconnectingWebSocket.debug to true.
         */
        static debugAll: boolean;
        onopen: (ev: Event) => void;
        onclose: (ev: CloseEvent) => void;
        onconnecting: () => void;
        onmessage: (ev: MessageEvent) => void;
        onerror: (ev: ErrorEvent) => void;
        constructor(url: string, protocols?: string[]);
        connect(reconnectAttempt: boolean): void;
        send(data: any): void;
        /**
         * Returns boolean, whether websocket was FORCEFULLY closed.
         */
        close(): boolean;
        /**
         * Additional public API method to refresh the connection if still open (close, re-open).
         * For example, if the app suspects bad data / missed heart beats, it can try to refresh.
         *
         * Returns boolean, whether websocket was closed.
         */
        refresh(): boolean;
        private log(...args);
    }
}
declare module org {
    module mwg {
        module plugin {
            class WSClient implements org.mwg.plugin.Storage {
                private url;
                private callbacks;
                private ws;
                private graph;
                private generator;
                private REQ_GET;
                private REQ_PUT;
                private REQ_LOCK;
                private REQ_UNLOCK;
                private REQ_REMOVE;
                private REQ_UPDATE;
                private RESP_GET;
                private RESP_PUT;
                private RESP_REMOVE;
                private RESP_LOCK;
                private RESP_UNLOCK;
                constructor(p_url: string);
                connect(p_graph: org.mwg.Graph, callback: org.mwg.Callback<boolean>): void;
                disconnect(callback: org.mwg.Callback<boolean>): void;
                get(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                put(stream: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                remove(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                lock(callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                unlock(previousLock: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                process_rpc_resp(payload: Int8Array): void;
                send_rpc_req(code: number, payload: org.mwg.struct.Buffer, callback: org.mwg.Callback<any>): void;
            }
        }
    }
}
