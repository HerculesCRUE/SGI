package org.crue.hercules.sgi.tp.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.spring.boot.web.client.RestTemplateProblemCustomizer;
import org.crue.hercules.sgi.framework.web.config.OAuth2ClientConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Creación de los clientes de acceso al API rest de otros módulos.
 */
@Configuration
public class RestClientConfig extends OAuth2ClientConfiguration {
  /**
   * RestTemplate usado para el acceso al API rest de otros módulos.
   * 
   * @param restTemplateBuilder {@link RestTemplateBuilder}
   * @return {@link RestTemplate}
   */
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder.requestFactory(HttpComponentsClientHttpRequestFactory.class).build();
  }

  /**
   * Permite interceptar las peticiones Http de los RestTemplate de modo que si se
   * recibe una respuesta de tipo "application/problem+json" se construye un
   * objeto {@link Problem} a partir del cuerpo de dicha respuesta y se lanza una
   * {@link ProblemException} en él.
   * 
   * @param mapper el ObjectMapper que permite reconstruir el objeto Problem a
   *               partir del contenido de la respuesta
   * @return el RestTemplateProblemCustomizer
   */
  @Bean
  public RestTemplateProblemCustomizer restTemplateProblemCustomizer(ObjectMapper mapper) {
    return new RestTemplateProblemCustomizer(mapper);
  }
}
