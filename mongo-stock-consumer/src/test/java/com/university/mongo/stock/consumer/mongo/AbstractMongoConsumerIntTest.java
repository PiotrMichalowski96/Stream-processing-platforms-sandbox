package com.university.mongo.stock.consumer.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.mongo.stock.consumer.config.MongoConsumerTestConfig;
import com.university.mongo.stock.consumer.entity.StockStatusDoc;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ActiveProfiles("TEST")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = MongoConsumerTestConfig.class)
public abstract class AbstractMongoConsumerIntTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  protected void assertDocumentSavedInMongoDB(StockStatusDoc expectedDocument) {
    List<StockStatusDoc> savedDocuments = mongoTemplate.findAll(StockStatusDoc.class);
    assertThat(savedDocuments).hasSize(1);

    StockStatusDoc actualDocument = savedDocuments.get(0);
    assertThat(actualDocument).usingRecursiveComparison()
        .ignoringFields("id", "timestamp")
        .isEqualTo(expectedDocument);
  }
}
