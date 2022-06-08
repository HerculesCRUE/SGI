package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.BaremacionController;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionIT extends BaremacionBaseIT {

  protected static final String PATH_PARAMETER_ID = "/{id}";
  protected static final String PATH_CREATE_TASK = "/createTask";
  protected static final String CONTROLLER_BASE_PATH = BaremacionController.MAPPING;

  @Test
  void baremacion_without_baremacion() throws Exception {
    Long idBaremacion = 1L;

    final ResponseEntity<ConvocatoriaBaremacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_CREATE_TASK + PATH_PARAMETER_ID, HttpMethod.POST,
        buildRequestBaremacion(null, null),
        ConvocatoriaBaremacion.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_publicacion_without_prc_validado() throws Exception {
    Long idBaremacion = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(1L,
        TipoEstadoProduccion.PENDIENTE);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    baremacionWithoutPuntuaciones(idBaremacion);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/convocatoria_baremacion.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_publicacion_with_fecha_inicio_ejecucion() throws Exception {
    Long idBaremacion = 1L;

    updateConvocatoriaBaremacionFechaInicioEjecucion(idBaremacion, Instant.now());

    final ResponseEntity<ConvocatoriaBaremacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_CREATE_TASK + PATH_PARAMETER_ID, HttpMethod.POST,
        buildRequestBaremacion(null, null),
        ConvocatoriaBaremacion.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_from_json() throws Exception {

    String personaRef = "22932567";
    HttpStatus statusCode = baremacionLibroFromJson(personaRef);
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_2_autores_from_json() throws Exception {

    String produccionCientificaJson = "publicacion-libro.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    Long produccionCientificaIdNew = createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores,
        numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef1 = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef1, areaRef, areaRefRaiz);

    List<Autor> autores = getAutorRepository().findAllByProduccionCientificaId(produccionCientificaIdNew);
    String personaRef2 = "01889311";
    areaRef = "020";
    areaRefRaiz = "S";
    updateAutorPersonaRef(autores.get(2).getId(), personaRef2, 3);
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef2, areaRef, areaRefRaiz);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionLibroAutores2(idBaremacion, personaRef1, personaRef2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_from_json_repeat() throws Exception {

    String produccionCientificaJson = "publicacion-libro.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionLibro(idBaremacion, personaRef);

    final ResponseEntity<Void> response2 = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionLibro(idBaremacion, personaRef);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_articulo_from_json() throws Exception {

    String produccionCientificaJson = "publicacion-articulo.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionArticulo(idBaremacion, personaRef);
  }

  private void checkPuntuacionArticulo(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(9);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(109L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("109.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(134L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.30"));

    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(135L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("1.30"));

    Assertions.assertThat(puntuacionBaremoItems.get(3).getBaremoId()).as("BaremoId").isEqualTo(136L);
    Assertions.assertThat(puntuacionBaremoItems.get(3).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("136.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(4).getBaremoId()).as("BaremoId").isEqualTo(137L);
    Assertions.assertThat(puntuacionBaremoItems.get(4).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("137.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(5).getBaremoId()).as("BaremoId").isEqualTo(138L);
    Assertions.assertThat(puntuacionBaremoItems.get(5).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("138.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(6).getBaremoId()).as("BaremoId").isEqualTo(139L);
    Assertions.assertThat(puntuacionBaremoItems.get(6).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("139.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(7).getBaremoId()).as("BaremoId").isEqualTo(140L);
    Assertions.assertThat(puntuacionBaremoItems.get(7).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("140.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(8).getBaremoId()).as("BaremoId").isEqualTo(145L);
    Assertions.assertThat(puntuacionBaremoItems.get(8).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("145.00"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("580.46"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("348.28"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("348.28"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("323.02"));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_2020_2021_from_json() throws Exception {

    String produccionCientificaJson = "publicacion-libro.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    produccionCientificaJson = "publicacion-libro-2020.json";

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionLibro20202021(idBaremacion, personaRef);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_2020_2021_convocatoria_duplicated_from_json() throws Exception {

    String produccionCientificaJson = "publicacion-libro.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    produccionCientificaJson = "publicacion-libro-2020.json";

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionLibro20202021(idBaremacion, personaRef);

    idBaremacion = getConvocatoriaBaremacionService().clone(idBaremacion, "Clonada - ", 0).getId();

    final ResponseEntity<Void> response2 = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST,
        buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionLibro20202021(idBaremacion, personaRef);
  }

  private void checkPuntuacionLibro20202021(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository()
        .findByBaremoConvocatoriaBaremacionId(idBaremacion);

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    // checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6.00"));
    // checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new
    // BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAllByProduccionCientificaConvocatoriaBaremacionId(idBaremacion);

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getAnio()).as("Anio").isEqualTo(2020);

    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getAnio()).as("Anio").isEqualTo(2021);

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAllByPuntuacionGrupoConvocatoriaBaremacionId(idBaremacion);

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14"));
    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findByConvocatoriaBaremacionId(idBaremacion);

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("8.28"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("13586.96"));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_articulo_from_json() throws Exception {

    String produccionCientificaJson = "publicacion-articulo.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    produccionCientificaJson = "publicacion-libro.json";

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_articulo_2_autores_from_json() throws Exception {

    String produccionCientificaJson = "publicacion-articulo.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    Long produccionCientificaIdNew = createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores,
        numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef1 = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef1, areaRef, areaRefRaiz);

    List<Autor> autores = getAutorRepository().findAllByProduccionCientificaId(produccionCientificaIdNew);
    String personaRef2 = "01889311";
    areaRef = "020";
    areaRefRaiz = "S";
    updateAutorPersonaRef(autores.get(2).getId(), personaRef2, 3);
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef2, areaRef, areaRefRaiz);

    produccionCientificaJson = "publicacion-libro.json";

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionLibroAndArticuloAutores2(idBaremacion, personaRef1, personaRef2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_articulo_from_json_repeat() throws Exception {

    String produccionCientificaJson = "publicacion-articulo.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    produccionCientificaJson = "publicacion-libro.json";

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionLibroAndArticulo(idBaremacion, personaRef);

    final ResponseEntity<Void> response2 = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionLibroAndArticulo(idBaremacion, personaRef);
  }

  private void checkPuntuacionLibroAutores2(Long idBaremacion, String personaRef1, String personaRef2) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);
    int i = 0;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAllByProduccionCientificaConvocatoriaBaremacionId(idBaremacion);

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(2);

    // Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
    // .isEqualTo(personaRef1);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    // Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef")
    // .isEqualTo(personaRef2);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("4.14"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("27173.91"));
  }

  private void checkPuntuacionLibroAndArticuloAutores2(Long idBaremacion, String personaRef1, String personaRef2) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(11);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 109L, new BigDecimal("109.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 134L, new BigDecimal("2.30"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 135L, new BigDecimal("1.30"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 136L, new BigDecimal("136.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 137L, new BigDecimal("137.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 138L, new BigDecimal("138.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 139L, new BigDecimal("139.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 140L, new BigDecimal("140.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 145L, new BigDecimal("145.00"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAllByProduccionCientificaConvocatoriaBaremacionId(idBaremacion);

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(3);

    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef1, new BigDecimal("6.90"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef1, new BigDecimal("386.97"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef2, new BigDecimal("386.97"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("232.18"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("232.18"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("484.54"));
  }

  private void checkPuntuacionLibroAndArticulo(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(11);
    int i = 0;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.30"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(109L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("109.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(134L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.30"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(135L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("1.30"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(136L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("136.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(137L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("137.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(138L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("138.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(139L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("139.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(140L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("140.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId").isEqualTo(145L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("145.00"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("580.46"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14"));

    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("348.28"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("352.42"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("319.22"));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_congreso_from_json() throws Exception {

    String produccionCientificaJson = "congreso.json";
    Integer numCampos = 11;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionCongreso(idBaremacion, personaRef);
  }

  private void checkPuntuacionCongreso(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(3);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(301L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("301.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(309L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("309.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(311L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("311.00"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("460.50"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("276.30"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("276.30"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("407.17"));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_comite_editorial_from_json() throws Exception {

    String produccionCientificaJson = "comite-editorial.json";
    Integer numCampos = 4;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionComiteEditorial(idBaremacion, personaRef);
  }

  private void checkPuntuacionComiteEditorial(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(1);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(201L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("201.00"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("67.00"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("40.20"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("40.20"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("2798.51"));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_direccion_tesis_from_json() throws Exception {

    String produccionCientificaJson = "direccion-tesis.json";
    Integer numCampos = 9;
    Integer numAutores = 2;
    Integer numIndicesImpacto = 0;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionDireccionTesis(idBaremacion, personaRef);
  }

  private void checkPuntuacionDireccionTesis(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(401L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("401.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(403L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("403.00"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("402.00"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("241.20"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("241.20"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("466.42"));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_obra_artistica_from_json() throws Exception {

    String produccionCientificaJson = "obra-artistica.json";
    Integer numCampos = 10;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 0;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionObraArtistica(idBaremacion, personaRef);
  }

  private void checkPuntuacionObraArtistica(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(1);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(503L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("503.00"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("251.50"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("150.90"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("150.90"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("745.53"));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_organizacion_actividad_from_json() throws Exception {

    String produccionCientificaJson = "organizacion-actividad.json";
    Integer numCampos = 5;
    Integer numAutores = 1;
    Integer numIndicesImpacto = 0;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionOrganizacionActividad(idBaremacion, personaRef);
  }

  private void checkPuntuacionOrganizacionActividad(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(1);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(601L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("601.00"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("601.00"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("360.60"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("360.60"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("311.98"));
  }

}
