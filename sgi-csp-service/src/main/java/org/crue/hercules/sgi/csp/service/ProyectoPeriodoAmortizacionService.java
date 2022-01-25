package org.crue.hercules.sgi.csp.service;

import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoAmortizacionNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoAmortizacion;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoAmortizacionRepository;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProyectoPeriodoAmortizacionService {

  private final ProyectoPeriodoAmortizacionRepository repository;

  public ProyectoPeriodoAmortizacionService(Validator validator, ProyectoPeriodoAmortizacionRepository repository) {
    this.repository = repository;
  }

  /**
   * Guardar un nuevo {@link ProyectoPeriodoAmortizacion}.
   *
   * @param proyectoPeriodoAmortizacion la entidad
   *                                    {@link ProyectoPeriodoAmortizacion} a
   *                                    guardar.
   * @return la entidad {@link ProyectoPeriodoAmortizacion} persistida.
   */
  @Transactional
  public ProyectoPeriodoAmortizacion create(@Valid ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion) {
    log.debug("create(ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion) - start");

    Assert.isNull(proyectoPeriodoAmortizacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProyectoPeriodoAmortizacion.class)).build());

    ProyectoPeriodoAmortizacion returnValue = repository.save(proyectoPeriodoAmortizacion);

    log.debug("create(ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ProyectoPeriodoAmortizacion}.
   *
   * @param proyectoPeriodoAmortizacion la entidad
   *                                    {@link ProyectoPeriodoAmortizacion} a
   *                                    actualizar.
   * @return la entidad {@link ProyectoPeriodoAmortizacion} persistida.
   */
  @Transactional
  public ProyectoPeriodoAmortizacion update(@Valid ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion) {
    log.debug("update(ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion) - start");

    Assert.notNull(proyectoPeriodoAmortizacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProyectoPeriodoAmortizacion.class)).build());

    return repository.findById(proyectoPeriodoAmortizacion.getId()).map(proyectoPeriodoAmortizacionExistente -> {

      proyectoPeriodoAmortizacionExistente.setImporte(proyectoPeriodoAmortizacion.getImporte());
      proyectoPeriodoAmortizacionExistente
          .setFechaLimiteAmortizacion(proyectoPeriodoAmortizacion.getFechaLimiteAmortizacion());
      proyectoPeriodoAmortizacionExistente.setProyectoAnualidadId(proyectoPeriodoAmortizacion.getProyectoAnualidadId());
      proyectoPeriodoAmortizacionExistente
          .setProyectoEntidadFinanciadoraId(proyectoPeriodoAmortizacion.getProyectoEntidadFinanciadoraId());
      proyectoPeriodoAmortizacionExistente
          .setProyectoSGERef(proyectoPeriodoAmortizacion.getProyectoSGERef());

      ProyectoPeriodoAmortizacion returnValue = repository.save(proyectoPeriodoAmortizacionExistente);
      log.debug("update(ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoPeriodoAmortizacionNotFoundException(proyectoPeriodoAmortizacion.getId()));

  }

  /**
   * Elimina el {@link ProyectoPeriodoAmortizacion}.
   *
   * @param id Id del {@link ProyectoPeriodoAmortizacion}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProyectoPeriodoAmortizacion.class)).build());

    if (!repository.existsById(id)) {
      throw new ProyectoPeriodoAmortizacionNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene una entidad {@link ProyectoPeriodoAmortizacion} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoPeriodoAmortizacion}.
   * @return la entidad {@link ProyectoPeriodoAmortizacion}.
   */
  public ProyectoPeriodoAmortizacion findById(Long id) {
    log.debug("findById(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProyectoPeriodoAmortizacion.class)).build());

    final ProyectoPeriodoAmortizacion returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoPeriodoAmortizacionNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene una lista paginada {@link ProyectoPeriodoAmortizacion} por referencia
   * de SGE.
   * 
   * @param proyectoSGERef Identificador del proyectoSGE
   * @param pageable       pageable
   * @return la lista de entidades {@link ProyectoPeriodoAmortizacion}.
   */
  public Page<ProyectoPeriodoAmortizacion> findAllByProyectoSGERef(String proyectoSGERef, Pageable pageable) {
    log.debug("findAllByProyectoSGERef(String query, Pageable pageable) - start");
    Page<ProyectoPeriodoAmortizacion> returnValue = repository.findAllByProyectoSGERef(proyectoSGERef, pageable);
    log.debug("findAllByProyectoSGERef(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene una lista paginada de todos los {@link ProyectoPeriodoAmortizacion}
   * de SGE.
   * 
   * @param query    query
   * @param pageable pageable
   * @return lista de entidades {@link ProyectoPeriodoAmortizacion}.
   */
  public Page<ProyectoPeriodoAmortizacion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ProyectoPeriodoAmortizacion> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<ProyectoPeriodoAmortizacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

}