package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;

import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.TipoFuenteImpactoCuartil.Cuartil;
import org.crue.hercules.sgi.prc.repository.TipoFuenteImpactoCuartilRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de comites editoriales
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaremacionComiteEditorialIT extends BaremacionBaseIT {

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
  @ValueSource(strings = { "COMITE_EDITORIAL_JCR_Q1#WOS_JCR#201",
      "COMITE_EDITORIAL_SCOPUS_Q1#SCOPUS_SJR#209",
      "COMITE_EDITORIAL_SCIMAGO_Q1#SCIMAGO#213",
      "COMITE_EDITORIAL_DIALNET_Q1#DIALNET#221",
      "COMITE_EDITORIAL_MIAR_Q1#MIAR#225",
      "COMITE_EDITORIAL_FECYT_Q1#FECYT#229",
  })
  @ParameterizedTest
  public void baremacion_comite_q1(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

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
  @ValueSource(strings = { "COMITE_EDITORIAL_JCR_Q2#WOS_JCR#202",
      "COMITE_EDITORIAL_SCOPUS_Q2#SCOPUS_SJR#210",
      "COMITE_EDITORIAL_SCIMAGO_Q2#SCIMAGO#214",
      "COMITE_EDITORIAL_DIALNET_Q2#DIALNET#222",
      "COMITE_EDITORIAL_MIAR_Q2#MIAR#226",
      "COMITE_EDITORIAL_FECYT_Q2#FECYT#230",
  })
  @ParameterizedTest
  public void baremacion_comite_q2(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

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
  @ValueSource(strings = { "COMITE_EDITORIAL_JCR_Q3#WOS_JCR#203",
      "COMITE_EDITORIAL_SCOPUS_Q3#SCOPUS_SJR#211",
      "COMITE_EDITORIAL_SCIMAGO_Q3#SCIMAGO#215",
      "COMITE_EDITORIAL_DIALNET_Q3#DIALNET#223",
      "COMITE_EDITORIAL_MIAR_Q3#MIAR#227",
      "COMITE_EDITORIAL_FECYT_Q3#FECYT#231",
  })
  @ParameterizedTest
  public void baremacion_comite_q3(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

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
  @ValueSource(strings = { "COMITE_EDITORIAL_JCR_Q4#WOS_JCR#204",
      "COMITE_EDITORIAL_SCOPUS_Q4#SCOPUS_SJR#212",
      "COMITE_EDITORIAL_SCIMAGO_Q4#SCIMAGO#216",
      "COMITE_EDITORIAL_DIALNET_Q4#DIALNET#224",
      "COMITE_EDITORIAL_MIAR_Q4#MIAR#228",
      "COMITE_EDITORIAL_FECYT_Q4#FECYT#232",
  })
  @ParameterizedTest
  public void baremacion_comite_q4(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

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
  @ValueSource(strings = { "ARTICULO#OTHERS#233" })
  @ParameterizedTest
  public void baremacion_comite_otras(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

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
  @ValueSource(strings = { "COMITE_EDITORIAL_CITEC_Q1#CITEC#205",
      "COMITE_EDITORIAL_ERIH_Q1#ERIH#217",
  })
  @ParameterizedTest
  public void baremacion_comite_q1_without_posicion(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

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
  @ValueSource(strings = { "COMITE_EDITORIAL_CITEC_Q2#CITEC#206",
      "COMITE_EDITORIAL_ERIH_Q2#ERIH#218",
  })
  @ParameterizedTest
  public void baremacion_comite_q2_without_posicion(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

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
  @ValueSource(strings = { "COMITE_EDITORIAL_CITEC_Q3#CITEC#207",
      "COMITE_EDITORIAL_ERIH_Q3#ERIH#219",
  })
  @ParameterizedTest
  public void baremacion_comite_q3_without_posicion(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

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
  @ValueSource(strings = { "COMITE_EDITORIAL_CITEC_Q4#CITEC#208",
      "COMITE_EDITORIAL_ERIH_Q4#ERIH#220",
  })
  @ParameterizedTest
  public void baremacion_comite_q4_without_posicion(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

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
