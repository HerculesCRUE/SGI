package org.crue.hercules.sgi.rel.service;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rel.exceptions.RelacionNotFoundException;
import org.crue.hercules.sgi.rel.model.Relacion;
import org.crue.hercules.sgi.rel.repository.RelacionRepository;
import org.crue.hercules.sgi.rel.repository.predicate.RelacionPredicateResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad Relacion.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RelacionService {

  private final RelacionRepository repository;

  public RelacionService(RelacionRepository relacionRepository) {
    this.repository = relacionRepository;
  }

  /**
   * Obtiene {@link Relacion} por su id.
   *
   * @param id el id de la entidad {@link Relacion}.
   * @return la entidad {@link Relacion}.
   */
  public Relacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Relacion returnValue = repository.findById(id).orElseThrow(() -> new RelacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link Relacion} paginadas y/o filtradas.
   *
   * @param query    la información del filtro.
   * @param pageable paginación
   * @return la lista de {@link Relacion} de un proyecto paginada.
   */
  public Page<Relacion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");

    Specification<Relacion> specs = SgiRSQLJPASupport.toSpecification(query, RelacionPredicateResolver.getInstance());

    Page<Relacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link Relacion}.
   *
   * @param relacion la entidad {@link Relacion} a guardar.
   * @return la entidad {@link Relacion} persistida.
   */
  @Transactional
  public Relacion create(Relacion relacion) {
    log.debug("create(Relacion Relacion) - start");

    Assert.isNull(relacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Relacion.class)).build());

    Relacion returnValue = repository.save(relacion);

    log.debug("create(Relacion Relacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Relacion}.
   *
   * @param relacion la entidad {@link Relacion} a actualizar.
   * @return la entidad {@link Relacion} persistida.
   */
  @Transactional
  public Relacion update(Relacion relacion) {
    log.debug("update(Relacion Relacion) - start");

    Assert.notNull(relacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Relacion.class)).build());

    return repository.findById(relacion.getId()).map(relacionExistente -> {

      // Establecemos los campos actualizables con los recibidos
      relacionExistente.setEntidadDestinoRef(relacion.getEntidadDestinoRef());
      relacionExistente.setEntidadOrigenRef(relacion.getEntidadOrigenRef());
      relacionExistente.setTipoEntidadDestino(relacion.getTipoEntidadDestino());
      relacionExistente.setTipoEntidadOrigen(relacion.getTipoEntidadOrigen());
      relacionExistente.setObservaciones(relacion.getObservaciones());
      // Actualizamos la entidad
      Relacion returnValue = repository.save(relacionExistente);
      log.debug("update(Relacion Relacion) - end");
      return returnValue;
    }).orElseThrow(() -> new RelacionNotFoundException(relacion.getId()));
  }

  /**
   * Elimina {@link Relacion} por id.
   * 
   * @param relacionId El id de la entidad {@link Relacion}.
   */
  @Transactional
  public void delete(Long relacionId) {
    log.debug("delete(Long relacionId) - start");
    Assert.notNull(relacionId, // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Relacion.class)).build());
    if (!repository.existsById(relacionId)) {
      throw new RelacionNotFoundException(relacionId);
    }

    repository.deleteById(relacionId);
    log.debug("delete(Long relacionId) - end");
  }
}
