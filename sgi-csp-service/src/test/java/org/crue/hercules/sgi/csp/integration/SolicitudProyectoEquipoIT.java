package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
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
 * Test de integracion de SolicitudProyectoEquipoIT.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SolicitudProyectoEquipoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoequipo";

  private HttpEntity<SolicitudProyectoEquipo> buildRequest(HttpHeaders headers, SolicitudProyectoEquipo entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E", "AUTH")));

    HttpEntity<SolicitudProyectoEquipo> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudProyectoEquipo() throws Exception {
    Long idSolicitudProyectoEquipo = 1L;

    final ResponseEntity<SolicitudProyectoEquipo> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoEquipo.class, idSolicitudProyectoEquipo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoEquipo solicitudProyectoEquipo = response.getBody();
    Assertions.assertThat(solicitudProyectoEquipo.getId()).as("getId()").isEqualTo(idSolicitudProyectoEquipo);
    Assertions.assertThat(solicitudProyectoEquipo.getSolicitudProyectoId()).as("getSolicitudProyectoId()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoEquipo.getPersonaRef()).as("getPersonaRef()").isEqualTo("personaRef-001");
  }

}
