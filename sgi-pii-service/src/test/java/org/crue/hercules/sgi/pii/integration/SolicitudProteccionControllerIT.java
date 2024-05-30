package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.PaisValidadoOutput;
import org.crue.hercules.sgi.pii.dto.ProcedimientoOutput;
import org.crue.hercules.sgi.pii.dto.SolicitudProteccionInput;
import org.crue.hercules.sgi.pii.dto.SolicitudProteccionOutput;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.junit.jupiter.api.Test;
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
class SolicitudProteccionControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/solicitudesproteccion";
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PAISESVALIDADOS = "/{solicitudProteccionId}/paisesvalidados";
  private static final String PATH_PROCEDIMIENTOS = "/{solicitudProteccionId}/procedimientos";

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
    "classpath:scripts/solicitud_proteccion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsValidSolicitudProteccionOutput() throws Exception {
    String roles[] = { "PII-INV-V" };
    Long solicitudProteccionId = 2L;
    Long invencionId = 1L;
    Long viaProteccionId = 3L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProteccionId).toUri();

    ResponseEntity<SolicitudProteccionOutput> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), SolicitudProteccionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProteccionOutput founded = response.getBody();

    Assertions.assertThat(founded).isNotNull();
    Assertions.assertThat(founded.getId()).as("getId()").isEqualTo(solicitudProteccionId);
    Assertions.assertThat(founded.getFechaCaducidad()).as("getFechaCaducidad()")
        .isNull();
    Assertions.assertThat(founded.getFechaConcesion()).as("getFechaConcesion()")
        .isNull();
    Assertions.assertThat(founded.getFechaFinPriorPresFasNacRec()).as("getFechaFinPriorPresFasNacRec()")
        .isNull();
    Assertions.assertThat(founded.getFechaPrioridadSolicitud()).as("getFechaPrioridadSolicitud()")
        .isEqualTo(Instant.parse("2021-07-22T00:00:00.000z"));
    Assertions.assertThat(founded.getNumeroConcesion()).as("getNumeroConcesion()")
        .isNull();
    Assertions.assertThat(founded.getNumeroPublicacion()).as("getNumeroPublicacion()")
        .isNull();
    Assertions.assertThat(founded.getNumeroRegistro()).as("getNumeroRegistro()")
        .isNull();
    Assertions.assertThat(founded.getNumeroSolicitud()).as("getNumeroSolicitud()")
        .isEqualTo("EP123456");
    Assertions.assertThat(founded.getPaisProteccionRef()).as("getPaisProteccionRef()")
        .isNull();
    Assertions.assertThat(founded.getTitulo()).as("getTitulo()")
        .isEqualTo("Solicitud Proteccion Test 2");
    Assertions.assertThat(founded.getInvencion()).as("getInvencion()")
        .isNotNull();
    Assertions.assertThat(founded.getInvencion().getId()).as("getInvencion().getId()")
        .isEqualTo(invencionId);
    Assertions.assertThat(founded.getTipoCaducidad()).as("getTipoCaducidad()").isNull();
    Assertions.assertThat(founded.getViaProteccion()).as("getViaProteccion()").isNotNull();
    Assertions.assertThat(founded.getViaProteccion().getId()).as("getViaProteccion().getId()")
        .isEqualTo(viaProteccionId);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsStatusCodeNotFound() throws Exception {
    String roles[] = { "PII-INV-V" };
    Long solicitudProteccionId = 12L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProteccionId).toUri();

    ResponseEntity<SolicitudProteccionOutput> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), SolicitudProteccionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsNewSolicitudProteccionOutput() throws Exception {
    String roles[] = { "PII-INV-E" };
    SolicitudProteccionInput input = this.buildMockSolicitudProteccionInput();

    ResponseEntity<SolicitudProteccionOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST,
        buildRequest(null, input, roles), SolicitudProteccionOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudProteccionOutput output = response.getBody();
    Assertions.assertThat(output).isNotNull();
    Assertions.assertThat(output.getId()).isNotNull();
    Assertions.assertThat(output.getFechaCaducidad()).isEqualTo(input.getFechaCaducidad());
    Assertions.assertThat(output.getFechaConcesion()).isEqualTo(input.getFechaConcesion());
    Assertions.assertThat(output.getFechaFinPriorPresFasNacRec()).isEqualTo(input.getFechaFinPriorPresFasNacRec());
    Assertions.assertThat(output.getFechaPrioridadSolicitud()).isEqualTo(input.getFechaPrioridadSolicitud());
    Assertions.assertThat(output.getFechaPublicacion()).isEqualTo(input.getFechaPublicacion());
    Assertions.assertThat(output.getAgentePropiedadRef()).isEqualTo(input.getAgentePropiedadRef());
    Assertions.assertThat(output.getComentarios()).isEqualTo(input.getComentarios());
    Assertions.assertThat(output.getNumeroConcesion()).isEqualTo(input.getNumeroConcesion());
    Assertions.assertThat(output.getNumeroPublicacion()).isEqualTo(input.getNumeroPublicacion());
    Assertions.assertThat(output.getNumeroRegistro()).isEqualTo(input.getNumeroRegistro());
    Assertions.assertThat(output.getNumeroSolicitud()).isEqualTo(input.getNumeroSolicitud());
    Assertions.assertThat(output.getPaisProteccionRef()).isEqualTo(input.getPaisProteccionRef());
    Assertions.assertThat(output.getTitulo()).isEqualTo(input.getTitulo());
    Assertions.assertThat(output.getEstado()).isEqualTo(input.getEstado());
    Assertions.assertThat(output.getInvencion()).isNotNull();
    Assertions.assertThat(output.getInvencion().getId()).isEqualTo(input.getInvencionId());
    Assertions.assertThat(output.getTipoCaducidad().getId()).isNotNull();
    Assertions.assertThat(output.getTipoCaducidad().getId()).isEqualTo(input.getTipoCaducidadId());
    Assertions.assertThat(output.getViaProteccion()).isNotNull();
    Assertions.assertThat(output.getViaProteccion().getId()).isEqualTo(input.getViaProteccionId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsUpdatedSolicitudProteccionOutput() throws Exception {
    String roles[] = { "PII-INV-E" };
    SolicitudProteccionInput input = this.buildMockSolicitudProteccionInput();
    Long solicitudProteccionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProteccionId).toUri();

    ResponseEntity<SolicitudProteccionOutput> response = this.restTemplate.exchange(uri, HttpMethod.PUT,
        this.buildRequest(null, input, roles), SolicitudProteccionOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProteccionOutput output = response.getBody();
    Assertions.assertThat(output).isNotNull();
    Assertions.assertThat(output.getId()).isEqualTo(solicitudProteccionId);
    Assertions.assertThat(output.getFechaCaducidad()).isEqualTo(input.getFechaCaducidad());
    Assertions.assertThat(output.getFechaConcesion()).isEqualTo(input.getFechaConcesion());
    Assertions.assertThat(output.getFechaFinPriorPresFasNacRec()).isEqualTo(input.getFechaFinPriorPresFasNacRec());
    Assertions.assertThat(output.getFechaPrioridadSolicitud()).isEqualTo(input.getFechaPrioridadSolicitud());
    Assertions.assertThat(output.getFechaPublicacion()).isEqualTo(input.getFechaPublicacion());
    Assertions.assertThat(output.getAgentePropiedadRef()).isEqualTo(input.getAgentePropiedadRef());
    Assertions.assertThat(output.getComentarios()).isEqualTo(input.getComentarios());
    Assertions.assertThat(output.getNumeroConcesion()).isEqualTo(input.getNumeroConcesion());
    Assertions.assertThat(output.getNumeroPublicacion()).isEqualTo(input.getNumeroPublicacion());
    Assertions.assertThat(output.getNumeroRegistro()).isEqualTo(input.getNumeroRegistro());
    Assertions.assertThat(output.getNumeroSolicitud()).isEqualTo(input.getNumeroSolicitud());
    Assertions.assertThat(output.getPaisProteccionRef()).isEqualTo(input.getPaisProteccionRef());
    Assertions.assertThat(output.getTitulo()).isEqualTo(input.getTitulo());
    Assertions.assertThat(output.getEstado()).isEqualTo(input.getEstado());
    Assertions.assertThat(output.getInvencion()).isNotNull();
    Assertions.assertThat(output.getInvencion().getId()).isEqualTo(1L);
    Assertions.assertThat(output.getTipoCaducidad()).isNull();
    Assertions.assertThat(output.getViaProteccion()).isNotNull();
    Assertions.assertThat(output.getViaProteccion().getId()).isEqualTo(input.getViaProteccionId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteSolicitudProteccionOutput() throws Exception {
    String roles[] = { "PII-INV-E" };
    Long solicitudProteccionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProteccionId).toUri();

    ResponseEntity<SolicitudProteccionOutput> response = this.restTemplate.exchange(uri, HttpMethod.DELETE,
        this.buildRequest(null, null, roles), SolicitudProteccionOutput.class);

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
  void findPaisesValidados_WithPagingAndSorting_ReturnPaisValidadoOutputSubList() throws Exception {
    String roles[] = { "PII-INV-E" };
    Long solicitudProteccionId = 1L;
    String filter = "solicitudProteccionId=1";
    String sort = "id,desc";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PAISESVALIDADOS)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(solicitudProteccionId).toUri();

    ResponseEntity<List<PaisValidadoOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<PaisValidadoOutput>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("5");

    List<PaisValidadoOutput> paises = response.getBody();
    Assertions.assertThat(paises).hasSize(5);
    Assertions.assertThat(paises.get(0)).isNotNull();
    Assertions.assertThat(paises.get(1)).isNotNull();
    Assertions.assertThat(paises.get(2)).isNotNull();
    Assertions.assertThat(paises.get(3)).isNotNull();
    Assertions.assertThat(paises.get(4)).isNotNull();

    Assertions.assertThat(paises.get(0).getId()).isEqualTo(5);
    Assertions.assertThat(paises.get(1).getId()).isEqualTo(4);
    Assertions.assertThat(paises.get(2).getId()).isEqualTo(3);
    Assertions.assertThat(paises.get(3).getId()).isEqualTo(2);
    Assertions.assertThat(paises.get(4).getId()).isEqualTo(1);

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
  void findProcedimientosBySolicitudProteccion_WithPagingAndSorting_ReturnsProcedimientoOutputSubLists()
      throws Exception {
    String roles[] = { "PII-INV-E" };
    Long solicitudProteccionId = 1L;
    String filter = "solicitudProteccionId=1";
    String sort = "id,desc";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PROCEDIMIENTOS)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(solicitudProteccionId).toUri();

    ResponseEntity<List<ProcedimientoOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProcedimientoOutput>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("5");

    List<ProcedimientoOutput> paises = response.getBody();
    Assertions.assertThat(paises).hasSize(5);
    Assertions.assertThat(paises.get(0)).isNotNull();
    Assertions.assertThat(paises.get(1)).isNotNull();
    Assertions.assertThat(paises.get(2)).isNotNull();
    Assertions.assertThat(paises.get(3)).isNotNull();
    Assertions.assertThat(paises.get(4)).isNotNull();

    Assertions.assertThat(paises.get(0).getId()).isEqualTo(5);
    Assertions.assertThat(paises.get(1).getId()).isEqualTo(4);
    Assertions.assertThat(paises.get(2).getId()).isEqualTo(3);
    Assertions.assertThat(paises.get(3).getId()).isEqualTo(2);
    Assertions.assertThat(paises.get(4).getId()).isEqualTo(1);
  }

  private SolicitudProteccionInput buildMockSolicitudProteccionInput() {
    return SolicitudProteccionInput.builder()
        .agentePropiedadRef("agentePropiedadRef")
        .comentarios("comentarios")
        .estado(SolicitudProteccion.EstadoSolicitudProteccion.SOLICITADA)
        .fechaPrioridadSolicitud(Instant.now())
        .invencionId(3L)
        .numeroSolicitud("1")
        .titulo("Testing Solicitud")
        .tipoCaducidadId(1L)
        .viaProteccionId(4L)
        .build();
  }
}