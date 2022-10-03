package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.SeguimientoEjecucionEconomicaController;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoEjecucionEconomica;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionOutput;
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
 * Test de integracion de ProyectoSeguimientoEjecucionEconomica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeguimientoEjecucionEconomicaIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = SeguimientoEjecucionEconomicaController.REQUEST_MAPPING;
  private static final String PATH_PROYECTOS = SeguimientoEjecucionEconomicaController.PATH_PROYECTOS;
  private static final String PATH_PERIODO_JUSTIFICACION = SeguimientoEjecucionEconomicaController.PATH_PERIODO_JUSTIFICACION;
  private static final String PATH_PERIODO_SEGUIMIENTO = SeguimientoEjecucionEconomicaController.PATH_PERIODO_SEGUIMIENTO;
  private static final String PATH_REQUERIMIENTO_JUSTIFICACION = SeguimientoEjecucionEconomicaController.PATH_REQUERIMIENTO_JUSTIFICACION;
  private static final String PATH_SEGUIMIENTO_JUSTIFICACION = SeguimientoEjecucionEconomicaController.PATH_SEGUIMIENTO_JUSTIFICACION;

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
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/proyecto_proyecto_sge.sql" 
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findProyectosSeguimientoEjecucionEconomica_WithPagingSorting_ReturnsProyectoSeguimientoEjecucionEconomicaSubList()
      throws Exception {

    // given: data for ProyectoSeguimientoEjecucionEconomica and a proyectoSgeRef
    String proyectoSgeRef = "proyecto-sge-ref-001";
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";

    // when: find ProyectoSeguimientoEjecucionEconomica
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PROYECTOS).queryParam("s", sort)
        .buildAndExpand(proyectoSgeRef).toUri();
    final ResponseEntity<List<ProyectoSeguimientoEjecucionEconomica>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<List<ProyectoSeguimientoEjecucionEconomica>>() {
        });

    // given: ProyectoSeguimientoEjecucionEconomica data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoSeguimientoEjecucionEconomica> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(responseData.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("PRO1");
    Assertions.assertThat(responseData.get(0).getCodigoExterno()).as("get(0).getCodigoExterno())")
        .isEqualTo("cod-externo-001");
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
      "classpath:scripts/proyecto_proyecto_sge.sql" ,
      "classpath:scripts/contexto_proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto_periodo_justificacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findProyectoPeriodosJustificacion_WithPagingSorting_ReturnsProyectoPeriodoJustificacionOutputSubList()
      throws Exception {

    // given: data for ProyectoPeriodoJustificacionOutput and a proyectoSgeRef
    String proyectoSgeRef = "proyecto-sge-ref-001";
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "observaciones,desc";

    // when: find ProyectoPeriodoJustificacionOutput
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PERIODO_JUSTIFICACION)
        .queryParam("s", sort).buildAndExpand(proyectoSgeRef).toUri();
    final ResponseEntity<List<ProyectoPeriodoJustificacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<List<ProyectoPeriodoJustificacionOutput>>() {
        });

    // given: ProyectoPeriodoJustificacionOutput data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoPeriodoJustificacionOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones())")
        .isEqualTo("testing periodo 3");
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("testing periodo 2");
    Assertions.assertThat(responseData.get(2).getObservaciones()).as("get(2).getObservaciones())")
        .isEqualTo("testing periodo 1");
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
      "classpath:scripts/proyecto_proyecto_sge.sql" ,
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto_periodo_seguimiento.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findProyectoPeriodosSeguimiento_WithPagingSorting_ReturnsProyectoPeriodoSeguimientoOutputSubList()
      throws Exception {

    // given: data for ProyectoPeriodoJustificacionOutput and a proyectoSgeRef
    String proyectoSgeRef = "proyecto-sge-ref-001";
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "observaciones,desc";

    // when: find ProyectoPeriodoJustificacionOutput
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PERIODO_SEGUIMIENTO)
        .queryParam("s", sort).buildAndExpand(proyectoSgeRef).toUri();
    final ResponseEntity<List<ProyectoPeriodoJustificacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<List<ProyectoPeriodoJustificacionOutput>>() {
        });

    // given: ProyectoPeriodoJustificacionOutput data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoPeriodoJustificacionOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("5");

    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones())")
        .isEqualTo("obs-5");
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("obs-4");
    Assertions.assertThat(responseData.get(2).getObservaciones()).as("get(2).getObservaciones())")
        .isEqualTo("obs-003");
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
  void findRequerimientosJustificacion_WithPagingSorting_ReturnsRequerimientoJustificacionOutputSubList()
      throws Exception {

    // given: data for RequerimientoJustificacionOutput and a proyectoSgeRef
    String proyectoSgeRef = "proyecto-sge-ref-001";
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "observaciones,desc";

    // when: find RequerimientoJustificacionOutput
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_REQUERIMIENTO_JUSTIFICACION)
        .queryParam("s", sort).buildAndExpand(proyectoSgeRef).toUri();
    final ResponseEntity<List<RequerimientoJustificacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<List<RequerimientoJustificacionOutput>>() {
        });

    // given: RequerimientoJustificacionOutput data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<RequerimientoJustificacionOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones())")
        .isEqualTo("obs-002");
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("obs-001");
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
  void findSeguimientosJustificacion_WithPagingSorting_ReturnsProyectoSeguimientoJustificacionOutputSubList()
      throws Exception {
    String proyectoSgeRef = "proyecto-sge-ref-001";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "justificanteReintegro,desc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_SEGUIMIENTO_JUSTIFICACION)
        .queryParam("s", sort).buildAndExpand(proyectoSgeRef).toUri();
    final ResponseEntity<List<ProyectoSeguimientoJustificacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<List<ProyectoSeguimientoJustificacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoSeguimientoJustificacionOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(responseData.get(0).getJustificanteReintegro()).as("get(0).getJustificanteReintegro())")
        .isEqualTo("justificante-reintegro-001");
  }
}
