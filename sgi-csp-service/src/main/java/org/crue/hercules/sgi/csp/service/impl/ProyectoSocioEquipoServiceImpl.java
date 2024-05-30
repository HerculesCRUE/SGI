package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioEquipoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSocioEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoSocioEquipoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio implementación para la gestión de {@link ProyectoSocio}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoSocioEquipoServiceImpl implements ProyectoSocioEquipoService {

  private final ProyectoSocioEquipoRepository repository;

  private final ProyectoSocioRepository proyectoSocioRepository;

  public ProyectoSocioEquipoServiceImpl(ProyectoSocioEquipoRepository repository,
      ProyectoSocioRepository proyectoSocioRepository) {
    this.repository = repository;
    this.proyectoSocioRepository = proyectoSocioRepository;
  }

  /**
   * Actualiza los datos del {@link ProyectoSocioEquipo}.
   * 
   * @param proyectoSocioId      Id de la {@link ProyectoSocio}.
   * @param proyectoSocioEquipos lista con los nuevos {@link ProyectoSocioEquipo}
   *                             a guardar.
   * @return ProyectoSocioEquipo {@link ProyectoSocioEquipo} actualizado.
   */
  @Override
  @Transactional
  public List<ProyectoSocioEquipo> update(Long proyectoSocioId, List<ProyectoSocioEquipo> proyectoSocioEquipos) {
    log.debug("update(Long proyectoSocioId,  List<ProyectoSocioEquipo> proyectoSocioEquipos) - start");

    ProyectoSocio proyectoSocio = proyectoSocioRepository.findById(proyectoSocioId)
        .orElseThrow(() -> new ProyectoSocioNotFoundException(proyectoSocioId));

    List<ProyectoSocioEquipo> proyectoSocioEquipoBD = repository.findAllByProyectoSocioId(proyectoSocioId);

    // Equipos eliminados
    List<ProyectoSocioEquipo> proyectoSocioEquipoEliminar = proyectoSocioEquipoBD.stream().filter(
        periodo -> proyectoSocioEquipos.stream().map(ProyectoSocioEquipo::getId)
            .noneMatch(id -> Objects.equals(id, periodo.getId())))
        .collect(Collectors.toList());

    if (!proyectoSocioEquipoEliminar.isEmpty()) {
      repository.deleteAll(proyectoSocioEquipoEliminar);
    }

    if (proyectoSocioEquipos.isEmpty()) {
      return new ArrayList<>();
    }

    // Ordena los periodos por getFechaInicio
    List<ProyectoSocioEquipo> proyectoSocioEquipoFechaInicioNull = proyectoSocioEquipos.stream()
        .filter(periodo -> periodo.getFechaInicio() == null).collect(Collectors.toList());

    List<ProyectoSocioEquipo> proyectoSocioEquipoConFechaInicio = proyectoSocioEquipos.stream()
        .filter(periodo -> periodo.getFechaInicio() != null).collect(Collectors.toList());

    proyectoSocioEquipoFechaInicioNull.sort(Comparator.comparing(ProyectoSocioEquipo::getPersonaRef));

    proyectoSocioEquipoConFechaInicio.sort(Comparator.comparing(ProyectoSocioEquipo::getFechaInicio)
        .thenComparing(Comparator.comparing(ProyectoSocioEquipo::getPersonaRef)));

    List<ProyectoSocioEquipo> proyectoSocioEquipoAll = new ArrayList<>();
    proyectoSocioEquipoAll.addAll(proyectoSocioEquipoFechaInicioNull);
    proyectoSocioEquipoAll.addAll(proyectoSocioEquipoConFechaInicio);

    ProyectoSocioEquipo proyectoSocioEquipoAnterior = null;
    for (ProyectoSocioEquipo proyectoSocioEquipo : proyectoSocioEquipoAll) {

      // Si tiene id se valida que exista y que tenga la solicitud proyecto equipo de
      // la que se
      // estan actualizando los periodos
      if (proyectoSocioEquipo.getId() != null) {
        ProyectoSocioEquipo periodoJustificacionBD = proyectoSocioEquipoBD.stream()
            .filter(equipoSocio -> Objects.equals(equipoSocio.getId(), proyectoSocioEquipo.getId())).findFirst()
            .orElseThrow(() -> new ProyectoSocioEquipoNotFoundException(proyectoSocioEquipo.getId()));

        Assert.isTrue(
            Objects.equals(periodoJustificacionBD.getProyectoSocioId(), proyectoSocioEquipo.getProyectoSocioId()),
            "No se puede modificar el proyecto socio del ProyectoSocioEquipo");
      }

      // Setea el proyecto socio recuperado del proyectoSocioId
      proyectoSocioEquipo.setProyectoSocioId(proyectoSocio.getId());

      // Validaciones
      Assert.notNull(proyectoSocioEquipo.getRolProyecto(),
          "El rol de participación no puede ser null para realizar la acción sobre ProyectoSocioEquipo");

      Assert.notNull(proyectoSocioEquipo.getPersonaRef(),
          "La persona ref no puede ser null para realizar la acción sobre ProyectoSocioEquipo");

      if (proyectoSocioEquipo.getFechaInicio() != null && proyectoSocioEquipo.getFechaFin() != null) {
        Assert.isTrue(proyectoSocioEquipo.getFechaInicio().isBefore(proyectoSocioEquipo.getFechaFin()),
            "La fecha de fin tiene que ser posterior a la fecha de inicio");
      }

      Assert.isTrue(
          proyectoSocioEquipoAnterior == null || (proyectoSocioEquipoAnterior != null
              && (!proyectoSocioEquipoAnterior.getPersonaRef().equals(proyectoSocioEquipo.getPersonaRef())
                  || (proyectoSocioEquipoAnterior.getPersonaRef().equals(proyectoSocioEquipo.getPersonaRef())
                      && proyectoSocioEquipo.getFechaInicio().isAfter(proyectoSocioEquipoAnterior.getFechaFin())))),
          "El equipo se solapa con otro existente");

      proyectoSocioEquipoAnterior = proyectoSocioEquipo;

    }

    List<ProyectoSocioEquipo> returnValue = repository.saveAll(proyectoSocioEquipoAll);
    log.debug("update(Long proyectoSocioId,  List<ProyectoSocioEquipo> proyectoSocioEquipos) - end");

    return returnValue;
  }

  /**
   * Obtiene una entidad {@link ProyectoSocioEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoSocioEquipo}.
   * @return ProyectoSocioEquipo la entidad {@link ProyectoSocioEquipo}.
   */
  @Override
  public ProyectoSocioEquipo findById(Long id) {
    log.debug("findById(Long id) - start");
    final ProyectoSocioEquipo returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoSocioEquipoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene la lisata de {@link ProyectoSocioEquipo} para un
   * {@link ProyectoSocio}.
   *
   * @param proyectoSocioId el id de la {@link ProyectoSocio}.
   * @return la lista de entidades {@link ProyectoSocioEquipo} de la
   *         {@link ProyectoSocio} paginadas.
   */
  @Override
  public Page<ProyectoSocioEquipo> findAllByProyectoSocio(Long proyectoSocioId, String query, Pageable paging) {
    log.debug("findAllByProyectoSocio(Long proyectoSocioId, String query, Pageable paging) - start");

    Specification<ProyectoSocioEquipo> specs = ProyectoSocioEquipoSpecifications.byProyectoSocioId(proyectoSocioId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoSocioEquipo> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyectoSocio(Long proyectoSocioId, String query, Pageable paging) - end");
    return returnValue;

  }

  /**
   * Recupera la lista de miembros del equipo del socio de un
   * {@link ProyectoSocio}.
   * 
   * @param proyectoSocioId Identificador de la {@link ProyectoSocio}.
   * @return lista de {@link ProyectoSocioEquipo}.
   */
  @Override
  public List<ProyectoSocioEquipo> findAllByProyectoSocio(Long proyectoSocioId) {
    log.debug("findAllByProyectoSocio(Long proyectoSocioId) - start");
    Specification<ProyectoSocioEquipo> specs = ProyectoSocioEquipoSpecifications.byProyectoSocioId(proyectoSocioId);
    List<ProyectoSocioEquipo> returnValue = repository.findAll(specs);
    log.debug("findAllByProyectoSocio(Long proyectoSocioId) - end");
    return returnValue;
  }

  /**
   * Comprueba si alguno de los {@link ProyectoSocioEquipo} del {@link Proyecto}
   * tienen fechas
   * 
   * @param proyectoId el id del {@link Proyecto}.
   * @return true si existen y false en caso contrario.
   */
  @Override
  public boolean proyectoHasProyectoSocioEquipoWithDates(Long proyectoId) {
    log.debug("proyectoHasProyectoSocioEquipoWithDates({})  - start", proyectoId);

    Specification<ProyectoSocioEquipo> specs = ProyectoSocioEquipoSpecifications.byProyectoId(proyectoId)
        .and(ProyectoSocioEquipoSpecifications.withFechaInicioOrFechaFin());

    boolean hasProyectoSocioEquipoWithDates = repository.count(specs) > 0;
    log.debug("proyectoHasProyectoSocioEquipoWithDates({})  - end", proyectoId);
    return hasProyectoSocioEquipoWithDates;
  }

}
