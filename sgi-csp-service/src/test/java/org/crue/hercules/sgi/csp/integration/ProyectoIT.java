package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoAgrupacionGastoOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadResumen;
import org.crue.hercules.sgi.csp.dto.ProyectoDto;
import org.crue.hercules.sgi.csp.dto.ProyectoEquipoDto;
import org.crue.hercules.sgi.csp.dto.ProyectoFacturacionOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoPalabraClaveInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPalabraClaveOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.dto.ProyectoResponsableEconomicoOutput;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.ProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
 * Test de integracion de Proyecto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/proyectos";
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_AREA_CONOCIMIENTO = "/proyecto-areas-conocimiento";
  private static final String PATH_ANUALIDAD_RESUMEN = "/anualidades";
  private static final String PATH_CLASIFICACION = "/proyecto-clasificaciones";
  private static final String PATH_DOCUMENTO = "/documentos";
  private static final String PATH_ENTIDAD_FINANCIADORA = "/proyectoentidadfinanciadoras";
  private static final String PATH_ENTIDAD_GESTORA = "/proyectoentidadgestoras";
  private static final String PATH_ESTADO_PROYECTO = "/estadoproyectos";
  private static final String PATH_FASES = "/proyectofases";
  private static final String PATH_HITOS = "/proyectohitos";
  private static final String PATH_PARTIDA = "/proyecto-partidas";
  private static final String PATH_PERIODO_SEGUIMIENTO = "/proyectoperiodoseguimientos";
  private static final String PATH_PROYECTO_SGE = "/proyectossge";
  private static final String PATH_PAQUETE_TRABAJO = "/proyectopaquetetrabajos";
  private static final String PATH_PROYECTO_EQUIPO = "/proyectoequipos";
  private static final String PATH_PROYECTO_SOCIO = "/proyectosocios";
  private static final String PATH_PRORROGA = "/proyecto-prorrogas";
  private static final String PATH_RESPONSABLE_ECONOMICO = "/proyectoresponsableseconomicos";
  private static final String PATH_MODIFICADOS_IDS = "/modificados-ids";
  private static final String PATH_ANUALIDADES_GASTO = "/anualidadesgasto";
  private static final String PATH_PROYECTO_CONCEPTOS_GASTO = "/proyectoconceptosgasto";
  private static final String PATH_PERMITIDOS = "/permitidos";
  private static final String PATH_NO_PERMITIDOS = "/nopermitidos";
  private static final String PATH_CAMBIAR_ESTADO = "/cambiar-estado";
  private static final String PATH_PRESUPUESTO_TOTALES = "/presupuesto-totales";
  private static final String PATH_PROYECTO_AGRUPACION_GASTO = "/proyectoagrupaciongasto";
  private static final String PATH_PROYECTO_PERIODO_JUSTIFICACION = "/proyectoperiodojustificacion";
  private static final String PATH_MODIFICABLE = "/modificable";
  private static final String PATH_PROYECTO_ANUALIDADES = "/proyectoanualidades";
  private static final String PATH_ANUALIDADES_GASTOS = "/anualidadesgastos";
  private static final String PATH_PROYECTOS_FACTURACION = "/proyectosfacturacion";
  private static final String PATH_PARAMETER_PROYECTO_ID = "/{proyectoId}";
  private static final String PATH_PALABRAS_CLAVE = "/palabrasclave";
  private static final String PATH_NOTIFICACIONES_PROYECTO = "/notificacionesproyectos";
  private static final String PATH_PRC_ANIO = "/produccioncientifica/{anioInicio}/{anioFin}";
  private static final String PATH_PRC_TOTAL_IMPORTE_CONCEDIDO = "/produccioncientifica/totalimporteconcedido/{proyectoId}";
  private static final String PATH_PRC_TOTAL_IMPORTE_CONCEDIDO_COSTES_INDIRECTOS = "/produccioncientifica/totalimporteconcedidocostesindirectos/{proyectoId}";
  private static final String PATH_PRC_PROYECTO_EQUIPO = "/produccioncientifica/equipo/{proyectoId}";
  private static final String PATH_ANUALIDAD_GASTOS = PATH_PARAMETER_ID + "/anualidad-gastos";
  private static final String PATH_ANUALIDAD_INGRESOS = PATH_PARAMETER_ID + "/anualidad-ingresos";
  private static final String PATH_GASTOS_PROYECTO = PATH_PARAMETER_ID + "/gastos-proyecto";

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
      "classpath:scripts/tipo_ambito_geografico.sql",
        // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyecto() throws Exception {
    String roles = "CSP-PRO-C";
    Proyecto proyecto = generarMockProyecto(null);

    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, proyecto, roles), Proyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Proyecto proyectoCreado = response.getBody();
    Assertions.assertThat(proyectoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off 
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", 
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql"
      // @formatter:on 
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoBySolicitud() throws Exception {
    String roles = "CSP-PRO-C";
    Proyecto proyecto = generarMockProyecto(null);

    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, proyecto, roles), Proyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Proyecto proyectoCreado = response.getBody();
    Assertions.assertThat(proyectoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());

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
      "classpath:scripts/estado_proyecto.sql" 
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyecto() throws Exception {
    String roles = "CSP-PRO-E";
    Long idProyecto = 1L;
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setObservaciones("observaciones actualizadas");

    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, proyecto, roles), Proyecto.class, idProyecto);

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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", 
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql", 
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnProyecto() throws Exception {
    String roles = "CSP-PRO-B";
    Long idProyecto = 1L;

    final ResponseEntity<Proyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null, roles), Proyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Proyecto proyecto = response.getBody();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyecto.getActivo()).as("getActivo()").isFalse();
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
      "classpath:scripts/estado_proyecto.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void reactivar_ReturnProyecto() throws Exception {
    String roles = "CSP-PRO-R";
    Long idProyecto = 5L;

    final ResponseEntity<Proyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null, roles), Proyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Proyecto proyecto = response.getBody();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyecto.getActivo()).as("getActivo()").isTrue();
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
      "classpath:scripts/estado_proyecto.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsById_Returns200() throws Exception {
    String roles = "CSP-PRO-E";
    // given: existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null, roles), Proyecto.class, id);
    // then: 200 OK
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
      "classpath:scripts/estado_proyecto.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyecto() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-MOD-V" };
    Long idProyecto = 1L;

    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), Proyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Proyecto proyecto = response.getBody();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyecto.getEstado().getId()).as("getEstado().getId()").isEqualTo(1);
    Assertions.assertThat(proyecto.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones-proyecto-001");
    Assertions.assertThat(proyecto.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("2");
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
      "classpath:scripts/estado_proyecto.sql",
    // @formatter:off
   })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsProyectoSubList() throws Exception {
    String[] roles = {"CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E", "CSP-PRO-B", "CSP-PRO-MOD-V"};
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
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<Proyecto>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Proyecto> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { 
     // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", 
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql", 
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" 
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodos_WithPagingSortingAndFiltering_ReturnsProyectoSubList() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E", "CSP-PRO-B", "CSP-PRO-R", "CSP-PRO-INV-VR" };
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
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<Proyecto>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Proyecto> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
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
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_hito.sql",
      "classpath:scripts/modelo_ejecucion.sql", 
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", 
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", 
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_hito.sql", 
      "classpath:scripts/proyecto_hito.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoHito_WithPagingSortingAndFiltering_ReturnsProyectoHitoSubList() throws Exception {
    String roles = "CSP-PRO-E";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "comentario=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_HITOS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoHito>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoHito> proyectosHitos = response.getBody();
    Assertions.assertThat(proyectosHitos).hasSize(3);
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/tipo_hito.sql",
    "classpath:scripts/proyecto_hito.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasProyectoHitos_Returns200() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_HITOS)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Proyecto> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Proyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/tipo_hito.sql",
    "classpath:scripts/proyecto_hito.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasProyectoHitos_ReturnsEmptyHitos() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_HITOS)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Proyecto> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Proyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /*
   * PROYECTO FASE
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_ejecucion.sql", 
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", 
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", 
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_fase.sql", 
      "classpath:scripts/proyecto_fase_aviso.sql",
      "classpath:scripts/proyecto_fase.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoFase_WithPagingSortingAndFiltering_ReturnsProyectoFaseSubList() throws Exception {
    String roles = "CSP-PRO-E";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "observaciones=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_FASES)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoFaseOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoFaseOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoFaseOutput> proyectosFases = response.getBody();
    Assertions.assertThat(proyectosFases).hasSize(3);
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/proyecto_fase_aviso.sql",
    "classpath:scripts/proyecto_fase.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasProyectoFases_Returns200() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_FASES)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Proyecto> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Proyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/proyecto_fase_aviso.sql",
    "classpath:scripts/proyecto_fase.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasProyectoFases_ReturnsEmptyFases() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_FASES)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class);

    // then: El proyecto no tiene fases
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /*
   * PROYECTO PAQUETE TRABAJO
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", 
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql", 
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", 
      "classpath:scripts/proyecto_paquete_trabajo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoPaqueteTrabajo_WithPagingSortingAndFiltering_ReturnsProyectoPaqueteTrabajoSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "nombre=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PAQUETE_TRABAJO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoPaqueteTrabajo>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoPaqueteTrabajo>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoPaqueteTrabajo> proyectosPaqueteTrabajos = response.getBody();
    Assertions.assertThat(proyectosPaqueteTrabajos).hasSize(3);
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
  void findAllProyectoSocio_WithPagingSortingAndFiltering_ReturnsProyectoSocioSubList() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "empresaRef=ke=00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoSocio>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoSocio>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoSocio> proyectoSocios = response.getBody();
    Assertions.assertThat(proyectoSocios).hasSize(3);
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/proyecto_socio.sql",  
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasAnyProyectoSocio_Returns200() throws Exception {
    String[] roles = { "CSP-SOL-E", "CSP-SOL-V" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Object.class);

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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/proyecto_socio.sql",  
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasAnyProyectoSocio_ReturnsEmptyProyectoSocio() throws Exception {
    String[] roles = { "CSP-SOL-E", "CSP-SOL-V" };

    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Object.class);

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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/proyecto_socio.sql",  
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasAnyProyectoSocioWithRolCoordinador_Returns200() throws Exception {
    String[] roles = { "CSP-SOL-E", "CSP-SOL-V" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO + "/coordinador")
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Object.class);

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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/proyecto_socio.sql",  
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasAnyProyectoSocioWithRolCoordinador_ReturnsEmptyProyectoSocioWithRolCoordinador() throws Exception {
    String[] roles = { "CSP-SOL-E", "CSP-SOL-V" };

    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO + "/coordinador")
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Object.class);

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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/proyecto_socio.sql",
    "classpath:scripts/proyecto_socio_periodo_pago.sql"  
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsProyectoSocioPeriodoPagoByProyectoSocioId_Returns200() throws Exception {
    String[] roles = { "CSP-SOL-E", "CSP-SOL-V" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO + "/periodospago")
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Object.class);

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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/proyecto_socio.sql",
    "classpath:scripts/proyecto_socio_periodo_pago.sql"  
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsProyectoSocioPeriodoPagoByProyectoSocioId_ReturnsEmptyProyectoSocioPeriodoPago() throws Exception {
    String[] roles = { "CSP-SOL-E", "CSP-SOL-V" };

    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO + "/periodospago")
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Object.class);

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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/proyecto_socio.sql",
    "classpath:scripts/proyecto_socio_periodo_justificacion.sql"  
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsProyectoSocioPeriodoJustificacionByProyectoSocioId_Returns200() throws Exception {
    String[] roles = { "CSP-SOL-E", "CSP-SOL-V" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO + "/periodosjustificacion")
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Object.class);

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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/proyecto_socio.sql",
    "classpath:scripts/proyecto_socio_periodo_justificacion.sql"  
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsProyectoSocioPeriodoJustificacionByProyectoSocioId_ReturnsEmptyProyectoSocioPeriodoJustificacion()
      throws Exception {
    String[] roles = { "CSP-SOL-E", "CSP-SOL-V" };

    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO + "/periodosjustificacion")
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Object.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /*
   * PROYECTO ENTIDAD GESTORA
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", 
      "classpath:scripts/proyecto_entidad_gestora.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoEntidadGestora_WithPagingSortingAndFiltering_ReturnsProyectoEntidadGestoraSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-MOD-V" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "entidadRef=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_GESTORA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoEntidadGestora>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoEntidadGestora>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoEntidadGestora> proyectosEntidadGestoras = response.getBody();
    Assertions.assertThat(proyectosEntidadGestoras).hasSize(3);
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", 
      "classpath:scripts/proyecto_entidad_gestora.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoEntidadGestora_ReturnEmptySubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-MOD-V" };
    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_GESTORA)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoEntidadGestora>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoEntidadGestora>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /*
   * 
   * PROYECTO EQUIPO
   * 
   */

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", 
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql", 
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoEquipo_WithPagingSortingAndFiltering_ReturnsProyectoEquipoSubList() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-MOD-V" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "personaRef=ke=00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_EQUIPO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoEquipo>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoEquipo>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoEquipo> proyectoEquipos = response.getBody();
    Assertions.assertThat(proyectoEquipos).hasSize(3);
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", 
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql", 
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoEquipo_ReturnsEmptySubList() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-MOD-V" };
    Long proyectoId = 3L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_EQUIPO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoEquipo>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoEquipo>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /*
   *
   * PROYECTO PRÓRROGA
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", 
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql", 
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", 
      "classpath:scripts/proyecto_prorroga.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoProrroga_WithPagingSortingAndFiltering_ReturnsProyectoProrrogaSubList() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "observaciones=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRORROGA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoProrroga>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoProrroga>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoProrroga> proyectosProrrogas = response.getBody();
    Assertions.assertThat(proyectosProrrogas).hasSize(3);
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", 
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql", 
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", 
      "classpath:scripts/proyecto_prorroga.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoProrroga_ReturnsEmptySubList() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRORROGA)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoProrroga>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoProrroga>>() {
        });

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
      "classpath:scripts/proyecto_prorroga.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasProyectoProrrogas_ReturnsDoNotGetProyectoProrrogas() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRORROGA)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Proyecto> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Proyecto.class);

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
      "classpath:scripts/proyecto_prorroga.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasProyectoProrrogas_Returns200() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRORROGA)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Proyecto> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Proyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  /**
   * 
   * ESTADO PROYECTO
   * 
   */
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
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllEstadoProyecto_WithPagingSortingAndFiltering_ReturnsProyectoEntidadFinanciadoraSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "comentario=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ESTADO_PROYECTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<EstadoProyecto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<EstadoProyecto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<EstadoProyecto> estadoProyecto = response.getBody();
    Assertions.assertThat(estadoProyecto).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");
    Assertions.assertThat(estadoProyecto.get(0).getId()).as("get(0).getId()")
        .isEqualTo(1L);
    Assertions.assertThat(estadoProyecto.get(0).getProyectoId()).as("get(0).getProyectoId()")
        .isEqualTo(1L);
    Assertions.assertThat(estadoProyecto.get(0).getComentario()).as("get(0).getComentario()")
        .isEqualTo("comentario-estado-proyecto-" + String.format("%03d", 1));

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_ambito_geografico.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllEstadoProyecto_ReturnsEmptyEntidadFinanciadoraSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 6L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ESTADO_PROYECTO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<EstadoProyecto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<EstadoProyecto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /**
   * 
   * PROYECTO ENTIDAD FINANCIADORA
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_ambito_geografico.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoEntidadFinanciadora_WithPagingSortingAndFiltering_ReturnsProyectoEntidadFinanciadoraSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-MOD-V" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "entidadRef=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_FINANCIADORA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoEntidadFinanciadora>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoEntidadFinanciadora>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoEntidadFinanciadora> proyectoEntidadFinanciadora = response.getBody();
    Assertions.assertThat(proyectoEntidadFinanciadora).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");
    Assertions.assertThat(proyectoEntidadFinanciadora.get(0).getProyectoId()).as("get(0).getProyectoId()")
        .isEqualTo(1L);
    Assertions.assertThat(proyectoEntidadFinanciadora.get(0).getEntidadRef()).as("get(0).getEntidadRef()")
        .isEqualTo("entidad-" + String.format("%03d", 1));
    Assertions.assertThat(proyectoEntidadFinanciadora.get(0).getTipoFinanciacion().getId())
        .as("get(0).getTipoFinanciacion().getId()")
        .isEqualTo(1L);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_ambito_geografico.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoEntidadFinanciadora_ReturnsEmptySubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-MOD-V" };
    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_FINANCIADORA)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoEntidadFinanciadora>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoEntidadFinanciadora>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /**
   * 
   * PROYECTO DOCUMENTOS
   * 
   */
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
    "classpath:scripts/proyecto_documento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllDocumentos_WithPagingSortingAndFiltering_ReturnsDocumentosSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "documentoRef=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DOCUMENTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoDocumento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoDocumento> proyectoDocumento = response.getBody();
    Assertions.assertThat(proyectoDocumento).hasSize(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");
    Assertions.assertThat(proyectoDocumento.get(0).getComentario()).as("get(0).getComentario()")
        .isEqualTo("comentario-" + String.format("%03d", 2));
    Assertions.assertThat(proyectoDocumento.get(1).getComentario()).as("get(0).getComentario()")
        .isEqualTo("comentario-" + String.format("%03d", 1));

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
    "classpath:scripts/proyecto_documento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllDocumentos_ReturnsEmptySubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DOCUMENTO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoDocumento>>() {
        });

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
      "classpath:scripts/tipo_documento.sql",
      "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/proyecto_documento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasProyectoDocumento_Returns200() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DOCUMENTO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<Proyecto>> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<Proyecto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  /**
   * 
   * PROYECTO DOCUMENTOS
   * 
   */
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
    "classpath:scripts/proyecto_periodo_seguimiento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoPeriodoSeguimiento_WithPagingSortingAndFiltering_ReturnsProyectoPeriodoSeguimientoSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "observaciones=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PERIODO_SEGUIMIENTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoPeriodoSeguimiento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoPeriodoSeguimiento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoPeriodoSeguimiento> proyectoPeriodoSeguimiento = response.getBody();
    Assertions.assertThat(proyectoPeriodoSeguimiento).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(proyectoPeriodoSeguimiento.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("obs-" + String.format("%03d", 3));
    Assertions.assertThat(proyectoPeriodoSeguimiento.get(1).getObservaciones()).as("get(1).getObservaciones()")
        .isEqualTo("obs-" + String.format("%03d", 2));
    Assertions.assertThat(proyectoPeriodoSeguimiento.get(2).getObservaciones()).as("get(2).getObservaciones()")
        .isEqualTo("obs-" + String.format("%03d", 1));

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
    "classpath:scripts/proyecto_periodo_seguimiento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoPeriodoSeguimiento_ReturnsEmptySubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    Long proyectoId = 6L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PERIODO_SEGUIMIENTO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoPeriodoSeguimiento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoPeriodoSeguimiento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /**
   * 
   * PROYECTO CLASIFICACIONES
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",  
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/proyecto_clasificacion.sql", 
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoClasificaciones_WithPagingSortingAndFiltering_ReturnsProyectoClasificacionesSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "clasificacionRef=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CLASIFICACION)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoClasificacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoClasificacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoClasificacion> proyectoClasificacion = response.getBody();
    Assertions.assertThat(proyectoClasificacion).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(proyectoClasificacion.get(0).getClasificacionRef()).as("get(0).getClasificacionRef()")
        .isEqualTo("clasificacion-ref-" + String.format("%03d", 3));
    Assertions.assertThat(proyectoClasificacion.get(1).getClasificacionRef()).as("get(1).getClasificacionRef()")
        .isEqualTo("clasificacion-ref-" + String.format("%03d", 2));
    Assertions.assertThat(proyectoClasificacion.get(2).getClasificacionRef()).as("get(2).getClasificacionRef()")
        .isEqualTo("clasificacion-ref-" + String.format("%03d", 1));

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
    "classpath:scripts/proyecto_clasificacion.sql", 
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoClasificaciones_ReturnsEmptySubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CLASIFICACION)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoClasificacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoClasificacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  /**
   * 
   * PROYECTO AREA CONOCIMIENTO
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",  
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/proyecto_area.sql", 
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllByProyectoId_WithPagingSortingAndFiltering_ReturnsProyectoAreaConocimientoSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "areaConocimientoRef=ke=00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_AREA_CONOCIMIENTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoAreaConocimiento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoAreaConocimiento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoAreaConocimiento> proyectoAreaConocimiento = response.getBody();
    Assertions.assertThat(proyectoAreaConocimiento).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(proyectoAreaConocimiento.get(0).getAreaConocimientoRef())
        .as("get(0).getAreaConocimientoRef()")
        .isEqualTo(String.format("%05d", 3));
    Assertions.assertThat(proyectoAreaConocimiento.get(1).getAreaConocimientoRef())
        .as("get(1).getAreaConocimientoRef()")
        .isEqualTo(String.format("%05d", 2));
    Assertions.assertThat(proyectoAreaConocimiento.get(2).getAreaConocimientoRef())
        .as("get(2).getAreaConocimientoRef()")
        .isEqualTo(String.format("%05d", 1));

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
    "classpath:scripts/proyecto_area.sql", 
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllByProyectoId_ReturnsEmptySubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_AREA_CONOCIMIENTO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoAreaConocimiento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoAreaConocimiento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /**
   * 
   * PROYECTO PROYECTO SGE
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/proyecto_proyecto_sge.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoProyectosSge_WithPagingSortingAndFiltering_ReturnsProyectoProyectoSgeSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-MOD-V" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "proyectoSgeRef=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SGE)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoProyectoSge>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoProyectoSge>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoProyectoSge> proyectoProyectoSge = response.getBody();
    Assertions.assertThat(proyectoProyectoSge).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");
    Assertions.assertThat(proyectoProyectoSge.get(0).getId()).as("get(0).getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoProyectoSge.get(0).getProyectoSgeRef())
        .as("get(0).getProyectoSgeRef()").isEqualTo("proyecto-sge-ref-" + String.format("%03d", 1));
    Assertions.assertThat(proyectoProyectoSge.get(0).getProyectoId())
        .as("get(0).getProyectoId()").isEqualTo(1L);

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
    "classpath:scripts/proyecto_proyecto_sge.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoProyectosSge_ReturnsEmptyList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-MOD-V" };

    Long proyectoId = 4L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SGE)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoProyectoSge>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoProyectoSge>>() {
        });

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
    "classpath:scripts/proyecto_proyecto_sge.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasProyectosSge_Returns200() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SGE)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Proyecto> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Proyecto.class);

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
    "classpath:scripts/proyecto_proyecto_sge.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasProyectosSge_ReturnsEmptyProyectosSge() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 4L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SGE)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Proyecto> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Proyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /**
   * 
   * PROYECTO ANUALIDAD RESUMEN
   * 
   */
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
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoAnualidadResumen_WithPagingSortingAndFiltering_ReturnsProyectoAnualidadResumenSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "anio==ke=20";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ANUALIDAD_RESUMEN)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoAnualidadResumen>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoAnualidadResumen>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoAnualidadResumen> proyectoAnualidadResumen = response.getBody();
    Assertions.assertThat(proyectoAnualidadResumen).hasSize(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");
    Assertions.assertThat(proyectoAnualidadResumen.get(0).getId()).as("get(0).getId()").isEqualTo(2L);
    Assertions.assertThat(proyectoAnualidadResumen.get(1).getId()).as("get(1).getId()").isEqualTo(1L);

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
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoAnualidadResumen_ReturnsEmptyList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 3L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ANUALIDAD_RESUMEN)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoAnualidadResumen>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoAnualidadResumen>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /**
   * 
   * PROYECTO PARTIDA
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
       "classpath:scripts/modelo_ejecucion.sql",
       "classpath:scripts/modelo_unidad.sql",
       "classpath:scripts/tipo_finalidad.sql",
       "classpath:scripts/tipo_ambito_geografico.sql",
       "classpath:scripts/tipo_regimen_concurrencia.sql",
       "classpath:scripts/convocatoria.sql",
       "classpath:scripts/proyecto.sql",
       "classpath:scripts/convocatoria_partida.sql",
       "classpath:scripts/proyecto_partida.sql",
    // @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoPartida_WithPagingSortingAndFiltering_ReturnsProyectoPartidaSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "codigo=ke=08";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARTIDA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoPartida>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoPartida>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoPartida> proyectoPartida = response.getBody();
    Assertions.assertThat(proyectoPartida).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");
    Assertions.assertThat(proyectoPartida.get(0).getId()).as("get(0).getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoPartida.get(0).getCodigo()).as("get(0).getCodigo()").isEqualTo("08.002B.541A.64215");

  }

  /**
   * 
   * PROYECTO PARTIDA
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
       "classpath:scripts/modelo_ejecucion.sql",
       "classpath:scripts/modelo_unidad.sql",
       "classpath:scripts/tipo_finalidad.sql",
       "classpath:scripts/tipo_ambito_geografico.sql",
       "classpath:scripts/tipo_regimen_concurrencia.sql",
       "classpath:scripts/convocatoria.sql",
       "classpath:scripts/proyecto.sql",
       "classpath:scripts/convocatoria_partida.sql",
       "classpath:scripts/proyecto_partida.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoPartida_WithPagingSortingAndFiltering_ReturnsEmptyList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "codigo=ke=08";

    Long proyectoId = 3L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARTIDA)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoPartida>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoPartida>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /**
   * 
   * PROYECTO RESPONSABLE ECONOMICO
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/proyecto_responsable_economico.sql", 
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllResponsablesEconomicosByProyecto_WithPagingSortingAndFiltering_ReturnsResponsablesEconomicosByProyectoSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "personaRef=ke=-00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_RESPONSABLE_ECONOMICO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoResponsableEconomicoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoResponsableEconomicoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoResponsableEconomicoOutput> proyectoResponsableEconomicoOutput = response.getBody();
    Assertions.assertThat(proyectoResponsableEconomicoOutput).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(proyectoResponsableEconomicoOutput.get(0).getPersonaRef()).as("get(0).getPersonaRef()")
        .isEqualTo("personaRef-" + String.format("%03d", 3));
    Assertions.assertThat(proyectoResponsableEconomicoOutput.get(1).getPersonaRef()).as("get(1).getPersonaRef()")
        .isEqualTo("personaRef-" + String.format("%03d", 2));
    Assertions.assertThat(proyectoResponsableEconomicoOutput.get(2).getPersonaRef()).as("get(2).getPersonaRef()")
        .isEqualTo("personaRef-" + String.format("%03d", 1));

  }

  /**
   * 
   * PROYECTO RESPONSABLE ECONOMICO
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/proyecto_responsable_economico.sql", 
    // @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findIds_WithPagingSortingAndFiltering_ReturnsEmptySubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "fechaModificacion=ge=2020-01-12T00:00:00Z";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_MODIFICADOS_IDS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<Long>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<Long>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
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
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoAnualidadGasto_WithPagingAndSorting_ReturnsAnualidadGastoSubList() throws Exception {

    String[] roles = { "CSP-PRO-V" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ANUALIDADES_GASTO)
        .queryParam("s", sort)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<AnualidadGasto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<AnualidadGasto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    List<AnualidadGasto> responseData = response.getBody();
    Assertions.assertThat(responseData).isNotNull()
        .hasSize(3);

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter: off
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
      "classpath:scripts/proyecto_concepto_gasto.sql"
      // @formatter: on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoGastosPermitidos_WithPagingAndSorting_ReturnsProyectoConceptoGastoSubList() throws Exception {

    String[] roles = { "CSP-PRO-V" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_CONCEPTOS_GASTO + PATH_PERMITIDOS)
        .queryParam("s", sort)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoConceptoGasto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoConceptoGasto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("2");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    List<ProyectoConceptoGasto> responseData = response.getBody();
    Assertions.assertThat(responseData).isNotNull().hasSize(2);

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter: off
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
      "classpath:scripts/proyecto_concepto_gasto.sql"
      // @formatter: on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoGastosNoPermitidos_WithPagingAndSorting_ReturnsProyectoConceptoGastoSubList() throws Exception {

    String[] roles = { "CSP-PRO-V" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_CONCEPTOS_GASTO + PATH_NO_PERMITIDOS)
        .queryParam("s", sort)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoConceptoGasto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProyectoConceptoGasto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("2");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    List<ProyectoConceptoGasto> responseData = response.getBody();
    Assertions.assertThat(responseData).isNotNull().hasSize(2);

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(4L);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(5L);
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
    "classpath:scripts/estado_proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void cambiarEstado_ReturnsProyecto() throws Exception {
    Long proyectoId = 1L;
    EstadoProyecto newEstado = generarMockEstadoProyecto(null);
    newEstado.setEstado(EstadoProyecto.Estado.RENUNCIADO);
    String roles = "CSP-PRO-E";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CAMBIAR_ESTADO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Proyecto> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(null, newEstado, roles), Proyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getEstado()).isNotNull();
    Assertions.assertThat(response.getBody().getEstado().getEstado()).isEqualTo(EstadoProyecto.Estado.RENUNCIADO);
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
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_partida.sql",
    "classpath:scripts/anualidad_gasto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void getProyectoPresupuestoTotales_ReturnsProyectoPresupuestoTotales() throws Exception {
    String roles = "CSP-SOL-E";
    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRESUPUESTO_TOTALES)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<ProyectoPresupuestoTotales> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles),
        ProyectoPresupuestoTotales.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getImporteTotalPresupuestoUniversidadSinCosteIndirecto().doubleValue())
        .isEqualTo(6000d);
    Assertions.assertThat(response.getBody().getImporteTotalPresupuestoSocios().doubleValue()).isEqualTo(0d);
    Assertions.assertThat(response.getBody().getImporteTotalConcedidoUniversidadSinCosteIndirecto().doubleValue())
        .isEqualTo(6000d);
    Assertions.assertThat(response.getBody().getImporteTotalConcedidoSocios().doubleValue()).isEqualTo(0d);
    Assertions.assertThat(response.getBody().getImporteTotalPresupuesto().doubleValue()).isEqualTo(71000d);
    Assertions.assertThat(response.getBody().getImporteTotalConcedido().doubleValue()).isEqualTo(63000d);
    Assertions.assertThat(response.getBody().getImporteTotalPresupuestoUniversidadCostesIndirectos().doubleValue())
        .isEqualTo(65000d);
    Assertions.assertThat(response.getBody().getImporteTotalConcedidoUniversidadCostesIndirectos().doubleValue())
        .isEqualTo(57000d);

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
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_agrupacion_gasto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoAgrupacionGastoByProyectoId_WithPagingSortingAndFiltering_ReturnsProyectoAgrupacionGastoOutputSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_AGRUPACION_GASTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoAgrupacionGastoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoAgrupacionGastoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    final List<ProyectoAgrupacionGastoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
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
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllPeriodoJustificacionByProyectoId_WithPagingSortingAndFiltering_ReturnsProyectoPeriodoJustificacionSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_PERIODO_JUSTIFICACION)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoPeriodoJustificacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoPeriodoJustificacion>>() {
        });

    final String expectedSize = "3";
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo(expectedSize);

    final List<ProyectoPeriodoJustificacion> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(Integer.valueOf(expectedSize));
    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
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
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void modificable_ReturnsStatusCode200() throws Exception {
    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_MODIFICABLE)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, "CSP-PRO-E"), Void.class);

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
      "classpath:scripts/proyecto_anualidad.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoAnualidad_ReturnsProyectoAnualidadList() throws Exception {
    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_ANUALIDADES)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoAnualidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, "CSP-PRO-V"), new ParameterizedTypeReference<List<ProyectoAnualidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody()).hasSize(3);

    List<ProyectoAnualidad> responseData = response.getBody().stream().sorted(new Comparator<ProyectoAnualidad>() {

      @Override
      public int compare(ProyectoAnualidad o1, ProyectoAnualidad o2) {

        return o1.getId().compareTo(o2.getId());
      }

    }).collect(Collectors.toList());

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(3);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(4);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(5);
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
  void findProyectoAnualidadesGasto_ReturnsAnualidadGastoOutputList() throws Exception {
    Long proyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ANUALIDADES_GASTOS)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoAnualidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, "CSP-PRO-V"), new ParameterizedTypeReference<List<ProyectoAnualidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody()).hasSize(4);

    List<ProyectoAnualidad> responseData = response.getBody().stream().sorted(new Comparator<ProyectoAnualidad>() {

      @Override
      public int compare(ProyectoAnualidad o1, ProyectoAnualidad o2) {

        return o1.getId().compareTo(o2.getId());
      }

    }).collect(Collectors.toList());

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();
    Assertions.assertThat(responseData.get(3)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(4);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(5);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(6);
    Assertions.assertThat(responseData.get(3).getId()).isEqualTo(7);
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
      "classpath:scripts/tipo_facturacion.sql",
      "classpath:scripts/proyecto_facturacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllProyectoFacturacion_WithPagingSortingAndFiltering_ReturnsProyectoFacturacionOutputSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "id>=1;id<=3";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTOS_FACTURACION)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoFacturacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoFacturacionOutput>>() {
        });

    final String expectedSize = "3";
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo(expectedSize);

    Assertions.assertThat(response.getBody()).isNotNull();

    final List<ProyectoFacturacionOutput> responseData = response.getBody().stream()
        .sorted(new Comparator<ProyectoFacturacionOutput>() {
          @Override
          public int compare(ProyectoFacturacionOutput o1, ProyectoFacturacionOutput o2) {

            return o1.getId().compareTo(o2.getId());
          }
        })
        .collect(Collectors.toList());
    Assertions.assertThat(responseData).hasSize(Integer.valueOf(expectedSize));

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3);
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
      "classpath:scripts/proyecto_palabra_clave.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findPalabraClave_WithPagingSortingAndFiltering_ReturnsProyectoPalabraClaveOutputSubList()
      throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "id>=1;id<=3";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_PROYECTO_ID + PATH_PALABRAS_CLAVE)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoPalabraClaveOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoPalabraClaveOutput>>() {
        });

    final String expectedSize = "3";
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo(expectedSize);

    Assertions.assertThat(response.getBody()).isNotNull();

    final List<ProyectoPalabraClaveOutput> responseData = response.getBody().stream()
        .sorted(new Comparator<ProyectoPalabraClaveOutput>() {
          @Override
          public int compare(ProyectoPalabraClaveOutput o1, ProyectoPalabraClaveOutput o2) {

            return o1.getId().compareTo(o2.getId());
          }
        })
        .collect(Collectors.toList());
    Assertions.assertThat(responseData).hasSize(Integer.valueOf(expectedSize));

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3);
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
      "classpath:scripts/proyecto_palabra_clave.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updatePalabrasClave_ReturnsProyectoPalabraClaveOutputList() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };

    Long proyectoId = 1L;
    List<ProyectoPalabraClaveInput> toUpdate = Arrays.asList(
        buildMockProyectoPalabraClaveInput(1L, "updated-01", proyectoId),
        buildMockProyectoPalabraClaveInput(2L, "updated-02", proyectoId));

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_PROYECTO_ID + PATH_PALABRAS_CLAVE)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoPalabraClaveOutput>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), new ParameterizedTypeReference<List<ProyectoPalabraClaveOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    final List<ProyectoPalabraClaveOutput> responseData = response.getBody().stream()
        .sorted(new Comparator<ProyectoPalabraClaveOutput>() {
          @Override
          public int compare(ProyectoPalabraClaveOutput o1, ProyectoPalabraClaveOutput o2) {

            return o1.getId().compareTo(o2.getId());
          }
        })
        .collect(Collectors.toList());
    Assertions.assertThat(responseData).hasSize(Integer.valueOf(2));

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2);

    Assertions.assertThat(responseData.get(0).getPalabraClaveRef()).isEqualTo("updated-01");
    Assertions.assertThat(responseData.get(1).getPalabraClaveRef()).isEqualTo("updated-02");
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
      "classpath:scripts/autorizacion.sql",
      "classpath:scripts/notificacion_proyecto_externo_cvn.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllNotificacionProyectoExternoCVN_ReturnsNotificacionProyectoExternoCVNOutputList() throws Exception {
    String[] roles = { "CSP-PRO-V", "CSP-PRO-E" };
    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_NOTIFICACIONES_PROYECTO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<NotificacionProyectoExternoCVNOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<NotificacionProyectoExternoCVNOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody()).hasSize(3);

    List<NotificacionProyectoExternoCVNOutput> responseData = response.getBody().stream()
        .sorted(new Comparator<NotificacionProyectoExternoCVNOutput>() {
          @Override
          public int compare(NotificacionProyectoExternoCVNOutput o1, NotificacionProyectoExternoCVNOutput o2) {
            return o1.getId().compareTo(o2.getId());
          }
        }).collect(Collectors.toList());

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
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
  void hasAnualidadGastos_Returns204() throws Exception {
    String[] roles = { "CSP-PRO-E" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ANUALIDAD_GASTOS)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
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
  void hasAnualidadIngresos_Returns204() throws Exception {
    String[] roles = { "CSP-PRO-E" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ANUALIDAD_INGRESOS)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
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
  void hasGastosProyecto_Returns204() throws Exception {
    String[] roles = { "CSP-PRO-E" };

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_GASTOS_PROYECTO)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
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

  private ProyectoPalabraClaveInput buildMockProyectoPalabraClaveInput(Long id, String palabraClaveRef,
      Long proyectoId) {
    return ProyectoPalabraClaveInput.builder()
        .palabraClaveRef(palabraClaveRef)
        .proyectoId(proyectoId)
        .build();
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
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/autorizacion.sql",
      "classpath:scripts/notificacion_proyecto_externo_cvn.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2020, 2021" })
  void findProyectosProduccionCientifica_ok(Integer anioInicio, Integer anioFin)
      throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<List<ProyectoDto>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PRC_ANIO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<ProyectoDto>>() {
        }, anioInicio, anioFin);

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
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/proyecto_anualidad.sql",
      "classpath:scripts/proyecto_partida.sql",
      "classpath:scripts/anualidad_gasto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "1, '63000.00'", "2, '30100.00'" })
  void getTotalImporteConcedidoAnualidadGasto_ok(Long proyectoId, String total)
      throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<BigDecimal> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PRC_TOTAL_IMPORTE_CONCEDIDO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<BigDecimal>() {
        }, proyectoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    BigDecimal totalImporteConcedido = response.getBody();
    Assertions.assertThat(totalImporteConcedido).isEqualTo(new BigDecimal(total));

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
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/proyecto_anualidad.sql",
      "classpath:scripts/proyecto_partida.sql",
      "classpath:scripts/anualidad_gasto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "1, '57000.00'", "2, '30100.00'" })
  void getTotalImporteConcedidoAnualidadGastoCostesIndirectos_ok(Long proyectoId, String total)
      throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<BigDecimal> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PRC_TOTAL_IMPORTE_CONCEDIDO_COSTES_INDIRECTOS,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<BigDecimal>() {
        }, proyectoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    BigDecimal totalImporteConcedido = response.getBody();
    Assertions.assertThat(totalImporteConcedido).isEqualTo(new BigDecimal(total));

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
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "1" })
  void findByProyectoIdAndAnio_ok(Long proyectoId) throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<List<ProyectoEquipoDto>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PRC_PROYECTO_EQUIPO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<ProyectoEquipoDto>>() {
        }, proyectoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<ProyectoEquipoDto> equipo = response.getBody();
    Integer numPersonasEquipo = equipo.size();
    Assertions.assertThat(numPersonasEquipo).isEqualTo(5);
  }

}
