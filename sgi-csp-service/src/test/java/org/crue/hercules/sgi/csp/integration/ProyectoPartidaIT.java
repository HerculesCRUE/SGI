package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoPartidaController;
import org.crue.hercules.sgi.csp.enums.TipoPartida;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProyectoPartidaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ProyectoPartidaController.REQUEST_MAPPING;
  private static final String PATH_MODIFICABLE = "/modificable";
  private static final String PATH_ANUALIDADES = "/anualidades";

  private HttpEntity<ProyectoPartida> buildRequest(HttpHeaders headers,
      ProyectoPartida entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ProyectoPartida> request = new HttpEntity<>(entity, headers);

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
    "classpath:scripts/convocatoria_partida.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoPartida() throws Exception {
    ProyectoPartida toCreate = buildMockProyectoPartida(null);

    final ResponseEntity<ProyectoPartida> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, "CSP-PRO-E"),
        ProyectoPartida.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoPartida created = response.getBody();

    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(created.getCodigo()).as("getCodigo()")
        .isEqualTo(toCreate.getCodigo());
    Assertions.assertThat(created.getProyectoId())
        .as("getProyectoId()").isEqualTo(toCreate.getProyectoId());
    Assertions.assertThat(created.getDescripcion())
        .as("getDescripcion()").isEqualTo(toCreate.getDescripcion());
    Assertions.assertThat(created.getTipoPartida())
        .as("getTipoPartida()").isEqualTo(toCreate.getTipoPartida());
    Assertions.assertThat(created.getConvocatoriaPartidaId())
        .as("getConvocatoriaPartidaId()").isEqualTo(toCreate.getConvocatoriaPartidaId());

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
    "classpath:scripts/convocatoria_partida.sql",
    "classpath:scripts/proyecto_partida.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoPartida() throws Exception {
    Long proyectoPartidaId = 1L;

    ProyectoPartida toUpdate = buildMockProyectoPartida(1L);
    toUpdate.setCodigo("UU.UUUU.UUUU.UPDAT");

    final ResponseEntity<ProyectoPartida> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, toUpdate, "CSP-PRO-E"),
        ProyectoPartida.class, proyectoPartidaId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPartida updated = response.getBody();
    Assertions.assertThat(updated.getId()).as("getId()")
        .isEqualTo(toUpdate.getId());
    Assertions.assertThat(updated.getCodigo()).as("getCodigo()")
        .isEqualTo(toUpdate.getCodigo());
    Assertions.assertThat(updated.getDescripcion()).as("getDescripcion()")
        .isEqualTo(toUpdate.getDescripcion());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()")
        .isEqualTo(toUpdate.getProyectoId());
    Assertions.assertThat(updated.getConvocatoriaPartidaId()).as("getConvocatoriaPartidaId()")
        .isEqualTo(toUpdate.getConvocatoriaPartidaId());
    Assertions.assertThat(updated.getTipoPartida()).as("getTipoPartida()")
        .isEqualTo(toUpdate.getTipoPartida());
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
    "classpath:scripts/convocatoria_partida.sql",
    "classpath:scripts/proyecto_partida.sql"
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off    
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/convocatoria_partida.sql",
    "classpath:scripts/proyecto_partida.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void exists_Returns200() throws Exception {
    // given: existing id
    Long toDeleteId = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null, "CSP-PRO-E"), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
    "classpath:scripts/convocatoria_partida.sql",
    "classpath:scripts/proyecto_partida.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoPartida() throws Exception {
    Long proyectoPartidaId = 1L;

    final ResponseEntity<ProyectoPartida> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, "CSP-PRO-E"),
        ProyectoPartida.class,
        proyectoPartidaId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPartida proyectoPartida = response.getBody();

    Assertions.assertThat(proyectoPartida).isNotNull();
    Assertions.assertThat(proyectoPartida.getId()).as("getId()")
        .isEqualTo(proyectoPartidaId);
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
    "classpath:scripts/convocatoria_partida.sql",
    "classpath:scripts/proyecto_partida.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void modificable_ReturnsHttpStatus200() throws Exception {
    Long proyectoPartidaId = 1L;

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_MODIFICABLE,
        HttpMethod.HEAD, buildRequest(null, null, "CSP-PRO-E"),
        Void.class,
        proyectoPartidaId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/convocatoria_partida.sql",
    "classpath:scripts/proyecto_partida.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/anualidad_gasto.sql",
    "classpath:scripts/anualidad_ingreso.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsAnyAnualidad_ReturnsHttpStatus200() throws Exception {
    Long proyectoPartidaId = 1L;

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ANUALIDADES,
        HttpMethod.HEAD, buildRequest(null, null, "CSP-PRO-E"),
        Void.class,
        proyectoPartidaId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  private ProyectoPartida buildMockProyectoPartida(Long proyectoPartidaId) {
    return ProyectoPartida.builder()
        .id(proyectoPartidaId)
        .codigo("AA.AAAA.BBBB.CCCCD")
        .convocatoriaPartidaId(1L)
        .descripcion("Nueva Partida Creada")
        .proyectoId(1L)
        .tipoPartida(TipoPartida.GASTO)
        .build();
  }
}