package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de Proyecto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class ProyectoIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/proyectos";
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_HITOS = "/proyectohitos";
  private static final String PATH_FASES = "/proyectofases";
  private static final String PATH_PROYECTO_SOCIO = "/proyectosocios";
  private static final String PATH_PAQUETE_TRABAJO = "/proyectopaquetetrabajos";
  private static final String PATH_PROYECTO_EQUIPO = "/proyectoequipos";
  private static final String PATH_ENTIDAD_GESTORA = "/proyectoentidadgestoras";
  private static final String PATH_PRORROGA = "/proyecto-prorrogas";

  private HttpEntity<Proyecto> buildRequest(HttpHeaders headers, Proyecto entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-PRO-C", "AUTH", "CSP-PRO-E", "CSP-PRO-B", "CSP-PRO-R")));

    HttpEntity<Proyecto> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsProyecto() throws Exception {
    Proyecto proyecto = generarMockProyecto(null);

    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, proyecto), Proyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Proyecto proyectoCreado = response.getBody();
    Assertions.assertThat(proyectoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsProyecto() throws Exception {
    Long idProyecto = 1L;
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setObservaciones("observaciones actualizadas");

    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, proyecto), Proyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Proyecto proyectoActualizado = response.getBody();
    Assertions.assertThat(proyectoActualizado.getId()).as("getId()").isEqualTo(proyecto.getId());
    Assertions.assertThat(proyectoActualizado.getEstado().getId()).as("getEstado().getId()")
        .isEqualTo(proyecto.getEstado().getId());
    Assertions.assertThat(proyectoActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoActualizado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnProyecto() throws Exception {
    Long idProyecto = 1L;

    final ResponseEntity<Proyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), Proyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Proyecto proyecto = response.getBody();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyecto.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void reactivar_ReturnProyecto() throws Exception {
    Long idProyecto = 5L;

    final ResponseEntity<Proyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        Proyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Proyecto proyecto = response.getBody();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyecto.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existsById_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Proyecto.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsProyecto() throws Exception {
    Long idProyecto = 1L;

    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Proyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Proyecto proyecto = response.getBody();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyecto.getEstado().getId()).as("getEstado().getId()").isEqualTo(1);
    Assertions.assertThat(proyecto.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones-proyecto-001");
    Assertions.assertThat(proyecto.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("2");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsProyectoSubList() throws Exception {

    // given: data for Proyecto

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "observaciones,desc";
    String filter = "unidadGestionRef==2";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<Proyecto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Proyecto>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Proyecto> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones())")
        .isEqualTo("observaciones-proyecto-" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-proyecto-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getObservaciones()).as("get(2).getObservaciones())")
        .isEqualTo("observaciones-proyecto-" + String.format("%03d", 1));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsProyectoSubList() throws Exception {

    // given: data for Proyecto

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "observaciones,desc";
    String filter = "unidadGestionRef==2";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();
    final ResponseEntity<List<Proyecto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Proyecto>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Proyecto> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("4");

    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones())")
        .isEqualTo("observaciones-proyecto-" + String.format("%03d", 5));
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-proyecto-" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(2).getObservaciones()).as("get(2).getObservaciones())")
        .isEqualTo("observaciones-proyecto-" + String.format("%03d", 2));
  }

  /*
   * PROYECTO HITO
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/tipo_hito.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_hito.sql", "classpath:scripts/proyecto_hito.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllProyectoHito_WithPagingSortingAndFiltering_ReturnsProyectoHitoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-THIT-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "comentario=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_HITOS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoHito>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ProyectoHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoHito> proyectosHitos = response.getBody();
    Assertions.assertThat(proyectosHitos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(proyectosHitos.get(0).getComentario()).as("get(0).getComentario()")
        .isEqualTo("comentario-proyecto-hito-" + String.format("%03d", 3));
    Assertions.assertThat(proyectosHitos.get(1).getComentario()).as("get(1).getComentario())")
        .isEqualTo("comentario-proyecto-hito-" + String.format("%03d", 2));
    Assertions.assertThat(proyectosHitos.get(2).getComentario()).as("get(2).getComentario()")
        .isEqualTo("comentario-proyecto-hito-" + String.format("%03d", 1));

  }

  /*
   * PROYECTO FASE
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_fase.sql", "classpath:scripts/proyecto_fase.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllProyectoFase_WithPagingSortingAndFiltering_ReturnsProyectoFaseSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-THIT-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "observaciones=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_FASES)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoFase>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ProyectoFase>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoFase> proyectosFases = response.getBody();
    Assertions.assertThat(proyectosFases.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(proyectosFases.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("observaciones-proyecto-fase-" + String.format("%03d", 3));
    Assertions.assertThat(proyectosFases.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-proyecto-fase-" + String.format("%03d", 2));
    Assertions.assertThat(proyectosFases.get(2).getObservaciones()).as("get(2).getObservaciones()")
        .isEqualTo("observaciones-proyecto-fase-" + String.format("%03d", 1));

  }

  /*
   * PROYECTO PAQUETE TRABAJO
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_paquete_trabajo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllProyectoPaqueteTrabajo_WithPagingSortingAndFiltering_ReturnsProyectoPaqueteTrabajoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "nombre=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PAQUETE_TRABAJO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoPaqueteTrabajo>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ProyectoPaqueteTrabajo>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoPaqueteTrabajo> proyectosPaqueteTrabajos = response.getBody();
    Assertions.assertThat(proyectosPaqueteTrabajos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(proyectosPaqueteTrabajos.get(0).getNombre()).as("get(0).getNombre()")
        .isEqualTo("proyecto-paquete-trabajo-" + String.format("%03d", 3));
    Assertions.assertThat(proyectosPaqueteTrabajos.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("proyecto-paquete-trabajo-" + String.format("%03d", 2));
    Assertions.assertThat(proyectosPaqueteTrabajos.get(2).getNombre()).as("get(2).getNombre()")
        .isEqualTo("proyecto-paquete-trabajo-" + String.format("%03d", 1));

  }

  /*
   * 
   * PROYECTO SOCIO
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllProyectoSocio_WithPagingSortingAndFiltering_ReturnsProyectoSocioSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "empresaRef=ke=00";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<ProyectoSocio>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ProyectoSocio>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoSocio> proyectoSocios = response.getBody();
    Assertions.assertThat(proyectoSocios.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(proyectoSocios.get(0).getEmpresaRef()).as("get(0).getEmpresaRef())")
        .isEqualTo("empresa-" + String.format("%03d", 3));
    Assertions.assertThat(proyectoSocios.get(1).getEmpresaRef()).as("get(1).getEmpresaRef())")
        .isEqualTo("empresa-" + String.format("%03d", 2));
    Assertions.assertThat(proyectoSocios.get(2).getEmpresaRef()).as("get(2).getEmpresaRef())")
        .isEqualTo("empresa-" + String.format("%03d", 1));
  }

  /*
   * PROYECTO ENTIDAD GESTORA
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_entidad_gestora.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllProyectoEntidadGestora_WithPagingSortingAndFiltering_ReturnsProyectoEntidadGestoraSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-THIT-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "entidadRef=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_GESTORA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoEntidadGestora>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ProyectoEntidadGestora>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoEntidadGestora> proyectosEntidadGestoras = response.getBody();
    Assertions.assertThat(proyectosEntidadGestoras.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(proyectosEntidadGestoras.get(0).getEntidadRef()).as("get(0).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 3));
    Assertions.assertThat(proyectosEntidadGestoras.get(1).getEntidadRef()).as("get(1).getEntidadRef())")
        .isEqualTo("entidad-" + String.format("%03d", 2));
    Assertions.assertThat(proyectosEntidadGestoras.get(2).getEntidadRef()).as("get(2).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 1));

  }

  /*
   * 
   * PROYECTO EQUIPO
   * 
   */

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllProyectoEquipo_WithPagingSortingAndFiltering_ReturnsProyectoEquipoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "personaRef=ke=00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_EQUIPO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoEquipo>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ProyectoEquipo>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoEquipo> proyectoEquipos = response.getBody();
    Assertions.assertThat(proyectoEquipos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(proyectoEquipos.get(0).getPersonaRef()).as("get(0).getPersonaRef())")
        .isEqualTo("ref-" + String.format("%03d", 4));
    Assertions.assertThat(proyectoEquipos.get(1).getPersonaRef()).as("get(1).getPersonaRef())")
        .isEqualTo("ref-" + String.format("%03d", 2));
    Assertions.assertThat(proyectoEquipos.get(2).getPersonaRef()).as("get(2).getPersonaRef())")
        .isEqualTo("ref-" + String.format("%03d", 1));
  }

  /*
   *
   * PROYECTO PRÓRROGA
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllProyectoProrroga_WithPagingSortingAndFiltering_ReturnsProyectoProrrogaSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-THIT-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "observaciones=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRORROGA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoProrroga>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ProyectoProrroga>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoProrroga> proyectosProrrogas = response.getBody();
    Assertions.assertThat(proyectosProrrogas.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(proyectosProrrogas.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("observaciones-proyecto-prorroga-" + String.format("%03d", 3));
    Assertions.assertThat(proyectosProrrogas.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-proyecto-prorroga-" + String.format("%03d", 2));
    Assertions.assertThat(proyectosProrrogas.get(2).getObservaciones()).as("get(2).getObservaciones()")
        .isEqualTo("observaciones-proyecto-prorroga-" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto Proyecto
   * 
   * @param id id del Proyecto
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(Long id) {
    EstadoProyecto estadoProyecto = generarMockEstadoProyecto(1L);

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setTitulo("PRO" + (id != null ? id : 1));
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones("observaciones-" + String.format("%03d", id));
    proyecto.setUnidadGestionRef("2");
    proyecto.setFechaInicio(Instant.now());
    proyecto.setFechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3))));
    proyecto.setModeloEjecucion(modeloEjecucion);
    proyecto.setFinalidad(tipoFinalidad);
    proyecto.setAmbitoGeografico(tipoAmbitoGeografico);
    proyecto.setConfidencial(Boolean.FALSE);
    proyecto.setActivo(true);

    if (id != null) {
      proyecto.setEstado(estadoProyecto);
    }

    return proyecto;
  }

  /**
   * Función que devuelve un objeto EstadoProyecto
   * 
   * @param id id del EstadoProyecto
   * @return el objeto EstadoProyecto
   */
  private EstadoProyecto generarMockEstadoProyecto(Long id) {
    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(id);
    estadoProyecto.setComentario("Estado-" + id);
    estadoProyecto.setEstado(EstadoProyecto.Estado.BORRADOR);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    return estadoProyecto;
  }

}
