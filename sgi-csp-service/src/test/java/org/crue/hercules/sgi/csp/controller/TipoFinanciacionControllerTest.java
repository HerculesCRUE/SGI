package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.service.TipoFinanciacionService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
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
 * TipoFinanciacionControllerTest
 */
@WebMvcTest(TipoFinanciacionController.class)
public class TipoFinanciacionControllerTest extends BaseControllerTest {

  @MockBean
  private TipoFinanciacionService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/tipofinanciaciones";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-C" })
  public void create_ReturnsTipoFinanciacion() throws Exception {
    // given: Un TipoFinanciacion nuevo
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(null);

    String tipoFinanciacionJson = mapper.writeValueAsString(tipoFinanciacion);

    BDDMockito.given(service.create(ArgumentMatchers.<TipoFinanciacion>any())).will((InvocationOnMock invocation) -> {
      TipoFinanciacion tipoFinanciacionCreado = invocation.getArgument(0);
      tipoFinanciacionCreado.setId(1L);
      return tipoFinanciacionCreado;
    });
    // when: Creamos un TipoFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(tipoFinanciacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo ModeleoTipoFinanciacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-C" })
  public void create_WithId_Returns400() throws Exception {

    // given: Un TipoFinanciacion que produce un error al crearse porque ya tiene id
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);

    String tipoFinanciacionJson = mapper.writeValueAsString(tipoFinanciacion);
    BDDMockito.given(service.create(ArgumentMatchers.<TipoFinanciacion>any()))
        .willThrow(new IllegalArgumentException());
    // when: Creamos un TipoFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(tipoFinanciacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-E" })
  public void update_ReturnsTipoFinanciacion() throws Exception {
    // given: Un TipoFinanciacion a modificar
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    String tipoFinanciacionJson = mapper.writeValueAsString(tipoFinanciacion);

    BDDMockito.given(service.update(ArgumentMatchers.<TipoFinanciacion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    // when: Actualizamos el TipoFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoFinanciacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el TipoFinanciacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-E" })
  public void update_WithIdNotExist_ReturnsNotFound() throws Exception {
    // given: Un TipoFinanciacion a modificar
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    String replaceTipoFinanciacionJson = mapper.writeValueAsString(tipoFinanciacion);
    BDDMockito.given(service.update(ArgumentMatchers.<TipoFinanciacion>any())).will((InvocationOnMock invocation) -> {
      throw new TipoFinanciacionNotFoundException(((TipoFinanciacion) invocation.getArgument(0)).getId());
    });

    // when: Actualizamos el TipoFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoFinanciacionJson))
        // then: No encuentra el TipoFinanciacion y devuelve un 404
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-E" })
  public void update_WithIdActivoFalse_ReturnsIllegalArgumentException() throws Exception {
    // given: Un TipoFinanciacion a modificar
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    tipoFinanciacion.setActivo(false);
    String replaceTipoFinanciacionJson = mapper.writeValueAsString(tipoFinanciacion);
    BDDMockito.given(service.update(tipoFinanciacion)).willThrow(new IllegalArgumentException());
    // when: Actualizamos el TipoFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoFinanciacionJson))
        // then: No encuentra el TipoFinanciacion y devuelve un 404
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithExistingId_ReturnsTipoFinanciacion() throws Exception {

    // given: Entidad con un determinado Id
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);

    BDDMockito.given(service.findById(tipoFinanciacion.getId())).willReturn(tipoFinanciacion);

    // when: Se busca la entidad por ese Id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera la entidad con el Id
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(tipoFinanciacion.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoFinanciacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(tipoFinanciacion.getDescripcion()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoFinanciacionNotFoundException(invocation.getArgument(0));
    });

    // when: Se busca entidad con ese id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se produce error porque no encuentra la entidad con ese Id
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-R" })
  public void reactivar_WithExistingId_ReturnTipoFinanciacion() throws Exception {
    // given: existing id
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    tipoFinanciacion.setActivo(false);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      TipoFinanciacion tipoFinanciacionDisabled = new TipoFinanciacion();
      BeanUtils.copyProperties(tipoFinanciacion, tipoFinanciacionDisabled);
      tipoFinanciacionDisabled.setActivo(true);
      return tipoFinanciacionDisabled;
    });

    // when: reactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, tipoFinanciacion.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: TipoFinanciacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoFinanciacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(tipoFinanciacion.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-R" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new TipoFinanciacionNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any());

    // when: reactivar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-B" })
  public void desactivar_WithExistingId_ReturnTipoFinanciacion() throws Exception {
    // given: existing id
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      TipoFinanciacion tipoFinanciacionDisabled = new TipoFinanciacion();
      BeanUtils.copyProperties(tipoFinanciacion, tipoFinanciacionDisabled);
      tipoFinanciacionDisabled.setActivo(false);
      return tipoFinanciacionDisabled;
    });

    // when: desactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, tipoFinanciacion.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: TipoFinanciacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoFinanciacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(tipoFinanciacion.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new TipoFinanciacionNotFoundException(id)).given(service)
        .disable(ArgumentMatchers.<Long>any());

    // when: desactivar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAll_WithPaging_ReturnsTipoRegimenConcurrenciaSubList() throws Exception {
    // given: One hundred TipoRegimenConcurrencia
    List<TipoFinanciacion> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoFinanciacion(Long.valueOf(i)));
    }

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          List<TipoFinanciacion> content = data.subList(fromIndex, toIndex);
          Page<TipoFinanciacion> page = new PageImpl<>(content, pageable, data.size());
          return page;

        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoRegimenConcurrencia are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoFinanciacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoFinanciacion>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoFinanciacion item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data TipoRegimenConcurrencia
    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Page<TipoRegimenConcurrencia> page = new PageImpl<>(Collections.emptyList());
          return page;
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
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-V", "CSP-TFNA-C", "CSP-TFNA-E", "CSP-TFNA-B",
      "CSP-TFNA-R" })
  public void findAllTodos_WithPaging_ReturnsTipoRegimenConcurrenciaSubList() throws Exception {
    // given: One hundred TipoRegimenConcurrencia
    List<TipoFinanciacion> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoFinanciacion(Long.valueOf(i)));
    }

    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          List<TipoFinanciacion> content = data.subList(fromIndex, toIndex);
          Page<TipoFinanciacion> page = new PageImpl<>(content, pageable, data.size());
          return page;

        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoRegimenConcurrencia are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoFinanciacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoFinanciacion>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoFinanciacion item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFNA-V", "CSP-TFNA-C", "CSP-TFNA-E", "CSP-TFNA-B",
      "CSP-TFNA-R" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: no data TipoRegimenConcurrencia
    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Page<TipoRegimenConcurrencia> page = new PageImpl<>(Collections.emptyList());
          return page;
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
   * Función que devuelve un objeto TipoFinanciacion
   * 
   * @param id id del TipoFinanciacion
   * @return el objeto TipoFinanciacion
   */
  public TipoFinanciacion generarMockTipoFinanciacion(Long id) {
    return generarMockTipoFinanciacion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoFinanciacion
   * 
   * @param id     id del TipoFinanciacion
   * @param nombre nombre del TipoFinanciacion
   * @return el objeto TipoFinanciacion
   */
  public TipoFinanciacion generarMockTipoFinanciacion(Long id, String nombre) {

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id);
    tipoFinanciacion.setActivo(true);
    tipoFinanciacion.setNombre(nombre);
    tipoFinanciacion.setDescripcion("descripcion-" + 1);

    return tipoFinanciacion;
  }
}
