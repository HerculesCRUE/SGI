package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.controller.TipoProteccionController;
import org.crue.hercules.sgi.pii.dto.TipoProteccionInput;
import org.crue.hercules.sgi.pii.dto.TipoProteccionOutput;
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
 * Test de integracion de TipoProteccion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoProteccionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = TipoProteccionController.MAPPING;
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_SUBTIPOS = "/subtipos";
  private static final String PATH_ACTIVAR = "/activar";
  private static final String PATH_DESACTIVAR = "/desactivar";

  private HttpEntity<TipoProteccionInput> buildRequest(HttpHeaders headers,
      TipoProteccionInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<TipoProteccionInput> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findActivos_WithPagingSortingAndFiltering_ReturnsTipoProteccionSubList() throws Exception {
    String[] roles = { "PII-TPR-V", "PII-TPR-C", "PII-TPR-E", "PII-TPR-B", "PII-TPR-R", "PII-INV-V", "PII-INV-C",
        "PII-INV-E", "PII-INV-B", "PII-INV-R", "PII-INV-MOD-V" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<TipoProteccionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoProteccionOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoProteccionOutput> tipoProteccionOutput = response.getBody();
    Assertions.assertThat(tipoProteccionOutput.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    Assertions.assertThat(tipoProteccionOutput.get(0).getNombre()).isEqualTo(
        "nombre-tipo-proteccion-" + String.format("%03d", 3));
    Assertions.assertThat(tipoProteccionOutput.get(1).getNombre()).isEqualTo(
        "nombre-tipo-proteccion-" + String.format("%03d", 2));
    Assertions.assertThat(tipoProteccionOutput.get(2).getNombre()).isEqualTo(
        "nombre-tipo-proteccion-" + String.format("%03d", 1));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
    // @formatter:off
   })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoProteccionSubList() throws Exception {
    String[] roles = {"PII-TPR-V", "PII-TPR-C", "PII-TPR-E", "PII-TPR-B", "PII-TPR-R"};   
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<TipoProteccionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoProteccionOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoProteccionOutput> tipoProteccionOutPut = response.getBody();
    Assertions.assertThat(tipoProteccionOutPut.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("4");

    Assertions.assertThat(tipoProteccionOutPut.get(0).getNombre()).as("0.nombre")
        .isEqualTo("nombre-tipo-proteccion-" + String.format("%03d", 6));
    Assertions.assertThat(tipoProteccionOutPut.get(1).getNombre()).as("1.nombre")
        .isEqualTo("nombre-tipo-proteccion-" + String.format("%03d", 3));
    Assertions.assertThat(tipoProteccionOutPut.get(2).getNombre()).as("2.nombre")
        .isEqualTo("nombre-tipo-proteccion-" + String.format("%03d", 2));

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
    // @formatter:off
   })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findSubtiposProteccion_WithPagingSortingAndFiltering_ReturnsTipoProteccionSubList() throws Exception {
    String[] roles = {"PII-TPR-V", "PII-TPR-C", "PII-TPR-E", "PII-TPR-B", "PII-TPR-R", "PII-INV-V", "PII-INV-C", "PII-INV-E"};   
    Long idTipoProteccion = 3L;

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    // when: find SubtiposProteccion
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SUBTIPOS).queryParam("s", sort).queryParam("q", filter)
        .buildAndExpand(idTipoProteccion).toUri();

    final ResponseEntity<List<TipoProteccionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoProteccionOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoProteccionOutput> tipoProteccionOutput = response.getBody();
    Assertions.assertThat(tipoProteccionOutput.size()).isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(tipoProteccionOutput.get(0).getId()).as("get(0).getId())")
        .isEqualTo(5);
    Assertions.assertThat(tipoProteccionOutput.get(1).getId()).as("get(1).getId())")
        .isEqualTo(4);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
    // @formatter:off
   })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllSubtiposProteccion_WithPagingSortingAndFiltering_ReturnsTipoProteccionSubList() throws Exception {
    String[] roles = {"PII-TPR-V", "PII-TPR-C", "PII-TPR-E", "PII-TPR-B", "PII-TPR-R", "PII-INV-V", "PII-INV-C", "PII-INV-E"};   
    Long idTipoProteccion = 3L;

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    // when: find SubtiposProteccion
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SUBTIPOS + PATH_TODOS).queryParam("s", sort).queryParam("q", filter)
        .buildAndExpand(idTipoProteccion).toUri();

    final ResponseEntity<List<TipoProteccionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoProteccionOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoProteccionOutput> tipoProteccionOutput = response.getBody();
    Assertions.assertThat(tipoProteccionOutput.size()).isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(tipoProteccionOutput.get(0).getId()).as("get(0).getId())")
        .isEqualTo(5);
    Assertions.assertThat(tipoProteccionOutput.get(1).getId()).as("get(1).getId())")
        .isEqualTo(4);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
    // @formatter:off
   })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsTipoProteccion() throws Exception {
    String[] roles = {"PII-TPR-V", "PII-TPR-C", "PII-TPR-E", "PII-TPR-B", "PII-TPR-R"};   
    Long tipoProteccionId = 1L;

    final ResponseEntity<TipoProteccionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null, roles), TipoProteccionOutput.class, tipoProteccionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoProteccionOutput tipoProteccionOutput = response.getBody();

    Assertions.assertThat(tipoProteccionOutput).isNotNull();
    Assertions.assertThat(tipoProteccionOutput.getId()).as("id")
        .isEqualTo(1);
    Assertions.assertThat(tipoProteccionOutput.getNombre()).as("nombre")
        .isEqualTo("nombre-tipo-proteccion-001");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
    // @formatter:off
   })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void activar_ReturnsTipoProteccionActivo() throws Exception {
    String[] roles = {"PII-TPR-R"};   
    Long tipoProteccionId = 6L;

    final ResponseEntity<TipoProteccionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null, roles), TipoProteccionOutput.class, tipoProteccionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoProteccionOutput tipoProteccionOutput = response.getBody();

    Assertions.assertThat(tipoProteccionOutput).isNotNull();
    Assertions.assertThat(tipoProteccionOutput.getId()).as("id")
        .isEqualTo(6);
    Assertions.assertThat(tipoProteccionOutput.getNombre()).as("nombre")
        .isEqualTo("nombre-tipo-proteccion-006");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
    // @formatter:off
   })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnsTipoProteccionInactivo() throws Exception {
    String[] roles = {"PII-TPR-B"};   
    Long tipoProteccionId = 1L;

    final ResponseEntity<TipoProteccionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null, roles), TipoProteccionOutput.class, tipoProteccionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoProteccionOutput tipoProteccionOutput = response.getBody();

    Assertions.assertThat(tipoProteccionOutput).isNotNull();
    Assertions.assertThat(tipoProteccionOutput.getId()).as("id")
        .isEqualTo(1);
    Assertions.assertThat(tipoProteccionOutput.getNombre()).as("nombre")
        .isEqualTo("nombre-tipo-proteccion-001");

  }

}
