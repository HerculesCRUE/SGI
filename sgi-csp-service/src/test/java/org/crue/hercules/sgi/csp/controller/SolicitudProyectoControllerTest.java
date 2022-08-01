package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto.TipoPresupuesto;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SolicitudProyectoControllerTest
 */
@WebMvcTest(SolicitudProyectoController.class)
class SolicitudProyectoControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoService service;
  @MockBean
  private SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;
  @MockBean
  private SolicitudProyectoSocioService solicitudProyectoSocioService;
  @MockBean
  private SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyecto";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_ReturnsSolicitudProyecto() throws Exception {
    // given: new SolicitudProyecto
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(null);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyecto>any()))
        .willAnswer(new Answer<SolicitudProyecto>() {
          @Override
          public SolicitudProyecto answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyecto givenData = invocation.getArgument(0, SolicitudProyecto.class);
            SolicitudProyecto newData = new SolicitudProyecto();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create SolicitudProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new SolicitudProyecto is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("colaborativo").value(solicitudProyecto.getColaborativo())).andExpect(
            MockMvcResultMatchers.jsonPath("tipoPresupuesto").value(solicitudProyecto.getTipoPresupuesto().toString()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithId_Returns400() throws Exception {
    // given: a SolicitudProyecto with id filled
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyecto>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithExistingId_ReturnsSolicitudProyecto() throws Exception {
    // given: existing SolicitudProyecto
    SolicitudProyecto updatedSolicitudProyecto = generarSolicitudProyecto(1L);

    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyecto>any()))
        .willAnswer(new Answer<SolicitudProyecto>() {
          @Override
          public SolicitudProyecto answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyecto givenData = invocation.getArgument(0, SolicitudProyecto.class);
            return givenData;
          }
        });

    // when: update SolicitudProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyecto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: SolicitudProyecto is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("colaborativo").value(updatedSolicitudProyecto.getColaborativo()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoPresupuesto")
            .value(updatedSolicitudProyecto.getTipoPresupuesto().toString()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: a SolicitudProyecto with non existing id
    SolicitudProyecto updatedSolicitudProyecto = generarSolicitudProyecto(1L);

    BDDMockito.willThrow(new SolicitudProyectoNotFoundException(updatedSolicitudProyecto.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyecto>any()))
        .willThrow(new SolicitudProyectoNotFoundException(updatedSolicitudProyecto.getId()));

    // when: update SolicitudProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyecto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.doNothing().when(service).delete(ArgumentMatchers.anyLong());
    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudProyectoNotFoundException(id)).given(service)
        .delete(ArgumentMatchers.<Long>any());

    // when: delete by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void findById_WithExistingId_ReturnsSolicitudProyecto() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<SolicitudProyecto>() {
      @Override
      public SolicitudProyecto answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarSolicitudProyecto(id);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyecto is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoNotFoundException(id);
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
   * Función que devuelve un objeto SolicitudProyecto
   * 
   * @param solicitudProyectoId
   * @param solicitudId
   * @return el objeto SolicitudProyecto
   */
  private SolicitudProyecto generarSolicitudProyecto(Long solicitudProyectoId) {

    // formatter: off

    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder().id(solicitudProyectoId)
        .acronimo("acronimo-" + solicitudProyectoId).colaborativo(Boolean.TRUE).tipoPresupuesto(TipoPresupuesto.GLOBAL)
        .coordinado(Boolean.TRUE).build();

    // formatter: on

    return solicitudProyecto;
  }
}
