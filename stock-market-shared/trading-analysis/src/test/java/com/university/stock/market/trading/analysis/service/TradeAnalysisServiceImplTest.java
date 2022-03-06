package com.university.stock.market.trading.analysis.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.domain.StockStatus.TradeAction;
import com.university.stock.market.trading.analysis.module.TradingAnalysisModule;
import com.university.stock.market.trading.analysis.util.TradeTestDataGenerator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TradeAnalysisServiceImplTest {

  private TradingAnalysisService<StockStatus, Stock> tradingAnalysisService;

  @BeforeEach
  void init() {
    Duration tradeDuration = Duration.ofMillis(100);
    int maxBarCount = 12;
    tradingAnalysisService = new TradingAnalysisServiceImpl(tradeDuration, maxBarCount);
  }

  @Test
  void shouldUpdateTradesForOneTicker() {
    //given
    String ticker = "USD/BTC";
    List<Stock> trades = Stream.of(100, 120, 130, 150, 160, 162, 169)
        .map(value -> TradeTestDataGenerator.createTradeWithPrice(ticker, value, value))
        .collect(Collectors.toList());

    StockStatus expectedStockStatus = StockStatus.builder()
        .recentQuota(trades.get(trades.size()-1))
        .maxPrice(BigDecimal.valueOf(169).setScale(4, RoundingMode.HALF_UP))
        .minPrice(BigDecimal.valueOf(100).setScale(4, RoundingMode.HALF_UP))
        .diffPrice(BigDecimal.valueOf(7).setScale(4, RoundingMode.HALF_UP))
        .tradeAction(TradeAction.SELL)
        .build();

    //when
    Stock firstTrade = trades.get(0);
    StockStatus currentStockStatus = tradingAnalysisService.updateTradeAnalysis(new StockStatus(), firstTrade);
    for(int i = 1; i < trades.size(); i++) {
      Stock currentTrade = trades.get(i);
      currentStockStatus = tradingAnalysisService.updateTradeAnalysis(currentStockStatus, currentTrade);
    }

    //then
    assertThat(currentStockStatus).usingRecursiveComparison().isEqualTo(expectedStockStatus);

    Map<String, TradingAnalysisModule<StockStatus, Stock>> tradeAnalysisModuleMap = tradingAnalysisService.getAnalysisModuleMap();
    assertThat(tradeAnalysisModuleMap).hasSize(1);
    assertThat(tradeAnalysisModuleMap.keySet()).containsExactly(ticker);
  }

  @Test
  void shouldUpdateTradesForManyTickers() {
    //given
    String ticker1 = "USD/BTC";
    List<Stock> trades1 = Stream.of(100, 120, 130, 150, 160, 162, 169)
        .map(value -> TradeTestDataGenerator.createTradeWithPrice(ticker1, value, value))
        .collect(Collectors.toList());

    String ticker2 = "EUR/PLN";
    List<Stock> trades2 = Stream.of(101, 104, 109, 120, 122, 127, 128)
        .map(value -> TradeTestDataGenerator.createTradeWithPrice(ticker2, value, value))
        .collect(Collectors.toList());

    //when
    Stock trade1 = trades1.get(0);
    Stock trade2 = trades2.get(0);
    StockStatus currentStockStatus1 = tradingAnalysisService.updateTradeAnalysis(new StockStatus(), trade1);
    StockStatus currentStockStatus2 = tradingAnalysisService.updateTradeAnalysis(new StockStatus(), trade2);
    for(int i = 1; i < 7; i++) {
      Stock currentTrade1 = trades1.get(i);
      Stock currentTrade2 = trades2.get(i);
      currentStockStatus1 = tradingAnalysisService.updateTradeAnalysis(currentStockStatus1, currentTrade1);
      currentStockStatus2 = tradingAnalysisService.updateTradeAnalysis(currentStockStatus2, currentTrade2);
    }

    //then
    Map<String, TradingAnalysisModule<StockStatus, Stock>> tradeAnalysisModuleMap = tradingAnalysisService.getAnalysisModuleMap();
    assertThat(tradeAnalysisModuleMap).hasSize(2);
    assertThat(tradeAnalysisModuleMap.keySet()).containsExactly(ticker1, ticker2);
  }
}