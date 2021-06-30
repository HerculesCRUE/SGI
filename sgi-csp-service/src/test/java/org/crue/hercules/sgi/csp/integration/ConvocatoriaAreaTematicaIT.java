package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
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
 * Test de integracion de ConvocatoriaAreaTematica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConvocatoriaAreaTematicaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaareatematicas";

  private HttpEntity<ConvocatoriaAreaTematica> buildRequest(HttpHeaders headers, ConvocatoriaAreaTematica entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-CON-C", "CSP-CON-E")));

    HttpEntity<ConvocatoriaAreaTematica> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaAreaTematica() throws Exception {

    // given: new ConvocatoriaAreaTematica
    ConvocatoriaAreaTematica newConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(null, 1L, 1L);

    // when: create ConvocatoriaAreaTematica
    final ResponseEntity<ConvocatoriaAreaTematica> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newConvocatoriaAreaTematica), ConvocatoriaAreaTematica.class);

    // then: new ConvocatoriaAreaTematica is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaAreaTematica responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newConvocatoriaAreaTematica.getConvocatoriaId());
    Assertions.assertThat(responseData.getAreaTematica().getId()).as("getAreaTematica().getId()")
        .isEqualTo(newConvocatoriaAreaTematica.getAreaTematica().getId());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones().getId()")
        .isEqualTo(newConvocatoriaAreaTematica.getObservaciones());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsConvocatoriaAreaTematica() throws Exception {

    // given: Existing ConvocatoriaAreaTematica to be updated
    ConvocatoriaAreaTematica convocatoriaAreaTematicaExistente = generarConvocatoriaAreaTematica(1L, 1L, 2L);
    ConvocatoriaAreaTematica convocatoriaAreaTematica = generarConvocatoriaAreaTematica(1L, 1L, 2L);
    convocatoriaAreaTematica.setObservaciones("observaciones-modificadas");

    // when: update ConvocatoriaAreaTematica
    final ResponseEntity<ConvocatoriaAreaTematica> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, convocatoriaAreaTematica),
        ConvocatoriaAreaTematica.class, convocatoriaAreaTematicaExistente.getId());

    // then: ConvocatoriaAreaTematica is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ConvocatoriaAreaTematica responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaAreaTematicaExistente.getConvocatoriaId());
    Assertions.assertThat(responseData.getAreaTematica().getId()).as("getAreaTematica().getId()")
        .isEqualTo(convocatoriaAreaTematicaExistente.getAreaTematica().getId());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaAreaTematica.getObservaciones());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ConvocatoriaAreaTematica to be deleted
    Long id = 1L;

    // when: delete ConvocatoriaAreaTematica
    final ResponseEntity<ConvocatoriaAreaTematica> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ConvocatoriaAreaTematica.class, id);

    // then: ConvocatoriaAreaTematica deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaAreaTematica() throws Exception {
    Long id = 1L;

    final ResponseEntity<ConvocatoriaAreaTematica> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaAreaTematica.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ConvocatoriaAreaTematica responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getAreaTematica().getId()).as("getAreaTematica().getId()").isEqualTo(2L);
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones-001");
  }

  /**
   * Función que devuelve un objeto ConvocatoriaAreaTematica
   * 
   * @param convocatoriaAreaTematicaId
   * @param convocatoriaId
   * @param areaTematicaId
   * @return el objeto ConvocatoriaAreaTematica
   */
  private ConvocatoriaAreaTematica generarConvocatoriaAreaTematica(Long convocatoriaAreaTematicaId, Long convocatoriaId,
      Long areaTematicaId) {

    return ConvocatoriaAreaTematica.builder().id(convocatoriaAreaTematicaId).convocatoriaId(convocatoriaId)
        .areaTematica(AreaTematica.builder().id(areaTematicaId).build())
        .observaciones("observaciones-" + convocatoriaAreaTematicaId).build();
  }
}
