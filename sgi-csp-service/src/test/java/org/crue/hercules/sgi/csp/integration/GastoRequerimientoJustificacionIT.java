package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GastoRequerimientoJustificacionController;
import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionOutput;
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
 * Test de integracion de GastoRequerimientoJustificacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GastoRequerimientoJustificacionIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = GastoRequerimientoJustificacionController.REQUEST_MAPPING;
  private static final String PATH_ID = GastoRequerimientoJustificacionController.PATH_ID;

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
  void create_ReturnsRequerimientoJustificacion() throws Exception {
    String alegacion = "Alegacion-001";
    Long requerimientoJustificacionId = 1L;
    GastoRequerimientoJustificacionInput input = generarMockGastoRequerimientoJustificacionInput(
        requerimientoJustificacionId, alegacion);

    final ResponseEntity<GastoRequerimientoJustificacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, input, DEFAULT_ROLES), GastoRequerimientoJustificacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    GastoRequerimientoJustificacionOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()").isNotNull();
    Assertions.assertThat(output.getAlegacion()).as("getAlegacion()")
        .isEqualTo(input.getAlegacion());
    Assertions.assertThat(output.getRequerimientoJustificacionId()).as("getRequerimientoJustificacionId()")
        .isEqualTo(input.getRequerimientoJustificacionId());
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
  void update_ReturnsRequerimientoJustificacion() throws Exception {
    Long gastoRequerimientoJustificacionId = 1L;
    String alegacion = "Alegacion-modificada";
    Long requerimientoJustificacionId = 1L;
    GastoRequerimientoJustificacionInput input = generarMockGastoRequerimientoJustificacionInput(
        requerimientoJustificacionId, alegacion);

    final ResponseEntity<GastoRequerimientoJustificacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, input, DEFAULT_ROLES), GastoRequerimientoJustificacionOutput.class,
        gastoRequerimientoJustificacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    GastoRequerimientoJustificacionOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()").isNotNull();
    Assertions.assertThat(output.getAlegacion()).as("getAlegacion()")
        .isEqualTo(input.getAlegacion());
    Assertions.assertThat(output.getRequerimientoJustificacionId()).as("getRequerimientoJustificacionId()")
        .isEqualTo(input.getRequerimientoJustificacionId());
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

  private GastoRequerimientoJustificacionInput generarMockGastoRequerimientoJustificacionInput(
      Long requerimientoJustificacionId, String alegacion) {
    return generarMockGastoRequerimientoJustificacionInput(Boolean.TRUE, alegacion,
        "12345", "11/1111",
        null, null, null,
        null, requerimientoJustificacionId);
  }

  private GastoRequerimientoJustificacionInput generarMockGastoRequerimientoJustificacionInput(Boolean aceptado,
      String alegacion, String gastoRef, String identificadorJustificacion, BigDecimal importeAceptado,
      BigDecimal importeAlegado, BigDecimal importeRechazado,
      String incidencia, Long requerimientoJustificacionId) {
    return GastoRequerimientoJustificacionInput.builder()
        .aceptado(aceptado)
        .alegacion(alegacion)
        .gastoRef(gastoRef)
        .identificadorJustificacion(identificadorJustificacion)
        .importeAceptado(importeAceptado)
        .importeAlegado(importeAlegado)
        .importeRechazado(importeRechazado)
        .incidencia(incidencia)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }
}
