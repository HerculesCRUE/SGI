package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.LineaInvestigacionOutput;
import org.crue.hercules.sgi.csp.model.LineaInvestigacion;
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
 * Test de integracion de LineaInvestigacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineaInvestigacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/lineasinvestigacion";

  private HttpEntity<LineaInvestigacion> buildRequest(HttpHeaders headers, LineaInvestigacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-LIN-V", "CSP-LIN-C",
        "CSP-LIN-E", "CSP-LIN-B", "CSP-LIN-R", "AUTH")));

    HttpEntity<LineaInvestigacion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsLineaInvestigacion() throws Exception {

    // given: new LineaInvestigacion
    LineaInvestigacion data = LineaInvestigacion.builder().nombre("nombre-1").activo(Boolean.TRUE).build();

    // when: create LineaInvestigacion
    final ResponseEntity<LineaInvestigacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, data), LineaInvestigacion.class);

    // then: new LineaInvestigacion is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    LineaInvestigacion responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(data.getNombre());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(data.getActivo());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/linea_investigacion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsLineaInvestigacion() throws Exception {

    // given: existing LineaInvestigacion to be updated
    LineaInvestigacion data = LineaInvestigacion.builder().id(2L).nombre("nombre-updated")
        .activo(Boolean.TRUE).build();

    // when: update LineaInvestigacion
    final ResponseEntity<LineaInvestigacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, data), LineaInvestigacion.class, data.getId());

    // then: LineaInvestigacion is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    LineaInvestigacion responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(data.getId());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(data.getNombre());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(data.getActivo());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/linea_investigacion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void reactivar_ReturnLineaInvestigacion() throws Exception {
    Long idLineaInvestigacion = 1L;

    final ResponseEntity<LineaInvestigacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        LineaInvestigacion.class, idLineaInvestigacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    LineaInvestigacion lineaInvestigacionDisabled = response.getBody();
    Assertions.assertThat(lineaInvestigacionDisabled.getId()).as("getId()").isEqualTo(idLineaInvestigacion);
    Assertions.assertThat(lineaInvestigacionDisabled.getNombre()).as("getNombre()")
        .isEqualTo("Psicología Laboral u Organizacional");
    Assertions.assertThat(lineaInvestigacionDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/linea_investigacion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnLineaInvestigacion() throws Exception {
    Long idLineaInvestigacion = 3L;

    final ResponseEntity<LineaInvestigacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), LineaInvestigacionOutput.class, idLineaInvestigacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    LineaInvestigacionOutput lineaInvestigacionDisabled = response.getBody();
    Assertions.assertThat(lineaInvestigacionDisabled.getId()).as("getId()").isEqualTo(idLineaInvestigacion);
    Assertions.assertThat(lineaInvestigacionDisabled.getNombre()).as("getNombre()").isEqualTo("Psicología general");
    Assertions.assertThat(lineaInvestigacionDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.FALSE);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/linea_investigacion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsLineaInvestigacion() throws Exception {
    Long id = 1L;

    final ResponseEntity<LineaInvestigacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), LineaInvestigacionOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    LineaInvestigacionOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo("Psicología Laboral u Organizacional");
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.FALSE);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/linea_investigacion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsLineaInvestigacionSubList() throws Exception {

    // given: data for LineaInvestigacion

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "nombre=ke=Psicología";

    // when: find LineaInvestigacion
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<LineaInvestigacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<LineaInvestigacion>>() {
        });

    // given: LineaInvestigacion data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<LineaInvestigacion> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(responseData.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("Psicología general");
    Assertions.assertThat(responseData.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("Psicología Clínica de la Salud");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/linea_investigacion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodos_WithPagingSortingAndFiltering_ReturnsLineaInvestigacionSubList() throws Exception {

    // given: data for LineaInvestigacion

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-LIN-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "nombre=ke=Psicología";

    // when: find LineaInvestigacion
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();
    final ResponseEntity<List<LineaInvestigacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<LineaInvestigacion>>() {
        });

    // given: LineaInvestigacion data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<LineaInvestigacion> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("Psicología Laboral u Organizacional");
    Assertions.assertThat(responseData.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("Psicología Clínica de la Salud");
    Assertions.assertThat(responseData.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("Psicología general");

  }

}
