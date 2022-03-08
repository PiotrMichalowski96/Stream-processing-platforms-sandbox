package com.university.stock.market.trading.analysis.service;

import com.university.stock.market.trading.analysis.module.TradingAnalysisModule;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * This class is responsible for running trading analysis module - one module per trade ticker
 * @param <T> represents result of the trade analysis
 * @param <U> represents new trade event
 */
@Getter
public abstract class TradingAnalysisService<T, U> {

  private final Map<String, TradingAnalysisModule<T, U>> analysisModuleMap;

  public TradingAnalysisService() {
    this.analysisModuleMap = new HashMap<>();
  }

  public T updateTradeAnalysis(T previousState, U newTrade) {

    String analysisModuleKey = retrieveAnalysisModuleKey(newTrade);

    if (!analysisModuleMap.containsKey(analysisModuleKey)) {
      TradingAnalysisModule<T, U> newAnalysisModule = createTradeAnalysisModule();
      analysisModuleMap.put(analysisModuleKey, newAnalysisModule);
      return newAnalysisModule.initializeTradeAnalysis(newTrade);
    }

    TradingAnalysisModule<T, U> analysisModule = analysisModuleMap.get(analysisModuleKey);
    return analysisModule.updateTradeAnalysis(previousState, newTrade);
  }

  protected abstract String retrieveAnalysisModuleKey(U newTrade);

  protected abstract TradingAnalysisModule<T, U> createTradeAnalysisModule();
}
