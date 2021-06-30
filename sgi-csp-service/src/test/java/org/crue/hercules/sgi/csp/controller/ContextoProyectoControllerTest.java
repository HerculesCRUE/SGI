package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ContextoProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.service.ContextoProyectoService;
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
 * ContextoProyectoControllerTest
 */
@WebMvcTest(ContextoProyectoController.class)
public class ContextoProyectoControllerTest extends BaseControllerTest {

  @MockBean
  private ContextoProyectoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyecto-contextoproyectos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_ReturnsContextoProyecto() throws Exception {
    // given: new ContextoProyecto
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ContextoProyecto>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ContextoProyecto newContextoProyecto = new ContextoProyecto();
          BeanUtils.copyProperties(invocation.getArgument(0), newContextoProyecto);
          newContextoProyecto.setId(1L);
          return newContextoProyecto;
        });

    // when: create ContextoProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(contextoProyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ContextoProyecto is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("intereses").value(contextoProyecto.getIntereses()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ContextoProyecto with id filled
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ContextoProyecto>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ContextoProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(contextoProyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsContextoProyecto() throws Exception {
    // given: Existing ContextoProyecto to be updated
    ContextoProyecto contextoProyectoExistente = generarMockContextoProyecto(1L);
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(1L);
    contextoProyecto.setIntereses("INTERESES");

    BDDMockito.given(service.findByProyecto(ArgumentMatchers.<Long>any())).willReturn(contextoProyectoExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<ContextoProyecto>any(), ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ContextoProyecto
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, contextoProyectoExistente.getProyectoId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(contextoProyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ContextoProyecto is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(contextoProyectoExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("intereses").value(contextoProyecto.getIntereses()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(1L);

    BDDMockito.willThrow(new ContextoProyectoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ContextoProyecto>any(), ArgumentMatchers.<Long>any());

    // when: update ContextoProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(contextoProyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void findByProyectoContextoProyecto_WithExistingId_ReturnsContextoProyecto() throws Exception {
    // given: existing id
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(1L);
    BDDMockito.given(service.findByProyecto(ArgumentMatchers.<Long>any())).willReturn(contextoProyecto);

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Proyecto is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void findByProyectoContextoProyecto_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findByProyecto(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ContextoProyectoNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void findByProyectoContextoProyecto_WithNoExistingContextoProyecto_Returns204() throws Exception {
    // given: Existing proyectoId and no existing ContextoProyecto
    BDDMockito.given(service.findByProyecto(ArgumentMatchers.<Long>any())).willReturn(null);

    // when: find
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 204 No Content
        andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto ContextoProyecto
   * 
   * @param id id del ContextoProyecto
   * @return el objeto ContextoProyecto
   */
  private ContextoProyecto generarMockContextoProyecto(Long id) {
    ContextoProyecto contextoProyecto = new ContextoProyecto();
    contextoProyecto.setId(id);
    contextoProyecto.setProyectoId(id);
    contextoProyecto.setIntereses("intereses");
    return contextoProyecto;
  }

}
