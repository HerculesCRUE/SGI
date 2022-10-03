package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.TipoProcedimientoInput;
import org.crue.hercules.sgi.pii.dto.TipoProcedimientoOutput;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento;
import org.crue.hercules.sgi.pii.repository.TipoProcedimientoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de TipoProcedimiento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TipoProcedimientoIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/tiposprocedimiento";
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_ACTIVAR = "/{id}/activar";
  private static final String PATH_PARAMETER_DESACTIVAR = "/{id}/desactivar";

  @Autowired
  private TipoProcedimientoRepository tipoProcedimientoRepository;

  private HttpEntity<TipoProcedimientoInput> buildRequest(HttpHeaders headers,
      TipoProcedimientoInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<TipoProcedimientoInput> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_procedimiento.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsTipoProcedimientoSubList() throws Exception {

    String[] roles = { "PII-TPR-V", "PII-TPR-C", "PII-TPR-E", "PII-TPR-B", "PII-TPR-R" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false)
        .toUri();

    final ResponseEntity<List<TipoProcedimientoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoProcedimientoOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoProcedimientoOutput> tipoProcedimientoOutput = response.getBody();
    Assertions.assertThat(tipoProcedimientoOutput).hasSize(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    Assertions.assertThat(tipoProcedimientoOutput.get(0).getNombre()).isEqualTo("nombre-003");
    Assertions.assertThat(tipoProcedimientoOutput.get(1).getNombre()).isEqualTo("nombre-002");
    Assertions.assertThat(tipoProcedimientoOutput.get(2).getNombre()).isEqualTo("nombre-001");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/tipo_procedimiento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsTipoProcedimientoOutput() throws Exception {

    String[] roles = { "PII-TPR-C" };

    TipoProcedimientoInput tipoProcedimientoInput = buildMockTipoProcedimientoInput();

    ResponseEntity<TipoProcedimientoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null,
            tipoProcedimientoInput, roles),
        TipoProcedimientoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    TipoProcedimientoOutput tipoProcedimientoOutput = response.getBody();

    Assertions.assertThat(tipoProcedimientoOutput.getId()).as("id").isNotNull();
    Assertions.assertThat(tipoProcedimientoOutput.getNombre()).as("nombre").isEqualTo("nombre-tipo-procedimiento");
    Assertions.assertThat(tipoProcedimientoOutput.getDescripcion()).as("descripcion")
        .isEqualTo("descripcion-tipo-procedimiento");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/tipo_procedimiento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findActivos_WithSortingAndPaging_ReturnsTipoProcedimientoOutputSubList() throws Exception {
    String[] roles = { "PII-TPR-V" };
    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false)
        .toUri();

    final ResponseEntity<List<TipoProcedimientoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoProcedimientoOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<TipoProcedimientoOutput> tiposProcedimientoOutput = response.getBody();
    Assertions.assertThat(tiposProcedimientoOutput).hasSize(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    Assertions.assertThat(tiposProcedimientoOutput.get(0).getNombre()).isEqualTo("nombre-003");
    Assertions.assertThat(tiposProcedimientoOutput.get(1).getNombre()).isEqualTo("nombre-002");
    Assertions.assertThat(tiposProcedimientoOutput.get(2).getNombre()).isEqualTo("nombre-001");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/tipo_procedimiento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsTipoProcedimientoOutput() throws Exception {
    String[] roles = { "PII-TPR-V" };
    Long toSearchId = 1L;

    TipoProcedimiento expected = this.tipoProcedimientoRepository.findById(toSearchId).get();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(toSearchId)
        .toUri();

    ResponseEntity<TipoProcedimientoOutput> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(null, null, roles), TipoProcedimientoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    TipoProcedimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(toSearchId);
    Assertions.assertThat(output.getActivo()).isEqualTo(expected.getActivo());
    Assertions.assertThat(output.getDescripcion()).isEqualTo(expected.getDescripcion());
    Assertions.assertThat(output.getNombre()).isEqualTo(expected.getNombre());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/tipo_procedimiento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void activar_ReturnsTipoProcedimientoOutput() throws Exception {
    String[] roles = { "PII-TPR-R" };
    Long toActivateId = 1L;

    this.tipoProcedimientoRepository.findById(toActivateId).ifPresent(tipo -> {
      tipo.setActivo(Boolean.FALSE);
      this.tipoProcedimientoRepository.save(tipo);
    });

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ACTIVAR)
        .buildAndExpand(toActivateId)
        .toUri();

    ResponseEntity<TipoProcedimientoOutput> response = this.restTemplate.exchange(uri, HttpMethod.PATCH,
        this.buildRequest(null, null, roles), TipoProcedimientoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    TipoProcedimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(toActivateId);
    Assertions.assertThat(output.getActivo()).isTrue();

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/tipo_procedimiento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnsTipoProcedimientoOutput() throws Exception {
    String[] roles = { "PII-TPR-B" };
    Long toActivateId = 1L;

    this.tipoProcedimientoRepository.findById(toActivateId).ifPresent(tipo -> {
      tipo.setActivo(Boolean.TRUE);
      this.tipoProcedimientoRepository.save(tipo);
    });

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_DESACTIVAR)
        .buildAndExpand(toActivateId)
        .toUri();

    ResponseEntity<TipoProcedimientoOutput> response = this.restTemplate.exchange(uri, HttpMethod.PATCH,
        this.buildRequest(null, null, roles), TipoProcedimientoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    TipoProcedimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(toActivateId);
    Assertions.assertThat(output.getActivo()).isFalse();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/tipo_procedimiento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsTipoProcedimientoOutput() throws Exception {
    String[] roles = { "PII-TPR-E" };
    Long toUpdateId = 1L;

    TipoProcedimientoInput input = this.buildMockTipoProcedimientoInput();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(toUpdateId)
        .toUri();

    ResponseEntity<TipoProcedimientoOutput> response = this.restTemplate.exchange(uri, HttpMethod.PUT,
        this.buildRequest(null, input, roles), TipoProcedimientoOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    TipoProcedimientoOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(toUpdateId);
    Assertions.assertThat(output.getDescripcion()).isEqualTo(input.getDescripcion());
    Assertions.assertThat(output.getNombre()).isEqualTo(input.getNombre());
  }

  /**
   * Función que devuelve un objeto TipoProcedimientoInput
   * 
   * @param id id del TipoProcedimiento
   * @return el objeto TipoProcedimientoInput
   */
  private TipoProcedimientoInput buildMockTipoProcedimientoInput() {
    TipoProcedimientoInput tipoProcedimientoInput = new TipoProcedimientoInput();
    tipoProcedimientoInput.setNombre("nombre-tipo-procedimiento");
    tipoProcedimientoInput.setDescripcion("descripcion-tipo-procedimiento");

    return tipoProcedimientoInput;
  }

}
