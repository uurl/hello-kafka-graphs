
package io.treutech;

import java.util.ArrayList;
import java.util.List;

import io.kgraph.Edge;
import org.apache.kafka.streams.KeyValue;

public class TestGraphUtils {

    public static List<KeyValue<Long, Long>> getLongLongVertices() {
        List<KeyValue<Long, Long>> vertices = new ArrayList<>();
        vertices.add(new KeyValue<>(1L, 1L));
        vertices.add(new KeyValue<>(2L, 2L));
        vertices.add(new KeyValue<>(3L, 3L));
        vertices.add(new KeyValue<>(4L, 4L));
        vertices.add(new KeyValue<>(5L, 5L));
        vertices.add(new KeyValue<>(6L, 6L));

        return vertices;
    }

    public static List<KeyValue<Edge<Long>, Long>> getLongLongEdges() {
        List<KeyValue<Edge<Long>, Long>> edges = new ArrayList<>();
        edges.add(new KeyValue<>(new Edge<>(1L, 2L), 12L));
        edges.add(new KeyValue<>(new Edge<>(1L, 3L), 13L));
        edges.add(new KeyValue<>(new Edge<>(2L, 3L), 23L));
        edges.add(new KeyValue<>(new Edge<>(3L, 4L), 34L));
        edges.add(new KeyValue<>(new Edge<>(3L, 6L), 36L));
        edges.add(new KeyValue<>(new Edge<>(4L, 5L), 45L));
        edges.add(new KeyValue<>(new Edge<>(4L, 6L), 46L));
        edges.add(new KeyValue<>(new Edge<>(5L, 6L), 56L));
        edges.add(new KeyValue<>(new Edge<>(6L, 1L), 61L));

        return edges;
    }
}
