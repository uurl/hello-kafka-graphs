
package io.treutech;

import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.junit.After;
import org.junit.ClassRule;

import io.kgraph.utils.ClientUtils;

public abstract class KStreamBuilder {
    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(1);

    protected KafkaStreams streams;
    protected Properties streamsConfiguration;

    protected <K, V> void startStreams(StreamsBuilder builder, Serde<K> keySerde, Serde<V> valueSerde) {
        String id = UUID.randomUUID().toString();
        streamsConfiguration = ClientUtils.streamsConfig("test-" + id, "test-client-" + id, CLUSTER.bootstrapServers(),
            keySerde.getClass(), valueSerde.getClass());
        streams = new KafkaStreams(builder.build(), streamsConfiguration);
        streams.start();
    }

    @After
    public void cleanup() throws Exception {
        if (streams != null) {
            streams.close();
        }
        if (streamsConfiguration != null) {
            IntegrationTestUtils.purgeLocalStreamsState(streamsConfiguration);
        }
    }
}
