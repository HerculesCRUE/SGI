package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SecurityConfig;
import org.crue.hercules.sgi.eti.exceptions.CargoComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.service.CargoComiteService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
 * CargoComiteControllerTest
 */
@WebMvcTest(CargoComiteController.class)
// Since WebMvcTest is only sliced controller layer for the testing, it would
// not take the security configurations.
@Import(SecurityConfig.class)
public class CargoComiteControllerTest extends BaseControllerTest {

  @MockBean
  private CargoComiteService cargoComiteService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CARGO_COMITE_CONTROLLER_BASE_PATH = "/cargocomites";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-VER" })
  public void getCargoComite_WithId_ReturnsCargoComite() throws Exception {
    BDDMockito.given(cargoComiteService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockCargoComite(1L, "CargoComite1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(CARGO_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("CargoComite1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-VER" })
  public void getCargoComite_NotFound_Returns404() throws Exception {
    BDDMockito.given(cargoComiteService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new CargoComiteNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(CARGO_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-EDITAR" })
  public void newCargoComite_ReturnsCargoComite() throws Exception {
    // given: Un cargo comité nuevo
    String nuevoCargoComiteJson = "{\"nombre\": \"CargoComite1\", \"activo\": \"true\"}";

    CargoComite cargoComite = generarMockCargoComite(1L, "CargoComite1");

    BDDMockito.given(cargoComiteService.create(ArgumentMatchers.<CargoComite>any())).willReturn(cargoComite);

    // when: Creamos un cargo comité
    mockMvc
        .perform(MockMvcRequestBuilders.post(CARGO_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoCargoComiteJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo cargo comité y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("CargoComite1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-EDITAR" })
  public void newCargoComite_Error_Returns400() throws Exception {
    // given: Un cargo comité nuevo que produce un error al crearse
    String nuevoCargoComiteJson = "{\"nombre\": \"CargoComite1\", \"activo\": \"true\"}";

    BDDMockito.given(cargoComiteService.create(ArgumentMatchers.<CargoComite>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un cargo comité
    mockMvc
        .perform(MockMvcRequestBuilders.post(CARGO_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoCargoComiteJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-EDITAR" })
  public void replaceCargoComite_ReturnsCargoComite() throws Exception {
    // given: Un cargo comité a modificar
    String replaceCargoComiteJson = "{\"id\": 1, \"nombre\": \"CargoComite1\", \"activo\": \"true\"}";

    CargoComite cargoComite = generarMockCargoComite(1L, "Replace CargoComite1");

    BDDMockito.given(cargoComiteService.update(ArgumentMatchers.<CargoComite>any())).willReturn(cargoComite);

    mockMvc
        .perform(MockMvcRequestBuilders.put(CARGO_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceCargoComiteJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el cargo comité y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace CargoComite1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-EDITAR" })
  public void replaceCargoComite_NotFound() throws Exception {
    // given: Un cargo comité a modificar
    String replaceCargoComiteJson = "{\"id\": 1, \"nombre\": \"CargoComite1\", \"activo\": \"true\"}";

    BDDMockito.given(cargoComiteService.update(ArgumentMatchers.<CargoComite>any()))
        .will((InvocationOnMock invocation) -> {
          throw new CargoComiteNotFoundException(((CargoComite) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(CARGO_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceCargoComiteJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-EDITAR" })
  public void removeCargoComite_ReturnsOk() throws Exception {
    BDDMockito.given(cargoComiteService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockCargoComite(1L, "CargoComite1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(CARGO_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-VER" })
  public void findAll_Unlimited_ReturnsFullCargoComiteList() throws Exception {
    // given: One hundred CargoComite
    List<CargoComite> cargoComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      cargoComites.add(generarMockCargoComite(Long.valueOf(i), "CargoComite" + String.format("%03d", i)));
    }

    BDDMockito.given(cargoComiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(cargoComites));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(CARGO_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred CargoComite
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-VER" })
  public void findAll_ReturnsNotContent() throws Exception {
    // given: CargoComite empty
    List<CargoComite> cargoComites = new ArrayList<>();

    BDDMockito.given(cargoComiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(cargoComites));

    mockMvc
        .perform(MockMvcRequestBuilders.get(CARGO_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-VER" })
  public void findAll_WithPaging_ReturnsCargoComiteSubList() throws Exception {
    // given: One hundred CargoComite
    List<CargoComite> cargoComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      cargoComites.add(generarMockCargoComite(Long.valueOf(i), "CargoComite" + String.format("%03d", i)));
    }

    BDDMockito.given(cargoComiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<CargoComite>>() {
          @Override
          public Page<CargoComite> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<CargoComite> content = cargoComites.subList(fromIndex, toIndex);
            Page<CargoComite> page = new PageImpl<>(content, pageable, cargoComites.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CARGO_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked CargoComites are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<CargoComite> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<CargoComite>>() {
        });

    // containing nombre='CargoComite031' to 'CargoComite040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      CargoComite cargoComite = actual.get(i);
      Assertions.assertThat(cargoComite.getNombre()).isEqualTo("CargoComite" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CARGOCOMITE-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredCargoComiteList() throws Exception {
    // given: One hundred CargoComite and a search query
    List<CargoComite> cargoComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      cargoComites.add(generarMockCargoComite(Long.valueOf(i), "CargoComite" + String.format("%03d", i)));
    }
    String query = "nombre~CargoComite%,id:5";

    BDDMockito.given(cargoComiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<CargoComite>>() {
          @Override
          public Page<CargoComite> answer(InvocationOnMock invocation) throws Throwable {
            List<CargoComite> content = new ArrayList<>();
            for (CargoComite cargoComite : cargoComites) {
              if (cargoComite.getNombre().startsWith("CargoComite") && cargoComite.getId().equals(5L)) {
                content.add(cargoComite);
              }
            }
            Page<CargoComite> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(CARGO_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred CargoComite
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto CargoComite
   * 
   * @param id     id del cargoComite
   * @param nombre el nombre del cargo comité
   * @return el objeto cargo comité
   */

  public CargoComite generarMockCargoComite(Long id, String nombre) {

    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(id);
    cargoComite.setNombre(nombre);
    cargoComite.setActivo(Boolean.TRUE);

    return cargoComite;
  }

}
