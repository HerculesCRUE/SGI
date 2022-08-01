package org.crue.hercules.sgi.eer.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.controller.TipoDocumentoController;
import org.crue.hercules.sgi.eer.dto.TipoDocumentoOutput;
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
  private static final String REQUEST_MAPPING = TipoDocumentoController.REQUEST_MAPPING;
  private static final String PATH_SUBTIPOS = TipoDocumentoController.PATH_SUBTIPOS;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
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
    "classpath:scripts/tipo_documento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findTiposActivos_WithPagingSorting_ReturnsTipoDocumentoSubList() throws Exception {
    // given: data for TipoDocumento

    // first page, 3 elements per page sorted by id desc and filtered
    // by padre.id
    String roles = "EER-EER-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";

    // when: find TipoDocumento
    URI uri = UriComponentsBuilder.fromUriString(REQUEST_MAPPING).queryParam("s", sort)
        .build(false).toUri();
    final ResponseEntity<List<TipoDocumentoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoDocumentoOutput>>() {
        });

    // given: TipoDocumento data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumentoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId())")
        .isEqualTo(3L);
    Assertions.assertThat(responseData.get(1).getId()).as("get(1).getId())")
        .isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getId()).as("get(2).getId())")
        .isEqualTo(1L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/tipo_documento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findSubtiposActivos_WithPagingSorting_ReturnsTipoDocumentoSubList() throws Exception {
    // given: data for TipoDocumento
    Long padreId = 1L;
    // first page, 3 elements per page sorted by id desc and filtered
    // by padre.id
    String roles = "EER-EER-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";

    // when: find TipoDocumento
    URI uri = UriComponentsBuilder.fromUriString(REQUEST_MAPPING + PATH_SUBTIPOS).queryParam("s", sort)
        .buildAndExpand(padreId).toUri();
    final ResponseEntity<List<TipoDocumentoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoDocumentoOutput>>() {
        });

    // given: TipoDocumento data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumentoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId())")
        .isEqualTo(6L);
    Assertions.assertThat(responseData.get(1).getId()).as("get(1).getId())")
        .isEqualTo(5L);
    Assertions.assertThat(responseData.get(2).getId()).as("get(2).getId())")
        .isEqualTo(4L);
  }
}
