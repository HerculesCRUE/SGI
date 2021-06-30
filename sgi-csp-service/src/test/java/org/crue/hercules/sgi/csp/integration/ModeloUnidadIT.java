package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de ModeloUnidad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ModeloUnidadIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelounidades";

  private HttpEntity<ModeloUnidad> buildRequest(HttpHeaders headers, ModeloUnidad entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-CON-V",
        "CSP-CON-C", "CSP-CON-E", "CSP-CON-INV-V", "CSP-ME-C", "CSP-ME-E")));

    HttpEntity<ModeloUnidad> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsModeloUnidad() throws Exception {

    // given: new ModeloUnidad
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(null, "unidad-1");

    // when: create ModeloUnidad
    final ResponseEntity<ModeloUnidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, modeloUnidad), ModeloUnidad.class);

    // then: new ModeloUnidad is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ModeloUnidad modeloUnidadResponse = response.getBody();
    Assertions.assertThat(modeloUnidadResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloUnidadResponse.getId()).as("getId()").isNotNull();
    Assertions.assertThat(modeloUnidadResponse.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloUnidadResponse.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloUnidad.getModeloEjecucion().getId());
    Assertions.assertThat(modeloUnidadResponse.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(modeloUnidad.getUnidadGestionRef());
    Assertions.assertThat(modeloUnidadResponse.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ModeloUnidad to be disabled
    Long id = 1L;

    // when: disable ModeloUnidad
    final ResponseEntity<ModeloUnidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ModeloUnidad.class, id);

    // then: ModeloUnidad is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsModeloUnidad() throws Exception {
    Long id = 1L;

    final ResponseEntity<ModeloUnidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ModeloUnidad.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloUnidad modeloUnidadResponse = response.getBody();

    Assertions.assertThat(modeloUnidadResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloUnidadResponse.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(modeloUnidadResponse.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloUnidadResponse.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(modeloUnidadResponse.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo("unidad-001");
    Assertions.assertThat(modeloUnidadResponse.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsModeloUnidadSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-ME-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "unidadGestionRef,desc";
    String filter = "modeloEjecucion.descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<ModeloUnidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloUnidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloUnidad> modeloUnidades = response.getBody();
    Assertions.assertThat(modeloUnidades.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modeloUnidades.get(0).getUnidadGestionRef()).as("get(0).getUnidadGestionRef()")
        .isEqualTo("3");
    Assertions.assertThat(modeloUnidades.get(1).getUnidadGestionRef()).as("get(1).getUnidadGestionRef()")
        .isEqualTo("2");
    Assertions.assertThat(modeloUnidades.get(2).getUnidadGestionRef()).as("get(2).getUnidadGestionRef()")
        .isEqualTo("1");
  }

  /**
   * Función que devuelve un objeto ModeloUnidad
   * 
   * @param id               id del ModeloUnidad
   * @param unidadGestionRef unidadGestionRef
   * @return el objeto ModeloUnidad
   */
  private ModeloUnidad generarMockModeloUnidad(Long id, String unidadGestionRef) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(id);
    modeloUnidad.setModeloEjecucion(modeloEjecucion);
    modeloUnidad.setUnidadGestionRef(unidadGestionRef);
    modeloUnidad.setActivo(true);

    return modeloUnidad;
  }

}
