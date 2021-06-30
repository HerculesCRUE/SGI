package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.eti.service.TipoInvestigacionTuteladaService;
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
 * TipoInvestigacionTuteladaControllerTest
 */
@WebMvcTest(TipoInvestigacionTuteladaController.class)
public class TipoInvestigacionTuteladaControllerTest extends BaseControllerTest {

  @MockBean
  private TipoInvestigacionTuteladaService tipoInvestigacionTuteladaService;

  private static final String TIPO_INVESTIGACION_TUTELADA_CONTROLLER_BASE_PATH = "/tipoinvestigaciontuteladas";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findAll_Unlimited_ReturnsFullTipoInvestigacionTuteladaList() throws Exception {
    // given: One hundred TipoInvestigacionTutelada
    List<TipoInvestigacionTutelada> tipoInvestigacionTuteladas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoInvestigacionTuteladas.add(generarMockTipoInvestigacionTutelada(Long.valueOf(i),
          "TipoInvestigacionTutelada" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            tipoInvestigacionTuteladaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoInvestigacionTuteladas));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_INVESTIGACION_TUTELADA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoInvestigacionTutelada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findAll_WithPaging_ReturnsTipoInvestigacionTuteladaSubList() throws Exception {
    // given: One hundred TipoInvestigacionTutelada
    List<TipoInvestigacionTutelada> tipoInvestigacionTuteladas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoInvestigacionTuteladas.add(generarMockTipoInvestigacionTutelada(Long.valueOf(i),
          "TipoInvestigacionTutelada" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            tipoInvestigacionTuteladaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoInvestigacionTutelada>>() {
          @Override
          public Page<TipoInvestigacionTutelada> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoInvestigacionTutelada> content = tipoInvestigacionTuteladas.subList(fromIndex, toIndex);
            Page<TipoInvestigacionTutelada> page = new PageImpl<>(content, pageable, tipoInvestigacionTuteladas.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_INVESTIGACION_TUTELADA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoInvestigacionTutelada are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoInvestigacionTutelada> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoInvestigacionTutelada>>() {
        });

    // containing nombre='TipoInvestigacionTutelada031' to
    // 'TipoInvestigacionTutelada040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoInvestigacionTutelada tipoInvestigacionTutelada = actual.get(i);
      Assertions.assertThat(tipoInvestigacionTutelada.getNombre())
          .isEqualTo("TipoInvestigacionTutelada" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoInvestigacionTuteladaList() throws Exception {
    // given: One hundred TipoInvestigacionTutelada and a search query
    List<TipoInvestigacionTutelada> tipoInvestigacionTuteladas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoInvestigacionTuteladas.add(generarMockTipoInvestigacionTutelada(Long.valueOf(i),
          "TipoInvestigacionTutelada" + String.format("%03d", i)));
    }
    String query = "nombre~TipoInvestigacionTutelada%,id:5";

    BDDMockito
        .given(
            tipoInvestigacionTuteladaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoInvestigacionTutelada>>() {
          @Override
          public Page<TipoInvestigacionTutelada> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoInvestigacionTutelada> content = new ArrayList<>();
            for (TipoInvestigacionTutelada tipoInvestigacionTutelada : tipoInvestigacionTuteladas) {
              if (tipoInvestigacionTutelada.getNombre().startsWith("TipoInvestigacionTutelada")
                  && tipoInvestigacionTutelada.getId().equals(5L)) {
                content.add(tipoInvestigacionTutelada);
              }
            }
            Page<TipoInvestigacionTutelada> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_INVESTIGACION_TUTELADA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoInvestigacionTutelada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: TipoInvestigacionTutelada empty
    List<TipoInvestigacionTutelada> tipoInvestigacionTutelada = new ArrayList<>();

    BDDMockito
        .given(
            tipoInvestigacionTuteladaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoInvestigacionTutelada));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_INVESTIGACION_TUTELADA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto TipoInvestigacionTutelada
   * 
   * @param id     id del tipoInvestigacionTutelada
   * @param nombre la descripción del tipo de Investigacion Tutelada
   * @return el objeto tipo Investigacion Tutelada
   */

  public TipoInvestigacionTutelada generarMockTipoInvestigacionTutelada(Long id, String nombre) {

    TipoInvestigacionTutelada tipoInvestigacionTutelada = new TipoInvestigacionTutelada();
    tipoInvestigacionTutelada.setId(id);
    tipoInvestigacionTutelada.setNombre(nombre);
    tipoInvestigacionTutelada.setActivo(Boolean.TRUE);

    return tipoInvestigacionTutelada;
  }

}
