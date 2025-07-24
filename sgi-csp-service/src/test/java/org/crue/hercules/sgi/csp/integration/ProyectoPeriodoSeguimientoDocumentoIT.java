package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumentoComentario;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumentoNombre;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumentoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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
  void create_ReturnsProyectoPeriodoSeguimientoDocumento() throws Exception {

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
    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo(I18nHelper.getValueForLanguage(newProyectoPeriodoSeguimientoDocumento.getNombre(), Language.ES));
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
  void update_ReturnsProyectoPeriodoSeguimientoDocumento() throws Exception {

    // given: Existing ProyectoPeriodoSeguimientoDocumento to be updated
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoExistente = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimientoDocumento(1L);
    Set<ProyectoPeriodoSeguimientoDocumentoComentario> comentarioDocumento = new HashSet<>();
    comentarioDocumento.add(new ProyectoPeriodoSeguimientoDocumentoComentario(Language.ES, "comentario-modificados"));
    proyectoPeriodoSeguimiento.setComentario(comentarioDocumento);

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
    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.getComentario(), Language.ES))
        .as("getComentario()").isEqualTo("comentario-modificados");
    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo(I18nHelper.getValueForLanguage(proyectoPeriodoSeguimientoExistente.getNombre(), Language.ES));
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
  void delete_Return204() throws Exception {
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
  void findById_ReturnsProyectoPeriodoSeguimientoDocumento() throws Exception {
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
    Set<TipoDocumentoNombre> nombreTipoDocumento = new HashSet<>();
    nombreTipoDocumento.add(new TipoDocumentoNombre(Language.ES, "TipoDocumento" + (id != null ? id : 1)));

    Set<TipoDocumentoDescripcion> descripcionTipoDocumento = new HashSet<>();
    descripcionTipoDocumento.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-" + (id != null ? id : 1)));

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId((id != null ? id : 1));
    tipoDocumento.setNombre(nombreTipoDocumento);
    tipoDocumento.setDescripcion(descripcionTipoDocumento);
    tipoDocumento.setActivo(Boolean.TRUE);

    Set<ProyectoPeriodoSeguimientoDocumentoNombre> nombreDocumento = new HashSet<>();
    nombreDocumento.add(new ProyectoPeriodoSeguimientoDocumentoNombre(Language.ES,
        "Nombre-" + String.format("%03d", (id != null ? id : 1))));

    Set<ProyectoPeriodoSeguimientoDocumentoComentario> comentarioDocumento = new HashSet<>();
    comentarioDocumento.add(new ProyectoPeriodoSeguimientoDocumentoComentario(Language.ES,
        "comentario-" + String.format("%03d", (id != null ? id : 1))));

    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = new ProyectoPeriodoSeguimientoDocumento();
    proyectoPeriodoSeguimientoDocumento.setId(id);
    proyectoPeriodoSeguimientoDocumento.setProyectoPeriodoSeguimientoId(id == null ? 1 : id);
    proyectoPeriodoSeguimientoDocumento.setNombre(nombreDocumento);
    proyectoPeriodoSeguimientoDocumento.setDocumentoRef("Doc-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimientoDocumento.setComentario(comentarioDocumento);
    proyectoPeriodoSeguimientoDocumento.setTipoDocumento(tipoDocumento);
    proyectoPeriodoSeguimientoDocumento.setVisible(Boolean.TRUE);

    return proyectoPeriodoSeguimientoDocumento;
  }
}
