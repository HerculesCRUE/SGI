package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.converter.ConvocatoriaFaseConverter;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseOutput;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.enums.TipoJustificacion;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHitoAviso;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaDocumentoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaFaseService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaHitoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPalabraClaveService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPartidaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoCategoriaProfesionalService;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoNivelAcademicoService;
import org.crue.hercules.sgi.csp.service.RequisitoIPCategoriaProfesionalService;
import org.crue.hercules.sgi.csp.service.RequisitoIPNivelAcademicoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * ConvocatoriaControllerTest
 */
@WebMvcTest(ConvocatoriaController.class)
class ConvocatoriaControllerTest extends BaseControllerTest {

  @Autowired
  private ModelMapper modelMapper;

  @MockBean
  private ConvocatoriaService service;
  @MockBean
  private ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService;
  @MockBean
  private ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService;
  @MockBean
  private ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService;
  @MockBean
  private ConvocatoriaDocumentoService convocatoriaDocumentoService;
  @MockBean
  private ConvocatoriaEnlaceService convocatoriaEnlaceService;
  @MockBean
  private ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService;
  @MockBean
  private ConvocatoriaFaseService convocatoriaFaseService;
  @MockBean
  private ConvocatoriaHitoService convocatoriaHitoService;
  @MockBean
  private ConvocatoriaPartidaService convocatoriaPartidaService;
  @MockBean
  private ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService;
  @MockBean
  private ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService;
  @MockBean
  private ConvocatoriaConceptoGastoService convocatoriaConceptoGastoService;
  @MockBean
  private ConvocatoriaConceptoGastoCodigoEcService convocatoriaConceptoGastoCodigoEcService;
  @MockBean
  private RequisitoIPNivelAcademicoService requisitoIPNivelAcademicoService;
  @MockBean
  private RequisitoIPCategoriaProfesionalService requisitoIPCategoriaProfesionalService;
  @MockBean
  private RequisitoEquipoCategoriaProfesionalService requisitoEquipoCategoriaProfesionalService;
  @MockBean
  private RequisitoEquipoNivelAcademicoService requisitoEquipoNivelAcademicoService;
  @MockBean
  private ConvocatoriaPalabraClaveService convocatoriaPalabraClaveService;
  @MockBean
  private ConvocatoriaFaseConverter convocatoriaFaseConverter;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String PATH_PARAMETER_REGISTRAR = "/registrar";
  private static final String PATH_PARAMETER_TODOS = "/todos";
  private static final String PATH_PARAMETER_MODIFICABLE = "/modificable";
  private static final String PATH_PARAMETER_REGISTRABLE = "/registrable";
  private static final String CONTROLLER_BASE_PATH = "/convocatorias";
  private static final String PATH_AREA_TEMATICA = "/convocatoriaareatematicas";
  private static final String PATH_ENTIDAD_DOCUMENTO = "/convocatoriadocumentos";
  private static final String PATH_ENTIDAD_ENLACE = "/convocatoriaenlaces";
  private static final String PATH_ENTIDAD_CONVOCANTE = "/convocatoriaentidadconvocantes";
  private static final String PATH_ENTIDAD_FINANCIADORA = "/convocatoriaentidadfinanciadoras";
  private static final String PATH_ENTIDAD_GESTORA = "/convocatoriaentidadgestoras";
  private static final String PATH_FASE = "/convocatoriafases";
  private static final String PATH_HITO = "/convocatoriahitos";
  private static final String PATH_PERIODO_JUSTIFICACION = "/convocatoriaperiodojustificaciones";
  private static final String PATH_PERIODO_SEGUIMIENTO_CIENTIFICO = "/convocatoriaperiodoseguimientocientificos";
  private static final String PATH_CONCEPTO_GASTO_PERMITIDO = "/convocatoriagastos/permitidos";
  private static final String PATH_CONCEPTO_GASTO_NO_PERMITIDO = "/convocatoriagastos/nopermitidos";
  private static final String PATH_PARAMETER_RESTRINGIDOS = "/restringidos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void create_WithId_Returns400() throws Exception {
    // given: a Convocatoria with id filled
    Convocatoria newConvocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<Convocatoria>any())).willThrow(new IllegalArgumentException());

    // when: create Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(newConvocatoria)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_WithExistingId_ReturnsConvocatoria() throws Exception {
    // given: existing Convocatoria
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setTitulo("titulo-modificado");
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(convocatoriaExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        return givenData;
      }
    });

    // when: update Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoria)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Convocatoria is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaExistente.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("unidadGestionRef").value(convocatoriaExistente.getUnidadGestionRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("modeloEjecucion.id")
            .value(convocatoriaExistente.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("codigo").value(convocatoriaExistente.getCodigo()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaPublicacion")
            .value(convocatoriaExistente.getFechaPublicacion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaProvisional")
            .value(convocatoriaExistente.getFechaProvisional().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaConcesion")
            .value(convocatoriaExistente.getFechaConcesion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(convocatoria.getTitulo()))
        .andExpect(MockMvcResultMatchers.jsonPath("objeto").value(convocatoriaExistente.getObjeto()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(convocatoria.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("finalidad.id").value(convocatoriaExistente.getFinalidad().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("regimenConcurrencia.id")
            .value(convocatoriaExistente.getRegimenConcurrencia().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("estado").value(Convocatoria.Estado.REGISTRADA.toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("duracion").value(convocatoriaExistente.getDuracion()))
        .andExpect(MockMvcResultMatchers.jsonPath("ambitoGeografico.id")
            .value(convocatoriaExistente.getAmbitoGeografico().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("clasificacionCVN").value(ClasificacionCVN.AYUDAS.toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(convocatoriaExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: a Convocatoria with non existing id
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.willThrow(new ConvocatoriaNotFoundException(convocatoria.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<Convocatoria>any()))
        .willThrow(new ConvocatoriaNotFoundException(convocatoria.getId()));

    // when: update Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoria.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoria)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void registrar_WithEstadoBorradorAnddExistingId_ReturnsConvocatoria() throws Exception {
    // given: existing Convocatoria with estado Borrador
    Convocatoria convocatoriaBorradorExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoriaBorradorExistente.setEstado(Convocatoria.Estado.BORRADOR);
    Convocatoria convocatoriaRegistrada = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(convocatoriaBorradorExistente);
    BDDMockito.given(service.registrar(ArgumentMatchers.<Long>any())).willReturn(convocatoriaRegistrada);

    // when: registrar Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REGISTRAR,
                convocatoriaBorradorExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaRegistrada)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Convocatoria estado is Registrada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaBorradorExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef")
            .value(convocatoriaBorradorExistente.getUnidadGestionRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("modeloEjecucion.id")
            .value(convocatoriaBorradorExistente.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("codigo").value(convocatoriaBorradorExistente.getCodigo()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaPublicacion")
            .value(convocatoriaBorradorExistente.getFechaPublicacion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaProvisional")
            .value(convocatoriaBorradorExistente.getFechaProvisional().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaConcesion")
            .value(convocatoriaBorradorExistente.getFechaConcesion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(convocatoriaBorradorExistente.getTitulo()))
        .andExpect(MockMvcResultMatchers.jsonPath("objeto").value(convocatoriaBorradorExistente.getObjeto()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value(convocatoriaBorradorExistente.getObservaciones()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("finalidad.id").value(convocatoriaBorradorExistente.getFinalidad().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("regimenConcurrencia.id")
            .value(convocatoriaBorradorExistente.getRegimenConcurrencia().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("estado").value(Convocatoria.Estado.REGISTRADA.toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("duracion").value(convocatoriaBorradorExistente.getDuracion()))
        .andExpect(MockMvcResultMatchers.jsonPath("ambitoGeografico.id")
            .value(convocatoriaBorradorExistente.getAmbitoGeografico().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("clasificacionCVN").value(ClasificacionCVN.AYUDAS.toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(convocatoriaBorradorExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void registrar_WithEstadoBorradorAndNoExistingId_Returns404() throws Exception {
    // given: a Convocatoria with non existing id
    Convocatoria convocatoriaBorradorExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoriaBorradorExistente.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.willThrow(new ConvocatoriaNotFoundException(convocatoriaBorradorExistente.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.registrar(ArgumentMatchers.anyLong()))
        .willThrow(new ConvocatoriaNotFoundException(convocatoriaBorradorExistente.getId()));

    // when: update Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REGISTRAR,
                convocatoriaBorradorExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaBorradorExistente)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-R" })
  void enable_WithExistingId_Return204() throws Exception {
    // given: existing id
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.FALSE);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Convocatoria convocatoriaDisabled = new Convocatoria();
      BeanUtils.copyProperties(convocatoria, convocatoriaDisabled);
      convocatoriaDisabled.setActivo(Boolean.TRUE);
      return convocatoriaDisabled;
    });

    // when: enable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, convocatoria.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: convocatoria is enabled
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoria.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-R" })
  void enable_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any());

    // when: enable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-B" })
  void disable_WithExistingId_Return204() throws Exception {
    // given: existing id
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Convocatoria convocatoriaDisabled = new Convocatoria();
      BeanUtils.copyProperties(convocatoria, convocatoriaDisabled);
      convocatoriaDisabled.setActivo(Boolean.FALSE);
      return convocatoriaDisabled;
    });

    // when: desactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, convocatoria.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: convocatoria is disabled
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoria.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.FALSE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-B" })
  void disable_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

    // when: disable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void modificable_ConvocatoriaRegistradaWithSolicitudesOrProyectosIsTrue_Returns204() throws Exception {
    // given: Existing id convocatoria registrada with Solicitudes or Proyectos
    Long id = 1L;
    BDDMockito.given(service.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    // when: check modificable by convocatoriaId
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_MODIFICABLE, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204 No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void modificable_ConvocatoriaRegistradaOrWithSolicitudesOrProyectosIsFalse_Returns200() throws Exception {
    // given: Existing id in any Estado without Solicitudes or Proyectos
    Long id = 1L;
    BDDMockito.given(service.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);

    // when: check modificable by convocatoriaId
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_MODIFICABLE, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 200 OK
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void registrable_ConvocatoriaRegistrable_Returns200() throws Exception {
    // given: Existing id convocatoria registrable
    Long id = 1L;
    BDDMockito.given(service.registrable(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    // when: check registrable by convocatoriaId
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REGISTRABLE, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 200 Ok
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void registrable_ConvocatoriaNoRegistrable_Returns204() throws Exception {
    // given: Existing id convocatoria NO registrable
    Long id = 1L;
    BDDMockito.given(service.registrable(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    // when: check registrable by convocatoriaId
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REGISTRABLE, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204 No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void existsById_WithExistingId_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: exists by id
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 200 OK
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void existsById_WithNoExistingId_Returns204() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: exists by id
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204 No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findById_WithExistingId_ReturnsConvocatoria() throws Exception {
    // given: existing id
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(convocatoriaExistente);

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Convocatoria is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllTodosRestringidos_WithPaging_ReturnsConvocatoriaSubList() throws Exception {
    // given: One hundred Convocatoria
    List<Convocatoria> convocatorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      convocatorias.add(
          generarMockConvocatoria(Long.valueOf(i), 1L, 1L, 1L, 1L, 1L, (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito.given(service.findAllTodosRestringidos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Convocatoria>>() {
          @Override
          public Page<Convocatoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Convocatoria> content = convocatorias.subList(fromIndex, toIndex);
            Page<Convocatoria> page = new PageImpl<>(content, pageable, convocatorias.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_TODOS + PATH_PARAMETER_RESTRINGIDOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Convocatoria are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Convocatoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Convocatoria>>() {
        });

    // containing Codigo='codigo-31' to 'codigo-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Convocatoria item = actual.get(i);
      Assertions.assertThat(item.getCodigo()).isEqualTo("codigo-" + String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllRestringidos_WithPaging_ReturnsConvocatoriaSubList() throws Exception {
    // given: One hundred Convocatoria
    List<Convocatoria> convocatorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      convocatorias.add(
          generarMockConvocatoria(Long.valueOf(i), 1L, 1L, 1L, 1L, 1L, (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito.given(service.findAllRestringidos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Convocatoria>>() {
          @Override
          public Page<Convocatoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Convocatoria> content = convocatorias.subList(fromIndex, toIndex);
            Page<Convocatoria> page = new PageImpl<>(content, pageable, convocatorias.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_RESTRINGIDOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Convocatoria are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Convocatoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Convocatoria>>() {
        });

    // containing Codigo='codigo-31' to 'codigo-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Convocatoria item = actual.get(i);
      Assertions.assertThat(item.getCodigo()).isEqualTo("codigo-" + String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllRestringidos_EmptyList_Returns204() throws Exception {
    // given: no data Convocatoria
    BDDMockito.given(service.findAllRestringidos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Convocatoria>>() {
          @Override
          public Page<Convocatoria> answer(InvocationOnMock invocation) throws Throwable {
            Page<Convocatoria> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_RESTRINGIDOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD GESTORA
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaEntidadGestora_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEntidadGestora para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaEntidadGestora> convocatoriasEntidadesGestoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesGestoras.add(generarConvocatoriaEntidadGestora(i, convocatoriaId, "entidad-" + i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadGestoraService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaEntidadGestora>>() {
          @Override
          public Page<ConvocatoriaEntidadGestora> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasEntidadesGestoras.size() ? convocatoriasEntidadesGestoras.size() : toIndex;
            List<ConvocatoriaEntidadGestora> content = convocatoriasEntidadesGestoras.subList(fromIndex, toIndex);
            Page<ConvocatoriaEntidadGestora> page = new PageImpl<>(content, pageable,
                convocatoriasEntidadesGestoras.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_GESTORA, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaEntidadGestora del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaEntidadGestora> convocatoriaEntidadGestoraResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaEntidadGestora>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEntidadGestora convocatoriaEntidadGestora = convocatoriaEntidadGestoraResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaEntidadGestora.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaEntidadGestora_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaEntidadGestora para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaEntidadGestora> convocatoriasEntidadesGestoras = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadGestoraService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaEntidadGestora>>() {
          @Override
          public Page<ConvocatoriaEntidadGestora> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ConvocatoriaEntidadGestora> page = new PageImpl<>(convocatoriasEntidadesGestoras, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_GESTORA, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * CONVOCATORIA FASE
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaFase_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaFase para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaFaseOutput> convocatoriasFases = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasFases.add(modelMapper.map(generarMockConvocatoriaFase(i), ConvocatoriaFaseOutput.class));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaFaseService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaFaseOutput>>() {
          @Override
          public Page<ConvocatoriaFaseOutput> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasFases.size() ? convocatoriasFases.size() : toIndex;
            List<ConvocatoriaFaseOutput> content = convocatoriasFases.subList(fromIndex, toIndex);
            Page<ConvocatoriaFaseOutput> page = new PageImpl<>(content, pageable, convocatoriasFases.size());
            return page;
          }
        });
    BDDMockito.given(this.convocatoriaFaseConverter.convert(ArgumentMatchers.<Page<ConvocatoriaFase>>any()))
        .willReturn(
            new PageImpl<>(convocatoriasFases.subList(30, 37), PageRequest.of(3, 10), convocatoriasFases.size()));

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_FASE, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaFase del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaFaseOutput> convocatoriaFaseResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ConvocatoriaFaseOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaFaseOutput convocatoriaFase = convocatoriaFaseResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaFase.getObservaciones()).isEqualTo("observaciones" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaFase_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaFase para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaFaseOutput> convocatoriasFases = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaFaseService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaFaseOutput>>() {
          @Override
          public Page<ConvocatoriaFaseOutput> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ConvocatoriaFaseOutput> page = new PageImpl<>(convocatoriasFases, pageable, 0);
            return page;
          }
        });

    BDDMockito.given(this.convocatoriaFaseConverter.convert(ArgumentMatchers.<Page<ConvocatoriaFase>>any()))
        .willReturn(
            new PageImpl<>(new LinkedList<>(), PageRequest.of(0, 10), 0));

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_FASE, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto ConvocatoriaFase
   * 
   * @param id     id del ConvocatoriaFase
   * @param nombre nombre del ConvocatoriaFase
   * @return el objeto ConvocatoriaFase
   */
  private ConvocatoriaFase generarMockConvocatoriaFase(Long id) {

    ConvocatoriaFase convocatoriaFase = new ConvocatoriaFase();
    convocatoriaFase.setId(id);
    convocatoriaFase.setConvocatoriaId(id);
    convocatoriaFase.setFechaInicio(Instant.now());
    convocatoriaFase.setFechaFin(Instant.now().plus(Period.ofDays(1)));
    convocatoriaFase.setTipoFase(
        TipoFase.builder().nombre("tipoFase" + id).descripcion("descripcionFase" + id).activo(Boolean.TRUE).build());
    convocatoriaFase.setObservaciones("observaciones" + id);

    return convocatoriaFase;
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD FINANCIADORA
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaEntidadFinanciadora_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEntidadFinanciadora para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaEntidadFinanciadora> convocatoriaEntidadFinanciadoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriaEntidadFinanciadoras.add(generarMockConvocatoriaEntidadFinanciadora(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadFinanciadoraService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriaEntidadFinanciadoras.size() ? convocatoriaEntidadFinanciadoras.size()
              : toIndex;
          List<ConvocatoriaEntidadFinanciadora> content = convocatoriaEntidadFinanciadoras.subList(fromIndex, toIndex);
          Page<ConvocatoriaEntidadFinanciadora> pageResponse = new PageImpl<>(content, pageable,
              convocatoriaEntidadFinanciadoras.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_FINANCIADORA, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaEntidadFinanciadora del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaEntidadFinanciadora> convocatoriaEntidadFinanciadorasResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaEntidadFinanciadora>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = convocatoriaEntidadFinanciadorasResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaEntidadFinanciadora.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaEntidadFinanciadora_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaEntidadFinanciadora para la
    // Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaEntidadFinanciadora> convocatoriaEntidadFinanciadoras = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadFinanciadoraService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<ConvocatoriaEntidadFinanciadora> pageResponse = new PageImpl<>(convocatoriaEntidadFinanciadoras,
              pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_FINANCIADORA, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD DOCUMENTO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaDocumento_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaDocumento para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaDocumento> convocatoriaDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriaDocumentos.add(generarMockConvocatoriaDocumento(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaDocumentoService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaDocumento>>() {
          @Override
          public Page<ConvocatoriaDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriaDocumentos.size() ? convocatoriaDocumentos.size() : toIndex;
            List<ConvocatoriaDocumento> content = convocatoriaDocumentos.subList(fromIndex, toIndex);
            Page<ConvocatoriaDocumento> page = new PageImpl<>(content, pageable, convocatoriaDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_DOCUMENTO, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaDocumento del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaDocumento> convocatoriaDocumentoResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaDocumento>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaDocumento convocatoriaDocumento = convocatoriaDocumentoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaDocumento.getDocumentoRef()).isEqualTo("documentoRef" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaDocumento_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaDocumento para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaDocumento> convocatoriaDocumentos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaDocumentoService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaDocumento>>() {
          @Override
          public Page<ConvocatoriaDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ConvocatoriaDocumento> page = new PageImpl<>(convocatoriaDocumentos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_DOCUMENTO, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD ENLACE
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaEnlace_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEnlace para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaEnlace> convocatoriaEnlaces = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriaEnlaces.add(generarMockConvocatoriaEnlace(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEnlaceService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaEnlace>>() {
          @Override
          public Page<ConvocatoriaEnlace> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriaEnlaces.size() ? convocatoriaEnlaces.size() : toIndex;
            List<ConvocatoriaEnlace> content = convocatoriaEnlaces.subList(fromIndex, toIndex);
            Page<ConvocatoriaEnlace> page = new PageImpl<>(content, pageable, convocatoriaEnlaces.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_ENLACE, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaEnlace del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaEnlace> convocatoriaEnlaceResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaEnlace>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEnlace convocatoriaEnlace = convocatoriaEnlaceResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaEnlace.getDescripcion()).isEqualTo("descripcion-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaEnlace_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaEnlace para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaEnlace> convocatoriaEnlaces = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEnlaceService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaEnlace>>() {
          @Override
          public Page<ConvocatoriaEnlace> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ConvocatoriaEnlace> page = new PageImpl<>(convocatoriaEnlaces, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_ENLACE, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD CONVOCANTE
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaEntidadConvocantes_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEntidadConvocante para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaEntidadConvocante> convocatoriasEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaEntidadConvocante(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadConvocanteService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesConvocantes.size() ? convocatoriasEntidadesConvocantes.size()
              : toIndex;
          List<ConvocatoriaEntidadConvocante> content = convocatoriasEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ConvocatoriaEntidadConvocante> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesConvocantes.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_CONVOCANTE, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaEntidadConvocante del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaEntidadConvocante> convocatoriaEntidadConvocantesResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaEntidadConvocante>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = convocatoriaEntidadConvocantesResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaEntidadConvocante.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaEntidadConvocantes_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaEntidadConvocante para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaEntidadConvocante> convocatoriasEntidadesConvocantes = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadConvocanteService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<ConvocatoriaEntidadConvocante> pageResponse = new PageImpl<>(convocatoriasEntidadesConvocantes, pageable,
              0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_CONVOCANTE, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void create_ReturnsConvocatoria() throws Exception {
    // given: new Convocatoria
    Convocatoria newConvocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        Convocatoria newData = new Convocatoria();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(newConvocatoria.getId());
        return newData;
      }
    });

    // when: create Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(newConvocatoria)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new Convocatoria is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value(newConvocatoria.getUnidadGestionRef()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("modeloEjecucion.id").value(newConvocatoria.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("codigo").value(newConvocatoria.getCodigo()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("fechaPublicacion").value(newConvocatoria.getFechaPublicacion().toString()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("fechaProvisional").value(newConvocatoria.getFechaProvisional().toString()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("fechaConcesion").value(newConvocatoria.getFechaConcesion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(newConvocatoria.getTitulo()))
        .andExpect(MockMvcResultMatchers.jsonPath("objeto").value(newConvocatoria.getObjeto()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(newConvocatoria.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("finalidad.id").value(newConvocatoria.getFinalidad().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("regimenConcurrencia.id")
            .value(newConvocatoria.getRegimenConcurrencia().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("estado").value(Convocatoria.Estado.REGISTRADA.toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("duracion").value(newConvocatoria.getDuracion()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("ambitoGeografico.id").value(newConvocatoria.getAmbitoGeografico().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("clasificacionCVN").value(ClasificacionCVN.AYUDAS.toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(newConvocatoria.getActivo()));
  }

  /**
   * 
   * CONVOCATORIA PERIODO JUSTIFICACION
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaPeriodoJustificacion_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEntidadConvocante para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaPeriodoJustificacion> convocatoriasPeriodoJustificaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasPeriodoJustificaciones.add(generarMockConvocatoriaPeriodoJustificacion(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaPeriodoJustificacionService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasPeriodoJustificaciones.size() ? convocatoriasPeriodoJustificaciones.size()
              : toIndex;
          List<ConvocatoriaPeriodoJustificacion> content = convocatoriasPeriodoJustificaciones.subList(fromIndex,
              toIndex);
          Page<ConvocatoriaPeriodoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasPeriodoJustificaciones.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PERIODO_JUSTIFICACION, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaEntidadConvocante del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaPeriodoJustificacion> convocatoriasPeriodoJustificacionesResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaPeriodoJustificacion>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = convocatoriasPeriodoJustificacionesResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaPeriodoJustificacion.getObservaciones()).isEqualTo("observaciones-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaPeriodoJustificacion_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaPeriodoJustificacion para la
    // Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaPeriodoJustificacion> convocatoriasPeriodoJustificaciones = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaPeriodoJustificacionService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<ConvocatoriaPeriodoJustificacion> pageResponse = new PageImpl<>(convocatoriasPeriodoJustificaciones,
              pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PERIODO_JUSTIFICACION, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * CONVOCATORIA PERIODO SEGUIMIENTO CIENTIFICO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaPeriodoSeguimientoCientifico_ReturnsPage() throws Exception {
    // given: Una lista con 100 ConvocatoriaPeriodoSeguimientoCientifico para la
    // Convocatoria

    Long convocatoriaId = 1L;
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 1, j = 2; i <= 100; i++, j += 2) {
      // @formatter:off
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico
          .builder()
          .id(Long.valueOf(i))
          .convocatoriaId(convocatoriaId)
          .numPeriodo(i - 1)
          .mesInicial((i * 2) - 1)
          .mesFinal(j * 1)
          .observaciones("observaciones-" + i)
          .build());
      // @formatter:on
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaPeriodoSeguimientoCientificoService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > listaConvocatoriaPeriodoSeguimientoCientifico.size()
              ? listaConvocatoriaPeriodoSeguimientoCientifico.size()
              : toIndex;
          List<ConvocatoriaPeriodoSeguimientoCientifico> content = listaConvocatoriaPeriodoSeguimientoCientifico
              .subList(fromIndex, toIndex);
          Page<ConvocatoriaPeriodoSeguimientoCientifico> pageResponse = new PageImpl<>(content, pageable,
              listaConvocatoriaPeriodoSeguimientoCientifico.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PERIODO_SEGUIMIENTO_CIENTIFICO, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaEntidadConvocante del 31 al 40
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificoResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ConvocatoriaPeriodoSeguimientoCientifico>>() {
        });

    for (int i = 31; i <= 10; i++) {
      ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = convocatoriaPeriodoSeguimientoCientificoResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaPeriodoSeguimientoCientifico.getObservaciones())
          .isEqualTo("observaciones-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllConvocatoriaPeriodoSeguimientoCientifico_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaPeriodoSeguimientoCientifico para la
    // Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriasPeriodoSeguimientoCientifico = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaPeriodoSeguimientoCientificoService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<ConvocatoriaPeriodoSeguimientoCientifico> pageResponse = new PageImpl<>(
              convocatoriasPeriodoSeguimientoCientifico, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PERIODO_SEGUIMIENTO_CIENTIFICO, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * MOCKS
   * 
   */

  /**
   * Función que genera Convocatoria
   * 
   * @param convocatoriaId
   * @param unidadGestionId
   * @param modeloEjecucionId
   * @param modeloTipoFinalidadId
   * @param tipoRegimenConcurrenciaId
   * @param tipoAmbitoGeogragicoId
   * @param activo
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, Long unidadGestionId, Long modeloEjecucionId,
      Long modeloTipoFinalidadId, Long tipoRegimenConcurrenciaId, Long tipoAmbitoGeogragicoId, Boolean activo) {

    // @formatter:off
    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()
            .id(modeloEjecucionId)
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))
            .activo(Boolean.TRUE)
            .build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId))
            .activo(Boolean.TRUE)
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .modeloEjecucion(modeloEjecucion)
            .tipoFinalidad(tipoFinalidad)
            .activo(Boolean.TRUE)
            .build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()
            .id(tipoRegimenConcurrenciaId)
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))
            .activo(Boolean.TRUE)
            .build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()
            .id(tipoAmbitoGeogragicoId)
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId))
            .activo(Boolean.TRUE)
            .build();

    Convocatoria convocatoria = Convocatoria.builder()
        .id(convocatoriaId)
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))
        .modeloEjecucion(modeloEjecucion)
        .codigo("codigo-" + String.format("%03d", convocatoriaId))
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaProvisional(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaConcesion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo-" + String.format("%03d", convocatoriaId))
        .objeto("objeto-" + String.format("%03d", convocatoriaId))
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())
        .regimenConcurrencia(tipoRegimenConcurrencia)
        .estado(Convocatoria.Estado.REGISTRADA)
        .duracion(12)
        .ambitoGeografico(tipoAmbitoGeografico)
        .clasificacionCVN(ClasificacionCVN.AYUDAS)
        .activo(activo)
        .build();
    // @formatter:on

    return convocatoria;
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadGestora
   * 
   * @param convocatoriaEntidadGestoraId
   * @param convocatoriaId
   * @param entidadRef
   * @return el objeto ConvocatoriaEntidadGestora
   */
  private ConvocatoriaEntidadGestora generarConvocatoriaEntidadGestora(Long convocatoriaEntidadGestoraId,
      Long convocatoriaId, String entidadRef) {

    return ConvocatoriaEntidadGestora.builder().id(convocatoriaEntidadGestoraId).convocatoriaId(convocatoriaId)
        .entidadRef(entidadRef).build();

  }

  /**
   * 
   * CONVOCATORIA AREA TEMATICA
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllConvocatoriaAreaTematica_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaAreaTematica para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaAreaTematica> convocatoriasEntidadesGestoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesGestoras.add(generarConvocatoriaAreaTematica(i, convocatoriaId, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaAreaTematicaService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaAreaTematica>>() {
          @Override
          public Page<ConvocatoriaAreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasEntidadesGestoras.size() ? convocatoriasEntidadesGestoras.size() : toIndex;
            List<ConvocatoriaAreaTematica> content = convocatoriasEntidadesGestoras.subList(fromIndex, toIndex);
            Page<ConvocatoriaAreaTematica> page = new PageImpl<>(content, pageable,
                convocatoriasEntidadesGestoras.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_AREA_TEMATICA, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaAreaTematica del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaAreaTematica> convocatoriaAreaTematicaResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaAreaTematica>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaAreaTematica convocatoriaAreaTematica = convocatoriaAreaTematicaResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaAreaTematica.getObservaciones()).isEqualTo("observaciones-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllConvocatoriaAreaTematica_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaAreaTematica para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaAreaTematica> convocatoriasEntidadesGestoras = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaAreaTematicaService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaAreaTematica>>() {
          @Override
          public Page<ConvocatoriaAreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ConvocatoriaAreaTematica> page = new PageImpl<>(convocatoriasEntidadesGestoras, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_AREA_TEMATICA, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * CONVOCATORIA HITO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllConvocatoriaHito_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaHito para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaHito> convocatoriaHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriaHitos.add(generarMockConvocatoriaHito(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaHitoService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaHito>>() {
          @Override
          public Page<ConvocatoriaHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriaHitos.size() ? convocatoriaHitos.size() : toIndex;
            List<ConvocatoriaHito> content = convocatoriaHitos.subList(fromIndex, toIndex);
            Page<ConvocatoriaHito> page = new PageImpl<>(content, pageable, convocatoriaHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_HITO, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaHito del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaHito> convocatoriaHitoResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ConvocatoriaHito>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaHito convocatoriaHito = convocatoriaHitoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaHito.getComentario()).isEqualTo("comentario-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllConvocatoriaHito_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaHito para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaHito> convocatoriaHitos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaHitoService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaHito>>() {
          @Override
          public Page<ConvocatoriaHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ConvocatoriaHito> page = new PageImpl<>(convocatoriaHitos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_HITO, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * CONVOCATORIA CONCEPTO GASTO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllConvocatoriaConceptoGastoPermitido_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaConceptoGasto para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaConceptoGasto> convocatoriaConceptoGastos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriaConceptoGastos.add(generarMockConvocatoriaConceptoGasto(i, true));
    }

    BDDMockito.given(convocatoriaConceptoGastoService
        .findAllByConvocatoriaAndPermitidoTrue(ArgumentMatchers.<Long>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaConceptoGasto>>() {
          @Override
          public Page<ConvocatoriaConceptoGasto> answer(InvocationOnMock invocation) throws Throwable {
            List<ConvocatoriaConceptoGasto> content = new ArrayList<>();
            for (ConvocatoriaConceptoGasto convocatoriaConceptoGasto : convocatoriaConceptoGastos) {
              content.add(convocatoriaConceptoGasto);
            }
            return new PageImpl<>(content);
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONCEPTO_GASTO_PERMITIDO, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(37))).andReturn();

    List<ConvocatoriaConceptoGasto> convocatoriaConceptoGastoResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaConceptoGasto>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaConceptoGasto convocatoriaConceptoGasto = convocatoriaConceptoGastoResponse.get(i - 1);
      Assertions.assertThat(convocatoriaConceptoGasto.getObservaciones()).isEqualTo("Obs-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllConvocatoriaConceptoGastoPermitido_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaConceptoGasto para la Convocatoria

    BDDMockito.given(convocatoriaConceptoGastoService
        .findAllByConvocatoriaAndPermitidoTrue(ArgumentMatchers.<Long>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONCEPTO_GASTO_PERMITIDO, 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllConvocatoriaConceptoGastoNoPermitido_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaConceptoGasto para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaConceptoGasto> convocatoriaConceptoGastos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriaConceptoGastos.add(generarMockConvocatoriaConceptoGasto(i, true));
    }

    BDDMockito.given(convocatoriaConceptoGastoService
        .findAllByConvocatoriaAndPermitidoFalse(ArgumentMatchers.<Long>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaConceptoGasto>>() {
          @Override
          public Page<ConvocatoriaConceptoGasto> answer(InvocationOnMock invocation) throws Throwable {
            List<ConvocatoriaConceptoGasto> content = new ArrayList<>();
            for (ConvocatoriaConceptoGasto convocatoriaConceptoGasto : convocatoriaConceptoGastos) {
              content.add(convocatoriaConceptoGasto);
            }
            return new PageImpl<>(content);
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONCEPTO_GASTO_NO_PERMITIDO, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(37))).andReturn();

    List<ConvocatoriaConceptoGasto> convocatoriaConceptoGastoResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaConceptoGasto>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaConceptoGasto convocatoriaConceptoGasto = convocatoriaConceptoGastoResponse.get(i - 1);
      Assertions.assertThat(convocatoriaConceptoGasto.getObservaciones()).isEqualTo("Obs-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllConvocatoriaConceptoGastoNoPermitido_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaConceptoGasto para la Convocatoria

    BDDMockito.given(convocatoriaConceptoGastoService
        .findAllByConvocatoriaAndPermitidoFalse(ArgumentMatchers.<Long>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_CONCEPTO_GASTO_NO_PERMITIDO, 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto ConvocatoriaAreaTematica
   * 
   * @param convocatoriaAreaTematicaId
   * @param convocatoriaId
   * @param areaTematicaId
   * @return el objeto ConvocatoriaAreaTematica
   */
  private ConvocatoriaAreaTematica generarConvocatoriaAreaTematica(Long convocatoriaAreaTematicaId, Long convocatoriaId,
      Long areaTematicaId) {

    return ConvocatoriaAreaTematica.builder().id(convocatoriaAreaTematicaId).convocatoriaId(convocatoriaId)
        .areaTematica(AreaTematica.builder().id(areaTematicaId).build())
        .observaciones("observaciones-" + convocatoriaAreaTematicaId).build();
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadConvocante
   * 
   * @param id id del ConvocatoriaEntidadConvocante
   * @return el objeto ConvocatoriaEntidadConvocante
   */
  private ConvocatoriaEntidadConvocante generarMockConvocatoriaEntidadConvocante(Long id) {
    Programa programa = new Programa();
    programa.setId(id);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = new ConvocatoriaEntidadConvocante();
    convocatoriaEntidadConvocante.setId(id);
    convocatoriaEntidadConvocante.setConvocatoriaId((id == null ? 0 : id));
    convocatoriaEntidadConvocante.setEntidadRef("entidad-" + (id == null ? 0 : id));
    convocatoriaEntidadConvocante.setPrograma(programa);

    return convocatoriaEntidadConvocante;
  }

  /**
   * Función que devuelve un objeto ConvocatoriaHito
   * 
   * @param id id del ConvocatoriaHito
   * @return el objeto ConvocatoriaHito
   */
  private ConvocatoriaHito generarMockConvocatoriaHito(Long id) {
    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id == null ? 1 : id);
    tipoHito.setActivo(true);

    ConvocatoriaHito convocatoriaHito = new ConvocatoriaHito();
    convocatoriaHito.setId(id);
    convocatoriaHito.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaHito.setFecha(Instant.parse("2020-10-19T00:00:00Z"));
    convocatoriaHito.setComentario("comentario-" + id);
    convocatoriaHito.setConvocatoriaHitoAviso(new ConvocatoriaHitoAviso(
        id == null ? 1 : id, id == null ? "1" : id.toString(), id == null ? "1" : id.toString(), false, false));
    convocatoriaHito.setTipoHito(tipoHito);

    return convocatoriaHito;
  }

  /**
   * Función que devuelve un objeto ConvocatoriaDocumento
   * 
   * @param id id del ConvocatoriaDocumento
   * @return el objeto ConvocatoriaDocumento
   */
  private ConvocatoriaDocumento generarMockConvocatoriaDocumento(Long id) {

    TipoFase tipoFase = TipoFase.builder().id(id).build();
    TipoDocumento tipoDocumento = TipoDocumento.builder().id(id).build();

    // @formatter:off
    return ConvocatoriaDocumento.builder()
        .id(id)
        .convocatoriaId(1L)
        .tipoFase(tipoFase)
        .tipoDocumento(tipoDocumento)
        .nombre("nombre doc-" + id)
        .publico(Boolean.TRUE)
        .observaciones("observaciones-" + id)
        .documentoRef("documentoRef" + id)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEnlace
   * 
   * @param id id del ConvocatoriaEnlace
   * @return el objeto ConvocatoriaEnlace
   */
  private ConvocatoriaEnlace generarMockConvocatoriaEnlace(Long id) {

    ConvocatoriaEnlace convocatoriaEnlace = new ConvocatoriaEnlace();
    convocatoriaEnlace.setId(id);
    convocatoriaEnlace.setConvocatoriaId(id);
    convocatoriaEnlace.setDescripcion("descripcion-" + id);
    convocatoriaEnlace.setUrl("www.url" + id + ".es");
    convocatoriaEnlace.setTipoEnlace(TipoEnlace.builder().id(id).nombre("tipoEnlace" + id)
        .descripcion("descripcionEnlace" + id).activo(Boolean.TRUE).build());

    return convocatoriaEnlace;
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadFinanciadora
   * 
   * @param id id del ConvocatoriaEntidadFinanciadora
   * @return el objeto ConvocatoriaEntidadFinanciadora
   */
  private ConvocatoriaEntidadFinanciadora generarMockConvocatoriaEntidadFinanciadora(Long id) {
    FuenteFinanciacion fuenteFinanciacion = new FuenteFinanciacion();
    fuenteFinanciacion.setId(id == null ? 1 : id);
    fuenteFinanciacion.setActivo(true);

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id == null ? 1 : id);
    tipoFinanciacion.setActivo(true);

    Programa programa = new Programa();
    programa.setId(id);

    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = new ConvocatoriaEntidadFinanciadora();
    convocatoriaEntidadFinanciadora.setId(id);
    convocatoriaEntidadFinanciadora.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaEntidadFinanciadora.setEntidadRef("entidad-" + (id == null ? 0 : id));
    convocatoriaEntidadFinanciadora.setFuenteFinanciacion(fuenteFinanciacion);
    convocatoriaEntidadFinanciadora.setTipoFinanciacion(tipoFinanciacion);
    convocatoriaEntidadFinanciadora.setPorcentajeFinanciacion(BigDecimal.valueOf(50));

    return convocatoriaEntidadFinanciadora;
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoJustificacion
   * 
   * @param id id del ConvocatoriaPeriodoJustificacion
   * @return el objeto ConvocatoriaPeriodoJustificacion
   */
  private ConvocatoriaPeriodoJustificacion generarMockConvocatoriaPeriodoJustificacion(Long id) {
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = new ConvocatoriaPeriodoJustificacion();
    convocatoriaPeriodoJustificacion.setId(id);
    convocatoriaPeriodoJustificacion.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaPeriodoJustificacion.setNumPeriodo(1);
    convocatoriaPeriodoJustificacion.setMesInicial(1);
    convocatoriaPeriodoJustificacion.setMesFinal(2);
    convocatoriaPeriodoJustificacion.setFechaInicioPresentacion(Instant.parse("2020-10-10T00:00:00Z"));
    convocatoriaPeriodoJustificacion.setFechaFinPresentacion(Instant.parse("2020-11-20T23:59:59Z"));
    convocatoriaPeriodoJustificacion.setObservaciones("observaciones-" + id);
    convocatoriaPeriodoJustificacion.setTipo(TipoJustificacion.PERIODICO);

    return convocatoriaPeriodoJustificacion;
  }

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGasto
   * 
   * @param id id del ConvocatoriaConceptoGasto
   * @return el objeto ConvocatoriaConceptoGasto
   */
  private ConvocatoriaConceptoGasto generarMockConvocatoriaConceptoGasto(Long id, Boolean permitido) {
    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id == null ? 1 : id);
    conceptoGasto.setActivo(true);

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = new ConvocatoriaConceptoGasto();
    convocatoriaConceptoGasto.setId(id);
    convocatoriaConceptoGasto.setConceptoGasto(conceptoGasto);
    convocatoriaConceptoGasto.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaConceptoGasto.setImporteMaximo(20.0);
    convocatoriaConceptoGasto.setMesInicial(1);
    convocatoriaConceptoGasto.setMesFinal(4);
    convocatoriaConceptoGasto.setObservaciones("Obs-" + id);
    convocatoriaConceptoGasto.setPermitido(permitido);

    return convocatoriaConceptoGasto;
  }

}