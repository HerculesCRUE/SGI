package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
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
 * Test de integracion de EquipoTrabajo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off      
  "classpath:scripts/tipo_actividad.sql",
  "classpath:scripts/peticion_evaluacion.sql",
  "classpath:scripts/equipo_trabajo.sql" 
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class EquipoTrabajoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EQUIPO_TRABAJO_CONTROLLER_BASE_PATH = "/equipotrabajos";

  private HttpEntity<EquipoTrabajo> buildRequest(HttpHeaders headers, EquipoTrabajo entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-EQUIPOTRABAJO-EDITAR", "ETI-EQUIPOTRABAJO-VER")));

    HttpEntity<EquipoTrabajo> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Test
  public void getEquipoTrabajo_WithId_ReturnsEquipoTrabajo() throws Exception {
    final ResponseEntity<EquipoTrabajo> response = restTemplate.exchange(
        EQUIPO_TRABAJO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        EquipoTrabajo.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EquipoTrabajo equipoTrabajo = response.getBody();

    Assertions.assertThat(equipoTrabajo.getId()).isEqualTo(2L);
    Assertions.assertThat(equipoTrabajo.getPeticionEvaluacion().getTitulo()).isEqualTo("PeticionEvaluacion2");
    Assertions.assertThat(equipoTrabajo.getPersonaRef()).isEqualTo("user-2");
  }

  @Test
  public void findAll_WithPaging_ReturnsEquipoTrabajoSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<EquipoTrabajo>> response = restTemplate.exchange(EQUIPO_TRABAJO_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(headers, null), new ParameterizedTypeReference<List<EquipoTrabajo>>() {
        });

    // then: Respuesta OK, EquipoTrabajos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EquipoTrabajo> equipoTrabajos = response.getBody();
    Assertions.assertThat(equipoTrabajos.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("7");

    // Contiene de peticionEvaluacion.tiutulo='PeticionEvaluacion7' a
    // 'PeticionEvaluacion8'
    Assertions.assertThat(equipoTrabajos.get(0).getPeticionEvaluacion().getTitulo()).isEqualTo("PeticionEvaluacion7");
    Assertions.assertThat(equipoTrabajos.get(1).getPeticionEvaluacion().getTitulo()).isEqualTo("PeticionEvaluacion8");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEquipoTrabajoList() throws Exception {
    // when: Búsqueda por id equals
    String query = "id==5";

    URI uri = UriComponentsBuilder.fromUriString(EQUIPO_TRABAJO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EquipoTrabajo>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EquipoTrabajo>>() {
        });

    // then: Respuesta OK, EquipoTrabajos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EquipoTrabajo> equipoTrabajos = response.getBody();
    Assertions.assertThat(equipoTrabajos.size()).isEqualTo(1);
    Assertions.assertThat(equipoTrabajos.get(0).getId()).isEqualTo(5L);
    Assertions.assertThat(equipoTrabajos.get(0).getPeticionEvaluacion().getTitulo()).startsWith("PeticionEvaluacion");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEquipoTrabajoList() throws Exception {
    // when: Ordenación por personaRef desc
    String query = "personaRef,desc";

    URI uri = UriComponentsBuilder.fromUriString(EQUIPO_TRABAJO_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EquipoTrabajo>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EquipoTrabajo>>() {
        });

    // then: Respuesta OK, EquipoTrabajos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EquipoTrabajo> equipoTrabajos = response.getBody();
    Assertions.assertThat(equipoTrabajos.size()).isEqualTo(7);
    for (int i = 0; i < 7; i++) {
      EquipoTrabajo equipoTrabajo = equipoTrabajos.get(i);
      Assertions.assertThat(equipoTrabajo.getId()).isEqualTo(8 - i);
      Assertions.assertThat(equipoTrabajo.getPeticionEvaluacion().getTitulo())
          .isEqualTo("PeticionEvaluacion" + String.format("%d", 8 - i));
      Assertions.assertThat(equipoTrabajo.getPersonaRef()).isEqualTo("user-" + String.format("%d", 8 - i));
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEquipoTrabajoSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por personaRef desc
    String sort = "personaRef,desc";
    // when: Filtra por personaRef like
    String filter = "personaRef=ke=user-";

    URI uri = UriComponentsBuilder.fromUriString(EQUIPO_TRABAJO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<EquipoTrabajo>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<EquipoTrabajo>>() {
        });

    // then: Respuesta OK, EquipoTrabajos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EquipoTrabajo> equipoTrabajos = response.getBody();
    Assertions.assertThat(equipoTrabajos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("7");

    // Contiene personaRef desde 'user-1' hasta 'user-8'
    Assertions.assertThat(equipoTrabajos.get(0).getPersonaRef()).isEqualTo("user-" + String.format("%d", 8));
    Assertions.assertThat(equipoTrabajos.get(1).getPersonaRef()).isEqualTo("user-" + String.format("%d", 7));
    Assertions.assertThat(equipoTrabajos.get(2).getPersonaRef()).isEqualTo("user-" + String.format("%d", 6));

  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */

  public PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos" + id);
    peticionEvaluacion.setResumen("Resumen" + id);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(titulo);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajo
   * 
   * @param id                 id del EquipoTrabajo
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */

  public EquipoTrabajo generarMockEquipoTrabajo(Long id, PeticionEvaluacion peticionEvaluacion) {

    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);
    equipoTrabajo.setPersonaRef("user-00" + (id == null ? 1 : id));

    return equipoTrabajo;
  }

}