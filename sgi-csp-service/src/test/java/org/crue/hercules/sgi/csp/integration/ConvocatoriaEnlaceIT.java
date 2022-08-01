package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
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
 * Test de integracion de ConvocatoriaEnlace.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConvocatoriaEnlaceIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaenlaces";

  private HttpEntity<ConvocatoriaEnlace> buildRequest(HttpHeaders headers, ConvocatoriaEnlace entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-E", "AUTH")));

    HttpEntity<ConvocatoriaEnlace> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsConvocatoriaEnlace() throws Exception {
    // given: new ConvocatoriaEnlace
    ConvocatoriaEnlace newConvocatoriaEnlace = generarMockConvocatoriaEnlace(null);
    // when: create ConvocatoriaEnlace
    final ResponseEntity<ConvocatoriaEnlace> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newConvocatoriaEnlace), ConvocatoriaEnlace.class);

    // then: new ConvocatoriaEnlace is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaEnlace responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newConvocatoriaEnlace.getConvocatoriaId());
    Assertions.assertThat(responseData.getTipoEnlace().getId()).as("getTipoEnlace().getId()")
        .isEqualTo(newConvocatoriaEnlace.getTipoEnlace().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsConvocatoriaEnlace() throws Exception {
    Long idConvocatoriaEnlace = 1L;
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(idConvocatoriaEnlace);

    final ResponseEntity<ConvocatoriaEnlace> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, convocatoriaEnlace), ConvocatoriaEnlace.class, idConvocatoriaEnlace);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaEnlace convocatoriaEnlaceActualizado = response.getBody();
    Assertions.assertThat(convocatoriaEnlaceActualizado.getId()).as("getId()").isNotNull();

    Assertions.assertThat(convocatoriaEnlaceActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEnlace.getConvocatoriaId());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(convocatoriaEnlace.getDescripcion());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getTipoEnlace().getId()).as("getTipoEnlace().getId()")
        .isEqualTo(convocatoriaEnlace.getTipoEnlace().getId());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getUrl()).as("getUrl()").isEqualTo(convocatoriaEnlace.getUrl());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return404() throws Exception {
    Long idConvocatoriaEnlace = 1L;

    final ResponseEntity<ConvocatoriaEnlace> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ConvocatoriaEnlace.class, idConvocatoriaEnlace);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsConvocatoriaEnlace() throws Exception {
    Long idConvocatoriaEnlace = 1L;

    final ResponseEntity<ConvocatoriaEnlace> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ConvocatoriaEnlace.class, idConvocatoriaEnlace);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaEnlace convocatoriaEnlace = response.getBody();
    Assertions.assertThat(convocatoriaEnlace.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaEnlace.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaEnlace.getDescripcion()).as("descripcion")
        .isEqualTo(convocatoriaEnlace.getDescripcion());
    Assertions.assertThat(convocatoriaEnlace.getUrl()).as("getUrl()").isEqualTo("www.url1.com");

  }

  /**
   * Función que devuelve un objeto ConvocatoriaEnlace
   * 
   * @param id     id del ConvocatoriaEnlace
   * @param nombre nombre del ConvocatoriaEnlace
   * @return el objeto ConvocatoriaEnlace
   */
  private ConvocatoriaEnlace generarMockConvocatoriaEnlace(Long id) {
    TipoEnlace tipoEnlace = new TipoEnlace();
    tipoEnlace.setId(id == null ? 1 : id);

    ConvocatoriaEnlace convocatoriaEnlace = new ConvocatoriaEnlace();
    convocatoriaEnlace.setId(id);
    convocatoriaEnlace.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaEnlace.setUrl("www.url" + id + ".es");
    convocatoriaEnlace.setDescripcion("descripcion-" + id);
    convocatoriaEnlace.setTipoEnlace(tipoEnlace);

    return convocatoriaEnlace;
  }
}
