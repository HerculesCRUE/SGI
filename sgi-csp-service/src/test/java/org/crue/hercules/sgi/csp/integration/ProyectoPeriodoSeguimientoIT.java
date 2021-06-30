package org.crue.hercules.sgi.csp.integration;

import java.time.Instant;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de ProyectoPeriodoSeguimiento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProyectoPeriodoSeguimientoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoperiodoseguimientos";

  private HttpEntity<ProyectoPeriodoSeguimiento> buildRequest(HttpHeaders headers, ProyectoPeriodoSeguimiento entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-PRO-B", "CSP-PRO-C", "CSP-PRO-E", "CSP-PRO-V")));

    HttpEntity<ProyectoPeriodoSeguimiento> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsProyectoPeriodoSeguimiento() throws Exception {
    // given: new ProyectoPeriodoSeguimiento
    ProyectoPeriodoSeguimiento newProyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L, 1L);
    newProyectoPeriodoSeguimiento.setId(null);

    // when: create ProyectoPeriodoSeguimiento
    final ResponseEntity<ProyectoPeriodoSeguimiento> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newProyectoPeriodoSeguimiento), ProyectoPeriodoSeguimiento.class);

    // then: new ProyectoPeriodoSeguimiento is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoPeriodoSeguimiento responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()")
        .isEqualTo(newProyectoPeriodoSeguimiento.getProyectoId());
    Assertions.assertThat(responseData.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(newProyectoPeriodoSeguimiento.getNumPeriodo());
    Assertions.assertThat(responseData.getTipoSeguimiento()).as("getTipoSeguimiento()")
        .isEqualTo(newProyectoPeriodoSeguimiento.getTipoSeguimiento());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_periodo_seguimiento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsProyectoPeriodoSeguimiento() throws Exception {
    Long idProyectoPeriodoSeguimiento = 1L;
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(
        idProyectoPeriodoSeguimiento, 1L);
    proyectoPeriodoSeguimiento.setObservaciones("obs modificado");

    final ResponseEntity<ProyectoPeriodoSeguimiento> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, proyectoPeriodoSeguimiento),
        ProyectoPeriodoSeguimiento.class, idProyectoPeriodoSeguimiento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoActualizado = response.getBody();
    Assertions.assertThat(proyectoPeriodoSeguimientoActualizado.getId()).as("getId()").isNotNull();

    Assertions.assertThat(proyectoPeriodoSeguimientoActualizado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoPeriodoSeguimiento.getProyectoId());
    Assertions.assertThat(proyectoPeriodoSeguimientoActualizado.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(proyectoPeriodoSeguimiento.getNumPeriodo());
    Assertions.assertThat(proyectoPeriodoSeguimientoActualizado.getTipoSeguimiento()).as("getTipoSeguimiento()")
        .isIn(TipoSeguimiento.FINAL, TipoSeguimiento.INTERMEDIO, TipoSeguimiento.PERIODICO);
    Assertions.assertThat(proyectoPeriodoSeguimientoActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoPeriodoSeguimiento.getObservaciones());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_periodo_seguimiento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idProyectoPeriodoSeguimiento = 1L;

    final ResponseEntity<ProyectoPeriodoSeguimiento> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ProyectoPeriodoSeguimiento.class, idProyectoPeriodoSeguimiento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_periodo_seguimiento.sql" })
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
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_periodo_seguimiento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsProyectoPeriodoSeguimiento() throws Exception {
    Long idProyectoPeriodoSeguimiento = 1L;

    final ResponseEntity<ProyectoPeriodoSeguimiento> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ProyectoPeriodoSeguimiento.class, idProyectoPeriodoSeguimiento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = response.getBody();
    Assertions.assertThat(proyectoPeriodoSeguimiento.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoPeriodoSeguimiento.getProyectoId()).as("getProyectoId()").isEqualTo(1L);
    Assertions.assertThat(proyectoPeriodoSeguimiento.getTipoSeguimiento()).as("getTipoSeguimiento()")
        .isIn(TipoSeguimiento.FINAL, TipoSeguimiento.INTERMEDIO, TipoSeguimiento.PERIODICO);
    Assertions.assertThat(proyectoPeriodoSeguimiento.getObservaciones()).as("getObservaciones()")
        .isEqualTo("obs-" + String.format("%03d", idProyectoPeriodoSeguimiento));

  }

  /**
   * Función que devuelve un objeto ProyectoPeriodoSeguimiento
   * 
   * @param id         id del ProyectoPeriodoSeguimiento
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoPeriodoSeguimiento
   */
  private ProyectoPeriodoSeguimiento generarMockProyectoPeriodoSeguimiento(Long id, Long proyectoId) {

    // @formatter:off
    return ProyectoPeriodoSeguimiento.builder()
        .id(id)
        .proyectoId(proyectoId)
        .numPeriodo(1)
        .fechaInicio(Instant.parse("2020-10-01T00:00:00Z"))
        .tipoSeguimiento(TipoSeguimiento.PERIODICO)
        .fechaFin(Instant.parse("2020-10-04T23:59:59Z"))
        .observaciones("obs-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }
}
