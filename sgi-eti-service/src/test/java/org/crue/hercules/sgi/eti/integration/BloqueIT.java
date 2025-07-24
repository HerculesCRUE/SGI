package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Bloque;
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
 * Test de integracion de Bloque.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/formulario.sql",
  "classpath:scripts/bloque.sql"
// @formatter:on  
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class BloqueIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String BLOQUE_CONTROLLER_BASE_PATH = "/bloques";

  private HttpEntity<Bloque> buildRequest(HttpHeaders headers, Bloque entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-Bloque-EDITAR", "ETI-Bloque-VER")));

    HttpEntity<Bloque> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Test
  public void getBloque_WithId_ReturnsBloque() throws Exception {
    final ResponseEntity<Bloque> response = restTemplate.exchange(BLOQUE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Bloque.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Bloque bloque = response.getBody();

    Assertions.assertThat(bloque.getId()).isEqualTo(1L);
  }

  @Test
  public void findAll_WithPaging_ReturnsBloqueSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<Bloque>> response = restTemplate.exchange(BLOQUE_CONTROLLER_BASE_PATH, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Bloque>>() {
        });

    // then: Respuesta OK, Bloques retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Bloque> bloques = response.getBody();
    Assertions.assertThat(bloques.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredBloqueList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(BLOQUE_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Bloque>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Bloque>>() {
        });

    // then: Respuesta OK, Bloques retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Bloque> bloques = response.getBody();
    Assertions.assertThat(bloques.size()).isEqualTo(1);
    Assertions.assertThat(bloques.get(0).getId()).isEqualTo(id);
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedBloqueList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "id,desc";

    URI uri = UriComponentsBuilder.fromUriString(BLOQUE_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Bloque>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Bloque>>() {
        });

    // then: Respuesta OK, Bloques retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Bloque> bloques = response.getBody();
    Assertions.assertThat(bloques.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Bloque bloque = bloques.get(i);
      Assertions.assertThat(bloque.getId()).isEqualTo(8 - i);
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsBloqueSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "id,desc";
    // when: Filtra por nombre like
    String filter = "orden==1,orden==2,orden==3";

    URI uri = UriComponentsBuilder.fromUriString(BLOQUE_CONTROLLER_BASE_PATH).queryParam("s",
        sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Bloque>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Bloque>>() {
        });

    // then: Respuesta OK, Bloques retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Bloque> bloques = response.getBody();
    Assertions.assertThat(bloques.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

  }

}