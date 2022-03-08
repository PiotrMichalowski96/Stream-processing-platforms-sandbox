package com.university.stock.market.trading.analysis.module;

import static com.university.stock.market.trading.analysis.util.TradeTestDataGenerator.createTradeWithPrice;
import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.domain.StockStatus.TradeAction;
import com.university.stock.market.trading.analysis.util.TradeTestDataGenerator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TradingAnalysisModuleTa4jImplTest {

  private TradingAnalysisModuleTa4jImpl tradingModule;

  @BeforeEach
  void init() {
    tradingModule = new TradingAnalysisModuleTa4jImpl(12, Duration.ofMillis(100));
  }

  @Test
  void shouldInitializeAnalysis() {
    //given
    Stock newTrade = createTradeWithPrice(110.3, 100);

    BigDecimal price = newTrade.getPrice();

    StockStatus expectedStockStatus = StockStatus.builder()
        .recentQuota(newTrade)
        .maxPrice(price)
        .minPrice(price)
        .diffPrice(BigDecimal.ZERO)
        .tradeAction(TradeAction.SELL)
        .build();

    //when
    StockStatus actualStockStatus = tradingModule.initializeTradeAnalysis(newTrade);

    //then
    assertThat(actualStockStatus).usingRecursiveComparison().isEqualTo(expectedStockStatus);
  }

  @Test
  void shouldUpdateTradeAnalysis() {
    //given
    Stock firstTrade = createTradeWithPrice(110.3, 100);

    StockStatus stockStatus = tradingModule.initializeTradeAnalysis(firstTrade);

    Stock newTrade = createTradeWithPrice(125.3, 100);

    StockStatus expectedStockStatus = StockStatus.builder()
        .recentQuota(newTrade)
        .maxPrice(BigDecimal.valueOf(125.3).setScale(4, RoundingMode.HALF_UP))
        .minPrice(BigDecimal.valueOf(110.3).setScale(4, RoundingMode.HALF_UP))
        .diffPrice(BigDecimal.valueOf(15.0000).setScale(4, RoundingMode.HALF_UP))
        .tradeAction(TradeAction.SELL)
        .build();

    //when
    StockStatus actualStockStatus = tradingModule.updateTradeAnalysis(stockStatus, newTrade);

    //then
    assertThat(actualStockStatus).usingRecursiveComparison().isEqualTo(expectedStockStatus);
  }

  @ParameterizedTest
  @MethodSource("providePricesAndExpectedStockStatus")
  void shouldUpdateTradeAnalysisForTradeSequence(List<Stock> tradeList, StockStatus expectedStockStatus) {
    //given input
    //when
    StockStatus currentStockStatus = tradingModule.initializeTradeAnalysis(tradeList.get(0));

    for(int i = 1; i < tradeList.size(); i++) {
      Stock currentTrade = tradeList.get(i);
      currentStockStatus = tradingModule.updateTradeAnalysis(currentStockStatus, currentTrade);
    }

    //then
    assertThat(currentStockStatus).isNotNull();
    assertThat(currentStockStatus).usingRecursiveComparison().isEqualTo(expectedStockStatus);
  }

  private static Stream<Arguments> providePricesAndExpectedStockStatus() {

    List<Stock> increasingPrices = Stream.of(100, 120, 130, 150, 160, 162, 169)
        .map(value -> TradeTestDataGenerator.createTradeWithPrice(value, value)) //time needs to increase
        .collect(Collectors.toList());

    StockStatus stockStatusForIncreasingPrices = StockStatus.builder()
        .recentQuota(increasingPrices.get(increasingPrices.size()-1))
        .maxPrice(BigDecimal.valueOf(169).setScale(4, RoundingMode.HALF_UP))
        .minPrice(BigDecimal.valueOf(100).setScale(4, RoundingMode.HALF_UP))
        .diffPrice(BigDecimal.valueOf(7).setScale(4, RoundingMode.HALF_UP))
        .tradeAction(TradeAction.SELL)
        .build();

    List<Stock> decreasingPrices = Stream.of(180, 178, 172, 167, 160, 158, 155)
        .map(value -> TradeTestDataGenerator.createTradeWithPrice(value, 200 - value)) //time needs to increase
        .collect(Collectors.toList());

    StockStatus stockStatusForDecreasingPrices = StockStatus.builder()
        .recentQuota(decreasingPrices.get(decreasingPrices.size()-1))
        .maxPrice(BigDecimal.valueOf(180).setScale(4, RoundingMode.HALF_UP))
        .minPrice(BigDecimal.valueOf(155).setScale(4, RoundingMode.HALF_UP))
        .diffPrice(BigDecimal.valueOf(-3).setScale(4, RoundingMode.HALF_UP))
        .tradeAction(TradeAction.BUY)
        .build();

    return Stream.of(
        Arguments.of(increasingPrices, stockStatusForIncreasingPrices),
        Arguments.of(decreasingPrices, stockStatusForDecreasingPrices)
    );
  }
}