Mwg graphical debugger
===

It is a web based Mwg debugger.

# Requirement
- npm
- bower: `npm install [-g] bower`
- gulp: `npm install [-g] gulp-cli`
- sass: `[sudo] gem install sass`
- [WebSocket plugin](../websocket)

# Launch
- `npm install`
- `bower install`
- `gulp`

Now a web page will open at `localhost:8080`, you can close it.
Open `index.html` file in your favorite browser. A WS client is running, and try to connect to a server on port `8050`.
Thus, you just need to run one in our program :
```java
 WSServer serverDebug = new WSServer([YOUR GRAPH],8050);
 serverDebug.start();
```



