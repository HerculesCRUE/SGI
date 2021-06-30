package org.crue.hercules.sgi.eti.controller;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ApartadoNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.service.ApartadoService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ApartadoControllerTest
 */
@WebMvcTest(ApartadoController.class)
public class ApartadoControllerTest extends BaseControllerTest {

  @MockBean
  private ApartadoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String APARTADO_CONTROLLER_BASE_PATH = "/apartados";
  private static final String PATH_PARAMETER_HIJOS = "/hijos";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-APARTADO-VER" })
  public void findById_WithExistingId_ReturnsApartado() throws Exception {

    // given: Entidad con un determinado Id
    Apartado response = getMockData(1L, 1L, null);
    // @formatter:off
    final String url = new StringBuffer(APARTADO_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.given(service.findById(response.getId())).willReturn(response);

    // when: Se busca la entidad por ese Id
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera la entidad con el Id
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("bloque").value(response.getBloque()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(response.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("padre").value(response.getPadre()))
        .andExpect(MockMvcResultMatchers.jsonPath("orden").value(response.getOrden()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-APARTADO-VER" })
  public void findById_WithNoExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    Apartado response = getMockData(1L, 1L, null);

    // @formatter:off
    final String url = new StringBuffer(APARTADO_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ApartadoNotFoundException(invocation.getArgument(0));
    });

    // when: Se busca entidad con ese id
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se produce error porque no encuentra la entidad con ese Id
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-APARTADO-VER" })
  public void findAll_Unlimited_ReturnsFullApartadoList() throws Exception {

    // given: Datos existentes
    final String url = new StringBuffer(APARTADO_CONTROLLER_BASE_PATH).toString();

    List<Apartado> response = new LinkedList<Apartado>();
    response.add(getMockData(1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L));

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(response));

    // when: Se buscan todos los datos
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los datos
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Apartado>>() {
        })).isEqualTo(response);

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-APARTADO-VER" })
  public void findAll_Unlimited_Returns204() throws Exception {

    // given: No hay datos
    final String url = new StringBuffer(APARTADO_CONTROLLER_BASE_PATH).toString();

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));
    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía);
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-APARTADO-VER" })
  public void findAll_WithPaging_ReturnsApartadoSubList() throws Exception {

    // given: Datos existentes
    String url = new StringBuffer(APARTADO_CONTROLLER_BASE_PATH).toString();

    List<Apartado> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L));
    response.add(getMockData(3L, 1L, 1L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Apartado> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", pageable.getPageNumber()).header("X-Page-Size", pageable.getPageSize())
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan los datos correctamente según la paginación solicitada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", String.valueOf(pageable.getPageNumber())))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", String.valueOf(pageable.getPageSize())))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", String.valueOf(response.size())))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Apartado>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-APARTADO-VER" })
  public void findAll_WithPaging_Returns204() throws Exception {

    // given: Datos existentes
    String url = new StringBuffer(APARTADO_CONTROLLER_BASE_PATH).toString();

    List<Apartado> response = new LinkedList<Apartado>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Apartado> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    mockMvc
        .perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", pageable.getPageNumber()).header("X-Page-Size", pageable.getPageSize())
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista de datos paginados vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-APARTADO-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredApartadoList() throws Exception {

    // given: Datos existentes
    List<Apartado> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L));
    response.add(getMockData(3L, 1L, 1L));
    response.add(getMockData(4L, 2L, null));
    response.add(getMockData(5L, 2L, 4L));

    final String url = new StringBuffer(APARTADO_CONTROLLER_BASE_PATH).toString();

    // search
    String query = "nombre~Apartado0%,id:3";

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Apartado>>() {
          @Override
          public Page<Apartado> answer(InvocationOnMock invocation) throws Throwable {
            List<Apartado> content = new LinkedList<>();
            for (Apartado item : response) {
              if (item.getNombre().startsWith("Apartado0") && item.getId() == 3L) {
                content.add(item);
              }
            }
            Page<Apartado> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: Se buscan los datos con el filtro indicado
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan los datos filtrados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Apartado>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-EVALR" })
  public void getHijosEmptyList() throws Exception {
    // given: Existe el apartado pero no tiene hijos
    Long id = 3L;
    final String url = new StringBuffer(APARTADO_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append(PATH_PARAMETER_HIJOS).toString();

    BDDMockito.given(service.findByPadreId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-EVALR" })
  public void getHijosValid() throws Exception {
    // given: Datos existentes con apartado
    Long id = 3L;
    final String url = new StringBuffer(APARTADO_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append(PATH_PARAMETER_HIJOS).toString();

    List<Apartado> apartados = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Apartado apartado = getMockData(Long.valueOf(i), Long.valueOf(i), Long.valueOf(i));
      apartados.add(apartado);
    }

    BDDMockito.given(service.findByPadreId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Apartado>>() {
          @Override
          public Page<Apartado> answer(InvocationOnMock invocation) throws Throwable {
            List<Apartado> content = new ArrayList<>();
            for (Apartado apartados : apartados) {
              content.add(apartados);
            }
            return new PageImpl<>(content);
          }
        });
    // when: Se buscan todos los apartados hijos de ese apartador
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los apartados hijos relacionados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
  }

  /**
   * Genera un objeto {@link Apartado}
   * 
   * @param id
   * @param bloqueId
   * @param componenteFormularioId
   * @param padreId
   * @return Apartado
   */
  private Apartado getMockData(Long id, Long bloqueId, Long padreId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion1");
    Bloque bloque = new Bloque(bloqueId, formulario, "Bloque " + bloqueId, bloqueId.intValue());

    Apartado padre = (padreId != null) ? getMockData(padreId, bloqueId, null) : null;

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final Apartado data = new Apartado();
    data.setId(id);
    data.setBloque(bloque);
    data.setNombre("Apartado" + txt);
    data.setPadre(padre);
    data.setOrden(id.intValue());
    data.setEsquema("{\"nombre\":\"EsquemaApartado" + txt + "\"}");

    return data;
  }
}
