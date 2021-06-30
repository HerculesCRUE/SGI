package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.SolicitudDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.SolicitudDocumentoService;
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
 * SolicitudDocumentoControllerTest
 */

@WebMvcTest(SolicitudDocumentoController.class)
public class SolicitudDocumentoControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudDocumentoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicituddocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void create_ReturnsSolicitudDocumento() throws Exception {
    // given: new SolicitudDocumento
    SolicitudDocumento solicitudDocumento = generarSolicitudDocumento(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudDocumento>any()))
        .willAnswer(new Answer<SolicitudDocumento>() {
          @Override
          public SolicitudDocumento answer(InvocationOnMock invocation) throws Throwable {
            SolicitudDocumento givenData = invocation.getArgument(0, SolicitudDocumento.class);
            SolicitudDocumento newData = new SolicitudDocumento();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create SolicitudDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new SolicitudDocumento is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudId").value(solicitudDocumento.getSolicitudId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("tipoDocumento.id").value(solicitudDocumento.getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value(solicitudDocumento.getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value(solicitudDocumento.getDocumentoRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(solicitudDocumento.getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a SolicitudDocumento with id filled
    SolicitudDocumento solicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudDocumento>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_WithExistingId_ReturnsSolicitudDocumento() throws Exception {
    // given: existing SolicitudDocumento
    SolicitudDocumento updatedSolicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);
    updatedSolicitudDocumento.setDocumentoRef("documentoRef-modificado");

    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudDocumento>any()))
        .willAnswer(new Answer<SolicitudDocumento>() {
          @Override
          public SolicitudDocumento answer(InvocationOnMock invocation) throws Throwable {
            SolicitudDocumento givenData = invocation.getArgument(0, SolicitudDocumento.class);
            return givenData;
          }
        });

    // when: update SolicitudDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudDocumento.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: SolicitudDocumento is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(updatedSolicitudDocumento.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudId").value(updatedSolicitudDocumento.getSolicitudId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id")
            .value(updatedSolicitudDocumento.getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value(updatedSolicitudDocumento.getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value(updatedSolicitudDocumento.getDocumentoRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(updatedSolicitudDocumento.getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a SolicitudDocumento with non existing id
    SolicitudDocumento updatedSolicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);

    BDDMockito.willThrow(new SolicitudDocumentoNotFoundException(updatedSolicitudDocumento.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudDocumento>any()))
        .willThrow(new SolicitudDocumentoNotFoundException(updatedSolicitudDocumento.getId()));

    // when: update SolicitudDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudDocumento.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
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
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudDocumentoNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsSolicitudDocumento() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<SolicitudDocumento>() {
      @Override
      public SolicitudDocumento answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarSolicitudDocumento(id, 1L, 1L);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudDocumento is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudDocumentoNotFoundException(id);
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
   * Función que devuelve un objeto SolicitudDocumento
   * 
   * @param solicitudDocumentoId
   * @param convocatoriaId
   * @param areaTematicaId
   * @return el objeto SolicitudDocumento
   */
  private SolicitudDocumento generarSolicitudDocumento(Long solicitudDocumentoId, Long solicitudId,
      Long tipoDocumentoId) {

    SolicitudDocumento solicitudDocumento = SolicitudDocumento.builder().id(solicitudDocumentoId)
        .solicitudId(solicitudId).comentario("comentarios-" + solicitudDocumentoId)
        .documentoRef("documentoRef-" + solicitudDocumentoId).nombre("nombre-" + solicitudDocumentoId)
        .tipoDocumento(TipoDocumento.builder().id(tipoDocumentoId).build()).build();

    return solicitudDocumento;
  }

}
