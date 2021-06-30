package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TipoTarea;
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
 * Test de integracion de Tarea.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off
  "classpath:scripts/formulario.sql", 
  "classpath:scripts/comite.sql", 
  "classpath:scripts/tipo_actividad.sql",
  "classpath:scripts/tipo_memoria.sql",
  "classpath:scripts/estado_retrospectiva.sql", 
  "classpath:scripts/retrospectiva.sql",
  "classpath:scripts/formacion_especifica.sql", 
  "classpath:scripts/tipo_tarea.sql",
  "classpath:scripts/tipo_estado_memoria.sql", 
  "classpath:scripts/peticion_evaluacion.sql", 
  "classpath:scripts/memoria.sql", 
  "classpath:scripts/equipo_trabajo.sql", 
  "classpath:scripts/tarea.sql" 
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class TareaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TAREA_CONTROLLER_BASE_PATH = "/tareas";

  private HttpEntity<Tarea> buildRequest(HttpHeaders headers, Tarea entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-TAREA-EDITAR", "ETI-TAREA-VER")));

    HttpEntity<Tarea> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getTarea_WithId_ReturnsTarea() throws Exception {
    final ResponseEntity<Tarea> response = restTemplate.exchange(TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Tarea.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Tarea tarea = response.getBody();

    Assertions.assertThat(tarea.getId()).as("id").isEqualTo(2L);
    Assertions.assertThat(tarea.getEquipoTrabajo()).as("equipoTrabajo").isNotNull();
    Assertions.assertThat(tarea.getEquipoTrabajo().getId()).as("equipoTrabajo.id").isEqualTo(2L);
    Assertions.assertThat(tarea.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(tarea.getMemoria().getId()).as("memoria.id").isEqualTo(3L);
    Assertions.assertThat(tarea.getTarea()).as("tarea").isEqualTo("Tarea2");
    Assertions.assertThat(tarea.getFormacion()).as("formacion").isEqualTo("Formacion2");
    Assertions.assertThat(tarea.getFormacionEspecifica()).as("formacionEspecifica").isNotNull();
    Assertions.assertThat(tarea.getFormacionEspecifica().getId()).as("formacionEspecifica.id").isEqualTo(1L);
    Assertions.assertThat(tarea.getOrganismo()).as("organismo").isEqualTo("Organismo2");
    Assertions.assertThat(tarea.getAnio()).as("anio").isEqualTo(2020);
  }

  @Test
  public void replaceTarea_ReturnsTarea() throws Exception {

    Tarea replaceTarea = generarMockTarea(1L, "Tarea1");

    final ResponseEntity<Tarea> response = restTemplate.exchange(TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, replaceTarea), Tarea.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Tarea tarea = response.getBody();

    Assertions.assertThat(tarea.getId()).as("id").isEqualTo(2L);
    Assertions.assertThat(tarea.getEquipoTrabajo()).as("equipoTrabajo").isNotNull();
    Assertions.assertThat(tarea.getEquipoTrabajo().getId()).as("equipoTrabajo.id").isEqualTo(2L);
    Assertions.assertThat(tarea.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(tarea.getMemoria().getId()).as("memoria.id").isEqualTo(2L);
    Assertions.assertThat(tarea.getTarea()).as("tarea").isEqualTo("Tarea1");
    Assertions.assertThat(tarea.getFormacion()).as("formacion").isEqualTo("Formacion1");
    Assertions.assertThat(tarea.getFormacionEspecifica()).as("formacionEspecifica").isNotNull();
    Assertions.assertThat(tarea.getFormacionEspecifica().getId()).as("formacionEspecifica.id").isEqualTo(1L);
    Assertions.assertThat(tarea.getOrganismo()).as("organismo").isEqualTo("Organismo1");
    Assertions.assertThat(tarea.getAnio()).as("anio").isEqualTo(2020);
  }

  @Test
  public void findAll_WithPaging_ReturnsTareaSubList() throws Exception {
    // when: Obtiene la page=1 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(TAREA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<Tarea>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Tarea>>() {
        });

    // then: Respuesta OK, tareas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Tarea> tareas = response.getBody();
    Assertions.assertThat(tareas.size()).as("size").isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).as("x-page").isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).as("x-page-size").isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).as("x-total-count").isEqualTo("7");

    // Contiene de tarea='Tarea7' a 'Tarea8'
    Assertions.assertThat(tareas.get(0).getTarea()).as("1.tarea").isEqualTo("Tarea7");
    Assertions.assertThat(tareas.get(1).getTarea()).as("2.tarea").isEqualTo("Tarea8");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTareaList() throws Exception {
    // when: Búsqueda por tarea like e id equals
    Long id = 5L;
    String query = "tarea=ke=Tarea;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(TAREA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Tarea>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Tarea>>() {
        });

    // then: Respuesta OK, Tareas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Tarea> tareas = response.getBody();
    Assertions.assertThat(tareas.size()).as("size").isEqualTo(1);
    Assertions.assertThat(tareas.get(0).getId()).as("id").isEqualTo(id);
    Assertions.assertThat(tareas.get(0).getTarea()).as("tarea").startsWith("Tarea5");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTareaList() throws Exception {
    // when: Ordenación por tarea desc
    String sort = "tarea,desc";

    URI uri = UriComponentsBuilder.fromUriString(TAREA_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Tarea>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Tarea>>() {
        });

    // then: Respuesta OK, Tareas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Tarea> tareas = response.getBody();
    Assertions.assertThat(tareas.size()).as("size").isEqualTo(7);
    for (int i = 0; i < 7; i++) {
      Tarea tarea = tareas.get(i);
      Assertions.assertThat(tarea.getId()).as((8 - i) + ".id").isEqualTo(8 - i);
      Assertions.assertThat(tarea.getTarea()).as((8 - i) + ".tarea").isEqualTo("Tarea" + String.format("%d", 8 - i));
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTareaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por tarea desc
    String sort = "tarea,desc";
    // when: Filtra por tarea like
    String filter = "tarea=ke=Tarea";

    URI uri = UriComponentsBuilder.fromUriString(TAREA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Tarea>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Tarea>>() {
        });

    // then: Respuesta OK, Tareas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Tarea> tareas = response.getBody();
    Assertions.assertThat(tareas.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("x-page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("x-page-size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("x-total-count").isEqualTo("7");

    // Contiene tarea='Tarea8', 'Tarea7', 'Tarea6'
    Assertions.assertThat(tareas.get(0).getTarea()).as("0.tarea").isEqualTo("Tarea" + String.format("%d", 8));
    Assertions.assertThat(tareas.get(1).getTarea()).as("1.tarea").isEqualTo("Tarea" + String.format("%d", 7));
    Assertions.assertThat(tareas.get(2).getTarea()).as("2.tarea").isEqualTo("Tarea" + String.format("%d", 6));
  }

  /**
   * Función que devuelve un objeto Tarea
   * 
   * @param id          id de la tarea
   * @param descripcion descripcion de la tarea
   * @return el objeto Tarea
   */
  public Tarea generarMockTarea(Long id, String descripcion) {
    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(2L);

    Memoria memoria = new Memoria();
    memoria.setId(2L);

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(1L);

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(1L);
    tipoTarea.setNombre("Diseño de proyecto y procedimientos");
    tipoTarea.setActivo(Boolean.TRUE);

    Tarea tarea = new Tarea();
    tarea.setId(id);
    tarea.setEquipoTrabajo(equipoTrabajo);
    tarea.setMemoria(memoria);
    tarea.setTarea(descripcion);
    tarea.setFormacion("Formacion" + (id != null ? id : ""));
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo("Organismo" + (id != null ? id : ""));
    tarea.setAnio(2020);
    tarea.setTipoTarea(tipoTarea);

    return tarea;
  }

}