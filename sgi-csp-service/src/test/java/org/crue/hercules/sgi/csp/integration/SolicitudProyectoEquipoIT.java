package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de SolicitudProyectoEquipoIT.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudProyectoEquipoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoequipo";
  private static final String PATH_PARAMETER_SOLICITUD_PROYECTO_ID = "/{solicitudProyectoId}";

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E", "AUTH")));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsSolicitudProyectoEquipo() throws Exception {
    Long idSolicitudProyectoEquipo = 1L;

    final ResponseEntity<SolicitudProyectoEquipo> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoEquipo.class, idSolicitudProyectoEquipo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoEquipo solicitudProyectoEquipo = response.getBody();
    Assertions.assertThat(solicitudProyectoEquipo.getId()).as("getId()").isEqualTo(idSolicitudProyectoEquipo);
    Assertions.assertThat(solicitudProyectoEquipo.getSolicitudProyectoId()).as("getSolicitudProyectoId()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoEquipo.getPersonaRef()).as("getPersonaRef()").isEqualTo("personaRef-001");
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/rol_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_equipo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateMiembrosEquipo_ReturnsSolicitudProyectoEquipoList() throws Exception {
    Long solicitudProyectoId = 1L;

    List<SolicitudProyectoEquipo> toUpdateList = Arrays.asList(
        this.buildMockSolicitudProyectoEquipo(1L, 1L, 1, 20, "01889311", 2L, false),
        this.buildMockSolicitudProyectoEquipo(2L, 1L, 1, 22, "01925489", 2L, false),
        this.buildMockSolicitudProyectoEquipo(3L, 1L, 1, 21, "usr-002", 1L, true));

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_SOLICITUD_PROYECTO_ID)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<List<SolicitudProyectoEquipo>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH, buildRequest(null, toUpdateList),
        new ParameterizedTypeReference<List<SolicitudProyectoEquipo>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    List<SolicitudProyectoEquipo> responseData = response.getBody();

    Assertions.assertThat(responseData).hasSize(3);

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getMesFin()).isEqualTo(20);
    Assertions.assertThat(responseData.get(1).getMesFin()).isEqualTo(22);
    Assertions.assertThat(responseData.get(2).getMesFin()).isEqualTo(21);

    Assertions.assertThat(responseData.get(0).getRolProyecto().getId()).isEqualTo(2L);
    Assertions.assertThat(responseData.get(1).getRolProyecto().getId()).isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getRolProyecto().getId()).isEqualTo(1L);
  }

  private SolicitudProyectoEquipo buildMockSolicitudProyectoEquipo(Long id, Long solicitudProyectoId, int mesInicio,
      int mesFin, String personaRef, Long idRol, Boolean rolPrincipal) {
    return SolicitudProyectoEquipo.builder()
        .id(id)
        .solicitudProyectoId(solicitudProyectoId)
        .mesFin(mesFin)
        .mesInicio(mesInicio)
        .personaRef(personaRef)
        .rolProyecto(RolProyecto.builder()
            .id(idRol)
            .rolPrincipal(rolPrincipal)
            .build())
        .build();
  }

}
