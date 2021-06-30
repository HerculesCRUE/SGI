package org.crue.hercules.sgi.eti.controller;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.RetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.service.RetrospectivaService;
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
 * RetrospectivaControllerTest
 */
@WebMvcTest(RetrospectivaController.class)
public class RetrospectivaControllerTest extends BaseControllerTest {

  @MockBean
  private RetrospectivaService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String RETROSPECTIVA_CONTROLLER_BASE_PATH = "/retrospectivas";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-EDITAR" })
  public void create_ReturnsRetrospectiva() throws Exception {

    // given: Nueva entidad sin Id
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    Retrospectiva response = getMockData(1L);
    String nuevoRetrospectivaJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.create(ArgumentMatchers.<Retrospectiva>any())).willReturn(response);

    // when: Se crea la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(nuevoRetrospectivaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: La entidad se crea correctamente
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("estadoRetrospectiva").value(response.getEstadoRetrospectiva()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("fechaRetrospectiva").value(response.getFechaRetrospectiva().toString()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-EDITAR" })
  public void create_WithId_Returns400() throws Exception {

    // given: Nueva entidad con Id
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();
    String nuevoRetrospectivaJson = mapper.writeValueAsString(getMockData(1L));

    BDDMockito.given(service.create(ArgumentMatchers.<Retrospectiva>any())).willThrow(new IllegalArgumentException());

    // when: Se crea la entidad
    // then: Se produce error porque ya tiene Id
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(nuevoRetrospectivaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-EDITAR" })
  public void update_WithExistingId_ReturnsRetrospectiva() throws Exception {

    // given: Entidad existente que se va a actualizar
    Retrospectiva response = getMockData(1L);
    // @formatter:off
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on
    String replaceRetrospectivaJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.update(ArgumentMatchers.<Retrospectiva>any())).willReturn(response);

    // when: Se actualiza la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(replaceRetrospectivaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Los datos se actualizan correctamente
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("estadoRetrospectiva").value(response.getEstadoRetrospectiva()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("fechaRetrospectiva").value(response.getFechaRetrospectiva().toString()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-EDITAR" })
  public void update_WithNoExistingId_Returns404() throws Exception {

    // given: Entidad a actualizar que no existe
    Retrospectiva response = getMockData(1L);
    // @formatter:off
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on
    String replaceRetrospectivaJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.update(ArgumentMatchers.<Retrospectiva>any())).will((InvocationOnMock invocation) -> {
      throw new RetrospectivaNotFoundException(((Retrospectiva) invocation.getArgument(0)).getId());
    });

    // when: Se actualiza la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(replaceRetrospectivaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se produce error porque no encuentra la entidad a actualizar
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-EDITAR" })
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente
    Retrospectiva response = getMockData(1L);
    // @formatter:off
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willReturn(response);

    // when: Se elimina la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.delete(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: La entidad se elimina correctamente
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-EDITAR" })
  public void delete_WithNoExistingId_Returns404() throws Exception {

    // given: Id de una entidad que no existe
    Retrospectiva retrospectiva = getMockData(1L);
    // @formatter:off
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.willThrow(new RetrospectivaNotFoundException(1L)).given(service).delete(ArgumentMatchers.<Long>any());

    // when: Se elimina la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.delete(url, retrospectiva.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se produce error porque no encuentra la entidad a eliminar
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-VER" })
  public void findById_WithExistingId_ReturnsRetrospectiva() throws Exception {

    // given: Entidad con un determinado Id
    Retrospectiva response = getMockData(1L);
    // @formatter:off
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH)
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
        .andExpect(MockMvcResultMatchers.jsonPath("estadoRetrospectiva").value(response.getEstadoRetrospectiva()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("fechaRetrospectiva").value(response.getFechaRetrospectiva().toString()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-VER" })
  public void findById_WithNoExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    Retrospectiva response = getMockData(1L);

    // @formatter:off
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new RetrospectivaNotFoundException(invocation.getArgument(0));
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
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-VER" })
  public void findAll_Unlimited_ReturnsFullRetrospectivaList() throws Exception {

    // given: Datos existentes
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    List<Retrospectiva> response = new LinkedList<Retrospectiva>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));

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
        new TypeReference<List<Retrospectiva>>() {
        })).isEqualTo(response);

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-VER" })
  public void findAll_Unlimited_Returns204() throws Exception {

    // given: No hay datos
    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));
    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía);
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-VER" })
  public void findAll_WithPaging_ReturnsRetrospectivaSubList() throws Exception {

    // given: Datos existentes
    String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    List<Retrospectiva> response = new LinkedList<>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));
    response.add(getMockData(3L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Retrospectiva> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

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
        new TypeReference<List<Retrospectiva>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-VER" })
  public void findAll_WithPaging_Returns204() throws Exception {

    // given: Datos existentes
    String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    List<Retrospectiva> response = new LinkedList<Retrospectiva>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Retrospectiva> pageResponse = new PageImpl<>(response, pageable, response.size());

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
  @WithMockUser(username = "user", authorities = { "ETI-RETROSPECTIVA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredRetrospectivaList() throws Exception {

    // given: Datos existentes
    List<Retrospectiva> response = new LinkedList<>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));
    response.add(getMockData(3L));
    response.add(getMockData(4L));
    response.add(getMockData(5L));

    final String url = new StringBuffer(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    // search
    String query = "fechaRetrospectiva<=2020-07-03,id:3";

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Retrospectiva>>() {
          @Override
          public Page<Retrospectiva> answer(InvocationOnMock invocation) throws Throwable {
            List<Retrospectiva> content = new LinkedList<>();
            for (Retrospectiva item : response) {
              if (item.getId().equals(3L)) {
                content.add(item);
              }
            }
            Page<Retrospectiva> page = new PageImpl<>(content);
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
        new TypeReference<List<Retrospectiva>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva getMockData(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(getMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva getMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("EstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }
}
