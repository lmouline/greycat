# Task Action: Set Property

The get action allows to set the value(s) of a node property. The property can be of any type, a relation, a map or a primitive Type.
See also [get](get.md) to discover how to get the value of a node property using Tasks.

This action should only be used after a task returning a node as result.

> The use of *setProperty* doesn't impact the current result, thus several setProperty can be chained.

setProperty requires 3 parameters:

* name of the property 
* type of the property
* value to set or name of the variable containing the value

Therefore, the following expression:

```java
newNode()
.setProperty("name",Type.String,"mynode")
.setProperty("level",Type.Int,"1")
.setProperty("level",Type.BOOL,"true")
.execute(g,null);
```

Will print the following output in console:

```
mynode
```


> If the goal is to retrieve and work on the node(s) linked to the current node by a relation, the [traverse](traverse.md) Task should be considered to have safely only nodes in the result.

