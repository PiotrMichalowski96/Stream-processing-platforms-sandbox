package com.university.stock.market.consumer.util;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Message {
  private String id;
  private LocalDateTime timestamp;
  private String message;
}
