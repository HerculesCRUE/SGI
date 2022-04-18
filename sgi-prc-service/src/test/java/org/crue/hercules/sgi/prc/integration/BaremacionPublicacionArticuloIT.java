package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.model.TipoFuenteImpactoCuartil.Cuartil;
import org.crue.hercules.sgi.prc.repository.TipoFuenteImpactoCuartilRepository;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de publicaciones artículos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionPublicacionArticuloIT extends BaremacionBaseIT {

  @Autowired
  private TipoFuenteImpactoCuartilRepository tipoFuenteImpactoCuartilRepository;

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
  @CsvSource({ "'WOS_JCR',101, '101.00'", // ARTICULO_JCR_Q1
      "'SCOPUS_SJR',109,'109.00'", // ARTICULO_SCOPUS_Q1
      "'SCIMAGO',113, '113.00'", // ARTICULO_SCIMAGO_Q1
      "'DIALNET',121, '121.00'", // ARTICULO_DIALNET_Q1
      "'MIAR', 125, '125.00'", // ARTICULO_MIARARTICULO_MIAR_Q1
      "'FECYT', 129, '129.00'", // ARTICULO_FEC ARTICULO_FECYT_Q1
  })
  @ParameterizedTest
  void baremacion_articulo_q1(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("2"));
      getIndiceImpactoRepository().save(entity);
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({
      "'WOS_JCR',102,'102.00'", // ARTICULO_JCR_Q2
      "'SCOPUS_SJR',110,'110.00'", // ARTICULO_SCOPUS_Q2
      "'SCIMAGO',114,'114.00'", // ARTICULO_SCIMAGO"'ARTICULO_SCIMAGO_Q2
      "'DIALNET',122,'122.00'", // ARTICULO_DIALNET' ARTICULO_DIALNET_Q2
      "'MIAR', 126, '126.00'", // ARTICULO_MIAR_Q2
      "'FECYT', 130, '130.00'", // ARTICULO_FECYT_Q2
  })
  @ParameterizedTest
  void baremacion_articulo_q2(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(100));
      entity.setNumeroRevistas(new BigDecimal("2"));
      getIndiceImpactoRepository().save(entity);
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'WOS_JCR',103, '103.00'", // ARTICULO_JCR_Q3
      "'SCOPUS_SJR',111, '111.00'", // ARTICULO_SCOPUS_Q3
      "'SCIMAGO',115, '115.00'", // ARTICULO_SCIMAGO_Q3
      "'DIALNET',123, '123.00'", // ARTICULO_DIALNET_Q3
      "'MIAR',127, '127.00'", // ARTICULO_MIAR_Q3
      "'FECYT', 131, '131.00'", // ARTICULO_FECYT_Q3
  })
  @ParameterizedTest
  void baremacion_articulo_q3(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(150));
      entity.setNumeroRevistas(new BigDecimal("2"));
      getIndiceImpactoRepository().save(entity);
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'WOS_JCR',104,'104.00'", // ARTICULO_JCR_Q4
      "'SCOPUS_SJR',112, '112.00'", // ARTICULO_SCOPUS_Q4
      "'SCIMAGO',116, '116.00'", // ARTICULO_SCIMAGO_Q4
      "'DIALNET',124, '124.00'", // ARTICULO_DIALNET_Q4
      "'MIAR',128, '128.00'", // ARTICULO_MIAR_Q4
      "'FECYT',132, '132.00'", // ARTICULO_FECYT_Q4
  })
  @ParameterizedTest
  void baremacion_articulo_q4(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(200));
      entity.setNumeroRevistas(new BigDecimal("2"));
      getIndiceImpactoRepository().save(entity);
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'OTHERS', 133, '133.00'" // ARTICULO
  })
  @ParameterizedTest
  void baremacion_articulo_otras(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      getIndiceImpactoRepository().save(entity);
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
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'CITEC', 105, '105.00'", // ARTICULO_CITEC_Q1
      "'ERIH', 117, '117.00'", // ARTICULO_ERIH_Q1
  })
  @ParameterizedTest
  void baremacion_articulo_q1_without_posicion(String value, Long baremoId, String puntos)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    tipoFuenteImpactoCuartilRepository.findByFuenteImpactoAndAnio(TipoFuenteImpacto.valueOf(value), 2021)
        .map(entity -> {
          entity.setCuartil(Cuartil.Q1);
          return tipoFuenteImpactoCuartilRepository.save(entity);
        }).orElse(null);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
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
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'CITEC', 106, '106.00'", // ARTICULO_CITEC_Q2
      "'ERIH', 118, '118.00'", // ARTICULO_ERIH_Q2
  })
  @ParameterizedTest
  void baremacion_articulo_q2_without_posicion(String value, Long baremoId, String puntos)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    tipoFuenteImpactoCuartilRepository.findByFuenteImpactoAndAnio(TipoFuenteImpacto.valueOf(value), 2021)
        .map(entity -> {
          entity.setCuartil(Cuartil.Q2);
          return tipoFuenteImpactoCuartilRepository.save(entity);
        }).orElse(null);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
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
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'CITEC', 107, '107.00'", // ARTICULO_CITEC_Q3
      "'ERIH',119, '119.00'", // ARTICULO_ERIH_Q3
  })
  @ParameterizedTest
  void baremacion_articulo_q3_without_posicion(String value, Long baremoId, String puntos)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    tipoFuenteImpactoCuartilRepository.findByFuenteImpactoAndAnio(TipoFuenteImpacto.valueOf(value), 2021)
        .map(entity -> {
          entity.setCuartil(Cuartil.Q3);
          return tipoFuenteImpactoCuartilRepository.save(entity);
        }).orElse(null);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
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
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'CITEC', 108, '108.00'", // ARTICULO_CITEC_Q4
      "'ERIH', 120, '120.00'", // ARTICULO_ERIH_Q4
  })
  @ParameterizedTest
  void baremacion_articulo_q4_without_posicion(String value, Long baremoId, String puntos)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    tipoFuenteImpactoCuartilRepository.findByFuenteImpactoAndAnio(TipoFuenteImpacto.valueOf(value), 2021)
        .map(entity -> {
          entity.setCuartil(Cuartil.Q4);
          return tipoFuenteImpactoCuartilRepository.save(entity);
        }).orElse(null);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
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
  void baremacion_articulo_modulador_extra_numero_autores_areas_autor1(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

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
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(133L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("133.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(134L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("1.10"));

    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(135L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("1.10"));

    // Extra Liderazgo
    Assertions.assertThat(puntuacionBaremoItems.get(3).getBaremoId()).as("BaremoId").isEqualTo(138L);
    Assertions.assertThat(puntuacionBaremoItems.get(3).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("138.00"));
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
  void baremacion_articulo_modulador_extra_numero_autores_areas_autor2(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

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
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(133L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("133.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(134L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.40"));

    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(135L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("2.40"));

    // Extra Liderazgo
    Assertions.assertThat(puntuacionBaremoItems.get(3).getBaremoId()).as("BaremoId").isEqualTo(138L);
    Assertions.assertThat(puntuacionBaremoItems.get(3).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("138.00"));
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
  void baremacion_articulo_modulador_extra_numero_autores_areas_autor_correspondencia(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    String personaRef = "52364567";

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

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
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(133L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("133.00"));

    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(134L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("1.40"));

    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(135L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("1.40"));

    // Extra Liderazgo
    Assertions.assertThat(puntuacionBaremoItems.get(3).getBaremoId()).as("BaremoId").isEqualTo(138L);
    Assertions.assertThat(puntuacionBaremoItems.get(3).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("138.00"));
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
  @CsvSource({ "'WOS_JCR',101, '101.00'", // ARTICULO_JCR_Q1
  })
  @ParameterizedTest
  void baremacion_articulo_extra_jcr_decil1(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(20));
      entity.setNumeroRevistas(new BigDecimal("2"));
      entity.setRevista25(Boolean.TRUE);
      getIndiceImpactoRepository().save(entity);
    });

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(baremoId);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal(puntos));

    // Extra JCR - Q1 - Decil1
    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(147L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("147.00"));
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
  @CsvSource({ "'WOS_JCR',101, '101.00', '0028-0836'", // ARTICULO_JCR_Q1 NATURE
      "'WOS_JCR',101, '101.00', '1476-4687'", // ARTICULO_JCR_Q1 NATURE
      "'WOS_JCR',101, '101.00', '1095-9203'", // ARTICULO_JCR_Q1 SCIENCE
  })
  @ParameterizedTest
  void baremacion_articulo_extra_nature_science(String value, Long baremoId, String puntos, String issn)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_160, issn);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("2"));
      entity.setRevista25(Boolean.FALSE);
      getIndiceImpactoRepository().save(entity);
    });

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(baremoId);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal(puntos));

    // Extra Nature o Science
    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(136L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("136.00"));
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
  @CsvSource({ "'WOS_JCR',101, '101.00', '1.1'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '1.100'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '01.100'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '10'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '10.1'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '100.1'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '100'", // ARTICULO_JCR_Q1
  })
  @ParameterizedTest
  void baremacion_articulo_extra_indice_normalizado(String value, Long baremoId, String puntos,
      String indiceNormalizado)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 2L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");
    indiceNormalizado = ProduccionCientificaFieldFormatUtil.formatNumber(indiceNormalizado);
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.INDICE_NORMALIZADO,
        indiceNormalizado);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("2"));
      entity.setRevista25(Boolean.FALSE);
      getIndiceImpactoRepository().save(entity);
    });

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(3);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(baremoId);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal(puntos));

    // Extra INDICE_NORMALIZADO
    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(137L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("137.00"));

    // Extra OPEN_ACCESS_ALL
    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(140L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("140.00"));
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
  @CsvSource({ "'WOS_JCR',101, '101.00', '1'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '1.0'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '1.00'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '01.00'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '0.1'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '0.10'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '00.100'", // ARTICULO_JCR_Q1
  })
  @ParameterizedTest
  void baremacion_articulo_extra_indice_normalizado_ko(String value, Long baremoId, String puntos,
      String indiceNormalizado)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 2L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.INDICE_NORMALIZADO,
        ProduccionCientificaFieldFormatUtil.formatNumber(indiceNormalizado));

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("2"));
      entity.setRevista25(Boolean.FALSE);
      getIndiceImpactoRepository().save(entity);
    });

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(baremoId);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal(puntos));

    // Extra OPEN_ACCESS_ALL
    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(140L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("140.00"));
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
  @CsvSource({ "'WOS_JCR',101, '101.00', 'PUBLICACION_MUY_RELEVANTE'", // ARTICULO_JCR_Q1
      "'WOS_JCR',101, '101.00', '060.010.010.300'", // ARTICULO_JCR_Q1
  })
  @ParameterizedTest
  void baremacion_articulo_extra_excelencia(String value, Long baremoId, String puntos, String codigoCVN)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 2L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.getByCode(codigoCVN),
        "true");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("2"));
      entity.setRevista25(Boolean.FALSE);
      getIndiceImpactoRepository().save(entity);
    });

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(3);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(baremoId);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal(puntos));

    // Extra PUBLICACION_MUY_RELEVANTE
    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(139L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("139.00"));

    // Extra OPEN_ACCESS_ALL
    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(140L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("140.00"));
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
  @CsvSource({ "'WOS_JCR',101, '101.00', 'ALL', 140, '140.00'", // TIPO_OPEN_ACCESS.ALL
      "'WOS_JCR',101, '101.00', 'GOLD', 141, '141.00'", // TIPO_OPEN_ACCESS.GOLD
      "'WOS_JCR',101, '101.00', 'HYBRID_GOLD', 142, '142.00'", // TIPO_OPEN_ACCESS.HYBRID_GOLD
      "'WOS_JCR',101, '101.00', 'BRONZE', 143, '143.00'", // TIPO_OPEN_ACCESS.BRONZE
      "'WOS_JCR',101, '101.00', 'GREEN', 144, '144.00'", // TIPO_OPEN_ACCESS.GREEN
  })
  @ParameterizedTest
  void baremacion_articulo_extra_open_access(String value, Long baremoId, String puntos,
      String tipoOpenAccess, Long baremoExtraId, String puntosExtra)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 2L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OPEN_ACCESS,
        tipoOpenAccess);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("2"));
      entity.setRevista25(Boolean.FALSE);
      getIndiceImpactoRepository().save(entity);
    });

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(baremoId);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal(puntos));

    // Extra TIPO_OPEN_ACCESS
    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(baremoExtraId);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal(puntosExtra));
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
  @CsvSource({ "'WOS_JCR',101, '101.00', 'INTERNACIONAL', 145, '145.00'", // INTERNACIONAL
      "'WOS_JCR',101, '101.00', 'INTERDISCIPLINAR', 146, '146.00'", // INTERDISCIPLINAR
  })
  @ParameterizedTest
  void baremacion_articulo_extra_internacionalizacion_multidisplinariedad(String value, Long baremoId,
      String puntos,
      String codigoCVN, Long baremoExtraId, String puntosExtra)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 2L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.getByCode(codigoCVN),
        "true");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OPEN_ACCESS, "XXX");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("2"));
      entity.setRevista25(Boolean.FALSE);
      getIndiceImpactoRepository().save(entity);
    });

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(2);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(baremoId);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal(puntos));

    // Extra
    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(baremoExtraId);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos").isEqualTo(new BigDecimal(puntosExtra));
  }

}
