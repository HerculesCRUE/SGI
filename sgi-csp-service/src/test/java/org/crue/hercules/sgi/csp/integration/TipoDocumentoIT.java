package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
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
 * Test de integracion de TipoDocumento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoDocumentoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String TIPO_DOCUMENTO_CONTROLLER_BASE_PATH = "/tipodocumentos";

  private HttpEntity<TipoDocumento> buildRequest(HttpHeaders headers, TipoDocumento entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-PRO-E",
        "CSP-TDOC-V", "CSP-TDOC-C", "CSP-TDOC-E", "CSP-TDOC-B", "CSP-TDOC-R", "CSP-ME-C", "CSP-ME-E")));

    HttpEntity<TipoDocumento> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsTipoDocumento() throws Exception {

    TipoDocumento tipoDocumento = generarMockTipoDocumento(null);

    final ResponseEntity<TipoDocumento> response = restTemplate.exchange(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, tipoDocumento), TipoDocumento.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    TipoDocumento tipoDocumentoCreado = response.getBody();
    Assertions.assertThat(tipoDocumentoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(tipoDocumentoCreado.getNombre()).as("getNombre()").isEqualTo(tipoDocumento.getNombre());
    Assertions.assertThat(tipoDocumentoCreado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoDocumento.getDescripcion());
    Assertions.assertThat(tipoDocumentoCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsTipoDocumento() throws Exception {
    Long idTipoDocumento = 1L;
    TipoDocumento tipoDocumento = generarMockTipoDocumento(idTipoDocumento, "nombre-actualizado");

    final ResponseEntity<TipoDocumento> response = restTemplate.exchange(
        TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, tipoDocumento),
        TipoDocumento.class, idTipoDocumento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoDocumento tipoDocumentoActualizado = response.getBody();
    Assertions.assertThat(tipoDocumentoActualizado.getId()).as("getId()").isEqualTo(tipoDocumento.getId());
    Assertions.assertThat(tipoDocumentoActualizado.getNombre()).as("getNombre()").isEqualTo(tipoDocumento.getNombre());
    Assertions.assertThat(tipoDocumentoActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoDocumento.getDescripcion());
    Assertions.assertThat(tipoDocumentoActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void reactivar_ReturnTipoDocumento() throws Exception {
    Long idTipoDocumento = 1L;

    final ResponseEntity<TipoDocumento> response = restTemplate.exchange(
        TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), TipoDocumento.class, idTipoDocumento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoDocumento tipoDocumentoDisabled = response.getBody();
    Assertions.assertThat(tipoDocumentoDisabled.getId()).as("getId()").isEqualTo(idTipoDocumento);
    Assertions.assertThat(tipoDocumentoDisabled.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(tipoDocumentoDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(tipoDocumentoDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnTipoDocumento() throws Exception {
    Long idTipoDocumento = 1L;

    final ResponseEntity<TipoDocumento> response = restTemplate.exchange(
        TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), TipoDocumento.class, idTipoDocumento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoDocumento tipoDocumentoDisabled = response.getBody();
    Assertions.assertThat(tipoDocumentoDisabled.getId()).as("getId()").isEqualTo(idTipoDocumento);
    Assertions.assertThat(tipoDocumentoDisabled.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(tipoDocumentoDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(tipoDocumentoDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.FALSE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsTipoDocumento() throws Exception {
    Long idTipoDocumento = 1L;

    final ResponseEntity<TipoDocumento> response = restTemplate.exchange(
        TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoDocumento.class, idTipoDocumento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoDocumento tipoDocumento = response.getBody();
    Assertions.assertThat(tipoDocumento.getId()).as("getId()").isEqualTo(idTipoDocumento);
    Assertions.assertThat(tipoDocumento.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(tipoDocumento.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(tipoDocumento.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoDocumentoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoDocumento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumento> tiposDocumento = response.getBody();
    Assertions.assertThat(tiposDocumento.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tiposDocumento.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tiposDocumento.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tiposDocumento.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsTipoDocumentoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoDocumento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumento> tiposDocumento = response.getBody();
    Assertions.assertThat(tiposDocumento.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tiposDocumento.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tiposDocumento.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tiposDocumento.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id) {
    return generarMockTipoDocumento(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setDescripcion("descripcion-" + id);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

}