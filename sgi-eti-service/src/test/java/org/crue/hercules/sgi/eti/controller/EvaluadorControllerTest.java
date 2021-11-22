package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EvaluadorNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.service.ConflictoInteresService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.eti.service.EvaluadorService;
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
 * EvaluadorControllerTest
 */
@WebMvcTest(EvaluadorController.class)
public class EvaluadorControllerTest extends BaseControllerTest {

  @MockBean
  private EvaluadorService evaluadorService;

  @MockBean
  private EvaluacionService evaluacionService;

  @MockBean
  private ConflictoInteresService conflictoInteresService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUADOR_CONTROLLER_BASE_PATH = "/evaluadores";
  private static final String PATH_PARAMETER_EVALUACIONES = "/evaluaciones";
  private static final String PATH_PARAMETER_SINCONFLICTOINTERES = "/comite/{idComite}/sinconflictointereses/{idMemoria}";
  private static final String PATH_PARAMETER_CONFLICTOS_INTERES = "/{id}/conflictos";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-V" })
  public void getEvaluador_WithId_ReturnsEvaluador() throws Exception {
    BDDMockito.given(evaluadorService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockEvaluador(1L, "Evaluador1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Evaluador1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-V" })
  public void getEvaluador_NotFound_Returns404() throws Exception {
    BDDMockito.given(evaluadorService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EvaluadorNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-C" })
  public void newEvaluador_ReturnsEvaluador() throws Exception {
    // given: Un evaluador nuevo
    Evaluador evaluador = generarMockEvaluador(1L, "Evaluador1");
    evaluador.setId(null);
    String nuevoEvaluadorJson = mapper.writeValueAsString(evaluador);
    evaluador.setId(1L);

    BDDMockito.given(evaluadorService.create(ArgumentMatchers.<Evaluador>any())).willReturn(evaluador);

    // when: Creamos un evaluador
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUADOR_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEvaluadorJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo evaluador y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Evaluador1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-C" })
  public void newEvaluador_Error_Returns400() throws Exception {
    // given: Un evaluador nuevo que produce un error al crearse
    String nuevoEvaluadorJson = "{\"resumen\": \"Evaluador1\", \"activo\": \"true\"}";

    BDDMockito.given(evaluadorService.create(ArgumentMatchers.<Evaluador>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un evaluador
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUADOR_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEvaluadorJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-E" })
  public void replaceEvaluador_ReturnsEvaluador() throws Exception {
    // given: Un evaluador a modificar
    Evaluador evaluador = generarMockEvaluador(1L, "Replace Evaluador1");
    String replaceEvaluadorJson = mapper.writeValueAsString(evaluador);

    BDDMockito.given(evaluadorService.update(ArgumentMatchers.<Evaluador>any())).willReturn(evaluador);

    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEvaluadorJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el evaluador y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Replace Evaluador1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-E" })
  public void replaceEvaluador_NotFound() throws Exception {
    // given: Un evaluador a modificar
    String replaceEvaluadorJson = mapper.writeValueAsString(generarMockEvaluador(1L, "Evaluador1"));

    BDDMockito.given(evaluadorService.update(ArgumentMatchers.<Evaluador>any())).will((InvocationOnMock invocation) -> {
      throw new EvaluadorNotFoundException(((Evaluador) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEvaluadorJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-B" })
  public void removeEvaluador_ReturnsOk() throws Exception {
    BDDMockito.given(evaluadorService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockEvaluador(1L, "Evaluador1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-V" })
  public void findAll_Unlimited_ReturnsFullEvaluadorList() throws Exception {
    // given: One hundred Evaluador
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }

    BDDMockito.given(evaluadorService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(evaluadores));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Evaluador
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-V" })
  public void findAll_WithPaging_ReturnsEvaluadorSubList() throws Exception {
    // given: One hundred Evaluador
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }

    BDDMockito.given(evaluadorService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Evaluador>>() {
          @Override
          public Page<Evaluador> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Evaluador> content = evaluadores.subList(fromIndex, toIndex);
            Page<Evaluador> page = new PageImpl<>(content, pageable, evaluadores.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Evaluadors are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Evaluador> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Evaluador>>() {
        });

    // containing resumen='Evaluador031' to 'Evaluador040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluador evaluador = actual.get(i);
      Assertions.assertThat(evaluador.getResumen()).isEqualTo("Evaluador" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluadorList() throws Exception {
    // given: One hundred Evaluador and a search query
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }
    String query = "resumen~Evaluador%,id:5";

    BDDMockito.given(evaluadorService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Evaluador>>() {
          @Override
          public Page<Evaluador> answer(InvocationOnMock invocation) throws Throwable {
            List<Evaluador> content = new ArrayList<>();
            for (Evaluador evaluador : evaluadores) {
              if (evaluador.getResumen().startsWith("Evaluador") && evaluador.getId().equals(5L)) {
                content.add(evaluador);
              }
            }
            Page<Evaluador> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Evaluador
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-V" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: Evaluadores empty
    List<Evaluador> evaluadores = new ArrayList<>();

    BDDMockito.given(evaluadorService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(evaluadores));
    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-VR", "ETI-EVC-EVALR" })
  public void getEvaluaciones_Unlimited_ReturnsFullEvaluacionList() throws Exception {
    // given: Existen 100 evaluaciones
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionService.findByEvaluador(ArgumentMatchers.anyString(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: las recupero sin paginación
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_EVALUACIONES)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: obtengo un listado de 100 evaluaciones
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-VR", "ETI-EVC-EVALR" })
  public void getEvaluaciones_Unlimited_ReturnsFullEmptyList() throws Exception {
    // given: No hay evaluaciones
    BDDMockito.given(evaluacionService.findByEvaluador(ArgumentMatchers.anyString(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(new ArrayList<>()));

    // when: listo todo
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_EVALUACIONES)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve una página vacia
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-VR", "ETI-EVC-EVALR" })
  public void findEvaluacionesEnSeguimiento_Unlimited_ReturnsFullEvaluacionList() throws Exception {
    // given: Existen 100 evaluaciones
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionService.findEvaluacionesEnSeguimientosByEvaluador(ArgumentMatchers.anyString(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: las recupero sin paginación
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH + "/evaluaciones-seguimiento")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: obtengo un listado de 100 evaluaciones
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-VR", "ETI-EVC-EVALR" })
  public void findEvaluacionesEnSeguimiento_Unlimited_ReturnsFullEmptyList() throws Exception {
    // given: No hay evaluaciones
    BDDMockito
        .given(evaluacionService.findEvaluacionesEnSeguimientosByEvaluador(ArgumentMatchers.anyString(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(new ArrayList<>()));

    // when: listo todo
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH + "/evaluaciones-seguimiento")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve una página vacia
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C", "ETI-CNV-E" })
  public void findAllByComiteSinconflictoInteresesMemoria_Unlimited_ReturnsFullEvaluadorList() throws Exception {
    // given: idComite, idMemoria, 10 evaluadores
    Long idComite = 1L;
    Long idMemoria = 1L;
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }

    BDDMockito.given(evaluadorService.findAllByComiteSinconflictoInteresesMemoria(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(evaluadores);

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_SINCONFLICTOINTERES, idComite, idMemoria)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Evaluador
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C", "ETI-CNV-E" })
  public void findAllByComiteSinconflictoInteresesMemoria_WithPaging_ReturnsEvaluadorSubList() throws Exception {
    // given: idComite, idMemoria, One hundred Evaluador
    Long idComite = 1L;
    Long idMemoria = 1L;
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }

    BDDMockito.given(evaluadorService.findAllByComiteSinconflictoInteresesMemoria(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(evaluadores);

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_SINCONFLICTOINTERES, idComite, idMemoria)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Evaluadors are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Evaluador> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Evaluador>>() {
        });

    for (int i = 0, j = 1; i < 10; i++, j++) {
      Evaluador evaluador = actual.get(i);
      Assertions.assertThat(evaluador.getResumen()).isEqualTo("Evaluador" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C", "ETI-CNV-E" })
  public void findAllByComiteSinconflictoInteresesMemoria_ReturnsNoContent() throws Exception {
    // given: Evaluadores empty
    Long idComite = 1L;
    Long idMemoria = 1L;
    List<Evaluador> evaluadores = new ArrayList<>();

    BDDMockito.given(evaluadorService.findAllByComiteSinconflictoInteresesMemoria(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(evaluadores);
    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_SINCONFLICTOINTERES, idComite, idMemoria)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-C", "ETI-EVR-E" })
  public void getConflictosInteres_Unlimited_ReturnsFullConflictoInteresList() throws Exception {
    // given: Existen 100 conflictos
    List<ConflictoInteres> conflictos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      conflictos.add(generarMockConflictoInteres(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito
        .given(
            conflictoInteresService.findAllByEvaluadorId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(conflictos));

    // when: las recupero sin paginación
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_CONFLICTOS_INTERES, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: obtengo un listado de 100 conflictos
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-C", "ETI-EVR-E" })
  public void getConflictosInteres_Unlimited_ReturnsFullEmptyList() throws Exception {
    // given: No hay conflictos
    BDDMockito
        .given(
            conflictoInteresService.findAllByEvaluadorId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(new ArrayList<>()));

    // when: listo todo
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_CONFLICTOS_INTERES, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve una página vacia
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
  }

  /**
   * Función que devuelve un objeto Evaluador
   * 
   * @param id      id del Evaluador
   * @param resumen el resumen de Evaluador
   * @return el objeto Evaluador
   */

  public Evaluador generarMockEvaluador(Long id, String resumen) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreSecretario", "nombreInvestigacion", Genero.M, "nombreDecreto",
        "articulo", formulario, Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(Instant.now());
    evaluador.setFechaBaja(Instant.now());
    evaluador.setResumen(resumen);
    evaluador.setPersonaRef("user-00" + id);
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param id     id del Evaluacion
   * @param sufijo el sufijo para título y nombre
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(Long id, String sufijo) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreSecretario", "nombreInvestigacion", Genero.M, "nombreDecreto",
        "articulo", formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.FALSE,
        new Retrospectiva(id, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), Instant.now()), 3,
        "CodOrganoCompetente", Boolean.TRUE, null);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.now());
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(Instant.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(Instant.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setVersion(2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

  /**
   * Función que devuelve un objeto ConflictoInteres
   * 
   * @param id                  id del ConflictoInteres
   * @param personaConflictoRef la persona del conflicto de interés
   * @return el objeto ConflictoInteres
   */
  public ConflictoInteres generarMockConflictoInteres(Long id, String sufijo) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    ConflictoInteres conflicto = new ConflictoInteres();
    conflicto.setId(id);
    conflicto.setEvaluador(generarMockEvaluador(id, "Resumen" + (id != null ? id : "1")));
    conflicto.setPersonaConflictoRef("user-00" + sufijoStr);
    return conflicto;
  }

}
