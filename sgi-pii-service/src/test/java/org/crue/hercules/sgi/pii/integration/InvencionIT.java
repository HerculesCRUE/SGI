package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.InformePatentabilidadOutput;
import org.crue.hercules.sgi.pii.dto.InvencionAreaConocimientoOutput;
import org.crue.hercules.sgi.pii.dto.InvencionInput;
import org.crue.hercules.sgi.pii.dto.InvencionInventorOutput;
import org.crue.hercules.sgi.pii.dto.InvencionOutput;
import org.crue.hercules.sgi.pii.dto.InvencionSectorAplicacionOutput;
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
 * Test de integracion de Invencion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class InvencionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/invenciones";
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_ACTIVAR = "/activar";
  private static final String PATH_DESACTIVAR = "/desactivar";
  private static final String PATH_AREAS = "/{id}/areasconocimiento";
  public static final String PATH_SECTORES = "/{id}/sectoresaplicacion";
  public static final String PATH_INFORMESPATENTABILIDAD = "/{id}/informespatentabilidad";
  public static final String PATH_INVENCION_INVENTOR = "/{invencionId}/invencion-inventores";
  public static final String PATH_INVENCION_GASTO = "/{invencionId}/gastos";

  private HttpEntity<InvencionInput> buildRequest(HttpHeaders headers,
      InvencionInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<InvencionInput> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findActivos_WithPagingSortingAndFiltering_ReturnInvencionOutputSubList() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R", "PII-INV-MOD-V" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<InvencionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionOutput> tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    Assertions.assertThat(tramoRepartoOutput.get(0).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(tramoRepartoOutput.get(1).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(tramoRepartoOutput.get(2).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnInvencionOutpuSubList() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<InvencionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionOutput> tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput.size()).isEqualTo(4);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("4");

    Assertions.assertThat(tramoRepartoOutput.get(0).getId()).as("id").isEqualTo(4);
    Assertions.assertThat(tramoRepartoOutput.get(1).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(tramoRepartoOutput.get(2).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(tramoRepartoOutput.get(3).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R", "PII-INV-MOD-V" };
    Long invencionOutputId = 1L;

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH
        + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null, roles), InvencionOutput.class, invencionOutputId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion-001");
    Assertions.assertThat(invencionOutput.getDescripcion()).as("descripcion").isEqualTo("descripcion-invencion-001");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-C" };
    InvencionInput invencionInput = generaMockInvencionInput();

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, invencionInput, roles), InvencionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(5);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion");
    Assertions.assertThat(invencionOutput.getDescripcion()).as("descripcion").isEqualTo("descripcion-invencion");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-E" };
    Long invencionId = 1L;
    InvencionInput invencionInput = generaMockInvencionInput();
    invencionInput.setComentarios("comentarios-invencion-modificado");

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT,
        buildRequest(null, invencionInput, roles), InvencionOutput.class, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion");
    Assertions.assertThat(invencionOutput.getComentarios()).as("comentarios")
        .isEqualTo("comentarios-invencion-modificado");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void activar_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-R" };
    Long invencionId = 4L;

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ACTIVAR,
        HttpMethod.PATCH,
        buildRequest(null, null, roles), InvencionOutput.class, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(4);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion-004");
    Assertions.assertThat(invencionOutput.getComentarios()).as("comentarios")
        .isEqualTo("comentarios-invencion-004");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-B" };
    Long invencionId = 1L;

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DESACTIVAR,
        HttpMethod.PATCH,
        buildRequest(null, null, roles), InvencionOutput.class, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion-001");
    Assertions.assertThat(invencionOutput.getComentarios()).as("comentarios")
        .isEqualTo("comentarios-invencion-001");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql",
  "classpath:scripts/invencion_area_conocimiento.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAreasConocimientoByInvencionId_ReturnInvencionAreaConocimientoOutput() throws Exception {

    String[] roles = { "PII-INV-E", "PII-INV-V", "PII-INV-C" };
    Long invencionId = 1L;

    final ResponseEntity<List<InvencionAreaConocimientoOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_AREAS,
        HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<InvencionAreaConocimientoOutput>>() {
        }, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionAreaConocimientoOutput> invencionAreaConocimientoOutput = response.getBody();

    Assertions.assertThat(invencionAreaConocimientoOutput.size()).isEqualTo(1);
    Assertions.assertThat(invencionAreaConocimientoOutput.get(0).getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionAreaConocimientoOutput.get(0).getInvencionId()).as("invencionId").isEqualTo(1);
    Assertions.assertThat(invencionAreaConocimientoOutput.get(0).getAreaConocimientoRef()).as("areaConocimientoRef")
        .isEqualTo("560");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql",
  "classpath:scripts/sector_aplicacion.sql",
  "classpath:scripts/invencion_sector_aplicacion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findSectoresAplicacionByInvencionId_ReturnInvencionSectorAplicacionOutputSubList() throws Exception {

    String[] roles = { "PII-INV-E", "PII-INV-V", "PII-INV-C" };
    Long invencionId = 1L;

    final ResponseEntity<List<InvencionSectorAplicacionOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_SECTORES,
        HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<InvencionSectorAplicacionOutput>>() {
        }, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionSectorAplicacionOutput> invencionSectorAplicacionOutput = response.getBody();

    Assertions.assertThat(invencionSectorAplicacionOutput.size()).isEqualTo(1);
    Assertions.assertThat(invencionSectorAplicacionOutput.get(0).getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionSectorAplicacionOutput.get(0).getInvencionId()).as("invencionId").isEqualTo(1);
    Assertions.assertThat(invencionSectorAplicacionOutput.get(0).getSectorAplicacion().getId()).as("sectorAplicacionId")
        .isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/resultado_informe_patentabilidad.sql",
  "classpath:scripts/invencion.sql",
  "classpath:scripts/informe_patentabilidad.sql",
// @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findInformesPatentabilidadByInvencionId_ReturnInformePatentabilidadOutputSubList() throws Exception {

    String[] roles = { "PII-INV-E", "PII-INV-V" };
    Long invencionId = 1L;

    final ResponseEntity<List<InformePatentabilidadOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_INFORMESPATENTABILIDAD,
        HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<InformePatentabilidadOutput>>() {
        }, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InformePatentabilidadOutput> informePatentabilidadOutput = response.getBody();

    Assertions.assertThat(informePatentabilidadOutput.size()).isEqualTo(1);
    Assertions.assertThat(informePatentabilidadOutput.get(0).getId()).as("id").isEqualTo(1);
    Assertions.assertThat(informePatentabilidadOutput.get(0).getInvencionId()).as("invencionId").isEqualTo(1);
    Assertions.assertThat(informePatentabilidadOutput.get(0).getComentarios()).as("comentarios")
        .isEqualTo("comentarios-001");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql",
  "classpath:scripts/invencion_inventor.sql",
// @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findInvencionInventoresByInvencionId__WithPagingSortingAndFiltering_ReturnInvencionInventoresOutputSubList()
      throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "inventorRef=ke=0";
    Long invencionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INVENCION_INVENTOR).queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(invencionId).toUri();

    final ResponseEntity<List<InvencionInventorOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionInventorOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionInventorOutput> invencionInventorOutput = response.getBody();

    Assertions.assertThat(invencionInventorOutput.size()).isEqualTo(3);
    Assertions.assertThat(invencionInventorOutput.get(0).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(invencionInventorOutput.get(1).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(invencionInventorOutput.get(2).getId()).as("id").isEqualTo(1);

  }

  /**
   * Función que devuelve un objeto InvencionInput
   * 
   * @return el objeto InvencionInput
   */
  private InvencionInput generaMockInvencionInput() {

    InvencionInput invencionInput = new InvencionInput();
    invencionInput.setTitulo("titulo-invencion");
    invencionInput.setDescripcion("descripcion-invencion");
    invencionInput.setComentarios("comentarios-invencion");
    invencionInput.setFechaComunicacion(Instant.parse("2020-10-19T00:00:00Z"));
    invencionInput.setTipoProteccionId(1L);

    return invencionInput;
  }

}
