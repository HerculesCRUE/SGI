package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoSeguimientoJustificacionController;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionOutput;
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
 * Test de integracion de ProyectoSeguimientoJustificacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProyectoSeguimientoJustificacionIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = ProyectoSeguimientoJustificacionController.REQUEST_MAPPING;
  private static final String PATH_ID = ProyectoSeguimientoJustificacionController.PATH_ID;

  private static final String[] DEFAULT_ROLES = { "CSP-SJUS-E", "CSP-SJUS-V" };

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("usr-002", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/proyecto_proyecto_sge.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoSeguimientoJustificacion() throws Exception {
    String justificanteReintegro = "Alegacion-001";
    Long proyectoProyectoSgeId = 1L;
    ProyectoSeguimientoJustificacionInput input = generarMockProyectoSeguimientoJustificacionInput(
        proyectoProyectoSgeId, justificanteReintegro);

    final ResponseEntity<ProyectoSeguimientoJustificacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, input, DEFAULT_ROLES), ProyectoSeguimientoJustificacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoSeguimientoJustificacionOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()").isNotNull();
    Assertions.assertThat(output.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(input.getJustificanteReintegro());
    Assertions.assertThat(output.getProyectoProyectoSge().getId()).as("getProyectoProyectoSge().getId()")
        .isEqualTo(input.getProyectoProyectoSgeId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/proyecto_proyecto_sge.sql",
    "classpath:scripts/proyecto_seguimiento_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoSeguimientoJustificacion() throws Exception {
    Long proyectoSeguimientoJustificacionId = 1L;
    String justificanteReintegro = "Alegacion-modificada";
    Long proyectoProyectoSgeId = 1L;
    ProyectoSeguimientoJustificacionInput input = generarMockProyectoSeguimientoJustificacionInput(
        proyectoProyectoSgeId, justificanteReintegro);

    final ResponseEntity<ProyectoSeguimientoJustificacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, input, DEFAULT_ROLES), ProyectoSeguimientoJustificacionOutput.class,
        proyectoSeguimientoJustificacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoSeguimientoJustificacionOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()").isNotNull();
    Assertions.assertThat(output.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(input.getJustificanteReintegro());
    Assertions.assertThat(output.getProyectoProyectoSge().getId()).as("getProyectoProyectoSge().getId()")
        .isEqualTo(input.getProyectoProyectoSgeId());
  }

  private ProyectoSeguimientoJustificacionInput generarMockProyectoSeguimientoJustificacionInput(
      Long proyectoProyectoSgeId, String justificanteReintegro) {
    return ProyectoSeguimientoJustificacionInput.builder()
        .proyectoProyectoSgeId(proyectoProyectoSgeId)
        .justificanteReintegro(justificanteReintegro)
        .build();
  }
}
