package com.university.flink.stock.processing.kafka;

import com.university.stock.market.model.domain.StockStatus;
import lombok.RequiredArgsConstructor;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

@RequiredArgsConstructor
public class KafkaPipelineFactory {

  private final String bootstrapServer;
  private final String inputTopic;
  private final String outputTopic;

  public KafkaSource<String> createKafkaSource() {
    return KafkaSource.<String>builder()
        .setBootstrapServers(bootstrapServer)
        .setTopics(inputTopic)
        .setStartingOffsets(OffsetsInitializer.latest())
        .setValueOnlyDeserializer(new SimpleStringSchema())
        .build();
  }

  public KafkaSink<StockStatus> createKafkaSink() {
    return KafkaSink.<StockStatus>builder()
        .setBootstrapServers(bootstrapServer)
        .setRecordSerializer(KafkaRecordSerializationSchema.builder()
            .setTopic(outputTopic)
            .setKafkaValueSerializer(StockStatusSerializer.class)
            .build()
        )
        .build();
  }
}
