package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoComentario;
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
 * Test de integracion de TipoComentario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/tipo_comentario.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class TipoComentarioIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_COMENTARIO_CONTROLLER_BASE_PATH = "/tipocomentarios";

  private HttpEntity<TipoComentario> buildRequest(HttpHeaders headers, TipoComentario entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-TIPOCOMENTARIO-EDITAR", "ETI-TIPOCOMENTARIO-VER")));

    HttpEntity<TipoComentario> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getTipoComentario_WithId_ReturnsTipoComentario() throws Exception {
    final ResponseEntity<TipoComentario> response = restTemplate.exchange(
        TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoComentario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoComentario tipoComentario = response.getBody();

    Assertions.assertThat(tipoComentario.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoComentario.getNombre()).isEqualTo("GESTOR");
  }

  @Test
  public void addTipoComentario_ReturnsTipoComentario() throws Exception {

    TipoComentario nuevoTipoComentario = new TipoComentario(1L, "EVALUADOR", Boolean.TRUE);

    final ResponseEntity<TipoComentario> response = restTemplate.exchange(TIPO_COMENTARIO_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoTipoComentario), TipoComentario.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isEqualTo(nuevoTipoComentario);
  }

  @Test
  public void removeTipoComentario_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoComentario> response = restTemplate.exchange(
        TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        TipoComentario.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeTipoComentario_DoNotGetTipoComentario() throws Exception {
    restTemplate.delete(TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<TipoComentario> response = restTemplate.exchange(
        TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoComentario.class, 3L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceTipoComentario_ReturnsTipoComentario() throws Exception {

    TipoComentario replaceTipoComentario = generarMockTipoComentario(1L, "TipoComentario1");

    final ResponseEntity<TipoComentario> response = restTemplate.exchange(
        TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceTipoComentario), TipoComentario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoComentario tipoComentario = response.getBody();

    Assertions.assertThat(tipoComentario.getId()).isNotNull();
    Assertions.assertThat(tipoComentario.getNombre()).isEqualTo(replaceTipoComentario.getNombre());
    Assertions.assertThat(tipoComentario.getActivo()).isEqualTo(replaceTipoComentario.getActivo());
  }

  @Test
  public void findAll_WithPaging_ReturnsTipoComentarioSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "1");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_COMENTARIO_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoComentario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene de nombre='EVALUADOR'
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).isEqualTo("EVALUADOR");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoComentarioList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 1L;
    String query = "nombre=ik=gestor;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_COMENTARIO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoComentario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(1);
    Assertions.assertThat(tipoComentarios.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).startsWith("GESTOR");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoComentarioList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_COMENTARIO_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoComentario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(2);
    Assertions.assertThat(tipoComentarios.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).isEqualTo("GESTOR");
    Assertions.assertThat(tipoComentarios.get(1).getId()).isEqualTo(2);
    Assertions.assertThat(tipoComentarios.get(1).getNombre()).isEqualTo("EVALUADOR");
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoComentarioSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre,desc";
    // when: Filtra por nombre like
    String filter = "nombre=ke=GEST";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_COMENTARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoComentario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene nombre='GESTOR'
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).isEqualTo("GESTOR");

  }

  /**
   * Función que devuelve un objeto TipoComentario
   * 
   * @param id     id del TipoComentario
   * @param nombre la descripción del TipoComentario
   * @return el objeto TipoComentario
   */

  public TipoComentario generarMockTipoComentario(Long id, String nombre) {

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(id);
    tipoComentario.setNombre(nombre);
    tipoComentario.setActivo(Boolean.TRUE);

    return tipoComentario;
  }

}