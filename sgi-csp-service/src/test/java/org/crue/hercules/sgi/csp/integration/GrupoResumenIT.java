package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GrupoResumenController;
import org.crue.hercules.sgi.csp.dto.GrupoResumenOutput;
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
public class GrupoResumenIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = GrupoResumenController.REQUEST_MAPPING;
  private static final String PATH_ID = GrupoResumenController.PATH_ID;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/grupo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsAutorizacion() throws Exception {
    String roles = "PRC-VAL-INV-ER";
    Long grupoId = 1L;

    final ResponseEntity<GrupoResumenOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.GET, buildRequest(null, null, roles), GrupoResumenOutput.class, grupoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    GrupoResumenOutput grupo = response.getBody();

    Assertions.assertThat(grupo.getId()).as("getId()").isEqualTo(grupoId);
  }
}
