package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de publicaciones libros.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionPublicacionLibroIT extends BaremacionBaseIT {

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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 1L, 3L })
  @ParameterizedTest
  void baremacion_libro_nacional_1(Long produccionCientificaId) throws Exception {
    Long idBaremacion = 1L;

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(1);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 1L, 2L, 3L })
  @ParameterizedTest
  void baremacion_libro_without_indice_impacto_anio(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setAnio(2100);
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithoutPuntuaciones(idBaremacion);
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 2L })
  @ParameterizedTest
  void baremacion_libro_without_indice_impacto_tipo_fuente(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    // Cambiamos el tipo de fuente para que no cumpla las condifiones
    updateTipoFuenteIndiceImpacto(produccionCientificaId, TipoFuenteImpacto.OTHERS);

    baremacionWithoutPuntuaciones(idBaremacion);
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 1L, 2L, 3L })
  @ParameterizedTest
  void baremacion_libro_without_tipo_produccion(Long produccionCientificaId) throws Exception {
    Long idBaremacion = 1L;

    // Cambiamos el valor de tipo produccion para que no cumpla las condiciones
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "xxx");

    baremacionWithoutPuntuaciones(idBaremacion);
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_nacional_and_extranjera() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    // AUTORIA_BCI_EDITORIAL_EXTRANJERA
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();
    updateEstadoProduccionCientifica(2L, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(2L, CodigoCVN.E060_010_010_010, "032");

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    int i = 0;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId" + i).isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos" + i)
        .isEqualTo(new BigDecimal("6.00"));
    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId" + i).isEqualTo(5L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos" + i)
        .isEqualTo(new BigDecimal("5.00"));
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_nacional_and_extranjera_2020_and_2021() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);
    updateEstadoProduccionCientifica(2L, TipoEstadoProduccion.VALIDADO);
    updateEstadoProduccionCientifica(3L, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(2L, CodigoCVN.E060_010_010_010, "032");

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(3);

    int i = 0;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId" + i).isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos" + i)
        .isEqualTo(new BigDecimal("6.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId" + i).isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos" + i)
        .isEqualTo(new BigDecimal("6.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId" + i).isEqualTo(5L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos" + i)
        .isEqualTo(new BigDecimal("5.00"));
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'032', 5, '5.00'", // AUTORIA_BCI_EDITORIAL_EXTRANJERA
      "'004', 11, '11.00'", // CAP_LIBRO_BCI_EDITORIAL_EXTRANJERA
      "'208',17, '17.00'", // EDICION_BCI_EDITORIAL_EXTRANJERA
  // "'COMENTARIO_SISTEMATICO_NORMAS',23, '23.00'" //
  // COMENTARIO_BCI_EDITORIAL_EXTRANJERA
  })
  @ParameterizedTest
  void baremacion_libro_extranjera(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 2L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        value);

    baremacionWithOnePuntuacion(idBaremacion, baremoId, puntos);
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
  @CsvSource({ "'032',6, '6.00'", // AUTORIA_BCI_EDITORIAL_NACIONAL
      "'004', 12, '12.00'", // CAP_LIBRO_BCI_EDITORIAL_NACIONAL
      "'208',18, '18.00'", /// EDICION_BCI_EDITORIAL_NACIONAL
  // "'COMENTARIO_SISTEMATICO_NORMAS', 24, '24.00'", //
  // COMENTARIO_BCI_EDITORIAL_NACIONAL
  })
  @ParameterizedTest
  void baremacion_libro_nacional_2(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        value);

    baremacionWithOnePuntuacion(idBaremacion, baremoId, puntos);
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
  @ValueSource(longs = { 48L, 50L, 1L })
  @ParameterizedTest
  void baremacion_libro_autoria_icee_q1(Long posicionPublicacion) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    // AUTORIA_ICEE_Q1
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.ICEE);
      entity.setRevista25(Boolean.TRUE);
      getIndiceImpactoRepository().save(entity);
    });

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(1);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(7L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("7.00"));
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
  @CsvSource({ "'032',7,'7.00'", // AUTORIA_Q1_RESTO_CUARTILES
      "'004',13,'13.00'", // CAP_LIBRO_Q1_RESTO_CUARTILES
      "'208',19,'19.00'", // EDICION_Q1_RESTO_CUARTILES
      "'COMENTARIO_SISTEMATICO_NORMAS',25,'25.00'", // COMENTARIO_Q1_RESTO_CUARTILES
  })
  @ParameterizedTest
  void baremacion_libro_icee_q1(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        value);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.ICEE);
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("2"));
      entity = getIndiceImpactoRepository().save(entity);
      Assertions.assertThat(entity).as("indiceImpacto").isNotNull();
    });

    baremacionWithOnePuntuacion(idBaremacion, baremoId, puntos);
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
  @CsvSource({ "'032',8,'8.00'", // AUTORIA_ICEE_RESTO_CUARTILES
      "'004',14,'14.00'", // CAP_LIBRO_ICEE_RESTO_CUARTILES
      "'208',20,'20.00'", // EDICION_ICEE_RESTO_CUARTILES
      "'COMENTARIO_SISTEMATICO_NORMAS',26, '26.00'" // COMENTARIO_ICEE_RESTO_CUARTILES
  })
  @ParameterizedTest
  void baremacion_libro_icee_resto_cuartiles(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        value);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.ICEE);
      entity.setPosicionPublicacion(new BigDecimal("55.0"));
      entity.setNumeroRevistas(new BigDecimal("2"));
      entity = getIndiceImpactoRepository().save(entity);
      Assertions.assertThat(entity).as("indiceImpacto").isNotNull();
    });

    baremacionWithOnePuntuacion(idBaremacion, baremoId, puntos);
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
  @CsvSource({ "'032',9, '9.00'", // AUTORIA_DIALNET
      "'004',15,'15.00'", // CAP_LIBRO_DIALNET
      "'208',21,'21.00'", // EDICION_DIALNET
  // "'COMENTARIO_SISTEMATICO_NORMAS',27,'27.00'", // COMENTARIO_DIALNET
  })
  @ParameterizedTest
  void baremacion_libro_dialnet(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        value);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.DIALNET);
      getIndiceImpactoRepository().save(entity);
      Assertions.assertThat(entity).as("indiceImpacto").isNotNull();

    });

    baremacionWithOnePuntuacion(idBaremacion, baremoId, puntos);
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
  @CsvSource({ "'032' , 10, '10.00'", // AUTORIA_OTRAS
      "'004', 16, '16.00'", // CAP_LIBRO_OTRAS
      "'208', 22, '22.00'", // EDICION_OTRAS
  // "'COMENTARIO_SISTEMATICO_NORMAS', 28, '28.00'", // COMENTARIO_OTRAS
  })
  @ParameterizedTest
  void baremacion_libro_otras(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    // AUTORIA_ICEE_OTRAS
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        value);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.OTHERS);
      entity = getIndiceImpactoRepository().save(entity);
      Assertions.assertThat(entity).as("indiceImpacto").isNotNull();
    });

    baremacionWithOnePuntuacion(idBaremacion, baremoId, puntos);
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
  @ValueSource(longs = { 1L })
  @ParameterizedTest
  void baremacion_libro_modulador_numero_autores_areas_autor1(Long produccionCientificaId) throws Exception {
    Long idBaremacion = 1L;

    String personaRef = "52364567";
    String areaRef = "033";
    String areaRefRaiz = "H";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("1.10"));
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
  @ValueSource(longs = { 1L })
  @ParameterizedTest
  void baremacion_libro_modulador_numero_autores_areas_autor2(Long produccionCientificaId) throws Exception {
    Long idBaremacion = 1L;

    String personaRef = "52364567";
    String areaRef = null;
    String areaRefRaiz = null;
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    Long autorId = 3L;
    personaRef = "01889311";
    areaRef = "020";
    areaRefRaiz = "S";
    updateAutorPersonaRef(autorId, personaRef, 3);
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.40"));
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
  @ValueSource(longs = { 1L })
  @ParameterizedTest
  void baremacion_libro_modulador_numero_autores_areas_autor_correspondencia(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    String personaRef = "52364567";

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_390,
        personaRef);

    String areaRef = "020";
    String areaRefRaiz = "S";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("1.40"));
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
      "classpath:scripts/tabla_editorial.sql",
      "classpath:scripts/editorial_prestigio.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 1L })
  @ParameterizedTest
  void baremacion_libro_modulador_numero_autores_areas_autor1_editorial_prestigio(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    String personaRef = "52364567";
    String areaRef = "033";
    String areaRefRaiz = "H";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(3);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("1.10"));

    // Editorial prestigio
    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(30L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("30.00"));
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
      "classpath:scripts/tabla_editorial.sql",
      "classpath:scripts/editorial_prestigio.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 1L })
  @ParameterizedTest
  void baremacion_libro_modulador_numero_autores_areas_autor2_editorial_prestigio(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    String personaRef = "52364567";
    String areaRef = null;
    String areaRefRaiz = null;
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    Long autorId = 3L;
    personaRef = "01889311";
    areaRef = "020";
    areaRefRaiz = "S";
    updateAutorPersonaRef(autorId, personaRef, 3);
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    updateEditorialPrestigio(4L, "EdítOríal S modifiCado");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_100,
        "EdiTóriál S modíficädo");

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(3);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.40"));

    // Editorial prestigio
    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(30L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("30.00"));
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
      "classpath:scripts/tabla_editorial.sql",
      "classpath:scripts/editorial_prestigio.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 1L })
  @ParameterizedTest
  void baremacion_libro_modulador_numero_autores_areas_autor2_editorial_prestigio_ko(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    String personaRef = "52364567";
    String areaRef = null;
    String areaRefRaiz = null;
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    Long autorId = 3L;
    personaRef = "01889311";
    areaRef = "020";
    areaRefRaiz = "S";
    updateAutorPersonaRef(autorId, personaRef, 3);
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    updateEditorialPrestigio(4L, "EdítOríal S modifiCado");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_100,
        "EdiTóriál S XXXX");

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.40"));

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
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 1L })
  @ParameterizedTest
  void baremacion_libro_modulador_numero_autores_areas_autor2_baremacion_completa(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    String personaRef = "52364567";
    String areaRef = "033";
    String areaRefRaiz = "H";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    Long autorId = 3L;
    personaRef = "01889311";
    areaRef = "020";
    areaRefRaiz = "S";
    updateAutorPersonaRef(autorId, personaRef, 3);
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(29L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.10"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(2);

    // Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef")
    // .isEqualTo("52364567");
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.30"));

    // Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef")
    // .isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.30"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("3.78"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("3.78"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("29761.90"));

  }

}
