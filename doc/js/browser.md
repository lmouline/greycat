# Browser usage

To use MWG in your browser, you simply have to add the needed ```.js``` files to your DOM, for example:

``` html
<script src="js/mwg.js"></script>
```

In addition, plugins can be loaded later, for example:

``` html
<script src="js/mwg.structure.js"></script>
<script src="js/mwg.ml.js"></script>
<script src="js/mwg.ws.js"></script>
```

JS files can be downloaded via the maven central repository, e.g., ```https://repo.maven.apache.org/maven2/org/kevoree/mwg/core/7/core-7-js.zip```

The name 'core' and the version number '7' must be replaced by the desired plugin name and version number.  In every MWG zip bundle one can find the following files:

- XYZ.js: standard file usable for debug and development environments 
- XYZ.min.js: compressed file for production usage
- XYZ.ts: TypeScript source for MWG extension
- XYZ.d.ts TypeScript header file for simple inclusion in TypeScript development environment.

For lightweight development, we advice you to use ```.min.js``` files.

Then, the JS API is practically identical to the Java API and can be used like in the following code snipped:

```js
var mwg = new org.mwg.GraphBuilder()
	.withStorage(new org.mwg.plugin
	.WSClient("ws://localhost:8081"))
	.build();
```
The line ```.WSClient("ws://localhost:8081"))``` is optional but allows to use a remote graph (client/server mode). Please read the distributed graph section to get more details on this topic.