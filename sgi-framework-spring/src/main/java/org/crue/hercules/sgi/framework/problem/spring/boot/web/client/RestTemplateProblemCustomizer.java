package org.crue.hercules.sgi.framework.problem.spring.boot.web.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

/**
 * Customizes a {@link RestTemplate} to add a
 * {@link RestTemplateProblemInterceptor}.
 */
public class RestTemplateProblemCustomizer implements RestTemplateCustomizer {
  private ObjectMapper mapper;

  /**
   * Creates new {@link RestTemplateProblemCustomizer}
   * 
   * @param mapper the {@link ObjectMapper} to use to create the
   *               {@link RestTemplateProblemInterceptor}
   */
  public RestTemplateProblemCustomizer(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Customizes the {@link RestTemplate} instance adding a
   * {@link RestTemplateProblemInterceptor}.
   * 
   * @param restTemplate the template to customize
   */
  @Override
  public void customize(RestTemplate restTemplate) {
    restTemplate.getInterceptors().add(new RestTemplateProblemInterceptor(mapper));
  }

}
