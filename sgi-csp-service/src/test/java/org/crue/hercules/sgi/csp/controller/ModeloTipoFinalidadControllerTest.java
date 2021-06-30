package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ModeloTipoFinalidadNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.service.ModeloTipoFinalidadService;
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
 * ModeloTipoFinalidadControllerTest
 */

@WebMvcTest(ModeloTipoFinalidadController.class)
public class ModeloTipoFinalidadControllerTest extends BaseControllerTest {

  @MockBean
  private ModeloTipoFinalidadService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelotipofinalidades";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void create_ReturnsTipoFinalidad() throws Exception {
    // given: new ModeloTipoFinalidad
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloTipoFinalidad>any()))
        .willAnswer(new Answer<ModeloTipoFinalidad>() {
          @Override
          public ModeloTipoFinalidad answer(InvocationOnMock invocation) throws Throwable {
            ModeloTipoFinalidad givenData = invocation.getArgument(0, ModeloTipoFinalidad.class);
            ModeloTipoFinalidad newData = new ModeloTipoFinalidad();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ModeloTipoFinalidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ModeloTipoFinalidad is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("modeloEjecucion.id").value(data.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFinalidad.id").value(data.getTipoFinalidad().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(data.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ModeloTipoFinalidad with id filled
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloTipoFinalidad>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ModeloTipoFinalidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(1L, 1L, 1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willReturn(data);

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ModeloTipoFinalidadNotFoundException(id)).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.willThrow(new ModeloTipoFinalidadNotFoundException(id)).given(service)
        .disable(ArgumentMatchers.<Long>any());

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
  public void findById_WithExistingId_ReturnsModeloTipoFinalidad() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<ModeloTipoFinalidad>() {
      @Override
      public ModeloTipoFinalidad answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarModeloTipoFinalidad(id, 1L, 1L);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ModeloTipoFinalidad is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ModeloTipoFinalidadNotFoundException(1L);
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
   * Función que devuelve un objeto ModeloTipoFinalidad
   * 
   * @param modeloTipoFinalidadId
   * @param modeloEjecucionId
   * @param tipoFinalidadId
   * @return el objeto ModeloTipoFinalidad
   */
  private ModeloTipoFinalidad generarModeloTipoFinalidad(Long modeloTipoFinalidadId, Long modeloEjecucionId,
      Long tipoFinalidadId) {

    ModeloTipoFinalidad modeloTipoFinalidad = new ModeloTipoFinalidad();
    modeloTipoFinalidad.setId(modeloTipoFinalidadId);
    modeloTipoFinalidad
        .setModeloEjecucion(ModeloEjecucion.builder().id(modeloEjecucionId).activo(Boolean.TRUE).build());
    modeloTipoFinalidad.setTipoFinalidad(TipoFinalidad.builder().id(tipoFinalidadId).activo(Boolean.TRUE).build());
    modeloTipoFinalidad.setActivo(Boolean.TRUE);

    return modeloTipoFinalidad;
  }

}
