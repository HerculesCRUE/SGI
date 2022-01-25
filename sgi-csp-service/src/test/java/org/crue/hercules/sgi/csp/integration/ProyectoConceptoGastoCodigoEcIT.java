package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoConceptoGastoCodigoEcController;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoConceptoGastoCodigoEcIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = ProyectoConceptoGastoCodigoEcController.MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_PROYECTO_CONCEPTO_GASTO_ID = "/{proyectoConceptoGastoId}";

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_concepto_gasto.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/convocatoria_concepto_gasto.sql",
    "classpath:scripts/convocatoria_concepto_gasto_codigo_ec.sql",
    "classpath:scripts/proyecto_concepto_gasto_codigo_ec.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoConceptoGastoCodigoEc() throws Exception {
    Long idToSearch = 1L;
    String[] roles = { "AUTH" };

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(idToSearch)
        .toUri();

    final ResponseEntity<ProyectoConceptoGastoCodigoEc> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), ProyectoConceptoGastoCodigoEc.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoConceptoGastoCodigoEc proyectoConceptoGastoCodigoEc = response.getBody();

    Assertions.assertThat(proyectoConceptoGastoCodigoEc).isNotNull();
    Assertions.assertThat(proyectoConceptoGastoCodigoEc.getId()).isEqualTo(idToSearch);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_concepto_gasto.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/convocatoria_concepto_gasto.sql",
    "classpath:scripts/convocatoria_concepto_gasto_codigo_ec.sql",
    "classpath:scripts/proyecto_concepto_gasto_codigo_ec.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsProyectoConceptoGastoCodigoEcSubList() throws Exception {

    String[] roles = { "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "codigoEconomicoRef==IB";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("q", filter)
        .queryParam("s", sort).build()
        .toUri();

    final ResponseEntity<List<ProyectoConceptoGastoCodigoEc>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoConceptoGastoCodigoEc>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    List<ProyectoConceptoGastoCodigoEc> responseData = response.getBody();

    Assertions.assertThat(responseData.isEmpty()).isFalse();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(3L);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(5L);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_concepto_gasto.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/convocatoria_concepto_gasto.sql",
    "classpath:scripts/convocatoria_concepto_gasto_codigo_ec.sql",
    "classpath:scripts/proyecto_concepto_gasto_codigo_ec.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoConceptoGastoCodigoEcList() throws Exception {

    Long proyectoConceptoGastoId = 3L;
    // @formatter:off
    String[] roles = { "CSP-CON-E" };
    List<ProyectoConceptoGastoCodigoEc>toUpdate = Arrays.asList(
      buildMockProyectoConceptoGastoCodigoEc(4L, "IB", 4L, Instant.parse("2022-11-11T23:59:59Z"), Instant.parse("2021-12-01T00:00:00Z"), "testing update 1", 3L),
      buildMockProyectoConceptoGastoCodigoEc(5L, "DA", 5L, Instant.parse("2022-06-30T23:59:59Z"), Instant.parse("2021-12-01T00:00:00Z"), "testing update 2", 3L));
    // @formatter:on     
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_PROYECTO_CONCEPTO_GASTO_ID)
        .buildAndExpand(proyectoConceptoGastoId)
        .toUri();

    final ResponseEntity<List<ProyectoConceptoGastoCodigoEc>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), new ParameterizedTypeReference<List<ProyectoConceptoGastoCodigoEc>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    List<ProyectoConceptoGastoCodigoEc> responseData = response.getBody();

    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.isEmpty()).isFalse();
    Assertions.assertThat(responseData.size()).isEqualTo(2);

    List<ProyectoConceptoGastoCodigoEc> sortedResponseData = responseData.stream()
        .sorted(new Comparator<ProyectoConceptoGastoCodigoEc>() {

          @Override
          public int compare(ProyectoConceptoGastoCodigoEc o1, ProyectoConceptoGastoCodigoEc o2) {
            return o1.getId().compareTo(o2.getId());
          }

        }).collect(Collectors.toList());

    Assertions.assertThat(sortedResponseData.get(0)).isNotNull();
    Assertions.assertThat(sortedResponseData.get(0)).isNotNull();

    Assertions.assertThat(sortedResponseData.get(0).getId()).isEqualTo(responseData.get(0).getId());
    Assertions.assertThat(sortedResponseData.get(1).getId()).isEqualTo(responseData.get(1).getId());

    Assertions.assertThat(sortedResponseData.get(0).getCodigoEconomicoRef())
        .isEqualTo(responseData.get(0).getCodigoEconomicoRef());
    Assertions.assertThat(sortedResponseData.get(1).getCodigoEconomicoRef())
        .isEqualTo(responseData.get(1).getCodigoEconomicoRef());

    Assertions.assertThat(sortedResponseData.get(0).getConvocatoriaConceptoGastoCodigoEcId())
        .isEqualTo(responseData.get(0).getConvocatoriaConceptoGastoCodigoEcId());
    Assertions.assertThat(sortedResponseData.get(1).getConvocatoriaConceptoGastoCodigoEcId())
        .isEqualTo(responseData.get(1).getConvocatoriaConceptoGastoCodigoEcId());

    Assertions.assertThat(sortedResponseData.get(0).getFechaFin())
        .isEqualTo(responseData.get(0).getFechaFin());
    Assertions.assertThat(sortedResponseData.get(1).getFechaFin())
        .isEqualTo(responseData.get(1).getFechaFin());

    Assertions.assertThat(sortedResponseData.get(0).getFechaInicio())
        .isEqualTo(responseData.get(0).getFechaInicio());
    Assertions.assertThat(sortedResponseData.get(1).getFechaInicio())
        .isEqualTo(responseData.get(1).getFechaInicio());

    Assertions.assertThat(sortedResponseData.get(0).getObservaciones())
        .isEqualTo(responseData.get(0).getObservaciones());
    Assertions.assertThat(sortedResponseData.get(1).getObservaciones())
        .isEqualTo(responseData.get(1).getObservaciones());

    Assertions.assertThat(sortedResponseData.get(0).getProyectoConceptoGastoId())
        .isEqualTo(responseData.get(0).getProyectoConceptoGastoId());
    Assertions.assertThat(sortedResponseData.get(1).getProyectoConceptoGastoId())
        .isEqualTo(responseData.get(1).getProyectoConceptoGastoId());

  }

  private ProyectoConceptoGastoCodigoEc buildMockProyectoConceptoGastoCodigoEc(Long id, String codigoEconomicoRef,
      Long convocatoriaConceptoGastoCodigoEcId, Instant fechaFin, Instant fechaInicio, String observaciones,
      Long proyectoConceptoGastoId) {
    // @formatter:off
    return ProyectoConceptoGastoCodigoEc
    .builder()
    .id(id)
    .codigoEconomicoRef(codigoEconomicoRef)
    .convocatoriaConceptoGastoCodigoEcId(convocatoriaConceptoGastoCodigoEcId)
    .fechaFin(fechaFin)
    .fechaInicio(fechaInicio)
    .observaciones(observaciones)
    .proyectoConceptoGastoId(proyectoConceptoGastoId)
    .build();
    // @formatter:on
  }
}