package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.LineaInvestigacionNotFoundException;
import org.crue.hercules.sgi.csp.model.LineaInvestigacion;
import org.crue.hercules.sgi.csp.service.LineaInvestigacionService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
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
 * LineaInvestigacionControllerTest
 */

@WebMvcTest(LineaInvestigacionController.class)
public class LineaInvestigacionControllerTest extends BaseControllerTest {

  @MockBean
  private LineaInvestigacionService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/lineasinvestigacion";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-C" })
  public void create_ReturnsLineaInvestigacion() throws Exception {
    // given: new LineaInvestigacion
    LineaInvestigacion data = generarMockLineaInvestigacion(null, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<LineaInvestigacion>any()))
        .willAnswer(new Answer<LineaInvestigacion>() {
          @Override
          public LineaInvestigacion answer(InvocationOnMock invocation) throws Throwable {
            LineaInvestigacion givenData = invocation.getArgument(0, LineaInvestigacion.class);
            LineaInvestigacion newData = new LineaInvestigacion();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create LineaInvestigacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new LineaInvestigacion is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(data.getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a LineaInvestigacion with id filled
    LineaInvestigacion data = generarMockLineaInvestigacion(1L, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<LineaInvestigacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: create LineaInvestigacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-E" })
  public void update_WithExistingId_ReturnsLineaInvestigacion() throws Exception {
    // given: existing LineaInvestigacion
    LineaInvestigacion data = generarMockLineaInvestigacion(1L, Boolean.TRUE);

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<LineaInvestigacion>() {
      @Override
      public LineaInvestigacion answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockLineaInvestigacion(id, Boolean.FALSE);
      }
    });
    BDDMockito.given(service.update(ArgumentMatchers.<LineaInvestigacion>any()))
        .willAnswer(new Answer<LineaInvestigacion>() {
          @Override
          public LineaInvestigacion answer(InvocationOnMock invocation) throws Throwable {
            LineaInvestigacion givenData = invocation.getArgument(0, LineaInvestigacion.class);
            return givenData;
          }
        });

    // when: update LineaInvestigacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: LineaInvestigacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(data.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(data.getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a LineaInvestigacion with non existing id
    LineaInvestigacion data = generarMockLineaInvestigacion(1L, Boolean.TRUE);

    BDDMockito.willThrow(new LineaInvestigacionNotFoundException(data.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<LineaInvestigacion>any()))
        .willThrow(new LineaInvestigacionNotFoundException(data.getId()));

    // when: update LineaInvestigacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-E" })
  public void update_WithDuplicatedNombre_Returns400() throws Exception {
    // given: a LineaInvestigacion with duplicated Nombre
    LineaInvestigacion data = generarMockLineaInvestigacion(1L, Boolean.TRUE);

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<LineaInvestigacion>() {
      @Override
      public LineaInvestigacion answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockLineaInvestigacion(id, Boolean.FALSE);
      }
    });
    BDDMockito.given(service.update(ArgumentMatchers.<LineaInvestigacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: update LineaInvestigacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-R" })
  public void reactivar_WithExistingId_ReturnLineaInvestigacion() throws Exception {
    // given: existing id
    LineaInvestigacion tipoEnlace = generarMockLineaInvestigacion(1L, Boolean.FALSE);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      LineaInvestigacion tipoEnlaceEnabled = new LineaInvestigacion();
      BeanUtils.copyProperties(tipoEnlace, tipoEnlaceEnabled);
      tipoEnlaceEnabled.setActivo(Boolean.TRUE);
      return tipoEnlaceEnabled;
    });

    // when: enable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, tipoEnlace.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return enabled LineaInvestigacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(tipoEnlace.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoEnlace.getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-R" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new LineaInvestigacionNotFoundException(id)).given(service)
        .enable(ArgumentMatchers.<Long>any());

    // when: enable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-B" })
  public void desactivar_WithExistingId_ReturnLineaInvestigacion() throws Exception {
    // given: existing id
    Long idBuscado = 1L;
    LineaInvestigacion tipoEnlace = generarMockLineaInvestigacion(idBuscado, Boolean.TRUE);

    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      LineaInvestigacion tipoEnlaceDisabled = new LineaInvestigacion();
      BeanUtils.copyProperties(tipoEnlace, tipoEnlaceDisabled);
      tipoEnlaceDisabled.setActivo(false);
      return tipoEnlaceDisabled;
    });

    // when: disable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return disabled LineaInvestigacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(idBuscado))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoEnlace.getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;
    BDDMockito.willThrow(new LineaInvestigacionNotFoundException(id)).given(service)
        .disable(ArgumentMatchers.<Long>any());

    // when: disable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithExistingId_ReturnsLineaInvestigacion() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<LineaInvestigacion>() {
      @Override
      public LineaInvestigacion answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockLineaInvestigacion(id, Boolean.TRUE);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested LineaInvestigacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new LineaInvestigacionNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-C", "CSP-LIN-E" })
  public void findAll_WithPaging_ReturnsLineaInvestigacionSubList() throws Exception {
    // given: One hundred LineaInvestigacion
    List<LineaInvestigacion> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockLineaInvestigacion(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<LineaInvestigacion>>() {
          @Override
          public Page<LineaInvestigacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<LineaInvestigacion> content = data.subList(fromIndex, toIndex);
            Page<LineaInvestigacion> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked LineaInvestigacion are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<LineaInvestigacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<LineaInvestigacion>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      LineaInvestigacion item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-C", "CSP-LIN-E" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data LineaInvestigacion
    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<LineaInvestigacion>>() {
          @Override
          public Page<LineaInvestigacion> answer(InvocationOnMock invocation) throws Throwable {
            Page<LineaInvestigacion> page = new PageImpl<>(Collections.emptyList());
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

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-V", "CSP-LIN-C", "CSP-LIN-E", "CSP-LIN-B",
      "CSP-LIN-R" })
  public void findAllTodos_WithPaging_ReturnsLineaInvestigacionSubList() throws Exception {
    // given: One hundred LineaInvestigacion
    List<LineaInvestigacion> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockLineaInvestigacion(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<LineaInvestigacion>>() {
          @Override
          public Page<LineaInvestigacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<LineaInvestigacion> content = data.subList(fromIndex, toIndex);
            Page<LineaInvestigacion> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked LineaInvestigacion are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<LineaInvestigacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<LineaInvestigacion>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      LineaInvestigacion item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LIN-V", "CSP-LIN-C", "CSP-LIN-E", "CSP-LIN-B",
      "CSP-LIN-R" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: no data LineaInvestigacion
    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<LineaInvestigacion>>() {
          @Override
          public Page<LineaInvestigacion> answer(InvocationOnMock invocation) throws Throwable {
            Page<LineaInvestigacion> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto LineaInvestigacion
   * 
   * @param id
   * @param activo
   * @return LineaInvestigacion
   */
  private LineaInvestigacion generarMockLineaInvestigacion(Long id, Boolean activo) {
    return LineaInvestigacion.builder().id(id).nombre("nombre-" + id).activo(activo).build();
  }

}
