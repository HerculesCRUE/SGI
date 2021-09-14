package org.crue.hercules.sgi.csp.service;

import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.ProyectoAgrupacionGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoAgrupacionGasto;
import org.crue.hercules.sgi.csp.repository.ProyectoAgrupacionGastoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoAgrupacionGastoSpecifications;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.modelmapper.internal.util.Assert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProyectoAgrupacionGastoService {

  private final ProyectoAgrupacionGastoRepository repository;

  public ProyectoAgrupacionGastoService(Validator validator, ProyectoAgrupacionGastoRepository repository) {
    this.repository = repository;
  }

  /**
   * Guardar un nuevo {@link ProyectoAgrupacionGasto}.
   *
   * @param proyectoAgrupacionGasto la entidad {@link ProyectoAgrupacionGasto} a
   *                                guardar.
   * @return la entidad {@link ProyectoAgrupacionGasto} persistida.
   */
  @Transactional
  @Validated({ ProyectoAgrupacionGasto.OnCrear.class })
  public ProyectoAgrupacionGasto create(@Valid ProyectoAgrupacionGasto proyectoAgrupacionGasto) {
    log.debug("create(ProyectoAgrupacionGasto proyectoAgrupacionGasto) - start");

    ProyectoAgrupacionGasto returnValue = repository.save(proyectoAgrupacionGasto);

    log.debug("create(ProyectoAgrupacionGasto proyectoAgrupacionGasto) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ProyectoAgrupacionGasto}.
   *
   * @param proyectoAgrupacionGasto la entidad {@link ProyectoAgrupacionGasto} a
   *                                actualizar.
   * @return la entidad {@link ProyectoAgrupacionGasto} persistida.
   */
  @Transactional
  @Validated({ ProyectoAgrupacionGasto.OnActualizar.class })
  public ProyectoAgrupacionGasto update(@Valid ProyectoAgrupacionGasto proyectoAgrupacionGasto) {
    log.debug("update(ProyectoAgrupacionGasto proyectoAgrupacionGasto) - start");

    Assert.notNull(proyectoAgrupacionGasto.getId(), "Id no puede ser null para actualizar proyectoAgrupacionGasto");

    return repository.findById(proyectoAgrupacionGasto.getId()).map(proyectoAgrupacionGastoExistente -> {

      proyectoAgrupacionGastoExistente.setNombre(proyectoAgrupacionGasto.getNombre());

      ProyectoAgrupacionGasto returnValue = repository.save(proyectoAgrupacionGastoExistente);
      log.debug("update(ProyectoAgrupacionGasto proyectoAgrupacionGasto) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoAgrupacionGastoNotFoundException(proyectoAgrupacionGasto.getId()));

  }

  /**
   * Elimina el {@link ProyectoAgrupacionGasto}.
   *
   * @param id Id del {@link ProyectoAgrupacionGasto}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(ProyectoAgrupacionGasto proyectoAgrupacionGasto) - start");
    repository.deleteById(id);
    log.debug("delete(ProyectoAgrupacionGasto proyectoAgrupacionGasto) - end");
  }

  /**
   * Obtener todas las entidades {@link ProyectoAgrupacionGasto} paginadas y/o
   * filtradas.
   * 
   * @param proyectoId id de la Proyecto
   * @param query      la información del filtro.
   * @param paging     la información de la paginación.
   * @return la lista de entidades {@link ProyectoAgrupacionGasto}
   */

  public Page<ProyectoAgrupacionGasto> findAllByProyectoId(Long proyectoId, String query, Pageable paging) {
    log.debug("findAllByProyectoId(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoAgrupacionGasto> specs = ProyectoAgrupacionGastoSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<ProyectoAgrupacionGasto> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyectoId(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link ProyectoAgrupacionGasto} por id.
   *
   * @param id el id de la entidad {@link ProyectoAgrupacionGasto}.
   * @return true si existe y false en caso contrario.
   */
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene una entidad {@link ProyectoAgrupacionGasto} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoAgrupacionGasto}.
   * @return la entidad {@link ProyectoAgrupacionGasto}.
   */
  public ProyectoAgrupacionGasto findById(Long id) {
    log.debug("findById(Long id) - start");
    final ProyectoAgrupacionGasto returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoAgrupacionGastoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  public Page<ProyectoAgrupacionGasto> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    // TODO: Pendiente evaluar si es necesario retringir por unidad de gestión
    Specification<ProyectoAgrupacionGasto> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ProyectoAgrupacionGasto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }
}
