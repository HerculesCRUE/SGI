package org.crue.hercules.sgi.eti.controller;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
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
import org.crue.hercules.sgi.eti.service.ActaService;
import org.crue.hercules.sgi.eti.service.AsistentesService;
import org.crue.hercules.sgi.eti.service.ConvocatoriaReunionService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
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
 * ConvocatoriaReunionControllerTest
 */
@WebMvcTest(ConvocatoriaReunionController.class)
public class ConvocatoriaReunionControllerTest extends BaseControllerTest {

  @MockBean
  private AsistentesService asistentesService;

  @MockBean
  private EvaluacionService evaluacionService;

  @MockBean
  private ConvocatoriaReunionService convocatoriaReunionService;

  @MockBean
  private ActaService actaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH = "/convocatoriareuniones";
  private static final String PATH_PARAMETER_BY_EVALUACIONES = "/evaluaciones";
  private static final String PATH_PARAMETER_WITH_DATOS_GENERALES = "/datos-generales";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C" })
  public void create_ReturnsConvocatoriaReunion() throws Exception {

    // given: Nueva entidad sin Id
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).toString();

    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);
    String nuevoConvocatoriaReunionJson = mapper.writeValueAsString(response);

    BDDMockito.given(convocatoriaReunionService.create(ArgumentMatchers.<ConvocatoriaReunion>any()))
        .willReturn(response);

    // when: Se crea la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(nuevoConvocatoriaReunionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: La entidad se crea correctamente
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value(response.getComite()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaEvaluacion").value(response.getFechaEvaluacion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaLimite").value(response.getFechaLimite().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("lugar").value(response.getLugar()))
        .andExpect(MockMvcResultMatchers.jsonPath("ordenDia").value(response.getOrdenDia()))
        .andExpect(MockMvcResultMatchers.jsonPath("anio").value(response.getAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("numeroActa").value(response.getNumeroActa()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("tipoConvocatoriaReunion").value(response.getTipoConvocatoriaReunion()))
        .andExpect(MockMvcResultMatchers.jsonPath("horaInicio").value(response.getHoraInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoInicio").value(response.getMinutoInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaEnvio").value(response.getFechaEnvio().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(response.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C" })
  public void create_WithId_Returns400() throws Exception {

    // given: Nueva entidad con Id
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).toString();
    String nuevoConvocatoriaReunionJson = mapper.writeValueAsString(getMockData(1L, 1L, 1L));

    BDDMockito.given(convocatoriaReunionService.create(ArgumentMatchers.<ConvocatoriaReunion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Se crea la entidad
    // then: Se produce error porque ya tiene Id
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(nuevoConvocatoriaReunionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-E" })
  public void update_WithExistingId_ReturnsConvocatoriaReunion() throws Exception {

    // given: Entidad existente que se va a actualizar
    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on
    String replaceConvocatoriaReunionJson = mapper.writeValueAsString(response);

    BDDMockito.given(convocatoriaReunionService.update(ArgumentMatchers.<ConvocatoriaReunion>any()))
        .willReturn(response);

    // when: Se actualiza la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(replaceConvocatoriaReunionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Los datos se actualizan correctamente
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value(response.getComite()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaEvaluacion").value(response.getFechaEvaluacion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaLimite").value(response.getFechaLimite().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("lugar").value(response.getLugar()))
        .andExpect(MockMvcResultMatchers.jsonPath("ordenDia").value(response.getOrdenDia()))
        .andExpect(MockMvcResultMatchers.jsonPath("anio").value(response.getAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("numeroActa").value(response.getNumeroActa()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("tipoConvocatoriaReunion").value(response.getTipoConvocatoriaReunion()))
        .andExpect(MockMvcResultMatchers.jsonPath("horaInicio").value(response.getHoraInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoInicio").value(response.getMinutoInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaEnvio").value(response.getFechaEnvio().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(response.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {

    // given: Entidad a actualizar que no existe
    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on
    String replaceConvocatoriaReunionJson = mapper.writeValueAsString(response);

    BDDMockito.given(convocatoriaReunionService.update(ArgumentMatchers.<ConvocatoriaReunion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new ConvocatoriaReunionNotFoundException(((ConvocatoriaReunion) invocation.getArgument(0)).getId());
        });

    // when: Se actualiza la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(replaceConvocatoriaReunionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se produce error porque no encuentra la entidad a actualizar
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-B" })
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente
    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.given(convocatoriaReunionService.findById(ArgumentMatchers.anyLong())).willReturn(response);

    // when: Se elimina la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.delete(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: La entidad se elimina correctamente
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-B" })
  public void delete_WithNoExistingId_Returns404() throws Exception {

    // given: Id de una entidad que no existe
    ConvocatoriaReunion convocatoriaReunion = getMockData(1L, 1L, 1L);
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.given(convocatoriaReunionService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new ConvocatoriaReunionNotFoundException(invocation.getArgument(0));
        });

    // when: Se elimina la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.delete(url, convocatoriaReunion.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se produce error porque no encuentra la entidad a eliminar
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-V" })
  public void findById_WithExistingId_ReturnsConvocatoriaReunion() throws Exception {

    // given: Entidad con un determinado Id
    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.given(convocatoriaReunionService.findById(response.getId())).willReturn(response);

    // when: Se busca la entidad por ese Id
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera la entidad con el Id
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value(response.getComite()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaEvaluacion").value(response.getFechaEvaluacion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaLimite").value(response.getFechaLimite().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("lugar").value(response.getLugar()))
        .andExpect(MockMvcResultMatchers.jsonPath("ordenDia").value(response.getOrdenDia()))
        .andExpect(MockMvcResultMatchers.jsonPath("anio").value(response.getAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("numeroActa").value(response.getNumeroActa()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("tipoConvocatoriaReunion").value(response.getTipoConvocatoriaReunion()))
        .andExpect(MockMvcResultMatchers.jsonPath("horaInicio").value(response.getHoraInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoInicio").value(response.getMinutoInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaEnvio").value(response.getFechaEnvio().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(response.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);

    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .toString();
    // @formatter:on

    BDDMockito.given(convocatoriaReunionService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new ConvocatoriaReunionNotFoundException(invocation.getArgument(0));
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
  @WithMockUser(username = "user", authorities = { "ETI-CNV-V", "ETI-CNV-E" })
  public void findByIdWithNumEvaluacionesActivasNoRevMin_WithExistingId_ReturnsConvocatoriaReunionDatosGenerales()
      throws Exception {

    // given: Entidad con un determinado Id
    ConvocatoriaReunionDatosGenerales response = new ConvocatoriaReunionDatosGenerales(getMockData(1L, 1L, 1L), 1L, 1L);
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .append(PATH_PARAMETER_WITH_DATOS_GENERALES)
        .toString();
    // @formatter:on

    BDDMockito.given(convocatoriaReunionService.findByIdWithDatosGenerales(response.getId())).willReturn(response);

    // when: Se busca la entidad por ese Id
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, response.getId()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera la entidad con el Id
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value(response.getComite()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaEvaluacion").value(response.getFechaEvaluacion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaLimite").value(response.getFechaLimite().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("lugar").value(response.getLugar()))
        .andExpect(MockMvcResultMatchers.jsonPath("ordenDia").value(response.getOrdenDia()))
        .andExpect(MockMvcResultMatchers.jsonPath("anio").value(response.getAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("numeroActa").value(response.getNumeroActa()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("tipoConvocatoriaReunion").value(response.getTipoConvocatoriaReunion()))
        .andExpect(MockMvcResultMatchers.jsonPath("horaInicio").value(response.getHoraInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoInicio").value(response.getMinutoInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaEnvio").value(response.getFechaEnvio().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(response.getActivo()))
        .andExpect(MockMvcResultMatchers.jsonPath("numEvaluaciones").value(response.getNumEvaluaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-V", "ETI-CNV-E" })
  public void findByIdWithNumEvaluacionesActivasNoRevMin_WithNoExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    ConvocatoriaReunionDatosGenerales response = new ConvocatoriaReunionDatosGenerales(getMockData(1L, 1L, 1L), 1L, 1L);

    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .append(PATH_PARAMETER_WITH_DATOS_GENERALES)
        .toString();
    // @formatter:on

    BDDMockito.given(convocatoriaReunionService.findByIdWithDatosGenerales(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new ConvocatoriaReunionNotFoundException(invocation.getArgument(0));
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
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-CNV-V" })
  public void findAll_Unlimited_ReturnsFullConvocatoriaReunionList() throws Exception {

    // given: Datos existentes
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).toString();

    List<ConvocatoriaReunion> response = new LinkedList<ConvocatoriaReunion>();
    response.add(getMockData(1L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));

    BDDMockito
        .given(convocatoriaReunionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
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
        new TypeReference<List<ConvocatoriaReunion>>() {
        })).isEqualTo(response);

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-CNV-V" })
  public void findAll_Unlimited_Returns204() throws Exception {

    // given: No hay datos
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).toString();

    BDDMockito
        .given(convocatoriaReunionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));
    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía);
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-CNV-V" })
  public void findAll_WithPaging_ReturnsConvocatoriaReunionSubList() throws Exception {

    // given: Datos existentes
    String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).toString();

    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));
    response.add(getMockData(3L, 2L, 3L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<ConvocatoriaReunion> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito
        .given(convocatoriaReunionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
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
        new TypeReference<List<ConvocatoriaReunion>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-CNV-V" })
  public void findAll_WithPaging_Returns204() throws Exception {

    // given: Datos existentes
    String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).toString();

    List<ConvocatoriaReunion> response = new LinkedList<ConvocatoriaReunion>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<ConvocatoriaReunion> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito
        .given(convocatoriaReunionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
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
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-CNV-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredConvocatoriaReunionList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));
    response.add(getMockData(3L, 2L, 1L));
    response.add(getMockData(4L, 2L, 2L));
    response.add(getMockData(5L, 3L, 3L));

    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).toString();

    // search
    String query = "numeroActa<4%,id:3";

    BDDMockito
        .given(convocatoriaReunionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaReunion>>() {
          @Override
          public Page<ConvocatoriaReunion> answer(InvocationOnMock invocation) throws Throwable {
            List<ConvocatoriaReunion> content = new LinkedList<>();
            for (ConvocatoriaReunion item : response) {
              if (item.getNumeroActa() < 4 && item.getId().equals(3L)) {
                content.add(item);
              }
            }
            Page<ConvocatoriaReunion> page = new PageImpl<>(content);
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
        new TypeReference<List<ConvocatoriaReunion>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findAsistentes_Unlimited_ReturnsFullAsistentesList() throws Exception {

    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID).append("/asistentes")
        .toString();
    // @formatter:on

    List<Asistentes> response = new ArrayList<>();
    response.add(generarMockAsistentes(1L, "Motivo1", Boolean.TRUE));
    response.add(generarMockAsistentes(2L, "Motivo2", Boolean.TRUE));

    BDDMockito.given(
        asistentesService.findAllByConvocatoriaReunionId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(response));

    // when: Se buscan todos los datos
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los datos
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Asistentes>>() {
        })).isEqualTo(response);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findAsistentes_Unlimited_Returns204() throws Exception {

    // given: Sin datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID).append("/asistentes")
        .toString();
    // @formatter:on

    BDDMockito.given(
        asistentesService.findAllByConvocatoriaReunionId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(url, convocatoriaReunionId).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía);
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findAsistentes_WithPaging_ReturnsAsistentesSubList() throws Exception {

    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID).append("/asistentes")
        .toString();
    // @formatter:on

    List<Asistentes> response = new ArrayList<>();
    response.add(generarMockAsistentes(1L, "Motivo1", Boolean.TRUE));
    response.add(generarMockAsistentes(2L, "Motivo2", Boolean.TRUE));
    response.add(generarMockAsistentes(3L, "Motivo3", Boolean.TRUE));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Asistentes> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(
        asistentesService.findAllByConvocatoriaReunionId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", pageable.getPageNumber())
            .header("X-Page-Size", pageable.getPageSize()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan los datos correctamente según la paginación solicitada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", String.valueOf(pageable.getPageNumber())))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", String.valueOf(pageable.getPageSize())))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", String.valueOf(response.size())))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Asistentes>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findAsistentes_WithPaging_Returns204() throws Exception {

    // given: Sin datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID).append("/asistentes")
        .toString();
    // @formatter:on
    List<Asistentes> response = new ArrayList<Asistentes>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Asistentes> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(
        asistentesService.findAllByConvocatoriaReunionId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", pageable.getPageNumber())
            .header("X-Page-Size", pageable.getPageSize()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista de datos paginados vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findEvaluacionesActivas_Unlimited_ReturnsFullEvaluacionList() throws Exception {
    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .append("/evaluaciones-activas").toString();
    // @formatter:on

    List<Evaluacion> response = new ArrayList<>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1)));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3)));

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: Se buscan todos los datos
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los datos
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Evaluacion>>() {
        })).isEqualTo(response);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-ACT-C", "ETI-ACT-E" })
  public void findEvaluacionesActivas_Unlimited_Returns204() throws Exception {

    // given: Sin datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .append("/evaluaciones-activas").toString();
    // @formatter:on

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(url, convocatoriaReunionId).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía);
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findEvaluacionesActivas_WithPaging_ReturnsEvaluacionSubList() throws Exception {

    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .append("/evaluaciones-activas").toString();
    // @formatter:on

    List<Evaluacion> response = new ArrayList<>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1)));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3)));
    response.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5)));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", pageable.getPageNumber())
            .header("X-Page-Size", pageable.getPageSize()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan los datos correctamente según la paginación solicitada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", String.valueOf(pageable.getPageNumber())))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", String.valueOf(pageable.getPageSize())))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", String.valueOf(response.size())))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Evaluacion>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findEvaluacionesActivas_WithPaging_Returns204() throws Exception {

    // given: Sin datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .append("/evaluaciones-activas").toString();
    // @formatter:on

    List<Evaluacion> response = new ArrayList<Evaluacion>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", pageable.getPageNumber())
            .header("X-Page-Size", pageable.getPageSize()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista de datos paginados vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-ACT-C", "ETI-ACT-E" })
  public void getEvaluaciones_WithPaging_ReturnsEvaluacionSubList() throws Exception {
    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID).append(PATH_PARAMETER_BY_EVALUACIONES)
        .toString();
    // @formatter:on

    List<Evaluacion> response = new ArrayList<>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1)));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3)));
    response.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5)));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", pageable.getPageNumber())
            .header("X-Page-Size", pageable.getPageSize()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan los datos correctamente según la paginación solicitada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", String.valueOf(pageable.getPageNumber())))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", String.valueOf(pageable.getPageSize())))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", String.valueOf(response.size())))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Evaluacion>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-ACT-C", "ETI-ACT-E" })
  public void getEvaluaciones_WithPaging_Returns204() throws Exception {
    // given: Sin datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID).append(PATH_PARAMETER_BY_EVALUACIONES)
        .toString();
    // @formatter:on

    List<Evaluacion> response = new ArrayList<Evaluacion>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", pageable.getPageNumber())
            .header("X-Page-Size", pageable.getPageSize()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista de datos paginados vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-ACT-C", "ETI-ACT-E" })
  public void getEvaluaciones_Unlimited_Returns204() throws Exception {
    // given: Sin datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID).append(PATH_PARAMETER_BY_EVALUACIONES)
        .toString();
    // @formatter:on

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(url, convocatoriaReunionId).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía);
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-ACT-C", "ETI-ACT-E" })
  public void getEvaluaciones_Unlimited_ReturnsFullEvaluacionList() throws Exception {
    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID).append(PATH_PARAMETER_BY_EVALUACIONES)
        .toString();
    // @formatter:on

    List<Evaluacion> response = new ArrayList<>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1)));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3)));

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: Se buscan todos los datos
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los datos
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Evaluacion>>() {
        })).isEqualTo(response);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C", "ETI-CNV-E" })
  public void delete_memoria_Return200() throws Exception {

    // given: Entidad existente
    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);
    Evaluacion respoEvaluacion = generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1));
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append("/" + response.getId().toString()).append("/evaluacion")
        .append("/" + respoEvaluacion.getId().toString())
        .toString();
    // @formatter:on

    BDDMockito.given(convocatoriaReunionService.findById(ArgumentMatchers.anyLong())).willReturn(response);

    // when: Se elimina la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.delete(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: La entidad se elimina correctamente
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findConvocatoriasSinActa() throws Exception {

    // given: Datos existentes
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + "/acta-no-asignada").toString();

    List<ConvocatoriaReunion> response = new LinkedList<ConvocatoriaReunion>();
    response.add(getMockData(1L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));

    BDDMockito.given(convocatoriaReunionService.findConvocatoriasSinActa()).willReturn(response);

    // when: Se buscan todos los datos
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los datos
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<ConvocatoriaReunion>>() {
        })).isEqualTo(response);

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findConvocatoriasSinActa_ReturnNoContent() throws Exception {

    // given: ConvocatoriaReunion empty
    List<ConvocatoriaReunion> response = new LinkedList<ConvocatoriaReunion>();

    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + "/acta-no-asignada").toString();

    BDDMockito.given(convocatoriaReunionService.findConvocatoriasSinActa()).willReturn(response);

    mockMvc
        .perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());

  }

  /**
   * Genera un objeto {@link ConvocatoriaReunion}
   * 
   * @param id
   * @param comiteId
   * @param tipoId
   * @return ConvocatoriaReunion
   */
  private ConvocatoriaReunion getMockData(Long id, Long comiteId, Long tipoId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(comiteId, "Comite" + comiteId, "nombreInvestigacion", Genero.M, formulario,
        Boolean.TRUE);

    String tipo_txt = (tipoId == 1L) ? "Ordinaria" : (tipoId == 2L) ? "Extraordinaria" : "Seguimiento";
    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(tipoId, tipo_txt, Boolean.TRUE);

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final ConvocatoriaReunion data = new ConvocatoriaReunion();
    data.setId(id);
    data.setComite(comite);
    data.setFechaEvaluacion(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());
    data.setFechaLimite(LocalDate.of(2020, 8, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());
    data.setLugar("Lugar " + txt);
    data.setOrdenDia("Orden del día convocatoria reunión " + txt);
    data.setAnio(2020);
    data.setNumeroActa(id);
    data.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    data.setHoraInicio(7 + id.intValue());
    data.setMinutoInicio(30);
    data.setFechaEnvio(Instant.parse("2020-07-13T00:00:00Z"));
    data.setActivo(Boolean.TRUE);

    return data;
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
    asistentes.setConvocatoriaReunion(getMockData(id, id, 1L));
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
    Comite comite = new Comite(1L, "Comite1", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);

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
    Comite comite = new Comite(1L, "Comite1", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.FALSE,
        new Retrospectiva(id, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), Instant.now()), 3,
        "CodOrganoCompetente", Boolean.TRUE, null);

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
    evaluacion.setConvocatoriaReunion(getMockData(id, 1L, 1L));
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setVersion(2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }
}
