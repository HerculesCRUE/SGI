package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoPeriodoJustificacionSeguimientoController;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionSeguimientoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionSeguimientoOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de ProyectoPeriodoJustificacionSeguimiento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProyectoPeriodoJustificacionSeguimientoIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = ProyectoPeriodoJustificacionSeguimientoController.REQUEST_MAPPING;
  private static final String PATH_ID = ProyectoPeriodoJustificacionSeguimientoController.PATH_ID;

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
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/proyecto_proyecto_sge.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
    "classpath:scripts/proyecto_periodo_justificacion_seguimiento.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoPeriodoJustificacionSeguimientoOutput() throws Exception {
    Long requerimientoJustificacionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ID)
        .queryParam("s").buildAndExpand(requerimientoJustificacionId).toUri();
    final ResponseEntity<ProyectoPeriodoJustificacionSeguimientoOutput> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(null, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<ProyectoPeriodoJustificacionSeguimientoOutput>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final ProyectoPeriodoJustificacionSeguimientoOutput responseData = response.getBody();
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(requerimientoJustificacionId);
    Assertions.assertThat(responseData.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo("justificante-reintegro-001");
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
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/proyecto_proyecto_sge.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
    "classpath:scripts/proyecto_periodo_justificacion_seguimiento.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_Returns404() throws Exception {
    Long requerimientoJustificacionId = 999999L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ID)
        .buildAndExpand(requerimientoJustificacionId).toUri();
    final ResponseEntity<ProyectoPeriodoJustificacionSeguimientoOutput> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(null, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<ProyectoPeriodoJustificacionSeguimientoOutput>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/proyecto_proyecto_sge.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoPeriodoJustificacionSeguimientoOutput() throws Exception {
    String justificanteReintegro = "justificante-reintegro-001";
    Long proyectoAnualidadId = 1L;
    Long proyectoPeriodoJustificacionId = 1L;
    ProyectoPeriodoJustificacionSeguimientoInput input = generarMockProyectoPeriodoJustificacionSeguimientoInput(
        justificanteReintegro, proyectoAnualidadId, proyectoPeriodoJustificacionId);

    final ResponseEntity<ProyectoPeriodoJustificacionSeguimientoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, input, DEFAULT_ROLES), ProyectoPeriodoJustificacionSeguimientoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoPeriodoJustificacionSeguimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()").isNotNull();
    Assertions.assertThat(output.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(input.getJustificanteReintegro());
    Assertions.assertThat(output.getProyectoAnualidadId()).as("getProyectoAnualidadId()")
        .isEqualTo(input.getProyectoAnualidadId());
    Assertions.assertThat(output.getProyectoPeriodoJustificacionId()).as("getProyectoPeriodoJustificacionId()")
        .isEqualTo(input.getProyectoPeriodoJustificacionId());
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
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/proyecto_proyecto_sge.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
    "classpath:scripts/proyecto_periodo_justificacion_seguimiento.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoPeriodoJustificacionSeguimientoOutput() throws Exception {
    Long proyectoPeriodoJustificacionSeguimientoId = 1L;
    String justificanteReintegro = "justificante-reintegro-modificado";
    Long proyectoAnualidadId = 1L;
    Long proyectoPeriodoJustificacionId = 1L;
    ProyectoPeriodoJustificacionSeguimientoInput input = generarMockProyectoPeriodoJustificacionSeguimientoInput(
        justificanteReintegro, proyectoAnualidadId, proyectoPeriodoJustificacionId);

    final ResponseEntity<ProyectoPeriodoJustificacionSeguimientoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, input, DEFAULT_ROLES), ProyectoPeriodoJustificacionSeguimientoOutput.class,
        proyectoPeriodoJustificacionSeguimientoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPeriodoJustificacionSeguimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()").isNotNull();
    Assertions.assertThat(output.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(input.getJustificanteReintegro());
    Assertions.assertThat(output.getProyectoAnualidadId()).as("getProyectoAnualidadId()")
        .isEqualTo(input.getProyectoAnualidadId());
    Assertions.assertThat(output.getProyectoPeriodoJustificacionId()).as("getProyectoPeriodoJustificacionId()")
        .isEqualTo(input.getProyectoPeriodoJustificacionId());
  }

  private ProyectoPeriodoJustificacionSeguimientoInput generarMockProyectoPeriodoJustificacionSeguimientoInput(
      String justificanteReintegro, Long proyectoAnualidadId, Long proyectoPeriodoJustificacionId) {
    return ProyectoPeriodoJustificacionSeguimientoInput.builder()
        .justificanteReintegro(justificanteReintegro)
        .proyectoAnualidadId(proyectoAnualidadId)
        .proyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId)
        .build();
  }
}
