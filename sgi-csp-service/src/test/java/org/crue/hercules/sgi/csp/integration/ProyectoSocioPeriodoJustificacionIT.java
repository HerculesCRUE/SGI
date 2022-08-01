package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Test de integracion de ProyectoSocioPeriodoJustificacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoSocioPeriodoJustificacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_PROYECTO_SOCIO_ID = "/{proyectoSocioId}";
  private static final String CONTROLLER_BASE_PATH = "/proyectosocioperiodojustificaciones";

  @Autowired
  private ProyectoSocioPeriodoJustificacionRepository proyectoSocioPeriodoJustificacionRepository;

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-E")));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<List<ProyectoSocioPeriodoJustificacion>> buildRequestList(HttpHeaders headers,
      List<ProyectoSocioPeriodoJustificacion> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-E")));

    HttpEntity<List<ProyectoSocioPeriodoJustificacion>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/rol_socio.sql", "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_socio.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoSocioPeriodoJustificacion() throws Exception {

    // given: un nuevo proyecto socio periodo justificación
    Long updateProyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion newProyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);

    // when: createProyectoSocioPeriodoJustificacionesProyectoSocio

    final ResponseEntity<ProyectoSocioPeriodoJustificacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newProyectoSocioPeriodoJustificacion),
        ProyectoSocioPeriodoJustificacion.class);

    // then: Se crea el nuevo ProyectoSocioPeriodoJustificacion, y se eliminan los
    // otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoSocioPeriodoJustificacion responseData = response.getBody();

    Assertions.assertThat(responseData.getProyectoSocioId()).as(".getProyectoSocioId()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacionId);
    Assertions.assertThat(responseData.getFechaInicio()).as(".getFechaInicio()")
        .isEqualTo(newProyectoSocioPeriodoJustificacion.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as(".getFechaFin()")
        .isEqualTo(newProyectoSocioPeriodoJustificacion.getFechaFin());
    Assertions.assertThat(responseData.getFechaInicioPresentacion()).as(".getFechaInicioPresentacion()")
        .isEqualTo(newProyectoSocioPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(responseData.getFechaFinPresentacion()).as(".getFechaFinPresentacion()")
        .isEqualTo(newProyectoSocioPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(responseData.getNumPeriodo()).as(".getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(responseData.getObservaciones()).as(".getObservaciones()")
        .isEqualTo(newProyectoSocioPeriodoJustificacion.getObservaciones());
    Assertions.assertThat(responseData.getDocumentacionRecibida()).as(".getDocumentacionRecibida()")
        .isEqualTo(newProyectoSocioPeriodoJustificacion.getDocumentacionRecibida());

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    URI uriFindAllProyectoSocioPeriodoJustificacion = UriComponentsBuilder
        .fromUriString("/proyectosocios" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH).queryParam("s", sort)
        .buildAndExpand(updateProyectoSocioPeriodoJustificacionId).toUri();

    final ResponseEntity<List<ProyectoSocioPeriodoJustificacion>> responseFindAllProyectoSocioPeriodoJustificacion = restTemplate
        .exchange(uriFindAllProyectoSocioPeriodoJustificacion, HttpMethod.GET, buildRequestList(headers, null),
            new ParameterizedTypeReference<List<ProyectoSocioPeriodoJustificacion>>() {
            });

    Assertions.assertThat(responseFindAllProyectoSocioPeriodoJustificacion.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoSocioPeriodoJustificacion> responseDataFindAll = responseFindAllProyectoSocioPeriodoJustificacion
        .getBody();
    Assertions.assertThat(responseDataFindAll.size()).as("size()").isEqualTo(1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/rol_socio.sql", "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_socio.sql", "classpath:scripts/proyecto_socio_periodo_justificacion.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoSocioPeriodoJustificacion() throws Exception {

    // given: existing ProyectoSocioPeriodoJustificacion to be updated
    ProyectoSocioPeriodoJustificacion updateProyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);

    // when: update ProyectoSocioPeriodoJustificacion
    final ResponseEntity<ProyectoSocioPeriodoJustificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, updateProyectoSocioPeriodoJustificacion), ProyectoSocioPeriodoJustificacion.class,
        updateProyectoSocioPeriodoJustificacion.getId());

    // then: ProyectoSocio is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProyectoSocioPeriodoJustificacion responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoSocioId()).as("getProyectoSocioId()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion.getProyectoSocioId());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion.getFechaFin());
    Assertions.assertThat(responseData.getFechaFinPresentacion()).as("getFechaFinPresentacion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(responseData.getFechaInicioPresentacion()).as("getFechaInicioPresentacion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(responseData.getFechaRecepcion()).as("getFechaRecepcion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion.getFechaRecepcion());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/rol_socio.sql", "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_socio.sql", "classpath:scripts/proyecto_socio_periodo_justificacion.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoSocioPeriodoJustificacion() throws Exception {
    Long idProyectoSocioPeriodoJustificacion = 1L;

    final ResponseEntity<ProyectoSocioPeriodoJustificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ProyectoSocioPeriodoJustificacion.class, idProyectoSocioPeriodoJustificacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoSocioPeriodoJustificacion convocatoriaPeriodoJustificacion = response.getBody();
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getId()).as("getId()")
        .isEqualTo(idProyectoSocioPeriodoJustificacion);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getProyectoSocioId()).as("getProyectoSocioId()")
        .isEqualTo(1L);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(Instant.parse("2021-01-11T00:00:00Z"));
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getFechaFin()).as("getFechaFin()")
        .isEqualTo(Instant.parse("2021-09-21T23:59:59Z"));
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getFechaInicioPresentacion())
        .as("getFechaInicioPresentacion()").isNull();
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getFechaFinPresentacion()).as("getFechaFinPresentacion()")
        .isNull();
    ;
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones 1");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/rol_socio.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_socio.sql",
      "classpath:scripts/proyecto_socio_periodo_justificacion.sql"
      // formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void exists_ReturnsHttpStatus200() throws Exception {
    Long idProyectoSocioPeriodoJustificacion = 1L;

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.HEAD, buildRequest(null, null),
        Void.class, idProyectoSocioPeriodoJustificacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/rol_socio.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_socio.sql",
      "classpath:scripts/proyecto_socio_periodo_justificacion.sql"
      // formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_ReturnsProyectoSocioPeriodoJustificacion() throws Exception {
    Long proyectoSocioId = 1L;
    List<ProyectoSocioPeriodoJustificacion> toDelete = proyectoSocioPeriodoJustificacionRepository
        .findAllByProyectoSocioId(proyectoSocioId);

    final ResponseEntity<List<ProyectoSocioPeriodoJustificacion>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_PROYECTO_SOCIO_ID, HttpMethod.PATCH, buildRequest(null, toDelete),
        new ParameterizedTypeReference<List<ProyectoSocioPeriodoJustificacion>>() {
        }, proyectoSocioId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  /**
   * Función que devuelve un objeto ProyectoSocioPeriodoJustificacion
   * 
   * @param id id del ProyectoSocioPeriodoJustificacion
   * @return el objeto ProyectoSocioPeriodoJustificacion
   */
  private ProyectoSocioPeriodoJustificacion generarMockProyectoSocioPeriodoJustificacion(Long id) {
    ProyectoSocioPeriodoJustificacion convocatoriaPeriodoJustificacion = new ProyectoSocioPeriodoJustificacion();
    convocatoriaPeriodoJustificacion.setId(id);
    convocatoriaPeriodoJustificacion.setProyectoSocioId(id == null ? 1 : id);
    convocatoriaPeriodoJustificacion.setNumPeriodo(1);
    convocatoriaPeriodoJustificacion.setFechaInicio(Instant.parse("2021-01-11T00:00:00Z"));
    convocatoriaPeriodoJustificacion.setFechaFin(Instant.parse("2021-09-21T23:59:59Z"));
    convocatoriaPeriodoJustificacion.setFechaInicioPresentacion(Instant.parse("2021-10-10T00:00:00Z"));
    convocatoriaPeriodoJustificacion.setFechaFinPresentacion(Instant.parse("2021-11-20T23:59:59Z"));
    convocatoriaPeriodoJustificacion.setObservaciones("observaciones-" + id);

    return convocatoriaPeriodoJustificacion;
  }

}
