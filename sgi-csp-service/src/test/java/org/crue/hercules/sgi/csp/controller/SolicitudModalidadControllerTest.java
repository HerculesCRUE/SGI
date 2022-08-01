package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.SolicitudModalidadNotFoundException;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.service.SolicitudModalidadService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SolicitudModalidadControllerTest
 */
@WebMvcTest(SolicitudModalidadController.class)
class SolicitudModalidadControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudModalidadService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudmodalidades";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-INV-C" })
  void create_ReturnsSolicitudModalidad() throws Exception {
    // given: new SolicitudModalidad
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudModalidad>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          SolicitudModalidad newSolicitudModalidad = new SolicitudModalidad();
          BeanUtils.copyProperties(invocation.getArgument(0), newSolicitudModalidad);
          newSolicitudModalidad.setId(1L);
          return newSolicitudModalidad;
        });

    // when: create SolicitudModalidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudModalidad)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new SolicitudModalidad is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudId").value(solicitudModalidad.getSolicitudId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value(solicitudModalidad.getEntidadRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("programa.id").value(solicitudModalidad.getPrograma().getId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-INV-C" })
  void create_WithId_Returns400() throws Exception {
    // given: a SolicitudModalidad with id filled
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudModalidad>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudModalidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudModalidad)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_ReturnsSolicitudModalidad() throws Exception {
    // given: Existing SolicitudModalidad to be updated
    SolicitudModalidad solicitudModalidadExistente = generarMockSolicitudModalidad(1L);
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(1L);
    Programa programa = new Programa();
    programa.setId(10L);
    solicitudModalidad.setPrograma(programa);

    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudModalidad>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update SolicitudModalidad
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudModalidadExistente.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitudModalidad)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: SolicitudModalidad is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(solicitudModalidadExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudId").value(solicitudModalidadExistente.getSolicitudId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value(solicitudModalidadExistente.getEntidadRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("programa.id").value(programa.getId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(1L);

    BDDMockito.willThrow(new SolicitudModalidadNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<SolicitudModalidad>any());

    // when: update SolicitudModalidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitudModalidad)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
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
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudModalidadNotFoundException(id)).given(service)
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
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithExistingId_ReturnsSolicitudModalidad() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockSolicitudModalidad(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudModalidad is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value("entidadRef"))
        .andExpect(MockMvcResultMatchers.jsonPath("programa.id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudModalidadNotFoundException(1L);
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
   * Función que devuelve un objeto SolicitudModalidad
   * 
   * @param id id del SolicitudModalidad
   * @return el objeto SolicitudModalidad
   */
  private SolicitudModalidad generarMockSolicitudModalidad(Long id) {
    Programa programa = new Programa();
    programa.setId(1L);

    SolicitudModalidad solicitudModalidad = new SolicitudModalidad();
    solicitudModalidad.setId(id);
    solicitudModalidad.setEntidadRef("entidadRef");
    solicitudModalidad.setSolicitudId(1L);
    solicitudModalidad.setPrograma(programa);

    return solicitudModalidad;
  }

}
