package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoResponsableEconomicoController;
import org.crue.hercules.sgi.csp.dto.ProyectoResponsableEconomicoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoResponsableEconomicoOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de ProyectoResponsableEconomico.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProyectoResponsableEconomicoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ProyectoResponsableEconomicoController.MAPPING;

  private HttpEntity<ProyectoResponsableEconomicoInput> buildRequest(
      HttpHeaders headers,
      ProyectoResponsableEconomicoInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", roles)));

    HttpEntity<ProyectoResponsableEconomicoInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_responsable_economico.sql"
    // @formatter:on 
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnProyectoResponsableEconomicoOutput() throws Exception {
    String roles = "CSP-PRO-E";
    Long proyectoResponsableEconomicoId = 3L;

    final ResponseEntity<ProyectoResponsableEconomicoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles),
        ProyectoResponsableEconomicoOutput.class,
        proyectoResponsableEconomicoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoResponsableEconomicoOutput responseData = response.getBody();

    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(3L);
    Assertions.assertThat(responseData.getPersonaRef()).as("getPersonaRef()").isEqualTo("personaRef-003");

  }

}
