package org.crue.hercules.sgi.pii.integration;

import java.math.BigDecimal;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.InvencionGastoInput;
import org.crue.hercules.sgi.pii.dto.InvencionGastoOutput;
import org.crue.hercules.sgi.pii.model.InvencionGasto;
import org.crue.hercules.sgi.pii.model.InvencionGasto.Estado;
import org.crue.hercules.sgi.pii.repository.InvencionGastoRepository;
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
class InvencionGastoControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/invenciongastos";
  private static final String PATH_PARAMETER_ID = "/{id}";

  @Autowired
  private InvencionGastoRepository invencionGastoRepository;

  private HttpEntity<InvencionGastoInput> buildRequest(HttpHeaders headers,
      InvencionGastoInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<InvencionGastoInput> request = new HttpEntity<>(entity, headers);
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
    "classpath:scripts/invencion_gasto.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsIngresoGastoOutput() throws Exception {
    String[] roles = { "PII-INV-V", "PII-INV-E" };
    Long toFindId = 3L;

    InvencionGasto expected = this.invencionGastoRepository.findById(toFindId).get();

    final ResponseEntity<InvencionGastoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null, roles),
        InvencionGastoOutput.class,
        toFindId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    InvencionGastoOutput founded = response.getBody();
    Assertions.assertThat(founded).isNotNull();
    Assertions.assertThat(founded.getId()).as("getId()").isEqualTo(toFindId);
    Assertions.assertThat(founded.getGastoRef()).as("getGastoRef()").isEqualTo(expected.getGastoRef());
    Assertions.assertThat(founded.getImportePendienteDeducir()).as("getImportePendienteDeducir()")
        .isEqualTo(expected.getImportePendienteDeducir());
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
    "classpath:scripts/invencion_gasto.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsIngresoGastoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    long initialCount = this.invencionGastoRepository.count();

    InvencionGastoInput toCreate = this.buildInvencionGastoInput();

    final ResponseEntity<InvencionGastoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, toCreate, roles), InvencionGastoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    InvencionGastoOutput created = response.getBody();
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(created.getGastoRef()).as("getGastoRef()").isEqualTo(toCreate.getGastoRef());
    Assertions.assertThat(created.getImportePendienteDeducir()).as("getImportePendienteDeducir()")
        .isEqualTo(toCreate.getImportePendienteDeducir());
    Assertions.assertThat(created.getInvencionId()).as("getInvencionId()").isEqualTo(toCreate.getInvencionId());

    long afterCreateCount = this.invencionGastoRepository.count();
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
    "classpath:scripts/invencion_gasto.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsIngresoGastoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long toUpdateId = 1L;

    InvencionGastoInput toUpdate = this.buildInvencionGastoInput();

    final ResponseEntity<InvencionGastoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, toUpdate, roles), InvencionGastoOutput.class, toUpdateId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    InvencionGastoOutput updated = response.getBody();
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(toUpdateId);
    Assertions.assertThat(updated.getGastoRef()).as("getGastoRef()").isEqualTo(toUpdate.getGastoRef());
    Assertions.assertThat(updated.getImportePendienteDeducir()).as("getImportePendienteDeducir()")
        .isEqualTo(toUpdate.getImportePendienteDeducir());
    Assertions.assertThat(updated.getInvencionId()).as("getInvencionId()").isEqualTo(toUpdate.getInvencionId());

  }

  private InvencionGastoInput buildInvencionGastoInput() {
    return InvencionGastoInput.builder()
        .estado(Estado.DEDUCIDO)
        .gastoRef("TEST-GASTO-REF")
        .importePendienteDeducir(new BigDecimal(1111))
        .invencionId(1L)
        .build();
  }
}