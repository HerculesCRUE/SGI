package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
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
 * Test de integracion de Apartado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off
  "classpath:scripts/formulario.sql",
  "classpath:scripts/bloque.sql", 
  "classpath:scripts/apartado.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class ApartadoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String APARTADO_CONTROLLER_BASE_PATH = "/apartados";

  private HttpEntity<Apartado> buildRequest(HttpHeaders headers, Apartado entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-APARTADO-EDITAR", "ETI-APARTADO-VER")));

    HttpEntity<Apartado> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void findById_WithExistingId_ReturnsApartado() throws Exception {

    // given: Entidad con un determinado Id
    final Apartado apartado = getMockData(1L, 1L, null);

    // when: Se busca la entidad por ese Id
    final ResponseEntity<Apartado> response = restTemplate.exchange(APARTADO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Apartado.class, apartado.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(apartado);
  }

  @Test
  public void findById_WithNotExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 6L;

    // when: Se busca la entidad por ese Id
    final ResponseEntity<Apartado> response = restTemplate.exchange(APARTADO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Apartado.class, id);

    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullApartadoList() throws Exception {

    // given: Datos existentes
    List<Apartado> result = new LinkedList<>();
    result.add(getMockData(1L, 1L, null));
    result.add(getMockData(2L, 1L, 1L));
    result.add(getMockData(3L, 1L, 1L));
    result.add(getMockData(4L, 1L, null));
    result.add(getMockData(5L, 1L, 4L));

    // when: Se buscan todos los datos
    final ResponseEntity<List<Apartado>> response = restTemplate.exchange(APARTADO_CONTROLLER_BASE_PATH, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<Apartado>>() {
        });

    // then: Se recuperan todos los datos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(result);
  }

  @Test
  public void findAll_WithPaging_ReturnsApartadoSubList() throws Exception {

    // given: Datos existentes
    List<Apartado> result = new LinkedList<>();
    result.add(getMockData(5L, 1L, 4L));

    // página 2 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "2");
    headers.add("X-Page-Size", "2");

    // when: Se buscan los datos paginados
    final ResponseEntity<List<Apartado>> response = restTemplate.exchange(APARTADO_CONTROLLER_BASE_PATH, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Apartado>>() {
        });

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().size()).isEqualTo(1);
    Assertions.assertThat(response.getBody()).isEqualTo(result);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Count")).isEqualTo("3");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredApartadoList() throws Exception {

    // given: Datos existentes
    List<Apartado> result = new LinkedList<>();
    result.add(getMockData(3L, 1L, 1L));

    // search by codigo like, id equals
    Long id = 3L;
    String query = "nombre=ke=Apartado0;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(APARTADO_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Se buscan los datos con el filtro indicado
    final ResponseEntity<List<Apartado>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Apartado>>() {
        });

    // then: Se recuperan los datos filtrados
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(result);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");

  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedApartadoList() throws Exception {

    // given: Datos existentes

    // sort by id desc
    String sort = "id,desc";

    URI uri = UriComponentsBuilder.fromUriString(APARTADO_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false)
        .toUri();

    // when: Se buscan los datos con la ordenación indicada
    final ResponseEntity<List<Apartado>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Apartado>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().get(0).getId()).isEqualTo(5L);
    Assertions.assertThat(response.getBody().get(4).getId()).isEqualTo(1L);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsApartadoSubList() throws Exception {

    // given: Datos existentes
    List<Apartado> result = new LinkedList<>();
    result.add(getMockData(1L, 1L, null));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    // sort
    String sort = "id,desc";

    // search
    String query = "nombre=ke=Apartado0";

    URI uri = UriComponentsBuilder.fromUriString(APARTADO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", query).build(false).toUri();

    // when: Se buscan los datos paginados con el filtro y orden indicados
    final ResponseEntity<List<Apartado>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Apartado>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().size()).isEqualTo(1);
    Assertions.assertThat(response.getBody()).isEqualTo(result);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Count")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");
  }

  /**
   * Genera un objeto {@link Apartado}
   * 
   * @param id
   * @param bloqueId
   * @param componenteFormularioId
   * @param padreId
   * @return Apartado
   */
  private Apartado getMockData(Long id, Long bloqueId, Long padreId) {

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(bloqueId, formulario, "Bloque" + bloqueId, bloqueId.intValue());

    Apartado padre = (padreId != null) ? getMockData(padreId, bloqueId, null) : null;

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final Apartado data = new Apartado();
    data.setId(id);
    data.setBloque(Bloque);
    data.setNombre("Apartado" + txt);
    data.setPadre(padre);
    data.setOrden(id.intValue());
    data.setEsquema("{\"nombre\":\"EsquemaApartado" + txt + "\"}");

    return data;
  }
}
