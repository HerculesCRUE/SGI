package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
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
 * Test de integracion de ModeloTipoEnlace.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ModeloTipoEnlaceIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelotipoenlaces";

  private HttpEntity<ModeloTipoEnlace> buildRequest(HttpHeaders headers, ModeloTipoEnlace entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-ME-E", "CSP-ME-C")));

    HttpEntity<ModeloTipoEnlace> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsModeloTipoEnlace() throws Exception {

    // given: new ModeloTipoEnlace
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(null, 1L);

    // when: create ModeloTipoEnlace
    final ResponseEntity<ModeloTipoEnlace> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, modeloTipoEnlace), ModeloTipoEnlace.class);

    // then: new ModeloTipoEnlace is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ModeloTipoEnlace modeloTipoEnlaceResponse = response.getBody();
    Assertions.assertThat(modeloTipoEnlaceResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceResponse.getId()).as("getId()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceResponse.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceResponse.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloTipoEnlace.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoEnlaceResponse.getTipoEnlace()).as("getTipoEnlace()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceResponse.getTipoEnlace().getId()).as("getTipoEnlace().getId()")
        .isEqualTo(modeloTipoEnlace.getTipoEnlace().getId());
    Assertions.assertThat(modeloTipoEnlaceResponse.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ModeloTipoEnlace to be disabled
    Long id = 1L;

    // when: disable ModeloTipoEnlace
    final ResponseEntity<ModeloTipoEnlace> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ModeloTipoEnlace.class, id);

    // then: ModeloTipoEnlace is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsModeloTipoEnlace() throws Exception {
    Long id = 1L;

    final ResponseEntity<ModeloTipoEnlace> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ModeloTipoEnlace.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloTipoEnlace modeloTipoEnlaceResponse = response.getBody();

    Assertions.assertThat(modeloTipoEnlaceResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceResponse.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(modeloTipoEnlaceResponse.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceResponse.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(modeloTipoEnlaceResponse.getTipoEnlace()).as("getTipoEnlace()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceResponse.getTipoEnlace().getId()).as("getTipoEnlace().getId()").isEqualTo(1L);
    Assertions.assertThat(modeloTipoEnlaceResponse.getActivo()).as("getActivo()").isEqualTo(true);
  }

  /**
   * Función que devuelve un objeto TipoEnlace
   * 
   * @param id
   * @param activo
   * @return TipoEnlace
   */
  private TipoEnlace generarMockTipoEnlace(Long id, Boolean activo) {
    return TipoEnlace.builder().id(id).nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo).build();
  }

  /**
   * Función que devuelve un objeto ModeloTipoEnlace
   * 
   * @param id id del ModeloTipoEnlace
   * @param id idTipoEnlace del TipoEnlace
   * @return el objeto ModeloTipoEnlace
   */
  private ModeloTipoEnlace generarMockModeloTipoEnlace(Long id, Long idTipoEnlace) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoEnlace modeloTipoFinalidad = new ModeloTipoEnlace();
    modeloTipoFinalidad.setId(id);
    modeloTipoFinalidad.setModeloEjecucion(modeloEjecucion);
    modeloTipoFinalidad.setTipoEnlace(generarMockTipoEnlace(idTipoEnlace, true));
    modeloTipoFinalidad.setActivo(true);

    return modeloTipoFinalidad;
  }
}
