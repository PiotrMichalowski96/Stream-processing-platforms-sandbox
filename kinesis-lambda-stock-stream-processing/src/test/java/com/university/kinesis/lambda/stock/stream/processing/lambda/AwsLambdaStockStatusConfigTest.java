package com.university.kinesis.lambda.stock.stream.processing.lambda;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.Record;
import com.university.kinesis.lambda.stock.stream.processing.kinesis.KinesisStockStatusRepository;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.trading.analysis.service.TradingAnalysisService;
import com.university.stock.market.trading.analysis.service.TradingAnalysisServiceImpl;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AwsLambdaStockStatusConfigTest {

  private static final String INPUT_FILE_PATH = "src/test/resources/samples/series_%d/input/stock_%d.json";
  private static final String OUTPUT_FILE_PATH = "src/test/resources/samples/series_%d/output/stockStatus_%d.json";

  @Mock
  private KinesisStockStatusRepository stockStatusRepository;

  @Captor
  private ArgumentCaptor<StockStatus> stockStatusArgumentCaptor;

  @Spy
  private TradingAnalysisService<StockStatus, Stock> tradingAnalysisService = new TradingAnalysisServiceImpl(
      Duration.ofMillis(100), 400);

  @InjectMocks
  private AwsLambdaStockStatusConfig config;

  @ParameterizedTest
  @CsvSource({"1, 15, 2", "2, 10, 3"})
  void shouldProcessSequenceOfMessages(int seriesNo, int inputMessageNo, int outputMessageNo) {
    //given
    Consumer<KinesisEvent> kinesisStreamProcessor = config.kinesisRecordsProcessing();

    List<KinesisEvent> inputKinesisEvents =  IntStream.range(1, inputMessageNo+1)
        .mapToObj(i -> String.format(INPUT_FILE_PATH, seriesNo, i))
        .map(JsonUtil::readFileAsString)
        .map(this::createInputKinesisEventWith)
        .collect(Collectors.toList());

    Map<String, StockStatus> expectedStockStatus = IntStream.range(1, outputMessageNo+1)
        .mapToObj(i -> String.format(OUTPUT_FILE_PATH, seriesNo, i))
        .map(filePath -> JsonUtil.extractFromJson(StockStatus.class, filePath))
        .collect(Collectors.toMap(stockStatus -> stockStatus.getRecentQuota().getTicker(), stockStatus -> stockStatus));

    //when
    inputKinesisEvents.forEach(kinesisStreamProcessor);

    //then
    verify(stockStatusRepository, times(inputMessageNo)).send(stockStatusArgumentCaptor.capture());

    Map<String, StockStatus> actualStockStatus = new HashMap<>();
    stockStatusArgumentCaptor.getAllValues()
        .forEach(stockStatus -> actualStockStatus.put(stockStatus.getRecentQuota().getTicker(), stockStatus));

    assertThat(actualStockStatus).usingRecursiveComparison().isEqualTo(expectedStockStatus);
  }

  private KinesisEvent createInputKinesisEventWith(String message) {
    Record kinesis = convertMessageToKinesisRecord(message);

    KinesisEventRecord kinesisEventRecord = new KinesisEventRecord();
    kinesisEventRecord.setKinesis(kinesis);

    KinesisEvent kinesisEvent = new KinesisEvent();
    kinesisEvent.setRecords(Collections.singletonList(kinesisEventRecord));
    return kinesisEvent;
  }

  private Record convertMessageToKinesisRecord(String message) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
    Record kinesis = new Record();
    kinesis.setData(byteBuffer);
    return kinesis;
  }
}