package org.crue.hercules.sgi.csp.integration;

import org.crue.hercules.sgi.framework.test.context.support.SgiTestProfileResolver;
import org.crue.hercules.sgi.framework.test.http.client.BufferingHttpComponentsClientHttpRequestFactory;
import org.crue.hercules.sgi.framework.test.http.client.SgiClientHttpRequestInterceptors;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
@ActiveProfiles(resolver = SgiTestProfileResolver.class)
public class BaseIT {

  @Autowired
  protected TestRestTemplate restTemplate;

  @Autowired
  protected TokenBuilder tokenBuilder;

  @TestConfiguration
  static class TestRestTemplateConfiguration {
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
      return new RestTemplateBuilder().additionalInterceptors(SgiClientHttpRequestInterceptors.printOnError())
          // The BufferingHttpComponentsClientHttpRequestFactory is required as we want to
          // use the response body both in the interceptor and for the initial calling
          // code. The default implementation allows to read the response body only once.
          .requestFactory(BufferingHttpComponentsClientHttpRequestFactory.class);
    }
  }

}