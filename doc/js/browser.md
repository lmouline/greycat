# Browser usage

To use MWG in your browser you simply have to add the various ```.js``` files to your DOM such as:

``` html
<script src="js/mwg.js"></script>
```

In addition various plugins can be load later such as:

``` html
<script src="js/mwg.structure.js"></script>
<script src="js/mwg.ml.js"></script>
<script src="js/mwg.ws.js"></script>
```

Such JS files can be downloaded through maven central repository using url such as ```https://repo.maven.apache.org/maven2/org/kevoree/mwg/core/7/core-7-js.zip```

Of course replace core by the name of the desired plugin name and the 7 by the desired version. In every MWG zip bundle man can find the following files:

- XYZ.js: standard file usable for debug and developement
- XYZ.min.js: compressed file for production usage
- XYZ.ts: TypeScript source for MWG extension
- XYZ.d.ts TypeScript header file for simple inclusion in TypeScript developement environement.

Of course for lightweight developement we advice to use ```.min.js``` files.

Then the JS API is identical to Java API and can be used such as:

```js
var mwg = new org.mwg.GraphBuilder()
	.withStorage(new org.mwg.plugin
	.WSClient("ws://localhost:8081"))
	.build();
```
The ```.WSClient("ws://localhost:8081"))``` is optional but allows to use a remote graph (client/server mode). Please read the distributed graph section to have more details.