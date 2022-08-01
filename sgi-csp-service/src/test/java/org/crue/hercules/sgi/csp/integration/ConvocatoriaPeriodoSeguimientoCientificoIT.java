package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de ConvocatoriaPeriodoSeguimientoCientifico.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConvocatoriaPeriodoSeguimientoCientificoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaperiodoseguimientocientificos";

  private HttpEntity<ConvocatoriaPeriodoSeguimientoCientifico> buildRequest(HttpHeaders headers,
      ConvocatoriaPeriodoSeguimientoCientifico entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-C", "CSP-PRO-E")));

    HttpEntity<ConvocatoriaPeriodoSeguimientoCientifico> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<List<ConvocatoriaPeriodoSeguimientoCientifico>> buildRequestList(HttpHeaders headers,
      List<ConvocatoriaPeriodoSeguimientoCientifico> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-C", "CSP-CON-E")));

    HttpEntity<List<ConvocatoriaPeriodoSeguimientoCientifico>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_ReturnsConvocatoriaPeriodoSeguimientoCientificoList()
      throws Exception {

    // given: una lista con uno de los ConvocatoriaPeriodoSeguimientoCientifico
    // actualizado, otro nuevo y sin los otros 3 periodos existentes
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico newConvocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        null, 27, 30, 1L, TipoSeguimiento.FINAL);
    ConvocatoriaPeriodoSeguimientoCientifico updatedConvocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        4L, 1, 26, 1L, TipoSeguimiento.INTERMEDIO);

    List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificos = Arrays
        .asList(newConvocatoriaPeriodoSeguimientoCientifico, updatedConvocatoriaPeriodoSeguimientoCientifico);

    // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaPeriodoSeguimientoCientifico>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH, buildRequestList(null, convocatoriaPeriodoSeguimientoCientificos),
        new ParameterizedTypeReference<List<ConvocatoriaPeriodoSeguimientoCientifico>>() {
        });

    // then: Se crea el nuevo ConvocatoriaPeriodoSeguimientoCientifico, se actualiza
    // el existe y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<ConvocatoriaPeriodoSeguimientoCientifico> responseData = response.getBody();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getId());
    Assertions.assertThat(responseData.get(0).getConvocatoriaId()).as("get(0).getConvocatoriaId()")
        .isEqualTo(convocatoriaId);
    Assertions.assertThat(responseData.get(0).getMesInicial()).as("get(0).getMesInicial()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(responseData.get(0).getMesFinal()).as("get(0).getMesFinal()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(responseData.get(0).getFechaInicioPresentacion()).as("get(0).getFechaInicioPresentacion()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getFechaInicioPresentacion());
    Assertions.assertThat(responseData.get(0).getFechaFinPresentacion()).as("get(0).getFechaFinPresentacion()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion());
    Assertions.assertThat(responseData.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(responseData.get(0).getTipoSeguimiento()).as("get(0).getTipoSeguimiento()")
        .isIn(TipoSeguimiento.FINAL, TipoSeguimiento.INTERMEDIO, TipoSeguimiento.PERIODICO);
    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getObservaciones());

    Assertions.assertThat(responseData.get(1).getConvocatoriaId()).as("get(1).getConvocatoriaId()")
        .isEqualTo(convocatoriaId);
    Assertions.assertThat(responseData.get(1).getMesInicial()).as("get(1).getMesInicial()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(responseData.get(1).getMesFinal()).as("get(1).getMesFinal()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(responseData.get(1).getFechaInicioPresentacion()).as("get(1).getFechaInicioPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getFechaInicioPresentacion());
    Assertions.assertThat(responseData.get(1).getFechaFinPresentacion()).as("get(1).getFechaFinPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion());
    Assertions.assertThat(responseData.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()").isEqualTo(2);
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getObservaciones());

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "mesInicial,asc";

    URI uriFindAllConvocatoriaPeriodoSeguimientoCientifico = UriComponentsBuilder
        .fromUriString("/convocatorias" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH).queryParam("s", sort)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaPeriodoSeguimientoCientifico>> responseFindAllConvocatoriaPeriodoSeguimientoCientifico = restTemplate
        .exchange(uriFindAllConvocatoriaPeriodoSeguimientoCientifico, HttpMethod.GET, buildRequestList(headers, null),
            new ParameterizedTypeReference<List<ConvocatoriaPeriodoSeguimientoCientifico>>() {
            });

    Assertions.assertThat(responseFindAllConvocatoriaPeriodoSeguimientoCientifico.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaPeriodoSeguimientoCientifico> responseDataFindAll = responseFindAllConvocatoriaPeriodoSeguimientoCientifico
        .getBody();
    Assertions.assertThat(responseDataFindAll.size()).as("size()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientificos.size());
    Assertions.assertThat(responseDataFindAll.get(0).getId()).as("responseDataFindAll.get(0).getId()")
        .isEqualTo(responseData.get(0).getId());
    Assertions.assertThat(responseDataFindAll.get(1).getId()).as("responseDataFindAll.get(1).getId()")
        .isEqualTo(responseData.get(1).getId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsConvocatoriaPeriodoSeguimientoCientifico() throws Exception {
    Long id = 1L;

    final ResponseEntity<ConvocatoriaPeriodoSeguimientoCientifico> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaPeriodoSeguimientoCientifico.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ConvocatoriaPeriodoSeguimientoCientifico responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(1);
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1);
    Assertions.assertThat(responseData.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(responseData.getTipoSeguimiento()).as("getTipoSeguimiento()").isIn(TipoSeguimiento.FINAL,
        TipoSeguimiento.INTERMEDIO, TipoSeguimiento.PERIODICO);
    Assertions.assertThat(responseData.getMesInicial()).as("getMesInicial()").isEqualTo(1);
    Assertions.assertThat(responseData.getMesFinal()).as("getMesFinal()").isEqualTo(2);
    Assertions.assertThat(responseData.getFechaInicioPresentacion()).as("getFechaInicioPresentacion()")
        .isEqualTo(Instant.parse("2020-01-01T00:00:00Z"));
    Assertions.assertThat(responseData.getFechaFinPresentacion()).as("getFechaFinPresentacion()")
        .isEqualTo(Instant.parse("2020-02-01T23:59:59Z"));
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-meses-01-02");
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoSeguimientoCientifico
   * 
   * @param id             id del ConvocatoriaPeriodoSeguimientoCientifico
   * @param mesInicial     Mes inicial
   * @param mesFinal       Mes final
   * @param tipo           Tipo SeguimientoCientifico
   * @param convocatoriaId Id Convocatoria
   * @return el objeto ConvocatoriaPeriodoSeguimientoCientifico
   */
  private ConvocatoriaPeriodoSeguimientoCientifico generarMockConvocatoriaPeriodoSeguimientoCientifico(Long id,
      Integer mesInicial, Integer mesFinal, Long convocatoriaId, TipoSeguimiento tipoSeguimiento) {
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = new ConvocatoriaPeriodoSeguimientoCientifico();
    convocatoriaPeriodoSeguimientoCientifico.setId(id);
    convocatoriaPeriodoSeguimientoCientifico.setConvocatoriaId(convocatoriaId == null ? 1 : convocatoriaId);
    convocatoriaPeriodoSeguimientoCientifico.setNumPeriodo(1);
    convocatoriaPeriodoSeguimientoCientifico.setMesInicial(mesInicial);
    convocatoriaPeriodoSeguimientoCientifico.setMesFinal(mesFinal);
    convocatoriaPeriodoSeguimientoCientifico.setTipoSeguimiento(tipoSeguimiento);
    convocatoriaPeriodoSeguimientoCientifico.setFechaInicioPresentacion(Instant.parse("2020-10-10T00:00:00Z"));
    convocatoriaPeriodoSeguimientoCientifico.setFechaFinPresentacion(Instant.parse("2020-11-20T23:59:59Z"));
    convocatoriaPeriodoSeguimientoCientifico.setObservaciones("observaciones-" + id);

    return convocatoriaPeriodoSeguimientoCientifico;
  }

}
