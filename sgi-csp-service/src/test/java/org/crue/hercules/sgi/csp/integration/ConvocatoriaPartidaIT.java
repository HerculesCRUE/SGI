package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoPartida;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ConvocatoriaPartidaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoria-partidas";
  private static final String PATH_MODIFICABLE = "/modificable";

  private HttpEntity<ConvocatoriaPartida> buildRequest(HttpHeaders headers,
      ConvocatoriaPartida entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ConvocatoriaPartida> request = new HttpEntity<>(entity, headers);

    return request;
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/convocatoria_partida.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnConvocatoriaPartida() throws Exception {
    Long convocatoriaPartidaId = 3L;

    final ResponseEntity<ConvocatoriaPartida> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, "AUTH"),
        ConvocatoriaPartida.class,
        convocatoriaPartidaId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaPartida convocatoriaPartida = response.getBody();

    Assertions.assertThat(convocatoriaPartida).isNotNull();
    Assertions.assertThat(convocatoriaPartida.getId()).as("getId()")
        .isEqualTo(convocatoriaPartidaId);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsConvocatoriaPartida() throws Exception {
    ConvocatoriaPartida convocatoriaPartida = buildMockConvocatoriaPartida(null);

    final ResponseEntity<ConvocatoriaPartida> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, convocatoriaPartida, "CSP-CON-E", "CSP-CON-C"), ConvocatoriaPartida.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ConvocatoriaPartida convocatoriaPartidaCreated = response.getBody();
    Assertions.assertThat(convocatoriaPartidaCreated.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaPartidaCreated.getCodigo()).as("getCodigo()").isEqualTo(
        convocatoriaPartidaCreated.getCodigo());
    Assertions.assertThat(convocatoriaPartidaCreated.getDescripcion()).as("getDescripcion()")
        .isEqualTo(convocatoriaPartidaCreated.getDescripcion());
    Assertions.assertThat(convocatoriaPartidaCreated.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaPartidaCreated.getConvocatoriaId());

  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/convocatoria_partida.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsConvocatoriaPartida() throws Exception {
    Long convocatoriaPartidaId = 1L;
    ConvocatoriaPartida convocatoriaPartidaToUpdate = buildMockConvocatoriaPartida(1L);
    convocatoriaPartidaToUpdate.setCodigo("COD-UPDATED");

    final ResponseEntity<ConvocatoriaPartida> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, convocatoriaPartidaToUpdate, "CSP-CON-E"),
        ConvocatoriaPartida.class, convocatoriaPartidaId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaPartida convocatoriaPartidaUpdated = response.getBody();
    Assertions.assertThat(convocatoriaPartidaUpdated.getId()).as("getId()")
        .isEqualTo(convocatoriaPartidaToUpdate.getId());
    Assertions.assertThat(convocatoriaPartidaUpdated.getCodigo()).as("getCodigo()")
        .isEqualTo(convocatoriaPartidaToUpdate.getCodigo());
    Assertions.assertThat(convocatoriaPartidaUpdated.getDescripcion()).as("getDescripcion()")
        .isEqualTo(convocatoriaPartidaToUpdate.getDescripcion());
    Assertions.assertThat(convocatoriaPartidaUpdated.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaPartidaToUpdate.getConvocatoriaId());
    Assertions.assertThat(convocatoriaPartidaUpdated.getTipoPartida()).as("getTipoPartida()")
        .isEqualTo(convocatoriaPartidaToUpdate.getTipoPartida());
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/convocatoria_partida.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Return204() throws Exception {
    // given: existing id
    Long toDeleteId = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, "CSP-CON-E"), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/convocatoria_partida.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void modificable_ReturnsStatusCode200() throws Exception {
    // given: existing id
    Long modificableId = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_MODIFICABLE,
        HttpMethod.HEAD, buildRequest(null, null, "CSP-CON-E"), Void.class, modificableId);
    // then: 200 ok
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  private ConvocatoriaPartida buildMockConvocatoriaPartida(Long convocatoriaPartidaId) throws Exception {
    return ConvocatoriaPartida.builder()
        .codigo("TEST_CREATED")
        .convocatoriaId(1L)
        .descripcion("Test Created Success")
        .tipoPartida(TipoPartida.GASTO)
        .id(convocatoriaPartidaId)
        .build();
  }
}