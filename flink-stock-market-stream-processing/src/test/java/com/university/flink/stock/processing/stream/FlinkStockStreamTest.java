package com.university.flink.stock.processing.stream;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.flink.stock.processing.util.CollectStockStatusSink;
import com.university.flink.stock.processing.util.FlinkClusterExtension;
import com.university.flink.stock.processing.util.TextMessageSource;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.StockStatus;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Timing for messages is important for this test. Message processing time depends on the machine performance.
 * In case test fails - try to set different time values.
 */
class FlinkStockStreamTest {

  @RegisterExtension
  private static FlinkClusterExtension flinkClusterExtension = new FlinkClusterExtension();

  @ParameterizedTest
  @CsvSource({"1, 16, 3", "2, 11, 4"})
  void testProcessingStreamOfStocks(int seriesNo, int inputMaxRange, int outputMaxRange)
      throws Exception {
    //given
    Long timeBetweenMessages = 100L; //0.1 sec
    Long timeAfterSendingMessages = 30000L; // 30sec

    String inputFilePath = "src/test/resources/samples/series_%d/input/stock_%d.json";

    List<String> inputStockList = IntStream.range(1, inputMaxRange)
        .mapToObj(i -> String.format(inputFilePath, seriesNo, i))
        .map(JsonUtil::readFileAsString)
        .collect(Collectors.toList());

    String outputFilePath = "src/test/resources/samples/series_%d/output/stockStatus_%d.json";
    Map<String, StockStatus> expectedStockStatus = IntStream.range(1, outputMaxRange)
        .mapToObj(i -> String.format(outputFilePath, seriesNo, i))
        .map(filePath -> JsonUtil.extractFromJson(StockStatus.class, filePath))
        .collect(Collectors.toMap(stockStatus -> stockStatus.getRecentQuota().getTicker(), stockStatus -> stockStatus));

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    CollectStockStatusSink collectStockStatusSink = new CollectStockStatusSink();
    CollectStockStatusSink.stockStatusMap.clear();

    TextMessageSource stockJsonSource = new TextMessageSource(inputStockList, timeBetweenMessages, timeAfterSendingMessages);

    //when
    new FlinkStockStreamBuilder(env)
        .witSourceFunction(stockJsonSource)
        .withSinkFunction(collectStockStatusSink)
        .build();

    env.execute();

    //then
    Map<String, StockStatus> result = CollectStockStatusSink.stockStatusMap;
    assertThat(result.values()).usingRecursiveComparison()
        .ignoringFields("resultMetadataDetails.processingTimeInMillis")
        .isEqualTo(expectedStockStatus.values());
  }
}