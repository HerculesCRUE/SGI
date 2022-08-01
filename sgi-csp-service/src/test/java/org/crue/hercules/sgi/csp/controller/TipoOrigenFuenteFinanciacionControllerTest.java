package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.service.TipoOrigenFuenteFinanciacionService;
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
 * TipoOrigenFuenteFinanciacionControllerTest
 */
@WebMvcTest(TipoOrigenFuenteFinanciacionController.class)
class TipoOrigenFuenteFinanciacionControllerTest extends BaseControllerTest {

  @MockBean
  private TipoOrigenFuenteFinanciacionService tipoOrigenFuenteFinanciacionService;

  private static final String TIPO_ORIGEN_FUENTE_FINANCIACION_CONTROLLER_BASE_PATH = "/tipoorigenfuentefinanciaciones";

  @Test
  @WithMockUser(username = "user")
  void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 TipoOrigenFuenteFinaciacion
    List<TipoOrigenFuenteFinanciacion> tiposOrigenFuenteFinaciacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposOrigenFuenteFinaciacion
          .add(generarMockTipoOrigenFuenteFinanciacion(i, "TipoOrigenFuenteFinanciacion" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(
        tipoOrigenFuenteFinanciacionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoOrigenFuenteFinanciacion>>() {
          @Override
          public Page<TipoOrigenFuenteFinanciacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposOrigenFuenteFinaciacion.size() ? tiposOrigenFuenteFinaciacion.size() : toIndex;
            List<TipoOrigenFuenteFinanciacion> content = tiposOrigenFuenteFinaciacion.subList(fromIndex, toIndex);
            Page<TipoOrigenFuenteFinanciacion> page = new PageImpl<>(content, pageable,
                tiposOrigenFuenteFinaciacion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ORIGEN_FUENTE_FINANCIACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los TipoOrigenFuenteFinanciacion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<TipoOrigenFuenteFinanciacion> tiposOrigenFuenteFinanciacionResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<TipoOrigenFuenteFinanciacion>>() {
        });

    for (int i = 31; i <= 37; i++) {
      TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion = tiposOrigenFuenteFinanciacionResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(tipoOrigenFuenteFinanciacion.getNombre())
          .isEqualTo("TipoOrigenFuenteFinanciacion" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user")
  void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de TipoOrigenFuenteFinanciacion
    List<TipoOrigenFuenteFinanciacion> tiposOrigenFuenteFinanciacion = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(
        tipoOrigenFuenteFinanciacionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoOrigenFuenteFinanciacion>>() {
          @Override
          public Page<TipoOrigenFuenteFinanciacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            Page<TipoOrigenFuenteFinanciacion> page = new PageImpl<>(tiposOrigenFuenteFinanciacion, pageable, 0);
            return page;
          }
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ORIGEN_FUENTE_FINANCIACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto TipoOrigenFuenteFinanciacion
   * 
   * @param id id del TipoOrigenFuenteFinanciacion
   * @return el objeto TipoOrigenFuenteFinanciacion
   */
  TipoOrigenFuenteFinanciacion generarMockTipoOrigenFuenteFinanciacion(Long id) {
    return generarMockTipoOrigenFuenteFinanciacion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoOrigenFuenteFinanciacion
   * 
   * @param id     id del TipoOrigenFuenteFinanciacion
   * @param nombre nombre del TipoOrigenFuenteFinanciacion
   * @return el objeto TipoOrigenFuenteFinanciacion
   */
  TipoOrigenFuenteFinanciacion generarMockTipoOrigenFuenteFinanciacion(Long id, String nombre) {

    TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion = new TipoOrigenFuenteFinanciacion();
    tipoOrigenFuenteFinanciacion.setId(id);
    tipoOrigenFuenteFinanciacion.setActivo(true);
    tipoOrigenFuenteFinanciacion.setNombre(nombre);

    return tipoOrigenFuenteFinanciacion;
  }

}