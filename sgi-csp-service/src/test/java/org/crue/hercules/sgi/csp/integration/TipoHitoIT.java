package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoHito;
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
 * Test de integracion de TipoHito.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoHitoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String TIPO_HITO_CONTROLLER_BASE_PATH = "/tipohitos";

  private HttpEntity<TipoHito> buildRequest(HttpHeaders headers, TipoHito entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-THITO-V",
        "CSP-THITO-C", "CSP-THITO-E", "CSP-THITO-B", "CSP-THITO-R", "CSP-ME-C", "CSP-ME-E", "AUTH")));

    HttpEntity<TipoHito> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithId_ReturnsTipoHito() throws Exception {
    final ResponseEntity<TipoHito> response = restTemplate.exchange(TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), TipoHito.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoHito tipoHito = response.getBody();

    Assertions.assertThat(tipoHito.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoHito.getNombre()).as("getNombre()").isEqualTo("TipoHito1");
    Assertions.assertThat(tipoHito.getDescripcion()).as("getDescripcion()").isEqualTo("Descripcion1");
    Assertions.assertThat(tipoHito.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsTipoHito() throws Exception {

    TipoHito tipoHito = generarMockTipoHito(null);

    final ResponseEntity<TipoHito> response = restTemplate.exchange(TIPO_HITO_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, tipoHito), TipoHito.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    TipoHito tipoHitoCreado = response.getBody();
    Assertions.assertThat(tipoHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(tipoHitoCreado.getNombre()).as("getNombre()").isEqualTo(tipoHito.getNombre());
    Assertions.assertThat(tipoHitoCreado.getDescripcion()).as("getDescripcion()").isEqualTo(tipoHito.getDescripcion());
    Assertions.assertThat(tipoHitoCreado.getActivo()).as("getActivo()").isEqualTo(true);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void reactivar_ReturnTipoHito() throws Exception {
    Long idTipoHito = 1L;

    final ResponseEntity<TipoHito> response = restTemplate.exchange(
        TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), TipoHito.class, idTipoHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoHito tipoHitoDisabled = response.getBody();
    Assertions.assertThat(tipoHitoDisabled.getId()).as("getId()").isEqualTo(idTipoHito);
    Assertions.assertThat(tipoHitoDisabled.getNombre()).as("getNombre()").isEqualTo("nombre1");
    Assertions.assertThat(tipoHitoDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("descripción1");
    Assertions.assertThat(tipoHitoDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnTipoHito() throws Exception {
    Long idTipoHito = 1L;

    final ResponseEntity<TipoHito> response = restTemplate.exchange(
        TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), TipoHito.class, idTipoHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoHito tipoHitoDisabled = response.getBody();
    Assertions.assertThat(tipoHitoDisabled.getId()).as("getId()").isEqualTo(idTipoHito);
    Assertions.assertThat(tipoHitoDisabled.getNombre()).as("getNombre()").isEqualTo("nombre1");
    Assertions.assertThat(tipoHitoDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("descripción1");
    Assertions.assertThat(tipoHitoDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.FALSE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsTipoHito() throws Exception {
    Long idTipoHito = 1L;
    TipoHito tipoHito = generarMockTipoHito(idTipoHito, "nombre-actualizado");

    final ResponseEntity<TipoHito> response = restTemplate.exchange(TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, tipoHito), TipoHito.class, idTipoHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoHito tipoHitoActualizado = response.getBody();
    Assertions.assertThat(tipoHitoActualizado.getId()).as("getId()").isEqualTo(tipoHito.getId());
    Assertions.assertThat(tipoHitoActualizado.getNombre()).as("getNombre()").isEqualTo(tipoHito.getNombre());
    Assertions.assertThat(tipoHitoActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoHito.getDescripcion());
    Assertions.assertThat(tipoHitoActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoHitoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_HITO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoHito>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoHito> tiposHito = response.getBody();
    Assertions.assertThat(tiposHito.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tiposHito.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tiposHito.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tiposHito.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsTipoHitoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_HITO_CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoHito>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoHito> tiposHito = response.getBody();
    Assertions.assertThat(tiposHito.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tiposHito.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tiposHito.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tiposHito.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  public TipoHito generarMockTipoHito(Long id) {
    return generarMockTipoHito(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  public TipoHito generarMockTipoHito(Long id, String nombre) {
    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id);
    tipoHito.setNombre(nombre);
    tipoHito.setDescripcion("descripcion-" + id);
    tipoHito.setActivo(Boolean.TRUE);
    return tipoHito;
  }

}
