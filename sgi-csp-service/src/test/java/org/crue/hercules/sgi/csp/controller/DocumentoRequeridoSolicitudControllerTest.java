package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.DocumentoRequeridoSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.DocumentoRequeridoSolicitudService;
import org.crue.hercules.sgi.csp.service.ProgramaService;
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
 * DocumentoRequeridoSolicitudControllerTest
 */
@WebMvcTest(DocumentoRequeridoSolicitudController.class)
public class DocumentoRequeridoSolicitudControllerTest extends BaseControllerTest {

  @MockBean
  private DocumentoRequeridoSolicitudService service;

  @MockBean
  private ProgramaService programaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/documentorequiridosolicitudes";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_ReturnsModeloDocumentoRequeridoSolicitud() throws Exception {
    // given: new DocumentoRequeridoSolicitud
    DocumentoRequeridoSolicitud documentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    documentoRequeridoSolicitud.setId(null);

    BDDMockito.given(service.create(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = new DocumentoRequeridoSolicitud();
          BeanUtils.copyProperties(invocation.getArgument(0), newDocumentoRequeridoSolicitud);
          newDocumentoRequeridoSolicitud.setId(1L);
          return newDocumentoRequeridoSolicitud;
        });

    // when: create DocumentoRequeridoSolicitud
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(documentoRequeridoSolicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new DocumentoRequeridoSolicitud is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("configuracionSolicitudId")
            .value(documentoRequeridoSolicitud.getConfiguracionSolicitudId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id")
            .value(documentoRequeridoSolicitud.getTipoDocumento().getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value(documentoRequeridoSolicitud.getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a DocumentoRequeridoSolicitud with id filled
    DocumentoRequeridoSolicitud documentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willThrow(new IllegalArgumentException());

    // when: create DocumentoRequeridoSolicitud
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(documentoRequeridoSolicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_ReturnsDocumentoRequeridoSolicitud() throws Exception {
    // given: Existing DocumentoRequeridoSolicitud to be updated
    DocumentoRequeridoSolicitud documentoRequeridoSolicitudExistente = generarMockDocumentoRequeridoSolicitud(1L);
    DocumentoRequeridoSolicitud documentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L);
    documentoRequeridoSolicitud.setObservaciones("observaciones-nuevas");

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(documentoRequeridoSolicitudExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update DocumentoRequeridoSolicitud
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, documentoRequeridoSolicitudExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(documentoRequeridoSolicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: DocumentoRequeridoSolicitud is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(documentoRequeridoSolicitudExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("configuracionSolicitudId")
            .value(documentoRequeridoSolicitudExistente.getConfiguracionSolicitudId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id")
            .value(documentoRequeridoSolicitudExistente.getTipoDocumento().getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value(documentoRequeridoSolicitud.getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    DocumentoRequeridoSolicitud documentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(id);

    BDDMockito.willThrow(new DocumentoRequeridoSolicitudNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<DocumentoRequeridoSolicitud>any());

    // when: update DocumentoRequeridoSolicitud
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(documentoRequeridoSolicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new DocumentoRequeridoSolicitudNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsDocumentoRequeridoSolicitud() throws Exception {
    // given: existing id
    Long id = 1L;
    DocumentoRequeridoSolicitud documentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(id);

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willReturn((documentoRequeridoSolicitud));

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested DocumentoRequeridoSolicitud is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(documentoRequeridoSolicitud.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("configuracionSolicitudId")
            .value(documentoRequeridoSolicitud.getConfiguracionSolicitudId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id")
            .value(documentoRequeridoSolicitud.getTipoDocumento().getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value(documentoRequeridoSolicitud.getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new DocumentoRequeridoSolicitudNotFoundException(1L);
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
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id) {

    // @formatter:off
    return TipoDocumento.builder()
        .id(id)
        .nombre("nombreTipoDocumento-" + id)
        .descripcion("descripcionTipoDocumento-" + id)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto DocumentoRequeridoSolicitud
   * 
   * @param id id del DocumentoRequeridoSolicitud
   * @return el objeto DocumentoRequeridoSolicitud
   */
  private DocumentoRequeridoSolicitud generarMockDocumentoRequeridoSolicitud(Long id) {

    // @formatter:off
    return DocumentoRequeridoSolicitud.builder()
        .id(id)
        .configuracionSolicitudId(id)
        .tipoDocumento(generarMockTipoDocumento(id))
        .observaciones("observacionesDocumentoRequeridoSolicitud-" + id)
        .build();
    // @formatter:on
  }

}
