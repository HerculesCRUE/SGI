package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;

import org.crue.hercules.sgi.csp.exceptions.SolicitudHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.service.SolicitudHitoService;
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
 * SolicitudHitoTest
 */
@WebMvcTest(SolicitudHitoController.class)
public class SolicitudHitoControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudHitoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudhitos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void create_ReturnsSolicitudHito() throws Exception {
    // given: new SolicitudHito
    SolicitudHito solicitudHito = generarSolicitudHito(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudHito>any())).willAnswer(new Answer<SolicitudHito>() {
      @Override
      public SolicitudHito answer(InvocationOnMock invocation) throws Throwable {
        SolicitudHito givenData = invocation.getArgument(0, SolicitudHito.class);
        SolicitudHito newData = new SolicitudHito();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(1L);
        return newData;
      }
    });

    // when: create SolicitudHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new SolicitudHito is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudId").value(solicitudHito.getSolicitudId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(solicitudHito.getTipoHito().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value(solicitudHito.getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("generaAviso").value(solicitudHito.getGeneraAviso()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a SolicitudHito with id filled
    SolicitudHito solicitudHito = generarSolicitudHito(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudHito>any())).willThrow(new IllegalArgumentException());

    // when: create SolicitudHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_WithExistingId_ReturnsSolicitudHito() throws Exception {
    // given: existing SolicitudHito
    SolicitudHito updatedSolicitudHito = generarSolicitudHito(1L, 1L, 1L);
    updatedSolicitudHito.setComentario("comentario-modificado");

    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudHito>any(), ArgumentMatchers.anyBoolean()))
        .willAnswer(new Answer<SolicitudHito>() {
          @Override
          public SolicitudHito answer(InvocationOnMock invocation) throws Throwable {
            SolicitudHito givenData = invocation.getArgument(0, SolicitudHito.class);
            return givenData;
          }
        });

    // when: update SolicitudHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudHito.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: SolicitudHito is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(updatedSolicitudHito.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudId").value(updatedSolicitudHito.getSolicitudId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(updatedSolicitudHito.getTipoHito().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value(updatedSolicitudHito.getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("generaAviso").value(updatedSolicitudHito.getGeneraAviso()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a SolicitudHito with non existing id
    SolicitudHito updatedSolicitudHito = generarSolicitudHito(1L, 1L, 1L);

    BDDMockito.willThrow(new SolicitudHitoNotFoundException(updatedSolicitudHito.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudHito>any(), ArgumentMatchers.anyBoolean()))
        .willThrow(new SolicitudHitoNotFoundException(updatedSolicitudHito.getId()));

    // when: update SolicitudHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudHito.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudHito)))
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

    BDDMockito.willThrow(new SolicitudHitoNotFoundException(id)).given(service).delete(ArgumentMatchers.<Long>any());

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
  public void findById_WithExistingId_ReturnsSolicitudHito() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<SolicitudHito>() {
      @Override
      public SolicitudHito answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarSolicitudHito(id, 1L, 1L);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudHito is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudHitoNotFoundException(id);
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
   * Función que devuelve un objeto SolicitudHito
   * 
   * @param solicitudHitoId
   * @param solicitudId
   * @param tipoDocumentoId
   * @return el objeto SolicitudHito
   */
  private SolicitudHito generarSolicitudHito(Long solicitudHitoId, Long solicitudId, Long tipoDocumentoId) {

    SolicitudHito solicitudHito = SolicitudHito.builder().id(solicitudHitoId).solicitudId(solicitudId)
        .comentario("comentario-" + solicitudHitoId).fecha(Instant.now()).generaAviso(Boolean.TRUE)
        .tipoHito(TipoHito.builder().id(tipoDocumentoId).build()).build();

    return solicitudHito;
  }

}
