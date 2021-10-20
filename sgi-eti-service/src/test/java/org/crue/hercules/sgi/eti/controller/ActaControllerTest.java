package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.service.ActaService;
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
 * ActaControllerTest
 */
@WebMvcTest(ActaController.class)
public class ActaControllerTest extends BaseControllerTest {

  @MockBean
  private ActaService actaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ACTA_CONTROLLER_BASE_PATH = "/actas";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void getActa_WithId_ReturnsActa() throws Exception {
    BDDMockito.given(actaService.findById(ArgumentMatchers.anyLong())).willReturn((generarMockActa(1L, 123)));

    mockMvc
        .perform(MockMvcRequestBuilders.get(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("horaInicio").value(10))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoInicio").value(15))
        .andExpect(MockMvcResultMatchers.jsonPath("horaFin").value(12))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoFin").value(0))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Resumen123"))
        .andExpect(MockMvcResultMatchers.jsonPath("numero").value(123))
        .andExpect(MockMvcResultMatchers.jsonPath("estadoActual.id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("inactiva").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void existsActa_WithId_Returns200() throws Exception {
    BDDMockito.given(actaService.existsById(ArgumentMatchers.anyLong())).willReturn(true);

    mockMvc
        .perform(MockMvcRequestBuilders.head(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void existsActa_WithId_Returns204() throws Exception {
    BDDMockito.given(actaService.existsById(ArgumentMatchers.anyLong())).willReturn(false);

    mockMvc
        .perform(MockMvcRequestBuilders.head(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void getActa_NotFound_Returns404() throws Exception {
    BDDMockito.given(actaService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ActaNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C" })
  public void newActa_ReturnsActa() throws Exception {
    // given: Un acta nuevo
    String nuevoActaJson = "{\"convocatoriaReunion\": {\"id\": 100}, \"horaInicio\": 10, \"minutoInicio\": 15, \"horaFin\": 12, \"minutoFin\": 0, \"resumen\": \"Resumen123\", \"numero\": 123, \"estadoActual\": {\"id\": 1}, \"inactiva\": true, \"activo\": true}";

    Acta acta = generarMockActa(1L, 123);

    BDDMockito.given(actaService.create(ArgumentMatchers.<Acta>any())).willReturn(acta);

    // when: Creamos un acta
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(ACTA_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevoActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo acta y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("horaInicio").value(10))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoInicio").value(15))
        .andExpect(MockMvcResultMatchers.jsonPath("horaFin").value(12))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoFin").value(0))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Resumen123"))
        .andExpect(MockMvcResultMatchers.jsonPath("numero").value(123))
        .andExpect(MockMvcResultMatchers.jsonPath("estadoActual.id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("inactiva").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C" })
  public void newActa_Error_Returns400() throws Exception {
    // given: Un acta nuevo que produce un error al crearse
    String nuevoActaJson = "{\"id\": 1, \"convocatoriaReunion\": {\"id\": 100}, \"horaInicio\": 10, \"minutoInicio\": 15, \"horaFin\": 12, \"minutoFin\": 0, \"resumen\": \"Resumen123\", \"numero\": 123, \"estadoActual\": {\"id\": 1}, \"inactiva\": true, \"activo\": true}";

    BDDMockito.given(actaService.create(ArgumentMatchers.<Acta>any())).willThrow(new IllegalArgumentException());

    // when: Creamos un acta
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(ACTA_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevoActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-E" })
  public void replaceActa_ReturnsActa() throws Exception {
    // given: Un acta a modificar
    String replaceActaJson = "{\"id\": 1, \"convocatoriaReunion\": {\"id\": 100}, \"horaInicio\": 10, \"minutoInicio\": 15, \"horaFin\": 12, \"minutoFin\": 0, \"resumen\": \"Resumen123\", \"numero\": 123, \"estadoActual\": {\"id\": 1}, \"inactiva\": true, \"activo\": true}";

    Acta actaActualizado = generarMockActa(1L, 456);

    BDDMockito.given(actaService.update(ArgumentMatchers.<Acta>any())).willReturn(actaActualizado);

    mockMvc
        .perform(MockMvcRequestBuilders.put(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el acta y lo devuelve
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("horaInicio").value(10))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoInicio").value(15))
        .andExpect(MockMvcResultMatchers.jsonPath("horaFin").value(12))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoFin").value(0))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Resumen456"))
        .andExpect(MockMvcResultMatchers.jsonPath("numero").value(456))
        .andExpect(MockMvcResultMatchers.jsonPath("estadoActual.id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("inactiva").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-E" })
  public void replaceActa_NotFound() throws Exception {
    // given: Un acta a modificar
    String replaceActaJson = "{\"id\": 1, \"convocatoriaReunion\": {\"id\": 100}, \"horaInicio\": 10, \"minutoInicio\": 15, \"horaFin\": 12, \"minutoFin\": 0, \"resumen\": \"Resumen123\", \"numero\": 123, \"estadoActual\": {\"id\": 1}, \"inactiva\": true, \"activo\": true}";

    BDDMockito.given(actaService.update(ArgumentMatchers.<Acta>any())).will((InvocationOnMock invocation) -> {
      throw new ActaNotFoundException(((Acta) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceActaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-B" })
  public void removeActa_ReturnsOk() throws Exception {
    BDDMockito.given(actaService.findById(ArgumentMatchers.anyLong())).willReturn(generarMockActa(1L, 123));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void findAll_Unlimited_ReturnsFullActaWithNumEvaluacionesList() throws Exception {
    // given: One hundred actas
    List<ActaWithNumEvaluaciones> actas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      actas.add(generarMockActaWithNumEvaluaciones(Long.valueOf(i), i));
    }

    BDDMockito.given(actaService.findAllActaWithNumEvaluaciones(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any())).willReturn(new PageImpl<>(actas));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(ACTA_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred actas
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void findAll_WithPaging_ReturnsActaWithNumEvaluacionesSubList() throws Exception {
    // given: One hundred actas
    List<ActaWithNumEvaluaciones> actas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      actas.add(generarMockActaWithNumEvaluaciones(Long.valueOf(i), i));
    }

    BDDMockito
        .given(actaService.findAllActaWithNumEvaluaciones(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any()))
        .willAnswer(new Answer<Page<ActaWithNumEvaluaciones>>() {
          @Override
          public Page<ActaWithNumEvaluaciones> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ActaWithNumEvaluaciones> content = actas.subList(fromIndex, toIndex);
            Page<ActaWithNumEvaluaciones> page = new PageImpl<>(content, pageable, actas.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(ACTA_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked actas are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<ActaWithNumEvaluaciones> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ActaWithNumEvaluaciones>>() {
        });

    // containing id='31' to '40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      ActaWithNumEvaluaciones acta = actual.get(i);
      Assertions.assertThat(acta.getId()).isEqualTo(j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredActaWithNumEvaluacionesList() throws Exception {
    // given: One hundred actas and a search query
    List<ActaWithNumEvaluaciones> actas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      actas.add(generarMockActaWithNumEvaluaciones(Long.valueOf(i), 123));
    }
    String query = "id:5";

    BDDMockito
        .given(actaService.findAllActaWithNumEvaluaciones(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any()))
        .willAnswer(new Answer<Page<ActaWithNumEvaluaciones>>() {
          @Override
          public Page<ActaWithNumEvaluaciones> answer(InvocationOnMock invocation) throws Throwable {
            List<ActaWithNumEvaluaciones> content = new ArrayList<>();
            for (ActaWithNumEvaluaciones acta : actas) {
              if (acta.getId() == 5) {
                content.add(acta);
              }
            }
            Page<ActaWithNumEvaluaciones> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(ACTA_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one acta
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-FIN" })
  public void finishActa_ReturnsOk() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/finalizar", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  /**
   * Función que devuelve un objeto Acta
   * 
   * @param id     id del acta
   * @param numero numero del acta
   * @return el objeto Acta
   */
  public Acta generarMockActa(Long id, Integer numero) {
    Comite comite = new Comite();
    comite.setId(1L);
    comite.setComite("CEEA");
    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);
    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(100L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.parse("2020-08-01T00:00:00Z"));
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(1L);
    tipoEstadoActa.setNombre("En elaboración");
    tipoEstadoActa.setActivo(Boolean.TRUE);

    Acta acta = new Acta();
    acta.setId(id);
    acta.setConvocatoriaReunion(convocatoriaReunion);
    acta.setHoraInicio(10);
    acta.setMinutoInicio(15);
    acta.setHoraFin(12);
    acta.setMinutoFin(0);
    acta.setResumen("Resumen" + numero);
    acta.setNumero(numero);
    acta.setEstadoActual(tipoEstadoActa);
    acta.setInactiva(true);
    acta.setActivo(true);

    return acta;
  }

  /**
   * Función que devuelve un objeto ActaWithNumEvaluaciones
   * 
   * @param id     id del acta
   * @param numero numero del acta
   * @return el objeto Acta
   */
  public ActaWithNumEvaluaciones generarMockActaWithNumEvaluaciones(Long id, Integer numero) {
    Acta acta = generarMockActa(id, numero);

    ActaWithNumEvaluaciones returnValue = new ActaWithNumEvaluaciones();
    returnValue.setId(acta.getId());
    returnValue.setComite(acta.getConvocatoriaReunion().getComite().getComite());
    returnValue.setFechaEvaluacion(acta.getConvocatoriaReunion().getFechaEvaluacion());
    returnValue.setNumeroActa(acta.getNumero());
    returnValue.setConvocatoria(acta.getConvocatoriaReunion().getTipoConvocatoriaReunion().getNombre());
    returnValue.setNumEvaluaciones(1);
    returnValue.setNumRevisiones(2);
    returnValue.setNumTotal(returnValue.getNumEvaluaciones() + returnValue.getNumRevisiones());
    returnValue.setEstadoActa(acta.getEstadoActual());
    return returnValue;
  }

}
