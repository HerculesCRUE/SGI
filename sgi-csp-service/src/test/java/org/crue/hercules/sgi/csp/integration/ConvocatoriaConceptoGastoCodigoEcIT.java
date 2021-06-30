package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de ConvocatoriaConceptoGastoCodigoEc.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })

public class ConvocatoriaConceptoGastoCodigoEcIT {
  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaconceptogastocodigoecs";

  private HttpEntity<ConvocatoriaConceptoGastoCodigoEc> buildRequest(HttpHeaders headers,
      ConvocatoriaConceptoGastoCodigoEc entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-E", "AUTH")));

    HttpEntity<ConvocatoriaConceptoGastoCodigoEc> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaConceptoGastoCodigoEc() throws Exception {
    Long idConvocatoriaConceptoGastoCodigoEc = 1L;

    final ResponseEntity<ConvocatoriaConceptoGastoCodigoEc> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaConceptoGastoCodigoEc.class, idConvocatoriaConceptoGastoCodigoEc);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGasto = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGasto.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGasto.getConvocatoriaConceptoGastoId())
        .as("getConvocatoriaConceptoGastoId()").isEqualTo(1L);
  }
}
