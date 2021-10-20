package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EstadoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.service.EstadoMemoriaService;
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
 * EstadoMemoriaControllerTest
 */
@WebMvcTest(EstadoMemoriaController.class)
public class EstadoMemoriaControllerTest extends BaseControllerTest {

  @MockBean
  private EstadoMemoriaService estadoMemoriaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ESTADO_MEMORIA_CONTROLLER_BASE_PATH = "/estadomemorias";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-VER" })
  public void getEstadoMemoria_WithId_ReturnsEstadoMemoria() throws Exception {
    BDDMockito.given(estadoMemoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockEstadoMemoria(1L, 1L)));

    mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria001"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEstadoMemoria.nombre").value("TipoEstadoMemoria001"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-VER" })
  public void getEstadoMemoria_NotFound_Returns404() throws Exception {
    BDDMockito.given(estadoMemoriaService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EstadoMemoriaNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-EDITAR" })
  public void newEstadoMemoria_ReturnsEstadoMemoria() throws Exception {
    // given: Un nuevo estado memoria
    String nuevoEstadoMemoriaJson = "{\"memoria\": {\"id\": 1}, \"tipoEstadoMemoria\": {\"id\": 1}, \"fechaEstado\": \"2016-03-19T00:00:00Z\"}";

    EstadoMemoria estadoMemoria = generarMockEstadoMemoria(1L, 1L);

    BDDMockito.given(estadoMemoriaService.create(ArgumentMatchers.<EstadoMemoria>any())).willReturn(estadoMemoria);

    // when: Creamos un estado memoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEstadoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo estado memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria001"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEstadoMemoria.nombre").value("TipoEstadoMemoria001"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-EDITAR" })
  public void newEstadoMemoria_Error_Returns400() throws Exception {
    // given: Un nuevo estado memoria que produce un error al crearse
    String nuevoEstadoMemoriaJson = "{\"memoria\": {\"id\": 1}, \"tipoEstadoMemoria\": {\"id\": 1}, \"fechaEstado\": \"2016-03-19T00:00:00Z\"}";

    BDDMockito.given(estadoMemoriaService.create(ArgumentMatchers.<EstadoMemoria>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un estado memoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEstadoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-EDITAR" })
  public void replaceEstadoMemoria_ReturnsEstadoMemoria() throws Exception {
    // given: Un estado memoria a modificar
    String replaceEstadoMemoriaJson = "{\"id\": 1 ,\"memoria\": {\"id\": 2}, \"tipoEstadoMemoria\": {\"id\": 2}, \"fechaEstado\": \"2016-03-19T00:00:00Z\"}";

    EstadoMemoria memoria = generarMockEstadoMemoria(1L, 2L);

    BDDMockito.given(estadoMemoriaService.update(ArgumentMatchers.<EstadoMemoria>any())).willReturn(memoria);

    mockMvc
        .perform(MockMvcRequestBuilders.put(ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEstadoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el estado memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.id").value(2L))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEstadoMemoria.id").value(2L));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-EDITAR" })
  public void replaceEstadoMemoria_NotFound() throws Exception {
    // given: Una memoria a modificar
    String replaceEstadoMemoriaJson = "{\"id\": 1 ,\"memoria\": {\"id\": 2}, \"tipoEstadoMemoria\": {\"id\": 1}, \"fechaEstado\": \"2016-03-19T00:00:00Z\"}";

    BDDMockito.given(estadoMemoriaService.update(ArgumentMatchers.<EstadoMemoria>any()))
        .will((InvocationOnMock invocation) -> {
          throw new EstadoMemoriaNotFoundException(((EstadoMemoria) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEstadoMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-EDITAR" })
  public void removeEstadoMemoria_ReturnsOk() throws Exception {
    BDDMockito.given(estadoMemoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockEstadoMemoria(1L, 1L));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-VER" })
  public void findAll_Unlimited_ReturnsFullEstadoMemoriaList() throws Exception {
    // given: One hundred EstadoMemoria
    List<EstadoMemoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockEstadoMemoria(Long.valueOf(i), Long.valueOf(i)));
    }

    BDDMockito.given(estadoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(memorias));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred EstadoMemoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-VER" })
  public void findAll_ReturnNotContent() throws Exception {
    // given: EstadoMemoria empty
    List<EstadoMemoria> memorias = new ArrayList<>();

    BDDMockito.given(estadoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(memorias));

    mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-VER" })
  public void findAll_WithPaging_ReturnsEstadoMemoriaSubList() throws Exception {
    // given: One hundred EstadoMemoria
    List<EstadoMemoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockEstadoMemoria(Long.valueOf(i), Long.valueOf(i)));
    }

    BDDMockito.given(estadoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EstadoMemoria>>() {
          @Override
          public Page<EstadoMemoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<EstadoMemoria> content = memorias.subList(fromIndex, toIndex);
            Page<EstadoMemoria> page = new PageImpl<>(content, pageable, memorias.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked EstadoMemorias are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<EstadoMemoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<EstadoMemoria>>() {
        });

    // containing titulo='EstadoMemoria031' to 'EstadoMemoria040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      EstadoMemoria estadoMemoria = actual.get(i);
      Assertions.assertThat(estadoMemoria.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(estadoMemoria.getTipoEstadoMemoria().getNombre())
          .isEqualTo("TipoEstadoMemoria" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ESTADOMEMORIA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredEstadoMemoriaList() throws Exception {
    // given: One hundred EstadoMemoria and a search query
    List<EstadoMemoria> estadoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      estadoMemorias.add(generarMockEstadoMemoria(Long.valueOf(i), Long.valueOf(i)));
    }
    String query = "id:5";

    BDDMockito.given(estadoMemoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EstadoMemoria>>() {
          @Override
          public Page<EstadoMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<EstadoMemoria> content = new ArrayList<>();
            for (EstadoMemoria estadoMemoria : estadoMemorias) {
              if (estadoMemoria.getId().equals(5L)) {
                content.add(estadoMemoria);
              }
            }
            Page<EstadoMemoria> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(ESTADO_MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred EstadoMemoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto estado memoria
   * 
   * @param id                   id del estado memoria
   * @param idDatosEstadoMemoria id de la memoria y del tipo estado memoria
   * @return el objeto estado memoria
   */
  private EstadoMemoria generarMockEstadoMemoria(Long id, Long idDatosEstadoMemoria) {
    return new EstadoMemoria(id,
        generarMockMemoria(idDatosEstadoMemoria, "ref-9898", "Memoria" + String.format("%03d", idDatosEstadoMemoria),
            1),
        generarMockTipoEstadoMemoria(idDatosEstadoMemoria,
            "TipoEstadoMemoria" + String.format("%03d", idDatosEstadoMemoria), Boolean.TRUE),
        Instant.now());

  }

  /**
   * Función que devuelve un objeto EstadoMemoria.
   * 
   * @param id            id del memoria.
   * @param numReferencia número de la referencia de la memoria.
   * @param titulo        titulo de la memoria.
   * @param version       version de la memoria.
   * @return el objeto tipo EstadoMemoria
   */

  private Memoria generarMockMemoria(Long id, String numReferencia, String titulo, Integer version) {

    return new Memoria(id, numReferencia, generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id),
        generarMockComite(id, "comite" + id, true), titulo, "user-00" + id,
        generarMockTipoMemoria(1L, "TipoMemoria1", true),
        generarMockTipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.TRUE,
        generarMockRetrospectiva(1L), version, "CodOrganoCompetente", Boolean.TRUE, null);
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
    peticionEvaluacion.setExterno(Boolean.FALSE);
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
   * Función que devuelve un objeto comité.
   * 
   * @param id     identificador del comité.
   * @param comite comité.
   * @param activo indicador de activo.
   */
  private Comite generarMockComite(Long id, String comite, Boolean activo) {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    return new Comite(id, comite, "nombreSecretario", "nombreInvestigacion", "nombreDecreto", "articulo", formulario,
        activo);

  }

  /**
   * Función que devuelve un objeto tipo memoria.
   * 
   * @param id     identificador del tipo memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   */
  private TipoMemoria generarMockTipoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoMemoria(id, nombre, activo);

  }

  /**
   * Función que devuelve un objeto tipo estado memoria.
   * 
   * @param id     identificador del tipo estado memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   */
  private TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoEstadoMemoria(id, nombre, activo);

  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva generarMockRetrospectiva(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(generarMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva generarMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("NombreEstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }
}
