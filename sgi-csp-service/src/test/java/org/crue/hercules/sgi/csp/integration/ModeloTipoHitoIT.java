package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
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
 * Test de integracion de ModeloTipoHito.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ModeloTipoHitoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelotipohitos";

  private HttpEntity<ModeloTipoHito> buildRequest(HttpHeaders headers, ModeloTipoHito entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-ME-C", "CSP-ME-E")));

    HttpEntity<ModeloTipoHito> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsModeloTipoHito() throws Exception {

    // given: new ModeloTipoHito
    ModeloTipoHito data = generarModeloTipoHito(null, 1L, 1L);

    // when: create ModeloTipoHito
    final ResponseEntity<ModeloTipoHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, data), ModeloTipoHito.class);

    // then: new ModeloTipoHito is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ModeloTipoHito responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(data.getModeloEjecucion().getId());
    Assertions.assertThat(responseData.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(data.getTipoHito().getId());
    Assertions.assertThat(responseData.getSolicitud()).as("getSolicitud()").isEqualTo(data.getSolicitud());
    Assertions.assertThat(responseData.getProyecto()).as("getProyecto()").isEqualTo(data.getProyecto());
    Assertions.assertThat(responseData.getConvocatoria()).as("getConvocatoria()").isEqualTo(data.getConvocatoria());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(data.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsModeloTipoHito() throws Exception {

    // given: Existing ModeloTipoHito to be updated
    ModeloTipoHito modeloTipoHitoExistente = generarModeloTipoHito(1L, 1L, 1L);
    ModeloTipoHito modeloTipoHito = generarModeloTipoHito(1L, 1L, 1L);
    modeloTipoHito.setSolicitud(Boolean.FALSE);
    modeloTipoHito.setProyecto(Boolean.TRUE);
    modeloTipoHito.setConvocatoria(Boolean.FALSE);

    // when: update ModeloTipoHito
    final ResponseEntity<ModeloTipoHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, modeloTipoHito), ModeloTipoHito.class, modeloTipoHitoExistente.getId());

    // then: ModeloTipoHito is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloTipoHito responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloTipoHitoExistente.getModeloEjecucion().getId());
    Assertions.assertThat(responseData.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(modeloTipoHitoExistente.getTipoHito().getId());
    Assertions.assertThat(responseData.getSolicitud()).as("getSolicitud()").isEqualTo(modeloTipoHito.getSolicitud());
    Assertions.assertThat(responseData.getProyecto()).as("getProyecto()").isEqualTo(modeloTipoHito.getProyecto());
    Assertions.assertThat(responseData.getConvocatoria()).as("getConvocatoria()")
        .isEqualTo(modeloTipoHito.getConvocatoria());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(modeloTipoHitoExistente.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ModeloTipoHito to be disabled
    Long id = 1L;

    // when: disable ModeloTipoHito
    final ResponseEntity<TipoHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), TipoHito.class, id);

    // then: ModeloTipoHito is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsModeloTipoHito() throws Exception {
    Long id = 1L;

    final ResponseEntity<ModeloTipoHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ModeloTipoHito.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloTipoHito responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getTipoHito().getId()).as("getTipoHito().getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getSolicitud()).as("getSolicitud()").isEqualTo(Boolean.TRUE);
    Assertions.assertThat(responseData.getProyecto()).as("getProyecto()").isEqualTo(Boolean.TRUE);
    Assertions.assertThat(responseData.getConvocatoria()).as("getConvocatoria()").isEqualTo(Boolean.TRUE);
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto ModeloTipoHito
   * 
   * @param modeloTipoHitoId
   * @param modeloEjecucionId
   * @param tipoHitoId
   * @return el objeto ModeloTipoHito
   */
  private ModeloTipoHito generarModeloTipoHito(Long modeloTipoHitoId, Long modeloEjecucionId, Long tipoHitoId) {

    ModeloTipoHito modeloTipoHito = new ModeloTipoHito();
    modeloTipoHito.setId(modeloTipoHitoId);
    modeloTipoHito.setModeloEjecucion(ModeloEjecucion.builder().id(modeloEjecucionId).activo(Boolean.TRUE).build());
    modeloTipoHito.setTipoHito(TipoHito.builder().id(tipoHitoId).activo(Boolean.TRUE).build());
    modeloTipoHito.setSolicitud(Boolean.TRUE);
    modeloTipoHito.setProyecto(Boolean.TRUE);
    modeloTipoHito.setConvocatoria(Boolean.TRUE);
    modeloTipoHito.setActivo(Boolean.TRUE);

    return modeloTipoHito;
  }
}
