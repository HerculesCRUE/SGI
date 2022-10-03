package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.AlegacionRequerimientoController;
import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoInput;
import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoOutput;
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
 * Test de integracion de AlegacionRequerimiento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlegacionRequerimientoIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = AlegacionRequerimientoController.REQUEST_MAPPING;
  private static final String PATH_ID = AlegacionRequerimientoController.PATH_ID;

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
  void create_ReturnsAlegacionRequerimiento() throws Exception {
    String suffix = "001";
    AlegacionRequerimientoInput input = generarMockAlegacionRequerimientoInput(
        suffix, 1L);

    final ResponseEntity<AlegacionRequerimientoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, input, DEFAULT_ROLES), AlegacionRequerimientoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    AlegacionRequerimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()").isNotNull();
    Assertions.assertThat(output.getRequerimientoJustificacionId()).as("getRequerimientoJustificacionId()")
        .isEqualTo(input.getRequerimientoJustificacionId());
    Assertions.assertThat(output.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(input.getJustificanteReintegro());
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
  void update_ReturnsAlegacionRequerimiento() throws Exception {
    Long alegacionRequerimientoId = 1L;
    String suffix = "-editado-001";
    AlegacionRequerimientoInput input = generarMockAlegacionRequerimientoInput(
        suffix, 1L);

    final ResponseEntity<AlegacionRequerimientoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, input, DEFAULT_ROLES),
        AlegacionRequerimientoOutput.class,
        alegacionRequerimientoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    AlegacionRequerimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).as("getId()")
        .isEqualTo(alegacionRequerimientoId);
    Assertions.assertThat(output.getRequerimientoJustificacionId()).as("getRequerimientoJustificacionId()")
        .isEqualTo(input.getRequerimientoJustificacionId());
    Assertions.assertThat(output.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(input.getJustificanteReintegro());
  }

  private AlegacionRequerimientoInput generarMockAlegacionRequerimientoInput(String suffix,
      Long requerimientoJustificacionId) {
    return generarMockAlegacionRequerimientoInput("Justificante-" + suffix, "Observacion-" + suffix,
        requerimientoJustificacionId);
  }

  private AlegacionRequerimientoInput generarMockAlegacionRequerimientoInput(String justificanteReintegro,
      String observaciones, Long requerimientoJustificacionId) {
    return AlegacionRequerimientoInput.builder()
        .justificanteReintegro(justificanteReintegro)
        .observaciones(observaciones)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }
}
