package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class ProyectoDocumentoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectodocumentos";

  private HttpEntity<ProyectoDocumento> buildRequest(HttpHeaders headers,
      ProyectoDocumento entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", roles)));

    HttpEntity<ProyectoDocumento> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off  
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/contexto_proyecto.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoDocumento() throws Exception {
    String roles = "CSP-PRO-E";
    ProyectoDocumento proyectoDocumento = generarMockProyectoDocumento(null);

    final ResponseEntity<ProyectoDocumento> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null,
            proyectoDocumento, roles),
        ProyectoDocumento.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoDocumento proyectoDocumentoCreado = response.getBody();

    Assertions.assertThat(proyectoDocumentoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoDocumentoCreado.getNombre()).as("getNombre()")
        .isEqualTo(proyectoDocumento.getNombre());
    Assertions.assertThat(proyectoDocumentoCreado.getDocumentoRef())
        .as("getDocumentoRef()").isEqualTo(proyectoDocumento.getDocumentoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off  
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/proyecto_documento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoDocumento() throws Exception {
    String roles = "CSP-PRO-E";
    Long idProyectoDocumento = 1L;
    ProyectoDocumento proyectoDocumento = generarMockProyectoDocumento(1L);
    proyectoDocumento.setComentario("COMENTARIO-MODIFICADO");

    final ResponseEntity<ProyectoDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, proyectoDocumento, roles), ProyectoDocumento.class, idProyectoDocumento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoDocumento proyectoDocumentoActualizado = response.getBody();
    Assertions.assertThat(proyectoDocumentoActualizado.getId()).as("getId()").isEqualTo(
        proyectoDocumento.getId());
    Assertions.assertThat(proyectoDocumentoActualizado.getComentario()).as("getComentario(()")
        .isEqualTo(proyectoDocumento.getComentario());
    Assertions.assertThat(proyectoDocumentoActualizado.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo(proyectoDocumento.getDocumentoRef());
    Assertions.assertThat(proyectoDocumentoActualizado.getNombre()).as("getNombre()")
        .isEqualTo(proyectoDocumento.getNombre());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off  
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/proyecto_documento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Return204() throws Exception {
    // given: existing id
    String roles = "CSP-PRO-E";
    Long toDeleteId = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, roles), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off  
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/proyecto_documento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoDocumento() throws Exception {
    String roles = "AUTH";
    Long idProyecto = 1L;

    final ResponseEntity<ProyectoDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), ProyectoDocumento.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProyectoDocumento proyectoDocumento = response.getBody();
    Assertions.assertThat(proyectoDocumento.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyectoDocumento.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(proyectoDocumento.getComentario()).as("getComentarios()").isEqualTo("comentario-001");
    Assertions.assertThat(proyectoDocumento.getProyectoId()).as("getProyectoId()").isEqualTo(1);
  }

  /**
   * Función que devuelve un objeto ProyectoDocumento
   * 
   * @param id id del ProyectoDocumento
   * @return el objeto ProyectoDocumento
   */
  private ProyectoDocumento generarMockProyectoDocumento(Long id) {
    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(1L);
    tipoDocumento.setActivo(true);
    tipoDocumento.setNombre("nombre-001");

    ProyectoDocumento proyectoDocumento = new ProyectoDocumento();
    proyectoDocumento.setId(id);
    proyectoDocumento.setComentario("comentario-001");
    proyectoDocumento.setNombre("nombre-proyectoDocumento-001");
    proyectoDocumento.setProyectoId(1L);
    proyectoDocumento.setTipoDocumento(tipoDocumento);
    proyectoDocumento.setTipoFase(null);
    proyectoDocumento.setVisible(true);
    proyectoDocumento.setDocumentoRef("documento-ref-001");

    return proyectoDocumento;
  }

}
