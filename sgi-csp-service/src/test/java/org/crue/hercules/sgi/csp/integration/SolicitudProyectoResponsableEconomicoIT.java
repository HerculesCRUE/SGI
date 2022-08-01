package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.SolicitudProyectoResponsableEconomicoController;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoResponsableEconomicoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoResponsableEconomicoOutput;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudProyectoResponsableEconomicoIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = SolicitudProyectoResponsableEconomicoController.MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_SOLICITUD_PROYECTO_ID = "/{solicitudProyectoId}";

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_responsable_economico.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsSolicitudProyectoResponsableEconomicoOutput() throws Exception {
    Long toFindId = 1L;
    String roles = "CSP-SOL-E";

    final ResponseEntity<SolicitudProyectoResponsableEconomicoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), SolicitudProyectoResponsableEconomicoOutput.class, toFindId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoResponsableEconomicoOutput founded = response.getBody();
    Assertions.assertThat(founded.getId()).as("getId()").isEqualTo(toFindId);
    Assertions.assertThat(founded.getMesFin()).as("getMesFin()").isEqualTo(1);
    Assertions.assertThat(founded.getMesInicio()).as("getMesInicio()").isEqualTo(3);
    Assertions.assertThat(founded.getPersonaRef()).as("getPersonaRef()").isEqualTo("00001");
    Assertions.assertThat(founded.getSolicitudProyectoId()).as("getSolicitudProyectoId()").isEqualTo(1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_responsable_economico.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateSolicituProyectoResponsablesEconomicos_ReturnsSolicitudProyectoResponsableEconomicoOutputList()
      throws Exception {
    Long solicitudId = 1L;
    List<SolicitudProyectoResponsableEconomicoInput> toUpdate = Arrays.asList(
        buildMockConvocatoriaPalabraClaveInput(1l, "00005", 3, 1),
        buildMockConvocatoriaPalabraClaveInput(2l, "00006", 6, 4),
        buildMockConvocatoriaPalabraClaveInput(3l, "00007", 9, 8));

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_SOLICITUD_PROYECTO_ID)
        .buildAndExpand(solicitudId).toUri();

    final ResponseEntity<List<SolicitudProyectoResponsableEconomicoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH,
        buildRequest(null, toUpdate, "CSP-SOL-E"),
        new ParameterizedTypeReference<List<SolicitudProyectoResponsableEconomicoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<SolicitudProyectoResponsableEconomicoOutput> updated = response.getBody();
    Assertions.assertThat(updated).hasSize(3);

    Assertions.assertThat(updated.get(0)).isNotNull();
    Assertions.assertThat(updated.get(1)).isNotNull();
    Assertions.assertThat(updated.get(2)).isNotNull();

    Assertions.assertThat(updated.get(0).getPersonaRef()).isEqualTo("00005");
    Assertions.assertThat(updated.get(1).getPersonaRef()).isEqualTo("00006");
    Assertions.assertThat(updated.get(2).getPersonaRef()).isEqualTo("00007");
  }

  private SolicitudProyectoResponsableEconomicoInput buildMockConvocatoriaPalabraClaveInput(Long id,
      String personaRef, int mesFin, int mesInicio) {
    return SolicitudProyectoResponsableEconomicoInput.builder()
        .id(id)
        .mesInicio(mesInicio)
        .mesFin(mesFin)
        .personaRef(personaRef)
        .build();
  }
}