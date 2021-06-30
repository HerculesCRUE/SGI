package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
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
 * Test de integracion de ModeloTipoFinalidad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ModeloTipoFinalidadIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelotipofinalidades";

  private HttpEntity<ModeloTipoFinalidad> buildRequest(HttpHeaders headers, ModeloTipoFinalidad entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-ME-E", "CSP-ME-C")));

    HttpEntity<ModeloTipoFinalidad> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsModeloTipoFinalidad() throws Exception {

    // given: new ModeloTipoFinalidad
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(null, 1L, 1L);

    // when: create ModeloTipoFinalidad
    final ResponseEntity<ModeloTipoFinalidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, data), ModeloTipoFinalidad.class);

    // then: new ModeloTipoFinalidad is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ModeloTipoFinalidad responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getModeloEjecucion().getId()).as("etModeloEjecucion().getId()")
        .isEqualTo(data.getModeloEjecucion().getId());
    Assertions.assertThat(responseData.getTipoFinalidad().getId()).as("responseData.getTipoFinalidad().getId()")
        .isEqualTo(data.getTipoFinalidad().getId());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(data.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ModeloTipoFinalidad to be disabled
    Long id = 1L;

    // when: disable ModeloTipoFinalidad
    final ResponseEntity<TipoFinalidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), TipoFinalidad.class, id);

    // then: ModeloTipoFinalidad is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsModeloTipoFinalidad() throws Exception {
    Long id = 1L;

    final ResponseEntity<ModeloTipoFinalidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ModeloTipoFinalidad.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloTipoFinalidad responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getTipoFinalidad().getId()).as("getTipoFinalidad().getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto ModeloTipoFinalidad
   * 
   * @param modeloTipoFinalidadId
   * @param modeloEjecucionId
   * @param tipoFinalidadId
   * @return el objeto ModeloTipoFinalidad
   */
  private ModeloTipoFinalidad generarModeloTipoFinalidad(Long modeloTipoFinalidadId, Long modeloEjecucionId,
      Long tipoFinalidadId) {

    ModeloTipoFinalidad modeloTipoFinalidad = new ModeloTipoFinalidad();
    modeloTipoFinalidad.setId(modeloTipoFinalidadId);
    modeloTipoFinalidad
        .setModeloEjecucion(ModeloEjecucion.builder().id(modeloEjecucionId).activo(Boolean.TRUE).build());
    modeloTipoFinalidad.setTipoFinalidad(TipoFinalidad.builder().id(tipoFinalidadId).activo(Boolean.TRUE).build());
    modeloTipoFinalidad.setActivo(Boolean.TRUE);

    return modeloTipoFinalidad;
  }
}
