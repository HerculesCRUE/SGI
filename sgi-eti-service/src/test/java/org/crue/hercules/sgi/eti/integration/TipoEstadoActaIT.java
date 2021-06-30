package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
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
 * Test de integracion de TipoEstadoActa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/tipo_estado_acta.sql" 
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class TipoEstadoActaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH = "/tipoestadoactas";

  private HttpEntity<TipoEstadoActa> buildRequest(HttpHeaders headers, TipoEstadoActa entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<TipoEstadoActa> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getTipoEstadoActa_WithId_ReturnsTipoEstadoActa() throws Exception {
    final ResponseEntity<TipoEstadoActa> response = restTemplate.exchange(
        TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoEstadoActa.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEstadoActa tipoEstadoActa = response.getBody();

    Assertions.assertThat(tipoEstadoActa.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEstadoActa.getNombre()).isEqualTo("En elaboración");
  }

  @Test
  public void addTipoEstadoActa_ReturnsTipoEstadoActa() throws Exception {

    TipoEstadoActa nuevoTipoEstadoActa = new TipoEstadoActa(1L, "Finalizada", Boolean.TRUE);

    final ResponseEntity<TipoEstadoActa> response = restTemplate.exchange(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoTipoEstadoActa), TipoEstadoActa.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isEqualTo(nuevoTipoEstadoActa);
  }

  @Test
  public void removeTipoEstadoActa_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoEstadoActa> response = restTemplate.exchange(
        TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        TipoEstadoActa.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeTipoEstadoActa_DoNotGetTipoEstadoActa() throws Exception {
    restTemplate.exchange(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null), TipoEstadoActa.class, 1L);

    final ResponseEntity<TipoEstadoActa> response = restTemplate.exchange(
        TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoEstadoActa.class, 3L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceTipoEstadoActa_ReturnsTipoEstadoActa() throws Exception {

    TipoEstadoActa replaceTipoEstadoActa = generarMockTipoEstadoActa(1L, "TipoEstadoActa1");

    final ResponseEntity<TipoEstadoActa> response = restTemplate.exchange(
        TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceTipoEstadoActa), TipoEstadoActa.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEstadoActa tipoEstadoActa = response.getBody();

    Assertions.assertThat(tipoEstadoActa.getId()).isNotNull();
    Assertions.assertThat(tipoEstadoActa.getNombre()).isEqualTo(replaceTipoEstadoActa.getNombre());
    Assertions.assertThat(tipoEstadoActa.getActivo()).isEqualTo(replaceTipoEstadoActa.getActivo());
  }

  @Test
  public void findAll_WithPaging_ReturnsTipoEstadoActaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "1");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoEstadoActa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoEstadoActa>>() {
        });

    // then: Respuesta OK, TipoEstadoActas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoActa> tipoEstadoActas = response.getBody();
    Assertions.assertThat(tipoEstadoActas.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene de nombre='Finalizada'
    Assertions.assertThat(tipoEstadoActas.get(0).getNombre()).isEqualTo("Finalizada");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoEstadoActaList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 1L;
    String query = "nombre=ke=En;id==" + id;

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEstadoActa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoEstadoActa>>() {
        });

    // then: Respuesta OK, TipoEstadoActas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoActa> tipoEstadoActas = response.getBody();
    Assertions.assertThat(tipoEstadoActas.size()).isEqualTo(1);
    Assertions.assertThat(tipoEstadoActas.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoEstadoActas.get(0).getNombre()).startsWith("En elaboración");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoEstadoActaList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre,desc";

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEstadoActa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoEstadoActa>>() {
        });

    // then: Respuesta OK, TipoEstadoActas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoActa> tipoEstadoActas = response.getBody();
    Assertions.assertThat(tipoEstadoActas.size()).isEqualTo(2);
    Assertions.assertThat(tipoEstadoActas.get(0).getId()).isEqualTo(2);
    Assertions.assertThat(tipoEstadoActas.get(0).getNombre()).isEqualTo("Finalizada");

    Assertions.assertThat(tipoEstadoActas.get(1).getId()).isEqualTo(1);
    Assertions.assertThat(tipoEstadoActas.get(1).getNombre()).isEqualTo("En elaboración");
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoEstadoActaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));
    // when: Ordena por nombre desc
    String sort = "nombre,desc";
    // when: Filtra por nombre like
    String filter = "nombre=ke=Finalizada";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoEstadoActa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoEstadoActa>>() {
        });

    // then: Respuesta OK, TipoEstadoActas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoActa> tipoEstadoActas = response.getBody();
    Assertions.assertThat(tipoEstadoActas.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene nombre='Finalizada'
    Assertions.assertThat(tipoEstadoActas.get(0).getNombre()).isEqualTo("Finalizada");

  }

  /**
   * Función que devuelve un objeto TipoEstadoActa
   * 
   * @param id     id del TipoEstadoActa
   * @param nombre la descripción del TipoEstadoActa
   * @return el objeto tipoEstadoActa
   */

  public TipoEstadoActa generarMockTipoEstadoActa(Long id, String nombre) {

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(id);
    tipoEstadoActa.setNombre(nombre);
    tipoEstadoActa.setActivo(Boolean.TRUE);

    return tipoEstadoActa;
  }

}