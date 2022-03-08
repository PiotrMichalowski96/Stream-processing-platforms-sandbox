package com.university.stock.market.trading.analysis.module;

import lombok.experimental.UtilityClass;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

@UtilityClass
class AnalysisUtil {

  private static final int SMA_BAR_COUNT = 12;

  /**
   * Builds a moving bar series (i.e. keeping only the maxBarCount last bars)
   *
   * @param maxBarCount the number of bars to keep in the bar series (at maximum)
   * @return a moving bar series
   */
  static BarSeries initMovingBarSeries(int maxBarCount) {
    BarSeries series = new BaseBarSeries();
    series.setMaximumBarCount(maxBarCount);
    return series;
  }

  /**
   * @param series a bar series
   * @return a custom strategy
   */
  static Strategy buildStrategy(BarSeries series) {
    if (series == null) {
      throw new IllegalArgumentException("Series cannot be null");
    }
    ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
    SMAIndicator sma = new SMAIndicator(closePrice, SMA_BAR_COUNT);

    // Signals
    // Buy when SMA goes over close price
    // Sell when close price goes over SMA
    return new BaseStrategy(new OverIndicatorRule(sma, closePrice), new UnderIndicatorRule(sma, closePrice));
  }
}
