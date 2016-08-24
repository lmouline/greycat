# Task Action: Get

The get action allows to get the value(s) of a node property. The property can be of any type, a relation, a map or a primitive Type.
See also [setProperty](setProperty.md) to discover how to set a property of a node using Tasks.

This action should only be used after a task returning a node as result.

> The result of the *get* will be put in the result and made accessible to the next Task.

Therefore, the following expression:

```java
newNode()
.setProperty("name","mynode")
.get("name")
.print("{{result}}"
.execute(g,null);
```

Will print the following output in console:

```
mynode
```


> If the goal is to retrieve and work on the node(s) linked to the current node by a relation, the [traverse](traverse.md) Task should be considered to have safely only nodes in the result.

