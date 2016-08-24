# Task Action: Loop

The loop action allows to express the repetition of a sub Task passed as parameter. Similaly to **for** loop construction in many imperative language, this action express the iteration over a range, through **INCLUSIVE** boundaries. 

> Additionally the loop action declare automatically a **i** variable containing the current loop index.

Therefore the following expression: 

```java
loop("0","3",print("{{i}}"));
```

Will print the following result:

```
0
1
2
3
```

In addition, loop action parameters are templates and can express math expression such as `"{{=3-1}}" therefore, exclusive bounds can be build this way.

Finally the loop action is transient and will not modify the current context. Therefore the task context after the loop action remains unchanged. If a cummulative results as to be build, the best it to use a **addToVariable** action within the repeted sub task.


