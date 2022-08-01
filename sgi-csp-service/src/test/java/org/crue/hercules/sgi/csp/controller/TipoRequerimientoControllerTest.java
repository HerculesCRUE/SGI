package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.converter.TipoRequerimientoConverter;
import org.crue.hercules.sgi.csp.dto.TipoRequerimientoOutput;
import org.crue.hercules.sgi.csp.model.TipoRequerimiento;
import org.crue.hercules.sgi.csp.service.TipoRequerimientoService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * TipoRequerimientoControllerTest
 */
@WebMvcTest(TipoRequerimientoController.class)
public class TipoRequerimientoControllerTest extends BaseControllerTest {

  @MockBean
  private TipoRequerimientoService service;
  @MockBean
  private TipoRequerimientoConverter converter;

  private static final String REQUEST_MAPPING = TipoRequerimientoController.REQUEST_MAPPING;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  public void findActivos_WithPaging_ReturnsTipoRequerimientoSubList() throws Exception {
    // given: One hundred TipoRequerimiento
    List<TipoRequerimiento> data = new ArrayList<>();
    for (long i = 1; i <= 100; i++) {
      data.add(generarMockTipoRequerimiento(i));
    }

    BDDMockito.given(service.findActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoRequerimiento>>() {
          @Override
          public Page<TipoRequerimiento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoRequerimiento> content = data.subList(fromIndex, toIndex);
            Page<TipoRequerimiento> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<Page<TipoRequerimiento>>any()))
        .willAnswer(new Answer<Page<TipoRequerimientoOutput>>() {
          @Override
          public Page<TipoRequerimientoOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoRequerimiento> pageInput = invocation.getArgument(0);
            List<TipoRequerimientoOutput> content = pageInput.getContent().stream().map(input -> {
              return generarMockTipoRequerimientoOutput(input.getId(), input.getNombre(), input.getActivo());
            }).collect(Collectors.toList());
            Page<TipoRequerimientoOutput> pageOutput = new PageImpl<>(content, pageInput.getPageable(),
                pageInput.getTotalElements());
            return pageOutput;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(REQUEST_MAPPING).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoRequerimiento are returned with the right page
        // information in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoRequerimientoOutput> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoRequerimientoOutput>>() {
        });

    // containing Nombre='TipoRequerimiento-31' to 'TipoRequerimiento-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoRequerimientoOutput item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("TipoRequerimiento-" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  public void findActivos_EmptyList_Returns204() throws Exception {
    // given: no data TipoRequerimiento
    BDDMockito.given(service.findActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoRequerimiento>>() {
          @Override
          public Page<TipoRequerimiento> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoRequerimiento> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<Page<TipoRequerimiento>>any()))
        .willAnswer(new Answer<Page<TipoRequerimientoOutput>>() {
          @Override
          public Page<TipoRequerimientoOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoRequerimientoOutput> page = new PageImpl<>(Collections.emptyList());
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

  private TipoRequerimiento generarMockTipoRequerimiento(Long id) {
    String nombreSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockTipoRequerimiento(id, "TipoRequerimiento-" + nombreSuffix, Boolean.TRUE);
  }

  private TipoRequerimiento generarMockTipoRequerimiento(Long id, String nombre, Boolean activo) {
    return TipoRequerimiento.builder()
        .activo(activo)
        .id(id)
        .nombre(nombre)
        .build();
  }

  private TipoRequerimientoOutput generarMockTipoRequerimientoOutput(Long id, String nombre, Boolean activo) {
    return TipoRequerimientoOutput.builder()
        .activo(activo)
        .id(id)
        .nombre(nombre)
        .build();
  }
}
