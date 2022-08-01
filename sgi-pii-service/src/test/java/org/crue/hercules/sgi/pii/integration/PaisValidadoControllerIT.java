package org.crue.hercules.sgi.pii.integration;

import java.time.Instant;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.PaisValidadoInput;
import org.crue.hercules.sgi.pii.dto.PaisValidadoOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PaisValidadoControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/paisesvalidados";
  private static final String PATH_PARAMETER_ID = "/{id}";

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/pais_validado.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsPaisValidadoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    PaisValidadoInput input = buildMockPaisValidadoInput();

    ResponseEntity<PaisValidadoOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles), PaisValidadoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isNotNull();

    PaisValidadoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isNotNull();
    Assertions.assertThat(output.getFechaValidacion()).isEqualTo(input.getFechaValidacion());
    Assertions.assertThat(output.getSolicitudProteccionId()).isEqualTo(input.getSolicitudProteccionId());
    Assertions.assertThat(output.getCodigoInvencion()).isEqualTo(input.getCodigoInvencion());
    Assertions.assertThat(output.getPaisRef()).isEqualTo(input.getPaisRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/pais_validado.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsStatusCodeNoContent() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long paisValidadoId = 1L;

    ResponseEntity<Void> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE,
        buildRequest(null, null, roles), Void.class, paisValidadoId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/pais_validado.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsUpdatedPaisValidadoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    PaisValidadoInput input = buildMockPaisValidadoInput();
    Long paisValidadoId = 1L;

    ResponseEntity<PaisValidadoOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT,
        buildRequest(null, input, roles), PaisValidadoOutput.class, paisValidadoId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    PaisValidadoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(paisValidadoId);
    Assertions.assertThat(output.getFechaValidacion()).isEqualTo(input.getFechaValidacion());
    Assertions.assertThat(output.getSolicitudProteccionId()).isEqualTo(input.getSolicitudProteccionId());
    Assertions.assertThat(output.getCodigoInvencion()).isEqualTo(input.getCodigoInvencion());
    Assertions.assertThat(output.getPaisRef()).isEqualTo(input.getPaisRef());
  }

  private PaisValidadoInput buildMockPaisValidadoInput() {
    return PaisValidadoInput.builder()
        .codigoInvencion("INV-001")
        .fechaValidacion(Instant.now())
        .paisRef("ESP")
        .solicitudProteccionId(1L)
        .build();
  }
}
