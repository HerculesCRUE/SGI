package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.BloqueNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.service.ApartadoService;
import org.crue.hercules.sgi.eti.service.BloqueService;
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
 * BloqueControllerTest
 */
@WebMvcTest(BloqueController.class)
public class BloqueControllerTest extends BaseControllerTest {

  @MockBean
  private BloqueService bloqueService;

  @MockBean
  private ApartadoService apartadoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String BLOQUE_CONTROLLER_BASE_PATH = "/bloques";
  private static final String PATH_PARAMETER_APARTADOS = "/apartados";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-Bloque-VER" })
  public void getBloque_WithId_ReturnsBloque() throws Exception {
    BDDMockito.given(bloqueService.findById(ArgumentMatchers.anyLong())).willReturn((generarMockBloque(1L, "Bloque1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(BLOQUE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Bloque1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-Bloque-VER" })
  public void getBloque_NotFound_Returns404() throws Exception {
    BDDMockito.given(bloqueService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new BloqueNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(BLOQUE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-Bloque-VER" })
  public void findAll_Unlimited_ReturnsFullBloqueList() throws Exception {
    // given: One hundred Bloque
    List<Bloque> bloques = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      bloques.add(generarMockBloque(Long.valueOf(i), "Bloque" + String.format("%03d", i)));
    }

    BDDMockito.given(bloqueService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(bloques));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(BLOQUE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Bloque
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-Bloque-VER" })
  public void findAll_ReturnsNotContent() throws Exception {
    // given: Bloque empty
    List<Bloque> bloques = new ArrayList<>();

    BDDMockito.given(bloqueService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(bloques));

    mockMvc
        .perform(MockMvcRequestBuilders.get(BLOQUE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-Bloque-VER" })
  public void findAll_WithPaging_ReturnsBloqueSubList() throws Exception {
    // given: One hundred Bloque
    List<Bloque> bloques = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      bloques.add(generarMockBloque(Long.valueOf(i), "Bloque" + String.format("%03d", i)));
    }

    BDDMockito.given(bloqueService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Bloque>>() {
          @Override
          public Page<Bloque> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Bloque> content = bloques.subList(fromIndex, toIndex);
            Page<Bloque> page = new PageImpl<>(content, pageable, bloques.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(BLOQUE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Bloques are returned with the right page
        // information in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Bloque> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Bloque>>() {
        });

    // containing nombre='Bloque031' to 'Bloque040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Bloque bloque = actual.get(i);
      Assertions.assertThat(bloque.getNombre()).isEqualTo("Bloque" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-Bloque-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredBloqueList() throws Exception {
    // given: One hundred Bloque and a search query
    List<Bloque> bloques = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      bloques.add(generarMockBloque(Long.valueOf(i), "Bloque" + String.format("%03d", i)));
    }
    String query = "nombre~Bloque%,id:5";

    BDDMockito.given(bloqueService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Bloque>>() {
          @Override
          public Page<Bloque> answer(InvocationOnMock invocation) throws Throwable {
            List<Bloque> content = new ArrayList<>();
            for (Bloque bloque : bloques) {
              if (bloque.getNombre().startsWith("Bloque") && bloque.getId() == 5L) {
                content.add(bloque);
              }
            }
            Page<Bloque> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(BLOQUE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Bloque
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-EVALR" })
  public void getApartadosEmptyList() throws Exception {
    // given: Existe la memoria pero no tiene documentacion
    Long id = 3L;
    final String url = new StringBuffer(BLOQUE_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append(PATH_PARAMETER_APARTADOS).toString();

    BDDMockito.given(apartadoService.findByBloqueId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-EVALR" })
  public void getApartadosValid() throws Exception {
    // given: Datos existentes con memoria
    Long id = 3L;
    final String url = new StringBuffer(BLOQUE_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append(PATH_PARAMETER_APARTADOS).toString();

    List<Apartado> apartados = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Apartado apartado = getMockApartado(Long.valueOf(i), 1L, Long.valueOf(i));
      apartados.add(apartado);
    }

    BDDMockito.given(apartadoService.findByBloqueId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
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
    // when: Se buscan todos los documentos de esa memoria
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los documentos relacionados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
  }

  /**
   * Función que devuelve un objeto Bloque
   * 
   * @param id     id del Bloque
   * @param nombre el nombre de Bloque
   * @return el objeto Bloque
   */

  public Bloque generarMockBloque(Long id, String nombre) {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("Formulario1");
    formulario.setDescripcion("Descripcion formulario 1");

    Bloque bloque = new Bloque();
    bloque.setId(id);
    bloque.setFormulario(formulario);
    bloque.setNombre(nombre);
    bloque.setOrden(1);

    return bloque;
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
    Bloque bloque = new Bloque(bloqueId, formulario, "Bloque " + bloqueId, bloqueId.intValue());

    Apartado padre = (padreId != null) ? getMockApartado(padreId, bloqueId, null) : null;

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
