package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.ProcedimientoDocumentoOutput;
import org.crue.hercules.sgi.pii.dto.ProcedimientoInput;
import org.crue.hercules.sgi.pii.dto.ProcedimientoOutput;
import org.crue.hercules.sgi.pii.model.Procedimiento;
import org.crue.hercules.sgi.pii.repository.ProcedimientoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProcedimientoControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/procedimientos";
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PROCEDIMIENTO_DOCUMENTOS = "/{procedimientoId}/documentos";

  @Autowired
  private ProcedimientoRepository procedimientoRepository;

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
    "classpath:scripts/procedimiento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProcedimientoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    ProcedimientoInput input = this.buildMockProcedimientoInput();
    Long nextId = 8L;

    ResponseEntity<ProcedimientoOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles), ProcedimientoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProcedimientoOutput output = response.getBody();
    Assertions.assertThat(output).isNotNull();
    Assertions.assertThat(output.getId()).isEqualTo(nextId);
    Assertions.assertThat(output.getFecha()).isEqualTo(input.getFecha());
    Assertions.assertThat(output.getFechaLimiteAccion()).isEqualTo(input.getFechaLimiteAccion());
    Assertions.assertThat(output.getGenerarAviso()).isTrue();
    Assertions.assertThat(output.getSolicitudProteccionId()).isEqualTo(input.getSolicitudProteccionId());
    Assertions.assertThat(output.getAccionATomar()).isEqualTo(input.getAccionATomar());
    Assertions.assertThat(output.getTipoProcedimiento()).isNotNull();
    Assertions.assertThat(output.getTipoProcedimiento().getId()).isEqualTo(input.getTipoProcedimientoId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/tipo_procedimiento.sql",
    "classpath:scripts/procedimiento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProcedimientoOutput() throws Exception {
    String[] roles = { "PII-INV-V" };
    Long procedimientoId = 1L;

    Procedimiento expected = this.procedimientoRepository.findById(procedimientoId).get();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(procedimientoId)
        .toUri();

    ResponseEntity<ProcedimientoOutput> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), ProcedimientoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    ProcedimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(procedimientoId);
    Assertions.assertThat(output.getFecha()).isEqualTo(expected.getFecha());
    Assertions.assertThat(output.getFechaLimiteAccion()).isEqualTo(expected.getFechaLimiteAccion());
    Assertions.assertThat(output.getGenerarAviso()).isTrue();
    Assertions.assertThat(output.getSolicitudProteccionId()).isEqualTo(expected.getSolicitudProteccionId());
    Assertions.assertThat(output.getAccionATomar()).isEqualTo(expected.getAccionATomar());
    Assertions.assertThat(output.getTipoProcedimiento().getId()).isEqualTo(expected.getTipoProcedimiento().getId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/tipo_procedimiento.sql",
    "classpath:scripts/procedimiento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsUpdatedProcedimientoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    ProcedimientoInput input = this.buildMockProcedimientoInput();
    Long toUpdateId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(toUpdateId)
        .toUri();

    ResponseEntity<ProcedimientoOutput> response = this.restTemplate.exchange(uri, HttpMethod.PUT,
        buildRequest(null, input, roles), ProcedimientoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProcedimientoOutput output = response.getBody();
    Assertions.assertThat(output).isNotNull();
    Assertions.assertThat(output.getId()).isEqualTo(toUpdateId);
    Assertions.assertThat(output.getFecha()).isEqualTo(input.getFecha());
    Assertions.assertThat(output.getFechaLimiteAccion()).isEqualTo(input.getFechaLimiteAccion());
    Assertions.assertThat(output.getGenerarAviso()).isTrue();
    Assertions.assertThat(output.getSolicitudProteccionId()).isEqualTo(input.getSolicitudProteccionId());
    Assertions.assertThat(output.getAccionATomar()).isEqualTo(input.getAccionATomar());
    Assertions.assertThat(output.getTipoProcedimiento()).isNotNull();
    Assertions.assertThat(output.getTipoProcedimiento().getId()).isEqualTo(input.getTipoProcedimientoId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/tipo_procedimiento.sql",
    "classpath:scripts/procedimiento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnStsatusCodeNoContent() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long toDeleteId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(toDeleteId)
        .toUri();

    ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.DELETE, buildRequest(null, null, roles),
        Void.class);

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
    "classpath:scripts/tipo_procedimiento.sql",
    "classpath:scripts/procedimiento.sql",
    "classpath:scripts/procedimiento_documento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findProcedimientoDocumentosByProcedimiento_WithPagingAndSorting_ReturnsProcedimientoDocumentoOutputSubList()
      throws Exception {
    String[] roles = { "PII-INV-E" };
    Long procedimientoId = 1L;

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PROCEDIMIENTO_DOCUMENTOS)
        .queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(procedimientoId).toUri();

    ResponseEntity<List<ProcedimientoDocumentoOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ProcedimientoDocumentoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");

    List<ProcedimientoDocumentoOutput> documentos = response.getBody();
    Assertions.assertThat(documentos).hasSize(5);
    Assertions.assertThat(documentos.get(0)).isNotNull();
    Assertions.assertThat(documentos.get(1)).isNotNull();
    Assertions.assertThat(documentos.get(2)).isNotNull();
    Assertions.assertThat(documentos.get(3)).isNotNull();
    Assertions.assertThat(documentos.get(4)).isNotNull();

  }

  private ProcedimientoInput buildMockProcedimientoInput() {

    return ProcedimientoInput.builder()
        .accionATomar("Acción")
        .comentarios("testing procedimiento")
        .fecha(Instant.now())
        .fechaLimiteAccion(Instant.now().plus(3, ChronoUnit.DAYS))
        .solicitudProteccionId(1L)
        .tipoProcedimientoId(1L)
        .generarAviso(Boolean.TRUE)
        .build();
  }

}