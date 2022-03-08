package org.crue.hercules.sgi.prc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.prc.dto.ConfiguracionCampoOutput;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.service.ConfiguracionCampoService;
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
 * ConfiguracionCampoControllerTest
 */
@WebMvcTest(ConfiguracionCampoController.class)
public class ConfiguracionCampoControllerTest extends BaseControllerTest {

  @MockBean
  private ConfiguracionCampoService service;

  private static final String CONTROLLER_BASE_PATH = ConfiguracionCampoController.MAPPING;

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConfiguracionCampo
    List<ConfiguracionCampo> configuracionesCampo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      configuracionesCampo.add(generarMockConfiguracionCampo(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > configuracionesCampo.size() ? configuracionesCampo.size() : toIndex;
          List<ConfiguracionCampo> content = configuracionesCampo.subList(fromIndex, toIndex);
          Page<ConfiguracionCampo> pageResponse = new PageImpl<>(content, pageable,
              configuracionesCampo.size());
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
        // then: Devuelve la pagina 3 con las ConfiguracionCampo del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<ConfiguracionCampoOutput> configuracionCampoResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConfiguracionCampoOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConfiguracionCampoOutput configuracionCampo = configuracionCampoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(configuracionCampo.getCodigo()).isEqualTo(CodigoCVN.CONVOCATORIA_EXCELENCIA.toString());
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data ConfiguracionCampo
    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConfiguracionCampo>>() {
          @Override
          public Page<ConfiguracionCampo> answer(InvocationOnMock invocation) throws Throwable {
            Page<ConfiguracionCampo> page = new PageImpl<>(Collections.emptyList());
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

  private ConfiguracionCampo generarMockConfiguracionCampo(Long id) {
    ConfiguracionCampo configuracionCampo = new ConfiguracionCampo();
    configuracionCampo.setId(id);
    configuracionCampo.setCodigoCVN(CodigoCVN.CONVOCATORIA_EXCELENCIA);

    return configuracionCampo;
  }
}
