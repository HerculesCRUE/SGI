package org.crue.hercules.sgi.csp.integration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoEntidadConvocanteController;
import org.crue.hercules.sgi.csp.dto.ProyectoEntidadConvocanteDto;
import org.crue.hercules.sgi.csp.model.Programa;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoEntidadConvocanteIT extends BaseIT {

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
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
  void create_ReturnsProyectoEntidadConvocante() throws Exception {
    // given: new ProyectoEntidadConvocanteDto
    Long proyectoId = 1L;
    ProyectoEntidadConvocanteDto proyectoEntidadConvocante = ProyectoEntidadConvocanteDto.builder()
        .entidadRef("Entidad-new").build();

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
  void setPrograma_WithExistingId_ReturnsProyectoEntidadConvocante() throws Exception {
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
  void delete_WithExistingId_Return204() throws Exception {
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
  void updateEntidadesConvocantes_ReturnsConvocatoriaEntidadConvocanteList() throws Exception {
    Long proyectoId = 1L;

    List<ProyectoEntidadConvocanteDto> toUpdateList = Arrays.asList(
        generarMockProyectoEntidadConvocanteDto(1L, 4L),
        generarMockProyectoEntidadConvocanteDto(null, 6L));

    final ResponseEntity<List<ProyectoEntidadConvocanteDto>> response = restTemplate.exchange(
        ProyectoEntidadConvocanteController.REQUEST_MAPPING,
        HttpMethod.PATCH,
        buildRequest(null,
            toUpdateList, "CSP-PRO-E"),
        new ParameterizedTypeReference<List<ProyectoEntidadConvocanteDto>>() {
        }, proyectoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<ProyectoEntidadConvocanteDto> proyectoEntidadesConvocantesUpdated = response.getBody();

    Assertions.assertThat(proyectoEntidadesConvocantesUpdated).hasSize(toUpdateList.size());

    Assertions.assertThat(proyectoEntidadesConvocantesUpdated.get(0).getPrograma().getId())
        .as("get(0).getPrograma().getId()")
        .isEqualTo(toUpdateList.get(0).getPrograma().getId());
    Assertions.assertThat(proyectoEntidadesConvocantesUpdated.get(1).getPrograma().getId())
        .as("get(1).getPrograma().getId()")
        .isEqualTo(toUpdateList.get(1).getPrograma().getId());
  }

  private ProyectoEntidadConvocanteDto generarMockProyectoEntidadConvocanteDto(Long id, Long programaId) {
    Programa programa = new Programa();
    programa.setId(programaId);

    ProyectoEntidadConvocanteDto proyectoEntidadConvocante = new ProyectoEntidadConvocanteDto();
    proyectoEntidadConvocante.setId(id);
    proyectoEntidadConvocante.setEntidadRef("entidad-" + (id == null ? 1 : id));
    proyectoEntidadConvocante.setPrograma(programa);

    return proyectoEntidadConvocante;
  }

}
