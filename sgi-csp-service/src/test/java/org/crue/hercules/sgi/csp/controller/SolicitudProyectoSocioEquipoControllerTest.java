package org.crue.hercules.sgi.csp.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioEquipoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SolicitudProyectoSocioEquipoControllerTest
 */
@WebMvcTest(SolicitudProyectoSocioEquipoController.class)
public class SolicitudProyectoSocioEquipoControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoSocioEquipoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectosocioequipo";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_WithExistingId_ReturnsSolicitudProyectoSocioEquipo() throws Exception {
    // given: una lista con uno de los ConvocatoriaPeriodoJustificacion actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoSocioEquipo newuSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(null, 1L);
    SolicitudProyectoSocioEquipo updatedSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(2L, 1L);
    updatedSolicitudProyectoSocioEquipo.setMesFin(6);

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioUpdated = Arrays
        .asList(updatedSolicitudProyectoSocioEquipo, newuSolicitudProyectoSocioEquipo);

    BDDMockito
        .given(service.update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoSocioEquipo>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoSocioEquipo> solitudProyectoEquipoSocios = invocation.getArgument(1);
          return solitudProyectoEquipoSocios.stream().map(solicitudProyectoSocioEquipo -> {
            if (solicitudProyectoSocioEquipo.getId() == null) {
              solicitudProyectoSocioEquipo.setId(5L);
            }
            solicitudProyectoSocioEquipo.setSolicitudProyectoSocioId(solicitudProyectoSocioId);
            return solicitudProyectoSocioEquipo;
          }).collect(Collectors.toList());
        });

    // when: update SolicitudProyectoSocioEquipo
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudProyectoSocioId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitudProyectoEquipoSocioUpdated)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: SolicitudProyectoSocioEquipo is updated
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].rolProyecto.id")
            .value(updatedSolicitudProyectoSocioEquipo.getRolProyecto().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].personaRef")
            .value(updatedSolicitudProyectoSocioEquipo.getPersonaRef()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[0].mesInicio").value(updatedSolicitudProyectoSocioEquipo.getMesInicio()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[0].mesFin").value(updatedSolicitudProyectoSocioEquipo.getMesFin()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    SolicitudProyectoSocioEquipo solicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(1L, 1L);
    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioUpdated = Arrays
        .asList(solicitudProyectoSocioEquipo);

    BDDMockito.willThrow(new SolicitudProyectoSocioEquipoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoSocioEquipo>anyList());

    // when: update SolicitudProyectoSocioEquipo
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitudProyectoEquipoSocioUpdated)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void findById_WithExistingId_ReturnsSolicitudProyectoSocioEquipo() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<SolicitudProyectoSocioEquipo>() {
          @Override
          public SolicitudProyectoSocioEquipo answer(InvocationOnMock invocation) throws Throwable {
            Long id = invocation.getArgument(0, Long.class);
            return generarSolicitudProyectoSocioEquipo(id, 1L);
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoSocioEquipo is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoSocioEquipoNotFoundException(id);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioEquipo
   * 
   * @param solicitudProyectoEquipoSocioId
   * @param entidadesRelacionadasId
   * @return el objeto SolicitudProyectoSocioEquipo
   */
  private SolicitudProyectoSocioEquipo generarSolicitudProyectoSocioEquipo(Long solicitudProyectoEquipoSocioId,
      Long entidadesRelacionadasId) {

    SolicitudProyectoSocioEquipo solicitudProyectoSocioEquipo = SolicitudProyectoSocioEquipo.builder()
        .id(solicitudProyectoEquipoSocioId).solicitudProyectoSocioId(entidadesRelacionadasId)
        .rolProyecto(RolProyecto.builder().id(entidadesRelacionadasId).build())
        .personaRef("user-" + solicitudProyectoEquipoSocioId).mesInicio(1).mesFin(3).build();

    return solicitudProyectoSocioEquipo;
  }

}
