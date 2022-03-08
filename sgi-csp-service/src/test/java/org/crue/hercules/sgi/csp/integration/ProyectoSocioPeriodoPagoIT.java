package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
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
 * Test de integracion de ProyectoSocioPeriodoPago.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoSocioPeriodoPagoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectosocioperiodopagos";

  private HttpEntity<ProyectoSocioPeriodoPago> buildRequest(HttpHeaders headers, ProyectoSocioPeriodoPago entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-E", "AUTH")));

    HttpEntity<ProyectoSocioPeriodoPago> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<List<ProyectoSocioPeriodoPago>> buildRequestList(HttpHeaders headers,
      List<ProyectoSocioPeriodoPago> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-E")));

    HttpEntity<List<ProyectoSocioPeriodoPago>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/rol_socio.sql",
      "classpath:scripts/proyecto_socio.sql", "classpath:scripts/proyecto_socio_periodo_pago.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoSocioPeriodoPago() throws Exception {
    // given: una lista con uno de los ProyectoSocioPeriodoPago actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    ProyectoSocioPeriodoPago newProyectoSocioPeriodoPago = generarMockProyectoSocioPeriodoPago(null);
    ProyectoSocioPeriodoPago updatedProyectoSocioPeriodoPago = generarMockProyectoSocioPeriodoPago(2L);

    List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagos = Arrays.asList(newProyectoSocioPeriodoPago,
        updatedProyectoSocioPeriodoPago);

    // when: updateProyectoSocioPeriodoPago
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<ProyectoSocioPeriodoPago>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequestList(null, proyectoSocioPeriodoPagos),
        new ParameterizedTypeReference<List<ProyectoSocioPeriodoPago>>() {
        });

    // then: Se crea el nuevo ProyectoSocioPeriodoPago, se actualiza el
    // existe y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<ProyectoSocioPeriodoPago> responseData = response.getBody();

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");

    URI uriFindAllProyectoSocioPeriodoPago = UriComponentsBuilder
        .fromUriString("/proyectosocios" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<ProyectoSocioPeriodoPago>> responseFindAllProyectoSocioPeriodoPago = restTemplate
        .exchange(uriFindAllProyectoSocioPeriodoPago, HttpMethod.GET, buildRequestList(headers, null),
            new ParameterizedTypeReference<List<ProyectoSocioPeriodoPago>>() {
            });

    Assertions.assertThat(responseFindAllProyectoSocioPeriodoPago.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoSocioPeriodoPago> responseDataFindAll = responseFindAllProyectoSocioPeriodoPago.getBody();
    Assertions.assertThat(responseDataFindAll.size()).as("size()").isEqualTo(proyectoSocioPeriodoPagos.size());
    Assertions.assertThat(responseDataFindAll.get(0).getFechaPrevistaPago())
        .as("responseDataFindAll.get(0).getFechaPrevistaPago()").isEqualTo(responseData.get(0).getFechaPrevistaPago());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/rol_socio.sql",
      "classpath:scripts/proyecto_socio.sql", "classpath:scripts/proyecto_socio_periodo_pago.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoSocioPeriodoPago() throws Exception {
    Long idProyectoSocioPeriodoPago = 1L;

    final ResponseEntity<ProyectoSocioPeriodoPago> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ProyectoSocioPeriodoPago.class, idProyectoSocioPeriodoPago);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProyectoSocioPeriodoPago proyectoSocioPeriodoPago = response.getBody();
    Assertions.assertThat(proyectoSocioPeriodoPago.getId()).as("getId()").isEqualTo(idProyectoSocioPeriodoPago);
    Assertions.assertThat(proyectoSocioPeriodoPago.getProyectoSocioId()).as("getProyectoSocioId()").isEqualTo(1);
    Assertions.assertThat(proyectoSocioPeriodoPago.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
  }

  /**
   * Función que devuelve un objeto ProyectoSocioPeriodoPago
   * 
   * @param id id del ProyectoSocioPeriodoPago
   * 
   * @return el objeto ProyectoSocioPeriodoPago
   */
  private ProyectoSocioPeriodoPago generarMockProyectoSocioPeriodoPago(Long id) {

    // @formatter:off
    ProyectoSocioPeriodoPago proyectoSocioPeriodoPago = ProyectoSocioPeriodoPago.builder()
      .id(id)
      .numPeriodo(1)
      .proyectoSocioId(1L)
      .fechaPrevistaPago(Instant.parse("2022-01-05T00:00:00Z"))
      .importe(new BigDecimal(25811)).build();
    // @formatter:on

    return proyectoSocioPeriodoPago;
  }
}
