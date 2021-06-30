package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
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
 * Test de integracion de Acta.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off
  "classpath:scripts/formulario.sql",
  "classpath:scripts/comite.sql", 
  "classpath:scripts/tipo_convocatoria_reunion.sql",
  "classpath:scripts/tipo_estado_acta.sql",
  "classpath:scripts/convocatoria_reunion.sql",
  "classpath:scripts/acta.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class ActaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ACTA_CONTROLLER_BASE_PATH = "/actas";
  private static final String ESTADOACTA_CONTROLLER_BASE_PATH = "/estadoactas";

  private HttpEntity<Acta> buildRequest(HttpHeaders headers, Acta entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Acta> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getActa_WithId_ReturnsActa() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    final ResponseEntity<Acta> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(headers, null), Acta.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Acta acta = response.getBody();

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(2L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(2L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen124");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(124);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1L);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);
  }

  @Test
  public void addActa_ReturnsActa() throws Exception {

    Acta nuevoActa = generarMockActa(null, 123);

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C")));

    final ResponseEntity<Acta> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(headers, nuevoActa), Acta.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final Acta acta = response.getBody();

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(9L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(2L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen123");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(123);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1L);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);

    // When: se ha creado un nuevo acta
    // Then: deberá existir un EstadoActa inicial para ese acta creado

    // Acta creado
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));
    final ResponseEntity<Acta> fullDataActa = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(headers, null), Acta.class, acta.getId());

    String query = "acta.id==" + acta.getId();
    URI uri = UriComponentsBuilder.fromUriString(ESTADOACTA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda EstadoEstado con el Acta creado
    final ResponseEntity<List<EstadoActa>> responseEstadoActa = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<EstadoActa>>() {
        });

    // then: Respuesta OK, retorna el EstadoActa inicial del Acta creado
    Assertions.assertThat(responseEstadoActa.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEstadoActa.getBody().size()).as("size").isEqualTo(1);
    Assertions.assertThat(responseEstadoActa.getBody().get(0).getTipoEstadoActa()).as("tipoEstadoActa")
        .isEqualTo(fullDataActa.getBody().getEstadoActual());
    Assertions.assertThat(responseEstadoActa.getBody().get(0).getActa()).as("acta").isEqualTo(fullDataActa.getBody());

  }

  @Test
  public void removeActa_Success() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-B")));

    // when: Delete con id existente
    long id = 2L;
    final ResponseEntity<Acta> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(headers, null), Acta.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void removeActa_DoNotGetActa() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-B")));

    final ResponseEntity<Acta> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(headers, null), Acta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void replaceActa_ReturnsActa() throws Exception {

    Acta replaceActa = generarMockActa(2L, 456);

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-E")));

    final ResponseEntity<Acta> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(headers, replaceActa), Acta.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Acta acta = response.getBody();

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(2L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(2L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen456");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(456);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1L);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);
  }

  @Test
  public void findAll_WithPaging_ReturnsActaWithNumEvaluacionesSubList() throws Exception {
    // when: Obtiene la page=1 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    // when: Ordenación por id asc
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false).toUri();

    final ResponseEntity<List<ActaWithNumEvaluaciones>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ActaWithNumEvaluaciones>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ActaWithNumEvaluaciones> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).as("x-page").isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).as("x-page-size").isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).as("x-total-count").isEqualTo("7");

    // Contiene de id=7 a 8
    Assertions.assertThat(actas.get(0).getId()).as("0.id").isEqualTo(7);
    Assertions.assertThat(actas.get(1).getId()).as("1.id").isEqualTo(8);
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredActaWithNumEvaluacionesList() throws Exception {
    // when: Búsqueda por acta id equals
    Long id = 5L;
    String query = "id==" + id;

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<ActaWithNumEvaluaciones>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ActaWithNumEvaluaciones>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ActaWithNumEvaluaciones> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(1);
    Assertions.assertThat(actas.get(0).getId()).as("id").isEqualTo(id);
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedActaWithNumEvaluacionesList() throws Exception {
    // when: Ordenación por id desc
    String sort = "id,desc";

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<ActaWithNumEvaluaciones>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ActaWithNumEvaluaciones>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ActaWithNumEvaluaciones> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(7);
    for (int i = 0; i < 7; i++) {
      ActaWithNumEvaluaciones acta = actas.get(i);
      Assertions.assertThat(acta.getId()).as((8 - i) + ".id").isEqualTo(8 - i);
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsActaWithNumEvaluacionesSubList() throws Exception {
    // when: Obtiene page=0 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    // when: Ordena por id desc
    String sort = "id,desc";
    // when: Filtra por id menor
    String filter = "id=lt=4";

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<ActaWithNumEvaluaciones>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ActaWithNumEvaluaciones>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ActaWithNumEvaluaciones> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("x-page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("x-page-size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("x-total-count").isEqualTo("2");

    // Contiene id=3, 2
    Assertions.assertThat(actas.get(0).getId()).as("0.id").isEqualTo(3);
    Assertions.assertThat(actas.get(1).getId()).as("1.id").isEqualTo(2);
  }

  @Test
  public void finishActa_Success() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-FIN")));

    // when: Finalizar acta con id existente
    long id = 2L;
    final ResponseEntity<Acta> response = restTemplate.exchange(
        ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/finalizar", HttpMethod.PUT, buildRequest(headers, null),
        Acta.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void finishActa_DoNotGetActa() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-FIN")));

    final ResponseEntity<Acta> response = restTemplate.exchange(
        ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/finalizar", HttpMethod.PUT, buildRequest(headers, null),
        Acta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  /**
   * Función que devuelve un objeto Acta
   * 
   * @param id     id del acta
   * @param numero numero del acta
   * @return el objeto Acta
   */
  public Acta generarMockActa(Long id, Integer numero) {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite();
    comite.setId(1L);
    comite.setComite("CEEA");
    comite.setFormulario(formulario);
    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);
    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(2L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.parse("2020-08-01T00:00:00Z"));
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(1L);
    tipoEstadoActa.setNombre("En elaboración");
    tipoEstadoActa.setActivo(Boolean.TRUE);

    Acta acta = new Acta();
    acta.setId(id);
    acta.setConvocatoriaReunion(convocatoriaReunion);
    acta.setHoraInicio(10);
    acta.setMinutoInicio(15);
    acta.setHoraFin(12);
    acta.setMinutoFin(0);
    acta.setResumen("Resumen" + numero);
    acta.setNumero(numero);
    acta.setEstadoActual(tipoEstadoActa);
    acta.setInactiva(true);
    acta.setActivo(true);

    return acta;
  }

  /**
   * Función que devuelve un objeto ActaWithNumEvaluaciones
   * 
   * @param id     id del acta
   * @param numero numero del acta
   * @return el objeto Acta
   */
  public ActaWithNumEvaluaciones generarMockActaWithNumEvaluaciones(Long id, Integer numero) {
    Acta acta = generarMockActa(id, numero);

    ActaWithNumEvaluaciones returnValue = new ActaWithNumEvaluaciones();
    returnValue.setId(acta.getId());
    returnValue.setComite(acta.getConvocatoriaReunion().getComite().getComite());
    returnValue.setFechaEvaluacion(acta.getConvocatoriaReunion().getFechaEvaluacion());
    returnValue.setNumeroActa(acta.getNumero());
    returnValue.setConvocatoria(acta.getConvocatoriaReunion().getTipoConvocatoriaReunion().getNombre());
    returnValue.setNumEvaluaciones(1);
    returnValue.setNumRevisiones(2);
    returnValue.setNumTotal(returnValue.getNumEvaluaciones() + returnValue.getNumRevisiones());
    returnValue.setEstadoActa(acta.getEstadoActual());
    return returnValue;
  }

}