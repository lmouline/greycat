# Structure Plugin:

This **mwg plugin** aims at offering more complex data structures to index multi-dimensional data.
It consists of 3 main nodes: **KDTree, NDTree, SparseNDTree**. There 3 structures implement the **NTree** interface.

The main concepts to understand for NTrees are the following:
- Keys in NTrees are double[] to allow indexing multi-dimensional data. 
- For the same NTree instance, all the keys should have the same dimension (all keys should have the same array length). 
- The inserted value should be of a type **Node** or **null** if you want to index only keys.
- NTrees need a **distance function** to be able to index the multi-dimension keys and compare them to each other. 
- Structure plugin offers implementation for classical distance functions: Euclidean, Cosine, Pearson, GeoDistance. 

## Tree types


## Last versions:

- 1.0 compatible with mwg API 1.x

## Changelog

- Implementing basic KDTree, NDTree, Sparse NDTree

## Dependency

Simply add the following dependency to your maven project:

```java
      <dependency>
            <groupId>org.kevoree.mwg.plugins</groupId>
            <artifactId>structure</artifactId>
            <version>REPLACE_BY_LAST_VERSION</version>
        </dependency>
```

## Usage

As any mwg plugin, the **StructurePlugin** should be inserted during the build step of the graph.


```java
import org.mwg.structure.StructurePlugin;

Graph graph = new GraphBuilder()
                  .withPlugin(new StructurePlugin())
                  .build();
```

## Configuration
1. In order to use one of the trees to index multi-dimensional data, the first step is to create an instance node of the desired tree:

```java
NTree kdTree= (NTree) graph.newTypedNode(0,0, KDTree.NAME);
```
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Similarly, in order to create an index tree of a different type, just replace the **KDTree.NAME**, by **NDTree.NAME** or **SparseNDTree.NAME**.

2. The next step is to configure the distance metric function to use by the tree. By default, the **Euclidean distance** is selected. 
Note that in some cases, it is very important to normalize the keys accross the dimensions in order to treat the dimensions equally. 
To select the distance to be used, use the following code: 

```java
 kdTree.setDistance(Distances.EUCLIDEAN); 
```

3. The third step, is to set the **distance threshold**, this is a double value, 
under which the index tree will replace the previous value by the new one. 
For example, if the threshold is set to 0.5, then the keys: [3.000, 5.000] and [3.001, 5.001] are considered 
the same key under the Euclidean distance, because the distance separating them is less than 0.5 units. 
The default distance threshold is set to **1e-10**. To set the distance threshold: 

```java
 kdTree.setDistanceThreshold(0.001);
```

4. For **NDTree and SparseNDTree**, a settin



## Task API 

   

