package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
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
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de ProyectoProrroga.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProyectoProrrogaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyecto-prorrogas";
  private static final String PATH_ENTIDAD_DOCUMENTO = "/prorrogadocumentos";

  private HttpEntity<ProyectoProrroga> buildRequest(HttpHeaders headers, ProyectoProrroga entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-PRO-B", "CSP-PRO-C", "CSP-PRO-E", "CSP-PRO-V")));

    HttpEntity<ProyectoProrroga> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsProyectoProrroga() throws Exception {
    // given: new ProyectoProrroga
    ProyectoProrroga newProyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    newProyectoProrroga.setId(null);

    // when: create ProyectoProrroga
    final ResponseEntity<ProyectoProrroga> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newProyectoProrroga), ProyectoProrroga.class);

    // then: new ProyectoProrroga is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoProrroga responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()")
        .isEqualTo(newProyectoProrroga.getProyectoId());
    Assertions.assertThat(responseData.getFechaConcesion()).as("getFechaConcesion()")
        .isEqualTo(newProyectoProrroga.getFechaConcesion());
    Assertions.assertThat(responseData.getNumProrroga()).as("getNumProrroga()")
        .isEqualTo(newProyectoProrroga.getNumProrroga());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()").isEqualTo(newProyectoProrroga.getFechaFin());
    Assertions.assertThat(responseData.getTipo()).as("getTipo()").isEqualTo(newProyectoProrroga.getTipo());
    Assertions.assertThat(responseData.getImporte()).as("getImporte()").isEqualTo(newProyectoProrroga.getImporte());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(newProyectoProrroga.getObservaciones());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsProyectoProrroga() throws Exception {
    Long idProyectoProrroga = 5L;
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setObservaciones("observaciones-modificadas");

    final ResponseEntity<ProyectoProrroga> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, proyectoProrroga), ProyectoProrroga.class, idProyectoProrroga);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoProrroga proyectoProrrogaActualizado = response.getBody();
    Assertions.assertThat(proyectoProrrogaActualizado.getId()).as("getId()").isEqualTo(idProyectoProrroga);
    Assertions.assertThat(proyectoProrrogaActualizado.getProyectoId()).as("getProyectoId()").isEqualTo(1L);
    Assertions.assertThat(proyectoProrrogaActualizado.getFechaConcesion()).as("getFechaConcesion()")
        .isEqualTo(Instant.parse("2020-06-01T00:00:00Z"));
    Assertions.assertThat(proyectoProrrogaActualizado.getNumProrroga()).as("getNumProrroga()").isEqualTo(1);
    Assertions.assertThat(proyectoProrrogaActualizado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(Instant.parse("2022-12-31T23:59:59Z"));
    Assertions.assertThat(proyectoProrrogaActualizado.getTipo()).as("getTipo()")
        .isEqualTo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE);
    Assertions.assertThat(proyectoProrrogaActualizado.getImporte()).as("getImporte()")
        .isEqualTo(BigDecimal.valueOf(123.45));
    Assertions.assertThat(proyectoProrrogaActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoProrroga.getObservaciones());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_tipo_fase.sql", "classpath:scripts/modelo_tipo_documento.sql",
      "classpath:scripts/prorroga_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idProyectoProrroga = 5L;

    final ResponseEntity<ProyectoProrroga> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ProyectoProrroga.class, idProyectoProrroga);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existsById_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Proyecto.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existsById_Returns204() throws Exception {
    // given: no existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Proyecto.class, id);
    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsProyectoProrroga() throws Exception {
    Long idProyectoProrroga = 1L;

    final ResponseEntity<ProyectoProrroga> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ProyectoProrroga.class, idProyectoProrroga);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoProrroga proyectoProrroga = response.getBody();
    Assertions.assertThat(proyectoProrroga.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoProrroga.getId()).as("getId()").isEqualTo(idProyectoProrroga);
    Assertions.assertThat(proyectoProrroga.getProyectoId()).as("getProyectoId()").isEqualTo(1L);
    Assertions.assertThat(proyectoProrroga.getFechaConcesion()).as("getFechaConcesion()")
        .isEqualTo(Instant.parse("2020-01-01T00:00:00Z"));
    Assertions.assertThat(proyectoProrroga.getNumProrroga()).as("getNumProrroga()").isEqualTo(1);
    Assertions.assertThat(proyectoProrroga.getFechaFin()).as("getFechaFin()")
        .isEqualTo(Instant.parse("2022-01-31T23:59:59Z"));
    Assertions.assertThat(proyectoProrroga.getTipo()).as("getTipo()").isEqualTo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE);
    Assertions.assertThat(proyectoProrroga.getImporte()).as("getImporte()").isEqualTo(BigDecimal.valueOf(123.45));
    Assertions.assertThat(proyectoProrroga.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-proyecto-prorroga-001");

  }

  /**
   * 
   * PRORROGA DOCUMENTO
   * 
   */

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_tipo_fase.sql", "classpath:scripts/modelo_tipo_documento.sql",
      "classpath:scripts/prorroga_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllProrrogaDocumento_WithPagingSortingAndFiltering_ReturnsProrrogaDocumentoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "comentario=ke=-00";

    Long proyectoProrrogaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_DOCUMENTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(proyectoProrrogaId).toUri();

    final ResponseEntity<List<ProrrogaDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ProrrogaDocumento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProrrogaDocumento> convocatoriasDocumentos = response.getBody();
    Assertions.assertThat(convocatoriasDocumentos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(convocatoriasDocumentos.get(0).getComentario()).as("get(0).getComentario()")
        .isEqualTo("comentario-prorroga-documento-" + String.format("%03d", 3));
    Assertions.assertThat(convocatoriasDocumentos.get(1).getComentario()).as("get(1).getComentario())")
        .isEqualTo("comentario-prorroga-documento-" + String.format("%03d", 2));
    Assertions.assertThat(convocatoriasDocumentos.get(2).getComentario()).as("get(2).getComentario()")
        .isEqualTo("comentario-prorroga-documento-" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto ProyectoProrroga
   * 
   * @param id         id del ProyectoProrroga
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoProrroga
   */
  private ProyectoProrroga generarMockProyectoProrroga(Long id, Long proyectoId) {

    // @formatter:off
    return ProyectoProrroga.builder()
        .id(id)
        .proyectoId(proyectoId)
        .numProrroga(1)
        .fechaConcesion(Instant.parse("2020-06-01T00:00:00Z"))
        .tipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE)
        .fechaFin(Instant.parse("2022-12-31T23:59:59Z"))
        .importe(BigDecimal.valueOf(123.45))
        .observaciones("observaciones-proyecto-prorroga-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }
}
