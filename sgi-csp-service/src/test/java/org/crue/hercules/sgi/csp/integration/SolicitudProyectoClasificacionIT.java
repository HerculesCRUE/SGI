package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
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
 * Test de integracion de SolicitudProyectoClasificacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SolicitudProyectoClasificacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitud-proyecto-clasificaciones";

  private HttpEntity<SolicitudProyectoClasificacion> buildRequest(HttpHeaders headers,
      SolicitudProyectoClasificacion entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<SolicitudProyectoClasificacion> request = new HttpEntity<>(entity, headers);

    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_clasificacion.sql",
    // @formatter:on 
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsSolicitudProyectoClasificacion() throws Exception {

    String[] roles = { "CSP-SOL-E", "CSP-SOL-INV-ER" };
    // given: new SolicitudProyectoClasificacion
    SolicitudProyectoClasificacion solicitudProyectoClasificacion = generarMockSolicitudProyectoClasificacion(null);

    // when: create SolicitudProyectoClasificacion
    final ResponseEntity<SolicitudProyectoClasificacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST,
        buildRequest(null, solicitudProyectoClasificacion, roles), SolicitudProyectoClasificacion.class);

    // then: new SolicitudProyectoClasificacion is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    SolicitudProyectoClasificacion responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getClasificacionRef()).as("getClasificacionRef()")
        .isEqualTo(solicitudProyectoClasificacion.getClasificacionRef());
    Assertions.assertThat(responseData.getSolicitudProyectoId()).as("getSolicitudProyectoId()")
        .isEqualTo(solicitudProyectoClasificacion.getSolicitudProyectoId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_clasificacion.sql",
    // @formatter:on 
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void deleteSolicitudProyectoClasificacion_Return204() throws Exception {

    String[] roles = { "CSP-SOL-E", "CSP-SOL-INV-ER" };

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<SolicitudProyectoClasificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, roles), SolicitudProyectoClasificacion.class, id);

    // then: 204
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoClasificacion
   * 
   * @param id id del SolicitudProyectoClasificacion
   * @return el objeto SolicitudProyectoClasificacion
   */
  private SolicitudProyectoClasificacion generarMockSolicitudProyectoClasificacion(Long id) {

    SolicitudProyectoClasificacion solicitudProyectoClasificacion = new SolicitudProyectoClasificacion();
    solicitudProyectoClasificacion.setId(id);
    solicitudProyectoClasificacion.setClasificacionRef("CNAE_11");
    solicitudProyectoClasificacion.setSolicitudProyectoId(1L);

    return solicitudProyectoClasificacion;
  }

}
