package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoFacturacionController;
import org.crue.hercules.sgi.csp.dto.ProyectoFacturacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoFacturacionOutput;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoFacturacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ProyectoFacturacionController.MAPPING;

  private HttpEntity<ProyectoFacturacionInput> buildRequest(HttpHeaders headers,
      ProyectoFacturacionInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ProyectoFacturacionInput> request = new HttpEntity<>(entity, headers);

    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off    
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/tipo_facturacion.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoFacturacionOutput() throws Exception {
    ProyectoFacturacionInput toCreate = buildMockProyectoFacturacionInput(null);

    final ResponseEntity<ProyectoFacturacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, "CSP-PRO-E"),
        ProyectoFacturacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoFacturacionOutput created = response.getBody();

    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(created.getProyectoId()).as("getProyectoId()")
        .isEqualTo(toCreate.getProyectoId());
    Assertions.assertThat(I18nHelper.getValueForLanguage(created.getComentario(), Language.ES))
        .as("getComentario()").isEqualTo(I18nHelper.getValueForLanguage(toCreate.getComentario(), Language.ES));
    Assertions.assertThat(created.getFechaConformidad())
        .as("getFechaConformidad()").isEqualTo(toCreate.getFechaConformidad());
    Assertions.assertThat(created.getFechaEmision())
        .as("getFechaEmision()").isEqualTo(toCreate.getFechaEmision());
    Assertions.assertThat(created.getImporteBase())
        .as("getImporteBase()").isEqualTo(toCreate.getImporteBase());
    Assertions.assertThat(created.getNumeroPrevision())
        .as("getNumeroPrevision()").isEqualTo(toCreate.getNumeroPrevision());
    Assertions.assertThat(created.getPorcentajeIVA())
        .as("getPorcentajeIVA()").isEqualTo(toCreate.getPorcentajeIVA());
    Assertions.assertThat(created.getTipoFacturacion().getId())
        .as("getTipoFacturacion().getId()").isEqualTo(toCreate.getTipoFacturacionId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off    
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/tipo_facturacion.sql",
      "classpath:scripts/proyecto_facturacion.sql",
      "classpath:scripts/estado_validacion_ip.sql",
      "classpath:scripts/proyecto_facturacion_update_estado_validacion_ip.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoFacturacionOutput() throws Exception {
    Long proyectoFacturacionId = 1L;
    ProyectoFacturacionInput toUpdate = buildMockProyectoFacturacionInput(proyectoFacturacionId);
    List<I18nFieldValueDto> proyectoFacturacionComentario = new ArrayList<I18nFieldValueDto>();
    proyectoFacturacionComentario
        .add(new I18nFieldValueDto(Language.ES, "updated"));

    toUpdate.setEstadoValidacionIP(ProyectoFacturacionInput.EstadoValidacionIP.builder()
        .estado(ProyectoFacturacionInput.TipoEstadoValidacion.VALIDADA)
        .comentario(proyectoFacturacionComentario)
        .build());

    final ResponseEntity<ProyectoFacturacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, toUpdate, "CSP-PRO-E"), ProyectoFacturacionOutput.class,
        proyectoFacturacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoFacturacionOutput updated = response.getBody();

    Assertions.assertThat(updated.getId()).as("getId()")
        .isEqualTo(toUpdate.getId());
    Assertions.assertThat(I18nHelper.getValueForLanguage(updated.getComentario(), Language.ES))
        .as("getComentario()")
        .isEqualTo(I18nHelper.getValueForLanguage(toUpdate.getComentario(), Language.ES));
    Assertions.assertThat(updated.getFechaConformidad()).as("getFechaConformidad()")
        .isEqualTo(toUpdate.getFechaConformidad());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()")
        .isEqualTo(toUpdate.getProyectoId());
    Assertions.assertThat(updated.getFechaEmision())
        .as("getFechaEmision()").isEqualTo(toUpdate.getFechaEmision());
    Assertions.assertThat(updated.getImporteBase())
        .as("getImporteBase()").isEqualTo(toUpdate.getImporteBase());
    Assertions.assertThat(updated.getNumeroPrevision())
        .as("getNumeroPrevision()").isEqualTo(toUpdate.getNumeroPrevision());
    Assertions.assertThat(updated.getPorcentajeIVA())
        .as("getPorcentajeIVA()").isEqualTo(toUpdate.getPorcentajeIVA());
    Assertions.assertThat(updated.getTipoFacturacion().getId())
        .as("getTipoFacturacion().getId()").isEqualTo(toUpdate.getTipoFacturacionId());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off    
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/tipo_facturacion.sql",
      "classpath:scripts/proyecto_facturacion.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Returns204() throws Exception {
    // given: existing id
    Long toDeleteId = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, "CSP-PRO-E"), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private ProyectoFacturacionInput buildMockProyectoFacturacionInput(Long id) {
    List<I18nFieldValueDto> proyectoFacturacionComentario = new ArrayList<I18nFieldValueDto>();
    proyectoFacturacionComentario
        .add(new I18nFieldValueDto(Language.ES, "testing create"));

    return ProyectoFacturacionInput.builder()
        .comentario(proyectoFacturacionComentario)
        .id(id)
        .proyectoId(1L)
        .fechaConformidad(Instant.now())
        .fechaEmision(Instant.now())
        .importeBase(new BigDecimal(200000))
        .porcentajeIVA(21)
        .tipoFacturacionId(1L)
        .numeroPrevision(1)
        .build();
  }
}