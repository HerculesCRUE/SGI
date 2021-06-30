package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EstadoActaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.service.EstadoActaService;
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
 * EstadoActaControllerTest
 */
@WebMvcTest(EstadoActaController.class)
public class EstadoActaControllerTest extends BaseControllerTest {

  @MockBean
  private EstadoActaService estadoActaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ESTADO_ACTA_CONTROLLER_BASE_PATH = "/estadoactas";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-VER" })
  public void getEstadoActa_WithId_ReturnsEstadoActa() throws Exception {
    BDDMockito.given(estadoActaService.findById(ArgumentMatchers.anyLong())).willReturn((generarMockEstadoActa(1L)));

    mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("acta.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEstadoActa.id").value(200));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-VER" })
  public void getEstadoActa_NotFound_Returns404() throws Exception {
    BDDMockito.given(estadoActaService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EstadoActaNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-EDITAR" })
  public void newEstadoActa_ReturnsEstadoActa() throws Exception {
    // given: Un estado acta nuevo
    String nuevoEstadoActaJson = "{\"acta\": {\"id\": 100}, \"tipoEstadoActa\": {\"id\": 200}, \"fechaEstado\": \"2020-07-14T19:30:00Z\"}";

    EstadoActa estadoActa = generarMockEstadoActa(1L);

    BDDMockito.given(estadoActaService.create(ArgumentMatchers.<EstadoActa>any())).willReturn(estadoActa);

    // when: Creamos un estado acta
    mockMvc
        .perform(MockMvcRequestBuilders.post(ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEstadoActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo estado acta y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("acta.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEstadoActa.id").value(200));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-EDITAR" })
  public void newEstadoActa_Error_Returns400() throws Exception {
    // given: Un estado acta nuevo que produce un error al crearse
    String nuevoEstadoActaJson = "{\"id\": 1, \"acta\": {\"id\": 100}, \"tipoEstadoActa\": {\"id\": 200}, \"fechaEstado\": \"2020-07-14T19:30:00Z\"}";

    BDDMockito.given(estadoActaService.create(ArgumentMatchers.<EstadoActa>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un estado acta
    mockMvc
        .perform(MockMvcRequestBuilders.post(ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEstadoActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-EDITAR" })
  public void replaceEstadoActa_ReturnsEstadoActa() throws Exception {
    // given: Un estado acta a modificar
    String replaceEstadoActaJson = "{\"id\": 1, \"acta\": {\"id\": 100}, \"tipoEstadoActa\": {\"id\": 200}, \"fechaEstado\": \"2020-07-14T19:30:00Z\"}";

    EstadoActa estadoActaActualizado = generarMockEstadoActa(1L);

    BDDMockito.given(estadoActaService.update(ArgumentMatchers.<EstadoActa>any())).willReturn(estadoActaActualizado);

    mockMvc
        .perform(MockMvcRequestBuilders.put(ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEstadoActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el estado acta y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("acta.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEstadoActa.id").value(200));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-EDITAR" })
  public void replaceEstadoActa_NotFound() throws Exception {
    // given: Un estado acta a modificar
    String replaceEstadoActaJson = "{\"id\": 1, \"acta\": {\"id\": 100}, \"tipoEstadoActa\": {\"id\": 200}, \"fechaEstado\": \"2020-07-14T19:30:00Z\"}";

    BDDMockito.given(estadoActaService.update(ArgumentMatchers.<EstadoActa>any()))
        .will((InvocationOnMock invocation) -> {
          throw new EstadoActaNotFoundException(((EstadoActa) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEstadoActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-EDITAR" })
  public void removeEstadoActa_ReturnsOk() throws Exception {
    BDDMockito.given(estadoActaService.findById(ArgumentMatchers.anyLong())).willReturn(generarMockEstadoActa(1L));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-VER" })
  public void findAll_Unlimited_ReturnsFullEstadoActaList() throws Exception {
    // given: One hundred estados actas
    List<EstadoActa> estadosActas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      estadosActas.add(generarMockEstadoActa(Long.valueOf(i)));
    }

    BDDMockito.given(estadoActaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(estadosActas));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred estados actas
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-VER" })
  public void findAll_Unlimited_ReturnsNotContent() throws Exception {
    // given: estadosactas empty
    List<EstadoActa> estadosActas = new ArrayList<>();

    BDDMockito.given(estadoActaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(estadosActas));

    mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-VER" })
  public void findAll_WithPaging_ReturnsEstadoActaSubList() throws Exception {
    // given: One hundred estados actas
    List<EstadoActa> estadosActas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      estadosActas.add(generarMockEstadoActa(Long.valueOf(i)));
    }

    BDDMockito.given(estadoActaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EstadoActa>>() {
          @Override
          public Page<EstadoActa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<EstadoActa> content = estadosActas.subList(fromIndex, toIndex);
            Page<EstadoActa> page = new PageImpl<>(content, pageable, estadosActas.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked estados actas are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<EstadoActa> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<EstadoActa>>() {
        });

    // containing id='31' to '40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      EstadoActa estadoActa = actual.get(i);
      Assertions.assertThat(estadoActa.getId()).isEqualTo(j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOACTA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredEstadoActaList() throws Exception {
    // given: One hundred estados actas and a search query
    List<EstadoActa> estadosActas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      estadosActas.add(generarMockEstadoActa(Long.valueOf(i)));
    }
    String query = "id:5";

    BDDMockito.given(estadoActaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EstadoActa>>() {
          @Override
          public Page<EstadoActa> answer(InvocationOnMock invocation) throws Throwable {
            List<EstadoActa> content = new ArrayList<>();
            for (EstadoActa estadoActa : estadosActas) {
              if (estadoActa.getId().equals(5L)) {
                content.add(estadoActa);
              }
            }
            Page<EstadoActa> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_ACTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one estado acta
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto EstadoActa
   * 
   * @param id id del estado acta
   * @return el objeto EstadoActa
   */
  public EstadoActa generarMockEstadoActa(Long id) {
    Acta acta = new Acta();
    acta.setId(100L);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(200L);

    EstadoActa estadoActa = new EstadoActa();
    estadoActa.setId(id);
    estadoActa.setActa(acta);
    estadoActa.setTipoEstadoActa(tipoEstadoActa);
    estadoActa.setFechaEstado(Instant.parse("2020-07-14T00:00:00Z"));

    return estadoActa;
  }

}
