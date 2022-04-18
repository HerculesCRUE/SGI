package org.crue.hercules.sgi.prc.integration;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de organización de actividades
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionOrganizacionActividadIT extends BaremacionBaseIT {

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
  void baremacion_organizacion_actividad_comite_nacional() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 700L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_020_030_110,
        "ORGANIZATIVO_COMITE");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_020_030_030, "724");

    baremacionWithOnePuntuacion(idBaremacion, 601L, "601.00");
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
  void baremacion_organizacion_actividad_comite_internacional() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 700L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_020_030_110,
        "ORGANIZATIVO_COMITE");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_020_030_030, "840");

    baremacionWithOnePuntuacion(idBaremacion, 602L, "602.00");
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
  void baremacion_organizacion_actividad_comite_presidente_nacional() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 700L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_020_030_110,
        "ORGANIZATIVO_PRESIDENTE_COMITE");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_020_030_030, "724");

    baremacionWithOnePuntuacion(idBaremacion, 603L, "603.00");
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
  void baremacion_organizacion_actividad_comite_presidente_internacional() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 700L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_020_030_110,
        "ORGANIZATIVO_PRESIDENTE_COMITE");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_020_030_030, "840");

    baremacionWithOnePuntuacion(idBaremacion, 604L, "604.00");
  }

}
