# Hello Kafka Graphs

## Creating Graphs

A graph in Kafka Graphs is represented by two tables from Kafka Streams, one for vertices and one for edges. The vertex table is comprised of an ID and a vertex value, while the edge table is comprised of a source ID, target ID, and edge value.

```java
KTable<Long, Long> vertices = ...
KTable<Edge<Long>, Long> edges = ...
KGraph<Long, Long, Long> graph = new KGraph<>(
    vertices,
    edges,
    GraphSerialized.with(Serdes.Long(), Serdes.Long(), Serdes.Long())
);
```

For example, in the class ```TestGraphUtils``` we define the following graph


![Graph](images/Graph.png?raw=true)

- Its ```vertex``` are defined by:
   ```
   1,1
   2,2
   3,3
   4,4
   5,5
   6,6
   ```

- Its ```edges``` are defined by:
   ```
   1,2,12
   1,3,13
   2,3,23
   3,4,34
   3,6,36
   4,5,45
   4,6,46
   5,6,56
   6,1,61
   ```

## Graph Operations

Kafka Graphs provides a number of APIs for transforming graphs in the same manner as Apache Flink Gelly and Apache Spark GraphX.

- Filtering methods
  - ``filterOnEdges``
  - ``filterOnVertices``
  - ``subgraph``
- Joining methods
  - ``joinWithEdges``
  - ``joinWithEdgesOnSource``
  - ``joinWithEdgesOnTarget``
  - ``joinWithVertices``
- Mapping methods
  - ``mapEdges``
  - ``mapVertices``
- Reducing methods
  - ``groupReduceOnEdges``
  - ``groupReduceOnNeighbors``
  - ``reduceOnEdges``
  - ``reduceOnNeighbors``
- Transforming methods
  - ``inDegrees``
  - ``outDegrees``
  - ``undirected``

For example, the following will compute the sum of the values of all incoming neighbors for each vertex.

```java
graph.reduceOnNeighbors(new SumValues(), EdgeDirection.IN);
```

## Running Graph Operations

In the class ```GraphOperations``` we run the Operations over the defined Graph

- The method ```testOutDegrees()```

  Calculates ```KTable<Long, Long> outDegrees = graph.outDegrees();```

  The result is the out-degree of each vertex:
     ```
     1,2
     2,1
     3,2
     4,2
     5,1
     6,1
     ```

- The method ```testInDegrees()```

  Calculates ```KTable<Long, Long> inDegrees = graph.inDegrees();```

  The result is the in-degree of each vertex:
     ```
     1,1
     2,1
     3,2
     4,1
     5,1
     6,3
     ```

- The method ```testUndirected()```

  Calculates ```KTable<Edge<Long>, Long> data = graph.undirected().edges();```

  The result is the all the edges without considering direction:
     ```
     1,2,12    2,1,12
     1,3,13    3,1,13
     2,3,23    3,2,23
     3,4,34    4,3,34
     3,6,36    6,3,36
     4,5,45    5,4,45
     4,6,46    6,4,46
     5,6,56    6,5,56
     6,1,61    1,6,61
     ```


- The method ```testSubGraph()```

  Calculates ```KTable<Edge<Long>, Long> data = graph.subgraph((k, v) -> v > 2, (k, e) -> e > 34).edges();```

  The result is all the edges with vertex greater than 2 and edge greater than 34:
     ```
     3,6,36
     4,5,45
     4,6,46
     5,6,56
     ```

- The method ```testFilterVertices()```

  Calculates ```KTable<Edge<Long>, Long> data = graph.filterOnVertices((k, v) -> v > 2).edges();```

  The result are all the vertex greater that 2:
    ```
    3,4,34
    3,6,36
    4,5,45
    4,6,46
    5,6,56
    ```


- The method ```testFilterEdges()```

  Calculates ```KTable<Edge<Long>, Long> data = graph.filterOnEdges((k, e) -> e > 34).edges();```

  The result are all the edges greater than 34:
    ```
    4,5,45
    4,6,46
    5,6,56
    6,1,61
    ```