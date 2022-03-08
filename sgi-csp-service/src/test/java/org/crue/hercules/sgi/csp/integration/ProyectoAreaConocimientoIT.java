package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoAreaConocimientoController;
import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
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
class ProyectoAreaConocimientoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ProyectoAreaConocimientoController.MAPPING;

  private HttpEntity<ProyectoAreaConocimiento> buildRequest(HttpHeaders headers,
      ProyectoAreaConocimiento entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ProyectoAreaConocimiento> request = new HttpEntity<>(entity, headers);

    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off    
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/tipo_facturacion.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoAreaConocimiento() throws Exception {
    ProyectoAreaConocimiento toCreate = buildMockProyectoAreaConocimiento(null);

    final ResponseEntity<ProyectoAreaConocimiento> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, "CSP-PRO-E"),
        ProyectoAreaConocimiento.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoAreaConocimiento created = response.getBody();

    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(created.getProyectoId()).as("getProyectoId()")
        .isEqualTo(toCreate.getProyectoId());
    Assertions.assertThat(created.getAreaConocimientoRef())
        .as("getAreaConocimientoRef()").isEqualTo(toCreate.getAreaConocimientoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off    
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/proyecto_area.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Returns204() throws Exception {
    // given: existing id
    Long toDeleteId = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, "CSP-PRO-E"), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private ProyectoAreaConocimiento buildMockProyectoAreaConocimiento(Long proyectoAreaConocimientoId) {
    return ProyectoAreaConocimiento.builder()
        .areaConocimientoRef("00001")
        .id(proyectoAreaConocimientoId)
        .proyectoId(1L)
        .build();
  }
}