package org.crue.hercules.sgi.eti.integration;

import java.time.Instant;
import java.util.Collections;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.controller.ChecklistController;
import org.crue.hercules.sgi.eti.dto.ChecklistInput;
import org.crue.hercules.sgi.eti.dto.ChecklistOutput;
import org.crue.hercules.sgi.eti.dto.ChecklistOutput.Formly;
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
public class ChecklistIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = ChecklistController.MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_RESPUESTA = PATH_PARAMETER_ID + "/respuesta";

  @Autowired
  protected ObjectMapper mapper;

  private HttpEntity<ChecklistInput> buildRequest(ChecklistInput entity, String... auth) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (auth.length > 0) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", auth)));
    }

    HttpEntity<ChecklistInput> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<String> buildRequest(String respuesta, String... auth) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (auth.length > 0) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", auth)));
    }

    HttpEntity<String> request = new HttpEntity<>(respuesta, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/formly.sql",
      "classpath:scripts/checklist.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void requestById_ReturnsChecklist() throws Exception {
    // given: El id de un Checklist existente
    Long id = 1L;

    // when: Solicitamos el Checklist
    final ResponseEntity<ChecklistOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest((ChecklistInput) null, "ETI-CHKLST-MOD-V"), ChecklistOutput.class, id);

    // then: El Checklist se recupera correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ChecklistOutput body = response.getBody();
    Assertions.assertThat(body.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(body.getFechaCreacion()).as("getFechaCreacion()")
        .isEqualTo(Instant.parse("2020-07-09T18:00:00Z"));
    Assertions.assertThat(body.getPersonaRef()).as("getPersonaRef()").isEqualTo("me");
    Assertions.assertThat(body.getRespuesta()).as("getRespuesta()").isEqualTo("{}");
    Formly formly = body.getFormly();
    Assertions.assertThat(formly).as("getFormly()").isNotNull();
    Assertions.assertThat(formly.getId()).as("getFormly().getId()").isEqualTo(6L);
    Assertions.assertThat(formly.getEsquema()).as("getFormly().getEsquema()")
        .isEqualTo("{\"name\":\"CHECKLIST\",\"version\":1}");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/formly.sql",
      "classpath:scripts/checklist.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addNew_CreatesChecklist() throws Exception {
    // given: Un nuevo Checklist
    Long formlyId = 7L; // Formly CHECKLIST v.2
    ChecklistInput checklist = ChecklistInput.builder().personaRef("user").formlyId(formlyId).respuesta("{}").build();

    // when: Creamos el Checklist
    Instant creationStart = Instant.now();
    final ResponseEntity<ChecklistOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(checklist, "ETI-CHKLST-MOD-C"), ChecklistOutput.class);
    Instant creationEnd = Instant.now();

    // then: El Checklist se crea correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final ChecklistOutput body = response.getBody();

    Assertions.assertThat(body.getId()).as("id").isNotNull();
    Assertions.assertThat(body.getFechaCreacion()).as("getFechaCreacion()").isAfter(creationStart);
    Assertions.assertThat(body.getFechaCreacion()).as("getFechaCreacion()").isBefore(creationEnd);
    Assertions.assertThat(body.getPersonaRef()).as("getPersonaRef()").isEqualTo("user");
    Assertions.assertThat(body.getRespuesta()).as("getRespuesta()").isEqualTo("{}");
    Formly formly = body.getFormly();
    Assertions.assertThat(formly).as("getFormly()").isNotNull();
    Assertions.assertThat(formly.getId()).as("getFormly().getId()").isEqualTo(formlyId);
    Assertions.assertThat(formly.getEsquema()).as("getFormly().getEsquema()")
        .isEqualTo("{\"name\":\"CHECKLIST\",\"version\":2}");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/formly.sql",
      "classpath:scripts/checklist.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void updateRespuesta_ReturnsUpdatedChecklist() throws Exception {
    // given: Un Checklist existente
    Long id = 1L;
    String nuevaRespuesta = "{\"valor\": 1}";

    // when: Creamos el Checklist
    final ResponseEntity<ChecklistOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_RESPUESTA,
        HttpMethod.PATCH, buildRequest(nuevaRespuesta, "ETI-CHKLST-MOD-C"), ChecklistOutput.class, id);

    // then: El Checklist se actualiza correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final ChecklistOutput body = response.getBody();

    Assertions.assertThat(body.getId()).as("id").isEqualTo(id);
    Assertions.assertThat(body.getRespuesta()).as("getRespuesta()").isNotBlank();

    JsonNode respuesta = mapper.readTree(body.getRespuesta());
    Assertions.assertThat(respuesta.findValue("valor").asInt()).as("getRespuesta().getValor()").isEqualTo(1);
  }
}
