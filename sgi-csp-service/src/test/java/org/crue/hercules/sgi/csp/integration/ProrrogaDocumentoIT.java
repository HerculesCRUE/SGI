package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.Proyecto;
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
 * Test de integracion de ProrrogaDocumento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProrrogaDocumentoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/prorrogadocumentos";

  private HttpEntity<ProrrogaDocumento> buildRequest(HttpHeaders headers, ProrrogaDocumento entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-PRO-E")));

    HttpEntity<ProrrogaDocumento> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_tipo_fase.sql", "classpath:scripts/modelo_tipo_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsProrrogaDocumento() throws Exception {
    // given: new ProrrogaDocumento
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);

    // when: create ProrrogaDocumento
    final ResponseEntity<ProrrogaDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newProrrogaDocumento), ProrrogaDocumento.class);

    // then: new ProrrogaDocumento is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProrrogaDocumento responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoProrrogaId()).as("getProyectoProrrogaId()")
        .isEqualTo(newProrrogaDocumento.getProyectoProrrogaId());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(newProrrogaDocumento.getNombre());
    Assertions.assertThat(responseData.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo(newProrrogaDocumento.getDocumentoRef());
    Assertions.assertThat(responseData.getTipoDocumento().getId()).as("getTipoDocumento().getId()()")
        .isEqualTo(newProrrogaDocumento.getTipoDocumento().getId());
    Assertions.assertThat(responseData.getComentario()).as("getComentario()")
        .isEqualTo(newProrrogaDocumento.getComentario());
    Assertions.assertThat(responseData.getVisible()).as("getVisible()").isEqualTo(newProrrogaDocumento.getVisible());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_tipo_fase.sql", "classpath:scripts/modelo_tipo_documento.sql",
      "classpath:scripts/prorroga_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsProrrogaDocumento() throws Exception {
    Long idProrrogaDocumento = 1L;
    ProrrogaDocumento prorrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    prorrogaDocumento.setComentario("comentario-modificado");

    final ResponseEntity<ProrrogaDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, prorrogaDocumento), ProrrogaDocumento.class, idProrrogaDocumento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProrrogaDocumento prorrogaDocumentoActualizado = response.getBody();
    Assertions.assertThat(prorrogaDocumentoActualizado.getId()).as("getId()").isEqualTo(idProrrogaDocumento);
    Assertions.assertThat(prorrogaDocumentoActualizado.getProyectoProrrogaId()).as("getProyectoProrrogaId()")
        .isEqualTo(1L);
    Assertions.assertThat(prorrogaDocumentoActualizado.getNombre()).as("getNombre()")
        .isEqualTo("prorroga-documento-001");
    Assertions.assertThat(prorrogaDocumentoActualizado.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo("documentoRef-001");
    Assertions.assertThat(prorrogaDocumentoActualizado.getTipoDocumento().getId()).as("getTipoDocumento().getId()()")
        .isEqualTo(1L);
    Assertions.assertThat(prorrogaDocumentoActualizado.getComentario()).as("getComentario()")
        .isEqualTo("comentario-modificado");
    Assertions.assertThat(prorrogaDocumentoActualizado.getVisible()).as("getVisible()").isEqualTo(Boolean.TRUE);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_tipo_fase.sql", "classpath:scripts/modelo_tipo_documento.sql",
      "classpath:scripts/prorroga_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idProrrogaDocumento = 5L;

    final ResponseEntity<ProrrogaDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ProrrogaDocumento.class, idProrrogaDocumento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_tipo_fase.sql", "classpath:scripts/modelo_tipo_documento.sql",
      "classpath:scripts/prorroga_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existsById_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Proyecto.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/proyecto_prorroga.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_tipo_fase.sql", "classpath:scripts/modelo_tipo_documento.sql",
      "classpath:scripts/prorroga_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsProrrogaDocumento() throws Exception {
    Long idProrrogaDocumento = 1L;

    final ResponseEntity<ProrrogaDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ProrrogaDocumento.class, idProrrogaDocumento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProrrogaDocumento prorrogaDocumento = response.getBody();
    Assertions.assertThat(prorrogaDocumento.getId()).as("getId()").isNotNull();
    Assertions.assertThat(prorrogaDocumento.getId()).as("getId()").isEqualTo(idProrrogaDocumento);
    Assertions.assertThat(prorrogaDocumento.getProyectoProrrogaId()).as("getProyectoProrrogaId()").isEqualTo(1L);
    Assertions.assertThat(prorrogaDocumento.getNombre()).as("getNombre()").isEqualTo("prorroga-documento-001");
    Assertions.assertThat(prorrogaDocumento.getDocumentoRef()).as("getDocumentoRef()").isEqualTo("documentoRef-001");
    Assertions.assertThat(prorrogaDocumento.getTipoDocumento().getId()).as("getTipoDocumento().getId()()")
        .isEqualTo(1L);
    Assertions.assertThat(prorrogaDocumento.getComentario()).as("getComentario()")
        .isEqualTo("comentario-prorroga-documento-001");
    Assertions.assertThat(prorrogaDocumento.getVisible()).as("getVisible()").isEqualTo(Boolean.TRUE);

  }

  /**
   * Función que devuelve un objeto ProrrogaDocumento
   * 
   * @param id                 id del ProrrogaDocumento
   * @param proyectoProrrogaId id del ProyectoProrroga
   * @return el objeto ProrrogaDocumento
   */
  private ProrrogaDocumento generarMockProrrogaDocumento(Long id, Long proyectoProrrogaId, Long tipoDocumentoId) {

    // @formatter:off
    return ProrrogaDocumento.builder()
        .id(id)
        .proyectoProrrogaId(proyectoProrrogaId)
        .nombre("prorroga-documento-" + (id == null ? "" : String.format("%03d", id)))
        .documentoRef("documentoRef-" + (id == null ? "" : String.format("%03d", id)))
        .tipoDocumento(TipoDocumento.builder().id(tipoDocumentoId).build())
        .comentario("comentario-prorroga-documento-" + (id == null ? "" : String.format("%03d", id)))
        .visible(Boolean.TRUE)
        .build();
    // @formatter:on
  }
}
