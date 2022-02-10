package com.university.stock.processing.streams;

import com.university.stock.model.domain.Stock;
import com.university.stock.model.domain.StockStatus;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Slf4j
@Configuration
@EnableKafka
@EnableKafkaStreams
@Getter
@Setter
public class StockStreamsConfig {

  @Value("${kafka.bootstrapAddress}")
  private String bootstrapAddress;

  @Value("${application.name}")
  private String appName;

  @Value("${kafka.topic.stock}")
  private String inputTopic;

  @Value("${kafka.topic.intermediary}")
  private String intermediaryTopic;

  @Value("${kafka.topic.statistic}")
  private String outputTopic;

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public KafkaStreamsConfiguration kafkaStreamConfig() {
    Map<String, Object> properties = new HashMap<>();

    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());

    return new KafkaStreamsConfiguration(properties);
  }

  @Bean
  public KStream<String, Stock> stocksStream(StreamsBuilder streamsBuilder) {

    final Serde<Stock> stockSerde = new JsonSerde<>(Stock.class);
    final Serde<StockStatus> stockStatusSerde = new JsonSerde<>(StockStatus.class);

    KStream<String, Stock> stockStream = streamsBuilder.stream(inputTopic, Consumed.with(Serdes.String(), stockSerde));

    stockStream
        .filter((k, stock) -> Objects.nonNull(stock))
        .selectKey((ignoredKey, stock) -> stock.getTicker())
        .to(intermediaryTopic, Produced.with(Serdes.String(), stockSerde));

    KTable<String, StockStatus> stockStatusTable = streamsBuilder
        .stream(intermediaryTopic, Consumed.with(Serdes.String(), stockSerde))
        .groupByKey(Grouped.with(Serdes.String(), stockSerde))
        .aggregate(
            this::initializeStockStatus,
            (key, stock, stockStatus) -> this.calculateStockStatus(stockStatus, stock),
            Materialized.<String, StockStatus, KeyValueStore<Bytes, byte[]>>as("stock-status-agg")
                .withKeySerde(Serdes.String())
                .withValueSerde(stockStatusSerde)
        );

    stockStatusTable.toStream().to(outputTopic, Produced.with(Serdes.String(), stockStatusSerde));
    return stockStream;
  }

  private StockStatus initializeStockStatus() {
    Stock stock = new Stock();
    stock.setExchange(BigDecimal.ZERO);

    return StockStatus.builder()
        .recentQuota(stock)
        .diffExchange(BigDecimal.ZERO)
        .minExchange(BigDecimal.ZERO)
        .maxExchange(BigDecimal.ZERO)
        .build();
  }

  private StockStatus calculateStockStatus(StockStatus previousStockStatus, Stock stock) {
    BigDecimal updatedExchange = Optional.ofNullable(stock)
        .map(Stock::getExchange)
        .orElse(BigDecimal.ZERO);

    BigDecimal diff = Optional.ofNullable(previousStockStatus.getRecentQuota())
        .map(Stock::getExchange)
        .map(previousExchange -> previousExchange.subtract(updatedExchange))
        .orElse(updatedExchange);

    BigDecimal minExchange = Optional.ofNullable(previousStockStatus.getRecentQuota())
        .map(Stock::getExchange)
        .filter(previousExchange -> previousExchange.compareTo(updatedExchange) < 0)
        .orElse(updatedExchange);

    BigDecimal maxExchange = Optional.ofNullable(previousStockStatus.getRecentQuota())
        .map(Stock::getExchange)
        .filter(previousExchange -> previousExchange.compareTo(updatedExchange) > 0)
        .orElse(updatedExchange);

    StockStatus stockStatus = StockStatus.builder()
        .recentQuota(stock)
        .diffExchange(diff)
        .minExchange(minExchange)
        .maxExchange(maxExchange)
        .build();

    logger.debug("Updating stock statistic: {}", stockStatus.toString());

    return stockStatus;
  }
}
