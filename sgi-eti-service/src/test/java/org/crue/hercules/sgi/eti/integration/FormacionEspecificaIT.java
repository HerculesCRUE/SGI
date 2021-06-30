package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
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
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de FormacionEspecifica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/formacion_especifica.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class FormacionEspecificaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH = "/formacionespecificas";

  private HttpEntity<FormacionEspecifica> buildRequest(HttpHeaders headers, FormacionEspecifica entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-FORMACIONESPECIFICA-EDITAR", "ETI-FORMACIONESPECIFICA-VER")));

    HttpEntity<FormacionEspecifica> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Test
  public void getFormacionEspecifica_WithId_ReturnsFormacionEspecifica() throws Exception {
    final ResponseEntity<FormacionEspecifica> response = restTemplate.exchange(
        FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        FormacionEspecifica.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final FormacionEspecifica formacionEspecifica = response.getBody();

    Assertions.assertThat(formacionEspecifica.getId()).isEqualTo(1L);
    Assertions.assertThat(formacionEspecifica.getNombre()).isEqualTo("A: Cuidado de los animales");
  }

  @Test
  public void addFormacionEspecifica_ReturnsFormacionEspecifica() throws Exception {

    FormacionEspecifica nuevoFormacionEspecifica = new FormacionEspecifica();
    nuevoFormacionEspecifica.setId(1L);
    nuevoFormacionEspecifica.setNombre("FormacionEspecifica1");
    nuevoFormacionEspecifica.setActivo(Boolean.TRUE);

    final ResponseEntity<FormacionEspecifica> response = restTemplate.exchange(
        FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(null, nuevoFormacionEspecifica),
        FormacionEspecifica.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isEqualTo(nuevoFormacionEspecifica);
  }

  @Test
  public void removeFormacionEspecifica_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<FormacionEspecifica> response = restTemplate.exchange(
        FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        FormacionEspecifica.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeFormacionEspecifica_DoNotGetFormacionEspecifica() throws Exception {

    final ResponseEntity<FormacionEspecifica> response = restTemplate.exchange(
        FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        FormacionEspecifica.class, 9L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceFormacionEspecifica_ReturnsFormacionEspecifica() throws Exception {

    FormacionEspecifica replaceFormacionEspecifica = generarMockFormacionEspecifica(1L, "FormacionEspecifica1");

    final ResponseEntity<FormacionEspecifica> response = restTemplate.exchange(
        FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceFormacionEspecifica), FormacionEspecifica.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final FormacionEspecifica formacionEspecifica = response.getBody();

    Assertions.assertThat(formacionEspecifica.getId()).isNotNull();
    Assertions.assertThat(formacionEspecifica.getNombre()).isEqualTo(replaceFormacionEspecifica.getNombre());
    Assertions.assertThat(formacionEspecifica.getActivo()).isEqualTo(replaceFormacionEspecifica.getActivo());
  }

  @Test
  public void findAll_WithPaging_ReturnsFormacionEspecificaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    final ResponseEntity<List<FormacionEspecifica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<FormacionEspecifica>>() {
        });

    // then: Respuesta OK, FormacionEspecificas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<FormacionEspecifica> formacionEspecificas = response.getBody();
    Assertions.assertThat(formacionEspecificas.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de nombre= 'H: No requiere' a 'F: Veterinario designado'
    Assertions.assertThat(formacionEspecificas.get(0).getNombre()).isEqualTo("H: No requiere");
    Assertions.assertThat(formacionEspecificas.get(1).getNombre()).isEqualTo("G: Sin especificar");
    Assertions.assertThat(formacionEspecificas.get(2).getNombre()).isEqualTo("F: Veterinario designado");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredFormacionEspecificaList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre=ke=animales;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<FormacionEspecifica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<FormacionEspecifica>>() {
        });

    // then: Respuesta OK, FormacionEspecificas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<FormacionEspecifica> formacionEspecificas = response.getBody();
    Assertions.assertThat(formacionEspecificas.size()).isEqualTo(1);
    Assertions.assertThat(formacionEspecificas.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(formacionEspecificas.get(0).getNombre()).startsWith("E: Responsable de la");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedFormacionEspecificaList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<FormacionEspecifica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<FormacionEspecifica>>() {
        });

    // then: Respuesta OK, FormacionEspecificas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<FormacionEspecifica> formacionEspecificas = response.getBody();
    Assertions.assertThat(formacionEspecificas.size()).isEqualTo(8);
    Assertions.assertThat(formacionEspecificas.get(0).getId()).isEqualTo(8);
    Assertions.assertThat(formacionEspecificas.get(0).getNombre()).isEqualTo("H: No requiere");

    Assertions.assertThat(formacionEspecificas.get(7).getId()).isEqualTo(1);
    Assertions.assertThat(formacionEspecificas.get(7).getNombre()).isEqualTo("A: Cuidado de los animales");

  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsFormacionEspecificaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre,desc";
    // when: Filtra por nombre like e id equals
    String filter = "nombre=ke=animales";

    URI uri = UriComponentsBuilder.fromUriString(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<FormacionEspecifica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<FormacionEspecifica>>() {
        });

    // then: Respuesta OK, FormacionEspecificas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<FormacionEspecifica> formacionEspecificas = response.getBody();
    Assertions.assertThat(formacionEspecificas.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene nombre='E: Responsable de la supervisión <<in situ>> del bienestar y
    // cuidado de los animales', 'B: Eutanasia de los animales',
    // 'A: Cuidado de los animales'
    Assertions.assertThat(formacionEspecificas.get(0).getNombre())
        .isEqualTo("E: Responsable de la supervisión in situ del bienestar y cuidado de los animales");
    Assertions.assertThat(formacionEspecificas.get(1).getNombre()).isEqualTo("B: Eutanasia de los animales");
    Assertions.assertThat(formacionEspecificas.get(2).getNombre()).isEqualTo("A: Cuidado de los animales");

  }

  /**
   * Función que devuelve un objeto FormacionEspecifica
   * 
   * @param id     id del formacionEspecifica
   * @param nombre la descripción del tipo de memoria
   * @return el objeto tipo memoria
   */

  public FormacionEspecifica generarMockFormacionEspecifica(Long id, String nombre) {

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(id);
    formacionEspecifica.setNombre(nombre);
    formacionEspecifica.setActivo(Boolean.TRUE);

    return formacionEspecifica;
  }

}