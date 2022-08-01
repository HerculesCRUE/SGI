package org.crue.hercules.sgi.pii.integration;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.InformePatentabilidadInput;
import org.crue.hercules.sgi.pii.dto.InformePatentabilidadOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import lombok.Data;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InformePatentabilidadControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/informespatentabilidad";
  private static final String PATH_PARAMETER_ID = "/{id}";

  private HttpEntity<InformePatentabilidadInput> buildRequest(HttpHeaders headers,
      InformePatentabilidadInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<InformePatentabilidadInput> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/resultado_informe_patentabilidad.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/informe_patentabilidad.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnCorrectInformePatentabilidadOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long informePatentabilidadOutputId = 1L;

    final ResponseEntity<InformePatentabilidadOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH
        + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null, roles), InformePatentabilidadOutput.class, informePatentabilidadOutputId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InformePatentabilidadOutput informePatentabilidadOutput = response.getBody();

    Assertions.assertThat(informePatentabilidadOutput.getId()).as("id").isEqualTo(informePatentabilidadOutputId);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/resultado_informe_patentabilidad.sql",
    "classpath:scripts/invencion.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ThrowsInformePatentabilidadNotFoundException() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long informePatentabilidadOutputId = 1L;

    final ResponseEntity<ResponseError> response = restTemplate.exchange(CONTROLLER_BASE_PATH
        + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null, roles), ResponseError.class, informePatentabilidadOutputId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/resultado_informe_patentabilidad.sql",
      "classpath:scripts/invencion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnCorrectInformePatentabilidadOutput() throws Exception {
    String[] roles = { "PII-INV-E" };

    Long invencionId = 1L;
    Instant fecha = Instant.now();

    InformePatentabilidadInput toCreate = this.buildMockInformePatentabilidadInput(invencionId, fecha);

    ResponseEntity<InformePatentabilidadOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, toCreate, roles), InformePatentabilidadOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final InformePatentabilidadOutput created = response.getBody();

    Assertions.assertThat(created.getId()).as("id").isNotNull();
    Assertions.assertThat(created.getComentarios()).as("getComentarios()").isEqualTo(toCreate.getComentarios());
    Assertions.assertThat(created.getContactoEntidadCreadora()).as("getContactoEntidadCreadora()")
        .isEqualTo(toCreate.getContactoEntidadCreadora());
    Assertions.assertThat(created.getContactoExaminador()).as("getContactoExaminador()")
        .isEqualTo(toCreate.getContactoExaminador());
    Assertions.assertThat(created.getDocumentoRef()).as("getDocumentoRef()").isEqualTo(toCreate.getDocumentoRef());
    Assertions.assertThat(created.getEntidadCreadoraRef()).as("getEntidadCreadoraRef()")
        .isEqualTo(toCreate.getEntidadCreadoraRef());
    Assertions.assertThat(created.getFecha()).as("getFecha()").isEqualTo(fecha);
    Assertions.assertThat(created.getInvencionId()).as("getInvencionId()").isEqualTo(invencionId);
    Assertions.assertThat(created.getNombre()).as("getNombre()").isEqualTo(toCreate.getNombre());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/resultado_informe_patentabilidad.sql",
      "classpath:scripts/invencion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnHttpStatusBAD_REQUEST_resultadoInformePatentabilidadId_shouldNotBeNull() throws Exception {
    String[] roles = { "PII-INV-E" };

    Long invencionId = 1L;
    Instant fecha = Instant.now();

    InformePatentabilidadInput toCreate = this.buildMockInformePatentabilidadInput(invencionId, fecha);
    toCreate.setResultadoInformePatentabilidadId(null);

    ResponseEntity<ResponseError> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, toCreate, roles), ResponseError.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    ResponseError.Error error = response.getBody().getErrors().get(0);
    Assertions.assertThat(error.getField()).as("getField()").isEqualTo("resultadoInformePatentabilidadId");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/resultado_informe_patentabilidad.sql",
      "classpath:scripts/invencion.sql",
      "classpath:scripts/informe_patentabilidad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnCorrectInformePatentabilidadOutput() throws Exception {
    String[] roles = { "PII-INV-E" };

    Long toUpdateId = 1L;
    Long invencionId = 1L;
    Instant fecha = Instant.now();

    InformePatentabilidadInput toUpdate = this.buildMockInformePatentabilidadInput(invencionId, fecha);

    ResponseEntity<InformePatentabilidadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, toUpdate, roles), InformePatentabilidadOutput.class, toUpdateId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final InformePatentabilidadOutput created = response.getBody();

    Assertions.assertThat(created.getId()).as("id").isEqualTo(toUpdateId);
    Assertions.assertThat(created.getComentarios()).as("getComentarios()").isEqualTo(toUpdate.getComentarios());
    Assertions.assertThat(created.getContactoEntidadCreadora()).as("getContactoEntidadCreadora()")
        .isEqualTo(toUpdate.getContactoEntidadCreadora());
    Assertions.assertThat(created.getContactoExaminador()).as("getContactoExaminador()")
        .isEqualTo(toUpdate.getContactoExaminador());
    Assertions.assertThat(created.getDocumentoRef()).as("getDocumentoRef()").isEqualTo(toUpdate.getDocumentoRef());
    Assertions.assertThat(created.getEntidadCreadoraRef()).as("getEntidadCreadoraRef()")
        .isEqualTo(toUpdate.getEntidadCreadoraRef());
    Assertions.assertThat(created.getFecha()).as("getFecha()").isEqualTo(fecha);
    Assertions.assertThat(created.getInvencionId()).as("getInvencionId()").isEqualTo(invencionId);
    Assertions.assertThat(created.getNombre()).as("getNombre()").isEqualTo(toUpdate.getNombre());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/resultado_informe_patentabilidad.sql",
      "classpath:scripts/invencion.sql",
      "classpath:scripts/informe_patentabilidad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnStatusCodeBAD_REQUEST_WhenResultadoInformePatentabilidadIdIsNull() throws Exception {
    String[] roles = { "PII-INV-E" };

    Long toUpdateId = 1L;
    Long invencionId = 1L;
    Instant fecha = Instant.now();

    InformePatentabilidadInput toUpdate = this.buildMockInformePatentabilidadInput(invencionId, fecha);
    toUpdate.setResultadoInformePatentabilidadId(null);

    ResponseEntity<ResponseError> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, toUpdate, roles), ResponseError.class, toUpdateId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    ResponseError.Error error = response.getBody().getErrors().get(0);
    Assertions.assertThat(error.getField()).as("getField()").isEqualTo("resultadoInformePatentabilidadId");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/resultado_informe_patentabilidad.sql",
      "classpath:scripts/invencion.sql",
      "classpath:scripts/informe_patentabilidad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnStatusCodeNO_CONTENT() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long toDeleteId = 1L;

    ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null, roles), Void.class, toDeleteId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  private InformePatentabilidadInput buildMockInformePatentabilidadInput(Long invencionId, Instant fecha) {
    return InformePatentabilidadInput.builder()
        .comentarios("mocked informe")
        .invencionId(invencionId)
        .contactoEntidadCreadora("mocked contacto entidad creadora")
        .contactoExaminador("mocked contacto examinador")
        .documentoRef("mocked documento ref")
        .entidadCreadoraRef("entidad creadora ref")
        .fecha(fecha)
        .nombre("mocked informe patentabilidad")
        .resultadoInformePatentabilidadId(1L)
        .build();
  }

  @Data
  public static class ResponseError {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    private List<Error> errors;

    @Data
    public static class Error {
      public String field;
      public String error;
    }
  }
}