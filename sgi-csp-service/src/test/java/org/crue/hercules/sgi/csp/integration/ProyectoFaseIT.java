package org.crue.hercules.sgi.csp.integration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseAvisoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseInput;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseOutput;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoFaseAviso;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.service.ProyectoFaseAvisoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de ProyectoFase.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoFaseIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectofases";

  @MockBean
  private ProyectoFaseAvisoService proyectoFaseAvisoService;

  private HttpEntity<ProyectoFaseInput> buildRequest(HttpHeaders headers, ProyectoFaseInput entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-E", "AUTH")));

    HttpEntity<ProyectoFaseInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_fase.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoFase() throws Exception {
    // given: new ProyectoFase
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    ProyectoFase newProyectoFase = generarMockProyectoFase(1L, 1L, 1L);
    newProyectoFase.setId(null);

    // when: create ProyectoFase
    final ResponseEntity<ProyectoFaseOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input), ProyectoFaseOutput.class);

    BDDMockito
        .given(this.proyectoFaseAvisoService.create(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<ProyectoFaseAvisoInput>any()))
        .willReturn(this.buildMockProyectoFaseAviso(1L, 1L));

    // then: new ProyectoFase is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoFaseOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()")
        .isEqualTo(newProyectoFase.getProyectoId());
    Assertions.assertThat(responseData.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(newProyectoFase.getTipoFase().getId());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formater: off
      "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_fase.sql",
      "classpath:scripts/proyecto_fase_aviso.sql",
      "classpath:scripts/proyecto_fase.sql"
      // @formater: on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoFase() throws Exception {
    Long idProyectoFase = 1L;
    ProyectoFaseInput proyectoFase = generarMockProyectoFaseInput(1L);
    proyectoFase.setObservaciones("observaciones modificado");

    final ResponseEntity<ProyectoFaseOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, proyectoFase), ProyectoFaseOutput.class, idProyectoFase);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoFaseOutput proyectoFaseActualizado = response.getBody();
    Assertions.assertThat(proyectoFaseActualizado.getId()).as("getId()").isNotNull();

    Assertions.assertThat(proyectoFaseActualizado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoFase.getProyectoId());
    Assertions.assertThat(proyectoFaseActualizado.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoFase.getFechaInicio());
    Assertions.assertThat(proyectoFaseActualizado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(proyectoFase.getFechaFin());
    Assertions.assertThat(proyectoFaseActualizado.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(proyectoFase.getTipoFaseId());
    Assertions.assertThat(proyectoFaseActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoFase.getObservaciones());
    Assertions.assertThat(proyectoFaseActualizado.getAviso1()).as("getAviso1()")
        .isNotNull();
    Assertions.assertThat(proyectoFaseActualizado.getAviso2()).as("getAviso2()")
        .isNotNull();

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_fase.sql",
      "classpath:scripts/proyecto_fase_aviso.sql",
      "classpath:scripts/proyecto_fase.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    Long idProyectoFase = 1L;

    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Void.class, idProyectoFase);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/tipo_fase.sql",
      "classpath:scripts/modelo_ejecucion.sql", "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql", "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/modelo_tipo_fase.sql",
      "classpath:scripts/proyecto_fase_aviso.sql",
      "classpath:scripts/proyecto_fase.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoFase() throws Exception {
    Long idProyectoFase = 1L;

    final ResponseEntity<ProyectoFaseOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ProyectoFaseOutput.class, idProyectoFase);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoFaseOutput proyectoFase = response.getBody();
    Assertions.assertThat(proyectoFase.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoFase.getTipoFase().getId()).as("getTipoFase().getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoFase.getProyectoId()).as("getProyectoId()").isEqualTo(1L);
    Assertions.assertThat(proyectoFase.getObservaciones()).as("observaciones")
        .isEqualTo("observaciones-proyecto-fase-" + String.format("%03d", idProyectoFase));
    Assertions.assertThat(proyectoFase.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(Instant.parse("2020-10-01T00:00:00Z"));
    Assertions.assertThat(proyectoFase.getFechaFin()).as("getFechaFin()")
        .isEqualTo(Instant.parse("2020-10-02T23:59:59Z"));

  }

  /**
   * Función que devuelve un objeto ProyectoFase
   * 
   * @param id         id del ProyectoFase
   * @param tipoFaseId id del TipoFase
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoFase
   */
  private ProyectoFase generarMockProyectoFase(Long id, Long tipoFaseId, Long proyectoId) {

    // @formatter:off
    return ProyectoFase.builder()
        .id(id)
        .tipoFase(TipoFase.builder().id(tipoFaseId).build())
        .proyectoId(proyectoId)
        .fechaInicio(Instant.parse("2020-10-19T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-20T00:00:00Z"))
        .observaciones("observaciones-proyecto-fase-" + (id == null ? "" : String.format("%03d", id)))
        .proyectoFaseAviso1(buildMockProyectoFaseAviso(1L, id))
        .proyectoFaseAviso2(buildMockProyectoFaseAviso(2L, id))
        .build();
    // @formatter:on
  }

  private ProyectoFaseInput generarMockProyectoFaseInput(Long id) {
    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id == null ? 1 : id);
    tipoFase.setActivo(true);

    ProyectoFaseInput proyectoFase = new ProyectoFaseInput();
    proyectoFase.setProyectoId(id == null ? 1 : id);
    proyectoFase.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    proyectoFase.setFechaFin(Instant.parse("2020-10-20T23:59:59Z"));
    proyectoFase.setObservaciones("observaciones-proyecto-fase-" + String.format("%03d", id));
    proyectoFase.setAviso1(buildMockProyectoFaseAvisoInput(id));
    proyectoFase.setAviso2(buildMockProyectoFaseAvisoInput(id));
    proyectoFase.setTipoFaseId(tipoFase.getId());

    return proyectoFase;
  }

  private ProyectoFaseAviso buildMockProyectoFaseAviso(Long id, Long proyectoFaseId) {
    return ProyectoFaseAviso.builder()
        .comunicadoRef("3333")
        .id(id)
        .tareaProgramadaRef("666")
        .build();
  }

  private ProyectoFaseAvisoInput buildMockProyectoFaseAvisoInput(Long proyectoFaseId) {
    List<ProyectoFaseAvisoInput.Destinatario> destinatarios = new LinkedList<>();
    destinatarios.add(ProyectoFaseAvisoInput.Destinatario.builder()
        .email("testing@um.com")
        .nombre("manolo")
        .build());

    return ProyectoFaseAvisoInput.builder()
        .asunto("asunto")
        .contenido("contenido mail")
        .destinatarios(destinatarios)
        .incluirIpsProyecto(new Boolean(Boolean.TRUE))
        .fechaEnvio(Instant.now().plus(15, ChronoUnit.MINUTES))
        .build();
  }
}
