package org.crue.hercules.sgi.rel.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rel.dto.RelacionInput;
import org.crue.hercules.sgi.rel.dto.RelacionOutput;
import org.crue.hercules.sgi.rel.model.Relacion;
import org.crue.hercules.sgi.rel.repository.RelacionRepository;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RelacionControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/relaciones";
  private static final String PATH_PARAMETER_ID = "/{id}";

  @Autowired
  private RelacionRepository relacionRepository;

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
    "classpath:scripts/relacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithSortingAndPaging_ReturnsRelacionList() throws Exception {
    String[] roles = { "REL-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "id<=5";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    ResponseEntity<List<RelacionOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<RelacionOutput>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/relacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ShouldReturnRelacionOutput() throws Exception {
    String[] roles = { "REL-V" };
    Long relacionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(relacionId)
        .toUri();

    ResponseEntity<RelacionOutput> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(null, null, roles), RelacionOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Relacion relacion = this.relacionRepository.findById(relacionId).get();
    RelacionOutput output = response.getBody();

    Assertions.assertThat(output).isNotNull();
    Assertions.assertThat(output.getId()).isEqualTo(relacion.getId());
    Assertions.assertThat(output.getEntidadDestinoRef()).isEqualTo(relacion.getEntidadDestinoRef());
    Assertions.assertThat(output.getEntidadOrigenRef()).isEqualTo(relacion.getEntidadOrigenRef());
    Assertions.assertThat(output.getObservaciones()).isEqualTo(relacion.getObservaciones());
    Assertions.assertThat(output.getTipoEntidadDestino()).isEqualTo(relacion.getTipoEntidadDestino());
    Assertions.assertThat(output.getTipoEntidadOrigen()).isEqualTo(relacion.getTipoEntidadOrigen());
  }

  @Test
  void findById_ShouldReturnRelacionNotFoundException() throws Exception {
    String[] roles = { "REL-V" };
    Long relacionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(relacionId)
        .toUri();

    ResponseEntity<Map<String, Object>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(null, null, roles), new ParameterizedTypeReference<Map<String, Object>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/relacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ShouldPersistNewRelacionObject() throws Exception {
    String[] roles = { "REL-C" };
    RelacionInput input = this.buildMockRelacionInput();
    Long beforePersistCount = this.relacionRepository.count();

    ResponseEntity<RelacionOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        this.buildRequest(null, input, roles), RelacionOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isNotNull();

    Long afterPersistCount = this.relacionRepository.count();
    Assertions.assertThat(beforePersistCount + 1).isEqualTo(afterPersistCount);

    RelacionOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(afterPersistCount);
    Assertions.assertThat(output.getEntidadDestinoRef()).isEqualTo(input.getEntidadDestinoRef());
    Assertions.assertThat(output.getEntidadOrigenRef()).isEqualTo(input.getEntidadOrigenRef());
    Assertions.assertThat(output.getTipoEntidadDestino()).isEqualTo(input.getTipoEntidadDestino());
    Assertions.assertThat(output.getTipoEntidadOrigen()).isEqualTo(input.getTipoEntidadOrigen());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/relacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ShouldUpdateAnExistingRelacionObject() throws Exception {
    String[] roles = { "REL-E" };
    Long relacionId = 1L;
    RelacionInput input = this.buildMockRelacionInput();

    ResponseEntity<RelacionOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT,
        this.buildRequest(null, input, roles), RelacionOutput.class, relacionId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    RelacionOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(relacionId);
    Assertions.assertThat(output.getEntidadDestinoRef()).isEqualTo(input.getEntidadDestinoRef());
    Assertions.assertThat(output.getEntidadOrigenRef()).isEqualTo(input.getEntidadOrigenRef());
    Assertions.assertThat(output.getTipoEntidadDestino()).isEqualTo(input.getTipoEntidadDestino());
    Assertions.assertThat(output.getTipoEntidadOrigen()).isEqualTo(input.getTipoEntidadOrigen());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/relacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_ShouldDeleteAnExistingRelacionObject() throws Exception {
    String[] roles = { "REL-B" };
    Long relacionId = 1L;

    ResponseEntity<Void> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE,
        this.buildRequest(null, null, roles), Void.class, relacionId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Assertions.assertThat(this.relacionRepository.findById(relacionId)).isEmpty();
  }

  private RelacionInput buildMockRelacionInput() {
    return RelacionInput.builder()
        .entidadDestinoRef("00001")
        .entidadOrigenRef("00002")
        .observaciones("testing")
        .tipoEntidadDestino(Relacion.TipoEntidad.PROYECTO)
        .tipoEntidadOrigen(Relacion.TipoEntidad.PROYECTO)
        .build();
  }
}
