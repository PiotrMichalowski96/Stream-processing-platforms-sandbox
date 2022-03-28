package com.university.flink.stock.processing.util;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

@RequiredArgsConstructor
public class TextMessageSource implements SourceFunction<String> {

  private final List<String> textMessageList;
  private final Long timeBetween;
  private final Long timeAfter;

  @Override
  public void run(SourceContext<String> ctx) throws InterruptedException {
    textMessageList.forEach(stockJson -> {
      synchronized (ctx.getCheckpointLock()) {
        ctx.collect(stockJson);
      }
      try {
        Thread.sleep(timeBetween);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });

    Thread.sleep(timeAfter);
  }

  @Override
  public void cancel() {
    // do nothing
  }
}
