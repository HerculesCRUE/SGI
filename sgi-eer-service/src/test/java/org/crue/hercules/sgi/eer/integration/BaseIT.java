package org.crue.hercules.sgi.eer.integration;

import org.crue.hercules.sgi.framework.test.http.client.BufferingHttpComponentsClientHttpRequestFactory;
import org.crue.hercules.sgi.framework.test.http.client.SgiClientHttpRequestInterceptors;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public abstract class BaseIT {

  @Autowired
  protected TestRestTemplate restTemplate;

  @Autowired
  protected TokenBuilder tokenBuilder;

  @TestConfiguration
  static class TestRestTemplateConfiguration {
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
      return new RestTemplateBuilder().additionalInterceptors(SgiClientHttpRequestInterceptors.logOnError())
          // The BufferingHttpComponentsClientHttpRequestFactory is required as we want to
          // use the response body both in the interceptor and for the initial calling
          // code. The default implementation allows to read the response body only once.
          .requestFactory(BufferingHttpComponentsClientHttpRequestFactory.class);
    }
  }

}