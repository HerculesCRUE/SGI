package org.crue.hercules.sgi.csp.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSocioPeriodoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.SgdocService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link ProyectoSocioPeriodoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoSocioPeriodoJustificacionServiceImpl implements ProyectoSocioPeriodoJustificacionService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_FIELD_ACTION_MODIFICAR = "action.modificar";
  private static final String MSG_FIELD_FECHA_FIN_PROYECTO_SOCIO = "fechaFinProyectoSocio";
  private static final String MSG_FIELD_FECHA_FIN_PROYECTO_SOCIO_PERIODO_JUSTIFICACION = "fechaFinProyectoSocioPeriodoJustificacion";
  private static final String MSG_FIELD_FECHA_INICIO_PRESENTACION = "fechaInicioPresentacion";
  private static final String MSG_FIELD_FECHA_FIN_PRESENTACION = "fechaFinPresentacion";
  private static final String MSG_MODEL_PROYECTO_SOCIO = "org.crue.hercules.sgi.csp.model.ProyectoSocio.message";
  private static final String MSG_MODEL_PROYECTO_PERIODO_JUSTIFICACION = "org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion.message";
  private static final String MSG_PROBLEM_ACCION_DENEGADA = "org.springframework.util.Assert.accion.denegada.message";
  private static final String MSG_PROBLEM_DATE_OVERLOAP = "org.springframework.util.Assert.date.overloap.message";

  private final ProyectoSocioPeriodoJustificacionRepository repository;
  private final ProyectoSocioRepository proyectoSocioRepository;
  private final ProyectoRepository proyectoRepository;

  private final ProyectoSocioPeriodoJustificacionDocumentoRepository proyectoSocioPeriodoJustificacionDocumentoRepository;
  /** SGDOC service */
  private final SgdocService sgdocService;

  public ProyectoSocioPeriodoJustificacionServiceImpl(
      ProyectoSocioPeriodoJustificacionRepository proyectoSocioPeriodoJustificacionRepository,
      ProyectoSocioRepository proyectoSocioRepository,
      ProyectoSocioPeriodoJustificacionDocumentoRepository proyectoSocioPeriodoJustificacionDocumentoRepository,
      ProyectoRepository proyectoRepository, SgdocService sgdocService) {
    this.repository = proyectoSocioPeriodoJustificacionRepository;
    this.proyectoSocioRepository = proyectoSocioRepository;
    this.proyectoSocioPeriodoJustificacionDocumentoRepository = proyectoSocioPeriodoJustificacionDocumentoRepository;
    this.proyectoRepository = proyectoRepository;
    this.sgdocService = sgdocService;
  }

  /**
   * Actualiza el listado de {@link ProyectoSocioPeriodoJustificacion} de la
   * {@link ProyectoSocio} con el listado proyectoSocioPeriodoJustificaciones
   * eliminando los elementos segun proceda.
   *
   * @param proyectoSocioId                     Id de la {@link ProyectoSocio}.
   * @param proyectoSocioPeriodoJustificaciones lista con los nuevos
   *                                            {@link ProyectoSocioPeriodoJustificacion}
   *                                            a guardar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion} persistida.
   */
  @Override
  @Transactional
  public List<ProyectoSocioPeriodoJustificacion> delete(Long proyectoSocioId,
      List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificaciones) {
    log.debug(
        "delete(Long proyectoSocioId, List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificaciones) - start");

    if (!proyectoSocioRepository.existsById(proyectoSocioId)) {
      throw new ProyectoSocioNotFoundException(proyectoSocioId);
    }

    List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificacionesBD = repository
        .findAllByProyectoSocioId(proyectoSocioId);

    // Periodos eliminados
    List<ProyectoSocioPeriodoJustificacion> periodoJustificacionesEliminar = proyectoSocioPeriodoJustificacionesBD
        .stream().filter(periodo -> proyectoSocioPeriodoJustificaciones.stream()
            .map(ProyectoSocioPeriodoJustificacion::getId).noneMatch(id -> Objects.equals(id, periodo.getId())))
        .collect(Collectors.toList());

    if (!periodoJustificacionesEliminar.isEmpty()) {

      List<Long> periodoJustificacionId = periodoJustificacionesEliminar.stream()
          .map(ProyectoSocioPeriodoJustificacion::getId).collect(Collectors.toList());

      periodoJustificacionId.stream().forEach(id -> {
        List<ProyectoSocioPeriodoJustificacionDocumento> documentos = proyectoSocioPeriodoJustificacionDocumentoRepository
            .findAllByProyectoSocioPeriodoJustificacionId(id);
        if (!documentos.isEmpty()) {
          documentos.stream().forEach(documento -> {
            sgdocService.delete(documento.getDocumentoRef());
          });
        }
      });

      proyectoSocioPeriodoJustificacionDocumentoRepository
          .deleteByProyectoSocioPeriodoJustificacionIdIn(periodoJustificacionId);

      repository.deleteAll(periodoJustificacionesEliminar);
    }

    log.debug(
        "delete(Long proyectoSocioId, List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificaciones) - end");

    return proyectoSocioPeriodoJustificaciones;
  }

  /**
   * Actualiza el {@link ProyectoSocioPeriodoJustificacion}
   *
   * @param proyectoSocioPeriodoJustificacion {@link ProyectoSocioPeriodoJustificacion}
   *                                          a actualizar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion} persistida.
   */
  @Override
  @Transactional
  public ProyectoSocioPeriodoJustificacion update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion,
      Long proyectoSocioPeriodoJustificacionId) {
    log.debug("update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificaciones) - start");

    AssertHelper.idNotNull(proyectoSocioPeriodoJustificacionId, ProyectoSocioPeriodoJustificacion.class);

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacionExistente = repository
        .findById(proyectoSocioPeriodoJustificacionId)
        .orElseThrow(() -> new ProyectoSocioPeriodoJustificacionNotFoundException(proyectoSocioPeriodoJustificacionId));

    Specification<ProyectoSocioPeriodoJustificacion> specByIdNotEqual = ProyectoSocioPeriodoJustificacionSpecifications
        .byIdNotEqual(proyectoSocioPeriodoJustificacionId);

    Specification<ProyectoSocioPeriodoJustificacion> specs = Specification.where(specByIdNotEqual);
    List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificacionesBD = repository.findAll(specs);

    proyectoSocioPeriodoJustificacion.setId(proyectoSocioPeriodoJustificacionId);
    proyectoSocioPeriodoJustificacionesBD.add(proyectoSocioPeriodoJustificacion);

    Assert.isTrue(
        Objects.equals(proyectoSocioPeriodoJustificacionExistente.getProyectoSocioId(),
            proyectoSocioPeriodoJustificacion
                .getProyectoSocioId()),
        () -> ProblemMessage.builder()
            .key(MSG_PROBLEM_ACCION_DENEGADA)
            .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(
                MSG_MODEL_PROYECTO_SOCIO))
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                MSG_MODEL_PROYECTO_PERIODO_JUSTIFICACION))
            .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_MODIFICAR))
            .build());

    validateProyectoSocioPeriodoJustificacion(proyectoSocioPeriodoJustificacion);

    AtomicInteger numPeriodo = new AtomicInteger(0);

    // Ordena los periodos por fechaInicio
    proyectoSocioPeriodoJustificacionesBD.sort(Comparator.comparing(ProyectoSocioPeriodoJustificacion::getFechaInicio));

    for (ProyectoSocioPeriodoJustificacion periodoJustificacion : proyectoSocioPeriodoJustificacionesBD) {
      periodoJustificacion.setNumPeriodo(numPeriodo.incrementAndGet());
    }

    repository.saveAll(proyectoSocioPeriodoJustificacionesBD);

    log.debug("update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificaciones) - end");

    return proyectoSocioPeriodoJustificacion;
  }

  /**
   * Crea el {@link ProyectoSocioPeriodoJustificacion}
   *
   * @param proyectoSocioPeriodoJustificacion {@link ProyectoSocioPeriodoJustificacion}
   *                                          a actualizar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion} persistida.
   */
  @Override
  @Transactional
  public ProyectoSocioPeriodoJustificacion create(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) {
    log.debug("create(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificaciones) - start");

    AssertHelper.idIsNull(proyectoSocioPeriodoJustificacion.getId(), ProyectoSocioPeriodoJustificacion.class);

    // Validaciones
    validateProyectoSocioPeriodoJustificacion(proyectoSocioPeriodoJustificacion);

    List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificacionesBD = repository
        .findAllByProyectoSocioId(proyectoSocioPeriodoJustificacion.getProyectoSocioId());

    proyectoSocioPeriodoJustificacionesBD.add(proyectoSocioPeriodoJustificacion);

    AtomicInteger numPeriodo = new AtomicInteger(0);

    // Ordena los periodos por fechaInicio
    proyectoSocioPeriodoJustificacionesBD.sort(Comparator.comparing(ProyectoSocioPeriodoJustificacion::getFechaInicio));

    for (ProyectoSocioPeriodoJustificacion periodoJustificacion : proyectoSocioPeriodoJustificacionesBD) {
      periodoJustificacion.setNumPeriodo(numPeriodo.incrementAndGet());
    }

    repository.saveAll(proyectoSocioPeriodoJustificacionesBD);

    log.debug("update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificaciones) - end");

    return proyectoSocioPeriodoJustificacion;

  }

  /**
   * Obtiene {@link ProyectoSocioPeriodoJustificacion} por su id.
   *
   * @param id el id de la entidad {@link ProyectoSocioPeriodoJustificacion}.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion}.
   */
  @Override
  public ProyectoSocioPeriodoJustificacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoSocioPeriodoJustificacion returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoSocioPeriodoJustificacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link ProyectoSocioPeriodoJustificacion} por id.
   *
   * @param id el id de la entidad {@link ProyectoSocioPeriodoJustificacion}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene las {@link ProyectoSocioPeriodoJustificacion} para una
   * {@link ProyectoSocio}.
   *
   * @param proyectoSocioId el id de la {@link ProyectoSocio}.
   * @param query           la información del filtro.
   * @param pageable        la información de la paginación.
   * @return la lista de entidades {@link ProyectoSocioPeriodoJustificacion} de la
   *         {@link ProyectoSocio} paginadas.
   */
  public Page<ProyectoSocioPeriodoJustificacion> findAllByProyectoSocio(Long proyectoSocioId, String query,
      Pageable pageable) {
    log.debug("findAllByProyectoSocio(Long proyectoSocioId, String query, Pageable pageable) - start");
    Specification<ProyectoSocioPeriodoJustificacion> specs = ProyectoSocioPeriodoJustificacionSpecifications
        .byProyectoSocioId(proyectoSocioId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoSocioPeriodoJustificacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyectoSocio(Long proyectoSocioId, String query, Pageable pageable) - end");
    return returnValue;
  }

  private void validateProyectoSocioPeriodoJustificacion(
      ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) {

    AssertHelper.idNotNull(proyectoSocioPeriodoJustificacion.getProyectoSocioId(), ProyectoSocio.class);

    ProyectoSocio proyectoSocio = proyectoSocioRepository
        .findById(proyectoSocioPeriodoJustificacion.getProyectoSocioId())
        .orElseThrow(() -> new ProyectoSocioNotFoundException(proyectoSocioPeriodoJustificacion.getProyectoSocioId()));

    AssertHelper.isBefore(
        proyectoSocioPeriodoJustificacion.getFechaInicio().isBefore(proyectoSocioPeriodoJustificacion.getFechaFin()));

    Proyecto proyecto = proyectoRepository.findById(proyectoSocio.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoSocio.getProyectoId()));
    if (proyecto.getEstado().getEstado() == EstadoProyecto.Estado.CONCEDIDO) {
      Assert.isTrue(
          proyectoSocioPeriodoJustificacion.getFechaInicioPresentacion() != null
              && proyectoSocioPeriodoJustificacion.getFechaFinPresentacion() != null,
          "Las fechas de presentación no pueden ser null cuando el estado del proyecto es Abierto");
    }

    if (proyectoSocioPeriodoJustificacion.getFechaInicioPresentacion() != null
        && proyectoSocioPeriodoJustificacion.getFechaFinPresentacion() != null) {
      AssertHelper.fieldBefore(
          proyectoSocioPeriodoJustificacion.getFechaInicioPresentacion()
              .isBefore(proyectoSocioPeriodoJustificacion.getFechaFinPresentacion()),
          MSG_FIELD_FECHA_INICIO_PRESENTACION, MSG_FIELD_FECHA_FIN_PRESENTACION);
    }

    AssertHelper.fieldBefore(
        proyectoSocio.getFechaFin() == null
            || !proyectoSocioPeriodoJustificacion.getFechaFin().isAfter(proyectoSocio.getFechaFin()),
        MSG_FIELD_FECHA_FIN_PROYECTO_SOCIO, MSG_FIELD_FECHA_FIN_PROYECTO_SOCIO_PERIODO_JUSTIFICACION);

    Assert.isTrue(!isRangoFechasSolapado(proyectoSocioPeriodoJustificacion),
        () -> ProblemMessage.builder()
            .key(MSG_PROBLEM_DATE_OVERLOAP)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                MSG_MODEL_PROYECTO_PERIODO_JUSTIFICACION))
            .parameter(MSG_KEY_FIELD, proyectoSocioPeriodoJustificacion.getNumPeriodo())
            .build());

  }

  /**
   * Comprueba si el rango de fechas del socio se solapa con alguno de los rangos
   * de ese mismo socio en el proyecto.
   * 
   * @param proyectoSocioPeriodoJustificacion un {@link ProyectoSocio}.
   * @return true si se solapa o false si no hay solapamiento.
   */
  private boolean isRangoFechasSolapado(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) {
    log.debug("isRangoFechasSolapado(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) - start");

    Specification<ProyectoSocioPeriodoJustificacion> specByIdNotEqual = ProyectoSocioPeriodoJustificacionSpecifications
        .byIdNotEqual(proyectoSocioPeriodoJustificacion.getId());

    Specification<ProyectoSocioPeriodoJustificacion> specProyectoSocioId = ProyectoSocioPeriodoJustificacionSpecifications
        .byProyectoSocioId(proyectoSocioPeriodoJustificacion.getProyectoSocioId());

    Specification<ProyectoSocioPeriodoJustificacion> specByRangoFechaSolapados = ProyectoSocioPeriodoJustificacionSpecifications
        .byRangoFechaSolapados(proyectoSocioPeriodoJustificacion.getFechaInicio(),
            proyectoSocioPeriodoJustificacion.getFechaFin());

    Specification<ProyectoSocioPeriodoJustificacion> specs = Specification.where(specByIdNotEqual)
        .and(specByRangoFechaSolapados).and(specProyectoSocioId);
    boolean returnValue = repository.count(specs) > 0;
    log.debug("isRangoFechasSolapado(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia de documentos relacionados al
   * {@link ProyectoSocioPeriodoJustificacion} por id.
   *
   * @param id el id de la entidad {@link ProyectoSocioPeriodoJustificacion}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsDocumentosById(Long id) {
    log.debug("existsDocumentosById(final Long id)  - start", id);
    final boolean existe = proyectoSocioPeriodoJustificacionDocumentoRepository
        .existsByProyectoSocioPeriodoJustificacionId(id);
    log.debug("existsDocumentosById(final Long id)  - end", id);
    return existe;
  }

}
