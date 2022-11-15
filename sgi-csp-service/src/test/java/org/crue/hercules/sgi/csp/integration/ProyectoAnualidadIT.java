package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoAnualidadController;
import org.crue.hercules.sgi.csp.dto.AnualidadGastoOutput;
import org.crue.hercules.sgi.csp.dto.AnualidadIngresoOutput;
import org.crue.hercules.sgi.csp.dto.AnualidadResumen;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadGastosTotales;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadInput;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadNotificacionSge;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadOutput;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoAnualidadIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ProyectoAnualidadController.MAPPING;
  private static final String PATH_PARTIDAS_RESUMEN = "/partidas-resumen";
  private static final String PATH_NOTIFICACIONES_SGE = "/notificaciones-sge";
  private static final String PATH_NOTIFICA_SGE = "/notificarsge";
  private static final String PATH_ANUALIDAD_GASTOS = "/anualidadgastos";
  private static final String PATH_ANUALIDAD_INGRESOS = "/anualidadingresos";
  private static final String PATH_GASTOS_TOTALES = ProyectoAnualidadController.PATH_GASTOS_TOTALES;

  private HttpEntity<ProyectoAnualidadInput> buildRequest(HttpHeaders headers,
      ProyectoAnualidadInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ProyectoAnualidadInput> request = new HttpEntity<>(entity, headers);

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
    "classpath:scripts/proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoAnualidadOutput() throws Exception {
    ProyectoAnualidadInput toCreate = buildMockProyectoAnualidadInput();

    final ResponseEntity<ProyectoAnualidadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, "CSP-PRO-E"),
        ProyectoAnualidadOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoAnualidadOutput proyectoAnualidadCreated = response.getBody();

    Assertions.assertThat(proyectoAnualidadCreated.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoAnualidadCreated.getAnio()).as("getAnio()")
        .isEqualTo(toCreate.getAnio());
    Assertions.assertThat(proyectoAnualidadCreated.getProyectoId())
        .as("getProyectoId()").isEqualTo(toCreate.getProyectoId());
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
    "classpath:scripts/proyecto_anualidad.sql"
    // @formatter:on   
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Return204() throws Exception {
    // given: existing id
    Long toDeleteId = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, "CSP-PRO-E"), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
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
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_partida.sql",
    "classpath:scripts/anualidad_gasto.sql"
    // @formatter:on   
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllAnualidadGasto_WithPagingSortingAndFiltering_ReturnsAnualidadGastoOutputSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "";

    Long proyectoAnualidadId = 1L;

    // when: find ProyectoAgrupacionGastoOutput
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ANUALIDAD_GASTOS)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(proyectoAnualidadId).toUri();

    final ResponseEntity<List<AnualidadGastoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, "CSP-PRO-E"),
        new ParameterizedTypeReference<List<AnualidadGastoOutput>>() {
        });

    // given: ProyectoAnualidadOutput data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    final List<AnualidadGastoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(2);

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2);

    Assertions.assertThat(responseData.get(0).getCodigoEconomicoRef()).isEqualTo("AL");
    Assertions.assertThat(responseData.get(1).getCodigoEconomicoRef()).isEqualTo("LO");

    Assertions.assertThat(responseData.get(0).getImporteConcedido().intValue())
        .isEqualTo(12000);
    Assertions.assertThat(responseData.get(1).getImporteConcedido().intValue()).isEqualTo(6000);

    Assertions.assertThat(responseData.get(0).getImportePresupuesto().intValue()).isEqualTo(15000);
    Assertions.assertThat(responseData.get(1).getImportePresupuesto().intValue()).isEqualTo(6000);

    Assertions.assertThat(responseData.get(0).getProyectoAnualidadId()).isEqualTo(proyectoAnualidadId);
    Assertions.assertThat(responseData.get(1).getProyectoAnualidadId()).isEqualTo(proyectoAnualidadId);

    Assertions.assertThat(responseData.get(0).getProyectoSgeRef()).isEqualTo("33939");
    Assertions.assertThat(responseData.get(1).getProyectoSgeRef()).isEqualTo("33939");

    Assertions.assertThat(responseData.get(0).getConceptoGasto().getId()).isEqualTo(11L);
    Assertions.assertThat(responseData.get(1).getConceptoGasto().getId()).isEqualTo(3L);

    Assertions.assertThat(responseData.get(0).getProyectoPartida().getId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getProyectoPartida().getId()).isEqualTo(1L);

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
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_partida.sql",
    "classpath:scripts/anualidad_ingreso.sql"
    // @formatter:on   
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllAnualidadIngreso_WithPagingSortingAndFiltering_ReturnsAnualidadIngresoOutputSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "";

    Long proyectoAnualidadId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ANUALIDAD_INGRESOS)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(proyectoAnualidadId).toUri();

    final ResponseEntity<List<AnualidadIngresoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, "CSP-PRO-E"),
        new ParameterizedTypeReference<List<AnualidadIngresoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    final List<AnualidadIngresoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(2);

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2);

    Assertions.assertThat(responseData.get(0).getCodigoEconomicoRef()).isEqualTo("AA.AAAA.AAAA.AAAAB");
    Assertions.assertThat(responseData.get(1).getCodigoEconomicoRef()).isEqualTo("AA.AAAA.AAAB.AAAAB");

    Assertions.assertThat(responseData.get(0).getImporteConcedido().intValue())
        .isEqualTo(11333);
    Assertions.assertThat(responseData.get(1).getImporteConcedido().intValue()).isEqualTo(11666);

    Assertions.assertThat(responseData.get(0).getProyectoAnualidadId()).isEqualTo(proyectoAnualidadId);
    Assertions.assertThat(responseData.get(1).getProyectoAnualidadId()).isEqualTo(proyectoAnualidadId);

    Assertions.assertThat(responseData.get(0).getProyectoSgeRef()).isEqualTo("33333");
    Assertions.assertThat(responseData.get(1).getProyectoSgeRef()).isEqualTo("33333");

    Assertions.assertThat(responseData.get(0).getProyectoPartida().getId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getProyectoPartida().getId()).isEqualTo(1L);
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
    "classpath:scripts/proyecto_anualidad.sql" 
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsProyectoAnualidadOutputSubList() throws Exception {
    // first page, 2 elements per page sorted by id asc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "id<=3";

    // when: find ProyectoAgrupacionGastoOutput
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<ProyectoAnualidadOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, "CSP-EJEC-E", "CSP-EJEC-V", "CSP-EJEC-INV-VR"),
        new ParameterizedTypeReference<List<ProyectoAnualidadOutput>>() {
        });

    // given: ProyectoAnualidadOutput data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoAnualidadOutput> responseData = response.getBody();

    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getProyectoId()).as("get(0).getProyectoId())")
        .isEqualTo(1L);
    Assertions.assertThat(responseData.get(0).getAnio()).as("get(0).getAnio())")
        .isEqualTo(2021);
    Assertions.assertThat(responseData.get(1).getProyectoId()).as("get(1).getProyectoId())")
        .isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getAnio()).as("get(1).getAnio())")
        .isEqualTo(2022);
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
    "classpath:scripts/proyecto_anualidad.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnProyectoAnualidadOutput() throws Exception {

    Long proyectoAnualidadId = 5L;

    final ResponseEntity<ProyectoAnualidadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, "CSP-PRO-V",
            "CSP-PRO-E"),
        ProyectoAnualidadOutput.class,
        proyectoAnualidadId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoAnualidadOutput proyectoAnualidadOutput = response.getBody();

    Assertions.assertThat(proyectoAnualidadOutput).isNotNull();
    Assertions.assertThat(proyectoAnualidadOutput.getId()).as("getId()")
        .isEqualTo(5L);
    Assertions.assertThat(proyectoAnualidadOutput.getAnio()).as("getAnio()")
        .isEqualTo(2023);

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
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/proyecto_anualidad.sql",
      "classpath:scripts/proyecto_partida.sql",
      "classpath:scripts/anualidad_gasto.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void getPartidasResumen_ReturnsAnualidadResumenList() throws Exception {
    String[] roles = { "CSP-PRO-V" };

    Long proyectoAnualidadId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARTIDAS_RESUMEN)
        .buildAndExpand(proyectoAnualidadId).toUri();

    final ResponseEntity<List<AnualidadResumen>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<AnualidadResumen>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody()).hasSize(1);
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
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/proyecto_anualidad.sql",
      "classpath:scripts/proyecto_partida.sql",
      "classpath:scripts/anualidad_gasto.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllNotificacionesSge_WithFiltering_ReturnsProyectoAnualidadNotificacionSgeList() throws Exception {
    String roles = "CSP-EJEC-E";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_NOTIFICACIONES_SGE).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<ProyectoAnualidadNotificacionSge>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoAnualidadNotificacionSge>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_anualidad.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void notificarSge_ReturnsProyectoAnualidad() throws Exception {
    String roles = "CSP-EJEC-E";

    Long proyectoAnualidadId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_NOTIFICA_SGE)
        .buildAndExpand(proyectoAnualidadId).toUri();

    final ResponseEntity<ProyectoAnualidad> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(null, null, roles), ProyectoAnualidad.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getId()).isEqualTo(proyectoAnualidadId);
    Assertions.assertThat(response.getBody().getEnviadoSge()).isTrue();
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
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_partida.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/anualidad_gasto.sql",
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void getTotalImportesProyectoAnualidad_ReturnsProyectoAnualidadGastosTotales() throws Exception {
    String roles = "CSP-SJUS-E";

    Long proyectoAnualidadId = 1L;
    BigDecimal importeConcendidoAnualidadCostesDirectos = new BigDecimal("6000.00");
    BigDecimal importeConcendidoAnualidadCostesIndirectos = new BigDecimal("12000.00");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_GASTOS_TOTALES)
        .buildAndExpand(proyectoAnualidadId).toUri();

    final ResponseEntity<ProyectoAnualidadGastosTotales> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), ProyectoAnualidadGastosTotales.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getImporteConcendidoAnualidadCostesDirectos()).isEqualTo(
        importeConcendidoAnualidadCostesDirectos);
    Assertions.assertThat(response.getBody().getImporteConcendidoAnualidadCostesIndirectos()).isEqualTo(
        importeConcendidoAnualidadCostesIndirectos);
  }

  ProyectoAnualidadInput buildMockProyectoAnualidadInput() {
    return ProyectoAnualidadInput.builder()
        .anio(2022)
        .enviadoSge(Boolean.TRUE)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now().plusSeconds(360000000))
        .presupuestar(Boolean.TRUE)
        .proyectoId(1L)
        .build();
  }
}