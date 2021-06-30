package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
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
 * Test de integracion de ContextoProyecto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContextoProyectoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyecto-contextoproyectos";

  private HttpEntity<ContextoProyecto> buildRequest(HttpHeaders headers, ContextoProyecto entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-C", "CSP-PRO-E")));

    HttpEntity<ContextoProyecto> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsContextoProyecto() throws Exception {

    // given: new ContextoProyecto
    ContextoProyecto newContextoProyecto = generarMockContextoProyecto(null);

    // when: create ContextoProyecto
    final ResponseEntity<ContextoProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newContextoProyecto), ContextoProyecto.class);

    // then: new ContextoProyecto is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ContextoProyecto responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()")
        .isEqualTo(newContextoProyecto.getProyectoId());
    Assertions.assertThat(responseData.getIntereses()).as("getIntereses()")
        .isEqualTo(newContextoProyecto.getIntereses());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/contexto_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsContextoProyecto() throws Exception {
    Long idContextoProyecto = 1L;
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(1L);

    final ResponseEntity<ContextoProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, contextoProyecto), ContextoProyecto.class, idContextoProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ContextoProyecto contextoProyectoActualizado = response.getBody();
    Assertions.assertThat(contextoProyectoActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(contextoProyectoActualizado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(contextoProyecto.getProyectoId());
    Assertions.assertThat(contextoProyectoActualizado.getIntereses()).as("getIntereses()")
        .isEqualTo(contextoProyecto.getIntereses());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/contexto_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findContextoProyectoProyecto_ReturnsContextoProyecto() throws Exception {
    Long idProyecto = 1L;

    final ResponseEntity<ContextoProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ContextoProyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ContextoProyecto contextoProyecto = response.getBody();
    Assertions.assertThat(contextoProyecto.getProyectoId()).as("getProyectoId()").isEqualTo(idProyecto);
    Assertions.assertThat(contextoProyecto.getIntereses()).as("getIntereses()")
        .isEqualTo(contextoProyecto.getIntereses());

  }

  /**
   * Función que devuelve un objeto ContextoProyecto
   * 
   * @param id id del ContextoProyecto
   * @return el objeto ContextoProyecto
   */
  private ContextoProyecto generarMockContextoProyecto(Long id) {
    ContextoProyecto contextoProyecto = new ContextoProyecto();
    contextoProyecto.setId(id);
    contextoProyecto.setProyectoId(id == null ? 1L : id);
    contextoProyecto.setIntereses("intereses");
    return contextoProyecto;
  }

}
