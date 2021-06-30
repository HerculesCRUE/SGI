package org.crue.hercules.sgi.csp.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.RolSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.service.RolSocioService;
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

/**
 * RolSocioControllerTest
 */
@WebMvcTest(RolSocioController.class)
public class RolSocioControllerTest extends BaseControllerTest {

  @MockBean
  private RolSocioService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/rolsocios";

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithExistingId_ReturnsRolSocio() throws Exception {
    // given: existing id
    RolSocio rolSocioExistente = generarMockRolSocio(1L);
    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(rolSocioExistente);

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested RolSocio is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new RolSocioNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAll_WithPaging_ReturnsRolSocioSubList() throws Exception {
    // given: One hundred RolSocio
    List<RolSocio> rolSocios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {

      if (i % 2 == 0) {
        rolSocios.add(generarMockRolSocio(Long.valueOf(i)));
      }
    }

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolSocio>>() {
          @Override
          public Page<RolSocio> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolSocio> content = rolSocios.subList(fromIndex, toIndex);
            Page<RolSocio> page = new PageImpl<>(content, pageable, rolSocios.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked RolSocio are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "50"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<RolSocio> actual = mapper.readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<RolSocio>>() {
        });

    // containing Codigo='codigo-62' to 'codigo-80'
    for (int i = 0, j = 62; i < 10; i++, j += 2) {
      RolSocio item = actual.get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo(Boolean.TRUE);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data RolSocio
    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolSocio>>() {
          @Override
          public Page<RolSocio> answer(InvocationOnMock invocation) throws Throwable {
            Page<RolSocio> page = new PageImpl<>(Collections.emptyList());
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
   * Función que genera RolSocio
   * 
   * @param rolSocioId
   * @return el rolSocio
   */
  private RolSocio generarMockRolSocio(Long rolSocioId) {

    String suffix = String.format("%03d", rolSocioId);

    // @formatter:off
    RolSocio rolSocio = RolSocio.builder()
        .id(rolSocioId)
        .abreviatura(suffix)
        .nombre("nombre-" + suffix)
        .descripcion("descripcion-" + suffix)
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on

    return rolSocio;
  }
}