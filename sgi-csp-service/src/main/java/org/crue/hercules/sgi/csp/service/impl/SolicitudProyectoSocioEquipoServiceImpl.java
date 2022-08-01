package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioEquipoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoSocioEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioEquipoService;
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
 * Service Implementation para gestion {@link SolicitudProyectoSocioEquipo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoSocioEquipoServiceImpl implements SolicitudProyectoSocioEquipoService {

  /** Solicitud service */
  private final SolicitudService solicitudService;

  /** Solicitud proyecto equipo socio repository */
  private final SolicitudProyectoSocioEquipoRepository repository;

  /** Solicitud proyecto socio repository */
  private final SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  /** Solicitud proyecto repository */
  private final SolicitudProyectoRepository solicitudProyectoRepository;

  public SolicitudProyectoSocioEquipoServiceImpl(SolicitudProyectoSocioEquipoRepository repository,
      SolicitudService solicitudService, SolicitudProyectoSocioRepository solicitudProyectoSocioRepository,
      SolicitudProyectoRepository solicitudProyectoRepository) {
    this.repository = repository;
    this.solicitudService = solicitudService;
    this.solicitudProyectoSocioRepository = solicitudProyectoSocioRepository;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoSocioEquipo}.
   * 
   * @param solicitudProyectoSocioId      Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudProyectoEquipoSocios lista con los nuevos
   *                                      {@link SolicitudProyectoSocioEquipo} a
   *                                      guardar.
   * @return SolicitudProyectoSocioEquipo {@link SolicitudProyectoSocioEquipo}
   *         actualizado.
   */
  @Override
  @Transactional
  public List<SolicitudProyectoSocioEquipo> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios) {
    log.debug(
        "update(Long solicitudProyectoSocioId,  List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios) - start");

    SolicitudProyectoSocio solicitudProyectoSocio = solicitudProyectoSocioRepository.findById(solicitudProyectoSocioId)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(solicitudProyectoSocioId));

    // comprobar si la solicitud es modificable
    SolicitudProyecto solicitudProyecto = solicitudProyectoRepository
        .findById(solicitudProyectoSocio.getSolicitudProyectoId())
        .orElseThrow(() -> new SolicitudProyectoNotFoundException(solicitudProyectoSocio.getSolicitudProyectoId()));
    Assert.isTrue(solicitudService.modificable(solicitudProyecto.getId()),
        "No se puede modificar SolicitudProyectoSocioEquipo");

    List<SolicitudProyectoSocioEquipo> existentes = repository
        .findAllBySolicitudProyectoSocioId(solicitudProyectoSocioId);

    // Periodos eliminados
    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoEliminar = existentes.stream()
        .filter(periodo -> solicitudProyectoEquipoSocios.stream().map(SolicitudProyectoSocioEquipo::getId)
            .noneMatch(id -> Objects.equals(id, periodo.getId())))
        .collect(Collectors.toList());

    if (!solicitudProyectoEquipoEliminar.isEmpty()) {
      repository.deleteAll(solicitudProyectoEquipoEliminar);
    }

    if (solicitudProyectoEquipoSocios.isEmpty()) {
      return new ArrayList<>();
    }

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoMesInicioNull = solicitudProyectoEquipoSocios.stream()
        .filter(periodo -> periodo.getMesInicio() == null).collect(Collectors.toList());

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoConMesInicio = solicitudProyectoEquipoSocios.stream()
        .filter(periodo -> periodo.getMesInicio() != null).collect(Collectors.toList());

    solicitudProyectoEquipoMesInicioNull.sort(Comparator.comparing(SolicitudProyectoSocioEquipo::getPersonaRef));

    solicitudProyectoEquipoConMesInicio.sort(Comparator.comparing(SolicitudProyectoSocioEquipo::getMesInicio)
        .thenComparing(Comparator.comparing(SolicitudProyectoSocioEquipo::getPersonaRef)));

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoAll = new ArrayList<>();
    solicitudProyectoEquipoAll.addAll(solicitudProyectoEquipoMesInicioNull);
    solicitudProyectoEquipoAll.addAll(solicitudProyectoEquipoConMesInicio);

    SolicitudProyectoSocioEquipo solicitudProyectoEquipoSocioAnterior = null;
    for (SolicitudProyectoSocioEquipo solicitudProyectoSocioEquipo : solicitudProyectoEquipoAll) {

      // Si tiene id se valida que exista y que tenga la solicitud proyecto equipo de
      // la que se estan actualizando los periodos
      if (solicitudProyectoSocioEquipo.getId() != null) {
        SolicitudProyectoSocioEquipo existente = existentes.stream()
            .filter(equipoSocio -> Objects.equals(equipoSocio.getId(), solicitudProyectoSocioEquipo.getId()))
            .findFirst()
            .orElseThrow(() -> new SolicitudProyectoSocioEquipoNotFoundException(solicitudProyectoSocioEquipo.getId()));

        Assert.isTrue(
            Objects.equals(existente.getSolicitudProyectoSocioId(),
                solicitudProyectoSocioEquipo.getSolicitudProyectoSocioId()),
            "No se puede modificar la solicitud proyecto socio del SolicitudProyectoSocioEquipo");
      }

      // Setea la solicitud proyecto socio recuperada del solicitudProyectoSocioId
      solicitudProyectoSocioEquipo.setSolicitudProyectoSocioId(solicitudProyectoSocio.getId());

      // Validaciones
      Assert.notNull(solicitudProyectoSocioEquipo.getRolProyecto(),
          "El rol de participación no puede ser null para realizar la acción sobre SolicitudProyectoSocioEquipo");

      Assert.notNull(solicitudProyectoSocioEquipo.getPersonaRef(),
          "La persona ref no puede ser null para realizar la acción sobre SolicitudProyectoSocioEquipo");

      Assert.isTrue(
          solicitudProyectoEquipoSocioAnterior == null || (!solicitudProyectoEquipoSocioAnterior.getPersonaRef()
              .equals(solicitudProyectoSocioEquipo.getPersonaRef())
              || (solicitudProyectoEquipoSocioAnterior.getPersonaRef()
                  .equals(solicitudProyectoSocioEquipo.getPersonaRef())
                  && (solicitudProyectoSocioEquipo.getMesInicio() > solicitudProyectoEquipoSocioAnterior
                      .getMesFin()))),
          "El periodo se solapa con otro existente");

      solicitudProyectoEquipoSocioAnterior = solicitudProyectoSocioEquipo;
    }

    List<SolicitudProyectoSocioEquipo> returnValue = repository.saveAll(solicitudProyectoEquipoSocios);
    log.debug(
        "update(Long solicitudProyectoSocioId,  List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios) - end");

    return returnValue;
  }

  /**
   * Obtiene una entidad {@link SolicitudProyectoSocioEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoSocioEquipo}.
   * @return SolicitudProyectoSocioEquipo la entidad
   *         {@link SolicitudProyectoSocioEquipo}.
   */
  @Override
  public SolicitudProyectoSocioEquipo findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoSocioEquipo returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoSocioEquipoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene la {@link SolicitudProyectoSocioEquipo} para una
   * {@link SolicitudProyectoSocio}.
   *
   * @param solicitudProyectoSocioId el id de la {@link SolicitudProyectoSocio}.
   * @return la lista de entidades {@link SolicitudProyectoSocioEquipo} de la
   *         {@link SolicitudProyectoSocio} paginadas.
   */
  @Override
  public Page<SolicitudProyectoSocioEquipo> findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query,
      Pageable paging) {
    log.debug("findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoSocioEquipo> specs = SolicitudProyectoSocioEquipoSpecifications
        .bySolicitudProyectoSocio(solicitudProyectoSocioId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoSocioEquipo> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query, Pageable paging) - end");
    return returnValue;

  }

}