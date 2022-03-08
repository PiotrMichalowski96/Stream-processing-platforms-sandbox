package com.university.stock.market.trading.analysis.service;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.trading.analysis.module.TradingAnalysisModule;
import com.university.stock.market.trading.analysis.module.TradingAnalysisModuleTa4jImpl;
import java.time.Duration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TradingAnalysisServiceImpl extends TradingAnalysisService<StockStatus, Stock> {

  private final Duration  tradeDuration;
  private final int maxBarCount;

  @Override
  protected String retrieveAnalysisModuleKey(Stock newTrade) {
    return newTrade.getTicker();
  }

  @Override
  protected TradingAnalysisModule<StockStatus, Stock> createTradeAnalysisModule() {
    return new TradingAnalysisModuleTa4jImpl(maxBarCount, tradeDuration);
  }
}
