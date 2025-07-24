package org.crue.hercules.sgi.csp.controller;

import java.util.HashSet;
import java.util.Set;

import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumentoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFaseDescripcion;
import org.crue.hercules.sgi.csp.model.TipoFaseNombre;
import org.crue.hercules.sgi.csp.service.ModeloTipoDocumentoService;
import org.crue.hercules.sgi.framework.i18n.Language;
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
 * ModeloTipoDocumentoControllerTest
 */
@WebMvcTest(ModeloTipoDocumentoController.class)
class ModeloTipoDocumentoControllerTest extends BaseControllerTest {

  @MockBean
  private ModeloTipoDocumentoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelotipodocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  void create_ReturnsModeloTipoDocumento() throws Exception {
    // given: new ModeloTipoDocumento
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloTipoDocumento>any()))
        .willAnswer(new Answer<ModeloTipoDocumento>() {
          @Override
          public ModeloTipoDocumento answer(InvocationOnMock invocation) throws Throwable {
            ModeloTipoDocumento givenData = invocation.getArgument(0, ModeloTipoDocumento.class);
            ModeloTipoDocumento newData = new ModeloTipoDocumento();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ModeloTipoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modeloTipoDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ModeloTipoDocumento is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("modeloEjecucion.id")
            .value(modeloTipoDocumento.getModeloEjecucion().getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("tipoDocumento.id").value(modeloTipoDocumento.getTipoDocumento().getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("modeloTipoFase.id").value(modeloTipoDocumento.getModeloTipoFase().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  void create_WithId_Returns400() throws Exception {
    // given: a ModeloTipoDocumento with id filled
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloTipoDocumento>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ModeloTipoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modeloTipoDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  void create_WithNonExistingModeloEjecucionId_Returns404() throws Exception {
    // given: a ModeloTipoDocumento with non existing ModeloEjecucionId
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloTipoDocumento>any()))
        .willThrow(new ModeloEjecucionNotFoundException(modeloTipoDocumento.getModeloEjecucion().getId()));

    // when: create ModeloTipoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modeloTipoDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willReturn(modeloTipoDocumento);

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, modeloTipoDocumento.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  void delete_NonExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ModeloTipoDocumentoNotFoundException(id)).given(service)
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

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id, String nombre) {
    Set<TipoDocumentoNombre> nombreTipoDocumento = new HashSet<>();
    nombreTipoDocumento.add(new TipoDocumentoNombre(Language.ES, nombre));

    Set<TipoDocumentoDescripcion> descripcionTipoDocumento = new HashSet<>();
    descripcionTipoDocumento.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-" + id));

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombreTipoDocumento);
    tipoDocumento.setDescripcion(descripcionTipoDocumento);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento
   * 
   * @param id id del ModeloTipoDocumento
   * @return el objeto ModeloTipoDocumento
   */
  private ModeloTipoDocumento generarMockModeloTipoDocumento(Long id) {
    return generarMockModeloTipoDocumento(id, id, id);
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento
   * 
   * @param id               id del ModeloTipoDocumento
   * @param idTipoDocumento  id idTipoDocumento del TipoDocumento
   * @param idModeloTipoFase id del ModeloTipoFase
   * @return el objeto ModeloTipoDocumento
   */
  private ModeloTipoDocumento generarMockModeloTipoDocumento(Long id, Long idTipoDocumento, Long idModeloTipoFase) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoDocumento modeloTipoDocumento = new ModeloTipoDocumento();
    modeloTipoDocumento.setId(id);
    modeloTipoDocumento.setModeloEjecucion(modeloEjecucion);
    modeloTipoDocumento.setTipoDocumento(
        generarMockTipoDocumento(idTipoDocumento, "TipoDocumento" + String.format("%03d", idTipoDocumento)));
    if (idModeloTipoFase != null) {
      modeloTipoDocumento.setModeloTipoFase(generarMockModeloTipoFase(idModeloTipoFase, idModeloTipoFase));
    }

    modeloTipoDocumento.setActivo(true);

    return modeloTipoDocumento;
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento
   * 
   * @param id id del ModeloTipoDocumento
   * @param id idTipoDocumento del TipoDocumento
   * @return el objeto ModeloTipoDocumento
   */
  private ModeloTipoFase generarMockModeloTipoFase(Long id, Long idTipoFase) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    Set<TipoFaseNombre> nombreTipoFase = new HashSet<>();
    nombreTipoFase.add(new TipoFaseNombre(Language.ES, "nombre-" + idTipoFase));

    Set<TipoFaseDescripcion> descripcionTipoFase = new HashSet<>();
    descripcionTipoFase.add(new TipoFaseDescripcion(Language.ES, "descripcion-" + idTipoFase));

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(idTipoFase);
    tipoFase.setNombre(nombreTipoFase);
    tipoFase.setDescripcion(descripcionTipoFase);
    tipoFase.setActivo(Boolean.TRUE);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setActivo(true);
    modeloTipoFase.setTipoFase(tipoFase);

    return modeloTipoFase;
  }

}
