package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ProyectoEntidadGestoraNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadGestoraService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
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
 * ProyectoEntidadGestoraControllerTest
 */
@WebMvcTest(ProyectoEntidadGestoraController.class)
class ProyectoEntidadGestoraControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoEntidadGestoraService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoentidadgestoras";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void create_ReturnsProyectoEntidadGestora() throws Exception {
    // given: new ProyectoEntidadGestora
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setId(null);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoEntidadGestora>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoEntidadGestora newProyectoEntidadGestora = new ProyectoEntidadGestora();
          BeanUtils.copyProperties(invocation.getArgument(0), newProyectoEntidadGestora);
          newProyectoEntidadGestora.setId(1L);
          return newProyectoEntidadGestora;
        });

    // when: create ProyectoEntidadGestora
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoEntidadGestora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ProyectoEntidadGestora is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoEntidadGestora.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value(proyectoEntidadGestora.getEntidadRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void create_WithId_Returns400() throws Exception {
    // given: a ProyectoEntidadGestora with id filled
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoEntidadGestora>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ProyectoEntidadGestora
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoEntidadGestora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void update_ReturnsProyectoEntidadGestora() throws Exception {
    // given: Existing ProyectoEntidadGestora to be updated
    ProyectoEntidadGestora proyectoEntidadGestoraExistente = generarMockProyectoEntidadGestora(1L, 1L);
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setEntidadRef("entidadRef-modificada");

    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoEntidadGestora>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ProyectoEntidadGestora
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoEntidadGestoraExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoEntidadGestora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoEntidadGestora is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoEntidadGestoraExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoEntidadGestoraExistente.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value(proyectoEntidadGestora.getEntidadRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);

    BDDMockito.willThrow(new ProyectoEntidadGestoraNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ProyectoEntidadGestora>any());

    // when: update ProyectoEntidadGestora
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoEntidadGestora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
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

    Mockito.verify(service, Mockito.times(1)).delete(ArgumentMatchers.anyLong());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProyectoEntidadGestoraNotFoundException(id)).given(service)
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

  /**
   * Función que devuelve un objeto ProyectoEntidadGestora
   * 
   * @param id         id del ProyectoEntidadGestora
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoEntidadGestora
   */
  private ProyectoEntidadGestora generarMockProyectoEntidadGestora(Long id, Long proyectoId) {

    // @formatter:off
    return ProyectoEntidadGestora.builder()
        .id(id)
        .proyectoId(proyectoId)
        .entidadRef("entidad-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }

}
