package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.TipoFuenteImpactoCuartil.Cuartil;
import org.crue.hercules.sgi.prc.repository.TipoFuenteImpactoCuartilRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de comites editoriales
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionComiteEditorialIT extends BaremacionBaseIT {

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
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'WOS_JCR',201, '201.00'", // COMITE_EDITORIAL_JCR_Q1
      "'SCOPUS_SJR',209, '209.00'", // COMITE_EDITORIAL_SCOPUS_Q1
      "'SCIMAGO',213, '213.00'", // COMITE_EDITORIAL_SCIMAGO_Q1
      "'DIALNET',221,'221.00'", // COMITE_EDITORIAL_DIALNET_Q1
      "'MIAR',225, '225.00'", // COMITE_EDITORIAL_MIAR_Q1
      "'FECYT',229, '229.00'", // COMITE_EDITORIAL_FECYT_Q1
  })
  @ParameterizedTest
  void baremacion_comite_q1(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("200"));
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
  @CsvSource({ "'WOS_JCR',202,'202.00'", // COMITE_EDITORIAL_JCR_Q2
      "'SCOPUS_SJR',210, '210.00'", // COMITE_EDITORIAL_SCOPUS_Q2
      "'SCIMAGO',214, '214.00'", // COMITE_EDITORIAL_SCIMAGO_Q2
      "'DIALNET',222, '222.00'", // COMITE_EDITORIAL_DIALNET_Q2
      "'MIAR',226, '226.00'", // COMITE_EDITORIAL_MIAR_Q2
      "'FECYT',230, '230.00'", // COMITE_EDITORIAL_FECYT_Q2
  })
  @ParameterizedTest
  void baremacion_comite_q2(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(100));
      entity.setNumeroRevistas(new BigDecimal("200"));
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
  @CsvSource({ "'WOS_JCR',203, '203.00'", // COMITE_EDITORIAL_JCR_Q3
      "'SCOPUS_SJR', 211, '211.00'", // COMITE_EDITORIAL_SCOPUS_Q3
      "'SCIMAGO', 215, '215.00'", // COMITE_EDITORIAL_SCIMAGO_Q3
      "'DIALNET',223, '223.00'", // COMITE_EDITORIAL_DIALNET_Q3
      "'MIAR', 227, '227.00'", // COMITE_EDITORIAL_MIAR_Q3
      "'FECYT', 231, '231.00'", // COMITE_EDITORIAL_FECYT_Q3
  })
  @ParameterizedTest
  void baremacion_comite_q3(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(150));
      entity.setNumeroRevistas(new BigDecimal("200"));
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
  @CsvSource({ "'WOS_JCR',204,'204.00'", // COMITE_EDITORIAL_JCR_Q4
      "'SCOPUS_SJR', 212, '212.00'", // COMITE_EDITORIAL_SCOPUS_Q4
      "'SCIMAGO', 216, '216.00'", // COMITE_EDITORIAL_SCIMAGCOMITE_EDITORIAL_SCIMAGO_Q4
      "'DIALNET', 224, '224.00'", // COMITE_EDITORIAL_DIALNET_Q4
      "'MIAR', 228, '228.00'", // COMITE_EDITORIAL_MIAR_Q4
      "'FECYT', 232, '232.00'", // COMITE_EDITORIAL_FECYT_Q4
  })
  @ParameterizedTest
  void baremacion_comite_q4(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(value));
      entity.setPosicionPublicacion(new BigDecimal(200));
      entity.setNumeroRevistas(new BigDecimal("200"));
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
  @CsvSource({ "'OTHERS',265,'265.00'" // COMITE_EDITORIAL
  })
  @ParameterizedTest
  void baremacion_comite_otras(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

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
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'CITEC',205,'205.00'", // COMITE_EDITORIAL_CITEC_Q1
      "'ERIH',217,'217.00'", // COMITE_EDITORIAL_ERIH_Q1
  })
  @ParameterizedTest
  void baremacion_comite_q1_without_posicion(String fuenteImpacto, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.valueOf(fuenteImpacto));
      entity.setPosicionPublicacion(null);
      entity.setNumeroRevistas(null);
      getIndiceImpactoRepository().save(entity);
    });

    tipoFuenteImpactoCuartilRepository.findByFuenteImpactoAndAnio(TipoFuenteImpacto.valueOf(fuenteImpacto), 2021)
        .map(entity -> {
          entity.setCuartil(Cuartil.Q1);
          return tipoFuenteImpactoCuartilRepository.save(entity);
        }).orElse(null);

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
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'CITEC',206,'206.00'", // COMITE_EDITORIAL_CITEC_Q2
      "'ERIH',218, '218.00'", // COMITE_EDITORIAL_ERIH_Q2
  })
  @ParameterizedTest
  void baremacion_comite_q2_without_posicion(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

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
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'CITEC',207,'207.00'", // COMITE_EDITORIAL_CITEC_Q3
      "'ERIH',219,'219.00'", // COMITE_EDITORIAL_ERIH_Q3
  })
  @ParameterizedTest
  void baremacion_comite_q3_without_posicion(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

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
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      "classpath:scripts/tipo_fuente_impacto_cuartil.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @CsvSource({ "'CITEC',208,'208.00'", // COMITE_EDITORIAL_CITEC_Q4
      "'ERIH',220,'220.00'", // COMITE_EDITORIAL_ERIH_Q4
  })
  @ParameterizedTest
  void baremacion_comite_q4_without_posicion(String value, Long baremoId, String puntos) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 300L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

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

}
