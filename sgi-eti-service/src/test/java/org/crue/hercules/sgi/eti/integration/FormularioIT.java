package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Formulario;
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
 * Test de integracion de Formulario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/formulario.sql" 
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class FormularioIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String FORMULARIO_CONTROLLER_BASE_PATH = "/formularios";

  private HttpEntity<Formulario> buildRequest(HttpHeaders headers, Formulario entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-FORMULARIO-EDITAR", "ETI-FORMULARIO-VER")));

    HttpEntity<Formulario> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Test
  public void getFormulario_WithId_ReturnsFormulario() throws Exception {
    final ResponseEntity<Formulario> response = restTemplate.exchange(
        FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null), Formulario.class,
        1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Formulario formulario = response.getBody();

    Assertions.assertThat(formulario.getId()).isEqualTo(1L);
    Assertions.assertThat(formulario.getNombre()).isEqualTo("M10");
    Assertions.assertThat(formulario.getDescripcion()).isEqualTo("Formulario M10");
  }

  @Test
  public void findAll_WithPaging_ReturnsFormularioSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "3");

    final ResponseEntity<List<Formulario>> response = restTemplate.exchange(FORMULARIO_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(headers, null), new ParameterizedTypeReference<List<Formulario>>() {
        });

    // then: Respuesta OK, Formularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Formulario> formularios = response.getBody();
    Assertions.assertThat(formularios.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene de nombre='Seguimiento Anual', 'Seguimiento Final' y 'Retrospectiva'
    Assertions.assertThat(formularios.get(0).getNombre()).isEqualTo("Seguimiento Anual");
    Assertions.assertThat(formularios.get(1).getNombre()).isEqualTo("Seguimiento Final");
    Assertions.assertThat(formularios.get(2).getNombre()).isEqualTo("Retrospectiva");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredFormularioList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 3L;
    String query = "nombre=ke=M;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(FORMULARIO_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Formulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<Formulario>>() {
        });

    // then: Respuesta OK, Formularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Formulario> formularios = response.getBody();
    Assertions.assertThat(formularios.size()).isEqualTo(1);
    Assertions.assertThat(formularios.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(formularios.get(0).getNombre()).startsWith("M");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedFormularioList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Formulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<Formulario>>() {
        });

    // then: Respuesta OK, Formularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Formulario> formularios = response.getBody();
    Assertions.assertThat(formularios.size()).isEqualTo(6);
    Assertions.assertThat(formularios.get(0).getId()).isEqualTo(5);
    Assertions.assertThat(formularios.get(0).getNombre()).isEqualTo("Seguimiento Final");
    Assertions.assertThat(formularios.get(3).getId()).isEqualTo(3);
    Assertions.assertThat(formularios.get(3).getNombre()).isEqualTo("M30");

  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsFormularioSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre,desc";
    // when: Filtra por nombre like
    String filter = "nombre=ke=0";

    URI uri = UriComponentsBuilder.fromUriString(FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Formulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Formulario>>() {
        });

    // then: Respuesta OK, Formularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Formulario> formularios = response.getBody();
    Assertions.assertThat(formularios.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene nombre='M10', 'M20' y 'M30'
    Assertions.assertThat(formularios.get(0).getNombre()).isEqualTo("M30");
    Assertions.assertThat(formularios.get(1).getNombre()).isEqualTo("M20");
    Assertions.assertThat(formularios.get(2).getNombre()).isEqualTo("M10");

  }

  /**
   * Función que devuelve un objeto Formulario
   * 
   * @param id          id del Formulario
   * @param nombre      el nombre del Formulario
   * @param descripcion la descripción del Formulario
   * @return el objeto Formulario
   */

  public Formulario generarMockFormulario(Long id, String nombre, String descripcion) {

    Formulario formulario = new Formulario();
    formulario.setId(id);
    formulario.setNombre(nombre);
    formulario.setDescripcion(descripcion);

    return formulario;
  }

}