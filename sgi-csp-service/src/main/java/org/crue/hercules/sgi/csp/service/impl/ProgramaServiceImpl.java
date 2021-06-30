package org.crue.hercules.sgi.csp.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProgramaSpecifications;
import org.crue.hercules.sgi.csp.service.ProgramaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Programa}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProgramaServiceImpl implements ProgramaService {

  private final ProgramaRepository repository;

  public ProgramaServiceImpl(ProgramaRepository programaRepository) {
    this.repository = programaRepository;
  }

  /**
   * Guardar un nuevo {@link Programa}.
   *
   * @param programa la entidad {@link Programa} a guardar.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  public Programa create(Programa programa) {
    log.debug("create(Programa programa) - start");

    Assert.isNull(programa.getId(), "Programa id tiene que ser null para crear un nuevo Programa");

    if (programa.getPadre() != null) {
      if (programa.getPadre().getId() == null) {
        programa.setPadre(null);
      } else {
        programa.setPadre(repository.findById(programa.getPadre().getId())
            .orElseThrow(() -> new ProgramaNotFoundException(programa.getPadre().getId())));
      }
    }

    if (programa.getPadre() == null) {
      Assert.isTrue(!existPlanWithNombre(programa.getNombre(), null), "Ya existe un plan con el mismo nombre");
    } else {
      Assert.isTrue(!existProgramaNombre(programa.getPadre().getId(), programa.getNombre(), null),
          "Ya existe un programa con el mismo nombre en el plan");
    }

    programa.setActivo(true);

    Programa returnValue = repository.save(programa);

    log.debug("create(Programa programa) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Programa}.
   *
   * @param programaActualizar la entidad {@link Programa} a actualizar.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  public Programa update(Programa programaActualizar) {
    log.debug("update(Programa programaActualizar) - start");

    Assert.notNull(programaActualizar.getId(), "Programa id no puede ser null para actualizar un Programa");

    if (programaActualizar.getPadre() != null) {
      if (programaActualizar.getPadre().getId() == null) {
        programaActualizar.setPadre(null);
      } else {
        programaActualizar.setPadre(repository.findById(programaActualizar.getPadre().getId())
            .orElseThrow(() -> new ProgramaNotFoundException(programaActualizar.getPadre().getId())));
      }
    }

    return repository.findById(programaActualizar.getId()).map(programa -> {
      if (programa.getPadre() == null) {
        Assert.isTrue(!existPlanWithNombre(programaActualizar.getNombre(), programaActualizar.getId()),
            "Ya existe un plan con el mismo nombre");
      } else {
        Assert.isTrue(!existProgramaNombre(programaActualizar.getPadre().getId(), programaActualizar.getNombre(),
            programa.getId()), "Ya existe un programa con el mismo nombre en el plan");
      }

      programa.setNombre(programaActualizar.getNombre());
      programa.setDescripcion(programaActualizar.getDescripcion());
      programa.setPadre(programaActualizar.getPadre());

      Programa returnValue = repository.save(programa);
      log.debug("update(Programa programaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProgramaNotFoundException(programaActualizar.getId()));
  }

  /**
   * Reactiva el {@link Programa}.
   *
   * @param id Id del {@link Programa}.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  public Programa enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "Programa id no puede ser null para reactivar un Programa");

    return repository.findById(id).map(programa -> {
      if (programa.getActivo()) {
        // Si esta activo no se hace nada
        return programa;
      }

      if (programa.getPadre() == null) {
        Assert.isTrue(!existPlanWithNombre(programa.getNombre(), programa.getId()),
            "Ya existe un plan con el mismo nombre");
      } else {
        Assert.isTrue(!existProgramaNombre(programa.getPadre().getId(), programa.getNombre(), programa.getId()),
            "Ya existe un programa con el mismo nombre en el plan");
      }

      programa.setActivo(true);

      Programa returnValue = repository.save(programa);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ProgramaNotFoundException(id));
  }

  /**
   * Desactiva el {@link Programa}.
   *
   * @param id Id del {@link Programa}.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  public Programa disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "Programa id no puede ser null para desactivar un Programa");

    return repository.findById(id).map(programa -> {
      if (!programa.getActivo()) {
        // Si no esta activo no se hace nada
        return programa;
      }

      programa.setActivo(false);
      Programa returnValue = repository.save(programa);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ProgramaNotFoundException(id));
  }

  /**
   * Obtiene {@link Programa} por su id.
   *
   * @param id el id de la entidad {@link Programa}.
   * @return la entidad {@link Programa}.
   */
  @Override
  public Programa findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Programa returnValue = repository.findById(id).orElseThrow(() -> new ProgramaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link Programa} activos.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  @Override
  public Page<Programa> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Programa> specs = ProgramaSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Programa> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los planes activos (los {@link Programa} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  @Override
  public Page<Programa> findAllPlan(String query, Pageable pageable) {
    log.debug("findAllPlan(String query, Pageable pageable) - start");
    Specification<Programa> specs = ProgramaSpecifications.activos().and(ProgramaSpecifications.planes())
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Programa> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllPlan(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los planes (los {@link Programa} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  @Override
  public Page<Programa> findAllTodosPlan(String query, Pageable pageable) {
    log.debug("findAllTodosPlan(String query, Pageable pageable) - start");
    Specification<Programa> specs = ProgramaSpecifications.planes().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Programa> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllTodosPlan(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link Programa} hijos directos del {@link Programa} con el id
   * indicado.
   *
   * @param programaId el id de la entidad {@link Programa}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  @Override
  public Page<Programa> findAllHijosPrograma(Long programaId, String query, Pageable pageable) {
    log.debug("findAllHijosPrograma(Long programaId, String query, Pageable pageable) - start");
    Specification<Programa> specs = ProgramaSpecifications.hijos(programaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Programa> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllHijosPrograma(Long programaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba si existe algun plan ({@link Programa} con padre null) con el
   * nombre indicado.
   *
   * @param nombre            nombre del plan.
   * @param programaIdExcluir Identificador del {@link Programa} que se excluye de
   *                          la busqueda.
   * @return true si existe algun plan con ese nombre.
   */
  private boolean existPlanWithNombre(String nombre, Long programaIdExcluir) {
    log.debug("existPlanWithNombre(String nombre, Long programaIdExcluir) - start");
    Specification<Programa> specPlanesByNombre = ProgramaSpecifications.planesByNombre(nombre, programaIdExcluir);

    boolean returnValue = !repository.findAll(specPlanesByNombre, Pageable.unpaged()).isEmpty();

    log.debug("existPlanWithNombre(String nombre, Long programaIdExcluir) - end");
    return returnValue;
  }

  /**
   * Comprueba si existe {@link Programa} con el nombre indicado en el arbol del
   * programa indicado.
   *
   * @param programaId        Identificador del {@link Programa}.
   * @param nombre            nombre del programa.
   * @param programaIdExcluir Identificador del {@link Programa} que se excluye de
   *                          la busqueda.
   * @return true si existe algun {@link Programa} con ese nombre.
   */
  private boolean existProgramaNombre(Long programaId, String nombre, Long programaIdExcluir) {
    log.debug("existProgramaNombre(Long programaId, String nombre, Long programaIdExcluir) - start");

    // Busca el programa raiz
    Programa programaRaiz = repository.findById(programaId).map(programa -> {
      return programa;
    }).orElseThrow(() -> new ProgramaNotFoundException(programaId));

    while (programaRaiz.getPadre() != null) {
      programaRaiz = repository.findById(programaRaiz.getPadre().getId()).get();
    }

    // Busca el nombre desde el nodo raiz nivel a nivel
    boolean nombreEncontrado = false;

    List<Programa> programasHijos = repository.findByPadreIdInAndActivoIsTrue(Arrays.asList(programaRaiz.getId()));
    nombreEncontrado = programasHijos.stream()
        .anyMatch(programa -> programa.getNombre().equals(nombre) && programa.getId() != programaIdExcluir);

    while (!nombreEncontrado && !programasHijos.isEmpty()) {
      programasHijos = repository
          .findByPadreIdInAndActivoIsTrue(programasHijos.stream().map(Programa::getId).collect(Collectors.toList()));
      nombreEncontrado = programasHijos.stream()
          .anyMatch(programa -> programa.getNombre().equals(nombre) && programa.getId() != programaIdExcluir);
    }

    log.debug("existProgramaNombre(Long programaId, String nombre, Long programaIdExcluir) - end");
    return nombreEncontrado;
  }

}
