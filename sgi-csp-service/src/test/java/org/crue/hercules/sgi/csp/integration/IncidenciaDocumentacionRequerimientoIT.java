package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.IncidenciaDocumentacionRequerimientoController;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoAlegacionInput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoInput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de IncidenciaDocumentacionRequerimiento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IncidenciaDocumentacionRequerimientoIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = IncidenciaDocumentacionRequerimientoController.REQUEST_MAPPING;
  private static final String PATH_ID = IncidenciaDocumentacionRequerimientoController.PATH_ID;
  private static final String PATH_ALEGAR = IncidenciaDocumentacionRequerimientoController.PATH_ALEGAR;

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
    "classpath:scripts/incidencia_documentacion_requerimiento.sql",
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
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsIncidenciaDocumentacionRequerimiento() throws Exception {
    String suffix = "001";
    IncidenciaDocumentacionRequerimientoInput input = generarMockIncidenciaDocumentacionRequerimientoInput(
        suffix);

    final ResponseEntity<IncidenciaDocumentacionRequerimientoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, input, DEFAULT_ROLES), IncidenciaDocumentacionRequerimientoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    IncidenciaDocumentacionRequerimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()").isNotNull();
    Assertions.assertThat(output.getIncidencia()).as("getIncidencia()")
        .isEqualTo(input.getIncidencia());
    Assertions.assertThat(output.getNombreDocumento()).as("getNombreDocumento()")
        .isEqualTo(input.getNombreDocumento());
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
  void update_ReturnsIncidenciaDocumentacionRequerimiento() throws Exception {
    Long incidenciaDocumentacionRequerimientoId = 1L;
    String suffix = "-editado-001";
    IncidenciaDocumentacionRequerimientoInput input = generarMockIncidenciaDocumentacionRequerimientoInput(
        suffix);

    final ResponseEntity<IncidenciaDocumentacionRequerimientoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, input, DEFAULT_ROLES),
        IncidenciaDocumentacionRequerimientoOutput.class,
        incidenciaDocumentacionRequerimientoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    IncidenciaDocumentacionRequerimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()")
        .isEqualTo(incidenciaDocumentacionRequerimientoId);
    Assertions.assertThat(output.getIncidencia()).as("getIncidencia()")
        .isEqualTo(input.getIncidencia());
    Assertions.assertThat(output.getNombreDocumento()).as("getNombreDocumento()")
        .isEqualTo(input.getNombreDocumento());
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
  void updateAlegacion_ReturnsIncidenciaDocumentacionRequerimiento() throws Exception {
    Long incidenciaDocumentacionRequerimientoId = 1L;
    String alegacion = "Alegacion-editada";
    IncidenciaDocumentacionRequerimientoAlegacionInput input = generarMockIncidenciaDocumentacionRequerimientoAlegacionInput(
        alegacion);

    final ResponseEntity<IncidenciaDocumentacionRequerimientoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ALEGAR,
        HttpMethod.PATCH, buildRequest(null, input, DEFAULT_ROLES),
        IncidenciaDocumentacionRequerimientoOutput.class,
        incidenciaDocumentacionRequerimientoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    IncidenciaDocumentacionRequerimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()")
        .isEqualTo(incidenciaDocumentacionRequerimientoId);
    Assertions.assertThat(output.getAlegacion()).as("getAlegacion()")
        .isEqualTo(input.getAlegacion());
  }

  private IncidenciaDocumentacionRequerimientoInput generarMockIncidenciaDocumentacionRequerimientoInput(
      String suffix) {
    return generarMockIncidenciaDocumentacionRequerimientoInput("Incidencia-" + suffix,
        "IncidenciaDocumentacionRequerimiento-" + suffix, 1L);
  }

  private IncidenciaDocumentacionRequerimientoInput generarMockIncidenciaDocumentacionRequerimientoInput(
      String incidencia, String nombreDocumento, Long requerimientoJustificacionId) {
    return IncidenciaDocumentacionRequerimientoInput.builder()
        .incidencia(incidencia)
        .nombreDocumento(nombreDocumento)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private IncidenciaDocumentacionRequerimientoAlegacionInput generarMockIncidenciaDocumentacionRequerimientoAlegacionInput(
      String alegacion) {
    return IncidenciaDocumentacionRequerimientoAlegacionInput.builder()
        .alegacion(alegacion)
        .build();
  }
}
