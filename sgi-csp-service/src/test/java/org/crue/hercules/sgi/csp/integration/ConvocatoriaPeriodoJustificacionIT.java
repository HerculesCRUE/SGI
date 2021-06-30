package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
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
 * Test de integracion de ConvocatoriaPeriodoJustificacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConvocatoriaPeriodoJustificacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaperiodojustificaciones";

  private HttpEntity<ConvocatoriaPeriodoJustificacion> buildRequest(HttpHeaders headers,
      ConvocatoriaPeriodoJustificacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH")));

    HttpEntity<ConvocatoriaPeriodoJustificacion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<List<ConvocatoriaPeriodoJustificacion>> buildRequestList(HttpHeaders headers,
      List<ConvocatoriaPeriodoJustificacion> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-E", "CSP-CON-C")));

    HttpEntity<List<ConvocatoriaPeriodoJustificacion>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void updateConvocatoriaPeriodoJustificacionesConvocatoria_ReturnsConvocatoriaPeriodoJustificacionList()
      throws Exception {

    // given: una lista con uno de los ConvocatoriaPeriodoJustificacion actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoJustificacion newConvocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null, 27, 30, ConvocatoriaPeriodoJustificacion.Tipo.FINAL, 1L);
    ConvocatoriaPeriodoJustificacion updatedConvocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        4L, 24, 26, ConvocatoriaPeriodoJustificacion.Tipo.PERIODICO, 1L);

    List<ConvocatoriaPeriodoJustificacion> convocatoriaPeriodoJustificaciones = Arrays
        .asList(newConvocatoriaPeriodoJustificacion, updatedConvocatoriaPeriodoJustificacion);

    // when: updateConvocatoriaPeriodoJustificacionesConvocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaPeriodoJustificacion>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequestList(null, convocatoriaPeriodoJustificaciones),
        new ParameterizedTypeReference<List<ConvocatoriaPeriodoJustificacion>>() {
        });

    // then: Se crea el nuevo ConvocatoriaPeriodoJustificacion, se actualiza el
    // existe y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<ConvocatoriaPeriodoJustificacion> responseData = response.getBody();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getId());
    Assertions.assertThat(responseData.get(0).getConvocatoriaId()).as("get(0).getConvocatoriaId()")
        .isEqualTo(convocatoriaId);
    Assertions.assertThat(responseData.get(0).getMesInicial()).as("get(0).getMesInicial()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(responseData.get(0).getMesFinal()).as("get(0).getMesFinal()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(responseData.get(0).getFechaInicioPresentacion()).as("get(0).getFechaInicioPresentacion()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(responseData.get(0).getFechaFinPresentacion()).as("get(0).getFechaFinPresentacion()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(responseData.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getObservaciones());
    Assertions.assertThat(responseData.get(0).getTipo()).as("get(0).getTipo()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getTipo());

    Assertions.assertThat(responseData.get(1).getConvocatoriaId()).as("get(1).getConvocatoriaId()")
        .isEqualTo(convocatoriaId);
    Assertions.assertThat(responseData.get(1).getMesInicial()).as("get(1).getMesInicial()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(responseData.get(1).getMesFinal()).as("get(1).getMesFinal()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(responseData.get(1).getFechaInicioPresentacion()).as("get(1).getFechaInicioPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(responseData.get(1).getFechaFinPresentacion()).as("get(1).getFechaFinPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(responseData.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()").isEqualTo(2);
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getObservaciones());
    Assertions.assertThat(responseData.get(1).getTipo()).as("get(1).getTipo()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getTipo());

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "mesInicial,asc";

    URI uriFindAllConvocatoriaPeriodoJustificacion = UriComponentsBuilder
        .fromUriString("/convocatorias" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH).queryParam("s", sort)
        .buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ConvocatoriaPeriodoJustificacion>> responseFindAllConvocatoriaPeriodoJustificacion = restTemplate
        .exchange(uriFindAllConvocatoriaPeriodoJustificacion, HttpMethod.GET, buildRequestList(headers, null),
            new ParameterizedTypeReference<List<ConvocatoriaPeriodoJustificacion>>() {
            });

    Assertions.assertThat(responseFindAllConvocatoriaPeriodoJustificacion.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaPeriodoJustificacion> responseDataFindAll = responseFindAllConvocatoriaPeriodoJustificacion
        .getBody();
    Assertions.assertThat(responseDataFindAll.size()).as("size()").isEqualTo(convocatoriaPeriodoJustificaciones.size());
    Assertions.assertThat(responseDataFindAll.get(0).getId()).as("responseDataFindAll.get(0).getId()")
        .isEqualTo(responseData.get(0).getId());
    Assertions.assertThat(responseDataFindAll.get(1).getId()).as("responseDataFindAll.get(1).getId()")
        .isEqualTo(responseData.get(1).getId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaPeriodoJustificacion() throws Exception {
    Long idConvocatoriaPeriodoJustificacion = 1L;

    final ResponseEntity<ConvocatoriaPeriodoJustificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaPeriodoJustificacion.class, idConvocatoriaPeriodoJustificacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = response.getBody();
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getId()).as("getId()")
        .isEqualTo(idConvocatoriaPeriodoJustificacion);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getMesInicial()).as("getMesInicial()").isEqualTo(1);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getMesFinal()).as("getMesFinal()").isEqualTo(2);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getFechaInicioPresentacion())
        .as("getFechaInicioPresentacion()").isEqualTo(Instant.parse("2020-10-10T00:00:00Z"));
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getFechaFinPresentacion()).as("getFechaFinPresentacion()")
        .isEqualTo(Instant.parse("2020-11-20T23:59:59Z"));
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-001");
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getTipo()).as("getTipo()")
        .isEqualTo(ConvocatoriaPeriodoJustificacion.Tipo.PERIODICO);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoJustificacion
   * 
   * @param id             id del ConvocatoriaPeriodoJustificacion
   * @param mesInicial     Mes inicial
   * @param mesFinal       Mes final
   * @param tipo           Tipo justificacion
   * @param convocatoriaId Id Convocatoria
   * @return el objeto ConvocatoriaPeriodoJustificacion
   */
  private ConvocatoriaPeriodoJustificacion generarMockConvocatoriaPeriodoJustificacion(Long id, Integer mesInicial,
      Integer mesFinal, ConvocatoriaPeriodoJustificacion.Tipo tipo, Long convocatoriaId) {
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = new ConvocatoriaPeriodoJustificacion();
    convocatoriaPeriodoJustificacion.setId(id);
    convocatoriaPeriodoJustificacion.setConvocatoriaId(convocatoriaId == null ? 1 : convocatoriaId);
    convocatoriaPeriodoJustificacion.setNumPeriodo(1);
    convocatoriaPeriodoJustificacion.setMesInicial(mesInicial);
    convocatoriaPeriodoJustificacion.setMesFinal(mesFinal);
    convocatoriaPeriodoJustificacion.setFechaInicioPresentacion(Instant.parse("2020-10-10T00:00:00Z"));
    convocatoriaPeriodoJustificacion.setFechaFinPresentacion(Instant.parse("2020-11-20T23:59:59Z"));
    convocatoriaPeriodoJustificacion.setObservaciones("observaciones-" + id);
    convocatoriaPeriodoJustificacion.setTipo(tipo);

    return convocatoriaPeriodoJustificacion;
  }

}
