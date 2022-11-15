package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.ProyectoDto;
import org.crue.hercules.sgi.csp.dto.ProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoEjecucionEconomica;
import org.crue.hercules.sgi.csp.dto.ProyectosCompetitivosPersonas;
import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.MissingInvestigadorPrincipalInProyectoEquipoException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoIVAException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.EstadoProyecto.Estado;
import org.crue.hercules.sgi.csp.model.EstadoProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.ProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.model.ProyectoIVA;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga.Tipo;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
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
import org.crue.hercules.sgi.csp.repository.predicate.ProyectoPredicateResolver;
import org.crue.hercules.sgi.csp.repository.predicate.ProyectoProyectoSgePredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaEntidadConvocanteSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoProyectoSgeSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSpecifications;
import org.crue.hercules.sgi.csp.service.ContextoProyectoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPartidaService;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ProyectoEquipoService;
import org.crue.hercules.sgi.csp.service.ProyectoFacturacionService;
import org.crue.hercules.sgi.csp.service.ProyectoPartidaService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoService;
import org.crue.hercules.sgi.csp.service.ProyectoResponsableEconomicoService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioEquipoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoPagoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.PeriodDateUtil;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio implementación para la gestión de {@link Proyecto}.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ProyectoServiceImpl implements ProyectoService {

  /**
   * Valor por defecto del atributo ajena en la copia de entidades financiadoras
   */
  private static final Boolean DEFAULT_COPY_ENTIDAD_FINANCIADORA_AJENA_VALUE = Boolean.FALSE;

  private final SgiConfigProperties sgiConfigProperties;
  private final ProyectoRepository repository;
  private final EstadoProyectoRepository estadoProyectoRepository;
  private final ModeloUnidadRepository modeloUnidadRepository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository;
  private final ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService;
  private final ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository;
  private final ProyectoEntidadConvocanteService proyectoEntidadConvocanteService;
  private final ConvocatoriaEntidadGestoraRepository convocatoriaEntidadGestoraRepository;
  private final ProyectoEntidadGestoraService proyectoEntidadGestoraService;
  private final ContextoProyectoService contextoProyectoService;
  private final ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository;
  private final ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService;
  private final SolicitudRepository solicitudRepository;
  private final SolicitudProyectoRepository solicitudProyectoRepository;
  private final SolicitudModalidadRepository solicitudModalidadRepository;
  private final SolicitudProyectoEquipoRepository solicitudEquipoRepository;
  private final ProyectoEquipoService proyectoEquipoService;
  private final SolicitudProyectoSocioRepository solicitudSocioRepository;
  private final ProyectoSocioService proyectoSocioService;
  private final SolicitudProyectoSocioEquipoRepository solicitudEquipoSocioRepository;
  private final ProyectoSocioEquipoService proyectoEquipoSocioService;
  private final SolicitudProyectoSocioPeriodoPagoRepository solicitudPeriodoPagoRepository;
  private final ProyectoSocioPeriodoPagoService proyectoSocioPeriodoPagoService;
  private final SolicitudProyectoSocioPeriodoJustificacionRepository solicitudPeriodoJustificacionRepository;
  private final ProyectoSocioPeriodoJustificacionService proyectoSocioPeriodoJustificacionService;
  private final ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;
  private final SolicitudProyectoEntidadFinanciadoraAjenaRepository solicitudProyectoEntidadFinanciadoraAjenaRepository;
  private final ProyectoAreaConocimientoRepository proyectoAreaConocimientoRepository;
  private final ProyectoClasificacionRepository proyectoClasificacionRepository;
  private final SolicitudProyectoAreaConocimientoRepository solicitudProyectoAreaConocimientoRepository;
  private final SolicitudProyectoClasificacionRepository solicitudProyectoClasificacionRepository;
  private final ProgramaRepository programaRepository;
  private final ProyectoProrrogaRepository proyectoProrrogaRepository;
  private final ProyectoPartidaService proyectoPartidaService;
  private final ConvocatoriaPartidaService convocatoriaPartidaService;
  private final ProyectoIVARepository proyectoIVARepository;
  private final ProyectoProyectoSgeRepository proyectoProyectoSGERepository;
  private final ProyectoConceptoGastoService proyectoConceptoGastoService;
  private final ProyectoConceptoGastoCodigoEcService proyectoConceptoGastoCodigoEcService;
  private final ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;
  private final SolicitudProyectoResponsableEconomicoRepository solicitudProyectoResponsableEconomicoRepository;
  private final ProyectoResponsableEconomicoService proyectoResponsableEconomicoService;
  private final Validator validator;
  private final ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository;
  private final ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository;
  private final EstadoProyectoPeriodoJustificacionRepository estadoProyectoPeriodoJustificacionRepository;
  private final ProyectoFacturacionService proyectoFacturacionService;
  private final ProyectoHelper proyectoHelper;

  public ProyectoServiceImpl(SgiConfigProperties sgiConfigProperties, ProyectoRepository repository,
      EstadoProyectoRepository estadoProyectoRepository, ModeloUnidadRepository modeloUnidadRepository,
      ConvocatoriaRepository convocatoriaRepository,
      ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository,
      ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService,
      ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository,
      ProyectoEntidadConvocanteService proyectoEntidadConvocanteService,
      ConvocatoriaEntidadGestoraRepository convocatoriaEntidadGestoraRepository,
      ProyectoEntidadGestoraService proyectoEntidadGestoraService,
      ContextoProyectoService contextoProyectoService,
      ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository,
      ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService, SolicitudRepository solicitudRepository,
      SolicitudProyectoRepository solicitudProyectoRepository,
      SolicitudModalidadRepository solicitudModalidadRepository,
      SolicitudProyectoEquipoRepository solicitudEquipoRepository, ProyectoEquipoService proyectoEquipoService,
      SolicitudProyectoSocioRepository solicitudSocioRepository, ProyectoSocioService proyectoSocioService,
      SolicitudProyectoSocioEquipoRepository solicitudEquipoSocioRepository,
      ProyectoSocioEquipoService proyectoEquipoSocioService,
      SolicitudProyectoSocioPeriodoPagoRepository solicitudPeriodoPagoRepository,
      ProyectoSocioPeriodoPagoService proyectoSocioPeriodoPagoService,
      SolicitudProyectoSocioPeriodoJustificacionRepository solicitudPeriodoJustificacionRepository,
      ProyectoSocioPeriodoJustificacionService proyectoSocioPeriodoJustificacionService,
      ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository,
      SolicitudProyectoEntidadFinanciadoraAjenaRepository solicitudProyectoEntidadFinanciadoraAjenaRepository,
      ProyectoProrrogaRepository proyectoProrrogaRepository,
      ProyectoAreaConocimientoRepository proyectoAreaConocimientoRepository,
      ProyectoClasificacionRepository proyectoClasificacionRepository,
      SolicitudProyectoAreaConocimientoRepository solicitudProyectoAreaConocimientoRepository,
      SolicitudProyectoClasificacionRepository solicitudProyectoClasificacionRepository,
      ProgramaRepository programaRepository, ProyectoPartidaService proyectoPartidaService,
      ConvocatoriaPartidaService convocatoriaPartidaService, ProyectoIVARepository proyectoIVARepository,
      ProyectoProyectoSgeRepository proyectoProyectoSGERepository,
      ProyectoConceptoGastoService proyectoConceptoGastoService,
      ProyectoConceptoGastoCodigoEcService proyectoConceptoGastoCodigoEcService,
      ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository,
      SolicitudProyectoResponsableEconomicoRepository solicitudProyectoResponsableEconomicoRepository,
      ProyectoResponsableEconomicoService proyectoResponsableEconomicoService, Validator validator,
      ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository,
      ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository,
      EstadoProyectoPeriodoJustificacionRepository estadoProyectoPeriodoJustificacionRepository,
      ProyectoFacturacionService proyectoFacturacionService,
      ProyectoHelper proyectoHelper) {

    this.sgiConfigProperties = sgiConfigProperties;
    this.repository = repository;
    this.estadoProyectoRepository = estadoProyectoRepository;
    this.modeloUnidadRepository = modeloUnidadRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.convocatoriaEntidadFinanciadoraRepository = convocatoriaEntidadFinanciadoraRepository;
    this.proyectoEntidadFinanciadoraService = proyectoEntidadFinanciadoraService;
    this.convocatoriaEntidadConvocanteRepository = convocatoriaEntidadConvocanteRepository;
    this.proyectoEntidadConvocanteService = proyectoEntidadConvocanteService;
    this.contextoProyectoService = contextoProyectoService;
    this.convocatoriaPeriodoSeguimientoCientificoRepository = convocatoriaPeriodoSeguimientoCientificoRepository;
    this.proyectoPeriodoSeguimientoService = proyectoPeriodoSeguimientoService;
    this.convocatoriaEntidadGestoraRepository = convocatoriaEntidadGestoraRepository;
    this.proyectoEntidadGestoraService = proyectoEntidadGestoraService;
    this.solicitudRepository = solicitudRepository;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
    this.solicitudModalidadRepository = solicitudModalidadRepository;
    this.solicitudEquipoRepository = solicitudEquipoRepository;
    this.proyectoEquipoService = proyectoEquipoService;
    this.solicitudSocioRepository = solicitudSocioRepository;
    this.proyectoSocioService = proyectoSocioService;
    this.solicitudEquipoSocioRepository = solicitudEquipoSocioRepository;
    this.proyectoEquipoSocioService = proyectoEquipoSocioService;
    this.solicitudPeriodoPagoRepository = solicitudPeriodoPagoRepository;
    this.proyectoSocioPeriodoPagoService = proyectoSocioPeriodoPagoService;
    this.solicitudPeriodoJustificacionRepository = solicitudPeriodoJustificacionRepository;
    this.proyectoSocioPeriodoJustificacionService = proyectoSocioPeriodoJustificacionService;
    this.convocatoriaConceptoGastoRepository = convocatoriaConceptoGastoRepository;
    this.solicitudProyectoEntidadFinanciadoraAjenaRepository = solicitudProyectoEntidadFinanciadoraAjenaRepository;
    this.proyectoAreaConocimientoRepository = proyectoAreaConocimientoRepository;
    this.proyectoClasificacionRepository = proyectoClasificacionRepository;
    this.solicitudProyectoAreaConocimientoRepository = solicitudProyectoAreaConocimientoRepository;
    this.solicitudProyectoClasificacionRepository = solicitudProyectoClasificacionRepository;
    this.programaRepository = programaRepository;
    this.proyectoProrrogaRepository = proyectoProrrogaRepository;
    this.proyectoPartidaService = proyectoPartidaService;
    this.convocatoriaPartidaService = convocatoriaPartidaService;
    this.proyectoIVARepository = proyectoIVARepository;
    this.proyectoProyectoSGERepository = proyectoProyectoSGERepository;
    this.proyectoConceptoGastoService = proyectoConceptoGastoService;
    this.proyectoConceptoGastoCodigoEcService = proyectoConceptoGastoCodigoEcService;
    this.convocatoriaConceptoGastoCodigoEcRepository = convocatoriaConceptoGastoCodigoEcRepository;
    this.solicitudProyectoResponsableEconomicoRepository = solicitudProyectoResponsableEconomicoRepository;
    this.proyectoResponsableEconomicoService = proyectoResponsableEconomicoService;
    this.validator = validator;
    this.convocatoriaPeriodoJustificacionRepository = convocatoriaPeriodoJustificacionRepository;
    this.proyectoPeriodoJustificacionRepository = proyectoPeriodoJustificacionRepository;
    this.estadoProyectoPeriodoJustificacionRepository = estadoProyectoPeriodoJustificacionRepository;
    this.proyectoFacturacionService = proyectoFacturacionService;
    this.proyectoHelper = proyectoHelper;
  }

  /**
   * Guarda la entidad {@link Proyecto}.
   *
   * @param proyecto la entidad {@link Proyecto} a guardar.
   * @return proyecto la entidad {@link Proyecto} persistida.
   */
  @Override
  @Transactional
  public Proyecto create(Proyecto proyecto) {
    log.debug("create(Proyecto proyecto) - start");
    Assert.isNull(proyecto.getId(), "Proyecto id tiene que ser null para crear un Proyecto");

    Assert.isTrue(SgiSecurityContextHolder.hasAuthorityForUO("CSP-PRO-C", proyecto.getUnidadGestionRef()),
        "La Unidad de Gestión no es gestionable por el usuario");

    proyecto.setActivo(Boolean.TRUE);

    this.validarDatos(proyecto, EstadoProyecto.Estado.BORRADOR);

    // Crea el proyecto
    repository.save(proyecto);

    // Crea el estado inicial del proyecto
    EstadoProyecto estadoProyecto = addEstadoProyecto(proyecto, EstadoProyecto.Estado.BORRADOR, null);

    proyecto.setEstado(estadoProyecto);

    // Crea el proyecto iva del proyecto si el porcentaje de IVA es cero o superior
    ProyectoIVA proyectoIVA = null;
    if (proyecto.getIva() != null && proyecto.getIva().getIva() != null) {
      proyectoIVA = addProyectoIVA(proyecto);
    }
    proyecto.setIva(proyectoIVA);

    // Actualiza el estado actual del proyecto con el nuevo estado
    Proyecto returnValue = repository.save(proyecto);

    // Si hay asignada una convocatoria se deben de rellenar las entidades
    // correspondientes con los datos de la convocatoria
    if (proyecto.getConvocatoriaId() != null) {
      this.copyDatosConvocatoriaToProyecto(proyecto);
    }

    log.debug("create(Proyecto proyecto) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link Proyecto}.
   *
   * @param proyectoActualizar proyectoActualizar {@link Proyecto} con los datos
   *                           actualizados.
   * @return {@link Proyecto} actualizado.
   */
  @Override
  @Transactional
  public Proyecto update(Proyecto proyectoActualizar) {
    log.debug("update(Proyecto proyecto) - start");

    return repository.findById(proyectoActualizar.getId()).map(data -> {
      proyectoHelper.checkCanAccessProyecto(data);
      Assert.isTrue(
          proyectoActualizar.getEstado().getId().equals(data.getEstado().getId())
              && ((proyectoActualizar.getConvocatoriaId() == null && data.getConvocatoriaId() == null)
                  || (proyectoActualizar.getConvocatoriaId() != null && data.getConvocatoriaId() != null
                      && proyectoActualizar.getConvocatoriaId().equals(data.getConvocatoriaId())))
              && ((proyectoActualizar.getSolicitudId() == null && data.getSolicitudId() == null)
                  || (proyectoActualizar.getSolicitudId() != null && data.getSolicitudId() != null
                      && proyectoActualizar.getSolicitudId().equals(data.getSolicitudId()))),
          "Existen campos del proyecto modificados que no se pueden modificar");

      data.setAcronimo(proyectoActualizar.getAcronimo());
      data.setAmbitoGeografico(proyectoActualizar.getAmbitoGeografico());
      data.setAnualidades(proyectoActualizar.getAnualidades());
      data.setClasificacionCVN(proyectoActualizar.getClasificacionCVN());
      data.setCodigoExterno(proyectoActualizar.getCodigoExterno());
      data.setCoordinado(proyectoActualizar.getCoordinado());
      data.setColaborativo(proyectoActualizar.getColaborativo());
      data.setConfidencial(proyectoActualizar.getConfidencial());
      data.setConvocatoriaExterna(proyectoActualizar.getConvocatoriaExterna());
      data.setCoordinadorExterno(proyectoActualizar.getCoordinadorExterno());
      data.setFechaFin(proyectoActualizar.getFechaFin());
      data.setFechaInicio(proyectoActualizar.getFechaInicio());
      data.setFinalidad(proyectoActualizar.getFinalidad());
      data.setImportePresupuestoCostesIndirectos(proyectoActualizar.getImportePresupuestoCostesIndirectos());
      data.setImporteConcedidoCostesIndirectos(proyectoActualizar.getImporteConcedidoCostesIndirectos());

      // Crea o actualiza el proyecto iva del proyecto si el porcentaje de IVA es cero
      // o superior

      if ((proyectoActualizar.getIva() != null && data.getIva() == null)
          || (proyectoActualizar.getIva() != null && data.getIva() != null)
              && (!Objects.equals(proyectoActualizar.getIva().getIva(), data.getIva().getIva()))) {
        ProyectoIVA proyectoIVA = updateProyectoIVA(data, proyectoActualizar);
        data.setIva(proyectoIVA);
      }

      // Si no se informa IVA igual o superior a 0 se elimina la causa de exención
      if (proyectoActualizar.getIva() != null && proyectoActualizar.getIva().getIva() != null
          && proyectoActualizar.getIva().getIva().equals(0)) {
        data.setCausaExencion(proyectoActualizar.getCausaExencion());
      } else {
        data.setCausaExencion(null);
      }
      data.setModeloEjecucion(proyectoActualizar.getModeloEjecucion());
      data.setObservaciones(proyectoActualizar.getObservaciones());
      data.setPermitePaquetesTrabajo(proyectoActualizar.getPermitePaquetesTrabajo());
      data.setExcelencia(proyectoActualizar.getExcelencia());
      data.setTitulo(proyectoActualizar.getTitulo());
      data.setUnidadGestionRef(proyectoActualizar.getUnidadGestionRef());
      data.setImportePresupuesto(proyectoActualizar.getImportePresupuesto());
      data.setImportePresupuestoSocios(proyectoActualizar.getImportePresupuestoSocios());
      data.setImporteConcedido(proyectoActualizar.getImporteConcedido());
      data.setImporteConcedidoSocios(proyectoActualizar.getImporteConcedidoSocios());
      data.setTotalImporteConcedido(proyectoActualizar.getTotalImporteConcedido());
      data.setTotalImportePresupuesto(proyectoActualizar.getTotalImportePresupuesto());

      this.validarDatos(data, data.getEstado().getEstado());

      List<ProyectoEquipo> equipos = null;
      if (data.getFechaFinDefinitiva() == null && proyectoActualizar.getFechaFinDefinitiva() != null) {
        // Si se informa por primera vez la fecha fin definitiva del proyecto, se
        // actualizan todos los equipos cuya fecha de fin sea igual a la del proyecto
        equipos = getEquiposUpdateFechaFinProyectoEquipo(data.getId(), data.getFechaFin(),
            proyectoActualizar.getFechaFinDefinitiva());
      } else if (data.getFechaFinDefinitiva() != null && proyectoActualizar.getFechaFinDefinitiva() != null
          && !data.getFechaFinDefinitiva().equals(proyectoActualizar.getFechaFinDefinitiva())) {
        // Si la fecha de fin definitiva del proyecto cambia de valor, se actualizan
        // todos los equipos cuya fecha de fin sea igual a la fecha definitiva del
        // proyecto
        equipos = getEquiposUpdateFechaFinProyectoEquipo(data.getId(), data.getFechaFinDefinitiva(),
            proyectoActualizar.getFechaFinDefinitiva());
      } else if (data.getFechaFinDefinitiva() != null && proyectoActualizar.getFechaFinDefinitiva() == null) {
        // Si la fecha de fin definitiva del proyecto cambia de valor a nulo
        equipos = getEquiposUpdateFechaFinProyectoEquipo(data.getId(), data.getFechaFinDefinitiva(),
            proyectoActualizar.getFechaFin());
      }
      data.setFechaFinDefinitiva(proyectoActualizar.getFechaFinDefinitiva());

      Proyecto returnValue = repository.save(data);
      if (!CollectionUtils.isEmpty(equipos)) {
        proyectoEquipoService.update(data.getId(), equipos);
      }
      log.debug("update(Proyecto proyecto) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoNotFoundException(proyectoActualizar.getId()));
  }

  /**
   * Se obtienen los {@link ProyectoEquipo} con las fechas de inicio y fin
   * adaptadas a la nueva fecha de fin
   *
   * @param proyectoId          identificador del {@link Proyecto}
   * @param fechaFinMaxPrevious fecha fin previa maxima para los
   *                            {@link ProyectoEquipo} del {@link Proyecto}
   * @param fechaFinNew         fecha fin nueva para actualizar el
   *                            {@link ProyectoEquipo}
   * @return la lista con los miembros del equipo del {@link Proyecto} con las
   *         fechas adaptadas a la nueva fecha de fin
   */
  private List<ProyectoEquipo> getEquiposUpdateFechaFinProyectoEquipo(Long proyectoId, Instant fechaFinMaxPrevious,
      Instant fechaFinNew) {
    return proyectoEquipoService.findAllByProyectoId(proyectoId).stream().map(miembroEquipo -> {
      if (miembroEquipo.getFechaInicio() != null && miembroEquipo.getFechaInicio().compareTo(fechaFinNew) > 0) {
        // La fecha de inicio nunca puede ser superior a la de fin
        miembroEquipo.setFechaInicio(fechaFinNew);
      }

      if (miembroEquipo.getFechaFin() != null
          && (miembroEquipo.getFechaFin().compareTo(fechaFinNew) > 0 || miembroEquipo
              .getFechaFin().compareTo(fechaFinMaxPrevious) == 0)) {
        // Se actualizan con la nueva fecha fin maxima los miembros con fecha fin igual
        // a la fecha fin maxima previa y los que tengan una fecha de fin superior a la
        // nueva fecha de fin
        miembroEquipo.setFechaFin(fechaFinNew);
      }

      return miembroEquipo;
    }).collect(Collectors.toList());
  }

  /**
   * Reactiva el {@link Proyecto}.
   *
   * @param id Id del {@link Proyecto}.
   * @return la entidad {@link Proyecto} persistida.
   */
  @Override
  @Transactional
  public Proyecto enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "Proyecto id no puede ser null para reactivar un Proyecto");

    return repository.findById(id).map(proyecto -> {
      Assert.isTrue(SgiSecurityContextHolder.hasAuthorityForUO("CSP-PRO-R", proyecto.getUnidadGestionRef()),
          "El proyecto pertenece a una Unidad de Gestión no gestionable por el usuario");

      if (proyecto.getActivo().booleanValue()) {
        // Si esta activo no se hace nada
        return proyecto;
      }

      proyecto.setActivo(true);

      Proyecto returnValue = repository.save(proyecto);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoNotFoundException(id));
  }

  /**
   * Desactiva el {@link Proyecto}.
   *
   * @param id Id del {@link Proyecto}.
   * @return la entidad {@link Proyecto} persistida.
   */
  @Override
  @Transactional
  public Proyecto disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "Proyecto id no puede ser null para desactivar un Proyecto");

    return repository.findById(id).map(proyecto -> {
      Assert.isTrue(SgiSecurityContextHolder.hasAuthorityForUO("CSP-PRO-B", proyecto.getUnidadGestionRef()),
          "El proyecto pertenece a una Unidad de Gestión no gestionable por el usuario");

      if (!proyecto.getActivo().booleanValue()) {
        // Si no esta activo no se hace nada
        return proyecto;
      }

      proyecto.setActivo(false);

      Proyecto returnValue = repository.save(proyecto);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoNotFoundException(id));
  }

  /**
   * Obtiene una entidad {@link Proyecto} por id.
   *
   * @param id Identificador de la entidad {@link Proyecto}.
   * @return Proyecto la entidad {@link Proyecto}.
   */
  @Override
  public Proyecto findById(Long id) {
    log.debug("findById(Long id) - start");
    final Proyecto returnValue = repository.findById(id).orElseThrow(() -> new ProyectoNotFoundException(id));
    proyectoHelper.checkCanAccessProyecto(returnValue);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Proyecto} por id.
   * Sin hacer comprobaciones de la Unidad de Gestión.
   *
   * @param id Identificador de la entidad {@link Proyecto}.
   * @return Proyecto la entidad {@link Proyecto}.
   */
  @Override
  public Proyecto findProyectoResumenById(Long id) {
    log.debug("findById(Long id) - start");
    final Proyecto returnValue = repository.findById(id).orElseThrow(() -> new ProyectoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Proyecto} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<Proyecto> findAllRestringidos(String query, Pageable paging) {
    log.debug("findAllRestringidos(String query, Pageable paging) - start");

    Specification<Proyecto> specs = ProyectoSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query,
        ProyectoPredicateResolver.getInstance(programaRepository, proyectoProrrogaRepository, sgiConfigProperties)));

    // No tiene acceso a todos los UO
    List<String> unidadesGestion = proyectoHelper.getUserUOsProyecto();

    if (!CollectionUtils.isEmpty(unidadesGestion)) {
      Specification<Proyecto> specByUnidadGestionRefIn = ProyectoSpecifications.unidadGestionRefIn(unidadesGestion);
      specs = specs.and(specByUnidadGestionRefIn);
    }

    Page<Proyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAllRestringidos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Proyecto} activas, que no estén en estado
   * borrador, en las que el usuario logueado está dentro del equipo o es un
   * responsable economico,
   * paginadas y filtradas
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<Proyecto> findAllActivosInvestigador(String query, Pageable paging) {
    log.debug("findAllActivosInvestigador(String query, Pageable paging) - start");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Specification<Proyecto> specs = ProyectoSpecifications.distinct().and(ProyectoSpecifications.activos()
        .and((ProyectoSpecifications.byInvestigadorId(authentication.getName()))
            .or(ProyectoSpecifications.byResponsableEconomicoId(authentication.getName())))
        .and(ProyectoSpecifications.byEstadoNotBorrador())
        .and(SgiRSQLJPASupport.toSpecification(query,
            ProyectoPredicateResolver.getInstance(programaRepository, proyectoProrrogaRepository,
                sgiConfigProperties))));

    Page<Proyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAllActivosInvestigador(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Proyecto} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Proyecto} paginadas y filtradas.
   */
  @Override
  public Page<Proyecto> findAllTodosRestringidos(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<Proyecto> specs = ProyectoSpecifications.distinct()
        .and(SgiRSQLJPASupport.toSpecification(query,
            ProyectoPredicateResolver.getInstance(programaRepository, proyectoProrrogaRepository,
                sgiConfigProperties)));

    List<String> unidadesGestion = SgiSecurityContextHolder
        .getUOsForAnyAuthority(new String[] { "CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E", "CSP-PRO-B", "CSP-PRO-R" });

    if (!CollectionUtils.isEmpty(unidadesGestion)) {
      Specification<Proyecto> specByUnidadGestionRefIn = ProyectoSpecifications.unidadGestionRefIn(unidadesGestion);
      specs = specs.and(specByUnidadGestionRefIn);
    }

    Page<Proyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Añade el nuevo {@link EstadoProyecto} y actualiza la {@link Proyecto} con
   * dicho estado.
   *
   * @param proyecto           la {@link Proyecto} para la que se añade el nuevo
   *                           estado.
   * @param tipoEstadoProyecto El nuevo {@link EstadoProyecto.Estado} de la
   *                           {@link Proyecto}.
   * @return la {@link Proyecto} con el estado actualizado.
   */
  private EstadoProyecto addEstadoProyecto(Proyecto proyecto, EstadoProyecto.Estado tipoEstadoProyecto,
      String comentario) {
    log.debug(
        "addEstadoProyecto(Proyecto proyecto, TipoEstadoProyectoEnum tipoEstadoProyecto, String comentario) - start");

    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setEstado(tipoEstadoProyecto);
    estadoProyecto.setProyectoId(proyecto.getId());
    estadoProyecto.setComentario(comentario);
    estadoProyecto.setFechaEstado(Instant.now());

    EstadoProyecto returnValue = estadoProyectoRepository.save(estadoProyecto);

    log.debug(
        "addEstadoProyecto(Proyecto proyecto, TipoEstadoProyectoEnum tipoEstadoProyecto, String comentario) - end");
    return returnValue;
  }

  /**
   * Añade el nuevo {@link ProyectoIVA} y actualiza la {@link Proyecto} con dicho
   * ProyectoIVA.
   * 
   * @param proyecto la {@link Proyecto} para la que se añade el nuevo estado.
   * @return la {@link Proyecto} con el estado actualizado.
   */
  private ProyectoIVA addProyectoIVA(Proyecto proyecto) {
    log.debug("addProyectoIVA(Proyecto proyecto) - start");

    ProyectoIVA proyectoIVA = new ProyectoIVA();
    proyectoIVA.setProyectoId(proyecto.getId());
    proyectoIVA.setIva(proyecto.getIva().getIva());
    proyectoIVA.setFechaInicio(proyecto.getFechaInicio());
    proyectoIVA.setFechaFin(null);
    proyectoIVA.setProyectoId(proyecto.getId());

    Set<ConstraintViolation<ProyectoIVA>> result = validator.validate(proyectoIVA);
    if (!result.isEmpty()) {
      throw new ConstraintViolationException(result);
    }

    ProyectoIVA returnValue = proyectoIVARepository.save(proyectoIVA);

    log.debug("addProyectoIVA(Proyecto proyecto) - end");
    return returnValue;
  }

  /**
   * Actualiza {@link ProyectoIVA} y actualiza la {@link Proyecto} con dicho
   * proyectoIVA.
   * 
   * @param proyectoGuardado    la {@link Proyecto} proyecto sin actualizar
   * 
   * @param proyectoActualizado la {@link Proyecto} proyecto a actualizar con el
   *                            nuevo proyecto iva
   * @return la {@link Proyecto} con el estado actualizado.
   */
  private ProyectoIVA updateProyectoIVA(Proyecto proyectoGuardado, Proyecto proyectoActualizado) {
    log.debug("updateProyectoIVA(Proyecto data, Proyecto proyectoActualizado) - start");

    // Creamos un nuevo proyecto IVA
    ProyectoIVA newProyectoIVA = new ProyectoIVA();
    newProyectoIVA.setProyectoId(proyectoActualizado.getId());
    if (proyectoActualizado.getIva() != null && proyectoActualizado.getIva().getIva() != null) {
      newProyectoIVA.setIva(proyectoActualizado.getIva().getIva());
    } else {
      newProyectoIVA.setIva(null);
    }
    newProyectoIVA.setFechaInicio(Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).plusDays(1)
        .withHour(00).withMinute(00).withSecond(00).toInstant());
    newProyectoIVA.setFechaFin(null);
    newProyectoIVA.setProyectoId(proyectoActualizado.getId());

    newProyectoIVA = proyectoIVARepository.save(newProyectoIVA);

    Set<ConstraintViolation<ProyectoIVA>> result = validator.validate(newProyectoIVA);
    if (!result.isEmpty()) {
      throw new ConstraintViolationException(result);
    }

    // Validar que no haya proyecto proyectosge vinculados, y si los hay que el
    // porcentaje de IVA sea mayor que cero
    if (newProyectoIVA.getIva() != null && newProyectoIVA.getIva().equals(0)) {
      Boolean hasProyectosSgeVinculados = proyectoProyectoSGERepository.existsByProyectoId(proyectoGuardado.getId());
      if (hasProyectosSgeVinculados.booleanValue()) {
        throw new ProyectoIVAException();
      }
    }

    // Al antiguo proyecto IVA le ponemos de fecha de fin actual
    ProyectoIVA oldProyectoIVA = proyectoGuardado.getIva();
    if (oldProyectoIVA != null && oldProyectoIVA.getIva() != null) {
      oldProyectoIVA.setFechaFin(Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).withHour(23)
          .withMinute(59).withSecond(59).toInstant());
      proyectoIVARepository.save(oldProyectoIVA);
    }

    log.debug("updateProyectoIVA(Proyecto data, Proyecto proyectoActualizado) - end");
    return newProyectoIVA;
  }

  /**
   * Se comprueba que los datos a guardar cumplan las validaciones oportunas
   * 
   * @param proyecto datos del proyecto
   * @param estado   estado del proyecto
   * 
   */
  private void validarDatos(Proyecto proyecto, Estado estado) {
    if (proyecto.getConvocatoriaId() != null) {
      Assert.isTrue(convocatoriaRepository.existsById(proyecto.getConvocatoriaId()),
          "La convocatoria con id '" + proyecto.getConvocatoriaId() + "' no existe");
    }

    if (proyecto.getFechaInicio() != null && proyecto.getFechaFin() != null) {
      Assert.isTrue(proyecto.getFechaFin().isAfter(proyecto.getFechaInicio()),
          "La fecha de fin debe ser posterior a la fecha de inicio");
    }

    // ModeloEjecucion correcto
    Optional<ModeloUnidad> modeloUnidad = modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(
        proyecto.getModeloEjecucion().getId(), proyecto.getUnidadGestionRef());

    Assert.isTrue(modeloUnidad.isPresent(), "ModeloEjecucion '" + proyecto.getModeloEjecucion().getNombre()
        + "' no disponible para la UnidadGestion " + proyecto.getUnidadGestionRef());

    // FechasFin after than última prórroga
    Optional<ProyectoProrroga> prorroga = proyectoProrrogaRepository
        .findFirstByProyectoIdOrderByFechaConcesionDesc(proyecto.getId());

    if (prorroga.isPresent() && !Tipo.IMPORTE.equals(prorroga.get().getTipo())
        && proyecto.getFechaFinDefinitiva() != null && prorroga.get().getFechaFin() != null) {
      Assert.isTrue(
          proyecto.getFechaFinDefinitiva().isAfter(prorroga.get().getFechaFin())
              || proyecto.getFechaFinDefinitiva().equals(prorroga.get().getFechaFin()),
          "La fecha de fin definitiva debe ser posterior a la fecha de fin de la última prórroga");
    }

    // Validación de campos obligatorios según estados. Solo aplicaría en el
    // actualizar ya que en el crear el estado siempre será "Borrador"
    this.checkCamposObligatoriosPorEstado(proyecto, estado);

    // Validación de datos IVA
    if (proyecto.getIva() != null && proyecto.getIva().getIva() != null && proyecto.getCausaExencion() != null) {
      Assert.isTrue(proyecto.getCausaExencion() != null,
          "El campo causa exención no puede tener valor si el porcentaje de IVA no es '0'");
    }
  }

  /**
   * Copia todos los datos de la {@link Convocatoria} al {@link Proyecto}
   *
   * @param proyecto la entidad {@link Proyecto}
   */
  private void copyDatosConvocatoriaToProyecto(Proyecto proyecto) {
    this.copyEntidadesFinanciadoras(proyecto.getId(), proyecto.getConvocatoriaId());
    this.copyEntidadesGestoras(proyecto);
    this.copyEntidadesConvocantesDeConvocatoria(proyecto.getId(), proyecto.getConvocatoriaId());
    this.copyAreaTematica(proyecto);
    this.copyPeriodoSeguimiento(proyecto);
    this.copyConfiguracionEconomica(proyecto, proyecto.getConvocatoriaId());
  }

  /**
   * Copia la informaci&oacute;n de EntidadesConvocantes de la Convocatoria en el
   * Proyecto
   *
   * @param proyectoId     Identificador del {@link Proyecto}
   * @param convocatoriaId Identificador de la {@link Convocatoria}
   */
  private void copyEntidadesConvocantesDeConvocatoria(Long proyectoId, Long convocatoriaId) {
    log.debug("copiarEntidadesConvocatesDeConvocatoria(Proyecto proyecto, Convocatoria convocatoria) - start");
    Specification<ConvocatoriaEntidadConvocante> specByConvocatoriaId = ConvocatoriaEntidadConvocanteSpecifications
        .byConvocatoriaId(convocatoriaId);

    List<ConvocatoriaEntidadConvocante> convocatoriaEntidadConvocantes = convocatoriaEntidadConvocanteRepository
        .findAll(specByConvocatoriaId);

    for (ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante : convocatoriaEntidadConvocantes) {
      if (proyectoEntidadConvocanteService.existsByProyectoIdAndEntidadRef(proyectoId,
          convocatoriaEntidadConvocante.getEntidadRef())) {
        ProyectoEntidadConvocante proyectoEntidadConvocante = proyectoEntidadConvocanteService
            .findByProyectoIdAndEntidadRef(proyectoId, convocatoriaEntidadConvocante.getEntidadRef());
        proyectoEntidadConvocante.setProyectoId(proyectoId);
        proyectoEntidadConvocante.setEntidadRef(convocatoriaEntidadConvocante.getEntidadRef());
        proyectoEntidadConvocante.setProgramaConvocatoria(convocatoriaEntidadConvocante.getPrograma());
        proyectoEntidadConvocanteService.update(proyectoEntidadConvocante);
      } else {
        ProyectoEntidadConvocante proyectoEntidadConvocante = new ProyectoEntidadConvocante();
        proyectoEntidadConvocante.setProyectoId(proyectoId);
        proyectoEntidadConvocante.setEntidadRef(convocatoriaEntidadConvocante.getEntidadRef());
        proyectoEntidadConvocante.setProgramaConvocatoria(convocatoriaEntidadConvocante.getPrograma());
        proyectoEntidadConvocanteService.create(proyectoEntidadConvocante);
      }
    }

    log.debug("copiarEntidadesConvocatesDeConvocatoria(Proyecto proyecto, Convocatoria convocatoria) - end");
  }

  /**
   * Copia la entidad área temática de una convocatoria a unproyecto
   *
   * @param proyecto la entidad {@link Proyecto}
   */
  private void copyAreaTematica(Proyecto proyecto) {

    if (Boolean.FALSE.equals(contextoProyectoService.existsByProyecto(proyecto.getId()))) {
      ContextoProyecto contextoProyectoNew = new ContextoProyecto();
      contextoProyectoNew.setProyectoId(proyecto.getId());
      contextoProyectoService.create(contextoProyectoNew);
    } else {
      ContextoProyecto contextoProyectoUpdate = contextoProyectoService.findByProyecto(proyecto.getId());
      contextoProyectoService.update(contextoProyectoUpdate, proyecto.getId());
    }
  }

  /**
   * Copia las entidades financiadores de una convocatoria a un proyecto
   *
   * @param proyectoId     Identificador del proyecto de destino
   * @param convocatoriaId Identificador de la convocatoria
   */
  private void copyEntidadesFinanciadoras(Long proyectoId, Long convocatoriaId) {
    log.debug("copyEntidadesFinanciadoras(Long proyectoId, Long convocatoriaId) - start");
    List<ConvocatoriaEntidadFinanciadora> entidadesConvocatoria = convocatoriaEntidadFinanciadoraRepository
        .findByConvocatoriaId(convocatoriaId);
    entidadesConvocatoria.stream().forEach(entidadConvocatoria -> {
      log.debug("Copy ConvocatoriaEntidadFinanciadora with id: {}", entidadConvocatoria.getId());
      ProyectoEntidadFinanciadora entidadProyecto = new ProyectoEntidadFinanciadora();
      entidadProyecto.setProyectoId(proyectoId);
      entidadProyecto.setEntidadRef(entidadConvocatoria.getEntidadRef());
      entidadProyecto.setFuenteFinanciacion(entidadConvocatoria.getFuenteFinanciacion());
      entidadProyecto.setTipoFinanciacion(entidadConvocatoria.getTipoFinanciacion());
      entidadProyecto.setPorcentajeFinanciacion(entidadConvocatoria.getPorcentajeFinanciacion());
      entidadProyecto.setImporteFinanciacion(entidadConvocatoria.getImporteFinanciacion());
      entidadProyecto.setAjena(DEFAULT_COPY_ENTIDAD_FINANCIADORA_AJENA_VALUE);

      this.proyectoEntidadFinanciadoraService.create(entidadProyecto);
    });
    log.debug("copyEntidadesFinanciadoras(Long proyectoId, Long convocatoriaId) - end");
  }

  /**
   * Copia los periodos de seguimiento de una convocatoria a un proyecto
   *
   * @param proyecto El proyecto de destino
   */
  private void copyPeriodoSeguimiento(Proyecto proyecto) {

    log.debug("copyPeriodoSeguimiento(Proyecto proyecto) - start");

    convocatoriaPeriodoSeguimientoCientificoRepository
        .findAllByConvocatoriaIdOrderByMesInicial(proyecto.getConvocatoriaId()).forEach(convocatoriaSeguimiento -> {

          log.debug("Copy ConvocatoriaPeriodoSeguimientoCientifico with id: {}", convocatoriaSeguimiento.getId());

          ProyectoPeriodoSeguimiento.ProyectoPeriodoSeguimientoBuilder projectBuilder = ProyectoPeriodoSeguimiento
              .builder();
          projectBuilder.numPeriodo(convocatoriaSeguimiento.getNumPeriodo())
              .tipoSeguimiento(convocatoriaSeguimiento.getTipoSeguimiento()).proyectoId(proyecto.getId())
              .fechaInicio(PeriodDateUtil.calculateFechaInicioPeriodo(proyecto.getFechaInicio(),
                  convocatoriaSeguimiento.getMesInicial(), sgiConfigProperties.getTimeZone()))
              .fechaFin(PeriodDateUtil.calculateFechaFinPeriodo(proyecto.getFechaInicio(),
                  convocatoriaSeguimiento.getMesFinal(), proyecto.getFechaFin(), sgiConfigProperties.getTimeZone()));

          if (convocatoriaSeguimiento.getFechaInicioPresentacion() != null) {
            projectBuilder.fechaInicioPresentacion(convocatoriaSeguimiento.getFechaInicioPresentacion());
          }
          if (convocatoriaSeguimiento.getFechaFinPresentacion() != null) {
            projectBuilder.fechaFinPresentacion(convocatoriaSeguimiento.getFechaFinPresentacion());
          }
          projectBuilder.observaciones(convocatoriaSeguimiento.getObservaciones());

          projectBuilder.convocatoriaPeriodoSeguimientoId(convocatoriaSeguimiento.getId());
          ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = projectBuilder.build();
          // Solamente crearemos el ProyectoPeriodoSeguimiento si sus fechas calculadas
          // están dentro de las fechas del proyecto
          if (proyectoPeriodoSeguimiento.getFechaInicio() == null || proyecto.getFechaFin() == null
              || (proyectoPeriodoSeguimiento.getFechaInicio() != null && proyecto.getFechaFin() != null
                  && !proyectoPeriodoSeguimiento.getFechaInicio().isAfter(proyecto.getFechaFin()))) {
            this.proyectoPeriodoSeguimientoService.create(proyectoPeriodoSeguimiento);
          }
        });

    log.debug("copyPeriodoSeguimiento(Proyecto proyecto) - end");
  }

  /**
   * Copia las entidades gestoras de una convocatoria a un proyecto
   *
   * @param proyecto la entidad {@link Proyecto}
   */
  private void copyEntidadesGestoras(Proyecto proyecto) {
    log.debug("copyEntidadesGestoras(Long proyectoId, Long convocatoriaId) - start");
    List<ConvocatoriaEntidadGestora> entidadesConvocatoria = convocatoriaEntidadGestoraRepository
        .findAllByConvocatoriaId(proyecto.getConvocatoriaId());
    entidadesConvocatoria.stream().forEach(entidadConvocatoria -> {
      log.debug("Copy copyEntidadesGestoras with id: {}", entidadConvocatoria.getId());
      ProyectoEntidadGestora entidadProyecto = new ProyectoEntidadGestora();
      entidadProyecto.setProyectoId(proyecto.getId());
      entidadProyecto.setEntidadRef(entidadConvocatoria.getEntidadRef());
      this.proyectoEntidadGestoraService.create(entidadProyecto);
    });
    log.debug("copyEntidadesGestoras(Long proyectoId, Long convocatoriaId) - end");
  }

  /**
   * Copia los datos generales del {@link SolicitudProyecto} al {@link Proyecto}
   *
   * @param proyecto          la entidad {@link Proyecto}
   * @param solicitudProyecto la entidad {@link SolicitudProyecto}
   * @return la entidad {@link Proyecto} con los nuevos datos
   */
  private Proyecto copyDatosGeneralesSolicitudProyectoToProyecto(Proyecto proyecto,
      SolicitudProyecto solicitudProyecto) {
    log.debug(
        "copyDatosGeneralesSolicitudProyectoToProyecto(Proyecto proyecto, SolicitudProyecto solicitudProyecto) - start");
    proyecto.setAcronimo(solicitudProyecto.getAcronimo());
    proyecto.setCodigoExterno(solicitudProyecto.getCodExterno());
    proyecto.setColaborativo(solicitudProyecto.getColaborativo());
    proyecto.setCoordinado(solicitudProyecto.getCoordinado());
    proyecto.setCoordinadorExterno(solicitudProyecto.getCoordinadorExterno());
    log.debug(
        "copyDatosGeneralesSolicitudProyectoToProyecto(Proyecto proyecto, SolicitudProyecto solicitudProyecto) - end");
    return proyecto;
  }

  /**
   * Copia los datos generales de la {@link Solicitud} al {@link Proyecto}
   *
   * @param proyecto  la entidad {@link Proyecto}
   * @param solicitud la entidad {@link Solicitud}
   * @return la entidad {@link Proyecto} con los nuevos datos
   */
  private Proyecto copyDatosGeneralesSolicitudToProyecto(Proyecto proyecto, Solicitud solicitud) {
    log.debug(
        "copyDatosGeneralesSolicitudToProyecto(Proyecto proyecto, Solicitud solicitud) - start");
    proyecto.setSolicitudId(solicitud.getId());
    proyecto.setConvocatoriaId(solicitud.getConvocatoriaId());
    proyecto.setUnidadGestionRef(solicitud.getUnidadGestionRef());
    if (solicitud.getConvocatoriaId() != null) {
      Convocatoria convocatoria = convocatoriaRepository.findById(solicitud.getConvocatoriaId())
          .orElseThrow(() -> new ConvocatoriaNotFoundException(solicitud.getConvocatoriaId()));
      proyecto.setFinalidad(convocatoria.getFinalidad());
      proyecto.setAmbitoGeografico(convocatoria.getAmbitoGeografico());
      proyecto.setClasificacionCVN(convocatoria.getClasificacionCVN());
      proyecto.setExcelencia(convocatoria.getExcelencia());
    } else {
      proyecto.setConvocatoriaExterna(solicitud.getConvocatoriaExterna());
    }
    log.debug(
        "copyDatosGeneralesSolicitudToProyecto(Proyecto proyecto, Solicitud solicitud) - end");
    return proyecto;
  }

  /**
   * Copia todos los datos de la {@link Solicitud} al {@link Proyecto}
   *
   * @param proyecto          la entidad {@link Proyecto}
   * @param solicitudProyecto la entidad {@link SolicitudProyecto}
   */
  private void copyDatosSolicitudToProyecto(Proyecto proyecto, SolicitudProyecto solicitudProyecto) {
    log.debug(
        "copyDatosSolicitudToProyecto(Proyecto proyecto, SolicitudProyecto solicitudProyecto) - start");
    this.copyContexto(proyecto, solicitudProyecto);
    this.copyAreasConocimiento(proyecto, solicitudProyecto.getId());
    this.copyClasificaciones(proyecto, solicitudProyecto.getId());
    this.copyEntidadesConvocantesDeSolicitud(proyecto);
    this.copyEntidadesFinanciadorasDeSolicitud(proyecto, solicitudProyecto.getId());
    this.copyMiembrosEquipo(proyecto, solicitudProyecto.getId());
    this.copySocios(proyecto, solicitudProyecto.getId());
    this.copyResponsablesEconomicos(proyecto, solicitudProyecto.getId());
    log.debug(
        "copyDatosSolicitudToProyecto(Proyecto proyecto, SolicitudProyecto solicitudProyecto) - end");
  }

  /**
   * Copia el los datos {@link ContextoProyecto} de la entidad
   * {@link SolicitudProyecto} al {@link Proyecto}
   *
   * @param proyecto          la entidad {@link Proyecto}
   * @param solicitudProyecto la entidad {@link SolicitudProyecto}
   * @return la entidad {@link Proyecto} con los nuevos datos
   */
  private void copyContexto(Proyecto proyecto, SolicitudProyecto solicitudProyecto) {
    log.debug("copyContexto(Proyecto proyecto, SolicitudProyecto solicitudProyecto) - start");
    ContextoProyecto contextoProyectoNew = new ContextoProyecto();
    contextoProyectoNew.setProyectoId(proyecto.getId());
    contextoProyectoNew.setObjetivos(solicitudProyecto.getObjetivos());
    contextoProyectoNew.setResultadosPrevistos(solicitudProyecto.getResultadosPrevistos());
    contextoProyectoNew.setIntereses(solicitudProyecto.getIntereses());
    contextoProyectoNew.setAreaTematica(solicitudProyecto.getAreaTematica());

    contextoProyectoService.create(contextoProyectoNew);
    log.debug("copyContexto(Proyecto proyecto, SolicitudProyecto solicitudProyecto) - end");
  }

  /**
   * Copia las áreas de conocimiento de una {@link Solicitud} a un
   * {@link Proyecto}
   *
   * @param proyecto            entidad {@link Proyecto}
   * @param solicitudProyectoId id de la {@link SolicitudProyecto}
   */
  private void copyAreasConocimiento(Proyecto proyecto, Long solicitudProyectoId) {
    log.debug("ccopyAreasConocimiento(Proyecto proyecto, Long solicitudProyectoId) - start");
    List<SolicitudProyectoAreaConocimiento> areasConocimineto = solicitudProyectoAreaConocimientoRepository
        .findAllBySolicitudProyectoId(solicitudProyectoId);
    areasConocimineto.stream().forEach(areaConocimentoSolicitud -> {
      log.debug("Copy SolicitudProyectoAreaConocimiento with id: {}", areaConocimentoSolicitud.getId());
      ProyectoAreaConocimiento areaConocimientoProyecto = new ProyectoAreaConocimiento();
      areaConocimientoProyecto.setProyectoId(proyecto.getId());
      areaConocimientoProyecto.setAreaConocimientoRef(areaConocimentoSolicitud.getAreaConocimientoRef());

      this.proyectoAreaConocimientoRepository.save(areaConocimientoProyecto);
    });
    log.debug("copyAreasConocimiento(Proyecto proyecto, Long solicitudProyectoId) - end");
  }

  /**
   * Copia las Clasificaciones de una {@link Solicitud} a un {@link Proyecto}
   *
   * @param proyecto            entidad {@link Proyecto}
   * @param solicitudProyectoId id de la {@link SolicitudProyecto}
   */
  private void copyClasificaciones(Proyecto proyecto, Long solicitudProyectoId) {
    log.debug("copyClasificaciones(Proyecto proyecto, Long solicitudProyectoId) - start");
    List<SolicitudProyectoClasificacion> clasificaciones = solicitudProyectoClasificacionRepository
        .findAllBySolicitudProyectoId(solicitudProyectoId);
    clasificaciones.stream().forEach(clasificacionSolicitud -> {
      log.debug("Copy SolicitudProyectoClasificacion with id: {}", clasificacionSolicitud.getId());
      ProyectoClasificacion clasificacionProyecto = new ProyectoClasificacion();
      clasificacionProyecto.setProyectoId(proyecto.getId());
      clasificacionProyecto.setClasificacionRef(clasificacionSolicitud.getClasificacionRef());

      this.proyectoClasificacionRepository.save(clasificacionProyecto);
    });
    log.debug("copyClasificaciones(Proyecto proyecto, Long solicitudProyectoId) - end");
  }

  /**
   * Copia las entidades convocantes de una {@link Solicitud} a un
   * {@link Proyecto}
   *
   * @param proyecto entidad {@link Proyecto}
   */
  private void copyEntidadesConvocantesDeSolicitud(Proyecto proyecto) {
    log.debug("copyEntidadesConvocantesDeSolicitud(Proyecto proyecto) - start");
    List<SolicitudModalidad> entidadesSolicitud = solicitudModalidadRepository
        .findAllBySolicitudId(proyecto.getSolicitudId());
    entidadesSolicitud.stream().forEach(entidadSolicitud -> {
      log.debug("Copy SolicitudModalidad with id: {}", entidadSolicitud.getId());
      ProyectoEntidadConvocante entidadProyecto = new ProyectoEntidadConvocante();
      entidadProyecto.setProyectoId(proyecto.getId());
      entidadProyecto.setPrograma(entidadSolicitud.getPrograma());
      entidadProyecto.setEntidadRef(entidadSolicitud.getEntidadRef());

      this.proyectoEntidadConvocanteService.create(entidadProyecto);
    });
    log.debug("copyEntidadesConvocantesDeSolicitud(Proyecto proyecto) - end");
  }

  /**
   * Copia las entidades financiadoras de una {@link Solicitud} a un
   * {@link Proyecto}
   *
   * @param proyecto entidad {@link Proyecto}
   */
  private void copyEntidadesFinanciadorasDeSolicitud(Proyecto proyecto, Long solicitudProyectoId) {
    log.debug("copyEntidadesFinanciadorasDeSolicitud(Proyecto proyecto, Long solicitudProyectoId) - start");
    List<SolicitudProyectoEntidadFinanciadoraAjena> entidadesSolicitud = solicitudProyectoEntidadFinanciadoraAjenaRepository
        .findAllBySolicitudProyectoId(solicitudProyectoId);
    entidadesSolicitud.stream().forEach(entidadSolicitud -> {
      log.debug("Copy SolicitudProyectoEntidadFinanciadoraAjena with id: {}", entidadSolicitud.getId());
      ProyectoEntidadFinanciadora entidadProyecto = new ProyectoEntidadFinanciadora();
      entidadProyecto.setProyectoId(proyecto.getId());
      entidadProyecto.setEntidadRef(entidadSolicitud.getEntidadRef());
      entidadProyecto.setFuenteFinanciacion(entidadSolicitud.getFuenteFinanciacion());
      entidadProyecto.setTipoFinanciacion(entidadSolicitud.getTipoFinanciacion());
      entidadProyecto.setPorcentajeFinanciacion(entidadSolicitud.getPorcentajeFinanciacion());
      entidadProyecto.setImporteFinanciacion(entidadSolicitud.getImporteFinanciacion());
      entidadProyecto.setAjena(Boolean.TRUE);

      this.proyectoEntidadFinanciadoraService.create(entidadProyecto);
    });
    log.debug("copyEntidadesFinanciadorasDeSolicitud(Proyecto proyecto, Long solicitudProyectoId) - end");
  }

  /**
   * Copia todos los miembros del equipo de una {@link Solicitud} a un
   * {@link Proyecto}
   *
   * @param proyecto entidad {@link Proyecto}
   */
  private void copyMiembrosEquipo(Proyecto proyecto, Long solicitudProyectoId) {

    log.debug("copyMiembrosEquipo(Proyecto proyecto) - start");

    List<ProyectoEquipo> proyectoEquipos = solicitudEquipoRepository.findAllBySolicitudProyectoId(solicitudProyectoId)
        .stream().map(solicitudProyectoEquipo -> {

          log.debug("Copy SolicitudProyectoEquipo with id: {}", solicitudProyectoEquipo.getId());

          ProyectoEquipo.ProyectoEquipoBuilder proyectoEquipoBuilder = ProyectoEquipo.builder();
          proyectoEquipoBuilder.proyectoId(proyecto.getId());
          if (solicitudProyectoEquipo.getMesInicio() != null) {
            proyectoEquipoBuilder.fechaInicio(PeriodDateUtil.calculateFechaInicioPeriodo(proyecto.getFechaInicio(),
                solicitudProyectoEquipo.getMesInicio(), sgiConfigProperties.getTimeZone()));
          }
          if (solicitudProyectoEquipo.getMesFin() != null) {
            proyectoEquipoBuilder.fechaFin(PeriodDateUtil.calculateFechaFinPeriodo(proyecto.getFechaInicio(),
                solicitudProyectoEquipo.getMesFin(), proyecto.getFechaFin(), sgiConfigProperties.getTimeZone()));
          }
          proyectoEquipoBuilder.rolProyecto(solicitudProyectoEquipo.getRolProyecto())
              .personaRef(solicitudProyectoEquipo.getPersonaRef());
          return proyectoEquipoBuilder.build();
        })
        // Solamente crearemos el ProyectoEquipo si sus fechas calculadas están dentro
        // de las fechas del proyecto
        .filter(proyectoEquipo -> proyectoEquipo.getFechaInicio() == null || proyecto.getFechaFin() == null
            || (proyectoEquipo.getFechaInicio() != null && proyecto.getFechaFin() != null
                && !proyectoEquipo.getFechaInicio().isAfter(proyecto.getFechaFin())))
        .collect(Collectors.toList());

    this.proyectoEquipoService.update(proyecto.getId(), proyectoEquipos);
    log.debug("copyMiembrosEquipo(Proyecto proyecto) - end");
  }

  /**
   * Copia los responsables economicos de una {@link Solicitud} a un
   * {@link Proyecto}. Los responsables que tengan una fecha de inicio posterior a
   * la fecha de fin del proyecto no se copian.
   * 
   * @param proyecto            entidad {@link Proyecto}
   * @param solicitudProyectoId Identificador del {@link SolicitudProyecto}
   */
  private void copyResponsablesEconomicos(Proyecto proyecto, Long solicitudProyectoId) {
    log.debug("copyResponsablesEconomicos(Proyecto proyecto) - start");

    List<ProyectoResponsableEconomico> responsablesEconomicosProyecto = solicitudProyectoResponsableEconomicoRepository
        .findAllBySolicitudProyectoId(solicitudProyectoId).stream().map(responsableEconomicoSolicitud -> {
          log.debug("Copy SolicitudProyectoResponsableEconomico with id: {}", responsableEconomicoSolicitud.getId());

          ProyectoResponsableEconomico proyectoResponsableEconomico = ProyectoResponsableEconomico.builder()
              .proyectoId(proyecto.getId()).personaRef(responsableEconomicoSolicitud.getPersonaRef()).build();

          if (responsableEconomicoSolicitud.getMesInicio() != null) {
            proyectoResponsableEconomico
                .setFechaInicio(PeriodDateUtil.calculateFechaInicioPeriodo(proyecto.getFechaInicio(),
                    responsableEconomicoSolicitud.getMesInicio(), sgiConfigProperties.getTimeZone()));
          }
          if (responsableEconomicoSolicitud.getMesFin() != null) {
            proyectoResponsableEconomico.setFechaFin(PeriodDateUtil.calculateFechaFinPeriodo(proyecto.getFechaInicio(),
                responsableEconomicoSolicitud.getMesFin(), proyecto.getFechaFin(), sgiConfigProperties.getTimeZone()));
          }
          return proyectoResponsableEconomico;
        })
        // Solamente crearemos el ResponsableEconomicoProyecto si sus fechas calculadas
        // están dentro de las fechas del proyecto
        .filter(responsableEconomicoProyecto -> responsableEconomicoProyecto.getFechaInicio() == null
            || proyecto.getFechaFin() == null
            || (responsableEconomicoProyecto.getFechaInicio() != null && proyecto.getFechaFin() != null
                && !responsableEconomicoProyecto.getFechaInicio().isAfter(proyecto.getFechaFin())))
        .collect(Collectors.toList());

    this.proyectoResponsableEconomicoService.updateProyectoResponsableEconomicos(proyecto.getId(),
        responsablesEconomicosProyecto);

    log.debug("copyResponsablesEconomicos(Proyecto proyecto) - end");
  }

  /**
   * Copia todos los socios de una {@link Solicitud} a un {@link Proyecto}
   *
   * @param proyecto entidad {@link Proyecto}
   */
  private void copySocios(Proyecto proyecto, Long solicitudProyectoId) {

    log.debug("copySocios(Proyecto proyecto) - start");

    solicitudSocioRepository.findAllBySolicitudProyectoId(solicitudProyectoId).stream().forEach(entidadSolicitud -> {

      log.debug("Copy SolicitudProyectoSocio with id: {}", entidadSolicitud.getId());

      ProyectoSocio proyectoSocio = createProyectoSocio(proyecto, entidadSolicitud);

      // Solamente crearemos el ProyectoSocio si sus fechas calculadas están dentro de
      // las fechas del proyecto
      if (proyectoSocio.getFechaInicio() == null || proyecto.getFechaFin() == null
          || (proyectoSocio.getFechaInicio() != null && proyecto.getFechaFin() != null
              && !proyectoSocio.getFechaInicio().isAfter(proyecto.getFechaFin()))) {
        ProyectoSocio proyectoSocioCreado = this.proyectoSocioService.create(proyectoSocio);

        copyProyectoEquipoSocio(proyecto, entidadSolicitud, proyectoSocioCreado);

        copyProyectoSocioPeriodoPago(proyecto, entidadSolicitud, proyectoSocioCreado);

        copyProyectoSocioPeriodoJusitificacion(proyecto, entidadSolicitud, proyectoSocioCreado);
      }
    });
    log.debug("copySocios(Proyecto proyecto) - end");
  }

  private ProyectoSocio createProyectoSocio(Proyecto proyecto, SolicitudProyectoSocio entidadSolicitud) {
    ProyectoSocio proyectoSocio = ProyectoSocio.builder().proyectoId(proyecto.getId())
        .rolSocio(entidadSolicitud.getRolSocio()).empresaRef(entidadSolicitud.getEmpresaRef())
        .importeConcedido(entidadSolicitud.getImporteSolicitado())
        .numInvestigadores(entidadSolicitud.getNumInvestigadores()).build();

    proyectoSocio.setFechaInicio(PeriodDateUtil.calculateFechaInicioPeriodo(proyecto.getFechaInicio(),
        entidadSolicitud.getMesInicio(), sgiConfigProperties.getTimeZone()));
    proyectoSocio.setFechaFin(PeriodDateUtil.calculateFechaFinPeriodo(proyecto.getFechaInicio(),
        entidadSolicitud.getMesFin(), proyecto.getFechaFin(), sgiConfigProperties.getTimeZone()));

    return proyectoSocio;
  }

  private void copyProyectoSocioPeriodoJusitificacion(Proyecto proyecto, SolicitudProyectoSocio entidadSolicitud,
      ProyectoSocio proyectoSocioCreado) {

    solicitudPeriodoJustificacionRepository.findAllBySolicitudProyectoSocioId(entidadSolicitud.getId()).stream()
        .forEach(entidadPeriodoJustificacionSolicitud -> {

          log.debug("Copy ProyectoSocioPeriodoJustificacion with id: {}",
              entidadPeriodoJustificacionSolicitud.getId());

          ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = ProyectoSocioPeriodoJustificacion
              .builder().proyectoSocioId(proyectoSocioCreado.getId())
              .fechaInicio(PeriodDateUtil.calculateFechaInicioPeriodo(proyecto.getFechaInicio(),
                  entidadPeriodoJustificacionSolicitud.getMesInicial(), sgiConfigProperties.getTimeZone()))
              .fechaFin(PeriodDateUtil.calculateFechaFinPeriodo(proyecto.getFechaInicio(),
                  entidadPeriodoJustificacionSolicitud.getMesFinal(), proyecto.getFechaFin(),
                  sgiConfigProperties.getTimeZone()))
              .numPeriodo(entidadPeriodoJustificacionSolicitud.getNumPeriodo())
              .observaciones(entidadPeriodoJustificacionSolicitud.getObservaciones())
              .fechaInicioPresentacion(entidadPeriodoJustificacionSolicitud.getFechaInicio())
              .fechaFinPresentacion(entidadPeriodoJustificacionSolicitud.getFechaFin()).build();

          // Solamente crearemos el ProyectoSocioPeriodoJustificacion si sus fechas
          // calculadas están dentro de las fechas del proyecto
          if (proyectoSocioPeriodoJustificacion.getFechaInicio() == null || proyecto.getFechaFin() == null
              || (proyectoSocioPeriodoJustificacion.getFechaInicio() != null && proyecto.getFechaFin() != null
                  && !proyectoSocioPeriodoJustificacion.getFechaInicio().isAfter(proyecto.getFechaFin()))) {
            this.proyectoSocioPeriodoJustificacionService.create(proyectoSocioPeriodoJustificacion);
          }
        });
  }

  private void copyProyectoSocioPeriodoPago(Proyecto proyecto, SolicitudProyectoSocio entidadSolicitud,
      ProyectoSocio proyectoSocio) {

    List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagos = solicitudPeriodoPagoRepository
        .findAllBySolicitudProyectoSocioId(entidadSolicitud.getId()).stream()
        .map(entidadPeriodoPagoSolicitud -> ProyectoSocioPeriodoPago.builder()
            .fechaPrevistaPago(PeriodDateUtil.calculateFechaInicioPeriodo(proyecto.getFechaInicio(),
                entidadPeriodoPagoSolicitud.getMes(), sgiConfigProperties.getTimeZone()))
            .importe(entidadPeriodoPagoSolicitud.getImporte()).numPeriodo(entidadPeriodoPagoSolicitud.getNumPeriodo())
            .proyectoSocioId(proyectoSocio.getId()).build())
        // Solamente crearemos el ProyectoSocioPeriodoPago si sus fechas calculadas
        // están dentro de las fechas del proyecto
        .filter(proyectoSocioPeriodoPago -> proyectoSocioPeriodoPago.getFechaPrevistaPago() == null
            || proyecto.getFechaFin() == null
            || (proyectoSocioPeriodoPago.getFechaPrevistaPago() != null && proyecto.getFechaFin() != null
                && !proyectoSocioPeriodoPago.getFechaPrevistaPago().isAfter(proyecto.getFechaFin())))
        .collect(Collectors.toList());
    this.proyectoSocioPeriodoPagoService.update(proyectoSocio.getId(), proyectoSocioPeriodoPagos);
  }

  private void copyProyectoEquipoSocio(Proyecto proyecto, SolicitudProyectoSocio entidadSolicitud,
      ProyectoSocio proyectoSocio) {

    List<ProyectoSocioEquipo> proyectoSocioEquipos = solicitudEquipoSocioRepository
        .findAllBySolicitudProyectoSocioId(entidadSolicitud.getId()).stream().map(entidadEquipoSolicitud -> {
          log.debug("Copy SolicitudProyectoSocioEquipo with id: {}", entidadEquipoSolicitud.getId());
          return ProyectoSocioEquipo.builder()
              .fechaInicio(PeriodDateUtil.calculateFechaInicioPeriodo(proyecto.getFechaInicio(),
                  entidadEquipoSolicitud.getMesInicio(), sgiConfigProperties.getTimeZone()))
              .fechaFin(PeriodDateUtil.calculateFechaFinPeriodo(proyecto.getFechaInicio(),
                  entidadEquipoSolicitud.getMesFin(), proyecto.getFechaFin(), sgiConfigProperties.getTimeZone()))

              .personaRef(entidadEquipoSolicitud.getPersonaRef()).rolProyecto(entidadEquipoSolicitud.getRolProyecto())
              .build();
        })
        // Solamente crearemos el ProyectoSocioEquipo si sus fechas calculadas
        // están dentro de las fechas del proyecto
        .filter(
            proyectoEquipoSocio -> proyectoEquipoSocio.getFechaInicio() == null || proyecto.getFechaFin() == null
                || (proyectoEquipoSocio.getFechaInicio() != null && proyecto.getFechaFin() != null
                    && !proyectoEquipoSocio.getFechaInicio().isAfter(proyecto.getFechaFin())))
        .collect(Collectors.toList());
    this.proyectoEquipoSocioService.update(proyectoSocio.getId(), proyectoSocioEquipos);
  }

  /**
   * Copia toda la configuración económica de una {@link Convocatoria} a un
   * {@link Proyecto}
   * 
   * @param proyecto       entidad {@link Proyecto}
   * @param convocatoriaId Identificador de la {@link Convocatoria}
   */
  private void copyConfiguracionEconomica(Proyecto proyecto, Long convocatoriaId) {
    log.debug("copyConfiguracionEconomica(Proyecto proyecto) - start");
    this.copyConceptosGasto(proyecto);
    this.copyPartidasPresupuestarias(proyecto.getId(), proyecto.getConvocatoriaId());
    this.copyPeriodosJustificacionFromConvocatoria(proyecto, convocatoriaId);
    log.debug("copyConfiguracionEconomica(Proyecto proyecto) - end");
  }

  /**
   * Copia toda los conceptos de gasto de una {@link Convocatoria} a un
   * {@link Proyecto}
   * 
   * @param proyecto entidad {@link Proyecto}
   */
  private void copyConceptosGasto(Proyecto proyecto) {
    log.debug("copyConceptosGasto(Proyecto proyecto) - start");
    List<ConvocatoriaConceptoGasto> conceptosGastoConvocatoria = convocatoriaConceptoGastoRepository
        .findAllByConvocatoriaIdAndConceptoGastoActivoTrue(proyecto.getConvocatoriaId());

    conceptosGastoConvocatoria.stream().forEach(conceptoGastoConvocatoria -> {
      log.debug("Copy ConvocatoriaConceptoGasto with id: {}", conceptoGastoConvocatoria.getId());
      ProyectoConceptoGasto conceptoGastoProyecto = new ProyectoConceptoGasto();
      conceptoGastoProyecto.setProyectoId(proyecto.getId());
      conceptoGastoProyecto.setConceptoGasto(conceptoGastoConvocatoria.getConceptoGasto());
      conceptoGastoProyecto.setImporteMaximo(conceptoGastoConvocatoria.getImporteMaximo());
      conceptoGastoProyecto.setPermitido(conceptoGastoConvocatoria.getPermitido());
      conceptoGastoProyecto.setObservaciones(conceptoGastoConvocatoria.getObservaciones());
      conceptoGastoProyecto.setConvocatoriaConceptoGastoId(conceptoGastoConvocatoria.getId());

      Instant fechaInicio = PeriodDateUtil.calculateFechaInicioPeriodo(proyecto.getFechaInicio(),
          conceptoGastoConvocatoria.getMesInicial(), sgiConfigProperties.getTimeZone());
      conceptoGastoProyecto.setFechaInicio(fechaInicio);

      Instant fechaFin = PeriodDateUtil.calculateFechaFinPeriodo(proyecto.getFechaInicio(),
          conceptoGastoConvocatoria.getMesFinal(), proyecto.getFechaFin(), sgiConfigProperties.getTimeZone());
      conceptoGastoProyecto.setFechaFin(fechaFin);

      // Solamente crearemos el ProyectoConceptoGasto si sus fechas
      // calculadas están dentro de las fechas del proyecto
      if (conceptoGastoProyecto.getFechaInicio() == null || proyecto.getFechaFin() == null
          || (conceptoGastoProyecto.getFechaInicio() != null && proyecto.getFechaFin() != null
              && !conceptoGastoProyecto.getFechaInicio().isAfter(proyecto.getFechaFin()))) {
        ProyectoConceptoGasto conceptoGastoCopiado = this.proyectoConceptoGastoService.create(conceptoGastoProyecto);
        this.copyConceptoGastoCodigosEc(conceptoGastoCopiado.getConvocatoriaConceptoGastoId(),
            conceptoGastoCopiado.getId(), proyecto);
      }

    });
    log.debug("copyConceptosGasto(Proyecto proyecto) - end");
  }

  /**
   * Copia todos los codigos economicos del concepto de gasto de la
   * {@link Convocatoria} a los del {@link Proyecto}
   * 
   * @param convocatoriaConceptoGastoId Identificador del
   *                                    {@link ConvocatoriaConceptoGasto}
   * @param proyectoConceptoGastoId     Identificador del
   *                                    {@link ProyectoConceptoGasto}
   * @param proyecto                    El {@link Proyecto} al que se copian los
   *                                    codigos economicos
   */
  private void copyConceptoGastoCodigosEc(Long convocatoriaConceptoGastoId, Long proyectoConceptoGastoId,
      Proyecto proyecto) {
    log.debug("copyConceptosGasto(Long convocatoriaConceptoGastoId, Long proyectoConceptoGastoId) - start");
    List<ConvocatoriaConceptoGastoCodigoEc> codigosEconomicosConceptosGastoConvocatoria = convocatoriaConceptoGastoCodigoEcRepository
        .findAllByConvocatoriaConceptoGastoId(convocatoriaConceptoGastoId);

    List<ProyectoConceptoGastoCodigoEc> proyectoConceptoGastoCodigoEcs = codigosEconomicosConceptosGastoConvocatoria
        .stream().map(codigoEconomicoConvocatoria -> {
          log.debug("Copy ConvocatoriaConceptoGastoCodigoEc with id: {}", codigoEconomicoConvocatoria.getId());
          ProyectoConceptoGastoCodigoEc codigoEconomicoProyecto = new ProyectoConceptoGastoCodigoEc();
          codigoEconomicoProyecto.setProyectoConceptoGastoId(proyectoConceptoGastoId);
          codigoEconomicoProyecto.setCodigoEconomicoRef(codigoEconomicoConvocatoria.getCodigoEconomicoRef());
          codigoEconomicoProyecto.setFechaInicio(codigoEconomicoConvocatoria.getFechaInicio());
          Instant fechaFin = codigoEconomicoConvocatoria.getFechaFin();
          Instant fechaFinProyecto = proyecto.getFechaFin();

          // La fechaFin nunca puede ser posterior a la fechaFin del proyecto
          if (fechaFin != null && fechaFinProyecto != null && fechaFin.isAfter(fechaFinProyecto)) {
            fechaFin = fechaFinProyecto;
          }
          codigoEconomicoProyecto.setFechaFin(fechaFin);
          codigoEconomicoProyecto.setObservaciones(codigoEconomicoConvocatoria.getObservaciones());
          codigoEconomicoProyecto.setConvocatoriaConceptoGastoCodigoEcId(codigoEconomicoConvocatoria.getId());

          return codigoEconomicoProyecto;
        })
        // Solamente crearemos el ProyectoConceptoGastoCodigoEc si sus fechas calculadas
        // están dentro de las fechas del proyecto
        .filter(codigoEconomicoProyecto -> codigoEconomicoProyecto.getFechaInicio() == null
            || proyecto.getFechaFin() == null
            || (codigoEconomicoProyecto.getFechaInicio() != null && proyecto.getFechaFin() != null
                && !codigoEconomicoProyecto.getFechaInicio().isAfter(proyecto.getFechaFin())))
        .collect(Collectors.toList());

    this.proyectoConceptoGastoCodigoEcService.update(proyectoConceptoGastoId, proyectoConceptoGastoCodigoEcs);
    log.debug("copyConceptosGasto(Long convocatoriaConceptoGastoId, Long proyectoConceptoGastoId) - end");
  }

  /**
   * Copia las partidas presupuestarias de una convocatoria a un proyecto
   *
   * @param proyectoId     Identificador del proyecto de destino
   * @param convocatoriaId Identificador de la convocatoria
   */
  private void copyPartidasPresupuestarias(Long proyectoId, Long convocatoriaId) {
    log.debug("copyPartidasPresupuestarias(Long proyectoId, Long convocatoriaId) - start");
    Page<ConvocatoriaPartida> partidasConvocatoria = convocatoriaPartidaService.findAllByConvocatoria(convocatoriaId,
        null, Pageable.unpaged());

    if (partidasConvocatoria != null && partidasConvocatoria.hasContent()) {
      partidasConvocatoria.getContent().stream().forEach(partidaConvocatoria -> {
        log.debug("Copy copyPartidasPresupuestarias with id: {}", partidaConvocatoria.getId());
        ProyectoPartida partidaProyecto = new ProyectoPartida();
        partidaProyecto.setProyectoId(proyectoId);
        partidaProyecto.setCodigo(partidaConvocatoria.getCodigo());
        partidaProyecto.setConvocatoriaPartidaId(partidaConvocatoria.getId());
        partidaProyecto.setDescripcion(partidaConvocatoria.getDescripcion());
        partidaProyecto.setTipoPartida(partidaConvocatoria.getTipoPartida());

        this.proyectoPartidaService.create(partidaProyecto);
      });
    }
    log.debug("copyPartidasPresupuestarias(Long proyectoId, Long convocatoriaId) - end");
  }

  /**
   * Se comprueba que los datos de la {@link Solicitud} a copiar para crear el
   * {@link Proyecto} cumplan las validaciones oportunas
   *
   * @param solicitud datos de la {@link Solicitud}
   */
  private void validarDatosSolicitud(Solicitud solicitud) {

    if (!solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA)
        && !solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA_PROVISIONAL)
        && !solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA_PROVISIONAL_ALEGADA)
        && !solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA_PROVISIONAL_NO_ALEGADA)) {
      throw new IllegalArgumentException(
          "La solicitud no se encuentra en un estado correcto para la creación del proyecto.");
    }

    Assert.isTrue(solicitud.getFormularioSolicitud().equals(FormularioSolicitud.PROYECTO)
        || solicitud.getFormularioSolicitud().equals(FormularioSolicitud.RRHH),
        "El formulario de la solicitud debe ser de tipo " + FormularioSolicitud.PROYECTO + " o "
            + FormularioSolicitud.RRHH);
  }

  /**
   * Guarda la entidad {@link Proyecto} a partir de los datos de la entidad
   * {@link Solicitud}.
   *
   * @param solicitudId identificador de la entidad {@link Solicitud} a copiar
   *                    datos.
   * @param proyecto    datos necesarios para crear el {@link Proyecto}
   * @return proyecto la entidad {@link Proyecto} persistida.
   */
  @Override
  @Transactional
  public Proyecto createProyectoBySolicitud(Long solicitudId, Proyecto proyecto) {
    log.debug("createProyectoBySolicitud(Long solicitudId, Proyecto proyecto) - start");
    AssertHelper.idIsNull(proyecto.getId(), Proyecto.class);
    proyectoHelper.checkCanCreateProyecto(proyecto);

    Solicitud solicitud = solicitudRepository.findById(solicitudId)
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudId));

    this.validarDatosSolicitud(solicitud);

    proyecto.setActivo(Boolean.TRUE);

    if (solicitud.getFormularioSolicitud().equals(FormularioSolicitud.PROYECTO)) {
      return createProyectoBySolicitudProyecto(solicitud, proyecto);
    } else if (solicitud.getFormularioSolicitud().equals(FormularioSolicitud.RRHH)) {
      return createProyectoBySolicitudRrhh(solicitud, proyecto);
    }

    return null;
  }

  /**
   * Crea un proyecto desde una solicitud de proyecto
   * 
   * @param solicitud datos de la {@link Solicitud} desde la que se crea
   * @param proyecto  datos necesarios para crear el {@link Proyecto}
   * @return proyecto la entidad {@link Proyecto} persistida.
   */
  private Proyecto createProyectoBySolicitudProyecto(Solicitud solicitud, Proyecto proyecto) {
    log.debug("createProyectoBySolicitudProyecto(Long solicitudId, Proyecto proyecto) - start");
    SolicitudProyecto solicitudProyecto = solicitudProyectoRepository.findById(solicitud.getId())
        .orElseThrow(() -> new SolicitudNotFoundException(solicitud.getId()));

    this.copyDatosGeneralesSolicitudToProyecto(proyecto, solicitud);
    this.copyDatosGeneralesSolicitudProyectoToProyecto(proyecto, solicitudProyecto);

    this.validarDatos(proyecto, EstadoProyecto.Estado.BORRADOR);

    // Crea el proyecto
    repository.save(proyecto);

    // Crea el estado inicial del proyecto
    EstadoProyecto estadoProyecto = addEstadoProyecto(proyecto, EstadoProyecto.Estado.BORRADOR, null);

    proyecto.setEstado(estadoProyecto);
    // Actualiza el estado actual del proyecto con el nuevo estado
    Proyecto returnValue = repository.save(proyecto);

    this.copyDatosSolicitudToProyecto(returnValue, solicitudProyecto);

    // Si hay asignada una convocatoria se deben de rellenar las entidades
    // correspondientes con los datos de la convocatoria
    if (proyecto.getConvocatoriaId() != null) {
      this.copyDatosConvocatoriaToProyecto(returnValue);
    }

    log.debug("createProyectoBySolicitudProyecto(Long solicitudId, Proyecto proyecto) - end");
    return returnValue;
  }

  /**
   * Crea un proyecto desde una solicitud de RRHH
   * 
   * @param solicitud datos de la {@link Solicitud} desde la que se crea
   * @param proyecto  datos necesarios para crear el {@link Proyecto}
   * @return proyecto la entidad {@link Proyecto} persistida.
   */
  private Proyecto createProyectoBySolicitudRrhh(Solicitud solicitud, Proyecto proyecto) {
    log.debug("createProyectoBySolicitudRrhh(Long solicitudId, Proyecto proyecto) - start");
    this.copyDatosGeneralesSolicitudToProyecto(proyecto, solicitud);

    this.validarDatos(proyecto, EstadoProyecto.Estado.BORRADOR);

    // Crea el proyecto
    repository.save(proyecto);

    // Crea el estado inicial del proyecto
    EstadoProyecto estadoProyecto = addEstadoProyecto(proyecto, EstadoProyecto.Estado.BORRADOR, null);

    proyecto.setEstado(estadoProyecto);
    // Actualiza el estado actual del proyecto con el nuevo estado
    Proyecto returnValue = repository.save(proyecto);

    createEmptyContexto(proyecto.getId());

    // Si hay asignada una convocatoria se deben de rellenar las entidades
    // correspondientes con los datos de la convocatoria
    if (proyecto.getConvocatoriaId() != null) {
      this.copyDatosConvocatoriaToProyecto(returnValue);
    }

    log.debug("createProyectoBySolicitudRrhh(Long solicitudId, Proyecto proyecto) - end");
    return returnValue;
  }

  /**
   * Copia el los datos {@link ContextoProyecto} de la entidad
   * {@link SolicitudProyecto} al {@link Proyecto}
   *
   * @param proyecto la entidad {@link Proyecto}
   * @return la entidad {@link Proyecto} con los nuevos datos
   */
  private void createEmptyContexto(Long proyectoId) {
    log.debug("createEmptyContexto(Long proyectoId) - start");
    ContextoProyecto contextoProyectoNew = new ContextoProyecto();
    contextoProyectoNew.setProyectoId(proyectoId);
    contextoProyectoService.create(contextoProyectoNew);
    log.debug("createEmptyContexto(Long proyectoId) - end");
  }

  /**
   * Se hace el cambio de estado de un proyecto.
   *
   * @param id Identificador de {@link Proyecto}.
   * @return {@link Proyecto} actualizado.
   */
  @Override
  @Transactional
  public Proyecto cambiarEstado(Long id, EstadoProyecto estadoProyecto) {

    log.debug("cambiarEstado(Long id, EstadoProyecto estadoProyecto) - start");

    Proyecto proyecto = repository.findById(id).orElseThrow(() -> new ProyectoNotFoundException(id));

    estadoProyecto.setProyectoId(proyecto.getId());

    // VALIDACIONES
    // Permisos
    proyectoHelper.checkUserHasAuthorityModifyProyecto(proyecto);

    // El nuevo estado es diferente al estado actual de del proyecto
    if (estadoProyecto.getEstado().equals(proyecto.getEstado().getEstado())) {
      throw new IllegalArgumentException("El proyecto ya se encuentra en el estado al que se quiere modificar.");
    }

    // Validaciones según el cambio de estado
    this.checkCamposObligatoriosPorEstado(proyecto, estadoProyecto.getEstado());

    // Cambio de fecha fin definitiva si el estado se va a modificar a RENUNCIADO o
    // RESCINDIDO
    Instant fechaActual = Instant.now();
    if (estadoProyecto.getEstado() == EstadoProyecto.Estado.RENUNCIADO
        || estadoProyecto.getEstado() == EstadoProyecto.Estado.RESCINDIDO) {

      Instant fechaFinNew = fechaActual.atZone(sgiConfigProperties.getTimeZone().toZoneId()).withHour(23)
          .withMinute(59).withSecond(59).withNano(0).toInstant();

      Instant fechaFinPrevious = proyecto.getFechaFinDefinitiva() != null ? proyecto.getFechaFinDefinitiva()
          : proyecto.getFechaFin();

      // La fecha debe actualizarse también para los miembros de los equipos.
      List<ProyectoEquipo> equiposActualizados = getEquiposUpdateFechaFinProyectoEquipo(proyecto.getId(),
          fechaFinPrevious, fechaFinNew);

      proyecto.setFechaFinDefinitiva(fechaFinNew);
      proyectoEquipoService.update(proyecto.getId(), equiposActualizados);
    }

    // Se cambia el estado del proyecto
    estadoProyecto.setFechaEstado(fechaActual);
    estadoProyecto = estadoProyectoRepository.save(estadoProyecto);
    proyecto.setEstado(estadoProyecto);

    Proyecto returnValue = repository.save(proyecto);

    log.debug("cambiarEstado(Long id, EstadoProyecto estadoProyecto) - end");
    return returnValue;
  }

  /**
   * Obtiene el {@link ProyectoPresupuestoTotales} de la {@link Solicitud}.
   * 
   * @param proyectoId Identificador de la entidad {@link Proyecto}.
   * @return {@link ProyectoPresupuestoTotales}.
   */
  @Override
  public ProyectoPresupuestoTotales getTotales(Long proyectoId) {
    log.debug("getTotales(Long proyectoId) - start");
    final ProyectoPresupuestoTotales returnValue = repository.getTotales(proyectoId);
    log.debug("getTotales(Long proyectoId) - end");
    return returnValue;
  }

  private void checkCamposObligatoriosPorEstado(Proyecto proyecto, Estado estado) {
    // Validación de campos obligatorios según estados. Solo aplicaría en el
    // actualizar ya que en el crear el estado siempre será "Borrador"
    if (estado != null && estado == EstadoProyecto.Estado.CONCEDIDO) {
      // En la validación del crear no pasará por aquí, aún no tendrá estado.
      Assert.isTrue(proyecto.getFinalidad() != null,
          "El campo finalidad debe ser obligatorio para el proyecto en estado 'CONCEDIDO'");

      Assert.isTrue(proyecto.getAmbitoGeografico() != null,
          "El campo ambitoGeografico debe ser obligatorio para el proyecto en estado 'CONCEDIDO'");

      Assert.isTrue(proyecto.getConfidencial() != null,
          "El campo confidencial debe ser obligatorio para el proyecto en estado 'CONCEDIDO'");

      Assert.isTrue(proyecto.getCoordinado() != null,
          "El campo Proyecto coordinado debe ser obligatorio para el proyecto en estado 'CONCEDIDO'");

      if (proyecto.getCoordinado() != null && proyecto.getCoordinado().booleanValue()) {
        Assert.isTrue(proyecto.getCoordinadorExterno() != null,
            "El campo coordinadorExterno debe ser obligatorio para el proyecto en estado 'CONCEDIDO'");
      }

      Assert.isTrue(proyecto.getPermitePaquetesTrabajo() != null,
          "El campo permitePaquetesTrabajo debe ser obligatorio para el proyecto en estado 'CONCEDIDO'");

      if (proyecto.getSolicitudId() != null) {
        Solicitud solicitud = solicitudRepository.findById(proyecto.getSolicitudId())
            .orElseThrow(() -> new SolicitudNotFoundException(proyecto.getSolicitudId()));

        if (solicitud.getFormularioSolicitud().equals(FormularioSolicitud.PROYECTO)) {
          List<ProyectoEquipo> equipos = proyectoEquipoService.findAllByProyectoId(proyecto.getId());

          if (equipos.stream().map(ProyectoEquipo::getPersonaRef)
              .noneMatch(personaRef -> personaRef.equals(solicitud.getSolicitanteRef()))) {
            throw new MissingInvestigadorPrincipalInProyectoEquipoException();
          }

        }
      }

    }
  }

  /**
   * Hace las comprobaciones necesarias para determinar si el {@link Proyecto}
   * puede ser modificado. También se utilizará para permitir la creación,
   * modificación o eliminación de ciertas entidades relacionadas con el
   * {@link Proyecto}.
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  @Override
  public boolean modificable(Long proyectoId) {
    log.debug("modificable(Long proyectoId) - start");
    boolean isModificable = proyectoHelper.hasUserAuthorityModifyProyecto(proyectoId);
    log.debug("modificable(Long proyectoId) - start");
    return isModificable;
  }

  /**
   * Obtiene los ids de {@link Proyecto} modificados que esten
   * activos y con {@link Proyecto#confidencial} a <code>false</code> que cumplan
   * las condiciones indicadas en el filtro de búsqueda
   *
   * @param query información del filtro.
   * @return el listado de ids de {@link Proyecto}.
   */
  @Override
  public List<Long> findIdsProyectosModificados(String query) {
    log.debug("findIdsProyectosModificados(String query) - start");

    Specification<Proyecto> specs = ProyectoSpecifications.activos().and(ProyectoSpecifications.confidencial(false))
        .and(SgiRSQLJPASupport.toSpecification(query,
            ProyectoPredicateResolver.getInstance(programaRepository, proyectoProrrogaRepository,
                sgiConfigProperties)));

    List<Long> returnValue = repository.findIds(specs);

    log.debug("findIdsProyectosModificados(String query) - end");

    return returnValue;
  }

  private void copyPeriodosJustificacionFromConvocatoria(Proyecto proyecto, Long convocatoriaId) {
    // @formatter:off
    this.proyectoPeriodoJustificacionRepository.saveAll(
      this.convocatoriaPeriodoJustificacionRepository.findAllByConvocatoriaId(convocatoriaId).stream()
        .filter(periodo -> checkIfFechaInicioIsInsideProyectoRange(proyecto, periodo))
        .map(periodo -> ProyectoPeriodoJustificacion.builder()
          .proyectoId(proyecto.getId())
          .numPeriodo(periodo.getNumPeriodo())
          .fechaInicioPresentacion(periodo.getFechaInicioPresentacion())
          .fechaFinPresentacion(periodo.getFechaFinPresentacion())
          .tipoJustificacion(periodo.getTipo())
          .observaciones(periodo.getObservaciones())
          .estado(createEstadoProyectoPeriodoJustificacionPendiente())
          .convocatoriaPeriodoJustificacionId(periodo.getId())
          .fechaInicio(PeriodDateUtil.calculateFechaInicioPeriodo(proyecto.getFechaInicio(),
              periodo.getMesInicial(), sgiConfigProperties.getTimeZone()))
          .fechaFin(resolveFechaFinFromPeriodoJustificacionConvocatoria(proyecto, periodo))
          .build()
        ).collect(Collectors.toList())
    );
    // @formatter: on
  }

  private EstadoProyectoPeriodoJustificacion createEstadoProyectoPeriodoJustificacionPendiente() {
    return this.estadoProyectoPeriodoJustificacionRepository.save(EstadoProyectoPeriodoJustificacion.builder()
      .estado(EstadoProyectoPeriodoJustificacion.TipoEstadoPeriodoJustificacion.PENDIENTE)
      .fechaEstado(Instant.now())
      .build());
  }

  private Instant resolveFechaFinFromPeriodoJustificacionConvocatoria(Proyecto proyecto, ConvocatoriaPeriodoJustificacion periodo) {
    Instant fechaFin = PeriodDateUtil.calculateFechaFinPeriodo(proyecto.getFechaInicio(),
      periodo.getMesFinal(), proyecto.getFechaFin(), sgiConfigProperties.getTimeZone());
    if(fechaFin.isAfter(proyecto.getFechaFin())){
      fechaFin = proyecto.getFechaFin();
    }
    return fechaFin;
  }

  private boolean checkIfFechaInicioIsInsideProyectoRange(Proyecto proyecto, ConvocatoriaPeriodoJustificacion periodo) {
    Instant fechaInicio = PeriodDateUtil.calculateFechaInicioPeriodo(proyecto.getFechaInicio(),
      periodo.getMesInicial(), sgiConfigProperties.getTimeZone());

    return (fechaInicio.isAfter(proyecto.getFechaInicio()) || fechaInicio.equals(proyecto.getFechaInicio()))
      && (fechaInicio.isBefore(proyecto.getFechaFin()));
  }

  /**
   *
   * @param proyectoId id del {@link Proyecto} del que cuelgan la lista de objetos {@link ProyectoFacturacion} a buscar
   * @param query información del filtro.
   * @param paging información de paginación
   * @return objeto {@link Page} con el listado de objetos de tipo {@link ProyectoFacturacion}
   */
  @Override
  public Page<ProyectoFacturacion> findAllProyectoFacturacionByProyectoId(Long proyectoId, String query, Pageable paging){
    return this.proyectoFacturacionService.findByProyectoId(proyectoId, paging);
  }

  /**
   * Devuelve una lista de ids de los objetos de tipo {@link Proyecto} que estñan asociados
   * con un objeto de tipo {@link Solicitud}
   * 
   * @param solicitudId id de la {@link Solicitud}
   * @return lista de ids de los objetos de tipo {@link Proyecto}
   */
  @Override
  public List<Long> findIdsBySolicitudId(Long solicitudId) {
    return this.repository.findIds(ProyectoSpecifications.bySolicitudId(solicitudId));
  }

  /**
   * Devuelve una lista de {@link ProyectoDto} que se incorporarán a la baremación
   * de producción científica
   * 
   * @param anioInicio año inicio de baremación
   * @param anioFin    año fin de baremación
   * 
   * @return Lista de {@link ProyectoDto}
   */
  @Override
  public List<ProyectoDto> findProyectosProduccionCientifica(Integer anioInicio, Integer anioFin){

    Instant fechaInicioBaremacion = PeriodDateUtil.calculateFechaInicioBaremacionByAnio(
        anioInicio, sgiConfigProperties.getTimeZone());

    Instant fechaFinBaremacion = PeriodDateUtil.calculateFechaFinBaremacionByAnio(
        anioFin, sgiConfigProperties.getTimeZone());

    return repository.findProyectosProduccionCientifica(fechaInicioBaremacion, fechaFinBaremacion);
  }
    
  /**  
   * Devuelve una lista paginada y filtrada {@link RelacionEjecucionEconomica} que se
   * encuentren dentro de la unidad de gestión del usuario logueado
   * 
   * @param query    filtro de búsqueda.
   * @param pageable {@link Pageable}.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} activas paginadas
   *         y filtradas.
   */
  @Override
  public Page<RelacionEjecucionEconomica> findRelacionesEjecucionEconomicaProyectos(String query, Pageable pageable) {
    log.debug("findRelacionesEjecucionEconomicaProyectos(String query, Pageable pageable) - start");

    Specification<ProyectoProyectoSge> specs = ProyectoProyectoSgeSpecifications.activos();
    if (query != null) {
      specs = specs.and(SgiRSQLJPASupport.toSpecification(query,  ProyectoProyectoSgePredicateResolver.getInstance(sgiConfigProperties)));
    }

    // No tiene acceso a todos los UO
    List<String> unidadesGestion = SgiSecurityContextHolder
        .getUOsForAnyAuthority(new String[] { "CSP-EJEC-V", "CSP-EJEC-E" });

    if (!CollectionUtils.isEmpty(unidadesGestion)) {
      Specification<ProyectoProyectoSge> specByUnidadGestionRefIn = ProyectoProyectoSgeSpecifications
          .unidadGestionRefIn(unidadesGestion);
      specs = specs.and(specByUnidadGestionRefIn);
    }

    Page<RelacionEjecucionEconomica> returnValue = proyectoProyectoSGERepository.findRelacionesEjecucionEconomica(specs, pageable);
    log.debug("findRelacionesEjecucionEconomicaProyectos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los datos de proyectos competitivos de las personas.
   *
   * @param personasRef Lista de id de las personas.
   * @param onlyAsRolPrincipal Indica si solo se comprueba la participacion con un rol principal
   * @param exludedProyectoId Excluye el {@link Proyecto} de la consulta
   * @return el {@link ProyectosCompetitivosPersonas}.
   */
  @Override
  public ProyectosCompetitivosPersonas getProyectosCompetitivosPersonas(List<String> personasRef, Boolean onlyAsRolPrincipal, Long exludedProyectoId) {
    log.debug("getProyectosCompetitivosPersonas(List<String> personasRef, Boolean onlyAsRolPrincipal, Long exludedProyectoId) - start");

    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    ProyectosCompetitivosPersonas proyectosCompetitivosPersona = ProyectosCompetitivosPersonas.builder()
        .numProyectosCompetitivos(
            repository.countProyectosClasificacionCvnPersonas(personasRef, ClasificacionCVN.COMPETITIVOS, onlyAsRolPrincipal, exludedProyectoId))
        .numProyectosCompetitivosActuales(
            repository.countProyectosClasificacionCvnPersonas(personasRef, ClasificacionCVN.COMPETITIVOS, onlyAsRolPrincipal, exludedProyectoId, fechaActual))
        .numProyectosNoCompetitivos(
            repository.countProyectosClasificacionCvnPersonas(personasRef, ClasificacionCVN.NO_COMPETITIVOS, onlyAsRolPrincipal, exludedProyectoId))
        .numProyectosNoCompetitivosActuales(
            repository.countProyectosClasificacionCvnPersonas(personasRef, ClasificacionCVN.NO_COMPETITIVOS, onlyAsRolPrincipal, exludedProyectoId, fechaActual))
        .build();

    log.debug("getProyectosCompetitivosPersonas(List<String> personasRef, Boolean onlyAsRolPrincipal, Long exludedProyectoId) - end");
    return proyectosCompetitivosPersona;
  }

  @Override
  public Page<ProyectoSeguimientoEjecucionEconomica> findProyectosSeguimientoEjecucionEconomica(String proyectoSgeRef, String query, Pageable pageable) {
    log.debug("findProyectosSeguimientoEjecucionEconomica(String query, Pageable pageable) - start");

    Specification<ProyectoProyectoSge> specs = ProyectoProyectoSgeSpecifications.activos()
    .and(ProyectoProyectoSgeSpecifications.byProyectoSgeRef(proyectoSgeRef));
    if (query != null) {
      specs = specs.and(SgiRSQLJPASupport.toSpecification(query, ProyectoProyectoSgePredicateResolver.getInstance(sgiConfigProperties)));
    }

    // No tiene acceso a todos los UO
    List<String> unidadesGestion = SgiSecurityContextHolder
        .getUOsForAnyAuthority(new String[] { "CSP-SJUS-V", "CSP-SJUS-E" });

    if (!CollectionUtils.isEmpty(unidadesGestion)) {
      Specification<ProyectoProyectoSge> specByUnidadGestionRefIn = ProyectoProyectoSgeSpecifications
          .unidadGestionRefIn(unidadesGestion);
      specs = specs.and(specByUnidadGestionRefIn);
    }

    Page<ProyectoSeguimientoEjecucionEconomica> returnValue = proyectoProyectoSGERepository.findProyectosSeguimientoEjecucionEconomica(specs, pageable);
    log.debug("findProyectosSeguimientoEjecucionEconomica(String query, Pageable pageable) - end");
    return returnValue;
  }

}
