package org.crue.hercules.sgi.prc.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.CampoProduccionCientificaController;
import org.crue.hercules.sgi.prc.dto.CampoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.CampoProduccionCientificaOutput;
import org.crue.hercules.sgi.prc.dto.ValorCampoOutput;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
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
 * Test de integracion de CampoProduccionCientifica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CampoProduccionCientificaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = CampoProduccionCientificaController.MAPPING;
  private static final String PATH_VALORES = CampoProduccionCientificaController.PATH_VALORES;

  private HttpEntity<CampoProduccionCientificaInput> buildRequest(HttpHeaders headers,
      CampoProduccionCientificaInput entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", roles)));

    HttpEntity<CampoProduccionCientificaInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsCampoProduccionCientifica() throws Exception {
    CampoProduccionCientificaInput campoProduccionCientifica = generarMockCampoProduccionCientificaInput();

    final ResponseEntity<CampoProduccionCientificaOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, campoProduccionCientifica), CampoProduccionCientificaOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    CampoProduccionCientificaOutput campoProduccionCientificaCreado = response.getBody();
    Assertions.assertThat(campoProduccionCientificaCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(campoProduccionCientifica.getCodigo()).as("getCodigo()")
        .isEqualTo(CodigoCVN.E060_010_010_010.getInternValue());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsUniqueException() throws Exception {
    CampoProduccionCientificaInput campoProduccionCientifica = generarMockCampoProduccionCientificaInput();
    campoProduccionCientifica.setCodigoCVN(CodigoCVN.E060_010_010_030);

    final ResponseEntity<CampoProduccionCientificaOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, campoProduccionCientifica),
        CampoProduccionCientificaOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsCampoProduccionCientifica() throws Exception {
    CampoProduccionCientificaInput campoProduccionCientifica = generarMockCampoProduccionCientificaInput();
    campoProduccionCientifica.setProduccionCientificaId(1L);
    campoProduccionCientifica.setCodigo(CodigoCVN.E060_010_010_070.getInternValue());

    final ResponseEntity<CampoProduccionCientificaOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, campoProduccionCientifica),
        CampoProduccionCientificaOutput.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    CampoProduccionCientificaOutput campoProduccionCientificaActualizado = response.getBody();
    Assertions.assertThat(campoProduccionCientificaActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(campoProduccionCientificaActualizado.getCodigo()).as("getCodigo()")
        .isEqualTo(CodigoCVN.E060_010_010_070.getInternValue());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Return204() throws Exception {
    Long toDeleteId = 1L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsCampoProduccionCientifica() throws Exception {
    Long idCampoProduccionCientifica = 1L;

    final ResponseEntity<CampoProduccionCientificaOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        CampoProduccionCientificaOutput.class, idCampoProduccionCientifica);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    CampoProduccionCientificaOutput campoProduccionCientifica = response.getBody();
    Assertions.assertThat(campoProduccionCientifica.getId()).as("getId()").isNotNull();
    Assertions.assertThat(campoProduccionCientifica.getCodigo()).as("getCodigo()")
        .isEqualTo(CodigoCVN.E060_010_010_030.getInternValue());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsCampoProduccionCientificaSubList() throws Exception {
    String[] roles = { "PRC-VAL-V" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<CampoProduccionCientificaOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<CampoProduccionCientificaOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<CampoProduccionCientificaOutput> campoProduccionesCientificas = response.getBody();
    Assertions.assertThat(campoProduccionesCientificas).hasSize(10);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");

    Assertions.assertThat(campoProduccionesCientificas.get(0).getId()).as("get(0).getId())")
        .isEqualTo(1L);
    Assertions.assertThat(campoProduccionesCientificas.get(1).getId()).as("get(1).getId())")
        .isEqualTo(2L);
    Assertions.assertThat(campoProduccionesCientificas.get(2).getId()).as("get(2).getId())")
        .isEqualTo(3L);
  }

  private CampoProduccionCientificaInput generarMockCampoProduccionCientificaInput() {
    CampoProduccionCientificaInput campoProduccionCientifica = new CampoProduccionCientificaInput();
    campoProduccionCientifica.setCodigo(CodigoCVN.E060_010_010_010.getInternValue());
    campoProduccionCientifica.setProduccionCientificaId(1L);
    return campoProduccionCientifica;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findValores_WithPagingAndSorting_ReturnsValorCampoOutputSubList()
      throws Exception {
    String[] roles = { "PRC-VAL-V" };
    final Long campoProduccionCientificaId = 1L;
    // first page, 3 elements per page sorted
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";

    // when: find ValorCampo
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_VALORES)
        .queryParam("s", sort)
        .buildAndExpand(campoProduccionCientificaId).toUri();

    final ResponseEntity<List<ValorCampoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<ValorCampoOutput>>() {
        });

    // given: ValorCampo data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ValorCampoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(responseData.get(0)).isNotNull();

    Assertions.assertThat(responseData.get(0).getValor())
        .as("get(0).getValor()")
        .isEqualTo("Título de la publicación1");
  }

}
