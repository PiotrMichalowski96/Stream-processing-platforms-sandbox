package com.university.flink.stock.processing;

import com.university.flink.stock.processing.kafka.KafkaPipelineFactory;
import com.university.flink.stock.processing.stream.FlinkStockStreamBuilder;
import com.university.stock.market.model.domain.StockStatus;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkStockProcessingApplication {

  private static final String JOB_NAME = "Stock Market Processing";

  public static void main(String[] args) throws Exception {

    inputArgSanityCheck(args);
    String bootstrap = args[0];
    String inputTopic = args[1];
    String outputTopic = args[2];

    KafkaPipelineFactory kafkaPipelineFactory = new KafkaPipelineFactory(bootstrap, inputTopic, outputTopic);

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    KafkaSource<String> kafkaSource = kafkaPipelineFactory.createKafkaSource();
    KafkaSink<StockStatus> kafkaSink = kafkaPipelineFactory.createKafkaSink();

    new FlinkStockStreamBuilder(env)
        .withKafkaSource(kafkaSource)
        .withKafkaSink(kafkaSink)
        .build();

    env.execute(JOB_NAME);
  }

  private static void inputArgSanityCheck(String[] args) {
    String errorMessage = "Wrong arguments. Need to add in order: Bootstrap adress, input topic and output topic.";
    if (args.length < 3 || Arrays.stream(args).anyMatch(StringUtils::isBlank)) {
      throw new RuntimeException(errorMessage);
    }
  }
}
