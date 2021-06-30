package org.crue.hercules.sgi.eti.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.controller.FormlyController;
import org.crue.hercules.sgi.eti.dto.FormlyOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FormlyIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_NOMBRE = "/{nombre}";
  private static final String CONTROLLER_BASE_PATH = FormlyController.MAPPING;

  private HttpEntity<Void> buildRequest(String... auth) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", auth)));

    HttpEntity<Void> request = new HttpEntity<>(null, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/formly.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsFormly() throws Exception {
    Long id = 2L;

    final ResponseEntity<FormlyOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest("ETI-CHKLST-MOD-V"), FormlyOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    FormlyOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo("FRM002");
    Assertions.assertThat(responseData.getVersion()).as("getVersion()").isEqualTo(1L);
    Assertions.assertThat(responseData.getEsquema()).as("getEsquema()").isEqualTo("{}");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/formly.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findByNombre_ReturnsLatesFormlyVersionWithGivenName() throws Exception {
    String nombre = "FRM001";

    final ResponseEntity<FormlyOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_NOMBRE,
        HttpMethod.GET, buildRequest("ETI-CHKLST-MOD-C"), FormlyOutput.class, nombre);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    FormlyOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(nombre);
    Assertions.assertThat(responseData.getVersion()).as("getVersion()").isEqualTo(3L);
    Assertions.assertThat(responseData.getEsquema()).as("getEsquema()").isEqualTo("{}");
  }

}
