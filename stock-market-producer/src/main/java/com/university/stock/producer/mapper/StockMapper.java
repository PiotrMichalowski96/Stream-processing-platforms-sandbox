package com.university.stock.producer.mapper;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.dto.QuoteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMapper extends MapperMethods {

  @Mapping(target = "ticker", source = "symbol")
  @Mapping(target = "timestamp", source = "timestamp", qualifiedBy = TimestampMapper.class)
  @Mapping(target = "volume", source = "dayVolume")
  Stock toStock(QuoteDTO quoteDTO);
}
