package org.crue.hercules.sgi.pii.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput.InvencionGastoCreateInput;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput.InvencionIngresoCreateInput;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput.RepartoGastoCreateInput;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput.RepartoIngresoCreateInput;
import org.crue.hercules.sgi.pii.dto.RepartoEquipoInventorOutput;
import org.crue.hercules.sgi.pii.dto.RepartoGastoOutput;
import org.crue.hercules.sgi.pii.dto.RepartoIngresoOutput;
import org.crue.hercules.sgi.pii.dto.RepartoInput;
import org.crue.hercules.sgi.pii.dto.RepartoOutput;
import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.model.Reparto.Estado;
import org.crue.hercules.sgi.pii.repository.RepartoRepository;
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
class RepartoControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/repartos";
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_GASTOS = "/{repartoId}/gastos";
  private static final String PATH_PARAMETER_INGRESOS = "/{repartoId}/ingresos";
  private static final String PATH_PARAMETER_EQUIPO_INVENTOR = "/{repartoId}/equiposinventor";
  private static final String PATH_PARAMETER_EJECUTAR = "/{id}/ejecutar";

  @Autowired
  private RepartoRepository repartoRepository;

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
    "classpath:scripts/reparto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsRepartoOutput() throws Exception {
    String[] roles = { "PII-INV-V" };
    Long repartoId = 1L;

    Reparto expected = this.repartoRepository.findById(repartoId).get();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(repartoId)
        .toUri();

    ResponseEntity<RepartoOutput> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), RepartoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    RepartoOutput output = response.getBody();
    Assertions.assertThat(output).isNotNull();
    Assertions.assertThat(output.getId()).isEqualTo(repartoId);
    Assertions.assertThat(output.getFecha()).isEqualTo(expected.getFecha());
    Assertions.assertThat(output.getImporteEquipoInventor()).isEqualTo(expected.getImporteEquipoInventor());
    Assertions.assertThat(output.getImporteUniversidad()).isEqualTo(expected.getImporteUniversidad());
    Assertions.assertThat(output.getInvencionId()).isEqualTo(expected.getInvencionId());
    Assertions.assertThat(output.getEstado()).isEqualTo(expected.getEstado());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_gasto.sql",
    "classpath:scripts/invencion_ingreso.sql",
    "classpath:scripts/reparto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnRepartoOutput() throws Exception {
    String roles[] = { "PII-INV-E" };
    RepartoCreateInput input = this.buildMockRepartoCreateInput();
    Long beforeCount = this.repartoRepository.count();

    ResponseEntity<RepartoOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles), RepartoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isNotNull();

    RepartoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(beforeCount + 1);
    Assertions.assertThat(output.getInvencionId()).isEqualTo(input.getInvencionId());

    Long afterCount = this.repartoRepository.count();
    Assertions.assertThat(afterCount).isEqualTo(beforeCount + 1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_gasto.sql",
    "classpath:scripts/invencion_ingreso.sql",
    "classpath:scripts/reparto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnRepartoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long repartoId = 1L;
    RepartoInput input = this.buildMockRepartoInput();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(repartoId)
        .toUri();

    ResponseEntity<RepartoOutput> response = this.restTemplate.exchange(uri, HttpMethod.PUT,
        this.buildRequest(null, input, roles), RepartoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    RepartoOutput output = response.getBody();
    Assertions.assertThat(output).isNotNull();
    Assertions.assertThat(output.getId()).isEqualTo(repartoId);
    Assertions.assertThat(output.getFecha()).isNotEqualTo(input.getFecha());
    Assertions.assertThat(output.getImporteEquipoInventor()).isEqualTo(input.getImporteEquipoInventor());
    Assertions.assertThat(output.getImporteUniversidad()).isEqualTo(input.getImporteUniversidad());
    Assertions.assertThat(output.getInvencionId()).isEqualTo(input.getInvencionId());
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
    "classpath:scripts/reparto_gasto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findGastos_WithPagingAndSorting_ReturnsRepartoGastoOutputSubList() throws Exception {
    String[] roles = { "PII-INV-V" };
    Long repartoId = 1L;

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_GASTOS).queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(repartoId).toUri();

    ResponseEntity<List<RepartoGastoOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<RepartoGastoOutput>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");

    List<RepartoGastoOutput> gastos = response.getBody();
    Assertions.assertThat(gastos).hasSize(3);
    Assertions.assertThat(gastos.get(0)).isNotNull();
    Assertions.assertThat(gastos.get(1)).isNotNull();
    Assertions.assertThat(gastos.get(2)).isNotNull();
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
    "classpath:scripts/reparto_ingreso.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findIngresos_WithPagingAndSorting_ReturnRepartoIngresoOutputSubList() throws Exception {
    String[] roles = { "PII-INV-V" };
    Long repartoId = 1L;

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_INGRESOS).queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(repartoId).toUri();

    ResponseEntity<List<RepartoIngresoOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<RepartoIngresoOutput>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");

    List<RepartoIngresoOutput> ingresos = response.getBody();
    Assertions.assertThat(ingresos).hasSize(3);
    Assertions.assertThat(ingresos.get(0)).isNotNull();
    Assertions.assertThat(ingresos.get(1)).isNotNull();
    Assertions.assertThat(ingresos.get(2)).isNotNull();
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
  void findEquipoInventor_WithPagingAndSorting_ReturnsRepartoEquipoInventorOutputSubList() throws Exception {
    String[] roles = { "PII-INV-V" };
    Long repartoId = 1L;

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_EQUIPO_INVENTOR)
        .queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(repartoId).toUri();

    ResponseEntity<List<RepartoEquipoInventorOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<RepartoEquipoInventorOutput>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");

    List<RepartoEquipoInventorOutput> inventores = response.getBody();
    Assertions.assertThat(inventores).hasSize(3);
    Assertions.assertThat(inventores.get(0)).isNotNull();
    Assertions.assertThat(inventores.get(1)).isNotNull();
    Assertions.assertThat(inventores.get(2)).isNotNull();
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
    "classpath:scripts/reparto_gasto.sql",
    "classpath:scripts/invencion_inventor.sql",
    "classpath:scripts/reparto_equipo_inventor.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void ejecutar_ReturnRepartoOutputEjecutado() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long repartoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_EJECUTAR)
        .buildAndExpand(repartoId)
        .toUri();

    ResponseEntity<RepartoOutput> response = this.restTemplate.exchange(uri, HttpMethod.PATCH,
        this.buildRequest(null, null, roles), RepartoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
  }

  private RepartoInput buildMockRepartoInput() {
    return RepartoInput.builder()
        .estado(Estado.EJECUTADO)
        .fecha(Instant.now())
        .importeEquipoInventor(new BigDecimal(111d))
        .importeUniversidad(new BigDecimal(111d))
        .invencionId(1L)
        .build();
  }

  private RepartoCreateInput buildMockRepartoCreateInput() {
    return RepartoCreateInput.builder()
        .gastos(Arrays.asList(buildMockRepartoGastoCreateInput(new BigDecimal(11L))))
        .ingresos(Arrays.asList(buildMockRepartoIngresoCreateInput(new BigDecimal(1L))))
        .invencionId(1L)
        .build();
  }

  private RepartoGastoCreateInput buildMockRepartoGastoCreateInput(BigDecimal importeADeducir) {
    return RepartoGastoCreateInput.builder()
        .importeADeducir(importeADeducir)
        .invencionGasto(this.buildMockInvencionGastoCreateInput())
        .build();
  }

  private RepartoIngresoCreateInput buildMockRepartoIngresoCreateInput(BigDecimal importeARepartir) {
    return RepartoIngresoCreateInput.builder()
        .importeARepartir(importeARepartir)
        .invencionIngreso(this.buildMockInvencionGastoCreateOutput())
        .build();
  }

  private InvencionGastoCreateInput buildMockInvencionGastoCreateInput() {
    return InvencionGastoCreateInput.builder()
        .gastoRef("gasto-ref")
        .id(1L)
        .invencionId(1L)
        .importePendienteDeducir(new BigDecimal(111L))
        .build();
  }

  private InvencionIngresoCreateInput buildMockInvencionGastoCreateOutput() {
    return InvencionIngresoCreateInput.builder()
        .ingresoRef("gasto-ref")
        .id(1L)
        .invencionId(1L)
        .importePendienteRepartir(new BigDecimal(111L))
        .build();
  }
}