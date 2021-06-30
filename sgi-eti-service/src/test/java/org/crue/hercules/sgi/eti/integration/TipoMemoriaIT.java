package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
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
 * Test de integracion de TipoMemoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off
  "classpath:scripts/tipo_memoria.sql" 
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class TipoMemoriaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_MEMORIA_CONTROLLER_BASE_PATH = "/tipomemorias";

  private HttpEntity<TipoMemoria> buildRequest(HttpHeaders headers, TipoMemoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-TIPOMEMORIA-EDITAR", "ETI-TIPOMEMORIA-VER")));

    HttpEntity<TipoMemoria> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getTipoMemoria_WithId_ReturnsTipoMemoria() throws Exception {
    final ResponseEntity<TipoMemoria> response = restTemplate.exchange(
        TIPO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoMemoria tipoMemoria = response.getBody();

    Assertions.assertThat(tipoMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoMemoria.getNombre()).isEqualTo("TipoMemoria001");
  }

  @Test
  public void addTipoMemoria_ReturnsTipoMemoria() throws Exception {

    TipoMemoria nuevoTipoMemoria = new TipoMemoria(1L, "TipoMemoria001", Boolean.TRUE);

    final ResponseEntity<TipoMemoria> response = restTemplate.exchange(TIPO_MEMORIA_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoTipoMemoria), TipoMemoria.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isEqualTo(nuevoTipoMemoria);
  }

  @Test
  public void removeTipoMemoria_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoMemoria> response = restTemplate.exchange(
        TIPO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        TipoMemoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeTipoMemoria_DoNotGetTipoMemoria() throws Exception {
    restTemplate.delete(TIPO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    // when: Delete con id no existente
    long id = 12L;
    final ResponseEntity<TipoMemoria> response = restTemplate.exchange(
        TIPO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoMemoria.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceTipoMemoria_ReturnsTipoMemoria() throws Exception {

    TipoMemoria replaceTipoMemoria = generarMockTipoMemoria(1L, "TipoMemoria001");

    final ResponseEntity<TipoMemoria> response = restTemplate.exchange(
        TIPO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, replaceTipoMemoria),
        TipoMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoMemoria tipoMemoria = response.getBody();

    Assertions.assertThat(tipoMemoria.getId()).isNotNull();
    Assertions.assertThat(tipoMemoria.getNombre()).isEqualTo(replaceTipoMemoria.getNombre());
    Assertions.assertThat(tipoMemoria.getActivo()).isEqualTo(replaceTipoMemoria.getActivo());
  }

  @Test
  public void findAll_WithPaging_ReturnsTipoMemoriaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_MEMORIA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoMemoria>>() {
        });

    // then: Respuesta OK, TipoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoMemoria> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de nombre='TipoMemoria6' a 'TipoMemoria8'
    Assertions.assertThat(tipoMemorias.get(0).getNombre()).isEqualTo("TipoMemoria006");
    Assertions.assertThat(tipoMemorias.get(1).getNombre()).isEqualTo("TipoMemoria007");
    Assertions.assertThat(tipoMemorias.get(2).getNombre()).isEqualTo("TipoMemoria008");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoMemoriaList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre=ke=TipoMemoria;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoMemoria>>() {
        });

    // then: Respuesta OK, TipoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoMemoria> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(1);
    Assertions.assertThat(tipoMemorias.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoMemorias.get(0).getNombre()).startsWith("TipoMemoria");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoMemoriaList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoMemoria>>() {
        });

    // then: Respuesta OK, TipoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoMemoria> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      TipoMemoria tipoMemoria = tipoMemorias.get(i);
      Assertions.assertThat(tipoMemoria.getId()).isEqualTo(8 - i);
      Assertions.assertThat(tipoMemoria.getNombre()).isEqualTo("TipoMemoria" + String.format("%03d", 8 - i));
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoMemoriaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre,desc";
    // when: Filtra por nombre like e id equals
    String filter = "nombre=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoMemoria>>() {
        });

    // then: Respuesta OK, TipoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoMemoria> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene nombre='TipoMemoria008', 'TipoMemoria007',
    // 'TipoMemoria006'
    Assertions.assertThat(tipoMemorias.get(0).getNombre()).isEqualTo("TipoMemoria" + String.format("%03d", 8));
    Assertions.assertThat(tipoMemorias.get(1).getNombre()).isEqualTo("TipoMemoria" + String.format("%03d", 7));
    Assertions.assertThat(tipoMemorias.get(2).getNombre()).isEqualTo("TipoMemoria" + String.format("%03d", 6));

  }

  /**
   * Función que devuelve un objeto TipoMemoria
   * 
   * @param id     id del tipoMemoria
   * @param nombre la descripción del tipo de memoria
   * @return el objeto tipo memoria
   */

  public TipoMemoria generarMockTipoMemoria(Long id, String nombre) {

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(id);
    tipoMemoria.setNombre(nombre);
    tipoMemoria.setActivo(Boolean.TRUE);

    return tipoMemoria;
  }

}