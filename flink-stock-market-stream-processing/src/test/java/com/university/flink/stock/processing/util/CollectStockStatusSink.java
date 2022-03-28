package com.university.flink.stock.processing.util;

import com.university.stock.market.model.domain.StockStatus;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class CollectStockStatusSink implements SinkFunction<StockStatus> {

  public static final Map<String, StockStatus> stockStatusMap = Collections.synchronizedMap(new HashMap<>());

  @Override
  public void invoke(StockStatus value, Context context) {
    String key = value.getRecentQuota().getTicker();
    stockStatusMap.put(key, value);
  }
}
