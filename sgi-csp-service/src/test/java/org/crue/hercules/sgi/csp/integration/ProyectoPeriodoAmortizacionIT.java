package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoPeriodoAmortizacionController;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoAmortizacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoAmortizacionOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProyectoPeriodoAmortizacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ProyectoPeriodoAmortizacionController.MAPPING;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_amortizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsProyectoPeriodoAmortizacionOutputSubList() throws Exception {
    String[] roles = { "CSP-PRO-E" };
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "proyectoAnualidadId==2";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<ProyectoPeriodoAmortizacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoPeriodoAmortizacionOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    final List<ProyectoPeriodoAmortizacionOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(2);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(3);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(4);

    Assertions.assertThat(responseData.get(0).getFechaLimiteAmortizacion())
        .isEqualTo(Instant.parse("2022-12-31T23:59:59.00z"));
    Assertions.assertThat(responseData.get(1).getFechaLimiteAmortizacion())
        .isEqualTo(Instant.parse("2022-12-31T23:59:59.00z"));
    Assertions.assertThat(responseData.get(2).getFechaLimiteAmortizacion())
        .isEqualTo(Instant.parse("2022-12-31T23:59:59.00z"));

    Assertions.assertThat(responseData.get(0).getImporte()).isEqualTo(11000);
    Assertions.assertThat(responseData.get(1).getImporte()).isEqualTo(11100);
    Assertions.assertThat(responseData.get(2).getImporte()).isEqualTo(11110);

    Assertions.assertThat(responseData.get(0).getProyectoSGERef()).isEqualTo("PRO-SGE-0002");
    Assertions.assertThat(responseData.get(1).getProyectoSGERef()).isEqualTo("PRO-SGE-0003");
    Assertions.assertThat(responseData.get(2).getProyectoSGERef()).isEqualTo("PRO-SGE-0004");

    Assertions.assertThat(responseData.get(0).getProyectoAnualidadId()).isEqualTo(2L);
    Assertions.assertThat(responseData.get(1).getProyectoAnualidadId()).isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getProyectoAnualidadId()).isEqualTo(2L);

    Assertions.assertThat(responseData.get(0).getProyectoEntidadFinanciadoraId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getProyectoEntidadFinanciadoraId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(2).getProyectoEntidadFinanciadoraId()).isEqualTo(1L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_amortizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoPeriodoAmortizacionOutput() throws Exception {
    Long toFindId = 2L;
    String[] roles = { "CSP-PRO-E" };

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(toFindId)
        .toUri();

    final ResponseEntity<ProyectoPeriodoAmortizacionOutput> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), ProyectoPeriodoAmortizacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPeriodoAmortizacionOutput responseData = response.getBody();

    Assertions.assertThat(responseData).isNotNull();

    Assertions.assertThat(responseData.getId()).isEqualTo(2);
    Assertions.assertThat(responseData.getFechaLimiteAmortizacion())
        .isEqualTo(Instant.parse("2022-12-31T23:59:59.00z"));
    Assertions.assertThat(responseData.getImporte()).isEqualTo(11000);
    Assertions.assertThat(responseData.getProyectoSGERef()).isEqualTo("PRO-SGE-0002");
    Assertions.assertThat(responseData.getProyectoAnualidadId()).isEqualTo(2L);
    Assertions.assertThat(responseData.getProyectoEntidadFinanciadoraId()).isEqualTo(1L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql", 
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_amortizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoPeriodoAmortizacionOutput() throws Exception {
    String[] roles = { "CSP-PRO-E" };

    ProyectoPeriodoAmortizacionInput toCreate = buildMockProyectoPeriodoAmortizacionInput("2022-12-31T23:59:59.00z",
        11000L,
        1L, 1L, "SGE-PRO-0011");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<ProyectoPeriodoAmortizacionOutput> response = restTemplate.exchange(uri, HttpMethod.POST,
        buildRequest(null, toCreate, roles), ProyectoPeriodoAmortizacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoPeriodoAmortizacionOutput created = response.getBody();
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getFechaLimiteAmortizacion()).isEqualTo(Instant.parse("2022-12-31T23:59:59.00z"));
    Assertions.assertThat(created.getImporte()).isEqualTo(toCreate.getImporte());
    Assertions.assertThat(created.getProyectoAnualidadId()).isEqualTo(toCreate.getProyectoAnualidadId());
    Assertions.assertThat(created.getProyectoEntidadFinanciadoraId())
        .isEqualTo(toCreate.getProyectoEntidadFinanciadoraId());
    Assertions.assertThat(created.getProyectoSGERef()).isEqualTo(toCreate.getProyectoSGERef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_amortizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update() throws Exception {
    Long toUpdateId = 1L;
    String[] roles = { "CSP-PRO-E" };
    ProyectoPeriodoAmortizacionInput toUpdate = buildMockProyectoPeriodoAmortizacionInput("2022-10-31T23:59:59.00z",
        11000L,
        1L, 1L, "SGE-PRO-0011");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(
        toUpdateId).toUri();

    final ResponseEntity<ProyectoPeriodoAmortizacionOutput> response = restTemplate.exchange(uri, HttpMethod.PUT,
        buildRequest(null, toUpdate, roles), ProyectoPeriodoAmortizacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPeriodoAmortizacionOutput updated = response.getBody();
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getFechaLimiteAmortizacion()).isEqualTo(toUpdate.getFechaLimiteAmortizacion());
    Assertions.assertThat(updated.getImporte()).isEqualTo(toUpdate.getImporte());
    Assertions.assertThat(updated.getProyectoAnualidadId()).isEqualTo(toUpdate.getProyectoAnualidadId());
    Assertions.assertThat(updated.getProyectoEntidadFinanciadoraId())
        .isEqualTo(toUpdate.getProyectoEntidadFinanciadoraId());
    Assertions.assertThat(updated.getProyectoSGERef()).isEqualTo(toUpdate.getProyectoSGERef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_periodo_amortizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsStatusCode204() throws Exception {
    String[] roles = { "CSP-PRO-E" };
    Long toDeleteId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(toDeleteId)
        .toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, buildRequest(null, null, roles),
        Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  private ProyectoPeriodoAmortizacionInput buildMockProyectoPeriodoAmortizacionInput(String fechaLimiteAmortizacion,
      Long importe, Long proyectoAnualidadId, Long proyectoEntidadFinanciadoraId, String proyectoSGERef) {
    return ProyectoPeriodoAmortizacionInput.builder()
        .fechaLimiteAmortizacion(Instant.parse(fechaLimiteAmortizacion))
        .proyectoAnualidadId(proyectoAnualidadId)
        .proyectoEntidadFinanciadoraId(proyectoEntidadFinanciadoraId)
        .proyectoSGERef(proyectoSGERef)
        .importe(importe)
        .build();
  }
}