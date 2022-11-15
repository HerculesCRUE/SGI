package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ConvocatoriaController;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseOutput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaPalabraClaveInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaPalabraClaveOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
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

/**
 * Test de integracion de Convocatoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConvocatoriaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String PATH_PARAMETER_REGISTRAR = "/registrar";
  private static final String PATH_PARAMETER_TODOS = "/todos";
  private static final String PATH_PARAMETER_MODIFICABLE = "/modificable";
  private static final String PATH_PARAMETER_REGISTRABLE = "/registrable";
  private static final String CONTROLLER_BASE_PATH = "/convocatorias";
  private static final String PATH_AREA_TEMATICA = "/convocatoriaareatematicas";
  private static final String PATH_ENTIDAD_DOCUMENTO = "/convocatoriadocumentos";
  private static final String PATH_ENTIDAD_ENLACES = "/convocatoriaenlaces";
  private static final String PATH_ENTIDAD_CONVOCANTE = "/convocatoriaentidadconvocantes";
  private static final String PATH_ENTIDAD_FINANCIADORA = "/convocatoriaentidadfinanciadoras";
  private static final String PATH_ENTIDAD_GESTORA = "/convocatoriaentidadgestoras";
  private static final String PATH_FASES = "/convocatoriafases";
  private static final String PATH_HITOS = "/convocatoriahitos";
  private static final String PATH_PERIODO_JUSTIFICACION = "/convocatoriaperiodojustificaciones";
  private static final String PATH_PERIODO_SEGUIMIENTO_CIENTIFICO = "/convocatoriaperiodoseguimientocientificos";
  private static final String PATH_ENTIDAD_CONCEPTO_GASTOS_PERMITIDOS = "/convocatoriagastos/permitidos";
  private static final String PATH_ENTIDAD_CONCEPTO_GASTOS_NO_PERMITIDOS = "/convocatoriagastos/nopermitidos";
  private static final String PATH_PARAMETER_RESTRINGIDOS = "/restringidos";
  private static final String PATH_PALABRAS_CLAVE = ConvocatoriaController.PATH_PALABRAS_CLAVE;
  private static final String PATH_INVESTIGADOR = "/investigador";
  private static final String PATH_CONVOCATORIA_HITOS = "/convocatoriahitos";
  private static final String PATH_CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS = "/convocatoria-partidas-presupuestarias";
  private static final String PATH_CONVOCATORIA_FASES = "/convocatoriafases";
  private static final String PATH_CONVOCATORIA_DOCUMENTOS = "/convocatoriadocumentos";
  private static final String PATH_CONVOCATORIA_ENLACES = "/convocatoriaenlaces";
  private static final String PATH_CONVOCATORIA_GASTO_CODIGO_EC = "/convocatoriagastocodigoec";
  private static final String PATH_PERMITIDOS = "/permitidos";
  private static final String PATH_NO_PERMITIDOS = "/nopermitidos";
  private static final String PATH_TRAMITABLE = "/tramitable";
  private static final String PATH_SOLICITUDES_REFERENCED = "/solicitudesreferenced";
  private static final String PATH_PROYECTOS_REFERENCED = "/proyectosreferenced";
  private static final String PATH_CLONE = "/clone";
  private static final String PATH_FORMULARIO_SOLICITUD = "/formulariosolicitud";
  private static final String PATH_ENLACES = ConvocatoriaController.PATH_ENLACES;

  private HttpEntity<Convocatoria> buildRequest(HttpHeaders headers, Convocatoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-C", "CSP-CON-B",
        "CSP-CON-R", "CSP-CON-E", "CSP-CON-INV-V", "CSP-CON-V", "AUTH")));

    HttpEntity<Convocatoria> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<Object> buildGenericRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsConvocatoria() throws Exception {

    // given: new Convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setId(null);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    // when: create Convocatoria
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, convocatoria), Convocatoria.class);

    // then: new Convocatoria is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Convocatoria responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(convocatoria.getUnidadGestionRef());
    Assertions.assertThat(responseData.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(convocatoria.getModeloEjecucion().getId());
    Assertions.assertThat(responseData.getCodigo()).as("getCodigo()").isEqualTo(convocatoria.getCodigo());
    Assertions.assertThat(responseData.getFechaPublicacion()).as("getFechaPublicacion()")
        .isEqualTo(convocatoria.getFechaPublicacion());
    Assertions.assertThat(responseData.getFechaProvisional()).as("getFechaProvisional()")
        .isEqualTo(convocatoria.getFechaProvisional());
    Assertions.assertThat(responseData.getFechaConcesion()).as("getFechaConcesion()")
        .isEqualTo(convocatoria.getFechaConcesion());
    Assertions.assertThat(responseData.getTitulo()).as("getTitulo()").isEqualTo(convocatoria.getTitulo());
    Assertions.assertThat(responseData.getObjeto()).as("getObjeto()").isEqualTo(convocatoria.getObjeto());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoria.getObservaciones());
    Assertions.assertThat(responseData.getFinalidad().getId()).as("getFinalidad().getId()")
        .isEqualTo(convocatoria.getFinalidad().getId());
    Assertions.assertThat(responseData.getRegimenConcurrencia().getId()).as("getRegimenConcurrencia().getId()")
        .isEqualTo(convocatoria.getRegimenConcurrencia().getId());
    Assertions.assertThat(responseData.getEstado()).as("getEstado()").isEqualTo(Convocatoria.Estado.BORRADOR);
    Assertions.assertThat(responseData.getDuracion()).as("getDuracion()").isEqualTo(convocatoria.getDuracion());
    Assertions.assertThat(responseData.getAmbitoGeografico().getId()).as("getAmbitoGeografico().getId()")
        .isEqualTo(convocatoria.getAmbitoGeografico().getId());
    Assertions.assertThat(responseData.getClasificacionCVN()).as("getClasificacionCVN()")
        .isEqualTo(convocatoria.getClasificacionCVN());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsConvocatoria() throws Exception {

    // given: existing Convocatoria to be updated
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setCodigo("codigo-modificado");
    convocatoria.setTitulo("titulo-modificado");
    convocatoria.setObservaciones("observaciones-modificadas");

    // when: update Convocatoria
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, convocatoria), Convocatoria.class, convocatoria.getId());

    // then: Convocatoria is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Convocatoria responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(convocatoria.getId());
    Assertions.assertThat(responseData.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(convocatoria.getUnidadGestionRef());
    Assertions.assertThat(responseData.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(convocatoria.getModeloEjecucion().getId());
    Assertions.assertThat(responseData.getCodigo()).as("getCodigo()").isEqualTo(convocatoria.getCodigo());
    Assertions.assertThat(responseData.getFechaPublicacion()).as("getFechaPublicacion()")
        .isEqualTo(convocatoria.getFechaPublicacion());
    Assertions.assertThat(responseData.getFechaProvisional()).as("getFechaProvisional()")
        .isEqualTo(convocatoria.getFechaProvisional());
    Assertions.assertThat(responseData.getFechaConcesion()).as("getFechaConcesion()")
        .isEqualTo(convocatoria.getFechaConcesion());
    Assertions.assertThat(responseData.getTitulo()).as("getTitulo()").isEqualTo(convocatoria.getTitulo());
    Assertions.assertThat(responseData.getObjeto()).as("getObjeto()").isEqualTo(convocatoria.getObjeto());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoria.getObservaciones());
    Assertions.assertThat(responseData.getFinalidad().getId()).as("getFinalidad().getId()")
        .isEqualTo(convocatoria.getFinalidad().getId());
    Assertions.assertThat(responseData.getRegimenConcurrencia().getId()).as("getRegimenConcurrencia().getId()")
        .isEqualTo(convocatoria.getRegimenConcurrencia().getId());
    Assertions.assertThat(responseData.getEstado()).as("getEstado()").isEqualTo(convocatoria.getEstado());
    Assertions.assertThat(responseData.getDuracion()).as("getDuracion()").isEqualTo(convocatoria.getDuracion());
    Assertions.assertThat(responseData.getAmbitoGeografico().getId()).as("getAmbitoGeografico().getId()")
        .isEqualTo(convocatoria.getAmbitoGeografico().getId());
    Assertions.assertThat(responseData.getClasificacionCVN()).as("getClasificacionCVN()")
        .isEqualTo(convocatoria.getClasificacionCVN());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(convocatoria.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void registrar_ReturnsConvocatoria() throws Exception {
    // given: existing Convocatoria id to registrar
    Long convocatoriaId = 1L;

    // when: registrar Convocatoria
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REGISTRAR, HttpMethod.PATCH, buildRequest(null, null),
        Convocatoria.class, convocatoriaId);

    // then: Convocatoria is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Convocatoria convocatoriaRegistrada = response.getBody();
    Assertions.assertThat(convocatoriaRegistrada.getId()).as("getId()").isEqualTo(convocatoriaId);
    Assertions.assertThat(convocatoriaRegistrada.getEstado()).as("getEstado()")
        .isEqualTo(Convocatoria.Estado.REGISTRADA);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void enable_ReturnsConvocatoria() throws Exception {
    // given: existing Convocatoria to be enabled
    Long id = 1L;

    // when: disable Convocatoria
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        Convocatoria.class, id);

    // then: Convocatoria is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Convocatoria convocatoria = response.getBody();
    Assertions.assertThat(convocatoria.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoria.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(convocatoria.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void disable_ReturnsConvocatoria() throws Exception {
    // given: existing Convocatoria to be disabled
    Long id = 1L;

    // when: disable Convocatoria
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), Convocatoria.class, id);

    // then: Convocatoria is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Convocatoria convocatoria = response.getBody();
    Assertions.assertThat(convocatoria.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoria.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(convocatoria.getActivo()).as("getActivo()").isEqualTo(Boolean.FALSE);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void modificable_WhenModificableReturnsTrue_Returns200() throws Exception {

    // given: existing Convocatoria When modificable returns true
    Long id = 1L;

    // when: check modificable
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_MODIFICABLE, HttpMethod.HEAD,
        buildRequest(null, null), Convocatoria.class, id);

    // then: Response is 200 OK
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void modificable_ConvocatoriaRegistradaWithSolicitudesOrProyectosIsTrue_Returns204() throws Exception {

    // given: existing Convocatoria registrada with Solicitudes or Proyectos
    Long id = 1L;

    // when: check vinculaciones
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_MODIFICABLE, HttpMethod.HEAD,
        buildRequest(null, null), Convocatoria.class, id);

    // then: Response is 204 No Content
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void registrable_WhenRegistrableReturnsTrue_Returns200() throws Exception {

    // given: existing Convocatoria When registrable returns true
    Long id = 1L;

    // when: check registrable
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REGISTRABLE, HttpMethod.HEAD,
        buildRequest(null, null), Convocatoria.class, id);

    // then: Response is 200 OK
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void registrable_WhenRegistrableReturnsFalse_Returns204() throws Exception {

    // given: existing Convocatoria When registrable returns false
    Long id = 1L;

    // when: check registrable
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REGISTRABLE, HttpMethod.HEAD,
        buildRequest(null, null), Convocatoria.class, id);

    // then: Response is 204 No Content
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsById_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Convocatoria.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsById_Returns204() throws Exception {
    // given: no existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Convocatoria> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Convocatoria.class, id);
    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsConvocatoria() throws Exception {
    Long id = 1L;

    final ResponseEntity<Convocatoria> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Convocatoria.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Convocatoria responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);

    Assertions.assertThat(responseData.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("unidad-001");
    Assertions.assertThat(responseData.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getCodigo()).as("getCodigo()").isEqualTo("codigo-001");
    Assertions.assertThat(responseData.getFechaPublicacion()).as("getFechaPublicacion()")
        .isEqualTo(Instant.parse("2021-08-01T00:00:00Z"));
    Assertions.assertThat(responseData.getFechaProvisional()).as("getFechaProvisional()")
        .isEqualTo(Instant.parse("2021-09-01T00:00:00Z"));
    Assertions.assertThat(responseData.getFechaConcesion()).as("getFechaConcesion()")
        .isEqualTo(Instant.parse("2021-10-01T00:00:00Z"));
    Assertions.assertThat(responseData.getTitulo()).as("getTitulo()").isEqualTo("titulo-001");
    Assertions.assertThat(responseData.getObjeto()).as("getObjeto()").isEqualTo("objeto-001");
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones-001");
    Assertions.assertThat(responseData.getFinalidad().getId()).as("getFinalidad().getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getRegimenConcurrencia().getId()).as("getRegimenConcurrencia().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(responseData.getEstado()).as("getEstado()").isEqualTo(Convocatoria.Estado.REGISTRADA);
    Assertions.assertThat(responseData.getDuracion()).as("getDuracion()").isEqualTo(12);
    Assertions.assertThat(responseData.getAmbitoGeografico().getId()).as("getAmbitoGeografico().getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getClasificacionCVN()).as("getClasificacionCVN()")
        .isEqualTo(ClasificacionCVN.AYUDAS);
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllRestringidos_WithPagingSortingAndFiltering_ReturnsConvocatoriaSubList() throws Exception {

    // given: data for Convocatoria

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V_1")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "codigo,desc";
    String filter = "titulo=ke=00";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_RESTRINGIDOS)
        .queryParam("s", sort).queryParam("q", filter).build(false).toUri();
    final ResponseEntity<List<Convocatoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Convocatoria>>() {
        });

    // given: Convocatoria data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Convocatoria> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getCodigo()).as("get(0).getCodigo())")
        .isEqualTo("codigo-" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(1).getCodigo()).as("get(1).getCodigo())")
        .isEqualTo("codigo-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getCodigo()).as("get(2).getCodigo())")
        .isEqualTo("codigo-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodosRestringidos_WithPagingSortingAndFiltering_ReturnsConvocatoriaSubList() throws Exception {

    // given: data for Convocatoria

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V_2")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");

    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    HttpEntity<Convocatoria> request = new HttpEntity<>(null, headers);
    String sort = "codigo,desc";
    String filter = "titulo=ke=00";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_TODOS + PATH_PARAMETER_RESTRINGIDOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();
    final ResponseEntity<List<Convocatoria>> response = restTemplate.exchange(uri, HttpMethod.GET, request,
        new ParameterizedTypeReference<List<Convocatoria>>() {
        });

    // given: Convocatoria data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Convocatoria> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getCodigo()).as("get(0).getCodigo())")
        .isEqualTo("codigo-" + String.format("%03d", 6));
    Assertions.assertThat(responseData.get(1).getCodigo()).as("get(1).getCodigo())")
        .isEqualTo("codigo-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getCodigo()).as("get(2).getCodigo())")
        .isEqualTo("codigo-" + String.format("%03d", 1));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodosRestringidos_EmptyList_Returns204() throws Exception {

    // given: no data for Convocatoria
    // when: find Convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "codigo,desc";
    String filter = "titulo=ke=00";

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_TODOS + PATH_PARAMETER_RESTRINGIDOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Convocatoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Convocatoria>>() {
        });

    // then: 204 no content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD CONVOCANTE
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaEntidadConvocantes_WithPagingSortingAndFiltering_ReturnsConvocatoriaEntidadConvocanteSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENTGES-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "entidadRef=ke=-0";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_CONVOCANTE)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaEntidadConvocante>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaEntidadConvocante>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaEntidadConvocante> convocatoriaEntidadConvocantes = response.getBody();
    Assertions.assertThat(convocatoriaEntidadConvocantes).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriaEntidadConvocantes.get(0).getEntidadRef()).as("get(0).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriaEntidadConvocantes.get(1).getEntidadRef()).as("get(1).getEntidadRef())")
        .isEqualTo("entidad-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriaEntidadConvocantes.get(2).getEntidadRef()).as("get(2).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 1));
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/programa.sql",
    "classpath:scripts/convocatoria_entidad_convocante.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateEntidadesConvocantes_ReturnsConvocatoriaEntidadConvocanteList() throws Exception {
    Long convocatoriaId = 1L;

    List<ConvocatoriaEntidadConvocante> toUpdateList = Arrays.asList(
        generarMockConvocatoriaEntidadConvocante(1L, 4L),
        generarMockConvocatoriaEntidadConvocante(null, 6L));

    final ResponseEntity<List<ConvocatoriaEntidadConvocante>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_CONVOCANTE,
        HttpMethod.PATCH,
        buildGenericRequest(null,
            toUpdateList, "CSP-CON-E"),
        new ParameterizedTypeReference<List<ConvocatoriaEntidadConvocante>>() {
        }, convocatoriaId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<ConvocatoriaEntidadConvocante> convocatoriaEntidadesConvocantesUpdated = response.getBody();

    Assertions.assertThat(convocatoriaEntidadesConvocantesUpdated).hasSize(toUpdateList.size());

    Assertions.assertThat(convocatoriaEntidadesConvocantesUpdated.get(0).getPrograma().getId())
        .as("get(0).getPrograma().getId()")
        .isEqualTo(toUpdateList.get(0).getPrograma().getId());
    Assertions.assertThat(convocatoriaEntidadesConvocantesUpdated.get(1).getPrograma().getId())
        .as("get(1).getPrograma().getId()")
        .isEqualTo(toUpdateList.get(1).getPrograma().getId());
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD FINANCIADORA
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaEntidadFinanciadora_WithPagingSortingAndFiltering_ReturnsConvocatoriaEntidadFinanciadoraSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENTGES-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "entidadRef=ke=-0";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_FINANCIADORA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaEntidadFinanciadora>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaEntidadFinanciadora>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaEntidadFinanciadora> convocatoriaEntidadFinanciadoras = response.getBody();
    Assertions.assertThat(convocatoriaEntidadFinanciadoras).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriaEntidadFinanciadoras.get(0).getEntidadRef()).as("get(0).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriaEntidadFinanciadoras.get(1).getEntidadRef()).as("get(1).getEntidadRef())")
        .isEqualTo("entidad-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriaEntidadFinanciadoras.get(2).getEntidadRef()).as("get(2).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 1));
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD GESTORA
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaEntidadGestora_WithPagingSortingAndFiltering_ReturnsConvocatoriaEntidadGestoraSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENTGES-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "entidadRef=ke=-0";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_GESTORA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaEntidadGestora>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaEntidadGestora>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaEntidadGestora> convocatoriasEntidadesGestoras = response.getBody();
    Assertions.assertThat(convocatoriasEntidadesGestoras).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriasEntidadesGestoras.get(0).getEntidadRef()).as("get(0).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriasEntidadesGestoras.get(1).getEntidadRef()).as("get(1).getEntidadRef())")
        .isEqualTo("entidad-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriasEntidadesGestoras.get(2).getEntidadRef()).as("get(2).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 1));
  }

  /**
   * 
   * CONVOCATORIA AREA TEMATICA
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaAreaTematica_WithPagingSortingAndFiltering_ReturnsConvocatoriaAreaTematicaSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CATEM-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "convocatoria.id==1;observaciones=ke=-0";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_AREA_TEMATICA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaAreaTematica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaAreaTematica>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaAreaTematica> convocatoriasAreasTematicas = response.getBody();
    Assertions.assertThat(convocatoriasAreasTematicas).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriasAreasTematicas.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriasAreasTematicas.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriasAreasTematicas.get(2).getObservaciones()).as("get(2).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 1));
  }

  /**
   * 
   * CONVOCATORIA DOCUMENTO
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaDocumento_WithPagingSortingAndFiltering_ReturnsConvocatoriaDocumentoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "observaciones=ke=-00";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_DOCUMENTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaDocumento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaDocumento> convocatoriasDocumentos = response.getBody();
    Assertions.assertThat(convocatoriasDocumentos).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriasDocumentos.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriasDocumentos.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriasDocumentos.get(2).getObservaciones()).as("get(2).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 1));
  }

  /**
   * 
   * CONVOCATORIA ENLACE
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaEnlace_WithPagingSortingAndFiltering_ReturnsConvocatoriaEnlaceSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_ENLACES)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaEnlace>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaEnlace>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaEnlace> convocatoriasEnlaces = response.getBody();
    Assertions.assertThat(convocatoriasEnlaces).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriasEnlaces.get(0).getDescripcion()).as("get(0).getDescripcion()")
        .isEqualTo("descripcion-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriasEnlaces.get(1).getDescripcion()).as("get(1).getDescripcion())")
        .isEqualTo("descripcion-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriasEnlaces.get(2).getDescripcion()).as("get(2).getDescripcion()")
        .isEqualTo("descripcion-" + String.format("%03d", 1));
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_enlace.sql",
    "classpath:scripts/modelo_tipo_enlace.sql",
    "classpath:scripts/convocatoria_enlace.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateEnlaces_ReturnsConvocatoriaEnlaceList() throws Exception {
    Long convocatoriaId = 1L;

    List<ConvocatoriaEnlace> toUpdateList = Arrays.asList(
        generarMockConvocatoriaEnlace(1L, "www.hola.es"),
        generarMockConvocatoriaEnlace(2L, "www.adios.es"));

    final ResponseEntity<List<ConvocatoriaEnlace>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ENLACES,
        HttpMethod.PATCH,
        buildGenericRequest(null,
            toUpdateList, "CSP-CON-E"),
        new ParameterizedTypeReference<List<ConvocatoriaEnlace>>() {
        }, convocatoriaId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<ConvocatoriaEnlace> convocatoriaEnlacesUpdated = response.getBody();

    Assertions.assertThat(convocatoriaEnlacesUpdated).hasSize(toUpdateList.size());

    Assertions.assertThat(convocatoriaEnlacesUpdated.get(0).getUrl()).as("get(0).getUrl()")
        .isEqualTo(toUpdateList.get(0).getUrl());
    Assertions.assertThat(convocatoriaEnlacesUpdated.get(1).getUrl()).as("get(1).getUrl()")
        .isEqualTo(toUpdateList.get(1).getUrl());
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEnlace
   * 
   * @param id     id del ConvocatoriaEnlace
   * @param nombre nombre del ConvocatoriaEnlace
   * @return el objeto ConvocatoriaEnlace
   */
  private ConvocatoriaEnlace generarMockConvocatoriaEnlace(Long id, String url) {
    TipoEnlace tipoEnlace = new TipoEnlace();
    tipoEnlace.setId(1L);

    ConvocatoriaEnlace convocatoriaEnlace = new ConvocatoriaEnlace();
    convocatoriaEnlace.setId(id);
    convocatoriaEnlace.setUrl(url);
    convocatoriaEnlace.setDescripcion("descripcion-" + id);
    convocatoriaEnlace.setTipoEnlace(tipoEnlace);

    return convocatoriaEnlace;
  }

  /*
   * CONVOCATORIA FASE
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaFase_WithPagingSortingAndFiltering_ReturnsConvocatoriaFaseSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "observaciones=ke=-00";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_FASES)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaFaseOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaFaseOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaFaseOutput> convocatoriasFases = response.getBody();
    Assertions.assertThat(convocatoriasFases).hasSize(3);
    Assertions.assertThat(convocatoriasFases.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriasFases.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriasFases.get(2).getObservaciones()).as("get(2).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 1));

  }

  /*
   * CONVOCATORIA HITO
   * 
   */
  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaHito_WithPagingSortingAndFiltering_ReturnsConvocatoriaHitoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CHIT-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "comentario=ke=-00";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_HITOS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaHito>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ConvocatoriaHito> convocatoriasHitos = response.getBody();
    Assertions.assertThat(convocatoriasHitos).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(convocatoriasHitos.get(0).getComentario()).as("get(0).getComentario()")
        .isEqualTo("comentario-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriasHitos.get(1).getComentario()).as("get(1).getComentario())")
        .isEqualTo("comentario-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriasHitos.get(2).getComentario()).as("get(2).getComentario()")
        .isEqualTo("comentario-" + String.format("%03d", 1));

  }

  /*
   *
   * CONVOCATORIA PERIODO JUSTIFICACION
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaPeriodoJustificacion_WithPagingSortingAndFiltering_ReturnsConvocatoriaPeriodoJustificacionSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "observaciones=ke=-00";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PERIODO_JUSTIFICACION)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaPeriodoJustificacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaPeriodoJustificacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaPeriodoJustificacion> convocatoriaPeriodoJustificaciones = response.getBody();
    Assertions.assertThat(convocatoriaPeriodoJustificaciones).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriaPeriodoJustificaciones.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriaPeriodoJustificaciones.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriaPeriodoJustificaciones.get(2).getObservaciones()).as("get(2).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 1));
  }

  /*
   *
   * CONVOCATORIA PERIODO SEGUIMIENTO CIENTIFICO
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaPeriodoSeguimientoCientifico_WithPagingSortingAndFiltering_ReturnsConvocatoriaPeriodoSeguimientoCientificoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CPSCI-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "numPeriodo,desc";
    String filter = "observaciones=ke=-00";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PERIODO_SEGUIMIENTO_CIENTIFICO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaPeriodoSeguimientoCientifico>> response = restTemplate.exchange(uri,
        HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<ConvocatoriaPeriodoSeguimientoCientifico>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificoes = response
        .getBody();
    Assertions.assertThat(convocatoriaPeriodoSeguimientoCientificoes).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriaPeriodoSeguimientoCientificoes.get(0).getObservaciones())
        .as("get(0).getObservaciones()").isEqualTo("observaciones-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriaPeriodoSeguimientoCientificoes.get(0).getTipoSeguimiento())
        .as("get(0).getTipoSeguimiento")
        .isIn(TipoSeguimiento.FINAL, TipoSeguimiento.INTERMEDIO, TipoSeguimiento.PERIODICO);
    Assertions.assertThat(convocatoriaPeriodoSeguimientoCientificoes.get(1).getObservaciones())
        .as("get(1).getObservaciones())").isEqualTo("observaciones-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriaPeriodoSeguimientoCientificoes.get(1).getTipoSeguimiento())
        .as("get(1).getTipoSeguimiento()")
        .isIn(TipoSeguimiento.FINAL, TipoSeguimiento.INTERMEDIO, TipoSeguimiento.PERIODICO);
    Assertions.assertThat(convocatoriaPeriodoSeguimientoCientificoes.get(2).getObservaciones())
        .as("get(2).getObservaciones()").isEqualTo("observaciones-" + String.format("%03d", 1));
    Assertions.assertThat(convocatoriaPeriodoSeguimientoCientificoes.get(2).getTipoSeguimiento())
        .as("get(2).getTipoSeguimiento")
        .isIn(TipoSeguimiento.FINAL, TipoSeguimiento.INTERMEDIO, TipoSeguimiento.PERIODICO);
  }

  /**
   * 
   * CONVOCATORIA CONCEPTO GASTO
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaConceptoGastoPermitidos_WithPagingSortingAndFiltering_ReturnsConvocatoriaConceptoGastoPermitidosSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CGAS-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "obs=ke=-00";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_CONCEPTO_GASTOS_PERMITIDOS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaConceptoGasto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaConceptoGasto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaConceptoGasto> convocatoriaConceptoGastos = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGastos).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriaConceptoGastos.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("obs-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriaConceptoGastos.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("obs-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriaConceptoGastos.get(2).getObservaciones()).as("get(2).getObservaciones()")
        .isEqualTo("obs-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaConceptoGastoNoPermitidos_WithPagingSortingAndFiltering_ReturnsConvocatoriaConceptoGastoNoPermitidosSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CGAS-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "obs=ke=-00";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_CONCEPTO_GASTOS_NO_PERMITIDOS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaConceptoGasto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaConceptoGasto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaConceptoGasto> convocatoriaConceptoGastos = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGastos).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriaConceptoGastos.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("obs-" + String.format("%03d", 6));
    Assertions.assertThat(convocatoriaConceptoGastos.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("obs-" + String.format("%03d", 5));
    Assertions.assertThat(convocatoriaConceptoGastos.get(2).getObservaciones()).as("get(2).getObservaciones()")
        .isEqualTo("obs-" + String.format("%03d", 4));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updatePalabrasClave_ReturnsConvocatoriaPalabraClaveOutputList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CGAS-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "";

    Long convocatoriaId = 1L;
    List<ConvocatoriaPalabraClaveInput> toUpdate = Arrays.asList(
        buildMockConvocatoriaPalabraClaveInput(convocatoriaId, "palabra-ref-uptd-1"),
        buildMockConvocatoriaPalabraClaveInput(convocatoriaId, "palabra-ref-uptd-2"),
        buildMockConvocatoriaPalabraClaveInput(convocatoriaId, "palabra-ref-uptd-3"));

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PALABRAS_CLAVE)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaPalabraClaveOutput>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildGenericRequest(null, toUpdate, "CSP-CON-E"),
        new ParameterizedTypeReference<List<ConvocatoriaPalabraClaveOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<ConvocatoriaPalabraClaveOutput> updated = response.getBody();
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
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/convocatoria_fase.sql",
    "classpath:scripts/configuracion_solicitud.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllInvestigador_WithPagingSortingAndFilter_ReturnsConvocatoriaSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CGAS-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "id==1";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INVESTIGADOR)
        .queryParam("s", sort).queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Convocatoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildGenericRequest(null, null, "CSP-CON-INV-V"), new ParameterizedTypeReference<List<Convocatoria>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<Convocatoria> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_hito.sql",
    "classpath:scripts/convocatoria_hito.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasConvocatoriaHitos_ReturnsStatusCode200() throws Exception {
    String roles = "CSP-CON-V";
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONVOCATORIA_HITOS)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildGenericRequest(null, null, roles),
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
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/convocatoria_fase.sql",
    "classpath:scripts/configuracion_solicitud.sql",
    "classpath:scripts/convocatoria_partida.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllConvocatoriaPartida_WithPagingSortingAndFiltering_ReturnConvocatoriaPartidaSubList() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CGAS-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "tipoPartida==INGRESO";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaPartida>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaPartida>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    final List<ConvocatoriaPartida> convocatoriaConceptoGastos = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGastos).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/convocatoria_fase.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasConvocatoriaFases_ReturnsStatusCode200() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONVOCATORIA_FASES)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD, buildRequest(null, null),
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
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/convocatoria_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/convocatoria_documento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasConvocatoriaDocumentos_ReturnsStatusCode200() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONVOCATORIA_DOCUMENTOS)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD, buildRequest(null, null),
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
    "classpath:scripts/tipo_enlace.sql",
    "classpath:scripts/convocatoria_enlace.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasConvocatoriaEnlaces_ReturnsStatusCode200() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONVOCATORIA_ENLACES)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD, buildRequest(null, null),
        Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
  void findAllConvocatoriaGastosCodigoEcPermitidos_WithPagingSortingAndFiltering_ReturnsConvocatoriaConceptoGastoCodigoEcSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V", "CSP-SOL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONVOCATORIA_GASTO_CODIGO_EC + PATH_PERMITIDOS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaConceptoGastoCodigoEc>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildGenericRequest(headers, null, "CSP-CON-V", "CSP-SOL-V"),
        new ParameterizedTypeReference<List<ConvocatoriaConceptoGastoCodigoEc>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    final List<ConvocatoriaConceptoGastoCodigoEc> convocatoriaConceptoGastos = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGastos).hasSize(2);
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
  void findAllConvocatoriaGastosCodigoEcNoPermitidos_WithPagingSortingAndFiltering_ReturnsConvocatoriaConceptoGastoCodigoEcSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V", "CSP-SOL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "";

    Long convocatoriaId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONVOCATORIA_GASTO_CODIGO_EC + PATH_NO_PERMITIDOS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaConceptoGastoCodigoEc>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildGenericRequest(headers, null, "CSP-CON-V", "CSP-SOL-V"),
        new ParameterizedTypeReference<List<ConvocatoriaConceptoGastoCodigoEc>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    final List<ConvocatoriaConceptoGastoCodigoEc> convocatoriaConceptoGastos = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGastos).hasSize(1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/convocatoria_fase.sql",
    "classpath:scripts/configuracion_solicitud.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void tramitable_ReturnsStatusCode204() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_TRAMITABLE)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildGenericRequest(null, null, "CSP-SOL-INV-C"), Void.class);
    /*
     * NO SE PUEDE HACER EL TEST DEL STATUS CODE 200 PORQUE LA FECHA INICIO Y FECHA
     * FIN ESTABLECIDAS EN LA TABLA
     * CONFIGURACION SOLICITUD, SE COMPARAN CON LA FECHA ACTUAL, POR TANTO HABRÁ UN
     * MOMENTO EN EL QUE EL TEST FALLE
     * PARA CURARSE EN SALUD, QUE ENTRE POR EL STATUS CODE NO CONTENT(204)
     */
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
  void hasSolicitudesReferenced_ReturnsStatusCode200() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUDES_REFERENCED)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildGenericRequest(null, null, "CSP-CON-E"), Void.class);

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
       "classpath:scripts/proyecto.sql"
    // @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasProyectosReferenced_ReturnsStatusCode200() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTOS_REFERENCED)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildGenericRequest(null, null, "CSP-CON-E"), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_ip.sql",
    "classpath:scripts/requisitoip_nivelacademico.sql",
    // @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findRequisitoIPNivelesAcademicos_ReturnsRequisitoIPNivelAcademicoOutputList() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + ConvocatoriaController.PATH_NIVELES_REQUISITOS_IP)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<RequisitoIPNivelAcademicoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildGenericRequest(null, null, "CSP-CON-V", "CSP-SOL-INV-C"),
        new ParameterizedTypeReference<List<RequisitoIPNivelAcademicoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_equipo.sql",
    "classpath:scripts/requisitoequipo_nivelacademico.sql"
    // @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findRequisitoEquipoNivelesAcademicos_ReturnsRequisitoEquipoNivelAcademicoOutputList() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + ConvocatoriaController.PATH_NIVELES_REQUISITOS_EQUIPO)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<RequisitoEquipoNivelAcademicoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildGenericRequest(null, null, "CSP-CON-V", "CSP-SOL-INV-C"),
        new ParameterizedTypeReference<List<RequisitoEquipoNivelAcademicoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
        "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_ip.sql",
    "classpath:scripts/requisitoip_categoriaprofesional.sql"
    // @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findRequisitosIpCategoriasProfesionales_ReturnsRequisitoIPCategoriaProfesionalOutputList() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + ConvocatoriaController.PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_IP)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<RequisitoIPCategoriaProfesionalOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildGenericRequest(null, null, "CSP-CON-V", "CSP-SOL-INV-C"),
        new ParameterizedTypeReference<List<RequisitoIPCategoriaProfesionalOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
        "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_equipo.sql",
    "classpath:scripts/requisitoequipo_categoriaprofesional.sql"
    // @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findCategoriasProfesionalesEquipo_ReturnsRequisitoEquipoCategoriaProfesionalOutputList() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + ConvocatoriaController.PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<RequisitoEquipoCategoriaProfesionalOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildGenericRequest(null, null, "CSP-CON-V", "CSP-CON-INV-V"),
        new ParameterizedTypeReference<List<RequisitoEquipoCategoriaProfesionalOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
        "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void clone_ReturnsLong() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CLONE)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<Long> response = restTemplate.exchange(uri,
        HttpMethod.POST,
        buildGenericRequest(null, null, "CSP-CON-C"), Long.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isNotNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/convocatoria_palabra_clave.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findPalabrasClave_WithPagingSortingAndFiltering_ReturnsConvocatoriaPalabraClaveOutputSubList() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CGAS-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "";
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(
            CONTROLLER_BASE_PATH + ConvocatoriaController.PATH_PALABRAS_CLAVE)
        .queryParam("q", filter).queryParam("s", sort).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaPalabraClaveOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildGenericRequest(null, null, "CSP-CON-E"),
        new ParameterizedTypeReference<List<ConvocatoriaPalabraClaveOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findFormularioSolicitudByConvocatoriaId_ReturnsFormularioSolicitud() throws Exception {
    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_FORMULARIO_SOLICITUD)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<FormularioSolicitud> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildGenericRequest(null, null, "CSP-SOL-E"), FormularioSolicitud.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody()).isEqualTo(FormularioSolicitud.PROYECTO);
  }

  private ConvocatoriaPalabraClaveInput buildMockConvocatoriaPalabraClaveInput(Long solicitudId, String palabraRef) {
    return ConvocatoriaPalabraClaveInput.builder()
        .convocatoriaId(solicitudId)
        .palabraClaveRef(palabraRef)
        .build();
  }

  /**
   * 
   * * MOCK
   */

  /**
   * Función que genera Convocatoria
   * 
   * @param convocatoriaId
   * @param unidadGestionId
   * @param modeloEjecucionId
   * @param modeloTipoFinalidadId
   * @param tipoRegimenConcurrenciaId
   * @param tipoAmbitoGeogragicoId
   * @param activo
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, Long unidadGestionId, Long modeloEjecucionId,
      Long modeloTipoFinalidadId, Long tipoRegimenConcurrenciaId, Long tipoAmbitoGeogragicoId, Boolean activo) {

    // @formatter:off
    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()
            .id(modeloEjecucionId)
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))
            .activo(Boolean.TRUE)
            .build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId))
            .activo(Boolean.TRUE)
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .modeloEjecucion(modeloEjecucion)
            .tipoFinalidad(tipoFinalidad)
            .activo(Boolean.TRUE)
            .build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()
            .id(tipoRegimenConcurrenciaId)
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))
            .activo(Boolean.TRUE)
            .build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()
            .id(tipoAmbitoGeogragicoId)
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId))
            .activo(Boolean.TRUE)
            .build();

    Convocatoria convocatoria = Convocatoria.builder()
        .id(convocatoriaId)
        .unidadGestionRef((unidadGestionId == null) ? null : "2")
        .modeloEjecucion(modeloEjecucion)
        .codigo("codigo-" + String.format("%03d", convocatoriaId))
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaProvisional(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaConcesion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo-" + String.format("%03d", convocatoriaId))
        .objeto("objeto-" + String.format("%03d", convocatoriaId))
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())
        .formularioSolicitud(FormularioSolicitud.PROYECTO)
        .regimenConcurrencia(tipoRegimenConcurrencia)
        .estado(Convocatoria.Estado.REGISTRADA)
        .duracion(12)
        .ambitoGeografico(tipoAmbitoGeografico)
        .clasificacionCVN(ClasificacionCVN.AYUDAS)
        .activo(activo)
        .build();
    // @formatter:on

    return convocatoria;

  }

  private ConvocatoriaEntidadConvocante generarMockConvocatoriaEntidadConvocante(Long id, Long programaId) {
    Programa programa = new Programa();
    programa.setId(programaId);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = new ConvocatoriaEntidadConvocante();
    convocatoriaEntidadConvocante.setId(id);
    convocatoriaEntidadConvocante.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaEntidadConvocante.setEntidadRef("entidad-" + (id == null ? 1 : id));
    convocatoriaEntidadConvocante.setPrograma(programa);

    return convocatoriaEntidadConvocante;
  }

}
