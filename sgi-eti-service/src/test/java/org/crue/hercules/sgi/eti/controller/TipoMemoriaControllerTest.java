package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.service.TipoMemoriaService;
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
 * TipoMemoriaControllerTest
 */
@WebMvcTest(TipoMemoriaController.class)
public class TipoMemoriaControllerTest extends BaseControllerTest {

  @MockBean
  private TipoMemoriaService tipoMemoriaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_MEMORIA_CONTROLLER_BASE_PATH = "/tipomemorias";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-VER" })
  public void getTipoMemoria_WithId_ReturnsTipoMemoria() throws Exception {
    BDDMockito.given(tipoMemoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoMemoria(1L, "TipoMemoria1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoMemoria1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-VER" })
  public void getTipoMemoria_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoMemoriaService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoMemoriaNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-EDITAR" })
  public void newTipoMemoria_ReturnsTipoMemoria() throws Exception {
    // given: Un tipo memoria nuevo
    String nuevoTipoMemoriaJson = "{\"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}";

    TipoMemoria tipoMemoria = generarMockTipoMemoria(1L, "TipoMemoria1");

    BDDMockito.given(tipoMemoriaService.create(ArgumentMatchers.<TipoMemoria>any())).willReturn(tipoMemoria);

    // when: Creamos un tipo memoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoMemoria1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-EDITAR" })
  public void newTipoMemoria_Error_Returns400() throws Exception {
    // given: Un tipo memoria nuevo que produce un error al crearse
    String nuevoTipoMemoriaJson = "{\"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoMemoriaService.create(ArgumentMatchers.<TipoMemoria>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un tipo memoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-EDITAR" })
  public void replaceTipoMemoria_ReturnsTipoMemoria() throws Exception {
    // given: Un tipo memoria a modificar
    String replaceTipoMemoriaJson = "{\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}";

    TipoMemoria tipoMemoria = generarMockTipoMemoria(1L, "Replace TipoMemoria1");

    BDDMockito.given(tipoMemoriaService.update(ArgumentMatchers.<TipoMemoria>any())).willReturn(tipoMemoria);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el tipo memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace TipoMemoria1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-EDITAR" })
  public void replaceTipoMemoria_NotFound() throws Exception {
    // given: Un tipo memoria a modificar
    String replaceTipoMemoriaJson = "{\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoMemoriaService.update(ArgumentMatchers.<TipoMemoria>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoMemoriaNotFoundException(((TipoMemoria) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-EDITAR" })
  public void removeTipoMemoria_ReturnsOk() throws Exception {
    BDDMockito.given(tipoMemoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockTipoMemoria(1L, "TipoMemoria1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-VER" })
  public void findAll_Unlimited_ReturnsFullTipoMemoriaList() throws Exception {
    // given: One hundred TipoMemoria
    List<TipoMemoria> tipoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemorias.add(generarMockTipoMemoria(Long.valueOf(i), "TipoMemoria" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoMemorias));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoMemoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: TipoMemoria empty
    List<TipoMemoria> tipoMemorias = new ArrayList<>();

    BDDMockito.given(tipoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoMemorias));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-VER" })
  public void findAll_WithPaging_ReturnsTipoMemoriaSubList() throws Exception {
    // given: One hundred TipoMemoria
    List<TipoMemoria> tipoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemorias.add(generarMockTipoMemoria(Long.valueOf(i), "TipoMemoria" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoMemoria>>() {
          @Override
          public Page<TipoMemoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoMemoria> content = tipoMemorias.subList(fromIndex, toIndex);
            Page<TipoMemoria> page = new PageImpl<>(content, pageable, tipoMemorias.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoMemorias are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoMemoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoMemoria>>() {
        });

    // containing nombre='TipoMemoria031' to 'TipoMemoria040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoMemoria tipoMemoria = actual.get(i);
      Assertions.assertThat(tipoMemoria.getNombre()).isEqualTo("TipoMemoria" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoMemoriaList() throws Exception {
    // given: One hundred TipoMemoria and a search query
    List<TipoMemoria> tipoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemorias.add(generarMockTipoMemoria(Long.valueOf(i), "TipoMemoria" + String.format("%03d", i)));
    }
    String query = "nombre~TipoMemoria%,id:5";

    BDDMockito.given(tipoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoMemoria>>() {
          @Override
          public Page<TipoMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoMemoria> content = new ArrayList<>();
            for (TipoMemoria tipoMemoria : tipoMemorias) {
              if (tipoMemoria.getNombre().startsWith("TipoMemoria") && tipoMemoria.getId().equals(5L)) {
                content.add(tipoMemoria);
              }
            }
            Page<TipoMemoria> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoMemoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto TipoMemoria
   * 
   * @param id     id del tipoMemoria
   * @param nombre la descripción del tipo de memoria
   * @return el objeto tipo memoria
   */

  public TipoMemoria generarMockTipoMemoria(Long id, String nombre) {

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(id);
    tipoMemoria.setNombre(nombre);
    tipoMemoria.setActivo(Boolean.TRUE);

    return tipoMemoria;
  }

}
