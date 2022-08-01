package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ModeloTipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.service.ModeloTipoFaseService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
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
 * ModeloTipoFaseControllerTest
 */
@WebMvcTest(ModeloTipoFaseController.class)
class ModeloTipoFaseControllerTest extends BaseControllerTest {

  @MockBean
  private ModeloTipoFaseService modeloTipoFaseService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String MODELO_TIPO_FASE_CONTROLLER_BASE_PATH = "/modelotipofases";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  void create_ReturnsModeloTipoFase() throws Exception {
    // given: Un ModeloTipoFase nuevo
    ModeloTipoFase modeloTipoFase = generarModeloTipoFase(null);

    String modeloTipoFaseJson = mapper.writeValueAsString(modeloTipoFase);

    BDDMockito.given(modeloTipoFaseService.create(ArgumentMatchers.<ModeloTipoFase>any()))
        .will((InvocationOnMock invocation) -> {
          ModeloTipoFase modeloTipoFaseCreado = invocation.getArgument(0);
          modeloTipoFaseCreado.setId(1L);
          return modeloTipoFaseCreado;
        });
    // when: Creamos un ModeloTipoFase
    mockMvc
        .perform(MockMvcRequestBuilders.post(MODELO_TIPO_FASE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(modeloTipoFaseJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo ModeleoTipoFase y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  void create_WithId_Returns400() throws Exception {

    // given: Un ModeloTipoFase que produce un error al crearse porque ya tiene id
    ModeloTipoFase modeloTipoFase = generarModeloTipoFase(1L);

    String modeloTipoFaseJson = mapper.writeValueAsString(modeloTipoFase);
    BDDMockito.given(modeloTipoFaseService.create(ArgumentMatchers.<ModeloTipoFase>any()))
        .willThrow(new IllegalArgumentException());
    // when: Creamos un ModeloTipoFase
    mockMvc
        .perform(MockMvcRequestBuilders.post(MODELO_TIPO_FASE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(modeloTipoFaseJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  void update_ReturnsModeloTipoFase() throws Exception {
    // given: Un ModeloTipoFase a modificar
    ModeloTipoFase modeloTipoFase = generarModeloTipoFaseConTipoFaseId(1L);
    String tipoFaseJson = mapper.writeValueAsString(modeloTipoFase);
    BDDMockito.given(modeloTipoFaseService.update(ArgumentMatchers.<ModeloTipoFase>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    // when: Actualizamos el ModeloTipoFase
    mockMvc
        .perform(MockMvcRequestBuilders.put(MODELO_TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoFaseJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el ModeloTipoFase y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoria").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  void update_WithIdNotExist_ReturnsNotFound() throws Exception {
    // given: Un ModeloTipoFase a modificar
    ModeloTipoFase modeloTipoFase = generarModeloTipoFaseConTipoFaseId(1L);
    String replaceModeloTipoFaseJson = mapper.writeValueAsString(modeloTipoFase);
    BDDMockito.given(modeloTipoFaseService.update(ArgumentMatchers.<ModeloTipoFase>any()))
        .will((InvocationOnMock invocation) -> {
          throw new ModeloTipoFaseNotFoundException(((ModeloTipoFase) invocation.getArgument(0)).getId());
        });

    // when: Actualizamos el ModeloTipoFase
    mockMvc
        .perform(MockMvcRequestBuilders.put(MODELO_TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceModeloTipoFaseJson))
        // then: No encuentra el TipoFase y devuelve un 404
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  void update_WithIdActivoFalse_ReturnsIllegalArgumentException() throws Exception {
    // given: Un ModeloTipoFase a modificar
    ModeloTipoFase modeloTipoFase = generarModeloTipoFaseConTipoFaseId(1L);
    modeloTipoFase.setActivo(false);
    String replaceModeloTipoFaseJson = mapper.writeValueAsString(modeloTipoFase);
    BDDMockito.given(modeloTipoFaseService.update(modeloTipoFase)).willThrow(new IllegalArgumentException());
    // when: Actualizamos el ModeloTipoFase
    mockMvc
        .perform(MockMvcRequestBuilders.put(MODELO_TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceModeloTipoFaseJson))
        // then: No encuentra el TipoFase y devuelve un 404
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithExistingId_ReturnsModeloTipoFase() throws Exception {

    // given: Entidad con un determinado Id
    ModeloTipoFase modeloTipoFase = generarModeloTipoFaseConTipoFaseId(1L);

    BDDMockito.given(modeloTipoFaseService.findById(modeloTipoFase.getId())).willReturn(modeloTipoFase);

    // when: Se busca la entidad por ese Id
    mockMvc
        .perform(MockMvcRequestBuilders.get(MODELO_TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera la entidad con el Id
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(modeloTipoFase.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFase").value(modeloTipoFase.getTipoFase()))
        .andExpect(MockMvcResultMatchers.jsonPath("proyecto").value(modeloTipoFase.getProyecto()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithNoExistingId_Returns404() throws Exception {

    BDDMockito.given(modeloTipoFaseService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ModeloTipoFaseNotFoundException(invocation.getArgument(0));
    });

    // when: Se busca entidad con ese id
    mockMvc
        .perform(MockMvcRequestBuilders.get(MODELO_TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se produce error porque no encuentra la entidad con ese Id
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  void disableById_Returns204() throws Exception {
    // given: ModeloTipoFase con el id buscado
    Long idBuscado = 1L;
    ModeloTipoFase modeloTipoFase = generarModeloTipoFaseConTipoFaseId(1L);

    BDDMockito.given(modeloTipoFaseService.disable(ArgumentMatchers.<Long>any())).willReturn(modeloTipoFase);

    // when: Eliminamos el ModeloTipoFase por su id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(MODELO_TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoFase generarMockTipoFase(Long id, String nombre) {

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre(nombre);
    tipoFase.setDescripcion("descripcion-" + id);
    tipoFase.setActivo(Boolean.TRUE);

    return tipoFase;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase
   * 
   * @param id id del ModeloTipoFase
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarModeloTipoFase(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setModeloEjecucion(modeloEjecucion);
    modeloTipoFase.setTipoFase(generarMockTipoFase(id, "TipoFase" + String.format("%03d", id)));
    modeloTipoFase.setConvocatoria(true);
    modeloTipoFase.setProyecto(true);
    modeloTipoFase.setSolicitud(false);
    modeloTipoFase.setActivo(true);

    return modeloTipoFase;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase con un TipoFase con id
   * 
   * @param id id del ModeloTipoFase
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarModeloTipoFaseConTipoFaseId(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setModeloEjecucion(modeloEjecucion);
    modeloTipoFase.setTipoFase(generarMockTipoFase(1L, "TipoFase" + String.format("%03d", 1)));
    modeloTipoFase.setConvocatoria(true);
    modeloTipoFase.setProyecto(true);
    modeloTipoFase.setSolicitud(false);
    modeloTipoFase.setActivo(true);

    return modeloTipoFase;
  }

}
