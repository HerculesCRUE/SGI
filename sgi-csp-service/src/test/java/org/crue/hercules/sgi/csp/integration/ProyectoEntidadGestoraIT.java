package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
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
 * Test de integracion de ProyectoEntidadGestora.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProyectoEntidadGestoraIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoentidadgestoras";

  private HttpEntity<ProyectoEntidadGestora> buildRequest(HttpHeaders headers, ProyectoEntidadGestora entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-PRO-E")));

    HttpEntity<ProyectoEntidadGestora> request = new HttpEntity<>(entity, headers);
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
  public void create_ReturnsProyectoEntidadGestora() throws Exception {
    // given: new ProyectoEntidadGestora
    ProyectoEntidadGestora newProyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    newProyectoEntidadGestora.setId(null);

    // when: create ProyectoEntidadGestora
    final ResponseEntity<ProyectoEntidadGestora> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newProyectoEntidadGestora), ProyectoEntidadGestora.class);

    // then: new ProyectoEntidadGestora is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoEntidadGestora responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()")
        .isEqualTo(newProyectoEntidadGestora.getProyectoId());
    Assertions.assertThat(responseData.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(newProyectoEntidadGestora.getEntidadRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_entidad_gestora.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsProyectoEntidadGestora() throws Exception {
    Long idProyectoEntidadGestora = 1L;
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setEntidadRef("entidad-modificada");

    final ResponseEntity<ProyectoEntidadGestora> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, proyectoEntidadGestora),
        ProyectoEntidadGestora.class, idProyectoEntidadGestora);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoEntidadGestora proyectoEntidadGestoraActualizado = response.getBody();
    Assertions.assertThat(proyectoEntidadGestoraActualizado.getId()).as("getId()").isEqualTo(idProyectoEntidadGestora);
    Assertions.assertThat(proyectoEntidadGestoraActualizado.getProyectoId()).as("getProyectoId()").isEqualTo(1L);
    Assertions.assertThat(proyectoEntidadGestoraActualizado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(proyectoEntidadGestora.getEntidadRef());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_entidad_gestora.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idProyectoEntidadGestora = 1L;

    final ResponseEntity<ProyectoEntidadGestora> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ProyectoEntidadGestora.class, idProyectoEntidadGestora);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  /**
   * Función que devuelve un objeto ProyectoEntidadGestora
   * 
   * @param id         id del ProyectoEntidadGestora
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoEntidadGestora
   */
  private ProyectoEntidadGestora generarMockProyectoEntidadGestora(Long id, Long proyectoId) {

    // @formatter:off
    return ProyectoEntidadGestora.builder()
        .id(id)
        .proyectoId(proyectoId)
        .entidadRef("entidad-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }
}
