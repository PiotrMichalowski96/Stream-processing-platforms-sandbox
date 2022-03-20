package com.university.elasticsearch.stock.consumer.mapper;

import com.university.elasticsearch.stock.consumer.model.StockElasticMessage;
import com.university.stock.market.model.domain.StockStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ElasticMessageMapper extends MapperMethods {

  @Mapping(target = "id", source = "json", qualifiedBy = HashIdMapper.class)
  @Mapping(target = "streamPlatform", source = "stockStatus.resultMetadataDetails.streamProcessing")
  @Mapping(target = "processingTimeInMillis", source = "stockStatus.resultMetadataDetails.processingTimeInMillis")
  @Mapping(target = "experimentCase", source = "stockStatus.recentQuota.inputMetadataDetails.experimentCase")
  @Mapping(target = "comment", source = "stockStatus.recentQuota.inputMetadataDetails.description")
  @Mapping(target = "message", source = "json")
  StockElasticMessage toStockElasticMessage(StockStatus stockStatus, String json);
}
