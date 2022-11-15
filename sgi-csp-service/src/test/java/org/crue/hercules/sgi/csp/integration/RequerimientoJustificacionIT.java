package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.RequerimientoJustificacionController;
import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionOutput;
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
 * Test de integracion de RequerimientoJustificacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RequerimientoJustificacionIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = RequerimientoJustificacionController.REQUEST_MAPPING;
  private static final String PATH_ID = RequerimientoJustificacionController.PATH_ID;
  private static final String PATH_INCIDENCIAS_DOCUMENTACION = RequerimientoJustificacionController.PATH_INCIDENCIAS_DOCUMENTACION;
  private static final String PATH_GASTOS = RequerimientoJustificacionController.PATH_GASTOS;
  private static final String PATH_ALEGACION = RequerimientoJustificacionController.PATH_ALEGACION;

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
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsRequerimientoJustificacionOutput() throws Exception {
    Long requerimientoJustificacionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ID)
        .queryParam("s").buildAndExpand(requerimientoJustificacionId).toUri();
    final ResponseEntity<RequerimientoJustificacionOutput> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(null, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<RequerimientoJustificacionOutput>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final RequerimientoJustificacionOutput responseData = response.getBody();
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(requerimientoJustificacionId);
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()").isEqualTo("obs-001");
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
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_Returns404() throws Exception {
    Long requerimientoJustificacionId = 999999L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ID)
        .buildAndExpand(requerimientoJustificacionId).toUri();
    final ResponseEntity<RequerimientoJustificacionOutput> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(null, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<RequerimientoJustificacionOutput>() {
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
    "classpath:scripts/proyecto_proyecto_sge.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_justificacion_seguimiento.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Returns400() throws Exception {
    Long requerimientoJustificacionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ID)
        .queryParam("s").buildAndExpand(requerimientoJustificacionId).toUri();
    final ResponseEntity<Void> response = restTemplate.exchange(uri,
        HttpMethod.DELETE,
        buildRequest(null, null, DEFAULT_ROLES),
        Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_justificacion_seguimiento.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Returns204() throws Exception {
    Long requerimientoJustificacionId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ID)
        .queryParam("s").buildAndExpand(requerimientoJustificacionId).toUri();
    final ResponseEntity<Void> response = restTemplate.exchange(uri,
        HttpMethod.DELETE,
        buildRequest(null, null, DEFAULT_ROLES),
        Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
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
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_justificacion_seguimiento.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_alsoDeletesRelatedProyectoPeriodoSeguimientoJustificacion_Returns204() throws Exception {
    Long requerimientoJustificacionId = 3L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ID)
        .queryParam("s").buildAndExpand(requerimientoJustificacionId).toUri();
    final ResponseEntity<Void> response = restTemplate.exchange(uri,
        HttpMethod.DELETE,
        buildRequest(null, null, DEFAULT_ROLES),
        Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
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
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsRequerimientoJustificacion() throws Exception {
    String observaciones = "RequerimientoJustificacion-001";
    Instant fechaNotificacion = Instant.parse("2023-05-13T00:00:00.000Z");
    RequerimientoJustificacionInput input = generarMockRequerimientoJustificacionInput(observaciones,
        fechaNotificacion);

    final ResponseEntity<RequerimientoJustificacionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, input, DEFAULT_ROLES), RequerimientoJustificacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    RequerimientoJustificacionOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()").isNotNull();
    Assertions.assertThat(output.getNumRequerimiento()).as("getNumRequerimiento()")
        .isEqualTo(1);
    Assertions.assertThat(output.getObservaciones()).as("getObservaciones()")
        .isEqualTo(input.getObservaciones());
    Assertions.assertThat(output.getProyectoProyectoSgeId()).as("getProyectoProyectoSgeId()")
        .isEqualTo(input.getProyectoProyectoSgeId());
    Assertions.assertThat(output.getTipoRequerimiento().getId()).as("getTipoRequerimiento().getId()")
        .isEqualTo(input.getTipoRequerimientoId());
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
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsRequerimientoJustificacion() throws Exception {
    Long requerimientoJustificacionId = 1L;
    String observaciones = "RequerimientoJustificacion-Actualizada";
    Instant fechaNotificacion = Instant.parse("2023-05-13T00:00:00.000Z");
    RequerimientoJustificacionInput input = generarMockRequerimientoJustificacionInput(observaciones,
        fechaNotificacion);

    final ResponseEntity<RequerimientoJustificacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, input, DEFAULT_ROLES), RequerimientoJustificacionOutput.class,
        requerimientoJustificacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RequerimientoJustificacionOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()")
        .isEqualTo(requerimientoJustificacionId);
    Assertions.assertThat(output.getNumRequerimiento()).as("getNumRequerimiento()")
        .isEqualTo(2);
    Assertions.assertThat(output.getObservaciones()).as("getObservaciones()")
        .isEqualTo(input.getObservaciones());
    Assertions.assertThat(output.getProyectoProyectoSgeId()).as("getProyectoProyectoSgeId()")
        .isEqualTo(input.getProyectoProyectoSgeId());
    Assertions.assertThat(output.getTipoRequerimiento().getId()).as("getTipoRequerimiento().getId()")
        .isEqualTo(input.getTipoRequerimientoId());
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
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
    "classpath:scripts/incidencia_documentacion_requerimiento.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findIncidenciasDocumentacion_WithPagingSorting_ReturnsIncidenciaDocumentacionRequerimientoOutputSubList()
      throws Exception {
    Long requerimientoJustificacionId = 1L;
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "incidencia,desc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INCIDENCIAS_DOCUMENTACION)
        .queryParam("s", sort).buildAndExpand(requerimientoJustificacionId).toUri();
    final ResponseEntity<List<IncidenciaDocumentacionRequerimientoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<List<IncidenciaDocumentacionRequerimientoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<IncidenciaDocumentacionRequerimientoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getIncidencia()).as("get(0).getIncidencia())")
        .isEqualTo("incidencia-003");
    Assertions.assertThat(responseData.get(1).getIncidencia()).as("get(0).getIncidencia())")
        .isEqualTo("incidencia-002");
    Assertions.assertThat(responseData.get(2).getIncidencia()).as("get(1).getIncidencia())")
        .isEqualTo("incidencia-001");
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
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
    "classpath:scripts/gasto_requerimiento_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findGastos_WithPagingSorting_ReturnsGastoRequerimientoJustificacionOutputSubList()
      throws Exception {
    Long requerimientoJustificacionId = 1L;
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "incidencia,desc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_GASTOS)
        .queryParam("s", sort).buildAndExpand(requerimientoJustificacionId).toUri();
    final ResponseEntity<List<GastoRequerimientoJustificacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<List<GastoRequerimientoJustificacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<GastoRequerimientoJustificacionOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getIncidencia()).as("get(0).getIncidencia())")
        .isEqualTo("incidencia-003");
    Assertions.assertThat(responseData.get(1).getIncidencia()).as("get(0).getIncidencia())")
        .isEqualTo("incidencia-002");
    Assertions.assertThat(responseData.get(2).getIncidencia()).as("get(1).getIncidencia())")
        .isEqualTo("incidencia-001");
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
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
    "classpath:scripts/alegacion_requerimiento.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAlegacion_ReturnsAlegacionRequerimiento() throws Exception {
    Long requerimientoJustificacionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ALEGACION)
        .buildAndExpand(requerimientoJustificacionId).toUri();
    final ResponseEntity<AlegacionRequerimientoOutput> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(null, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<AlegacionRequerimientoOutput>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final AlegacionRequerimientoOutput responseData = response.getBody();
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getRequerimientoJustificacionId()).as("getId()")
        .isEqualTo(requerimientoJustificacionId);
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()").isEqualTo("Observacion-001");
  }

  private RequerimientoJustificacionInput generarMockRequerimientoJustificacionInput(String observaciones,
      Instant fechaNotificacion) {
    return generarMockRequerimientoJustificacionInput(fechaNotificacion, observaciones, 1L, null, 1L);
  }

  private RequerimientoJustificacionInput generarMockRequerimientoJustificacionInput(Instant fechaNotificacion,
      String observaciones, Long proyectoProyectoSgeId, Long requerimientoPrevioId, Long tipoRequerimientoId) {
    return RequerimientoJustificacionInput.builder()
        .fechaNotificacion(fechaNotificacion)
        .observaciones(observaciones)
        .proyectoProyectoSgeId(proyectoProyectoSgeId)
        .requerimientoPrevioId(requerimientoPrevioId)
        .tipoRequerimientoId(tipoRequerimientoId)
        .build();
  }
}
