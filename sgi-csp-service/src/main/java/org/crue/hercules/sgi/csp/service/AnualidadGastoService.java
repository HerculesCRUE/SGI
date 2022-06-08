package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoAnualidadNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPartidaNotFoundException;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.repository.AnualidadGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoAnualidadRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoPartidaRepository;
import org.crue.hercules.sgi.csp.repository.specification.AnualidadGastoSpecifications;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link AnualidadGasto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class AnualidadGastoService {

  private final AnualidadGastoRepository repository;

  private final ProyectoAnualidadRepository proyectoAnualidadRepository;

  private final ProyectoPartidaRepository proyectoPartidaRepository;

  private final ConceptoGastoRepository conceptoGastoRepository;

  public AnualidadGastoService(AnualidadGastoRepository anualidadGastoRepository,
      ProyectoAnualidadRepository proyectoAnualidadRepository, ProyectoPartidaRepository proyectoPartidaRepository,
      ConceptoGastoRepository conceptoGastoRepository) {
    this.repository = anualidadGastoRepository;
    this.proyectoAnualidadRepository = proyectoAnualidadRepository;
    this.proyectoPartidaRepository = proyectoPartidaRepository;
    this.conceptoGastoRepository = conceptoGastoRepository;
  }

  /**
   * Obtiene los {@link AnualidadGasto} para un {@link ProyectoAnualidad}.
   *
   * @param proyectoAnualidadId el id de la {@link ProyectoAnualidad}.
   * @param query               la información del filtro.
   * @param pageable            la información de la paginación.
   * @return la lista de entidades {@link AnualidadGasto} del
   *         {@link ProyectoAnualidad} paginadas.
   */
  public Page<AnualidadGasto> findAllByProyectoAnualidad(Long proyectoAnualidadId, String query, Pageable pageable) {
    log.debug("findAllByProyectoAnualiadad(Long proyectoAnualidadId, String query, Pageable pageable) - start");
    Specification<AnualidadGasto> specs = AnualidadGastoSpecifications.byProyectoAnualidadId(proyectoAnualidadId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<AnualidadGasto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyectoAnualiadad(Long proyectoAnualidadId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link AnualidadGasto} de la
   * {@link ProyectoAnualidad} con el listado codigosEconomicos añadiendo,
   * editando o eliminando los elementos segun proceda.
   * 
   * @param anualidadesGasto    listado de {@link AnualidadGasto}.
   * @param proyectoAnualidadId id {@link ProyectoAnualidad} a actualizar.
   * @return {@link AnualidadGasto} actualizado.
   */
  @Transactional
  public List<AnualidadGasto> update(Long proyectoAnualidadId, List<AnualidadGasto> anualidadesGasto) {
    log.debug("update(Long proyectoAnualidadId, List<AnualidadGasto> anualidadesIngreso) - start");

    if (!proyectoAnualidadRepository.findById(proyectoAnualidadId).isPresent()) {
      throw new ProyectoAnualidadNotFoundException(proyectoAnualidadId);
    }

    Specification<AnualidadGasto> specs = AnualidadGastoSpecifications.byProyectoAnualidadId(proyectoAnualidadId);
    List<AnualidadGasto> anualidadesGastoBD = repository.findAll(specs);

    // Anualidades eliminados
    List<AnualidadGasto> anualidadesGastoEliminar = anualidadesGastoBD.stream()
        .filter(anualidadGasto -> anualidadesGasto.stream().map(AnualidadGasto::getId)
            .noneMatch(id -> Objects.equals(id, anualidadGasto.getId())))
        .collect(Collectors.toList());

    if (!anualidadesGastoEliminar.isEmpty()) {
      repository.deleteAll(anualidadesGastoEliminar);
    }

    if (anualidadesGasto.isEmpty()) {
      return new ArrayList<>();
    }

    for (AnualidadGasto anualidadGasto : anualidadesGasto) {

      if (!proyectoPartidaRepository.findById(anualidadGasto.getProyectoPartida().getId()).isPresent()) {
        throw new ProyectoPartidaNotFoundException(anualidadGasto.getProyectoPartida().getId());
      }

      anualidadGasto.setProyectoAnualidadId(proyectoAnualidadId);

      if (!conceptoGastoRepository.findById(anualidadGasto.getConceptoGasto().getId()).isPresent()) {
        throw new ConceptoGastoNotFoundException(anualidadGasto.getConceptoGasto().getId());
      }
    }

    List<AnualidadGasto> returnValue = repository.saveAll(anualidadesGasto);
    log.debug("update(Long proyectoAnualidadId, List<AnualidadGasto> anualidadesIngreso) - end");
    return returnValue;
  }

  public List<AnualidadGasto> findAnualidadesGastosByProyectoId(Long proyectoId) {

    return this.repository.findAnualidadGastoByProyectoPartidaProyectoId(proyectoId);
  }

  /**
   * Indica si existen {@link AnualidadGasto} de un {@link Proyecto}
   * 
   * @param proyectoId identificador de la {@link Proyecto}
   * @return si existen {@link AnualidadGasto} asociados al {@link Proyecto}
   */
  public boolean existsByProyecto(Long proyectoId) {
    log.debug("existsByProyecto(Long proyectoId) - start");
    boolean returnValue = repository.existsByProyectoAnualidadProyectoId(proyectoId);
    log.debug("existsByProyecto(Long proyectoId) - end");
    return returnValue;
  }

}
