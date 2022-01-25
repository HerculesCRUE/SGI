package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFinalidadRepository;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.repository.TipoRegimenConcurrenciaRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

public class ConvocatoriaServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaRepository repository;
  @Mock
  private ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository;
  @Mock
  private ModeloUnidadRepository modeloUnidadRepository;
  @Mock
  private ModeloTipoFinalidadRepository modeloTipoFinalidadRepository;
  @Mock
  private TipoRegimenConcurrenciaRepository tipoRegimenConcurrenciaRepository;
  @Mock
  private TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository;
  @Mock
  private ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository;
  @Mock
  private ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  @Mock
  private SolicitudRepository solicitudReposiotry;
  @Mock
  private ProyectoRepository proyectoReposiotry;
  @Mock
  private ConvocatoriaClonerService convocatoriaClonerService;
  @Mock
  private AutorizacionRepository autorizacionRepository;

  private ConvocatoriaService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaServiceImpl(repository, convocatoriaPeriodoJustificacionRepository,
        modeloUnidadRepository, modeloTipoFinalidadRepository, tipoRegimenConcurrenciaRepository,
        tipoAmbitoGeograficoRepository, convocatoriaPeriodoSeguimientoCientificoRepository,
        configuracionSolicitudRepository, this.solicitudReposiotry, this.proyectoReposiotry, convocatoriaClonerService, autorizacionRepository);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_ReturnsConvocatoria() {
    // given: new Convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        Convocatoria newData = new Convocatoria();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(1L);
        return newData;
      }
    });

    // when: create Convocatoria
    Convocatoria created = service.create(convocatoria);

    // then: new Convocatoria is created
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getUnidadGestionRef()).isEqualTo(convocatoria.getUnidadGestionRef());
    Assertions.assertThat(created.getModeloEjecucion().getId()).isEqualTo(convocatoria.getModeloEjecucion().getId());
    Assertions.assertThat(created.getCodigo()).isEqualTo(convocatoria.getCodigo());
    Assertions.assertThat(created.getFechaPublicacion()).isEqualTo(convocatoria.getFechaPublicacion());
    Assertions.assertThat(created.getFechaProvisional()).isEqualTo(convocatoria.getFechaProvisional());
    Assertions.assertThat(created.getFechaConcesion()).isEqualTo(convocatoria.getFechaConcesion());
    Assertions.assertThat(created.getTitulo()).isEqualTo(convocatoria.getTitulo());
    Assertions.assertThat(created.getObjeto()).isEqualTo(convocatoria.getObjeto());
    Assertions.assertThat(created.getObservaciones()).isEqualTo(convocatoria.getObservaciones());
    Assertions.assertThat(created.getFinalidad().getId()).isEqualTo(convocatoria.getFinalidad().getId());
    Assertions.assertThat(created.getRegimenConcurrencia().getId())
        .isEqualTo(convocatoria.getRegimenConcurrencia().getId());
    Assertions.assertThat(created.getEstado()).isEqualTo(Convocatoria.Estado.BORRADOR);
    Assertions.assertThat(created.getDuracion()).isEqualTo(convocatoria.getDuracion());
    Assertions.assertThat(created.getAmbitoGeografico().getId()).isEqualTo(convocatoria.getAmbitoGeografico().getId());
    Assertions.assertThat(created.getClasificacionCVN()).isEqualTo(convocatoria.getClasificacionCVN());
    Assertions.assertThat(created.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithMinimumRequiredData_ReturnsConvocatoria() {
    // given: new Convocatoria with minimum required data
    // @formatter:off
    Convocatoria convocatoria = Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .build();
    // @formatter:on
    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        Convocatoria newData = new Convocatoria();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(1L);
        return newData;
      }
    });

    // when: create Convocatoria
    Convocatoria created = service.create(convocatoria);

    // then: new Convocatoria is created with minimum required data
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getUnidadGestionRef()).isEqualTo(convocatoria.getUnidadGestionRef());
    Assertions.assertThat(created.getModeloEjecucion()).isNull();
    Assertions.assertThat(created.getCodigo()).isEqualTo(convocatoria.getCodigo());
    Assertions.assertThat(created.getFechaPublicacion()).isEqualTo(convocatoria.getFechaPublicacion());
    Assertions.assertThat(created.getFechaProvisional()).isEqualTo(convocatoria.getFechaProvisional());
    Assertions.assertThat(created.getFechaConcesion()).isEqualTo(convocatoria.getFechaConcesion());
    Assertions.assertThat(created.getTitulo()).isEqualTo(convocatoria.getTitulo());
    Assertions.assertThat(created.getObjeto()).isNull();
    Assertions.assertThat(created.getObservaciones()).isNull();
    Assertions.assertThat(created.getFinalidad()).isNull();
    Assertions.assertThat(created.getRegimenConcurrencia()).isNull();
    Assertions.assertThat(created.getEstado()).isEqualTo(Convocatoria.Estado.BORRADOR);
    Assertions.assertThat(created.getDuracion()).isNull();
    Assertions.assertThat(created.getAmbitoGeografico()).isNull();
    Assertions.assertThat(created.getClasificacionCVN()).isNull();
    Assertions.assertThat(created.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with id filled
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Id tiene que ser null para crear la Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_BorradorWithoutUnidadRef_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Borrador without UnidadRef
    Convocatoria convocatoria = generarMockConvocatoria(null, null, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as UnidadRef is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UnidadGestionRef no puede ser null en la Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C_1" })
  public void create_UnidadGestionInvalid_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Borrador with ModeloEjecucion and without UnidadGestion
    Convocatoria convocatoria = generarMockConvocatoria(null, null, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    convocatoria.setUnidadGestionRef("2");

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "El usuario no tiene permisos para crear una convocatoria asociada a la unidad de gestión recibida.");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithModeloEjecucionNotAsignedToUnidad_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with ModeloEjecucion not asigned to given UnidadGestion
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as ModeloEjecucion is not asigned to given
        // UnidadGestion
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion '%s' no disponible para la UnidadGestion %s",
            convocatoria.getModeloEjecucion().getNombre(), convocatoria.getUnidadGestionRef());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithModeloUnidadDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with ModeloUnidad disabled
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as ModeloUnidad is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion '%s' no está activo para la UnidadGestion %s",
            convocatoria.getModeloEjecucion().getNombre(), convocatoria.getUnidadGestionRef());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithModeloEjecucionDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with ModeloEjecucion disabled
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.getModeloEjecucion().setActivo(Boolean.FALSE);

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as ModeloEjecucion is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion '%s' no está activo", convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_BorradorWithoutTitulo_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Borrador without Titulo
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setTitulo(null);

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as Titulo is not present
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Titulo no puede ser null en la Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_BorradorWithTipoFinalidadAndWithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Borrador with TipoFinalidad without ModeloEjecucion
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, null, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as ModeloEjecucion is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion requerido para obtener TipoFinalidad");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithTipoFinalidadNotAsignedToModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with TipoFinalidad not asigned to given ModeloEjecucion
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito.given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as TipoFinalidad is not asigned to given
        // ModeloEjecucion
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFinalidad '%s' no disponible para el ModeloEjecucion %s",
            convocatoria.getFinalidad().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithModeloTipoFinalidadDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with ModeloTipoFinalidad disabled
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    ModeloTipoFinalidad modeloTipoFinalidad = generarMockModeloTipoFinalidad(convocatoria, Boolean.FALSE);

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito.given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFinalidad));

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as ModeloTipoFinalidad is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFinalidad '%s' no está activo para el ModeloEjecucion %s",
            convocatoria.getFinalidad().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithTipoFinalidadDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with TipoFinalidad disabled
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.getFinalidad().setActivo(Boolean.FALSE);

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as TipoFinalidad is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFinalidad '%s' no está activo", convocatoria.getFinalidad().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithNoExistingTipoRegimenConcurrencia_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with no existing TipoRegimenConcurrencia
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as TipoRegimenConcurrencia does not exist
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("RegimenConcurrencia '%s' no disponible", convocatoria.getRegimenConcurrencia().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithTipoRegimenConcurrenciaDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with a TipoRegimenConcurrencia disabled
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.getRegimenConcurrencia().setActivo(Boolean.FALSE);

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as TipoRegimenConcurrencia is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("RegimenConcurrencia '%s' no está activo", convocatoria.getRegimenConcurrencia().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithNoExistingTipoAmbitoGeografico_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with no existing TipoAmbitoGeografico
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as TipoAmbitoGeografico does not exist
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("AmbitoGeografico '%s' no disponible", convocatoria.getAmbitoGeografico().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithTipoAmbitoGeograficoDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with a TipoAmbitoGeografico disabled
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.getAmbitoGeografico().setActivo(Boolean.FALSE);

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> service.create(convocatoria))
        // then: throw exception as TipoAmbitoGeografico is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("AmbitoGeografico '%s' no está activo", convocatoria.getAmbitoGeografico().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithExistingId_ReturnsConvocatoria() {
    // given: existing Convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        givenData.setCodigo("codigo-modificado");
        return givenData;
      }
    });

    // when: update Convocatoria
    Convocatoria updated = service.update(convocatoria);

    // then: Convocatoria is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(convocatoria.getId());
    Assertions.assertThat(updated.getUnidadGestionRef()).isEqualTo(convocatoria.getUnidadGestionRef());
    Assertions.assertThat(updated.getModeloEjecucion().getId()).isEqualTo(convocatoria.getModeloEjecucion().getId());
    Assertions.assertThat(updated.getCodigo()).isEqualTo("codigo-modificado");
    Assertions.assertThat(updated.getFechaPublicacion()).isEqualTo(convocatoria.getFechaPublicacion());
    Assertions.assertThat(updated.getFechaProvisional()).isEqualTo(convocatoria.getFechaProvisional());
    Assertions.assertThat(updated.getFechaConcesion()).isEqualTo(convocatoria.getFechaConcesion());
    Assertions.assertThat(updated.getTitulo()).isEqualTo(convocatoria.getTitulo());
    Assertions.assertThat(updated.getObjeto()).isEqualTo(convocatoria.getObjeto());
    Assertions.assertThat(updated.getObservaciones()).isEqualTo(convocatoria.getObservaciones());
    Assertions.assertThat(updated.getFinalidad().getId()).isEqualTo(convocatoria.getFinalidad().getId());
    Assertions.assertThat(updated.getRegimenConcurrencia().getId())
        .isEqualTo(convocatoria.getRegimenConcurrencia().getId());
    Assertions.assertThat(updated.getEstado()).isEqualTo(convocatoria.getEstado());
    Assertions.assertThat(updated.getDuracion()).isEqualTo(convocatoria.getDuracion());
    Assertions.assertThat(updated.getAmbitoGeografico().getId()).isEqualTo(convocatoria.getAmbitoGeografico().getId());
    Assertions.assertThat(updated.getClasificacionCVN()).isEqualTo(convocatoria.getClasificacionCVN());
    Assertions.assertThat(updated.getActivo()).isEqualTo(convocatoria.getActivo());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void updateBorrador_WithMinimumRequiredData_ReturnsConvocatoria() {
    // given: existing Convocatoria is updated to estado Borrador with minimum
    // required data
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    // @formatter:off
    Convocatoria convocatoriaBorrador = Convocatoria.builder()
        .id(convocatoria.getId())
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo")
        .unidadGestionRef("2")
        .formularioSolicitud(FormularioSolicitud.PROYECTO)
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .build();
    // @formatter:on

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.save(ArgumentMatchers.any())).willReturn(convocatoriaBorrador);

    // when: update Convocatoria
    Convocatoria updated = service.update(convocatoriaBorrador);

    // then: Convocatoria is updated to Borrador with minimum required data
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(convocatoria.getId());
    Assertions.assertThat(updated.getUnidadGestionRef()).isEqualTo(convocatoriaBorrador.getUnidadGestionRef());
    Assertions.assertThat(updated.getModeloEjecucion()).isNull();
    Assertions.assertThat(updated.getCodigo()).isEqualTo(convocatoriaBorrador.getCodigo());
    Assertions.assertThat(updated.getFechaPublicacion()).isEqualTo(convocatoriaBorrador.getFechaPublicacion());
    Assertions.assertThat(updated.getTitulo()).isEqualTo(convocatoriaBorrador.getTitulo());
    Assertions.assertThat(updated.getObjeto()).isNull();
    Assertions.assertThat(updated.getObservaciones()).isNull();
    Assertions.assertThat(updated.getFinalidad()).isNull();
    Assertions.assertThat(updated.getRegimenConcurrencia()).isNull();
    Assertions.assertThat(updated.getEstado()).isEqualTo(Convocatoria.Estado.BORRADOR);
    Assertions.assertThat(updated.getDuracion()).isNull();
    Assertions.assertThat(updated.getAmbitoGeografico()).isNull();
    Assertions.assertThat(updated.getClasificacionCVN()).isNull();
    Assertions.assertThat(updated.getActivo()).isEqualTo(convocatoria.getActivo());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing Convocatoria
        () -> service.update(convocatoria))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a Convocatoria without id filled
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Id no puede ser null para actualizar Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_RegistradaWithoutUnidadRef_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Registrada without UnidadRef
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, null, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as UnidadRef is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UnidadGestionRef no puede ser null en la Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_BorradorWithoutUnidadRef_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Borrador without UnidadRef
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoriaExistente.setEstado(Convocatoria.Estado.BORRADOR);
    Convocatoria convocatoria = generarMockConvocatoria(1L, null, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as UnidadRef is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UnidadGestionRef no puede ser null en la Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_UnidadGestionWithVinculaciones_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with updated UnidadGestion and vinculaciones
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setUnidadGestionRef("1");
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    List<Convocatoria> convocatorias = new ArrayList<>();
    convocatorias.add(convocatoriaExistente);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(repository.tieneVinculaciones(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: Update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede modificar la unidad de gestión al existir registros dependientes en las pantallas Enlaces, Plazos y fases, Hitos o Documentos");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_ModeloEjecucionWithVinculaciones_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with updated ModeloEjecucion and vinculaciones
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.getModeloEjecucion().setId(2L);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    List<Convocatoria> convocatorias = new ArrayList<>();
    convocatorias.add(convocatoriaExistente);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(repository.tieneVinculaciones(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: Update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede modificar el modelo de ejecución al existir registros dependientes en las pantallas Enlaces, Plazos y fases, Hitos o Documentos");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_RegistradaWithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Registrada without ModeloEjecucion
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, null, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as ModeloEjecucion is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion no puede ser null en la Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithModeloEjecucionNotAsignedToUnidad_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with ModeloEjecucion not asigned to given UnidadGestion
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as ModeloEjecucion is not asigned to given
        // UnidadGestion
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion '%s' no disponible para la UnidadGestion %s",
            convocatoriaExistente.getModeloEjecucion().getNombre(), convocatoria.getUnidadGestionRef());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithSameModeloUnidadDisabled_ReturnsConvocatoria() {
    // given: a Convocatoria with the same ModeloUnidad disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.FALSE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willReturn(convocatoria);

    Assertions.assertThatCode(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: No exception as disabled ModeloUnidad is the same
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithModeloUnidadDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with updated ModeloEjecucion ModeloUnidad disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 2L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as ModeloUnidad with updated ModeloEjecucion is
        // disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion '%s' no está activo para la UnidadGestion %s",
            convocatoria.getModeloEjecucion().getNombre(), convocatoria.getUnidadGestionRef());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithSameModeloEjecucionDisabled_ReturnsConvocatoria() {
    // given: a Convocatoria with the same ModeloEjecucion disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.getModeloEjecucion().setActivo(Boolean.FALSE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willReturn(convocatoria);

    Assertions.assertThatCode(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: No exception as disabled ModeloEjecucion is the same
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithModeloEjecucionDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with updated ModeloEjecucion disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 2L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.getModeloEjecucion().setActivo(Boolean.FALSE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as updated ModeloEjecucion is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion '%s' no está activo", convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_RegistradaWithoutTitulo_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Registrada without Titulo
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setTitulo(null);
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));
    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as Titulo is not present
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Titulo no puede ser null en la Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_BorradorWithoutTitulo_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Borrador without Titulo
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoriaExistente.setEstado(Convocatoria.Estado.BORRADOR);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setTitulo(null);
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as Titulo is not present
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Titulo no puede ser null en la Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_RegistradaWithoutModeloTipoFinalidad_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Registrada without ModeloTipoFinalidad
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, null, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as ModeloTipoFinalidad is not present
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Finalidad no puede ser null en la Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_BorradorWithTipoFinalidadAndWithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Borrador with TipoFinalidad without ModeloEjecucion
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoriaExistente.setEstado(Convocatoria.Estado.BORRADOR);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, null, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as ModeloEjecucion is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion requerido para obtener TipoFinalidad");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithTipoFinalidadNotAsignedToModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with TipoFinalidad not asigned to given ModeloEjecucion
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito.given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as TipoFinalidad is not asigned to given
        // ModeloEjecucion
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFinalidad '%s' no disponible para el ModeloEjecucion %s",
            convocatoria.getFinalidad().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithSameModeloTipoFinalidadDisabled_ReturnsConvocatoria() {
    // given: a Convocatoria with updated ModeloTipoFinalidad disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.getFinalidad().setActivo(Boolean.FALSE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.FALSE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willReturn(convocatoria);

    Assertions.assertThatCode(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: No exception as disabled ModeloTipoFinalidad is the same
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithModeloTipoFinalidadDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with updated ModeloTipoFinalidad disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 2L, 1L, 1L, Boolean.TRUE);
    convocatoria.getFinalidad().setActivo(Boolean.FALSE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as updated ModeloTipoFinalidad is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFinalidad '%s' no está activo para el ModeloEjecucion %s",
            convocatoria.getFinalidad().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithSameTipoFinalidadDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with same TipoFinalidad disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.getFinalidad().setActivo(Boolean.FALSE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.FALSE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willReturn(convocatoria);

    Assertions.assertThatCode(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: No exception as disabled ModeloTipoFinalidad is the same
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithTipoFinalidadDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with updated TipoFinalidad disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 2L, 1L, 1L, Boolean.TRUE);
    convocatoria.getFinalidad().setActivo(Boolean.FALSE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as updated TipoFinalidad is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFinalidad '%s' no está activo", convocatoria.getFinalidad().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithNoExistingTipoRegimenConcurrencia_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with no existing TipoRegimenConcurrencia
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as TipoRegimenConcurrencia does not exist
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("RegimenConcurrencia '%s' no disponible", convocatoria.getRegimenConcurrencia().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithSameTipoRegimenConcurrenciaDisabled_ReturnsConvocatoria() {
    // given: a Convocatoria with updated TipoRegimenConcurrencia disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.getRegimenConcurrencia().setActivo(Boolean.FALSE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));
    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willReturn(convocatoria);

    Assertions.assertThatCode(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: No exception as disabled TipoRegimenConcurrencia is the same
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithTipoRegimenConcurrenciaDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with updated TipoRegimenConcurrencia disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 2L, 1L, Boolean.TRUE);
    convocatoria.getRegimenConcurrencia().setActivo(Boolean.FALSE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as TipoRegimenConcurrencia is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("RegimenConcurrencia '%s' no está activo", convocatoria.getRegimenConcurrencia().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_RegistradaWithoutTipoAmbitoGeografico_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Registrada without TipoAmbitoGeografico
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, null, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as TipoAmbitoGeografico is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("AmbitoGeografico no puede ser null en la Convocatoria");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithNoExistingTipoAmbitoGeografico_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with no existing TipoAmbitoGeografico
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as TipoAmbitoGeografico does not exist
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("AmbitoGeografico '%s' no disponible", convocatoria.getAmbitoGeografico().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithSameTipoAmbitoGeograficoDisabled_ReturnsConvocatoria() {
    // given: a Convocatoria with updated TipoAmbitoGeografico disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.getAmbitoGeografico().setActivo(Boolean.FALSE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willReturn(convocatoria);

    Assertions.assertThatCode(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: No exception as disabled TipoAmbitoGeografico is the same
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithTipoAmbitoGeograficoDisabled_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with updated TipoAmbitoGeografico disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 2L, Boolean.TRUE);
    convocatoria.getAmbitoGeografico().setActivo(Boolean.FALSE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as updated TipoAmbitoGeografico is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("AmbitoGeografico '%s' no está activo", convocatoria.getAmbitoGeografico().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithDuracionLowerConvocatoriaPeriodoJustificacionMesFinal_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with updated TipoAmbitoGeografico disabled
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 2L, Boolean.TRUE);
    convocatoria.setDuracion(2);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = new ConvocatoriaPeriodoJustificacion();
    convocatoriaPeriodoJustificacion.setId(1L);
    convocatoriaPeriodoJustificacion.setMesFinal(100);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito
        .given(convocatoriaPeriodoJustificacionRepository
            .findFirstByConvocatoriaIdOrderByNumPeriodoDesc(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as duracion no valid
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Hay ConvocatoriaPeriodoJustificacion con mesFinal inferior a la nueva duracion");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithDuracionLowerConvocatoriaPeriodoSeguimientoCientifico_ThrowsIllegalArgumentException() {
    // given: a Periodos from MesInicial 3 to MesFinal 12 and Convocatoria with
    // duracion < 12
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 2L, Boolean.TRUE);
    convocatoria.setDuracion(2);

    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      // @formatter:off
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico
          .builder()
          .id(Long.valueOf(i - 1))
          .convocatoriaId(convocatoriaExistente.getId())
          .numPeriodo(i - 1)
          .mesInicial((i * 2) - 1)
          .mesFinal(j * 1)
          .build());
      // @formatter:on
    }

    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito
        .given(convocatoriaPeriodoSeguimientoCientificoRepository
            .findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as duracion no valid
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Existen periodos de seguimiento científico con una duración en meses superior a la indicada");
  }

  /**
   * INICIO Test Requeridos en ConfiguracionSolicitud para las Registradas
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_RegistradaWithoutConfiguracionSolicitud_ThrowsNotFoundException() {
    // given: a Convocatoria Registrada without ConfiguracionSolicitud
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as ConfiguracionSolicitud is not found
        .isInstanceOf(ConfiguracionSolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_RegistradaWithoutTramitacionSGI_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Registrada without TramitacionSGI at
    // ConfiguracionSolicitud
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);
    configuracionSolicitud.setTramitacionSGI(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as TramitacionSGI is not present
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Habilitar presentacion SGI no puede ser null para crear ConfiguracionSolicitud cuando la convocatoria está registrada");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_RegistradaWithTramitacionSGITrueANDWithoutFasePresentacionSolicitudes_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Registrada With TramitacionSGI = true and without
    // FasePresentacionSolicitudes at
    // ConfiguracionSolicitud
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);
    configuracionSolicitud.setTramitacionSGI(Boolean.TRUE);
    configuracionSolicitud.setFasePresentacionSolicitudes(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as FasePresentacionSolicitudes is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Plazo presentación solicitudes no puede ser null cuando se establece presentacion SGI");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_RegistradaWithTramitacionSGIFalseANDWithoutFasePresentacionSolicitudes_DoesNotThrowAnyException() {
    // given: a Convocatoria Registrada With TramitacionSGI = false and without
    // FasePresentacionSolicitudes at
    // ConfiguracionSolicitud
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setObservaciones("observaciones-modificadas");
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaExistente, 1L);
    configuracionSolicitud.setTramitacionSGI(Boolean.FALSE);
    configuracionSolicitud.setFasePresentacionSolicitudes(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito
        .given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockModeloUnidad(1L, convocatoria.getModeloEjecucion(),
            convocatoria.getUnidadGestionRef(), Boolean.TRUE)));

    BDDMockito
        .given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFinalidad(convocatoria, Boolean.TRUE)));

    BDDMockito.given(tipoRegimenConcurrenciaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getRegimenConcurrencia()));

    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoria.getAmbitoGeografico()));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        givenData.setCodigo("codigo-modificado");
        return givenData;
      }
    });

    Assertions.assertThatCode(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: No exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_RegistradaWithoutFormularioSolicitud_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Registrada without FormularioSolicitud
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setFormularioSolicitud(null);
    convocatoria.setObservaciones("observaciones-modificadas");

    Assertions.assertThatThrownBy(
        // when: update Convocatoria
        () -> service.update(convocatoria))
        // then: throw exception as FormularioSolicitud is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FormularioSolicitud no puede ser null para actualizar Convocatoria");
  }

  /**
   * FINAL Test Requeridos en ConfiguracionSolicitud para las Registradas
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-R" })
  public void enable_WithExistingId_ReturnsConvocatoria() {
    // given: existing Convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        givenData.setActivo(Boolean.TRUE);
        return givenData;
      }
    });

    // when: enable Convocatoria
    Convocatoria enabledData = service.enable(convocatoria.getId());

    // then: Convocatoria is enabled
    Assertions.assertThat(enabledData).isNotNull();
    Assertions.assertThat(enabledData.getId()).isNotNull();
    Assertions.assertThat(enabledData.getId()).isEqualTo(convocatoria.getId());
    Assertions.assertThat(enabledData.getUnidadGestionRef()).isEqualTo(convocatoria.getUnidadGestionRef());
    Assertions.assertThat(enabledData.getModeloEjecucion().getId())
        .isEqualTo(convocatoria.getModeloEjecucion().getId());
    Assertions.assertThat(enabledData.getCodigo()).isEqualTo(convocatoria.getCodigo());
    Assertions.assertThat(enabledData.getFechaPublicacion()).isEqualTo(convocatoria.getFechaPublicacion());
    Assertions.assertThat(enabledData.getFechaProvisional()).isEqualTo(convocatoria.getFechaProvisional());
    Assertions.assertThat(enabledData.getFechaConcesion()).isEqualTo(convocatoria.getFechaConcesion());
    Assertions.assertThat(enabledData.getTitulo()).isEqualTo(convocatoria.getTitulo());
    Assertions.assertThat(enabledData.getObjeto()).isEqualTo(convocatoria.getObjeto());
    Assertions.assertThat(enabledData.getObservaciones()).isEqualTo(convocatoria.getObservaciones());
    Assertions.assertThat(enabledData.getFinalidad().getId()).isEqualTo(convocatoria.getFinalidad().getId());
    Assertions.assertThat(enabledData.getRegimenConcurrencia().getId())
        .isEqualTo(convocatoria.getRegimenConcurrencia().getId());
    Assertions.assertThat(enabledData.getEstado()).isEqualTo(convocatoria.getEstado());
    Assertions.assertThat(enabledData.getDuracion()).isEqualTo(convocatoria.getDuracion());
    Assertions.assertThat(enabledData.getAmbitoGeografico().getId())
        .isEqualTo(convocatoria.getAmbitoGeografico().getId());
    Assertions.assertThat(enabledData.getClasificacionCVN()).isEqualTo(convocatoria.getClasificacionCVN());
    Assertions.assertThat(enabledData.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-R" })
  public void enable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: enable non existing Convocatoria
        () -> service.enable(convocatoria.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-B" })
  public void disable_WithExistingId_ReturnsConvocatoria() {
    // given: existing Convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        givenData.setActivo(Boolean.FALSE);
        return givenData;
      }
    });

    // when: disable Convocatoria
    Convocatoria disabledData = service.disable(convocatoria.getId());

    // then: Convocatoria is disabled
    Assertions.assertThat(disabledData).isNotNull();
    Assertions.assertThat(disabledData.getId()).isNotNull();
    Assertions.assertThat(disabledData.getId()).isEqualTo(convocatoria.getId());
    Assertions.assertThat(disabledData.getUnidadGestionRef()).isEqualTo(convocatoria.getUnidadGestionRef());
    Assertions.assertThat(disabledData.getModeloEjecucion().getId())
        .isEqualTo(convocatoria.getModeloEjecucion().getId());
    Assertions.assertThat(disabledData.getCodigo()).isEqualTo(convocatoria.getCodigo());
    Assertions.assertThat(disabledData.getFechaPublicacion()).isEqualTo(convocatoria.getFechaPublicacion());
    Assertions.assertThat(disabledData.getFechaProvisional()).isEqualTo(convocatoria.getFechaProvisional());
    Assertions.assertThat(disabledData.getFechaConcesion()).isEqualTo(convocatoria.getFechaConcesion());
    Assertions.assertThat(disabledData.getTitulo()).isEqualTo(convocatoria.getTitulo());
    Assertions.assertThat(disabledData.getObjeto()).isEqualTo(convocatoria.getObjeto());
    Assertions.assertThat(disabledData.getObservaciones()).isEqualTo(convocatoria.getObservaciones());
    Assertions.assertThat(disabledData.getFinalidad().getId()).isEqualTo(convocatoria.getFinalidad().getId());
    Assertions.assertThat(disabledData.getRegimenConcurrencia().getId())
        .isEqualTo(convocatoria.getRegimenConcurrencia().getId());
    Assertions.assertThat(disabledData.getEstado()).isEqualTo(convocatoria.getEstado());
    Assertions.assertThat(disabledData.getDuracion()).isEqualTo(convocatoria.getDuracion());
    Assertions.assertThat(disabledData.getAmbitoGeografico().getId())
        .isEqualTo(convocatoria.getAmbitoGeografico().getId());
    Assertions.assertThat(disabledData.getClasificacionCVN()).isEqualTo(convocatoria.getClasificacionCVN());
    Assertions.assertThat(disabledData.getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-B" })
  public void disable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: disable non existing Convocatoria
        () -> service.disable(convocatoria.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-B" })
  public void disable_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Modificable returns false
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: disable
        () -> service.disable(convocatoria.getId()))
        // then:Exception is thrown
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede eliminar Convocatoria. No tiene los permisos necesarios o está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  public void registrar_WithEstadoBorradorAndExistingId_ReturnsConvocatoria() {
    // given: existing Convocatoria with Estado Borrador
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        givenData.setEstado(Convocatoria.Estado.REGISTRADA);
        return givenData;
      }
    });

    // when: registrar Convocatoria
    Convocatoria convocatoriaRegistrada = service.registrar(convocatoria.getId());

    // then: Convocatoria is estado Registrada
    Assertions.assertThat(convocatoriaRegistrada).isNotNull();
    Assertions.assertThat(convocatoriaRegistrada.getId()).isNotNull();
    Assertions.assertThat(convocatoriaRegistrada.getId()).isEqualTo(convocatoria.getId());
    Assertions.assertThat(convocatoriaRegistrada.getUnidadGestionRef()).isEqualTo(convocatoria.getUnidadGestionRef());
    Assertions.assertThat(convocatoriaRegistrada.getModeloEjecucion().getId())
        .isEqualTo(convocatoria.getModeloEjecucion().getId());
    Assertions.assertThat(convocatoriaRegistrada.getCodigo()).isEqualTo(convocatoria.getCodigo());
    Assertions.assertThat(convocatoriaRegistrada.getFechaPublicacion()).isEqualTo(convocatoria.getFechaPublicacion());
    Assertions.assertThat(convocatoriaRegistrada.getFechaProvisional()).isEqualTo(convocatoria.getFechaProvisional());
    Assertions.assertThat(convocatoriaRegistrada.getFechaConcesion()).isEqualTo(convocatoria.getFechaConcesion());
    Assertions.assertThat(convocatoriaRegistrada.getTitulo()).isEqualTo(convocatoria.getTitulo());
    Assertions.assertThat(convocatoriaRegistrada.getObjeto()).isEqualTo(convocatoria.getObjeto());
    Assertions.assertThat(convocatoriaRegistrada.getObservaciones()).isEqualTo(convocatoria.getObservaciones());
    Assertions.assertThat(convocatoriaRegistrada.getFinalidad().getId()).isEqualTo(convocatoria.getFinalidad().getId());
    Assertions.assertThat(convocatoriaRegistrada.getRegimenConcurrencia().getId())
        .isEqualTo(convocatoria.getRegimenConcurrencia().getId());
    Assertions.assertThat(convocatoriaRegistrada.getEstado()).isEqualTo(Convocatoria.Estado.REGISTRADA);
    Assertions.assertThat(convocatoriaRegistrada.getDuracion()).isEqualTo(convocatoria.getDuracion());
    Assertions.assertThat(convocatoriaRegistrada.getAmbitoGeografico().getId())
        .isEqualTo(convocatoria.getAmbitoGeografico().getId());
    Assertions.assertThat(convocatoriaRegistrada.getClasificacionCVN()).isEqualTo(convocatoria.getClasificacionCVN());
    Assertions.assertThat(convocatoriaRegistrada.getActivo()).isEqualTo(convocatoria.getActivo());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrar_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: registrar non existing Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void registrar_WithoutId_ThrowsIllegalArgumentException() {
    // given: a Convocatoria without id filled
    Convocatoria convocatoria = generarMockConvocatoria(null, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Id no puede ser null para registrar Convocatoria");
  }

  @Test
  public void registrar_WithEstadoNotBorrador_ThrowsIllegalArgumentException() {
    // given: a Convocatoria with estado not 'Borrador'
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.REGISTRADA);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as Estado is not Borrador
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Convocatoria deber estar en estado 'Borrador' para pasar a 'Registrada'");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrar_WithEstadoBorradorAndWithoutUnidadGestion_ThrowsIllegalArgumentException() {
    // given: a Convocatoria without UnidadGestion
    Convocatoria convocatoria = generarMockConvocatoria(1L, null, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as UnidadGestion is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UnidadGestionRef no puede ser null en la Convocatoria");
  }

  @Test
  public void registrar_WithEstadoBorradorAndWithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: a Convocatoria without ModeloEjecucion
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, null, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as ModeloEjecucion is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion no puede ser null en la Convocatoria");
  }

  @Test
  public void registrar_WithEstadoBorradorAndWithoutTitulo_ThrowsIllegalArgumentException() {
    // given: a Convocatoria without Titulo
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setTitulo(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as Titulo is not present
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Titulo no puede ser null en la Convocatoria");
  }

  @Test
  public void registrar_WithEstadoBorradorAndWithoutTipoFinalidad_ThrowsIllegalArgumentException() {
    // given: a Convocatoria without TipoFinalidad
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, null, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as TipoFinalidad is not present
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Finalidad no puede ser null en la Convocatoria");
  }

  @Test
  public void registrar_WithEstadoBorradorAndWithoutTipoAmbitoGeografico_ThrowsIllegalArgumentException() {
    // given: a Convocatoria without TipoAmbitoGeografico
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, null, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as TipoAmbitoGeografico is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("AmbitoGeografico no puede ser null en la Convocatoria");
  }

  /**
   * INICIO Test Requeridos en ConfiguracionSolicitud para las Registradas
   */

  @Test
  public void registrar_WithEstadoBorradorAndWithoutConfiguracionSolicitud_ThrowsNotFoundException() {
    // given: a Convocatoria Borrador without ConfiguracionSolicitud
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as ConfiguracionSolicitud is not found
        .isInstanceOf(ConfiguracionSolicitudNotFoundException.class);
  }

  @Test
  public void registrar_WithEstadoBorradorAndWithoutTramitacionSGI_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Borrador without TramitacionSGI at
    // ConfiguracionSolicitud
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    configuracionSolicitud.setTramitacionSGI(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as TramitacionSGI is not present
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Habilitar presentacion SGI no puede ser null para crear ConfiguracionSolicitud cuando la convocatoria está registrada");
  }

  @Test
  public void registrar_WithEstadoBorradorWithTramitacionSGITrueANDWithoutFasePresentacionSolicitudes_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Borrador With TramitacionSGI = true and without
    // FasePresentacionSolicitudes at ConfiguracionSolicitud
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    configuracionSolicitud.setTramitacionSGI(Boolean.TRUE);
    configuracionSolicitud.setFasePresentacionSolicitudes(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as FasePresentacionSolicitudes is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Plazo presentación solicitudes no puede ser null cuando se establece presentacion SGI");
  }

  @Test
  public void registrar_WithEstadoBorradorAndWithTramitacionSGIFalseANDWithoutFasePresentacionSolicitudes_DoesNotThrowAnyException() {
    // given: a Convocatoria Borrador With TramitacionSGI = true and without
    // FasePresentacionSolicitudes at ConfiguracionSolicitud
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    configuracionSolicitud.setTramitacionSGI(Boolean.FALSE);
    configuracionSolicitud.setFasePresentacionSolicitudes(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito.given(repository.save(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        givenData.setEstado(Convocatoria.Estado.REGISTRADA);
        return givenData;
      }
    });

    Assertions.assertThatCode(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: no exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void registrar_WithEstadoBorradorAndWithoutFormularioSolicitud_ThrowsIllegalArgumentException() {
    // given: a Convocatoria Borrador without FormularioSolicitud at
    // ConfiguracionSolicitud
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setFormularioSolicitud(null);
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: registrar Convocatoria
        () -> service.registrar(convocatoria.getId()))
        // then: throw exception as FormularioSolicitud is not present
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FormularioSolicitud no puede ser null en la Convocatoria");
  }

  /**
   * FINAL Test Requeridos en ConfiguracionSolicitud para las Registradas
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void modificable_ConvocatoriaIdWithoutSolicitudesOrProyectos_ReturnsTRUE() throws Exception {
    // given: existing id modificable
    Long id = 1L;
    String unidadGestionRef = "2";

    BDDMockito.given(repository.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any()))
        .willReturn(Boolean.FALSE);

    // when: check modificable
    boolean responseData = service.isRegistradaConSolicitudesOProyectos(id, unidadGestionRef,
        new String[] { "CSP-CON-V" });

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isTrue();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void modificable_ConvocatoriaIdWithSolicitudesOrProyectos_ReturnsFALSE() throws Exception {
    // given: given: existing id not modificable
    Long id = 1L;
    String unidadGestionRef = "2";

    BDDMockito.given(repository.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any()))
        .willReturn(Boolean.TRUE);

    // when: check modificable
    boolean responseData = service.isRegistradaConSolicitudesOProyectos(id, unidadGestionRef,
        new String[] { "CSP-CON-E" });

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WhenIsRegistrable_ReturnsTRUE() throws Exception {
    // given: existing id registrable
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(configuracionSolicitud));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isTrue();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutId_ReturnsFalse() throws Exception {
    // given: no id
    Long id = null;

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithNoExistingId_ReturnsFalse() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithEstadoRegistrada_ReturnsFalse() throws Exception {
    // given: existing id with Estado Registrada
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.REGISTRADA);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutUnidadGestionRef_ReturnsFalse() throws Exception {
    // given: existing id without UnidadGestionRef
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setUnidadGestionRef(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutModeloEjecucion_ReturnsFalse() throws Exception {
    // given: existing id without ModeloEjecucion
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setModeloEjecucion(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutFechaPublicacion_ReturnsFalse() throws Exception {
    // given: existing id without FechaPublicacion
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setFechaPublicacion(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutTitulo_ReturnsFalse() throws Exception {
    // given: existing id without Titulo
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setTitulo(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutFinalidad_ReturnsFalse() throws Exception {
    // given: existing id without Finalidad
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setFinalidad(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutAmbitoGeografico_ReturnsFalse() throws Exception {
    // given: existing id without AmbitoGeografico
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setAmbitoGeografico(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutConfiguracionSolicitud_ReturnsFalse() throws Exception {
    // given: existing id without ConfiguracionSolicitud
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutTramitacionSGI_ReturnsFalse() throws Exception {
    // given: existing id without TramitacionSGI
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    configuracionSolicitud.setTramitacionSGI(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(configuracionSolicitud));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutFormularioSolicitud_ReturnsFalse() throws Exception {
    // given: existing id without FormularioSolicitud
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setFormularioSolicitud(null);
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void registrable_WithoutFasePresentacionSolicitudesWhenTramitacionSGIIsTrue_ReturnsFalse() throws Exception {
    // given: existing id without BaremacionRef
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(id, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    configuracionSolicitud.setTramitacionSGI(Boolean.TRUE);
    configuracionSolicitud.setFasePresentacionSolicitudes(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(configuracionSolicitud));

    // when: check registrable
    boolean responseData = service.registrable(id);

    // then: returns FALSE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  public void existsById_WithExistingId_ReturnsTRUE() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isTrue();
  }

  @Test
  public void existsById_WithNoExistingId_ReturnsFALSE() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findById_WithExistingId_ReturnsConvocatoria() throws Exception {
    // given: existing Convocatoria
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoriaExistente));

    // when: find by id Convocatoria
    Convocatoria convocatoria = service.findById(convocatoriaExistente.getId());

    // then: returns Convocatoria
    Assertions.assertThat(convocatoria).isNotNull();
    Assertions.assertThat(convocatoria.getId()).isNotNull();
    Assertions.assertThat(convocatoria.getId()).isEqualTo(convocatoriaExistente.getId());
    Assertions.assertThat(convocatoria.getUnidadGestionRef()).isEqualTo(convocatoriaExistente.getUnidadGestionRef());
    Assertions.assertThat(convocatoria.getModeloEjecucion().getId())
        .isEqualTo(convocatoriaExistente.getModeloEjecucion().getId());
    Assertions.assertThat(convocatoria.getCodigo()).isEqualTo(convocatoriaExistente.getCodigo());
    Assertions.assertThat(convocatoria.getFechaPublicacion()).isEqualTo(convocatoriaExistente.getFechaPublicacion());
    Assertions.assertThat(convocatoria.getFechaProvisional()).isEqualTo(convocatoriaExistente.getFechaProvisional());
    Assertions.assertThat(convocatoria.getFechaConcesion()).isEqualTo(convocatoriaExistente.getFechaConcesion());
    Assertions.assertThat(convocatoria.getTitulo()).isEqualTo(convocatoriaExistente.getTitulo());
    Assertions.assertThat(convocatoria.getObjeto()).isEqualTo(convocatoriaExistente.getObjeto());
    Assertions.assertThat(convocatoria.getObservaciones()).isEqualTo(convocatoriaExistente.getObservaciones());
    Assertions.assertThat(convocatoria.getFinalidad().getId()).isEqualTo(convocatoriaExistente.getFinalidad().getId());
    Assertions.assertThat(convocatoria.getRegimenConcurrencia().getId())
        .isEqualTo(convocatoriaExistente.getRegimenConcurrencia().getId());
    Assertions.assertThat(convocatoria.getEstado()).isEqualTo(convocatoriaExistente.getEstado());
    Assertions.assertThat(convocatoria.getDuracion()).isEqualTo(convocatoriaExistente.getDuracion());
    Assertions.assertThat(convocatoria.getAmbitoGeografico().getId())
        .isEqualTo(convocatoriaExistente.getAmbitoGeografico().getId());
    Assertions.assertThat(convocatoria.getClasificacionCVN()).isEqualTo(convocatoriaExistente.getClasificacionCVN());
    Assertions.assertThat(convocatoria.getActivo()).isEqualTo(convocatoriaExistente.getActivo());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Convocatoria
    List<Convocatoria> convocatorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      if (i % 2 == 0) {
        convocatorias.add(generarMockConvocatoria(Long.valueOf(i), 1L, 1L, 1L, 1L, 1L, Boolean.TRUE));
      }
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<Convocatoria>>any(), ArgumentMatchers.<Pageable>any()))
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

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Convocatoria> page = service.findAll(null, paging);

    // then: A Page with ten Convocatoria are returned
    // containing Codigo='codigo-62' to 'codigo-80'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(50);

    for (int i = 0, j = 62; i < 10; i++, j += 2) {
      Convocatoria item = page.getContent().get(i);
      Assertions.assertThat(item.getCodigo()).isEqualTo("codigo-" + String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo(Boolean.TRUE);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred Convocatoria
    List<Convocatoria> convocatorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      convocatorias.add(
          generarMockConvocatoria(Long.valueOf(i), 1L, 1L, 1L, 1L, 1L, (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<Convocatoria>>any(), ArgumentMatchers.<Pageable>any()))
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

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Convocatoria> page = service.findAll(null, paging);

    // then: A Page with ten Convocatoria are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Convocatoria item = page.getContent().get(i);
      Assertions.assertThat(item.getCodigo()).isEqualTo("codigo-" + String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllRestringidos_WithPaging_ReturnsPage() {
    // given: One hundred Convocatoria
    List<Convocatoria> convocatorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      convocatorias.add(
          generarMockConvocatoria(Long.valueOf(i), 1L, 1L, 1L, 1L, 1L, (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<Convocatoria>>any(), ArgumentMatchers.<Pageable>any()))
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

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Convocatoria> page = service.findAllRestringidos(null, paging);

    // then: A Page with ten Convocatoria are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Convocatoria item = page.getContent().get(i);
      Assertions.assertThat(item.getCodigo()).isEqualTo("codigo-" + String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllTodosRestringidos_WithPaging_ReturnsPage() {
    // given: One hundred Convocatoria
    List<Convocatoria> convocatorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      convocatorias.add(
          generarMockConvocatoria(Long.valueOf(i), 1L, 1L, 1L, 1L, 1L, (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<Convocatoria>>any(), ArgumentMatchers.<Pageable>any()))
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

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Convocatoria> page = service.findAllTodosRestringidos(null, paging);

    // then: A Page with ten Convocatoria are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Convocatoria item = page.getContent().get(i);
      Assertions.assertThat(item.getCodigo()).isEqualTo("codigo-" + String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

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

    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder().id(modeloEjecucionId)
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId)).activo(Boolean.TRUE).build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder().id(modeloTipoFinalidadId)
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId)).activo(Boolean.TRUE).build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder().id(modeloTipoFinalidadId).modeloEjecucion(modeloEjecucion)
            .tipoFinalidad(tipoFinalidad).activo(Boolean.TRUE).build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder().id(tipoRegimenConcurrenciaId)
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))
            .activo(Boolean.TRUE).build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder().id(tipoAmbitoGeogragicoId)
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId)).activo(Boolean.TRUE)
            .build();

    Convocatoria convocatoria = Convocatoria.builder().id(convocatoriaId)
        .unidadGestionRef((unidadGestionId == null) ? null : "2").modeloEjecucion(modeloEjecucion)
        .codigo("codigo-" + String.format("%03d", convocatoriaId))
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z")).fechaProvisional(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaConcesion(Instant.parse("2021-08-01T00:00:00Z")).titulo("titulo-" + String.format("%03d", convocatoriaId))
        .objeto("objeto-" + String.format("%03d", convocatoriaId))
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())
        .formularioSolicitud(FormularioSolicitud.PROYECTO).regimenConcurrencia(tipoRegimenConcurrencia)
        .estado(Convocatoria.Estado.REGISTRADA).duracion(12).ambitoGeografico(tipoAmbitoGeografico)
        .clasificacionCVN(ClasificacionCVN.AYUDAS).activo(activo).build();

    return convocatoria;
  }

  /**
   * Función que genera ModeloUnidad
   * 
   * @param modeloUnidadId
   * @param modeloEjecucion
   * @param unidadGestionId
   * @param activo
   * @return
   */
  private ModeloUnidad generarMockModeloUnidad(Long modeloUnidadId, ModeloEjecucion modeloEjecucion,
      String unidadGestionId, Boolean activo) {

    return ModeloUnidad.builder().id(modeloUnidadId).modeloEjecucion(modeloEjecucion).unidadGestionRef(unidadGestionId)
        .activo(activo).build();

  }

  /**
   * Función que genera ModeloTipoFinalidad a parit de una convocatoria
   * 
   * @param convocatoria
   * @return
   */
  private ModeloTipoFinalidad generarMockModeloTipoFinalidad(Convocatoria convocatoria, Boolean activo) {

    return ModeloTipoFinalidad.builder().id(convocatoria.getFinalidad().getId())
        .modeloEjecucion(convocatoria.getModeloEjecucion()).tipoFinalidad(convocatoria.getFinalidad()).activo(activo)
        .build();
  }

  /**
   * Genera un objeto ConfiguracionSolicitud
   * 
   * @param configuracionSolicitudId
   * @param convocatoria
   * @param convocatoriaFaseId
   * @return
   */
  private ConfiguracionSolicitud generarMockConfiguracionSolicitud(Long configuracionSolicitudId,
      Convocatoria convocatoria, Long convocatoriaFaseId) {

    // @formatter:off
    TipoFase tipoFase = TipoFase.builder()
        .id(convocatoriaFaseId)
        .nombre("nombre-1")
        .activo(Boolean.TRUE)
        .build();

    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()
        .id(convocatoriaFaseId)
        .convocatoriaId(convocatoria.getId())
        .tipoFase(tipoFase)
        .fechaInicio(Instant.parse("2020-10-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-15T00:00:00Z"))
        .observaciones("observaciones")
        .build();

    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()
        .id(configuracionSolicitudId)
        .convocatoriaId(convocatoria.getId())
        .tramitacionSGI(Boolean.TRUE)
        .fasePresentacionSolicitudes(convocatoriaFase)
        .importeMaximoSolicitud(BigDecimal.valueOf(12345))
        .build();
    // @formatter:on

    return configuracionSolicitud;
  }

  /**
   * Genera un objeto ConfiguracionSolicitud
   * 
   * @param configuracionSolicitudId
   * @param convocatoriaId
   * @param convocatoriaFaseId
   * @return
   */
  private ConfiguracionSolicitud generarMockConfiguracionSolicitud(Long configuracionSolicitudId, Long convocatoriaId,
      Long convocatoriaFaseId) {
    // @formatter:off
    TipoFase tipoFase = TipoFase.builder()
        .id(convocatoriaFaseId)
        .nombre("nombre-1")
        .activo(Boolean.TRUE)
        .build();

    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()
        .id(convocatoriaFaseId)
        .convocatoriaId(convocatoriaId)
        .tipoFase(tipoFase)
        .fechaInicio(Instant.parse("2020-10-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-15T00:00:00Z"))
        .observaciones("observaciones")
        .build();

    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()
        .id(configuracionSolicitudId)
        .convocatoriaId(convocatoriaId)
        .tramitacionSGI(Boolean.TRUE)
        .fasePresentacionSolicitudes(convocatoriaFase)
        .importeMaximoSolicitud(BigDecimal.valueOf(12345))
        .build();
    // @formatter:on

    return configuracionSolicitud;
  }

}
