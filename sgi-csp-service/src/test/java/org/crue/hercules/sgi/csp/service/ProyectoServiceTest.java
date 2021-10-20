package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaAreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadGestoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.EstadoProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.EstadoProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoAreaConocimientoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoClasificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoIVARepository;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProrrogaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProyectoSgeRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudModalidadRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoAreaConocimientoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoClasificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEntidadFinanciadoraAjenaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * ProyectoServiceTest
 */
public class ProyectoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoRepository repository;
  @Mock
  private EstadoProyectoRepository estadoProyectoRepository;
  @Mock
  private ModeloUnidadRepository modeloUnidadRepository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository;
  @Mock
  private ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService;
  @Mock
  private ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository;
  @Mock
  private ProyectoEntidadConvocanteService proyectoEntidadConvocanteService;
  @Mock
  private ConvocatoriaAreaTematicaRepository convocatoriaAreaTematicaRepository;
  @Mock
  private ContextoProyectoService contextoProyectoService;
  @Mock
  private ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository;
  @Mock
  private ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService;
  @Mock
  private ConvocatoriaEntidadGestoraRepository convocatoriaEntidadGestoraRepository;
  @Mock
  private ProyectoEntidadGestoraService proyectoEntidadGestoraService;
  @Mock
  private SolicitudRepository solicitudRepository;
  @Mock
  private SolicitudProyectoRepository solicitudProyectoRepository;
  @Mock
  private SolicitudModalidadRepository solicitudModalidadRepository;
  @Mock
  private SolicitudProyectoEquipoRepository solicitudEquipoRepository;
  @Mock
  private ProyectoEquipoService proyectoEquipoService;
  @Mock
  private SolicitudProyectoSocioRepository solicitudSocioRepository;
  @Mock
  private ProyectoSocioService proyectoSocioService;
  @Mock
  private SolicitudProyectoSocioEquipoRepository solicitudEquipoSocioRepository;
  @Mock
  private ProyectoSocioEquipoService proyectoEquipoSocioService;
  @Mock
  private SolicitudProyectoSocioPeriodoPagoRepository solicitudPeriodoPagoRepository;
  @Mock
  private ProyectoSocioPeriodoPagoService proyectoSocioPeriodoPagoService;
  @Mock
  private SolicitudProyectoSocioPeriodoJustificacionRepository solicitudPeriodoJustificacionRepository;
  @Mock
  private ProyectoSocioPeriodoJustificacionService proyectoSocioPeriodoJustificacionService;
  @Mock
  private ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;
  @Mock
  private SolicitudProyectoEntidadFinanciadoraAjenaRepository solicitudProyectoEntidadFinanciadoraAjenaRepository;
  @Mock
  private SolicitudProyectoAreaConocimientoRepository solicitudProyectoAreaConocimientoRepository;
  @Mock
  private SolicitudProyectoClasificacionRepository solicitudProyectoClasificacionRepository;
  @Mock
  private ProyectoAreaConocimientoRepository proyectoAreaConocimientoRepository;
  @Mock
  private ProyectoClasificacionRepository proyectoClasificacionRepository;
  @Mock
  ProgramaRepository programaRepository;
  @Mock
  ProyectoProrrogaRepository proyectoProrrogaRepository;
  @Mock
  ProyectoPartidaService proyectoPartidaService;
  @Mock
  ConvocatoriaPartidaService convocatoriaPartidaService;
  @Mock
  ProyectoIVARepository proyectoIVARepository;
  @Mock
  ProyectoProyectoSgeRepository proyectoProyectoSgeRepository;
  @Mock
  ProyectoConceptoGastoService proyectoConceptoGastoService;
  @Mock
  ProyectoConceptoGastoCodigoEcService proyectoConceptoGastoCodigoEcService;
  @Mock
  ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;
  @Mock
  SolicitudProyectoResponsableEconomicoRepository solicitudProyectoResponsableEconomicoRepository;
  @Mock
  ProyectoResponsableEconomicoService proyectoResponsableEconomicoService;
  @Mock
  Validator validator;
  @Mock
  private ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository;
  @Mock
  private ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository;
  @Mock
  private EstadoProyectoPeriodoJustificacionRepository estadoProyectoPeriodoJustificacionRepository;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  private ProyectoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoServiceImpl(sgiConfigProperties, repository, estadoProyectoRepository, modeloUnidadRepository,
        convocatoriaRepository, convocatoriaEntidadFinanciadoraRepository, proyectoEntidadFinanciadoraService,
        convocatoriaEntidadConvocanteRepository, proyectoEntidadConvocanteService, convocatoriaEntidadGestoraRepository,
        proyectoEntidadGestoraService, convocatoriaAreaTematicaRepository, contextoProyectoService,
        convocatoriaPeriodoSeguimientoCientificoRepository, proyectoPeriodoSeguimientoService, solicitudRepository,
        solicitudProyectoRepository, solicitudModalidadRepository, solicitudEquipoRepository, proyectoEquipoService,
        solicitudSocioRepository, proyectoSocioService, solicitudEquipoSocioRepository, proyectoEquipoSocioService,
        solicitudPeriodoPagoRepository, proyectoSocioPeriodoPagoService, solicitudPeriodoJustificacionRepository,
        proyectoSocioPeriodoJustificacionService, convocatoriaConceptoGastoRepository,
        solicitudProyectoEntidadFinanciadoraAjenaRepository, proyectoProrrogaRepository,
        proyectoAreaConocimientoRepository, proyectoClasificacionRepository,
        solicitudProyectoAreaConocimientoRepository, solicitudProyectoClasificacionRepository, programaRepository,
        proyectoPartidaService, convocatoriaPartidaService, proyectoIVARepository, proyectoProyectoSgeRepository,
        proyectoConceptoGastoService, proyectoConceptoGastoCodigoEcService, convocatoriaConceptoGastoCodigoEcRepository,
        solicitudProyectoResponsableEconomicoRepository, proyectoResponsableEconomicoService, validator,
        convocatoriaPeriodoJustificacionRepository, proyectoPeriodoJustificacionRepository,
        estadoProyectoPeriodoJustificacionRepository);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void create_ReturnsProyecto() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any())).will((InvocationOnMock invocation) -> {
      Proyecto proyectoCreado = invocation.getArgument(0);
      if (proyectoCreado.getId() == null) {
        proyectoCreado.setId(1L);
      }

      return proyectoCreado;
    });

    BDDMockito.given(estadoProyectoRepository.save(ArgumentMatchers.<EstadoProyecto>any()))
        .will((InvocationOnMock invocation) -> {
          EstadoProyecto estadoProyectoCreado = invocation.getArgument(0);
          estadoProyectoCreado.setId(1L);
          return estadoProyectoCreado;
        });

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    // when: Creamos el Proyecto
    Proyecto proyectoCreado = service.create(proyecto);

    // then: El Proyecto se crea correctamente
    Assertions.assertThat(proyectoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());
    Assertions.assertThat(proyectoCreado.getActivo()).as("getActivo").isEqualTo(proyecto.getActivo());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void createWithConvocatoria_ReturnsProyecto() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setConvocatoriaId(1L);

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any())).will((InvocationOnMock invocation) -> {
      Proyecto proyectoCreado = invocation.getArgument(0);
      if (proyectoCreado.getId() == null) {
        proyectoCreado.setId(1L);
      }

      return proyectoCreado;
    });

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(estadoProyectoRepository.save(ArgumentMatchers.<EstadoProyecto>any()))
        .will((InvocationOnMock invocation) -> {
          EstadoProyecto estadoProyectoCreado = invocation.getArgument(0);
          estadoProyectoCreado.setId(1L);
          return estadoProyectoCreado;
        });

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    // when: Creamos el Proyecto
    Proyecto proyectoCreado = service.create(proyecto);

    // then: El Proyecto se crea correctamente
    Assertions.assertThat(proyectoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());
    Assertions.assertThat(proyectoCreado.getActivo()).as("getActivo").isEqualTo(proyecto.getActivo());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void createWithConvocatoriaAndConvocatoriaAreaTematica_ReturnsProyecto() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setConvocatoriaId(1L);

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any())).will((InvocationOnMock invocation) -> {
      Proyecto proyectoCreado = invocation.getArgument(0);
      if (proyectoCreado.getId() == null) {
        proyectoCreado.setId(1L);
      }

      return proyectoCreado;
    });

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(estadoProyectoRepository.save(ArgumentMatchers.<EstadoProyecto>any()))
        .will((InvocationOnMock invocation) -> {
          EstadoProyecto estadoProyectoCreado = invocation.getArgument(0);
          estadoProyectoCreado.setId(1L);
          return estadoProyectoCreado;
        });

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    // when: Creamos el Proyecto
    Proyecto proyectoCreado = service.create(proyecto);

    // then: El Proyecto se crea correctamente
    Assertions.assertThat(proyectoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());
    Assertions.assertThat(proyectoCreado.getActivo()).as("getActivo").isEqualTo(proyecto.getActivo());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void create_WithConvocatoriaNotExists_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setConvocatoriaId(1L);

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La convocatoria con id '" + proyecto.getConvocatoriaId() + "' no existe");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto que ya tiene id
    Proyecto proyecto = generarMockProyecto(1L);

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Proyecto id tiene que ser null para crear un Proyecto");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_UGI" })
  public void create_WithoutUnidadGestion_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setUnidadGestionRef(null);

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La Unidad de Gestión no es gestionable por el usuario");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void create_WithoutModeloUnidad_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.empty());

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion '" + proyecto.getModeloEjecucion().getNombre()
            + "' no disponible para la UnidadGestion " + proyecto.getUnidadGestionRef());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void create_WithCosteHoraAndWithoutTimesheetTrue_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setTimesheet(false);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El proyecto requiere timesheet");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void create_WithCosteHoraAndWithoutTipoHorasAnuales_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setTipoHorasAnuales(null);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El campo tipoHorasAnuales debe ser obligatorio para el proyecto");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E_2" })
  public void update_ReturnsProyecto() {
    // given: Un nuevo Proyecto con las observaciones actualizadas
    Proyecto proyecto = generarMockProyecto(1L);
    Proyecto proyectoObservacionesActualizadas = generarMockProyecto(1L);
    proyectoObservacionesActualizadas.setObservaciones("observaciones actualizadas");

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));
    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Proyecto
    Proyecto proyectoActualizada = service.update(proyectoObservacionesActualizadas);

    // then: El Proyecto se actualiza correctamente.
    Assertions.assertThat(proyectoActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getId()).as("getId()").isEqualTo(proyecto.getId());
    Assertions.assertThat(proyectoActualizada.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoActualizada.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());
    Assertions.assertThat(proyectoActualizada.getActivo()).as("getActivo").isEqualTo(proyecto.getActivo());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E_2" })
  public void updateWithConvocatoria_ReturnsProyecto() {
    // given: Un nuevo Proyecto con las observaciones actualizadas
    Long convocatoriaId = 1L;
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setConvocatoriaId(convocatoriaId);
    Proyecto proyectoObservacionesActualizadas = generarMockProyecto(1L);
    proyectoObservacionesActualizadas.setConvocatoriaId(convocatoriaId);
    proyectoObservacionesActualizadas.setObservaciones("observaciones actualizadas");

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));
    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Proyecto
    Proyecto proyectoActualizada = service.update(proyectoObservacionesActualizadas);

    // then: El Proyecto se actualiza correctamente.
    Assertions.assertThat(proyectoActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getId()).as("getId()").isEqualTo(proyecto.getId());
    Assertions.assertThat(proyectoActualizada.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoActualizada.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());
    Assertions.assertThat(proyectoActualizada.getActivo()).as("getActivo").isEqualTo(proyecto.getActivo());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void update_WithConvocatoriaNotExists_ThrowsIllegalArgumentException() {
    // given: Actualizar proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setConvocatoriaId(1L);

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Actualizamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La convocatoria con id '" + proyecto.getConvocatoriaId() + "' no existe");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_UGI" })
  public void update_WithoutUnidadGestion_ThrowsIllegalArgumentException() {
    // given: Actualizar Proyecto
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));
    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(new ModeloUnidad()));

    // when: Actualizamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El proyecto no pertenece a una Unidad de Gestión gestionable por el usuario");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void update_WithoutModeloUnidad_ThrowsIllegalArgumentException() {
    // given: Actualizar Proyecto
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.empty());

    // when: Actualizamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion '" + proyecto.getModeloEjecucion().getNombre()
            + "' no disponible para la UnidadGestion " + proyecto.getUnidadGestionRef());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E_2" })
  public void update_WithDistinctConvocatoria_ThrowsIllegalArgumentException() {
    // given: Actualizar Proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setConvocatoriaId(1L);
    Proyecto proyectoConvocatoriaUpdate = generarMockProyecto(1L);
    proyecto.setConvocatoriaId(2L);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    // then: Lanza una excepcion porque no se puede modificar la convocatoria del
    // proyecto
    Assertions.assertThatThrownBy(() -> service.update(proyectoConvocatoriaUpdate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Existen campos del proyecto modificados que no se pueden modificar");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void update_WithCosteHoraAndWithoutTimesheetTrue_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setTimesheet(false);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El proyecto requiere timesheet");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void update_WithCosteHoraAndWithoutTipoHorasAnuales_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setTipoHorasAnuales(null);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El campo tipoHorasAnuales debe ser obligatorio para el proyecto");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-R_2" })
  public void enable_ReturnsProyecto() {
    // given: Un nuevo Proyecto inactivo
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el Proyecto
    Proyecto programaActualizado = service.enable(proyecto.getId());

    // then: El Proyecto se activa correctamente.
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(proyecto.getId());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void enable_WithIdNotExist_ThrowsProyectoNotFoundException() {
    // given: Un id de un Proyecto que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el Proyecto
    // then: Lanza una excepcion porque el Proyecto no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste)).isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-B_2" })
  public void disable_ReturnsProyecto() {
    // given: Un Proyecto activo
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el Proyecto
    Proyecto proyectoActualizada = service.disable(proyecto.getId());

    // then: El Proyecto se desactivan correctamente
    Assertions.assertThat(proyectoActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoActualizada.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void disable_WithIdNotExist_ThrowsProyectoNotFoundException() {
    // given: Un id de un Proyecto que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el Proyecto
    // then: Lanza una excepcion porque el Proyecto no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E_2" })
  public void findById_ReturnsProyecto() {
    // given: Un Proyecto con el id buscado
    Long idBuscado = 1L;
    Proyecto proyectoBuscada = generarMockProyecto(idBuscado);
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(proyectoBuscada));

    // when: Buscamos el Proyecto por su id
    Proyecto proyecto = service.findById(idBuscado);

    // then: el Proyecto
    Assertions.assertThat(proyecto).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(proyecto.getEstado().getId()).as("getEstado().getId()").isEqualTo(1);
    Assertions.assertThat(proyecto.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones-001");
    Assertions.assertThat(proyecto.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("2");
    Assertions.assertThat(proyecto.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void findById_WithIdNotExist_ThrowsProyectoNotFoundException() throws Exception {
    // given: Ningun Proyecto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Proyecto por su id
    // then: lanza un ProyectoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Proyecto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
            List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
            Page<Proyecto> page = new PageImpl<>(content, pageable, proyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Proyecto> page = service.findAllRestringidos(null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_2" })
  public void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Proyecto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
            List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
            Page<Proyecto> page = new PageImpl<>(content, pageable, proyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Proyecto> page = service.findAllTodosRestringidos(null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  /**
   * Función que devuelve un objeto Proyecto
   * 
   * @param id id del Proyecto
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(Long id) {
    EstadoProyecto estadoProyecto = generarMockEstadoProyecto(1L);

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setTitulo("PRO" + (id != null ? id : 1));
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones("observaciones-" + String.format("%03d", id));
    proyecto.setUnidadGestionRef("2");
    proyecto.setFechaInicio(Instant.now());
    proyecto.setFechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(1))));
    proyecto.setModeloEjecucion(modeloEjecucion);
    proyecto.setFinalidad(tipoFinalidad);
    proyecto.setAmbitoGeografico(tipoAmbitoGeografico);
    proyecto.setConfidencial(Boolean.FALSE);
    proyecto.setCosteHora(true);
    proyecto.setTimesheet(true);
    proyecto.setTipoHorasAnuales(Proyecto.TipoHorasAnuales.REAL);
    proyecto.setActivo(true);

    if (id != null) {
      proyecto.setEstado(estadoProyecto);
    }

    return proyecto;
  }

  /**
   * Función que devuelve un objeto EstadoProyecto
   * 
   * @param id id del EstadoProyecto
   * @return el objeto EstadoProyecto
   */
  private EstadoProyecto generarMockEstadoProyecto(Long id) {
    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(id);
    estadoProyecto.setComentario("Estado-" + id);
    estadoProyecto.setEstado(EstadoProyecto.Estado.BORRADOR);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    return estadoProyecto;
  }
}
