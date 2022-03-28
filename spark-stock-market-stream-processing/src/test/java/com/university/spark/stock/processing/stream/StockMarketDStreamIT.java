package com.university.spark.stock.processing.stream;

import static com.university.stock.market.model.kafka.KafkaUtil.createAndSendRecord;
import static com.university.stock.market.model.kafka.KafkaUtil.readKeyStockStatusToMap;
import static org.assertj.core.api.Assertions.assertThat;

import com.university.spark.stock.processing.config.SparkKafkaConfigRetriever;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.kafka.KafkaUtil;
import com.university.stock.market.model.kafka.StockMarketDeserializer;
import com.university.stock.market.model.kafka.StockMarketSerializer;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
class StockMarketDStreamIT {

  private static final SparkConf SPARK_CONF = new SparkConf().setAppName("sparkStockProcessingTest").setMaster("local[*]");
  private static final SparkKafkaConfigRetriever CONFIG_RETRIEVER = new SparkKafkaConfigRetriever("application-test.properties");

  @Container
  private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

  private static Properties streamProducerProp;
  private static Map<String, Object> streamProp ;

  private final String inputTopic = CONFIG_RETRIEVER.getInputTopic();
  private final String outputTopic = CONFIG_RETRIEVER.getOutputTopic();

  private static KafkaProducer<String, Stock> producer;
  private static KafkaConsumer<String, StockStatus> consumer;

  private JavaStreamingContext sc;

  @BeforeAll
  static void init() {
    String bootstrapServer = KAFKA_CONTAINER.getBootstrapServers();
    CONFIG_RETRIEVER.getConfig().addProperty("kafka.bootstrapAddress", bootstrapServer);

    streamProducerProp = CONFIG_RETRIEVER.createKafkaProducerProperties();
    streamProp = CONFIG_RETRIEVER.configureKafkaParams();

    producer = KafkaUtil.createProducer(bootstrapServer,  new StockMarketSerializer<>());
    consumer = KafkaUtil.createConsumer(bootstrapServer, new StockMarketDeserializer<>(StockStatus.class));
  }

  @BeforeEach
  void setUp() {
    sc = new JavaStreamingContext(SPARK_CONF, Durations.seconds(1));
    StockMarketDStream stockMarketStream = new StockMarketDStream(sc, inputTopic, outputTopic, streamProducerProp);
    stockMarketStream.stockStream(streamProp);
    sc.start();
    consumer.subscribe(List.of(outputTopic));
  }

  @AfterEach
  void cleanUpEach() {
    sc.stop();
  }

  @AfterAll
  static void close() {
    consumer.close();
    producer.close();
  }

  @Test
  @Order(1)
  void testKafkaContainer() {
    assertThat(KAFKA_CONTAINER.isRunning()).isTrue();
  }

  @Order(2)
  @ParameterizedTest
  @CsvSource({"1, 16, 3", "2, 11, 4"})
  void testProcessingStreamOfStocks(int seriesNo, int inputMaxRange, int outputMaxRange) throws InterruptedException {
    //given
    String inputFilePath = "src/test/resources/samples/series_%d/input/stock_%d.json";

    List<Stock> inputStockList = IntStream.range(1, inputMaxRange)
        .mapToObj(i -> String.format(inputFilePath, seriesNo, i))
        .map(filePath -> JsonUtil.extractFromJson(Stock.class, filePath))
        .collect(Collectors.toList());

    String outputFilePath = "src/test/resources/samples/series_%d/output/stockStatus_%d.json";
    Map<String, StockStatus> expectedStockStatus = IntStream.range(1, outputMaxRange)
        .mapToObj(i -> String.format(outputFilePath, seriesNo, i))
        .map(filePath -> JsonUtil.extractFromJson(StockStatus.class, filePath))
        .collect(Collectors.toMap(stockStatus -> stockStatus.getRecentQuota().getTicker(), stockStatus -> stockStatus));

    //when
    inputStockList.forEach(stock -> createAndSendRecord(producer, inputTopic, stock));

    Thread.sleep(5000);

    Map<String, StockStatus> actualStockStatus = readKeyStockStatusToMap(consumer);

    //then
    assertThat(actualStockStatus.keySet()).usingRecursiveComparison()
        .isEqualTo(expectedStockStatus.keySet());

    assertThat(actualStockStatus.values()).usingRecursiveComparison()
        .ignoringFields("resultMetadataDetails.processingTimeInMillis")
        .isEqualTo(expectedStockStatus.values());
  }
}