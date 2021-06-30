package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ModeloTipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.service.ModeloTipoHitoService;
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
 * ModeloTipoHitoControllerTest
 */

@WebMvcTest(ModeloTipoHitoController.class)
public class ModeloTipoHitoControllerTest extends BaseControllerTest {

  @MockBean
  private ModeloTipoHitoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelotipohitos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void create_ReturnsModeloTipoHito() throws Exception {
    // given: new ModeloTipoHito
    ModeloTipoHito data = generarModeloTipoHito(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloTipoHito>any())).willAnswer(new Answer<ModeloTipoHito>() {
      @Override
      public ModeloTipoHito answer(InvocationOnMock invocation) throws Throwable {
        ModeloTipoHito givenData = invocation.getArgument(0, ModeloTipoHito.class);
        ModeloTipoHito newData = new ModeloTipoHito();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(1L);
        return newData;
      }
    });

    // when: create ModeloTipoHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ModeloTipoHito is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("modeloEjecucion.id").value(data.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(data.getTipoHito().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitud").value(data.getSolicitud()))
        .andExpect(MockMvcResultMatchers.jsonPath("proyecto").value(data.getProyecto()))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoria").value(data.getConvocatoria()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(data.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ModeloTipoHito with id filled
    ModeloTipoHito data = generarModeloTipoHito(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloTipoHito>any())).willThrow(new IllegalArgumentException());

    // when: create ModeloTipoHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_ReturnsModeloTipoHito() throws Exception {
    // given: Existing ModeloTipoHito to be updated
    ModeloTipoHito modeloTipoHitoExistente = generarModeloTipoHito(1L, 1L, 1L);
    ModeloTipoHito modeloTipoHito = generarModeloTipoHito(1L, 1L, 1L);
    modeloTipoHito.setSolicitud(Boolean.FALSE);
    modeloTipoHito.setProyecto(Boolean.TRUE);
    modeloTipoHito.setConvocatoria(Boolean.FALSE);
    BDDMockito.given(service.update(ArgumentMatchers.<ModeloTipoHito>any())).willAnswer(new Answer<ModeloTipoHito>() {
      @Override
      public ModeloTipoHito answer(InvocationOnMock invocation) throws Throwable {
        ModeloTipoHito givenModeloTipoHito = invocation.getArgument(0, ModeloTipoHito.class);
        givenModeloTipoHito.setSolicitud(modeloTipoHito.getSolicitud());
        givenModeloTipoHito.setProyecto(modeloTipoHito.getProyecto());
        givenModeloTipoHito.setConvocatoria(modeloTipoHito.getConvocatoria());
        return givenModeloTipoHito;
      }
    });

    // when: update ModeloTipoHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, modeloTipoHitoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(modeloTipoHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ModeloTipoHito is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(modeloTipoHitoExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("modeloEjecucion.id")
            .value(modeloTipoHitoExistente.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(modeloTipoHitoExistente.getTipoHito().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitud").value(modeloTipoHito.getSolicitud()))
        .andExpect(MockMvcResultMatchers.jsonPath("proyecto").value(modeloTipoHito.getProyecto()))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoria").value(modeloTipoHito.getConvocatoria()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(modeloTipoHitoExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ModeloTipoHito modeloTipoHito = generarModeloTipoHito(null, 1L, 1L);

    BDDMockito.willThrow(new ModeloTipoHitoNotFoundException(id)).given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.willThrow(new ModeloTipoHitoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ModeloTipoHito>any());

    // when: update ModeloTipoHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(modeloTipoHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    ModeloTipoHito data = generarModeloTipoHito(1L, 1L, 1L);
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

    BDDMockito.willThrow(new ModeloTipoHitoNotFoundException(id)).given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.willThrow(new ModeloTipoHitoNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

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
  public void findById_WithExistingId_ReturnsModeloTipoHito() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<ModeloTipoHito>() {
      @Override
      public ModeloTipoHito answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarModeloTipoHito(id, 1L, 1L);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ModeloTipoHito is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ModeloTipoHitoNotFoundException(1L);
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
   * Función que devuelve un objeto ModeloTipoHito
   * 
   * @param modeloTipoHitoId
   * @param modeloEjecucionId
   * @param tipoHitoId
   * @return el objeto ModeloTipoHito
   */
  private ModeloTipoHito generarModeloTipoHito(Long modeloTipoHitoId, Long modeloEjecucionId, Long tipoHitoId) {

    ModeloTipoHito modeloTipoHito = new ModeloTipoHito();
    modeloTipoHito.setId(modeloTipoHitoId);
    modeloTipoHito.setModeloEjecucion(ModeloEjecucion.builder().id(modeloEjecucionId).activo(Boolean.TRUE).build());
    modeloTipoHito.setTipoHito(TipoHito.builder().id(tipoHitoId).activo(Boolean.TRUE).build());
    modeloTipoHito.setSolicitud(Boolean.TRUE);
    modeloTipoHito.setProyecto(Boolean.TRUE);
    modeloTipoHito.setConvocatoria(Boolean.TRUE);
    modeloTipoHito.setActivo(Boolean.TRUE);

    return modeloTipoHito;
  }

}
