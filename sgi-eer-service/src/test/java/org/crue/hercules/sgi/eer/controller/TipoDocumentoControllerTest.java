package org.crue.hercules.sgi.eer.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.converter.TipoDocumentoConverter;
import org.crue.hercules.sgi.eer.dto.TipoDocumentoOutput;
import org.crue.hercules.sgi.eer.model.TipoDocumento;
import org.crue.hercules.sgi.eer.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * TipoDocumentoControllerTest
 */

@WebMvcTest(TipoDocumentoController.class)
public class TipoDocumentoControllerTest extends BaseControllerTest {
  private static final String REQUEST_MAPPING = TipoDocumentoController.REQUEST_MAPPING;
  private static final String PATH_SUBTIPOS = TipoDocumentoController.PATH_SUBTIPOS;

  @MockBean
  private TipoDocumentoService service;
  @MockBean
  private TipoDocumentoConverter converter;

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-E" })
  public void findTiposActivos_WithPaging_ReturnsTipoDocumentoSubList() throws Exception {
    // given: 40 TipoDocumento
    List<TipoDocumento> data = new ArrayList<>();
    for (int i = 1; i <= 40; i++) {
      data.add(generateTipoDocumentoMock(Long.valueOf(i)));
    }

    List<TipoDocumentoOutput> dataOutput = new ArrayList<>();
    for (int i = 1; i <= 40; i++) {
      dataOutput.add(generateTipoDocumentoOutputMock(Long.valueOf(i)));
    }

    PageRequest paging = PageRequest.of(3, 10);

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Page<TipoDocumento>>any()))
        .willReturn(new PageImpl<TipoDocumentoOutput>(dataOutput, paging, dataOutput.size()));

    BDDMockito.given(service.findTiposActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoDocumento> content = data.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(REQUEST_MAPPING).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoDocumento are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "40"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(40))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoDocumento> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoDocumento>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 1; i < 10; i++, j++) {
      TipoDocumento item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("Nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-C", "EER-EER-E" })
  public void findTiposActivos_EmptyList_Returns204() throws Exception {

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Page<TipoDocumento>>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));
    // given: no data TipoDocumento
    BDDMockito.given(service.findTiposActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoDocumento> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(REQUEST_MAPPING).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-E" })
  public void findSubtiposActivos_WithPaging_ReturnsTipoDocumentoSubList() throws Exception {
    // given: 40 TipoDocumento
    Long padreId = 55L;
    List<TipoDocumento> data = new ArrayList<>();
    for (int i = 1; i <= 40; i++) {
      data.add(generateTipoDocumentoMock(Long.valueOf(i)));
    }

    List<TipoDocumentoOutput> dataOutput = new ArrayList<>();
    for (int i = 1; i <= 40; i++) {
      dataOutput.add(generateTipoDocumentoOutputMock(Long.valueOf(i)));
    }

    PageRequest paging = PageRequest.of(3, 10);

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Page<TipoDocumento>>any()))
        .willReturn(new PageImpl<TipoDocumentoOutput>(dataOutput, paging, dataOutput.size()));

    BDDMockito
        .given(service.findSubtiposActivos(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoDocumento> content = data.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(REQUEST_MAPPING + PATH_SUBTIPOS, padreId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoDocumento are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "40"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(40))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoDocumento> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoDocumento>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 1; i < 10; i++, j++) {
      TipoDocumento item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("Nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-C", "EER-EER-E" })
  public void findSubtiposActivos_EmptyList_Returns204() throws Exception {
    Long padreId = 55L;

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<Page<TipoDocumento>>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));
    // given: no data TipoDocumento
    BDDMockito
        .given(service.findSubtiposActivos(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoDocumento> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(REQUEST_MAPPING + PATH_SUBTIPOS, padreId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private TipoDocumento generateTipoDocumentoMock(Long id) {
    return generateTipoDocumentoMock(id, Boolean.TRUE, "Nombre-" + id.toString(), "Descripcion", null);
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
    return generateTipoDocumentoOutputMock(id, Boolean.TRUE, "Nombre-" + id.toString(), "Descripcion", null);
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
