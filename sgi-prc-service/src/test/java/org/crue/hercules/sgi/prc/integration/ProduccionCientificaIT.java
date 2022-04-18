package org.crue.hercules.sgi.prc.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.ProduccionCientificaController;
import org.crue.hercules.sgi.prc.dto.AcreditacionOutput;
import org.crue.hercules.sgi.prc.dto.ActividadOutput;
import org.crue.hercules.sgi.prc.dto.AutorOutput;
import org.crue.hercules.sgi.prc.dto.CongresoOutput;
import org.crue.hercules.sgi.prc.dto.DireccionTesisOutput;
import org.crue.hercules.sgi.prc.dto.EstadoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.IndiceImpactoOutput;
import org.crue.hercules.sgi.prc.dto.ObraArtisticaOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaOutput;
import org.crue.hercules.sgi.prc.dto.ProyectoOutput;
import org.crue.hercules.sgi.prc.dto.PublicacionOutput;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiPiiService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgoService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
 * Test de integracion de ProduccionCientifica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProduccionCientificaIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = ProduccionCientificaController.MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PUBLICACIONES = ProduccionCientificaController.PATH_PUBLICACIONES;
  private static final String PATH_COMITES_EDITORIALES = ProduccionCientificaController.PATH_COMITES_EDITORIALES;
  private static final String PATH_CONGRESOS = ProduccionCientificaController.PATH_CONGRESOS;
  private static final String PATH_OBRAS_ARTISTICAS = ProduccionCientificaController.PATH_OBRAS_ARTISTICAS;
  private static final String PATH_ACTIVIDADES = ProduccionCientificaController.PATH_ACTIVIDADES;
  private static final String PATH_DIRECCIONES_TESIS = ProduccionCientificaController.PATH_DIRECCIONES_TESIS;
  private static final String PATH_INDICES_IMPACTO = ProduccionCientificaController.PATH_INDICES_IMPACTO;
  private static final String PATH_AUTORES = ProduccionCientificaController.PATH_AUTORES;
  private static final String PATH_PROYECTOS = ProduccionCientificaController.PATH_PROYECTOS;
  private static final String PATH_ACREDITACIONES = ProduccionCientificaController.PATH_ACREDITACIONES;
  private static final String PATH_RECHAZAR = "/rechazar";
  private static final String PATH_VALIDAR = "/validar";

  private static final String PUBLICACION_REF_VALUE = "publicacion-ref-";
  private static final String COMITE_EDITORIAL_REF_VALUE = "comite-ref-";
  private static final String CONGRESO_REF_VALUE = "congreso-ref-";
  private static final String OBRA_ARTISTICA_REF_VALUE = "obra-artistica-ref-";
  private static final String ACTIVIDAD_REF_VALUE = "organizacion_actividades-ref-";
  private static final String DIRECCION_TESIS_REF_VALUE = "direccion-tesis-ref-";

  @MockBean
  private SgiApiSgpService sgiApiSgpService;

  @MockBean
  private SgiApiSgoService sgiApiSgoService;

  @MockBean
  private SgiApiCspService sgiApiCspService;

  @MockBean
  private SgiApiPiiService sgiApiPiiService;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity,
      String... roles)
      throws Exception {
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
      "classpath:scripts/produccion_cientifica.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProduccionCientifica() throws Exception {
    Long idProduccionCientifica = 1L;
    String roles = "PRC-VAL-V";

    final ResponseEntity<ProduccionCientificaOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null, roles),
        ProduccionCientificaOutput.class, idProduccionCientifica);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProduccionCientificaOutput produccionCientifica = response.getBody();
    Assertions.assertThat(produccionCientifica.getId()).as("getId()").isNotNull();
    Assertions.assertThat(produccionCientifica.getProduccionCientificaRef()).as("getProduccionCientificaRef()")
        .isEqualTo(PUBLICACION_REF_VALUE + "001");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @ValueSource(strings = { "estado.estado==\"PENDIENTE\"" })
  void findAllPublicacionesFilterByEstado_WithPagingSortingAndFiltering_ReturnsProduccionCientificaSubList(
      String filter)
      throws Exception {
    String roles = "PRC-VAL-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PUBLICACIONES)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<PublicacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<PublicacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PublicacionOutput> produccionesCientificas = response.getBody();
    int numPRCs = produccionesCientificas.size();
    Assertions.assertThat(numPRCs).as("numPRCs").isEqualTo(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(produccionesCientificas.get(0).getProduccionCientificaRef())
        .as("get(0).getProduccionCientificaRef())")
        .isEqualTo(PUBLICACION_REF_VALUE + String.format("%03d", 1));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @ValueSource(strings = { "tituloPublicacion=ik=ubli", "investigador=ik=52364567",
      "fechaPublicacionDesde=ge=2020-01-01T23:00:00Z;fechaPublicacionHasta=le=2021-02-01T23:00:00Z" })
  void findAllPublicacionesByFilter_WithPagingSortingAndFiltering_ReturnsProduccionCientificaSubList(
      String filter)
      throws Exception {
    String roles = "PRC-VAL-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PUBLICACIONES)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<PublicacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<PublicacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PublicacionOutput> produccionesCientificas = response.getBody();
    int numPRCs = produccionesCientificas.size();
    Assertions.assertThat(numPRCs).as("numPRCs").isEqualTo(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(produccionesCientificas.get(0).getProduccionCientificaRef())
        .as("get(0).getProduccionCientificaRef())")
        .isEqualTo(PUBLICACION_REF_VALUE + String.format("%03d", 1));
    Assertions.assertThat(produccionesCientificas.get(1).getProduccionCientificaRef())
        .as("get(1).getProduccionCientificaRef())")
        .isEqualTo(PUBLICACION_REF_VALUE + String.format("%03d", 2));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllPublicacionesByInvestigador2_WithPagingSortingAndFiltering_ReturnsProduccionCientificaSubList()
      throws Exception {
    String roles = "PRC-VAL-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "investigador=ik=persona_ref3_2";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PUBLICACIONES)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<PublicacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<PublicacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PublicacionOutput> produccionesCientificas = response.getBody();
    int numPRCs = produccionesCientificas.size();
    Assertions.assertThat(numPRCs).as("numPRCs").isEqualTo(1);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(produccionesCientificas.get(0).getProduccionCientificaRef())
        .as("get(0).getProduccionCientificaRef())")
        .isEqualTo(PUBLICACION_REF_VALUE + String.format("%03d", 3));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllPublicacionesByInvestigadorAndTituloPublicacion_WithPagingSortingAndFiltering_ReturnsProduccionCientificaSubList()
      throws Exception {
    String roles = "PRC-VAL-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "investigador=ik=persona_ref3_2;tituloPublicacion=ik=ubli";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PUBLICACIONES)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<PublicacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<PublicacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PublicacionOutput> produccionesCientificas = response.getBody();
    int numPRCs = produccionesCientificas.size();
    Assertions.assertThat(numPRCs).as("numPRCs").isEqualTo(1);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(produccionesCientificas.get(0).getProduccionCientificaRef())
        .as("get(0).getProduccionCientificaRef())")
        .isEqualTo(PUBLICACION_REF_VALUE + String.format("%03d", 3));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllPublicacionesByFechaPublicacion_WithPagingSortingAndFiltering_ReturnsProduccionCientificaSubList()
      throws Exception {
    String roles = "PRC-VAL-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "fechaPublicacionDesde=ge=2021-01-01T23:00:00Z;fechaPublicacionHasta=le=2021-02-01T23:00:00Z";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PUBLICACIONES)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<PublicacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<PublicacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PublicacionOutput> produccionesCientificas = response.getBody();
    int numPRCs = produccionesCientificas.size();
    Assertions.assertThat(numPRCs).as("numPRCs").isEqualTo(2);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(produccionesCientificas.get(0).getProduccionCientificaRef())
        .as("get(0).getProduccionCientificaRef())")
        .isEqualTo(PUBLICACION_REF_VALUE + String.format("%03d", 2));
    Assertions.assertThat(produccionesCientificas.get(1).getProduccionCientificaRef())
        .as("get(1).getProduccionCientificaRef())")
        .isEqualTo(PUBLICACION_REF_VALUE + String.format("%03d", 1));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @ValueSource(strings = { "nombre=ik=Título", "issn=ik=ISSN",
      "fechaInicioDesde=ge=2020-01-1T23:00:00Z;fechaInicioHasta=le=2021-02-22T23:00:00Z" })
  void findAllComitesEditorialesByFilter_WithPagingSortingAndFiltering_ReturnsProduccionCientificaSubList(
      String filter)
      throws Exception {
    String roles = "PRC-VAL-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_COMITES_EDITORIALES)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<PublicacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<PublicacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PublicacionOutput> produccionesCientificas = response.getBody();
    Assertions.assertThat(produccionesCientificas).as("numElements").hasSize(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(produccionesCientificas.get(0).getProduccionCientificaRef())
        .as("get(0).getProduccionCientificaRef())")
        .isEqualTo(COMITE_EDITORIAL_REF_VALUE + String.format("%03d", 1));
    Assertions.assertThat(produccionesCientificas.get(1).getProduccionCientificaRef())
        .as("get(1).getProduccionCientificaRef())")
        .isEqualTo(COMITE_EDITORIAL_REF_VALUE + String.format("%03d", 2));
    Assertions.assertThat(produccionesCientificas.get(2).getProduccionCientificaRef())
        .as("get(1).getProduccionCientificaRef())")
        .isEqualTo(COMITE_EDITORIAL_REF_VALUE + String.format("%03d", 3));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @ValueSource(strings = { "tipoEvento=ik=008",
      "fechaCelebracionDesde=ge=2021-01-01T00:00:00Z;fechaCelebracionHasta=le=2021-02-01T00:00:00Z" })
  void findAllCongresosByFilter_WithPagingSortingAndFiltering_ReturnsProduccionCientificaSubList(
      String filter)
      throws Exception {
    String roles = "PRC-VAL-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_CONGRESOS)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<CongresoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<CongresoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<CongresoOutput> produccionesCientificas = response.getBody();
    Assertions.assertThat(produccionesCientificas).as("numElements").hasSize(2);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(produccionesCientificas.get(0).getProduccionCientificaRef())
        .as("get(0).getProduccionCientificaRef())")
        .isEqualTo(CONGRESO_REF_VALUE + String.format("%03d", 1));
    Assertions.assertThat(produccionesCientificas.get(1).getProduccionCientificaRef())
        .as("get(1).getProduccionCientificaRef())")
        .isEqualTo(CONGRESO_REF_VALUE + String.format("%03d", 2));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @ValueSource(strings = { "descripcion=ik=1",
      "nombreExposicion=ik=1" })
  public void findAllObrasArtisticasByFilter_WithPagingSortingAndFiltering_ReturnsProduccionCientificaSubList(
      String filter)
      throws Exception {
    String roles = "PRC-VAL-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_OBRAS_ARTISTICAS)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<ObraArtisticaOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ObraArtisticaOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ObraArtisticaOutput> produccionesCientificas = response.getBody();
    Assertions.assertThat(produccionesCientificas).as("numElements").hasSize(2);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(produccionesCientificas.get(0).getProduccionCientificaRef())
        .as("get(0).getProduccionCientificaRef())")
        .isEqualTo(OBRA_ARTISTICA_REF_VALUE + String.format("%03d", 1));
    Assertions.assertThat(produccionesCientificas.get(1).getProduccionCientificaRef())
        .as("get(1).getProduccionCientificaRef())")
        .isEqualTo(OBRA_ARTISTICA_REF_VALUE + String.format("%03d", 2));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @ValueSource(strings = {
      "fechaInicioDesde=ge=2021-01-01T00:00:00Z;fechaInicioHasta=le=2021-02-01T00:00:00Z"
  })
  public void findAllActividadesByFilter_WithPagingSortingAndFiltering_ReturnsProduccionCientificaSubList(
      String filter)
      throws Exception {
    String roles = "PRC-VAL-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_ACTIVIDADES)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<ActividadOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ActividadOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ActividadOutput> produccionesCientificas = response.getBody();
    Assertions.assertThat(produccionesCientificas).as("numElements").hasSize(2);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(produccionesCientificas.get(0).getProduccionCientificaRef())
        .as("get(0).getProduccionCientificaRef())")
        .isEqualTo(ACTIVIDAD_REF_VALUE + String.format("%03d", 1));
    Assertions.assertThat(produccionesCientificas.get(1).getProduccionCientificaRef())
        .as("get(1).getProduccionCientificaRef())")
        .isEqualTo(ACTIVIDAD_REF_VALUE + String.format("%03d", 2));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @ValueSource(strings = {
      "fechaDefensaDesde=ge=2021-01-01T00:00:00Z;fechaDefensaHasta=le=2021-02-01T00:00:00Z"
  })
  public void findAllDireccionesTesisByFilter_WithPagingSortingAndFiltering_ReturnsProduccionCientificaSubList(
      String filter)
      throws Exception {
    String roles = "PRC-VAL-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_DIRECCIONES_TESIS)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<DireccionTesisOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<DireccionTesisOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DireccionTesisOutput> produccionesCientificas = response.getBody();
    Assertions.assertThat(produccionesCientificas).as("numElements").hasSize(2);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(produccionesCientificas.get(0).getProduccionCientificaRef())
        .as("get(0).getProduccionCientificaRef())")
        .isEqualTo(DIRECCION_TESIS_REF_VALUE + String.format("%03d", 1));
    Assertions.assertThat(produccionesCientificas.get(1).getProduccionCientificaRef())
        .as("get(1).getProduccionCientificaRef())")
        .isEqualTo(DIRECCION_TESIS_REF_VALUE + String.format("%03d", 2));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void validar_ReturnsProduccionCientifica()
      throws Exception {
    String roles = "PRC-VAL-E";
    final Long produccionCientificaId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH +
        PATH_PARAMETER_ID + PATH_VALIDAR)
        .buildAndExpand(produccionCientificaId)
        .toUri();

    final ResponseEntity<PublicacionOutput> response = restTemplate.exchange(uri,
        HttpMethod.PATCH,
        buildRequest(null, null, roles), new ParameterizedTypeReference<PublicacionOutput>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final PublicacionOutput produccionCientificaValidada = response.getBody();

    Assertions.assertThat(produccionCientificaValidada.getId())
        .as(".getId()")
        .isEqualTo(produccionCientificaId);
    Assertions.assertThat(produccionCientificaValidada.getEstado().getEstado())
        .as(".getEstado().getEstado()")
        .isEqualTo(TipoEstadoProduccion.VALIDADO);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void rechazar_ReturnsProduccionCientifica()
      throws Exception {
    String roles = "PRC-VAL-E";
    final Long produccionCientificaId = 2L;
    final String comentarioRechazo = "rechazado";
    EstadoProduccionCientificaInput estadoProduccionCientificaInput = EstadoProduccionCientificaInput.builder()
        .comentario(comentarioRechazo).build();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH +
        PATH_PARAMETER_ID + PATH_RECHAZAR)
        .buildAndExpand(produccionCientificaId)
        .toUri();

    final ResponseEntity<PublicacionOutput> response = restTemplate.exchange(uri,
        HttpMethod.PATCH,
        buildRequest(null,
            estadoProduccionCientificaInput, roles),
        new ParameterizedTypeReference<PublicacionOutput>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final PublicacionOutput produccionCientificaValidada = response.getBody();

    Assertions.assertThat(produccionCientificaValidada.getId())
        .as(".getId()")
        .isEqualTo(produccionCientificaId);
    Assertions.assertThat(produccionCientificaValidada.getEstado().getEstado())
        .as(".getEstado().getEstado()")
        .isEqualTo(TipoEstadoProduccion.RECHAZADO);
    Assertions.assertThat(produccionCientificaValidada.getEstado().getComentario())
        .as(".getEstado().getComentario()")
        .isEqualTo(comentarioRechazo);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/produccion_cientifica.sql",
    "classpath:scripts/indice_impacto.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findIndicesImpacto_WithPagingAndSorting_ReturnsIndiceImpactoOutputSubList()
      throws Exception {
    String[] roles = { "PRC-VAL-V" };
    final Long produccionCientificaId = 1L;
    // first page, 3 elements per page sorted
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";

    // when: find IndiceImpacto
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_INDICES_IMPACTO)
        .queryParam("s", sort)
        .buildAndExpand(produccionCientificaId).toUri();

    final ResponseEntity<List<IndiceImpactoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<IndiceImpactoOutput>>() {
        });

    // given: IndiceImpacto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<IndiceImpactoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(responseData.get(0)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId())
        .as(".getId()")
        .isEqualTo(1L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/produccion_cientifica.sql",
    "classpath:scripts/autor.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAutores_WithPagingAndSorting_ReturnsAutorOutputSubList()
      throws Exception {
    String[] roles = { "PRC-VAL-V" };
    final Long produccionCientificaId = 1L;
    // first page, 3 elements per page sorted
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";

    // when: find Autor
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_AUTORES)
        .queryParam("s", sort)
        .buildAndExpand(produccionCientificaId).toUri();

    final ResponseEntity<List<AutorOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<AutorOutput>>() {
        });

    // given: Autor data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<AutorOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId())
        .as(".getId()")
        .isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId())
        .as(".getId()")
        .isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getId())
        .as(".getId()")
        .isEqualTo(3L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/produccion_cientifica.sql",
    "classpath:scripts/proyecto.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findProyectos_WithPagingAndSorting_ReturnsProyectoOutputSubList()
      throws Exception {
    String[] roles = { "PRC-VAL-V" };
    final Long produccionCientificaId = 1L;
    // first page, 3 elements per page sorted
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";

    // when: find Proyecto
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PROYECTOS)
        .queryParam("s", sort)
        .buildAndExpand(produccionCientificaId).toUri();

    final ResponseEntity<List<ProyectoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<ProyectoOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(2);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId())
        .as(".getId()")
        .isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId())
        .as(".getId()")
        .isEqualTo(2L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/produccion_cientifica.sql",
    "classpath:scripts/acreditacion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAcreditaciones_WithPagingAndSorting_ReturnsAcreditacionOutputSubList()
      throws Exception {
    String[] roles = { "PRC-VAL-V" };
    final Long produccionCientificaId = 1L;
    // first page, 3 elements per page sorted
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";

    // when: find Proyecto
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_ACREDITACIONES)
        .queryParam("s", sort)
        .buildAndExpand(produccionCientificaId).toUri();

    final ResponseEntity<List<AcreditacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<AcreditacionOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<AcreditacionOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(2);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId())
        .as(".getId()")
        .isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId())
        .as(".getId()")
        .isEqualTo(2L);
  }
}
