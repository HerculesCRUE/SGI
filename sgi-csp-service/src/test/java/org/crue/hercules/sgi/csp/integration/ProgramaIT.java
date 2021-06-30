package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Programa;
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
 * Test de integracion de Programa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProgramaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/programas";

  private HttpEntity<Programa> buildRequest(HttpHeaders headers, Programa entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", "CSP-PRG-C", "CSP-CON-C", "CSP-CON-E", "CSP-CON-V", "CSP-CON-INV-V",
                "CSP-SOL-V", "CSP-SOL-C", "CSP-SOL-E", "CSP-SOL-B", "CSP-SOL-R", "CSP-PRO-C", "CSP-PRO-V", "CSP-PRO-E",
                "CSP-PRO-B", "CSP-PRO-R", "CSP-PRG-R", "CSP-PRG-E", "CSP-PRG-B", "AUTH")));

    HttpEntity<Programa> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsPrograma() throws Exception {
    Programa programa = generarMockPrograma(null, "nombre-002", 9999L);

    final ResponseEntity<Programa> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, programa), Programa.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Programa programaCreado = response.getBody();
    Assertions.assertThat(programaCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(programaCreado.getNombre()).as("getNombre()").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaCreado.getDescripcion()).as("getDescripcion()").isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaCreado.getPadre().getId()).as("getPadre().getId()")
        .isEqualTo(programa.getPadre().getId());
    Assertions.assertThat(programaCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsPrograma() throws Exception {
    Long idPrograma = 2L;
    Programa programa = generarMockPrograma(idPrograma, "nombre-actualizado", 1L);

    final ResponseEntity<Programa> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, programa), Programa.class, idPrograma);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Programa programaActualizado = response.getBody();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(programa.getId());
    Assertions.assertThat(programaActualizado.getNombre()).as("getNombre()").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnPrograma() throws Exception {
    Long idPrograma = 1L;

    final ResponseEntity<Programa> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), Programa.class, idPrograma);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Programa programa = response.getBody();
    Assertions.assertThat(programa.getId()).as("getId()").isNotNull();
    Assertions.assertThat(programa.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(programa.getDescripcion()).as("descripcion-001").isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programa.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void reactivar_ReturnPrograma() throws Exception {
    Long idPrograma = 1L;

    final ResponseEntity<Programa> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        Programa.class, idPrograma);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Programa programa = response.getBody();
    Assertions.assertThat(programa.getId()).as("getId()").isNotNull();
    Assertions.assertThat(programa.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(programa.getDescripcion()).as("descripcion-001");
    Assertions.assertThat(programa.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsPrograma() throws Exception {
    Long idPrograma = 1L;

    final ResponseEntity<Programa> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Programa.class, idPrograma);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Programa programa = response.getBody();
    Assertions.assertThat(programa.getId()).as("getId()").isEqualTo(idPrograma);
    Assertions.assertThat(programa.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(programa.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-001");
    Assertions.assertThat(programa.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsProgramaSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<Programa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Programa>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Programa> programas = response.getBody();
    Assertions.assertThat(programas.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(programas.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(programas.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(programas.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllPlan_WithPagingSortingAndFiltering_ReturnsProgramaSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/plan").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Programa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Programa>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Programa> programas = response.getBody();
    Assertions.assertThat(programas.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(programas.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(programas.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(programas.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodosPlan_WithPagingSortingAndFiltering_ReturnsProgramaSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/plan/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Programa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Programa>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Programa> programas = response.getBody();
    Assertions.assertThat(programas.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(programas.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(programas.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(programas.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllHijosPrograma_WithPagingSortingAndFiltering_ReturnsProgramaSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-ME-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    Long programaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/hijos")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(programaId).toUri();

    final ResponseEntity<List<Programa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Programa>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Programa> programas = response.getBody();
    Assertions.assertThat(programas.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(programas.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 4));
    Assertions.assertThat(programas.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(programas.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id id del Programa
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id) {
    return generarMockPrograma(id, "nombre-" + String.format("%03d", id), null);
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id              id del Programa
   * @param nombre          nombre del Programa
   * @param idProgramaPadre id del Programa padre
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id, String nombre, Long idProgramaPadre) {
    Programa programa = new Programa();
    programa.setId(id);
    programa.setNombre(nombre);
    programa.setDescripcion("descripcion-" + String.format("%03d", id));

    if (idProgramaPadre != null) {
      programa.setPadre(generarMockPrograma(idProgramaPadre));
    }
    programa.setActivo(true);

    return programa;
  }

}
