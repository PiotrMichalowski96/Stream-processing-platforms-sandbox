package com.university.flink.stock.processing.stream;

import static com.university.stock.market.model.domain.ResultMetadataDetails.StreamProcessing.FLINK;
import static com.university.stock.market.model.util.ResultMetadataDetailsUtil.createMetadataDetails;

import com.university.flink.stock.processing.kafka.KafkaPipelineFactory;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.domain.StockStatus.TradeAction;
import com.university.stock.market.trading.analysis.service.TradingAnalysisService;
import com.university.stock.market.trading.analysis.service.TradingAnalysisServiceImpl;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

@RequiredArgsConstructor
public class FlinkStockStream {

  //Flink required computation module to be static
  private static final TradingAnalysisService<StockStatus, Stock> TRADING_ANALYSIS_SERVICE = new TradingAnalysisServiceImpl(Duration.ofMillis(100), 400);
  private static final String SOURCE_NAME = "Kafka Stock Market Source With Monotonous Timestamps Watermark Strategy";

  private final StreamExecutionEnvironment streamEnv;
  private final KafkaSource<String> kafkaSource;
  private final KafkaSink<StockStatus> kafkaSink;


  public FlinkStockStream(StreamExecutionEnvironment streamEnv, String bootstrap,
      String inputTopic, String outputTopic) {

    KafkaPipelineFactory kafkaPipelineFactory = new KafkaPipelineFactory(bootstrap, inputTopic, outputTopic);
    this.kafkaSink = kafkaPipelineFactory.createKafkaSink();
    this.kafkaSource = kafkaPipelineFactory.createKafkaSource();
    this.streamEnv = streamEnv;
  }

  public void stockStream() {

    streamEnv.fromSource(kafkaSource, WatermarkStrategy.forMonotonousTimestamps(), SOURCE_NAME)
        .map(stock -> JsonUtil.convertToObjectFrom(Stock.class, stock))
        .map(FlinkStockStream::mapToStockStatus)
        .filter(stockStatus -> Optional.ofNullable(stockStatus.getRecentQuota())
            .map(Stock::getTicker)
            .isPresent())
        .keyBy(stockStatus -> stockStatus.getRecentQuota().getTicker())
        .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
        .reduce((newStatus, oldStatus) -> {
          Stock newTrade = newStatus.getRecentQuota();
          StockStatus updatedStatus = TRADING_ANALYSIS_SERVICE.updateTradeAnalysis(oldStatus, newTrade);
          ResultMetadataDetails resultMetadataDetails = createMetadataDetails(FLINK, newTrade);
          updatedStatus.setResultMetadataDetails(resultMetadataDetails);
          return updatedStatus;
        })
        .filter(stockStatus -> Optional.ofNullable(stockStatus.getResultMetadataDetails()).isPresent())
        .sinkTo(kafkaSink);
  }

  private static StockStatus mapToStockStatus(Stock stock) {
    BigDecimal price = stock.getPrice();
    return StockStatus.builder()
        .recentQuota(stock)
        .minPrice(price)
        .maxPrice(price)
        .diffPrice(BigDecimal.ZERO)
        .tradeAction(TradeAction.SELL)
        .build();
  }
}
