package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.BaremacionController;
import org.crue.hercules.sgi.prc.dto.csp.ProyectoDto;
import org.crue.hercules.sgi.prc.dto.csp.ProyectoEquipoDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.service.BaremacionProyectoService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.StringUtils;

/**
 * Test de integracion de Baremacion de proyectos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionProyectoIT extends BaremacionBaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = BaremacionController.MAPPING;

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
  void baremacion_libro_and_proyecto_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = null;
    BigDecimal importeConcedidoCostesIndirectos = null;

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.FALSE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto), Arrays.asList(proyectoEquipo), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, null);

    checkPuntuacionLibroWithoutProyecto(idBaremacion, personaRef);
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
  void baremacion_libro_and_contrato_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = new BigDecimal("100.00");
    BigDecimal importeConcedidoCostesIndirectos = new BigDecimal("50.00");

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.TRUE)

        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.FALSE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto), Arrays.asList(proyectoEquipo), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, null);

    checkPuntuacionLibroContratoWithoutProyecto(idBaremacion, personaRef);
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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_proyecto2_2021_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = new BigDecimal("100.00");
    BigDecimal importeConcedidoCostesIndirectos = new BigDecimal("50.00");

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.FALSE)
        .build();

    BigDecimal totalImporteConcedido2 = new BigDecimal("110.00");
    BigDecimal importeConcedidoCostesIndirectos2 = new BigDecimal("51.00");
    proyectoId = 2L;
    ProyectoDto proyecto2 = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo2")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido2)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos2)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto, proyecto2), Arrays.asList(proyectoEquipo), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, "OTHERS");
    checkCopyProyectoAutor(proyecto2, Arrays.asList(proyectoEquipo), totalImporteConcedido2,
        importeConcedidoCostesIndirectos2,
        "OTHERS");

    checkPuntuacionLibroProyecto2(idBaremacion, personaRef);
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
  void baremacion_libro_and_contrato2_2021_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = new BigDecimal("100.00");
    BigDecimal importeConcedidoCostesIndirectos = new BigDecimal("50.00");

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.TRUE)

        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.FALSE)
        .build();

    BigDecimal totalImporteConcedido2 = new BigDecimal("110000.00");
    BigDecimal importeConcedidoCostesIndirectos2 = new BigDecimal("51.00");
    proyectoId = 2L;
    ProyectoDto proyecto2 = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo2")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.TRUE)

        .totalImporteConcedido(totalImporteConcedido2)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos2)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto, proyecto2), Arrays.asList(proyectoEquipo), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, null);
    checkCopyProyectoAutor(proyecto2, Arrays.asList(proyectoEquipo), totalImporteConcedido2,
        importeConcedidoCostesIndirectos2, null);

    checkPuntuacionLibroContrato2(idBaremacion, personaRef);
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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_proyecto2_2020_2021_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2020;

    BigDecimal totalImporteConcedido = new BigDecimal("100.00");
    BigDecimal importeConcedidoCostesIndirectos = new BigDecimal("50.00");

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.FALSE)
        .build();

    Integer anio2 = 2021;
    BigDecimal totalImporteConcedido2 = new BigDecimal("110.00");
    BigDecimal importeConcedidoCostesIndirectos2 = new BigDecimal("51.00");
    proyectoId = 2L;
    ProyectoDto proyecto2 = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo2")
        .fechaInicio(Instant.parse(anio2 + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio2 + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio2 + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido2)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos2)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto, proyecto2), Arrays.asList(proyectoEquipo), anio, anio2);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, "OTHERS");
    checkCopyProyectoAutor(proyecto2, Arrays.asList(proyectoEquipo), totalImporteConcedido2,
        importeConcedidoCostesIndirectos2,
        "OTHERS");

    checkPuntuacionLibroProyecto2(idBaremacion, personaRef);
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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_proyecto_and_contrato_2020_2021_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2020;

    BigDecimal totalImporteConcedido = new BigDecimal("100.00");
    BigDecimal importeConcedidoCostesIndirectos = new BigDecimal("50.00");

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.FALSE)
        .build();

    Integer anio2 = 2021;
    BigDecimal totalImporteConcedido2 = new BigDecimal("110.00");
    BigDecimal importeConcedidoCostesIndirectos2 = new BigDecimal("51.00");
    proyectoId = 2L;
    ProyectoDto proyecto2 = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo2")
        .fechaInicio(Instant.parse(anio2 + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio2 + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio2 + "-12-31T00:00:00Z"))
        .contrato(Boolean.TRUE)

        .totalImporteConcedido(totalImporteConcedido2)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos2)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto, proyecto2), Arrays.asList(proyectoEquipo), anio, anio2);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, "OTHERS");
    checkCopyProyectoAutor(proyecto2, Arrays.asList(proyectoEquipo), totalImporteConcedido2,
        importeConcedidoCostesIndirectos2,
        "OTHERS");

    checkPuntuacionLibroContratoAndProyecto(idBaremacion, personaRef);
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
  void baremacion_libro_and_proyecto_without_fecha_fin_definitiva_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = null;
    BigDecimal importeConcedidoCostesIndirectos = null;

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(null)
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.FALSE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto), Arrays.asList(proyectoEquipo), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, null);

    checkPuntuacionLibroWithoutProyecto(idBaremacion, personaRef);
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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_proyecto_with_importes_mock_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(null)
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(null)
        .importeConcedidoCostesIndirectos(null)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.FALSE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto), Arrays.asList(proyectoEquipo), 2020, 2021);

    BigDecimal totalImporteConcedido = new BigDecimal("10.00");
    BigDecimal importeConcedidoCostesIndirectos = new BigDecimal("5.00");
    BDDMockito.given(getSgiApiCspService().getTotalImporteConcedidoAnualidadGasto(proyectoId)).willReturn(
        totalImporteConcedido);
    BDDMockito.given(getSgiApiCspService().getTotalImporteConcedidoAnualidadGastoCostesIndirectos(proyectoId))
        .willReturn(
            importeConcedidoCostesIndirectos);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, "OTHERS");

    checkPuntuacionLibroOneProyecto(idBaremacion, personaRef);
  }

  protected void mockProyectosAndEquipo(List<ProyectoDto> proyectos, List<ProyectoEquipoDto> equipo, Integer anioInicio,
      Integer anioFin) {

    BDDMockito.given(getSgiApiCspService().findProyectosProduccionCientifica(anioInicio, anioFin))
        .willReturn(proyectos);

    proyectos.stream()
        .forEach(proyectoEquipo -> BDDMockito
            .given(getSgiApiCspService().findProyectoEquipoByProyectoId(proyectoEquipo.getId()))
            .willReturn(equipo));
  }

  private void checkCopyProyectoAutor(ProyectoDto proyecto, List<ProyectoEquipoDto> equipo,
      BigDecimal totalImporteConcedido, BigDecimal importeConcedidoCostesIndirectos, String ambitoMapeoTipos) {

    Long produccionCientificaId = getProduccionCientificaRepository()
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(
            BaremacionProyectoService.PREFIX_PROYECTOS + proyecto.getId())
        .get().getId();

    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);

    Integer numCampos = !isContrato(proyecto)
        ? (StringUtils.hasText(ambitoMapeoTipos) ? 7 : 6)
        : 5;
    numCampos = null == importeConcedidoCostesIndirectos ? numCampos - 1 : numCampos;
    numCampos = null == totalImporteConcedido ? numCampos - 1 : numCampos;
    int numCamposReal = campos.size();
    Assertions.assertThat(numCamposReal).as("number of campos created").isEqualTo(numCampos);

    CodigoCVN codigoCVN = !isContrato(proyecto)
        ? CodigoCVN.E050_020_010_010
        : CodigoCVN.E050_020_020_010;
    ValorCampo valorCampo = getValorCampoByCodigoCVN(campos, codigoCVN);
    Assertions.assertThat(valorCampo.getValor()).as(codigoCVN.name()).isEqualTo(proyecto.getTitulo());

    codigoCVN = !isContrato(proyecto)
        ? CodigoCVN.E050_020_010_270
        : CodigoCVN.E050_020_020_180;
    valorCampo = getValorCampoByCodigoCVN(campos, codigoCVN);
    Assertions.assertThat(valorCampo.getValor()).as(codigoCVN.name()).isEqualTo(
        proyecto.getFechaInicio().toString());

    Instant fechaFin = ObjectUtils.defaultIfNull(proyecto.getFechaFinDefinitiva(), proyecto.getFechaFin());
    codigoCVN = !isContrato(proyecto)
        ? CodigoCVN.E050_020_010_410
        : CodigoCVN.FECHA_FIN_CONTRATO;
    valorCampo = getValorCampoByCodigoCVN(campos, codigoCVN);
    Assertions.assertThat(valorCampo.getValor()).as(codigoCVN.name()).isEqualTo(fechaFin.toString());

    if (null != totalImporteConcedido) {

      codigoCVN = !isContrato(proyecto)
          ? CodigoCVN.E050_020_010_290
          : CodigoCVN.E050_020_020_200;
      valorCampo = getValorCampoByCodigoCVN(campos, codigoCVN);
      totalImporteConcedido = null != proyecto.getTotalImporteConcedido() ? proyecto.getTotalImporteConcedido()
          : totalImporteConcedido;
      String valorTotalImporteConcedido = ProduccionCientificaFieldFormatUtil
          .formatNumber(totalImporteConcedido.toString());
      Assertions.assertThat(valorCampo.getValor()).as(codigoCVN.name()).isEqualTo(valorTotalImporteConcedido);
    }

    if (!isContrato(proyecto)) {
      String excelencia = Boolean.TRUE
          .equals(ObjectUtils.defaultIfNull(proyecto.getConvocatoriaExcelencia(), Boolean.FALSE)) ? "true" : "false";
      valorCampo = getValorCampoByCodigoCVN(campos, CodigoCVN.CONVOCATORIA_EXCELENCIA);
      Assertions.assertThat(valorCampo.getValor()).as("CONVOCATORIA_EXCELENCIA").isEqualTo(excelencia);

      if (StringUtils.hasText(ambitoMapeoTipos)) {
        valorCampo = getValorCampoByCodigoCVN(campos, CodigoCVN.E050_020_010_040);
        Assertions.assertThat(valorCampo.getValor()).as("E050_020_010_040").isEqualTo(ambitoMapeoTipos);
      }
    }

    if (null != importeConcedidoCostesIndirectos) {
      codigoCVN = !isContrato(proyecto)
          ? CodigoCVN.CUANTIA_COSTES_INDIRECTOS_PROYECTO
          : CodigoCVN.CUANTIA_COSTES_INDIRECTOS_CONTRATO;
      valorCampo = getValorCampoByCodigoCVN(campos, codigoCVN);
      importeConcedidoCostesIndirectos = null != proyecto.getImporteConcedidoCostesIndirectos()
          ? proyecto.getImporteConcedidoCostesIndirectos()
          : importeConcedidoCostesIndirectos;
      String valorImporteConcedido = ProduccionCientificaFieldFormatUtil
          .formatNumber(importeConcedidoCostesIndirectos.toString());
      Assertions.assertThat(valorCampo.getValor()).as(codigoCVN.name()).isEqualTo(valorImporteConcedido);
    }

    List<Autor> autores = getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId);
    int numAutoresReal = autores.size();
    Assertions.assertThat(numAutoresReal).as("number of autores created").isEqualTo(equipo.size());
    equipo.forEach(proyectoEquipo -> {
      boolean personaFound = autores.stream().anyMatch(autor -> autor.getPersonaRef().equals(
          proyectoEquipo.getPersonaRef()));
      Assertions.assertThat(personaFound).as("PersonaRefInAutores").isTrue();
    });

  }

  private boolean isContrato(ProyectoDto proyecto) {
    return null != proyecto.getContrato() && proyecto.getContrato().equals(Boolean.TRUE);
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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_proyecto_with_mapeoTipos_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = null;
    BigDecimal importeConcedidoCostesIndirectos = null;

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(4L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.FALSE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto), Arrays.asList(proyectoEquipo), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, "010");

    checkPuntuacionLibroAndOneProyectoOneAutorWithExtra(idBaremacion, personaRef);
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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_proyecto_with_mapeoTipos_and_ip_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = null;
    BigDecimal importeConcedidoCostesIndirectos = null;

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(4L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.TRUE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto), Arrays.asList(proyectoEquipo), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, "010");

    checkPuntuacionLibroAndOneProyectoOneAutorIPWithExtra(idBaremacion, personaRef);
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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_proyecto_with_mapeoTipos_and_ip2_from_json() throws Exception {

    String produccionCientificaJson = "publicacion-libro.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String personaRef2 = "22932568";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef2, areaRef, areaRefRaiz);
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = null;
    BigDecimal importeConcedidoCostesIndirectos = null;

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(4L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo2 = ProyectoEquipoDto.builder()
        .id(2L)
        .personaRef(personaRef2)
        .ip(Boolean.TRUE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto), Arrays.asList(proyectoEquipo, proyectoEquipo2), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo, proyectoEquipo2), totalImporteConcedido,
        importeConcedidoCostesIndirectos, "010");

    checkPuntuacionLibroAndOneProyectoTwoAutorsIPWithExtra(idBaremacion, personaRef, personaRef2);
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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_proyecto_with_mapeoTipos_and_ip2sames_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = null;
    BigDecimal importeConcedidoCostesIndirectos = null;

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(4L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo2 = ProyectoEquipoDto.builder()
        .id(2L)
        .personaRef(personaRef)
        .ip(Boolean.TRUE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto), Arrays.asList(proyectoEquipo, proyectoEquipo2), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_proyecto_without_autor_in_fecha_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = null;
    BigDecimal importeConcedidoCostesIndirectos = null;

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse(anio + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(4L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .fechaFin(Instant.parse((anio - 12) + "-12-31T00:00:00Z"))
        .ip(Boolean.FALSE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto), Arrays.asList(proyectoEquipo), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, "010");

    checkPuntuacionLibroAndOneProyectoWithoutAutorInFechaWithExtra(idBaremacion, personaRef);
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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_proyecto_in_2020_2021_with_mapeoTipos_from_json() throws Exception {

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

    Long proyectoId = 1L;
    Integer anio = 2021;

    BigDecimal totalImporteConcedido = null;
    BigDecimal importeConcedidoCostesIndirectos = null;

    ProyectoDto proyecto = ProyectoDto.builder()
        .id(proyectoId)
        .titulo("titulo")
        .fechaInicio(Instant.parse((anio - 1) + "-01-15T00:00:00Z"))
        .fechaFin(Instant.parse(anio + "-12-31T00:00:00Z"))
        .fechaFinDefinitiva(Instant.parse(anio + "-12-31T00:00:00Z"))
        .contrato(Boolean.FALSE)
        .totalImporteConcedido(totalImporteConcedido)
        .importeConcedidoCostesIndirectos(importeConcedidoCostesIndirectos)
        .ambitoGeograficoId(1L)
        .convocatoriaExcelencia(Boolean.TRUE)
        .build();

    ProyectoEquipoDto proyectoEquipo = ProyectoEquipoDto.builder()
        .id(1L)
        .personaRef(personaRef)
        .ip(Boolean.FALSE)
        .build();

    mockProyectosAndEquipo(Arrays.asList(proyecto), Arrays.asList(proyectoEquipo), 2020, 2021);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyProyectoAutor(proyecto, Arrays.asList(proyectoEquipo), totalImporteConcedido,
        importeConcedidoCostesIndirectos, "OTHERS");

    checkPuntuacionLibroAndProyecto20202021OneAutor(idBaremacion, personaRef);
  }

  private void checkPuntuacionLibroAndProyecto20202021OneAutor(Long idBaremacion, String personaRef) {
    List<ProduccionCientifica> prcItems = getProduccionCientificaRepository().findAll();

    int numPrcItems = prcItems.size();
    Assertions.assertThat(numPrcItems).as("numPrcItems").isEqualTo(4);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(424L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("424.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(424L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("424.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(3).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(3).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(3);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("424.00"));
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getAnio()).as("Anio").isEqualTo(2020);

    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("424.00"));
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getAnio()).as("Anio").isEqualTo(2021);

    Assertions.assertThat(puntuacionItemsInvestigador.get(2).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(2).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));
    Assertions.assertThat(puntuacionItemsInvestigador.get(2).getAnio()).as("Anio").isEqualTo(2021);

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(3);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("254.40"));

    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("254.40"));

    Assertions.assertThat(puntuacionGrupoInvestigador.get(2).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("512.94"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenio")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("219.32"));
  }

  private void checkPuntuacionLibroAndOneProyectoOneAutorWithExtra(Long idBaremacion, String personaRef) {
    List<ProduccionCientifica> prcItems = getProduccionCientificaRepository().findAll();

    int numPrcItems = prcItems.size();
    Assertions.assertThat(numPrcItems).as("numPrcItems").isEqualTo(4);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(422L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("422.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(426L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("426.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(3).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(3).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("848.00"));

    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("508.80"));

    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("512.94"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenio")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("219.32"));
  }

  private void checkPuntuacionLibroAndOneProyectoOneAutorIPWithExtra(Long idBaremacion, String personaRef) {
    List<ProduccionCientifica> prcItems = getProduccionCientificaRepository().findAll();

    int numPrcItems = prcItems.size();
    Assertions.assertThat(numPrcItems).as("numPrcItems").isEqualTo(4);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(5);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(422L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("422.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(426L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("426.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(428L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("428.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(3).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(3).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(4).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(4).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("1276.00"));

    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("765.60"));

    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("769.74"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenio")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("146.15"));
  }

  private void checkPuntuacionLibroAndOneProyectoTwoAutorsIPWithExtra(Long idBaremacion, String personaRef,
      String personaRef2) {
    List<ProduccionCientifica> prcItems = getProduccionCientificaRepository().findAll();

    int numPrcItems = prcItems.size();
    Assertions.assertThat(numPrcItems).as("numPrcItems").isEqualTo(4);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(5);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(422L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("422.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(426L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("426.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(428L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("856.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(3).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(3).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(4).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(4).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(3);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("852.00"));
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef2);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("852.00"));

    Assertions.assertThat(puntuacionItemsInvestigador.get(2).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(2).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("511.20"));

    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("515.34"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenio")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("218.30"));
  }

  private void checkPuntuacionLibroAndOneProyectoWithoutAutorInFechaWithExtra(Long idBaremacion, String personaRef) {
    List<ProduccionCientifica> prcItems = getProduccionCientificaRepository().findAll();

    int numPrcItems = prcItems.size();
    Assertions.assertThat(numPrcItems).as("numPrcItems").isEqualTo(4);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(422L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("422.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(426L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("426.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(3).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(3).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
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

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenio")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("27173.91"));
  }

  private void checkPuntuacionLibroContratoWithoutProyecto(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 451L, new BigDecimal("10.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 2L, new BigDecimal("0.05"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(3);

    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("10.00"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("0.05"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(3);

    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("6.00"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("0.03"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.03"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("10.14"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("750000.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("11094.67"));
  }

  private void checkPuntuacionLibroOneProyecto(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 424L, new BigDecimal("424.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 2L, new BigDecimal("0.01"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(3);

    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("424.00"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("0.01"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(3);

    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("254.40"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("0.01"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.01"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("258.54"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos").isEqualTo(new BigDecimal("2250000.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("435.14"));
  }

  private void checkPuntuacionLibroContratoAndProyecto(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(6);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 451L, new BigDecimal("10.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 424L, new BigDecimal("424.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 2L, new BigDecimal("0.05"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(5);

    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("424.00"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("10.00"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("0.05"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(5);

    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("254.40"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("6.00"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("0.03"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.06"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("264.54"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos").isEqualTo(new BigDecimal("375000.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("425.27"));
  }

  private void checkPuntuacionLibroProyecto2(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(6);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 424L, new BigDecimal("424.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 2L, new BigDecimal("0.05"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(5);

    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("424.00"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("0.05"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(5);

    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("254.40"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("0.03"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.06"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("512.94"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos").isEqualTo(new BigDecimal("375000.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("219.32"));
  }

  private void checkPuntuacionLibroContrato2(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(6);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 451L, new BigDecimal("10.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 451L, new BigDecimal("20.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 2L, new BigDecimal("0.05"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(5);

    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("10.00"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("20.00"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("0.05"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(5);

    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("6.00"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("12.00"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("0.03"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.06"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("22.14"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos").isEqualTo(new BigDecimal("375000.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("5081.30"));
  }

  private void checkPuntuacionLibroWithoutProyecto(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
        .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
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
}
