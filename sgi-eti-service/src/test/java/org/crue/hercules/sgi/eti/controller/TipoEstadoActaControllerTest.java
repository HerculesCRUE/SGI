package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoEstadoActaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.service.TipoEstadoActaService;
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
 * TipoEstadoActaControllerTest
 */
@WebMvcTest(TipoEstadoActaController.class)
public class TipoEstadoActaControllerTest extends BaseControllerTest {

  @MockBean
  private TipoEstadoActaService tipoEstadoActaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH = "/tipoestadoactas";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOACTA-VER" })
  public void getTipoEstadoActa_WithId_ReturnsTipoEstadoActa() throws Exception {
    BDDMockito.given(tipoEstadoActaService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoEstadoActa(1L, "TipoEstadoActa1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoEstadoActa1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOACTA-VER" })
  public void getTipoEstadoActa_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoEstadoActaService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoEstadoActaNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOACTA-EDITAR" })
  public void newTipoEstadoActa_ReturnsTipoEstadoActa() throws Exception {
    // given: Un tipo estado acta nuevo
    String nuevoTipoEstadoActaJson = "{\"nombre\": \"TipoEstadoActa1\", \"activo\": \"true\"}";

    TipoEstadoActa tipoEstadoActa = generarMockTipoEstadoActa(1L, "TipoEstadoActa1");

    BDDMockito.given(tipoEstadoActaService.create(ArgumentMatchers.<TipoEstadoActa>any())).willReturn(tipoEstadoActa);

    // when: Creamos un tipo estado acta
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoEstadoActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo estado acta y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoEstadoActa1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOACTA-EDITAR" })
  public void newTipoEstadoActa_Error_Returns400() throws Exception {
    // given: Un tipo estado acta nuevo que produce un error al crearse
    String nuevoTipoEstadoActaJson = "{\"nombre\": \"TipoEstadoActa1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoEstadoActaService.create(ArgumentMatchers.<TipoEstadoActa>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un tipo estado acta
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoEstadoActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOACTA-EDITAR" })
  public void replaceTipoEstadoActa_ReturnsTipoEstadoActa() throws Exception {
    // given: Un tipo estado acta a modificar
    String replaceTipoEstadoActaJson = "{\"id\": 1, \"nombre\": \"TipoEstadoActa1\", \"activo\": \"true\"}";

    TipoEstadoActa tipoEstadoActa = generarMockTipoEstadoActa(1L, "Replace TipoEstadoActa1");

    BDDMockito.given(tipoEstadoActaService.update(ArgumentMatchers.<TipoEstadoActa>any())).willReturn(tipoEstadoActa);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoEstadoActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el tipo estado acta y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace TipoEstadoActa1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOACTA-EDITAR" })
  public void replaceTipoEstadoActa_NotFound() throws Exception {
    // given: Un tipo estado acta a modificar
    String replaceTipoEstadoActaJson = "{\"id\": 1, \"nombre\": \"TipoEstadoActa1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoEstadoActaService.update(ArgumentMatchers.<TipoEstadoActa>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoEstadoActaNotFoundException(((TipoEstadoActa) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoEstadoActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOACTA-EDITAR" })
  public void removeTipoEstadoActa_ReturnsOk() throws Exception {
    BDDMockito.given(tipoEstadoActaService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockTipoEstadoActa(1L, "TipoEstadoActa1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void findAll_Unlimited_ReturnsFullTipoEstadoActaList() throws Exception {
    // given: One hundred TipoEstadoActa
    List<TipoEstadoActa> tipoEstadoActas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEstadoActas.add(generarMockTipoEstadoActa(Long.valueOf(i), "TipoEstadoActa" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEstadoActaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoEstadoActas));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoEstadoActa
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void findAll_Unlimited_ReturnsNoContent() throws Exception {
    // given: TipoEstadoActa empty
    List<TipoEstadoActa> tipoEstadoActas = new ArrayList<>();

    BDDMockito.given(tipoEstadoActaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoEstadoActas));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void findAll_WithPaging_ReturnsTipoEstadoActaSubList() throws Exception {
    // given: One hundred TipoEstadoActa
    List<TipoEstadoActa> tipoEstadoActas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEstadoActas.add(generarMockTipoEstadoActa(Long.valueOf(i), "TipoEstadoActa" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEstadoActaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoEstadoActa>>() {
          @Override
          public Page<TipoEstadoActa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoEstadoActa> content = tipoEstadoActas.subList(fromIndex, toIndex);
            Page<TipoEstadoActa> page = new PageImpl<>(content, pageable, tipoEstadoActas.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoEstadoActas are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoEstadoActa> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoEstadoActa>>() {
        });

    // containing nombre='TipoEstadoActa031' to 'TipoEstadoActa040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoEstadoActa tipoEstadoActa = actual.get(i);
      Assertions.assertThat(tipoEstadoActa.getNombre()).isEqualTo("TipoEstadoActa" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoEstadoActaList() throws Exception {
    // given: One hundred TipoEstadoActa and a search query
    List<TipoEstadoActa> tipoEstadoActas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEstadoActas.add(generarMockTipoEstadoActa(Long.valueOf(i), "TipoEstadoActa" + String.format("%03d", i)));
    }
    String query = "nombre~TipoEstadoActa%,id:5";

    BDDMockito.given(tipoEstadoActaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoEstadoActa>>() {
          @Override
          public Page<TipoEstadoActa> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoEstadoActa> content = new ArrayList<>();
            for (TipoEstadoActa tipoEstadoActa : tipoEstadoActas) {
              if (tipoEstadoActa.getNombre().startsWith("TipoEstadoActa") && tipoEstadoActa.getId().equals(5L)) {
                content.add(tipoEstadoActa);
              }
            }
            Page<TipoEstadoActa> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoEstadoActa
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto TipoEstadoActa
   * 
   * @param id     id del TipoEstadoActa
   * @param nombre la descripción del TipoEstadoActa
   * @return el objeto TipoEstadoActa
   */

  public TipoEstadoActa generarMockTipoEstadoActa(Long id, String nombre) {

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(id);
    tipoEstadoActa.setNombre(nombre);
    tipoEstadoActa.setActivo(Boolean.TRUE);

    return tipoEstadoActa;
  }

}
