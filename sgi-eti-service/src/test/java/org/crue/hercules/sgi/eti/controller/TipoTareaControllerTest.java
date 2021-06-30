package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoTareaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.service.TipoTareaService;
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
 * TipoTareaControllerTest
 */
@WebMvcTest(TipoTareaController.class)
public class TipoTareaControllerTest extends BaseControllerTest {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_TAREA_CONTROLLER_BASE_PATH = "/tipostarea";

  @MockBean
  private TipoTareaService tipoTareaService;

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-VER" })
  public void getTipoTarea_WithId_ReturnsTipoTarea() throws Exception {
    BDDMockito.given(tipoTareaService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoTarea(1L, "TipoTarea1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoTarea1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-VER" })
  public void getTipoTarea_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoTareaService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoTareaNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-EDITAR" })
  public void newTipoTarea_ReturnsTipoTarea() throws Exception {
    // given: Un tipo tarea nuevo
    String nuevoTipoTareaJson = "{\"id\": 1, \"nombre\": \"TipoTarea1\", \"activo\": \"true\"}";

    TipoTarea tipoTarea = generarMockTipoTarea(1L, "TipoTarea1");

    BDDMockito.given(tipoTareaService.create(ArgumentMatchers.<TipoTarea>any())).willReturn(tipoTarea);

    // when: Creamos un tipo tarea
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_TAREA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoTareaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo tarea y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoTarea1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-EDITAR" })
  public void newTipoTarea_Error_Returns400() throws Exception {
    // given: Un tipo tarea nuevo que produce un error al crearse
    String nuevoTipoTareaJson = "{\"id\": 1, \"nombre\": \"TipoTarea1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoTareaService.create(ArgumentMatchers.<TipoTarea>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un tipo tarea
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_TAREA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoTareaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-EDITAR" })
  public void replaceTipoTarea_ReturnsTipoTarea() throws Exception {
    // given: Un tipo tarea a modificar
    String replaceTipoTareaJson = "{\"id\": 1, \"nombre\": \"TipoTarea1\", \"activo\": \"true\"}";

    TipoTarea tipoTarea = generarMockTipoTarea(1L, "Replace TipoTarea1");

    BDDMockito.given(tipoTareaService.update(ArgumentMatchers.<TipoTarea>any())).willReturn(tipoTarea);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoTareaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el tipo tarea y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace TipoTarea1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-EDITAR" })
  public void replaceTipoTarea_NotFound() throws Exception {
    // given: Un tipo tarea a modificar
    String replaceTipoTareaJson = "{\"id\": 1, \"nombre\": \"TipoTarea1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoTareaService.update(ArgumentMatchers.<TipoTarea>any())).will((InvocationOnMock invocation) -> {
      throw new TipoTareaNotFoundException(((TipoTarea) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoTareaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-EDITAR" })
  public void removeTipoTarea_ReturnsOk() throws Exception {
    BDDMockito.given(tipoTareaService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockTipoTarea(1L, "TipoTarea1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-VER" })
  public void findAll_Unlimited_ReturnsFullTipoTareaList() throws Exception {
    // given: One hundred tipos tarea
    List<TipoTarea> tiposTarea = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tiposTarea.add(generarMockTipoTarea(Long.valueOf(i), "TipoTarea" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoTareaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tiposTarea));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_TAREA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred tipos tarea
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-VER" })
  public void findAll_ReturnsNotFound() throws Exception {
    // given: tipos tarea empty
    List<TipoTarea> tiposTarea = new ArrayList<>();
    tiposTarea.isEmpty();

    BDDMockito.given(tipoTareaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tiposTarea));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_TAREA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-VER" })
  public void findAll_WithPaging_ReturnsTipoTareaSubList() throws Exception {
    // given: One hundred tipos tarea
    List<TipoTarea> tiposTarea = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tiposTarea.add(generarMockTipoTarea(Long.valueOf(i), "TipoTarea" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoTareaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoTarea>>() {
          @Override
          public Page<TipoTarea> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoTarea> content = tiposTarea.subList(fromIndex, toIndex);
            Page<TipoTarea> page = new PageImpl<>(content, pageable, tiposTarea.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_TAREA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked tipos tarea are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoTarea> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoTarea>>() {
        });

    // containing nombre='TipoTarea031' to 'TipoTarea040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoTarea tipoTarea = actual.get(i);
      Assertions.assertThat(tipoTarea.getNombre()).isEqualTo("TipoTarea" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOTAREA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoTareaList() throws Exception {
    // given: One hundred tipos tarea and a search query
    List<TipoTarea> tiposTarea = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tiposTarea.add(generarMockTipoTarea(Long.valueOf(i), "TipoTarea" + String.format("%03d", i)));
    }
    String query = "nombre~TipoTarea%,id:5";

    BDDMockito.given(tipoTareaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoTarea>>() {
          @Override
          public Page<TipoTarea> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoTarea> content = new ArrayList<>();
            for (TipoTarea tipoTarea : tiposTarea) {
              if (tipoTarea.getNombre().startsWith("TipoTarea") && tipoTarea.getId().equals(5L)) {
                content.add(tipoTarea);
              }
            }
            Page<TipoTarea> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_TAREA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred tipos tarea
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto TipoTarea
   * 
   * @param id     id del tipoTarea
   * @param nombre la descripción del tipo de tarea
   * @return el objeto tipo tarea
   */
  public TipoTarea generarMockTipoTarea(Long id, String nombre) {
    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(id);
    tipoTarea.setNombre(nombre);
    tipoTarea.setActivo(Boolean.TRUE);

    return tipoTarea;
  }

}
