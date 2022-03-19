package com.university.stock.market.model.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ResultMetadataDetails implements Serializable {

  private StreamProcessing streamProcessing;
  private Long processingTimeInMillis;

  public enum StreamProcessing {
    KAFKA_STREAMS, AWS_KINESIS_LAMBDA, SPARK, FLINK
  }
}
