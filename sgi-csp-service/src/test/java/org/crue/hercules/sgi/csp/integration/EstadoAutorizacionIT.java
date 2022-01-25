package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.EstadoAutorizacionController;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EstadoAutorizacionIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = EstadoAutorizacionController.REQUEST_MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/estado_autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsEstadoAutorizacion() throws Exception {
    String roles = "AUTH";
    Long idEstadoAutorizacion = 1L;

    final ResponseEntity<EstadoAutorizacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), EstadoAutorizacion.class, idEstadoAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    EstadoAutorizacion estadoAutorizacion = response.getBody();

    Assertions.assertThat(estadoAutorizacion).isNotNull();
    Assertions.assertThat(estadoAutorizacion.getId()).as("getId()").isEqualTo(idEstadoAutorizacion);
  }
}