package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.BaremacionController;
import org.crue.hercules.sgi.prc.dto.csp.ProyectoProyectoSgeDto;
import org.crue.hercules.sgi.prc.dto.pi.InvencionDto;
import org.crue.hercules.sgi.prc.dto.pi.InvencionDto.SolicitudProteccionDto;
import org.crue.hercules.sgi.prc.dto.rel.RelacionOutput;
import org.crue.hercules.sgi.prc.dto.sgepii.IngresoColumnaDefDto;
import org.crue.hercules.sgi.prc.dto.sgepii.IngresoDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.MapeoTipos;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.service.BaremacionInvencionService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
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
class BaremacionInvencionIT extends BaremacionBaseIT {

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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_invencion_2020_patente_nacional_from_json() throws Exception {

    String personaRef = "22932567";
    Long viaProteccionId = 1L; // España
    SolicitudProteccionDto solicitudProteccionDto = generarMockSolicitudProteccion(1L,
        Instant.parse("2020-01-21T00:00:00Z"), viaProteccionId);

    InvencionDto invencionDto = InvencionDto.builder()
        .id(1L)
        .titulo("titulo")
        .tipoProteccionId(1L) // mapeo_tipos = 141
        .cuantia(new BigDecimal("10000"))
        .participaciones(Arrays.asList(new BigDecimal("10")))
        .solicitudesProteccion(Arrays.asList(solicitudProteccionDto))
        .inventores(Arrays.asList(personaRef)).build();

    List<InvencionDto> invenciones = Arrays.asList(invencionDto);

    Long idBaremacion = baremacionLibroInvencionOneYearOneBaremoPrincipal(personaRef, invenciones);

    checkPuntuacionLibroAndInvencion2020PatenteNacional(idBaremacion, personaRef);
  }

  private void checkPuntuacionLibroAndInvencion2020PatenteNacional(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 705L, new BigDecimal("1.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 701L, new BigDecimal("70.10"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("71.10"));

    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("42.66")); // 60% puntuacionItemsInvestigador

    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14")); // 60% puntuacionItemsInvestigador

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("46.80")); // sum puntuacionGrupoInvestigador

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("2403.85"));
  }

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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_invencion_2020_patente_internacional_from_json() throws Exception {

    String personaRef = "22932567";

    Long viaProteccionId = 2L; // Europea
    SolicitudProteccionDto solicitudProteccionDto = generarMockSolicitudProteccion(1L,
        Instant.parse("2020-01-21T00:00:00Z"), viaProteccionId);

    InvencionDto invencionDto = InvencionDto.builder()
        .id(1L)
        .titulo("titulo")
        .tipoProteccionId(1L) // mapeo_tipos = 141
        .cuantia(new BigDecimal("10000"))
        .participaciones(Arrays.asList(new BigDecimal("10")))
        .solicitudesProteccion(Arrays.asList(solicitudProteccionDto))
        .inventores(Arrays.asList(personaRef)).build();

    List<InvencionDto> invenciones = Arrays.asList(invencionDto);

    Long idBaremacion = baremacionLibroInvencionOneYearOneBaremoPrincipal(personaRef, invenciones);

    checkPuntuacionLibroAndInvencion2020PatenteInternacional(idBaremacion, personaRef);
  }

  private void checkPuntuacionLibroAndInvencion2020PatenteInternacional(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 705L, new BigDecimal("1.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 702L, new BigDecimal("70.20"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("71.20"));

    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("42.72")); // 60% puntuacionItemsInvestigador

    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14")); // 60% puntuacionItemsInvestigador

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("46.86")); // sum puntuacionGrupoInvestigador

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("2400.77"));
  }

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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "8", // OTHERS
      "9", // OTHERS
      "2", // 126
      "3", // 109
      "4", // 122
      "5", // 122
      "6", // 122
  })
  void baremacion_libro_and_invencion_2020_otro_nacional_from_json(Long tipoProteccionId) throws Exception {

    String personaRef = "22932567";

    Long viaProteccionId = 1L; // España
    SolicitudProteccionDto solicitudProteccionDto = generarMockSolicitudProteccion(1L,
        Instant.parse("2020-01-21T00:00:00Z"), viaProteccionId);

    InvencionDto invencionDto = InvencionDto.builder()
        .id(1L)
        .titulo("titulo")
        .tipoProteccionId(tipoProteccionId) // mapeo_tipos = OTHERS
        .cuantia(new BigDecimal("10000"))
        .participaciones(Arrays.asList(new BigDecimal("10")))
        .solicitudesProteccion(Arrays.asList(solicitudProteccionDto))
        .inventores(Arrays.asList(personaRef)).build();

    List<InvencionDto> invenciones = Arrays.asList(invencionDto);

    Long idBaremacion = baremacionLibroInvencionOneYearOneBaremoPrincipal(personaRef, invenciones);

    checkPuntuacionLibroAndInvencion2020PatenteOtroNacional(idBaremacion, personaRef);
  }

  private void checkPuntuacionLibroAndInvencion2020PatenteOtroNacional(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 705L, new BigDecimal("1.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 703L, new BigDecimal("70.30"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("71.30"));

    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(2);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("42.78")); // 60% puntuacionItemsInvestigador

    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14")); // 60% puntuacionItemsInvestigador

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("46.92")); // sum puntuacionGrupoInvestigador

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("2397.70"));
  }

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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2", // 126
      "3", // 109
      "4", // 122
      "5", // 122
      "6", // 122
  })
  void baremacion_libro_and_invencion_2020_otro_internacional_from_json(Long tipoProteccionId) throws Exception {

    String personaRef = "22932567";

    Long viaProteccionId = 2L; // Europea
    SolicitudProteccionDto solicitudProteccionDto = generarMockSolicitudProteccion(1L,
        Instant.parse("2020-01-21T00:00:00Z"), viaProteccionId);

    InvencionDto invencionDto = InvencionDto.builder()
        .id(1L)
        .titulo("titulo")
        .tipoProteccionId(tipoProteccionId) // mapeo_tipos = OTHERS
        .cuantia(new BigDecimal("10000"))
        .participaciones(Arrays.asList(new BigDecimal("10")))
        .solicitudesProteccion(Arrays.asList(solicitudProteccionDto))
        .inventores(Arrays.asList(personaRef)).build();

    List<InvencionDto> invenciones = Arrays.asList(invencionDto);

    Long idBaremacion = baremacionLibroInvencionOneYearOneBaremoPrincipal(personaRef, invenciones);

    checkPuntuacionLibroAndInvencion2020PatenteOtroInternacional(idBaremacion, personaRef);
  }

  private void checkPuntuacionLibroAndInvencion2020PatenteOtroInternacional(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(4);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 705L, new BigDecimal("1.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 704L, new BigDecimal("70.40"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(2);

    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("71.40"));
    checkPuntuacionItemInvestigador(puntuacionItemsInvestigador, personaRef, new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(2);

    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("42.84"));
    checkPuntuacionGrupoInvestigador(puntuacionGrupoInvestigador, new BigDecimal("4.14"));

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("46.98")); // sum puntuacionGrupoInvestigador

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("2394.64"));
  }

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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_invencion_2020_2021_patente_nacional_from_json() throws Exception {

    String personaRef = "22932567";
    Long viaProteccionId = 1L; // España
    SolicitudProteccionDto solicitudProteccion2020Dto = generarMockSolicitudProteccion(1L,
        Instant.parse("2020-01-21T00:00:00Z"), viaProteccionId);
    SolicitudProteccionDto solicitudProteccion2021Dto = generarMockSolicitudProteccion(1L,
        Instant.parse("2021-01-21T00:00:00Z"), viaProteccionId);

    InvencionDto invencionDto = InvencionDto.builder()
        .id(1L)
        .titulo("titulo")
        .tipoProteccionId(1L) // mapeo_tipos = 141
        .cuantia(new BigDecimal("10000"))
        .participaciones(Arrays.asList(
            new BigDecimal("10"),
            new BigDecimal("10")))
        .solicitudesProteccion(Arrays.asList(
            solicitudProteccion2020Dto,
            solicitudProteccion2021Dto))
        .inventores(Arrays.asList(personaRef)).build();

    List<InvencionDto> invenciones = Arrays.asList(invencionDto);

    Long idBaremacion = baremacionLibroInvencionOneYearOneBaremoPrincipal(personaRef, invenciones);

    checkPuntuacionLibroAndInvencion20202021PatenteNacional(idBaremacion, personaRef);
  }

  private void checkPuntuacionLibroAndInvencion20202021PatenteNacional(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(6);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 705L, new BigDecimal("1.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 701L, new BigDecimal("70.10"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(3);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("71.10")); // 2020

    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("71.10")); // 2021

    Assertions.assertThat(puntuacionItemsInvestigador.get(2).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(2).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(3);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("42.66")); // 60% puntuacionItemsInvestigador 2020

    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("42.66")); // 60% puntuacionItemsInvestigador 2021

    Assertions.assertThat(puntuacionGrupoInvestigador.get(2).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14")); // 60% puntuacionItemsInvestigador

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("89.46")); // sum puntuacionGrupoInvestigador

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("1257.55"));
  }

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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_invencion_2020_2021_patente_nacional_and_europea_from_json() throws Exception {

    String personaRef = "22932567";
    Long viaProteccionId = 1L; // España
    SolicitudProteccionDto solicitudProteccion2020Dto = generarMockSolicitudProteccion(1L,
        Instant.parse("2020-01-21T00:00:00Z"), viaProteccionId);
    viaProteccionId = 2L; // Europea
    SolicitudProteccionDto solicitudProteccion2021Dto = generarMockSolicitudProteccion(1L,
        Instant.parse("2021-01-21T00:00:00Z"), viaProteccionId);

    InvencionDto invencionDto = InvencionDto.builder()
        .id(1L)
        .titulo("titulo")
        .tipoProteccionId(1L) // mapeo_tipos = 141
        .cuantia(new BigDecimal("10000"))
        .participaciones(Arrays.asList(
            new BigDecimal("10"),
            new BigDecimal("20")))
        .solicitudesProteccion(Arrays.asList(
            solicitudProteccion2020Dto,
            solicitudProteccion2021Dto))
        .inventores(Arrays.asList(personaRef)).build();

    List<InvencionDto> invenciones = Arrays.asList(invencionDto);

    Long idBaremacion = baremacionLibroInvencionOneYearOneBaremoPrincipal(personaRef, invenciones);

    checkPuntuacionLibroAndInvencion20202021PatenteNacionalAndEuropea(idBaremacion, personaRef);
  }

  private void checkPuntuacionLibroAndInvencion20202021PatenteNacionalAndEuropea(Long idBaremacion, String personaRef) {
    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(6);

    checkPuntuacionBaremoItem(puntuacionBaremoItems, 705L, new BigDecimal("1.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 705L, new BigDecimal("2.00"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 701L, new BigDecimal("70.10"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 702L, new BigDecimal("140.40"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 6L, new BigDecimal("6"));
    checkPuntuacionBaremoItem(puntuacionBaremoItems, 29L, new BigDecimal("2.30"));

    List<PuntuacionItemInvestigador> puntuacionItemsInvestigador = getPuntuacionItemInvestigadorRepository()
        .findAll();

    int numPuntuacionesItemsInvestigador = puntuacionItemsInvestigador.size();
    Assertions.assertThat(numPuntuacionesItemsInvestigador).as("numPuntuacionesItemsInvestigador").isEqualTo(3);

    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("71.10")); // 2020

    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(1).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("142.40")); // 2021

    Assertions.assertThat(puntuacionItemsInvestigador.get(2).getPersonaRef()).as("PersonaRef").isEqualTo(personaRef);
    Assertions.assertThat(puntuacionItemsInvestigador.get(2).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal("6.90"));

    List<PuntuacionGrupoInvestigador> puntuacionGrupoInvestigador = getPuntuacionGrupoInvestigadorRepository()
        .findAll();

    int numPuntuacionesGrupoInvestigador = puntuacionGrupoInvestigador.size();
    Assertions.assertThat(numPuntuacionesGrupoInvestigador).as("numPuntuacionesGrupoInvestigador").isEqualTo(3);

    Assertions.assertThat(puntuacionGrupoInvestigador.get(0).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("42.66")); // 60% puntuacionItemsInvestigador 2020

    Assertions.assertThat(puntuacionGrupoInvestigador.get(1).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("85.44")); // 60% puntuacionItemsInvestigador 2021

    Assertions.assertThat(puntuacionGrupoInvestigador.get(2).getPuntos()).as("PuntosGrupoInvestigador")
        .isEqualTo(new BigDecimal("4.14")); // 60% puntuacionItemsInvestigador

    List<PuntuacionGrupo> puntuacionGrupo = getPuntuacionGrupoRepository().findAll();

    int numPuntuacionesGrupo = puntuacionGrupo.size();
    Assertions.assertThat(numPuntuacionesGrupo).as("numPuntuacionesGrupo").isEqualTo(1);

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosCostesIndirectos()).as("PuntosGrupoCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosSexenios()).as("PuntosGrupoSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(puntuacionGrupo.get(0).getPuntosProduccion()).as("PuntosGrupoProduccion")
        .isEqualTo(new BigDecimal("132.24")); // sum puntuacionGrupoInvestigador

    ConvocatoriaBaremacion convocatoriaBaremacion = getConvocatoriaBaremacionRepository().findById(idBaremacion).get();

    Assertions.assertThat(convocatoriaBaremacion.getPuntoCostesIndirectos())
        .as("PuntosBaremacionCostesIndirectos")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoSexenio()).as("PuntosBaremacionSexenios")
        .isEqualTo(new BigDecimal("0.00"));

    Assertions.assertThat(convocatoriaBaremacion.getPuntoProduccion()).as("PuntosBaremacionProduccion")
        .isEqualTo(new BigDecimal("850.73"));
  }

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
      "classpath:scripts/mapeo_tipos.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_and_invencion_2020_patente_nacional_cuantia_calc_from_json() throws Exception {

    String personaRef = "22932567";
    Long viaProteccionId = 1L; // España
    SolicitudProteccionDto solicitudProteccionDto = generarMockSolicitudProteccion(1L,
        Instant.parse("2020-01-21T00:00:00Z"), viaProteccionId);

    Long invencionId = 111L;
    InvencionDto invencionDto = InvencionDto.builder()
        .id(invencionId)
        .titulo("titulo")
        .tipoProteccionId(1L) // mapeo_tipos = 141
        .cuantia(new BigDecimal("66.00")) // valor calculado procedente del mock de cálculo licencias
        .participaciones(Arrays.asList(new BigDecimal("10")))
        .solicitudesProteccion(Arrays.asList(solicitudProteccionDto))
        .inventores(Arrays.asList(personaRef)).build();

    List<InvencionDto> invenciones = Arrays.asList(invencionDto);

    mockCuantiaLicencias();

    Long idBaremacion = baremacionLibroInvencionOneYearOneBaremoPrincipal(personaRef, invenciones);

    checkPuntuacionLibroAndInvencion2020PatenteNacional(idBaremacion, personaRef);
  }

  @SuppressWarnings({ "unchecked" })
  private void mockCuantiaLicencias() {

    List<RelacionOutput> relaciones = (List<RelacionOutput>) getObjectListFromJson(
        "prc/relaciones.json", RelacionOutput.class.getName());

    List<ProyectoProyectoSgeDto> proyectosSge = (List<ProyectoProyectoSgeDto>) getObjectListFromJson(
        "prc/proyectos-sge.json", ProyectoProyectoSgeDto.class.getName());

    List<IngresoDto> ingresos = (List<IngresoDto>) getObjectListFromJson(
        "prc/ingresos-invencion.json", IngresoDto.class.getName());

    List<IngresoColumnaDefDto> ingresoColumnas = (List<IngresoColumnaDefDto>) getObjectListFromJson(
        "prc/ingresos-invencion-columnas.json", IngresoColumnaDefDto.class.getName());

    BDDMockito.given(getSgiApiRelService().findAllRelaciones(ArgumentMatchers.anyString()))
        .willReturn(relaciones);

    BDDMockito.given(getSgiApiCspService().findProyectosSgeByProyectoId(ArgumentMatchers.anyLong()))
        .willReturn(proyectosSge);

    BDDMockito.given(getSgiApiSgePiiService().findIngresosInvencion(ArgumentMatchers.anyString()))
        .willReturn(ingresos);

    BDDMockito.given(getSgiApiSgePiiService().findIngresosInvencionColumnasDef(ArgumentMatchers.anyString()))
        .willReturn(ingresoColumnas);
  }

  private Long baremacionLibroInvencionOneYearOneBaremoPrincipal(String personaRef, List<InvencionDto> invenciones)
      throws Exception {
    String produccionCientificaJson = "publicacion-libro.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores, numIndicesImpacto);

    Long idBaremacion = 1L;

    String areaRef = "165";
    String areaRefRaiz = "J";
    mockPersonaAndAreaConocimientoAndGrupoInvestigacion(personaRef, areaRef, areaRefRaiz);

    mockInvenciones(invenciones);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    checkCopyInvencion(invenciones);
    return idBaremacion;
  }

  private void mockInvenciones(List<InvencionDto> invenciones) {

    BDDMockito.given(getSgiApiCnfService().findByName(ArgumentMatchers.anyString()))
        .willReturn("Q3018001");

    BDDMockito.given(getSgiApiPiiService().findInvencionesProduccionCientifica(ArgumentMatchers.anyInt(),
        ArgumentMatchers.anyInt(), ArgumentMatchers.anyString()))
        .willReturn(invenciones);
  }

  private void checkCopyInvencion(List<InvencionDto> invenciones) {
    invenciones.stream().forEach(invencion -> {

      Long produccionCientificaId = getProduccionCientificaRepository()
          .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(
              BaremacionInvencionService.PREFIX_INVENCIONES + invencion.getId())
          .get().getId();

      List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
          .findAllByProduccionCientificaId(produccionCientificaId);

      Integer numCampos = campos.size();

      Assertions.assertThat(numCampos).as("number of campos created").isEqualTo(7);

      // titulo
      CodigoCVN codigoCVNTitulo = CodigoCVN.E050_030_010_020;
      ValorCampo valorCampo = getValorCampoByCodigoCVN(campos, codigoCVNTitulo);
      Assertions.assertThat(valorCampo.getValor()).as(codigoCVNTitulo.name()).isEqualTo(invencion.getTitulo());

      // tipo proteccion
      CodigoCVN codigoCVNTipoProteccion = CodigoCVN.E050_030_010_030;
      ValorCampo valorCampoTipoProteccion = getValorCampoByCodigoCVN(campos, codigoCVNTipoProteccion);
      Assertions.assertThat(valorCampoTipoProteccion.getValor()).as(codigoCVNTipoProteccion.name())
          .isEqualTo(getValorMapeoTipos(codigoCVNTipoProteccion, invencion.getTipoProteccionId()));

      // participacion
      CodigoCVN codigoCVNParticipacion = CodigoCVN.PORCENTAJE_TITULARIDAD;
      final List<ValorCampo> valoresCamposParticipaciones = getValoresCamposByCodigoCVN(campos, codigoCVNParticipacion);
      IntStream.range(0, invencion.getParticipaciones().size())
          .parallel().forEach(i -> Assertions.assertThat(valoresCamposParticipaciones.get(i).getValor())
              .as(String.format("participacion[%d]", i))
              .isEqualTo(ProduccionCientificaFieldFormatUtil.formatNumber(
                  invencion.getParticipaciones().get(i).toString())));

      // fecha concesion
      CodigoCVN codigoCVNFechaConcesion = CodigoCVN.E050_030_010_320;
      final List<ValorCampo> valoresCamposFechaConcesion = getValoresCamposByCodigoCVN(campos, codigoCVNFechaConcesion);
      IntStream.range(0, invencion.getParticipaciones().size())
          .parallel().forEach(i -> {
            Assertions.assertThat(valoresCamposFechaConcesion.get(i).getValor())
                .as(String.format("fechaConcesion[%d]", i))
                .isEqualTo(invencion.getSolicitudesProteccion().get(i).getFechaConcesion().toString());
          });

      // AmbitoGeografico España
      CodigoCVN codigoCVNAmbitoEspania = CodigoCVN.E050_030_010_160;
      final List<ValorCampo> valoresCamposAmbitoEspania = getValoresCamposByCodigoCVN(campos, codigoCVNAmbitoEspania);
      IntStream.range(0, invencion.getParticipaciones().size())
          .parallel().forEach(i -> Assertions.assertThat(valoresCamposAmbitoEspania.get(i).getValor())
              .as(String.format("AmbitoEspania[%d]", i))
              .isEqualTo(getValorMapeoTipos(
                  codigoCVNAmbitoEspania, invencion.getSolicitudesProteccion().get(i).getViaProteccionId())));

      // AmbitoGeografico Europa
      CodigoCVN codigoCVNAmbitoEuropa = CodigoCVN.E050_030_010_170;
      final List<ValorCampo> valoresCamposAmbitoEuropa = getValoresCamposByCodigoCVN(campos, codigoCVNAmbitoEuropa);
      IntStream.range(0, invencion.getParticipaciones().size())
          .parallel().forEach(i -> Assertions.assertThat(valoresCamposAmbitoEuropa.get(i).getValor())
              .as(String.format("AmbitoEuropa[%d]", i))
              .isEqualTo(getValorMapeoTipos(
                  codigoCVNAmbitoEuropa, invencion.getSolicitudesProteccion().get(i).getViaProteccionId())));

      // cuantia
      CodigoCVN codigoCVNCuantia = CodigoCVN.CUANTIA_LICENCIAS;
      ValorCampo valorCampoCuantia = getValorCampoByCodigoCVN(campos, codigoCVNCuantia);
      Assertions.assertThat(valorCampoCuantia.getValor()).as(codigoCVNCuantia.name())
          .isEqualTo(ProduccionCientificaFieldFormatUtil.formatNumber(invencion.getCuantia().toString()));
    });
  }

  private SolicitudProteccionDto generarMockSolicitudProteccion(Long id, Instant fechaConcesion,
      Long viaProteccionId) {
    return SolicitudProteccionDto.builder()
        .id(id)
        .fechaConcesion(fechaConcesion)
        .viaProteccionId(viaProteccionId)
        .build();
  }

  private String getValorMapeoTipos(CodigoCVN codigoCVN, Long idTipoRef) {
    return getMapeoTiposRepository().findByCodigoCVNAndIdTipoRef(codigoCVN, idTipoRef).map(MapeoTipos::getValor)
        .orElse(null);
  }

}
