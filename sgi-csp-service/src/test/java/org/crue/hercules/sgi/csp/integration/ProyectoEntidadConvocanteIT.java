package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoEntidadConvocanteController;
import org.crue.hercules.sgi.csp.dto.ProyectoEntidadConvocanteDto;
import org.crue.hercules.sgi.csp.model.Programa;
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
public class ProyectoEntidadConvocanteIT extends BaseIT {

  private HttpEntity<ProyectoEntidadConvocanteDto> buildRequest(HttpHeaders headers,
      ProyectoEntidadConvocanteDto entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ProyectoEntidadConvocanteDto> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<Programa> buildRequest(HttpHeaders headers, Programa entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Programa> request = new HttpEntity<>(entity, headers);
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
      "classpath:scripts/estado_proyecto.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsProyectoEntidadConvocante() throws Exception {
    // given: new ProyectoEntidadConvocanteDto
    Long proyectoId = 1L;
    ProyectoEntidadConvocanteDto proyectoEntidadConvocante = ProyectoEntidadConvocanteDto.builder()
        .entidadRef("Entidad").build();

    // when: create ProyectoEntidadConvocanteDto
    final ResponseEntity<ProyectoEntidadConvocanteDto> response = restTemplate.exchange(
        ProyectoEntidadConvocanteController.REQUEST_MAPPING, HttpMethod.POST,
        buildRequest(null, proyectoEntidadConvocante, "CSP-PRO-E"), ProyectoEntidadConvocanteDto.class, proyectoId);

    // then: new ProyectoEntidadConvocanteDto is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoEntidadConvocanteDto proyectoEntidadConvocanteCreado = response.getBody();
    Assertions.assertThat(proyectoEntidadConvocanteCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoEntidadConvocanteCreado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(proyectoEntidadConvocante.getEntidadRef());
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
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/programa.sql",
      "classpath:scripts/proyecto_entidad_convocante.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void setPrograma_WithExistingId_ReturnsProyectoEntidadConvocante() throws Exception {
    // given: existing ProyectoEntidadConvocanteDto
    Long proyectoId = 1L;
    Long proyectoEntidadConvocanteId = 1L;
    Programa programa = Programa.builder().id(proyectoEntidadConvocanteId).build();

    // when: Programa set for ProyectoEntidadConvocanteDto
    final ResponseEntity<ProyectoEntidadConvocanteDto> response = restTemplate.exchange(
        ProyectoEntidadConvocanteController.REQUEST_MAPPING
            + ProyectoEntidadConvocanteController.PATH_ENTIDADCONVOCANTE_PROGRAMA,
        HttpMethod.PATCH, buildRequest(null, programa, "CSP-PRO-E"), ProyectoEntidadConvocanteDto.class, proyectoId,
        proyectoEntidadConvocanteId);

    // then: ProyectoEntidadConvocanteDto is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProyectoEntidadConvocanteDto proyectoEntidadConvocanteActualizado = response.getBody();
    Assertions.assertThat(proyectoEntidadConvocanteActualizado.getId()).as("getId()")
        .isEqualTo(proyectoEntidadConvocanteId);
    Assertions.assertThat(proyectoEntidadConvocanteActualizado.getPrograma()).isNotNull();
    Programa returnPrograma = proyectoEntidadConvocanteActualizado.getPrograma();
    Assertions.assertThat(returnPrograma.getId()).as("getPrograma().getId()").isEqualTo(programa.getId());
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
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/programa.sql",
      "classpath:scripts/proyecto_entidad_convocante.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    Long proyectoId = 1L;
    Long proyectoEntidadConvocanteId = 1L;

    // when: delete by id
    final ResponseEntity<ProyectoEntidadConvocanteDto> response = restTemplate.exchange(
        ProyectoEntidadConvocanteController.REQUEST_MAPPING
            + ProyectoEntidadConvocanteController.PATH_ENTIDADCONVOCANTE,
        HttpMethod.DELETE, buildRequest(null, (ProyectoEntidadConvocanteDto) null, "CSP-PRO-E"),
        ProyectoEntidadConvocanteDto.class, proyectoId, proyectoEntidadConvocanteId);

    // then: 204
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

}
