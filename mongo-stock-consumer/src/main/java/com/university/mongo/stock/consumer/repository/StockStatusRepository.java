package com.university.mongo.stock.consumer.repository;

import com.university.mongo.stock.consumer.entity.StockStatusDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockStatusRepository extends MongoRepository<StockStatusDoc, String> {
}
