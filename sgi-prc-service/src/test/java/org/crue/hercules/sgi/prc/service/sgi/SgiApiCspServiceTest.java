package org.crue.hercules.sgi.prc.service.sgi;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.crue.hercules.sgi.prc.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * SgiApiCspServiceTest
 */
class SgiApiCspServiceTest extends BaseServiceTest {

  private SgiApiCspService sgiApiCspService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    sgiApiCspService = new SgiApiCspService(restApiProperties, restTemplate);
  }

  @Test
  void isPersonaBaremable_ko() throws Exception {
    String personaRef = "43234234";
    Integer anio = 2022;
    Assertions.assertThatThrownBy(() -> sgiApiCspService.isPersonaBaremable(personaRef, anio))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findGrupoEquipoByPersonaRefAndFechaBaremacion_ko() throws Exception {
    String personaRef = "43234234";
    Integer anio = 2022;
    Assertions
        .assertThatThrownBy(() -> sgiApiCspService.findGrupoEquipoByPersonaRefAndFechaBaremacion(personaRef, anio))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findGrupoById_ko() throws Exception {
    Long grupoRef = 2L;
    Assertions.assertThatThrownBy(() -> sgiApiCspService.findGrupoById(grupoRef))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void isGrupoBaremable_ko() throws Exception {
    Long grupoRef = 2L;
    Integer anio = 2022;
    Assertions.assertThatThrownBy(() -> sgiApiCspService.isGrupoBaremable(grupoRef, anio))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findAllGruposByAnio_ko() throws Exception {
    Integer anio = 2022;
    Assertions.assertThatThrownBy(() -> sgiApiCspService.findAllGruposByAnio(anio))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findAllGruposEquipoByGrupoIdAndAnio_ko() throws Exception {
    Long grupoRef = 2L;
    Integer anio = 2022;
    Assertions.assertThatThrownBy(() -> sgiApiCspService.findAllGruposEquipoByGrupoIdAndAnio(
        grupoRef, anio)).isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findProyectosProduccionCientifica_ko() throws Exception {
    Integer anioInicio = 2020;
    Integer anioFin = 2022;
    Assertions.assertThatThrownBy(() -> sgiApiCspService.findProyectosProduccionCientifica(
        anioInicio, anioFin)).isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void getTotalImporteConcedidoAnualidadGasto_ko() throws Exception {
    Long proyectoId = 1L;
    Assertions.assertThatThrownBy(() -> sgiApiCspService.getTotalImporteConcedidoAnualidadGasto(
        proyectoId)).isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void getTotalImporteConcedidoAnualidadGastoCostesIndirectos_ko() throws Exception {
    Long proyectoId = 1L;
    Assertions.assertThatThrownBy(() -> sgiApiCspService.getTotalImporteConcedidoAnualidadGastoCostesIndirectos(
        proyectoId)).isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findProyectoEquipoByProyectoIdAndAnio_ko() throws Exception {
    Long proyectoId = 1L;
    Assertions.assertThatThrownBy(() -> sgiApiCspService.findProyectoEquipoByProyectoId(proyectoId))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findProyectosSGEByProyectoId_ko() throws Exception {
    Long proyectoId = 1L;
    Assertions.assertThatThrownBy(() -> sgiApiCspService.findProyectosSgeByProyectoId(proyectoId))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion_ko() throws Exception {
    Long grupoRef = 1L;
    Assertions
        .assertThatThrownBy(
            () -> sgiApiCspService.findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(grupoRef))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findAllGruposByPersonaRef_ko() throws Exception {
    String personaRef = "22222";
    Assertions
        .assertThatThrownBy(
            () -> sgiApiCspService.findAllGruposByPersonaRef(personaRef))
        .isInstanceOf(MicroserviceCallException.class);
  }
}
