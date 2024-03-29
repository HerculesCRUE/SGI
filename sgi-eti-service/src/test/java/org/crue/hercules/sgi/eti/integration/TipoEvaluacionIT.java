package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
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
 * Test de integracion de TipoEvaluacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/tipo_evaluacion.sql", 
  "classpath:scripts/dictamen.sql" 
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class TipoEvaluacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER = "/{boolean}";
  private static final String TIPO_EVALUACION_CONTROLLER_BASE_PATH = "/tipoevaluaciones";
  private static final String DICTAMENES_REV_MINIMA = "/dictamenes-revision-minima";
  private static final String MEMORIA_RETROSPECTIVA_PATH = "/memoria-retrospectiva";
  private static final String SEGUIMIENT_ANUAL_FINAL_PATH = "/seguimiento-anual-final";

  private HttpEntity<TipoEvaluacion> buildRequest(HttpHeaders headers, TipoEvaluacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-TIPOEVALUACION-EDITAR", "ETI-TIPOEVALUACION-VER")));

    HttpEntity<TipoEvaluacion> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Test
  public void getTipoEvaluacion_WithId_ReturnsTipoEvaluacion() throws Exception {
    final ResponseEntity<TipoEvaluacion> response = restTemplate.exchange(
        TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEvaluacion tipoEvaluacion = response.getBody();

    Assertions.assertThat(tipoEvaluacion.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEvaluacion.getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Test
  public void addTipoEvaluacion_ReturnsTipoEvaluacion() throws Exception {

    TipoEvaluacion nuevoTipoEvaluacion = new TipoEvaluacion();
    nuevoTipoEvaluacion.setId(1L);
    nuevoTipoEvaluacion.setNombre("TipoEvaluacion1");
    nuevoTipoEvaluacion.setActivo(Boolean.TRUE);

    final ResponseEntity<TipoEvaluacion> response = restTemplate.exchange(TIPO_EVALUACION_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoTipoEvaluacion), TipoEvaluacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody().getId()).isNotNull();
  }

  @Test
  public void removeTipoEvaluacion_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoEvaluacion> response = restTemplate.exchange(
        TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        TipoEvaluacion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeTipoEvaluacion_DoNotGetTipoEvaluacion() throws Exception {

    final ResponseEntity<TipoEvaluacion> response = restTemplate.exchange(
        TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        TipoEvaluacion.class, 12L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceTipoEvaluacion_ReturnsTipoEvaluacion() throws Exception {

    TipoEvaluacion replaceTipoEvaluacion = generarMockTipoEvaluacion(1L, "TipoEvaluacion1");

    final ResponseEntity<TipoEvaluacion> response = restTemplate.exchange(
        TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceTipoEvaluacion), TipoEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEvaluacion tipoEvaluacion = response.getBody();

    Assertions.assertThat(tipoEvaluacion.getId()).isNotNull();
    Assertions.assertThat(tipoEvaluacion.getNombre()).isEqualTo(replaceTipoEvaluacion.getNombre());
    Assertions.assertThat(tipoEvaluacion.getActivo()).isEqualTo(replaceTipoEvaluacion.getActivo());
  }

  @Test
  public void findAll_WithPaging_ReturnsTipoEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "4");
    String sort = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    final ResponseEntity<List<TipoEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoEvaluacion>>() {
        });

    // then: Respuesta OK, TipoEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEvaluacion> tipoEvaluaciones = response.getBody();
    Assertions.assertThat(tipoEvaluaciones.size()).isEqualTo(4);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("4");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de nombre='TipoEvaluacion4' a 'TipoEvaluacion1'
    Assertions.assertThat(tipoEvaluaciones.get(0).getNombre()).isEqualTo("TipoEvaluacion4");
    Assertions.assertThat(tipoEvaluaciones.get(1).getNombre()).isEqualTo("TipoEvaluacion3");
    Assertions.assertThat(tipoEvaluaciones.get(2).getNombre()).isEqualTo("TipoEvaluacion2");
    Assertions.assertThat(tipoEvaluaciones.get(3).getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoEvaluacionList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre=ke=TipoEvaluacion;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_EVALUACION_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoEvaluacion>>() {
        });

    // then: Respuesta OK, TipoEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEvaluacion> tipoEvaluaciones = response.getBody();
    Assertions.assertThat(tipoEvaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(tipoEvaluaciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoEvaluaciones.get(0).getNombre()).startsWith("TipoEvaluacion");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoEvaluacionList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoEvaluacion>>() {
        });

    // then: Respuesta OK, TipoEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEvaluacion> tipoEvaluaciones = response.getBody();
    Assertions.assertThat(tipoEvaluaciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      TipoEvaluacion tipoEvaluacion = tipoEvaluaciones.get(i);
      Assertions.assertThat(tipoEvaluacion.getId()).isEqualTo(8 - i);
      Assertions.assertThat(tipoEvaluacion.getNombre()).isEqualTo("TipoEvaluacion" + String.format("%d", 8 - i));
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoEvaluacionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre,desc";
    // when: Filtra por nombre like e id equals
    String filter = "nombre=ke=TipoEvaluacion";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoEvaluacion>>() {
        });

    // then: Respuesta OK, TipoEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEvaluacion> tipoEvaluaciones = response.getBody();
    Assertions.assertThat(tipoEvaluaciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene nombre='TipoEvaluacion8', 'TipoEvaluacion7',
    // 'TipoEvaluacion6'
    Assertions.assertThat(tipoEvaluaciones.get(0).getNombre()).isEqualTo("TipoEvaluacion" + String.format("%d", 8));
    Assertions.assertThat(tipoEvaluaciones.get(1).getNombre()).isEqualTo("TipoEvaluacion" + String.format("%d", 7));
    Assertions.assertThat(tipoEvaluaciones.get(2).getNombre()).isEqualTo("TipoEvaluacion" + String.format("%d", 6));

  }

  @Test
  public void findTipoEvaluacionMemoriaRetrospectiva_ReturnsListaTipoEvaluacion() throws Exception {

    TipoEvaluacion tipoEvaluacion1 = generarMockTipoEvaluacion(1L, "TipoEvaluacion1");
    TipoEvaluacion tipoEvaluacion2 = generarMockTipoEvaluacion(2L, "TipoEvaluacion2");

    List<TipoEvaluacion> listaTipoEvaluacion = new ArrayList<TipoEvaluacion>();
    listaTipoEvaluacion.add(tipoEvaluacion1);
    listaTipoEvaluacion.add(tipoEvaluacion2);

    final String url = new StringBuffer(TIPO_EVALUACION_CONTROLLER_BASE_PATH).append(MEMORIA_RETROSPECTIVA_PATH)
        .toString();

    final ResponseEntity<List<TipoEvaluacion>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoEvaluacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEvaluacion> tipoEvaluaciones = response.getBody();
    Assertions.assertThat(tipoEvaluaciones).isEqualTo(listaTipoEvaluacion);
  }

  @Test
  public void findTipoEvaluacionSeguimientoAnualFinal_ReturnsListaTipoEvaluacion() throws Exception {

    TipoEvaluacion tipoEvaluacion1 = generarMockTipoEvaluacion(3L, "TipoEvaluacion3");
    TipoEvaluacion tipoEvaluacion2 = generarMockTipoEvaluacion(4L, "TipoEvaluacion4");

    List<TipoEvaluacion> listaTipoEvaluacion = new ArrayList<TipoEvaluacion>();
    listaTipoEvaluacion.add(tipoEvaluacion1);
    listaTipoEvaluacion.add(tipoEvaluacion2);

    final String url = new StringBuffer(TIPO_EVALUACION_CONTROLLER_BASE_PATH).append(SEGUIMIENT_ANUAL_FINAL_PATH)
        .toString();

    final ResponseEntity<List<TipoEvaluacion>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoEvaluacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEvaluacion> tipoEvaluaciones = response.getBody();
    Assertions.assertThat(tipoEvaluaciones).isEqualTo(listaTipoEvaluacion);
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @param id     id del TipoEvaluacion
   * @param nombre la descripción del TipoEvaluacion
   * @return el objeto TipoEvaluacion
   */

  public TipoEvaluacion generarMockTipoEvaluacion(Long id, String nombre) {

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(id);
    tipoEvaluacion.setNombre(nombre);
    tipoEvaluacion.setActivo(Boolean.TRUE);

    return tipoEvaluacion;
  }

  /**
   * Función que devuelve un objeto Dictamen
   * 
   * @param id             id del Dictamen
   * @param nombre         nombre del Dictamen
   * @param tipoEvaluacion Tipo Evaluación del Dictamen
   * @return el objeto Dictamen
   */

  public Dictamen generarMockDictamen(Long id, String nombre, TipoEvaluacion tipoEvaluacion) {

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre(nombre);
    dictamen.setActivo(Boolean.TRUE);
    dictamen.setTipoEvaluacion(tipoEvaluacion);

    return dictamen;
  }

}