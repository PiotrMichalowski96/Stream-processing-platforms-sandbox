package com.university.stock.processing.streams;

import com.university.stock.market.common.test.util.JsonUtil;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Slf4j
class StockStreamsConfigTest {

  private static final String INPUT_TOPIC_NAME = "inputTopic";
  private static final String OUTPUT_TOPIC_NAME = "outputTopic";

  private StockStreamsConfig kafkaStreams;

  private TopologyTestDriver testDriver;
  private TestInputTopic<String, Stock> inputTopic;
  private TestOutputTopic<String, StockStatus> outputTopic;

  @BeforeEach
  void setup() {
    StreamsBuilder builder = new StreamsBuilder();
    kafkaStreams = initializeStockStreamsConfig();

    //Create Actual Stream Processing pipeline
    kafkaStreams.stocksStream(builder);

    testDriver = new TopologyTestDriver(builder.build(), kafkaStreams.kafkaStreamConfig().asProperties());

    inputTopic = testDriver.createInputTopic(INPUT_TOPIC_NAME, new StringSerializer(), new JsonSerializer<>());
    outputTopic = testDriver.createOutputTopic(OUTPUT_TOPIC_NAME, new StringDeserializer(), new JsonDeserializer<>(StockStatus.class));
  }

  @AfterEach
  void tearDown() {
    try {
      testDriver.close();
    } catch (final RuntimeException e) {
      logger.warn("Ignoring exception, test failing in Windows due this exception: {}", e.getLocalizedMessage());
    }
  }

  @Test
  void testProcessingStreamOfStocks() {
    //given
    String inputFilePath = "src/test/resources/samples/input/stock_1_%d.json";

    List<Stock> inputStockList = IntStream.range(1, 5)
        .mapToObj(i -> String.format(inputFilePath, i))
        .map(filePath -> JsonUtil.extractFromJson(Stock.class, filePath))
        .collect(Collectors.toList());

    String outputFilePath = "src/test/resources/samples/output/stockStatus_1_%d.json";
    Map<String, StockStatus> expectedStockStatus = IntStream.range(1, 4)
        .mapToObj(i -> String.format(outputFilePath, i))
        .map(filePath -> JsonUtil.extractFromJson(StockStatus.class, filePath))
        .collect(Collectors.toMap(stockStatus -> stockStatus.getRecentQuota().getTicker(), stockStatus -> stockStatus));

    //when
    inputStockList.forEach(inputTopic::pipeInput);

    Map<String, StockStatus> actualStockStatus = outputTopic.readKeyValuesToMap();

    //then
    Assertions.assertThat(actualStockStatus).usingRecursiveComparison().isEqualTo(expectedStockStatus);
  }

  private static StockStreamsConfig initializeStockStreamsConfig() {
    StockStreamsConfig kafkaStreamsConfig = new StockStreamsConfig();
    kafkaStreamsConfig.setInputTopic(INPUT_TOPIC_NAME);
    kafkaStreamsConfig.setIntermediaryTopic("intermediaryTopic");
    kafkaStreamsConfig.setOutputTopic(OUTPUT_TOPIC_NAME);
    kafkaStreamsConfig.setAppName(RandomStringUtils.randomAlphabetic(10));
    kafkaStreamsConfig.setBootstrapAddress("1.2.3.4");
    return kafkaStreamsConfig;
  }
}