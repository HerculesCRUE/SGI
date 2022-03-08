package org.crue.hercules.sgi.prc.service;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link ProduccionCientifica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProduccionCientificaService {

  private final ProduccionCientificaRepository repository;
  private final EstadoProduccionCientificaService estadoProduccionCientificaService;

  public ProduccionCientificaService(
      ProduccionCientificaRepository produccionCientificaRepository,
      EstadoProduccionCientificaService estadoProduccionCientificaService) {
    this.repository = produccionCientificaRepository;
    this.estadoProduccionCientificaService = estadoProduccionCientificaService;
  }

  /**
   * Recupera todas las {@link PublicacionResumen} con su
   * título, fecha y tipo de producción
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado paginado de {@link PublicacionResumen}
   */
  public Page<PublicacionResumen> findAllPublicaciones(String query, Pageable pageable) {
    log.debug("findAllPublicaciones(String query, Pageable pageable) - start");

    Page<PublicacionResumen> returnValue = repository.findAllPublicaciones(query, pageable);
    log.debug("findAllPublicaciones(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ProduccionCientifica} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ProduccionCientifica} paginadas y/o
   *         filtradas.
   */
  public Page<ProduccionCientifica> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ProduccionCientifica> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ProduccionCientifica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ProduccionCientifica} por su id.
   *
   * @param id el id de la entidad {@link ProduccionCientifica}.
   * @return la entidad {@link ProduccionCientifica}.
   */
  public ProduccionCientifica findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProduccionCientifica returnValue = repository.findById(id)
        .orElseThrow(() -> new ProduccionCientificaNotFoundException(id.toString()));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Cambia el estado de la entidad {@link ProduccionCientifica}.
   *
   * @param id                           el id de la entidad
   *                                     {@link ProduccionCientifica}.
   * @param tipoEstadoProduccionToUpdate estado al que se va a actualizar.
   * @param comentario                   motivo del rechazo.
   * @return la entidad {@link ProduccionCientifica} actualizada.
   */
  @Transactional
  public ProduccionCientifica cambiarEstado(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate,
      String comentario) {
    log.debug("cambiarEstado(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProduccionCientifica.class)).build());

    return repository.findById(id).map(produccionCientifica -> {
      if (produccionCientifica.getEstado() != null
          && produccionCientifica.getEstado().getEstado() != TipoEstadoProduccion.PENDIENTE) {
        // Si es diferente de PENDIENTE no se hace nada
        return produccionCientifica;
      }

      EstadoProduccionCientifica estadoProduccionCientificaToUpdate = EstadoProduccionCientifica
          .builder()
          .estado(tipoEstadoProduccionToUpdate)
          .produccionCientificaId(produccionCientifica.getId())
          .comentario(comentario)
          .build();
      EstadoProduccionCientifica estadoProduccionCientificaUpdated = estadoProduccionCientificaService
          .create(estadoProduccionCientificaToUpdate);
      produccionCientifica.setEstado(estadoProduccionCientificaUpdated);

      ProduccionCientifica returnValue = repository.save(produccionCientifica);

      log.debug("cambiarEstado(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate) - end");
      return returnValue;
    }).orElseThrow(() -> new ProduccionCientificaNotFoundException(id.toString()));
  }
}
