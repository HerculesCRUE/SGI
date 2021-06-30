package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.EstadoActa;
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
 * Test de integracion de EstadoActa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off      
  "classpath:scripts/formulario.sql",
  "classpath:scripts/tipo_convocatoria_reunion.sql",
  "classpath:scripts/tipo_estado_acta.sql",
  "classpath:scripts/comite.sql",
  "classpath:scripts/convocatoria_reunion.sql",
  "classpath:scripts/acta.sql",
  "classpath:scripts/estado_acta.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class EstadoActaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ESTADO_ACTA_CONTROLLER_BASE_PATH = "/estadoactas";

  private HttpEntity<EstadoActa> buildRequest(HttpHeaders headers, EstadoActa entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ESTADOACTA-EDITAR", "ETI-ESTADOACTA-VER")));

    HttpEntity<EstadoActa> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Test
  public void getEstadoActa_WithId_ReturnsEstadoActa() throws Exception {
    final ResponseEntity<EstadoActa> response = restTemplate.exchange(
        ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        EstadoActa.class, 4L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EstadoActa estadoActa = response.getBody();

    Assertions.assertThat(estadoActa.getId()).as("id").isEqualTo(4L);
    Assertions.assertThat(estadoActa.getActa()).as("acta").isNotNull();
    Assertions.assertThat(estadoActa.getActa().getId()).as("acta.id").isEqualTo(2L);
    Assertions.assertThat(estadoActa.getTipoEstadoActa()).as("tipoEstadoActa").isNotNull();
    Assertions.assertThat(estadoActa.getTipoEstadoActa().getId()).as("tipoEstadoActa.id").isEqualTo(1L);
    Assertions.assertThat(estadoActa.getFechaEstado()).as("fechaEstado")
        .isEqualTo(Instant.parse("2020-07-14T00:00:00Z"));
  }

  @Test
  public void addEstadoActa_ReturnsEstadoActa() throws Exception {

    EstadoActa nuevoEstadoActa = generarMockEstadoActa(null);

    final ResponseEntity<EstadoActa> response = restTemplate.exchange(ESTADO_ACTA_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, nuevoEstadoActa), EstadoActa.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody().getId()).isNotNull();

    final EstadoActa estadoActa = response.getBody();

    Assertions.assertThat(estadoActa.getId()).as("id").isNotNull();
    Assertions.assertThat(estadoActa.getActa()).as("acta").isNotNull();
    Assertions.assertThat(estadoActa.getActa().getId()).as("acta.id").isEqualTo(2L);
    Assertions.assertThat(estadoActa.getTipoEstadoActa()).as("tipoEstadoActa").isNotNull();
    Assertions.assertThat(estadoActa.getTipoEstadoActa().getId()).as("tipoEstadoActa.id").isEqualTo(2L);
    Assertions.assertThat(estadoActa.getFechaEstado()).as("fechaEstado")
        .isEqualTo(Instant.parse("2020-07-14T00:00:00Z"));
  }

  @Test
  public void removeEstadoActa_Success() throws Exception {

    // when: Delete con id existente
    long id = 4L;
    final ResponseEntity<EstadoActa> response = restTemplate.exchange(
        ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        EstadoActa.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void removeEstadoActa_DoNotGetEstadoActa() throws Exception {

    final ResponseEntity<EstadoActa> response = restTemplate.exchange(
        ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        EstadoActa.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void replaceEstadoActa_ReturnsEstadoActa() throws Exception {

    EstadoActa replaceEstadoActa = generarMockEstadoActa(2L);

    final ResponseEntity<EstadoActa> response = restTemplate.exchange(
        ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, replaceEstadoActa),
        EstadoActa.class, 4L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EstadoActa estadoActa = response.getBody();

    Assertions.assertThat(estadoActa.getId()).as("id").isEqualTo(4L);
    Assertions.assertThat(estadoActa.getActa()).as("acta").isNotNull();
    Assertions.assertThat(estadoActa.getActa().getId()).as("acta.id").isEqualTo(2L);
    Assertions.assertThat(estadoActa.getTipoEstadoActa()).as("tipoEstadoActa").isNotNull();
    Assertions.assertThat(estadoActa.getTipoEstadoActa().getId()).as("tipoEstadoActa.id").isEqualTo(2L);
    Assertions.assertThat(estadoActa.getFechaEstado()).as("fechaEstado")
        .isEqualTo(Instant.parse("2020-07-14T00:00:00Z"));
  }

  @Test
  public void findAll_WithPaging_ReturnsEstadoActaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<EstadoActa>> response = restTemplate.exchange(ESTADO_ACTA_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(headers, null), new ParameterizedTypeReference<List<EstadoActa>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoActa> estadosActas = response.getBody();
    Assertions.assertThat(estadosActas.size()).as("size").isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).as("x-page").isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).as("x-page-size").isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).as("x-total-count").isEqualTo("7");

    // Contiene de id=9 a 10
    Assertions.assertThat(estadosActas.get(0).getId()).as("0.id").isEqualTo(9);
    Assertions.assertThat(estadosActas.get(1).getId()).as("1.id").isEqualTo(10);
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEstadoActaList() throws Exception {
    // when: Búsqueda por estado acta id equals
    Long id = 5L;
    String query = "id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EstadoActa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EstadoActa>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoActa> estadosActas = response.getBody();
    Assertions.assertThat(estadosActas.size()).as("size").isEqualTo(1);
    Assertions.assertThat(estadosActas.get(0).getId()).as("id").isEqualTo(id);
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEstadoActaList() throws Exception {
    // when: Ordenación por id desc
    String sort = "id,desc";

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EstadoActa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EstadoActa>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoActa> estadosActas = response.getBody();
    Assertions.assertThat(estadosActas.size()).as("size").isEqualTo(7);
    for (int i = 0; i < 7; i++) {
      EstadoActa estadoActa = estadosActas.get(i);
      Assertions.assertThat(estadoActa.getId()).as((8 - i) + ".id").isEqualTo(10 - i);
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEstadoActaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por id desc
    String sort = "id,desc";
    // when: Filtra por id menor
    String filter = "id=lt=6";

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<EstadoActa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<EstadoActa>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoActa> estadosActas = response.getBody();
    Assertions.assertThat(estadosActas.size()).as("size").isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("x-page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("x-page-size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("x-total-count").isEqualTo("2");

    // Contiene id=4, 5
    Assertions.assertThat(estadosActas.get(0).getId()).as("0.id").isEqualTo(5);
    Assertions.assertThat(estadosActas.get(1).getId()).as("1.id").isEqualTo(4);
  }

  /**
   * Función que devuelve un objeto EstadoActa
   * 
   * @param id id del estado acta
   * @return el objeto EstadoActa
   */
  public EstadoActa generarMockEstadoActa(Long id) {
    Acta acta = new Acta();
    acta.setId(2L);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(2L);

    EstadoActa estadoActa = new EstadoActa();
    estadoActa.setId(id);
    estadoActa.setActa(acta);
    estadoActa.setTipoEstadoActa(tipoEstadoActa);
    estadoActa.setFechaEstado(Instant.parse("2020-07-14T00:00:00Z"));

    return estadoActa;
  }

}