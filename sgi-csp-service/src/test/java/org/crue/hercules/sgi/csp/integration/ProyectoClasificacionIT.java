package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoClasificacion;
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

class ProyectoClasificacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyecto-clasificaciones";

  private HttpEntity<ProyectoClasificacion> buildRequest(HttpHeaders headers,
      ProyectoClasificacion entity, String roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ProyectoClasificacion> request = new HttpEntity<>(entity, headers);
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
    "classpath:scripts/estado_proyecto.sql",   
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoClasificacion() throws Exception {
    String roles = "CSP-PRO-E";
    ProyectoClasificacion proyectoClasificacion = generarMockProyectoClasificacion(null);

    final ResponseEntity<ProyectoClasificacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST,
        buildRequest(null, proyectoClasificacion, roles), ProyectoClasificacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoClasificacion proyectoClasificacionCreado = response.getBody();
    Assertions.assertThat(proyectoClasificacionCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoClasificacionCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoClasificacion.getProyectoId());
    Assertions.assertThat(proyectoClasificacionCreado.getClasificacionRef()).as("getClasificacionRef()")
        .isEqualTo(proyectoClasificacion.getClasificacionRef());

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
    "classpath:scripts/estado_proyecto.sql",   
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_ReturnDoNotGetProyectoClasificacion() throws Exception {
    // given: existing ProyectoClasificacion to be deleted
    String roles = "CSP-PRO-E";
    Long id = 1L;

    // when: delete ProyectoClasificacion
    final ResponseEntity<ProyectoClasificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, roles), ProyectoClasificacion.class, id);

    // then: ProyectoClasificacion deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

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
    "classpath:scripts/estado_proyecto.sql",   
    "classpath:scripts/proyecto_clasificacion.sql"   
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsStatusCode204() throws Exception {
    String roles = "CSP-PRO-E";
    Long id = 1L;

    // when: delete ProyectoClasificacion
    final ResponseEntity<ProyectoClasificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, roles), ProyectoClasificacion.class, id);

    // then: ProyectoClasificacion deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  /**
   * Función que devuelve un objeto ProyectoClasificacion
   * 
   * @param id id del ProyectoClasificacion
   * @return el objeto ProyectoClasificacion
   */
  private ProyectoClasificacion generarMockProyectoClasificacion(Long id) {
    ProyectoClasificacion proyectoClasificacion = new ProyectoClasificacion();
    proyectoClasificacion.setId(id);
    proyectoClasificacion.setClasificacionRef("clasificacion-ref" + (id == null ? "" : String.format("%03d", id)));
    proyectoClasificacion.setProyectoId(1L);

    return proyectoClasificacion;
  }

}
