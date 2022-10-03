package org.crue.hercules.sgi.eer.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.controller.EmpresaController;
import org.crue.hercules.sgi.eer.dto.EmpresaAdministracionSociedadOutput;
import org.crue.hercules.sgi.eer.dto.EmpresaComposicionSociedadOutput;
import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoOutput;
import org.crue.hercules.sgi.eer.dto.EmpresaOutput;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.Empresa.EstadoEmpresa;
import org.crue.hercules.sgi.eer.model.Empresa.TipoEmpresa;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad.TipoAdministracion;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad.TipoAportacion;
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
 * Test de integracion de Empresa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmpresaIT extends BaseIT {

  private static final String PATH_DELIMITER = "/";
  private static final String PATH_ID = PATH_DELIMITER + "{id}";
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String CONTROLLER_BASE_PATH = "/empresas";
  private static final String PATH_DOCUMENTOS = EmpresaController.PATH_DOCUMENTOS;
  private static final String PATH_EMPRESA_COMPOSICION_SOCIEDAD = PATH_ID + PATH_DELIMITER + "composiciones-sociedades";
  private static final String PATH_EMPRESA_ADMINISTRACION_SOCIEDAD = PATH_ID + PATH_DELIMITER
      + "administraciones-sociedades";
  private static final String PATH_MODIFICADOS_IDS = PATH_DELIMITER + "modificados-ids";

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsEmpresa() throws Exception {
    String[] roles = { "EER-EER-V", "EER-EER-C", "EER-EER-E", "EER-EER-B", "EER-EER-R" };
    // given: new Empresa
    Empresa data = Empresa.builder().fechaSolicitud(Instant.now()).tipoEmpresa(TipoEmpresa.EBT)
        .estado(EstadoEmpresa.EN_TRAMITACION).objetoSocial("objetoSocial")
        .conocimientoTecnologia("conocimientoTecnologia").nombreRazonSocial("nombreRazonSocial").activo(Boolean.TRUE)
        .build();

    // when: create Empresa
    final ResponseEntity<Empresa> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, data, roles), Empresa.class);

    // then: new Empresa is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Empresa responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getNombreRazonSocial()).as("getNombreRazonSocial()")
        .isEqualTo(data.getNombreRazonSocial());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(data.getActivo());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/empresa.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsEmpresa() throws Exception {
    String[] roles = { "EER-EER-V", "EER-EER-C", "EER-EER-E", "EER-EER-B", "EER-EER-R" };
    Long id = 2L;
    // given: existing Empresa to be updated
    Empresa data = Empresa.builder().id(id).fechaSolicitud(Instant.now()).tipoEmpresa(TipoEmpresa.EBT)
        .estado(EstadoEmpresa.EN_TRAMITACION).objetoSocial("objetoSocial")
        .conocimientoTecnologia("conocimientoTecnologia").nombreRazonSocial("nombreRazonSocial " + id)
        .activo(Boolean.TRUE)
        .build();

    // when: update Empresa
    final ResponseEntity<Empresa> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, data, roles), Empresa.class, data.getId());

    // then: Empresa is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Empresa responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(data.getId());
    Assertions.assertThat(responseData.getNombreRazonSocial()).as("getNombreRazonSocial()")
        .isEqualTo(data.getNombreRazonSocial());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(data.getActivo());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/empresa.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnEmpresa() throws Exception {
    String[] roles = { "EER-EER-V", "EER-EER-C", "EER-EER-E", "EER-EER-B", "EER-EER-R" };
    Long idEmpresa = 3L;

    final ResponseEntity<EmpresaOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null, roles), EmpresaOutput.class, idEmpresa);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    EmpresaOutput empresaDisabled = response.getBody();
    Assertions.assertThat(empresaDisabled.getId()).as("getId()").isEqualTo(idEmpresa);
    Assertions.assertThat(empresaDisabled.getNombreRazonSocial()).as("getNombreRazonSocial()")
        .isEqualTo("nombreRazonSocial 3");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/empresa.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsEmpresa() throws Exception {
    String[] roles = { "EER-EER-V", "EER-EER-C", "EER-EER-E", "EER-EER-B", "EER-EER-R" };
    Long id = 1L;

    final ResponseEntity<EmpresaOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), EmpresaOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    EmpresaOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getNombreRazonSocial()).as("getNombreRazonSocial()")
        .isEqualTo("nombreRazonSocial 1");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/empresa.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsEmpresaSubList() throws Exception {
    String[] roles = { "EER-EER-V", "EER-EER-C", "EER-EER-E", "EER-EER-B", "EER-EER-R" };
    // given: data for Empresa

    // first page, 3 elements per page sorted by nombreRazonSocial desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "nombreRazonSocial=ke=nombreRazonSocial";

    // when: find Empresa
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<Empresa>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<Empresa>>() {
        });

    // given: Empresa data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Empresa> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getNombreRazonSocial()).as("get(0).getNombreRazonSocial())")
        .isEqualTo("nombreRazonSocial 3");
    Assertions.assertThat(responseData.get(1).getNombreRazonSocial()).as("get(1).getNombreRazonSocial())")
        .isEqualTo("nombreRazonSocial 2");
    Assertions.assertThat(responseData.get(2).getNombreRazonSocial()).as("get(2).getNombreRazonSocial())")
        .isEqualTo("nombreRazonSocial 1");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
      "classpath:scripts/empresa.sql",
      "classpath:scripts/tipo_documento.sql",
      "classpath:scripts/empresa_documento.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findDocumentos_WithPagingSortingAndFiltering_ReturnsEmpresaDocumentoSubList() throws Exception {
    String[] roles = { "EER-EER-V", "EER-EER-C", "EER-EER-E", "EER-EER-B", "EER-EER-R" };
    // given: data for EmpresaDocumento
    Long empresaId = 1L;
    // first page, 3 elements per page sorted by id des and filtered by nombre
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "nombre=ke=Documento";

    // when: find EmpresaDocumento
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_DOCUMENTOS).queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(empresaId).toUri();
    final ResponseEntity<List<EmpresaDocumentoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<EmpresaDocumentoOutput>>() {
        });

    // given: EmpresaDocumento data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EmpresaDocumentoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(responseData.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("Documento de procedimiento 1");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
      "classpath:scripts/empresa.sql",
      "classpath:scripts/empresa_composicion_sociedad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllEmpresaComposicionSociedad_WithPagingSortingAndFiltering_ReturnsEmpresaComposicionSociedadOutputSubList()
      throws Exception {

    String[] roles = { "EER-EER-V" };
    // given: data for EmpresaDocumento
    Long empresaId = 1L;
    // first page, 3 elements per page sorted by id des and filtered by nombre
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "tipoAportacion==NO_DINERARIA";

    // when: find EmpresaDocumento
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_EMPRESA_COMPOSICION_SOCIEDAD)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(empresaId).toUri();

    final ResponseEntity<List<EmpresaComposicionSociedadOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<EmpresaComposicionSociedadOutput>>() {
        });

    // given: EmpresaDocumento data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EmpresaComposicionSociedadOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(responseData.get(0).getTipoAportacion()).isEqualTo(TipoAportacion.NO_DINERARIA);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
      "classpath:scripts/empresa.sql",
      "classpath:scripts/empresa_administracion_sociedad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllEmpresaAdministracionSociedad_WithPagingSortingAndFiltering_ReturnsEmpresaAdministracionSociedadOutputSubList()
      throws Exception {

    String[] roles = { "EER-EER-V" };
    // given: data for EmpresaDocumento
    Long empresaId = 1L;
    // first page, 3 elements per page sorted by id des and filtered by nombre
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "tipoAdministracion==ADMINISTRADOR_SOLIDARIO";

    // when: find EmpresaDocumento
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_EMPRESA_ADMINISTRACION_SOCIEDAD)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(empresaId).toUri();

    final ResponseEntity<List<EmpresaAdministracionSociedadOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<EmpresaAdministracionSociedadOutput>>() {
        });

    // given: EmpresaDocumento data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EmpresaAdministracionSociedadOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(responseData.get(0).getTipoAdministracion())
        .isEqualTo(TipoAdministracion.ADMINISTRADOR_SOLIDARIO);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
      "classpath:scripts/empresa.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findIdsEmpresaModificados_ShouldReturnsModifiedsIdsList()
      throws Exception {

    String[] roles = { "EER-EER-V" };
    Long empresaId = 1L;
    String filter = "id==" + empresaId;

    // when: find EmpresaDocumento
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_MODIFICADOS_IDS)
        .queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<Long>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<Long>>() {
        });

    // given: EmpresaDocumento data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Long> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    Assertions.assertThat(responseData.get(0)).isEqualTo(empresaId);
  }
}
