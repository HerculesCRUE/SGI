package org.crue.hercules.sgi.rep.integration.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;

import org.crue.hercules.sgi.rep.integration.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InformeEvaluacionReportServiceIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{idEvaluacion}";
  private static final String INFORME_EVALUACION_CONTROLLER_BASE_PATH = "/informe-evaluacion";

  private HttpEntity<Resource> buildRequest(HttpHeaders headers, Resource entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s",
          tokenBuilder.buildToken("user", "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR", "ETI-EVC-EVALR")));
    }

    HttpEntity<Resource> request = new HttpEntity<>(entity, headers);
    return request;
  }

  void testPdfInformeEvaluacion() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR", "ETI-EVC-EVALR")));

    final ResponseEntity<Resource> response = restTemplate.exchange(
        INFORME_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(headers, null), Resource.class, 1L);

    final Resource resource = response.getBody();
    // given: report generated
    assertNotNull(resource);
  }

}