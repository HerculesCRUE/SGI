package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoAnualidadNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPartidaNotFoundException;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.repository.AnualidadIngresoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoAnualidadRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoPartidaRepository;
import org.crue.hercules.sgi.csp.repository.specification.AnualidadIngresoSpecifications;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link AnualidadIngreso}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class AnualidadIngresoService {

  private final AnualidadIngresoRepository repository;

  private final ProyectoAnualidadRepository proyectoAnualidadRepository;

  private final ProyectoPartidaRepository proyectoPartidaRepository;

  public AnualidadIngresoService(AnualidadIngresoRepository anualidadIngresoRepository,
      ProyectoAnualidadRepository proyectoAnualidadRepository, ProyectoPartidaRepository proyectoPartidaRepository) {
    this.repository = anualidadIngresoRepository;
    this.proyectoAnualidadRepository = proyectoAnualidadRepository;
    this.proyectoPartidaRepository = proyectoPartidaRepository;
  }

  /**
   * Actualiza el listado de {@link AnualidadIngreso} de la
   * {@link ProyectoAnualidad} con el listado anualidadIngresos añadiendo,
   * editando o eliminando los elementos segun proceda.
   * 
   * @param proyectoAnualidadId Id del {@link ProyectoAnualidad}.
   * @param anualidadesIngreso  lista con los nuevos {@link AnualidadIngreso} a
   *                            guardar.
   * @return Lista actualizada con los {@link AnualidadIngreso}.
   */
  @Transactional
  public List<AnualidadIngreso> update(Long proyectoAnualidadId, List<AnualidadIngreso> anualidadesIngreso) {
    log.debug("update(Long proyectoAnualidadId, List<AnualidadIngreso> anualidadesIngreso) - start");

    if (!proyectoAnualidadRepository.findById(proyectoAnualidadId).isPresent()) {
      throw new ProyectoAnualidadNotFoundException(proyectoAnualidadId);
    }

    Specification<AnualidadIngreso> specs = AnualidadIngresoSpecifications.byProyectoAnualidadId(proyectoAnualidadId);
    List<AnualidadIngreso> anualidadesIngresoBD = repository.findAll(specs);

    // Anualidades eliminados
    List<AnualidadIngreso> anualidadesIngresoEliminar = anualidadesIngresoBD.stream()
        .filter(anualidadIngreso -> anualidadesIngreso.stream().map(AnualidadIngreso::getId)
            .noneMatch(id -> Objects.equals(id, anualidadIngreso.getId())))
        .collect(Collectors.toList());

    if (!anualidadesIngresoEliminar.isEmpty()) {
      repository.deleteAll(anualidadesIngresoEliminar);
    }

    if (anualidadesIngreso.isEmpty()) {
      return new ArrayList<>();
    }

    for (AnualidadIngreso anualidadIngreso : anualidadesIngreso) {

      if (!proyectoPartidaRepository.findById(anualidadIngreso.getProyectoPartida().getId()).isPresent()) {
        throw new ProyectoPartidaNotFoundException(anualidadIngreso.getProyectoPartida().getId());
      }

      anualidadIngreso.setProyectoAnualidadId(proyectoAnualidadId);

    }

    List<AnualidadIngreso> returnValue = repository.saveAll(anualidadesIngreso);
    log.debug("update(Long proyectoAnualidadId, List<AnualidadIngreso> anualidadesIngreso) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link AnualidadIngreso} para un {@link ProyectoAnualidad}.
   *
   * @param proyectoAnualidadId el id de la {@link ProyectoAnualidad}.
   * @param query               la información del filtro.
   * @param pageable            la información de la paginación.
   * @return la lista de entidades {@link AnualidadIngreso} del
   *         {@link ProyectoAnualidad} paginadas.
   */
  public Page<AnualidadIngreso> findAllByProyectoAnualiadad(Long proyectoAnualidadId, String query, Pageable pageable) {
    log.debug("findAllByProyectoAnualiadad(Long proyectoAnualidadId, String query, Pageable pageable) - start");
    Specification<AnualidadIngreso> specs = AnualidadIngresoSpecifications.byProyectoAnualidadId(proyectoAnualidadId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<AnualidadIngreso> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyectoAnualiadad(Long proyectoAnualidadId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Indica si existen {@link AnualidadIngreso} de un {@link Proyecto}
   * 
   * @param proyectoId identificador de la {@link Proyecto}
   * @return si existen {@link AnualidadIngreso} asociados al {@link Proyecto}
   */
  public boolean existsByProyecto(Long proyectoId) {
    log.debug("existsByProyecto(Long proyectoId) - start");
    boolean returnValue = repository.existsByProyectoAnualidadProyectoId(proyectoId);
    log.debug("existsByProyecto(Long proyectoId) - end");
    return returnValue;
  }

}
