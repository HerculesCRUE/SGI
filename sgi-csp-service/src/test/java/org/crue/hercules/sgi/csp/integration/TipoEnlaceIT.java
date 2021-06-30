package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
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
 * Test de integracion de TipoEnlace.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoEnlaceIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/tipoenlaces";

  private HttpEntity<TipoEnlace> buildRequest(HttpHeaders headers, TipoEnlace entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TENL-V", "CSP-TENL-C",
        "CSP-TENL-E", "CSP-TENL-B", "CSP-ME-C", "CSP-ME-E", "CSP-TENL-R", "AUTH")));

    HttpEntity<TipoEnlace> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsTipoEnlace() throws Exception {

    // given: new TipoEnlace
    TipoEnlace data = TipoEnlace.builder().nombre("nombre-1").descripcion("descripcion-1").activo(Boolean.TRUE).build();

    // when: create TipoEnlace
    final ResponseEntity<TipoEnlace> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, data), TipoEnlace.class);

    // then: new TipoEnlace is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    TipoEnlace responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(data.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(data.getDescripcion());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(data.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsTipoEnlace() throws Exception {

    // given: existing TipoEnlace to be updated
    TipoEnlace data = TipoEnlace.builder().id(1L).nombre("nombre-updated").descripcion("descripcion-updated")
        .activo(Boolean.TRUE).build();

    // when: update TipoEnlace
    final ResponseEntity<TipoEnlace> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, data), TipoEnlace.class, data.getId());

    // then: TipoEnlace is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoEnlace responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(data.getId());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(data.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(data.getDescripcion());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(data.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void reactivar_ReturnTipoEnlace() throws Exception {
    Long idTipoEnlace = 1L;

    final ResponseEntity<TipoEnlace> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        TipoEnlace.class, idTipoEnlace);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoEnlace tipoEnlaceDisabled = response.getBody();
    Assertions.assertThat(tipoEnlaceDisabled.getId()).as("getId()").isEqualTo(idTipoEnlace);
    Assertions.assertThat(tipoEnlaceDisabled.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(tipoEnlaceDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(tipoEnlaceDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnTipoEnlace() throws Exception {
    Long idTipoEnlace = 1L;

    final ResponseEntity<TipoEnlace> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), TipoEnlace.class, idTipoEnlace);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoEnlace tipoEnlaceDisabled = response.getBody();
    Assertions.assertThat(tipoEnlaceDisabled.getId()).as("getId()").isEqualTo(idTipoEnlace);
    Assertions.assertThat(tipoEnlaceDisabled.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(tipoEnlaceDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(tipoEnlaceDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.FALSE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsTipoEnlace() throws Exception {
    Long id = 1L;

    final ResponseEntity<TipoEnlace> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), TipoEnlace.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoEnlace responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoEnlaceSubList() throws Exception {

    // given: data for TipoEnlace

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    // when: find TipoEnlace
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<TipoEnlace>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoEnlace>>() {
        });

    // given: TipoEnlace data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEnlace> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsTipoEnlaceSubList() throws Exception {

    // given: data for TipoEnlace

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    // when: find TipoEnlace
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();
    final ResponseEntity<List<TipoEnlace>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoEnlace>>() {
        });

    // given: TipoEnlace data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEnlace> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

}
