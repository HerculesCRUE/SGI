package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GrupoController;
import org.crue.hercules.sgi.csp.dto.GrupoDto;
import org.crue.hercules.sgi.csp.dto.GrupoInput;
import org.crue.hercules.sgi.csp.dto.GrupoOutput;
import org.crue.hercules.sgi.csp.dto.GrupoPalabraClaveInput;
import org.crue.hercules.sgi.csp.dto.GrupoPalabraClaveOutput;
import org.crue.hercules.sgi.csp.model.GrupoTipo.Tipo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
 * Test de integracion de Grupo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GrupoIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = GrupoController.REQUEST_MAPPING;
  private static final String PATH_BAREMABLES_ANIO = GrupoController.PATH_BAREMABLES_ANIO;
  private static final String PATH_GRUPO_BAREMABLE_GRUPO_REF_ANIO = GrupoController.PATH_GRUPO_BAREMABLE_GRUPO_REF_ANIO;
  private static final String PATH_MODIFICADOS_IDS = GrupoController.PATH_MODIFICADOS_IDS;
  private static final String PATH_PALABRAS_CLAVE = GrupoController.PATH_PALABRAS_CLAVE;
  private static final String PATH_TODOS = GrupoController.PATH_TODOS;
  private static final String PATH_ID = GrupoController.PATH_ID;

  private static final String DEFAULT_CODIGO = "COD";
  private static final String DEFAULT_NOMBRE = "nombre";
  private static final Tipo DEFAULT_TIPO = Tipo.ALTO_RENDIMIENTO;
  private static final boolean DEFAULT_ESPECIAL_INVESTIGACION = true;
  private static final Instant DEFAULT_FECHA_INICIO = Instant.now();
  private static final Instant DEFAULT_FECHA_FIN = Instant
      .from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)));

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
      "classpath:scripts/rol_proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsGrupo() throws Exception {
    GrupoInput toCreate = buildMockGrupo(null);
    String roles = "CSP-GIN-C";

    final ResponseEntity<GrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, toCreate, roles), GrupoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    GrupoOutput created = response.getBody();
    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(created.getCodigo()).as("getCodigo()")
        .isEqualTo(toCreate.getCodigo());
    Assertions.assertThat(created.getNombre()).as("getNombre()")
        .isEqualTo(toCreate.getNombre());
    Assertions.assertThat(created.getTipo()).as("getTipo()")
        .isEqualTo(toCreate.getTipo());
    Assertions.assertThat(created.getEspecialInvestigacion()).as("getEspecialInvestigacion()")
        .isEqualTo(toCreate.getEspecialInvestigacion());
    Assertions.assertThat(created.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(toCreate.getFechaInicio());
    Assertions.assertThat(created.getFechaFin()).as("getFechaFin()")
        .isEqualTo(toCreate.getFechaFin());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsGrupo() throws Exception {
    String roles = "CSP-GIN-E";
    Long grupoId = 1L;
    GrupoInput toUpdate = buildMockGrupo(1L);
    toUpdate.setNombre("actualizado");
    toUpdate.setTipo(Tipo.PRECOMPETITIVO);

    final ResponseEntity<GrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, toUpdate, roles), GrupoOutput.class, grupoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    GrupoOutput updated = response.getBody();

    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(grupoId);
    Assertions.assertThat(updated.getCodigo()).as("getCodigo()")
        .isEqualTo(toUpdate.getCodigo());
    Assertions.assertThat(updated.getNombre()).as("getNombre()")
        .isEqualTo(toUpdate.getNombre());
    Assertions.assertThat(updated.getTipo()).as("getTipo()")
        .isEqualTo(toUpdate.getTipo());
    Assertions.assertThat(updated.getEspecialInvestigacion()).as("getEspecialInvestigacion()")
        .isEqualTo(toUpdate.getEspecialInvestigacion());
    Assertions.assertThat(updated.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(toUpdate.getFechaInicio());
    Assertions.assertThat(updated.getFechaFin()).as("getFechaFin()")
        .isEqualTo(toUpdate.getFechaFin());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsAutorizacion() throws Exception {
    String roles = "CSP-GIN-E";
    Long grupoId = 1L;

    final ResponseEntity<GrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.GET, buildRequest(null, null, roles), GrupoOutput.class, grupoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    GrupoOutput autorizacion = response.getBody();

    Assertions.assertThat(autorizacion.getId()).as("getId()").isEqualTo(grupoId);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsById_Returns200() throws Exception {
    // given: existing id
    String roles = "CSP-GIN-E";
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<GrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.HEAD, buildRequest(null, null, roles), GrupoOutput.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsById_Returns204() throws Exception {
    // given: no existing id
    String roles = "CSP-GIN-E";
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<GrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.HEAD, buildRequest(null, null, roles), GrupoOutput.class, id);
    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsGrupoSubList() throws Exception {
    String[] roles = { "CSP-GIN-PRC-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "id<=3";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<GrupoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<GrupoOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<GrupoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(2);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(responseData.get(0)).as("get(0)").isNotNull();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()").isEqualTo(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodos_WithPagingSortingAndFiltering_ReturnsGrupoSubList() throws Exception {
    String[] roles = { "CSP-GIN-E" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "id<=3";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<GrupoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<GrupoOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<GrupoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(2);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(responseData.get(0)).as("get(0)").isNotNull();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()").isEqualTo(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_equipo.sql", 
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2, 2021" })
  void is_grupo_baremable_ok(Long grupoRef, Integer anio) throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_GRUPO_BAREMABLE_GRUPO_REF_ANIO,
        HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class, grupoRef, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_equipo.sql", 
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2, 2023" })
  void is_grupo_baremable_ko(Long grupoRef, Integer anio) throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_GRUPO_BAREMABLE_GRUPO_REF_ANIO,
        HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class, grupoRef, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_equipo.sql", 
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2021" })
  void findAllByAnio_ok(Integer anio)
      throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<List<GrupoDto>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMABLES_ANIO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<GrupoDto>>() {
        }, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<GrupoDto> grupos = response.getBody();
    int numElements = grupos.size();
    Assertions.assertThat(numElements).as("numGrupos").isEqualTo(1);

    Assertions.assertThat(grupos.get(0).getId()).as("get(0).getId())").isEqualTo(2L);
    Assertions.assertThat(grupos.get(0).getNombre()).as("get(0).getNombre())").isEqualTo("Grupo investigación 2");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_equipo.sql", 
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2023" })
  void findAllByAnio_ko(Integer anio)
      throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<List<GrupoDto>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMABLES_ANIO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<GrupoDto>>() {
        }, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_equipo.sql", 
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findIdsGruposModificados() throws Exception {
    String roles = "CSP-GIN-V";
    String filter = "fechaModificacion=ge=2021-08-18T22:00:00Z";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_MODIFICADOS_IDS).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<Long>> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<Long>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Long> gruposIds = response.getBody();
    int numElements = gruposIds.size();
    Assertions.assertThat(numElements).as("numGrupos").isEqualTo(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_palabra_clave.sql", 
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findPalabraClave_WithPagingSortingAndFiltering_ReturnsProyectoPalabraClaveOutputSubList()
      throws Exception {
    String[] roles = { "CSP-GIN-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "id>=1;id<=3";

    Long grupoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PALABRAS_CLAVE)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(grupoId).toUri();

    final ResponseEntity<List<GrupoPalabraClaveOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<GrupoPalabraClaveOutput>>() {
        });

    final String expectedSize = "3";
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo(expectedSize);

    Assertions.assertThat(response.getBody()).isNotNull();

    final List<GrupoPalabraClaveOutput> responseData = response.getBody().stream()
        .sorted(new Comparator<GrupoPalabraClaveOutput>() {
          @Override
          public int compare(GrupoPalabraClaveOutput o1, GrupoPalabraClaveOutput o2) {

            return o1.getId().compareTo(o2.getId());
          }
        })
        .collect(Collectors.toList());
    Assertions.assertThat(responseData).hasSize(Integer.valueOf(expectedSize));

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_palabra_clave.sql", 
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updatePalabrasClave_ReturnsGrupoPalabraClaveOutputList() throws Exception {
    String[] roles = { "CSP-GIN-E" };

    Long grupoId = 1L;
    List<GrupoPalabraClaveInput> toUpdate = Arrays.asList(
        buildMockGrupoPalabraClaveInput(1L, "updated-01"),
        buildMockGrupoPalabraClaveInput(2L, "updated-02"));

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PALABRAS_CLAVE)
        .buildAndExpand(grupoId).toUri();

    final ResponseEntity<List<GrupoPalabraClaveOutput>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), new ParameterizedTypeReference<List<GrupoPalabraClaveOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    final List<GrupoPalabraClaveOutput> responseData = response.getBody().stream()
        .sorted(new Comparator<GrupoPalabraClaveOutput>() {
          @Override
          public int compare(GrupoPalabraClaveOutput o1, GrupoPalabraClaveOutput o2) {

            return o1.getId().compareTo(o2.getId());
          }
        })
        .collect(Collectors.toList());
    Assertions.assertThat(responseData).hasSize(Integer.valueOf(2));

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2);

    Assertions.assertThat(responseData.get(0).getPalabraClaveRef()).isEqualTo("updated-01");
    Assertions.assertThat(responseData.get(1).getPalabraClaveRef()).isEqualTo("updated-02");
  }

  private GrupoPalabraClaveInput buildMockGrupoPalabraClaveInput(Long id, String palabraClaveRef) {
    return GrupoPalabraClaveInput.builder()
        .palabraClaveRef(palabraClaveRef)
        .build();
  }

  private GrupoInput buildMockGrupo(Long id) {
    return GrupoInput.builder()
        .codigo(DEFAULT_CODIGO)
        .especialInvestigacion(DEFAULT_ESPECIAL_INVESTIGACION)
        .fechaInicio(DEFAULT_FECHA_INICIO)
        .fechaFin(DEFAULT_FECHA_FIN)
        .nombre(DEFAULT_NOMBRE)
        .tipo(DEFAULT_TIPO)
        .build();
  }

}
