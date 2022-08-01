package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.AreaTematica;
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
 * Test de integracion de AreaTematica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AreaTematicaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/areatematicas";

  private HttpEntity<AreaTematica> buildRequest(HttpHeaders headers, AreaTematica entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V", "CSP-CON-C",
        "CSP-CON-INV-V", "CSP-AREA-C", "CSP-AREA-E", "CSP-AREA-R")));

    HttpEntity<AreaTematica> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsAreaTematica() throws Exception {
    AreaTematica areaTematica = generarMockAreaTematica(null, "A-001", 9999L);

    final ResponseEntity<AreaTematica> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, areaTematica), AreaTematica.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    AreaTematica areaTematicaCreado = response.getBody();
    Assertions.assertThat(areaTematicaCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(areaTematicaCreado.getNombre()).as("getNombre()").isEqualTo(areaTematica.getNombre());
    Assertions.assertThat(areaTematicaCreado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(areaTematica.getDescripcion());
    Assertions.assertThat(areaTematicaCreado.getPadre().getId()).as("getPadre().getId()")
        .isEqualTo(areaTematica.getPadre().getId());
    Assertions.assertThat(areaTematicaCreado.getActivo()).as("getActivo()").isTrue();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsAreaTematica() throws Exception {
    Long idAreaTematica = 2L;
    AreaTematica areaTematica = generarMockAreaTematica(idAreaTematica, "A-UPD", 1L);

    final ResponseEntity<AreaTematica> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, areaTematica), AreaTematica.class, idAreaTematica);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    AreaTematica areaTematicaActualizado = response.getBody();
    Assertions.assertThat(areaTematicaActualizado.getId()).as("getId()").isEqualTo(areaTematica.getId());
    Assertions.assertThat(areaTematicaActualizado.getNombre()).as("getNombre()").isEqualTo(areaTematica.getNombre());
    Assertions.assertThat(areaTematicaActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(areaTematica.getDescripcion());
    Assertions.assertThat(areaTematicaActualizado.getActivo()).as("getActivo()").isTrue();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void reactivar_ReturnAreaTematica() throws Exception {
    Long idAreaTematica = 1L;

    final ResponseEntity<AreaTematica> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        AreaTematica.class, idAreaTematica);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    AreaTematica areaTematicaDisabled = response.getBody();
    Assertions.assertThat(areaTematicaDisabled.getId()).as("getId()").isEqualTo(idAreaTematica);
    Assertions.assertThat(areaTematicaDisabled.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(areaTematicaDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-001");
    Assertions.assertThat(areaTematicaDisabled.getPadre()).as("getPadre()").isNull();
    Assertions.assertThat(areaTematicaDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnAreaTematica() throws Exception {
    Long idAreaTematica = 1L;

    final ResponseEntity<AreaTematica> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), AreaTematica.class, idAreaTematica);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    AreaTematica areaTematicaDisabled = response.getBody();
    Assertions.assertThat(areaTematicaDisabled.getId()).as("getId()").isEqualTo(idAreaTematica);
    Assertions.assertThat(areaTematicaDisabled.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(areaTematicaDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-001");
    Assertions.assertThat(areaTematicaDisabled.getPadre()).as("getPadre()").isNull();
    Assertions.assertThat(areaTematicaDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.FALSE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsAreaTematica() throws Exception {
    Long idAreaTematica = 1L;

    final ResponseEntity<AreaTematica> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), AreaTematica.class, idAreaTematica);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    AreaTematica areaTematica = response.getBody();
    Assertions.assertThat(areaTematica.getId()).as("getId()").isEqualTo(idAreaTematica);
    Assertions.assertThat(areaTematica.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(areaTematica.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-001");
    Assertions.assertThat(areaTematica.getActivo()).as("getActivo()").isTrue();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsAreaTematicaSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<AreaTematica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<AreaTematica>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<AreaTematica> areaTematicas = response.getBody();
    Assertions.assertThat(areaTematicas).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(areaTematicas.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("A-" + String.format("%03d", 3));
    Assertions.assertThat(areaTematicas.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("A-" + String.format("%03d", 2));
    Assertions.assertThat(areaTematicas.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("A-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllGrupo_WithPagingSortingAndFiltering_ReturnsAreaTematicaSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-ARTM-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/grupo").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<AreaTematica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<AreaTematica>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<AreaTematica> areaTematicas = response.getBody();
    Assertions.assertThat(areaTematicas).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(areaTematicas.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(areaTematicas.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(areaTematicas.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodosGrupo_WithPagingSortingAndFiltering_ReturnsAreaTematicaSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-ARTM-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/grupo/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<AreaTematica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<AreaTematica>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<AreaTematica> areaTematicas = response.getBody();
    Assertions.assertThat(areaTematicas).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(areaTematicas.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(areaTematicas.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(areaTematicas.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllHijosAreaTematica_WithPagingSortingAndFiltering_ReturnsAreaTematicaSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    Long areaTematicaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/hijos")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(areaTematicaId).toUri();

    final ResponseEntity<List<AreaTematica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<AreaTematica>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<AreaTematica> areaTematicas = response.getBody();
    Assertions.assertThat(areaTematicas).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(areaTematicas.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("A-" + String.format("%03d", 4));
    Assertions.assertThat(areaTematicas.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("A-" + String.format("%03d", 3));
    Assertions.assertThat(areaTematicas.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("A-" + String.format("%03d", 2));
  }

  /**
   * Función que devuelve un objeto AreaTematica
   * 
   * @param id id del AreaTematica
   * @return el objeto AreaTematica
   */
  private AreaTematica generarMockAreaTematica(Long id) {
    return generarMockAreaTematica(id, "A-" + String.format("%03d", id), null);
  }

  /**
   * Función que devuelve un objeto AreaTematica
   * 
   * @param id                  id del AreaTematica
   * @param nombre              nombre del AreaTematica
   * @param idAreaTematicaPadre id del AreaTematica padre
   * @return el objeto AreaTematica
   */
  private AreaTematica generarMockAreaTematica(Long id, String nombre, Long idAreaTematicaPadre) {
    AreaTematica areaTematica = new AreaTematica();
    areaTematica.setId(id);
    areaTematica.setNombre(nombre);
    areaTematica.setDescripcion("descripcion-" + String.format("%03d", id));

    if (idAreaTematicaPadre != null) {
      areaTematica.setPadre(generarMockAreaTematica(idAreaTematicaPadre));
    }
    areaTematica.setActivo(true);

    return areaTematica;
  }

}
