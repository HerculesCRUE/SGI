package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.RespuestaNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.service.RespuestaService;
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
 * RespuestaControllerTest
 */
@WebMvcTest(RespuestaController.class)
public class RespuestaControllerTest extends BaseControllerTest {

  @MockBean
  private RespuestaService respuestaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String RESPUESTA_CONTROLLER_BASE_PATH = "/respuestas";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-VER" })
  public void getRespuesta_WithId_ReturnsRespuesta() throws Exception {
    BDDMockito.given(respuestaService.findById(ArgumentMatchers.anyLong())).willReturn((generarMockRespuesta(1L)));

    mockMvc
        .perform(MockMvcRequestBuilders.get(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("valor.valor").value("Valor1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-VER" })
  public void getRespuesta_NotFound_Returns404() throws Exception {
    BDDMockito.given(respuestaService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new RespuestaNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-EDITAR" })
  public void newRespuesta_ReturnsRespuesta() throws Exception {
    // given: Un Respuesta nuevo
    String nuevoRespuestaJson = "{\"valor\": \"Valor1\", \"memoria\": {\"memoria\": {\"id\": 1}, \"formulario\": {\"id\": 1}, \"activo\": true}, \"componenteFormulario\": {\"esquema\": \"Esquema1\"}}";

    Respuesta respuesta = generarMockRespuesta(1L);

    BDDMockito.given(respuestaService.create(ArgumentMatchers.<Respuesta>any())).willReturn(respuesta);

    // when: Creamos un Respuesta
    mockMvc
        .perform(MockMvcRequestBuilders.post(RESPUESTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoRespuestaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo Respuesta y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("valor.valor").value("Valor1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-EDITAR" })
  public void newRespuesta_updateRespuesta() throws Exception {
    // given: Un Respuesta nuevo que produce un error al crearse
    String nuevoRespuestaJson = "{\"valor\": \"Valor1\", \"memoria\": {\"id\": 1}, \"apartado\": {\"id\": 1}, \"formulario\": {\"id\": 1}, \"activo\": true}, \"componenteFormulario\": {\"esquema\": \"Esquema1\"}}";
    Respuesta respuesta = generarMockRespuesta(1L);
    respuesta.setValor("{\"valor\":\"Valor actualizado\"}");
    BDDMockito.given(respuestaService.create(ArgumentMatchers.<Respuesta>any()))
        .willThrow(new IllegalArgumentException());
    BDDMockito
        .given(
            respuestaService.findByMemoriaIdAndApartadoId(ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(respuesta);
    BDDMockito.given(respuestaService.update(ArgumentMatchers.<Respuesta>any())).willReturn(respuesta);

    // when: Creamos un Respuesta
    mockMvc
        .perform(MockMvcRequestBuilders.post(RESPUESTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoRespuestaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("valor.valor").value("Valor actualizado"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-EDITAR" })
  public void replaceRespuesta_ReturnsRespuesta() throws Exception {
    // given: Un Respuesta a modificar
    String replaceRespuestaJson = "{\"id\": 1, \"valor\": \"Valor1\", \"memoria\": {\"memoria\": {\"id\": 1}, \"formulario\": {\"id\": 1}, \"activo\": true}, \"componenteFormulario\": {\"esquema\": \"Esquema1\"}}";

    Respuesta respuesta = generarMockRespuesta(1L);
    respuesta.setValor("{\"valor\":\"Valor actializado\"}");

    BDDMockito.given(respuestaService.update(ArgumentMatchers.<Respuesta>any())).willReturn(respuesta);

    mockMvc
        .perform(MockMvcRequestBuilders.put(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceRespuestaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el Respuesta y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("valor.valor").value("Valor actializado"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-EDITAR" })
  public void replaceRespuesta_NotFound() throws Exception {
    // given: Un Respuesta a modificar
    String replaceRespuestaJson = "{\"id\": 1, \"valor\": \"Valor1\", \"memoria\": {\"memoria\": {\"id\": 1}, \"formulario\": {\"id\": 1}, \"activo\": true}, \"componenteFormulario\": {\"esquema\": \"Esquema1\"}}";

    BDDMockito.given(respuestaService.update(ArgumentMatchers.<Respuesta>any())).will((InvocationOnMock invocation) -> {
      throw new RespuestaNotFoundException(((Respuesta) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceRespuestaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-EDITAR" })
  public void removeRespuesta_ReturnsOk() throws Exception {
    BDDMockito.given(respuestaService.findById(ArgumentMatchers.anyLong())).willReturn(generarMockRespuesta(1L));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-VER" })
  public void findAll_Unlimited_ReturnsFullRespuestaList() throws Exception {
    // given: One hundred Respuesta
    List<Respuesta> Respuestas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Respuestas.add(generarMockRespuesta(Long.valueOf(i)));
    }

    BDDMockito.given(respuestaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Respuestas));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(RESPUESTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Respuesta
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-VER" })
  public void findAll_WithPaging_ReturnsRespuestaSubList() throws Exception {
    // given: One hundred Respuesta
    List<Respuesta> respuestas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      respuestas.add(generarMockRespuesta(Long.valueOf(i)));
    }

    BDDMockito.given(respuestaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Respuesta>>() {
          @Override
          public Page<Respuesta> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Respuesta> content = respuestas.subList(fromIndex, toIndex);
            Page<Respuesta> page = new PageImpl<>(content, pageable, respuestas.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(RESPUESTA_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Respuestas are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Respuesta> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Respuesta>>() {
        });

    // containing id='31' to '40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Respuesta Respuesta = actual.get(i);
      Assertions.assertThat(Respuesta.getId()).isEqualTo(j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredRespuestaList() throws Exception {
    // given: One hundred Respuesta and a search query
    List<Respuesta> respuestas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      respuestas.add(generarMockRespuesta(Long.valueOf(i)));
    }
    String query = "id:5";

    BDDMockito.given(respuestaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Respuesta>>() {
          @Override
          public Page<Respuesta> answer(InvocationOnMock invocation) throws Throwable {
            List<Respuesta> content = new ArrayList<>();
            for (Respuesta respuesta : respuestas) {
              if (respuesta.getId().equals(5L)) {
                content.add(respuesta);
              }
            }
            Page<Respuesta> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(RESPUESTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Respuesta
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: Respuesta empty
    List<Respuesta> Respuestas = new ArrayList<>();

    BDDMockito.given(respuestaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Respuestas));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(RESPUESTA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-VER" })
  public void getLastRespuesta_FromMemoria_ReturnsRespuesta() throws Exception {
    BDDMockito.given(respuestaService.findLastByMemoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of((generarMockRespuesta(1L))));

    mockMvc
        .perform(MockMvcRequestBuilders.get(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/last", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("valor.valor").value("Valor1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-RESPUESTA-VER" })
  public void getLastRespuesta_FromMemoria_ReturnsEmpty() throws Exception {
    BDDMockito.given(respuestaService.findLastByMemoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    mockMvc
        .perform(MockMvcRequestBuilders.get(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID
            + "/last", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent())
        .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
  }

  /**
   * Función que devuelve un objeto Respuesta
   * 
   * @param id id del Respuesta
   * @return el objeto Respuesta
   */

  public Respuesta generarMockRespuesta(Long id) {
    Memoria memoria = new Memoria();
    memoria.setId(id);

    Formulario formulario = new Formulario();
    formulario.setId(id);

    Apartado apartado = getMockApartado(id, 1L, null);

    Respuesta respuesta = new Respuesta();
    respuesta.setId(id);
    respuesta.setMemoria(memoria);
    respuesta.setApartado(apartado);
    respuesta.setValor("{\"valor\":\"Valor" + id + "\"}");

    return respuesta;
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
  private Apartado getMockApartado(Long id, Long bloqueId, Long padreId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion1");
    Bloque Bloque = new Bloque(bloqueId, formulario, "Bloque " + bloqueId, bloqueId.intValue());

    Apartado padre = (padreId != null) ? getMockApartado(padreId, bloqueId, null) : null;

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final Apartado data = new Apartado();
    data.setId(id);
    data.setBloque(Bloque);
    data.setNombre("Apartado" + txt);
    data.setPadre(padre);
    data.setOrden(id.intValue());
    data.setEsquema("{\"nombre\":\"EsquemaApartado" + txt + "\"}");

    return data;
  }
}
