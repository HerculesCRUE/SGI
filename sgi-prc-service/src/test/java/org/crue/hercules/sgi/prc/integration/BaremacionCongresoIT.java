package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoRanking;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de congresos
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionCongresoIT extends BaremacionBaseIT {

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
  @CsvSource({ "'GII_GRIN_SCIE','CLASE2',301", // CONGRESO_GRUPO1_O_CORE_A_POR
      "'CORE','CLASE1',301", // CONGRESO_GRUPO1_O_CORE_A_POR
      "'BCI','CLASE1',301", // CONGRESO_GRUPO1_O_CORE_A_POR
      "'ICEE','A_POR',301", // CONGRESO_GRUPO1_O_CORE_A_POR
  })
  @ParameterizedTest
  void baremacion_congreso_grupo1_core_a_por_ko_fuente_impacto_and_ranking(String fuenteImpacto, String tipoRanking,
      Long baremoId) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 400L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_010, "008");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_080, "XXX");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_050, "XXX");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(fuenteImpacto));
      entity.setRanking(TipoRanking.valueOf(tipoRanking));
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
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'GII_GRIN_SCIE','CLASE2',301", // CONGRESO_GRUPO1_O_CORE_A_POR
      "'CORE','CLASE1',301", // CONGRESO_GRUPO1_O_CORE_A_POR
      "'BCI','CLASE1',301", // CONGRESO_GRUPO1_O_CORE_A_POR
      "'ICEE','A_POR',301", // CONGRESO_GRUPO1_O_CORE_A_POR
  })
  @ParameterizedTest
  void baremacion_congreso_grupo1_core_a_por_ko_tipo(String fuenteImpacto, String tipoRanking, Long baremoId)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 400L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_010, "XXX");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_080, "XXX");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_050, "XXX");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(fuenteImpacto));
      entity.setRanking(TipoRanking.valueOf(tipoRanking));
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
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'GII_GRIN_SCIE','CLASE1',301,'301.00'", // CONGRESO_GRUPO1_O_CORE_A_POR
      "'CORE','A_POR',301,'301.00'", // CONGRESO_GRUPO1_O_CORE_A_POR
  })
  @ParameterizedTest
  void baremacion_congreso_grupo1_core_a_by_fuente_impacto_and_extra(String fuenteImpacto, String tipoRanking,
      Long baremoId,
      String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 400L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_010, "008");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_080, "XXX");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_050, "XXX");
    // Baremacion extra
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.RESUMEN_REVISTA, "true");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(fuenteImpacto));
      entity.setRanking(TipoRanking.valueOf(tipoRanking));
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
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal(puntos));

    // Baremación extra RESUMEN_REVISTA
    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(309L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("309.00"));

    // Baremación extra nacional
    Assertions.assertThat(puntuacionBaremoItems.get(2).getBaremoId()).as("BaremoId").isEqualTo(311L);
    Assertions.assertThat(puntuacionBaremoItems.get(2).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("311.00"));

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
  @CsvSource({ "'020','970',303,'303.00'", // CONGRESO_INTERNACIONAL_POSTER_O_CARTEL
      "'030','970',303,'303.00'", // CONGRESO_INTERNACIONAL_POSTER_O_CARTEL
      "'000','970',306,'306.00'", // CONGRESO_NACIONAL_POSTER_O_CARTEL
      "'010','970',306,'306.00'", // CONGRESO_NACIONAL_POSTER_O_CARTEL
      "'020','960',304,'304.00'", // CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'030','960',304,'304.00'", // CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'000','960',307,'307.00'", // CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'010','960',307,'307.00'", // CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'020','080',305,'305.00'", // CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'020','730',305,'305.00'", // CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'030','080',305,'305.00'", // CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'030','730',305,'305.00'", // CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'000','080',308,'308.00'", // CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'000','730',308,'308.00'", // CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'010','080',308,'308.00'", // CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA
      "'010','730',308,'308.00'", // CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA
  })
  @ParameterizedTest
  void baremacion_congreso_internacional_nacional_by_ambito_and_tipo_participacion(String fuenteImpacto,
      String tipoRanking, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 400L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_010, "008");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_080,
        fuenteImpacto);
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_050,
        tipoRanking);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.OTHERS);
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
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal(puntos));

    // Baremación extra nacional
    Assertions.assertThat(puntuacionBaremoItems.get(1).getBaremoId()).as("BaremoId").isEqualTo(311L);
    Assertions.assertThat(puntuacionBaremoItems.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("311.00"));
  }

}
