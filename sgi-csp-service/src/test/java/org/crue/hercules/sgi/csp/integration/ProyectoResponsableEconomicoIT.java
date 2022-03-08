package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoResponsableEconomicoController;
import org.crue.hercules.sgi.csp.dto.ProyectoResponsableEconomicoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoResponsableEconomicoOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de ProyectoResponsableEconomico.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoResponsableEconomicoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ProyectoResponsableEconomicoController.MAPPING;
  private static final String PATH_PARAMETER_PROYECTO_ID = "/{proyectoId}";

  private HttpEntity<Object> buildRequest(
      HttpHeaders headers,
      Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
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
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_responsable_economico.sql"
    // @formatter:on 
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnProyectoResponsableEconomicoOutput() throws Exception {
    String roles = "CSP-PRO-E";
    Long proyectoResponsableEconomicoId = 3L;

    final ResponseEntity<ProyectoResponsableEconomicoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles),
        ProyectoResponsableEconomicoOutput.class,
        proyectoResponsableEconomicoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoResponsableEconomicoOutput responseData = response.getBody();

    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(3L);
    Assertions.assertThat(responseData.getPersonaRef()).as("getPersonaRef()").isEqualTo("personaRef-003");

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
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto_responsable_economico.sql"
    // @formatter:on 
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateProyectoResponsablesEconomicos_ReturnsProyectoResponsableEconomicoOutputList() throws Exception {
    String roles = "CSP-PRO-E";
    Long proyectoId = 1L;
    List<ProyectoResponsableEconomicoInput> toUpdate = java.util.Arrays.asList(
        buildMockProyectoResponsableEconomicoInput(1L, "user", Instant.parse("2020-01-12T00:00:00Z"),
            Instant.parse("2021-03-29T23:59:59Z")),
        buildMockProyectoResponsableEconomicoInput(2L, "user", Instant.parse("2021-03-30T23:59:59Z"),
            Instant.parse("2021-05-29T23:59:59Z")),
        buildMockProyectoResponsableEconomicoInput(3L, "user", Instant.parse("2021-05-30T23:59:59Z"),
            Instant.parse("2021-12-31T23:59:59Z")));

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_PROYECTO_ID)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoResponsableEconomicoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles),
        new ParameterizedTypeReference<List<ProyectoResponsableEconomicoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<ProyectoResponsableEconomicoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.size()).isEqualTo(3);

    List<ProyectoResponsableEconomicoOutput> responseDataSorted = responseData.stream()
        .sorted(new Comparator<ProyectoResponsableEconomicoOutput>() {

          @Override
          public int compare(ProyectoResponsableEconomicoOutput o1, ProyectoResponsableEconomicoOutput o2) {

            return o1.getId().compareTo(o2.getId());
          }
        }).collect(Collectors.toList());

    Assertions.assertThat(responseDataSorted.get(0)).isNotNull();
    Assertions.assertThat(responseDataSorted.get(1)).isNotNull();
    Assertions.assertThat(responseDataSorted.get(2)).isNotNull();

    Assertions.assertThat(responseDataSorted.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseDataSorted.get(1).getId()).isEqualTo(2);
    Assertions.assertThat(responseDataSorted.get(2).getId()).isEqualTo(3);

    Assertions.assertThat(responseDataSorted.get(0).getPersonaRef()).isEqualTo(toUpdate.get(0).getPersonaRef());
    Assertions.assertThat(responseDataSorted.get(1).getPersonaRef()).isEqualTo(toUpdate.get(1).getPersonaRef());
    Assertions.assertThat(responseDataSorted.get(2).getPersonaRef()).isEqualTo(toUpdate.get(2).getPersonaRef());

    Assertions.assertThat(responseDataSorted.get(0).getFechaInicio()).isEqualTo(toUpdate.get(0).getFechaInicio());
    Assertions.assertThat(responseDataSorted.get(1).getFechaInicio()).isEqualTo(toUpdate.get(1).getFechaInicio());
    Assertions.assertThat(responseDataSorted.get(2).getFechaInicio()).isEqualTo(toUpdate.get(2).getFechaInicio());

    Assertions.assertThat(responseDataSorted.get(0).getFechaFin()).isEqualTo(toUpdate.get(0).getFechaFin());
    Assertions.assertThat(responseDataSorted.get(1).getFechaFin()).isEqualTo(toUpdate.get(1).getFechaFin());
    Assertions.assertThat(responseDataSorted.get(2).getFechaFin()).isEqualTo(toUpdate.get(2).getFechaFin());

    Assertions.assertThat(responseDataSorted.get(0).getProyectoId()).isEqualTo(proyectoId);
    Assertions.assertThat(responseDataSorted.get(1).getProyectoId()).isEqualTo(proyectoId);
    Assertions.assertThat(responseDataSorted.get(2).getProyectoId()).isEqualTo(proyectoId);
  }

  ProyectoResponsableEconomicoInput buildMockProyectoResponsableEconomicoInput(Long id, String personaRef,
      Instant fechaInicio, Instant fechaFin) {
    return ProyectoResponsableEconomicoInput.builder()
        .id(id)
        .personaRef(personaRef)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .build();
  }

}
