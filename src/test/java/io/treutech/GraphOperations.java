
package io.treutech;

import java.util.List;
import java.util.Properties;

import io.kgraph.Edge;
import io.kgraph.GraphSerialized;
import io.kgraph.KGraph;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.junit.Test;

import io.kgraph.utils.ClientUtils;
import io.kgraph.utils.KryoSerde;
import io.kgraph.utils.StreamUtils;
import io.treutech.utils.TestUtils;

public class GraphOperations extends KStreamBuilder {

  private String expectedResult;

  @Test
  public void testOutDegrees() throws Exception {
    Properties producerConfig = ClientUtils.producerConfig(CLUSTER.bootstrapServers(), LongSerializer.class,
        LongSerializer.class, new Properties()
    );
    StreamsBuilder builder = new StreamsBuilder();

    KTable<Long, Long> vertices =
        StreamUtils.tableFromCollection(builder, producerConfig, Serdes.Long(), Serdes.Long(),
            TestGraphUtils.getLongLongVertices());

    KTable<Edge<Long>, Long> edges =
        StreamUtils.tableFromCollection(builder, producerConfig, new KryoSerde<>(), Serdes.Long(),
            TestGraphUtils.getLongLongEdges());

    KGraph<Long, Long, Long> graph = new KGraph<>(
        vertices, edges, GraphSerialized.with(Serdes.Long(), Serdes.Long(), Serdes.Long()));

    KTable<Long, Long> outDegrees = graph.outDegrees();

    expectedResult = "1,2\n" +
        "2,1\n" +
        "3,2\n" +
        "4,2\n" +
        "5,1\n" +
        "6,1\n";

    startStreams(builder, Serdes.Long(), Serdes.Long());

    Thread.sleep(5000);

    List<KeyValue<Long, Long>> result = StreamUtils.listFromTable(streams, outDegrees);

    TestUtils.compareResultAsTuples(result, expectedResult);
  }

  public static void main(String[] args) {
    GraphOperations go = new GraphOperations();
    try {
      go.testOutDegrees();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testInDegrees() throws Exception {
    Properties producerConfig = ClientUtils.producerConfig(CLUSTER.bootstrapServers(), LongSerializer.class,
        LongSerializer.class, new Properties()
    );
    StreamsBuilder builder = new StreamsBuilder();

    KTable<Long, Long> vertices =
        StreamUtils.tableFromCollection(builder, producerConfig, Serdes.Long(), Serdes.Long(),
            TestGraphUtils.getLongLongVertices());

    KTable<Edge<Long>, Long> edges =
        StreamUtils.tableFromCollection(builder, producerConfig, new KryoSerde<>(), Serdes.Long(),
            TestGraphUtils.getLongLongEdges());

    KGraph<Long, Long, Long> graph = new KGraph<>(
        vertices, edges, GraphSerialized.with(Serdes.Long(), Serdes.Long(), Serdes.Long()));

    KTable<Long, Long> inDegrees = graph.inDegrees();

    startStreams(builder, Serdes.Long(), Serdes.Long());

    Thread.sleep(5000);

    List<KeyValue<Long, Long>> result = StreamUtils.listFromTable(streams, inDegrees);

    expectedResult = "1,1\n" +
        "2,1\n" +
        "3,2\n" +
        "4,1\n" +
        "5,1\n" +
        "6,3\n";

    TestUtils.compareResultAsTuples(result, expectedResult);
  }

  @Test
  public void testUndirected() throws Exception {
    Properties producerConfig = ClientUtils.producerConfig(CLUSTER.bootstrapServers(), LongSerializer.class,
        LongSerializer.class, new Properties()
    );
    StreamsBuilder builder = new StreamsBuilder();

    KTable<Long, Long> vertices =
        StreamUtils.tableFromCollection(builder, producerConfig, Serdes.Long(), Serdes.Long(),
            TestGraphUtils.getLongLongVertices());

    KTable<Edge<Long>, Long> edges =
        StreamUtils.tableFromCollection(builder, producerConfig, new KryoSerde<>(), Serdes.Long(),
            TestGraphUtils.getLongLongEdges());

    KGraph<Long, Long, Long> graph = new KGraph<>(
        vertices, edges, GraphSerialized.with(Serdes.Long(), Serdes.Long(), Serdes.Long()));

    KTable<Edge<Long>, Long> data = graph.undirected().edges();

    startStreams(builder, Serdes.Long(), Serdes.Long());

    Thread.sleep(5000);

    List<KeyValue<Edge<Long>, Long>> result = StreamUtils.listFromTable(streams, data);

    expectedResult = "1,2,12\n" + "2,1,12\n" +
        "1,3,13\n" + "3,1,13\n" +
        "2,3,23\n" + "3,2,23\n" +
        "3,4,34\n" + "4,3,34\n" +
        "3,6,36\n" + "6,3,36\n" +
        "4,5,45\n" + "5,4,45\n" +
        "4,6,46\n" + "6,4,46\n" +
        "5,6,56\n" + "6,5,56\n" +
        "6,1,61\n" + "1,6,61\n";

    TestUtils.compareResultAsTuples(result, expectedResult);
  }

  @Test
  public void testSubGraph() throws Exception {
    Properties producerConfig = ClientUtils.producerConfig(CLUSTER.bootstrapServers(), LongSerializer.class,
        LongSerializer.class, new Properties()
    );
    StreamsBuilder builder = new StreamsBuilder();

    KTable<Long, Long> vertices =
        StreamUtils.tableFromCollection(builder, producerConfig, Serdes.Long(), Serdes.Long(),
            TestGraphUtils.getLongLongVertices());

    KTable<Edge<Long>, Long> edges =
        StreamUtils.tableFromCollection(builder, producerConfig, new KryoSerde<>(), Serdes.Long(),
            TestGraphUtils.getLongLongEdges());

    KGraph<Long, Long, Long> graph = new KGraph<>(
        vertices, edges, GraphSerialized.with(Serdes.Long(), Serdes.Long(), Serdes.Long()));

    KTable<Edge<Long>, Long> data = graph.subgraph((k, v) -> v > 2, (k, e) -> e > 34).edges();

    startStreams(builder, Serdes.Long(), Serdes.Long());

    Thread.sleep(5000);

    List<KeyValue<Edge<Long>, Long>> result = StreamUtils.listFromTable(streams, data);

    expectedResult = "3,6,36\n" +
        "4,5,45\n" +
        "4,6,46\n" +
        "5,6,56\n";

    TestUtils.compareResultAsTuples(result, expectedResult);
  }

  @Test
  public void testFilterVertices() throws Exception {
    Properties producerConfig = ClientUtils.producerConfig(CLUSTER.bootstrapServers(), LongSerializer.class,
        LongSerializer.class, new Properties()
    );
    StreamsBuilder builder = new StreamsBuilder();

    KTable<Long, Long> vertices =
        StreamUtils.tableFromCollection(builder, producerConfig, Serdes.Long(), Serdes.Long(),
            TestGraphUtils.getLongLongVertices());

    KTable<Edge<Long>, Long> edges =
        StreamUtils.tableFromCollection(builder, producerConfig, new KryoSerde<>(), Serdes.Long(),
            TestGraphUtils.getLongLongEdges());

    KGraph<Long, Long, Long> graph = new KGraph<>(
        vertices, edges, GraphSerialized.with(Serdes.Long(), Serdes.Long(), Serdes.Long()));

    KTable<Edge<Long>, Long> data = graph.filterOnVertices((k, v) -> v > 2).edges();

    startStreams(builder, Serdes.Long(), Serdes.Long());

    Thread.sleep(5000);

    List<KeyValue<Edge<Long>, Long>> result = StreamUtils.listFromTable(streams, data);

    expectedResult = "3,4,34\n" +
        "3,6,36\n" +
        "4,5,45\n" +
        "4,6,46\n" +
        "5,6,56\n";

    TestUtils.compareResultAsTuples(result, expectedResult);
  }

  @Test
  public void testFilterEdges() throws Exception {
    Properties producerConfig = ClientUtils.producerConfig(CLUSTER.bootstrapServers(), LongSerializer.class,
        LongSerializer.class, new Properties()
    );
    StreamsBuilder builder = new StreamsBuilder();

    KTable<Long, Long> vertices =
        StreamUtils.tableFromCollection(builder, producerConfig, Serdes.Long(), Serdes.Long(),
            TestGraphUtils.getLongLongVertices());

    KTable<Edge<Long>, Long> edges =
        StreamUtils.tableFromCollection(builder, producerConfig, new KryoSerde<>(), Serdes.Long(),
            TestGraphUtils.getLongLongEdges());

    KGraph<Long, Long, Long> graph = new KGraph<>(
        vertices, edges, GraphSerialized.with(Serdes.Long(), Serdes.Long(), Serdes.Long()));

    KTable<Edge<Long>, Long> data = graph.filterOnEdges((k, e) -> e > 34).edges();

    startStreams(builder, Serdes.Long(), Serdes.Long());

    Thread.sleep(5000);

    List<KeyValue<Edge<Long>, Long>> result = StreamUtils.listFromTable(streams, data);

    expectedResult = "3,6,36\n" +
        "4,5,45\n" +
        "4,6,46\n" +
        "5,6,56\n" +
        "6,1,61\n";

    TestUtils.compareResultAsTuples(result, expectedResult);
  }
}
