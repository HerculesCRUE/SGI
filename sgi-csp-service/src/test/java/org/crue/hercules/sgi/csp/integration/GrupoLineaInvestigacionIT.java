package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GrupoLineaInvestigacionController;
import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion;
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

public class GrupoLineaInvestigacionIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = GrupoLineaInvestigacionController.REQUEST_MAPPING;
  private static final String PATH_CLASIFICACION = GrupoLineaInvestigacionController.PATH_CLASIFICACIONES;

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  /**
   * 
   * GRUPO LINEA CLASIFICACIONES
   * 
   */
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/linea_investigacion.sql",
      "classpath:scripts/grupo_linea_investigacion.sql",
      "classpath:scripts/grupo_linea_clasificacion.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllGrupoLineaClasificaciones_WithPagingSortingAndFiltering_ReturnsGrupoLineaClasificacionesSubList()
      throws Exception {
    String roles = "CSP-GIN-E";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "clasificacionRef=ke=c";

    Long grupoLineaInvestigacionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_CLASIFICACION)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(grupoLineaInvestigacionId).toUri();

    final ResponseEntity<List<GrupoLineaClasificacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<GrupoLineaClasificacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<GrupoLineaClasificacion> grupoLineaClasificacion = response.getBody();
    Assertions.assertThat(grupoLineaClasificacion.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(grupoLineaClasificacion.get(0).getClasificacionRef()).as("get(0).getClasificacionRef()")
        .isEqualTo("c3");
    Assertions.assertThat(grupoLineaClasificacion.get(1).getClasificacionRef()).as("get(1).getClasificacionRef()")
        .isEqualTo("c2");
    Assertions.assertThat(grupoLineaClasificacion.get(2).getClasificacionRef()).as("get(2).getClasificacionRef()")
        .isEqualTo("c1");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/linea_investigacion.sql",
      "classpath:scripts/grupo_linea_investigacion.sql",
      "classpath:scripts/grupo_linea_clasificacion.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllGrupoLineaClasificaciones_ReturnsEmptySubList()
      throws Exception {
    String roles = "CSP-GIN-E";
    Long grupoLineaInvestigacionId = 3L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_CLASIFICACION)
        .buildAndExpand(grupoLineaInvestigacionId).toUri();

    final ResponseEntity<List<GrupoLineaClasificacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<GrupoLineaClasificacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

}
