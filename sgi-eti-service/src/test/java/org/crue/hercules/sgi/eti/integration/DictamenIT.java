package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
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
 * Test de integracion de Dictamen.
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
public class DictamenIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String DICTAMEN_CONTROLLER_BASE_PATH = "/dictamenes";
  private static final String DICTAMEN_MEMORIA_REVISION_MINIMA_PATH = "/memoria-revision-minima";
  private static final String DICTAMEN_MEMORIA_NO_REVISION_MINIMA_PATH = "/memoria-no-revision-minima";
  private static final String DICTAMEN_RETROSPECTIVA_PATH = "/retrospectiva";

  private HttpEntity<Dictamen> buildRequest(HttpHeaders headers, Dictamen entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-DICTAMEN-EDITAR", "ETI-DICTAMEN-VER")));

    HttpEntity<Dictamen> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getDictamen_WithId_ReturnsDictamen() throws Exception {
    final ResponseEntity<Dictamen> response = restTemplate.exchange(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Dictamen.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Dictamen dictamen = response.getBody();

    Assertions.assertThat(dictamen.getId()).isEqualTo(1L);
    Assertions.assertThat(dictamen.getNombre()).isEqualTo("Favorable");
  }

  @Test
  public void addDictamen_ReturnsDictamen() throws Exception {

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Dictamen nuevoDictamen = new Dictamen();
    nuevoDictamen.setId(1L);
    nuevoDictamen.setNombre("Dictamen1");
    nuevoDictamen.setTipoEvaluacion(tipoEvaluacion);
    nuevoDictamen.setActivo(Boolean.TRUE);

    final ResponseEntity<Dictamen> response = restTemplate.exchange(DICTAMEN_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, nuevoDictamen), Dictamen.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isEqualTo(nuevoDictamen);

  }

  @Test
  public void removeDictamen_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Dictamen> response = restTemplate.exchange(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Dictamen.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeDictamen_DoNotGetDictamen() throws Exception {
    restTemplate.exchange(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null), Dictamen.class, 7L);

    final ResponseEntity<Dictamen> response = restTemplate.exchange(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Dictamen.class, 7L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceDictamen_ReturnsDictamen() throws Exception {

    Dictamen replaceDictamen = generarMockDictamen(1L, "Dictamen1");

    final ResponseEntity<Dictamen> response = restTemplate.exchange(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, replaceDictamen), Dictamen.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Dictamen dictamen = response.getBody();

    Assertions.assertThat(dictamen.getId()).isNotNull();
    Assertions.assertThat(dictamen.getNombre()).isEqualTo(replaceDictamen.getNombre());
    Assertions.assertThat(dictamen.getTipoEvaluacion().getNombre())
        .isEqualTo(replaceDictamen.getTipoEvaluacion().getNombre());
    Assertions.assertThat(dictamen.getActivo()).isEqualTo(replaceDictamen.getActivo());
  }

  @Test
  public void findAll_WithPaging_ReturnsDictamenSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    URI uri = UriComponentsBuilder.fromUriString(DICTAMEN_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamenes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamenes = response.getBody();
    Assertions.assertThat(dictamenes.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene de nombre='Pendiente de correcciones' y 'No procede evaluar'
    Assertions.assertThat(dictamenes.get(0).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(dictamenes.get(1).getNombre()).isEqualTo("No procede evaluar");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredDictamenList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 2L;
    String query = "nombre=ke=Favorable;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(DICTAMEN_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamenes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamenes = response.getBody();
    Assertions.assertThat(dictamenes.size()).isEqualTo(1);
    Assertions.assertThat(dictamenes.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(dictamenes.get(0).getNombre()).startsWith("Favorable");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedDictamenList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(DICTAMEN_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamenes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamenes = response.getBody();
    Assertions.assertThat(dictamenes.size()).isEqualTo(6);
    Assertions.assertThat(dictamenes.get(0).getId()).isEqualTo(3);
    Assertions.assertThat(dictamenes.get(0).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(dictamenes.get(3).getId()).isEqualTo(1);
    Assertions.assertThat(dictamenes.get(3).getNombre()).isEqualTo("Favorable");
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsDictamenSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre,desc";
    // when: Filtra por nombre like
    String filter = "nombre=ik=pendiente";

    URI uri = UriComponentsBuilder.fromUriString(DICTAMEN_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamenes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamenes = response.getBody();
    Assertions.assertThat(dictamenes.size()).isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene nombre='Favorable pendiente de revisión mínima', 'Favorable'
    Assertions.assertThat(dictamenes.get(0).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(dictamenes.get(1).getNombre()).isEqualTo("Favorable pendiente de revisión mínima");

  }

  @Test
  public void findAllByMemoriaRevisionMinima_ReturnsDictamenList() throws Exception {

    URI uri = UriComponentsBuilder.fromUriString(DICTAMEN_CONTROLLER_BASE_PATH + DICTAMEN_MEMORIA_REVISION_MINIMA_PATH)
        .build(false).toUri();

    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamenes
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamenes = response.getBody();
    Assertions.assertThat(dictamenes.size()).isEqualTo(2);

    // Contiene de nombre='Favorable' y 'Favorable pendiente de revisión mínima'
    Assertions.assertThat(dictamenes.get(0).getNombre()).isEqualTo("Favorable");
    Assertions.assertThat(dictamenes.get(1).getNombre()).isEqualTo("Favorable pendiente de revisión mínima");
  }

  @Test
  public void findAllByMemoriaNoRevisionMinima_ReturnsDictamenList() throws Exception {

    URI uri = UriComponentsBuilder
        .fromUriString(DICTAMEN_CONTROLLER_BASE_PATH + DICTAMEN_MEMORIA_NO_REVISION_MINIMA_PATH).build(false).toUri();

    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamenes
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamenes = response.getBody();
    Assertions.assertThat(dictamenes.size()).isEqualTo(4);

    // Contiene de nombre='Favorable', 'Favorable pendiente de revisión mínima',
    // 'Pendiente de correcciones' y 'No procede evaluar'
    Assertions.assertThat(dictamenes.get(0).getNombre()).isEqualTo("Favorable");
    Assertions.assertThat(dictamenes.get(1).getNombre()).isEqualTo("Favorable pendiente de revisión mínima");
    Assertions.assertThat(dictamenes.get(2).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(dictamenes.get(3).getNombre()).isEqualTo("No procede evaluar");
  }

  @Test
  public void findAllByRetrospectiva_ReturnsDictamenList() throws Exception {

    URI uri = UriComponentsBuilder.fromUriString(DICTAMEN_CONTROLLER_BASE_PATH + DICTAMEN_RETROSPECTIVA_PATH)
        .build(false).toUri();

    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamenes
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamenes = response.getBody();
    Assertions.assertThat(dictamenes.size()).isEqualTo(2);

    // Contiene de nombre='Favorable', 'Desfavorable'
    Assertions.assertThat(dictamenes.get(0).getNombre()).isEqualTo("Favorable");
    Assertions.assertThat(dictamenes.get(1).getNombre()).isEqualTo("Desfavorable");
  }

  /**
   * Función que devuelve un objeto Dictamen
   * 
   * @param id     id del dictamen
   * @param nombre la descripción del dictamen
   * @return el objeto dictamen
   */

  public Dictamen generarMockDictamen(Long id, String nombre) {

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre(nombre);
    dictamen.setTipoEvaluacion(tipoEvaluacion);
    dictamen.setActivo(Boolean.TRUE);

    return dictamen;
  }

}