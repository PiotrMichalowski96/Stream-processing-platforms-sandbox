package com.university.flink.stock.processing.stream;

import static com.university.stock.market.model.domain.ResultMetadataDetails.StreamProcessing.FLINK;
import static com.university.stock.market.model.util.ResultMetadataDetailsUtil.createMetadataDetails;

import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.domain.StockStatus.TradeAction;
import com.university.stock.market.trading.analysis.service.TradingAnalysisService;
import com.university.stock.market.trading.analysis.service.TradingAnalysisServiceImpl;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.sink.Sink;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

@Slf4j
@RequiredArgsConstructor
public class FlinkStockStreamBuilder {

  //Flink required computation module to be static
  private static final TradingAnalysisService<StockStatus, Stock> TRADING_ANALYSIS_SERVICE = new TradingAnalysisServiceImpl(Duration.ofMillis(100), 400);
  private static final String KAFKA_SOURCE_NAME = "Kafka Stock Market Source With Monotonous Timestamps Watermark Strategy";

  private final StreamExecutionEnvironment streamEnv;

  private DataStreamSource<String> inputStockStream;
  private Sink<StockStatus, ?, ?, ?> stockStatusSink;
  private SinkFunction<StockStatus> stockStatusSinkFunction;

  public FlinkStockStreamBuilder withKafkaSource(KafkaSource<String> kafkaSource) {
    this.inputStockStream = streamEnv.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), KAFKA_SOURCE_NAME);
    return this;
  }

  public FlinkStockStreamBuilder withElementSource(Collection<String> elements) {
    this.inputStockStream = streamEnv.fromCollection(elements);
    return this;
  }

  public FlinkStockStreamBuilder withKafkaSink(KafkaSink<StockStatus> kafkaSink) {
    this.stockStatusSink = kafkaSink;
    return this;
  }

  public FlinkStockStreamBuilder withSinkFunction(SinkFunction<StockStatus> sinkFunction) {
    this.stockStatusSinkFunction = sinkFunction;
    return this;
  }

  public void build() {
    SingleOutputStreamOperator<StockStatus> stream = this.inputStockStream
        .map(stock -> JsonUtil.convertToObjectFrom(Stock.class, stock))
        .map(FlinkStockStreamBuilder::mapToStockStatus)
        .filter(stockStatus -> Optional.ofNullable(stockStatus.getRecentQuota())
            .map(Stock::getTicker)
            .isPresent())
        .keyBy(stockStatus -> stockStatus.getRecentQuota().getTicker())
        .window(TumblingProcessingTimeWindows.of(Time.seconds(30)))
        .reduce((oldStatus, newStatus) -> {
          Stock newTrade = newStatus.getRecentQuota();
          StockStatus updatedStatus = TRADING_ANALYSIS_SERVICE.updateTradeAnalysis(oldStatus, newTrade);
          ResultMetadataDetails resultMetadataDetails = createMetadataDetails(FLINK, newTrade);
          updatedStatus.setResultMetadataDetails(resultMetadataDetails);
          return updatedStatus;
        })
        .filter(stockStatus -> Optional.ofNullable(stockStatus.getResultMetadataDetails()).isPresent());

    if (stockStatusSink != null) {
      stream.sinkTo(stockStatusSink);
    } else if (stockStatusSinkFunction != null) {
      stream.addSink(stockStatusSinkFunction);
    } else {
      logger.error("There is no sink added for the stream processing");
    }
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
