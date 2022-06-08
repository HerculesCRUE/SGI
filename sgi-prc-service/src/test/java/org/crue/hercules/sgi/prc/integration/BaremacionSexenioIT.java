package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.BaremacionController;
import org.crue.hercules.sgi.prc.dto.sgp.SexenioDto;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de sexenios.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionSexenioIT extends BaremacionBaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = BaremacionController.MAPPING;

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
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
  void baremacion_libro_and_sexenio_from_json() throws Exception {

    String produccionCientificaJson = "publicacion-libro.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String personaRef = "22932567";
    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    mockSexenio(2020, personaRef, "5");
    mockSexenio(2021, personaRef, "6");

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkPuntuacionLibroAndSexenio(idBaremacion, personaRef);
  }

  private void checkPuntuacionLibroAndSexenio(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 1L, new BigDecimal("75.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 1L, new BigDecimal("90.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(3);

    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("75.00"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("90.00"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(3);

    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("45.00"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("54.00"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("99.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("4.14"));

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("151.52"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("27173.91"));
  }

  protected void mockSexenio(Integer anio, String personaRef, String numeroSexenios) {
    Instant fechaFinBaremacion = ProduccionCientificaFieldFormatUtil.calculateFechaFinBaremacionByAnio(anio,
        getSgiConfigProperties().getTimeZone());
    String strFechaFinBaremacion = ProduccionCientificaFieldFormatUtil.formatInstantToStringWithTimeZoneAndPattern(
        fechaFinBaremacion, getSgiConfigProperties().getTimeZone(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
    BDDMockito.given(getSgiApiSgpService().findSexeniosByFecha(strFechaFinBaremacion))
        .willReturn((Arrays.asList(generarMockSexenio(personaRef, numeroSexenios))));
  }

  protected SexenioDto generarMockSexenio(String personaRef, String numeroSexenios) {
    return SexenioDto.builder()
        .numero(numeroSexenios)
        .personaRef(personaRef)
        .build();
  }
}
