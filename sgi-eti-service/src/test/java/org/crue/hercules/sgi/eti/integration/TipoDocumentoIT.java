package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
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
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de TipoDocumento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/formulario.sql", 
  "classpath:scripts/tipo_documento.sql" 
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class TipoDocumentoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_DOCUMENTO_CONTROLLER_BASE_PATH = "/tipodocumentos";

  private HttpEntity<TipoDocumento> buildRequest(HttpHeaders headers, TipoDocumento entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-TIPODOCUMENTO-EDITAR", "ETI-TIPODOCUMENTO-VER")));

    HttpEntity<TipoDocumento> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getTipoDocumento_WithId_ReturnsTipoDocumento() throws Exception {

    Formulario formulario = new Formulario();
    formulario.setId(4L);
    formulario.setNombre("Seguimiento Anual");
    formulario.setDescripcion("Descripcion");

    final ResponseEntity<TipoDocumento> response = restTemplate.exchange(
        TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoDocumento.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoDocumento tipoDocumento = response.getBody();

    Assertions.assertThat(tipoDocumento.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento1");
    Assertions.assertThat(tipoDocumento.getFormulario()).isEqualTo(formulario);
  }

  @Test
  public void removeTipoDocumento_DoNotGetTipoDocumento() throws Exception {
    restTemplate.delete(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<TipoDocumento> response = restTemplate.exchange(
        TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoDocumento.class, 13L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void findAll_WithPaging_ReturnsTipoDocumentoSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoDocumento>>() {
        });

    // then: Respuesta OK, TipoDocumentos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumento> tipoDocumentos = response.getBody();
    Assertions.assertThat(tipoDocumentos.size()).isEqualTo(5);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("12");

    // Contiene de nombre='TipoDocumento6' a 'TipoDocumento8'
    Assertions.assertThat(tipoDocumentos.get(0).getNombre()).isEqualTo("TipoDocumento6");
    Assertions.assertThat(tipoDocumentos.get(1).getNombre()).isEqualTo("TipoDocumento7");
    Assertions.assertThat(tipoDocumentos.get(2).getNombre()).isEqualTo("TipoDocumento8");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoDocumentoList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre=ke=TipoDocumento;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoDocumento>>() {
        });

    // then: Respuesta OK, TipoDocumentos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumento> tipoDocumentos = response.getBody();
    Assertions.assertThat(tipoDocumentos.size()).isEqualTo(1);
    Assertions.assertThat(tipoDocumentos.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoDocumentos.get(0).getNombre()).startsWith("TipoDocumento");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoDocumentoList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoDocumento>>() {
        });

    // then: Respuesta OK, TipoDocumentos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumento> tipoDocumentos = response.getBody();
    Assertions.assertThat(tipoDocumentos.size()).isEqualTo(12);
    for (int i = 0; i < 8; i++) {
      TipoDocumento tipoDocumento = tipoDocumentos.get(i);
      Assertions.assertThat(tipoDocumento.getId()).isEqualTo(9 - i);
      Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento" + String.format("%d", 9 - i));
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoDocumentoSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre,desc";
    // when: Filtra por nombre like e id equals
    String filter = "nombre=ke=TipoDocumento";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoDocumento>>() {
        });

    // then: Respuesta OK, TipoDocumentos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumento> tipoDocumentos = response.getBody();
    Assertions.assertThat(tipoDocumentos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("12");

    // Contiene nombre='TipoDocumento8', 'TipoDocumento7',
    // 'TipoDocumento6'
    Assertions.assertThat(tipoDocumentos.get(0).getNombre()).isEqualTo("TipoDocumento" + String.format("%d", 9));
    Assertions.assertThat(tipoDocumentos.get(1).getNombre()).isEqualTo("TipoDocumento" + String.format("%d", 8));
    Assertions.assertThat(tipoDocumentos.get(2).getNombre()).isEqualTo("TipoDocumento" + String.format("%d", 7));

  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id     id del tipoDocumento
   * @param nombre la descripción del tipoDocumento
   * @return el objeto tipoDocumento
   */

  public TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("M10");
    formulario.setDescripcion("Formulario M10");

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setFormulario(formulario);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

}