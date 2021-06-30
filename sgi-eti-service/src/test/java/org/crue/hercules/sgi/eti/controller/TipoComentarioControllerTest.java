package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoComentarioNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.service.TipoComentarioService;
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
 * TipoComentarioControllerTest
 */
@WebMvcTest(TipoComentarioController.class)
public class TipoComentarioControllerTest extends BaseControllerTest {

  @MockBean
  private TipoComentarioService tipoComentarioService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_COMENTARIO_CONTROLLER_BASE_PATH = "/tipocomentarios";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-VER" })
  public void getTipoComentario_WithId_ReturnsTipoComentario() throws Exception {
    BDDMockito.given(tipoComentarioService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoComentario(1L, "TipoComentario1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoComentario1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-VER" })
  public void getTipoComentario_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoComentarioService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoComentarioNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-EDITAR" })
  public void newTipoComentario_ReturnsTipoComentario() throws Exception {
    // given: Un TipoComentario nuevo
    String nuevoTipoComentarioJson = "{\"nombre\": \"TipoComentario1\", \"activo\": \"true\"}";

    TipoComentario tipoComentario = generarMockTipoComentario(1L, "TipoComentario1");

    BDDMockito.given(tipoComentarioService.create(ArgumentMatchers.<TipoComentario>any())).willReturn(tipoComentario);

    // when: Creamos un TipoComentario
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_COMENTARIO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoComentarioJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo TipoComentario y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoComentario1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-EDITAR" })
  public void newTipoComentario_Error_Returns400() throws Exception {
    // given: Un TipoComentario nuevo que produce un error al crearse
    String nuevoTipoComentarioJson = "{\"nombre\": \"TipoComentario1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoComentarioService.create(ArgumentMatchers.<TipoComentario>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un TipoComentario
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_COMENTARIO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoComentarioJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-EDITAR" })
  public void replaceTipoComentario_ReturnsTipoComentario() throws Exception {
    // given: Un TipoComentario a modificar
    String replaceTipoComentarioJson = "{\"id\": 1, \"nombre\": \"TipoComentario1\", \"activo\": \"true\"}";

    TipoComentario tipoComentario = generarMockTipoComentario(1L, "Replace TipoComentario1");

    BDDMockito.given(tipoComentarioService.update(ArgumentMatchers.<TipoComentario>any())).willReturn(tipoComentario);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoComentarioJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el TipoComentario y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace TipoComentario1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-EDITAR" })
  public void replaceTipoComentario_NotFound() throws Exception {
    // given: Un TipoComentario a modificar
    String replaceTipoComentarioJson = "{\"id\": 1, \"nombre\": \"TipoComentario1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoComentarioService.update(ArgumentMatchers.<TipoComentario>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoComentarioNotFoundException(((TipoComentario) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoComentarioJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-EDITAR" })
  public void removeTipoComentario_ReturnsOk() throws Exception {
    BDDMockito.given(tipoComentarioService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockTipoComentario(1L, "TipoComentario1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-VER" })
  public void findAll_Unlimited_ReturnsFullTipoComentarioList() throws Exception {
    // given: One hundred TipoComentario
    List<TipoComentario> tipoComentarios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoComentarios.add(generarMockTipoComentario(Long.valueOf(i), "TipoComentario" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoComentarioService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoComentarios));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_COMENTARIO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoComentario
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-VER" })
  public void findAll_WithPaging_ReturnsTipoComentarioSubList() throws Exception {
    // given: One hundred TipoComentario
    List<TipoComentario> tipoComentarios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoComentarios.add(generarMockTipoComentario(Long.valueOf(i), "TipoComentario" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoComentarioService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoComentario>>() {
          @Override
          public Page<TipoComentario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoComentario> content = tipoComentarios.subList(fromIndex, toIndex);
            Page<TipoComentario> page = new PageImpl<>(content, pageable, tipoComentarios.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_COMENTARIO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoComentarios are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoComentario> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoComentario>>() {
        });

    // containing nombre='TipoComentario031' to 'TipoComentario040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoComentario tipoComentario = actual.get(i);
      Assertions.assertThat(tipoComentario.getNombre()).isEqualTo("TipoComentario" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoComentarioList() throws Exception {
    // given: One hundred TipoComentario and a search query
    List<TipoComentario> tipoComentarios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoComentarios.add(generarMockTipoComentario(Long.valueOf(i), "TipoComentario" + String.format("%03d", i)));
    }
    String query = "nombre~TipoComentario%,id:5";

    BDDMockito.given(tipoComentarioService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoComentario>>() {
          @Override
          public Page<TipoComentario> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoComentario> content = new ArrayList<>();
            for (TipoComentario tipoComentario : tipoComentarios) {
              if (tipoComentario.getNombre().startsWith("TipoComentario") && tipoComentario.getId().equals(5L)) {
                content.add(tipoComentario);
              }
            }
            Page<TipoComentario> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_COMENTARIO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoComentario
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCOMENTARIO-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: TipoComentario empty
    List<TipoComentario> tipoComentarios = new ArrayList<>();

    BDDMockito.given(tipoComentarioService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoComentarios));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_COMENTARIO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto TipoComentario
   * 
   * @param id     id del TipoComentario
   * @param nombre la descripción de TipoComentario
   * @return el objeto TipoComentario
   */

  public TipoComentario generarMockTipoComentario(Long id, String nombre) {

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(id);
    tipoComentario.setNombre(nombre);
    tipoComentario.setActivo(Boolean.TRUE);

    return tipoComentario;
  }

}
