package com.university.stock.market.trading.analysis.module;

/**
 * Interface for calculating live trading analysis based on previous analysis and received new trade
 */
public interface TradingAnalysisModule<T, U> {
  T initializeTradeAnalysis(U newTrade);
  T updateTradeAnalysis(T previousState, U newTrade);
}
