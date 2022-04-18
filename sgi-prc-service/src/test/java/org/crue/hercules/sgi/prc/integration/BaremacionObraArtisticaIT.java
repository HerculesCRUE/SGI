package org.crue.hercules.sgi.prc.integration;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de obras artísticas
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionObraArtisticaIT extends BaremacionBaseIT {

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
  void baremacion_obra_artistica_exp_gr1_individual() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 600L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OBRA, "EXPOSICION");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_040, "840");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_100, "true");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_110, "false");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.COLECTIVA, "false");

    baremacionWithOnePuntuacion(idBaremacion, 501L, "501.00");
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
  void baremacion_obra_artistica_exp_gr1_colectiva() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 600L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OBRA, "EXPOSICION");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_040, "840");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_100, "true");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_110, "false");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.COLECTIVA, "true");

    baremacionWithOnePuntuacion(idBaremacion, 502L, "502.00");
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
  void baremacion_obra_artistica_exp_gr2_individual() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 600L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OBRA, "EXPOSICION");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_040,
        "724");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_100, "true");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_110, "false");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.COLECTIVA, "false");

    baremacionWithOnePuntuacion(idBaremacion, 503L, "503.00");
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
  void baremacion_obra_artistica_exp_gr2_colectiva() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 600L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OBRA, "EXPOSICION");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_040,
        "724");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_100, "true");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_110, "false");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.COLECTIVA, "true");

    baremacionWithOnePuntuacion(idBaremacion, 504L, "504.00");
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
  void baremacion_obra_artistica_exp_gr3() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 600L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OBRA, "EXPOSICION");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_040, "724");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_100, "false");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_110, "false");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.COLECTIVA, "true");

    baremacionWithOnePuntuacion(idBaremacion, 505L, "505.00");
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
  void baremacion_obra_artistica_exp_gr4() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 600L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OBRA, "EXPOSICION");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_040, "724");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_100, "false");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_110, "false");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.COLECTIVA, "false");

    baremacionWithOnePuntuacion(idBaremacion, 506L, "506.00");
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
  void baremacion_obra_artistica_disenio_gr1_not_spain() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 600L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OBRA, "DISENO");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_040, "840");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_050, "ES62");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_100, "true");

    baremacionWithOnePuntuacion(idBaremacion, 507L, "507.00");
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
  void baremacion_obra_artistica_disenio_gr1_murcia() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 600L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OBRA, "DISENO");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_040, "724");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_050, "ES12");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_100, "true");

    baremacionWithOnePuntuacion(idBaremacion, 507L, "507.00");
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
  void baremacion_obra_artistica_disenio_gr2() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 600L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OBRA, "DISENO");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_040,
        "724");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_050,
        "ES62");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_100, "true");

    baremacionWithOnePuntuacion(idBaremacion, 508L, "508.00");
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
  void baremacion_obra_artistica_disenio_gr3() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 600L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.TIPO_OBRA, "DISENO");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E050_020_030_100, "false");

    baremacionWithOnePuntuacion(idBaremacion, 509L, "509.00");
  }

}
