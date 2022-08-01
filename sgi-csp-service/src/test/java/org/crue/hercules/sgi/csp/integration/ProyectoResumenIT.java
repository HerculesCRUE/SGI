package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoResumenController;
import org.crue.hercules.sgi.csp.dto.ProyectoResumenOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoResumenIT extends BaseIT {
  private static final String USER_PERSONA_REF = "user";
  private static final String CONTROLLER_BASE_PATH = ProyectoResumenController.REQUEST_MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken(USER_PERSONA_REF, roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", 
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql", 
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoResumen() throws Exception {
    String rol = "CSP-PRO-PRC-V";
    Long idProyecto = 1L;

    final ResponseEntity<ProyectoResumenOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, rol), ProyectoResumenOutput.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProyectoResumenOutput proyectoResumen = response.getBody();
    Assertions.assertThat(proyectoResumen.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyectoResumen.getTitulo()).as("getTitulo()").isEqualTo("PRO1");
    Assertions.assertThat(proyectoResumen.getCodigoExterno()).as("getCodigoExterno()").isEqualTo("cod-externo-001");
  }
}
