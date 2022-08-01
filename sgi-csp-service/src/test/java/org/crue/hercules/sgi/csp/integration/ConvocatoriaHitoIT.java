package org.crue.hercules.sgi.csp.integration;

import java.time.Instant;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoOutput;
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
 * Test de integracion de ConvocatoriaHito.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConvocatoriaHitoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriahitos";

  private HttpEntity<ConvocatoriaHitoInput> buildRequest(HttpHeaders headers, ConvocatoriaHitoInput entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-CON-V", "CSP-CON-C", "CSP-CON-E")));

    HttpEntity<ConvocatoriaHitoInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsConvocatoriaHito() throws Exception {
    // given: new ConvocatoriaHito
    ConvocatoriaHitoInput newConvocatoriaHito = generarMockConvocatoriaHitoInput(null);
    // when: create ConvocatoriaHito
    final ResponseEntity<ConvocatoriaHitoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newConvocatoriaHito), ConvocatoriaHitoOutput.class);

    // then: new ConvocatoriaHito is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaHitoOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newConvocatoriaHito.getConvocatoriaId());
    Assertions.assertThat(responseData.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(newConvocatoriaHito.getTipoHitoId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsConvocatoriaHito() throws Exception {
    Long idConvocatoriaHito = 1L;
    ConvocatoriaHitoInput convocatoriaHito = generarMockConvocatoriaHitoInput(1L);

    final ResponseEntity<ConvocatoriaHitoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, convocatoriaHito), ConvocatoriaHitoOutput.class, idConvocatoriaHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaHitoOutput convocatoriaHitoActualizado = response.getBody();
    Assertions.assertThat(convocatoriaHitoActualizado.getId()).as("getId()").isNotNull();

    Assertions.assertThat(convocatoriaHitoActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaHito.getConvocatoriaId());
    Assertions.assertThat(convocatoriaHitoActualizado.getFecha()).as("getFecha()")
        .isEqualTo(convocatoriaHito.getFecha());
    Assertions.assertThat(convocatoriaHitoActualizado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(convocatoriaHito.getTipoHitoId());
    Assertions.assertThat(convocatoriaHitoActualizado.getComentario()).as("getComentario()")
        .isEqualTo(convocatoriaHito.getComentario());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    Long idConvocatoriaHito = 1L;

    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Void.class, idConvocatoriaHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsConvocatoriaHito() throws Exception {
    Long idConvocatoriaHito = 1L;

    final ResponseEntity<ConvocatoriaHitoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ConvocatoriaHitoOutput.class, idConvocatoriaHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaHitoOutput convocatoriaHito = response.getBody();
    Assertions.assertThat(convocatoriaHito.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaHito.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaHito.getComentario()).as("comentario")
        .isEqualTo(convocatoriaHito.getComentario());
    Assertions.assertThat(convocatoriaHito.getFecha()).as("getFecha()").isEqualTo("2021-10-22T00:00:00Z");

  }

  private ConvocatoriaHitoInput generarMockConvocatoriaHitoInput(Long id) {
    // @formatter:off
    return ConvocatoriaHitoInput.builder()
        .convocatoriaId(id == null ? 1L : id)
        .fecha(Instant.parse("2020-10-19T00:00:00Z"))
        .comentario("comentario" + (id == null ?1L : id))
        .tipoHitoId(1L)
        .aviso(null)
        .build();
    // @formatter:on
  }
}
