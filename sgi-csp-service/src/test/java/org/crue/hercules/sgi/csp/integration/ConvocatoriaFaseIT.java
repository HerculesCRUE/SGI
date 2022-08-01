package org.crue.hercules.sgi.csp.integration;

import java.time.Instant;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseOutput;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de ConvocatoriaFase.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConvocatoriaFaseIT extends BaseIT {

  @Autowired
  private ModelMapper modelMapper;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriafases";

  private HttpEntity<ConvocatoriaFaseInput> buildRequest(HttpHeaders headers, ConvocatoriaFaseInput entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-CON-C", "CSP-CON-E")));

    HttpEntity<ConvocatoriaFaseInput> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsConvocatoriaFase() throws Exception {

    // given: new ConvocatoriaFase
    ConvocatoriaFase newConvocatoriaFase = generarMockConvocatoriaFase(null);

    // when: create ConvocatoriaFase
    final ResponseEntity<ConvocatoriaFaseOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, modelMapper.map(newConvocatoriaFase, ConvocatoriaFaseInput.class)),
        ConvocatoriaFaseOutput.class);

    // then: new ConvocatoriaFase is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaFaseOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newConvocatoriaFase.getConvocatoriaId());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaIncio()")
        .isEqualTo(newConvocatoriaFase.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()").isEqualTo(newConvocatoriaFase.getFechaFin());
    Assertions.assertThat(responseData.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(newConvocatoriaFase.getTipoFase().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsConvocatoriaFase() throws Exception {
    Long idConvocatoriaFase = 1L;
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    final ResponseEntity<ConvocatoriaFaseOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, modelMapper.map(convocatoriaFase, ConvocatoriaFaseInput.class)),
        ConvocatoriaFaseOutput.class, idConvocatoriaFase);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaFaseOutput convocatoriaFaseActualizado = response.getBody();
    Assertions.assertThat(convocatoriaFaseActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaFaseActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaFase.getConvocatoriaId());
    Assertions.assertThat(convocatoriaFaseActualizado.getObservaciones()).as("getObservacion()")
        .isEqualTo(convocatoriaFase.getObservaciones());
    Assertions.assertThat(convocatoriaFaseActualizado.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(convocatoriaFase.getTipoFase().getId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    // given: existing ConvocatoriaFase to be deleted
    Long id = 1L;

    // when: delete ConvocatoriaFase
    final ResponseEntity<ConvocatoriaFaseOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ConvocatoriaFaseOutput.class, id);

    // then: ConvocatoriaFase deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsConvocatoriaFase() throws Exception {
    Long idConvocatoriaFase = 1L;

    final ResponseEntity<ConvocatoriaFaseOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ConvocatoriaFaseOutput.class, idConvocatoriaFase);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaFaseOutput convocatoriaFase = response.getBody();
    Assertions.assertThat(convocatoriaFase.getId()).as("getId()").isEqualTo(idConvocatoriaFase);
    Assertions.assertThat(convocatoriaFase.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaFase.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(Instant.parse("2020-10-18T00:00:00Z"));
    Assertions.assertThat(convocatoriaFase.getFechaFin()).as("getFechaFin()")
        .isEqualTo(Instant.parse("2020-11-01T23:59:59Z"));
    Assertions.assertThat(convocatoriaFase.getTipoFase().getId()).as("getTipoFase().getId()").isEqualTo(1L);

  }

  /**
   * Función que devuelve un objeto ConvocatoriaFase
   * 
   * @param id id del ConvocatoriaFase
   * @return el objeto ConvocatoriaFase
   */
  private ConvocatoriaFase generarMockConvocatoriaFase(Long id) {
    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id == null ? 1 : id);
    tipoFase.setActivo(true);

    ConvocatoriaFase convocatoriaFase = new ConvocatoriaFase();
    convocatoriaFase.setId(id);
    convocatoriaFase.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaFase.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    convocatoriaFase.setFechaFin(Instant.parse("2020-10-28T23:59:59Z"));
    convocatoriaFase.setTipoFase(tipoFase);
    convocatoriaFase.setObservaciones("observaciones" + id);

    return convocatoriaFase;
  }

}
