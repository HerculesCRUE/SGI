package org.crue.hercules.sgi.csp.integration;

import java.time.Instant;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
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
 * Test de integracion de ProyectoPaqueteTrabajo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProyectoPaqueteTrabajoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectopaquetetrabajos";

  private HttpEntity<ProyectoPaqueteTrabajo> buildRequest(HttpHeaders headers, ProyectoPaqueteTrabajo entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-PRO-E")));

    HttpEntity<ProyectoPaqueteTrabajo> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_paquete_trabajo.sql" })
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_paquete_trabajo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsProyectoPaqueteTrabajo() throws Exception {
    Long idProyectoPaqueteTrabajo = 1L;

    final ResponseEntity<ProyectoPaqueteTrabajo> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ProyectoPaqueteTrabajo.class, idProyectoPaqueteTrabajo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = response.getBody();
    Assertions.assertThat(proyectoPaqueteTrabajo.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoPaqueteTrabajo.getId()).as("getId()").isEqualTo(idProyectoPaqueteTrabajo);
    Assertions.assertThat(proyectoPaqueteTrabajo.getProyectoId()).as("getProyectoId()").isEqualTo(1L);
    Assertions.assertThat(proyectoPaqueteTrabajo.getNombre()).as("getNombre()")
        .isEqualTo("proyecto-paquete-trabajo-001");
    Assertions.assertThat(proyectoPaqueteTrabajo.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(Instant.parse("2020-01-01T00:00:00Z"));
    Assertions.assertThat(proyectoPaqueteTrabajo.getFechaFin()).as("getFechaFin()")
        .isEqualTo(Instant.parse("2020-01-15T23:59:59Z"));
    Assertions.assertThat(proyectoPaqueteTrabajo.getPersonaMes()).as("getPersonaMes()").isEqualTo(1D);
    Assertions.assertThat(proyectoPaqueteTrabajo.getDescripcion()).as("getDescripcion()")
        .isEqualTo("descripcion-proyecto-equipo-trabajo-001");

  }

}
