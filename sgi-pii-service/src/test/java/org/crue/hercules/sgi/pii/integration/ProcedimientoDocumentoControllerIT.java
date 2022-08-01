package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.ProcedimientoDocumentoInput;
import org.crue.hercules.sgi.pii.dto.ProcedimientoDocumentoOutput;
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
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProcedimientoDocumentoControllerIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = "/procedimientodocumentos";
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
    "classpath:scripts/tipo_procedimiento.sql",
    "classpath:scripts/procedimiento.sql",
    "classpath:scripts/procedimiento_documento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsFoundedProcedimientoDocumentoOutput() throws Exception {
    String roles[] = { "PII-INV-E", "PII-INV-V" };
    Long procedimientoDocumentoId = 6L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(procedimientoDocumentoId).toUri();

    ResponseEntity<ProcedimientoDocumentoOutput> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), ProcedimientoDocumentoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProcedimientoDocumentoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isNotNull();
    Assertions.assertThat(output.getNombre()).isEqualTo("Documento 3 Procedimiento 2");
    Assertions.assertThat(output.getProcedimientoId()).isEqualTo(2L);
    Assertions.assertThat(output.getDocumentoRef()).isEqualTo("61f34b61-0e67-40a6-a581-2e188c1cbd78");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/tipo_procedimiento.sql",
    "classpath:scripts/procedimiento.sql",
    "classpath:scripts/procedimiento_documento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsNewProcedimientoDocumentoOutput() throws Exception {
    String roles[] = { "PII-INV-C" };
    Long procedimientoId = 2L;
    String docName = "testing-doc";
    ProcedimientoDocumentoInput input = this.buildMockProcedimientoDocumentoInput(docName, procedimientoId);

    ResponseEntity<ProcedimientoDocumentoOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, input, roles), ProcedimientoDocumentoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProcedimientoDocumentoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isNotNull();
    Assertions.assertThat(output.getNombre()).isEqualTo(docName);
    Assertions.assertThat(output.getProcedimientoId()).isEqualTo(procedimientoId);
    Assertions.assertThat(output.getDocumentoRef()).isEqualTo(input.getDocumentoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/tipo_procedimiento.sql",
    "classpath:scripts/procedimiento.sql",
    "classpath:scripts/procedimiento_documento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsUpdatedProcedimientoDocumentoOutput() throws Exception {
    String roles[] = { "PII-INV-E" };
    Long procedimientoId = 1L;
    Long procedimientoDocumentoId = 6L;
    String docName = "testing-update-doc";
    ProcedimientoDocumentoInput input = this.buildMockProcedimientoDocumentoInput(docName, procedimientoId);

    ResponseEntity<ProcedimientoDocumentoOutput> response = this.restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, input, roles), ProcedimientoDocumentoOutput.class,
        procedimientoDocumentoId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProcedimientoDocumentoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isNotNull();
    Assertions.assertThat(output.getId()).isEqualTo(procedimientoDocumentoId);
    Assertions.assertThat(output.getNombre()).isEqualTo(docName);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/tipo_procedimiento.sql",
    "classpath:scripts/procedimiento.sql",
    "classpath:scripts/procedimiento_documento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsStatusCodeNoContent() throws Exception {
    String roles[] = { "PII-INV-E" };
    Long procedimientoDocumentoId = 6L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(procedimientoDocumentoId).toUri();

    ResponseEntity<ProcedimientoDocumentoOutput> response = this.restTemplate.exchange(uri, HttpMethod.DELETE,
        buildRequest(null, null, roles), ProcedimientoDocumentoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private ProcedimientoDocumentoInput buildMockProcedimientoDocumentoInput(String nombre, Long procedimientoId) {
    return ProcedimientoDocumentoInput.builder()
        .documentoRef("documentoRef")
        .nombre(nombre)
        .procedimientoId(procedimientoId)
        .build();
  }
}