package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.FormacionEspecificaNotFoundException;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.service.FormacionEspecificaService;
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
 * FormacionEspecificaControllerTest
 */
@WebMvcTest(FormacionEspecificaController.class)
public class FormacionEspecificaControllerTest extends BaseControllerTest {

  @MockBean
  private FormacionEspecificaService formacionEspecificaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH = "/formacionespecificas";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-VER" })
  public void getFormacionEspecifica_WithId_ReturnsFormacionEspecifica() throws Exception {
    BDDMockito.given(formacionEspecificaService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockFormacionEspecifica(1L, "FormacionEspecifica1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("FormacionEspecifica1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-VER" })
  public void getFormacionEspecifica_NotFound_Returns404() throws Exception {
    BDDMockito.given(formacionEspecificaService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new FormacionEspecificaNotFoundException(invocation.getArgument(0));
        });
    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-EDITAR" })
  public void newFormacionEspecifica_ReturnsFormacionEspecifica() throws Exception {
    // given: Una formación específica nueva
    String nuevoFormacionEspecificaJson = "{\"nombre\": \"FormacionEspecifica1\", \"activo\": \"true\"}";

    FormacionEspecifica formacionEspecifica = generarMockFormacionEspecifica(1L, "FormacionEspecifica1");

    BDDMockito.given(formacionEspecificaService.create(ArgumentMatchers.<FormacionEspecifica>any()))
        .willReturn(formacionEspecifica);

    // when: Creamos una formación específica
    mockMvc
        .perform(MockMvcRequestBuilders.post(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoFormacionEspecificaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea la nueva formación específica y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("FormacionEspecifica1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-EDITAR" })
  public void newFormacionEspecifica_Error_Returns400() throws Exception {
    // given: Una formación específica nueva que produce un error al crearse
    String nuevoFormacionEspecificaJson = "{\"nombre\": \"FormacionEspecifica1\", \"activo\": \"true\"}";

    BDDMockito.given(formacionEspecificaService.create(ArgumentMatchers.<FormacionEspecifica>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos una formación específica
    mockMvc
        .perform(MockMvcRequestBuilders.post(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoFormacionEspecificaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-EDITAR" })
  public void replaceFormacionEspecifica_ReturnsFormacionEspecifica() throws Exception {
    // given: Una formación específica a modificar
    String replaceFormacionEspecificaJson = "{\"id\": 1, \"nombre\": \"FormacionEspecifica1\", \"activo\": \"true\"}";

    FormacionEspecifica formacionEspecifica = generarMockFormacionEspecifica(1L, "Replace FormacionEspecifica1");

    BDDMockito.given(formacionEspecificaService.update(ArgumentMatchers.<FormacionEspecifica>any()))
        .willReturn(formacionEspecifica);

    mockMvc
        .perform(MockMvcRequestBuilders.put(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceFormacionEspecificaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica la formación específica y la devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace FormacionEspecifica1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-EDITAR" })
  public void replaceFormacionEspecifica_NotFound() throws Exception {
    // given: Una formación específica a modificar
    String replaceFormacionEspecificaJson = "{\"id\": 1, \"nombre\": \"FormacionEspecifica1\", \"activo\": \"true\"}";

    BDDMockito.given(formacionEspecificaService.update(ArgumentMatchers.<FormacionEspecifica>any()))
        .will((InvocationOnMock invocation) -> {
          throw new FormacionEspecificaNotFoundException(((FormacionEspecifica) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceFormacionEspecificaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-EDITAR" })
  public void removeFormacionEspecifica_ReturnsOk() throws Exception {
    BDDMockito.given(formacionEspecificaService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockFormacionEspecifica(1L, "FormacionEspecifica1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-VER" })
  public void findAll_Unlimited_ReturnsFullFormacionEspecificaList() throws Exception {
    // given: One hundred FormacionEspecifica
    List<FormacionEspecifica> formacionEspecificas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formacionEspecificas
          .add(generarMockFormacionEspecifica(Long.valueOf(i), "FormacionEspecifica" + String.format("%03d", i)));
    }

    BDDMockito
        .given(formacionEspecificaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(formacionEspecificas));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred FormacionEspecifica
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-VER" })
  public void findAll_ReturnsNotContent() throws Exception {
    // given: FormacionEspecifica empty
    List<FormacionEspecifica> formacionEspecificas = new ArrayList<>();

    BDDMockito
        .given(formacionEspecificaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(formacionEspecificas));

    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-VER" })
  public void findAll_WithPaging_ReturnsFormacionEspecificaSubList() throws Exception {
    // given: One hundred FormacionEspecifica
    List<FormacionEspecifica> formacionEspecificas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formacionEspecificas
          .add(generarMockFormacionEspecifica(Long.valueOf(i), "FormacionEspecifica" + String.format("%03d", i)));
    }

    BDDMockito
        .given(formacionEspecificaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<FormacionEspecifica>>() {
          @Override
          public Page<FormacionEspecifica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<FormacionEspecifica> content = formacionEspecificas.subList(fromIndex, toIndex);
            Page<FormacionEspecifica> page = new PageImpl<>(content, pageable, formacionEspecificas.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked FormacionEspecificas are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<FormacionEspecifica> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<FormacionEspecifica>>() {
        });

    // containing nombre='FormacionEspecifica031' to 'FormacionEspecifica040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      FormacionEspecifica formacionEspecifica = actual.get(i);
      Assertions.assertThat(formacionEspecifica.getNombre())
          .isEqualTo("FormacionEspecifica" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-FORMACIONESPECIFICA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredFormacionEspecificaList() throws Exception {
    // given: One hundred FormacionEspecifica and a search query
    List<FormacionEspecifica> formacionEspecificas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formacionEspecificas
          .add(generarMockFormacionEspecifica(Long.valueOf(i), "FormacionEspecifica" + String.format("%03d", i)));
    }
    String query = "nombre~FormacionEspecifica%,id:5";

    BDDMockito
        .given(formacionEspecificaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<FormacionEspecifica>>() {
          @Override
          public Page<FormacionEspecifica> answer(InvocationOnMock invocation) throws Throwable {
            List<FormacionEspecifica> content = new ArrayList<>();
            for (FormacionEspecifica formacionEspecifica : formacionEspecificas) {
              if (formacionEspecifica.getNombre().startsWith("FormacionEspecifica")
                  && formacionEspecifica.getId().equals(5L)) {
                content.add(formacionEspecifica);
              }
            }
            Page<FormacionEspecifica> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMACION_ESPECIFICA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred FormacionEspecifica
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto FormacionEspecifica
   * 
   * @param id     id del formacionEspecifica
   * @param nombre la descripción del tipo de memoria
   * @return el objeto tipo memoria
   */

  public FormacionEspecifica generarMockFormacionEspecifica(Long id, String nombre) {

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(id);
    formacionEspecifica.setNombre(nombre);
    formacionEspecifica.setActivo(Boolean.TRUE);

    return formacionEspecifica;
  }

}
