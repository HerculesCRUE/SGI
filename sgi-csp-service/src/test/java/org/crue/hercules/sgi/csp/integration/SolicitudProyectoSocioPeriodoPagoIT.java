package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de SolicitudProyectoSocioPeriodoPago.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class SolicitudProyectoSocioPeriodoPagoIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectosocioperiodopago";

  private HttpEntity<SolicitudProyectoSocioPeriodoPago> buildRequest(HttpHeaders headers,
      SolicitudProyectoSocioPeriodoPago entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudProyectoSocioPeriodoPago> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<List<SolicitudProyectoSocioPeriodoPago>> buildRequestList(HttpHeaders headers,
      List<SolicitudProyectoSocioPeriodoPago> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<List<SolicitudProyectoSocioPeriodoPago>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitudProyectoSocioPeriodoPago() throws Exception {
    // given: una lista con uno de los SolicitudProyectoSocioPeriodoPago
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoSocioPeriodoPago newSolicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(
        null, 1L);
    SolicitudProyectoSocioPeriodoPago updatedSolicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(
        4L, 1L);

    updatedSolicitudProyectoSocioPeriodoPago.setMes(6);

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagos = Arrays
        .asList(newSolicitudProyectoSocioPeriodoPago, updatedSolicitudProyectoSocioPeriodoPago);

    // when: updateSolicitudProyectoSocioPeriodoPago
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<SolicitudProyectoSocioPeriodoPago>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH, buildRequestList(null, solicitudProyectoSocioPeriodoPagos),
        new ParameterizedTypeReference<List<SolicitudProyectoSocioPeriodoPago>>() {
        });

    // then: Se crea el nuevo SolicitudProyectoSocioPeriodoPago, se actualiza el
    // existe y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<SolicitudProyectoSocioPeriodoPago> responseData = response.getBody();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "mes,asc";

    URI uriFindAllSolicitudProyectoSocioPeriodoPago = UriComponentsBuilder
        .fromUriString("/solicitudproyectosocio" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH).queryParam("s", sort)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<SolicitudProyectoSocioPeriodoPago>> responseFindAllSolicitudProyectoSocioPeriodoPago = restTemplate
        .exchange(uriFindAllSolicitudProyectoSocioPeriodoPago, HttpMethod.GET, buildRequestList(headers, null),
            new ParameterizedTypeReference<List<SolicitudProyectoSocioPeriodoPago>>() {
            });

    Assertions.assertThat(responseFindAllSolicitudProyectoSocioPeriodoPago.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoSocioPeriodoPago> responseDataFindAll = responseFindAllSolicitudProyectoSocioPeriodoPago
        .getBody();
    Assertions.assertThat(responseDataFindAll.size()).as("size()").isEqualTo(solicitudProyectoSocioPeriodoPagos.size());
    Assertions.assertThat(responseDataFindAll.get(0).getId()).as("responseDataFindAll.get(0).getId()")
        .isEqualTo(responseData.get(0).getId());
    Assertions.assertThat(responseDataFindAll.get(1).getId()).as("responseDataFindAll.get(1).getId()")
        .isEqualTo(responseData.get(1).getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudProyectoSocioPeriodoPago() throws Exception {
    Long idSolicitudProyectoSocioPeriodoPago = 1L;

    final ResponseEntity<SolicitudProyectoSocioPeriodoPago> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoSocioPeriodoPago.class, idSolicitudProyectoSocioPeriodoPago);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = response.getBody();
    Assertions.assertThat(solicitudProyectoSocioPeriodoPago.getId()).as("getId()")
        .isEqualTo(idSolicitudProyectoSocioPeriodoPago);
    Assertions.assertThat(solicitudProyectoSocioPeriodoPago.getSolicitudProyectoSocioId())
        .as("getSolicitudProyectoSocio().getId()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoSocioPeriodoPago.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(3);
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioPeriodoPago
   * 
   * @param solicitudProyectoSocioPeriodoPagoId
   * @param solicitudProyectoSocioId
   * @return el objeto SolicitudProyectoSocioPeriodoPago
   */
  private SolicitudProyectoSocioPeriodoPago generarSolicitudProyectoSocioPeriodoPago(
      Long solicitudProyectoSocioPeriodoPagoId, Long solicitudProyectoSocioId) {

    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = SolicitudProyectoSocioPeriodoPago.builder()
        .id(solicitudProyectoSocioPeriodoPagoId).solicitudProyectoSocioId(solicitudProyectoSocioId).numPeriodo(3)
        .importe(new BigDecimal(789)).mes(3).build();
    return solicitudProyectoSocioPeriodoPago;
  }

}
