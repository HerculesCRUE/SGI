package org.crue.hercules.sgi.eti.controller;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EstadoRetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.service.EstadoRetrospectivaService;
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
 * EstadoRetrospectivaControllerTest
 */
@WebMvcTest(EstadoRetrospectivaController.class)
public class EstadoRetrospectivaControllerTest extends BaseControllerTest {

  @MockBean
  private EstadoRetrospectivaService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH = "/estadoretrospectivas";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-EDITAR" })
  public void create_ReturnsEstadoRetrospectiva() throws Exception {

    // given: Nueva entidad sin Id
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    EstadoRetrospectiva response = getMockData(1L);
    String nuevoEstadoRetrospectivaJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.create(ArgumentMatchers.<EstadoRetrospectiva>any())).willReturn(response);

    // when: Se crea la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(nuevoEstadoRetrospectivaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: La entidad se crea correctamente
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(response.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(response.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-EDITAR" })
  public void create_WithId_Returns400() throws Exception {

    // given: Nueva entidad con Id
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();
    String nuevoEstadoRetrospectivaJson = mapper.writeValueAsString(getMockData(1L));

    BDDMockito.given(service.create(ArgumentMatchers.<EstadoRetrospectiva>any()))
        .willThrow(new IllegalArgumentException());

    // when: Se crea la entidad
    // then: Se produce error porque ya tiene Id
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(nuevoEstadoRetrospectivaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-EDITAR" })
  public void update_WithExistingId_ReturnsEstadoRetrospectiva() throws Exception {

    // given: Entidad existente que se va a actualizar
    EstadoRetrospectiva response = getMockData(1L);
    // @formatter:off
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on
    String replaceEstadoRetrospectivaJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.update(ArgumentMatchers.<EstadoRetrospectiva>any())).willReturn(response);

    // when: Se actualiza la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(replaceEstadoRetrospectivaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Los datos se actualizan correctamente
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(response.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(response.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-EDITAR" })
  public void update_WithNoExistingId_Returns404() throws Exception {

    // given: Entidad a actualizar que no existe
    EstadoRetrospectiva response = getMockData(1L);
    // @formatter:off
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on
    String replaceEstadoRetrospectivaJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.update(ArgumentMatchers.<EstadoRetrospectiva>any()))
        .will((InvocationOnMock invocation) -> {
          throw new EstadoRetrospectivaNotFoundException(((EstadoRetrospectiva) invocation.getArgument(0)).getId());
        });

    // when: Se actualiza la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(replaceEstadoRetrospectivaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se produce error porque no encuentra la entidad a actualizar
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-EDITAR" })
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente
    EstadoRetrospectiva response = getMockData(1L);
    // @formatter:off
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH)
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
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-EDITAR" })
  public void delete_WithNoExistingId_Returns404() throws Exception {

    // given: Id de una entidad que no existe
    EstadoRetrospectiva EstadoRetrospectiva = getMockData(1L);
    // @formatter:off
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EstadoRetrospectivaNotFoundException(invocation.getArgument(0));
    });

    // when: Se elimina la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.delete(url, EstadoRetrospectiva.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se produce error porque no encuentra la entidad a eliminar
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-VER" })
  public void findById_WithExistingId_ReturnsEstadoRetrospectiva() throws Exception {

    // given: Entidad con un determinado Id
    EstadoRetrospectiva response = getMockData(1L);
    // @formatter:off
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH)
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
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(response.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(response.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-VER" })
  public void findById_WithNoExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    EstadoRetrospectiva response = getMockData(1L);

    // @formatter:off
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EstadoRetrospectivaNotFoundException(invocation.getArgument(0));
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
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-VER" })
  public void findAll_Unlimited_ReturnsFullEstadoRetrospectivaList() throws Exception {

    // given: Datos existentes
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    List<EstadoRetrospectiva> response = new LinkedList<EstadoRetrospectiva>();
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
        new TypeReference<List<EstadoRetrospectiva>>() {
        })).isEqualTo(response);

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-VER" })
  public void findAll_Unlimited_Returns204() throws Exception {

    // given: No hay datos
    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));
    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía);
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-VER" })
  public void findAll_WithPaging_ReturnsEstadoRetrospectivaSubList() throws Exception {

    // given: Datos existentes
    String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    List<EstadoRetrospectiva> response = new LinkedList<>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));
    response.add(getMockData(3L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<EstadoRetrospectiva> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

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
        new TypeReference<List<EstadoRetrospectiva>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-VER" })
  public void findAll_WithPaging_Returns204() throws Exception {

    // given: Datos existentes
    String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    List<EstadoRetrospectiva> response = new LinkedList<EstadoRetrospectiva>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<EstadoRetrospectiva> pageResponse = new PageImpl<>(response, pageable, response.size());

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
  @WithMockUser(username = "user", authorities = { "ETI-ESTADORETROSPECTIVA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredEstadoRetrospectivaList() throws Exception {

    // given: Datos existentes
    List<EstadoRetrospectiva> response = new LinkedList<>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));
    response.add(getMockData(3L));
    response.add(getMockData(4L));
    response.add(getMockData(5L));

    final String url = new StringBuffer(ESTADO_RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    // search
    String query = "nombre~NombreEstadoRetrospectiva0%,id:3";

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EstadoRetrospectiva>>() {
          @Override
          public Page<EstadoRetrospectiva> answer(InvocationOnMock invocation) throws Throwable {
            List<EstadoRetrospectiva> content = new LinkedList<>();
            for (EstadoRetrospectiva item : response) {
              if (item.getNombre().startsWith("NombreEstadoRetrospectiva0") && item.getId().equals(3L)) {
                content.add(item);
              }
            }
            Page<EstadoRetrospectiva> page = new PageImpl<>(content);
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
        new TypeReference<List<EstadoRetrospectiva>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva getMockData(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("NombreEstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }
}
