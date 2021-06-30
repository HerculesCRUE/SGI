package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ProrrogaDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.ProrrogaDocumentoService;
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
 * ProrrogaDocumentoControllerTest
 */
@WebMvcTest(ProrrogaDocumentoController.class)
public class ProrrogaDocumentoControllerTest extends BaseControllerTest {

  @MockBean
  private ProrrogaDocumentoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/prorrogadocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_ReturnsProrrogaDocumento() throws Exception {
    // given: new ProrrogaDocumento
    ProrrogaDocumento prorrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    prorrogaDocumento.setId(null);

    BDDMockito.given(service.create(ArgumentMatchers.<ProrrogaDocumento>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProrrogaDocumento newProrrogaDocumento = new ProrrogaDocumento();
          BeanUtils.copyProperties(invocation.getArgument(0), newProrrogaDocumento);
          newProrrogaDocumento.setId(1L);
          return newProrrogaDocumento;
        });

    // when: create ProrrogaDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(prorrogaDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ProrrogaDocumento is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("proyectoProrrogaId").value(prorrogaDocumento.getProyectoProrrogaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(prorrogaDocumento.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value(prorrogaDocumento.getDocumentoRef()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("tipoDocumento.id").value(prorrogaDocumento.getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value(prorrogaDocumento.getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("visible").value(prorrogaDocumento.getVisible()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ProrrogaDocumento with id filled
    ProrrogaDocumento prorrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProrrogaDocumento>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ProrrogaDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(prorrogaDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsProrrogaDocumento() throws Exception {
    // given: Existing ProrrogaDocumento to be updated
    ProrrogaDocumento prorrogaDocumentoExistente = generarMockProrrogaDocumento(1L, 1L, 1L);
    ProrrogaDocumento prorrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    prorrogaDocumento.setComentario("comentario-modificado");

    BDDMockito.given(service.update(ArgumentMatchers.<ProrrogaDocumento>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ProrrogaDocumento
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, prorrogaDocumentoExistente.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(prorrogaDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProrrogaDocumento is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(prorrogaDocumentoExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoProrrogaId")
            .value(prorrogaDocumentoExistente.getProyectoProrrogaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(prorrogaDocumentoExistente.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value(prorrogaDocumentoExistente.getDocumentoRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id")
            .value(prorrogaDocumentoExistente.getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value(prorrogaDocumento.getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("visible").value(prorrogaDocumentoExistente.getVisible()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProrrogaDocumento prorrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);

    BDDMockito.willThrow(new ProrrogaDocumentoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ProrrogaDocumento>any());

    // when: update ProrrogaDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(prorrogaDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void delete_WithExistingId_Return204() throws Exception {
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
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProrrogaDocumentoNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsProrrogaDocumento() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProrrogaDocumento(invocation.getArgument(0), 1L, 1L);
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProrrogaDocumento is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoProrrogaId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("prorroga-documento-" + String.format("%03d", id)))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value("documentoRef-" + String.format("%03d", id)))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("visible").value(Boolean.TRUE)).andExpect(MockMvcResultMatchers
            .jsonPath("comentario").value("comentario-prorroga-documento-" + String.format("%03d", id)));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProrrogaDocumentoNotFoundException(1L);
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
   * Función que devuelve un objeto ProrrogaDocumento
   * 
   * @param id                 id del ProrrogaDocumento
   * @param proyectoProrrogaId id del ProyectoProrroga
   * @return el objeto ProrrogaDocumento
   */
  private ProrrogaDocumento generarMockProrrogaDocumento(Long id, Long proyectoProrrogaId, Long tipoDocumentoId) {

    // @formatter:off
    return ProrrogaDocumento.builder()
        .id(id)
        .proyectoProrrogaId(proyectoProrrogaId)
        .nombre("prorroga-documento-" + (id == null ? "" : String.format("%03d", id)))
        .documentoRef("documentoRef-" + (id == null ? "" : String.format("%03d", id)))
        .tipoDocumento(TipoDocumento.builder().id(tipoDocumentoId).build())
        .comentario("comentario-prorroga-documento-" + (id == null ? "" : String.format("%03d", id)))
        .visible(Boolean.TRUE)
        .build();
    // @formatter:on
  }

}
