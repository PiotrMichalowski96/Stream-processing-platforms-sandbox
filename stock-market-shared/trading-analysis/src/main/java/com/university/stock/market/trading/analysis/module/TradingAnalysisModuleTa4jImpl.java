package com.university.stock.market.trading.analysis.module;

import static com.university.stock.market.trading.analysis.module.TradingAnalysisCriteria.performTradeAnalysisBasedOnCriteria;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.domain.StockStatus.TradeAction;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.Strategy;

/**
 * Implements Trading Analysis using Ta4j library
 */
@Slf4j
public class TradingAnalysisModuleTa4jImpl implements TradingAnalysisModule<StockStatus, Stock> {

  @NonNull
  private final Duration tradeBarDuration;
  @NonNull
  private final BarSeries series;
  @NonNull
  private final Strategy strategy;

  public TradingAnalysisModuleTa4jImpl(int maxBarCount, Duration tradeBarDuration) {
    this.tradeBarDuration = tradeBarDuration;
    this.series = AnalysisUtil.initMovingBarSeries(maxBarCount);
    this.strategy = AnalysisUtil.buildStrategy(series);
  }

  @Override
  public StockStatus initializeTradeAnalysis(Stock newTrade) {
    Bar initBar = createBarFrom(newTrade);
    series.addBar(initBar);

    BigDecimal price = newTrade.getPrice();
    return StockStatus.builder()
        .recentQuota(newTrade)
        .diffPrice(BigDecimal.ZERO)
        .minPrice(price)
        .maxPrice(price)
        .tradeAction(TradeAction.SELL)
        .build();
  }

  @Override
  public StockStatus updateTradeAnalysis(StockStatus stockStatus, Stock newTrade) {
    Bar newBar = createBarFrom(newTrade);
    series.addBar(newBar);

    int endIndex = series.getEndIndex();
    StockStatus analyzedStatus = performTradeAnalysisBasedOnCriteria(stockStatus, newTrade, strategy, endIndex);

    logger.debug("Updating stock statistic: {}", analyzedStatus);
    return analyzedStatus;
  }

  private Bar createBarFrom(Stock trade) {
    BigDecimal price = trade.getPrice();
    BigDecimal volume = Optional.ofNullable(trade.getVolume())
        .map(BigDecimal::new)
        .orElse(BigDecimal.ZERO);
    ZonedDateTime barTimestamp = createBarTimestamp(trade);
    return new BaseBar(tradeBarDuration, barTimestamp, price, price, price, price, volume);
  }

  private ZonedDateTime createBarTimestamp(Stock trade) {
    ZonedDateTime timestamp = trade.getTimestamp().atZone(ZoneOffset.UTC);
    if (series.isEmpty()) {
      return timestamp;
    }
    return Optional.ofNullable(series.getLastBar())
        .map(Bar::getEndTime)
        .filter(endTime -> endTime.isAfter(timestamp) || endTime.isEqual(timestamp))
        .map(endTime -> endTime.plusSeconds(1))
        .orElse(timestamp);
  }
}
