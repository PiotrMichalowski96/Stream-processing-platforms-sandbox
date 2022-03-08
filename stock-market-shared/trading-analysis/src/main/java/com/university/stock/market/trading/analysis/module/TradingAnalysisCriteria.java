package com.university.stock.market.trading.analysis.module;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.domain.StockStatus.TradeAction;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;
import org.ta4j.core.Strategy;

enum TradingAnalysisCriteria {

  CALCULATE_MAX_PRICE() {
    @Override
    public void updateStockStatus(StockStatus stockStatus,
        Stock newTrade,
        Strategy strategy,
        int endIndex) {

      BigDecimal maxPrice = Optional.ofNullable(stockStatus.getMaxPrice())
          .orElse(BigDecimal.valueOf(Double.MIN_VALUE));

      Optional.ofNullable(newTrade)
          .map(Stock::getPrice)
          .filter(newPrice -> newPrice.compareTo(maxPrice) > 0)
          .ifPresent(stockStatus::setMaxPrice);
    }
  },
  CALCULATE_MIN_PRICE() {
    @Override
    public void updateStockStatus(StockStatus stockStatus,
        Stock newTrade,
        Strategy strategy,
        int endIndex) {

      BigDecimal minPrice = Optional.ofNullable(stockStatus.getMinPrice())
          .orElse(BigDecimal.valueOf(Double.MAX_VALUE));

      Optional.ofNullable(newTrade)
          .map(Stock::getPrice)
          .filter(newPrice -> newPrice.compareTo(minPrice) < 0)
          .ifPresent(stockStatus::setMinPrice);
    }
  },
  CALCULATE_DIFFERENCE_PRICE() {
    @Override
    public void updateStockStatus(StockStatus stockStatus,
        Stock newTrade,
        Strategy strategy,
        int endIndex) {

      BigDecimal oldPrice = Optional.ofNullable(stockStatus.getRecentQuota())
          .map(Stock::getPrice)
          .orElse(BigDecimal.ZERO);

      Optional.ofNullable(newTrade)
          .map(Stock::getPrice)
          .map(newPrice -> newPrice.subtract(oldPrice))
          .ifPresent(stockStatus::setDiffPrice);
    }
  },
  SHOULD_EXIT() {
    @Override
    public void updateStockStatus(StockStatus stockStatus,
        Stock newTrade,
        Strategy strategy,
        int endIndex) {

      if (strategy.shouldExit(endIndex)) {
        stockStatus.setTradeAction(TradeAction.SELL);
      }
    }
  },
  SHOULD_BUY() {
    @Override
    public void updateStockStatus(StockStatus stockStatus,
        Stock newTrade,
        Strategy strategy,
        int endIndex) {

      if (strategy.shouldEnter(endIndex)) {
        stockStatus.setTradeAction(TradeAction.BUY);
      }
    }
  };

  abstract void updateStockStatus(StockStatus stockStatus,
      Stock newTrade,
      Strategy strategy,
      int endIndex);

  static StockStatus performTradeAnalysisBasedOnCriteria(StockStatus stockStatus,
      Stock newTrade,
      Strategy strategy,
      int endIndex) {

    Stream.of(TradingAnalysisCriteria.values())
        .forEach(criteria -> criteria.updateStockStatus(stockStatus, newTrade, strategy, endIndex));

    stockStatus.setRecentQuota(newTrade);
    return stockStatus;
  }
}
