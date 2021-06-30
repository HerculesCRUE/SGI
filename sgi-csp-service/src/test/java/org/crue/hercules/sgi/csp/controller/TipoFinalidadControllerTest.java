package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoFinalidadNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.service.TipoFinalidadService;
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
 * TipoFinalidadControllerTest
 */
@WebMvcTest(TipoFinalidadController.class)
public class TipoFinalidadControllerTest extends BaseControllerTest {

  @MockBean
  private TipoFinalidadService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/tipofinalidades";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFIN-C" })
  public void create_ReturnsTipoFinalidad() throws Exception {
    // given: new TipoFinalidad
    TipoFinalidad data = generarMockTipoFinalidad(null, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<TipoFinalidad>any())).willAnswer(new Answer<TipoFinalidad>() {
      @Override
      public TipoFinalidad answer(InvocationOnMock invocation) throws Throwable {
        TipoFinalidad givenData = invocation.getArgument(0, TipoFinalidad.class);
        TipoFinalidad newData = new TipoFinalidad();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(1L);
        return newData;
      }
    });

    // when: create TipoFinalidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new TipoFinalidad is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(data.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(data.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(data.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFIN-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a TipoFinalidad with id filled
    TipoFinalidad data = generarMockTipoFinalidad(1L, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<TipoFinalidad>any())).willThrow(new IllegalArgumentException());

    // when: create TipoFinalidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFIN-E" })
  public void update_WithExistingId_ReturnsTipoFinalidad() throws Exception {
    // given: existing TipoFinalidad
    TipoFinalidad data = generarMockTipoFinalidad(1L, Boolean.TRUE);

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<TipoFinalidad>() {
      @Override
      public TipoFinalidad answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockTipoFinalidad(id, Boolean.FALSE);
      }
    });
    BDDMockito.given(service.update(ArgumentMatchers.<TipoFinalidad>any())).willAnswer(new Answer<TipoFinalidad>() {
      @Override
      public TipoFinalidad answer(InvocationOnMock invocation) throws Throwable {
        TipoFinalidad givenData = invocation.getArgument(0, TipoFinalidad.class);
        return givenData;
      }
    });

    // when: update TipoFinalidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: TipoFinalidad is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(data.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(data.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(data.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(data.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFIN-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a TipoFinalidad with non existing id
    TipoFinalidad data = generarMockTipoFinalidad(1L, Boolean.TRUE);

    BDDMockito.willThrow(new TipoFinalidadNotFoundException(data.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<TipoFinalidad>any()))
        .willThrow(new TipoFinalidadNotFoundException(data.getId()));

    // when: update TipoFinalidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFIN-R" })
  public void reactivar_WithExistingId_ReturnTipoFinalidad() throws Exception {
    // given: existing id
    TipoFinalidad tipoFinalidad = generarMockTipoFinalidad(1L, Boolean.FALSE);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      TipoFinalidad tipoFinalidadEnabled = new TipoFinalidad();
      BeanUtils.copyProperties(tipoFinalidad, tipoFinalidadEnabled);
      tipoFinalidadEnabled.setActivo(Boolean.TRUE);
      return tipoFinalidadEnabled;
    });

    // when: enable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, tipoFinalidad.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return enabled TipoFinalidad
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(tipoFinalidad.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoFinalidad.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(tipoFinalidad.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFIN-R" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new TipoFinalidadNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-TFIN-B" })
  public void desactivar_WithExistingId_ReturnTipoFinalidad() throws Exception {
    // given: existing id
    Long idBuscado = 1L;
    TipoFinalidad tipoFinalidad = generarMockTipoFinalidad(idBuscado, Boolean.TRUE);

    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      TipoFinalidad tipoFinalidadDisabled = new TipoFinalidad();
      BeanUtils.copyProperties(tipoFinalidad, tipoFinalidadDisabled);
      tipoFinalidadDisabled.setActivo(false);
      return tipoFinalidadDisabled;
    });

    // when: disable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return disabled TipoFinalidad
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(idBuscado))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoFinalidad.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(tipoFinalidad.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.FALSE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFIN-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;
    BDDMockito.willThrow(new TipoFinalidadNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-PRO-C" })
  public void findById_WithExistingId_ReturnsTipoFinalidad() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<TipoFinalidad>() {
      @Override
      public TipoFinalidad answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockTipoFinalidad(id, Boolean.TRUE);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested TipoFinalidad is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-C" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoFinalidadNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-INV-V" })
  public void findAll_WithPaging_ReturnsTipoFinalidadSubList() throws Exception {
    // given: One hundred TipoFinalidad
    List<TipoFinalidad> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoFinalidad(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFinalidad>>() {
          @Override
          public Page<TipoFinalidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoFinalidad> content = data.subList(fromIndex, toIndex);
            Page<TipoFinalidad> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoFinalidad are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoFinalidad> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoFinalidad>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoFinalidad item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-INV-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data TipoFinalidad
    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFinalidad>>() {
          @Override
          public Page<TipoFinalidad> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoFinalidad> page = new PageImpl<>(Collections.emptyList());
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
  @WithMockUser(username = "user", authorities = { "CSP-TFIN-V", "CSP-TFIN-C", "CSP-TFIN-E", "CSP-TFIN-B",
      "CSP-TFIN-R" })
  public void findAllTodos_WithPaging_ReturnsTipoFinalidadSubList() throws Exception {
    // given: One hundred TipoFinalidad
    List<TipoFinalidad> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoFinalidad(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFinalidad>>() {
          @Override
          public Page<TipoFinalidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoFinalidad> content = data.subList(fromIndex, toIndex);
            Page<TipoFinalidad> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoFinalidad are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoFinalidad> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoFinalidad>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoFinalidad item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFIN-V", "CSP-TFIN-C", "CSP-TFIN-E", "CSP-TFIN-B",
      "CSP-TFIN-R" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: no data TipoFinalidad
    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFinalidad>>() {
          @Override
          public Page<TipoFinalidad> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoFinalidad> page = new PageImpl<>(Collections.emptyList());
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
   * Función que devuelve un objeto TipoFinalidad
   * 
   * @param id
   * @param activo
   * @return TipoFinalidad
   */
  private TipoFinalidad generarMockTipoFinalidad(Long id, Boolean activo) {
    return TipoFinalidad.builder().id(id).nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo)
        .build();
  }

}
