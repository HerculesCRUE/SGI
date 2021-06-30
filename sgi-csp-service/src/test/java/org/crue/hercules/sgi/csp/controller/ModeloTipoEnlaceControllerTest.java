package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.service.ModeloTipoEnlaceService;
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
 * ModeloTipoEnlaceControllerTest
 */
@WebMvcTest(ModeloTipoEnlaceController.class)
public class ModeloTipoEnlaceControllerTest extends BaseControllerTest {

  @MockBean
  private ModeloTipoEnlaceService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelotipoenlaces";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void create_ReturnsModeloTipoEnlace() throws Exception {
    // given: new ModeloTipoEnlace
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(null);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloTipoEnlace>any()))
        .willAnswer(new Answer<ModeloTipoEnlace>() {
          @Override
          public ModeloTipoEnlace answer(InvocationOnMock invocation) throws Throwable {
            ModeloTipoEnlace givenData = invocation.getArgument(0, ModeloTipoEnlace.class);
            ModeloTipoEnlace newData = new ModeloTipoEnlace();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ModeloTipoEnlace
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modeloTipoEnlace)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ModeloTipoEnlace is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("modeloEjecucion.id").value(modeloTipoEnlace.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEnlace.id").value(modeloTipoEnlace.getTipoEnlace().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ModeloTipoEnlace with id filled
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloTipoEnlace>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ModeloTipoEnlace
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modeloTipoEnlace)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void create_WithNonExistingModeloEjecucionId_Returns404() throws Exception {
    // given: a ModeloTipoEnlace with non existing ModeloEjecucionId
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloTipoEnlace>any()))
        .willThrow(new ModeloEjecucionNotFoundException(modeloTipoEnlace.getModeloEjecucion().getId()));

    // when: create ModeloTipoEnlace
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modeloTipoEnlace)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willReturn(modeloTipoEnlace);

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, modeloTipoEnlace.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void delete_NonExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ModeloTipoEnlaceNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsModeloTipoEnlace() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<ModeloTipoEnlace>() {
      @Override
      public ModeloTipoEnlace answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockModeloTipoEnlace(id);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ModeloTipoEnlace is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ModeloTipoEnlaceNotFoundException(1L);
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
   * Función que devuelve un objeto TipoEnlace
   * 
   * @param id
   * @param activo
   * @return TipoEnlace
   */
  private TipoEnlace generarMockTipoEnlace(Long id, Boolean activo) {
    return TipoEnlace.builder().id(id).nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo).build();
  }

  /**
   * Función que devuelve un objeto ModeloTipoEnlace
   * 
   * @param id id del ModeloTipoEnlace
   * @return el objeto ModeloTipoEnlace
   */
  private ModeloTipoEnlace generarMockModeloTipoEnlace(Long id) {
    return generarMockModeloTipoEnlace(id, id);
  }

  /**
   * Función que devuelve un objeto ModeloTipoEnlace
   * 
   * @param id id del ModeloTipoEnlace
   * @param id idTipoEnlace del TipoEnlace
   * @return el objeto ModeloTipoEnlace
   */
  private ModeloTipoEnlace generarMockModeloTipoEnlace(Long id, Long idTipoEnlace) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoEnlace modeloTipoFinalidad = new ModeloTipoEnlace();
    modeloTipoFinalidad.setId(id);
    modeloTipoFinalidad.setModeloEjecucion(modeloEjecucion);
    modeloTipoFinalidad.setTipoEnlace(generarMockTipoEnlace(idTipoEnlace, true));
    modeloTipoFinalidad.setActivo(true);

    return modeloTipoFinalidad;
  }

}
