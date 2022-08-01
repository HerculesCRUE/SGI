package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
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
 * Test de integracion de ModeloTipoFase.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ModeloTipoFaseIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelotipofases";

  private HttpEntity<ModeloTipoFase> buildRequest(HttpHeaders headers, ModeloTipoFase entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-ME-C", "CSP-ME-E", "AUTH")));

    HttpEntity<ModeloTipoFase> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsModeloTipoFase() throws Exception {

    // given: new ModeloTipoFase
    ModeloTipoFase modeloTipoFase = generarModeloTipoFaseConTipoFaseId(null);

    // when: create ModeloTipoFase
    final ResponseEntity<ModeloTipoFase> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, modeloTipoFase), ModeloTipoFase.class);

    // then: new ModeloTipoFase is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ModeloTipoFase modeloTipoFaseResponse = response.getBody();
    Assertions.assertThat(modeloTipoFaseResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoFaseResponse.getId()).as("getId()").isNotNull();
    Assertions.assertThat(modeloTipoFaseResponse.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoFaseResponse.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloTipoFase.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoFaseResponse.getTipoFase()).as("getTipoFase()").isNotNull();
    Assertions.assertThat(modeloTipoFaseResponse.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(modeloTipoFase.getTipoFase().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    // given: existing ModeloTipoFase to be disabled
    Long id = 1L;

    // when: disable ModeloTipoFase
    final ResponseEntity<ModeloTipoFase> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ModeloTipoFase.class, id);

    // then: ModeloTipoFase is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsModeloTipoFase() throws Exception {
    Long id = 1L;

    final ResponseEntity<ModeloTipoFase> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ModeloTipoFase.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloTipoFase modeloTipoFaseResponse = response.getBody();

    Assertions.assertThat(modeloTipoFaseResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoFaseResponse.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(modeloTipoFaseResponse.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoFaseResponse.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(modeloTipoFaseResponse.getTipoFase()).as("getTipoFase()").isNotNull();
    Assertions.assertThat(modeloTipoFaseResponse.getTipoFase().getId()).as("getTipoFase().getId()").isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_WithExistingId_ReturnsModeloTipoFase() throws Exception {

    // given: Entidad existente que se va a actualizar
    ModeloTipoFase modeloTipoFase = generarModeloTipoFase(1L);
    modeloTipoFase.setConvocatoria(false);

    // when: Se actualiza la entidad
    final ResponseEntity<ModeloTipoFase> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, modeloTipoFase), ModeloTipoFase.class, modeloTipoFase.getId());

    ModeloTipoFase updatedModeloTipoFase = response.getBody();
    // then: Los datos se actualizan correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(updatedModeloTipoFase.getConvocatoria()).as("getConvocatoria()").isFalse();
  }

  /*
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoDocumento
   * 
   * @return el objeto TipoDocumento
   */
  private TipoFase generarMockTipoFase(Long id, String nombre) {

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre(nombre);
    tipoFase.setDescripcion("descripcion-" + id);
    tipoFase.setActivo(Boolean.TRUE);

    return tipoFase;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase
   * 
   * @param id id del ModeloTipoFase
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarModeloTipoFase(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setModeloEjecucion(modeloEjecucion);
    modeloTipoFase.setTipoFase(generarMockTipoFase(id, "TipoFase" + String.format("%03d", id)));
    modeloTipoFase.setConvocatoria(true);
    modeloTipoFase.setProyecto(true);
    modeloTipoFase.setSolicitud(false);
    modeloTipoFase.setActivo(true);

    return modeloTipoFase;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase con un TipoFase con id
   * 
   * @param id id del ModeloTipoFase
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarModeloTipoFaseConTipoFaseId(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setModeloEjecucion(modeloEjecucion);
    modeloTipoFase.setTipoFase(generarMockTipoFase(1L, "TipoFase" + String.format("%03d", 1)));
    modeloTipoFase.setConvocatoria(true);
    modeloTipoFase.setProyecto(true);
    modeloTipoFase.setSolicitud(false);
    modeloTipoFase.setActivo(true);

    return modeloTipoFase;
  }
}
