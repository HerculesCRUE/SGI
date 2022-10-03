package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.SectorLicenciadoInput;
import org.crue.hercules.sgi.pii.dto.SectorLicenciadoOutput;
import org.crue.hercules.sgi.pii.repository.SectorLicenciadoRepository;
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
class SectorLicenciadoControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/sectoreslicenciados";
  private static final String PATH_PARAMETER_ID = "/{id}";

  @Autowired
  private SectorLicenciadoRepository sectorLicenciadoRepository;

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
    "classpath:scripts/sector_aplicacion.sql",
    "classpath:scripts/sector_licenciado.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithSortingAndPaging_ReturnsSectorLicenciadoOutputSubList() throws Exception {
    String[] roles = { "PII-INV-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "exclusividad==false";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build().toUri();

    ResponseEntity<List<SectorLicenciadoOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<SectorLicenciadoOutput>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");

    List<SectorLicenciadoOutput> gastos = response.getBody();
    Assertions.assertThat(gastos).hasSize(3);
    Assertions.assertThat(gastos.get(0)).isNotNull();
    Assertions.assertThat(gastos.get(1)).isNotNull();
    Assertions.assertThat(gastos.get(2)).isNotNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/sector_aplicacion.sql",
    "classpath:scripts/sector_licenciado.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findByContratoRef_ReturnsSectorLicenciadoOutputSubList() throws Exception {
    String[] roles = { "PII-INV-V" };
    String contratoRef = "CONT_01";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("contratoRef", contratoRef).build()
        .toUri();

    ResponseEntity<List<SectorLicenciadoOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<SectorLicenciadoOutput>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody()).hasSize(5);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/sector_aplicacion.sql",
    "classpath:scripts/sector_licenciado.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsSectorLicenciadoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long sectorLicenciadoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(sectorLicenciadoId).toUri();

    ResponseEntity<SectorLicenciadoOutput> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(null, null, roles), SectorLicenciadoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    SectorLicenciadoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(sectorLicenciadoId);
    Assertions.assertThat(output.getExclusividad()).isFalse();
    Assertions.assertThat(output.getFechaFinLicencia()).isEqualTo(Instant.parse("2022-11-06T11:06:00.00Z"));
    Assertions.assertThat(output.getFechaInicioLicencia()).isEqualTo(Instant.parse("2022-11-05T11:11:00.00Z"));
    Assertions.assertThat(output.getInvencionId()).isEqualTo(1L);
    Assertions.assertThat(output.getSectorAplicacion()).isNotNull();
    Assertions.assertThat(output.getSectorAplicacion().getId()).isEqualTo(1);
    Assertions.assertThat(output.getPaisRef()).isEqualTo("ES");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/sector_aplicacion.sql",
    "classpath:scripts/sector_licenciado.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsSectorLicenciadoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    SectorLicenciadoInput input = this.buildMockSectorLicenciadoInput();

    ResponseEntity<SectorLicenciadoOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        this.buildRequest(null, input, roles), SectorLicenciadoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isNotNull();

    SectorLicenciadoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(sectorLicenciadoRepository.count());
    Assertions.assertThat(output.getExclusividad()).isEqualTo(input.getExclusividad());
    Assertions.assertThat(output.getFechaFinLicencia()).isEqualTo(input.getFechaFinLicencia());
    Assertions.assertThat(output.getFechaInicioLicencia()).isEqualTo(input.getFechaInicioLicencia());
    Assertions.assertThat(output.getInvencionId()).isEqualTo(input.getInvencionId());
    Assertions.assertThat(output.getSectorAplicacion()).isNotNull();
    Assertions.assertThat(output.getSectorAplicacion().getId()).isEqualTo(input.getSectorAplicacionId());
    Assertions.assertThat(output.getPaisRef()).isEqualTo(input.getPaisRef());
    Assertions.assertThat(output.getContratoRef()).isEqualTo(input.getContratoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/sector_aplicacion.sql",
    "classpath:scripts/sector_licenciado.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsSectorLicenciadoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long sectorLicenciadoId = 1L;
    SectorLicenciadoInput input = this.buildMockSectorLicenciadoInput();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(sectorLicenciadoId).toUri();

    ResponseEntity<SectorLicenciadoOutput> response = this.restTemplate.exchange(uri, HttpMethod.PUT,
        this.buildRequest(null, input, roles), SectorLicenciadoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    SectorLicenciadoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(sectorLicenciadoId);
    Assertions.assertThat(output.getExclusividad()).isEqualTo(input.getExclusividad());
    Assertions.assertThat(output.getInvencionId()).isEqualTo(input.getInvencionId());
    Assertions.assertThat(output.getSectorAplicacion()).isNotNull();
    Assertions.assertThat(output.getSectorAplicacion().getId()).isEqualTo(input.getSectorAplicacionId());
    Assertions.assertThat(output.getPaisRef()).isEqualTo(input.getPaisRef());
    Assertions.assertThat(output.getContratoRef()).isEqualTo(input.getContratoRef());
    Assertions.assertThat(output.getFechaFinLicencia()).isEqualTo(input.getFechaFinLicencia());
    Assertions.assertThat(output.getFechaInicioLicencia()).isEqualTo(input.getFechaInicioLicencia());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/sector_aplicacion.sql",
    "classpath:scripts/sector_licenciado.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_ReturnsStatusCodeNO_CONTENT() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long sectorLicenciadoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(sectorLicenciadoId).toUri();

    ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.DELETE,
        this.buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private SectorLicenciadoInput buildMockSectorLicenciadoInput() {
    return SectorLicenciadoInput.builder()
        .contratoRef("CNTR-RF-TEST")
        .exclusividad(Boolean.TRUE)
        .fechaFinLicencia(Instant.now().plus(40, ChronoUnit.DAYS))
        .fechaInicioLicencia(Instant.now())
        .invencionId(1L)
        .paisRef("ES")
        .sectorAplicacionId(1L)
        .build();
  }
}