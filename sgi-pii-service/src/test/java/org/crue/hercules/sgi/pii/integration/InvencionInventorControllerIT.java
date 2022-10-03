package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.InvencionInventorOutput;
import org.crue.hercules.sgi.pii.model.InvencionInventor;
import org.crue.hercules.sgi.pii.repository.InvencionInventorRepository;
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
class InvencionInventorControllerIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = "/invencion-inventores";
  private static final String PATH_INVENCION_INVENTORES = "/{id}/inventores";
  private static final String PATH_PARAMETER_ID = "/{id}";

  @Autowired
  private InvencionInventorRepository invencionInventorRepository;

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
    "classpath:scripts/invencion_inventor.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findActiveInvencionInventores_WithPagingSortingAndFiltering_ReturnInvencionInventoresOutputSubList()
      throws Exception {
    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "inventorRef=ke=0";
    Long invencionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INVENCION_INVENTORES).queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(invencionId).toUri();

    final ResponseEntity<List<InvencionInventorOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionInventorOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionInventorOutput> invencionInventorOutput = response.getBody();

    Assertions.assertThat(invencionInventorOutput).hasSize(3);
    Assertions.assertThat(invencionInventorOutput.get(0).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(invencionInventorOutput.get(1).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(invencionInventorOutput.get(2).getId()).as("id").isEqualTo(1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_inventor.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsInvencionInventorOutput() throws Exception {
    String[] roles = { "PII-INV-V", "PII-INV-E" };

    Long invencionInventorId = 1L;
    InvencionInventor expected = this.invencionInventorRepository.findById(invencionInventorId).get();

    final ResponseEntity<InvencionInventorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null, roles),
        InvencionInventorOutput.class, invencionInventorId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    InvencionInventorOutput founded = response.getBody();

    Assertions.assertThat(founded).isNotNull();
    Assertions.assertThat(founded.getActivo()).as("getActivo()").isTrue();
    Assertions.assertThat(founded.getId()).as("getId()").isEqualTo(invencionInventorId);
    Assertions.assertThat(founded.getInvencionId()).as("getInvencionId()").isEqualTo(expected.getInvencionId());
    Assertions.assertThat(founded.getInventorRef()).as("getInventorRef()").isEqualTo(expected.getInventorRef());
    Assertions.assertThat(founded.getParticipacion()).as("getParticipacion()").isEqualTo(expected.getParticipacion());
    Assertions.assertThat(founded.getRepartoUniversidad()).as("getRepartoUniversidad()")
        .isEqualTo(expected.getRepartoUniversidad());
  }
}