# Structure Plugin:

This **GreyCat plugin** aims at offering more complex data structures to index multi-dimensional data.
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
import StructurePlugin;

Graph graph = new GraphBuilder()
                  .withPlugin(new StructurePlugin())
                  .build();
```

## Configuration
### KDTree


- In order to use one of the trees to index multi-dimensional data, the first step is to create an instance node of the desired tree:
Similarly, in order to create an index tree of a different type, just replace the **KDTree.NAME**, by **NDTree.NAME** or **SparseNDTree.NAME**.

- The next step is to configure the distance metric function to use by the tree. By default, the **Euclidean distance** is selected. 
Note that in some cases, it is very important to normalize the keys accross the dimensions in order to treat the dimensions equally. 
To select the distance to be used, use the following code: 


- The third step, is to set the **distance threshold**, this is a double value, 
under which the index tree will replace the previous value by the new one. 
For example, if the threshold is set to 0.5, then the keys: [3.000, 5.000] and [3.001, 5.001] are considered 
the same key under the Euclidean distance, because the distance separating them is less than 0.5 units. 
The default distance threshold is set to **1e-10**. To set the distance threshold: 

```java
NTree kdTreeOld= (NTree) graph.newTypedNode(0,0, KDTree.NAME);
kdTreeOld.setDistance(Distances.EUCLIDEAN);    // Default distance
kdTreeOld.setDistanceThreshold(1e-10);         // Default threshold
```

### NDTree

- For **NDTree**, you should set the borders of the multi-dimensional space and the resolution of the lowest level cell in the grid in all dimensions.
For instance, if we have 3 dimensions: x,y,z, all varying from 0 to 1, and we want to have at the lowest level, a resolution of 0.1 in all axises, we set the following:

```java
NTree ndTree = (NTree) graph.newTypedNode(0, 0, NDTree.NAME);
ndTree.setDistance(Distances.EUCLIDEAN); //Default distance
ndTree.setAt(NDTree.BOUND_MIN, Type.DOUBLE_ARRAY, new double[]{0, 0, 0});
ndTree.setAt(NDTree.BOUND_MAX, Type.DOUBLE_ARRAY, new double[]{1, 1, 1});
ndTree.setAt(NDTree.RESOLUTION, Type.DOUBLE_ARRAY, new double[]{0.1, 0.1, 0.1});
```

### SparseNDTree

- For **SparseNDTree**, in a similar way than **NDTree**, you should set the borders of the multi-dimensional space.
Instead of setting the resolution of the lowest level cell, the sparse NDTree takes an integer of how many subelements
 it can handle on the parent cell, before it creates a subcell. This parameter is called: MAX_CHILDREN. 
 So basically the sparseNDTree will grow incrementally in depth when the parent subcell has more than this max children parameters. 
 

```java
NTree sparseNDTree = (NTree) graph.newTypedNode(0, 0, SparseNDTree.NAME);
sparseNDTree.setDistance(Distances.EUCLIDEAN); //Default distance
sparseNDTree.setAt(SparseNDTree.BOUND_MIN, Type.DOUBLE_ARRAY, new double[]{0, 0, 0});
sparseNDTree.setAt(SparseNDTree.BOUND_MAX, Type.DOUBLE_ARRAY, new double[]{1, 1, 1});
sparseNDTree.setAt(SparseNDTree.MAX_CHILDREN, Type.INT, 5);

```

## Task API 

   

