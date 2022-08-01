package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
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
 * Test de integracion de ModeloTipoDocumento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ModeloTipoDocumentoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelotipodocumentos";

  private HttpEntity<ModeloTipoDocumento> buildRequest(HttpHeaders headers, ModeloTipoDocumento entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-ME-C", "CSP-ME-E")));

    HttpEntity<ModeloTipoDocumento> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsModeloTipoDocumento() throws Exception {

    // given: new ModeloTipoDocumento
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(1L, 1L, 1L);
    modeloTipoDocumento.setId(null);

    // when: create ModeloTipoDocumento
    final ResponseEntity<ModeloTipoDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, modeloTipoDocumento), ModeloTipoDocumento.class);

    // then: new ModeloTipoDocumento is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ModeloTipoDocumento modeloTipoDocumentoResponse = response.getBody();
    Assertions.assertThat(modeloTipoDocumentoResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoResponse.getId()).as("getId()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoResponse.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoResponse.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloTipoDocumento.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoDocumentoResponse.getTipoDocumento()).as("getTipoDocumento()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoResponse.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(modeloTipoDocumento.getTipoDocumento().getId());
    Assertions.assertThat(modeloTipoDocumentoResponse.getModeloTipoFase()).as("getModeloTipoFase()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoResponse.getModeloTipoFase().getId()).as("getModeloTipoFase().getId()")
        .isEqualTo(modeloTipoDocumento.getModeloTipoFase().getId());
    Assertions.assertThat(modeloTipoDocumentoResponse.getActivo()).as("getActivo()").isTrue();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    // given: existing ModeloTipoDocumento to be disabled
    Long id = 1L;

    // when: disable ModeloTipoDocumento
    final ResponseEntity<ModeloTipoDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ModeloTipoDocumento.class, id);

    // then: ModeloTipoDocumento is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsModeloTipoDocumento() throws Exception {
    Long id = 1L;

    final ResponseEntity<ModeloTipoDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ModeloTipoDocumento.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloTipoDocumento modeloTipoDocumentoResponse = response.getBody();

    Assertions.assertThat(modeloTipoDocumentoResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoResponse.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(modeloTipoDocumentoResponse.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoResponse.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(modeloTipoDocumentoResponse.getTipoDocumento()).as("getTipoDocumento()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoResponse.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(modeloTipoDocumentoResponse.getModeloTipoFase()).as("getModeloTipoFase()").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoResponse.getModeloTipoFase().getId()).as("getModeloTipoFase().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(modeloTipoDocumentoResponse.getActivo()).as("getActivo()").isTrue();
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setDescripcion("descripcion-" + id);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento
   * 
   * @param id               id del ModeloTipoDocumento
   * @param idTipoDocumento  id idTipoDocumento del TipoDocumento
   * @param idModeloTipoFase id del ModeloTipoFase
   * @return el objeto ModeloTipoDocumento
   */
  private ModeloTipoDocumento generarMockModeloTipoDocumento(Long id, Long idTipoDocumento, Long idModeloTipoFase) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoDocumento modeloTipoDocumento = new ModeloTipoDocumento();
    modeloTipoDocumento.setId(id);
    modeloTipoDocumento.setModeloEjecucion(modeloEjecucion);
    modeloTipoDocumento.setTipoDocumento(
        generarMockTipoDocumento(idTipoDocumento, "TipoDocumento" + String.format("%03d", idTipoDocumento)));
    if (idModeloTipoFase != null) {
      modeloTipoDocumento.setModeloTipoFase(generarMockModeloTipoFase(idModeloTipoFase, idModeloTipoFase));
    }

    modeloTipoDocumento.setActivo(true);

    return modeloTipoDocumento;
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento
   * 
   * @param id id del ModeloTipoDocumento
   * @param id idTipoDocumento del TipoDocumento
   * @return el objeto ModeloTipoDocumento
   */
  private ModeloTipoFase generarMockModeloTipoFase(Long id, Long idTipoFase) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(idTipoFase);
    tipoFase.setNombre("nombre-" + idTipoFase);
    tipoFase.setDescripcion("descripcion-" + idTipoFase);
    tipoFase.setActivo(Boolean.TRUE);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setActivo(true);
    modeloTipoFase.setTipoFase(tipoFase);

    return modeloTipoFase;
  }
}
