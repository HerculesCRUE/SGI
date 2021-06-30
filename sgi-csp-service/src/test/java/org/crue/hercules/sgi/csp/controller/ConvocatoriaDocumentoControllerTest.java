package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.service.ConvocatoriaDocumentoService;
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
 * ConvocatoriaDocumentoControllerTest
 */
@WebMvcTest(ConvocatoriaDocumentoController.class)
public class ConvocatoriaDocumentoControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaDocumentoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriadocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_ReturnsConvocatoriaDocumento() throws Exception {
    // given: new ConvocatoriaDocumento
    ConvocatoriaDocumento convocatoriaDocumento = generarMockConvocatoriaDocumento(1L);
    convocatoriaDocumento.setId(null);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaDocumento>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ConvocatoriaDocumento newConvocatoriaDocumento = new ConvocatoriaDocumento();
          BeanUtils.copyProperties(invocation.getArgument(0), newConvocatoriaDocumento);
          newConvocatoriaDocumento.setId(1L);
          return newConvocatoriaDocumento;
        });

    // when: create ConvocatoriaDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConvocatoriaDocumento is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaDocumento.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFase.id").value(convocatoriaDocumento.getTipoFase().getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("tipoDocumento.id").value(convocatoriaDocumento.getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(convocatoriaDocumento.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("publico").value(convocatoriaDocumento.getPublico()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(convocatoriaDocumento.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value(convocatoriaDocumento.getDocumentoRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ConvocatoriaDocumento with id filled
    ConvocatoriaDocumento convocatoriaDocumento = generarMockConvocatoriaDocumento(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaDocumento>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConvocatoriaDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_ReturnsConvocatoriaDocumento() throws Exception {
    // given: Existing ConvocatoriaDocumento to be updated
    ConvocatoriaDocumento convocatoriaDocumentoExistente = generarMockConvocatoriaDocumento(1L);
    ConvocatoriaDocumento convocatoriaDocumento = generarMockConvocatoriaDocumento(1L);
    convocatoriaDocumento.setNombre("nombre-modificado");
    convocatoriaDocumento.setObservaciones("observaciones-modificadas");
    convocatoriaDocumento.setDocumentoRef("documentoRef-modificado");

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(convocatoriaDocumentoExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaDocumento>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ConvocatoriaDocumento
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaDocumentoExistente.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConvocatoriaDocumento is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaDocumentoExistente.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaDocumentoExistente.getConvocatoriaId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("tipoFase.id").value(convocatoriaDocumentoExistente.getTipoFase().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id")
            .value(convocatoriaDocumentoExistente.getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(convocatoriaDocumento.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("publico").value(convocatoriaDocumentoExistente.getPublico()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(convocatoriaDocumento.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value(convocatoriaDocumento.getDocumentoRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConvocatoriaDocumento convocatoriaDocumento = generarMockConvocatoriaDocumento(1L);

    BDDMockito.willThrow(new ConvocatoriaDocumentoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ConvocatoriaDocumento>any());

    // when: update ConvocatoriaDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void delete_WithoutId_Return400() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaDocumentoNotFoundException(id)).given(service)
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaDocumentoNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsConvocatoriaDocumento() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockConvocatoriaDocumento(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaDocumento is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaDocumentoNotFoundException(1L);
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
   * Función que devuelve un objeto ConvocatoriaDocumento
   * 
   * @param id id del ConvocatoriaDocumento
   * @return el objeto ConvocatoriaDocumento
   */
  private ConvocatoriaDocumento generarMockConvocatoriaDocumento(Long id) {

    TipoFase tipoFase = TipoFase.builder().id(id).build();
    TipoDocumento tipoDocumento = TipoDocumento.builder().id(id).build();

    // @formatter:off
    return ConvocatoriaDocumento.builder()
        .id(id)
        .convocatoriaId(1L)
        .tipoFase(tipoFase)
        .tipoDocumento(tipoDocumento)
        .nombre("nombre doc-" + id)
        .publico(Boolean.TRUE)
        .observaciones("observaciones-" + id)
        .documentoRef("documentoRef" + id)
        .build();
    // @formatter:on
  }

}