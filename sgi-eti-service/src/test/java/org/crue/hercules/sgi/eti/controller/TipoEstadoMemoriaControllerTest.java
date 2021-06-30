package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoEstadoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.service.TipoEstadoMemoriaService;
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
 * TipoEstadoMemoriaControllerTest
 */
@WebMvcTest(TipoEstadoMemoriaController.class)
public class TipoEstadoMemoriaControllerTest extends BaseControllerTest {

  @MockBean
  private TipoEstadoMemoriaService tipoEstadoMemoriaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH = "/tipoestadomemorias";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-VER" })
  public void getTipoEstadoMemoria_WithId_ReturnsTipoEstadoMemoria() throws Exception {
    BDDMockito.given(tipoEstadoMemoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoEstadoMemoria(1L, "TipoEstadoMemoria1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoEstadoMemoria1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-VER" })
  public void getTipoEstadoMemoria_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoEstadoMemoriaService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoEstadoMemoriaNotFoundException(invocation.getArgument(0));
        });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-EDITAR" })
  public void newTipoEstadoMemoria_ReturnsTipoEstadoMemoria() throws Exception {
    // given: Un tipo estado memoria nuevo
    String nuevoTipoEstadoMemoriaJson = "{\"nombre\": \"TipoEstadoMemoria1\", \"activo\": \"true\"}";

    TipoEstadoMemoria tipoEstadoMemoria = generarMockTipoEstadoMemoria(1L, "TipoEstadoMemoria1");

    BDDMockito.given(tipoEstadoMemoriaService.create(ArgumentMatchers.<TipoEstadoMemoria>any()))
        .willReturn(tipoEstadoMemoria);

    // when: Creamos un tipo estado memoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoEstadoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoEstadoMemoria1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-EDITAR" })
  public void newTipoEstadoMemoria_Error_Returns400() throws Exception {
    // given: Un tipo estado memoria nuevo que produce un error al crearse
    String nuevoTipoEstadoMemoriaJson = "{\"nombre\": \"TipoEstadoMemoria1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoEstadoMemoriaService.create(ArgumentMatchers.<TipoEstadoMemoria>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un tipo estado memoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoEstadoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-EDITAR" })
  public void replaceTipoEstadoMemoria_ReturnsTipoEstadoMemoria() throws Exception {
    // given: Un tipo estado memoria a modificar
    String replaceTipoEstadoMemoriaJson = "{\"id\": 1, \"nombre\": \"TipoEstadoMemoria1\", \"activo\": \"true\"}";

    TipoEstadoMemoria tipoEstadoMemoria = generarMockTipoEstadoMemoria(1L, "Replace TipoEstadoMemoria1");

    BDDMockito.given(tipoEstadoMemoriaService.update(ArgumentMatchers.<TipoEstadoMemoria>any()))
        .willReturn(tipoEstadoMemoria);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoEstadoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el tipo estado memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace TipoEstadoMemoria1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-EDITAR" })
  public void replaceTipoEstadoMemoria_NotFound() throws Exception {
    // given: Un tipo estado memoria a modificar
    String replaceTipoEstadoMemoriaJson = "{\"id\": 1, \"nombre\": \"TipoEstadoMemoria1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoEstadoMemoriaService.update(ArgumentMatchers.<TipoEstadoMemoria>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoEstadoMemoriaNotFoundException(((TipoEstadoMemoria) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoEstadoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-EDITAR" })
  public void removeTipoEstadoMemoria_ReturnsOk() throws Exception {
    BDDMockito.given(tipoEstadoMemoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockTipoEstadoMemoria(1L, "TipoEstadoMemoria1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-VER" })
  public void findAll_Unlimited_ReturnsFullTipoEstadoMemoriaList() throws Exception {
    // given: One hundred TipoEstadoMemoria
    List<TipoEstadoMemoria> tipoEstadoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEstadoMemorias
          .add(generarMockTipoEstadoMemoria(Long.valueOf(i), "TipoEstadoMemoria" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEstadoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoEstadoMemorias));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoEstadoMemoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: TipoEstadoMemoria empty
    List<TipoEstadoMemoria> tipoEstadoMemorias = new ArrayList<>();

    BDDMockito.given(tipoEstadoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoEstadoMemorias));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-VER" })
  public void findAll_WithPaging_ReturnsTipoEstadoMemoriaSubList() throws Exception {
    // given: One hundred TipoEstadoMemoria
    List<TipoEstadoMemoria> tipoEstadoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEstadoMemorias
          .add(generarMockTipoEstadoMemoria(Long.valueOf(i), "TipoEstadoMemoria" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEstadoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoEstadoMemoria>>() {
          @Override
          public Page<TipoEstadoMemoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoEstadoMemoria> content = tipoEstadoMemorias.subList(fromIndex, toIndex);
            Page<TipoEstadoMemoria> page = new PageImpl<>(content, pageable, tipoEstadoMemorias.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoEstadoMemorias are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoEstadoMemoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoEstadoMemoria>>() {
        });

    // containing nombre='TipoEstadoMemoria031' to 'TipoEstadoMemoria040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoEstadoMemoria tipoEstadoMemoria = actual.get(i);
      Assertions.assertThat(tipoEstadoMemoria.getNombre()).isEqualTo("TipoEstadoMemoria" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOESTADOMEMORIA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoEstadoMemoriaList() throws Exception {
    // given: One hundred TipoEstadoMemoria and a search query
    List<TipoEstadoMemoria> tipoEstadoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEstadoMemorias
          .add(generarMockTipoEstadoMemoria(Long.valueOf(i), "TipoEstadoMemoria" + String.format("%03d", i)));
    }
    String query = "nombre~TipoEstadoMemoria%,id:5";

    BDDMockito.given(tipoEstadoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoEstadoMemoria>>() {
          @Override
          public Page<TipoEstadoMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoEstadoMemoria> content = new ArrayList<>();
            for (TipoEstadoMemoria tipoEstadoMemoria : tipoEstadoMemorias) {
              if (tipoEstadoMemoria.getNombre().startsWith("TipoEstadoMemoria")
                  && tipoEstadoMemoria.getId().equals(5L)) {
                content.add(tipoEstadoMemoria);
              }
            }
            Page<TipoEstadoMemoria> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoEstadoMemoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria
   * 
   * @param id     id del TipoEstadoMemoria
   * @param nombre el nombre del TipoEstadoMemoria
   * @return el objeto TipoEstadoMemoria
   */

  public TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre) {

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(id);
    tipoEstadoMemoria.setNombre(nombre);
    tipoEstadoMemoria.setActivo(Boolean.TRUE);

    return tipoEstadoMemoria;
  }

}
