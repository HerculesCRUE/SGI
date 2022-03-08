package org.crue.hercules.sgi.pii.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.controller.TramoRepartoController;
import org.crue.hercules.sgi.pii.dto.TramoRepartoInput;
import org.crue.hercules.sgi.pii.dto.TramoRepartoOutput;
import org.crue.hercules.sgi.pii.model.TramoReparto.Tipo;
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
 * Test de integracion de TramoReparto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TramoRepartoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = TramoRepartoController.MAPPING;
  private static final String PATH_MODIFICABLE = "/modificable";

  private HttpEntity<TramoRepartoInput> buildRequest(HttpHeaders headers,
      TramoRepartoInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<TramoRepartoInput> request = new HttpEntity<>(entity, headers);

    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tramo_reparto.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTramoRepartoOutputSubList() throws Exception {

    String[] roles = { "PII-TRE-V", "PII-TRE-C", "PII-TRE-E", "PII-TRE-B", "PII-TRE-R", "PII-INV-V", "PII-INV-E" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "porcentajeInventores=ge=40";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TramoRepartoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TramoRepartoOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TramoRepartoOutput> tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");

    Assertions.assertThat(tramoRepartoOutput.get(0).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(tramoRepartoOutput.get(1).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tramo_reparto.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsTramoRepartoOutput() throws Exception {

    String[] roles = { "PII-TRE-V", "PII-TRE-C", "PII-TRE-E", "PII-TRE-B", "PII-TRE-R" };
    Long tramoRepartoOutputId = 1L;

    final ResponseEntity<TramoRepartoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH
        + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null, roles), TramoRepartoOutput.class, tramoRepartoOutputId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final TramoRepartoOutput tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(tramoRepartoOutput.getDesde()).as("desde").isEqualTo(1);
    Assertions.assertThat(tramoRepartoOutput.getHasta()).as("hasta").isEqualTo(500);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tramo_reparto.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsTramoRepartoOutput() throws Exception {

    String[] roles = { "PII-TRE-C" };
    TramoRepartoInput nuevoTramoRepartoInput = generaMockTramoRepartoInput();

    final ResponseEntity<TramoRepartoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, nuevoTramoRepartoInput, roles), TramoRepartoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final TramoRepartoOutput tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput.getId()).as("id").isEqualTo(3);
    Assertions.assertThat(tramoRepartoOutput.getDesde()).as("desde").isEqualTo(1001);
    Assertions.assertThat(tramoRepartoOutput.getHasta()).as("hasta").isEqualTo(10000);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tramo_reparto.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsTramoRepartoOutput() throws Exception {

    String[] roles = { "PII-TRE-E" };
    Long tramoRepartoId = 2L;
    TramoRepartoInput nuevoTramoRepartoInput = generaMockTramoRepartoInput();
    nuevoTramoRepartoInput.setDesde(501);
    nuevoTramoRepartoInput.setHasta(999);

    final ResponseEntity<TramoRepartoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT,
        buildRequest(null, nuevoTramoRepartoInput, roles), TramoRepartoOutput.class, tramoRepartoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final TramoRepartoOutput tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput.getId()).as("id").isEqualTo(2);
    Assertions.assertThat(tramoRepartoOutput.getDesde()).as("desde").isEqualTo(501);
    Assertions.assertThat(tramoRepartoOutput.getHasta()).as("hasta").isEqualTo(999);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tramo_reparto.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void deleteTramoReparto_Success() throws Exception {

    String[] roles = { "PII-TRE-B" };
    Long tramoRepartoId = 2L;

    final ResponseEntity<TramoRepartoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE,
        buildRequest(null, null, roles), TramoRepartoOutput.class, tramoRepartoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tramo_reparto.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void isTramoRepartoModificable_Success() throws Exception {

    String[] roles = { "PII-TRE-V", "PII-TRE-C", "PII-TRE-E" };
    Long tramoRepartoId = 2L;

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_MODIFICABLE,
        HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class, tramoRepartoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  /**
   * Función que devuelve un objeto TramoRepartoInput
   * 
   * @return el objeto TramoRepartoInput
   */
  private TramoRepartoInput generaMockTramoRepartoInput() {
    TramoRepartoInput tramoRepartoInput = new TramoRepartoInput();
    tramoRepartoInput.setDesde(1001);
    tramoRepartoInput.setHasta(10000);
    tramoRepartoInput.setPorcentajeInventores(BigDecimal.valueOf(50));
    tramoRepartoInput.setPorcentajeUniversidad(BigDecimal.valueOf(50));
    tramoRepartoInput.setTipo(Tipo.INTERMEDIO);

    return tramoRepartoInput;
  }

}
