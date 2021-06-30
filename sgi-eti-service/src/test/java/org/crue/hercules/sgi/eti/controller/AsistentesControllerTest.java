package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.AsistentesNotFoundException;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.service.AsistentesService;
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
 * AsistentesControllerTest
 */
@WebMvcTest(AsistentesController.class)
public class AsistentesControllerTest extends BaseControllerTest {

  @MockBean
  private AsistentesService asistenteService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ASISTENTE_CONTROLLER_BASE_PATH = "/asistentes";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ASISTENTES-VER" })
  public void getAsistentes_WithId_ReturnsAsistentes() throws Exception {
    BDDMockito.given(asistenteService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockAsistentes(1L, "Motivo 1", Boolean.TRUE)));

    mockMvc.perform(MockMvcRequestBuilders.get(ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("motivo").value("Motivo 1"))
        .andExpect(MockMvcResultMatchers.jsonPath("asistencia").value(Boolean.TRUE))
        .andExpect(MockMvcResultMatchers.jsonPath("evaluador.id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value(1));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ASISTENTES-VER" })
  public void getAsistentes_NotFound_Returns404() throws Exception {
    BDDMockito.given(asistenteService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new AsistentesNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C" })
  public void newAsistentes_ReturnsAsistentes() throws Exception {
    // given: Una entidad Asistentes nueva
    String nuevoAsistentesJson = "{ \"motivo\": \"Motivo 1\", \"asistenecia\": \"true\", \"convocatoriaReunion\": {\"id\": \"1\"}, \"evaluador\": {\"id\": \"1\"}}";

    Asistentes asistente = generarMockAsistentes(1L, "Motivo 1", Boolean.TRUE);

    BDDMockito.given(asistenteService.create(ArgumentMatchers.<Asistentes>any())).willReturn(asistente);

    // when: Creamos Asistentes
    mockMvc
        .perform(MockMvcRequestBuilders.post(ASISTENTE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoAsistentesJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea los nuevos Asistentes y los devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("motivo").value("Motivo 1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C" })
  public void newAsistentes_Error_Returns400() throws Exception {
    // given: Unos Asistentes nuevos que produce un error al crearse
    String nuevoAsistentesJson = "{ \"motivo\": \"Motivo1\", \"asistenecia\": \"true\", \"convocatoriaReunion\": {\"id\": \"1\"}, \"evaluador\": {\"id\": \"1\"}}";

    BDDMockito.given(asistenteService.create(ArgumentMatchers.<Asistentes>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos Asistentes
    mockMvc
        .perform(MockMvcRequestBuilders.post(ASISTENTE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoAsistentesJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void replaceAsistentes_ReturnsAsistentes() throws Exception {
    // given: Asistentes a modificar
    String replaceAsistentesJson = "{\"id\": 1, \"motivo\": \"Motivo1\", \"asistenecia\": \"true\", \"convocatoriaReunion\": {\"id\": \"1\"}, \"evaluador\": {\"id\": \"1\"}}";

    Asistentes asistente = generarMockAsistentes(1L, "Replace Motivo 1", Boolean.TRUE);

    BDDMockito.given(asistenteService.update(ArgumentMatchers.<Asistentes>any())).willReturn(asistente);

    mockMvc
        .perform(MockMvcRequestBuilders.put(ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceAsistentesJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica Asistentes y los devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("motivo").value("Replace Motivo 1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void replaceAsistentes_NotFound() throws Exception {
    // given: Asistentes a modificar
    String replaceAsistentesJson = "{\"id\": 1, \"motivo\": \"Motivo1\", \"asistenecia\": \"true\", \"convocatoriaReunion\": {\"id\": \"1\"}, \"evaluador\": {\"id\": \"1\"}}";

    BDDMockito.given(asistenteService.update(ArgumentMatchers.<Asistentes>any()))
        .will((InvocationOnMock invocation) -> {
          throw new AsistentesNotFoundException(((Asistentes) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceAsistentesJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ASISTENTES-EDITAR" })
  public void removeAsistentes_ReturnsOk() throws Exception {
    BDDMockito.given(asistenteService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockAsistentes(1L, "Motivo 1", Boolean.TRUE));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ASISTENTES-VER" })
  public void findAll_Unlimited_ReturnsFullAsistentesList() throws Exception {
    // given: One hundred Asistentes
    List<Asistentes> asistentes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      asistentes.add(generarMockAsistentes(Long.valueOf(i), "Motivo" + String.format("%03d", i), Boolean.TRUE));
    }

    BDDMockito.given(asistenteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(asistentes));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(ASISTENTE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Asistentes
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ASISTENTES-VER" })
  public void findAll_ReturnNotContent() throws Exception {
    // given: Asistentes empty
    List<Asistentes> asistentes = new ArrayList<>();

    BDDMockito.given(asistenteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(asistentes));

    mockMvc
        .perform(MockMvcRequestBuilders.get(ASISTENTE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ASISTENTES-VER" })
  public void findAll_WithPaging_ReturnsAsistentesSubList() throws Exception {
    // given: One hundred Asistentes
    List<Asistentes> asistentes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      asistentes.add(generarMockAsistentes(Long.valueOf(i), "Motivo" + String.format("%03d", i), Boolean.TRUE));
    }

    BDDMockito.given(asistenteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Asistentes>>() {
          @Override
          public Page<Asistentes> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Asistentes> content = asistentes.subList(fromIndex, toIndex);
            Page<Asistentes> page = new PageImpl<>(content, pageable, asistentes.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(ASISTENTE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Asistentess are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Asistentes> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Asistentes>>() {
        });

    // containing motivo='Motivo031' to 'Motivo040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Asistentes asistente = actual.get(i);
      Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ASISTENTES-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredAsistentesList() throws Exception {
    // given: One hundred Asistentes and a search query
    List<Asistentes> asistentes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {

      asistentes.add(generarMockAsistentes(Long.valueOf(i), "Motivo" + String.format("%03d", i), Boolean.TRUE));
    }
    String query = "motivo~Motivo%,id:5";

    BDDMockito.given(asistenteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Asistentes>>() {
          @Override
          public Page<Asistentes> answer(InvocationOnMock invocation) throws Throwable {
            List<Asistentes> content = new ArrayList<>();
            for (Asistentes asistente : asistentes) {
              if (asistente.getMotivo().startsWith("Motivo") && asistente.getId() == 5L) {
                content.add(asistente);
              }
            }
            Page<Asistentes> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(ASISTENTE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Asistentes
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto Asistentes
   * 
   * @param id         id del asistentes
   * @param motivo     motivo
   * @param asistencia asistencia
   * @return el objeto Asistentes
   */

  private Asistentes generarMockAsistentes(Long id, String motivo, Boolean asistencia) {

    Asistentes asistentes = new Asistentes();
    asistentes.setId(id);
    asistentes.setEvaluador(generarMockEvaluador(id, "Resumen " + motivo));
    asistentes.setConvocatoriaReunion(getMockConvocatoriaReunion(id, id));
    asistentes.setMotivo(motivo);
    asistentes.setAsistencia(asistencia);

    return asistentes;
  }

  /**
   * Función que devuelve un objeto Evaluador
   * 
   * @param id      id del Evaluador
   * @param resumen el resumen de Evaluador
   * @return el objeto Evaluador
   */

  private Evaluador generarMockEvaluador(Long id, String resumen) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(Instant.now());
    evaluador.setFechaBaja(Instant.now());
    evaluador.setResumen(resumen);
    evaluador.setPersonaRef("user-" + String.format("%03d", id));
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  /**
   * Genera un objeto {@link ConvocatoriaReunion}
   * 
   * @param id       id de la convocatoria reunión
   * @param comiteId comite id
   * @return un objeto {@link ConvocatoriaReunion}
   */
  private ConvocatoriaReunion getMockConvocatoriaReunion(Long id, Long comiteId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(comiteId, "Comite" + comiteId, formulario, Boolean.TRUE);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    final ConvocatoriaReunion data = new ConvocatoriaReunion();
    data.setId(id);
    data.setComite(comite);
    data.setFechaEvaluacion(Instant.parse("2020-07-20T00:00:00Z"));
    data.setFechaLimite(Instant.parse("2020-08-20T23:59:59Z"));
    data.setLugar("Lugar " + String.format("%03d", id));
    data.setOrdenDia("Orden del día convocatoria reunión " + String.format("%03d", id));
    data.setAnio(2020);
    data.setNumeroActa(100L);
    data.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    data.setHoraInicio(7);
    data.setMinutoInicio(30);
    data.setFechaEnvio(Instant.parse("2020-07-13T00:00:00Z"));
    data.setActivo(Boolean.TRUE);

    return data;
  }

}
