package com.university.flink.stock.processing.test;

import static com.university.stock.market.model.kafka.KafkaUtil.createAndSendRecord;

import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.kafka.KafkaUtil;
import com.university.stock.market.model.kafka.StockMarketSerializer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Test;

public class KafkaSender {

  private static KafkaProducer<String, Stock> producer = KafkaUtil.createProducer("127.0.0.1:9092",  new StockMarketSerializer<>());

  @Test
  void send() {

    String inputFilePath = "src/test/resources/samples/series_%d/input/stock_%d.json";


    List<Stock> inputStockList = IntStream.range(1, 16)
        .mapToObj(i -> String.format(inputFilePath, 1, i))
        .map(filePath -> JsonUtil.extractFromJson(Stock.class, filePath))
        .collect(Collectors.toList());

    inputStockList.forEach(stock -> {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      createAndSendRecord(producer, "stock_test", stock);
    });

  }

}
