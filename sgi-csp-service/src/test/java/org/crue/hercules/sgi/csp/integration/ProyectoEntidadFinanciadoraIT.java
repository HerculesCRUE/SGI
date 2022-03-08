package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
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
 * Test de integracion de ProyectoEntidadFinanciadora.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoEntidadFinanciadoraIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoentidadfinanciadoras";

  private HttpEntity<ProyectoEntidadFinanciadora> buildRequest(HttpHeaders headers, ProyectoEntidadFinanciadora entity,
      String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));
    HttpEntity<ProyectoEntidadFinanciadora> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql" 
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoEntidadFinanciadora() throws Exception {

    // given: new ProyectoEntidadFinanciadora
    ProyectoEntidadFinanciadora newProyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(null);

    // when: create ProyectoEntidadFinanciadora
    final ResponseEntity<ProyectoEntidadFinanciadora> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newProyectoEntidadFinanciadora, "AUTH", "CSP-PRO-E", "AUTH"),
        ProyectoEntidadFinanciadora.class);

    // then: new ProyectoEntidadFinanciadora is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoEntidadFinanciadora responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()")
        .isEqualTo(newProyectoEntidadFinanciadora.getProyectoId());
    Assertions.assertThat(responseData.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(newProyectoEntidadFinanciadora.getEntidadRef());
    Assertions.assertThat(responseData.getFuenteFinanciacion().getId()).as("getFuenteFinanciacion().getId()")
        .isEqualTo(newProyectoEntidadFinanciadora.getFuenteFinanciacion().getId());
    Assertions.assertThat(responseData.getTipoFinanciacion().getId()).as("getTipoFinanciacion().getId()")
        .isEqualTo(newProyectoEntidadFinanciadora.getTipoFinanciacion().getId());
    Assertions.assertThat(responseData.getPorcentajeFinanciacion()).as("getPorcentajeFinanciacion()")
        .isEqualTo(newProyectoEntidadFinanciadora.getPorcentajeFinanciacion());
    Assertions.assertThat(responseData.getAjena()).as("getAjena()")
        .isEqualTo(newProyectoEntidadFinanciadora.getAjena());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoEntidadFinanciadora() throws Exception {
    Long idProyectoEntidadFinanciadora = 1L;
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(
        idProyectoEntidadFinanciadora);

    final ResponseEntity<ProyectoEntidadFinanciadora> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, proyectoEntidadFinanciadora, "CSP-PRO-E"), ProyectoEntidadFinanciadora.class,
        idProyectoEntidadFinanciadora);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoEntidadFinanciadora proyectoEntidadFinanciadoraActualizado = response.getBody();
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoEntidadFinanciadora.getProyectoId());
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(proyectoEntidadFinanciadora.getEntidadRef());
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()").isEqualTo(proyectoEntidadFinanciadora.getFuenteFinanciacion().getId());
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()").isEqualTo(proyectoEntidadFinanciadora.getTipoFinanciacion().getId());
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getPorcentajeFinanciacion())
        .as("getPorcentajeFinanciacion()").isEqualTo(proyectoEntidadFinanciadora.getPorcentajeFinanciacion());
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getAjena()).as("getAjena()")
        .isEqualTo(proyectoEntidadFinanciadora.getAjena());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql", 
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    // given: existing ProyectoEntidadFinanciadora to be deleted
    Long id = 1L;

    // when: delete ProyectoEntidadFinanciadora
    final ResponseEntity<ProyectoEntidadFinanciadora> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null, "CSP-PRO-E"),
        ProyectoEntidadFinanciadora.class, id);

    // then: ProyectoEntidadFinanciadora deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql", 
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoEntidadFinanciadora() throws Exception {
    Long idProyectoEntidadFinanciadora = 1L;

    final ResponseEntity<ProyectoEntidadFinanciadora> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null, "CSP-PRO-E"),
        ProyectoEntidadFinanciadora.class, idProyectoEntidadFinanciadora);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = response.getBody();
    Assertions.assertThat(proyectoEntidadFinanciadora.getId()).as("getId()").isEqualTo(idProyectoEntidadFinanciadora);
    Assertions.assertThat(proyectoEntidadFinanciadora.getProyectoId()).as("getProyectoId()").isEqualTo(1L);
    Assertions.assertThat(proyectoEntidadFinanciadora.getEntidadRef()).as("getEntidadRef()").isEqualTo("entidad-001");
    Assertions.assertThat(proyectoEntidadFinanciadora.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoEntidadFinanciadora.getTipoFinanciacion().getId()).as("getTipoFinanciacion().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(proyectoEntidadFinanciadora.getPorcentajeFinanciacion().floatValue())
        .as("getPorcentajeFinanciacion()").isEqualTo(20F);
    Assertions.assertThat(proyectoEntidadFinanciadora.getAjena()).as("getAjena()").isEqualTo(false);
  }

  /**
   * Función que devuelve un objeto ProyectoEntidadFinanciadora
   * 
   * @param id id del ProyectoEntidadFinanciadora
   * @return el objeto ProyectoEntidadFinanciadora
   */
  private ProyectoEntidadFinanciadora generarMockProyectoEntidadFinanciadora(Long id) {

    FuenteFinanciacion fuenteFinanciacion = FuenteFinanciacion.builder()
    //@formatter:off
      .id(id == null ? 1 : id)
      .activo(true)
      .build();
    //@formatter:on

    TipoFinanciacion tipoFinanciacion = TipoFinanciacion.builder()
    //@formatter:off
      .id(id == null ? 1 : id)
      .activo(true)
      .build();
    //@formatter:on

    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = ProyectoEntidadFinanciadora.builder()
    //@formatter:off
      .id(id)
      .proyectoId(id == null ? 1 : id)
      .entidadRef("entidad-" + String.format("%03d", id == null ? 0 : id))
      .fuenteFinanciacion(fuenteFinanciacion)
      .tipoFinanciacion(tipoFinanciacion)
      .porcentajeFinanciacion(BigDecimal.valueOf(50))
      .ajena(false)
      .build();
    //@formatter:on

    return proyectoEntidadFinanciadora;
  }

}
