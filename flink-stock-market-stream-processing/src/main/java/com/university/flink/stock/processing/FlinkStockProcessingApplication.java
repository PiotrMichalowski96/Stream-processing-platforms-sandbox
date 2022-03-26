package com.university.flink.stock.processing;

import com.university.flink.stock.processing.stream.FlinkStockStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkStockProcessingApplication {

  private static final String JOB_NAME = "Stock Market Processing";

  private static final String BOOTSTRAP = "127.0.0.1:9092";
  private static final String INPUT_TOPIC = "stock_test";
  private static final String OUTPUT_TOPIC = "temporary_topic";


  public static void main(String[] args) throws Exception {

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    FlinkStockStream stockStream = new FlinkStockStream(env, BOOTSTRAP, INPUT_TOPIC, OUTPUT_TOPIC);

    stockStream.stockStream();

    env.execute(JOB_NAME);
  }
}
