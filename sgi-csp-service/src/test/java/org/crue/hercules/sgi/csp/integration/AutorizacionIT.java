package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.AutorizacionController;
import org.crue.hercules.sgi.csp.dto.AutorizacionInput;
import org.crue.hercules.sgi.csp.dto.AutorizacionOutput;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.EstadoAutorizacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AutorizacionIT extends BaseIT {

  @Autowired
  private EstadoAutorizacionRepository estadoAutorizacionRepository;

  private static final String DEFAULT_TITULO_PROYECTO = "proyecto 1";
  private static final String DEFAULT_SOLICITANTE_REF = "00112233";
  private static final String DEFAULT_RESPONSABLE_REF = "27333555";
  private static final String DEFAULT_OBSERVACIONES = "autorizacion nueva";
  private static final int DEFAULT_HORAS_DEDICADAS = 24;
  private static final String DEFAULT_ENTIDAD_REF = "34444333";
  private static final String DEFAULT_DATOS_RESPONSABLES = "datos responsable";
  private static final String DEFAULT_DATOS_ENTIDAD = "datos entidad creada";
  private static final String DEFAULT_DATOS_CONVOCATORIA = "datos convocatoria creada";
  private static final long DEFAULT_CONVOCATORIA_ID = 1L;
  private static final String USER_PERSONA_REF = "user";

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = AutorizacionController.REQUEST_MAPPING;
  private static final String PATH_PRESENTAR = "/presentar";
  private static final String PATH_PRESENTABLE = "/presentable";
  private static final String PATH_CAMBIAR_ESTADO = "/cambiar-estado";
  private static final String PATH_VINCULACIONES_NOTIFICACIONES_PROYECTOS_EXTERNOS = "/vinculacionesnotificacionesproyectosexternos";
  private static final String PATH_ESTADOS = "/estados";
  private static final String PATH_CERTIFICADOS = "/certificados";
  private static final String PATH_HAS_CERTIFICADO_AUTORIZACION_VISIBLE = "/hascertificadoautorizacionvisible";

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken(USER_PERSONA_REF, roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsAutorizacion() throws Exception {
    AutorizacionInput toCreate = buildMockAutorizacion(null);
    String roles = "CSP-AUT-INV-C";

    final ResponseEntity<AutorizacionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, toCreate, roles), AutorizacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    AutorizacionOutput created = response.getBody();
    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(created.getConvocatoriaId()).as("getConvocaoriaId()")
        .isEqualTo(toCreate.getConvocatoriaId());
    Assertions.assertThat(created.getObservaciones()).as("getObservaciones()")
        .isEqualTo(toCreate.getObservaciones());
    Assertions.assertThat(created.getDatosConvocatoria()).as("getDatosConvocatoria()")
        .isEqualTo(toCreate.getDatosConvocatoria());
    Assertions.assertThat(created.getDatosEntidad()).as("getDatosEntidad()")
        .isEqualTo(toCreate.getDatosEntidad());
    Assertions.assertThat(created.getDatosResponsable()).as("getDatosResponsable()")
        .isEqualTo(toCreate.getDatosResponsable());
    Assertions.assertThat(created.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(toCreate.getEntidadRef());
    Assertions.assertThat(created.getHorasDedicacion()).as("getHorasDedicacion()")
        .isEqualTo(toCreate.getHorasDedicacion());
    Assertions.assertThat(created.getResponsableRef()).as("getResponsableRef()")
        .isEqualTo(toCreate.getResponsableRef());
    Assertions.assertThat(created.getSolicitanteRef()).as("getSolicitanteRef()")
        .isEqualTo(USER_PERSONA_REF);
    Assertions.assertThat(created.getTituloProyecto()).as("getTituloProyecto()")
        .isEqualTo(toCreate.getTituloProyecto());
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsAutorizacion() throws Exception {
    String roles = "CSP-AUT-INV-ER";
    Long idAutorizacion = 1L;
    AutorizacionInput toUpdate = buildMockAutorizacion(1L);
    toUpdate.setObservaciones("observaciones actualizadas");

    final ResponseEntity<AutorizacionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, toUpdate, roles), AutorizacionOutput.class, idAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    AutorizacionOutput updated = response.getBody();

    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(idAutorizacion);
    Assertions.assertThat(updated.getConvocatoriaId()).as("getConvocaoriaId()")
        .isEqualTo(toUpdate.getConvocatoriaId());
    Assertions.assertThat(updated.getObservaciones()).as("getObservaciones()")
        .isEqualTo(toUpdate.getObservaciones());
    Assertions.assertThat(updated.getDatosConvocatoria()).as("getDatosConvocatoria()")
        .isEqualTo(toUpdate.getDatosConvocatoria());
    Assertions.assertThat(updated.getDatosEntidad()).as("getDatosEntidad()")
        .isEqualTo(toUpdate.getDatosEntidad());
    Assertions.assertThat(updated.getDatosResponsable()).as("getDatosResponsable()")
        .isEqualTo(toUpdate.getDatosResponsable());
    Assertions.assertThat(updated.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(toUpdate.getEntidadRef());
    Assertions.assertThat(updated.getHorasDedicacion()).as("getHorasDedicacion()")
        .isEqualTo(toUpdate.getHorasDedicacion());
    Assertions.assertThat(updated.getResponsableRef()).as("getResponsableRef()")
        .isEqualTo(toUpdate.getResponsableRef());
    Assertions.assertThat(updated.getSolicitanteRef()).as("getSolicitanteRef()")
        .isEqualTo(DEFAULT_SOLICITANTE_REF);
    Assertions.assertThat(updated.getTituloProyecto()).as("getTituloProyecto()")
        .isEqualTo(toUpdate.getTituloProyecto());

  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsAutorizacion() throws Exception {
    String roles = "CSP-AUT-E";
    Long idAutorizacion = 1L;

    final ResponseEntity<AutorizacionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), AutorizacionOutput.class, idAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    AutorizacionOutput autorizacion = response.getBody();

    Assertions.assertThat(autorizacion.getId()).as("getId()").isEqualTo(idAutorizacion);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/estado_autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void presentar_ReturnsAutorizacion() throws Exception {
    String roles = "CSP-AUT-INV-ER";
    Long idAutorizacion = 3L;

    final ResponseEntity<AutorizacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRESENTAR,
        HttpMethod.PATCH, buildRequest(null, null, roles), AutorizacionOutput.class, idAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    AutorizacionOutput autorizacion = response.getBody();

    Assertions.assertThat(autorizacion.getId()).as("getId()").isEqualTo(idAutorizacion);

    EstadoAutorizacion estadoAutorizacion = this.estadoAutorizacionRepository.findById(autorizacion.getEstadoId())
        .orElse(null);

    Assertions.assertThat(estadoAutorizacion).isNotNull();
    Assertions.assertThat(estadoAutorizacion.getEstado()).isEqualTo(EstadoAutorizacion.Estado.PRESENTADA);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/estado_autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void presentable_ReturnsStatusCode200() throws Exception {
    String roles = "CSP-AUT-INV-ER";
    Long idAutorizacion = 3L;

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRESENTABLE,
        HttpMethod.HEAD, buildRequest(null, null, roles), Void.class, idAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsAutorizacionSubList() throws Exception {
    String[] roles = { "CSP-AUT-E", "CSP-AUT-B", "CSP-AUT-INV-C", "CSP-AUT-INV-ER", "CSP-AUT-INV-BR" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<AutorizacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<AutorizacionOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<AutorizacionOutput> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0)).as("get(0)").isNotNull();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()").isEqualTo(3);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsStatusCode204() throws Exception {
    // given: existing id
    Long toDeleteId = 1L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, "CSP-AUT-B"), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/estado_autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void cambiarEstado_ReturnsAutorizacion() throws Exception {
    String roles = "CSP-AUT-E";
    Long idAutorizacion = 1L;
    EstadoAutorizacion nuevoEstado = EstadoAutorizacion.builder()
        .id(1L)
        .estado(EstadoAutorizacion.Estado.REVISION)
        .autorizacionId(idAutorizacion)
        .fecha(Instant.now())
        .comentario("Nuevo estado")
        .build();

    final ResponseEntity<AutorizacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CAMBIAR_ESTADO,
        HttpMethod.PATCH, buildRequest(null, nuevoEstado, roles), AutorizacionOutput.class, idAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    AutorizacionOutput autorizacion = response.getBody();

    Assertions.assertThat(autorizacion).isNotNull();
    EstadoAutorizacion estado = this.estadoAutorizacionRepository.findById(autorizacion.getEstadoId()).orElse(null);

    Assertions.assertThat(estado).isNotNull();
    Assertions.assertThat(estado.getEstado()).isEqualTo(nuevoEstado.getEstado());
  }

  @Test
  void hasAutorizacionNotificacionProyectoExterno_ReturnsStatusCode204() throws Exception {
    String roles = "CSP-AUT-E";
    Long idAutorizacion = 1L;

    final ResponseEntity<AutorizacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_VINCULACIONES_NOTIFICACIONES_PROYECTOS_EXTERNOS,
        HttpMethod.HEAD, buildRequest(null, null, roles), AutorizacionOutput.class, idAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    AutorizacionOutput autorizacion = response.getBody();

    Assertions.assertThat(autorizacion).isNull();
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/estado_autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllEstadoAutorizacion_WithPagingSortingAndFiltering_ReturnsEstadoAutorizacionSubList() throws Exception {
    String[] roles = { "CSP-AUT-E", "CSP-AUT-B", "CSP-AUT-INV-C", "CSP-AUT-INV-ER", "CSP-AUT-INV-BR" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "id==1;id>=4;id<=5";

    Long autorizacionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ESTADOS)
        .queryParam("s", sort).queryParam("q", filter)
        .buildAndExpand(autorizacionId).toUri();
    final ResponseEntity<List<EstadoAutorizacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<EstadoAutorizacion>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<EstadoAutorizacion> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0)).as("get(0)").isNotNull();
    Assertions.assertThat(responseData.get(1)).as("get(1)").isNotNull();
    Assertions.assertThat(responseData.get(2)).as("get(2)").isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId()).as("get(1).getId()").isEqualTo(4L);
    Assertions.assertThat(responseData.get(2).getId()).as("get(2).getId()").isEqualTo(5L);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/estado_autorizacion.sql",
    "classpath:scripts/certificado_autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllCertificadoAutorizacion_WithPagingSorting_ReturnsCertificadoAutorizacionSubList()
      throws Exception {
    String[] roles = { "CSP-AUT-E", "CSP-AUT-B", "CSP-AUT-INV-C", "CSP-AUT-INV-ER", "CSP-AUT-INV-BR" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";

    Long autorizacionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CERTIFICADOS)
        .queryParam("s", sort)
        .buildAndExpand(autorizacionId).toUri();

    final ResponseEntity<List<CertificadoAutorizacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<CertificadoAutorizacion>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<CertificadoAutorizacion> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0)).as("get(0)").isNotNull();
    Assertions.assertThat(responseData.get(1)).as("get(1)").isNotNull();
    Assertions.assertThat(responseData.get(2)).as("get(2)").isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId()).as("get(1).getId()").isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getId()).as("get(2).getId()").isEqualTo(3L);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/estado_autorizacion.sql",
    "classpath:scripts/certificado_autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasCertificadoAutorizacionVisible_ReturnsStatusCode200()
      throws Exception {
    String[] roles = { "CSP-AUT-E" };

    Long autorizacionId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_HAS_CERTIFICADO_AUTORIZACION_VISIBLE)
        .buildAndExpand(autorizacionId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class);

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  private AutorizacionInput buildMockAutorizacion(Long id) {
    return AutorizacionInput.builder()
        .convocatoriaId(DEFAULT_CONVOCATORIA_ID)
        .datosConvocatoria(DEFAULT_DATOS_CONVOCATORIA)
        .datosEntidad(DEFAULT_DATOS_ENTIDAD)
        .datosResponsable(DEFAULT_DATOS_RESPONSABLES)
        .entidadRef(DEFAULT_ENTIDAD_REF)
        .horasDedicacion(DEFAULT_HORAS_DEDICADAS)
        .observaciones(DEFAULT_OBSERVACIONES)
        .responsableRef(DEFAULT_RESPONSABLE_REF)
        .tituloProyecto(DEFAULT_TITULO_PROYECTO)
        .build();
  }

}