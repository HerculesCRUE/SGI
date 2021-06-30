package org.crue.hercules.sgi.usr.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.usr.model.Unidad;
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
 * Test de integracion de Unidad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UnidadIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/unidades";

  private HttpEntity<Unidad> buildRequest(HttpHeaders headers, Unidad entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V_1", "CSP-SOL-E")));

    HttpEntity<Unidad> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsUnidad() throws Exception {
    Long idUnidad = 1L;

    final ResponseEntity<Unidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Unidad.class, idUnidad);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Unidad unidad = response.getBody();
    Assertions.assertThat(unidad.getId()).as("getId()").isNotNull();
    Assertions.assertThat(unidad.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(unidad.getAcronimo()).as("getAcronimo()").isEqualTo("acronimo-1");
    Assertions.assertThat(unidad.getDescripcion()).as("descripcion-001").isEqualTo(unidad.getDescripcion());
    Assertions.assertThat(unidad.getActivo()).as("getActivo()").isTrue();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsUnidadSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=\"00\"";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<Unidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Unidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Unidad> unidades = response.getBody();
    Assertions.assertThat(unidades.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(unidades.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(unidades.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(unidades.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodosRestringidos_WithPagingSortingAndFiltering_ReturnsUnidadSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/restringidos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Unidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Unidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Unidad> unidades = response.getBody();
    Assertions.assertThat(unidades.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(unidades.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }
}