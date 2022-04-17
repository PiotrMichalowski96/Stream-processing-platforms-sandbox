package com.university.mongo.stock.consumer.mapper;

import com.university.mongo.stock.consumer.entity.StockStatusDoc;
import com.university.stock.market.consumer.mapper.MessageMapper;
import com.university.stock.market.model.domain.StockStatus;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public abstract class StockStatusDocMapper implements MessageMapper<StockStatusDoc> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
  @Mapping(target = "streamPlatform", source = "stockStatus.resultMetadataDetails.streamProcessing")
  @Mapping(target = "processingTimeInMillis", source = "stockStatus.resultMetadataDetails.processingTimeInMillis")
  @Mapping(target = "experimentCase", source = "stockStatus.recentQuota.inputMetadataDetails.experimentCase")
  @Mapping(target = "comment", source = "stockStatus.recentQuota.inputMetadataDetails.description")
  @Mapping(target = "message", source = "json")
  public abstract StockStatusDoc toMessage(StockStatus stockStatus, String json);
}
