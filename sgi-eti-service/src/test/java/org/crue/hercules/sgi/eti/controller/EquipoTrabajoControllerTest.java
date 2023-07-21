package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EquipoTrabajoNotFoundException;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.service.EquipoTrabajoService;
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
 * EquipoTrabajoControllerTest
 */
@WebMvcTest(EquipoTrabajoController.class)
public class EquipoTrabajoControllerTest extends BaseControllerTest {

  @MockBean
  private EquipoTrabajoService equipoTrabajoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EQUIPO_TRABAJO_CONTROLLER_BASE_PATH = "/equipotrabajos";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EQUIPOTRABAJO-VER" })
  public void getEquipoTrabajo_WithId_ReturnsEquipoTrabajo() throws Exception {
    BDDMockito.given(equipoTrabajoService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockEquipoTrabajo(1L, generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"))));

    mockMvc
        .perform(MockMvcRequestBuilders.get(EQUIPO_TRABAJO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("personaRef").value("user-001"))
        .andExpect(MockMvcResultMatchers.jsonPath("peticionEvaluacion.titulo").value("PeticionEvaluacion1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EQUIPOTRABAJO-VER" })
  public void getEquipoTrabajo_NotFound_Returns404() throws Exception {
    BDDMockito.given(equipoTrabajoService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EquipoTrabajoNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(EQUIPO_TRABAJO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EQUIPOTRABAJO-VER" })
  public void findAll_Unlimited_ReturnsFullEquipoTrabajoList() throws Exception {
    // given: One hundred EquipoTrabajo
    List<EquipoTrabajo> equipoTrabajos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      equipoTrabajos.add(generarMockEquipoTrabajo(Long.valueOf(i),
          generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i))));
    }

    BDDMockito.given(equipoTrabajoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(equipoTrabajos));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(EQUIPO_TRABAJO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred EquipoTrabajo
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EQUIPOTRABAJO-VER" })
  public void findAll_ReturnsNotContent() throws Exception {
    // given: EquipoTrabajo empty
    List<EquipoTrabajo> equipoTrabajos = new ArrayList<>();

    BDDMockito.given(equipoTrabajoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(equipoTrabajos));

    mockMvc
        .perform(MockMvcRequestBuilders.get(EQUIPO_TRABAJO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EQUIPOTRABAJO-VER" })
  public void findAll_WithPaging_ReturnsEquipoTrabajoSubList() throws Exception {
    // given: One hundred EquipoTrabajo
    List<EquipoTrabajo> equipoTrabajos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      equipoTrabajos.add(generarMockEquipoTrabajo(Long.valueOf(i),
          generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i))));
    }

    BDDMockito.given(equipoTrabajoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EquipoTrabajo>>() {
          @Override
          public Page<EquipoTrabajo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<EquipoTrabajo> content = equipoTrabajos.subList(fromIndex, toIndex);
            Page<EquipoTrabajo> page = new PageImpl<>(content, pageable, equipoTrabajos.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(EQUIPO_TRABAJO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked EquipoTrabajos are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<EquipoTrabajo> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<EquipoTrabajo>>() {
        });

    // containing peticionEvaluacion.titulo='PeticionEvaluacion031' to
    // 'PeticionEvaluacion040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      EquipoTrabajo equipoTrabajo = actual.get(i);
      Assertions.assertThat(equipoTrabajo.getPeticionEvaluacion().getTitulo())
          .isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EQUIPOTRABAJO-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredEquipoTrabajoList() throws Exception {
    // given: One hundred EquipoTrabajo and a search query
    List<EquipoTrabajo> equipoTrabajos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      equipoTrabajos.add(generarMockEquipoTrabajo(Long.valueOf(i),
          generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i))));
    }
    String query = "id:5";

    BDDMockito.given(equipoTrabajoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EquipoTrabajo>>() {
          @Override
          public Page<EquipoTrabajo> answer(InvocationOnMock invocation) throws Throwable {
            List<EquipoTrabajo> content = new ArrayList<>();
            for (EquipoTrabajo equipoTrabajo : equipoTrabajos) {
              if (equipoTrabajo.getId().equals(5L)) {
                content.add(equipoTrabajo);
              }
            }
            Page<EquipoTrabajo> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(EQUIPO_TRABAJO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred EquipoTrabajo
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */

  public PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos" + id);
    peticionEvaluacion.setResumen("Resumen" + id);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(titulo);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajo
   * 
   * @param id                 id del EquipoTrabajo
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */

  public EquipoTrabajo generarMockEquipoTrabajo(Long id, PeticionEvaluacion peticionEvaluacion) {

    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);
    equipoTrabajo.setPersonaRef("user-00" + id);

    return equipoTrabajo;
  }

}
