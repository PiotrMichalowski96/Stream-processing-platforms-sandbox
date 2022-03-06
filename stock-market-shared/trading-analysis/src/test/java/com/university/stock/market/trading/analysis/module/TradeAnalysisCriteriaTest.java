package com.university.stock.market.trading.analysis.module;

import static com.university.stock.market.trading.analysis.module.AnalysisUtil.buildStrategy;
import static com.university.stock.market.trading.analysis.module.AnalysisUtil.initMovingBarSeries;
import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.domain.StockStatus.TradeAction;
import com.university.stock.market.trading.analysis.util.TradeTestDataGenerator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

class TradeAnalysisCriteriaTest {

  @Test
  void shouldPerformTradeAnalysis() {
    //given
    Stock previousStock = TradeTestDataGenerator.createTradeWithPrice(110.3, 1);

    StockStatus stockStatus = StockStatus.builder()
        .recentQuota(previousStock)
        .maxPrice(BigDecimal.valueOf(120.5))
        .minPrice(BigDecimal.valueOf(100.2))
        .diffPrice(BigDecimal.valueOf(12.5))
        .tradeAction(TradeAction.BUY)
        .build();

    BarSeries series = initMovingBarSeries(20);
    Strategy strategy = buildStrategy(series);
    int endIndex = series.getEndIndex();

    Stock newTrade = TradeTestDataGenerator.createTradeWithPrice(125.3, 2);

    StockStatus expectedStockStatus = StockStatus.builder()
        .recentQuota(newTrade)
        .maxPrice(BigDecimal.valueOf(125.3).setScale(4, RoundingMode.HALF_UP))
        .minPrice(BigDecimal.valueOf(100.2))
        .diffPrice(BigDecimal.valueOf(15.0000).setScale(4, RoundingMode.HALF_UP))
        .tradeAction(TradeAction.BUY)
        .build();

    //when
    StockStatus actualStockStatus = TradingAnalysisCriteria.performTradeAnalysisBasedOnCriteria(
        stockStatus, newTrade, strategy, endIndex);

    //then
    assertThat(actualStockStatus).usingRecursiveComparison().isEqualTo(expectedStockStatus);
  }
}