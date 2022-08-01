package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.service.ProgramaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoJustificacionService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SolicitudProyectoSocioPeriodoJustificacionControllerTest
 */
@WebMvcTest(SolicitudProyectoSocioPeriodoJustificacionController.class)
class SolicitudProyectoSocioPeriodoJustificacionControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoSocioPeriodoJustificacionService service;

  @MockBean
  private ProgramaService programaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectosocioperiodojustificaciones";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_ReturnsSolicitudProyectoSocioPeriodoJustificacionList() throws Exception {
    // given: una lista con uno de los SolicitudProyectoSocioPeriodoJustificacion
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoSocioPeriodoJustificacion newSolicitudProyectoSocioPeriodoJustificacion = generarMockSolicitudProyectoSocioPeriodoJustificacion(
        null, 27, 30, 1L);
    SolicitudProyectoSocioPeriodoJustificacion updatedSolicitudProyectoSocioPeriodoJustificacion = generarMockSolicitudProyectoSocioPeriodoJustificacion(
        4L, 24, 26, 1L);

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificaciones = Arrays
        .asList(updatedSolicitudProyectoSocioPeriodoJustificacion, newSolicitudProyectoSocioPeriodoJustificacion);

    BDDMockito
        .given(service.update(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<SolicitudProyectoSocioPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoSocioPeriodoJustificacion> periodoJustificaciones = invocation.getArgument(1);
          return periodoJustificaciones.stream().map(periodoJustificacion -> {
            if (periodoJustificacion.getId() == null) {
              periodoJustificacion.setId(5L);
            }
            periodoJustificacion.setSolicitudProyectoSocioId(solicitudProyectoSocioId);
            return periodoJustificacion;
          }).collect(Collectors.toList());
        });

    // when: update
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudProyectoSocioId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoSocioPeriodoJustificaciones)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se crea el nuevo SolicitudProyectoSocioPeriodoJustificacion, se
        // actualiza el
        // existe y se eliminan el resto
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id")
            .value(solicitudProyectoSocioPeriodoJustificaciones.get(0).getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].solicitudProyectoSocioId").value(solicitudProyectoSocioId))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].numPeriodo")
            .value(solicitudProyectoSocioPeriodoJustificaciones.get(0).getNumPeriodo()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].mesInicial")
            .value(solicitudProyectoSocioPeriodoJustificaciones.get(0).getMesInicial()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].mesFinal")
            .value(solicitudProyectoSocioPeriodoJustificaciones.get(0).getMesFinal()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].fechaInicio").value("2020-10-10T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].fechaFin").value("2020-11-20T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].observaciones")
            .value(solicitudProyectoSocioPeriodoJustificaciones.get(0).getObservaciones()))

        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(5))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].solicitudProyectoSocioId").value(solicitudProyectoSocioId))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].numPeriodo")
            .value(solicitudProyectoSocioPeriodoJustificaciones.get(1).getNumPeriodo()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].mesInicial")
            .value(solicitudProyectoSocioPeriodoJustificaciones.get(1).getMesInicial()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].mesFinal")
            .value(solicitudProyectoSocioPeriodoJustificaciones.get(1).getMesFinal()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].fechaInicio").value("2020-10-10T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].fechaFin").value("2020-11-20T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].observaciones")
            .value(solicitudProyectoSocioPeriodoJustificaciones.get(1).getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion = generarMockSolicitudProyectoSocioPeriodoJustificacion(
        1L);

    BDDMockito.willThrow(new SolicitudProyectoSocioPeriodoJustificacionNotFoundException(id)).given(service)
        .update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoSocioPeriodoJustificacion>anyList());

    // when: update
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Arrays.asList(solicitudProyectoSocioPeriodoJustificacion))))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void findById_WithExistingId_ReturnsSolicitudProyectoSocioPeriodoJustificacion() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockSolicitudProyectoSocioPeriodoJustificacion(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoSocioPeriodoJustificacion is resturned as
        // JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoSocioId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("numPeriodo").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicial").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFinal").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-10T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-11-20T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value("observaciones-1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoSocioPeriodoJustificacionNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioPeriodoJustificacion
   * 
   * @param id id del SolicitudProyectoSocioPeriodoJustificacion
   * @return el objeto SolicitudProyectoSocioPeriodoJustificacion
   */
  private SolicitudProyectoSocioPeriodoJustificacion generarMockSolicitudProyectoSocioPeriodoJustificacion(Long id) {
    return generarMockSolicitudProyectoSocioPeriodoJustificacion(id, 1, 2, id);
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioPeriodoJustificacion
   * 
   * @param id                  id del SolicitudProyectoSocioPeriodoJustificacion
   * @param mesInicial          Mes inicial
   * @param mesFinal            Mes final
   * @param solicitudProyectoId Id SolicitudProyecto
   * @return el objeto SolicitudProyectoSocioPeriodoJustificacion
   */
  private SolicitudProyectoSocioPeriodoJustificacion generarMockSolicitudProyectoSocioPeriodoJustificacion(Long id,
      Integer mesInicial, Integer mesFinal, Long solicitudProyectoId) {
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion = new SolicitudProyectoSocioPeriodoJustificacion();
    solicitudProyectoSocioPeriodoJustificacion.setId(id);
    solicitudProyectoSocioPeriodoJustificacion
        .setSolicitudProyectoSocioId(solicitudProyectoId == null ? 1 : solicitudProyectoId);
    solicitudProyectoSocioPeriodoJustificacion.setNumPeriodo(1);
    solicitudProyectoSocioPeriodoJustificacion.setMesInicial(mesInicial);
    solicitudProyectoSocioPeriodoJustificacion.setMesFinal(mesFinal);
    solicitudProyectoSocioPeriodoJustificacion.setFechaInicio(Instant.parse("2020-10-10T00:00:00Z"));
    solicitudProyectoSocioPeriodoJustificacion.setFechaFin(Instant.parse("2020-11-20T23:59:59Z"));
    solicitudProyectoSocioPeriodoJustificacion.setObservaciones("observaciones-" + id);

    return solicitudProyectoSocioPeriodoJustificacion;
  }

}
