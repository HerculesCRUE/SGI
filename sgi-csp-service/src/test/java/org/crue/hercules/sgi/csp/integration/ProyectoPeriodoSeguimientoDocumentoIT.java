package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
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
 * Test de integracion de ProyectoPeriodoSeguimientoDocumento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoPeriodoSeguimientoDocumentoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoperiodoseguimientodocumentos";

  private HttpEntity<ProyectoPeriodoSeguimientoDocumento> buildRequest(HttpHeaders headers,
      ProyectoPeriodoSeguimientoDocumento entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-PRO-E")));

    HttpEntity<ProyectoPeriodoSeguimientoDocumento> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_periodo_seguimiento.sql",
      "classpath:scripts/tipo_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsProyectoPeriodoSeguimientoDocumento() throws Exception {

    // given: new ProyectoPeriodoSeguimientoDocumento
    ProyectoPeriodoSeguimientoDocumento newProyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        null);

    // when: create ProyectoPeriodoSeguimientoDocumento
    final ResponseEntity<ProyectoPeriodoSeguimientoDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newProyectoPeriodoSeguimientoDocumento),
        ProyectoPeriodoSeguimientoDocumento.class);

    // then: new ProyectoPeriodoSeguimientoDocumento is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoPeriodoSeguimientoDocumento responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoPeriodoSeguimientoId()).as("getProyectoPeriodoSeguimientoId()")
        .isEqualTo(newProyectoPeriodoSeguimientoDocumento.getProyectoPeriodoSeguimientoId());
    Assertions.assertThat(responseData.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo(newProyectoPeriodoSeguimientoDocumento.getDocumentoRef());
    Assertions.assertThat(responseData.getComentario()).as("getComentario()")
        .isEqualTo(newProyectoPeriodoSeguimientoDocumento.getComentario());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()")
        .isEqualTo(newProyectoPeriodoSeguimientoDocumento.getNombre());
    Assertions.assertThat(responseData.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(newProyectoPeriodoSeguimientoDocumento.getTipoDocumento().getId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_periodo_seguimiento.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/proyecto_periodo_seguimiento_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsProyectoPeriodoSeguimientoDocumento() throws Exception {

    // given: Existing ProyectoPeriodoSeguimientoDocumento to be updated
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoExistente = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimientoDocumento(1L);
    proyectoPeriodoSeguimiento.setComentario("comentario-modificados");

    // when: update ProyectoPeriodoSeguimientoDocumento
    final ResponseEntity<ProyectoPeriodoSeguimientoDocumento> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, proyectoPeriodoSeguimiento),
        ProyectoPeriodoSeguimientoDocumento.class, proyectoPeriodoSeguimientoExistente.getId());

    // then: ProyectoPeriodoSeguimientoDocumento is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProyectoPeriodoSeguimientoDocumento responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoPeriodoSeguimientoId()).as("getProyectoPeriodoSeguimientoId()")
        .isEqualTo(proyectoPeriodoSeguimientoExistente.getProyectoPeriodoSeguimientoId());
    Assertions.assertThat(responseData.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo(proyectoPeriodoSeguimientoExistente.getDocumentoRef());
    Assertions.assertThat(responseData.getComentario()).as("getComentario()").isEqualTo("comentario-modificados");
    Assertions.assertThat(responseData.getNombre()).as("getNombre()")
        .isEqualTo(proyectoPeriodoSeguimientoExistente.getNombre());
    Assertions.assertThat(responseData.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(proyectoPeriodoSeguimientoExistente.getTipoDocumento().getId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_periodo_seguimiento.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/proyecto_periodo_seguimiento_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ProyectoPeriodoSeguimientoDocumento to be deleted
    Long id = 1L;

    // when: delete ProyectoPeriodoSeguimientoDocumento
    final ResponseEntity<ProyectoPeriodoSeguimientoDocumento> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ProyectoPeriodoSeguimientoDocumento.class, id);

    // then: ProyectoPeriodoSeguimientoDocumento deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_periodo_seguimiento.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/proyecto_periodo_seguimiento_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsProyectoPeriodoSeguimientoDocumento() throws Exception {
    Long id = 1L;

    final ResponseEntity<ProyectoPeriodoSeguimientoDocumento> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ProyectoPeriodoSeguimientoDocumento.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProyectoPeriodoSeguimientoDocumento responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getProyectoPeriodoSeguimientoId()).as("getProyectoPeriodoSeguimientoId()")
        .isEqualTo(1L);
  }

  /**
   * Función que devuelve un objeto ProyectoPeriodoSeguimientoDocumento
   * 
   * @param id id del ProyectoPeriodoSeguimientoDocumento
   * @return el objeto ProyectoPeriodoSeguimientoDocumento
   */
  private ProyectoPeriodoSeguimientoDocumento generarMockProyectoPeriodoSeguimientoDocumento(Long id) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId((id != null ? id : 1));
    tipoDocumento.setNombre("TipoDocumento" + (id != null ? id : 1));
    tipoDocumento.setDescripcion("descripcion-" + (id != null ? id : 1));
    tipoDocumento.setActivo(Boolean.TRUE);

    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = new ProyectoPeriodoSeguimientoDocumento();
    proyectoPeriodoSeguimientoDocumento.setId(id);
    proyectoPeriodoSeguimientoDocumento.setProyectoPeriodoSeguimientoId(id == null ? 1 : id);
    proyectoPeriodoSeguimientoDocumento.setNombre("Nombre-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimientoDocumento.setDocumentoRef("Doc-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimientoDocumento.setComentario("comentario-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimientoDocumento.setTipoDocumento(tipoDocumento);
    proyectoPeriodoSeguimientoDocumento.setVisible(Boolean.TRUE);

    return proyectoPeriodoSeguimientoDocumento;
  }
}
