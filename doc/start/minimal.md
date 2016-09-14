# First minimal graph

Create and connect a simple graph using the following line

```java
Graph g = new GraphBuilder().build();
g.connect(isconnected-> {
	//your next code go here...
});
```

Then create a first node and set some attributes:

```java
Node sensor0 = g.newNode(0, 0);
sensor0.set("id", "4494F");
sensor0.set("name", "sensor0"); 
```

Create a second node and attach the first one in a relation:

```java
Node room0 = g.newNode(0, 0);
room0.set("name", "room0");
room0.add("sensors", sensor0);
```

Finally a relation can be traversed through:

```java
room0.rel("sensors", (Node[] sensors) -> { //iterate over saved sensors
	System.out.println("Sensors");
	for (int i = 0; i < sensors.length; i++) {
		System.out.println("\t" + sensors[i].toString());
	}
});
```

