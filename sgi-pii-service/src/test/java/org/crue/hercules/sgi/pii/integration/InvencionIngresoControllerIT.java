package org.crue.hercules.sgi.pii.integration;

import java.math.BigDecimal;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.InvencionIngresoInput;
import org.crue.hercules.sgi.pii.dto.InvencionIngresoOutput;
import org.crue.hercules.sgi.pii.model.InvencionIngreso;
import org.crue.hercules.sgi.pii.model.InvencionIngreso.Estado;
import org.crue.hercules.sgi.pii.repository.InvencionIngresoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InvencionIngresoControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/invencioningresos";
  private static final String PATH_PARAMETER_ID = "/{id}";

  @Autowired
  private InvencionIngresoRepository invencionIngresoRepository;

  private HttpEntity<InvencionIngresoInput> buildRequest(HttpHeaders headers,
      InvencionIngresoInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<InvencionIngresoInput> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_inventor.sql",
    "classpath:scripts/periodo_titularidad.sql",
    "classpath:scripts/periodo_titularidad_titular.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_ingreso.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsIngresoGastoOutput() throws Exception {
    String[] roles = { "PII-INV-V", "PII-INV-E" };
    Long toFindId = 3L;

    InvencionIngreso expected = this.invencionIngresoRepository.findById(toFindId).get();

    final ResponseEntity<InvencionIngresoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null, roles),
        InvencionIngresoOutput.class,
        toFindId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    InvencionIngresoOutput founded = response.getBody();
    Assertions.assertThat(founded).isNotNull();
    Assertions.assertThat(founded.getId()).as("getId()").isEqualTo(toFindId);
    Assertions.assertThat(founded.getIngresoRef()).as("getIngresoRef()").isEqualTo(expected.getIngresoRef());
    Assertions.assertThat(founded.getImportePendienteRepartir()).as("getImportePendienteRepartir()")
        .isEqualTo(expected.getImportePendienteRepartir());
    Assertions.assertThat(founded.getInvencionId()).as("getInvencionId()").isEqualTo(expected.getInvencionId());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_inventor.sql",
    "classpath:scripts/periodo_titularidad.sql",
    "classpath:scripts/periodo_titularidad_titular.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_ingreso.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsIngresoGastoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    long initialCount = this.invencionIngresoRepository.count();

    InvencionIngresoInput toCreate = this.buildInvencionIngresoInput();

    final ResponseEntity<InvencionIngresoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, toCreate, roles), InvencionIngresoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    InvencionIngresoOutput created = response.getBody();
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(created.getIngresoRef()).as("getIngresoRef()").isEqualTo(toCreate.getIngresoRef());
    Assertions.assertThat(created.getImportePendienteRepartir()).as("getImportePendienteRepartir()")
        .isEqualTo(toCreate.getImportePendienteRepartir());
    Assertions.assertThat(created.getInvencionId()).as("getInvencionId()").isEqualTo(toCreate.getInvencionId());

    long afterCreateCount = this.invencionIngresoRepository.count();
    Assertions.assertThat(afterCreateCount).isEqualTo(initialCount + 1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_inventor.sql",
    "classpath:scripts/periodo_titularidad.sql",
    "classpath:scripts/periodo_titularidad_titular.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_ingreso.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsIngresoGastoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long toUpdateId = 1L;

    InvencionIngresoInput toUpdate = this.buildInvencionIngresoInput();

    final ResponseEntity<InvencionIngresoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, toUpdate, roles), InvencionIngresoOutput.class, toUpdateId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    InvencionIngresoOutput updated = response.getBody();
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(toUpdateId);
    Assertions.assertThat(updated.getIngresoRef()).as("getIngresoRef()").isEqualTo(toUpdate.getIngresoRef());
    Assertions.assertThat(updated.getImportePendienteRepartir()).as("getImportePendienteRepartir()")
        .isEqualTo(toUpdate.getImportePendienteRepartir());
    Assertions.assertThat(updated.getInvencionId()).as("getInvencionId()").isEqualTo(toUpdate.getInvencionId());

  }

  private InvencionIngresoInput buildInvencionIngresoInput() {
    return InvencionIngresoInput.builder()
        .estado(Estado.NO_REPARTIDO)
        .ingresoRef("TEST-INGRESO-REF")
        .importePendienteRepartir(new BigDecimal(1111))
        .invencionId(1L)
        .build();
  }
}