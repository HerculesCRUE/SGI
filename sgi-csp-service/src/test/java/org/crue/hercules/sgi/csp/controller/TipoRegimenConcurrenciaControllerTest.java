package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoRegimenConcurrenciaNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.service.TipoRegimenConcurrenciaService;
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
 * TipoRegimenConcurrenciaControllerTest
 */
@WebMvcTest(TipoRegimenConcurrenciaController.class)
class TipoRegimenConcurrenciaControllerTest extends BaseControllerTest {

  @MockBean
  private TipoRegimenConcurrenciaService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/tiporegimenconcurrencias";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TRCO-E" })
  void findById_WithExistingId_ReturnsTipoRegimenConcurrencia() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<TipoRegimenConcurrencia>() {
      @Override
      public TipoRegimenConcurrencia answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockTipoRegimenConcurrencia(id, Boolean.TRUE);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested TipoRegimenConcurrencia is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TRCO-E" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoRegimenConcurrenciaNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void findAll_WithPaging_ReturnsTipoRegimenConcurrenciaSubList() throws Exception {
    // given: One hundred TipoRegimenConcurrencia
    List<TipoRegimenConcurrencia> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoRegimenConcurrencia(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(service.findActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoRegimenConcurrencia>>() {
          @Override
          public Page<TipoRegimenConcurrencia> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoRegimenConcurrencia> content = data.subList(fromIndex, toIndex);
            Page<TipoRegimenConcurrencia> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoRegimenConcurrencia are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoRegimenConcurrencia> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoRegimenConcurrencia>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoRegimenConcurrencia item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void findAll_EmptyList_Returns204() throws Exception {
    // given: no data TipoRegimenConcurrencia
    BDDMockito.given(service.findActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoRegimenConcurrencia>>() {
          @Override
          public Page<TipoRegimenConcurrencia> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoRegimenConcurrencia> page = new PageImpl<>(Collections.emptyList());
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
   * Función que devuelve un objeto TipoRegimenConcurrencia
   * 
   * @param id
   * @param activo
   * @return TipoRegimenConcurrencia
   */
  private TipoRegimenConcurrencia generarMockTipoRegimenConcurrencia(Long id, Boolean activo) {
    return TipoRegimenConcurrencia.builder().id(id).nombre("nombre-" + id).activo(activo).build();
  }

}
