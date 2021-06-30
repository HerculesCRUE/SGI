package org.crue.hercules.sgi.csp.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioPeriodoPagoNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoSocioPeriodoPagoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoPagoService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion
 * {@link SolicitudProyectoSocioPeriodoPago}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoSocioPeriodoPagoServiceImpl implements SolicitudProyectoSocioPeriodoPagoService {

  private final SolicitudProyectoSocioPeriodoPagoRepository repository;

  private final SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  private final SolicitudService solicitudService;

  /** Solicitud proyecto repository */
  private final SolicitudProyectoRepository solicitudProyectoRepository;

  /**
   * {@link SolicitudProyectoSocioPeriodoPagoServiceImpl}
   * 
   * @param repository                       {@link SolicitudProyectoSocioPeriodoPagoRepository}
   * @param solicitudProyectoSocioRepository {@link SolicitudProyectoSocioRepository}
   * @param solicitudService                 {@link SolicitudService}
   * @param solicitudProyectoRepository      {@link SolicitudProyectoRepository}
   */
  public SolicitudProyectoSocioPeriodoPagoServiceImpl(SolicitudProyectoSocioPeriodoPagoRepository repository,
      SolicitudProyectoSocioRepository solicitudProyectoSocioRepository, SolicitudService solicitudService,
      SolicitudProyectoRepository solicitudProyectoRepository) {
    this.repository = repository;
    this.solicitudProyectoSocioRepository = solicitudProyectoSocioRepository;
    this.solicitudService = solicitudService;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
  }

  /**
   * Actualiza el listado de {@link SolicitudProyectoSocioPeriodoPago} de la
   * {@link SolicitudProyectoSocio} con el listado solicitudPeriodoPagos
   * añadiendo, editando o eliminando los elementos segun proceda.
   *
   * @param solicitudProyectoSocioId Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudPeriodoPagos    lista con los nuevos
   *                                 {@link SolicitudProyectoSocioPeriodoPago} a
   *                                 guardar.
   * @return la entidad {@link SolicitudProyectoSocioPeriodoPago} persistida.
   */
  @Override
  @Transactional
  public List<SolicitudProyectoSocioPeriodoPago> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoSocioPeriodoPago> solicitudPeriodoPagos) {
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long solicitudProyectoSocioId, List<SolicitudProyectoSocioPeriodoPago> solicitudPeriodoPagos) - start");

    SolicitudProyectoSocio solicitudProyectoSocio = solicitudProyectoSocioRepository.findById(solicitudProyectoSocioId)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(solicitudProyectoSocioId));

    // comprobar si la solicitud es modificable
    SolicitudProyecto solicitudProyecto = solicitudProyectoRepository
        .findById(solicitudProyectoSocio.getSolicitudProyectoId())
        .orElseThrow(() -> new SolicitudProyectoNotFoundException(solicitudProyectoSocio.getSolicitudProyectoId()));
    Assert.isTrue(solicitudService.modificable(solicitudProyecto.getId()),
        "No se puede modificar SolicitudProyectoSocioPeriodoPago");

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagosBD = repository
        .findAllBySolicitudProyectoSocioId(solicitudProyectoSocioId);

    // Periodos pago eliminados
    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagoEliminar = solicitudProyectoSocioPeriodoPagosBD
        .stream().filter(periodo -> !solicitudPeriodoPagos.stream().map(SolicitudProyectoSocioPeriodoPago::getId)
            .anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!solicitudProyectoSocioPeriodoPagoEliminar.isEmpty()) {
      repository.deleteAll(solicitudProyectoSocioPeriodoPagoEliminar);
    }

    // Ordena los periodos por mesInicial
    solicitudPeriodoPagos.sort(Comparator.comparing(SolicitudProyectoSocioPeriodoPago::getMes));

    AtomicInteger numPeriodo = new AtomicInteger(0);

    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPagoAnterior = null;
    for (SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago : solicitudPeriodoPagos) {
      // Actualiza el numero de periodo
      solicitudProyectoSocioPeriodoPago.setNumPeriodo(numPeriodo.incrementAndGet());

      // Si tiene id se valida que exista y que tenga la solicitudProyectoSocio de la
      // que se
      // estan actualizando los periodos
      if (solicitudProyectoSocioPeriodoPago.getId() != null) {
        SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPagoBD = solicitudProyectoSocioPeriodoPagosBD
            .stream().filter(periodo -> periodo.getId() == solicitudProyectoSocioPeriodoPago.getId()).findFirst()
            .orElseThrow(() -> new SolicitudProyectoSocioPeriodoPagoNotFoundException(
                solicitudProyectoSocioPeriodoPago.getId()));

        Assert.isTrue(
            solicitudProyectoSocioPeriodoPagoBD.getSolicitudProyectoSocioId() == solicitudProyectoSocioPeriodoPago
                .getSolicitudProyectoSocioId(),
            "No se puede modificar la solicitud proyecto socio del SolicitudProyectoSocioPeriodoPago");
      }

      // Setea la solicitudProyectoSocio recuperada del solicitudProyectoSocioId
      solicitudProyectoSocioPeriodoPago.setSolicitudProyectoSocioId(solicitudProyectoSocio.getId());

      // Validaciones

      Assert.notNull(solicitudProyectoSocioPeriodoPago.getMes(),
          "Mes no puede ser null para realizar la acción sobre SolicitudProyectoSocioPeriodoPago");

      SolicitudProyecto solicitudProyectoPeriodo = solicitudProyectoRepository
          .findById(solicitudProyectoSocio.getSolicitudProyectoId())
          .orElseThrow(() -> new SolicitudProyectoNotFoundException(solicitudProyectoSocio.getSolicitudProyectoId()));
      Assert.isTrue(
          solicitudProyectoPeriodo.getDuracion() == null
              || solicitudProyectoSocioPeriodoPago.getMes() <= solicitudProyectoPeriodo.getDuracion(),
          "El mes no puede ser superior a la duración en meses indicada en la Convocatoria");

      Assert.isTrue(
          solicitudProyectoSocioPeriodoPagoAnterior == null
              || (solicitudProyectoSocioPeriodoPagoAnterior != null && !solicitudProyectoSocioPeriodoPagoAnterior
                  .getMes().equals(solicitudProyectoSocioPeriodoPago.getMes())),
          "El periodo se solapa con otro existente");

      solicitudProyectoSocioPeriodoPagoAnterior = solicitudProyectoSocioPeriodoPago;
    }

    List<SolicitudProyectoSocioPeriodoPago> returnValue = repository.saveAll(solicitudPeriodoPagos);
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long solicitudProyectoSocioId, List<SolicitudProyectoSocioPeriodoPago> solicitudPeriodoPagos) - end");

    return returnValue;
  }

  /**
   * Obtiene una entidad {@link SolicitudProyectoSocioPeriodoPago} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link SolicitudProyectoSocioPeriodoPago}.
   * @return SolicitudProyectoSocioPeriodoPago la entidad
   *         {@link SolicitudProyectoSocioPeriodoPago}.
   */
  @Override
  public SolicitudProyectoSocioPeriodoPago findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoSocioPeriodoPago returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoSocioPeriodoPagoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SolicitudProyectoSocioPeriodoPago} para un
   * {@link SolicitudProyectoSocio}.
   *
   * @param idSolicitudProyectoSocio el id del {@link SolicitudProyectoSocio}.
   * @param query                    la información del filtro.
   * @param paging                   la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoSocioPeriodoPago} del
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<SolicitudProyectoSocioPeriodoPago> findAllBySolicitudProyectoSocio(Long idSolicitudProyectoSocio,
      String query, Pageable paging) {
    log.debug("findAllBySolicitudProyecto(Long solicitudProyectoId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoSocioPeriodoPago> specs = SolicitudProyectoSocioPeriodoPagoSpecifications
        .bySolicitudProyectoSocioId(idSolicitudProyectoSocio).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoSocioPeriodoPago> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyecto(Long solicitudProyectoId, String query, Pageable paging) - end");
    return returnValue;
  }

}