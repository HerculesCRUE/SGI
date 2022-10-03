package org.crue.hercules.sgi.eer.controller;

import org.crue.hercules.sgi.eer.converter.EmpresaDocumentoConverter;
import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoInput;
import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoOutput;
import org.crue.hercules.sgi.eer.dto.TipoDocumentoOutput;
import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.crue.hercules.sgi.eer.model.TipoDocumento;
import org.crue.hercules.sgi.eer.service.EmpresaDocumentoService;
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
 * EmpresaDocumentoController
 */

@WebMvcTest(EmpresaDocumentoController.class)
class EmpresaDocumentoControllerTest extends BaseControllerTest {
  private static final String REQUEST_MAPPING = EmpresaDocumentoController.REQUEST_MAPPING;
  private static final String PATH_ID = EmpresaDocumentoController.PATH_ID;

  @MockBean
  private EmpresaDocumentoService service;
  @MockBean
  private EmpresaDocumentoConverter converter;

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-E" })
  void create_ReturnsEmpresaDocumento() throws Exception {
    // given: new EmpresaDocumento
    EmpresaDocumentoInput input = generateEmpresaDocumentoInputMock();
    EmpresaDocumentoOutput output = generateEmpresaDocumentoOutputMock(1L);

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<EmpresaDocumentoInput>any())).willReturn(generateEmpresaDocumentoMock(null));

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<EmpresaDocumento>any())).willReturn(output);

    BDDMockito.given(service.create(ArgumentMatchers.<EmpresaDocumento>any()))
        .willAnswer(new Answer<EmpresaDocumento>() {
          @Override
          public EmpresaDocumento answer(InvocationOnMock invocation) throws Throwable {
            EmpresaDocumento givenData = invocation.getArgument(0, EmpresaDocumento.class);
            EmpresaDocumento newData = new EmpresaDocumento();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create EmpresaDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(REQUEST_MAPPING).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new EmpresaDocumento is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(output.getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-E" })
  void update_ReturnsEmpresaDocumento() throws Exception {
    // given: existing EmpresaDocumento
    Long empresaDocumentoId = 1L;
    EmpresaDocumentoInput input = generateEmpresaDocumentoInputMock();
    EmpresaDocumentoOutput output = generateEmpresaDocumentoOutputMock(empresaDocumentoId);

    BDDMockito.given(converter.convert(ArgumentMatchers.<Long>any(), ArgumentMatchers.<EmpresaDocumentoInput>any()))
        .willReturn(generateEmpresaDocumentoMock(empresaDocumentoId));

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<EmpresaDocumento>any())).willReturn(output);

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<EmpresaDocumento>() {
      @Override
      public EmpresaDocumento answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generateEmpresaDocumentoMock(id);
      }
    });
    BDDMockito.given(service.update(ArgumentMatchers.<EmpresaDocumento>any()))
        .willAnswer(new Answer<EmpresaDocumento>() {
          @Override
          public EmpresaDocumento answer(InvocationOnMock invocation) throws Throwable {
            EmpresaDocumento givenData = invocation.getArgument(0, EmpresaDocumento.class);
            return givenData;
          }
        });

    // when: update EmpresaDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.put(REQUEST_MAPPING + PATH_ID, empresaDocumentoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: EmpresaDocumento is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(output.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(output.getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-E" })
  void delete_Returns204() throws Exception {
    // given: existing EmpresaDocumento
    Long empresaDocumentoId = 1L;

    mockMvc
        .perform(MockMvcRequestBuilders.delete(REQUEST_MAPPING + PATH_ID, empresaDocumentoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: EmpresaDocumento is updated
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-V" })
  void findById_ReturnsEmpresaDocumento() throws Exception {
    // given: existing id
    Long empresaDocumentoId = 1L;
    BDDMockito.given(converter.convert(
        ArgumentMatchers.<EmpresaDocumento>any())).willReturn(generateEmpresaDocumentoOutputMock(empresaDocumentoId));
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<EmpresaDocumento>() {
      @Override
      public EmpresaDocumento answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generateEmpresaDocumentoMock(id);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(REQUEST_MAPPING + PATH_ID, empresaDocumentoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested EmpresaDocumento is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(empresaDocumentoId));
  }

  private EmpresaDocumento generateEmpresaDocumentoMock(Long id) {
    return generateEmpresaDocumentoMock(id, 1L,
        generateTipoDocumentoMock(id), "Documento", "Comentario", "documento-ref-1");
  }

  private EmpresaDocumento generateEmpresaDocumentoMock(Long id, Long empresaId, TipoDocumento tipoDocumento,
      String nombre,
      String comentarios, String documentoRef) {
    return EmpresaDocumento.builder()
        .comentarios(comentarios)
        .documentoRef(documentoRef)
        .empresaId(empresaId)
        .id(id)
        .nombre(nombre)
        .tipoDocumento(tipoDocumento)
        .build();
  }

  private EmpresaDocumentoOutput generateEmpresaDocumentoOutputMock(Long id) {
    return generateEmpresaDocumentoOutputMock(id, 1L,
        generateTipoDocumentoOutputMock(id), "Documento", "Comentario", "documento-ref-1");
  }

  private EmpresaDocumentoOutput generateEmpresaDocumentoOutputMock(Long id, Long empresaId,
      TipoDocumentoOutput tipoDocumento, String nombre, String comentarios, String documentoRef) {
    return EmpresaDocumentoOutput.builder()
        .comentarios(comentarios)
        .documentoRef(documentoRef)
        .empresaId(empresaId)
        .id(id)
        .nombre(nombre)
        .tipoDocumento(tipoDocumento)
        .build();
  }

  private EmpresaDocumentoInput generateEmpresaDocumentoInputMock() {
    return generateEmpresaDocumentoInputMock(1L,
        1L, "Documento", "Comentario", "documento-ref-1");
  }

  private EmpresaDocumentoInput generateEmpresaDocumentoInputMock(Long empresaId,
      Long tipoDocumentoId, String nombre, String comentarios, String documentoRef) {
    return EmpresaDocumentoInput.builder()
        .comentarios(comentarios)
        .documentoRef(documentoRef)
        .empresaId(empresaId)
        .nombre(nombre)
        .tipoDocumentoId(tipoDocumentoId)
        .build();
  }

  private TipoDocumento generateTipoDocumentoMock(Long id) {
    return generateTipoDocumentoMock(id, Boolean.TRUE, "Nombre", "Descripcion", null);
  }

  private TipoDocumento generateTipoDocumentoMock(Long id, Boolean activo, String nombre, String descripcion,
      TipoDocumento padre) {
    return TipoDocumento.builder()
        .activo(activo)
        .descripcion(descripcion)
        .id(id)
        .nombre(nombre)
        .padre(padre)
        .build();
  }

  private TipoDocumentoOutput generateTipoDocumentoOutputMock(Long id) {
    return generateTipoDocumentoOutputMock(id, Boolean.TRUE, "Nombre", "Descripcion", null);
  }

  private TipoDocumentoOutput generateTipoDocumentoOutputMock(Long id, Boolean activo, String nombre,
      String descripcion, TipoDocumentoOutput padre) {
    return TipoDocumentoOutput.builder()
        .activo(activo)
        .descripcion(descripcion)
        .id(id)
        .nombre(nombre)
        .padre(padre)
        .build();
  }
}
