package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.AnualidadGastoController;
import org.crue.hercules.sgi.csp.dto.AnualidadGastoInput;
import org.crue.hercules.sgi.csp.dto.AnualidadGastoInput.ConceptoGasto;
import org.crue.hercules.sgi.csp.dto.AnualidadGastoOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AnualidadGastoIT extends BaseIT {

  private static final String PATH_PARAMETER_PROYECTO_ANUALIDAD_ID = "/{proyectoAnualidadId}";
  private static final String CONTROLLER_BASE_PATH = AnualidadGastoController.MAPPING;

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      List<AnualidadGastoInput> entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);

    return request;
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_anualidad.sql",
    "classpath:scripts/proyecto_partida.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsAnualidadGastoOutputList() throws Exception {
    Long proyectoAnualidadId = 1L;

    List<AnualidadGastoInput> toUpdateList = Arrays.asList(buildMockAnualidadGastoInput(1L, "AA"),
        buildMockAnualidadGastoInput(2L, "AR"));

    final ResponseEntity<List<AnualidadGastoOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_PROYECTO_ANUALIDAD_ID,
        HttpMethod.PATCH, buildRequest(null,
            toUpdateList, "CSP-PRO-E"),
        new ParameterizedTypeReference<List<AnualidadGastoOutput>>() {
        }, proyectoAnualidadId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    List<AnualidadGastoOutput> anualidadesGastosUpdated = response.getBody();

    Assertions.assertThat(anualidadesGastosUpdated.size()).isEqualTo(toUpdateList.size());

    Assertions.assertThat(anualidadesGastosUpdated.get(0).getCodigoEconomicoRef()).as("get(0).getCodigoEconomicoRef()")
        .isEqualTo(toUpdateList.get(0).getCodigoEconomicoRef());
    Assertions.assertThat(anualidadesGastosUpdated.get(1).getCodigoEconomicoRef()).as("get(1).getCodigoEconomicoRef()")
        .isEqualTo(toUpdateList.get(1).getCodigoEconomicoRef());

  }

  private AnualidadGastoInput buildMockAnualidadGastoInput(Long id, String codEconomicoRef) {
    return AnualidadGastoInput.builder()
        .id(id)
        .codigoEconomicoRef(codEconomicoRef)
        .importeConcedido(BigDecimal.valueOf(10333))
        .importePresupuesto(BigDecimal.valueOf(11666))
        .proyectoSgeRef("33939")
        .anualidadId(1L)
        .conceptoGasto(ConceptoGasto.builder()
            .id(1L)
            .build())
        .proyectoPartida(AnualidadGastoInput.ProyectoPartida.builder()
            .id(1L)
            .build())
        .build();
  }

}