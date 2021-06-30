package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
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
 * Test de integracion de EstadoRetrospectiva.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off    
  "classpath:scripts/estado_retrospectiva.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class EstadoRetrospectivaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH = "/estadoretrospectivas";

  private HttpEntity<EstadoRetrospectiva> buildRequest(HttpHeaders headers, EstadoRetrospectiva entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-ESTADORETROSPECTIVA-EDITAR", "ETI-ESTADORETROSPECTIVA-VER")));

    HttpEntity<EstadoRetrospectiva> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void create_ReturnsEstadoRetrospectiva() throws Exception {

    // given: Nueva entidad
    final EstadoRetrospectiva newEstadoRetrospectiva = getMockData(1L);

    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    // when: Se crea la entidad
    final ResponseEntity<EstadoRetrospectiva> response = restTemplate.exchange(url, HttpMethod.POST,
        buildRequest(null, newEstadoRetrospectiva), EstadoRetrospectiva.class);

    // then: La entidad se crea correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final EstadoRetrospectiva estadoRetrospectiva = response.getBody();
    Assertions.assertThat(estadoRetrospectiva.getId()).isNotNull();
    newEstadoRetrospectiva.setId(estadoRetrospectiva.getId());
    Assertions.assertThat(estadoRetrospectiva).isEqualTo(newEstadoRetrospectiva);
  }

  @Test
  public void update_WithExistingId_ReturnsEstadoRetrospectiva() throws Exception {

    // given: Entidad existente que se va a actualizar
    final EstadoRetrospectiva updatedEstadoRetrospectiva = getMockData(2L);
    updatedEstadoRetrospectiva.setId(1L);

    // @formatter:off
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    // when: Se actualiza la entidad
    final ResponseEntity<EstadoRetrospectiva> response = restTemplate.exchange(url, HttpMethod.PUT,
        buildRequest(null, updatedEstadoRetrospectiva), EstadoRetrospectiva.class, updatedEstadoRetrospectiva.getId());

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(updatedEstadoRetrospectiva);
  }

  @Test
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente
    Long id = 1L;

    // @formatter:off
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    ResponseEntity<EstadoRetrospectiva> response = restTemplate.exchange(url, HttpMethod.GET, buildRequest(null, null),
        EstadoRetrospectiva.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getActivo()).isEqualTo(Boolean.TRUE);

    // when: Se elimina la entidad
    response = restTemplate.exchange(url, HttpMethod.DELETE, buildRequest(null, null), EstadoRetrospectiva.class, id);

    // then: La entidad se elimina correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    response = restTemplate.exchange(url, HttpMethod.GET, buildRequest(null, null), EstadoRetrospectiva.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Test
  public void findById_WithExistingId_ReturnsEstadoRetrospectiva() throws Exception {

    // given: Entidad con un determinado Id
    final EstadoRetrospectiva estadoRetrospectiva = getMockData(1L);

    // @formatter:off
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    // when: Se busca la entidad por ese Id
    ResponseEntity<EstadoRetrospectiva> response = restTemplate.exchange(url, HttpMethod.GET, buildRequest(null, null),
        EstadoRetrospectiva.class, estadoRetrospectiva.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(estadoRetrospectiva);
  }

  @Test
  public void findById_WithNotExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 6L;
    // @formatter:off
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    // when: Se busca la entidad por ese Id
    ResponseEntity<EstadoRetrospectiva> response = restTemplate.exchange(url, HttpMethod.GET, buildRequest(null, null),
        EstadoRetrospectiva.class, id);

    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullEstadoRetrospectivaList() throws Exception {

    // given: Datos existentes
    List<EstadoRetrospectiva> response = new LinkedList<>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));
    response.add(getMockData(3L));
    response.add(getMockData(4L));
    response.add(getMockData(5L));
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();
    // when: Se buscan todos los datos
    final ResponseEntity<List<EstadoRetrospectiva>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EstadoRetrospectiva>>() {
        });

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
  }

  @Test
  public void findAll_WithPaging_ReturnsEstadoRetrospectivaSubList() throws Exception {

    // given: Datos existentes
    List<EstadoRetrospectiva> response = new LinkedList<>();
    response.add(getMockData(5L));

    // página 2 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "2");
    headers.add("X-Page-Size", "2");

    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    // when: Se buscan los datos paginados
    final ResponseEntity<List<EstadoRetrospectiva>> result = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<EstadoRetrospectiva>>() {
        });

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).isEqualTo(1);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("3");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEstadoRetrospectivaList() throws Exception {

    // given: Datos existentes
    List<EstadoRetrospectiva> response = new LinkedList<>();
    response.add(getMockData(3L));

    // search by codigo like, id equals
    Long id = 3L;
    String query = "nombre=ke=EstadoRetrospectiva0;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Se buscan los datos con el filtro indicado
    final ResponseEntity<List<EstadoRetrospectiva>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EstadoRetrospectiva>>() {
        });

    // then: Se recuperan los datos filtrados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");

  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEstadoRetrospectivaList() throws Exception {

    // given: Datos existentes

    // sort by id desc
    String sort = "id,desc";

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    // when: Se buscan los datos con la ordenación indicada
    final ResponseEntity<List<EstadoRetrospectiva>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EstadoRetrospectiva>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().get(0).getId()).isEqualTo(5L);
    Assertions.assertThat(result.getBody().get(4).getId()).isEqualTo(1L);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("5");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEstadoRetrospectivaSubList() throws Exception {

    // given: Datos existentes
    List<EstadoRetrospectiva> response = new LinkedList<>();
    response.add(getMockData(1L));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    // sort
    String sort = "id,desc";

    // search
    String query = "nombre=ke=EstadoRetrospectiva0";

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", query).build(false).toUri();

    // when: Se buscan los datos paginados con el filtro y orden indicados
    final ResponseEntity<List<EstadoRetrospectiva>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<EstadoRetrospectiva>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).isEqualTo(1);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva getMockData(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("EstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }
}
