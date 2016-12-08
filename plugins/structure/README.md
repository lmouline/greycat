# Structure Plugin:

This **mwg plugin** aims at offering more complex data structures to index multi-dimensional data.
It consists of 3 main nodes: **KDTree, NDTree, SparseNDTree**. There 3 structures implement the **NTree** interface.

The main concepts to understand for NTrees are the following:
- Keys in NTrees are double[] to allow indexing multi-dimensional data. 
- For the same NTree instance, all the keys should have the same dimension (all keys should have the same array length). 
- The inserted value should be of a type **Node** or **null** if you want to index only keys.
- NTrees need a **distance function** to be able to index the multi-dimension keys and compare them to each other. 
- Structure plugin offers implementation for classical distance functions: Euclidean, Cosine, Pearson, GeoDistance. 


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
In order to use one of the trees to index multi-dimensional data, the first step is to create an instance node of the desired tree:

```java
NTree kdTree= (NTree) graph.newTypedNode(0,0, KDTree.NAME);
```

Similarly, in order to create an index tree of a different type, replace the **KDTree.NAME**, by **NDTree.NAME** or **SparseNDTree.NAME**.
