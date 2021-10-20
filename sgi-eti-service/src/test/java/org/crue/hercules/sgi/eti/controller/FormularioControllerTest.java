package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.FormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.service.BloqueService;
import org.crue.hercules.sgi.eti.service.FormularioService;
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
 * FormularioControllerTest
 */
@WebMvcTest(FormularioController.class)
public class FormularioControllerTest extends BaseControllerTest {

  @MockBean
  private FormularioService formularioService;

  @MockBean
  private BloqueService bloqueService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String FORMULARIO_CONTROLLER_BASE_PATH = "/formularios";
  private static final String PATH_PARAMETER_BLOQUES = "/bloques";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMULARIO-VER" })
  public void getFormulario_WithId_ReturnsFormulario() throws Exception {
    BDDMockito.given(formularioService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockFormulario(1L, "Formulario1", "Descripcion1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Formulario1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("Descripcion1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMULARIO-VER" })
  public void getFormulario_NotFound_Returns404() throws Exception {
    BDDMockito.given(formularioService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new FormularioNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMULARIO-VER" })
  public void findAll_WithPaging_ReturnsFormularioSubList() throws Exception {
    // given: One hundred Formulario
    List<Formulario> Formularioes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Formularioes.add(generarMockFormulario(Long.valueOf(i), "Formulario" + String.format("%03d", i), "Descripcion"));
    }

    BDDMockito.given(formularioService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Formulario>>() {
          @Override
          public Page<Formulario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Formulario> content = Formularioes.subList(fromIndex, toIndex);
            Page<Formulario> page = new PageImpl<>(content, pageable, Formularioes.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(FORMULARIO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Formularios are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Formulario> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Formulario>>() {
        });

    // containing nombre='Formulario031' to 'Formulario040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Formulario formulario = actual.get(i);
      Assertions.assertThat(formulario.getNombre()).isEqualTo("Formulario" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMULARIO-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredFormularioList() throws Exception {
    // given: One hundred Formulario and a search query
    List<Formulario> formularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formularios.add(generarMockFormulario(Long.valueOf(i), "Formulario" + String.format("%03d", i), "Descripcion"));
    }
    String query = "nombre~Formulario%,id:5";

    BDDMockito.given(formularioService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Formulario>>() {
          @Override
          public Page<Formulario> answer(InvocationOnMock invocation) throws Throwable {
            List<Formulario> content = new ArrayList<>();
            for (Formulario formulario : formularios) {
              if (formulario.getNombre().startsWith("Formulario") && formulario.getId().equals(5L)) {
                content.add(formulario);
              }
            }
            Page<Formulario> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMULARIO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Formulario
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMULARIO-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: TipoDocumento empty
    List<Formulario> formularios = new ArrayList<>();

    BDDMockito.given(formularioService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(formularios));

    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMULARIO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-EVALR", "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  public void getBloquesEmptyList() throws Exception {
    // given: Existe el formulario pero no tiene bloques

    Long id = 3L;
    final String url = new StringBuffer(FORMULARIO_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append(PATH_PARAMETER_BLOQUES).toString();

    BDDMockito.given(bloqueService.findByFormularioId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-EVALR", "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  public void getBloquesValid() throws Exception {
    // given: Datos existentes con formulario
    Long id = 3L;
    final String url = new StringBuffer(FORMULARIO_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append(PATH_PARAMETER_BLOQUES).toString();

    List<Formulario> formularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Formulario formulario = generarMockFormulario(Long.valueOf(i), String.valueOf(id), String.valueOf(id));
      formularios.add(formulario);
    }

    BDDMockito.given(bloqueService.findByFormularioId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Formulario>>() {
          @Override
          public Page<Formulario> answer(InvocationOnMock invocation) throws Throwable {
            List<Formulario> content = new ArrayList<>();
            for (Formulario formularios : formularios) {
              content.add(formularios);
            }
            return new PageImpl<>(content);
          }
        });
    // when: Se buscan todos los bloques de ese formulario
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los bloques relacionados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
  }

  /**
   * Función que devuelve un objeto Formulario
   * 
   * @param id          id del Formulario
   * @param nombre      el nombre del Formulario
   * @param descripcion la descripción del Formulario
   * @return el objeto Formulario
   */

  public Formulario generarMockFormulario(Long id, String nombre, String descripcion) {

    Formulario formulario = new Formulario();
    formulario.setId(id);
    formulario.setNombre(nombre);
    formulario.setDescripcion(descripcion);

    return formulario;
  }

}
