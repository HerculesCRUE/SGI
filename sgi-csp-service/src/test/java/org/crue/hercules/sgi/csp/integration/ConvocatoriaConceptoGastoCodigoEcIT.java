package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
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
 * Test de integracion de ConvocatoriaConceptoGastoCodigoEc.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class ConvocatoriaConceptoGastoCodigoEcIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaconceptogastocodigoecs";
  private static final String PATH_PARAMETER_CONVOCATORIA_CONCEPTO_GASTO_ID = "/{convocatoriaConceptoGastoId}";

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-E", "AUTH")));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsConvocatoriaConceptoGastoCodigoEc() throws Exception {
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_concepto_gasto.sql",
    "classpath:scripts/convocatoria_concepto_gasto.sql",
    "classpath:scripts/convocatoria_concepto_gasto_codigo_ec.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsConvocatoriaConceptoGastoCodigoEcList() throws Exception {

    Long convocatoriaConceptoGastoId = 1L;
    List<ConvocatoriaConceptoGastoCodigoEc> toUpdate = Arrays
        .asList(buildMockConvocatoriaConceptoGastoCodigoEc(1L, "MA", convocatoriaConceptoGastoId,
            Instant.parse("2022-02-27T22:00:00.000Z"), Instant.parse("2022-12-30T23:59:59.000Z")));

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_CONVOCATORIA_CONCEPTO_GASTO_ID)
        .buildAndExpand(convocatoriaConceptoGastoId).toUri();

    final ResponseEntity<List<ConvocatoriaConceptoGastoCodigoEc>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH, buildRequest(null, toUpdate),
        new ParameterizedTypeReference<List<ConvocatoriaConceptoGastoCodigoEc>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    List<ConvocatoriaConceptoGastoCodigoEc> updated = response.getBody();
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated).hasSize(1);
    Assertions.assertThat(updated.get(0)).isNotNull();
    Assertions.assertThat(updated.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(updated.get(0).getConvocatoriaConceptoGastoId()).isEqualTo(convocatoriaConceptoGastoId);
    Assertions.assertThat(updated.get(0).getCodigoEconomicoRef()).isEqualTo(toUpdate.get(0).getCodigoEconomicoRef());
    Assertions.assertThat(updated.get(0).getFechaInicio()).isEqualTo(toUpdate.get(0).getFechaInicio());
    Assertions.assertThat(updated.get(0).getFechaFin()).isEqualTo(toUpdate.get(0).getFechaFin());
  }

  private ConvocatoriaConceptoGastoCodigoEc buildMockConvocatoriaConceptoGastoCodigoEc(Long id,
      String codigoEconomicoRef, Long convocatoriaConceptoGastoId, Instant fechaInicio, Instant fechaFin) {
    return ConvocatoriaConceptoGastoCodigoEc.builder()
        .id(id)
        .codigoEconomicoRef(codigoEconomicoRef)
        .convocatoriaConceptoGastoId(convocatoriaConceptoGastoId)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .build();
  }

}
