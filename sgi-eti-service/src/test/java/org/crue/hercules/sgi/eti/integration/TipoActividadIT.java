package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoActividad;
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
 * Test de integracion de TipoActividad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/tipo_actividad.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class TipoActividadIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH = "/tipoactividades";

  private HttpEntity<TipoActividad> buildRequest(HttpHeaders headers, TipoActividad entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-TIPOACTIVIDAD-EDITAR",
        "ETI-TIPOACTIVIDAD-VER", "ETI-PEV-CR", "ETI-MEM-CR", "ETI-PEV-C-INV", "ETI-PEV-ER-INV")));

    HttpEntity<TipoActividad> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getTipoActividad_WithId_ReturnsTipoActividad() throws Exception {
    final ResponseEntity<TipoActividad> response = restTemplate.exchange(
        TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoActividad.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoActividad tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoActividad.getNombre()).isEqualTo("Proyecto de investigacion");
  }

  @Test
  public void addTipoActividad_ReturnsTipoActividad() throws Exception {

    TipoActividad nuevoTipoActividad = new TipoActividad(1L, "Proyecto de investigacion", Boolean.TRUE);

    final ResponseEntity<TipoActividad> response = restTemplate.exchange(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoTipoActividad), TipoActividad.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isEqualTo(nuevoTipoActividad);
  }

  @Test
  public void removeTipoActividad_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoActividad> response = restTemplate.exchange(
        TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        TipoActividad.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeTipoActividad_DoNotGetTipoActividad() throws Exception {
    restTemplate.exchange(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null), TipoActividad.class, 1L);

    final ResponseEntity<TipoActividad> response = restTemplate.exchange(
        TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoActividad.class, 4L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceTipoActividad_ReturnsTipoActividad() throws Exception {

    TipoActividad replaceTipoActividad = generarMockTipoActividad(1L, "TipoActividad1");

    final ResponseEntity<TipoActividad> response = restTemplate.exchange(
        TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceTipoActividad), TipoActividad.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoActividad tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isNotNull();
    Assertions.assertThat(tipoActividad.getNombre()).isEqualTo(replaceTipoActividad.getNombre());
    Assertions.assertThat(tipoActividad.getActivo()).isEqualTo(replaceTipoActividad.getActivo());
  }

  @Test
  public void findAll_WithPaging_ReturnsTipoActividadSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoActividad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene de nombre='Investigacion tutelada'
    Assertions.assertThat(tipoActividades.get(0).getNombre()).isEqualTo("Investigacion tutelada");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoActividadList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 1L;
    String query = "nombre=ke=Proyecto;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoActividad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(1);
    Assertions.assertThat(tipoActividades.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoActividades.get(0).getNombre()).startsWith("Proyecto de investigacion");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoActividadList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoActividad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(3);
    Assertions.assertThat(tipoActividades.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(tipoActividades.get(0).getNombre()).isEqualTo("Proyecto de investigacion");
    Assertions.assertThat(tipoActividades.get(2).getId()).isEqualTo(3);
    Assertions.assertThat(tipoActividades.get(2).getNombre()).isEqualTo("Investigacion tutelada");
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoActividadSubList() throws Exception {
    // when: Obtiene page=0 con pagesize=2
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "2");
    // when: Ordena por nombre desc
    String sort = "nombre,desc";
    // when: Filtra por nombre like
    String filter = "nombre=ke=Proyecto";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoActividad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene nombre='Proyecto de investigacion'
    Assertions.assertThat(tipoActividades.get(0).getNombre()).isEqualTo("Proyecto de investigacion");

  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @param id     id del tipoActividad
   * @param nombre la descripción del tipo de actividad
   * @return el objeto tipo actividad
   */

  public TipoActividad generarMockTipoActividad(Long id, String nombre) {

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(id);
    tipoActividad.setNombre(nombre);
    tipoActividad.setActivo(Boolean.TRUE);

    return tipoActividad;
  }

}