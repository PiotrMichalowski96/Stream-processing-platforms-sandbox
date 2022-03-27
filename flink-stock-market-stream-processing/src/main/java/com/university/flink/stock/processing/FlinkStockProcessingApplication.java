package com.university.flink.stock.processing;

import com.university.flink.stock.processing.kafka.KafkaPipelineFactory;
import com.university.flink.stock.processing.stream.FlinkStockStreamBuilder;
import com.university.stock.market.model.domain.StockStatus;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkStockProcessingApplication {

  private static final String JOB_NAME = "Stock Market Processing";

  private static final String BOOTSTRAP = "127.0.0.1:9092";
  private static final String INPUT_TOPIC = "stock_test";
  private static final String OUTPUT_TOPIC = "temporary_topic";


  public static void main(String[] args) throws Exception {

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    KafkaPipelineFactory kafkaPipelineFactory = new KafkaPipelineFactory(BOOTSTRAP, INPUT_TOPIC, OUTPUT_TOPIC);

    KafkaSource<String> kafkaSource = kafkaPipelineFactory.createKafkaSource();
    KafkaSink<StockStatus> kafkaSink = kafkaPipelineFactory.createKafkaSink();

    new FlinkStockStreamBuilder(env)
        .withKafkaSource(kafkaSource)
        .withKafkaSink(kafkaSink)
        .build();

    env.execute(JOB_NAME);
  }
}
