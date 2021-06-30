package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoSocioPeriodoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoJustificacionService;
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
 * {@link SolicitudProyectoSocioPeriodoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoSocioPeriodoJustificacionServiceImpl
    implements SolicitudProyectoSocioPeriodoJustificacionService {

  private final SolicitudProyectoSocioPeriodoJustificacionRepository repository;

  private final SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  private final SolicitudService solicitudService;

  private final SolicitudProyectoRepository solicitudProyectoRepository;

  public SolicitudProyectoSocioPeriodoJustificacionServiceImpl(
      SolicitudProyectoSocioPeriodoJustificacionRepository repository,
      SolicitudProyectoSocioRepository solicitudProyectoSocioRepository, SolicitudService solicitudService,
      SolicitudProyectoRepository solicitudProyectoRepository) {
    this.repository = repository;
    this.solicitudProyectoSocioRepository = solicitudProyectoSocioRepository;
    this.solicitudService = solicitudService;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoSocioPeriodoJustificacion}.
   * 
   * @param solicitudProyectoSocioId        Id de la
   *                                        {@link SolicitudProyectoSocio}.
   * @param solicitudPeriodoJustificaciones lista con los nuevos
   *                                        {@link SolicitudProyectoSocioPeriodoJustificacion}
   *                                        a guardar.
   * @return {@link SolicitudProyectoSocioPeriodoJustificacion} actualizado.
   */
  @Override
  @Transactional
  public List<SolicitudProyectoSocioPeriodoJustificacion> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoSocioPeriodoJustificacion> solicitudPeriodoJustificaciones) {

    log.debug(
        "update(Long solicitudProyectoSocioId, List<SolicitudProyectoSocioPeriodoJustificacion> solicitudPeriodoJustificaciones) - start");

    SolicitudProyectoSocio solicitudProyectoSocio = solicitudProyectoSocioRepository.findById(solicitudProyectoSocioId)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(solicitudProyectoSocioId));

    // comprobar si la solicitud es modificable
    SolicitudProyecto solicitudProyecto = solicitudProyectoRepository
        .findById(solicitudProyectoSocio.getSolicitudProyectoId())
        .orElseThrow(() -> new SolicitudProyectoNotFoundException(solicitudProyectoSocio.getSolicitudProyectoId()));
    Assert.isTrue(solicitudService.modificable(solicitudProyecto.getId()),
        "No se puede modificar SolicitudProyectoSocioPeriodoJustificacion");

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacionesBD = repository
        .findAllBySolicitudProyectoSocioId(solicitudProyectoSocioId);

    // Periodos eliminados
    List<SolicitudProyectoSocioPeriodoJustificacion> periodoJustificacionesEliminar = solicitudProyectoSocioPeriodoJustificacionesBD
        .stream()
        .filter(periodo -> !solicitudPeriodoJustificaciones.stream()
            .map(SolicitudProyectoSocioPeriodoJustificacion::getId).anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!periodoJustificacionesEliminar.isEmpty()) {
      repository.deleteAll(periodoJustificacionesEliminar);
    }

    if (solicitudPeriodoJustificaciones.isEmpty()) {
      return new ArrayList<>();
    }

    // Ordena los periodos por mesInicial
    solicitudPeriodoJustificaciones
        .sort(Comparator.comparing(SolicitudProyectoSocioPeriodoJustificacion::getMesInicial));

    AtomicInteger numPeriodo = new AtomicInteger(0);

    SolicitudProyectoSocioPeriodoJustificacion periodoJustificacionAnterior = null;
    for (SolicitudProyectoSocioPeriodoJustificacion periodoJustificacion : solicitudPeriodoJustificaciones) {
      // Actualiza el numero de periodo
      periodoJustificacion.setNumPeriodo(numPeriodo.incrementAndGet());

      // Si tiene id se valida que exista y que tenga la solicitud proyecto socio de
      // la que se
      // estan actualizando los periodos
      if (periodoJustificacion.getId() != null) {
        SolicitudProyectoSocioPeriodoJustificacion periodoJustificacionBD = solicitudProyectoSocioPeriodoJustificacionesBD
            .stream().filter(periodo -> periodo.getId() == periodoJustificacion.getId()).findFirst().orElseThrow(
                () -> new SolicitudProyectoSocioPeriodoJustificacionNotFoundException(periodoJustificacion.getId()));

        Assert.isTrue(
            periodoJustificacionBD.getSolicitudProyectoSocioId() == periodoJustificacion.getSolicitudProyectoSocioId(),
            "No se puede modificar la solicitud proyecto socio del SolicitudProyectoSocioPeriodoJustificacion");
      }

      // Setea la convocatoria recuperada del convocatoriaId
      periodoJustificacion.setSolicitudProyectoSocioId(solicitudProyectoSocio.getId());

      // Validaciones
      Assert.isTrue(periodoJustificacion.getMesInicial() < periodoJustificacion.getMesFinal(),
          "El mes final tiene que ser posterior al mes inicial");

      if (periodoJustificacion.getFechaInicio() != null && periodoJustificacion.getFechaFin() != null) {
        Assert.isTrue(periodoJustificacion.getFechaInicio().isBefore(periodoJustificacion.getFechaFin()),
            "La fecha de fin tiene que ser posterior a la fecha de inicio");
      }

      SolicitudProyectoSocio solicitudProyectoSocioPeriodoJustificacion = solicitudProyectoSocioRepository
          .findById(periodoJustificacion.getSolicitudProyectoSocioId()).orElseThrow(
              () -> new SolicitudProyectoSocioNotFoundException(periodoJustificacion.getSolicitudProyectoSocioId()));
      SolicitudProyecto solicitudProyectoPeriodoJustificacion = solicitudProyectoRepository
          .findById(solicitudProyectoSocioPeriodoJustificacion.getSolicitudProyectoId())
          .orElseThrow(() -> new SolicitudProyectoNotFoundException(
              solicitudProyectoSocioPeriodoJustificacion.getSolicitudProyectoId()));
      Assert.isTrue(
          solicitudProyectoPeriodoJustificacion.getDuracion() == null
              || periodoJustificacion.getMesFinal() <= solicitudProyectoPeriodoJustificacion.getDuracion(),
          "El mes final no puede ser superior a la duración en meses indicada en la solicitud de proyecto");

      Assert.isTrue(
          periodoJustificacionAnterior == null || (periodoJustificacionAnterior != null
              && periodoJustificacionAnterior.getMesFinal() < periodoJustificacion.getMesInicial()),
          "El periodo se solapa con otro existente");

      periodoJustificacionAnterior = periodoJustificacion;
    }

    List<SolicitudProyectoSocioPeriodoJustificacion> returnValue = repository.saveAll(solicitudPeriodoJustificaciones);

    log.debug(
        "update(Long solicitudProyectoSocioId,  List<SolicitudProyectoSocioPeriodoJustificacion> solicitudPeriodoJustificaciones) - end");

    return returnValue;

  }

  /**
   * Comprueba la existencia del
   * {@link SolicitudProyectoSocioPeriodoJustificacion} por id.
   *
   * @param id el id de la entidad
   *           {@link SolicitudProyectoSocioPeriodoJustificacion}.
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
   * Obtiene una entidad {@link SolicitudProyectoSocioPeriodoJustificacion} por
   * id.
   * 
   * @param id Identificador de la entidad
   *           {@link SolicitudProyectoSocioPeriodoJustificacion}.
   * @return SolicitudProyectoSocioPeriodoJustificacion la entidad
   *         {@link SolicitudProyectoSocioPeriodoJustificacion}.
   */
  @Override
  public SolicitudProyectoSocioPeriodoJustificacion findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoSocioPeriodoJustificacion returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoSocioPeriodoJustificacionNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyectoSocioPeriodoJustificacion}.
   *
   * @param id Id del {@link SolicitudProyectoSocioPeriodoJustificacion}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "SolicitudProyectoSocioPeriodoJustificacion id no puede ser null para eliminar un SolicitudProyectoSocioPeriodoJustificacion");
    if (!repository.existsById(id)) {
      throw new SolicitudProyectoSocioPeriodoJustificacionNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudProyectoSocioPeriodoJustificacion} para un
   * {@link Solicitud}.
   *
   * @param solicitudProyectoSocioId el id del {@link SolicitudProyectoSocio}.
   * @param query                    la información del filtro.
   * @param paging                   la información de la paginación.
   * @return la lista de entidades
   *         {@link SolicitudProyectoSocioPeriodoJustificacion} del
   *         {@link SolicitudProyectoSocio} paginadas.
   */
  @Override
  public Page<SolicitudProyectoSocioPeriodoJustificacion> findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId,
      String query, Pageable paging) {
    log.debug("findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoSocioPeriodoJustificacion> specs = SolicitudProyectoSocioPeriodoJustificacionSpecifications
        .bySolicitudId(solicitudProyectoSocioId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoSocioPeriodoJustificacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query, Pageable paging) - end");
    return returnValue;
  }

}