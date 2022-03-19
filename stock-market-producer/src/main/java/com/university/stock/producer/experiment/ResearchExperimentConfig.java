package com.university.stock.producer.experiment;

import com.university.stock.market.model.domain.InputMetadataDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration is exposing hardcoded metadata of stream processing events.
 * It is information required during comparison of different stream processing platform.
 * It describes research experiment case.
 */
@Configuration
public class ResearchExperimentConfig {

  @Bean
  public InputMetadataDetails inputMetadataDetails(
      @Value("${master.thesis.experiment.case}") String experimentCase,
      @Value("${master.thesis.experiment.description}") String experimentDescription) {

    return InputMetadataDetails.builder()
        .experimentCase(experimentCase)
        .description(experimentDescription)
        .build();
  }
}
