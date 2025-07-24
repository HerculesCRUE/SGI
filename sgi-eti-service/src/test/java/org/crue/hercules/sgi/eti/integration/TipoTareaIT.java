package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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
 * Test de integracion de TipoTarea.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off
  "classpath:scripts/tipo_tarea.sql" 
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class TipoTareaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_TAREA_CONTROLLER_BASE_PATH = "/tipostarea";

  private HttpEntity<TipoTarea> buildRequest(HttpHeaders headers, TipoTarea entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-TIPOTAREA-EDITAR", "ETI-TIPOTAREA-VER")));

    HttpEntity<TipoTarea> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getTipoTarea_WithId_ReturnsTipoTarea() throws Exception {
    final ResponseEntity<TipoTarea> response = restTemplate.exchange(
        TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null), TipoTarea.class,
        1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoTarea tipoTarea = response.getBody();

    Assertions.assertThat(tipoTarea.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoTarea.getNombre(), Language.ES)).as("nombre")
        .isEqualTo("Diseño de proyecto y procedimientos");
    Assertions.assertThat(tipoTarea.getActivo()).as("activo").isEqualTo(true);
  }

  @Test
  public void findAll_WithPaging_ReturnsTipoTareaSubList() throws Exception {
    // when: Obtiene la page=0 con pagesize=2
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "2");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_TAREA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoTarea>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoTarea>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoTarea> tiposTarea = response.getBody();
    Assertions.assertThat(tiposTarea.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene de nombre='Diseño de proyecto y procedimientos' y 'Eutanasia'
    Assertions.assertThat(I18nHelper.getValueForLanguage(tiposTarea.get(0).getNombre(), Language.ES))
        .isEqualTo("Diseño de proyecto y procedimientos");
    Assertions.assertThat(
        I18nHelper.getValueForLanguage(tiposTarea.get(1).getNombre(), Language.ES)).isEqualTo("Eutanasia");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoTareaList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 1L;
    String query = "nombre.value=ke=proyecto;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_TAREA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoTarea>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoTarea>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoTarea> tiposTarea = response.getBody();
    Assertions.assertThat(tiposTarea.size()).isEqualTo(1);
    Assertions.assertThat(tiposTarea.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tiposTarea.get(0).getNombre(),
        Language.ES)).startsWith("Diseño de proyecto y procedimientos");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoTareaList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre.value,desc";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_TAREA_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoTarea>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoTarea>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoTarea> tiposTarea = response.getBody();
    Assertions.assertThat(tiposTarea.size()).isEqualTo(3);
    Assertions.assertThat(tiposTarea.get(0).getId()).as("0.id").isEqualTo(3);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tiposTarea.get(0).getNombre(),
        Language.ES)).as("0.nombre").isEqualTo("Manipulación de animales");
    Assertions.assertThat(tiposTarea.get(1).getId()).as("1.id").isEqualTo(2);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tiposTarea.get(1).getNombre(),
        Language.ES)).as("1.nombre").isEqualTo("Eutanasia");
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoTareaSubList() throws Exception {
    // when: Obtiene page=0 con pagesize=4
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "4");
    // when: Ordena por nombre desc
    String sort = "nombre.value,desc";
    // when: Filtra por nombre like
    String filter = "nombre.value=ke=de";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_TAREA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoTarea>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoTarea>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoTarea> tiposTarea = response.getBody();
    Assertions.assertThat(tiposTarea.size()).isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("4");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene nombre='Diseño de proyecto y procedimientos', 'Manipulación de
    // animales'
    Assertions.assertThat(I18nHelper.getValueForLanguage(tiposTarea.get(0).getNombre(),
        Language.ES)).as("0.nombre").isEqualTo("Manipulación de animales");
    Assertions.assertThat(I18nHelper.getValueForLanguage(tiposTarea.get(1).getNombre(),
        Language.ES)).as("1.nombre")
        .isEqualTo("Diseño de proyecto y procedimientos");

  }

}
