package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.dto.DetalleGrupoInvestigacionOutput;
import org.crue.hercules.sgi.prc.dto.DetalleProduccionInvestigadorOutput;
import org.crue.hercules.sgi.prc.dto.DetalleProduccionInvestigadorOutput.TipoProduccionInvestigadorOutput;
import org.crue.hercules.sgi.prc.dto.ResumenPuntuacionGrupoAnioOutput;
import org.crue.hercules.sgi.prc.dto.ResumenPuntuacionGrupoAnioOutput.ResumenPuntuacionGrupo;
import org.crue.hercules.sgi.prc.dto.csp.GrupoDto;
import org.crue.hercules.sgi.prc.service.ProduccionCientificaReportService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de report de Baremacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaremacionReportIT extends BaremacionBaseIT {

  private static final String PATH_REPORT_RESUMEN_GRUPOS = "/resumenpuntuaciongrupos/{anio}";
  private static final String PATH_REPORT_DETALLE_INVESTIGADOR = "/detalleproduccioninvestigador/{anio}/{personaRef}";
  private static final String PATH_REPORT_DETALLE_GRUPO = "/detallegrupo/{anio}/{grupoId}";

  @MockBean
  private ProduccionCientificaReportService produccionCientificaReportService;

  protected HttpEntity<Void> buildRequestBaremacionReport(HttpHeaders headers, Void entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "PRC-INF-G")));

    HttpEntity<Void> request = new HttpEntity<>(entity, headers);
    return request;
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_from_json_and_report_resumen_puntuacion_grupos() throws Exception {

    String personaRef = "22932567";
    String nombrePersonaRef = "Nombre apellidos";
    HttpStatus statusCode = baremacionLibroFromJson(personaRef);
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.ACCEPTED);

    BDDMockito.given(getSgiApiCspService()
        .findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(nombrePersonaRef));

    Integer anio = 2021;
    BDDMockito.given(
        produccionCientificaReportService.getDataReportResumenPuntuacionGrupos(anio))
        .willReturn(generateMockResumenPuntuacionGrupoAnioOutput(anio));

    final ResponseEntity<ResumenPuntuacionGrupoAnioOutput> responseReportData = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_REPORT_RESUMEN_GRUPOS, HttpMethod.GET,
        buildRequestBaremacionReport(null, null),
        ResumenPuntuacionGrupoAnioOutput.class, anio);

    Assertions.assertThat(responseReportData.getStatusCode()).isEqualTo(HttpStatus.OK);
    ResumenPuntuacionGrupoAnioOutput reportData = responseReportData.getBody();
    Assertions.assertThat(reportData.getAnio()).as("anio").isEqualTo(anio);
    Assertions.assertThat(reportData.getPuntuacionesGrupos().get(0).getPersonaResponsable()).as("persona responsable")
        .isEqualTo("persona");
    Assertions.assertThat(reportData.getPuntuacionesGrupos().get(0).getPuntosProduccion()).as("puntos produccion")
        .isEqualTo(new BigDecimal("53.43"));
    Assertions.assertThat(reportData.getPuntuacionesGrupos().get(0).getPuntosCostesIndirectos())
        .as("puntos costes indirectos").isEqualTo(new BigDecimal("35.43"));
    Assertions.assertThat(reportData.getPuntuacionesGrupos().get(0).getPuntosSexenios()).as("puntos sexenios")
        .isEqualTo(new BigDecimal("5.43"));

  }

  private ResumenPuntuacionGrupoAnioOutput generateMockResumenPuntuacionGrupoAnioOutput(Integer anio) {
    List<ResumenPuntuacionGrupo> puntuacionesGrupos = new ArrayList<>();
    puntuacionesGrupos.add(ResumenPuntuacionGrupo.builder()
        .grupo("grupo")
        .personaResponsable("persona")
        .puntosSexenios(new BigDecimal("5.43"))
        .puntosCostesIndirectos(new BigDecimal("35.43"))
        .puntosProduccion(new BigDecimal("53.43"))
        .build());
    puntuacionesGrupos.add(ResumenPuntuacionGrupo.builder()
        .grupo("grupo2")
        .personaResponsable("persona2")
        .puntosSexenios(new BigDecimal("15.43"))
        .puntosCostesIndirectos(new BigDecimal("315.43"))
        .puntosProduccion(new BigDecimal("153.43"))
        .build());
    return ResumenPuntuacionGrupoAnioOutput.builder()
        .anio(anio)
        .puntuacionesGrupos(puntuacionesGrupos)
        .build();
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
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void baremacion_libro_from_json_and_report_detalle_investigador() throws Exception {

    String personaRef = "22932567";
    HttpStatus statusCode = baremacionLibroFromJson(personaRef);
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.ACCEPTED);

    Integer anio = 2021;
    BDDMockito.given(
        produccionCientificaReportService.getDataDetalleProduccionInvestigador(anio, personaRef))
        .willReturn(generateMockDetalleProduccionInvestigadorOutput(anio));

    final ResponseEntity<DetalleProduccionInvestigadorOutput> responseReportData = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_REPORT_DETALLE_INVESTIGADOR, HttpMethod.GET,
        buildRequestBaremacionReport(null, null),
        DetalleProduccionInvestigadorOutput.class, anio, personaRef);

    Assertions.assertThat(responseReportData.getStatusCode()).isEqualTo(HttpStatus.OK);
    DetalleProduccionInvestigadorOutput reportData = responseReportData.getBody();
    Assertions.assertThat(reportData.getAnio()).as("anio").isEqualTo(anio);
    Assertions.assertThat(reportData.getTipos().get(0).getPuntos()).as("produccion").isEqualTo(new BigDecimal("5.43"));
    Assertions.assertThat(reportData.getTipos().get(1).getPuntos()).as("libros").isEqualTo(new BigDecimal("7.80"));

  }

  private DetalleProduccionInvestigadorOutput generateMockDetalleProduccionInvestigadorOutput(Integer anio) {
    List<TipoProduccionInvestigadorOutput> tipos = new ArrayList<>();
    tipos.add(TipoProduccionInvestigadorOutput.builder()
        .titulo("produccion")
        .puntos(new BigDecimal("5.43"))
        .build());
    tipos.add(TipoProduccionInvestigadorOutput.builder()
        .titulo("libros")
        .puntos(new BigDecimal("7.80"))
        .build());
    return DetalleProduccionInvestigadorOutput.builder()
        .anio(anio)
        .investigador("investigador")
        .tipos(tipos)
        .build();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:cleanup.sql",
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
  void baremacion_libro_from_json_and_report_detalle_grupo() throws Exception {

    String personaRef = "22932567";
    Long grupoId = 1L;
    HttpStatus statusCode = baremacionLibroFromJson(personaRef);
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.ACCEPTED);

    String nombreGrupo = "Grupo de investigación 1";

    BDDMockito.given(getSgiApiCspService()
        .findGrupoById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(GrupoDto.builder().nombre(nombreGrupo).build()));

    Integer anio = 2021;
    BDDMockito.given(
        produccionCientificaReportService.getDataReportDetalleGrupo(anio, grupoId))
        .willReturn(generateMockDetalleGrupoInvestigacionOutput(anio, nombreGrupo));

    final ResponseEntity<DetalleGrupoInvestigacionOutput> responseReportData = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_REPORT_DETALLE_GRUPO, HttpMethod.GET,
        buildRequestBaremacionReport(null, null),
        DetalleGrupoInvestigacionOutput.class, anio, grupoId);

    Assertions.assertThat(responseReportData.getStatusCode()).isEqualTo(HttpStatus.OK);
    DetalleGrupoInvestigacionOutput reportData = responseReportData.getBody();
    Assertions.assertThat(reportData.getAnio()).as("anio").isEqualTo(anio);
    Assertions.assertThat(reportData.getGrupo()).as("grupo").isEqualTo(nombreGrupo);

  }

  private DetalleGrupoInvestigacionOutput generateMockDetalleGrupoInvestigacionOutput(Integer anio, String grupo) {
    return DetalleGrupoInvestigacionOutput.builder()
        .anio(anio)
        .grupo(grupo)
        .build();
  }

}