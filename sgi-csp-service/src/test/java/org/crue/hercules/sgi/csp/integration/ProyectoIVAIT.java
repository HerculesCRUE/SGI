package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoIVA;
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
 * Test de integracion de ProyectoIVA.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoIVAIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoiva";
  private static final String PATH_PARAMETER_HISTORICO = "/historico";

  private HttpEntity<ProyectoIVA> buildRequest(HttpHeaders headers,
      ProyectoIVA entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ProyectoIVA> request = new HttpEntity<>(entity, headers);

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
    "classpath:scripts/proyecto_iva.sql"
    // @formatter:on 
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoIva() throws Exception {

    String roles = "CSP-PRO-E";
    // given: new ProyectoIVA
    ProyectoIVA proyectoIVA = generarMockProyectoIVA(1L);
    proyectoIVA.setId(null);

    // when: create ProyectoIVA
    final ResponseEntity<ProyectoIVA> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, proyectoIVA, roles), ProyectoIVA.class);

    // then: new ProyectoIVA is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoIVA responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getIva()).as("getIva()")
        .isEqualTo(proyectoIVA.getIva());
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoIVA.getProyectoId());
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
    "classpath:scripts/proyecto_iva.sql"
    // @formatter:on 
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllByProyectoId_WithPagingSorting_ReturnsProyectoIVASubList()
      throws Exception {
    String roles = "CSP-PRO-E";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_HISTORICO)
        .queryParam("s", sort).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoIVA>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoIVA>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoIVA> proyectosIVA = response.getBody();
    Assertions.assertThat(proyectosIVA).hasSize(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");
    Assertions.assertThat(proyectosIVA.get(0).getIva()).as("get(0).getIva()")
        .isEqualTo(5);
    Assertions.assertThat(proyectosIVA.get(1).getIva()).as("get(1).getIva()")
        .isEqualTo(3);

  }

  /**
   * Función que devuelve un objeto ProyectoIVA
   * 
   * @param id id del ProyectoIVA
   * @return el objeto ProyectoIVA
   */
  private ProyectoIVA generarMockProyectoIVA(Long id) {
    ProyectoIVA proyectoIVA = new ProyectoIVA();
    proyectoIVA.setId(id);
    proyectoIVA.setProyectoId(1L);
    proyectoIVA.setIva(3);
    proyectoIVA.setFechaInicio(Instant.parse("2020-01-12T00:00:00Z"));
    proyectoIVA.setFechaFin(Instant.parse("2021-12-31T23:59:59Z"));

    return proyectoIVA;
  }

}
