package org.crue.hercules.sgi.eer.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.converter.EmpresaAdministracionSociedadConverter;
import org.crue.hercules.sgi.eer.converter.EmpresaComposicionSociedadConverter;
import org.crue.hercules.sgi.eer.converter.EmpresaConverter;
import org.crue.hercules.sgi.eer.converter.EmpresaEquipoEmprendedorConverter;
import org.crue.hercules.sgi.eer.converter.EmpresaDocumentoConverter;
import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoOutput;
import org.crue.hercules.sgi.eer.dto.EmpresaInput;
import org.crue.hercules.sgi.eer.dto.EmpresaOutput;
import org.crue.hercules.sgi.eer.dto.TipoDocumentoOutput;
import org.crue.hercules.sgi.eer.exceptions.EmpresaNotFoundException;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.Empresa.EstadoEmpresa;
import org.crue.hercules.sgi.eer.model.Empresa.TipoEmpresa;
import org.crue.hercules.sgi.eer.service.EmpresaAdministracionSociedadService;
import org.crue.hercules.sgi.eer.service.EmpresaComposicionSociedadService;
import org.crue.hercules.sgi.eer.service.EmpresaEquipoEmprendedorService;
import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.crue.hercules.sgi.eer.model.TipoDocumento;
import org.crue.hercules.sgi.eer.service.EmpresaDocumentoService;
import org.crue.hercules.sgi.eer.service.EmpresaService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * EmpresaControllerTest
 */

@WebMvcTest(EmpresaController.class)
public class EmpresaControllerTest extends BaseControllerTest {

  @MockBean
  private EmpresaService service;
  @MockBean
  private EmpresaConverter converter;
  @MockBean
  private EmpresaEquipoEmprendedorService empresaEquipoEmprendedorService;
  @MockBean
  private EmpresaComposicionSociedadService empresaComposicionSociedadService;
  @MockBean
  private EmpresaAdministracionSociedadService empresaAdministracionSociedadService;
  @MockBean
  private EmpresaEquipoEmprendedorConverter empresaEquipoEmprendedorConverter;
  @MockBean
  private EmpresaDocumentoService empresaDocumentoService;
  @MockBean
  private EmpresaDocumentoConverter empresaDocumentoConverter;
  @MockBean
  private EmpresaComposicionSociedadConverter empresaComposicionSociedadConverter;
  @MockBean
  private EmpresaAdministracionSociedadConverter empresaAdministracionSociedadConverter;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String CONTROLLER_BASE_PATH = "/empresas";
  private static final String PATH_DOCUMENTOS = EmpresaController.PATH_DOCUMENTOS;

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-C" })
  public void create_ReturnsEmpresa() throws Exception {
    // given: new Empresa
    Empresa data = generarMockEmpresa(null, Boolean.TRUE);
    EmpresaOutput empresa = generarMockEmpresaOutput(1L, Boolean.TRUE);

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<EmpresaInput>any())).willReturn(generarMockEmpresa(null, Boolean.TRUE));

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Empresa>any())).willReturn(empresa);

    BDDMockito.given(service.create(ArgumentMatchers.<Empresa>any()))
        .willAnswer(new Answer<Empresa>() {
          @Override
          public Empresa answer(InvocationOnMock invocation) throws Throwable {
            Empresa givenData = invocation.getArgument(0, Empresa.class);
            Empresa newData = new Empresa();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create Empresa
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new Empresa is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombreRazonSocial").value(empresa.getNombreRazonSocial()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a Empresa with id filled
    Empresa data = generarMockEmpresa(1L, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<Empresa>any()))
        .willThrow(new IllegalArgumentException());

    // when: create Empresa
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-E" })
  public void update_WithExistingId_ReturnsEmpresa() throws Exception {
    // given: existing Empresa
    Empresa data = generarMockEmpresa(1L, Boolean.TRUE);

    BDDMockito.given(converter.convert(ArgumentMatchers.<Long>any(), ArgumentMatchers.<EmpresaInput>any()))
        .willReturn(data);

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Empresa>any())).willReturn(generarMockEmpresaOutput(1L, Boolean.TRUE));

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<Empresa>() {
      @Override
      public Empresa answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockEmpresa(id, Boolean.FALSE);
      }
    });
    BDDMockito.given(service.update(ArgumentMatchers.<Empresa>any()))
        .willAnswer(new Answer<Empresa>() {
          @Override
          public Empresa answer(InvocationOnMock invocation) throws Throwable {
            Empresa givenData = invocation.getArgument(0, Empresa.class);
            return givenData;
          }
        });

    // when: update Empresa
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Empresa is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(data.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombreRazonSocial").value(data.getNombreRazonSocial()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a Empresa with non existing id
    Empresa data = generarMockEmpresa(1L, Boolean.TRUE);

    BDDMockito.willThrow(new EmpresaNotFoundException(data.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<Empresa>any()))
        .willThrow(new EmpresaNotFoundException(data.getId()));

    // when: update Empresa
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-E" })
  public void update_WithDuplicatedNombre_Returns400() throws Exception {
    // given: a Empresa with duplicated Nombre
    Empresa data = generarMockEmpresa(1L, Boolean.TRUE);

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<Empresa>() {
      @Override
      public Empresa answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockEmpresa(id, Boolean.FALSE);
      }
    });
    BDDMockito.given(service.update(ArgumentMatchers.<Empresa>any()))
        .willThrow(new IllegalArgumentException());

    // when: update Empresa
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-B" })
  public void desactivar_WithExistingId_ReturnEmpresa() throws Exception {
    // given: existing id
    Long idBuscado = 1L;
    Empresa empresa = generarMockEmpresa(idBuscado, Boolean.TRUE);

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Empresa>any())).willReturn(generarMockEmpresaOutput(idBuscado, Boolean.TRUE));

    BDDMockito.given(service.desactivar(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Empresa empresaDisabled = new Empresa();
      BeanUtils.copyProperties(empresa, empresaDisabled);
      empresaDisabled.setActivo(false);
      return empresaDisabled;
    });

    // when: disable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return disabled Empresa
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(idBuscado))
        .andExpect(MockMvcResultMatchers.jsonPath("nombreRazonSocial").value(empresa.getNombreRazonSocial()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;
    BDDMockito.willThrow(new EmpresaNotFoundException(id)).given(service)
        .desactivar(id);

    // when: disable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-V" })
  public void findById_WithExistingId_ReturnsEmpresa() throws Exception {

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Empresa>any())).willReturn(generarMockEmpresaOutput(1L, Boolean.TRUE));
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<Empresa>() {
      @Override
      public Empresa answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockEmpresa(id, Boolean.TRUE);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Empresa is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Empresa>any())).willReturn(generarMockEmpresaOutput(1L, Boolean.TRUE));
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EmpresaNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-C", "EER-EER-E" })
  public void findActivos_WithPaging_ReturnsEmpresaSubList() throws Exception {
    // given: One hundred Empresa
    List<Empresa> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockEmpresa(Long.valueOf(i), Boolean.TRUE));
    }

    List<EmpresaOutput> dataOutput = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      dataOutput.add(generarMockEmpresaOutput(Long.valueOf(i), Boolean.TRUE));
    }

    PageRequest paging = PageRequest.of(3, 10);

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Page<Empresa>>any()))
        .willReturn(new PageImpl<EmpresaOutput>(dataOutput, paging, dataOutput.size()));

    BDDMockito.given(service.findActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Empresa>>() {
          @Override
          public Page<Empresa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Empresa> content = data.subList(fromIndex, toIndex);
            Page<Empresa> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Empresa are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Empresa> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Empresa>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 1; i < 10; i++, j++) {
      Empresa item = actual.get(i);
      Assertions.assertThat(item.getNombreRazonSocial()).isEqualTo("nombreRazonSocial-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-C", "EER-EER-E" })
  public void findActivos_EmptyList_Returns204() throws Exception {

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Page<Empresa>>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));
    // given: no data Empresa
    BDDMockito.given(service.findActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Empresa>>() {
          @Override
          public Page<Empresa> answer(InvocationOnMock invocation) throws Throwable {
            Page<Empresa> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto Empresa
   * 
   * @param id
   * @param activo
   * @return Empresa
   */
  private Empresa generarMockEmpresa(Long id, Boolean activo) {
    return Empresa.builder().id(id).fechaSolicitud(Instant.now()).tipoEmpresa(TipoEmpresa.EBT)
        .estado(EstadoEmpresa.EN_TRAMITACION).objetoSocial("objetoSocial")
        .conocimientoTecnologia("conocimientoTecnologia").nombreRazonSocial("nombreRazonSocial-" + id).activo(activo)
        .build();
  }

  /**
   * Función que devuelve un objeto Empresa
   * 
   * @param id
   * @param activo
   * @return Empresa
   */
  private EmpresaOutput generarMockEmpresaOutput(Long id, Boolean activo) {
    return EmpresaOutput.builder().id(id).fechaSolicitud(Instant.now()).tipoEmpresa(TipoEmpresa.EBT)
        .estado(EstadoEmpresa.EN_TRAMITACION).objetoSocial("objetoSocial")
        .conocimientoTecnologia("conocimientoTecnologia").nombreRazonSocial("nombreRazonSocial-" + id).activo(activo)
        .build();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-E" })
  public void findDocumentos_WithPaging_ReturnsEmpresaDocumentoSubList() throws Exception {
    // given: 40 EmpresaDocumento con empresaId
    Long empresaId = 1L;
    List<EmpresaDocumento> data = new ArrayList<>();
    for (int i = 1; i <= 40; i++) {
      data.add(generateEmpresaDocumentoMock(Long.valueOf(i)));
    }

    List<EmpresaDocumentoOutput> dataOutput = new ArrayList<>();
    for (int i = 1; i <= 40; i++) {
      dataOutput.add(generateEmpresaDocumentoOutputMock(Long.valueOf(i)));
    }

    PageRequest paging = PageRequest.of(3, 10);

    BDDMockito.given(empresaDocumentoConverter.convert(
        ArgumentMatchers.<Page<EmpresaDocumento>>any()))
        .willReturn(new PageImpl<EmpresaDocumentoOutput>(dataOutput, paging, dataOutput.size()));

    BDDMockito
        .given(empresaDocumentoService.findAllByEmpresaId(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EmpresaDocumento>>() {
          @Override
          public Page<EmpresaDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<EmpresaDocumento> content = data.subList(fromIndex, toIndex);
            Page<EmpresaDocumento> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_DOCUMENTOS, empresaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Empresa are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "40"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(40))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<EmpresaDocumento> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<EmpresaDocumento>>() {
        });

    // containing Nombre='Documento-31' to 'Documento-40'
    for (int i = 0, j = 1; i < 10; i++, j++) {
      EmpresaDocumento item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("Documento-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-E" })
  public void findDocumentos_EmptyList_Returns204() throws Exception {
    Long empresaId = 1L;
    BDDMockito.given(empresaDocumentoConverter.convert(
        ArgumentMatchers.<Page<EmpresaDocumento>>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));
    // given: no data EmpresaDocumento
    BDDMockito
        .given(empresaDocumentoService.findAllByEmpresaId(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EmpresaDocumento>>() {
          @Override
          public Page<EmpresaDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Page<EmpresaDocumento> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_DOCUMENTOS, empresaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private EmpresaDocumento generateEmpresaDocumentoMock(Long id) {
    return generateEmpresaDocumentoMock(id, 1L,
        generateTipoDocumentoMock(id), "Documento-" + id.toString(), "Comentario", "documento-ref-1");
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
        generateTipoDocumentoOutputMock(id), "Documento-" + id.toString(), "Comentario", "documento-ref-1");
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
