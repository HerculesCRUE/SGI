package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.dto.SolicitudPalabraClaveInput;
import org.crue.hercules.sgi.csp.dto.SolicitudPalabraClaveOutput;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotalConceptoGasto;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoResponsableEconomicoOutput;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud.Estado;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.junit.jupiter.api.Test;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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

/**
 * Test de integracion de Solicitud.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/solicitudes";
  private static final String PATH_SOLICITUD_MODALIDADES = "/solicitudmodalidades";
  private static final String PATH_ESTADOS_SOLICITUD = "/estadosolicitudes";
  private static final String PATH_ENTIDAD_FINANCIADORA_AJENA = "/solicitudproyectoentidadfinanciadoraajenas";
  private static final String PATH_SOLICITUD_PROYECTO_PRESUPUESTO = "/solicitudproyectopresupuestos";
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_ENTIDAD_CONVOCATORIA = "/entidadconvocatoria";
  private static final String PATH_PARAMETER_ENTIDAD_REF = "/{entidadRef}";
  private static final String PATH_SOLICITUD_PROYECTO = "/solicitudproyecto";
  private static final String PATH_SOLICITUD_PROYECTO_EQUIPO = "/solicitudproyectoequipo";
  private static final String PATH_EXISTS_SOLICITANTE = "/existssolicitante";
  private static final String PATH_TOTALES = "/totales";
  private static final String PATH_TOTALES_CONCEPTO_GASTO = "/totalesconceptogasto";
  private static final String PATH_CONVOCATORIA_SGI = "/convocatoria-sgi";
  private static final String PATH_CREAR_PROYECTO = "/crearproyecto";
  private static final String PATH_MODIFICABLE = "/modificable";
  private static final String PATH_CAMBIAR_ESTADO = "/cambiar-estado";
  private static final String PATH_INVESTIGADOR = "/investigador";
  private static final String PATH_SOLICITUD_PROYECTO_CLASIFICACIONES = "/solicitud-proyecto-clasificaciones";
  private static final String PATH_SOLICITUD_PROYECTO_AREA_CONOCIMIENTO = "/solicitud-proyecto-areas-conocimiento";
  private static final String PATH_SOLICITUD_PROYECTO_RESPONSABLES_ECONOMICOS = "/solicitudproyectoresponsableseconomicos";
  private static final String PATH_SOLICITUD_PROYECTO_TIPO_GLOBAL = "/solicitudproyecto-global";
  private static final String PATH_SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA = "/solicitudproyectoentidadfinanciadora";
  private static final String PATH_TIPO_PRESUPUESTO_MIXTO = "/tipopresupuestomixto";
  private static final String PATH_SOLICITUD_PROYECTO_ENTIDAD = "/solicitudproyectoentidad";
  private static final String PATH_TIPO_PRESUPUESTO_POR_ENTIDAD = "/tipopresupuestoporentidad";
  private static final String PATH_PROYECTOS_IDS = "/proyectosids";
  private static final String PATH_PALABRAS_CLAVE = "/palabrasclave";
  private static final String PATH_ENTIDAD_AJENA = "/entidadajena";
  private static final String PATH_CONVOCATORIA = "/convocatoria";
  private static final String PATH_CONVOCATORIA_ENTIDAD_CONVOCANTES = "/convocatoriaentidadconvocantes";
  private static final String PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_IP = "/categoriasprofesionalesrequisitosip";
  private static final String PATH_NIVELES_REQUISITOS_IP = "/nivelesrequisitosip";
  private static final String PATH_NIVELES_REQUISITOS_EQUIPO = "/nivelesrequisitosequipo";
  private static final String PATH_MODIFICABLE_ESTADO_AND_DOCUMENTOS_BY_INVESTIGADOR = "/modificableestadoanddocumentosbyinvestigador";

  private static final String[] DEFAULT_ROLES = { "AUTH", "CSP-SOL-C", "CSP-SOL-E", "CSP-SOL-V", "CSP-SOL-B",
      "CSP-SOL-R", "CSP-SOL-ETI-V" };

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("usr-002", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsSolicitud() throws Exception {
    Solicitud solicitud = generarMockSolicitud(null);

    final ResponseEntity<Solicitud> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitud, DEFAULT_ROLES), Solicitud.class);

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
  void update_ReturnsSolicitud() throws Exception {
    Long idSolicitud = 1L;
    Solicitud solicitud = generarMockSolicitud(1L);
    solicitud.setObservaciones("observaciones actualizadas");

    final ResponseEntity<Solicitud> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, solicitud, DEFAULT_ROLES), Solicitud.class, idSolicitud);

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
  void desactivar_ReturnSolicitud() throws Exception {
    Long idSolicitud = 1L;

    final ResponseEntity<Solicitud> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null, DEFAULT_ROLES), Solicitud.class, idSolicitud);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Solicitud solicitud = response.getBody();
    Assertions.assertThat(solicitud.getId()).as("getId()").isEqualTo(idSolicitud);
    Assertions.assertThat(solicitud.getActivo()).as("getActivo()").isFalse();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void reactivar_ReturnSolicitud() throws Exception {
    Long idSolicitud = 1L;

    final ResponseEntity<Solicitud> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null, "CSP-SOL-R"),
        Solicitud.class, idSolicitud);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Solicitud solicitud = response.getBody();
    Assertions.assertThat(solicitud.getId()).as("getId()").isEqualTo(idSolicitud);
    Assertions.assertThat(solicitud.getActivo()).as("getActivo()").isTrue();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsSolicitud() throws Exception {
    Long idSolicitud = 1L;

    final ResponseEntity<Solicitud> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, DEFAULT_ROLES), Solicitud.class, idSolicitud);

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
    Assertions.assertThat(solicitud.getConvocatoriaExterna()).as("getConvocatoriaExterna()").isNull();
    Assertions.assertThat(solicitud.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("2");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsSolicitudSubList() throws Exception {

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
        buildRequest(headers, null, DEFAULT_ROLES), new ParameterizedTypeReference<List<Solicitud>>() {
        });

    // given: Solicitud data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Solicitud> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
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
  void findAllTodos_WithPagingSortingAndFiltering_ReturnsSolicitudSubList() throws Exception {

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
        buildRequest(headers, null, DEFAULT_ROLES), new ParameterizedTypeReference<List<Solicitud>>() {
        });

    // given: Solicitud data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Solicitud> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
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
  void findAllSolicitudModalidad_WithPagingSortingAndFiltering_ReturnsSolicitudModalidadSubList()
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
        buildRequest(headers, null, DEFAULT_ROLES), new ParameterizedTypeReference<List<SolicitudModalidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudModalidad> solicitudModalidades = response.getBody();
    Assertions.assertThat(solicitudModalidades).hasSize(3);
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
  void findAllEstadoSolicitud_WithPagingSortingAndFiltering_ReturnsEstadoSolicitudSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ESTADOS_SOLICITUD)
        .queryParam("s", sort).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<EstadoSolicitud>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, DEFAULT_ROLES), new ParameterizedTypeReference<List<EstadoSolicitud>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoSolicitud> estadosSolicitud = response.getBody();
    Assertions.assertThat(estadosSolicitud).hasSize(3);
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
  void findAllSolicitudDocumento_WithPagingSortingAndFiltering_ReturnsSolicitudDocumentoSubList()
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
        buildRequest(headers, null, DEFAULT_ROLES), new ParameterizedTypeReference<List<SolicitudDocumento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudDocumento> solicitudDocumentos = response.getBody();
    Assertions.assertThat(solicitudDocumentos).hasSize(3);
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
  void findAllSolicitudHito_WithPagingSortingAndFiltering_ReturnsSolicitudHitoSubList() throws Exception {
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
        buildRequest(headers, null, DEFAULT_ROLES), new ParameterizedTypeReference<List<SolicitudHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudHito> solicitudHitos = response.getBody();
    Assertions.assertThat(solicitudHitos).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findSolicitudProyecto_ReturnsSolicitudProyectoSubList() throws Exception {
    Long idSolicitud = 1L;

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyecto", HttpMethod.GET,
        buildRequest(null, null, "CSP-SOL-E"),
        SolicitudProyecto.class, idSolicitud);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existSolictudProyectoDatos_Returns204() throws Exception {

    // given: existing Solicitud datos proyecto for solicitud
    Long id = 1L;

    // when: check exist solicitud datos proyecto
    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyecto", HttpMethod.HEAD,
        buildRequest(null, null, "CSP-SOL-V"),
        SolicitudProyecto.class, id);

    // then: Response is 200 OK
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existSolictudProyectoDatos_Returns200() throws Exception {

    // given: existing Solicitud datos proyecto for solicitud
    Long id = 1L;

    // when: check exist solicitud datos proyecto
    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyecto", HttpMethod.HEAD,
        buildRequest(null, null, "CSP-SOL-V"),
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
  void findAllSolicitudProyectoSocio_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoSocioSubList()
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
        buildRequest(headers, null, DEFAULT_ROLES), new ParameterizedTypeReference<List<SolicitudProyectoSocio>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoSocio> solicitudProyectoSocio = response.getBody();
    Assertions.assertThat(solicitudProyectoSocio).hasSize(1);
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
  void findAllSolicitudProyectoEntidadFinanciadoraAjena_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoEntidadFinanciadoraAjenaSubList()
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
        HttpMethod.GET, buildRequest(headers, null, DEFAULT_ROLES),
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
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllSolicitudProyectoPresupuesto_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoPresupuestoSubList()
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
        buildRequest(headers, null, DEFAULT_ROLES),
        new ParameterizedTypeReference<List<SolicitudProyectoPresupuesto>>() {
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
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existSolicitudProyectoPresupuestoEntidadConvocatoria_Returns204() throws Exception {
    // given: existing Solicitud id and entidadRef
    Long id = 1L;
    String entidadRef = "00001";

    // when: check exist solicitud datos proyecto presupuesto entidad convocatoria
    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_PRESUPUESTO + PATH_ENTIDAD_CONVOCATORIA
            + PATH_PARAMETER_ENTIDAD_REF,
        HttpMethod.HEAD, buildRequest(null, null, "CSP-PRO-E"),
        Object.class, id, entidadRef);

    // then: Response is 204 No Content
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findSolictudProyectoDatos_ReturnsSolicitudProyecto() throws Exception {
    Long solicitudId = 1L;

    // when: check exist solicitud datos proyecto presupuesto entidad convocatoria
    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO, HttpMethod.GET,
        buildRequest(null, null, "CSP-SOL-E"),
        SolicitudProyecto.class, solicitudId);

    // then: Response is 200
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyecto solicitudProyecto = response.getBody();

    Assertions.assertThat(solicitudProyecto).isNotNull();
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/rol_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_equipo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllSolicitudProyectoEquipo_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoEquipoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_EQUIPO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoEquipo>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, DEFAULT_ROLES), new ParameterizedTypeReference<List<SolicitudProyectoEquipo>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoEquipo> solicitudProyectoEquipo = response.getBody();
    Assertions.assertThat(solicitudProyectoEquipo.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/rol_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_equipo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existSolicitanteInSolicitudProyectoEquipo_ReturnsStatusCode200() throws Exception {
    // given: existing Solicitud
    Long id = 1L;

    // when: check exists solicitud proyecto equipo
    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_EXISTS_SOLICITANTE, HttpMethod.HEAD,
        buildRequest(null, null, "CSP-SOL-V"),
        Object.class, id);

    // then: Response is 200
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existSolictudProyectoDatos_ReturnsStatusCode200() throws Exception {
    // given: existing Solicitud Proyecto
    Long id = 1L;

    // when: check exists solicitud proyecto
    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO, HttpMethod.HEAD,
        buildRequest(null, null, "CSP-SOL-V"),
        Object.class, id);

    // then: Response is 200
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

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
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void getSolicitudProyectoPresupuestoTotales_ReturnsSolicitudProyectoPresupuestoTotales() throws Exception {
    // Given existing solicitud id
    Long solicitudId = 1L;

    // when: get solicitud proyecto presupuesto totales
    final ResponseEntity<SolicitudProyectoPresupuestoTotales> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_PRESUPUESTO + PATH_TOTALES, HttpMethod.GET,
        buildRequest(null, null, "CSP-SOL-E"),
        SolicitudProyectoPresupuestoTotales.class, solicitudId);

    // then: Response is 200
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyectoPresupuestoTotales totales = response.getBody();

    Assertions.assertThat(totales).isNotNull();
    Assertions.assertThat(totales.getImporteTotalPresupuestadoAjeno()).as("getImporteTotalPresupuestadoAjeno()")
        .isNotNull();
    Assertions.assertThat(totales.getImporteTotalPresupuestadoNoAjeno()).as("getImporteTotalPresupuestadoNoAjeno()")
        .isNotNull();
    Assertions.assertThat(totales.getImporteTotalSolicitadoAjeno()).as("getImporteTotalSolicitadoAjeno()").isNotNull();
    Assertions.assertThat(totales.getImporteTotalSolicitadoNoAjeno()).as("getImporteTotalSolicitadoNoAjeno()")
        .isNotNull();
  }

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
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllSolicitudProyectoPresupuestoTotalConceptoGastos_ReturnsSolicitudProyectoPresupuestoTotalConceptoGastoList()
      throws Exception {
    // Given existing solicitud id
    Long solicitudId = 1L;

    // when: get solicitud proyecto presupuesto totales
    final ResponseEntity<List<SolicitudProyectoPresupuestoTotalConceptoGasto>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_PRESUPUESTO + PATH_TOTALES_CONCEPTO_GASTO,
        HttpMethod.GET, buildRequest(null, null, "CSP-SOL-E"),
        new ParameterizedTypeReference<List<SolicitudProyectoPresupuestoTotalConceptoGasto>>() {
        }, solicitudId);

    // then: Response is 200
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<SolicitudProyectoPresupuestoTotalConceptoGasto> totales = response.getBody();

    Assertions.assertThat(totales).hasSize(4);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasConvocatoriaSgi_ReturnsStatusCode200() throws Exception {
    // given: existing Solicitud Proyecto
    Long solicitudId = 1L;

    // when: check exists convocatoria sgi
    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONVOCATORIA_SGI, HttpMethod.HEAD,
        buildRequest(null, null, "CSP-SOL-V"),
        Object.class, solicitudId);

    // then: Response is 200
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void isPosibleCrearProyecto_RetursStatusCode204() throws Exception {
    // given: existing Solicitud Proyecto
    Long solicitudId = 1L;

    // when: check can create proyecto
    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CREAR_PROYECTO, HttpMethod.HEAD,
        buildRequest(null, null, "CSP-PRO-C"),
        Object.class, solicitudId);

    // then: Response is 204
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void modificable_ReturnsStatusCode204() throws Exception {
    // given: existing Solicitud Proyecto
    Long solicitudId = 1L;

    // when: check if solicitud is modificable
    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_MODIFICABLE, HttpMethod.HEAD,
        buildRequest(null, null, "CSP-SOL-V"),
        Object.class, solicitudId);

    // then: Response is 204
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

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
    "classpath:scripts/rol_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_equipo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void cambiarEstado_ReturnsSolicitud() throws Exception {
    Long solicitudId = 1L;
    EstadoSolicitud estado = buildMockEstadoSolicitud(Estado.DESISTIDA);

    final ResponseEntity<Solicitud> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CAMBIAR_ESTADO,
        HttpMethod.PATCH, buildRequest(null, estado, "CSP-SOL-INV-ER"), Solicitud.class, solicitudId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Solicitud solicitudActualizado = response.getBody();
    Assertions.assertThat(solicitudActualizado.getId()).as("getId()").isEqualTo(solicitudId);
    Assertions.assertThat(solicitudActualizado.getEstado().getEstado()).as("getEstado().getEstado()")
        .isEqualTo(Estado.DESISTIDA);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllInvestigador_WithPagingSortingAndFiltering_ReturnsSolicitudSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "";

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_INVESTIGADOR)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand().toUri();

    final ResponseEntity<List<Solicitud>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-INV-ER"), new ParameterizedTypeReference<List<Solicitud>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Solicitud> solicitudes = response.getBody();
    Assertions.assertThat(solicitudes.size()).as("size").isEqualTo(4);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("4");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_clasificacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllSolicitudProyectoClasificaciones_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoClasificacionesSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "id<=3";
    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_CLASIFICACIONES)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoClasificacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-E"),
        new ParameterizedTypeReference<List<SolicitudProyectoClasificacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoClasificacion> solicitudes = response.getBody();
    Assertions.assertThat(solicitudes.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_area.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllBySolicitudProyectoId_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoAreaConocimientoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "id<=3";
    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_AREA_CONOCIMIENTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoAreaConocimiento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-E"),
        new ParameterizedTypeReference<List<SolicitudProyectoAreaConocimiento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoAreaConocimiento> solicitudes = response.getBody();
    Assertions.assertThat(solicitudes.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_responsable_economico.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllResponsablesEconomicosBySolicitud_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoResponsableEconomicoOutputSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "id<=3";
    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_RESPONSABLES_ECONOMICOS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoResponsableEconomicoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-E"),
        new ParameterizedTypeReference<List<SolicitudProyectoResponsableEconomicoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoResponsableEconomicoOutput> solicitudes = response.getBody();
    Assertions.assertThat(solicitudes.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
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
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existSolicitudProyectoPresupuesto_ReturnsStatusCode200() throws Exception {
    Long solicitudProyectoId = 1L;

    // when: check exist solicitud proyecto presupuesto
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_PRESUPUESTO, HttpMethod.HEAD,
        buildRequest(null, null, "CSP-PRO-V"),
        Void.class, solicitudProyectoId);

    // then: Response is 200
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasSolicitudProyectoTipoGlobal_ReturnsStatusCode200() throws Exception {
    Long solicitudProyectoId = 3L;

    // when: check exist solicitud proyecto has tipo global
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_TIPO_GLOBAL, HttpMethod.HEAD,
        buildRequest(null, null, "CSP-SOL-E"),
        Void.class, solicitudProyectoId);

    // then: Response is 200
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllSolicitudProyectoEntidadFinanciadora_WithPagingSortingAndFiltering_ReturnsConvocatoriaEntidadFinanciadoraSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "id<=3";
    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<ConvocatoriaEntidadFinanciadora>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-E"),
        new ParameterizedTypeReference<List<ConvocatoriaEntidadFinanciadora>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaEntidadFinanciadora> solicitudes = response.getBody();
    Assertions.assertThat(solicitudes.size()).as("size").isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findSolicitudProyectoEntidadTipoPresupuestoMixto_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoEntidadSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "";
    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_ENTIDAD + PATH_TIPO_PRESUPUESTO_MIXTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoEntidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-E"), new ParameterizedTypeReference<List<SolicitudProyectoEntidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoEntidad> solicitudes = response.getBody();
    Assertions.assertThat(solicitudes.size()).as("size").isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findSolicitudProyectoEntidadTipoPresupuestoMixto_WithPagingSortingAndFiltering_ReturnsStatusCode204()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "";
    Long solicitudId = 3L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_ENTIDAD + PATH_TIPO_PRESUPUESTO_MIXTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoEntidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-E"), new ParameterizedTypeReference<List<SolicitudProyectoEntidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findSolicitudProyectoEntidadTipoPresupuestoPorEntidad_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoEntidadSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "";
    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_ENTIDAD
            + PATH_TIPO_PRESUPUESTO_POR_ENTIDAD)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoEntidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-E"), new ParameterizedTypeReference<List<SolicitudProyectoEntidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoEntidad> solicitudes = response.getBody();
    Assertions.assertThat(solicitudes.size()).as("size").isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findSolicitudProyectoEntidadTipoPresupuestoPorEntidad_WithPagingSortingAndFiltering_ReturnsStatusCode204()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "";
    Long solicitudId = 3L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_ENTIDAD
            + PATH_TIPO_PRESUPUESTO_POR_ENTIDAD)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoEntidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-E"), new ParameterizedTypeReference<List<SolicitudProyectoEntidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/proyecto_with_solicitud_id.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findProyectosIdsBySolicitudId_ReturnsLongList() throws Exception {
    Long solicitudId = 1L;

    // when: check exist solicitud proyecto has tipo global
    final ResponseEntity<List<Long>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTOS_IDS, HttpMethod.GET,
        buildRequest(null, null, "CSP-SOL-E"),
        new ParameterizedTypeReference<List<Long>>() {
        }, solicitudId);

    // then: Response is 200
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    List<Long> ids = response.getBody();

    Assertions.assertThat(ids).hasSize(5);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/proyecto_with_solicitud_id.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findProyectosIdsBySolicitudId_ReturnsStatusCode204() throws Exception {
    Long solicitudId = 2L;

    // when: check exist solicitud proyecto has tipo global
    final ResponseEntity<List<Long>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTOS_IDS, HttpMethod.GET,
        buildRequest(null, null, "CSP-SOL-E"),
        new ParameterizedTypeReference<List<Long>>() {
        }, solicitudId);

    // then: Response is 200
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_palabra_clave.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findPalabrasClave_WithPagingSortingAndFiltering_ReturnsSolicitudPalabraClaveOutputSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "";

    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PALABRAS_CLAVE)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudPalabraClaveOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-E"), new ParameterizedTypeReference<List<SolicitudPalabraClaveOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<SolicitudPalabraClaveOutput> palabrasClave = response.getBody();
    Assertions.assertThat(palabrasClave).hasSize(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_palabra_clave.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updatePalabrasClave_ReturnsSolicitudPalabraClaveOutputList() throws Exception {
    Long solicitudId = 1L;
    List<SolicitudPalabraClaveInput> toUpdate = Arrays.asList(
        buildMockSolicitudPalabraClaveInput(solicitudId, "palabra-ref-uptd-1"),
        buildMockSolicitudPalabraClaveInput(solicitudId, "palabra-ref-uptd-2"),
        buildMockSolicitudPalabraClaveInput(solicitudId, "palabra-ref-uptd-3"));

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PALABRAS_CLAVE)
        .buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudPalabraClaveOutput>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(null, toUpdate, "CSP-SOL-E"), new ParameterizedTypeReference<List<SolicitudPalabraClaveOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<SolicitudPalabraClaveOutput> updated = response.getBody();
    Assertions.assertThat(updated).hasSize(3);

    Assertions.assertThat(updated.get(0)).isNotNull();
    Assertions.assertThat(updated.get(1)).isNotNull();
    Assertions.assertThat(updated.get(2)).isNotNull();

    Assertions.assertThat(updated.get(0).getPalabraClaveRef()).isEqualTo("palabra-ref-uptd-1");
    Assertions.assertThat(updated.get(1).getPalabraClaveRef()).isEqualTo("palabra-ref-uptd-2");
    Assertions.assertThat(updated.get(2).getPalabraClaveRef()).isEqualTo("palabra-ref-uptd-3");
  }

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
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existSolicitudProyectoPresupuestoEntidadAjena_ReturnsHttpStatusCode204() throws Exception {
    Long solicitudId = 1L;
    String entidadRef = "00001";
    String roles = "CSP-SOL-E";

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_PRESUPUESTO
            + PATH_ENTIDAD_AJENA + PATH_PARAMETER_ENTIDAD_REF)
        .buildAndExpand(solicitudId, entidadRef).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD, buildRequest(null, null, roles),
        Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private SolicitudPalabraClaveInput buildMockSolicitudPalabraClaveInput(Long solicitudId, String palabraRef) {
    return SolicitudPalabraClaveInput.builder()
        .solicitudId(solicitudId)
        .palabraClaveRef(palabraRef)
        .build();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findConvocatoriaBySolicitudId_ReturnsConvocatoria() throws Exception {
    String roles = "CSP-SOL-INV-ER";
    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONVOCATORIA)
        .buildAndExpand(solicitudId).toUri();

    final ResponseEntity<Convocatoria> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), Convocatoria.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getId()).isEqualTo(1L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/programa.sql",
    "classpath:scripts/convocatoria_entidad_convocante.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaEntidadConvocantes_WithPagingAndSorting_ReturnsConvocatoriaEntidadConvocanteSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    Long solicitudId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONVOCATORIA_ENTIDAD_CONVOCANTES)
        .queryParam("s", sort).buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<ConvocatoriaEntidadConvocante>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-SOL-INV-ER"),
        new ParameterizedTypeReference<List<ConvocatoriaEntidadConvocante>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/requisito_ip.sql",
    "classpath:scripts/requisitoip_categoriaprofesional.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findRequisitosIpCategoriasProfesionales_ReturnsRequisitoIPCategoriaProfesionalOutputList() throws Exception {
    Long solicitudId = 1L;
    String roles = "CSP-SOL-INV-ER";

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_IP)
        .buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<RequisitoIPCategoriaProfesionalOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET, buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<RequisitoIPCategoriaProfesionalOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody()).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/requisito_ip.sql",
    "classpath:scripts/requisitoip_nivelacademico.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findRequisitoIPNivelesAcademicos_ReturnsRequisitoIPNivelAcademicoOutputList() throws Exception {
    Long solicitudId = 1L;
    String roles = "CSP-SOL-INV-ER";

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_NIVELES_REQUISITOS_IP)
        .buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<RequisitoIPNivelAcademicoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET, buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<RequisitoIPNivelAcademicoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody()).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/requisito_equipo.sql",
    "classpath:scripts/requisitoequipo_nivelacademico.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findRequisitoEquipoNivelesAcademicos_ReturnsRequisitoEquipoNivelAcademicoOutputList() throws Exception {
    Long solicitudId = 1L;
    String roles = "CSP-SOL-INV-ER";

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_NIVELES_REQUISITOS_EQUIPO)
        .buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<RequisitoEquipoNivelAcademicoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET, buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<RequisitoEquipoNivelAcademicoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody()).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void modificableEstadoAndDocumentosByInvestigador_ReturnsHttpStatusCode200() throws Exception {
    Long solicitudId = 1L;
    String roles = "CSP-SOL-INV-ER";

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_MODIFICABLE_ESTADO_AND_DOCUMENTOS_BY_INVESTIGADOR)
        .buildAndExpand(solicitudId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri,
        HttpMethod.HEAD, buildRequest(null, null, roles),
        Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void getCodigoRegistroInterno_ReturnsCodigoRegistroInterno() throws Exception {
    Long idSolicitud = 1L;

    final ResponseEntity<String> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/codigo-registro-interno",
        HttpMethod.GET, buildRequest(null, null, DEFAULT_ROLES), String.class, idSolicitud);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    String codigoRegistroInterno = response.getBody();
    Assertions.assertThat(codigoRegistroInterno).isEqualTo(JSONObject.quote("SGI_SLC1202011061027"));
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

  private EstadoSolicitud buildMockEstadoSolicitud(Estado estado) {
    return EstadoSolicitud.builder()
        .estado(estado)
        .fechaEstado(Instant.now())
        .build();
  }

}
