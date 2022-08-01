package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
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
 * Test de integracion de TipoFinanciacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TipoFinanciacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/tipofinanciaciones";

  private HttpEntity<TipoFinanciacion> buildRequest(HttpHeaders headers, TipoFinanciacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V", "CSP-CON-INV-V", "CSP-SOL-E",
            "CSP-SOL-V", "CSP-PRO-E", "CSP-TFNA-V", "CSP-TFNA-C", "CSP-TFNA-E", "CSP-TFNA-B", "CSP-TFNA-R", "AUTH")));
    // "CSP-TFAS-B", "CSP-TFAS-C", "CSP-TFAS-E", "CSP-TFAS-V"
    HttpEntity<TipoFinanciacion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsTipoFinanciacion() throws Exception {

    // given: new TipoFinanciacion
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(null);

    // when: create TipoFinanciacion
    final ResponseEntity<TipoFinanciacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, tipoFinanciacion), TipoFinanciacion.class);

    // then: new TipoFinanciacion is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    TipoFinanciacion tipoFinanciacionResponse = response.getBody();
    Assertions.assertThat(tipoFinanciacionResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoFinanciacionResponse.getId()).as("getId()").isNotNull();
    Assertions.assertThat(tipoFinanciacionResponse.getNombre()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(tipoFinanciacionResponse.getActivo()).as("getActivo()").isTrue();

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnTipoFinanciacion() throws Exception {
    Long idTipoFinanciacion = 1L;

    final ResponseEntity<TipoFinanciacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), TipoFinanciacion.class, idTipoFinanciacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoFinanciacion tipoFinanciacion = response.getBody();
    Assertions.assertThat(tipoFinanciacion.getId()).as("getId()").isNotNull();
    Assertions.assertThat(tipoFinanciacion.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(tipoFinanciacion.getDescripcion()).as("descripcion-001")
        .isEqualTo(tipoFinanciacion.getDescripcion());
    Assertions.assertThat(tipoFinanciacion.getActivo()).as("getActivo()").isFalse();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void reactivar_ReturnTipoFinanciacion() throws Exception {
    Long idTipoFinanciacion = 1L;

    final ResponseEntity<TipoFinanciacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        TipoFinanciacion.class, idTipoFinanciacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoFinanciacion tipoFinanciacion = response.getBody();
    Assertions.assertThat(tipoFinanciacion.getId()).as("getId()").isNotNull();
    Assertions.assertThat(tipoFinanciacion.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(tipoFinanciacion.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-001");
    Assertions.assertThat(tipoFinanciacion.getActivo()).as("getActivo()").isTrue();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsTipoFinanciacion() throws Exception {
    Long id = 1L;

    final ResponseEntity<TipoFinanciacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), TipoFinanciacion.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoFinanciacion tipoFinanciacion = response.getBody();

    Assertions.assertThat(tipoFinanciacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoFinanciacion.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoFinanciacion.getNombre()).as("getNombre()").isNotNull();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_WithExistingId_ReturnsTipoFinanciacion() throws Exception {

    // given: Entidad existente que se va a actualizar
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    tipoFinanciacion.setNombre("nuevoNombre");

    // when: Se actualiza la entidad
    final ResponseEntity<TipoFinanciacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, tipoFinanciacion), TipoFinanciacion.class, tipoFinanciacion.getId());

    TipoFinanciacion updatedTipoFinanciacion = response.getBody();
    // then: Los datos se actualizan correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(updatedTipoFinanciacion.getNombre()).as("getNombre()").isEqualTo("nuevoNombre");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsTipoFinanciacionSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<TipoFinanciacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoFinanciacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoFinanciacion> tiposFinanciacion = response.getBody();
    Assertions.assertThat(tiposFinanciacion).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tiposFinanciacion.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tiposFinanciacion.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tiposFinanciacion.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodos_WithPagingSortingAndFiltering_ReturnsTipoFinanciacionSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoFinanciacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoFinanciacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoFinanciacion> tiposFinanciacion = response.getBody();
    Assertions.assertThat(tiposFinanciacion).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tiposFinanciacion.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tiposFinanciacion.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tiposFinanciacion.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto TipoFinanciacion
   * 
   * @param id id del TipoFinanciacion
   * @return el objeto TipoFinanciacion
   */
  public TipoFinanciacion generarMockTipoFinanciacion(Long id) {
    return generarMockTipoFinanciacion(id, "Nnombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoFinanciacion
   * 
   * @param id     id del TipoFinanciacion
   * @param nombre nombre del TipoFinanciacion
   * @return el objeto TipoFinanciacion
   */
  public TipoFinanciacion generarMockTipoFinanciacion(Long id, String nombre) {

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id);
    tipoFinanciacion.setActivo(true);
    tipoFinanciacion.setNombre(nombre);
    tipoFinanciacion.setDescripcion("descripcion-" + 1);

    return tipoFinanciacion;
  }

}
