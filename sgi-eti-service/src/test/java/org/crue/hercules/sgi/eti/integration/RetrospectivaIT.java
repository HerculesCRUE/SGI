package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
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
 * Test de integracion de Retrospectiva.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/estado_retrospectiva.sql", 
  "classpath:scripts/retrospectiva.sql" 
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class RetrospectivaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String RETROSPECTIVA_CONTROLLER_BASE_PATH = "/retrospectivas";

  private HttpEntity<Retrospectiva> buildRequest(HttpHeaders headers, Retrospectiva entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-RETROSPECTIVA-EDITAR", "ETI-RETROSPECTIVA-VER")));

    HttpEntity<Retrospectiva> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void create_ReturnsRetrospectiva() throws Exception {

    // given: Nueva entidad
    final Retrospectiva newRetrospectiva = getMockData(1L);
    newRetrospectiva.setId(null);

    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    // when: Se crea la entidad
    final ResponseEntity<Retrospectiva> response = restTemplate.exchange(url, HttpMethod.POST,
        buildRequest(null, newRetrospectiva), Retrospectiva.class);

    // then: La entidad se crea correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final Retrospectiva retrospectiva = response.getBody();
    Assertions.assertThat(retrospectiva.getId()).isNotNull();
    newRetrospectiva.setId(retrospectiva.getId());
    Assertions.assertThat(retrospectiva).isEqualTo(newRetrospectiva);
  }

  @Test
  public void update_WithExistingId_ReturnsRetrospectiva() throws Exception {

    // given: Entidad existente que se va a actualizar
    final Retrospectiva updatedRetrospectiva = getMockData(3L);
    updatedRetrospectiva.setId(4L);

    // @formatter:off
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    // when: Se actualiza la entidad
    final ResponseEntity<Retrospectiva> response = restTemplate.exchange(url, HttpMethod.PUT,
        buildRequest(null, updatedRetrospectiva), Retrospectiva.class, updatedRetrospectiva.getId());

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(updatedRetrospectiva);
  }

  @Test
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente
    Long id = 3L;

    // @formatter:off
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    ResponseEntity<Retrospectiva> response = restTemplate.exchange(url, HttpMethod.GET, buildRequest(null, null),
        Retrospectiva.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // when: Se elimina la entidad
    response = restTemplate.exchange(url, HttpMethod.DELETE, buildRequest(null, null), Retrospectiva.class, id);

    // then: La entidad se elimina correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    response = restTemplate.exchange(url, HttpMethod.GET, buildRequest(null, null), Retrospectiva.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void findById_WithExistingId_ReturnsRetrospectiva() throws Exception {

    // given: Entidad con un determinado Id
    final Retrospectiva retrospectiva = getMockData(3L);

    // @formatter:off
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    // when: Se busca la entidad por ese Id
    ResponseEntity<Retrospectiva> response = restTemplate.exchange(url, HttpMethod.GET, buildRequest(null, null),
        Retrospectiva.class, retrospectiva.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(retrospectiva);
  }

  @Test
  public void findById_WithNotExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 9L;
    // @formatter:off
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    // when: Se busca la entidad por ese Id
    ResponseEntity<Retrospectiva> response = restTemplate.exchange(url, HttpMethod.GET, buildRequest(null, null),
        Retrospectiva.class, id);

    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullRetrospectivaList() throws Exception {

    // given: Datos existentes
    List<Retrospectiva> response = new LinkedList<>();
    response.add(getMockData(3L, 1L));
    response.add(getMockData(4L, 2L));
    response.add(getMockData(5L, 1L));
    response.add(getMockData(6L, 2L));
    response.add(getMockData(7L, 1L));
    response.add(getMockData(8L, 4L));

    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    // when: Se buscan todos los datos
    final ResponseEntity<List<Retrospectiva>> result = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<Retrospectiva>>() {
        });

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).isEqualTo(6L);
  }

  @Test
  public void findAll_WithPaging_ReturnsRetrospectivaSubList() throws Exception {

    // given: Datos existentes
    List<Retrospectiva> response = new LinkedList<>();
    response.add(getMockData(7L, 1L));
    response.add(getMockData(8L, 4L));

    // página 2 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "2");
    headers.add("X-Page-Size", "2");

    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    // when: Se buscan los datos paginados
    final ResponseEntity<List<Retrospectiva>> result = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Retrospectiva>>() {
        });

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).isEqualTo(2);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("3");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("6");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredRetrospectivaList() throws Exception {

    // given: Datos existentes
    List<Retrospectiva> response = new LinkedList<>();
    response.add(getMockData(5L));

    // search by codigo like, id equals
    Long id = 5L;
    String query = "fechaRetrospectiva=le=2020-07-05T23:59:59Z;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(RETROSPECTIVA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Se buscan los datos con el filtro indicado
    final ResponseEntity<List<Retrospectiva>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<Retrospectiva>>() {
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
  public void findAll_WithSortQuery_ReturnsOrderedRetrospectivaList() throws Exception {

    // given: Datos existentes

    // sort by id desc
    String sort = "id,desc";

    URI uri = UriComponentsBuilder.fromUriString(RETROSPECTIVA_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false)
        .toUri();

    // when: Se buscan los datos con la ordenación indicada
    final ResponseEntity<List<Retrospectiva>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<Retrospectiva>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().get(0).getId()).isEqualTo(8L);
    Assertions.assertThat(result.getBody().get(4).getId()).isEqualTo(4L);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("6");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("6");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("6");
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsRetrospectivaSubList() throws Exception {

    // given: Datos existentes
    List<Retrospectiva> response = new LinkedList<>();
    response.add(getMockData(3L));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    // sort
    String sort = "id,desc";

    // search
    String query = "estadoRetrospectiva.nombre=ke=Retrospectiva0";

    URI uri = UriComponentsBuilder.fromUriString(RETROSPECTIVA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", query).build(false).toUri();

    // when: Se buscan los datos paginados con el filtro y orden indicados
    final ResponseEntity<List<Retrospectiva>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Retrospectiva>>() {
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
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva getMockData(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(getMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());

    return data;
  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @param estado
   * @return Retrospectiva
   */
  private Retrospectiva getMockData(Long id, Long estado) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(getMockDataEstadoRetrospectiva(estado));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva getMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("EstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }
}
