package org.crue.hercules.sgi.csp.service;

import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.AgrupacionGastoConceptoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoAgrupacionGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto;
import org.crue.hercules.sgi.csp.repository.AgrupacionGastoConceptoRepository;
import org.crue.hercules.sgi.csp.repository.specification.AgrupacionGastoConceptoSpecifications;
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
public class AgrupacionGastoConceptoService {

  private final AgrupacionGastoConceptoRepository repository;

  public AgrupacionGastoConceptoService(Validator validator, AgrupacionGastoConceptoRepository repository) {
    this.repository = repository;
  }

  /**
   * Guardar un nuevo {@link AgrupacionGastoConcepto}.
   *
   * @param agrupacionGastoConcepto la entidad {@link AgrupacionGastoConcepto} a
   *                                guardar.
   * @return la entidad {@link AgrupacionGastoConcepto} persistida.
   */
  @Transactional
  @Validated({ AgrupacionGastoConcepto.OnCrear.class })
  public AgrupacionGastoConcepto create(@Valid AgrupacionGastoConcepto agrupacionGastoConcepto) {
    log.debug("create(AgrupacionGastoConcepto agrupacionGastoConcepto) - start");

    AgrupacionGastoConcepto returnValue = repository.save(agrupacionGastoConcepto);

    log.debug("create(AgrupacionGastoConcepto agrupacionGastoConcepto) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link AgrupacionGastoConcepto}.
   *
   * @param agrupacionGastoConcepto la entidad {@link AgrupacionGastoConcepto} a
   *                                actualizar.
   * @return la entidad {@link AgrupacionGastoConcepto} persistida.
   */
  @Transactional
  @Validated({ AgrupacionGastoConcepto.OnActualizar.class })
  public AgrupacionGastoConcepto update(@Valid AgrupacionGastoConcepto agrupacionGastoConcepto) {
    log.debug("update(ProyectoAgrupacionGasto proyectoAgrupacionGasto) - start");

    Assert.notNull(agrupacionGastoConcepto.getId(), "Id no puede ser null para actualizar proyectoAgrupacionGasto");

    return repository.findById(agrupacionGastoConcepto.getId()).map(proyectoAgrupacionGastoExistente -> {

      proyectoAgrupacionGastoExistente.setConceptoGasto(agrupacionGastoConcepto.getConceptoGasto());

      AgrupacionGastoConcepto returnValue = repository.save(proyectoAgrupacionGastoExistente);
      log.debug("update(ProyectoAgrupacionGasto proyectoAgrupacionGasto) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoAgrupacionGastoNotFoundException(agrupacionGastoConcepto.getId()));

  }

  /**
   * Elimina el {@link AgrupacionGastoConcepto}.
   *
   * @param id Id del {@link AgrupacionGastoConcepto}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(AgrupacionGastoConcepto agrupacionGastoConcepto) - start");
    repository.deleteById(id);
    log.debug("delete(AgrupacionGastoConcepto agrupacionGastoConcepto) - end");
  }

  /**
   * Obtener todas las entidades {@link AgrupacionGastoConcepto} paginadas y/o
   * filtradas.
   * 
   * @param agrupacionId id de la Proyecto
   * @param query        la información del filtro.
   * @param paging       la información de la paginación.
   * @return la lista de entidades {@link AgrupacionGastoConcepto}
   */
  public Page<AgrupacionGastoConcepto> findAllByAgrupacionId(Long agrupacionId, String query, Pageable paging) {
    log.debug("findAllbyProyectoAgrupacionGastoId(Long agrupacionId, String query, Pageable pageable) - start");
    Specification<AgrupacionGastoConcepto> specs = AgrupacionGastoConceptoSpecifications
        .byProyectoAgrupacionGastoId(agrupacionId).and(SgiRSQLJPASupport.toSpecification(query));
    Page<AgrupacionGastoConcepto> returnValue = repository.findAll(specs, paging);
    log.debug("findAllbyProyectoAgrupacionGastoId(Long agrupacionId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link AgrupacionGastoConcepto} por id.
   *
   * @param id el id de la entidad {@link AgrupacionGastoConcepto}.
   * @return true si existe y false en caso contrario.
   */
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene una entidad {@link AgrupacionGastoConcepto} por id.
   * 
   * @param id Identificador de la entidad {@link AgrupacionGastoConcepto}.
   * @return la entidad {@link AgrupacionGastoConcepto}.
   */
  public AgrupacionGastoConcepto findById(Long id) {
    log.debug("findById(Long id) - start");
    final AgrupacionGastoConcepto returnValue = repository.findById(id)
        .orElseThrow(() -> new AgrupacionGastoConceptoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  public Page<AgrupacionGastoConcepto> findAll(Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");

    Page<AgrupacionGastoConcepto> returnValue = repository.findAll(pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

}
