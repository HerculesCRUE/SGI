package org.crue.hercules.sgi.prc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.prc.dto.ConfiguracionBaremoOutput;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoNodo;
import org.crue.hercules.sgi.prc.service.ConfiguracionBaremoService;
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
 * ConfiguracionBaremoControllerTest
 */
@WebMvcTest(ConfiguracionBaremoController.class)
class ConfiguracionBaremoControllerTest extends BaseControllerTest {

  @MockBean
  private ConfiguracionBaremoService service;

  private static final String CONTROLLER_BASE_PATH = ConfiguracionBaremoController.MAPPING;
  private static final EpigrafeCVN DEFAULT_DATA_EPIGRAFE_CVN = EpigrafeCVN.E030_040_000_000;
  private static final String DEFAULT_DATA_NOMBRE = "Configuracion Baremo";
  private static final TipoBaremo DEFAULT_DATA_TIPO_BAREMO = TipoBaremo.ARTICULO;
  private static final TipoNodo DEFAULT_DATA_TIPO_NODO = TipoNodo.PESO;

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V" })
  void findActivos_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConfiguracionBaremo
    List<ConfiguracionBaremo> configuracionesBaremo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      configuracionesBaremo.add(generarMockConfiguracionBaremo(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findActivos(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > configuracionesBaremo.size() ? configuracionesBaremo.size() : toIndex;
          List<ConfiguracionBaremo> content = configuracionesBaremo.subList(fromIndex, toIndex);
          Page<ConfiguracionBaremo> pageResponse = new PageImpl<>(content, pageable,
              configuracionesBaremo.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con las ConfiguracionBaremo del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<ConfiguracionBaremoOutput> configuracionBaremoResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConfiguracionBaremoOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConfiguracionBaremoOutput configuracionBaremo = configuracionBaremoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(configuracionBaremo.getNombre()).isEqualTo(DEFAULT_DATA_NOMBRE + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V" })
  void findActivos_EmptyList_Returns204() throws Exception {
    // given: no data ConfiguracionBaremo
    BDDMockito.given(service.findActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConfiguracionBaremo>>() {
          @Override
          public Page<ConfiguracionBaremo> answer(InvocationOnMock invocation) throws Throwable {
            Page<ConfiguracionBaremo> page = new PageImpl<>(Collections.emptyList());
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

  private ConfiguracionBaremo generarMockConfiguracionBaremo(Long id) {
    return this.generarMockConfiguracionBaremo(
        id, DEFAULT_DATA_EPIGRAFE_CVN, DEFAULT_DATA_NOMBRE + String.format("%03d", id),
        DEFAULT_DATA_TIPO_BAREMO, DEFAULT_DATA_TIPO_NODO);
  }

  private ConfiguracionBaremo generarMockConfiguracionBaremo(
      Long id, EpigrafeCVN epigrafeCVN, String nombre, TipoBaremo tipoBaremo, TipoNodo tipoNodo) {
    return ConfiguracionBaremo.builder()
        .id(id)
        .epigrafeCVN(epigrafeCVN)
        .nombre(nombre)
        .tipoBaremo(tipoBaremo)
        .tipoNodo(tipoNodo)
        .build();
  }
}
