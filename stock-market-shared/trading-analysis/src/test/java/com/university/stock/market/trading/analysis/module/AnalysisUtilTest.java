package com.university.stock.market.trading.analysis.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;

class AnalysisUtilTest {

  @Test
  void shouldInitBarSeries() {
    //given
    int maxBarCount = 30;

    //when
    BarSeries barSeries = AnalysisUtil.initMovingBarSeries(maxBarCount);

    //then
    assertThat(barSeries).isNotNull();
    assertThat(barSeries.getMaximumBarCount()).isEqualTo(maxBarCount);
  }

  @Test
  void shouldCreateStrategy() {
    //given
    BarSeries barSeries = new BaseBarSeries();

    //when
    Strategy strategy = AnalysisUtil.buildStrategy(barSeries);

    //then
    assertThat(strategy).isNotNull();
    assertThat(strategy).isExactlyInstanceOf(BaseStrategy.class);
  }

  @Test
  void shouldNotCreateStrategyButThrowException() {
    assertThatThrownBy(() -> AnalysisUtil.buildStrategy(null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}