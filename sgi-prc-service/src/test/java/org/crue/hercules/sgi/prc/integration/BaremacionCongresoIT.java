package org.crue.hercules.sgi.prc.integration;

import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoRanking;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de congresos
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaremacionCongresoIT extends BaremacionBaseIT {

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "CONGRESO_GRUPO1_O_CORE_A_POR#GII_GRIN_SCIE#CLASE2#301",
      "CONGRESO_GRUPO1_O_CORE_A_POR#CORE#CLASE1#301",
      "CONGRESO_GRUPO1_O_CORE_A_POR#BCI#CLASE1#301",
      "CONGRESO_GRUPO1_O_CORE_A_POR#ICEE#A_POR#301",
  })
  @ParameterizedTest
  public void baremacion_congreso_grupo1_core_a_por_ko_fuente_impacto_and_ranking(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 400L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_010, "008");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_080, "XXX");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_050, "XXX");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setRanking(TipoRanking.valueOf(arrParams[2]));
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "CONGRESO_GRUPO1_O_CORE_A_POR#GII_GRIN_SCIE#CLASE2#301",
      "CONGRESO_GRUPO1_O_CORE_A_POR#CORE#CLASE1#301",
      "CONGRESO_GRUPO1_O_CORE_A_POR#BCI#CLASE1#301",
      "CONGRESO_GRUPO1_O_CORE_A_POR#ICEE#A_POR#301",
  })
  @ParameterizedTest
  public void baremacion_congreso_grupo1_core_a_por_ko_tipo(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 400L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_010, "XXX");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_080, "XXX");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_050, "XXX");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setRanking(TipoRanking.valueOf(arrParams[2]));
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "CONGRESO_GRUPO1_O_CORE_A_POR#GII_GRIN_SCIE#CLASE1#301",
      "CONGRESO_GRUPO1_O_CORE_A_POR#CORE#A_POR#301",
  })
  @ParameterizedTest
  public void baremacion_congreso_grupo1_core_a_by_fuente_impacto(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 400L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_010, "008");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_080, "XXX");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_050, "XXX");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setRanking(TipoRanking.valueOf(arrParams[2]));
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[3], arrParams[3] + ".00");
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "CONGRESO_INTERNACIONAL_POSTER_O_CARTEL#AMBITO_020#970#303",
      "CONGRESO_INTERNACIONAL_POSTER_O_CARTEL#AMBITO_030#970#303",
      "CONGRESO_NACIONAL_POSTER_O_CARTEL#AMBITO_000#970#306",
      "CONGRESO_NACIONAL_POSTER_O_CARTEL#AMBITO_010#970#306",
      "CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_020#960#304",
      "CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_030#960#304",
      "CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_000#960#307",
      "CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_010#960#307",
      "CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_020#080#305",
      "CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_020#730#305",
      "CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_030#080#305",
      "CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_030#730#305",
      "CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_000#080#308",
      "CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_000#730#308",
      "CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_010#080#308",
      "CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA#AMBITO_010#730#308",
  })
  @ParameterizedTest
  public void baremacion_congreso_internacional_nacional_by_ambito_and_tipo_participacion(String parameters)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 400L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_010, "008");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_080,
        TablaMaestraCVN.valueOf(arrParams[1]).getInternValue());
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_020_050,
        arrParams[2]);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.OTHERS);
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[3], arrParams[3] + ".00");
  }

}
