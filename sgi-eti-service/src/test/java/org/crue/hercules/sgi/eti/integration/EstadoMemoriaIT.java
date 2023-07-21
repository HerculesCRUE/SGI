package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
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
 * Test de integracion de EstadoMemoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off      
  "classpath:scripts/formulario.sql",
  "classpath:scripts/comite.sql",
  "classpath:scripts/tipo_actividad.sql",
  "classpath:scripts/tipo_memoria.sql",
  "classpath:scripts/tipo_estado_memoria.sql",
  "classpath:scripts/estado_retrospectiva.sql",
  "classpath:scripts/retrospectiva.sql",
  "classpath:scripts/peticion_evaluacion.sql",
  "classpath:scripts/memoria.sql",
  "classpath:scripts/estado_memoria.sql" 
// @formatter:on  
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class EstadoMemoriaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ESTADO_MEMORIA_CONTROLLER_BASE_PATH = "/estadomemorias";

  private HttpEntity<EstadoMemoria> buildRequest(HttpHeaders headers, EstadoMemoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-ESTADOMEMORIA-EDITAR", "ETI-ESTADOMEMORIA-VER")));

    HttpEntity<EstadoMemoria> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Test
  public void getEstadoMemoria_WithId_ReturnsEstadoMemoria() throws Exception {
    final ResponseEntity<EstadoMemoria> response = restTemplate.exchange(
        ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        EstadoMemoria.class, 3L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EstadoMemoria estadoMemoria = response.getBody();

    Assertions.assertThat(estadoMemoria.getId()).isEqualTo(3L);
    Assertions.assertThat(estadoMemoria.getMemoria().getTitulo()).isEqualTo("Memoria003");
    Assertions.assertThat(estadoMemoria.getTipoEstadoMemoria().getNombre()).isEqualTo("En secretaría");
  }

  @Test
  public void addEstadoMemoria_ReturnsEstadoMemoria() throws Exception {

    EstadoMemoria nuevoEstadoMemoria = generarMockEstadoMemoria(null, 2L);

    final ResponseEntity<EstadoMemoria> response = restTemplate.exchange(ESTADO_MEMORIA_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoEstadoMemoria), EstadoMemoria.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody().getId()).isNotNull();
  }

  @Test
  public void removeEstadoMemoria_Success() throws Exception {

    // when: Delete con id existente
    long id = 3L;
    final ResponseEntity<EstadoMemoria> response = restTemplate.exchange(
        ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        EstadoMemoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeEstadoMemoria_DoNotGetEstadoMemoria() throws Exception {

    final ResponseEntity<EstadoMemoria> response = restTemplate.exchange(
        ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        EstadoMemoria.class, 9L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceEstadoMemoria_ReturnsEstadoMemoria() throws Exception {

    EstadoMemoria replaceEstadoMemoria = generarMockEstadoMemoria(2L, 2L);

    final ResponseEntity<EstadoMemoria> response = restTemplate.exchange(
        ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceEstadoMemoria), EstadoMemoria.class, 3L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EstadoMemoria estadoMemoria = response.getBody();

    Assertions.assertThat(estadoMemoria.getId()).isNotNull();
    Assertions.assertThat(estadoMemoria.getMemoria().getTitulo())
        .isEqualTo(replaceEstadoMemoria.getMemoria().getTitulo());
    Assertions.assertThat(estadoMemoria.getTipoEstadoMemoria().getNombre())
        .isEqualTo(replaceEstadoMemoria.getTipoEstadoMemoria().getNombre());
  }

  @Test
  public void findAll_WithPaging_ReturnsEstadoMemoriaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<EstadoMemoria>> response = restTemplate.exchange(ESTADO_MEMORIA_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(headers, null), new ParameterizedTypeReference<List<EstadoMemoria>>() {
        });

    // then: Respuesta OK, EstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoMemoria> estadoMemorias = response.getBody();
    Assertions.assertThat(estadoMemorias.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene la 'Memoria8' y tipo estado memoria nombre='No procede evaluar'
    Assertions.assertThat(estadoMemorias.get(0).getMemoria().getTitulo()).isEqualTo("Memoria008");
    Assertions.assertThat(estadoMemorias.get(0).getTipoEstadoMemoria().getNombre()).isEqualTo("No procede evaluar");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEstadoMemoriaList() throws Exception {
    // when: Búsqueda por titulo like e id equals
    Long id = 5L;
    String query = "memoria.titulo=ke=Memoria;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EstadoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EstadoMemoria>>() {
        });

    // then: Respuesta OK, EstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoMemoria> estadoMemorias = response.getBody();
    Assertions.assertThat(estadoMemorias.size()).isEqualTo(1);
    Assertions.assertThat(estadoMemorias.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(estadoMemorias.get(0).getMemoria().getTitulo()).startsWith("Memoria");
    Assertions.assertThat(estadoMemorias.get(0).getTipoEstadoMemoria().getNombre()).startsWith("En evaluación");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEstadoMemoriaList() throws Exception {
    // when: Ordenación por memoria titulo desc
    String query = "id,desc";

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EstadoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EstadoMemoria>>() {
        });

    // then: Respuesta OK, EstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoMemoria> estadoMemorias = response.getBody();
    Assertions.assertThat(estadoMemorias.size()).isEqualTo(6);
    for (int i = 0; i < 6; i++) {
      EstadoMemoria estadoMemoria = estadoMemorias.get(i);
      Assertions.assertThat(estadoMemoria.getId()).isEqualTo(8 - i);
      Assertions.assertThat(estadoMemoria.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 8 - i));
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEstadoMemoriaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por memoria titulo desc
    String sort = "id,desc";
    // when: Filtra por titulo like e id equals
    String filter = "memoria.titulo=ke=Memoria";

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<EstadoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<EstadoMemoria>>() {
        });

    // then: Respuesta OK, EstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoMemoria> estadoMemorias = response.getBody();
    Assertions.assertThat(estadoMemorias.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene titulo='Memoria8', 'Memoria7', 'Memoria6'
    Assertions.assertThat(estadoMemorias.get(0).getMemoria().getTitulo())
        .isEqualTo("Memoria" + String.format("%03d", 8));
    Assertions.assertThat(estadoMemorias.get(1).getMemoria().getTitulo())
        .isEqualTo("Memoria" + String.format("%03d", 7));
    Assertions.assertThat(estadoMemorias.get(2).getMemoria().getTitulo())
        .isEqualTo("Memoria" + String.format("%03d", 6));

  }

  /**
   * Función que devuelve un objeto estado memoria
   * 
   * @param id                   id del estado memoria
   * @param idDatosEstadoMemoria id de la memoria y del tipo estado memoria
   * @return el objeto estado memoria
   */
  private EstadoMemoria generarMockEstadoMemoria(Long id, Long idDatosEstadoMemoria) {
    return new EstadoMemoria(id,
        generarMockMemoria(idDatosEstadoMemoria, "ref-9898", "Memoria" + String.format("%03d", idDatosEstadoMemoria),
            1),
        generarMockTipoEstadoMemoria(idDatosEstadoMemoria,
            "TipoEstadoMemoria" + String.format("%03d", idDatosEstadoMemoria), Boolean.TRUE),
        Instant.now());

  }

  /**
   * Función que devuelve un objeto EstadoMemoria.
   * 
   * @param id            id del memoria.
   * @param numReferencia número de la referencia de la memoria.
   * @param titulo        titulo de la memoria.
   * @param version       version de la memoria.
   * @return el objeto tipo Memoria
   */

  private Memoria generarMockMemoria(Long id, String numReferencia, String titulo, Integer version) {

    return new Memoria(id, numReferencia, generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id),
        generarMockComite(id, "comite" + id, true), titulo, "user-00" + id,
        generarMockTipoMemoria(1L, "TipoMemoria1", true),
        generarMockTipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.TRUE,
        generarMockRetrospectiva(1L), version, "CodOrganoCompetente", Boolean.TRUE, null);
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
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
   * Función que devuelve un objeto comité.
   * 
   * @param id     identificador del comité.
   * @param comite comité.
   * @param activo indicador de activo.
   */
  private Comite generarMockComite(Long id, String comite, Boolean activo) {
    Formulario formulario = new Formulario(id, "M" + id + "0", "Descripcion");
    return new Comite(id, comite, "nombreInvestigacion", Genero.M, formulario, activo);

  }

  /**
   * Función que devuelve un objeto tipo memoria.
   * 
   * @param id     identificador del tipo memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   */
  private TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoEstadoMemoria(id, nombre, activo);

  }

  /**
   * Función que devuelve un objeto tipo memoria.
   * 
   * @param id     identificador del tipo memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   */
  private TipoMemoria generarMockTipoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoMemoria(id, nombre, activo);

  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva generarMockRetrospectiva(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(generarMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva generarMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("NombreEstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }

}