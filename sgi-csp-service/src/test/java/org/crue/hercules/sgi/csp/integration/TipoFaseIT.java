package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoFase;
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
 * Test de integracion de TipoFase.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoFaseIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String TIPO_FASE_CONTROLLER_BASE_PATH = "/tipofases";

  private HttpEntity<TipoFase> buildRequest(HttpHeaders headers, TipoFase entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-TFASE-C",
        "CSP-TFASE-V", "CSP-TFASE-C", "CSP-TFASE-E", "CSP-TFASE-B", "CSP-TFASE-R", "CSP-ME-C", "CSP-ME-E")));

    HttpEntity<TipoFase> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithId_ReturnsTipoFase() throws Exception {
    final ResponseEntity<TipoFase> response = restTemplate.exchange(TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), TipoFase.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoFase tipoFase = response.getBody();

    Assertions.assertThat(tipoFase.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoFase.getNombre()).as("getNombre()").isEqualTo("TipoFase1");
    Assertions.assertThat(tipoFase.getDescripcion()).as("getDescripcion()").isEqualTo("Descripcion1");
    Assertions.assertThat(tipoFase.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsTipoFase() throws Exception {

    TipoFase tipoFase = generarMockTipoFase(null);

    final ResponseEntity<TipoFase> response = restTemplate.exchange(TIPO_FASE_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, tipoFase), TipoFase.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    TipoFase tipoFaseCreado = response.getBody();
    Assertions.assertThat(tipoFaseCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(tipoFaseCreado.getNombre()).as("getNombre()").isEqualTo(tipoFase.getNombre());
    Assertions.assertThat(tipoFaseCreado.getDescripcion()).as("getDescripcion()").isEqualTo(tipoFase.getDescripcion());
    Assertions.assertThat(tipoFaseCreado.getActivo()).as("getActivo()").isEqualTo(true);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void reactivar_ReturnTipoFase() throws Exception {
    Long idTipoFase = 1L;

    final ResponseEntity<TipoFase> response = restTemplate.exchange(
        TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), TipoFase.class, idTipoFase);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoFase tipoFaseDisabled = response.getBody();
    Assertions.assertThat(tipoFaseDisabled.getId()).as("getId()").isEqualTo(idTipoFase);
    Assertions.assertThat(tipoFaseDisabled.getNombre()).as("getNombre()").isEqualTo("TipoFase1");
    Assertions.assertThat(tipoFaseDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("Descripción1");
    Assertions.assertThat(tipoFaseDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnTipoFase() throws Exception {
    Long idTipoFase = 1L;

    final ResponseEntity<TipoFase> response = restTemplate.exchange(
        TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), TipoFase.class, idTipoFase);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoFase tipoFaseDisabled = response.getBody();
    Assertions.assertThat(tipoFaseDisabled.getId()).as("getId()").isEqualTo(idTipoFase);
    Assertions.assertThat(tipoFaseDisabled.getNombre()).as("getNombre()").isEqualTo("TipoFase1");
    Assertions.assertThat(tipoFaseDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("Descripción1");
    Assertions.assertThat(tipoFaseDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.FALSE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsTipoFase() throws Exception {
    Long idTipoFase = 1L;
    TipoFase tipoFase = generarMockTipoFase(idTipoFase, "nombre-actualizado");

    final ResponseEntity<TipoFase> response = restTemplate.exchange(TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, tipoFase), TipoFase.class, idTipoFase);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoFase tipoFaseActualizado = response.getBody();
    Assertions.assertThat(tipoFaseActualizado.getId()).as("getId()").isEqualTo(tipoFase.getId());
    Assertions.assertThat(tipoFaseActualizado.getNombre()).as("getNombre()").isEqualTo(tipoFase.getNombre());
    Assertions.assertThat(tipoFaseActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoFase.getDescripcion());
    Assertions.assertThat(tipoFaseActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoFaseSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_FASE_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoFase>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoFase>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoFase> tiposFase = response.getBody();
    Assertions.assertThat(tiposFase.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tiposFase.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tiposFase.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tiposFase.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsTipoFaseSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_FASE_CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoFase>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoFase>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoFase> tiposFase = response.getBody();
    Assertions.assertThat(tiposFase.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tiposFase.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tiposFase.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tiposFase.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoFase
   * @return el objeto TipoFase
   */
  public TipoFase generarMockTipoFase(Long id) {
    return generarMockTipoFase(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoFase
   * @return el objeto TipoFase
   */
  public TipoFase generarMockTipoFase(Long id, String nombre) {
    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre(nombre);
    tipoFase.setDescripcion("descripcion-" + id);
    tipoFase.setActivo(Boolean.TRUE);
    return tipoFase;
  }

}
