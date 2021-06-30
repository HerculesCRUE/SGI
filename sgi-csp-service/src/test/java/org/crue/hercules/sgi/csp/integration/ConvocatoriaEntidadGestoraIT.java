package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de ConvocatoriaEntidadGestora.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConvocatoriaEntidadGestoraIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaentidadgestoras";

  private HttpEntity<ConvocatoriaEntidadGestora> buildRequest(HttpHeaders headers, ConvocatoriaEntidadGestora entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-E")));

    HttpEntity<ConvocatoriaEntidadGestora> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaEntidadGestora() throws Exception {

    // given: new ConvocatoriaEntidadGestora
    ConvocatoriaEntidadGestora newConvocatoriaEntidadGestora = ConvocatoriaEntidadGestora.builder().convocatoriaId(1L)
        .entidadRef("entidad-001").build();

    // when: create ConvocatoriaEntidadGestora
    final ResponseEntity<ConvocatoriaEntidadGestora> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newConvocatoriaEntidadGestora), ConvocatoriaEntidadGestora.class);

    // then: new ConvocatoriaEntidadGestora is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaEntidadGestora responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newConvocatoriaEntidadGestora.getConvocatoriaId());
    Assertions.assertThat(responseData.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(newConvocatoriaEntidadGestora.getEntidadRef());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ConvocatoriaEntidadGestora to be deleted
    Long id = 1L;

    // when: delete ConvocatoriaEntidadGestora
    final ResponseEntity<ConvocatoriaEntidadGestora> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ConvocatoriaEntidadGestora.class, id);

    // then: ConvocatoriaEntidadGestora deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }
}
