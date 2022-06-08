package org.crue.hercules.sgi.prc.integration;

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.dto.sgp.DireccionTesisDto;
import org.crue.hercules.sgi.prc.dto.sgp.DireccionTesisDto.TipoTesisDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de DireccionTesis
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionDireccionTesisIT extends BaremacionBaseIT {

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
  @Test
  void baremacion_direccion_tesis_tesis() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 500L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E030_040_000_010, "067");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.MENCION_INDUSTRIAL, "false");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(
        produccionCientificaId, CodigoCVN.MENCION_INTERNACIONAL, "false");

    baremacionWithOnePuntuacion(idBaremacion, 401L, "401.00");
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
  @CsvSource({ "'066',402,'402.00'", // direccion_tesis_tesina
      "'055',402,'402.00'", // direccion_tesis_tfm
      "'071',402,'402.00'", // direccion_tesis_dea
      "'OTHERS',402,'402.00'", // direccion_tesis_other
  })
  @ParameterizedTest
  void baremacion_direccion_no_tesis(String value, Long baremoId, String puntos)
      throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 500L;

    ProduccionCientifica produccionCientifica = updateEstadoProduccionCientifica(produccionCientificaId,
        TipoEstadoProduccion.VALIDADO);
    Assertions.assertThat(produccionCientifica).as("produccionCientifica").isNotNull();

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E030_040_000_010,
        value);
    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.MENCION_INDUSTRIAL, "false");
    updateValorCampoByCodigoCVNAndProduccionCientificaId(
        produccionCientificaId, CodigoCVN.MENCION_INTERNACIONAL, "false");

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
      "classpath:scripts/tabla_indice.sql",
      "classpath:scripts/indice_experimentalidad.sql",    
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  // @Test
  void baremacion_direccion_tesis_from_import() throws Exception {
    Long idBaremacion = 1L;
    String personaRef = "anyPerson";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    BDDMockito.given(getSgiApiSgpService().findTesisByAnio(2020))
        .willReturn(Arrays.asList(generarMockDireccionTesis("T1", personaRef, "067")));

    Assertions.assertThat(idBaremacion).as("idBaremacion").isNotNull();

    baremacionWithOnePuntuacion(idBaremacion, 401L, "401.00");
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
  @CsvSource({ "'066',402,'402.00'", // direccion_tesis_tesina
      "'055',402,'402.00'", // direccion_tesis_tfm
      "'071',402,'402.00'", // direccion_tesis_dea
      "'OTHERS',402,'402.00'", // direccion_tesis_other
  })
  // @ParameterizedTest
  void baremacion_direccion_no_tesis_from_import(String value, Long baremoId, String puntos)
      throws Exception {
    Long idBaremacion = 1L;

    String personaRef = "anyPerson";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    BDDMockito.given(getSgiApiSgpService().findTesisByAnio(2020))
        .willReturn(Arrays.asList(generarMockDireccionTesis("T1", personaRef, value)));

    Assertions.assertThat(idBaremacion).as("idBaremacion").isNotNull();

    baremacionWithOnePuntuacion(idBaremacion, baremoId, puntos);
  }

  protected DireccionTesisDto generarMockDireccionTesis(String tesisId, String personaRef, String tipoProyectoId) {

    return DireccionTesisDto.builder()
        .id(tesisId)
        .personaRef(personaRef)
        .tituloTrabajo("tituloTrabajo")
        .fechaDefensa("2020-12-12")
        .alumno("alumno")
        .tipoProyecto(TipoTesisDto.builder().id(tipoProyectoId).build())
        .calificacionObtenida("calificacionObtenida")
        .coDirectorTesisRef("coDirectorTesisRef")
        .doctoradoEuropeo(null)
        .fechaMencionDoctoradoEuropeo("2020-12-12")
        .mencionCalidad(Boolean.TRUE)
        .fechaMencionCalidad(null)
        .mencionInternacional(Boolean.FALSE)
        .mencionIndustrial(null)
        .build();
  }

}
