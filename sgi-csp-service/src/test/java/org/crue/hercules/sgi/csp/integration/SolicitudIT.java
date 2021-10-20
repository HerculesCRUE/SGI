package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
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
 * Test de integracion de Solicitud.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class SolicitudIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/solicitudes";
  private static final String PATH_SOLICITUD_MODALIDADES = "/solicitudmodalidades";
  private static final String PATH_ESTADOS_SOLICITUD = "/estadosolicitudes";
  private static final String PATH_ENTIDAD_FINANCIADORA_AJENA = "/solicitudproyectoentidadfinanciadoraajenas";
  private static final String PATH_SOLICITUD_PROYECTO_PRESUPUESTO = "/solicitudproyectopresupuestos";
  private static final String PATH_TODOS = "/todos";

  private HttpEntity<Solicitud> buildRequest(HttpHeaders headers, Solicitud entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "AUTH", "CSP-SOL-C", "CSP-SOL-E", "CSP-SOL-V", "CSP-SOL-B", "CSP-SOL-R")));

    HttpEntity<Solicitud> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsSolicitud() throws Exception {
    Solicitud solicitud = generarMockSolicitud(null);

    final ResponseEntity<Solicitud> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitud), Solicitud.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Solicitud solicitudCreado = response.getBody();
    Assertions.assertThat(solicitudCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudCreado.getCodigoExterno()).as("getCodigoExterno()")
        .isEqualTo(solicitud.getCodigoExterno());
    Assertions.assertThat(solicitudCreado.getCodigoRegistroInterno()).as("getCodigoRegistroInterno()").isNotNull();
    Assertions.assertThat(solicitudCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(solicitudCreado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(solicitud.getConvocatoriaId());
    Assertions.assertThat(solicitudCreado.getCreadorRef()).as("getCreadorRef()").isNotNull();
    Assertions.assertThat(solicitudCreado.getSolicitanteRef()).as("getSolicitanteRef()")
        .isEqualTo(solicitud.getSolicitanteRef());
    Assertions.assertThat(solicitudCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(solicitud.getObservaciones());
    Assertions.assertThat(solicitudCreado.getConvocatoriaExterna()).as("getConvocatoriaExterna()")
        .isEqualTo(solicitud.getConvocatoriaExterna());
    Assertions.assertThat(solicitudCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(solicitud.getUnidadGestionRef());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitud() throws Exception {
    Long idSolicitud = 1L;
    Solicitud solicitud = generarMockSolicitud(1L);
    solicitud.setObservaciones("observaciones actualizadas");

    final ResponseEntity<Solicitud> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, solicitud), Solicitud.class, idSolicitud);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Solicitud solicitudActualizado = response.getBody();
    Assertions.assertThat(solicitudActualizado.getId()).as("getId()").isEqualTo(solicitud.getId());
    Assertions.assertThat(solicitudActualizado.getCodigoExterno()).as("getCodigoExterno()")
        .isEqualTo(solicitud.getCodigoExterno());
    Assertions.assertThat(solicitudActualizado.getCodigoRegistroInterno()).as("getCodigoRegistroInterno()")
        .isEqualTo(solicitud.getCodigoRegistroInterno());
    Assertions.assertThat(solicitudActualizado.getEstado().getId()).as("getEstado().getId()")
        .isEqualTo(solicitud.getEstado().getId());
    Assertions.assertThat(solicitudActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(solicitud.getConvocatoriaId());
    Assertions.assertThat(solicitudActualizado.getCreadorRef()).as("getCreadorRef()")
        .isEqualTo(solicitud.getCreadorRef());
    Assertions.assertThat(solicitudActualizado.getSolicitanteRef()).as("getSolicitanteRef()")
        .isEqualTo(solicitud.getSolicitanteRef());
    Assertions.assertThat(solicitudActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(solicitud.getObservaciones());
    Assertions.assertThat(solicitudActualizado.getConvocatoriaExterna()).as("getConvocatoriaExterna()")
        .isEqualTo(solicitud.getConvocatoriaExterna());
    Assertions.assertThat(solicitudActualizado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(solicitud.getUnidadGestionRef());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnSolicitud() throws Exception {
    Long idSolicitud = 1L;

    final ResponseEntity<Solicitud> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), Solicitud.class, idSolicitud);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Solicitud solicitud = response.getBody();
    Assertions.assertThat(solicitud.getId()).as("getId()").isEqualTo(idSolicitud);
    Assertions.assertThat(solicitud.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void reactivar_ReturnSolicitud() throws Exception {
    Long idSolicitud = 1L;

    final ResponseEntity<Solicitud> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        Solicitud.class, idSolicitud);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Solicitud solicitud = response.getBody();
    Assertions.assertThat(solicitud.getId()).as("getId()").isEqualTo(idSolicitud);
    Assertions.assertThat(solicitud.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitud() throws Exception {
    Long idSolicitud = 1L;

    final ResponseEntity<Solicitud> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Solicitud.class, idSolicitud);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Solicitud solicitud = response.getBody();
    Assertions.assertThat(solicitud.getId()).as("getId()").isEqualTo(idSolicitud);
    Assertions.assertThat(solicitud.getCodigoExterno()).as("getCodigoExterno()").isNull();
    Assertions.assertThat(solicitud.getCodigoRegistroInterno()).as("getCodigoRegistroInterno()")
        .isEqualTo("SGI_SLC1202011061027");
    Assertions.assertThat(solicitud.getEstado().getId()).as("getEstado().getId()").isEqualTo(1);
    Assertions.assertThat(solicitud.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1);
    Assertions.assertThat(solicitud.getCreadorRef()).as("getCreadorRef()").isEqualTo("usr-001");
    Assertions.assertThat(solicitud.getSolicitanteRef()).as("getSolicitanteRef()").isEqualTo("usr-002");
    Assertions.assertThat(solicitud.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones 1");
    Assertions.assertThat(solicitud.getConvocatoriaExterna()).as("getConvocatoriaExterna()").isEqualTo(null);
    Assertions.assertThat(solicitud.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("2");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsSolicitudSubList() throws Exception {

    // given: data for Solicitud

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "observaciones,desc";
    String filter = "unidadGestionRef==2";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<Solicitud>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Solicitud>>() {
        });

    // given: Solicitud data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Solicitud> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getObservaciones()).as("get(2).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsSolicitudSubList() throws Exception {

    // given: data for Solicitud

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "observaciones,desc";
    String filter = "unidadGestionRef==2";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();
    final ResponseEntity<List<Solicitud>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Solicitud>>() {
        });

    // given: Solicitud data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Solicitud> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getObservaciones()).as("get(2).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 1));
  }

  /**
   * 
   * SOLICITUD MODALIDAD
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllSolicitudModalidad_WithPagingSortingAndFiltering_ReturnsSolicitudModalidadSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "entidadRef=ke=-00";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_MODALIDADES)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudModalidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<SolicitudModalidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudModalidad> solicitudModalidades = response.getBody();
    Assertions.assertThat(solicitudModalidades.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(solicitudModalidades.get(0).getEntidadRef()).as("get(0).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 3));
    Assertions.assertThat(solicitudModalidades.get(1).getEntidadRef()).as("get(1).getEntidadRef())")
        .isEqualTo("entidad-" + String.format("%03d", 2));
    Assertions.assertThat(solicitudModalidades.get(2).getEntidadRef()).as("get(2).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 1));
  }

  /**
   * 
   * SOLICITUD ESTADOS
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllEstadoSolicitud_WithPagingSortingAndFiltering_ReturnsEstadoSolicitudSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ESTADOS_SOLICITUD)
        .queryParam("s", sort).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<EstadoSolicitud>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<EstadoSolicitud>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoSolicitud> estadosSolicitud = response.getBody();
    Assertions.assertThat(estadosSolicitud.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(estadosSolicitud.get(0).getEstado()).as("get(0).getEstado()")
        .isEqualTo(EstadoSolicitud.Estado.EXCLUIDA_DEFINITIVA);
    Assertions.assertThat(estadosSolicitud.get(1).getEstado()).as("get(1).getEstado()")
        .isEqualTo(EstadoSolicitud.Estado.SOLICITADA);
    Assertions.assertThat(estadosSolicitud.get(2).getEstado()).as("get(2).getEstado()")
        .isEqualTo(EstadoSolicitud.Estado.BORRADOR);
  }

  /**
   * 
   * SOLICITUD DOCUMENTOS
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllSolicitudDocumento_WithPagingSortingAndFiltering_ReturnsSolicitudDocumentoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "documentoRef=ke=-00";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicituddocumentos")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<SolicitudDocumento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudDocumento> solicitudDocumentos = response.getBody();
    Assertions.assertThat(solicitudDocumentos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
  }

  /**
   * 
   * SOLICITUD HITO
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllSolicitudHito_WithPagingSortingAndFiltering_ReturnsSolicitudHitoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "comentario=ke=-00";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudhitos")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudHito>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<SolicitudHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudHito> solicitudHitos = response.getBody();
    Assertions.assertThat(solicitudHitos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findSolicitudProyecto_ReturnsSolicitudProyectoSubList() throws Exception {
    Long idSolicitud = 1L;

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyecto", HttpMethod.GET, buildRequest(null, null),
        SolicitudProyecto.class, idSolicitud);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existSolictudProyectoDatos_Returns204() throws Exception {

    // given: existing Solicitud datos proyecto for solicitud
    Long id = 1L;

    // when: check exist solicitud datos proyecto
    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyecto", HttpMethod.HEAD, buildRequest(null, null),
        SolicitudProyecto.class, id);

    // then: Response is 200 OK
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existSolictudProyectoDatos_Returns200() throws Exception {

    // given: existing Solicitud datos proyecto for solicitud
    Long id = 1L;

    // when: check exist solicitud datos proyecto
    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyecto", HttpMethod.HEAD, buildRequest(null, null),
        SolicitudProyecto.class, id);

    // then: Response is 204 No Content
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  /**
   * 
   * SOLICITUD PROYECTO SOCIO
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllSolicitudProyectoSocio_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoSocioSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "rolSocio.id==1";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectosocio")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoSocio>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<SolicitudProyectoSocio>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoSocio> solicitudProyectoSocio = response.getBody();
    Assertions.assertThat(solicitudProyectoSocio.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
  }

  /**
   * 
   * SOLICITUD PROYECTO ENTIDAD FINANCIADORA AJENA
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllSolicitudProyectoEntidadFinanciadoraAjena_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoEntidadFinanciadoraAjenaSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "entidadRef=ke=00";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_FINANCIADORA_AJENA).queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoEntidadFinanciadoraAjena>> response = restTemplate.exchange(uri,
        HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<SolicitudProyectoEntidadFinanciadoraAjena>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoEntidadFinanciadoraAjena> solicitudProyectoEntidadFinanciadoraAjenas = response
        .getBody();
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenas.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenas.get(0).getEntidadRef())
        .as("get(0).getEntidadRef()").isEqualTo("entidad-" + String.format("%03d", 3));
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenas.get(1).getEntidadRef())
        .as("get(1).getEntidadRef())").isEqualTo("entidad-" + String.format("%03d", 2));
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenas.get(2).getEntidadRef())
        .as("get(2).getEntidadRef()").isEqualTo("entidad-" + String.format("%03d", 1));
  }

  /**
   * 
   * SOLICITUD PROYECTO PRESUPUESTO
   * 
   */

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllSolicitudProyectoPresupuesto_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoPresupuestoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "observaciones=ke=00";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_PRESUPUESTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoPresupuesto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<SolicitudProyectoPresupuesto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoPresupuesto> solicitudProyectoPresupuestos = response.getBody();
    Assertions.assertThat(solicitudProyectoPresupuestos.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(solicitudProyectoPresupuestos.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 3));
    Assertions.assertThat(solicitudProyectoPresupuestos.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 2));
    Assertions.assertThat(solicitudProyectoPresupuestos.get(2).getObservaciones()).as("get(2).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto Solicitud
   * 
   * @param id id del Solicitud
   * @return el objeto Solicitud
   */
  private Solicitud generarMockSolicitud(Long id) {
    EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
    estadoSolicitud.setId(1L);
    estadoSolicitud.setEstado(EstadoSolicitud.Estado.BORRADOR);

    Programa programa = new Programa();
    programa.setId(1L);

    Solicitud solicitud = new Solicitud();
    solicitud.setId(id);
    solicitud.setTitulo("titulo");
    solicitud.setCodigoExterno(null);
    solicitud.setConvocatoriaId(1L);
    solicitud.setSolicitanteRef("usr-002");
    solicitud.setObservaciones("observaciones");
    solicitud.setConvocatoriaExterna(null);
    solicitud.setUnidadGestionRef("2");
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    solicitud.setActivo(true);

    if (id != null) {
      solicitud.setEstado(estadoSolicitud);
      solicitud.setCodigoRegistroInterno("SGI_SLC1202011061027");
      solicitud.setCreadorRef("usr-001");
    }

    return solicitud;
  }

}
