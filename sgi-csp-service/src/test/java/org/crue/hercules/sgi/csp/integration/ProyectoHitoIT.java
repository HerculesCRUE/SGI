package org.crue.hercules.sgi.csp.integration;

import java.time.Instant;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.ProyectoHitoAvisoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoHitoInput;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
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
 * Test de integracion de ProyectoHito.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoHitoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectohitos";

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-E", "AUTH")));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/tipo_hito.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_hito.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoHito() throws Exception {
    // given: new ProyectoHito
    ProyectoHitoInput newProyectoHito = generarMockProyectoHito(1L, 1L);
    newProyectoHito.setAviso(null);

    // when: create ProyectoHito
    final ResponseEntity<ProyectoHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newProyectoHito), ProyectoHito.class);

    // then: new ProyectoHito is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoHito responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()")
        .isEqualTo(newProyectoHito.getProyectoId());
    Assertions.assertThat(responseData.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(newProyectoHito.getTipoHitoId());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/tipo_hito.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_hito.sql", "classpath:scripts/proyecto_hito.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoHito() throws Exception {
    Long idProyectoHito = 1L;
    ProyectoHitoInput proyectoHito = generarMockProyectoHito(1L, 1L);
    proyectoHito.setComentario("comentario modificado");
    proyectoHito.setAviso(null);

    final ResponseEntity<ProyectoHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, proyectoHito), ProyectoHito.class, idProyectoHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoHito proyectoHitoActualizado = response.getBody();
    Assertions.assertThat(proyectoHitoActualizado.getId()).as("getId()").isNotNull();

    Assertions.assertThat(proyectoHitoActualizado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoHito.getProyectoId());
    Assertions.assertThat(proyectoHitoActualizado.getFecha()).as("getFecha()").isEqualTo(proyectoHito.getFecha());
    Assertions.assertThat(proyectoHitoActualizado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(proyectoHito.getTipoHitoId());
    Assertions.assertThat(proyectoHitoActualizado.getComentario()).as("getComentario()")
        .isEqualTo(proyectoHito.getComentario());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/tipo_hito.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_hito.sql", "classpath:scripts/proyecto_hito.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    Long idProyectoHito = 1L;

    final ResponseEntity<ProyectoHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ProyectoHito.class, idProyectoHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/tipo_hito.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_hito.sql", "classpath:scripts/proyecto_hito.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsById_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Proyecto.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/tipo_hito.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_hito.sql", "classpath:scripts/proyecto_hito.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoHito() throws Exception {
    Long idProyectoHito = 1L;

    final ResponseEntity<ProyectoHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ProyectoHito.class, idProyectoHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoHito proyectoHito = response.getBody();
    Assertions.assertThat(proyectoHito.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoHito.getTipoHito().getId()).as("getTipoHito().getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoHito.getProyectoId()).as("getProyectoId()").isEqualTo(1L);
    Assertions.assertThat(proyectoHito.getComentario()).as("comentario")
        .isEqualTo("comentario-proyecto-hito-" + String.format("%03d", idProyectoHito));
    Assertions.assertThat(proyectoHito.getFecha()).as("getFecha()").isEqualTo("2020-10-01T00:00:00Z");

  }

  private ProyectoHitoInput generarMockProyectoHito(Long tipoHitoId, Long proyectoId) {

    // @formatter:off
    return ProyectoHitoInput.builder()
        .tipoHitoId(1L)
        .proyectoId(proyectoId)
        .fecha(Instant.parse("2020-10-01T00:00:00Z"))
        .comentario("comentario-proyecto-hito-")
        .aviso(ProyectoHitoAvisoInput.builder().build())
        .build();
    // @formatter:on
  }
}
