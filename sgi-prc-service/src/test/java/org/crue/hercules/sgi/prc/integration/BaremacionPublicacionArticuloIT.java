package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;

import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.TipoFuenteImpactoCuartil.Cuartil;
import org.crue.hercules.sgi.prc.repository.TipoFuenteImpactoCuartilRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de publicaciones artículos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaremacionPublicacionArticuloIT extends BaremacionBaseIT {

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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "ARTICULO_JCR_Q1#WOS_JCR#101",
      "ARTICULO_SCOPUS_Q1#SCOPUS_SJR#109",
      "ARTICULO_SCIMAGO_Q1#SCIMAGO#113",
      "ARTICULO_DIALNET_Q1#DIALNET#121",
      "ARTICULO_MIAR_Q1#MIAR#125",
      "ARTICULO_FECYT_Q1#FECYT#129",
  })
  @ParameterizedTest
  public void baremacion_publicacion_articulo_q1(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("2"));
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
  @ValueSource(strings = { "ARTICULO_JCR_Q2#WOS_JCR#102",
      "ARTICULO_SCOPUS_Q2#SCOPUS_SJR#110",
      "ARTICULO_SCIMAGO_Q2#SCIMAGO#114",
      "ARTICULO_DIALNET_Q2#DIALNET#122",
      "ARTICULO_MIAR_Q2#MIAR#126",
      "ARTICULO_FECYT_Q2#FECYT#130",
  })
  @ParameterizedTest
  public void baremacion_publicacion_articulo_q2(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(new BigDecimal(100));
      entity.setNumeroRevistas(new BigDecimal("2"));
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
  @ValueSource(strings = { "ARTICULO_JCR_Q3#WOS_JCR#103",
      "ARTICULO_SCOPUS_Q3#SCOPUS_SJR#111",
      "ARTICULO_SCIMAGO_Q3#SCIMAGO#115",
      "ARTICULO_DIALNET_Q3#DIALNET#123",
      "ARTICULO_MIAR_Q3#MIAR#127",
      "ARTICULO_FECYT_Q3#FECYT#131",
  })
  @ParameterizedTest
  public void baremacion_publicacion_articulo_q3(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(new BigDecimal(150));
      entity.setNumeroRevistas(new BigDecimal("2"));
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
  @ValueSource(strings = { "ARTICULO_JCR_Q4#WOS_JCR#104",
      "ARTICULO_SCOPUS_Q4#SCOPUS_SJR#112",
      "ARTICULO_SCIMAGO_Q4#SCIMAGO#116",
      "ARTICULO_DIALNET_Q4#DIALNET#124",
      "ARTICULO_MIAR_Q4#MIAR#128",
      "ARTICULO_FECYT_Q4#FECYT#132",
  })
  @ParameterizedTest
  public void baremacion_publicacion_articulo_q4(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(new BigDecimal(200));
      entity.setNumeroRevistas(new BigDecimal("2"));
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
  @ValueSource(strings = { "ARTICULO#OTHERS#133" })
  @ParameterizedTest
  public void baremacion_publicacion_articulo_otras(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "ARTICULO_CITEC_Q1#CITEC#105",
      "ARTICULO_ERIH_Q1#ERIH#117",
  })
  @ParameterizedTest
  public void baremacion_publicacion_articulo_q1_without_posicion(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    tipoFuenteImpactoCuartilRepository.findByFuenteImpactoAndAnio(TipoFuenteImpacto.valueOf(arrParams[1]), 2021)
        .map(entity -> {
          entity.setCuartil(Cuartil.Q1);
          return tipoFuenteImpactoCuartilRepository.save(entity);
        }).orElse(null);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "ARTICULO_CITEC_Q2#CITEC#106",
      "ARTICULO_ERIH_Q2#ERIH#118",
  })
  @ParameterizedTest
  public void baremacion_publicacion_articulo_q2_without_posicion(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    tipoFuenteImpactoCuartilRepository.findByFuenteImpactoAndAnio(TipoFuenteImpacto.valueOf(arrParams[1]), 2021)
        .map(entity -> {
          entity.setCuartil(Cuartil.Q2);
          return tipoFuenteImpactoCuartilRepository.save(entity);
        }).orElse(null);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "ARTICULO_CITEC_Q3#CITEC#107",
      "ARTICULO_ERIH_Q3#ERIH#119",
  })
  @ParameterizedTest
  public void baremacion_publicacion_articulo_q3_without_posicion(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    tipoFuenteImpactoCuartilRepository.findByFuenteImpactoAndAnio(TipoFuenteImpacto.valueOf(arrParams[1]), 2021)
        .map(entity -> {
          entity.setCuartil(Cuartil.Q3);
          return tipoFuenteImpactoCuartilRepository.save(entity);
        }).orElse(null);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "ARTICULO_CITEC_Q4#CITEC#108",
      "ARTICULO_ERIH_Q4#ERIH#120",
  })
  @ParameterizedTest
  public void baremacion_publicacion_articulo_q4_without_posicion(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "020");

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    tipoFuenteImpactoCuartilRepository.findByFuenteImpactoAndAnio(TipoFuenteImpacto.valueOf(arrParams[1]), 2021)
        .map(entity -> {
          entity.setCuartil(Cuartil.Q4);
          return tipoFuenteImpactoCuartilRepository.save(entity);
        }).orElse(null);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(arrParams[1]));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
  }

}
