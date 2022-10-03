package org.crue.hercules.sgi.pii.integration;

import java.math.BigDecimal;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.RepartoEquipoInventorInput;
import org.crue.hercules.sgi.pii.dto.RepartoEquipoInventorOutput;
import org.crue.hercules.sgi.pii.model.RepartoEquipoInventor;
import org.crue.hercules.sgi.pii.repository.RepartoEquipoInventorRepository;
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
class RepartoEquipoInventorControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/repartoequiposinventor";
  private static final String PATH_PARAMETER_ID = "/{id}";

  @Autowired
  private RepartoEquipoInventorRepository repartoEquipoInventorRepository;

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
    "classpath:scripts/invencion_gasto.sql",
    "classpath:scripts/invencion_ingreso.sql",
    "classpath:scripts/reparto.sql",
    "classpath:scripts/reparto_ingreso.sql",
    "classpath:scripts/invencion_inventor.sql",
    "classpath:scripts/reparto_equipo_inventor.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsRepartoEquipoInventorOutput() throws Exception {
    String[] roles = { "PII-INV-V" };
    Long toSearchId = 1L;

    RepartoEquipoInventor expected = this.repartoEquipoInventorRepository.findById(toSearchId).get();

    ResponseEntity<RepartoEquipoInventorOutput> response = this.restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, this.buildRequest(null, null, roles), RepartoEquipoInventorOutput.class, toSearchId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    RepartoEquipoInventorOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(toSearchId);
    Assertions.assertThat(output.getImporteNomina()).isEqualTo(expected.getImporteNomina());
    Assertions.assertThat(output.getImporteOtros()).isEqualTo(expected.getImporteOtros());
    Assertions.assertThat(output.getImporteProyecto()).isEqualTo(expected.getImporteProyecto());
    Assertions.assertThat(output.getInvencionInventorId()).isEqualTo(expected.getInvencionInventorId());
    Assertions.assertThat(output.getRepartoId()).isEqualTo(expected.getRepartoId());
    Assertions.assertThat(output.getProyectoRef()).isEqualTo(expected.getProyectoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_gasto.sql",
    "classpath:scripts/invencion_ingreso.sql",
    "classpath:scripts/reparto.sql",
    "classpath:scripts/reparto_ingreso.sql",
    "classpath:scripts/invencion_inventor.sql",
    "classpath:scripts/reparto_equipo_inventor.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsRepartoEquipoInventorOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    RepartoEquipoInventorInput input = this.buildMockRepartoEquipoInventorInput();

    Long countBeforeCreate = this.repartoEquipoInventorRepository.count();
    Long nextId = countBeforeCreate + 1;

    ResponseEntity<RepartoEquipoInventorOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, this.buildRequest(null, input, roles), RepartoEquipoInventorOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isNotNull();

    RepartoEquipoInventorOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(nextId);
    Assertions.assertThat(output.getImporteNomina()).isEqualTo(input.getImporteNomina());
    Assertions.assertThat(output.getImporteOtros()).isEqualTo(input.getImporteOtros());
    Assertions.assertThat(output.getImporteProyecto()).isEqualTo(input.getImporteProyecto());
    Assertions.assertThat(output.getInvencionInventorId()).isEqualTo(input.getInvencionInventorId());
    Assertions.assertThat(output.getRepartoId()).isEqualTo(input.getRepartoId());
    Assertions.assertThat(output.getProyectoRef()).isEqualTo(input.getProyectoRef());
    Assertions.assertThat(countBeforeCreate).isEqualTo(this.repartoEquipoInventorRepository.count() - 1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_gasto.sql",
    "classpath:scripts/invencion_ingreso.sql",
    "classpath:scripts/reparto.sql",
    "classpath:scripts/reparto_ingreso.sql",
    "classpath:scripts/invencion_inventor.sql",
    "classpath:scripts/reparto_equipo_inventor.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsRepartoEquipoInventorOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long toUpdateId = 1L;
    RepartoEquipoInventorInput input = this.buildMockRepartoEquipoInventorInput();

    ResponseEntity<RepartoEquipoInventorOutput> response = this.restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, this.buildRequest(null, input, roles), RepartoEquipoInventorOutput.class, toUpdateId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    RepartoEquipoInventorOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(toUpdateId);
    Assertions.assertThat(output.getImporteNomina()).isEqualTo(input.getImporteNomina());
    Assertions.assertThat(output.getImporteOtros()).isEqualTo(input.getImporteOtros());
    Assertions.assertThat(output.getImporteProyecto()).isEqualTo(input.getImporteProyecto());
    Assertions.assertThat(output.getInvencionInventorId()).isEqualTo(input.getInvencionInventorId());
    Assertions.assertThat(output.getRepartoId()).isEqualTo(input.getRepartoId());
    Assertions.assertThat(output.getProyectoRef()).isEqualTo(input.getProyectoRef());
  }

  private RepartoEquipoInventorInput buildMockRepartoEquipoInventorInput() {
    return RepartoEquipoInventorInput.builder()
        .importeNomina(new BigDecimal(300L))
        .importeOtros(new BigDecimal(100L))
        .importeProyecto(new BigDecimal(500L))
        .invencionInventorId(1L)
        .proyectoRef("PRO-REF-TEST")
        .repartoId(1L)
        .build();
  }
}