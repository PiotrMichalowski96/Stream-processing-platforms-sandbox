package com.university.elasticsearch.stock.consumer.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class StockElasticMessage {

  private String id;
  private LocalDateTime timestamp;
  private String streamPlatform;
  private Long processingTimeInMillis;
  private String experimentCase;
  private String comment;
  private String message;
}
