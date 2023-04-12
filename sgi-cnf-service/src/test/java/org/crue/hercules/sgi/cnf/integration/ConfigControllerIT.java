package org.crue.hercules.sgi.cnf.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.cnf.dto.ConfigOutput;
import org.crue.hercules.sgi.cnf.dto.CreateConfigInput;
import org.crue.hercules.sgi.cnf.dto.UpdateConfigInput;
import org.crue.hercules.sgi.cnf.repository.ConfigRepository;
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
class ConfigControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/config";
  private static final String PATH_TIME_ZONE = "/time-zone";
  private static final String PATH_NAME = "/{name}";

  @Autowired
  private ConfigRepository configRepository;

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, MediaType mediaType, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(mediaType == null ? MediaType.APPLICATION_JSON : mediaType));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  void timeZone_ReturnsEuropeMadridTimeZone() throws Exception {

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TIME_ZONE).build(false).toUri();

    String[] roles = { "CSP-COM-E" };

    final ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, MediaType.TEXT_PLAIN, roles), String.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo("Europe/Madrid");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/config.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void get_ShouldReturnConfigOutput() throws Exception {
    String[] roles = { "CSP-COM-E" };
    String configName = "entidad-implantacion";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_NAME).buildAndExpand(configName).toUri();

    ResponseEntity<ConfigOutput> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(null, null, null, roles), ConfigOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getValue()).isEqualTo("Universidad");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/config.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithSortingAndPaging_ShouldReturnConfigOutputList() throws Exception {
    String[] roles = { "CSP-COM-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "name,asc";
    String filter = "name=ke=csp-pro";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    ResponseEntity<List<ConfigOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, null, roles), new ParameterizedTypeReference<List<ConfigOutput>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("8");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/config.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ShouldReturnNewPersistedConfigOutput() throws Exception {
    String[] roles = { "ADM-CNF-E" };
    CreateConfigInput input = buildMockCreateConfigInput();

    ResponseEntity<ConfigOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        this.buildRequest(null, input, null, roles), ConfigOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isNotNull();

    ConfigOutput output = response.getBody();
    Assertions.assertThat(output.getDescription()).isEqualTo(input.getDescription());
    Assertions.assertThat(output.getName()).isEqualTo(input.getName());
    Assertions.assertThat(output.getValue()).isEqualTo(input.getValue());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/config.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ShouldReturnModifiedConfigOutput() throws Exception {
    String[] roles = { "ADM-CNF-E" };
    String configName = "entidad-implantacion";
    UpdateConfigInput input = buildMockUpdateConfigInput();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_NAME).buildAndExpand(configName).toUri();

    ResponseEntity<ConfigOutput> response = this.restTemplate.exchange(uri, HttpMethod.PUT,
        this.buildRequest(null, input, null, roles), ConfigOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    ConfigOutput output = response.getBody();
    Assertions.assertThat(output.getDescription()).isEqualTo(input.getDescription());
    Assertions.assertThat(output.getName()).isEqualTo(configName);
    Assertions.assertThat(output.getValue()).isEqualTo(input.getValue());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/config.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_ShouldReturnStatusCodeOK() throws Exception {
    String[] roles = { "ADM-CNF-E" };
    String configName = "entidad-implantacion";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_NAME).buildAndExpand(configName).toUri();

    ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.DELETE,
        this.buildRequest(null, null, null, roles), Void.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    Assertions.assertThat(this.configRepository.existsById(configName)).isFalse();
  }

  private CreateConfigInput buildMockCreateConfigInput() {

    return CreateConfigInput.builder()
        .description("Testing conifg")
        .name("test-config")
        .value("testing-val")
        .build();
  }

  private UpdateConfigInput buildMockUpdateConfigInput() {

    return UpdateConfigInput.builder()
        .description("Testing conifg")
        .value("testing-val")
        .build();
  }
}