package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GrupoLineaClasificacionController;
import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion;
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

class GrupoLineaClasificacionIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = GrupoLineaClasificacionController.REQUEST_MAPPING;
  private static final String PATH_PARAMETER_ID = GrupoLineaClasificacionController.PATH_ID;

  private HttpEntity<GrupoLineaClasificacion> buildRequest(HttpHeaders headers,
      GrupoLineaClasificacion entity, String roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<GrupoLineaClasificacion> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/linea_investigacion.sql",
      "classpath:scripts/grupo_linea_investigacion.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoClasificacion() throws Exception {
    String roles = "CSP-GIN-E";
    GrupoLineaClasificacion grupoLineaClasificacion = generarMockGrupoLineaClasificacion(null);

    final ResponseEntity<GrupoLineaClasificacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST,
        buildRequest(null, grupoLineaClasificacion, roles), GrupoLineaClasificacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    GrupoLineaClasificacion grupoLineaClasificacionCreado = response.getBody();
    Assertions.assertThat(grupoLineaClasificacionCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(grupoLineaClasificacionCreado.getClasificacionRef()).as("getClasificacionRef()")
        .isEqualTo(grupoLineaClasificacion.getClasificacionRef());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/linea_investigacion.sql",
      "classpath:scripts/grupo_linea_investigacion.sql",
      "classpath:scripts/grupo_linea_clasificacion.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_ReturnStatusCode404() throws Exception {
    // given: existing ProyectoClasificacion to be deleted
    String roles = "CSP-GIN-E";
    Long id = 100L;

    // when: delete ProyectoClasificacion
    final ResponseEntity<GrupoLineaClasificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, roles), GrupoLineaClasificacion.class, id);

    // then: ProyectoClasificacion deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/linea_investigacion.sql",
      "classpath:scripts/grupo_linea_investigacion.sql",
      "classpath:scripts/grupo_linea_clasificacion.sql" 
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsStatusCode204() throws Exception {
    String roles = "CSP-GIN-E";
    Long id = 1L;

    // when: delete ProyectoClasificacion
    final ResponseEntity<GrupoLineaClasificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, roles), GrupoLineaClasificacion.class, id);

    // then: ProyectoClasificacion deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  /**
   * Función que devuelve un objeto ProyectoClasificacion
   * 
   * @param id id del ProyectoClasificacion
   * @return el objeto ProyectoClasificacion
   */
  private GrupoLineaClasificacion generarMockGrupoLineaClasificacion(Long id) {
    GrupoLineaClasificacion grupoLineaClasificacion = new GrupoLineaClasificacion();
    grupoLineaClasificacion.setId(id);
    grupoLineaClasificacion.setClasificacionRef("clasificacion-ref" + (id == null ? "" : String.format("%03d", id)));
    grupoLineaClasificacion.setGrupoLineaInvestigacionId(1L);

    return grupoLineaClasificacion;
  }

}
