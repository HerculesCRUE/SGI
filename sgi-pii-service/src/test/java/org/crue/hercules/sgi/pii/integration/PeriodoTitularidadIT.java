package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.controller.PeriodoTitularidadController;
import org.crue.hercules.sgi.pii.dto.PeriodoTitularidadInput;
import org.crue.hercules.sgi.pii.dto.PeriodoTitularidadOutput;
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
 * Test de integracion de PeriodoTitularidad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class PeriodoTitularidadIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = PeriodoTitularidadController.MAPPING;
  private static final String PATH_PERIODOTITULARIDAD_ID = "/{periodoTitularidadId}";

  private HttpEntity<PeriodoTitularidadInput> buildRequest(HttpHeaders headers,
      PeriodoTitularidadInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<PeriodoTitularidadInput> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/invencion.sql",
      "classpath:scripts/periodo_titularidad.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnPeriodoTitularidadOutputSubList() throws Exception {

    String[] roles = { "PII-INV-C", "PII-INV-E", "PII-INV-V" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "fechaInicio=ge=2020-01-01T00:00:00Z";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<PeriodoTitularidadOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<PeriodoTitularidadOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeriodoTitularidadOutput> periodoTitularidadOutput = response.getBody();
    Assertions.assertThat(periodoTitularidadOutput).hasSize(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    Assertions.assertThat(periodoTitularidadOutput.get(0).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(periodoTitularidadOutput.get(1).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(periodoTitularidadOutput.get(2).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/invencion.sql",
      "classpath:scripts/periodo_titularidad.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnPeriodoTitularidadOutput() throws Exception {

    String[] roles = { "PII-INV-C" };
    PeriodoTitularidadInput periodoTitularidadInput = generaMockPeriodoTitularidadInput();

    final ResponseEntity<PeriodoTitularidadOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST,
        buildRequest(null, periodoTitularidadInput, roles), PeriodoTitularidadOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final PeriodoTitularidadOutput periodoTitularidadOutput = response.getBody();
    Assertions.assertThat(periodoTitularidadOutput.getId()).as("id").isEqualTo(4);
    Assertions.assertThat(periodoTitularidadOutput.getInvencionId()).as("invencionId").isEqualTo(1);
    Assertions.assertThat(periodoTitularidadOutput.getFechaInicio()).as("FechaInicio").isEqualTo(
        Instant.parse("2020-10-19T00:00:00Z"));

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/invencion.sql",
      "classpath:scripts/periodo_titularidad.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deletePeriodoTitularidad_Success() throws Exception {

    String[] roles = { "PII-INV-E", "PII-INV-B" };
    Long periodoTitularidadId = 3L;

    final ResponseEntity<PeriodoTitularidadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PERIODOTITULARIDAD_ID,
        HttpMethod.DELETE,
        buildRequest(null, null, roles), PeriodoTitularidadOutput.class, periodoTitularidadId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/invencion.sql",
      "classpath:scripts/periodo_titularidad.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnPeriodoTitularidadOutput() throws Exception {

    String[] roles = { "PII-INV-C", "PII-INV-E" };
    Long periodoTitularidadId = 3L;
    PeriodoTitularidadInput periodoTitularidadInput = generaMockPeriodoTitularidadInput();
    periodoTitularidadInput.setFechaInicio(Instant.parse("2020-02-18T22:00:00Z"));

    final ResponseEntity<PeriodoTitularidadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PERIODOTITULARIDAD_ID,
        HttpMethod.PUT,
        buildRequest(null, periodoTitularidadInput, roles), PeriodoTitularidadOutput.class, periodoTitularidadId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final PeriodoTitularidadOutput periodoTitularidadOutput = response.getBody();
    Assertions.assertThat(periodoTitularidadOutput.getId()).as("id").isEqualTo(3);
    Assertions.assertThat(periodoTitularidadOutput.getInvencionId()).as("invencionId").isEqualTo(1);
    Assertions.assertThat(periodoTitularidadOutput.getFechaInicio()).as("fechaInicio")
        .isEqualTo((Instant.parse("2020-02-18T22:00:00Z")));

  }

  /**
   * Función que devuelve un objeto PeriodoTitularidadInput
   * 
   * @return el objeto PeriodoTitularidadInput
   */
  private PeriodoTitularidadInput generaMockPeriodoTitularidadInput() {
    PeriodoTitularidadInput periodoTitularidadInput = new PeriodoTitularidadInput();
    periodoTitularidadInput.setFechaFin(Instant.parse("2020-10-20T22:59:59Z"));
    periodoTitularidadInput.setFechaFinPrevious(null);
    periodoTitularidadInput.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    periodoTitularidadInput.setInvencionId(1L);
    return periodoTitularidadInput;
  }
}
