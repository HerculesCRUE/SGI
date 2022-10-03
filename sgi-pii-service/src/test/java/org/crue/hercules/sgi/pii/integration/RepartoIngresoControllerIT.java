package org.crue.hercules.sgi.pii.integration;

import java.math.BigDecimal;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.RepartoIngresoInput;
import org.crue.hercules.sgi.pii.dto.RepartoIngresoOutput;
import org.crue.hercules.sgi.pii.model.RepartoIngreso;
import org.crue.hercules.sgi.pii.repository.RepartoIngresoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class RepartoIngresoControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/repartoingresos";
  private static final String PATH_PARAMETER_ID = "/{id}";

  @Autowired
  private RepartoIngresoRepository repartoIngresoRepository;

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
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_ingreso.sql",
    "classpath:scripts/reparto.sql",
    "classpath:scripts/reparto_ingreso.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsRepartoIngresoOutput() throws Exception {
    String[] roles = { "PII-INV-V" };
    Long toSearchId = 1L;

    RepartoIngreso expected = this.repartoIngresoRepository.findById(toSearchId).get();

    ResponseEntity<RepartoIngresoOutput> response = this.restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, this.buildRequest(null, null, roles), RepartoIngresoOutput.class, toSearchId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    RepartoIngresoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(toSearchId);
    Assertions.assertThat(output.getImporteARepartir()).isEqualTo(expected.getImporteARepartir());
    Assertions.assertThat(output.getInvencionIngresoId()).isEqualTo(expected.getInvencionIngresoId());
    Assertions.assertThat(output.getRepartoId()).isEqualTo(expected.getRepartoId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_ingreso.sql",
    "classpath:scripts/reparto.sql",
    "classpath:scripts/reparto_ingreso.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsRepartoIngresoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    RepartoIngresoInput input = this.buildMockRepartoIngresoInput();
    Long countBeforeCreate = this.repartoIngresoRepository.count();
    Long nextId = countBeforeCreate + 1;

    ResponseEntity<RepartoIngresoOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        this.buildRequest(null, input, roles), RepartoIngresoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isNotNull();

    RepartoIngresoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(nextId);
    Assertions.assertThat(output.getImporteARepartir()).isEqualTo(input.getImporteARepartir());
    Assertions.assertThat(output.getInvencionIngresoId()).isEqualTo(input.getInvencionIngresoId());
    Assertions.assertThat(output.getRepartoId()).isEqualTo(input.getRepartoId());

    Assertions.assertThat(countBeforeCreate).isEqualTo(this.repartoIngresoRepository.count() - 1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_ingreso.sql",
    "classpath:scripts/reparto.sql",
    "classpath:scripts/reparto_ingreso.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsRepartoIngresoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long toUpdateId = 1L;
    RepartoIngresoInput input = this.buildMockRepartoIngresoInput();

    ResponseEntity<RepartoIngresoOutput> response = this.restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, this.buildRequest(null, input, roles), RepartoIngresoOutput.class, toUpdateId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    RepartoIngresoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(toUpdateId);
    Assertions.assertThat(output.getImporteARepartir()).isEqualTo(input.getImporteARepartir());
    Assertions.assertThat(output.getInvencionIngresoId()).isEqualTo(input.getInvencionIngresoId());
    Assertions.assertThat(output.getRepartoId()).isEqualTo(input.getRepartoId());
  }

  private RepartoIngresoInput buildMockRepartoIngresoInput() {
    return RepartoIngresoInput.builder()
        .importeARepartir(new BigDecimal(300))
        .invencionIngresoId(1L)
        .repartoId(1L)
        .build();
  }
}